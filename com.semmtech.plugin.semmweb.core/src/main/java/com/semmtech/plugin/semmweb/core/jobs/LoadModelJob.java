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

package com.semmtech.plugin.semmweb.core.jobs;


import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.net.HttpHeaders;
import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.hp.hpl.jena.ontology.OntDocumentManager;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.ProfileRegistry;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ModelMaker;
import com.hp.hpl.jena.rdf.model.Selector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.shared.DoesNotExistException;
import com.hp.hpl.jena.util.FileUtils;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.XSD;
import com.semmtech.jena.ontology.OntDocumentManagerConfiguration;
import com.semmtech.jena.readers.JenaReadersUtil;
import com.semmtech.jena.skolem.Skolemizer;
import com.semmtech.plugin.semmweb.core.extensionpoint.IPublisher;
import com.semmtech.plugin.semmweb.core.jobs.ILoadModelErrorHandler.Stage;
import com.semmtech.plugin.semmweb.core.model.FileModelMakerManager;
import com.semmtech.plugin.semmweb.core.model.ModelChangedAdapter;
import com.semmtech.plugin.semmweb.core.model.OntModelUtils;
import com.semmtech.plugin.semmweb.core.preferences.DocumentManagerPreference;
import com.semmtech.plugin.semmweb.core.preferences.SkolemizationPreference;
import com.semmtech.plugin.semmweb.core.util.PublicationProviderUtil;
import com.semmtech.plugin.semmweb.core.util.ResourcesUtil;
import com.semmtech.semantics.model.ExtendedModelFactory;
import com.semmtech.semantics.model.PredicateSelector;
import com.semmtech.semantics.util.RDFParserUtil;
import com.semmtech.ui.plugin.jobs.JobWithMonitor;


public class LoadModelJob extends JobWithMonitor {
    private static Logger logger = Logger.getLogger(LoadModelJob.class);

    public class ModelMetaData {
        public String filename;
        public String serializationLanguage;
        public String encoding;

        public String baseUri;
        public String ontologyLanguageURI;
    }

    protected OntDocumentManager manager;
    protected boolean processImports;
    protected OntModelSpec spec;
    protected LinkedList<String> queuedImportUris;
    protected final List<String> ignoreUris;
    protected final IFile file;
    protected ILoadModelJobListener listener;
    protected ILoadModelErrorHandler errorHandler;
    protected int statementsAdded;

    protected OntModel ontModel;
    protected ModelMetaData metaData;

    protected boolean ignorePreload;

    /**
     * This document manager is setted by the extern and provides a cache for
     * the already loaded models
     */
    protected OntDocumentManager cacheDocumentManager;

    /**
     * Create a LoadModelJob. The parameters ignorePreload and
     * ignoreAdditionalImports are setted to true.
     * 
     * @param file
     *            File that contains the Model
     * @param name
     *            Name of the Job
     * 
     * @see LoadModelJob#LoadModelJob(IFile, String, boolean, boolean)
     */
    public LoadModelJob(IFile file, String name) {
        this(file, name, true, true);
    }

    /**
     * 
     * @param file
     *            File that contains the Model
     * @param name
     *            Name of the Job
     * @param ignorePreload
     *            Ignore the preloading of the models directly specified by the
     *            namespace prefix mapping
     * @param ignoreAdditionalImports
     *            Ignore the loading of the models imported by this model (and
     *            the imports of the imports).
     */
    public LoadModelJob(IFile file, String name, boolean ignorePreload,
            boolean ignoreAdditionalImports) {
        super(name);
        this.file = file;
        this.ignorePreload = ignorePreload;
        this.processImports = !ignoreAdditionalImports;
        this.ignoreUris = Lists.newArrayList();
    }

    public void setErrorHandler(ILoadModelErrorHandler handler) {
        this.errorHandler = handler;
    }

    public void setListener(ILoadModelJobListener listener) {
        this.listener = listener;
    }

    protected IProject getProject() {
        return file.getProject();
    }

    protected String getFilename() {
        return file.getName();
    }

    protected InputStream getBaseModelStream() throws Exception {
        return ResourcesUtil.getFileUtf8Stream(file);
    }

    protected String getBaseModelEncoding() {
        try {
            return file.getCharset();
        }
        catch (CoreException e) {
            return "UTF-8";
        }
    }

