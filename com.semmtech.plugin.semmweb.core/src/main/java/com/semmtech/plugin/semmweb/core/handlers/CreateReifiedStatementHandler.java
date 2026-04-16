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
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.handlers.HandlerUtil;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.RDF;
import com.semmtech.plugin.semmweb.core.commands.Commands;
import com.semmtech.plugin.semmweb.core.model.IModelProvider;
import com.semmtech.plugin.semmweb.core.model.ModelTransaction;
import com.semmtech.plugin.semmweb.core.wizards.CreateReifiedStatementWizard;


/**
 * 
 * @author Sander Stolk
 */
public class CreateReifiedStatementHandler extends CreateResourceHandler {
    public static final String ID = "com.semmtech.plugin.semmweb.core.commands.createResource";

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        OntModel model = getActiveOntModel(event);
        IStructuredSelection selection = getStructuredSelection(event);
        if ((model == null) || (selection == null)) {
            return null;
        }
        if ((selection.getFirstElement() == null)
                || !(selection.getFirstElement() instanceof Statement)) {
            return null;
        }
        Statement selectedStatement = (Statement) selection.getFirstElement();

        Resource type = getResourceType(model, event);
        boolean anonymousAllowed = getAnonymousAllowed(event);
        boolean noWizard = getNoWizard(event);

        if (!noWizard) {
            String wizardTitle = getDefaultWizardTitle();

            IModelProvider modelProvider = getActiveModelProvider(event);
            CreateReifiedStatementWizard wizard = new CreateReifiedStatementWizard(wizardTitle,
                    modelProvider, selectedStatement, type);
            wizard.setAnonymousAllowed(anonymousAllowed);
            wizard.setBaseModel(getActiveBaseModel(event));

            Shell parentShell = HandlerUtil.getActiveShell(event);
            WizardDialog dialog = new WizardDialog(parentShell, wizard);
            dialog.create();
            ModelTransaction transaction = modelProvider
                    .createTransaction("Created a new reified statement");
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
        return null;
    }

    @Override
    protected Resource getDefaultResourceType(OntModel model) {
        return model.getResource(RDF.Statement.getURI());
    }

    @Override
    protected String getDefaultWizardTitle() {
        return "New Reified Statement";
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
