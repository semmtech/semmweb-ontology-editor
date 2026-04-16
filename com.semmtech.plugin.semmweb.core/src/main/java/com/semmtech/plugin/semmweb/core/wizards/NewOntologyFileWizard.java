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


import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.IPageChangeProvider;
import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.vocabulary.FOAF;
import com.hp.hpl.jena.util.FileUtils;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.semmtech.jena.ontology.OntologySpec;
import com.semmtech.net.URIUtils;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.CorePluginImages;
import com.semmtech.plugin.semmweb.core.extensionpoint.IPublishErrorHandler;
import com.semmtech.plugin.semmweb.core.extensionpoint.IPublisher;
import com.semmtech.plugin.semmweb.core.extensionpoint.IPublisher.VersioningMode;
import com.semmtech.plugin.semmweb.core.io.ModelIOUtils;
import com.semmtech.plugin.semmweb.core.model.Creator;
import com.semmtech.plugin.semmweb.core.navigator.ISemanticElement;
import com.semmtech.plugin.semmweb.core.preferences.DocumentManagerPreference;
import com.semmtech.plugin.semmweb.core.resources.CoreResourcePropertiesManager;
import com.semmtech.plugin.semmweb.core.util.ResourcesUtil;
import com.semmtech.semantics.rdfmeta.model.Ontology;
import com.semmtech.semantics.rdfmeta.model.impl.RdfmetaResourceFactory;
import com.semmtech.semantics.vocabulary.VAEM;
import com.semmtech.semantics.vocabulary.VANN;


/**
 * This is the RDF/OWL ontology file wizard. Its role is to create a new
 * ontology file in the provided container. If the container resource (a folder
 * or a project) is selected in the workspace when the wizard is opened, it will
 * accept it as the target container.
 * 
 * The wizard creates one ontology file.
 * 
 * @author Mike Henrichs
 * @author Sander Stolk
 */
