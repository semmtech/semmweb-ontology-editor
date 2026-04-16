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

package com.semmtech.plugin.semmweb.core.handlers;


import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.handlers.HandlerUtil;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;
import com.semmtech.plugin.semmweb.core.model.IModelProvider;
import com.semmtech.plugin.semmweb.core.model.ModelTransaction;
import com.semmtech.plugin.semmweb.core.wizards.CreatePropertyWizard;


public class CreatePropertyHandler extends CreateResourceHandler {
    public static final String ID = "com.semmtech.plugin.semmweb.core.commands.createProperty";

    public static final String PARAMETER_AS_SUB_PROPERTY = "asSubProperty";
    public static final String PARAMETER_SUPER_PROPERTY_URI = "superPropertyURI";
    public static final String PARAMETER_AS_INVERSE_PROPERTY = "asInverseProperty";
    public static final String PARAMETER_INVERSE_PROPERTY_URI = "inversePropertyURI";

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        IModelProvider provider = getActiveModelProvider(event);
        IStructuredSelection selection = getStructuredSelection(event);
        Resource selected = getSelectedResource(event);
        if (provider != null) {
            OntModel model = provider.getOntModel();

            // Get any of the the wizard parameters
            Resource type = getResourceType(model, event);
            boolean asSubProperty = getAsSubProperty(event);
            boolean asInverseProperty = getAsInverseProperty(event);
            Property superProperty = getSuperProperty(model, event);
            Property inverseProperty = getInverseProperty(model, event);
            Property labelProperty = getLabelProperty(model, event);
            boolean noWizard = getNoWizard(event);

            // Retrieve the super property from current selection
            if (superProperty == null && asSubProperty) {
                if (selected != null && selected instanceof Property)
                    superProperty = (Property) selected;
            }
            // Retrieve the inverse property from current selection
            else if (inverseProperty == null && asInverseProperty) {
                if (selected != null && selected instanceof Property)
                    inverseProperty = (Property) selected;
            }

            if (!noWizard) {
                String wizardTitle = getWizardTitle(event, type);
                CreatePropertyWizard wizard = new CreatePropertyWizard(wizardTitle, provider, type);
                IWorkbench workbench = HandlerUtil.getActiveWorkbenchWindow(event).getWorkbench();
                wizard.init(workbench, selection);
                wizard.setLabelProperty(labelProperty);
                if (superProperty != null)
                    wizard.setSuperProperty(superProperty);
                else if (inverseProperty != null)
                    wizard.setInverseProperty(inverseProperty);

                Shell parentShell = HandlerUtil.getActiveShell(event);
                WizardDialog dialog = new WizardDialog(parentShell, wizard);
                dialog.create();

                ModelTransaction transaction = provider.createTransaction("Add new property");
                if (dialog.open() != Window.OK) {
                    provider.abortTransaction(transaction);
                }
                else {
                    if (wizard.openResourceEditor()) {
                        Resource selectedResource = wizard.getResource();
                        openResource(event, selectedResource);
                    }
                    provider.commitTransaction(transaction);
                }
            }
        }

        return null;
    }

    @Override
    protected Resource getDefaultResourceType(OntModel model) {
        return model.getResource(RDF.Property.getURI());
    }

    @Override
    protected String getDefaultWizardTitle() {
        return "New Property";
    }

    /**
     * Returns the resource provided through the command parameter, can be null
     * if no parameter value has been provided.
     * 
     * @param model
     * @param event
     * @return
     */
    private Property getSuperProperty(OntModel model, ExecutionEvent event) {
        String propertyUri = event.getParameter(PARAMETER_SUPER_PROPERTY_URI);
        Property property = null;
        if (propertyUri != null)
            property = model.getProperty(propertyUri);
        return property;
    }

    /**
     * Returns the inverse property provided through the command parameter, can
     * be null if no parameter value has been provided.
     * 
     * @param model
     * @param event
     * @return
     */
    private Property getInverseProperty(OntModel model, ExecutionEvent event) {
        String propertyUri = event.getParameter(PARAMETER_INVERSE_PROPERTY_URI);
        Property property = null;
        if (propertyUri != null)
            property = model.getProperty(propertyUri);
        return property;
    }

    private boolean getAsSubProperty(ExecutionEvent event) {
        if (event.getParameter(PARAMETER_AS_SUB_PROPERTY) != null)
            return Boolean.parseBoolean(event.getParameter(PARAMETER_AS_SUB_PROPERTY));
        return false;
    }

    private boolean getAsInverseProperty(ExecutionEvent event) {
        if (event.getParameter(PARAMETER_AS_INVERSE_PROPERTY) != null)
            return Boolean.parseBoolean(event.getParameter(PARAMETER_AS_INVERSE_PROPERTY));
        return false;
    }
}
