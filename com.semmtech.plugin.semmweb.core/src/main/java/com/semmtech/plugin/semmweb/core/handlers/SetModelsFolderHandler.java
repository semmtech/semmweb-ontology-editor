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


import java.io.IOException;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;
import org.eclipse.ui.services.IServiceLocator;

import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.preferences.ModelsFolderPreference;
import com.semmtech.ui.plugin.util.Selections;


public class SetModelsFolderHandler extends AbstractHandler {

    private static Logger logger = Logger.getLogger(SetModelsFolderHandler.class);

    public static final String ID = "com.semmtech.plugin.semmweb.core.commands.addModelsFolder";

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        ISelection selection = HandlerUtil.getCurrentSelection(event);
        IFolder selected = Selections.retrieveFirstAsType(selection, IFolder.class);
        if (selected != null) {
            IProject project = selected.getProject();
            String path = selected.getProjectRelativePath().toString();
            if (project != null) {
                ModelsFolderPreference preference = ModelsFolderPreference.fromProject(project);
                preference.setModelsFolderPath(path);
                try {
                    preference.save();
                    project.build(IncrementalProjectBuilder.CLEAN_BUILD, null);
                }
                catch (CoreException ex) {
                    logger.error("Error attempting to rebuild project!", ex);
                }
                catch (IOException ex) {
                }
            }
        }
        return null;
    }

    public static CommandContributionItem createCommand(IServiceLocator serviceLocator,
            String label, String imageKey) {
        CommandContributionItemParameter param = new CommandContributionItemParameter(
                serviceLocator, null, ID, SWT.PUSH);
        param.label = label;
        param.icon = CorePlugin.getDefault().getImageDescriptor(imageKey);
        return new CommandContributionItem(param);
    }
}
