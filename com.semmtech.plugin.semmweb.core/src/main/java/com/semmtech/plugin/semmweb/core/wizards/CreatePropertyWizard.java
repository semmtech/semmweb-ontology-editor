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
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.CorePluginImages;
import com.semmtech.plugin.semmweb.core.model.IModelProvider;
import com.semmtech.plugin.semmweb.core.util.ResourceURIUtil;
import com.semmtech.semantics.vocabulary.SEMM;


public class CreatePropertyWizard extends CreateResourceWizard {

    protected PropertyPropertiesWizardPage propertiesPage;

    private Property superProperty = null;
    private Property inverseProperty = null;
    private Resource domainResource = null;
    private Resource rangeResource = null;
    private Resource subjectRoleResource = null;
    private Resource objectRoleResource = null;

    public CreatePropertyWizard(String title, IModelProvider modelProvider) {
        this(title, modelProvider, RDF.Property);
    }

    public CreatePropertyWizard(String title, IModelProvider modelProvider, Resource type) {
        super(title, modelProvider, type);
        super.setAnonymousAllowed(false);
    }

    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        if (getContainer() != null) {
            ((IPageChangeProvider) getContainer()).addPageChangedListener(this);
        }
    }

    @Override
    public void createPageControls(Composite pageContainer) {
        super.createPageControls(pageContainer);
        ((IPageChangeProvider) getContainer()).addPageChangedListener(this);
    }

    @Override
    public void addPages() {
        super.addPages();

        createPage.setImageDescriptor(CorePlugin.getDefault().getImageDescriptor(
                CorePluginImages.IMG_BANNER_WIZARD_PROPERTY));
        createPage.setGenerateUUIDEnabled(false);
        createPage.setGenerateUUID(false);
        createPage.setAnonymousEnabled(false);
        createPage.setURIFormat(ResourceURIUtil.FORMAT_PROPERTY);
        createPage.setTitle("Property");

        relabelPage.setImageDescriptor(CorePlugin.getDefault().getImageDescriptor(
                CorePluginImages.IMG_BANNER_WIZARD_PROPERTY));

        propertiesPage = new PropertyPropertiesWizardPage("inversePage", model, null);
        propertiesPage.setInverseProperty(inverseProperty);
        propertiesPage.setSuperProperty(superProperty);
        propertiesPage.setImageDescriptor(CorePlugin.getDefault().getImageDescriptor(
                CorePluginImages.IMG_BANNER_WIZARD_PROPERTY));
        addPage(propertiesPage);
    }

    private void createPropertyStatements(Property property) {
        if (superProperty != null) {
            property.addProperty(RDFS.subPropertyOf, superProperty);
        }
        if (inverseProperty != null) {
            property.addProperty(OWL.inverseOf, inverseProperty);
        }
        if (domainResource != null) {
            property.addProperty(RDFS.domain, domainResource);
        }
        if (rangeResource != null) {
            property.addProperty(RDFS.range, rangeResource);
        }
        if (subjectRoleResource != null) {
            property.addProperty(SEMM.hasSubjectRole, subjectRoleResource);
        }
        if (objectRoleResource != null) {
            property.addProperty(SEMM.hasObjectRole, objectRoleResource);
        }
    }

    @Override
    public boolean performFinish() {
        updateDefaultNamespaceFromPage();
        updateResourceFromPage(true);
        updateLabelStatementsFromPage();

        Property property = resource.as(Property.class);
        updatePropertiesFromPage();

        createPropertyStatements(property);

        return true;
    }

    @Override
    public void pageChanged(PageChangedEvent event) {
        super.pageChanged(event);
        Object page = event.getSelectedPage();
        updatePropertiesFromPage();
        if (page == propertiesPage) {
            propertiesPage.setProperty(resource.as(Property.class));
        }
    }

    private void updatePropertiesFromPage() {
        inverseProperty = propertiesPage.getInverseProperty();
        superProperty = propertiesPage.getSuperProperty();
        domainResource = propertiesPage.getDomainResource();
        rangeResource = propertiesPage.getRangeResource();
        subjectRoleResource = propertiesPage.getSubjectRoleResource();
        objectRoleResource = propertiesPage.getObjectRoleResource();
    }

    public void setSuperProperty(Property superProperty) {
        this.superProperty = superProperty;
    }

    public void setInverseProperty(Property inverseProperty) {
        this.inverseProperty = inverseProperty;
    }
}
