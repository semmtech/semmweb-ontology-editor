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


import org.apache.log4j.Logger;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;


public class SimpleJobChangeReporter extends JobChangeAdapter {
    private static final Logger logger = Logger.getLogger(SimpleJobChangeReporter.class);

    @Override
    public void scheduled(IJobChangeEvent event) {
        logger.debug("Scheduling job '" + event.getJob().getName() + "'");
    }

    @Override
    public void running(IJobChangeEvent event) {
        logger.debug("Running job '" + event.getJob().getName() + "'");
    }

    @Override
    public void awake(IJobChangeEvent event) {
        logger.debug("Awakening job '" + event.getJob().getName() + "'");
    }

    @Override
    public void aboutToRun(IJobChangeEvent event) {
        logger.debug("About to run job '" + event.getJob().getName() + "'");
    }

    @Override
    public void sleeping(IJobChangeEvent event) {
        logger.debug("Sleeping job '" + event.getJob().getName() + "'");
    }

    @Override
    public void done(IJobChangeEvent event) {
        if (event.getResult().isOK()) {
            logger.debug("Job '" + event.getJob().getName() + "' completed successfully");
        }
        else {
            logger.debug("Job '" + event.getJob().getName() + "' did not complete successfully");
        }
    }
}
