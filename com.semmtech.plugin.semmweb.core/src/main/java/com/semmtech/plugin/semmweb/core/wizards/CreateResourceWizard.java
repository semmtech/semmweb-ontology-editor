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
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.IPageChangeProvider;
import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.CorePluginImages;
import com.semmtech.plugin.semmweb.core.model.IModelProvider;
import com.semmtech.plugin.semmweb.core.model.OntModelUtils;
import com.semmtech.plugin.semmweb.core.preferences.LabelsPreference;
import com.semmtech.plugin.semmweb.core.util.ResourceURIUtil;
import com.semmtech.semantics.vocabulary.SEMM;


/**
 * Resource wizard allows for a new resource to be created against the model.
 * 
 * TODO: Create a interface which exposes the main functionality, which allows
 * new wizards to extend these functionalities pages can then use the generic
 * interface to get to the overall resource and other data.
 * 
 * @author Mike Henrichs
 * 
 */
public class CreateResourceWizard extends ModifyModelWizard implements INewWizard,
        IPageChangedListener {
    public static final String ID = "com.semmtech.plugin.semmweb.wizards.createResource";

    private static Logger logger = Logger.getLogger(CreateResourceWizard.class);

    protected PreferredNamespaceWizardPage namespacePage;
    protected CreateResourceWizardPage createPage;
    protected RelabelResourceWizardPage relabelPage;

    protected IModelProvider modelProvider;

    protected static Set<String> namespacePageShownForModels = Sets.newHashSet();

    protected boolean anonymousAllowed = true;
    protected boolean isAnonymous = false;
    protected boolean openEditorAllowed = true;
    protected String baseUri = null;
    protected String initialPreferredNamespace = null;
    protected Resource resource = null;
    protected Resource type = null;
    protected Property labelProperty;
    protected Model baseModel;

    private Object previousPage;

    public CreateResourceWizard(String title, IModelProvider modelProvider, Resource type) {
        this(title, modelProvider, type, null);
    }

    public CreateResourceWizard(String title, IModelProvider modelProvider, Resource type,
            Resource resource) {
        super(modelProvider);
        setWindowTitle(title);
        setNeedsProgressMonitor(true);

        this.modelProvider = modelProvider;
        this.resource = resource;

        if (resource == null || !resource.hasProperty(RDF.type)) {
            this.type = type;
        }
        else if (resource.hasProperty(RDF.type)) {
            this.type = resource.getPropertyResourceValue(RDF.type);
        }

        this.labelProperty = model.getProperty(RDFS.label.getURI());
        this.baseUri = CorePlugin.getDefault().getActiveModelProvider().getBaseURI();

        this.initialPreferredNamespace = baseUri;
        if (Strings.isNullOrEmpty(baseUri)) {
            String modelURI = OntModelUtils.getURI(model);
            if (modelURI != null && (modelURI.endsWith("#") || modelURI.endsWith("/"))) {
                this.initialPreferredNamespace = modelURI;
            }
        }
    }

    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        if (getContainer() != null && getContainer() instanceof IPageChangeProvider) {
            IPageChangeProvider provider = (IPageChangeProvider) getContainer();
            provider.addPageChangedListener(this);
        }
    }

    @Override
    public void createPageControls(Composite pageContainer) {
        super.createPageControls(pageContainer);

        // FIXME: Check if this add the listener again (see init method)
        if (getContainer() instanceof IPageChangeProvider) {
            IPageChangeProvider provider = (IPageChangeProvider) getContainer();
            provider.addPageChangedListener(this);
        }
    }

    @Override
    public void addPages() {
        getShell().setImage(CorePlugin.getDefault().getImage(CorePluginImages.IMG_SEMMTECH_ICON));

        namespacePage = new PreferredNamespaceWizardPage("defaultNamespace", model);
        namespacePage.setBaseModel(baseModel);
        namespacePage.setPreferredNamespaceURI(initialPreferredNamespace);

        createPage = new CreateResourceWizardPage("createResource", model, type);
        createPage.setURIFormat(ResourceURIUtil.FORMAT_DEFAULT);
        createPage.setAnonymous(isAnonymous);
        createPage.setAnonymousEnabled(anonymousAllowed);
        createPage.setOpendEditorOnFinish(openEditorAllowed);
        createPage.setBaseModel(baseModel);
        createPage.setPreferredNamespace(initialPreferredNamespace);

        relabelPage = new RelabelResourceWizardPage("relabelResource", localCopyOfModel, null);

        addPage(namespacePage);
        addPage(createPage);
        addPage(relabelPage);
        String imageKey = CorePluginImages.IMG_BANNER_WIZARD_RDFS_CLASS;
        if (type.isAnon()) {

        }
        else if (type.getURI().equals(OWL.Ontology.getURI())) {
            imageKey = CorePluginImages.IMG_BANNER_WIZARD_ONTOLOGY;
        }
        else if (type.getURI().equals(OWL.Class.getURI())) {
            imageKey = CorePluginImages.IMG_BANNER_WIZARD_OWL_CLASS;
        }
        else if (type.getURI().equals(OWL.Thing.getURI())) {
            imageKey = CorePluginImages.IMG_BANNER_WIZARD_INDIVIDUAL;
        }
        else if (type.getURI().equals(OWL.Restriction.getURI())) {
            imageKey = CorePluginImages.IMG_BANNER_WIZARD_RESTRICTION;
        }
        else if (type.getURI().equals(RDF.Property.getURI())) {
            imageKey = CorePluginImages.IMG_BANNER_WIZARD_PROPERTY;
        }
        else if (type.getURI().equals(RDF.Statement.getURI())) {
            imageKey = CorePluginImages.IMG_BANNER_WIZARD_TRIPLE;
        }
        namespacePage.setImageDescriptor(CorePlugin.getDefault().getImageDescriptor(imageKey));
        createPage.setImageDescriptor(CorePlugin.getDefault().getImageDescriptor(imageKey));
        relabelPage.setImageDescriptor(CorePlugin.getDefault().getImageDescriptor(imageKey));
    }

    @Override
    public IWizardPage getStartingPage() {
        boolean startWithNamespacePage = false;
        if (Strings.isNullOrEmpty(initialPreferredNamespace)
                && !namespacePageShownForModels.contains(modelProvider.getModelURI())) {
            startWithNamespacePage = true;
        }
        return (startWithNamespacePage) ? namespacePage : createPage;
    }

    @Override
    public boolean performFinish() {
        updateDefaultNamespaceFromPage();
        updateResourceFromPage(true);

        if (resource != null && resource.isAnon()) {
            logger.debug(String.format("Created anonymous resource <%s>", resource.getId()
                    .toString()));
        }
        else if (resource != null && !resource.isAnon()) {
            logger.debug(String.format("Created resource with URI %s", resource.getURI()));
        }

        updateLabelStatementsFromPage();
        return true;
    }

    @Override
    public void pageChanged(PageChangedEvent event) {
        Object page = event.getSelectedPage();
        updateResourceFromPage(false);

        if (page == createPage && previousPage == namespacePage) {
            reset();
            String preferredUri = namespacePage.getPreferredNamespaceURI();
            createPage.setPreferredNamespace(preferredUri);
            createPage.validatePage();
        }
        else if (page == relabelPage) {
            relabelPage.setResource(resource);
        }
        previousPage = page;
    }

    public void setAnonymousAllowed(boolean anonymousAllowed) {
        this.anonymousAllowed = anonymousAllowed;
    }

    public void setAnonymous(boolean isAnonymous) {
        this.isAnonymous = isAnonymous;
    }

    public void setAllowOpenEditorOnFinish(boolean openEditorAllowed) {
        this.openEditorAllowed = openEditorAllowed;
    }

    public void setLabelProperty(Property labelProperty) {
        this.labelProperty = labelProperty;
    }

    public Resource getResource() {
        return resource;
    }

    protected final void updateDefaultNamespaceFromPage() {
        namespacePageShownForModels.add(modelProvider.getModelURI());
        modelProvider.setBaseURI(namespacePage.getPreferredNamespaceURI());
    }

    private void createUpdateResourceStatements(OntModel model, Resource resource) {
        if (resource != null) {
            boolean createLabelStatement = LabelsPreference.showReadableLabels();

            type = createPage.getType();
            Statement statement = model.createStatement(resource, RDF.type, type);
            createdStatements.add(statement);

            createdStatements.addAll(followValueRestrictions(model));

            if (createLabelStatement && !Strings.isNullOrEmpty(createPage.getName())) {
                statement = model.createStatement(resource, labelProperty,
                        model.createLiteral(createPage.getName(), null));
                createdStatements.add(statement);
            }

            model.add(createdStatements);
        }
    }

    protected final void updateResourceFromPage(boolean finish) {
        reset();
        OntModel targetModel = (finish) ? model : localCopyOfModel;
        String uri = createPage.getURI();
        if (createPage.isAnonymous()) {
            resource = targetModel.createResource();
        }
        else if (Strings.isNullOrEmpty(uri)) {
            resource = null;
        }
        else {
            resource = targetModel.createResource(uri);
        }
        createUpdateResourceStatements(targetModel, resource);
    }

    private void reset() {
        if (createdStatements.size() > 0) {
            localCopyOfModel.remove(createdStatements);
            createdStatements.clear();
        }
    }

    private void createUpdateLabelStatements(OntModel ontModel, List<Statement> addStatements,
            List<Statement> removeStatements) {
        ontModel.remove(removeStatements);
        ontModel.add(addStatements);
    }

    protected void updateLabelStatementsFromPage() {
        relabelPage.setResource(resource);
        createUpdateLabelStatements(model, relabelPage.getAddedStatements(),
                relabelPage.getRemovedStatements());
    }

    public boolean openResourceEditor() {
        return createPage.openResourceEditor();
    }

    /**
     * Expirimental example code, which checks restrictions and instantiates the
     * hasValue restriction.
     * 
     * @param targetModel
     */
    private List<Statement> followValueRestrictions(OntModel targetModel) {
        List<Statement> addStatements = Lists.newArrayList();
        try {
            if (type.hasProperty(RDFS.subClassOf) && !resource.isAnon()) {
                String resourceName = resource.getLocalName();
                String prefix = targetModel.getNsURIPrefix(resource.getNameSpace());
                String superClassName = type.getLocalName();

                if (type.hasProperty(RDF.type, OWL.Class)) {
                    OntClass clazz = targetModel.getOntClass(type.getURI());
                    // / Initial set (listSuperClasses does not return
                    // restrictions)
                    Set<OntClass> superClasses = clazz.listSuperClasses(false).toSet();
                    // Add additional superClass, which Jena did not find
                    boolean done = false;
                    while (!done) {
                        done = true;
                        for (OntClass superClass : Lists.newArrayList(superClasses)) {
                            for (Statement stmt : superClass.listProperties(RDFS.subClassOf)
                                    .toSet()) {
                                RDFNode object = stmt.getObject();
                                if (object == null || !object.isResource()) {
                                    continue;
                                }
                                Resource s = object.asResource();
                                if (!s.hasProperty(RDF.type, OWL.Class)
                                        && !s.hasProperty(RDF.type, OWL.Restriction)) {
                                    continue;
                                }
                                OntClass c = s.as(OntClass.class);
                                if (!superClasses.contains(c)) {
                                    done = false;
                                    superClasses.add(c);
                                }
                            }
                        }
                    }
                    for (OntClass superClass : superClasses) {
                        if (!superClass.hasProperty(RDF.type, OWL.Restriction)) {
                            continue;
                        }
                        if (!superClass.hasProperty(OWL.onProperty)) {
                            continue;
                        }
                        if (superClass.hasProperty(OWL.hasValue)) {
                            Property property = superClass.getPropertyResourceValue(OWL.onProperty)
                                    .as(Property.class);
                            RDFNode value = superClass.getProperty(OWL.hasValue).getObject();
                            Statement statement = targetModel.createStatement(resource, property,
                                    value);
                            // targetModel.add(statement);
                            addStatements.add(statement);
                        }
                        if (superClass.hasProperty(OWL.someValuesFrom)) {
                            Resource possessedAspectClass = superClass
                                    .getPropertyResourceValue(OWL.someValuesFrom);
                            String possessedAspectName = null;
                            String aspectName = null;
                            Resource possessedAspect = null;
                            Resource qualification = null;
                            Resource aspectClass = null;
                            Resource aspect = null;
                            if (possessedAspectClass.hasProperty(RDFS.subClassOf,
                                    SEMM.PossessedAspect)) {
                                possessedAspectName = possessedAspectClass.getLocalName().replace(
                                        superClassName, resourceName);
                                possessedAspect = targetModel.createResource(targetModel
                                        .expandPrefix(String.format("%s:%s", prefix,
                                                possessedAspectName)));
                            }
                            for (Statement s2 : possessedAspectClass
                                    .listProperties(RDFS.subClassOf).toList()) {
                                if (!s2.getObject().isResource()) {
                                    continue;
                                }
                                Resource r2 = s2.getObject().asResource();
                                if (r2.hasProperty(OWL.onProperty, SEMM.isQualifiedAs)
                                        && r2.hasProperty(OWL.hasValue)) {
                                    qualification = r2.getPropertyResourceValue(OWL.hasValue);
                                }
                                else if (r2.hasProperty(OWL.onProperty, SEMM.isRoleOf)
                                        && r2.hasProperty(OWL.allValuesFrom)) {
                                    aspectClass = r2.getPropertyResourceValue(OWL.allValuesFrom);
                                    aspectName = String.format("%sOf%s",
                                            aspectClass.getLocalName(), resourceName);
                                    aspect = model.createResource(targetModel.expandPrefix(String
                                            .format("%s:%s", prefix, aspectName)));
                                }
                            }
                            if (possessedAspect != null && qualification != null && aspect != null) {
                                Statement statement = targetModel.createStatement(possessedAspect,
                                        RDF.type, possessedAspectClass);
                                // targetModel.add(statement);
                                addStatements.add(statement);
                                statement = targetModel.createStatement(possessedAspect,
                                        RDFS.label, possessedAspectName);
                                // targetModel.add(statement);
                                addStatements.add(statement);
                                statement = targetModel.createStatement(aspect, RDF.type,
                                        aspectClass);
                                // targetModel.add(statement);
                                addStatements.add(statement);
                                statement = targetModel.createStatement(aspect, RDFS.label,
                                        aspectName);
                                // targetModel.add(statement);
                                addStatements.add(statement);
                                statement = targetModel.createStatement(possessedAspect,
                                        SEMM.isRoleOf, aspect);
                                // targetModel.add(statement);
                                addStatements.add(statement);
                                statement = targetModel.createStatement(possessedAspect,
                                        SEMM.isQualifiedAs, qualification);
                                // targetModel.add(statement);
                                addStatements.add(statement);
                                statement = targetModel.createStatement(resource,
                                        SEMM.hasPossessedAspect, possessedAspect);
                                // targetModel.add(statement);
                                addStatements.add(statement);
                                statement = targetModel.createStatement(resource, SEMM.hasAspect,
                                        aspect);
                                // targetModel.add(statement);
                                addStatements.add(statement);
                            }
                        }
                    }
                }
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return addStatements;
    }

    public void setBaseModel(Model activeBaseModel) {
        this.baseModel = activeBaseModel;
    }

}
