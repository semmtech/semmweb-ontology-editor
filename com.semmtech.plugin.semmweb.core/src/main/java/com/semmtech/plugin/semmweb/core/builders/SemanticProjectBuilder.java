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

package com.semmtech.plugin.semmweb.core.builders;


import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.IFileEditorMapping;
import org.eclipse.ui.PlatformUI;

import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.hp.hpl.jena.ontology.OntDocumentManager;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.expr.E_Str;
import com.hp.hpl.jena.sparql.expr.ExprVar;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.semmtech.jena.ontology.OntDocumentManagerConfiguration;
import com.semmtech.jena.ontology.OntologySpec;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.forms.editor.OntologyFormEditor;
import com.semmtech.plugin.semmweb.core.internal.navigator.Import;
import com.semmtech.plugin.semmweb.core.internal.navigator.ImportCollection;
import com.semmtech.plugin.semmweb.core.internal.navigator.Model;
import com.semmtech.plugin.semmweb.core.internal.navigator.ModelCollection;
import com.semmtech.plugin.semmweb.core.internal.navigator.Namespace;
import com.semmtech.plugin.semmweb.core.internal.navigator.NamespaceCollection;
import com.semmtech.plugin.semmweb.core.internal.navigator.SemanticProject;
import com.semmtech.plugin.semmweb.core.internal.navigator.SemanticProjectManager;
import com.semmtech.plugin.semmweb.core.jobs.ILoadModelErrorHandler;
import com.semmtech.plugin.semmweb.core.jobs.LoadModelJob;
import com.semmtech.plugin.semmweb.core.jobs.LoadModelJob.ModelMetaData;
import com.semmtech.plugin.semmweb.core.jobs.LoadModelJobAdapter;
import com.semmtech.plugin.semmweb.core.jobs.RefreshImportsJob;
import com.semmtech.plugin.semmweb.core.markers.ImportProblem;
import com.semmtech.plugin.semmweb.core.markers.ParseProblem;
import com.semmtech.plugin.semmweb.core.model.IModelProvider;
import com.semmtech.plugin.semmweb.core.model.OntModelUtils;
import com.semmtech.plugin.semmweb.core.navigator.ISemanticElement;
import com.semmtech.plugin.semmweb.core.navigator.ImportType;
import com.semmtech.plugin.semmweb.core.preferences.DocumentManagerPreference;
import com.semmtech.plugin.semmweb.core.preferences.ModelsFolderPreference;
import com.semmtech.plugin.semmweb.core.util.ImportURLUtils;
import com.semmtech.plugin.semmweb.core.util.SemanticProjectUtils;
import com.semmtech.semantics.query.QuerySolutions;
import com.semmtech.semantics.sparql.QueryBuilder;
import com.semmtech.semantics.util.NamespaceMapping;
import com.semmtech.semantics.util.NamespaceUtil;


/**
 * For Semantic Projects in Eclipse RCP, we should provide an INCREMENTAL_BUILD
 * smart enough to avoid FULL_BUILD. The FULL_BUILD should be called only when
 * the user cleans the project, or the entire workspace, or when the
 * IResourceDelta is not available (eg. first build). The result of the build
 * process is the information about Imports and Namespaces of the Models. This
 * information will be stored as Session Property of each model resource. The
 * session properties, being saved in memory, will be lost when a resource is
 * deleted from the workspace, when the parent project is closed, or when the
 * workspace is closed. A smart way to improve the performance is to read the
 * Model and the Imports only once. At the moment if an import is used three
 * times then it will be read three times. We could cache, for a single build
 * process, the loaded Models and use them if they are necessary again. Then
 * discarding these information once the build process will be finished. Note 1:
 * Take in account the possibility to add metadata to the cached files as well
 * (the ones that are themselves IResources) Note 2: If a copy of an IResource
 * is done, the session property will be lost for that copy.
 * 
 * @author Mike Henrichs
 * @author Simone Rondelli
 */
public class SemanticProjectBuilder extends IncrementalProjectBuilder {

    private static Logger logger = Logger.getLogger(SemanticProjectBuilder.class);

    public static final String BUILDER_ID = CorePlugin.PLUGIN_ID + ".semanticProjectBuilder";

    private static final Query BUILDER_QUERY = createBuilderQuery();

    /**
     * If this parameter have true value then a pre build is performed. The pre
     * build creates a temporary tree that just provides the list of the files
     * on the HD
     */
    public static final String PRE_BUILD_PARAM = "preBuild";

    private static final String PERSISTENT_NS = "com.semmtech.plugin.semmweb.core.model.sessionparam.persistent";
    private static final String WORKING_NS = "com.semmtech.plugin.semmweb.core.model.sessionparam.working";

    private static final QualifiedName MODEL_SESSION_PARAM = new QualifiedName(PERSISTENT_NS,
            "model");

    private static final QualifiedName IMPORTS_SESSION_PARAM = new QualifiedName(PERSISTENT_NS,
            "imports");

    private static final QualifiedName NAMESPACES_SESSION_PARAM = new QualifiedName(PERSISTENT_NS,
            "namespaces");

    private static final QualifiedName MODEL_WORKING_SESSION_PARAM = new QualifiedName(WORKING_NS,
            "model");

    private static final QualifiedName IMPORTS_WORKING_SESSION_PARAM = new QualifiedName(
            WORKING_NS, "imports");

