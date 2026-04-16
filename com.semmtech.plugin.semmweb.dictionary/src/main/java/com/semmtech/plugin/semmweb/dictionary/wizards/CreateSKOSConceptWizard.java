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

package com.semmtech.plugin.semmweb.dictionary.wizards;


import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import com.google.common.collect.Lists;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.CorePluginImages;
import com.semmtech.plugin.semmweb.core.model.OntModelUtils;
import com.semmtech.plugin.semmweb.core.preferences.LabelsPreference;
import com.semmtech.plugin.semmweb.core.wizards.CreateResourceWizardPage;
import com.semmtech.semantics.vocabulary.SKOS;


/**
 * TODO: See local copy of model
 * 
 * @author Mike Henrichs
 * @author Sander Stolk
 */
public class CreateSKOSConceptWizard extends Wizard implements INewWizard {
    private static final Logger logger = Logger.getLogger(CreateSKOSConceptWizard.class);

    private CreateResourceWizardPage createPage;
    @SuppressWarnings("unused")
    private CreateSKOSConceptWizardPage conceptPage;

    private boolean anonymousAllowed;

    private boolean isTopConcept;
    private boolean inScheme;
    private boolean inCollection;

    private boolean isNarrower;
    private boolean isBroader;
    private boolean isRelated;

    private OntModel model;
    private OntModel localCopyOfModel;
    private List<Statement> tempStatements;

    private Resource resource = null;
    private Property labelPropery;
    private Resource type;
    private IStructuredSelection selection = null;

    public CreateSKOSConceptWizard(String title, OntModel model) {
        setWindowTitle(title);
        setNeedsProgressMonitor(true);

        this.anonymousAllowed = false;
        this.type = model.createResource(SKOS.Concept.getURI());
        this.model = model;
        this.labelPropery = model.getProperty(RDFS.label.getURI());
        // / Create a local copy, of the underlying model
        // / TODO: Use a basic GenericRuleReasoner to create a leaner version of
        // the model (only relevant triples are kept).
        this.localCopyOfModel = OntModelUtils.copyModel(model);

        // / Added statements will be used to collect all statements which will
        // be put into the model.
        this.tempStatements = Lists.newArrayList();
    }

    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        logger.debug("init called! with selection.getFirstElement = '"
                + selection.getFirstElement() + "'");
        this.selection = selection;
    }

    @Override
    public void addPages() {
        getShell().setImage(CorePlugin.getDefault().getImage(CorePluginImages.IMG_SEMMTECH_ICON));

        createPage = new CreateResourceWizardPage("createResource", localCopyOfModel, type);
        createPage.setAnonymousEnabled(anonymousAllowed);
        conceptPage = new CreateSKOSConceptWizardPage("createSKOSConcept", localCopyOfModel);
        addPage(createPage);

        // / TODO: First only create the resources and use the form
        // addPage(conceptPage);
    }

    @Override
    public boolean performFinish() {
        // / If resource is still null - create the resource and associated
        // triples
        updateResourceFromPage(true);

        if (inScheme) {
            model.add(model.createStatement(resource, SKOS.inScheme,
                    (Resource) selection.getFirstElement()));
        }
        if (isTopConcept) {
            model.add(model.createStatement(resource, SKOS.topConceptOf,
                    (Resource) selection.getFirstElement()));
            model.add(model.createStatement((Resource) selection.getFirstElement(),
                    SKOS.hasTopConcept, resource));
        }
        else if (isNarrower) {
            model.add(model.createStatement((Resource) selection.getFirstElement(), SKOS.narrower,
                    resource));
            model.add(model.createStatement(resource, SKOS.broader,
                    (Resource) selection.getFirstElement()));
        }
        else if (isBroader) {
            model.add(model.createStatement((Resource) selection.getFirstElement(), SKOS.broader,
                    resource));
            model.add(model.createStatement(resource, SKOS.narrower,
                    (Resource) selection.getFirstElement()));
        }
        else if (isRelated) {
            model.add(model.createStatement((Resource) selection.getFirstElement(), SKOS.related,
                    resource));
            model.add(model.createStatement(resource, SKOS.related,
                    (Resource) selection.getFirstElement()));
        }
        else if (inCollection) {
            model.add(model.createStatement((Resource) selection.getFirstElement(), SKOS.member,
                    resource));
        }
        return true;
    }

    /**
     * Uses the URI from the CreateResourceWizarPage to create the triples
     * associated with the new resource.
     */
    protected void updateResourceFromPage(boolean finish) {
        // / Cleanup
        if (tempStatements.size() > 0) {
            localCopyOfModel.remove(tempStatements);
            tempStatements.clear();
        }

        // / Create the Resource object
        OntModel targetModel = (finish) ? model : localCopyOfModel;
        String uri = createPage.getURI();
        if (!createPage.isAnonymous() && (uri == null || uri.length() == 0)) {
            resource = null;
        }
        else if (createPage.isAnonymous()) {
            resource = targetModel.createResource();
        }
        else {
            resource = targetModel.createResource(uri);
        }

        // / Create the statements
        if (resource != null) {
            boolean createLabelStatement = LabelsPreference.showReadableLabels();
            type = createPage.getType();
            Statement statement = targetModel.createStatement(resource, RDF.type, type);
            targetModel.add(statement);
            tempStatements.add(statement);

            if (createLabelStatement) {
                statement = targetModel.createStatement(resource, labelPropery,
                        targetModel.createLiteral(createPage.getName(), null));
                targetModel.add(statement);
                tempStatements.add(statement);
            }
        }
    }

    public Resource getResource() {
        return resource;
    }

    public void setIsTopConcept(boolean isTopConcept) {
        this.isTopConcept = isTopConcept;
    }

    public void setInScheme(boolean inScheme) {
        this.inScheme = inScheme;
    }

    public void setInCollection(boolean inCollection) {
        this.inCollection = inCollection;
    }

    public void setIsNarrower(boolean isNarrower) {
        this.isNarrower = isNarrower;
    }

    public void setIsBroader(boolean isBroader) {
        this.isBroader = isBroader;
    }

    public void setIsRelated(boolean isRelated) {
        this.isRelated = isRelated;
    }

    public void setLabelProperty(Property labelProperty) {
        this.labelPropery = labelProperty;
    }

    public boolean openResourceEditor() {
        return createPage.openResourceEditor();
    }
}
