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


import java.util.Map;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.handlers.HandlerUtil;

import com.google.common.base.Strings;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.semmtech.plugin.semmweb.core.commands.Commands;
import com.semmtech.plugin.semmweb.core.model.IModelProvider;
import com.semmtech.plugin.semmweb.core.model.ModelTransaction;
import com.semmtech.plugin.semmweb.core.wizards.CreateClassWizard;
import com.semmtech.plugin.semmweb.core.wizards.CreateResourceWizard;


public class CreateResourceHandler extends SelectedResourceHandler {
    public static final String ID = "com.semmtech.plugin.semmweb.core.commands.createResource";

    public static final String PARAMETER_RESOURCE_TYPE_URI = "resourceTypeURI";
    public static final String PARAMETER_RESOURCE_TYPE_SELECTION = "resourceTypeSelection";
    public static final String PARAMETER_ANONYMOUS_ALLOWED = "anonymousAllowed";
    public static final String PARAMETER_NO_WIZARD = "noWizard";
    public static final String PARAMETER_WIZARD_TITLE = "wizardTitle";
    public static final String PARAMETER_LABEL_PROPERTY_URI = "labelPropertyURI";

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        OntModel model = getActiveOntModel(event);
        if (model != null) {
            Resource type = getResourceType(model, event);
            Property labelProperty = getLabelProperty(model, event);
            boolean anonymousAllowed = getAnonymousAllowed(event);
            boolean noWizard = getNoWizard(event);

            if (!noWizard) {
                String wizardTitle = getWizardTitle(event, type);
                IModelProvider modelProvider = getActiveModelProvider(event);
                CreateResourceWizard wizard = (type.equals(OWL.Class) || type.equals(RDFS.Class)) ? new CreateClassWizard(
                        wizardTitle, modelProvider, type) : new CreateResourceWizard(wizardTitle,
                        modelProvider, type);
                wizard.setAnonymousAllowed(anonymousAllowed);
                wizard.setLabelProperty(labelProperty);
                wizard.setBaseModel(getActiveBaseModel(event));

                Shell parentShell = HandlerUtil.getActiveShell(event);
                WizardDialog dialog = new WizardDialog(parentShell, wizard);
                dialog.create();
                ModelTransaction transaction = modelProvider
                        .createTransaction("Created a new resource");
                if (dialog.open() != Window.OK) {
                    modelProvider.abortTransaction(transaction);
                }
                else {
                    if (wizard.openResourceEditor()) {
                        openResource(event, wizard.getResource());
                    }
                    modelProvider.commitTransaction(transaction);
                }
            }
        }
        return null;
    }

    /**
     * Returns the resource provided through the command parameter. If no such
     * parameter is available, the resource will be returned that is provided by
     * the variable set in the parameter resourceTypeSelection. Otherwise, the
     * resource returned by <code>getDefaultResourceType</code> will be
     * returned.
     * 
     * @param model
     * @param event
     * @return
     */
    protected Resource getResourceType(OntModel model, ExecutionEvent event) {
        String typeUri = event.getParameter(PARAMETER_RESOURCE_TYPE_URI);
        Resource type = getDefaultResourceType(model);
        if (typeUri != null) {
            type = model.getResource(typeUri);
        }
        else {
            String variable = event.getParameter(PARAMETER_RESOURCE_TYPE_SELECTION);
            if (!Strings.isNullOrEmpty(variable)) {
                if (getProvidedResource(event, variable) != null) {
                    type = getProvidedResource(event, variable);
                }
            }
        }
        return type;
    }

    protected Resource getDefaultResourceType(OntModel model) {
        return model.getResource(OWL.Thing.getURI());
    }

    /**
     * Returns the label property provided through the command parameter, can be
     * null if no parameter value has been provided.
     * 
     * @param model
     * @param event
     * @return
     */
    protected Property getLabelProperty(OntModel model, ExecutionEvent event) {
        String propertyUri = event.getParameter(PARAMETER_LABEL_PROPERTY_URI);
        Property property = model.getProperty(RDFS.label.getURI());
        if (propertyUri != null) {
            property = model.getProperty(propertyUri);
        }
        return property;
    }

    /**
     * Returns a boolean identifying if resources may be anonymous; default is
     * true if no parameter has been provided to the command.
     * 
     * @param event
     * @return
     */
    protected boolean getAnonymousAllowed(ExecutionEvent event) {
        if (event.getParameter(PARAMETER_ANONYMOUS_ALLOWED) != null) {
            return Boolean.parseBoolean(event.getParameter(PARAMETER_ANONYMOUS_ALLOWED));
        }
        return true;
    }

    /**
     * Returns a boolean identifying if resources is created without a wizard
     * dialog (default name will be localname of type plus an numerical unique
     * identifier)
     * 
     * @param event
     * @return
     */
    protected boolean getNoWizard(ExecutionEvent event) {
        if (event.getParameter(PARAMETER_NO_WIZARD) != null) {
            return Boolean.parseBoolean(event.getParameter(PARAMETER_NO_WIZARD));
        }
        return false;
    }

    /**
     * Returns a String to be used as title for the wizard.
     * 
     * @param event
     * @return
     */
    protected String getWizardTitle(ExecutionEvent event, Resource type) {
        if (event.getParameter(PARAMETER_WIZARD_TITLE) != null) {
            return event.getParameter(PARAMETER_WIZARD_TITLE).toString();
        }
        if (type.equals(OWL.Class) || type.equals(RDFS.Class)) {
            return "New Class";
        }
        return getDefaultWizardTitle();
    }

    protected String getDefaultWizardTitle() {
        return "New Resource";
    }

    public static Action createAction(final IWorkbenchPartSite site, final String actionText,
            final Map<String, String> parameterizationMap) {
        Action result = new Action(actionText) {
            @Override
            public void run() {
                Commands.execute(ID, parameterizationMap);
            }
        };
        return result;
    }
}