    private static final QualifiedName NAMESPACES_WORKING_SESSION_PARAM = new QualifiedName(
            WORKING_NS, "namespaces");

    private List<IFile> modelFiles;

    /*
     * These two maps contains the earlier state of the DocumentManager. They
     * are filled at the end of each build process. Therefore, at first build,
     * they are empty.
     */
    private Map<String, String> alternateUrlMappings;
    private Map<String, String> prefixMapping;

    /**
     * Contains the mapping between the resource and the builded models.
     */
    private Map<IResource, OntModel> resourceToModel;

    /**
     * In this document manager will be stored a cache of the model loaded
     * during a single build process. This means that at the startup it will be
     * empty, and is filled during the process and, in the end, is freed.
     */
    private OntDocumentManager sessionDocumentManager;

    private SemanticProject semanticProject;

    private final List<Job> jobs;

    public SemanticProjectBuilder() {
        jobs = Lists.newArrayList();
        alternateUrlMappings = Maps.newHashMap();
        prefixMapping = Maps.newHashMap();
    }

    @Override
    protected IProject[] build(int kind, Map<String, String> args, IProgressMonitor monitor)
            throws CoreException {

        long time = System.currentTimeMillis();
        IProject project = getProject();

        switch (kind) {
        case INCREMENTAL_BUILD:
            logger.trace(String.format("INCREMENTAL_BUILD; project: %s; args: %s",
                    project.getName(), args.toString()));
            break;
        case AUTO_BUILD:
            logger.trace(String.format("AUTO_BUILD; project: %s; args: %s", project.getName(),
                    args.toString()));
            break;
        case FULL_BUILD:
            logger.trace(String.format("FULL_BUILD; project: %s; args: %s", project.getName(),
                    args.toString()));
            break;
        default:
            String message = String.format("Unknown build kind: %d - this should not be possible!",
                    kind);
            logger.error(message);
            throw new IllegalStateException(message);
        }

        IFolder modelsFolder = SemanticProjectUtils.getModelsFolder(project);

        if (modelsFolder == null) {
            logger.trace("The project doesn't have the models folder; the build process is skipped.");
            return null;
        }

        sessionDocumentManager = initializeSessionDocumentManager();
        modelFiles = SemanticProjectUtils.getModelFiles(project);
        resourceToModel = Maps.newHashMap();

        IProgressMonitor group = Job.getJobManager().createProgressGroup();
        group.beginTask(getProject().getName(), modelFiles.size() * 10);

        SemanticProjectManager manager = SemanticProjectManager.getSemanticProjectManager(project);
        semanticProject = manager.obtainProject();

        synchronized (semanticProject) {
            ModelCollection parent = getModelCollection(semanticProject);

            if (parent == null) {
                parent = new ModelCollection(semanticProject);
            }

            if ("true".equals(args.get(PRE_BUILD_PARAM))) {
                for (IFile modelFile : modelFiles) {
                    String relativeFileUrl = modelFile.getProjectRelativePath().toString();
                    relativeFileUrl = StringUtils.removeStart(relativeFileUrl, modelsFolder
                            .getLocation().lastSegment());

                    Model model = new Model(parent);
                    model.setName(modelFile.getName());
                    model.setLocationURL(relativeFileUrl);
                    model.setLocalTimeStamp(modelFile.getLocalTimeStamp());

                    // toString avoids the "unused" warning
                    new ImportCollection(model).toString();
                    new NamespaceCollection(model).toString();
                }
                project.refreshLocal(IResource.DEPTH_INFINITE, monitor);

                // trick to skip the build process
                kind = Integer.MIN_VALUE;
            }
        }

        cleanWorkingCopyProperties();

        if (kind == FULL_BUILD) {
            fullBuild(group);

            // If the full build has been automatically called after the
            // clean method the POST_BUILD event (that updates the
            // SemanticProjectContentProvider) has already been called after
            // the clean() method. Refreshing the project we force another
            // call to this method.
            project.refreshLocal(IResource.DEPTH_INFINITE, monitor);
        }
        else if (kind == INCREMENTAL_BUILD || kind == AUTO_BUILD) {
            incrementalBuild(group);
        }

        // saves the document manager state to make them available in the next
        // build
        OntDocumentManagerConfiguration documentManager = DocumentManagerPreference.fromProject(
                getProject()).getDocumentManagerConfig();
        alternateUrlMappings = documentManagerImportsToMap(documentManager);
        prefixMapping = documentManagerPrefixToMap(documentManager);

        sessionDocumentManager.clearCache();
        modelFiles.clear();
        resourceToModel.clear();
        semanticProject = null;

        long elapsed = System.currentTimeMillis() - time;
        logger.trace(String.format("The build process for project \"%s\" took %dms",
                project.getName(), elapsed));

        monitor.done();
        group.done();
        return null;
    }

    @Override
    protected void clean(IProgressMonitor monitor) throws CoreException {
        super.clean(monitor);

        IProject project = getProject();

        monitor.setTaskName("Clean Semantic Project: " + project.getName());

        SemanticProjectManager manager = SemanticProjectManager
                .getSemanticProjectManager(getProject());
        manager.clear();

        for (IFile file : SemanticProjectUtils.getModelFiles(getProject())) {
            for (QualifiedName name : file.getSessionProperties().keySet()) {
                if (PERSISTENT_NS.equals(name.getQualifier())
                        || WORKING_NS.equals(name.getQualifier())) {
                    file.setSessionProperty(name, null);
                }
            }
        }
    }

