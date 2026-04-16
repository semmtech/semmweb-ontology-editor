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
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.OWL;
import com.semmtech.plugin.semmweb.core.model.IModelProvider;
import com.semmtech.plugin.semmweb.core.model.ModelTransaction;
import com.semmtech.plugin.semmweb.core.model.RestrictionType;
import com.semmtech.plugin.semmweb.core.wizards.CreateRestrictionWizard;


public class CreateRestrictionHandler extends CreateResourceHandler {
    public static final String ID = "com.semmtech.plugin.semmweb.core.commands.createRestriction";

    public static final String PARAMETER_RESTRICTION_TYPE = "restrictionType";
    public static final String PARAMETER_TYPE_PROPERTY_URI = "typePropertyURI";
    public static final String PARAMETER_ON_PROPERTY_URI = "onPropertyURI";

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        IModelProvider modelProvider = getActiveModelProvider(event);
        getStructuredSelection(event);
        getSelectedResource(event);
        if (modelProvider != null) {
            OntModel model = modelProvider.getOntModel();

            // Get any of the the wizard parameters
            Resource type = getResourceType(model, event);
            Property onProperty = getOnProperty(model, event);
            Property typeProperty = getTypeProperty(model, event);
            Property labelProperty = getLabelProperty(model, event);
            boolean noWizard = getNoWizard(event);

            if (!noWizard) {
                String wizardTitle = getWizardTitle(event, type);
                CreateRestrictionWizard wizard = new CreateRestrictionWizard(wizardTitle,
                        getActiveModelProvider(event), type, null);
                wizard.setAnonymousAllowed(true);
                wizard.setLabelProperty(labelProperty);
                if (typeProperty != null) {
                    wizard.setTypeProperty(typeProperty);
                }
                else {
                    wizard.setTypeProperty(OWL.allValuesFrom);
                }
                if (onProperty != null) {
                    wizard.setOnProperty(onProperty);
                }

                Shell parentShell = HandlerUtil.getActiveShell(event);
                WizardDialog dialog = new WizardDialog(parentShell, wizard);
                dialog.create();

                ModelTransaction transaction = modelProvider
                        .createTransaction("Created a new restriction");
                if (dialog.open() != Window.OK) {
                    modelProvider.abortTransaction(transaction);
                }
                else {
                    if (wizard.openResourceEditor()) {
                        for (Resource selectedResource : wizard.getRestrictions()) {
                            openResource(event, selectedResource);
                        }
                    }
                    modelProvider.commitTransaction(transaction);
                }
            }
        }
        return null;
    }

    @Override
    protected Resource getDefaultResourceType(OntModel model) {
        return model.getResource(OWL.Restriction.getURI());
    }

    @Override
    protected String getDefaultWizardTitle() {
        return "New Restriction";
    }

    /**
     * Returns the resource provided through the command parameter, can be null
     * if no parameter value has been provided.
     * 
     * @param model
     * @param event
     * @return
     */
    private Property getOnProperty(OntModel model, ExecutionEvent event) {
        String propertyUri = event.getParameter(PARAMETER_ON_PROPERTY_URI);
        Property property = null;
        if (propertyUri != null) {
            property = model.getProperty(propertyUri);
        }
        return property;
    }

    private Property getTypeProperty(OntModel model, ExecutionEvent event) {
        String propertyUri = event.getParameter(PARAMETER_TYPE_PROPERTY_URI);
        Property property = null;
        if (propertyUri != null) {
            property = model.getProperty(propertyUri);
        }
        return property;
    }

    /**
     * TODO: Update the plugin.xml extensions
     * 
     * @param event
     * @return
     */
    @SuppressWarnings("unused")
    private int getRestrictionType(ExecutionEvent event) {
        if (event.getParameter(PARAMETER_RESTRICTION_TYPE) != null) {
            return Integer.parseInt(event.getParameter(PARAMETER_RESTRICTION_TYPE));
        }
        return RestrictionType.NONE;
    }
}
