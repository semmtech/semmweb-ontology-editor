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

package com.semmtech.plugin.semmweb.core.actions;


import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;
import org.eclipse.ui.navigator.ICommonMenuConstants;
import org.eclipse.ui.navigator.ICommonViewerSite;
import org.eclipse.ui.navigator.ICommonViewerWorkbenchSite;

import com.semmtech.plugin.semmweb.core.navigator.IModel;
import com.semmtech.plugin.semmweb.core.wizards.AnonymizeWizard;
import com.semmtech.plugin.semmweb.core.wizards.SkolemizeWizard;
import com.semmtech.ui.plugin.navigator.CommonViewerActionProvider;
import com.semmtech.ui.plugin.util.Selections;


public class ModelConversionActionProvider extends CommonViewerActionProvider {

    private IWorkbenchWindow window;

    public ModelConversionActionProvider() {

    }

    @Override
    public void init(ICommonActionExtensionSite site) {
        super.init(site);

        ICommonViewerSite viewerSite = site.getViewSite();
        if (viewerSite instanceof ICommonViewerWorkbenchSite) {
            window = getWindow();

            // newModelAction = new NewOntologyFileAction(window);
            // newModelAction.setText("RDF/OWL Model");
            //
            // importModelAction = new ImportOntologyAction(window);
            // pasteModelAction = new PasteModelAction(window);
        }
    }

    @Override
    public void fillContextMenu(IMenuManager parent) {
        final ISelection selection = getSelection();
        Object selected = Selections.retrieveFirst(selection);

        if (selected instanceof IModel) {
            IModel model = (IModel) selected;
            final IFile modelFile = (IFile) model.getResource();

            MenuManager menu = new MenuManager("Convert", "model.conversion");

            menu.add(new Action("Skolemize Model...") {
                @Override
                public void run() {
                    Shell shell = window.getShell();
                    SkolemizeWizard wizard = new SkolemizeWizard(modelFile);
                    wizard.init(window.getWorkbench(), (IStructuredSelection) selection);
                    WizardDialog dialog = new WizardDialog(shell, wizard);
                    dialog.open();

                }
            });

            menu.add(new Action("Anonymize Model...") {
                @Override
                public void run() {
                    Shell shell = window.getShell();
                    AnonymizeWizard wizard = new AnonymizeWizard(modelFile);
                    wizard.init(window.getWorkbench(), (IStructuredSelection) selection);
                    WizardDialog dialog = new WizardDialog(shell, wizard);
                    dialog.open();

                }
            });

            parent.appendToGroup(ICommonMenuConstants.GROUP_ADDITIONS, menu);
        }

    }
}
