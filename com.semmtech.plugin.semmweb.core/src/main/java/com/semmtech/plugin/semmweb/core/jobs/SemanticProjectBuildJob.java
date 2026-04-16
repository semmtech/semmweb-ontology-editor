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


import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.google.common.collect.Maps;
import com.semmtech.plugin.semmweb.core.builders.SemanticProjectBuilder;


/**
 * This job is useful to run async build of semantic projects without hanging
 * the interface.
 * 
 * @author Simone Rondelli
 */
public class SemanticProjectBuildJob extends Job {

    private static Logger logger = Logger.getLogger(SemanticProjectBuildJob.class);

    private final IProject project;
    private final int buildKind;
    private final Map<String, String> args;

    /**
     * Create a BuildProjectJob with
     * {@link IncrementalProjectBuilder#INCREMENTAL_BUILD} as default build kind
     */
    public SemanticProjectBuildJob(IProject project) {
        this(project, IncrementalProjectBuilder.INCREMENTAL_BUILD);
    }

    public SemanticProjectBuildJob(IProject project, int buildKind) {
        super("Build Project");
        this.project = project;
        this.buildKind = buildKind;
        args = Maps.newHashMap();
    }

    public void setArgs(Map<String, String> args) {
        this.args.clear();
        this.args.putAll(args);
    }

    public void addArg(String key, String value) {
        args.put(key, value);
    }

    @Override
    protected IStatus run(IProgressMonitor monitor) {
        try {
            project.build(buildKind, SemanticProjectBuilder.BUILDER_ID, args, monitor);
        }
        catch (CoreException ex) {
            logger.error("An error occurred while building the project: " + project, ex);
            return Status.CANCEL_STATUS;
        }
        return Status.OK_STATUS;
    }

}