    private OntDocumentManager initializeSessionDocumentManager() {
        OntDocumentManager sessionDocumentManager = new OntDocumentManager();
        sessionDocumentManager.setCacheModels(true);
        sessionDocumentManager.setProcessImports(false);
        return sessionDocumentManager;
    }

    private void fullBuild(IProgressMonitor monitor) {
        Set<IResource> fullReload = Sets.newHashSet();
        Set<IResource> importsReload = Sets.newHashSet();

        for (IResource file : modelFiles) {
            if (getModelProperties(file) == null || getImportProperties(file) == null
                    || getNamespaceProperties(file) == null) {
                fullReload.add(file);
            }
        }

        keepOnlyModels(fullReload);

        // reload the necessary information and store the session properties on
        // the IResource
        fullReload(fullReload, monitor);

        // check the dependencies for the edited Models
        for (IResource r : fullReload) {
            if (resourceToModel.containsKey(r)) {
                ModelSessionProperties modelMetadata = getModelProperties(r);
                List<IResource> dependencies = getDependentResources(modelMetadata.getPublicUri());
                importsReload.addAll(dependencies);
            }
        }

        // ensure that the model that have to be fully reloaded won't be
        // partially reloaded
        importsReload.removeAll(fullReload);

        importsReload(importsReload, fullReload, Sets.<String> newHashSet(), monitor);

        Set<IResource> modifiedResources = Sets.union(fullReload, importsReload);

        buildModels(modifiedResources, monitor);
    }

    private void incrementalBuild(IProgressMonitor monitor) {
        Set<IResource> fullReload = Sets.newHashSet();
        Set<IResource> importsReload = Sets.newHashSet();

        IProject project = semanticProject.getProject();
        IResourceDelta delta = getDelta(project);
        IPath modelsPath = SemanticProjectUtils.getModelsFolder(project).getFullPath();
        ModelCollectionDelta collectionDelta = visitDelta(modelsPath, delta);

        if (collectionDelta.isEmpty()) {
            logger.trace("Delta is empty; skip the build process.");
            return;
        }

        // remove the deleted resources
        keepOnlyModels(collectionDelta.removedResources);

        ModelCollection modelCollection = getModelCollection(semanticProject);
        for (IResource toDelete : collectionDelta.removedResources) {
            // modelCollection.getChild(IResource) doesn't work in this case
            // because the resource doesn't exist anymore
            for (ISemanticElement semElem : modelCollection
                    .getChildrenByType(ISemanticElement.MODEL)) {
                Model model = (Model) semElem;
                if (model.getLocationURL().equals(toDelete.getName())) {
                    modelCollection.removeChild(model);
                    break;
                }
            }
        }

        // all the new/edited resource need a full rebuild
        fullReload.addAll(collectionDelta.addedResources);
        fullReload.addAll(collectionDelta.modifiedResources);

        keepOnlyModels(fullReload);

        // reload the necessary information and store the session properties on
        // the IResource
        fullReload(fullReload, monitor);

        // check the dependencies for the edited Models
        for (IResource r : fullReload) {
            if (resourceToModel.containsKey(r)) {
                ModelSessionProperties modelMetadata = getModelProperties(r);
                List<IResource> dependencies = getDependentResources(modelMetadata.getPublicUri());
                importsReload.addAll(dependencies);
            }
        }

        // check the dependencies for the deleted Models
        for (IResource r : collectionDelta.removedResources) {
            String url = r.getLocationURI().toString().replace("file:/", "file:///");

            // Being the model deleted the session properties has been lost.
            // Then i have to search in the document manager 'backup' if there
            // is some mapping between an uri and the deleted file
            for (String uri : alternateUrlMappings.keySet()) {
                String altUrl = alternateUrlMappings.get(uri);
                if (Objects.equal(url, altUrl)) {
                    List<IResource> dependencies = getDependentResources(uri);
                    importsReload.addAll(dependencies);
                }
            }
        }

        Set<String> changedUris = Sets.newHashSet();

        // Check the changes in the document manager
        if (collectionDelta.isDocumentManagerChanged) {
            changedUris = getChangedDocumentManagerUris();

            for (String changedUri : changedUris) {
                List<IResource> dependencies = getDependentResources(changedUri);
                importsReload.addAll(dependencies);
            }
        }

        // ensure that the model that have to be fully reloaded won't be
        // partially reloaded
        importsReload.removeAll(fullReload);

        importsReload(importsReload, fullReload, changedUris, monitor);

        Set<IResource> modifiedResources = Sets.union(fullReload, importsReload);

        buildModels(modifiedResources, monitor);
    }

    private void buildModels(Collection<IResource> modifiedResources, IProgressMonitor monitor) {
        monitor.subTask("Build models " + resourceToString(modifiedResources));

        ModelCollection modelCollection = getModelCollection(semanticProject);

        for (IResource modelRes : modifiedResources) {
            OntModel ontModel = getWorkingCopyModel((IFile) modelRes);

            if (ontModel != null) {
                Model persistedCopy = (Model) modelCollection.getChild(modelRes);

                if (persistedCopy != null) {
                    Model workingCopy = buildModel(modelRes, null, ontModel);
                    persistedCopy.setWorkingCopy(workingCopy);
                }
                else {
                    persistedCopy = buildModel(modelRes, modelCollection, ontModel);
                }
            }
            else {
                ontModel = resourceToModel.get(modelRes);
                buildModel(modelRes, modelCollection, ontModel);
            }
        }
    }

