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


import org.eclipse.jface.dialogs.IPageChangeProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.RDF;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.CorePluginImages;
import com.semmtech.plugin.semmweb.core.model.IModelProvider;


/**
 * 
 * @author Sander Stolk
 */
public class CreateReifiedStatementWizard extends CreateResourceWizard {
    private Statement statement;

    public CreateReifiedStatementWizard(String title, IModelProvider modelProvider,
            Statement statement) {
        this(title, modelProvider, statement, RDF.Statement);
    }

    public CreateReifiedStatementWizard(String title, IModelProvider modelProvider,
            Statement statement, Resource type) {
        super(title, modelProvider, type);
        super.setAnonymousAllowed(false);
        this.statement = statement;
    }

    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        if (getContainer() != null)
            ((IPageChangeProvider) getContainer()).addPageChangedListener(this);
    }

    @Override
    public void createPageControls(Composite pageContainer) {
        super.createPageControls(pageContainer);
        ((IPageChangeProvider) getContainer()).addPageChangedListener(this);
    }

    @Override
    public void addPages() {
        super.addPages();

        String imageBannerWizard = CorePluginImages.IMG_BANNER_WIZARD_REIFICATION;
        createPage
                .setImageDescriptor(CorePlugin.getDefault().getImageDescriptor(imageBannerWizard));
        createPage.setTitle("Statement");
        createPage.setGenerateUUIDEnabled(false);
        createPage.setGenerateUUID(true);
        relabelPage.setImageDescriptor(CorePlugin.getDefault()
                .getImageDescriptor(imageBannerWizard));

    }

    @Override
    public boolean performFinish() {
        updateDefaultNamespaceFromPage();
        updateResourceFromPage(true);
        updateLabelStatementsFromPage();
        createSubjectPredicateObject();
        return true;
    }

    private void createSubjectPredicateObject() {
        Resource resource = getResource();
        resource.addProperty(RDF.subject, statement.getSubject());
        resource.addProperty(RDF.predicate, statement.getPredicate());
        resource.addProperty(RDF.object, statement.getObject());
    }
}
