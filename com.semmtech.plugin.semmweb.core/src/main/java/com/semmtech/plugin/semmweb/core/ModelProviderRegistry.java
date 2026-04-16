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

package com.semmtech.plugin.semmweb.core;


import java.util.Map;

import com.google.common.collect.Maps;
import com.semmtech.plugin.semmweb.core.model.IModelProvider;


/**
 * This class will be used to map model URI between IModelProvider. The model's
 * URI is equal to the URI used to open the editor's input, this URI is
 * retrieved using the IURIEditorInput interface (also see the
 * SemanticModelEditior).
 * 
 * @author Mike Henrichs
 * 
 */
public class ModelProviderRegistry {

    private static Map<String, IModelProvider> providers = Maps.newHashMap();

    /**
     * Register the provided provider as the IModelProvider for the model with
     * model URI as provided.
     * 
     * @param modelURI
     * @param provider
     */
    public static void register(String modelURI, IModelProvider provider) {
        providers.put(modelURI, provider);
    }

    /**
     * Unregisters the provided provider for the model with model URI as
     * provided.
     * 
     * @param modelURI
     * @param provider
     */
    public static void unregister(String modelURI) {
        providers.remove(modelURI);
    }

    /**
     * Returns the IModelProvider for the given model with provided Model URI.
     * 
     * @param modelURI
     * @return
     */
    public static IModelProvider getProvider(String modelURI) {
        return providers.get(modelURI);
    }

    /**
     * Returns true if there is a known provider for the specified modelURI
     * 
     * @param modelURI
     * @return true if there is a known provider for the specified modelURI
     */
    public static boolean exists(String modelURI) {
        return providers.containsKey(modelURI);
    }
}