public class NewOntologyFileWizard extends SemmtechWizard implements INewWizard,
        IPageChangedListener {

    private static final String WINDOW_TITLE = "New RDF/OWL Ontology File";

    public static final String ID = "com.semmtech.plugin.semmweb.wizards.newOntologyFile";

    private OntologyAnnotationWizardPage annotationPage;
    private OntologyNamespaceWizardPage uriPage;
    private OntModelImportsWizardPage importsPage;
    private OntologyFileWizardPage filePage;
    private PublicationLocationWizardPage locationPage;

    private IStructuredSelection selection;

    private boolean openOnFinish = true;
    private IPath modelPath;
    private String filename;

    private List<String> checkdNamespaces;

    /**
     * Constructor for NewOntModelWizard.
     */
    public NewOntologyFileWizard() {
        super();
        checkdNamespaces = Lists.newArrayList();
        setWindowTitle(WINDOW_TITLE);
        setNeedsProgressMonitor(true);
    }

    @Override
    public void addPages() {
        setShellImage();

        annotationPage = new OntologyAnnotationWizardPage();
        uriPage = new OntologyNamespaceWizardPage();

        IProject project = null;
        if (selection != null && !selection.isEmpty()) {
            Object selected = selection.getFirstElement();
            if (selected instanceof IProject) {
                project = (IProject) selected;
            }
            else if (selected instanceof IResource) {
                IResource resource = (IResource) selected;
                project = resource.getProject();
            }
            else if (selected instanceof ISemanticElement) {
                ISemanticElement element = (ISemanticElement) selected;
                project = element.getProject();
            }
        }

        importsPage = new OntModelImportsWizardPage(project);
        filePage = new OntologyFileWizardPage(selection);
        filePage.showUseDefaultCheckbox(false);
        filePage.setUseDefault(!Strings.isNullOrEmpty(filePage.getFolder()));
        locationPage = new PublicationLocationWizardPage();

        addPage(annotationPage);
        addPage(locationPage);
        addPage(uriPage);
        addPage(filePage);
        addPage(importsPage);

        ImageDescriptor banner = CorePlugin.getDefault().getImageDescriptor(
                CorePluginImages.IMG_BANNER_WIZARD_ONTOLOGY);
        annotationPage.setImageDescriptor(banner);
        uriPage.setImageDescriptor(banner);
        importsPage.setImageDescriptor(banner);
        filePage.setImageDescriptor(banner);

        ((IPageChangeProvider) getContainer()).addPageChangedListener(this);
    }

    @Override
    public boolean canFinish() {
        if (!annotationPage.isPageComplete()) {
            return false;
        }
        if (!locationPage.isPageComplete() && annotationPage.getPublishOnline()) {
            return false;
        }
        if (!uriPage.isPageComplete()) {
            return false;
        }
        if (!filePage.isPageComplete()) {
            return false;
        }
        if (!importsPage.isPageComplete()) {
            return false;
        }
        return true;
    }

    @Override
    public IWizardPage getNextPage(IWizardPage page) {
        if (page.equals(annotationPage)) {
            if (annotationPage.getPublishOnline()) {
                return locationPage;
            }
            return uriPage;
        }
        return super.getNextPage(page);
    }

    @Override
    public IWizardPage getPreviousPage(IWizardPage page) {
        if (page.equals(uriPage)) {
            if (annotationPage.getPublishOnline()) {
                return locationPage;
            }
            return annotationPage;
        }
        return super.getPreviousPage(page);
    }

    @Override
    public void pageChanged(PageChangedEvent event) {
        Object page = event.getSelectedPage();
        final String name = annotationPage.getName();
        final String acronym = annotationPage.getAcronym();

        if (page.equals(locationPage)) {
            locationPage.setDefaultPath(name);
        }
        else if (page.equals(uriPage)) {
            boolean publishOnline = annotationPage.getPublishOnline();
            String namespaceUri = null;
            if (!publishOnline) {
                namespaceUri = URIUtils.combineSegments(true, "https://www.example.org/", acronym);
            }
            else {
                namespaceUri = locationPage.getOntologyURI();
            }
            uriPage.setNamespaceLocked(publishOnline);
            uriPage.suggestPrefix(acronym);
            uriPage.suggestNamespaceURI(namespaceUri);
        }
        else if (page == importsPage) {
            IProject project = filePage.getProject();
            DocumentManagerPreference preference = DocumentManagerPreference.fromProject(project);
            importsPage.setProject(project);
            importsPage.clearMandatory();

            if (annotationPage.getAddMetaInformation()) {
                if (preference.hasOntologySpec(DCTerms.getURI())) {
                    OntologySpec dcterms = preference.getOntologySpec(DCTerms.getURI());
                    importsPage.addMandatoryOntology(dcterms);
                }
                if (preference.hasOntologySpec(FOAF.getURI())) {
                    OntologySpec foaf = preference.getOntologySpec(FOAF.getURI());
                    importsPage.addMandatoryOntology(foaf);
                }
                if (preference.hasOntologySpec(VANN.getURI())) {
                    OntologySpec vann = preference.getOntologySpec(VANN.getURI());
                    importsPage.addMandatoryOntology(vann);
                }
            }

            for (String ns : checkdNamespaces) {
                importsPage.addCheckedNamespace(ns, true);
            }
        }
        else if (page == filePage) {
            String filename = String.format("%s.ttl",
                    StringUtils.defaultIfEmpty(acronym, "ontology"));
            filePage.suggestFilename(filename);
        }
    }

    private Resource createOntology(Model model, String ontologyUri, String prefix) {
        if (annotationPage.getAddMetaInformation()) {
            return createRdfmetaOntology(model, ontologyUri, prefix);
        }
        return createOWLOntology(model, ontologyUri);
    }

    private Resource createOWLOntology(Model model, String ontologyUri) {
        Resource ontology = model.createResource(ontologyUri, OWL.Ontology);
        String name = annotationPage.getName();
        String description = annotationPage.getDescription();
        if (!Strings.isNullOrEmpty(name)) {
            ontology.addProperty(RDFS.label, name);
        }
        if (!Strings.isNullOrEmpty(description)) {
            ontology.addProperty(RDFS.comment, description);
        }
        return ontology;
    }

    private Resource createRdfmetaOntology(Model model, String ontologyUri, String prefix) {
        RdfmetaResourceFactory rdfmetaFactory = RdfmetaResourceFactory.createMetaModel(model);
        Ontology ontology = rdfmetaFactory.createOntology(ontologyUri);

        SimpleDateFormat rdfmetaDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String datetime = rdfmetaDateFormat.format(annotationPage.getDateTime());
        String acronym = annotationPage.getAcronym();
        String name = annotationPage.getName();
        String description = annotationPage.getDescription();

        // start adding metadata properties
        ontology.addProperty(RDF.type, OWL.Ontology);
        ontology.addProperty(DCTerms.title, name);
        ontology.addProperty(VAEM.acronym, acronym);
        ontology.addProperty(DCTerms.description, description);
        // ontology.addProperty(VANN.preferredNamespaceUri,
        // Strings.isNullOrEmpty(ontologyUri) ? "" : ontologyUri,
        // XSDDatatype.XSDstring);
        ontology.addProperty(VANN.preferredNamespacePrefix, Strings.isNullOrEmpty(prefix) ? ""
                : prefix, XSDDatatype.XSDstring);
        ontology.addProperty(DCTerms.created, datetime, XSDDatatype.XSDstring);

        Creator creator = annotationPage.getCreator();
        Resource agent = model.createResource();
        if (creator.getPerson() != null) {
            agent.addProperty(RDF.type, FOAF.Person);
            agent.addProperty(FOAF.firstName, creator.getPerson().getFirstName(),
                    XSDDatatype.XSDstring);
            agent.addProperty(FOAF.surname, creator.getPerson().getLastName(),
                    XSDDatatype.XSDstring);
            agent.addProperty(FOAF.mbox, creator.getPerson().getEmail(), XSDDatatype.XSDstring);
            if (creator.getOrganisation() != null) {
                Resource organisation = model.createResource();
                organisation.addProperty(RDF.type, FOAF.Organization);
                organisation.addProperty(FOAF.name, creator.getOrganisation().getName(),
                        XSDDatatype.XSDstring);
                organisation.addProperty(VAEM.acronym, creator.getOrganisation().getAcronym(),
                        XSDDatatype.XSDstring);
                organisation.addProperty(FOAF.member, agent);
            }
        }
        else if (creator.getOrganisation() != null) {
            agent.addProperty(RDF.type, FOAF.Organization);
            agent.addProperty(FOAF.name, creator.getOrganisation().getName(), XSDDatatype.XSDstring);
            agent.addProperty(VAEM.acronym, creator.getOrganisation().getAcronym(),
                    XSDDatatype.XSDstring);
        }
        if (agent.hasProperty(RDF.type)) {
            ontology.addProperty(DCTerms.creator, agent);
        }
        return ontology;
    }

    /**
     * This method is called when 'Finish' button is pressed in the wizard. We
     * will create an operation and run it using wizard as execution context.
     */
    @Override
    public boolean performFinish() {
        IRunnableWithProgress op = new IRunnableWithProgress() {
            @Override
            public void run(IProgressMonitor monitor) throws InvocationTargetException {
                try {
                    monitor.beginTask("Creating Ontology File", 4);

                    monitor.subTask("Building new model...");
                    filename = filePage.getFilename();
                    boolean publishOnline = annotationPage.getPublishOnline();
                    String namespaceUri = uriPage.getNamespaceURI();
                    String ontologyUri = namespaceUri;
                    String prefix = uriPage.getPrefix();
                    String ontologyUriVersioned = null;

                    Model model = ModelFactory.createDefaultModel();
                    model.setNsPrefix(OntologySpec.XSD.getPrefix(), OntologySpec.XSD.getPublicURI());
                    model.setNsPrefix(OntologySpec.RDF.getPrefix(), OntologySpec.RDF.getPublicURI());
                    model.setNsPrefix(OntologySpec.RDFS.getPrefix(),
                            OntologySpec.RDFS.getPublicURI());
                    model.setNsPrefix(OntologySpec.OWL.getPrefix(), OntologySpec.OWL.getPublicURI());
                    model.setNsPrefix(OntologySpec.DCTERMS.getPrefix(),
                            OntologySpec.DCTERMS.getPublicURI());
                    model.setNsPrefix(OntologySpec.FOAF.getPrefix(),
                            OntologySpec.FOAF.getPublicURI());
                    model.setNsPrefix(OntologySpec.VANN.getPrefix(),
                            OntologySpec.VANN.getPublicURI());

                    Resource ontology = createOntology(model, ontologyUri, prefix);

                    monitor.subTask("Updating prefixes and imports...");
                    model.setNsPrefix(prefix, namespaceUri);
                    for (OntologySpec spec : importsPage.getImports()) {
                        if (!Strings.isNullOrEmpty(spec.getPrefix())) {
                            model.setNsPrefix(spec.getPrefix(), spec.getPublicURI());
                        }
                        ontology.addProperty(OWL.imports, model.getResource(spec.getPublicURI()));
                    }

                    if (publishOnline && locationPage.getPublisher() != null) {
                        monitor.subTask("Publishing model to server...");
                        IPublisher publisher = locationPage.getPublisher();

                        VersioningMode versioningMode = locationPage.getVersioningMode();
                        Object versioningModeSettings = locationPage.getCustomVersionLabel();
                        ontologyUriVersioned = publisher.publishModel(model, ontologyUri, model,
                                versioningMode, versioningModeSettings, new IPublishErrorHandler() {

                                    @Override
                                    public void error(final String message) {
                                        Display.getDefault().syncExec(new Runnable() {
                                            @Override
                                            public void run() {
                                                MessageDialog.openError(getShell(),
                                                        "Publishing Ontology", message);
                                            }
                                        });
                                    }
                                });
                    }
                    monitor.worked(1);

                    monitor.subTask("Saving model to file...");
                    String containerName = filePage.getFolder();
                    String lang = FileUtils.guessLang(filename);
                    if (filename.endsWith(".owl")) {
                        lang = FileUtils.langXMLAbbrev;
                    }

                    IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
                    IContainer container = (IContainer) root.findMember(new Path(containerName));
                    final IFile file = container.getFile(new Path(filename));
                    modelPath = file.getProjectRelativePath();
                    String encoding = ResourcesPlugin.getEncoding();
                    String baseUri = uriPage.getNamespaceURI();

                    try {
                        ModelIOUtils.writeModel(model, file, lang, baseUri, encoding, monitor);
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (publishOnline) {
                        VersioningMode vm = locationPage.getVersioningMode();
                        CoreResourcePropertiesManager.setSourceLocation(file, ontologyUri);
                        CoreResourcePropertiesManager.setSourceVersioningmethod(file, vm);
                        CoreResourcePropertiesManager.setSourceVersion(file, ontologyUriVersioned);
                    }

                    monitor.worked(1);
                    monitor.setTaskName("Opening file for editing...");

                    if (openOnFinish) {
                        getShell().getDisplay().asyncExec(new Runnable() {
                            @Override
                            public void run() {
                                IWorkbenchPage page = PlatformUI.getWorkbench()
                                        .getActiveWorkbenchWindow().getActivePage();
                                try {
                                    IDE.openEditor(page, file, true);
                                }
                                catch (PartInitException e) {
                                }
                            }
                        });
                    }
                    monitor.worked(1);
                }
                catch (CoreException e) {
                    throw new InvocationTargetException(e);
                }
                finally {
                    monitor.done();
                }
            }
        };
        try {
            getContainer().run(true, false, op);
        }
        catch (InterruptedException e) {
            return false;
        }
        catch (InvocationTargetException e) {
            Throwable realException = e.getTargetException();
            MessageDialog.openError(getShell(), "Error", realException.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        this.selection = selection;
        if (!ResourcesUtil.existsSemanticProjects()) {
            MessageDialog
                    .openInformation(
                            getShell(),
                            "Project Required",
                            "It seems that no Semantic Projects exist yet in your workspace.\n"
                                    + "Please create a Semantic Project before creating a new Semantic File.");
        }
    }

    public void setOpenOnFinish(boolean openOnFinish) {
        this.openOnFinish = openOnFinish;
    }

    public IPath getModelPath() {
        return modelPath;
    }

    public String getFilename() {
        return filename;
    }

    /**
     * Specify the OntologySpec that have to be selected by default.
     * 
     * @param specs
     */
    public void addCheckedOntologies(String... uris) {
        for (String uri : uris) {
            checkdNamespaces.add(uri);
        }
    }
}