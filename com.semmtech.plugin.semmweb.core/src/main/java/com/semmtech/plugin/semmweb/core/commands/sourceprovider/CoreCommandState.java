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

package com.semmtech.plugin.semmweb.core.commands.sourceprovider;


import java.util.Map;

import org.eclipse.ui.AbstractSourceProvider;
import org.eclipse.ui.ISources;

import com.google.common.collect.Maps;
import com.semmtech.plugin.semmweb.core.CorePlugin;


/**
 * SourceProvider for the command state used by the CorePlugin.
 * 
 * @author Mike Henrichs
 * 
 */
public class CoreCommandState extends AbstractSourceProvider {
    /** State used to keep track of an active model provider */
    public final static String MODEL_PROVIDER_ACTIVE_STATE = "com.semmtech.plugin.semmweb.core.commands.sourceprovider.modelProviderActive";
    /** Active */
    public final static String ACTIVE = "ACTIVE";
    /** Inactive */
    public final static String INACTIVE = "INACTIVE";

    private boolean modelActive;

    public CoreCommandState() {

    }

    @Override
    public void dispose() {
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Map getCurrentState() {
        Map<String, String> map = Maps.newHashMapWithExpectedSize(1);
        String value = (modelActive) ? ACTIVE : INACTIVE;
        map.put(MODEL_PROVIDER_ACTIVE_STATE, value);
        return map;
    }

    @Override
    public String[] getProvidedSourceNames() {
        return new String[] { MODEL_PROVIDER_ACTIVE_STATE };
    }

    public boolean isModelActive() {
        return modelActive;
    }

    /**
     * Refreshes the state to determine whether a model provider is active.
     */
    public void updateState() {
        if (!modelActive && CorePlugin.getDefault().getActiveModelProvider() != null) {
            modelActive = true;
            fireSourceChanged(ISources.WORKBENCH, MODEL_PROVIDER_ACTIVE_STATE, ACTIVE);
        }
        else if (modelActive && CorePlugin.getDefault().getActiveModelProvider() == null) {
            modelActive = false;
            fireSourceChanged(ISources.WORKBENCH, MODEL_PROVIDER_ACTIVE_STATE, INACTIVE);
        }
    }
}
