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
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import com.semmtech.plugin.semmweb.core.jena.ontology.WorkspaceDocumentManagerConfiguration;
import com.semmtech.plugin.semmweb.core.jobs.SemanticProjectBuildJob;
import com.semmtech.plugin.semmweb.core.preferences.DocumentManagerPreference;
import com.semmtech.ui.plugin.EclipseUIPlugin;
import com.semmtech.ui.plugin.jobs.JobWithMonitor;


public class ClearCacheJob extends JobWithMonitor {

    private static Logger logger = Logger.getLogger(ClearCacheJob.class);

    private IProject project;
    private List<String> publicUris;
    private Shell shell;

    private boolean rebuildProject;

    public ClearCacheJob(IProject project, List<String> publicUris) {
        super("Clear Cache");
        this.publicUris = publicUris;
        this.project = project;
        this.shell = EclipseUIPlugin.getStandardDisplay().getActiveShell();
        this.rebuildProject = false;
    }

    @Override
    protected IStatus run(IProgressMonitor monitor) {
        monitor.beginTask("Clearing cache...", 3 * publicUris.size());
        IStatus status = Status.CANCEL_STATUS;

        for (String publicUri : publicUris) {
            try {
                monitor.subTask("Clearing cache for URI: " + publicUri);

                CacheManager manager = CacheManager.fromProject(project);
                IFile cacheFile = manager.getCacheFile(publicUri);
                if (cacheFile == null) {
                    continue;
                }

                monitor.subTask("Updating mapping in Document Manager...");

                DocumentManagerPreference preferences = DocumentManagerPreference
                        .fromProject(project);
                WorkspaceDocumentManagerConfiguration config = preferences
                        .getDocumentManagerConfig();
                config.setWorkspaceAltURL(publicUri, null);
                preferences.setDocumentManagerConfig(config);
                preferences.save();

                monitor.subTask("Deleting file...");

                manager.removeFile(publicUri);
                cacheFile.delete(true, monitor);
                manager.getCacheFolder(false).refreshLocal(IResource.DEPTH_INFINITE, monitor);

                status = Status.OK_STATUS;

            }
            catch (IOException e) {
                String message = "Error while updating the Document Manager " + publicUri + ": "
                        + e.getMessage();
                logger.error(message);
                showError(message);
            }
            catch (CoreException e) {
                String message = "Error while deleting cache file of " + publicUri + ": "
                        + e.getMessage();
                logger.error(message);
                String message2 = "However the configuration has been updated and the cache will no longer be used.";
                showError(message + "\n\n" + message2);
            }

        }

        if (rebuildProject) {
            SemanticProjectBuildJob rebuild = new SemanticProjectBuildJob(project);
            rebuild.setUser(false);
            rebuild.schedule();
        }

        stopMonitorUpdate();
        monitor.done();

        return status;
    }

    private void showError(final String message) {
        shell.getDisplay().syncExec(new Runnable() {

            @Override
            public void run() {
                MessageDialog.openError(shell, "Error", message);
            }
        });
    }

    public void setRebuildProject(boolean rebuildProject) {
        this.rebuildProject = rebuildProject;
    }

}
