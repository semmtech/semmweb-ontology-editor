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

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.navigator.IImport;
import com.semmtech.plugin.semmweb.core.preferences.DocumentManagerPreference;
import com.semmtech.ui.plugin.util.Selections;


public class EnableImportHandler extends AbstractHandler {
    public static final String ID = "com.semmtech.plugin.semmweb.core.commands.enableImport";

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        ISelection selection = HandlerUtil.getCurrentSelection(event);
        List<IImport> imports = Selections.retrieveAllAsType(selection, IImport.class);
        for (IImport immport : imports) {
            if (immport.isDisabled()) {
                IProject project = immport.getProject();
                DocumentManagerPreference.fromProject(project).setDisabledImport(immport.getURI(),
                        false);
            }
        }
        return null;
    }

    public static CommandContributionItem createCommand(IServiceLocator serviceLocator,
            String label, String imageKey) {
        CommandContributionItemParameter param = new CommandContributionItemParameter(
                serviceLocator, null, ID, SWT.PUSH);
        param.label = label;
        Map<String, String> parameters = Maps.newHashMap();
        param.parameters = parameters;
        if (!Strings.isNullOrEmpty(imageKey)) {
            param.icon = CorePlugin.getDefault().getImageDescriptor(imageKey);
        }
        return new CommandContributionItem(param);
    }

}