    @Override
    protected IStatus run(final IProgressMonitor monitor) {
        startMonitorUpdate(monitor, "Reading Model " + file.getName(), 10);
        long start = Calendar.getInstance().getTimeInMillis();

        try {
            IProject project = getProject();
            DocumentManagerPreference preferences = DocumentManagerPreference.fromProject(project);
            OntDocumentManagerConfiguration documentManagerConfig = preferences
                    .getDocumentManagerConfig();

            statementsAdded = 0;
            metaData = new ModelMetaData();
            ignoreUris.clear();

            // Try and read the model from the input
            updateSubTask("Resetting readers...");
            JenaReadersUtil.reset();

            ModelMaker maker = FileModelMakerManager.getInstance().getModelMaker(getProject());
            Model parsedModel = maker.createFreshModel();
            parsedModel.register(new ModelChangedAdapter() {
                @Override
                public void addedStatement(Statement statement) {
                    statementsAdded++;
                }
            });

            // Read failure message is set if not null
            // readFailureMessage = null;
            boolean parseError = false;
            updateSubTask("Parsing base model from file...");
            try {
                metaData.filename = getFilename();
                metaData.serializationLanguage = FileUtils.guessLang(metaData.filename);
                if (metaData.filename.endsWith(".owl")) {
                    metaData.serializationLanguage = FileUtils.langXMLAbbrev;
                }

                metaData.encoding = getBaseModelEncoding();

                try (InputStream stream = getBaseModelStream();) {
                    // Note: Base URI is not given as parameter; as this is only
                    // used by the read method to set a default base URI - read
                    // still uses baseURI defined in file if present
                    metaData.baseUri = RDFParserUtil.retrieveBaseURI(stream,
                            metaData.serializationLanguage);
                }

                addWorked(1);

                // We search into the document manager if this model has a
                // mappings. In this case we use the uri provided in the
                // document manager as a lock object. Otherwise the absolute
                // path will be used as URI of the model.
                String baseModelName = file.getLocationURI().toString()
                        .replace("file:/", "file:///");
                List<String> mappingUris = documentManagerConfig.getMappingUris(baseModelName);

                if (!mappingUris.isEmpty()) {
                    baseModelName = mappingUris.get(0);
                }

                Object lockObject = new String();

                if (cacheDocumentManager != null) {
                    // using the String#intern() method we ensure the uniqueness
                    // of that string.
                    lockObject = baseModelName.intern();
                }

                // In case we are using a cacheDocumentManager we want to
                // synchronize all the jobs on the currently loaded URI. This
                // means that if there are 3 jobs that tries to download SKOS,
                // just one of them will be allowed to do that. The others will
                // use the already downloaded model.
                synchronized (lockObject) {
                    Model cachedModel = null;

                    if (monitor.isCanceled()) {
                        return Status.CANCEL_STATUS;
                    }

                    if (cacheDocumentManager != null) {
                        cachedModel = cacheDocumentManager.getModel(baseModelName);
                    }

                    if (cachedModel == null) {

                        try (InputStream stream = getBaseModelStream();) {
                            logger.trace(String.format(
                                    "Model %s not found in cache, proceed with reading.",
                                    baseModelName));
                            parsedModel.read(stream, metaData.baseUri,
                                    metaData.serializationLanguage);
                        }
                    }
                    else {
                        logger.trace(String.format("Model %s found in cache.", baseModelName));
                        parsedModel.add(cachedModel);
                    }

                    // If the model doesn't have any mapping in the document
                    // manager it means that is not referred by other models
                    // so the cache is not necessary
                    if (cacheDocumentManager != null && cachedModel == null
                            && !mappingUris.isEmpty() && !parseError) {
                        cacheDocumentManager.addModel(baseModelName, parsedModel);
                        logger.trace(String.format("Model %s added in cache.", baseModelName));
                    }
                    addWorked(1);
                }
            }
            catch (Throwable ex) {
                if (errorHandler != null) {
                    errorHandler.error(Stage.BASE, "Error parsing base model from file", ex);
                }
                parseError = true;
            }

            // Error during reading model from text editor (only the empty
            // model, without any imports etc.)
            if (!parseError) {
                metaData.ontologyLanguageURI = ProfileRegistry.RDFS_LANG;
                List<String> namespaceUris = Lists.newArrayList(parsedModel.getNsPrefixMap()
                        .values());
                if (namespaceUris.contains(OWL.getURI())) {
                    metaData.ontologyLanguageURI = ProfileRegistry.OWL_LANG;
                }

                manager = documentManagerConfig.createManager();
                // Store the setting for processImport; and force the
                // manager to false
                processImports = processImports && manager.getProcessImports();
                manager.setProcessImports(false);

                ModelMaker baseMaker = FileModelMakerManager.getInstance().getModelMaker(project);
                ModelMaker importsMaker = FileModelMakerManager.getInstance().getModelMaker(
                        project, ".imports");
                spec = new OntModelSpec(baseMaker, importsMaker, manager, null,
                        metaData.ontologyLanguageURI);
                ontModel = ExtendedModelFactory.createOntologyModel(spec, parsedModel);

                handlePreLoading(namespaceUris, monitor);

                // Manually load imports (instead of using Jena's built-in
                // mechanism)
                // handleImports();
                boolean done = false;
                queuedImportUris = Lists.newLinkedList();
                while (!done && !monitor.isCanceled()) {
                    done = !handleImports(monitor);
                }

                manager.setReadFailureHandler(null);
            }

        }
        finally {
            if (listener != null && !monitor.isCanceled()) {
                listener.modelLoaded(ontModel);
                listener.modelLoaded(ontModel, metaData);
            }

            monitor.done();
            stopMonitorUpdate();
        }
        logger.trace(String.format("Execution on %s took %s ms.", getFilename(), Calendar
                .getInstance().getTimeInMillis() - start));
        return monitor.isCanceled() ? Status.CANCEL_STATUS : Status.OK_STATUS;
    }

