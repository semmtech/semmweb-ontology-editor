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

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

import com.google.common.collect.Lists;
import com.semmtech.plugin.semmweb.core.CorePlugin;


public class CoreExtensions {

    private static Logger logger = Logger.getLogger(CoreExtensions.class);

    public static final String VALIDATORS_ID = "modelValidators";
    public static final String PROCESSORS_ID = "modelProcessors";
    public static final String MODEL_DELETION_ID = "modelDeletion";
    public static final String MODEL_RESOURCE_CONTENT_ID = "modelResourceContent";

    public static final String CLASS_PROPERTY = "class";

    private CoreExtensions() {
    }

    /**
     * Returns the configuration elements for the given Core's extension point
     * 
     * @param extensionPointName
     * @return
     */
    public static IConfigurationElement[] getConfigurationElementsFor(String extensionPointName) {
        IConfigurationElement[] elements = Platform.getExtensionRegistry()
                .getConfigurationElementsFor(CorePlugin.PLUGIN_ID, extensionPointName);
        return elements;
    }

    public static List<IModelValidator> findValidators() {
        final List<IModelValidator> validators = Lists.newArrayList();
        try {
            for (IConfigurationElement element : getConfigurationElementsFor(VALIDATORS_ID)) {
                Object object = element.createExecutableExtension(CLASS_PROPERTY);
                if (object instanceof IModelValidator)
                    validators.add((IModelValidator) object);
            }
        }
        catch (CoreException ex) {
            logger.error("Error while getting extension: " + VALIDATORS_ID, ex);
        }
        return validators;
    }

    public static List<IModelProcessor> findProcessors() {
        final List<IModelProcessor> validators = Lists.newArrayList();
        try {
            for (IConfigurationElement element : getConfigurationElementsFor(PROCESSORS_ID)) {
                Object object = element.createExecutableExtension(CLASS_PROPERTY);
                if (object instanceof IModelProcessor)
                    validators.add((IModelProcessor) object);
            }
        }
        catch (CoreException ex) {
            logger.error("Error while getting extension: " + PROCESSORS_ID, ex);
        }
        return validators;
    }

    public static List<IModelDeletionListener> findDeletionListener() {
        List<IModelDeletionListener> modelDeletionListener = Lists.newArrayList();
        IExtensionRegistry registry = Platform.getExtensionRegistry();
        IConfigurationElement[] config = registry.getConfigurationElementsFor(CorePlugin.PLUGIN_ID,
                MODEL_DELETION_ID);

        for (IConfigurationElement conf : config) {
            try {
                Object extension = conf.createExecutableExtension("class");

                if (extension instanceof IModelDeletionListener) {
                    IModelDeletionListener menuExtender = (IModelDeletionListener) extension;
                    modelDeletionListener.add(menuExtender);
                }
            }
            catch (CoreException ex) {
                logger.error("Error while getting extension: " + MODEL_DELETION_ID, ex);
            }
        }
        return modelDeletionListener;
    }
}