    /**
     * Performs a full reload of the given set of models. Once the models are
     * loaded it removes from the fullReload Set the ones that haven't been
     * successfully loaded (unparseable models)
     * 
     * @param monitor
     */
    private void fullReload(Set<IResource> fullReload, IProgressMonitor monitor) {

        for (IResource resource : fullReload) {
            IFile file = (IFile) resource;

            deleteParseProblems(file);
            OntModel workingCopyModel = getWorkingCopyModel(file);

            if (workingCopyModel == null) {
                LoadModelJob fullReloadJob = new LoadModelJob(file, "Load Model from "
                        + file.getName(), true, false);
                fullReloadJob.setUser(false);
                fullReloadJob.setCacheDocumentManager(sessionDocumentManager);
                fullReloadJob.setListener(new ModelLoadedHandler(file));
                fullReloadJob.setErrorHandler(new ModelLoadErrorHandler(file));
                fullReloadJob.setProgressGroup(monitor, 10);
                fullReloadJob.schedule();
                jobs.add(fullReloadJob);
            }
            else {
                ModelSessionProperties modelInfo = generateModelProperties(file, workingCopyModel,
                        true);
                generateImportProperties(file, workingCopyModel, modelInfo.getPublicUri(), true);
                generateNamespaceProperties(file, workingCopyModel, true);
            }
        }

        joinRunningJobs();
    }

    /**
     * 
     * @param needRefresh
     *            The models corresponding to these resources needs the imports
     *            to be reloaded
     * @param fullReloaded
     *            The models corresponding to these resources have alreasy been
     *            full reloaded
     * @param changedUris
     *            These uris are the ones changed in the document manager
     * @param monitor
     */
    private void importsReload(Set<IResource> needRefresh, Set<IResource> fullReloaded,
            Set<String> changedUris, IProgressMonitor monitor) {

        for (IResource resource : needRefresh) {
            IFile file = (IFile) resource;

            ImportSessionProperties importMetadata = getImportProperties(resource);
            ModelSessionProperties modelMetadata = getModelProperties(resource);
            String modelUri = modelMetadata.getPublicUri();

            // I will create a fake model that just contains the imports and the
            // name. This because we want to avoid the entire Model loading
            OntModel ontModel = ModelFactory.createOntologyModel();
            Resource ontology = ontModel.createResource(modelUri, OWL.Ontology);
            ontology.addProperty(RDFS.label, modelMetadata.getName());

            // In case of ontologyless models this trick make sure that the
            // imports will be added anyway. This because an ontologyless model
            // has no direct imports
            boolean isOntologyUriAbsent = Objects.equal(modelMetadata.getName(),
                    modelMetadata.getPublicUri());

            Set<String> editedUris = Sets.newHashSet(changedUris);
            for (IResource editedResource : fullReloaded) {
                if (resourceToModel.containsKey(editedResource)) {
                    ModelSessionProperties editedResourceProp = getModelProperties(editedResource);
                    editedUris.add(editedResourceProp.getPublicUri());
                }
            }

            // Check all the imports dependent on the changed resource
            Set<String> editedImportUris = Sets.newHashSet();
            for (String s : editedUris) {
                Set<String> dependenciesUris = searchDependencies(importMetadata, s);
                editedImportUris.addAll(dependenciesUris);
            }

            // This cycle will determine which of the earlier stored import will
            // be added again to the model and, in this case, if they needs to
            // be reloaded or not
            for (ImportInfo importInfo : importMetadata.listImportInfo()) {
                String importUri = importInfo.getImportUri();

                boolean dependentOnEditedResources = editedImportUris.contains(importUri);
                boolean isEditedResource = editedUris.contains(importUri);

                boolean addImport = isEditedResource || isOntologyUriAbsent
                        || !dependentOnEditedResources;

                if (addImport) {
                    Resource importedOntology = ontModel.createResource(importUri);

                    if (importInfo.isDirectImport()) {
                        ontModel.add(ontology, OWL.imports, importedOntology);
                    }
                    else {
                        for (String derivingOntologyUri : importInfo.getDerivedFrom()) {
                            if (!editedImportUris.contains(derivingOntologyUri)) {
                                Resource derivingOntology = ontModel
                                        .createResource(derivingOntologyUri);
                                ontModel.add(derivingOntology, OWL.imports, importedOntology);
                            }
                        }
                    }

                    // This trick skips the reload of the import in the
                    // RefreshImportsJob
                    if (!dependentOnEditedResources) {
                        ontModel.addLoadedImport(importUri);
                    }
                }
            }

            RefreshImportsJob refreshImportsJob = new RefreshImportsJob(file,
                    "Reloading imports for Model: " + modelUri, ontModel);
            refreshImportsJob.setCacheDocumentManager(sessionDocumentManager);
            refreshImportsJob.setListener(new ModelLoadedHandler(file));
            refreshImportsJob.setProgressGroup(monitor, 10);
            refreshImportsJob.setUser(false);
            jobs.add(refreshImportsJob);
        }

        // the jobs have to be run later otherwise the ImportInfo could change
        // while are used to calculate the stuff above
        for (Job j : jobs) {
            j.schedule();
        }

        joinRunningJobs();
    }

