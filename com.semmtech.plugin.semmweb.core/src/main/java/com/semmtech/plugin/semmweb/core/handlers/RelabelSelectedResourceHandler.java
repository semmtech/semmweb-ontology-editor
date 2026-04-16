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
import org.eclipse.jface.action.Action;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

import com.hp.hpl.jena.rdf.model.Resource;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.commands.Commands;
import com.semmtech.plugin.semmweb.core.model.IModelProvider;
import com.semmtech.plugin.semmweb.core.model.ModelTransaction;
import com.semmtech.plugin.semmweb.core.wizards.RelabelResourceWizard;


/**
 * 
 * @author Sander Stolk
 */
public class RelabelSelectedResourceHandler extends SelectedResourceHandler {
    public static final String ID = "com.semmtech.plugin.semmweb.core.commands.relabelSelectedResource";

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        Resource selectedResource = getSelectedResource(event);
        relabelResource(selectedResource);
        return null;
    }

    private static void relabelResource(final Resource resource) {
        IModelProvider modelProvider = CorePlugin.getDefault().getActiveModelProvider();

        if (modelProvider != null && modelProvider.getOntModel() != null && resource != null) {
            Shell parentShell = CorePlugin.getActiveWorkbenchShell();
            RelabelResourceWizard wizard = new RelabelResourceWizard(modelProvider, resource);
            WizardDialog dialog = new WizardDialog(parentShell, wizard);

            // dialog.create();

            String transactionDescription = "Relabeled resource";
            ModelTransaction transaction = modelProvider.createTransaction(transactionDescription);

            if (dialog.open() != Window.OK) {
                modelProvider.abortTransaction(transaction);
            }
            else {
                modelProvider.commitTransaction(transaction);
            }
        }
    }

    public static Action createAction(final String actionText) {
        Action result = new Action(actionText) {
            @Override
            public void run() {
                Commands.execute(ID, null);
            }
        };
        return result;
    }

    public static Action createAction(final String actionText, final Resource resource) {
        Action result = new Action(actionText) {
            @Override
            public void run() {
                relabelResource(resource);
            }
        };
        return result;
    }
}
