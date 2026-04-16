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

package com.semmtech.plugin.semmweb.core.actions;


import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.SWT;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;
import org.eclipse.ui.navigator.ICommonMenuConstants;
import org.eclipse.ui.services.IServiceLocator;

import com.semmtech.plugin.semmweb.core.handlers.ToggleSemanticProjectHandler;
import com.semmtech.plugin.semmweb.core.nature.SemanticProject;
import com.semmtech.ui.plugin.navigator.CommonViewerActionProvider;
import com.semmtech.ui.plugin.util.Selections;


public class CoreActionProvider extends CommonViewerActionProvider {

    private static Logger logger = Logger.getLogger(CoreActionProvider.class);

    @Override
    public void fillContextMenu(IMenuManager menu) {
        List<IProject> projects = Selections.retrieveAllAsType(getSelection(), IProject.class);

        if (projects.size() != 1) {
            return;
        }

        IProject project = projects.get(0);

        try {
            if (!project.isOpen() || project.getNature(SemanticProject.NATURE_ID) != null) {
                return;
            }

            IServiceLocator locator = getServiceLocator();
            IMenuManager submenu = new MenuManager("Configure", "semmweb.configureProject");

            CommandContributionItemParameter commitParam = new CommandContributionItemParameter(
                    locator, "toggleSemanticNature", ToggleSemanticProjectHandler.ID, SWT.PUSH);
            commitParam.label = "Toggle Semantic Project";
            submenu.add(new CommandContributionItem(commitParam));

            menu.appendToGroup(ICommonMenuConstants.GROUP_ADDITIONS, submenu);
        }
        catch (CoreException e) {
            logger.error("Error occurred while getting project nature: " + project, e);
        }
    }
}