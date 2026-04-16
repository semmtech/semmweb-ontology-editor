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


import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import com.google.common.collect.Lists;
import com.semmtech.plugin.semmweb.core.cache.CacheManager;
import com.semmtech.plugin.semmweb.core.nature.SemanticProject;


public class ToggleSemanticProjectHandler extends AbstractHandler {

    private static Logger logger = Logger.getLogger(ToggleSemanticProjectHandler.class);
    public static final String ID = "com.semmtech.plugin.semmweb.core.commands.toggleSemanticProject";

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        ISelection selection = HandlerUtil.getCurrentSelection(event);
        if (!(selection instanceof IStructuredSelection)) {
            return null;
        }

        Iterator<?> iter = ((IStructuredSelection) selection).iterator();
        while (iter.hasNext()) {
            Object element = iter.next();
            if (!(element instanceof IProject))
                continue;
            IProject project = (IProject) element;
            // cannot modify closed projects
            if (!project.isOpen())
                continue;

            // get the description
            IProjectDescription description;
            try {
                description = project.getDescription();
            }
            catch (CoreException e) {
                e.printStackTrace();
                continue;
            }

            // Toggle the nature
            List<String> newIds = Lists.newArrayList();
            newIds.addAll(Arrays.asList(description.getNatureIds()));
            int index = newIds.indexOf(SemanticProject.NATURE_ID);
            if (index == -1) {
                newIds.add(SemanticProject.NATURE_ID);
                try {
                    project.setDefaultCharset("UTF-8", null);
                }
                catch (CoreException ex) {
                    logger.warn(
                            "Unable to set the default charset for the workspace root, see inner exception",
                            ex);
                }
            }
            else {
                newIds.remove(index);
            }
            description.setNatureIds(newIds.toArray(new String[newIds.size()]));

            // Save the description
            try {
                project.setDescription(description, null);
                if (index == -1) {
                    CacheManager manager = CacheManager.fromProject(project);
                    if (!manager.cacheFolderExists()) {
                        CacheManager.initNewProject(project);
                    }
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}
