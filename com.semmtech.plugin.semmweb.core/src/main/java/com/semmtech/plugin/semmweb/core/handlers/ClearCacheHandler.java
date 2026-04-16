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


import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;
import org.eclipse.ui.services.IServiceLocator;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.cache.ClearCacheJob;
import com.semmtech.plugin.semmweb.core.navigator.IImport;
import com.semmtech.ui.plugin.util.Selections;


/**
 * Delete the cache file of the selected import and update the Document Manager
 * 
 * @author Simone Rondelli
 */
public class ClearCacheHandler extends AbstractHandler {

    public static final String ID = "com.semmtech.plugin.semmweb.core.commands.clearCache";

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        ISelection selection = HandlerUtil.getCurrentSelection(event);
        List<IImport> imports = Selections.retrieveAllAsType(selection, IImport.class);

        List<ClearCacheJob> jobs = createClearCacheJobs(imports);
        for (ClearCacheJob job : jobs) {
            job.schedule();
        }
        return null;
    }

    protected List<ClearCacheJob> createClearCacheJobs(List<IImport> imports) {
        return createClearCacheJobs(getUrisPerProject(imports));
    }

    protected List<ClearCacheJob> createClearCacheJobs(Map<IProject, List<String>> urisPerProject) {
        List<ClearCacheJob> jobs = Lists.newArrayList();
        for (IProject project : urisPerProject.keySet()) {
            ClearCacheJob job = new ClearCacheJob(project, urisPerProject.get(project));
            job.setUser(true);
            job.setRebuildProject(true);
            jobs.add(job);
        }
        return jobs;
    }

    protected Map<IProject, List<String>> getUrisPerProject(List<IImport> imports) {
        Map<IProject, List<String>> urisPerProject = Maps.newHashMap();

        for (IImport immport : imports) {
            IProject project = immport.getProject();
            if (project == null) {
                continue;
            }

            List<String> uris = urisPerProject.get(project);
            if (uris == null) {
                uris = Lists.newArrayList();
            }

            uris.add(immport.getURI());
            urisPerProject.put(project, uris);
        }
        return urisPerProject;
    }

    public static CommandContributionItem createCommand(IServiceLocator serviceLocator,
            String label, String imageKey) {
        CommandContributionItemParameter commandParam = new CommandContributionItemParameter(
                serviceLocator, "clearCache", ID, SWT.PUSH);

        commandParam.icon = CorePlugin.getDefault().getImageDescriptor(imageKey);
        commandParam.label = label;

        return new CommandContributionItem(commandParam);
    }
}
