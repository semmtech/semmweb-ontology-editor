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
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;


public abstract class JobWithMonitor extends Job {

    private String subtask;
    private Job monitorUpdateJob;
    private int interval = 200;
    private int worked;

    public JobWithMonitor(String name) {
        super(name);
    }

    protected void startMonitorUpdate(final IProgressMonitor monitor) {
        monitorUpdateJob = new Job("__monitorUpdateJob") {

            @Override
            public IStatus run(IProgressMonitor inner) {
                while (!inner.isCanceled()) {
                    if (worked > 0) {
                        monitor.worked(worked);
                        worked = 0;
                    }
                    monitor.subTask(subtask);
                    try {
                        Thread.sleep(interval);
                    }
                    catch (InterruptedException e) {
                        return Status.OK_STATUS;
                    }
                }
                return Status.OK_STATUS;
            }
        };
        monitorUpdateJob.setSystem(true);
        monitorUpdateJob.schedule();
    }

    protected void startMonitorUpdate(final IProgressMonitor monitor, final String name,
            final int totalWork) {
        monitorUpdateJob = new Job("__monitorUpdateJob") {
            @Override
            public IStatus run(IProgressMonitor inner) {
                monitor.beginTask(name, totalWork);
                while (!inner.isCanceled()) {
                    if (worked > 0) {
                        monitor.worked(worked);
                        worked = 0;
                    }
                    monitor.subTask(subtask);
                    try {
                        Thread.sleep(interval);
                    }
                    catch (InterruptedException e) {
                        return Status.OK_STATUS;
                    }
                }
                return Status.OK_STATUS;
            }
        };
        monitorUpdateJob.setSystem(true);
        monitorUpdateJob.schedule();
    }

    protected void addWorked(int worked) {
        this.worked += worked;
    }

    protected void stopMonitorUpdate() {
        if (monitorUpdateJob != null) {
            Jobs.cancelWithJoin(monitorUpdateJob);
        }
    }

    public void setUpdateInterval(int milliseconds) {
        this.interval = milliseconds;
    }

    protected void updateSubTask(String subtask) {
        this.subtask = subtask;
    }
}
