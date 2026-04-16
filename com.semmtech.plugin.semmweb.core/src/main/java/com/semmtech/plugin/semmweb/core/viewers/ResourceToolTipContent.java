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

package com.semmtech.plugin.semmweb.core.viewers;


import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.forms.editor.OntologyFormEditor;
import com.semmtech.plugin.semmweb.core.markers.SemanticProblem;
import com.semmtech.plugin.semmweb.core.model.IModelProvider;
import com.semmtech.semantics.sparql.QueryBuilder;
import com.semmtech.semantics.vocabulary.SKOS;


/**
 * 
 * @author Sander Stolk
 */
public class ResourceToolTipContent extends Composite {
    protected static final int INDENT_WIDTH = 16;
    protected static final int TOOLTIP_MARGIN_HEIGHT = 0;
    protected static final int TOOLTIP_MARGIN_WIDTH = 0;
    protected static final int TOOLTIP_MARGIN_TOP = 1;
    protected static final int TOOLTIP_MARGIN_LEFT = 3;
    protected static final int TOOLTIP_MARGIN_RIGHT = 7;
    protected static final int TOOLTIP_MARGIN_BOTTOM = 3;
    protected static final int TOOLTIP_SPACING = 0;

    protected Font boldFont;

    protected final Resource resource;
    protected ILabelProvider labelProvider;
    protected IModelProvider modelProvider;

    public ResourceToolTipContent(Composite parent, Resource resource, int style) {
        this(parent, null, resource, style);
    }

    public ResourceToolTipContent(Composite parent, IModelProvider modelProvider,
            Resource resource, int style) {
        super(parent, style);
        this.resource = resource;
        if (modelProvider == null) {
            modelProvider = CorePlugin.getDefault().getActiveModelProvider();
        }
        this.modelProvider = modelProvider;
        if (modelProvider != null) {
            this.labelProvider = modelProvider.getLabelProvider();
        }
        createContent();
    }

    protected void initializeBoldFont() {
        if (boldFont == null) {
            FontData fontData = getParent().getFont().getFontData()[0];
            boldFont = new Font(Display.getCurrent(), new FontData(fontData.getName(),
                    fontData.getHeight(), SWT.BOLD));
        }
    }

    private void createContent() {
        if (modelProvider == null) {
            return;
        }

        initializeBoldFont();

        RowLayout layout = new RowLayout(SWT.VERTICAL);
        layout.marginHeight = TOOLTIP_MARGIN_HEIGHT;
        layout.marginWidth = TOOLTIP_MARGIN_WIDTH;
        layout.marginTop = TOOLTIP_MARGIN_TOP;
        layout.marginLeft = TOOLTIP_MARGIN_LEFT;
        layout.marginRight = TOOLTIP_MARGIN_RIGHT;
        layout.marginBottom = TOOLTIP_MARGIN_BOTTOM;
        layout.spacing = TOOLTIP_SPACING;
        layout.fill = true;

        setLayout(layout);

        createResourceTitle(resource);
        createLabelSection(resource);
        createCommentSection(resource);
        // createPropertyResourcesSection("Type:", resource, RDF.type);
        // createAllPropertiesSection(resource, ImmutableList.of(RDF.type,
        // RDFS.label, RDFS.comment));
        createIdentificationSection(resource);
        createModelsSection(resource);
        createProblemsSection(resource);
    }

    protected void createResourceTitle(Resource resource) {
        RowLayout layout = new RowLayout();
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        layout.marginBottom = 5;
        layout.spacing = 3;

        RowData data = new RowData();

        Composite composite = new Composite(this, SWT.NONE);
        composite.setLayout(layout);
        composite.setLayoutData(data);

        Label iconLabel = new Label(composite, SWT.NONE);
        iconLabel.setImage(labelProvider.getImage(resource));
        Label nameLabel = new Label(composite, SWT.NONE);
        nameLabel.setText(labelProvider.getText(resource));
        nameLabel.setFont(boldFont);
    }

