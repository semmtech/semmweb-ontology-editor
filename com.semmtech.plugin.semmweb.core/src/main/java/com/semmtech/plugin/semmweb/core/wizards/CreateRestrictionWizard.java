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

import org.eclipse.jface.dialogs.IPageChangeProvider;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;

import com.google.common.collect.Sets;
import com.hp.hpl.jena.ontology.Restriction;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.OWL2;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.CorePluginImages;
import com.semmtech.plugin.semmweb.core.dialog.RestrictionValidator;
import com.semmtech.plugin.semmweb.core.model.IModelProvider;


public class CreateRestrictionWizard extends CreateResourceWizard {
    public static final String RESTRICTIONS_PAGE_NAME = "restrictionPage";

    protected RestrictionWizardPage restrictionPage;

    protected Restriction oldRestriction;
    protected Resource affectedClass;
    protected List<Restriction> relatedRestrictions;

    protected RestrictionValidator validator;
    protected Property onProperty;
    protected Property typeProperty = OWL.allValuesFrom;
    protected RDFNode value;
    protected Resource onClass;

    public CreateRestrictionWizard(String title, IModelProvider modelProvider,
            Restriction restriction) {
        this(title, modelProvider, OWL.Restriction, restriction);
    }

    public CreateRestrictionWizard(String title, IModelProvider modelProvider, Resource type,
            Restriction restriction) {
        this(title, modelProvider, type, restriction, null, null);
    }

    public CreateRestrictionWizard(String title, IModelProvider modelProvider,
            Restriction restriction, Resource affectedClass, List<Restriction> relatedRestrictions) {
        this(title, modelProvider, OWL.Restriction, restriction, affectedClass, relatedRestrictions);
    }

    public CreateRestrictionWizard(String title, IModelProvider modelProvider, Resource type,
            Restriction restriction, Resource affectedClass, List<Restriction> relatedRestrictions) {
        super(title, modelProvider, type, (restriction == null) ? null : restriction.asResource());
        super.setAnonymousAllowed(false);
        oldRestriction = restriction;
        if (restriction != null) {
            if (restriction.hasProperty(OWL.onProperty))
                onProperty = restriction.getPropertyResourceValue(OWL.onProperty)
                        .as(Property.class);
            for (Property p : Sets.newHashSet(OWL.minCardinality, OWL.maxCardinality,
                    OWL.cardinality, OWL2.minQualifiedCardinality, OWL2.maxQualifiedCardinality,
                    OWL2.qualifiedCardinality)) {
                if (restriction.hasProperty(p)) {
                    typeProperty = p;
                    break;
                }
            }
            for (Property p : Sets.newHashSet(OWL.allValuesFrom, OWL.someValuesFrom, OWL.hasValue)) {
                if (restriction.hasProperty(p)) {
                    typeProperty = p;
                    value = restriction.getProperty(p).getObject();
                    break;
                }
            }
            if (restriction.hasProperty(OWL2.onClass)) {
                onClass = restriction.getPropertyResourceValue(OWL2.onClass);
            }
        }
        this.affectedClass = affectedClass;
        this.relatedRestrictions = relatedRestrictions;
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

        createPage.setImageDescriptor(CorePlugin.getDefault().getImageDescriptor(
                CorePluginImages.IMG_BANNER_WIZARD_RESTRICTION));
        createPage.setAnonymousEnabled(true);
        createPage.setAnonymous(true);

        relabelPage.setImageDescriptor(CorePlugin.getDefault().getImageDescriptor(
                CorePluginImages.IMG_BANNER_WIZARD_RESTRICTION));

        restrictionPage = new RestrictionWizardPage(RESTRICTIONS_PAGE_NAME, localCopyOfModel);
        restrictionPage.setImageDescriptor(CorePlugin.getDefault().getImageDescriptor(
                CorePluginImages.IMG_BANNER_WIZARD_RESTRICTION));
        restrictionPage.setOnProperty(onProperty);
        restrictionPage.setValidator(validator);
        restrictionPage.setTypeProperty(typeProperty);
        restrictionPage.setValue(value);
        restrictionPage.setOnClass(onClass);

        restrictionPage.setOldRestriction(oldRestriction);
        restrictionPage.setAffectedClass(affectedClass);
        restrictionPage.setRelatedRestrictions(relatedRestrictions);

        addPage(restrictionPage);
    }

    @Override
    public IWizardPage getStartingPage() {
        return restrictionPage;
    }

    @Override
    public IWizardPage getPreviousPage(IWizardPage page) {
        if (page == restrictionPage) {
            return null;
        }
        return super.getPreviousPage(page);
    }

    @Override
    public boolean performFinish() {
        updateDefaultNamespaceFromPage();

        // updateResourceFromPage(true);
        // updateRestrictionFromPage();

        // TODO: should I really discard updating the current
        // resource/restriction like this and just create new ones?

        restrictionPage.createRestrictionsStatements(model);

        return true;
    }

    @Override
    public void pageChanged(PageChangedEvent event) {
        super.pageChanged(event);
        // updateRestrictionFromPage();
        if (event.getSelectedPage() == restrictionPage) {
            // / TODO: Set values
            // restrictionPage.setRestriction(resource.as(Restriction.class));
        }
    }

    // private void updateRestrictionFromPage() {
    // onProperty = restrictionPage.getOnProperty();
    // typeProperty = restrictionPage.getTypeProperty();
    // value = restrictionPage.getValue();
    // onClass = restrictionPage.getOnClass();
    //
    // resource.addProperty(OWL.onProperty, onProperty);
    // resource.addProperty(typeProperty, value);
    // if (onClass != null)
    // resource.addProperty(OWL2.onClass, onClass);
    // }

    public void setOnProperty(Property onProperty) {
        this.onProperty = onProperty;
    }

    public void setTypeProperty(Property typeProperty) {
        this.typeProperty = typeProperty;
    }

    public void setOnClass(Resource onClass) {
        this.onClass = onClass;
    }

    public void setValue(RDFNode value) {
        this.value = value;
    }

    public RestrictionValidator getValidator() {
        return validator;
    }

    public void setValidator(RestrictionValidator validator) {
        this.validator = validator;
    }

    public List<Restriction> getRestrictions() {
        return restrictionPage.getNewRestrictions();
    }
}