    /**
     * Search the dependencies of the given uri between ImportSessionProperties
     * using the Depth-first-search algorithm. The ImportSessionProperties can
     * be seen as a Graph.
     * <p>
     * Consider the following imports:
     * 
     * <pre>
     * A -> B 
     * B -> C
     * C -> D
     * A -> E
     * </pre>
     * 
     * If this function is run on the graph above giving B as start the result
     * will be : "B C D"
     * 
     * @param current
     *            The import graph
     * @param uri
     *            the start uri
     * @return
     */
    private Set<String> searchDependencies(ImportSessionProperties current, String uri) {
        Set<String> result = Sets.newHashSet();

        Deque<String> toVisit = Queues.newArrayDeque();
        toVisit.push(uri);

        while (!toVisit.isEmpty()) {
            uri = toVisit.pop();

            if (!result.contains(uri)) {
                result.add(uri);

                for (String w : current.getDependencies(uri)) {
                    toVisit.push(w);
                }
            }
        }
        return result;
    }

    /**
     * Create an instance of the IModel using the information stored as session
     * property on the IResource
     * 
     * @param file
     *            Resource that represents the model
     * @param parent
     *            The parent IModelCollection. If null the IModel is considered
     *            as working copy
     * @param ontModel
     *            The instance of the OntModel is used for create the Imports
     *            Problem Marker. Can be null if the model hasn't been
     *            successfully loaded
     */
    private Model buildModel(IResource file, ModelCollection parent, OntModel ontModel) {
        boolean isWorkingCopy = parent == null;

        // delete any old import markers related to this model
        for (IMarker importMarker : ImportProblem.findMarkers(file, isWorkingCopy)) {
            try {
                importMarker.delete();
            }
            catch (CoreException e) {
                logger.error(
                        "An error occurred while deleting the Import Marker from file " + file, e);
            }
        }

        Model model = null;

        if (isWorkingCopy) {
            model = new Model(null);
        }
        else {
            @SuppressWarnings("null")
            ISemanticElement oldModel = parent.getChild(file);
            if (oldModel != null) {
                parent.removeChild(oldModel);
            }

            model = new Model(parent);
        }

        String modelsFolder = ModelsFolderPreference.fromProject(getProject())
                .getModelsFolderPath();
        String relativeFileUrl = file.getProjectRelativePath().toString();
        relativeFileUrl = StringUtils.removeStart(relativeFileUrl, modelsFolder);

        // If the model hasn't been parsed than the session properties won't
        // be available
        if (ontModel == null) {
            model.setName(file.getName());
            model.setLocationURL(relativeFileUrl);
            model.setLocalTimeStamp(file.getLocalTimeStamp());
            return model;
        }

        ModelSessionProperties modelMetadata = getModelProperties(file);
        ImportSessionProperties importMetadata = getImportProperties(file);
        NamespaceSessionProperties namespaceMetadata = getNamespaceProperties(file);

        model.setName(modelMetadata.getName());
        model.setLocationURL(relativeFileUrl);
        model.setLocalTimeStamp(file.getLocalTimeStamp());

        NamespaceCollection namespaceCollection = new NamespaceCollection(model);
        for (NamespaceInfo nsInfo : namespaceMetadata.listNamespaceInfo()) {
            Namespace namespace = new Namespace(namespaceCollection);
            namespace.setURI(nsInfo.getUri());
            namespace.setPrefix(nsInfo.getPrefix());
        }

        ImportCollection importCollection = new ImportCollection(model);
        for (ImportInfo importInfo : importMetadata.listImportInfo()) {
            String importUri = importInfo.getImportUri();

            Import immport = new Import(importCollection);
            immport.setURI(importUri);
            immport.setImportType(importInfo.getType());
            immport.setDirect(importInfo.isDirectImport());

            for (String ontologyUri : importInfo.getDerivedFrom()) {
                immport.addImportedByOntologyUri(ontologyUri);
            }

            // Check if user has manually disabled the import in the project
            boolean importDisabled = DocumentManagerPreference.fromProject(getProject())
                    .isDisabledImport(importUri);

            // create import problem markers when necessary
            if (!ontModel.hasLoadedImport(importUri) && !importDisabled) {
                ImportProblem importProblem = new ImportProblem(file, isWorkingCopy);
                importProblem.setLocation(importUri);
                importProblem.setMessage("Failed to import ontology with the following URI: "
                        + importUri);
                importProblem.generateMarker();
            }
        }

        return model;
    }

    /**
     * Make sure that the working copy properties are deleted if the model is no
     * longer opened in the editor
     */
    private void cleanWorkingCopyProperties() {
        for (IFile file : modelFiles) {
            try {
                if (getWorkingCopyModel(file) == null) {
                    for (QualifiedName name : file.getSessionProperties().keySet()) {
                        if (WORKING_NS.equals(name.getQualifier())) {
                            file.setSessionProperty(name, null);
                        }
                    }
                }
            }
            catch (CoreException ex) {
                logger.error("Error while cleaning the working copy session properties", ex);
            }
        }
    }

    private Map<String, String> documentManagerImportsToMap(
            OntDocumentManagerConfiguration documentManager) {
        Map<String, String> importsMap = Maps.newHashMap();

        for (OntologySpec spec : documentManager.listOntologySpecs()) {
            importsMap.put(spec.getPublicURI(), spec.getAltURL());
        }
        return importsMap;
    }