    protected void createCommentSection(Resource resource) {
        RowLayout layout = new RowLayout(SWT.VERTICAL);
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        layout.marginBottom = 9;
        layout.spacing = 5;

        Composite composite = new Composite(this, SWT.NONE);
        composite.setLayout(layout);

        if (!resource.hasProperty(RDFS.comment) && !resource.hasProperty(SKOS.definition)) {
            return;
        }

        for (StmtIterator iter = resource.listProperties(RDFS.comment); iter.hasNext();) {
            Statement stmt = iter.next();
            createCommentLabel(composite, labelProvider.getText(stmt.getObject()));
        }
        for (StmtIterator iter = resource.listProperties(SKOS.definition); iter.hasNext();) {
            Statement stmt = iter.next();
            createCommentLabel(composite, labelProvider.getText(stmt.getObject()));
        }
    }

    private void createCommentLabel(Composite parent, String comment) {
        Label commentLabel = new Label(parent, SWT.WRAP);
        RowData layoutData = new RowData();
        layoutData.width = 500;
        commentLabel.setText(comment);
        commentLabel.setLayoutData(layoutData);
    }

    protected void createModelsSection(Resource resource) {
        if (modelProvider == null) {
            return;
        }

        RowLayout layout = new RowLayout(SWT.VERTICAL);
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        layout.marginTop = 5;
        layout.spacing = 1;

        Composite composite = new Composite(this, SWT.NONE);
        composite.setLayout(layout);

        Label label = new Label(composite, SWT.NONE);
        label.setText("Described in:");
        label.setFont(boldFont);

        layout = new RowLayout(SWT.VERTICAL);
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        layout.marginLeft = INDENT_WIDTH;
        layout.spacing = 1;

        composite = new Composite(composite, SWT.NONE);
        composite.setLayout(layout);

        // Retrieve all model URIs in which this resource occurs
        List<String> containingModelURIs = Lists.newArrayList();
        if (isSubjectInModel(resource, modelProvider.getBaseModel())) {
            if (modelProvider.getModelURI() != null) {
                Path basePath = new Path(modelProvider.getModelURI());
                containingModelURIs.add(basePath.lastSegment());
            }
            else {
                containingModelURIs.add("Base");
            }
        }
        List<String> subModelURIs = modelProvider.getSubModelURIs();
        for (String subModelURI : subModelURIs) {
            Model subModel = modelProvider.getSubModel(subModelURI);
            if (isSubjectInModel(resource, subModel)) {
                String prefix = modelProvider.getOntModel().getNsURIPrefix(subModelURI);
                if (prefix != null) {
                    containingModelURIs.add(prefix);
                }
                else {
                    containingModelURIs.add(subModelURI);
                }
            }
        }

        // Create labels for those model uris
        for (String containingModelURI : containingModelURIs) {
            if (containingModelURI != null) {
                Label uriLabel = new Label(composite, SWT.NONE);
                try {
                    uriLabel.setText(URLDecoder.decode(containingModelURI, "UTF-8"));
                }
                catch (UnsupportedEncodingException e) {
                    uriLabel.setText(containingModelURI);
                }
            }
        }
    }

    private boolean isSubjectInModel(Resource resource, Model model) {
        Var varP = Var.alloc("predicate");
        Var varO = Var.alloc("object");
        QueryBuilder qb = QueryBuilder.createAsk();
        qb.addTriplePattern(resource, varP, varO);
        return qb.execAsk(model);
    }

