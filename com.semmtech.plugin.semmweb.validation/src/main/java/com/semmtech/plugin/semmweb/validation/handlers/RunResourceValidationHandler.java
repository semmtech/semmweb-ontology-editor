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

package com.semmtech.plugin.semmweb.validation.handlers;


import org.apache.log4j.Logger;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.jobs.Job;

import com.semmtech.plugin.semmweb.validation.jobs.RunResourcesValidationJob;


/**
 * 
 * @author Sander Stolk
 */
public class RunResourceValidationHandler extends AbstractValidationHandler {
    private static Logger logger = Logger.getLogger(RunResourceValidationHandler.class);
    public static final String ID = "com.semmtech.plugin.semmweb.validation.commands.runResourceValidation";

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        logger.debug("execute called!");
        super.execute(event);

        if (file != null && provider != null && activeResource != null) {
            executeJobWithPreferredInference(new JobCreator() {
                @Override
                public Job createJob() {
                    // run resources validation job
                    RunResourcesValidationJob job = new RunResourcesValidationJob(file, provider,
                            activeResource);
                    job.setUser(true);
                    return job;
                }
            });

        }
        return null;
    }

}
