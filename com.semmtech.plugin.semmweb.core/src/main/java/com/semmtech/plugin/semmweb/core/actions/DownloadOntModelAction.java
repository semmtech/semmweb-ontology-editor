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


import org.eclipse.core.resources.IContainer;
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
import com.semmtech.plugin.semmweb.core.wizards.DownloadOntModelWizard;


/**
 * 
 * @author Sander Stolk
 */
public class DownloadOntModelAction extends Action {
    protected String url;
    protected IContainer container;
    protected String filename;

    /**
     * The workbench window this action will run in
     */
    private IWorkbenchWindow window;

    /**
     * This default constructor allows the the action to be called from the
     * welcome page.
     */
    public DownloadOntModelAction() {
        this(PlatformUI.getWorkbench().getActiveWorkbenchWindow());
    }

    /**
     * Creates a new action for launching the new project selection wizard.
     * 
     * @param window
     *            the workbench window to query the current selection and shell
     *            for opening the wizard.
     */
    public DownloadOntModelAction(IWorkbenchWindow window) {
        super("Download RDF/OWL Ontology File from Web");
        if (window == null) {
            throw new IllegalArgumentException("window cannot be null");
        }
        this.window = window;
        setDescription("Open a wizard to download an RDF/OWL ontology file from the internet.");
        setImageDescriptor(CorePlugin.getDefault().getImageDescriptor(
                CorePluginImages.IMG_ONTOLOGY_FILE_ADD));
    }

    public DownloadOntModelAction(String url, IContainer container, String filename) {
        super();
        this.url = url;
        this.container = container;
        this.filename = filename;
    }

    @Override
    public void run() {
        // Create wizard selection wizard.
        IWorkbench workbench = PlatformUI.getWorkbench();
        DownloadOntModelWizard wizard = new DownloadOntModelWizard(url, container, filename);

        IStructuredSelection selectionToPass = StructuredSelection.EMPTY;
        if (window != null) {
            ISelection selection = window.getSelectionService().getSelection();
            if (selection instanceof IStructuredSelection) {
                selectionToPass = (IStructuredSelection) selection;
            }
        }
        wizard.init(workbench, selectionToPass);

        // Create wizard dialog.
        WizardDialog dialog = new WizardDialog(null, wizard);
        dialog.create();

        // Open wizard.
        dialog.open();
    }
}
