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

package com.semmtech.plugin.semmweb.core.cache;


import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.hp.hpl.jena.sparql.vocabulary.FOAF;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.DC_11;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.jena.ontology.WorkspaceDocumentManagerConfiguration;
import com.semmtech.plugin.semmweb.core.preferences.DocumentManagerPreference;
import com.semmtech.plugin.semmweb.core.preferences.ModelsFolderPreference;
import com.semmtech.plugin.semmweb.core.util.SemanticProjectUtils;
import com.semmtech.plugin.semmweb.core.util.URIs;
import com.semmtech.semantics.vocabulary.SKOS;
import com.semmtech.semantics.vocabulary.SKOSXL;
import com.semmtech.semantics.vocabulary.VANN;


/**
 * Manages project cache.
 * 
 * @author Simone Rondelli
 * 
 */
public class CacheManager {
    private static final String CACHE_MODELS_FOLDER = "src/main/resources/models/cache/";
    private static final String RDF_FILENAME = "rdf.rdf";
    private static final String RDFS_FILENAME = "rdfs.rdf";
    private static final String OWL_FILENAME = "owl.rdf";
    private static final String DC_FILENAME = "dc.ttl";
    private static final String DCTERMS_FILENAME = "dcterms.ttl";
    private static final String FOAF_FILENAME = "foaf.rdf";
    private static final String SKOS_FILENAME = "skos.rdf";
    private static final String SKOSXL_FILENAME = "skosxl.rdf";
    private static final String VANN_FILENAME = "vann.rdf";

    private static final Logger logger = Logger.getLogger(CacheManager.class);

    public static final String CACHE_DIR = ".cache";

    private static final Map<IProject, CacheManager> managers;

    private final IProject project;
    private IFolder cacheFolder;
    private Map<String, IFile> mappings;

    static {
        managers = Maps.newConcurrentMap();
    }

    private CacheManager(IProject project) {
        this.project = project;
        mappings = Maps.newConcurrentMap();
    }

    public static synchronized CacheManager fromProject(IProject project) {
        CacheManager manager = managers.get(project);
        if (manager == null) {
            manager = new CacheManager(project);
            managers.put(project, manager);
        }
        return manager;
    }

    /**
     * If the folder doesn't exist, this method tries to create it. If the
     * creation of the folder is performed during the building of the project
     * the workspace will hang. Use the method {@link #cacheFolderExists()} to
     * determine if the folder exists without creating it.
     */
    public IFolder getCacheFolder(boolean createIfAbsent) {
        if (cacheFolder == null || !cacheFolder.exists()) {
            try {
                IFolder modelsFolder = SemanticProjectUtils.getModelsFolder(project);
                if (modelsFolder == null || !modelsFolder.exists()) {
                    String modelsFolderPath = ModelsFolderPreference.fromProject(project)
                            .getModelsFolderPath();
                    modelsFolder = project.getFolder(modelsFolderPath);
                    modelsFolder.create(true, true, null);
                }

                cacheFolder = modelsFolder.getFolder(CACHE_DIR);
                if (!cacheFolder.exists() && createIfAbsent) {
                    cacheFolder.create(true, true, null);
                }
            }
            catch (Exception e) {
                throw new IllegalStateException("Impossible to create .cache folder");
            }
        }
        return (cacheFolder == null || !cacheFolder.exists()) ? null : cacheFolder;
    }

    public boolean cacheFolderExists() {
        IFolder modelsFolder = SemanticProjectUtils.getModelsFolder(project);
        if (modelsFolder == null || !modelsFolder.exists()) {
            return false;
        }
        IFolder cacheFolder = modelsFolder.getFolder(CACHE_DIR);
        return (cacheFolder != null && cacheFolder.exists());
    }

    /**
     * Returns the cached file for the passed public URI
     * 
     * @param publicUri
     * @return File or null
     */
    public IFile getCacheFile(String publicUri) {
        IFile cachedFile = mappings.get(publicUri);

        if (cachedFile == null || !cachedFile.exists()) {
            IFolder cacheFolder = getCacheFolder(false);
            if (cacheFolder == null) {
                return null;
            }

            DocumentManagerPreference preferences = DocumentManagerPreference.fromProject(project);
            String altUrl = preferences.getDocumentManagerConfig().getAltURL(publicUri);
            if (altUrl == null || !URIs.hasFileScheme(altUrl)) {
                return null;
            }

            try {
                altUrl = StringUtils.removeStart(altUrl, "file:");
                altUrl = StringUtils.stripStart(altUrl, "/");
                IPath cachePath = new Path(altUrl);
                IPath cacheFolderPath = cachePath.removeLastSegments(1);
                if (!cacheFolder.getLocation().equals(cacheFolderPath)) {
                    // alt url folder did not match cached folder
                    return null;
                }
                String cacheFilenamePath = cachePath.lastSegment();
                cachedFile = cacheFolder.getFile(cacheFilenamePath);

                if (cachedFile.exists()) {
                    mappings.put(publicUri, cachedFile);
                }
            }
            catch (Throwable t) {
                return null;
            }
        }

        if (cachedFile.exists()) {
            return cachedFile;
        }

        return null;
    }

