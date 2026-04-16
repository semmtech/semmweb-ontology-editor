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
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;
import com.semmtech.plugin.semmweb.core.model.IModelProvider;
import com.semmtech.plugin.semmweb.core.model.ModelTransaction;
import com.semmtech.plugin.semmweb.core.wizards.EditPropertyWizard;
import com.semmtech.semantics.util.JenaUtil;


/**
 * 
 * @author Sander Stolk
 */
public class EditPropertyHandler extends CreateResourceHandler {
    public static final String ID = "com.semmtech.plugin.semmweb.core.commands.editProperty";

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        IModelProvider provider = getActiveModelProvider(event);
        IStructuredSelection selection = getStructuredSelection(event);
        Resource selected = getSelectedResource(event);
        if (provider != null) {
            OntModel model = provider.getOntModel();

            // Get any of the the wizard parameters
            Resource type = getResourceType(model, event);
            boolean noWizard = getNoWizard(event);

            if (!noWizard) {
                String wizardTitle = getWizardTitle(event, type);
                EditPropertyWizard wizard = new EditPropertyWizard(wizardTitle, provider,
                        JenaUtil.asOntProperty(selected, model));
                IWorkbench workbench = HandlerUtil.getActiveWorkbenchWindow(event).getWorkbench();
                wizard.init(workbench, selection);

                Shell parentShell = HandlerUtil.getActiveShell(event);
                WizardDialog dialog = new WizardDialog(parentShell, wizard);
                dialog.create();

                ModelTransaction transaction = provider.createTransaction("Edited property");
                if (dialog.open() != Window.OK) {
                    provider.abortTransaction(transaction);
                }
                else {
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
        return "Edit Property";
    }
}