    protected void createLabelSection(Resource resource) {
        RowLayout layout = new RowLayout();
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        layout.spacing = 1;

        Composite composite = new Composite(this, SWT.NONE);
        composite.setLayout(layout);

        if (!resource.hasProperty(RDFS.label) && !resource.hasProperty(SKOS.prefLabel)) {
            return;
        }

        boolean first = true;
        for (StmtIterator iter = resource.listProperties(RDFS.label); iter.hasNext();) {
            Statement stmt = iter.next();
            if (!first) {
                Label commaLabel = new Label(composite, SWT.NONE);
                commaLabel.setText(", ");
            }
            Label commentLabel = new Label(composite, SWT.NONE);
            commentLabel.setText(labelProvider.getText(stmt.getObject()));
            first = false;
        }
        for (StmtIterator iter = resource.listProperties(SKOS.prefLabel); iter.hasNext();) {
            Statement stmt = iter.next();
            if (!first) {
                Label commaLabel = new Label(composite, SWT.NONE);
                commaLabel.setText(", ");
            }
            Label commentLabel = new Label(composite, SWT.NONE);
            commentLabel.setText(labelProvider.getText(stmt.getObject()));
            first = false;
        }
    }

    protected void createPropertyResourcesSection(String title, Resource resource,
            Property predicate) {
        createPropertyResourcesSection(title, resource, predicate, false);
    }

    protected void createPropertyResourcesSection(String title, Resource resource,
            Property predicate, boolean showIcons) {
        if (!resource.hasProperty(predicate)) {
            return;
        }

        RowLayout layout = new RowLayout(SWT.VERTICAL);
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        layout.marginBottom = 0;
        layout.spacing = 1;

        Composite composite = new Composite(this, SWT.NONE);
        composite.setLayout(layout);

        Label label = new Label(composite, SWT.NONE);
        label.setText(title);
        label.setFont(boldFont);

        layout = new RowLayout();
        layout.spacing = (showIcons) ? 2 : 0;
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        layout.marginTop = 1;
        layout.marginLeft = INDENT_WIDTH;

        Composite children = new Composite(composite, SWT.NONE);
        children.setLayout(layout);

        boolean first = true;

        for (StmtIterator iter = resource.listProperties(predicate); iter.hasNext();) {
            Statement stmt = iter.next();
            if (!first) {
                Label commaLabel = new Label(children, SWT.NONE);
                commaLabel.setText(", ");
            }
            if (showIcons) {
                Label iconLabel = new Label(children, SWT.NONE);
                iconLabel.setImage(labelProvider.getImage(stmt.getObject()));
            }
            Label valueLabel = new Label(children, SWT.NONE);
            valueLabel.setText(labelProvider.getText(stmt.getObject()));
            first = false;
        }
    }

    protected void createAllPropertiesSection(Resource resource, List<Property> ignores) {
        boolean titleCreated = false;
        Composite composite = null;

        for (StmtIterator iter = resource.listProperties(); iter.hasNext();) {
            Statement stmt = iter.next();
            if (ignores.contains(stmt.getPredicate())) {
                continue;
            }
            if (!titleCreated) {
                RowLayout layout = new RowLayout(SWT.VERTICAL);
                layout.marginWidth = 0;
                layout.marginHeight = 0;
                layout.marginBottom = 3;
                layout.spacing = 0;

                RowData data = new RowData();
                // data.width = 200;

                composite = new Composite(this, SWT.NONE);
                composite.setLayout(layout);
                composite.setLayoutData(data);

                Label label = new Label(composite, SWT.NONE);
                label.setText("Properties:");
                label.setFont(boldFont);
                titleCreated = true;
            }
            if (titleCreated) {
                RowLayout layout = new RowLayout();
                layout.spacing = 4;
                layout.marginWidth = 0;
                layout.marginHeight = 0;
                layout.marginTop = 2;
                layout.marginBottom = 0;
                layout.marginLeft = INDENT_WIDTH;

                RowData data = new RowData();
                // data.width = 200;

                Composite propertyComposite = new Composite(composite, SWT.NONE);
                propertyComposite.setLayout(layout);
                propertyComposite.setLayoutData(data);

                Label predicateLabel = new Label(propertyComposite, SWT.NONE);
                predicateLabel.setText(String.format("%s", labelProvider.getText("Dummy")));// stmt.getPredicate())));
                predicateLabel.setFont(boldFont);

                Label valueLabel = new Label(propertyComposite, SWT.NONE);
                valueLabel.setText(labelProvider.getText(stmt.getObject()));
                valueLabel.setLayoutData(data);

            }
        }
    }

