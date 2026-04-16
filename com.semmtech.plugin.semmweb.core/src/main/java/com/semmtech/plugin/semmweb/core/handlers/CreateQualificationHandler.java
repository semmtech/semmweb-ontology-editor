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
import com.semmtech.plugin.semmweb.core.model.IModelProvider;
import com.semmtech.plugin.semmweb.core.model.ModelTransaction;
import com.semmtech.plugin.semmweb.core.model.ResourceStatements;
import com.semmtech.plugin.semmweb.core.wizards.CreateResourceWizard;
import com.semmtech.semantics.vocabulary.SEMM;


public class CreateQualificationHandler extends CreateResourceHandler {
    public static final String ID = "com.semmtech.plugin.semmweb.core.commands.createQualification";

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        IModelProvider modelProvider = getActiveModelProvider(event);
        OntModel model = getActiveOntModel(event);
        if (model != null) {
            Resource nature = getSelectedResource(event);

            // Get any of the the wizard parameters
            Resource type = getResourceType(model, event);
            Property labelProperty = getLabelProperty(model, event);
            boolean noWizard = getNoWizard(event);

            if (!noWizard) {
                String wizardTitle = getWizardTitle(event, type);
                CreateResourceWizard wizard = new CreateResourceWizard(wizardTitle,
                        getActiveModelProvider(event), type);
                wizard.setAnonymousAllowed(false);
                wizard.setLabelProperty(labelProperty);
                wizard.setSuppressNotify(true);

                Shell parentShell = HandlerUtil.getActiveShell(event);
                WizardDialog dialog = new WizardDialog(parentShell, wizard);
                dialog.create();

                String transactionDescription = "Created a new qualification";
                ModelTransaction transaction = modelProvider
                        .createTransaction(transactionDescription);
                if (dialog.open() != Window.OK) {
                    modelProvider.abortTransaction(transaction);
                }
                else {
                    Resource qualification = wizard.getResource();

                    ResourceStatements.createResourceAsQualification(qualification, nature);

                    modelProvider.commitTransaction(transaction);
                    if (wizard.openResourceEditor()) {
                        openResource(event, wizard.getResource());
                    }
                }
            }
        }
        return null;
    }

    @Override
    protected Resource getDefaultResourceType(OntModel model) {
        return model.getResource(SEMM.Qualification.getURI());
    }

    @Override
    protected String getDefaultWizardTitle() {
        return "New Qualification";
    }
}
