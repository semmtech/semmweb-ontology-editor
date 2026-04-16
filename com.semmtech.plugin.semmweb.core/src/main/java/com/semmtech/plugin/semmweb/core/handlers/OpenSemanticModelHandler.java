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

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;
import org.eclipse.ui.services.IServiceLocator;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.commands.Commands;
import com.semmtech.plugin.semmweb.core.navigator.IModel;
import com.semmtech.ui.plugin.util.Selections;


public class OpenSemanticModelHandler extends AbstractHandler {
    public static final String ID = "com.semmtech.plugin.semmweb.core.commands.openSemanticModel";

    public static final String PARAMETER_FILE_PATH = "filePath";

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        ISelection selection = HandlerUtil.getCurrentSelection(event);
        String filePath = event.getParameter(PARAMETER_FILE_PATH);
        IFile file = null;

        if (!Strings.isNullOrEmpty(filePath)) {
            IPath path = new Path(filePath);
            file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
        }
        else if (Selections.hasFirstOfType(selection, IFile.class)) {
            file = Selections.retrieveFirstAsType(selection, IFile.class);
        }

        if (file != null) {
            return CorePlugin.getDefault().openModelEditor(file);
        }
        return null;
    }

    public static void openModel(IModel model) {
        if (model == null) {
            return;
        }

        IResource resource = model.getResource();
        if (resource == null) {
            return;
        }

        IPath filePath = resource.getFullPath();
        if (filePath != null) {
            openModel(filePath.toString());
        }
    }

    public static void openModel(String filePath) {
        if (Strings.isNullOrEmpty(filePath)) {
            return;
        }

        Map<String, String> parameters = Maps.newHashMap();
        parameters.put(OpenSemanticModelHandler.PARAMETER_FILE_PATH, filePath);
        Commands.execute(OpenSemanticModelHandler.ID, parameters);
    }

    public static CommandContributionItem createCommand(IServiceLocator serviceLocator,
            String label, String imageKey, String filePath) {

        CommandContributionItemParameter param = new CommandContributionItemParameter(
                serviceLocator, "openSemanticModel", ID, SWT.PUSH);

        Map<String, String> parameters = Maps.newHashMap();
        parameters.put(OpenSemanticModelHandler.PARAMETER_FILE_PATH, filePath);

        param.label = label;
        param.icon = CorePlugin.getDefault().getImageDescriptor(imageKey);
        param.parameters = parameters;

        return new CommandContributionItem(param);
    }

}
