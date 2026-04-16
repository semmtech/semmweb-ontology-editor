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

package com.semmtech.plugin.semmweb.core.extensionpoint;


import org.eclipse.jface.preference.IPreferenceStore;

import com.hp.hpl.jena.rdf.model.Model;


public abstract class AbstractModelProcessor implements IModelProcessor {
    private final IPreferenceStore preferenceStore;
    private final String processorId;

    protected AbstractModelProcessor(String processorId, IPreferenceStore preferenceStore) {
        this(processorId, preferenceStore, false);
    }

    protected AbstractModelProcessor(String processorId, IPreferenceStore preferenceStore,
            boolean defaultState) {
        this.processorId = processorId;
        this.preferenceStore = preferenceStore;
        if (!preferenceStore.contains(processorId)) {
            preferenceStore.setDefault(processorId, defaultState);
        }
    }

    @Override
    public abstract void processModel(Model model);

    @Override
    public abstract String getName();

    @Override
    public abstract String getDescription();

    @Override
    public boolean isEnabled() {
        return preferenceStore.getBoolean(processorId);
    }

    @Override
    public void setEnabled(boolean enabled) {
        preferenceStore.setValue(processorId, enabled);
    }
}
