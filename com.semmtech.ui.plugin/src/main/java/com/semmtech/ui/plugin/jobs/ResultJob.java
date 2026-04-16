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


import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;

import com.google.common.collect.Lists;


/**
 * 
 * @author Sander Stolk
 */
abstract public class ResultJob extends Job {
    protected final List<ResultJobListener> resultListeners;

    public ResultJob(String name) {
        super(name);
        this.resultListeners = Lists.newArrayList();
    }

    /**
     * Should always return its result before returning. Do so by calling the
     * method returnResult.
     */
    @Override
    abstract protected IStatus run(IProgressMonitor monitor);

    protected void addResultListener(ResultJobListener listener) {
        resultListeners.add(listener);
    }

    protected void returnResult(Object result) {
        for (ResultJobListener resultListener : resultListeners) {
            resultListener.setResult(result);
        }
    }

    public interface ResultJobListener {
        public void setResult(Object result);
    }
}
