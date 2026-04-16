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


import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.google.common.collect.Lists;
import com.hp.hpl.jena.ontology.OntModel;
import com.semmtech.plugin.semmweb.core.preferences.DocumentManagerPreference;


public class RefreshImportsJob extends LoadModelJob {

    public RefreshImportsJob(IFile file, String name, OntModel ontModel) {
        super(file, name, true, false);
        super.ontModel = ontModel;
    }

    @Override
    protected IStatus run(IProgressMonitor monitor) {
        startMonitorUpdate(monitor, "Loading Imports for " + file.getName(), 5);

        IProject project = getProject();
        DocumentManagerPreference preferences = DocumentManagerPreference.fromProject(project);
        manager = preferences.getDocumentManagerConfig().createManager();

        boolean done = false;
        queuedImportUris = Lists.newLinkedList();
        while (!done && !monitor.isCanceled()) {
            done = !handleImports(monitor);
        }

        if (listener != null && !monitor.isCanceled()) {
            listener.modelLoaded(ontModel);
            listener.modelLoaded(ontModel, metaData);
        }

        monitor.done();
        stopMonitorUpdate();

        return monitor.isCanceled() ? Status.CANCEL_STATUS : Status.OK_STATUS;
    }
}