    /**
     * This method checks if any pre-loading needs to be performed, based on the
     * settings in preferences.
     * 
     * @param namespaceUris
     */
    protected void handlePreLoading(Iterable<String> namespaceUris, IProgressMonitor monitor) {
        DocumentManagerPreference preferences = DocumentManagerPreference.fromProject(getProject());
        boolean preload = preferences.isPreLoadingRDFOWL() || preferences.isPreLoadingAllways();

        if (preload && !ignorePreload) {
            for (String uri : namespaceUris) {

                if (monitor.isCanceled()) {
                    return;
                }

                if (preferences.isPreLoadingAllways()
                        || (uri.equals(RDF.getURI()) || uri.equals(RDFS.getURI()) || uri.equals(OWL
                                .getURI()))) {

                    updateSubTask("Pre-loading " + uri);

                    // This next statement imports RDFS (without the #-symbol at
                    // the end of the uri); The OWL ontology imports this URI
                    if (!ontModel.hasLoadedImport(uri)) {
                        updateSubTask(String.format("Pre-loading <%s>...", uri));

                        try {
                            loadSubModel(uri, getModelURL(uri), ontModel, cacheDocumentManager);
                        }
                        catch (Throwable ex) {
                            if (errorHandler != null) {
                                errorHandler.error(Stage.PRELOADING,
                                        String.format("Unable to pre-load <%s>", uri), ex);
                            }
                            ignoreUris.add(uri);
                        }
                    }
                    addWorked(1);
                }
            }
        }
    }

    /**
     * Tries to import all import models. Returns whether one or more new
     * imports were loaded successfully.
     */
    protected boolean handleImports(IProgressMonitor monitor) {
        boolean newImports = false;
        Selector importsSelector = new PredicateSelector(OWL.imports);
        final Set<String> importUris = Sets.newHashSet();

        if (!processImports) {
            return false;
        }

        for (ExtendedIterator<Statement> iter = ontModel.listStatements(importsSelector); iter
                .hasNext();) {
            Statement stmt = iter.next();
            if (!stmt.getObject().isResource() || stmt.getObject().isAnon()) {
                continue;
            }
            String uri = stmt.getObject().asResource().getURI();

            if (RDFS.getURI().equals(uri + "#")) {
                uri = RDFS.getURI();
            }

            if (!ontModel.hasLoadedImport(uri) && !ignoreUris.contains(uri)) {
                importUris.add(uri);
            }
        }

        queuedImportUris = Lists.newLinkedList(importUris);
        while (!queuedImportUris.isEmpty()) {
            if (monitor.isCanceled()) {
                return false;
            }

            String uri = queuedImportUris.remove(0);
            if (isDisabledImport(uri)) {
                // Check if import has been disabled within the project
                continue;
            }

            notifyBeforeImport(uri);

            try {
                updateSubTask(String.format(" processing import %s", uri));
                String url = getModelURL(uri);
                boolean success = (loadSubModel(uri, url, ontModel, cacheDocumentManager) != null);
                newImports = success;
                addWorked(1);
            }
            catch (final Throwable ex) {
                if (errorHandler != null) {
                    String url = getModelURL(uri);
                    if (ex instanceof DoesNotExistException) {
                        errorHandler
                                .error(Stage.IMPORTS,
                                        String.format(
                                                "Unable to either retrieve or parse imported model from <%s>, the location \"%s\" does not appear to exist!",
                                                uri, url), ex);
                    }
                    else if (ex.getCause() instanceof UnknownHostException) {
                        errorHandler
                                .error(Stage.IMPORTS,
                                        String.format(
                                                "Unable to either retrieve or parse imported model from <%s>, the location \"%s\" has unknown host!",
                                                uri, url), ex);
                    }
                    else {
                        errorHandler
                                .error(Stage.IMPORTS,
                                        String.format(
                                                "Unable to either retrieve or parse imported model <%s> from location \"%s\"",
                                                uri, url), ex);
                    }
                }
                ignoreUris.add(uri);
            }

        }
        return newImports;
    }

