/********************************************************************************
 * Copyright (c) 2011-2016, 2026 Semmtech B.V., Hoofddorp.
 *    ___  _____ __  __ __  __ _____ _____ ___ _   _ 
 *   / __|| ____|  \/  |  \/  |_   _| ____/ __| | | |
 *   \__ \|  _| | |\/| | |\/| | | | |  _|| |  | |_| |
 *    __) | |___| |  | | |  | | | | | |__| |__|  _  |
 *   |___/|_____|_|  |_|_|  |_| |_| |_____\___|_| |_| B.V.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package com.semmtech.plugin.semmweb.core.widgets;


import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.AnnotationModel;
import org.eclipse.jface.text.source.AnnotationPainter;
import org.eclipse.jface.text.source.IAnnotationAccess;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.VerticalRuler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.editors.text.TextSourceViewerConfiguration;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.eclipse.ui.texteditor.DefaultMarkerAnnotationAccess;
import org.topbraid.spin.arq.ARQFactory;
import org.topbraid.spin.model.SPINFactory;
import org.topbraid.spin.vocabulary.SP;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.QueryParseException;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.CorePluginImages;
import com.semmtech.plugin.semmweb.core.forms.editor.AbstractModelResourceContent;
import com.semmtech.plugin.semmweb.core.model.ModelTransaction;
import com.semmtech.ui.plugin.DelayedRunnableExecution;
import com.semmtech.ui.plugin.widgets.ExtendedSourceViewer;
import com.semmtech.ui.plugin.widgets.Widgets;


/**
 * 
 * @author Sander Stolk
 */
public class QueryContentPart extends AbstractPropertyObjectContentPart {

    protected final static String ANNOTATION_TYPE_ERROR = "org.eclipse.ui.workbench.texteditor.error";
    protected final DelayedRunnableExecution delayedEditValidation;

    protected Resource query;
    protected Property property;

    protected ExtendedSourceViewer sourceViewer;
    protected String initialText;
    protected IAnnotationModel annotationModel;
    protected Annotation syntaxError;

    private FormColors formColors;
    private FormToolkit toolkit;

    public QueryContentPart(AbstractModelResourceContent contentParent, Composite parent,
            FormToolkit toolkit, Property property, Resource query) {
        super(contentParent, parent, toolkit);
        this.query = query;
        this.property = property;
        this.annotationModel = new AnnotationModel();

        this.delayedEditValidation = new DelayedRunnableExecution(new Runnable() {
            @Override
            public void run() {
                validateEdit();
            }
        });

        createContent();
    }

    @Override
    public RDFNode getObject() {
        return query;
    }

    public void alterContent(Resource query) {
        this.query = query;
        fillContent();
        refresh();
    }