    private Map<String, String> documentManagerPrefixToMap(
            OntDocumentManagerConfiguration documentManager) {
        Map<String, String> prefixMap = Maps.newHashMap();

        for (OntologySpec spec : documentManager.listOntologySpecs()) {
            prefixMap.put(spec.getPublicURI(), spec.getPrefix());
        }
        return prefixMap;
    }

    /**
     * Retrieves the publicUri changed between the last stored version of the
     * DocumentManager and the current one. This method take in account the
     * added/removed/changed uris both for the altUrl and for the namespaces.
     */
    private Set<String> getChangedDocumentManagerUris() {
        Set<String> changedUris = Sets.newHashSet();

        OntDocumentManagerConfiguration documentManager = DocumentManagerPreference.fromProject(
                getProject()).getDocumentManagerConfig();

        Map<String, String> currentAltUrlMap = documentManagerImportsToMap(documentManager);
        Map<String, String> currentPrefixMap = documentManagerPrefixToMap(documentManager);

        MapDifference<String, String> altUrlDiff = Maps.difference(alternateUrlMappings,
                currentAltUrlMap);
        MapDifference<String, String> prefixDiff = Maps.difference(prefixMapping, currentPrefixMap);

        changedUris.addAll(altUrlDiff.entriesDiffering().keySet());
        changedUris.addAll(altUrlDiff.entriesOnlyOnLeft().keySet());
        changedUris.addAll(altUrlDiff.entriesOnlyOnRight().keySet());

        changedUris.addAll(prefixDiff.entriesDiffering().keySet());
        changedUris.addAll(prefixDiff.entriesOnlyOnLeft().keySet());
        changedUris.addAll(prefixDiff.entriesOnlyOnRight().keySet());

        return changedUris;
    }

    private ModelCollection getModelCollection(SemanticProject semanticProject) {
        if (semanticProject.getChildrenByType(ISemanticElement.MODEL_COLLECTION).size() > 0) {
            // NOTE: Currently it is assumed that each Semantic project will
            // only have a single Models collection
            for (ISemanticElement element : semanticProject
                    .getChildrenByType(ISemanticElement.MODEL_COLLECTION)) {
                if (element instanceof ModelCollection) {
                    return (ModelCollection) element;
                }
            }
        }
        return null;
    }

    private ModelCollectionDelta visitDelta(final IPath modelsPath, IResourceDelta delta) {
        final ModelCollectionDelta collectionDelta = new ModelCollectionDelta();

        try {
            delta.accept(new IResourceDeltaVisitor() {
                @Override
                public boolean visit(IResourceDelta delta) throws CoreException {
                    IPath path = delta.getFullPath();
                    if (modelsPath.removeTrailingSeparator().isPrefixOf(path)) {
                        switch (delta.getKind()) {
                        case IResourceDelta.ADDED:
                            collectionDelta.addedResources.add(delta.getResource());
                            break;
                        case IResourceDelta.CHANGED:
                            collectionDelta.modifiedResources.add(delta.getResource());
                            break;
                        case IResourceDelta.REMOVED:
                            collectionDelta.removedResources.add(delta.getResource());
                            break;
                        default:
                            break;
                        }
                        return true;
                    }
                    else if (path.isPrefixOf(modelsPath)) {
                        return true;
                    }
                    else if (path.lastSegment().equals(".settings")) {
                        return true;
                    }
                    else if (path.lastSegment().contains(
                            DocumentManagerPreference.PREFERENCE_QUALIFIER)) {
                        collectionDelta.isDocumentManagerChanged = true;
                        return true;
                    }
                    return false;
                }
            });
        }
        catch (CoreException e) {
            throw new IllegalStateException("Error while visting the delta for the project "
                    + getProject(), e);
        }

        return collectionDelta;
    }

    private boolean extensionBoundTo(String extension, String editorId) {
        if (!Strings.isNullOrEmpty(extension) && !Strings.isNullOrEmpty(editorId)) {
            IEditorRegistry editorReg = PlatformUI.getWorkbench().getEditorRegistry();
            IFileEditorMapping[] mappings = editorReg.getFileEditorMappings();
            for (IFileEditorMapping mapping : mappings) {
                if (extension.equals(mapping.getExtension())) {
                    for (IEditorDescriptor editorDesc : mapping.getEditors()) {
                        if (editorId.equals(editorDesc.getId())) {
                            return true;
                        }
                    }
                }
            }

        }
        return false;
    }

    private void deleteParseProblems(final IFile file) {
        IMarker[] parseProblemMarkers = ParseProblem.findMarkers(file);
        for (IMarker parseProblemMarker : parseProblemMarkers) {
            try {
                parseProblemMarker.delete();
            }
            catch (CoreException e) {
                logger.error("Error while removing parse problem Markers", e);
            }
        }
    }

    /**
     * Retrieve all the resources of the models that are dependent from the
     * passed model resource. The session parameters of the resource will be
     * used for this operation.
     * <p>
     * This method take in account the possibility that a Model imports itself
     */
    private List<IResource> getDependentResources(String modelUri) {
        List<IResource> modelResources = Lists.newArrayList();

        for (IResource otherModel : modelFiles) {
            ImportSessionProperties importMetadata = getImportProperties(otherModel);

            if (importMetadata != null && importMetadata.hasImportedUri(modelUri)) {
                modelResources.add(otherModel);
            }
        }
        return modelResources;
    }

