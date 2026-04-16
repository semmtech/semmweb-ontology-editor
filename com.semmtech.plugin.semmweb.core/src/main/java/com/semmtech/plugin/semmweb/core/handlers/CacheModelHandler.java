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

package com.semmtech.plugin.semmweb.core.handlers;


import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;
import org.eclipse.ui.services.IServiceLocator;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.CorePluginImages;
import com.semmtech.plugin.semmweb.core.cache.CacheModelJob;


/**
 * 
 * @author Mike Henrichs
 */
public class CacheModelHandler extends AbstractHandler {

    private static final Logger logger = Logger.getLogger(CacheModelHandler.class);

    public static final String ID = "com.semmtech.plugin.semmweb.core.commands.cacheModel";

    public static final String PARAMETER_MODEL_URI = "modelUri";
    public static final String PARAMETER_MODEL_URL = "modelUrl";
    public static final String PARAMETER_PROJECT_NAME = "projectName";
    public static final String PARAMETER_MODELS_FOLDER = "modelsFolder";

    /**
     * If this parameter is set to true the model will be downloaded to .tmp
     * directory instead of .cache and won't be added to the Document Manager.
     * 
     * Moreover the created file is returned.
     */
    public static final String PARAMETER_TEMPORARY = "temporary";

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        final IProject project = getProject(event);
        String modelUri = event.getParameter(PARAMETER_MODEL_URI);
        boolean temnporary = "true".equalsIgnoreCase(event.getParameter(PARAMETER_TEMPORARY));

        if (project != null && !Strings.isNullOrEmpty(modelUri)) {
            CacheModelJob cacheModelJob = new CacheModelJob(project, modelUri, temnporary,
                    HandlerUtil.getActiveShell(event));
            cacheModelJob.setUser(true);
            cacheModelJob.setRebuildProject(true);
            cacheModelJob.schedule();

            if (temnporary) {
                try {
                    cacheModelJob.join();
                    return cacheModelJob.getCachedFile();
                }
                catch (InterruptedException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
        return null;
    }

    private IProject getProject(ExecutionEvent event) {
        String projectName = event.getParameter(PARAMETER_PROJECT_NAME);
        return ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
    }

    public static CommandContributionItem createCommand(IServiceLocator serviceLocator,
            String label, String modelUri, String projectName) {
        return createCommand(serviceLocator, label, modelUri, projectName,
                CorePluginImages.IMG_ONTOLOGY_CACHE);
    }

    public static CommandContributionItem createCommand(IServiceLocator serviceLocator,
            String label, String modelUri, String projectName, String imageKey) {

        CommandContributionItemParameter param = new CommandContributionItemParameter(
                serviceLocator, null, ID, SWT.PUSH);

        Map<String, String> parameters = Maps.newHashMap();
        parameters.put(PARAMETER_MODEL_URI, modelUri);
        parameters.put(PARAMETER_PROJECT_NAME, projectName);

        param.label = label;
        param.parameters = parameters;
        param.icon = CorePlugin.getDefault().getImageDescriptor(imageKey);

        return new CommandContributionItem(param);
    }

}
