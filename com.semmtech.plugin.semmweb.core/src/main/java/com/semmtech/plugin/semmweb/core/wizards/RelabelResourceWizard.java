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

package com.semmtech.plugin.semmweb.core.wizards;


import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.CorePluginImages;
import com.semmtech.plugin.semmweb.core.model.IModelProvider;


public class RelabelResourceWizard extends ModifyModelWizard {
    public static final String ID = "com.semmtech.plugin.semmweb.wizards.relabelResource";

    private RelabelResourceWizardPage relabelPage;
    private Resource resource;

    public RelabelResourceWizard(IModelProvider modelProvider, Resource resource) {
        super(modelProvider);
        setWindowTitle("Relabel Resource");
        setNeedsProgressMonitor(true);

        this.resource = resource;
    }

    @Override
    public void addPages() {
        getShell().setImage(CorePlugin.getDefault().getImage(CorePluginImages.IMG_SEMMTECH_ICON));
        relabelPage = new RelabelResourceWizardPage("relabelPage", model, resource);
        relabelPage.setResource(resource);
        addPage(relabelPage);
    }

    @Override
    public boolean performFinish() {
        updateLabelStatementsFromPage();
        return true;
    }

    private void createUpdateLabelStatements(OntModel ontModel, List<Statement> addStatements,
            List<Statement> removeStatements) {
        ontModel.remove(removeStatements);
        ontModel.add(addStatements);
    }

    private void updateLabelStatementsFromPage() {
        createUpdateLabelStatements(model, relabelPage.getAddedStatements(),
                relabelPage.getRemovedStatements());
    }

    public void init(IWorkbench workbench, IStructuredSelection selection) {

    }

    @Override
    public void setSuppressNotify(boolean suppress) {
        this.suppressNotify = suppress;
    }
}