    private void joinRunningJobs() {
        while (!jobs.isEmpty()) {
            Job job = jobs.get(0);
            try {
                job.join();
            }
            catch (InterruptedException ex) {
                logger.error("Job Interrupted", ex);
            }
            jobs.remove(job);
        }
    }

    /**
     * <pre>
     * SELECT DISTINCT  (str(?ontology) AS ?ontologyUri) (str(?import) AS ?importUri)
     * WHERE { 
     *      ?ontology  <http://www.w3.org/2002/07/owl#imports>  ?import . 
     * }
     * </pre>
     */
    private static Query createBuilderQuery() {
        Var varOntology = Var.alloc("ontology");
        Var varOntologyUri = Var.alloc("ontologyUri");
        Var varImport = Var.alloc("import");
        Var varImportUri = Var.alloc("importUri");

        QueryBuilder qb = QueryBuilder.createSelect(true);
        qb.getProject().add(varOntologyUri, new E_Str(new ExprVar(varOntology)));
        qb.getProject().add(varImportUri, new E_Str(new ExprVar(varImport)));
        qb.addTriplePattern(varOntology, OWL.imports, varImport);

        return qb.getQuery();
    }

    private ModelSessionProperties generateModelProperties(IResource modelFile, OntModel input,
            boolean isWorkingCopy) {
        // find the name of the model
        String modelName = OntModelUtils.getName(input);
        if (Strings.isNullOrEmpty(modelName)) {
            modelName = modelFile.getName();
        }

        String modelUri = OntModelUtils.getURI(input);

        if (Strings.isNullOrEmpty(modelUri)) {
            modelUri = modelName;
        }

        ModelSessionProperties modelMetadata = new ModelSessionProperties(modelUri, modelName);

        try {
            if (isWorkingCopy) {
                modelFile.setSessionProperty(MODEL_WORKING_SESSION_PARAM, modelMetadata);
            }
            else {
                modelFile.setSessionProperty(MODEL_SESSION_PARAM, modelMetadata);
            }
        }
        catch (CoreException e) {
            logger.error("Impossible to store Model Metadata for the resource " + modelFile, e);
        }

        return modelMetadata;
    }

    private void generateImportProperties(IResource modelFile, OntModel input, String modelUri,
            boolean isWorkingCopy) {
        QueryExecution execution = QueryExecutionFactory.create(BUILDER_QUERY, input);
        ResultSet result = execution.execSelect();

        ArrayListMultimap<String, String> importsToOntologiesMapping = ArrayListMultimap.create();
        while (result.hasNext()) {
            QuerySolution solution = result.next();
            String ontologyUri = QuerySolutions.getLexicalForm(solution, "ontologyUri");
            String importUri = QuerySolutions.getLexicalForm(solution, "importUri");

            // special check to ensure RDFS is handled properly
            if (RDFS.getURI().equals(importUri + "#")) {
                importUri = RDFS.getURI();
            }

            if (!Strings.isNullOrEmpty(importUri)) {
                importsToOntologiesMapping.put(importUri, ontologyUri);
            }
        }

        ImportSessionProperties importMetadata = new ImportSessionProperties();

        for (String importUri : importsToOntologiesMapping.keySet()) {
            ImportType importType = ImportURLUtils.guessImportType(importUri, getProject());
            List<String> originOntology = Lists.newArrayList(importsToOntologiesMapping
                    .get(importUri));
            ImportInfo metadata = new ImportInfo(importUri, modelUri, originOntology, importType);
            importMetadata.addImportInfo(metadata);
        }

        try {
            if (isWorkingCopy) {
                modelFile.setSessionProperty(IMPORTS_WORKING_SESSION_PARAM, importMetadata);
            }
            else {
                modelFile.setSessionProperty(IMPORTS_SESSION_PARAM, importMetadata);
            }
        }
        catch (CoreException e) {
            logger.error("Impossible to store import metadata for the resource " + modelFile, e);
        }
    }

    private void generateNamespaceProperties(IResource modelFile, OntModel input,
            boolean isWorkingCopy) {

        NamespaceSessionProperties namespaceMetadata = new NamespaceSessionProperties();

        for (NamespaceMapping mapping : NamespaceUtil.getNamespaceMappings(input.getBaseModel(),
                true)) {
            NamespaceInfo info = new NamespaceInfo(mapping.getURI(), mapping.getPrefix());
            namespaceMetadata.addNamespaceInfo(info);
        }

        try {
            if (isWorkingCopy) {
                modelFile.setSessionProperty(NAMESPACES_WORKING_SESSION_PARAM, namespaceMetadata);
            }
            else {
                modelFile.setSessionProperty(NAMESPACES_SESSION_PARAM, namespaceMetadata);
            }
        }
        catch (CoreException e) {
            logger.error("Impossible to store namespace metadata for the resource " + modelFile, e);
        }
    }

    private class ModelCollectionDelta {
        final List<IResource> addedResources;
        final List<IResource> modifiedResources;
        final List<IResource> removedResources;
        boolean isDocumentManagerChanged;

        ModelCollectionDelta() {
            addedResources = Lists.newArrayList();
            modifiedResources = Lists.newArrayList();
            removedResources = Lists.newArrayList();
        }

