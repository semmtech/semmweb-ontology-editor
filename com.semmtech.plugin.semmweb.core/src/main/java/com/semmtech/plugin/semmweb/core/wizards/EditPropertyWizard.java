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


import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.CorePluginImages;
import com.semmtech.plugin.semmweb.core.model.IModelProvider;
import com.semmtech.semantics.vocabulary.SEMM;


/**
 * 
 * @author Sander Stolk
 */
public class EditPropertyWizard extends ModifyModelWizard implements INewWizard {

    private Property property;

    private Property superProperty = null;
    private Property inverseProperty = null;
    private Resource domainResource = null;
    private Resource rangeResource = null;
    private Resource subjectRoleResource = null;
    private Resource objectRoleResource = null;

    public EditPropertyWizard(String title, IModelProvider modelProvider, Property property) {
        super(modelProvider);
        this.property = property;
        setWindowTitle(title);
    }

    protected PropertyPropertiesWizardPage propertiesPage;

    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
    }

    @Override
    public void addPages() {
        propertiesPage = new PropertyPropertiesWizardPage("propertiesPage", model, null);
        propertiesPage.setInverseProperty(inverseProperty);
        propertiesPage.setSuperProperty(superProperty);
        propertiesPage.setImageDescriptor(CorePlugin.getDefault().getImageDescriptor(
                CorePluginImages.IMG_BANNER_WIZARD_PROPERTY));
        propertiesPage.setProperty(property);
        addPage(propertiesPage);
    }

    private void createPropertyStatements(Property property) {
        property.removeAll(RDFS.subPropertyOf);
        if (superProperty != null) {
            property.addProperty(RDFS.subPropertyOf, superProperty);
        }
        property.removeAll(OWL.inverseOf);
        if (inverseProperty != null) {
            property.addProperty(OWL.inverseOf, inverseProperty);
        }
        property.removeAll(RDFS.domain);
        if (domainResource != null) {
            property.addProperty(RDFS.domain, domainResource);
        }
        property.removeAll(RDFS.range);
        if (rangeResource != null) {
            property.addProperty(RDFS.range, rangeResource);
        }
        property.removeAll(SEMM.hasSubjectRole);
        if (subjectRoleResource != null) {
            property.addProperty(SEMM.hasSubjectRole, subjectRoleResource);
        }
        property.removeAll(SEMM.hasObjectRole);
        if (objectRoleResource != null) {
            property.addProperty(SEMM.hasObjectRole, objectRoleResource);
        }
    }

    @Override
    public boolean performFinish() {
        updatePropertiesFromPage();
        createPropertyStatements(property);

        return true;
    }

    private void updatePropertiesFromPage() {
        inverseProperty = propertiesPage.getInverseProperty();
        superProperty = propertiesPage.getSuperProperty();
        domainResource = propertiesPage.getDomainResource();
        rangeResource = propertiesPage.getRangeResource();
        subjectRoleResource = propertiesPage.getSubjectRoleResource();
        objectRoleResource = propertiesPage.getObjectRoleResource();
    }
}