    private boolean isDisabledImport(String uri) {
        IProject project = getProject();
        return DocumentManagerPreference.fromProject(project).isDisabledImport(uri);
    }

    protected String getModelURL(String uri) {
        return manager.doAltURLMapping(uri);
    }

    static public Model loadSubModel(String uri, String url, OntModel ontModel) throws Throwable {
        return loadSubModel(uri, url, ontModel, null);
    }

    static public Model loadSubModel(String uri, String url, OntModel ontModel,
            OntDocumentManager cacheDocumentManager) throws Throwable {

        ModelMaker importsMaker = ModelFactory.createMemModelMaker();
        // special check to ensure XML Schema is not attempted to be loaded
        if (XSD.getURI().equals(uri) || XSD.getURI().equals(uri + "#")) {
            return null;
        }
        // special check to ensure RDFS gets loaded properly
        if (RDFS.getURI().equals(uri + "#")) {
            uri = RDFS.getURI();
        }

        Model importedModel = importsMaker.createModel(uri);
        Object lockObject = new String();

        if (cacheDocumentManager != null) {
            lockObject = uri.intern();
        }

        synchronized (lockObject) {
            long time = System.currentTimeMillis();

            Model cached = null;
            if (cacheDocumentManager != null) {
                cached = cacheDocumentManager.getModel(uri);
            }

            if (cached == null) {
                logger.trace(String.format("SubModel %s not found in cache, proceed with reading.",
                        uri));

                String guessedLang = FileUtils.guessLang(url);
                if (url.endsWith(".owl")) {
                    guessedLang = FileUtils.langXMLAbbrev;
                }

                File file = ResourcesUtil.getLocalFile(url);
                if (file != null) {
                    if (readFromStream(importedModel, ResourcesUtil.getFileUtf8Stream(file),
                            guessedLang) == null) {
                        throw new Exception("Failed to read RDF model from file: "
                                + file.getAbsolutePath());
                    }
                }
                else {
                    java.lang.System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2");

                    Model retrievedModel = getModelViaRegisteredPublisher(url);
                    if (retrievedModel != null) {
                        importedModel.add(retrievedModel.listStatements());
                        importedModel.setNsPrefixes(retrievedModel.getNsPrefixMap());
                    }
                    else {
                        if (readFromUrl(importedModel, url, guessedLang) == null) {
                            throw new Exception("Failed to read RDF model from URL: " + url);
                        }
                    }
                }

                // Check for Skolemizations; by perfoming this here the
                // de-skolemized model will be stored in the cache as well
                boolean isSkolemized = Skolemizer.isSkolemized(importedModel);
                // If the model contains skolemized data then be sure to
                // 1. deskolemize this data to make resource anonymous again
                // 2. store the skolemization data in a sub model for future
                if (SkolemizationPreference.isSkolemizationEnabled() && isSkolemized) {
                    Skolemizer skolemizer = new Skolemizer();
                    Map<String, String> prefixMap = importedModel.getNsPrefixMap();
                    skolemizer.setKeepAnonIds(false);
                    Model deskolemizedModel = skolemizer.deskolemize(importedModel);
                    Model anonymousModel = ModelFactory.createDefaultModel();
                    Skolemizer.extractSkolemData(deskolemizedModel, anonymousModel, null);

                    importsMaker.removeModel(uri);
                    importedModel = importsMaker.createModel(uri);
                    importedModel.add(anonymousModel);
                    importedModel.setNsPrefixes(prefixMap);
                }
            }
            else {
                importedModel.add(cached);
                logger.trace(String.format("SubModel %s found in cache.", uri));
            }

            if (cacheDocumentManager != null && cached == null) {
                cacheDocumentManager.addModel(uri, importedModel);
                logger.trace(String.format("SubModel %s added in cache.", uri));
            }

            ontModel.addSubModel(importedModel);
            ontModel.addLoadedImport(uri);

            if (logger.isTraceEnabled()) {
                long elapsed = System.currentTimeMillis() - time;
                String modelName = OntModelUtils.getName(ontModel);
                if (modelName == null) {
                    modelName = OntModelUtils.getURI(ontModel);
                }
                logger.trace(String.format("Loading time for the SubModel %s imported by %s is %d",
                        uri, modelName, elapsed));
            }
        }

        return importedModel;
    }

