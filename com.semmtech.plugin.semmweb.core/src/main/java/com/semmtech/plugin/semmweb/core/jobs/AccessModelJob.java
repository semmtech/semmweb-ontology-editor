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


import com.semmtech.plugin.semmweb.core.model.IModelProvider;
import com.semmtech.ui.plugin.jobs.JobWithMonitor;


public abstract class AccessModelJob extends JobWithMonitor {
    private IModelProvider modelProvider;

    public AccessModelJob(String name) {
        super(name);
    }

    public void schedule(IModelProvider modelProvider) {
        this.modelProvider = modelProvider;
        schedule();
    }

    public void setModelProvider(IModelProvider modelProvider) {
        this.modelProvider = modelProvider;
    }

    public IModelProvider getModelProvider() {
        return modelProvider;
    }

}