        boolean isEmpty() {
            return addedResources.isEmpty() && modifiedResources.isEmpty()
                    && removedResources.isEmpty() && !isDocumentManagerChanged;
        }
    }

    private static ModelSessionProperties getModelProperties(IResource resource) {
        try {
            Object metadata = resource.getSessionProperty(MODEL_WORKING_SESSION_PARAM);

            if (metadata == null) {
                metadata = resource.getSessionProperty(MODEL_SESSION_PARAM);
            }

            if (metadata instanceof ModelSessionProperties) {
                return (ModelSessionProperties) metadata;
            }
        }
        catch (CoreException e) {
            logger.error("Error while retrieving the Model URI from the resource: " + resource, e);
        }
        return null;
    }

    private static ImportSessionProperties getImportProperties(IResource resource) {
        try {
            Object metadata = resource.getSessionProperty(IMPORTS_WORKING_SESSION_PARAM);

            if (metadata == null) {
                metadata = resource.getSessionProperty(IMPORTS_SESSION_PARAM);
            }

            if (metadata instanceof ImportSessionProperties) {
                return (ImportSessionProperties) metadata;
            }
        }
        catch (CoreException e) {
            logger.error("Error while retrieving the Model Imports from the resource: " + resource,
                    e);
        }
        return null;
    }

    private NamespaceSessionProperties getNamespaceProperties(IResource resource) {
        try {
            Object metadata = resource.getSessionProperty(NAMESPACES_WORKING_SESSION_PARAM);

            if (metadata == null) {
                metadata = resource.getSessionProperty(NAMESPACES_SESSION_PARAM);
            }

            if (metadata instanceof NamespaceSessionProperties) {
                return (NamespaceSessionProperties) metadata;
            }
        }
        catch (CoreException e) {
            logger.error("Error while retrieving the Namespace metadata from the resource: "
                    + resource, e);
        }

        return null;
    }

    private void keepOnlyModels(Collection<IResource> resources) {
        Iterator<IResource> iter = resources.iterator();
        IPath modelsPath = SemanticProjectUtils.getModelsFolder(getProject()).getFullPath();

        while (iter.hasNext()) {
            IResource res = iter.next();

            if (!(res instanceof IFile)) {
                iter.remove();
                continue;
            }

            IFile file = (IFile) res;

            // Check if extension is a genuine model extension.
            if (!extensionBoundTo(file.getFileExtension(), OntologyFormEditor.ID)) {
                iter.remove();
                continue;
            }

            if (!modelsPath.equals(res.getFullPath().removeLastSegments(1))) {
                iter.remove();
            }
        }
    }

    private class ModelLoadedHandler extends LoadModelJobAdapter {

        private final IFile file;

        ModelLoadedHandler(IFile file) {
            this.file = file;
        }

        @Override
        public void modelLoaded(OntModel model, ModelMetaData metaData) {
            if (model == null) {
                return;
            }

            resourceToModel.put(file, model);
            ModelSessionProperties modelProp = generateModelProperties(file, model, false);
            String modelUri = modelProp.getPublicUri();

            generateImportProperties(file, model, modelUri, false);
            generateNamespaceProperties(file, model, false);

            OntModel workingCopyModel = getWorkingCopyModel(file);

            if (workingCopyModel != null) {
                generateModelProperties(file, workingCopyModel, true);
                generateImportProperties(file, workingCopyModel, modelUri, true);
                generateNamespaceProperties(file, workingCopyModel, true);
            }
        }
    }

    private static class ModelLoadErrorHandler implements ILoadModelErrorHandler {

        private final IFile file;

        ModelLoadErrorHandler(IFile file) {
            this.file = file;
        }

        @Override
        public void error(Stage stage, String message, Throwable exception) {
            logger.info(String.format("Error occurred loading file %s: %s -> %s", file.getName(),
                    message, exception.getMessage()));

            if (stage == Stage.BASE) {
                // Add marker
                String exceptionMessage = exception.getMessage();
                ParseProblem problem = new ParseProblem(file);
                problem.setSeverity(IMarker.SEVERITY_ERROR);
                problem.setMessage(exception.getMessage());
                problem.setLocation(getLine(exceptionMessage), getColumn(exceptionMessage));
                problem.generateMarker();
            }
        }

        private int getLine(String message) {
            return getIntAfter("line", message);
        }

        private int getColumn(String message) {
            return getIntAfter("column", message);
        }

        private int getIntAfter(String token, String message) {
            if (message != null) {
                message = message.toLowerCase();
                int lineIndex = message.indexOf(token);
                if (lineIndex >= 0) {
                    message = message.substring(lineIndex + token.length() + 1);
                    try {
                        int lineNr = NumberFormat.getInstance().parse(message).intValue();
                        if (lineNr >= 0) {
                            return lineNr;
                        }
                    }
                    catch (ParseException e) {
                    }
                }
            }
            return -1;
        }
    }

    private OntModel getWorkingCopyModel(IFile file) {
        IModelProvider modelProvider = CorePlugin.getDefault().getModelProvider(file);

        if (modelProvider != null && modelProvider.isModelLoaded()) {
            return modelProvider.getOntModel();
        }
        return null;
    }

    private String resourceToString(Collection<IResource> resources) {
        String formatted = "";

        for (IResource res : resources) {
            formatted += res.getName() + ", ";
        }

        formatted += " ";

        return String.format("[%s]", formatted.replace(",  ", ""));
    }
}
