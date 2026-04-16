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


import java.util.UUID;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.google.common.base.Strings;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelMaker;
import com.hp.hpl.jena.util.FileUtils;
import com.semmtech.plugin.semmweb.core.jena.ontology.WorkspaceDocumentManagerConfiguration;
import com.semmtech.plugin.semmweb.core.jena.ontology.WorkspaceOntologySpec;
import com.semmtech.plugin.semmweb.core.jobs.SemanticProjectBuildJob;
import com.semmtech.plugin.semmweb.core.model.FileModelMakerManager;
import com.semmtech.plugin.semmweb.core.preferences.DocumentManagerPreference;
import com.semmtech.plugin.semmweb.core.preferences.ModelsFolderPreference;
import com.semmtech.ui.plugin.jobs.JobWithMonitor;


/**
 * @author Simone Rondelli
 * 
 */
public class CacheModelJob extends JobWithMonitor {

    private static Logger logger = Logger.getLogger(CacheModelJob.class);

    private final IProject project;
    private final String modelUri;
    private final boolean temporary;
    private final Shell shell;
    private IFile cachedFile;

    private boolean rebuildProject;

    public CacheModelJob(IProject project, String modelUri, boolean temporary, Shell shell) {
        super("Caching Model " + modelUri);
        this.project = project;
        this.modelUri = modelUri;
        this.temporary = temporary;
        this.shell = shell;
        this.rebuildProject = false;
    }

    @Override
    protected IStatus run(IProgressMonitor monitor) {
        try {
            startMonitorUpdate(monitor, "Caching model " + modelUri, 3);
            updateSubTask("Creating model maker...");

            String modelsFolder = ModelsFolderPreference.fromProject(project).getModelsFolderPath();
            String qualifier;

            if (temporary) {
                qualifier = String.format("%s.tmp", modelsFolder);
            }
            else {
                qualifier = String.format("%s.cache", modelsFolder);
            }

            ModelMaker maker = FileModelMakerManager.getInstance()
                    .getModelMaker(project, qualifier);

            addWorked(1);
            updateSubTask("Retrieving model...");

            String url = modelUri;
            DocumentManagerPreference preferences = DocumentManagerPreference.fromProject(project);
            String altURL = preferences.getDocumentManagerConfig().getExternalAltURL(modelUri);
            if (!Strings.isNullOrEmpty(altURL)) {
                url = altURL;
            }
            String lang = FileUtils.guessLang(url);

            if (url.endsWith(".owl")) {
                lang = FileUtils.langXMLAbbrev;
            }

            maker.removeModel(modelUri);
            String filename = String.format("%s.rdf", UUID.randomUUID().toString());
            Model cachedModel = maker.createModel(filename);

            try {
                cachedModel.begin();
                cachedModel.read(url, lang);
                cachedModel.commit();
                cachedFile = project.getFile(qualifier + "/" + filename);
                cachedFile.getParent().refreshLocal(IResource.DEPTH_INFINITE, monitor);
            }
            catch (final Exception ex) {
                final String message = String.format(
                        "Error while caching the model with URI \"%s\"", modelUri);

                logger.error(message, ex);

                Display.getDefault().syncExec(new Runnable() {

                    @Override
                    public void run() {
                        MessageDialog.openError(shell, "Cache Error", message);
                    }
                });

                return Status.CANCEL_STATUS;
            }

            addWorked(1);

            if (!temporary) {
                updateSubTask("Updating Document Manager...");

                String projectLocation = project.getLocation().toString();
                String cachedPath = String.format("file:///%s/%s/%s", projectLocation, qualifier,
                        filename);

                WorkspaceDocumentManagerConfiguration config = preferences
                        .getDocumentManagerConfig();
                WorkspaceOntologySpec spec = config.getOntologySpec(modelUri);
                if (spec == null) {
                    spec = new WorkspaceOntologySpec(modelUri);
                }
                spec.setWorkspaceAltURL(cachedPath);
                config.addOntologySpec(spec);
                preferences.setDocumentManagerConfig(config);
                try {
                    CacheManager.fromProject(project).addFile(modelUri, cachedFile);
                    preferences.save();

                    if (rebuildProject) {
                        SemanticProjectBuildJob rebuild = new SemanticProjectBuildJob(project);
                        rebuild.setUser(false);
                        rebuild.schedule();
                    }
                }
                catch (Exception ex) {
                    logger.error("Exception occured trying to save document manager preferences",
                            ex);
                }
            }

            addWorked(1);
        }
        finally {
            stopMonitorUpdate();
            monitor.done();
        }

        return Status.OK_STATUS;
    }

    public IFile getCachedFile() {
        return cachedFile;
    }

    /**
     * If set to true launch a rebuild of the project in the end of the cache
     * process.
     * 
     * @param rebuildProject
     */
    public void setRebuildProject(boolean rebuildProject) {
        this.rebuildProject = rebuildProject;
    }
}