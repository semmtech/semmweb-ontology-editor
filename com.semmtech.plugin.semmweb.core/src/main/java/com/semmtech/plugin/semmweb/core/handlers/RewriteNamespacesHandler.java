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


import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;
import org.eclipse.ui.services.IServiceLocator;

import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.navigator.IModel;
import com.semmtech.plugin.semmweb.core.navigator.ISemanticElement;
import com.semmtech.plugin.semmweb.core.wizards.RewriteNamespacesWizard;
import com.semmtech.semantics.util.NamespaceRewriteRule;
import com.semmtech.ui.plugin.util.Selections;


/**
 * 
 * @author Sander Stolk
 */
public class RewriteNamespacesHandler extends AbstractHandler {

    public static final String ID = "com.semmtech.plugin.semmweb.core.commands.rewriteNamespaces";

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        ISelection selection = HandlerUtil.getCurrentSelection(event);
        IModel selectedModel = null;

        ISemanticElement selected = Selections.retrieveFirstAsType(selection,
                ISemanticElement.class);
        if (selected != null) {
            if (selected instanceof IModel) {
                selectedModel = (IModel) selected;
            }
            else {
                ISemanticElement model = selected.getAncestor(ISemanticElement.MODEL);
                selectedModel = (IModel) model;
            }
        }

        if (selectedModel != null) {
            if (selectedModel.getResource() instanceof IFile) {
                IFile file = (IFile) selectedModel.getResource();
                execute(file, null);
            }
        }
        return null;
    }

    /** The argument <code>initialRules</code> may be null. */
    public static void execute(IFile file, List<NamespaceRewriteRule> initialRules) {
        Shell shell = Display.getDefault().getActiveShell();
        if (shell != null) {
            WizardDialog dialog = new WizardDialog(shell, new RewriteNamespacesWizard(file,
                    initialRules));
            dialog.setBlockOnOpen(true);
            dialog.open();
        }
    }

    /** The argument <code>initialRules</code> may be null. */
    public static IAction createAction(final IFile file,
            final List<NamespaceRewriteRule> initialRules) {
        return new Action("Rewrite...") {
            @Override
            public void run() {
                execute(file, initialRules);
            }
        };
    }

    public static CommandContributionItem createCommand(IServiceLocator serviceLocator,
            String label, String imageKey) {
        CommandContributionItemParameter param = new CommandContributionItemParameter(
                serviceLocator, null, ID, SWT.PUSH);
        param.label = label;
        param.icon = CorePlugin.getDefault().getImageDescriptor(imageKey);
        return new CommandContributionItem(param);
    }
}
