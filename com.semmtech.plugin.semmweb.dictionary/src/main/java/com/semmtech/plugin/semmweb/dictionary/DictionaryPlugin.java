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

package com.semmtech.plugin.semmweb.dictionary;


import java.util.List;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;

import com.google.common.collect.Lists;
import com.semmtech.ui.plugin.EclipseUIPlugin;


/**
 * The activator class controls the plug-in life cycle
 */
public class DictionaryPlugin extends EclipseUIPlugin {

    // The plug-in ID
    public static final String PLUGIN_ID = "com.semmtech.plugin.semmweb.dictionary"; //$NON-NLS-1$

    // The shared instance
    private static DictionaryPlugin plugin;

    private final Logger logger = Logger.getLogger(DictionaryPlugin.class);
    private List<DictionaryPluginEventListener> pluginListeners = Lists.newArrayList();

    public void addPluginListener(DictionaryPluginEventListener listener) {
        if (!pluginListeners.contains(listener)) {
            pluginListeners.add(listener);
        }
    }

    public void removePluginListener(DictionaryPluginEventListener listener) {
        pluginListeners.remove(listener);
    }

    public void notifyEvent(DictionaryPluginEvent event) {
        for (DictionaryPluginEventListener listener : pluginListeners) {
            listener.notifyEvent(event);
        }
    }

    /**
     * The constructor
     */
    public DictionaryPlugin() {
        super(PLUGIN_ID);
    }

    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;

        logger.info("Dictionary plugin started");
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }

    /**
     * Returns the shared instance
     * 
     * @return the shared instance
     */
    public static DictionaryPlugin getDefault() {
        return plugin;
    }
}
