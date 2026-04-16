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

package com.semmtech.plugin.semmweb.laces.ldp.handlers;


import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

import com.hp.hpl.jena.ontology.OntModel;
import com.semmtech.plugin.semmweb.core.jobs.LoadModelJob;
import com.semmtech.plugin.semmweb.core.jobs.LoadModelJobAdapter;
import com.semmtech.plugin.semmweb.core.navigator.IModel;
import com.semmtech.plugin.semmweb.laces.ldp.wizards.PublishLDPOntologyWizard;
import com.semmtech.ui.plugin.util.Selections;


/**
 * Allow the user to define a Target server for the selected model and, after
 * that, send the content to the server
 * 
 * @author Mike Henrichs
 */
public class PublishOntologyHandler extends AbstractHandler {
    public static final String ID = "com.semmtech.plugin.semmweb.laces.ldp.commands.publishOntology";

    protected IFile file;
    protected OntModel model;

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        initFile(event);
        Shell parentShell = HandlerUtil.getActiveShell(event);
        return execute(file, parentShell);
    }

    public Object execute(IFile file, Shell shell) throws ExecutionException {
        this.file = file;
        if (file == null || shell == null) {
            return null;
        }

        loadModel();
        if (model == null) {
            return null;
        }

        PublishLDPOntologyWizard wizard = new PublishLDPOntologyWizard(file, model);
        WizardDialog dialog = new WizardDialog(shell, wizard);
        dialog.create();
        dialog.open();

        return null;
    }

    protected void initFile(ExecutionEvent event) {
        ISelection selection = HandlerUtil.getCurrentSelection(event);
        IModel model = Selections.retrieveFirstAsType(selection, IModel.class);

        if (model != null) {
            file = (IFile) model.getResource();
        }
        else {
            file = Selections.retrieveFirstAsType(selection, IFile.class);
        }
    }

    protected void loadModel() {
        final OntModel loadedModels[] = { null };
        LoadModelJob loadModelJob = new LoadModelJob(file, file.getName(), true, true);
        loadModelJob.setListener(new LoadModelJobAdapter() {
            @Override
            public void modelLoaded(OntModel model) {
                loadedModels[0] = model;
            }
        });
        loadModelJob.schedule();
        try {
            loadModelJob.join();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (loadedModels[0] != null) {
            model = loadedModels[0];
        }
    }
}
