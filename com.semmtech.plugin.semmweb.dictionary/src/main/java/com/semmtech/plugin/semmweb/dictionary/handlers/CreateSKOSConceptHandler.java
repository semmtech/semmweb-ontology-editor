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

package com.semmtech.plugin.semmweb.dictionary.handlers;


import org.apache.log4j.Logger;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.handlers.HandlerUtil;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.model.IModelProvider;
import com.semmtech.plugin.semmweb.core.model.ModelTransaction;
import com.semmtech.plugin.semmweb.dictionary.wizards.CreateSKOSConceptWizard;


public class CreateSKOSConceptHandler extends AbstractHandler {
    public static final String ID = "com.semmtech.plugin.semmweb.dictionary.commands.createSKOSConcept";
    private static final Logger logger = Logger.getLogger(CreateSKOSConceptHandler.class);

    public static final String PARAMETER_IN_SCHEME = "inScheme";
    public static final String PARAMETER_IS_TOP_CONCEPT = "isTopConcept";
    public static final String PARAMETER_IN_COLLECTION = "inCollection";
    public static final String PARAMETER_IS_NARROWER = "isNarrower";
    public static final String PARAMETER_IS_BROADER = "isBroader";
    public static final String PARAMETER_IS_RELATED = "isRelated";
    public static final String PARAMETER_LABEL_PROPERTY_URI = "labelPropertyURI";

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        logger.debug("CreateSKOSConceptHandler.execute() called!");
        ISelection selection = HandlerUtil.getCurrentSelection(event);
        if (selection instanceof IStructuredSelection) {
            IStructuredSelection structuredSelection = (IStructuredSelection) selection;
            IEditorPart editorPart = HandlerUtil.getActiveEditor(event);
            if (editorPart instanceof IModelProvider) {
                IModelProvider provider = (IModelProvider) editorPart;
                OntModel model = ((IModelProvider) editorPart).getOntModel();
                Property labelProperty = getLabelProperty(model, event);

                CreateSKOSConceptWizard wizard = new CreateSKOSConceptWizard("New Concept", model);
                wizard.setIsTopConcept(getIsTopConcept(event));
                wizard.setInScheme(getInScheme(event));
                wizard.setInCollection(getInCollection(event));
                wizard.setIsNarrower(getIsNarrower(event));
                wizard.setIsBroader(getIsBroader(event));
                wizard.setIsRelated(getIsRelated(event));
                wizard.setLabelProperty(labelProperty);

                IWorkbench workbench = HandlerUtil.getActiveWorkbenchWindow(event).getWorkbench();
                wizard.init(workbench, structuredSelection);

                Shell parentShell = HandlerUtil.getActiveShell(event);
                WizardDialog dialog = new WizardDialog(parentShell, wizard);

                dialog.create();

                String transactionDescription = "Create SKOS concept wizard has finished";
                ModelTransaction transaction = provider.createTransaction(transactionDescription);
                if (dialog.open() != Window.OK) {
                    provider.abortTransaction(transaction);
                }
                else {
                    if (wizard.openResourceEditor()) {
                        Resource selectedResource = wizard.getResource();
                        CorePlugin.getDefault().openResource(selectedResource);
                    }
                    provider.commitTransaction(transaction);
                }
            }
        }
        return null;
    }

    private boolean getIsTopConcept(ExecutionEvent event) {
        if (event.getParameter(PARAMETER_IS_TOP_CONCEPT) != null)
            return Boolean.parseBoolean(event.getParameter(PARAMETER_IS_TOP_CONCEPT));
        return false;
    }

    private boolean getInScheme(ExecutionEvent event) {
        if (event.getParameter(PARAMETER_IN_SCHEME) != null)
            return Boolean.parseBoolean(event.getParameter(PARAMETER_IN_SCHEME));
        return false;
    }

    private boolean getInCollection(ExecutionEvent event) {
        if (event.getParameter(PARAMETER_IN_COLLECTION) != null)
            return Boolean.parseBoolean(event.getParameter(PARAMETER_IN_COLLECTION));
        return false;
    }

    private boolean getIsNarrower(ExecutionEvent event) {
        if (event.getParameter(PARAMETER_IS_NARROWER) != null)
            return Boolean.parseBoolean(event.getParameter(PARAMETER_IS_NARROWER));
        return false;
    }

    private boolean getIsBroader(ExecutionEvent event) {
        if (event.getParameter(PARAMETER_IS_BROADER) != null)
            return Boolean.parseBoolean(event.getParameter(PARAMETER_IS_BROADER));
        return false;
    }

    private boolean getIsRelated(ExecutionEvent event) {
        if (event.getParameter(PARAMETER_IS_RELATED) != null)
            return Boolean.parseBoolean(event.getParameter(PARAMETER_IS_RELATED));
        return false;
    }

    /**
     * Returns the label property provided through the command patameter, can be
     * null if no parameter value has been provided.
     * 
     * @param model
     * @param event
     * @return
     */
    private Property getLabelProperty(OntModel model, ExecutionEvent event) {
        String propertyUri = event.getParameter(PARAMETER_LABEL_PROPERTY_URI);
        Property property = model.getProperty(RDFS.label.getURI());
        if (propertyUri != null)
            property = model.getProperty(propertyUri);
        return property;
    }
}
