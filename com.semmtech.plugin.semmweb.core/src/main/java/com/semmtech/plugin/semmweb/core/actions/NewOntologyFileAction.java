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


import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.CorePluginImages;
import com.semmtech.plugin.semmweb.core.wizards.NewOntologyFileWizard;


public class NewOntologyFileAction extends Action {

    /**
     * The wizard dialog width
     */
    @SuppressWarnings("unused")
    private static final int SIZING_WIZARD_WIDTH = 500;

    /**
     * The wizard dialog height
     */
    @SuppressWarnings("unused")
    private static final int SIZING_WIZARD_HEIGHT = 500;

    /**
     * The workbench window this action will run in
     */
    private IWorkbenchWindow window;

    /**
     * This default constructor allows the the action to be called from the
     * welcome page.
     */
    public NewOntologyFileAction() {
        this(PlatformUI.getWorkbench().getActiveWorkbenchWindow());
    }

    /**
     * Creates a new action for launching the new project selection wizard.
     * 
     * @param window
     *            the workbench window to query the current selection and shell
     *            for opening the wizard.
     */
    public NewOntologyFileAction(IWorkbenchWindow window) {
        super("RDF/OWL Ontology File");
        if (window == null) {
            throw new IllegalArgumentException("window cannot be null");
        }
        this.window = window;
        setDescription("Open a wizard to create a new RDF/OWL ontology file.");
        setImageDescriptor(CorePlugin.getDefault().getImageDescriptor(
                CorePluginImages.IMG_ONTOLOGY_FILE_ADD));
    }

    /*
     * (non-Javadoc) Method declared on IAction.
     */
    @Override
    public void run() {
        // Create wizard selection wizard.
        IWorkbench workbench = PlatformUI.getWorkbench();
        NewOntologyFileWizard wizard = new NewOntologyFileWizard();

        ISelection selection = window.getSelectionService().getSelection();
        IStructuredSelection selectionToPass = StructuredSelection.EMPTY;
        if (selection instanceof IStructuredSelection) {
            selectionToPass = (IStructuredSelection) selection;
        }
        wizard.init(workbench, selectionToPass);

        // Create wizard dialog.
        WizardDialog dialog = new WizardDialog(null, wizard);
        dialog.create();

        // Open wizard.
        dialog.open();
    }
}