    /**
     * Returns true if the model denoted by public URI is in the local cache
     * folder under the project directory
     * <p>
     * <b>Warning:</b> Typically you would want to use the function
     * isInCache(String publicUri, String altURL) instead.
     * </p>
     */
    public boolean isInCache(String publicUri) {
        return getCacheFile(publicUri) != null;
    }

    /**
     * Returns true if the model denoted by public URI is in the local cache
     * folder under the project directory with the given altURL.
     */
    public boolean isInCache(String publicUri, String altURL) {
        IFile cachedFile = getCacheFile(publicUri);
        if (cachedFile == null || !cachedFile.exists() || altURL == null) {
            return false;
        }

        String filePrefix = "file:";

        altURL = com.semmtech.net.URIUtils.normalizeUrl(altURL);
        altURL = StringUtils.removeStart(altURL, filePrefix);
        altURL = StringUtils.stripStart(altURL, "/");

        String cachedURL = cachedFile.getLocationURI().toString();
        cachedURL = StringUtils.removeStart(cachedURL, filePrefix);
        cachedURL = StringUtils.stripStart(cachedURL, "/");

        return altURL.equals(cachedURL);
    }

    public void addFile(String publicUri, IFile file) {
        mappings.put(publicUri, file);
    }

    public void removeFile(String publicUri) {
        mappings.remove(publicUri);
    }

    public static void initNewProject(IProject project) throws Exception {
        Bundle bundle = Platform.getBundle(CorePlugin.PLUGIN_ID);
        Enumeration<URL> urls = bundle.findEntries(CACHE_MODELS_FOLDER, "*", true);

        if (urls == null) {
            return;
        }

        CacheManager manager = CacheManager.fromProject(project);
        IFolder cacheFolder = manager.getCacheFolder(true);
        if (cacheFolder == null || !cacheFolder.exists()) {
            throw new Exception("Could not create the cache folder.");
        }

        while (urls.hasMoreElements()) {
            final URL url = urls.nextElement();
            try {
                String fileName = FilenameUtils.getName(url.toString());
                if (Strings.isNullOrEmpty(fileName)) {
                    continue;
                }
                if (!fileName.endsWith(".rdf") && !fileName.endsWith(".ttl")) {
                    continue;
                }
                IFile destFile = cacheFolder.getFile(fileName);

                if (!destFile.exists()) {
                    logger.debug("Creating cache file: " + destFile.getFullPath());
                    destFile.create(url.openStream(), true, null);
                }

                if (OWL_FILENAME.equalsIgnoreCase(fileName)) {
                    manager.mappings.put(OWL.getURI(), destFile);
                }
                else if (RDF_FILENAME.equalsIgnoreCase(fileName)) {
                    manager.mappings.put(RDF.getURI(), destFile);
                }
                else if (RDFS_FILENAME.equalsIgnoreCase(fileName)) {
                    manager.mappings.put(RDFS.getURI(), destFile);
                }
                else if (DC_FILENAME.equalsIgnoreCase(fileName)) {
                    manager.mappings.put(DC_11.getURI(), destFile);
                }
                else if (DCTERMS_FILENAME.equalsIgnoreCase(fileName)) {
                    manager.mappings.put(DCTerms.getURI(), destFile);
                }
                else if (FOAF_FILENAME.equalsIgnoreCase(fileName)) {
                    manager.mappings.put(FOAF.getURI(), destFile);
                }
                else if (SKOS_FILENAME.equalsIgnoreCase(fileName)) {
                    manager.mappings.put(SKOS.getURI(), destFile);
                }
                else if (SKOSXL_FILENAME.equalsIgnoreCase(fileName)) {
                    manager.mappings.put(SKOSXL.getURI(), destFile);
                }
                else if (VANN_FILENAME.equalsIgnoreCase(fileName)) {
                    manager.mappings.put(VANN.getURI(), destFile);
                }
            }
            catch (IOException e) {
                String message = "Error while initializing models cache: " + e.getMessage();
                throw new IllegalStateException(message, e);
            }
        }

        DocumentManagerPreference documentManagerPreference = DocumentManagerPreference
                .fromProject(project);
        WorkspaceDocumentManagerConfiguration ontConf = documentManagerPreference
                .getDocumentManagerConfig();
        for (String uri : ontConf.listPublicURIs()) {
            IFile file = manager.getCacheFile(uri);
            if (file != null) {
                ontConf.setWorkspaceAltURL(uri, file.getLocation().toString());
            }
        }
        documentManagerPreference.setDocumentManagerConfig(ontConf);
        documentManagerPreference.save();
    }

}