    static protected String getRedirectURL(String originalURL, String lang) {
        HttpURLConnection conn = null;
        String result = null;

        String mimeType = "application/rdf+xml";
        switch (lang) {
        case FileUtils.langN3:
            mimeType = "text/n3";
            break;
        case FileUtils.langNTriple:
            mimeType = "application/n-triples";
            break;
        case FileUtils.langTurtle:
            mimeType = "text/turtle";
            break;
        case FileUtils.langXML:
        case FileUtils.langXMLAbbrev:
        default:
            mimeType = "application/rdf+xml";
        }

        try {
            URL urlObject = new URL(originalURL);
            conn = (HttpURLConnection) urlObject.openConnection();
            conn.setRequestMethod("HEAD");
            conn.setRequestProperty("Accept", mimeType);
            conn.setInstanceFollowRedirects(false);
            int responseCode = conn.getResponseCode();
            if (responseCode == 301 || responseCode == 302 || responseCode == 303
                    || responseCode == 307 || responseCode == 308) {
                String location = conn.getHeaderField(HttpHeaders.LOCATION);
                location = URLDecoder.decode(location, "UTF-8");
                URL next = new URL(urlObject, location); // relative URLs
                result = next.toExternalForm().toString();
            }
        }
        catch (Exception e) {
            // No redirect URL could be obtained
        }
        if (conn != null) {
            conn.disconnect();
        }
        return result;
    }

    /**
     * Notifies the user about an import being executed; if settings prevent the
     * notifications to be given, this method does nothing.
     * 
     * @param uri
     */
    protected void notifyBeforeImport(final String uri) {
    }

    static protected List<String> getSupportedRDFLanguages(String prioLang) {
        // List of RDF languages, with the guessed language as the first element
        // listed
        List<String> rdfLanguages = new LinkedList<>(Arrays.asList(FileUtils.langXML,
                FileUtils.langTurtle, FileUtils.langXMLAbbrev, FileUtils.langNTriple,
                FileUtils.langN3));
        if (prioLang != null) {
            rdfLanguages.remove(prioLang);
            rdfLanguages.add(0, prioLang);
        }
        return rdfLanguages;
    }

    static protected Model readFromStream(Model model, InputStream stream, String guessedLang) {
        // try reading with any of the supported languages
        List<String> rdfLanguages = getSupportedRDFLanguages(guessedLang);
        for (String lang : rdfLanguages) {
            try {
                return model.read(stream, new String(), lang);
            }
            catch (Exception e) {
            }
        }
        return null;
    }

    static protected Model readFromUrl(Model model, String url, String guessedLang) {
        // try reading with any of the supported languages
        List<String> rdfLanguages = getSupportedRDFLanguages(guessedLang);
        for (String lang : rdfLanguages) {
            try {
                String redirectURL;
                do {
                    redirectURL = getRedirectURL(url, lang);
                    if (redirectURL != null) {
                        url = redirectURL;
                    }
                } while (redirectURL != null);
                return model.read(url, lang);
            }
            catch (Exception e) {
                logger.error("[Reading URL " + url + " w/ lang " + lang + "] \r\n         " + e);
                if (e.toString().contains("javax.net.ssl.SSLHandshakeException")) {
                    return null; // no use in trying other languages;
                                 // certificate issue
                }
            }
        }
        return null;
    }

    static protected Model getModelViaRegisteredPublisher(String url) {
        IPublisher publisher = PublicationProviderUtil.getPublisherFor(url);
        if (publisher != null) {
            return publisher.downloadModel(url, null);
        }
        return null;
    }

    public void setCacheDocumentManager(OntDocumentManager cacheDocumentManager) {
        this.cacheDocumentManager = cacheDocumentManager;
    }

}