    protected void createIdentificationSection(Resource resource) {
        RowLayout layout = new RowLayout(SWT.VERTICAL);
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        layout.marginTop = 5;
        layout.spacing = 1;

        Composite composite = new Composite(this, SWT.NONE);
        composite.setLayout(layout);

        Label label = new Label(composite, SWT.NONE);
        if (resource.isAnon()) {
            label.setText("Id:");
        }
        else {
            label.setText("URI:");
        }
        label.setFont(boldFont);

        layout = new RowLayout(SWT.VERTICAL);
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        layout.marginLeft = INDENT_WIDTH;
        layout.spacing = 1;

        composite = new Composite(composite, SWT.NONE);
        composite.setLayout(layout);

        Label uriLabel = new Label(composite, SWT.NONE);
        if (resource.isAnon()) {
            uriLabel.setText(resource.getId().toString());
        }
        else {
            String uri = resource.getURI();
            try {
                uriLabel.setText(URLDecoder.decode(uri, "UTF-8"));
            }
            catch (UnsupportedEncodingException e) {
                uriLabel.setText(uri);
            }
        }
    }

    protected void createProblemsSection(Resource resource) {
        IModelProvider provider = CorePlugin.getDefault().getActiveModelProvider();
        IResource file = null;
        if (provider instanceof OntologyFormEditor) {
            OntologyFormEditor editor = (OntologyFormEditor) provider;
            file = editor.getResource();
        }
        if (file == null) {
            return;
        }

        List<SemanticProblem> problems = SemanticProblem.find(file, resource);
        if (problems.isEmpty()) {
            return;
        }

        RowLayout layout = new RowLayout(SWT.VERTICAL);
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        layout.marginTop = 5;
        layout.spacing = 1;

        Composite composite = new Composite(this, SWT.NONE);
        composite.setLayout(layout);

        Label label = new Label(composite, SWT.NONE);
        label.setText("Semantic problems:");
        label.setFont(boldFont);

        layout = new RowLayout(SWT.VERTICAL);
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        layout.marginLeft = INDENT_WIDTH;
        layout.spacing = 1;

        composite = new Composite(composite, SWT.NONE);
        composite.setLayout(layout);

        // Create labels
        for (SemanticProblem problem : problems) {
            if (!Strings.isNullOrEmpty(problem.getMessage())) {
                String message = problem.getMessage();
                String text = message;

                // String propertyPath = problem.getPropertyPath();
                // if (!Strings.isNullOrEmpty(propertyPath)) {
                // propertyPath =
                // provider.getOntModel().shortForm(propertyPath);
                // text = String.format("%s (@ %s)", message, propertyPath);
                // }

                Label uriLabel = new Label(composite, SWT.NONE);
                uriLabel.setText(text);
            }
        }
    }

    public void addCustomSection(String title, String content) {
        RowLayout layout = new RowLayout(SWT.VERTICAL);
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        layout.marginTop = 5;
        layout.spacing = 1;

        Composite composite = new Composite(this, SWT.NONE);
        composite.setLayout(layout);

        Label label = new Label(composite, SWT.NONE);
        label.setText(title);
        label.setFont(boldFont);

        layout = new RowLayout(SWT.VERTICAL);
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        layout.marginLeft = INDENT_WIDTH;
        layout.spacing = 1;

        composite = new Composite(composite, SWT.NONE);
        composite.setLayout(layout);

        Label contentLabel = new Label(composite, SWT.NONE);
        contentLabel.setText(content);
    }

    public void dispose() {
        super.dispose();

        if (boldFont != null) {
            boldFont.dispose();
        }
    }
}
