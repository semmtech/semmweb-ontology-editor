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

import org.apache.log4j.Logger;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;
import org.eclipse.ui.services.IServiceLocator;

import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.cache.CacheModelJob;
import com.semmtech.plugin.semmweb.core.cache.ClearCacheJob;
import com.semmtech.plugin.semmweb.core.navigator.IImport;
import com.semmtech.ui.plugin.util.Selections;


/**
 * Refresh the cache deleting the old file and downloading a new one.
 * 
 * @author Simone Rondelli
 */
public class RefreshCacheHandler extends ClearCacheHandler {

    private static Logger logger = Logger.getLogger(RefreshCacheHandler.class);

    public static final String ID = "com.semmtech.plugin.semmweb.core.commands.refreshCache";

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        ISelection selection = HandlerUtil.getCurrentSelection(event);
        List<IImport> imports = Selections.retrieveAllAsType(selection, IImport.class);
        Map<IProject, List<String>> urisPerProject = getUrisPerProject(imports);

        List<ClearCacheJob> clearJobs = createClearCacheJobs(urisPerProject);
        for (ClearCacheJob job : clearJobs) {
            job.setRebuildProject(false);
            job.schedule();
        }

        for (ClearCacheJob job : clearJobs) {
            try {
                job.join();
            }
            catch (InterruptedException e) {
                logger.error(e.getMessage(), e);
            }
        }

        for (IProject project : urisPerProject.keySet()) {
            for (String uri : urisPerProject.get(project)) {
                CacheModelJob job = new CacheModelJob(project, uri, false,
                        HandlerUtil.getActiveShell(event));
                job.setUser(true);
                job.setRebuildProject(true);
                job.schedule();
            }
        }

        return null;
    }

    public static CommandContributionItem createCommand(IServiceLocator serviceLocator,
            String label, String imageKey) {
        CommandContributionItemParameter commandParam = new CommandContributionItemParameter(
                serviceLocator, "refreshCache", ID, SWT.PUSH);

        commandParam.icon = CorePlugin.getDefault().getImageDescriptor(imageKey);
        commandParam.label = label;

        return new CommandContributionItem(commandParam);
    }

}
