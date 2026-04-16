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

package com.semmtech.ui.plugin.jobs;


import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;


/**
 * 
 * @author Sander Stolk
 */
public final class Jobs {

    public static void cancelWithJoin(Job job) {
        if (job == null) {
            return;
        }
        if (!job.cancel()) {
            try {
                job.join();
            }
            catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Schedules the given ResultJob, runs it synchronously, and returns its
     * result when available. If a monitor is provided and is cancelled during
     * the run of the ResultJob, this function will kill the job and return
     * null.
     */
    public static Object getResult(ResultJob job, IProgressMonitor monitor) {
        if (job == null) {
            return null;
        }

        final Object result[] = { null };
        final boolean resultObtained[] = { false };

        job.addResultListener(new ResultJob.ResultJobListener() {
            @Override
            public void setResult(Object r) {
                result[0] = r;
                resultObtained[0] = true;
            }
        });
        job.schedule();

        while (!resultObtained[0]) {
            boolean cancel = false;

            if (!cancel && monitor != null && monitor.isCanceled()) {
                cancel = true;
            }
            if (!cancel) {
                int sleepInterval = 1000;
                try {
                    Thread.sleep(sleepInterval);
                }
                catch (InterruptedException e) {
                    cancel = true;
                }
            }
            if (cancel) {
                Thread thread = job.getThread();
                if (thread != null) {
                    thread.interrupt();
                }
                return null;
            }
        }

        return (!resultObtained[0]) ? null : result[0];
    }
}
