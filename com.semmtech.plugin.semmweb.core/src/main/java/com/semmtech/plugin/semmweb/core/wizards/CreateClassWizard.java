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
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.CorePluginImages;
import com.semmtech.plugin.semmweb.core.model.IModelProvider;
import com.semmtech.plugin.semmweb.core.model.ResourceStatements;


public class CreateClassWizard extends CreateResourceWizard {

    protected ClassPropertiesWizardPage propertiesPage;

    private Resource superClass = null;

    public CreateClassWizard(String title, IModelProvider modelProvider) {
        this(title, modelProvider, RDFS.Class);
    }

    public CreateClassWizard(String title, IModelProvider modelProvider, Resource type) {
        super(title, modelProvider, type);
        super.setAnonymousAllowed(false);
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

        String imageBannerWizard = type.equals(OWL.Class) ? CorePluginImages.IMG_BANNER_WIZARD_OWL_CLASS
                : CorePluginImages.IMG_BANNER_WIZARD_RDFS_CLASS;

        createPage
                .setImageDescriptor(CorePlugin.getDefault().getImageDescriptor(imageBannerWizard));
        createPage.setGenerateUUIDEnabled(false);
        createPage.setGenerateUUID(false);
        createPage.setAnonymousEnabled(true);
        createPage.setTitle("Class");
        relabelPage.setImageDescriptor(CorePlugin.getDefault()
                .getImageDescriptor(imageBannerWizard));

        propertiesPage = new ClassPropertiesWizardPage("propertiesPage", model, null, superClass);
        // TODO: set these images to those of RDF class whenever applicable
        propertiesPage.setImageDescriptor(CorePlugin.getDefault().getImageDescriptor(
                imageBannerWizard));
        addPage(propertiesPage);
    }

    @Override
    public boolean performFinish() {
        updateDefaultNamespaceFromPage();
        updateResourceFromPage(true);
        updateLabelStatementsFromPage();

        Property property = resource.as(Property.class);
        updatePropertiesFromPage();
        if (superClass != null) {
            ResourceStatements.createResourceAsSubclassStatements(property, superClass);
        }
        return true;
    }

    @Override
    public void pageChanged(PageChangedEvent event) {
        super.pageChanged(event);
        Object page = event.getSelectedPage();
        updatePropertiesFromPage();
        if (page == propertiesPage) {
            propertiesPage.setClass(resource.as(Property.class));
        }
    }

    private void updatePropertiesFromPage() {
        superClass = propertiesPage.getSuperClass();
    }

    public void setSuperClass(Resource superClass) {
        this.superClass = superClass;
    }
}
