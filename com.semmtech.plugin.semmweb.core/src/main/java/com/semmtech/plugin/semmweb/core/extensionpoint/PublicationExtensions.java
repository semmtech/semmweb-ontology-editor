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


import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SafeRunner;

import com.google.common.collect.Lists;
import com.semmtech.plugin.semmweb.core.CorePlugin;


public final class PublicationExtensions {
    private static final String PUBLISHERS_ID = "publishers";
    private static final String CLASS_PROPERTY = "class";

    private PublicationExtensions() {
    }

    public static List<IPublicationProvider> findProviders() {
        final List<IPublicationProvider> providers = Lists.newArrayList();
        IConfigurationElement[] config = Platform.getExtensionRegistry()
                .getConfigurationElementsFor(CorePlugin.PLUGIN_ID, PUBLISHERS_ID);
        try {
            for (IConfigurationElement element : config) {
                Object object = element.createExecutableExtension(CLASS_PROPERTY);
                if (object instanceof IPublicationProvider)
                    providers.add((IPublicationProvider) object);
            }
        }
        catch (CoreException ex) {
            ex.printStackTrace();
        }
        return providers;
    }

    public static List<IPublisher> findPublishers() {
        final List<IPublisher> publishers = Lists.newArrayList();
        IConfigurationElement[] config = Platform.getExtensionRegistry()
                .getConfigurationElementsFor(CorePlugin.PLUGIN_ID, PUBLISHERS_ID);
        try {
            for (IConfigurationElement element : config) {
                final Object object = element.createExecutableExtension(CLASS_PROPERTY);
                if (object instanceof IPublicationProvider) {
                    ISafeRunnable runnable = new ISafeRunnable() {
                        @Override
                        public void handleException(Throwable e) {
                            System.err.println("Exception in publishProvider!");
                        }

                        @Override
                        public void run() throws Exception {
                            publishers.addAll(((IPublicationProvider) object).getPublishers());
                        }
                    };
                    SafeRunner.run(runnable);
                }
            }
        }
        catch (CoreException ex) {
            ex.printStackTrace();
        }
        return publishers;
    }
}