    @SuppressWarnings("deprecation")
    private void createContent() {
        formColors = getDefaultFormColors();
        toolkit = new FormToolkit(formColors);
        toolkit.setBorderStyle(SWT.WRAP | SWT.MULTI);

        setBackground(formColors.getColor(BACKGROUND));
        {
            TableWrapLayout layout = new TableWrapLayout();
            layout.numColumns = 2;
            layout.verticalSpacing = 0;
            layout.topMargin = 1;
            layout.rightMargin = 0;
            layout.leftMargin = 2;
            layout.bottomMargin = 3;
            layout.horizontalSpacing = 3;
            setLayout(layout);
        }

        Label iconLabel = new Label(this, SWT.NONE);
        {
            TableWrapData data = new TableWrapData(TableWrapData.LEFT, TableWrapData.TOP);
            data.heightHint = 18;
            iconLabel.setLayoutData(data);
        }
        iconLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDoubleClick(MouseEvent event) {
                if (query != null && query.isResource()) {
                    CorePlugin.getDefault().openResource(query.asResource());
                }
            }
        });
        iconLabel.setImage(CorePlugin.getDefault().getImage(CorePluginImages.IMG_SPARQL));

        IAnnotationAccess annotationAccess = new DefaultMarkerAnnotationAccess();
        IVerticalRuler ruler = new VerticalRuler(20, annotationAccess);
        sourceViewer = new ExtendedSourceViewer(this, ruler, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
        sourceViewer.configure(new TextSourceViewerConfiguration());// see
                                                                    // SparqlViewerConfiguration
                                                                    // in our
                                                                    // sparql
                                                                    // project!
        sourceViewer.getControl().setLayoutData(
                new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.TOP));

        openQueryInViewer();

        // to paint the annotations in the text (e.g. squiggles)
        AnnotationPainter ap = new AnnotationPainter(sourceViewer, annotationAccess);
        final RGB ERROR_RGB = new RGB(255, 0, 0);
        ap.addAnnotationType(ANNOTATION_TYPE_ERROR);
        ap.setAnnotationTypeColor(ANNOTATION_TYPE_ERROR, new Color(Display.getDefault(), ERROR_RGB));
        sourceViewer.addPainter(ap);

        sourceViewer.appendVerifyKeyListener(new VerifyKeyListener() {
            @Override
            public void verifyKey(VerifyEvent event) {
                if (event.stateMask == SWT.SHIFT || event.stateMask == SWT.CTRL) {
                    if (event.character == SWT.CR || event.character == SWT.KEYPAD_CR) {
                        event.doit = false;
                        confirmEdit();
                    }
                }
                else if (event.character == SWT.ESC) {
                    event.doit = false;
                    delayedEditValidation.abort();

                    if (syntaxError != null && annotationModel != null) {
                        annotationModel.removeAnnotation(syntaxError);
                        syntaxError = null;
                    }
                    sourceViewer.setDocument(new Document(initialText), annotationModel);
                    sourceViewer.getControl().setBackground(formColors.getColor(DEFAULT_BORDER));
                }
                else {
                    delayedEditValidation.start();
                }
            }
        });

        fillContent();
    }

    private void fillContent() {
        refreshQuery();
    }

    private String getQueryText() {
        if (query == null) {
            return new String();
        }
        org.topbraid.spin.model.Query spinQuery = (query != null) ? SPINFactory.asQuery(query)
                : null;
        return (spinQuery != null) ? spinQuery.toString() : new String();
    }

    @Override
    public boolean updateContent() {
        boolean updateRequired = false;
        String queryText = getQueryText();

        if (!queryText.equals(initialText)) {
            updateRequired = true;
        }
        if (updateRequired) {
            refreshQuery();
        }
        return updateRequired;
    }

    public void refreshQuery() {
        openQueryInViewer();
        refresh();
    }

    protected void openQueryInViewer() {
        initialText = getQueryText();
        if (syntaxError != null && annotationModel != null) {
            annotationModel.removeAnnotation(syntaxError);
            syntaxError = null;
        }
        sourceViewer.setDocument(new Document(initialText), annotationModel);
        delayedEditValidation.abort();
        sourceViewer.getControl().setBackground(formColors.getColor(DEFAULT_BORDER));
    }

    @Override
    public boolean setFocus() {
        return sourceViewer.getControl().setFocus();
    }

    protected boolean confirmEdit() {
        delayedEditValidation.abort();

        boolean validated = validateEdit();
        if (validated) {
            finishEdit();
            updateModel();
        }
        else {
            // jump to error
            if (syntaxError != null && annotationModel != null) {
                Position pos = annotationModel.getPosition(syntaxError);
                if (pos != null) {
                    TextSelection selection = new TextSelection(sourceViewer.getDocument(),
                            pos.offset, 0);
                    sourceViewer.setSelection(selection, true);
                }
            }
        }
        return validated;
    }

    protected void finishEdit() {
        initialText = sourceViewer.getDocument().get();
        sourceViewer.getControl().setBackground(formColors.getColor(DEFAULT_BORDER));
    }

    protected Resource getQueryType(String queryText) {
        OntModel model = getModelProvider().getOntModel();
        com.hp.hpl.jena.query.Query arqQuery = ARQFactory.get().createQuery(model, queryText);

        if (arqQuery.isConstructType()) {
            return SP.Construct;
        }
        if (arqQuery.isSelectType()) {
            return SP.Select;
        }
        if (arqQuery.isAskType()) {
            return SP.Ask;
        }
        if (arqQuery.isDescribeType()) {
            return SP.Describe;
        }
        return null;
    }

    protected void updateModel() {
        String transactionDescription = "Change due to commit in Query part";
        ModelTransaction transaction = getModelProvider().createTransaction(transactionDescription);

        // get new ARQ query
        com.hp.hpl.jena.query.Query arqQuery = null;
        String queryText = sourceViewer.getDocument().get();
        if (!Strings.isNullOrEmpty(queryText)) {
            try {
                OntModel model = getModelProvider().getOntModel();
                arqQuery = ARQFactory.get().createQuery(model, queryText);
            }
            catch (Throwable t) {
                // Should not occur, as the query is known to be valid
            }
        }

        // remove old SPIN query from model
        String queryURI = null;
        if (query != null) {
            getResource().removeProperty(property, query);
            if (query.isResource()) {
                queryURI = query.asResource().getURI();
                removeQueryFromModel(query.asResource());
            }
        }

        // add new SPIN query to model
        if (arqQuery == null) {
            query = null;
        }
        else {
            // save query and its patterns as triples
            // ARQ2SPIN arq2SPIN = new
            // ARQ2SPIN(getModelProvider().getOntModel());
            // org.topbraid.spin.model.Query spinQuery =
            // arq2SPIN.createQuery(arqQuery, queryURI);
            // query = spinQuery;
            // getResource().addProperty(property, query);

            // save query as literal using property sp:text
            Resource queryType = getQueryType(queryText);
            query = (queryURI != null) ? getResource().getModel().createResource(queryURI,
                    queryType) : getResource().getModel().createResource(queryType);
            query.addLiteral(SP.text, queryText);
            getResource().addProperty(property, query);
        }

        getModelProvider().commitTransaction(transaction);
    }

    protected boolean validateEdit() {
        boolean valid = validateCurrentValue();

        String borderColor = DEFAULT_BORDER;
        if (!valid) {
            borderColor = ERROR_BORDER;
        }
        else if (!initialText.equals(sourceViewer.getDocument().get())) {
            borderColor = MODIFY_BORDER;
        }
        sourceViewer.getControl().setBackground(formColors.getColor(borderColor));
        return valid;
    }

    protected boolean validateCurrentValue() {
        // first clear the current annotation
        if (syntaxError != null && annotationModel != null) {
            annotationModel.removeAnnotation(syntaxError);
            syntaxError = null;
        }

        if (Widgets.isNullOrDisposed(sourceViewer.getControl())) {
            return false;
        }
        String queryText = sourceViewer.getDocument().get();
        if (Strings.isNullOrEmpty(queryText)) {
            return true;
        }

        try {
            OntModel model = getModelProvider().getOntModel();
            com.hp.hpl.jena.query.Query arqQuery = ARQFactory.get().createQuery(model, queryText);
            return (arqQuery != null);
        }
        catch (QueryParseException qpe) {
            // query could not be parsed
            String message = qpe.getMessage();
            syntaxError = new Annotation(ANNOTATION_TYPE_ERROR, false, message);
            int pos = 0;

            /*
             * As the exception can have a different line and column from what
             * is stated in the message (see
             * com.hp.hpl.jena.sparql.lang.ParserARQ#perform), try to extract
             * the numbers from the message if possible.
             */
            int line = getNumberFromMessage("line", message, qpe.getLine() + 1);
            int column = getNumberFromMessage("column", message, qpe.getColumn() + 1);
            try {
                pos = sourceViewer.getDocument().getLineOffset(line - 1) + column - 1;
            }
            catch (Exception e) {
            }

            if (annotationModel != null) {
                annotationModel.addAnnotation(syntaxError, new Position(pos, 2));
            }
        }
        catch (Throwable t) {
            // invalid query
            syntaxError = new Annotation(ANNOTATION_TYPE_ERROR, false, t.getMessage());
            if (annotationModel != null) {
                annotationModel.addAnnotation(syntaxError, new Position(0));
            }
        }

        return false;
    }

    protected int getNumberFromMessage(String afterToken, String message, int defaultValue) {
        message = message.toLowerCase();
        afterToken = afterToken.toLowerCase();
        int index = message.indexOf(afterToken);
        if (index != -1) {
            index += afterToken.length();
            try {
                message = message.substring(index);
                message = message.trim();
                return NumberFormat.getInstance().parse(message).intValue();
            }
            catch (ParseException e) {
            }
        }
        return defaultValue;
    }

    /**
     * Remove any triples originating from query, as well as all those
     * originating from an anon node. Does not remove the query as object from
     * the model.
     */
    protected void removeQueryFromModel(Resource query) {
        removeQueryPartFromModel(query);
    }

    private void removeQueryPartFromModel(Resource resource) {
        List<RDFNode> objects = getObjectsFrom(resource);
        resource.removeProperties();
        for (RDFNode object : objects) {
            if (object.isAnon()) {
                removeQueryPartFromModel(object.asResource());
            }
        }
    }

    private List<RDFNode> getObjectsFrom(Resource resource) {
        List<RDFNode> objects = Lists.newArrayList();
        StmtIterator stmtIter = resource.listProperties();
        while (stmtIter.hasNext()) {
            objects.add(stmtIter.next().getObject());
        }
        return objects;
    }
}
