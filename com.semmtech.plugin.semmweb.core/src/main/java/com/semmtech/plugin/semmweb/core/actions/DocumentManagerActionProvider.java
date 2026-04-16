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


import java.util.Collections;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.navigator.ICommonMenuConstants;
import org.eclipse.ui.services.IServiceLocator;

import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.CorePluginImages;
import com.semmtech.plugin.semmweb.core.dialog.DocumentManagerPropertyPage;
import com.semmtech.plugin.semmweb.core.handlers.SetModelsFolderHandler;
import com.semmtech.plugin.semmweb.core.nature.SemanticProject;
import com.semmtech.plugin.semmweb.core.navigator.ISemanticElement;
import com.semmtech.ui.plugin.navigator.CommonViewerActionProvider;
import com.semmtech.ui.plugin.util.Selections;


public class DocumentManagerActionProvider extends CommonViewerActionProvider {

    private static final String DOCUMENT_MANAGER_MENU_NAME = "semmweb.documentManager";//$NON-NLS-1$

    @Override
    public void fillContextMenu(IMenuManager menu) {
        IMenuManager submenu = new MenuManager("Document Manager", DOCUMENT_MANAGER_MENU_NAME);

        Object selected = Selections.retrieveFirst(getSelection());
        IProject project = null;
        IServiceLocator locator = getServiceLocator();
        if (selected instanceof IFolder) {
            project = ((IFolder) selected).getProject();
            ContributionItem item = SetModelsFolderHandler.createCommand(locator,
                    "Use as Models Folder", CorePluginImages.IMG_MODELS_FOLDER_ADD);
            submenu.add(item);
            submenu.add(new Separator());
        }
        else if (selected instanceof IResource) {
            project = ((IResource) selected).getProject();
        }
        // else if (selected instanceof IModelCollection) {
        // project = ((IModelCollection) selected).getProject();
        // ContributionItem item =
        // UnsetModelsFolderHandler.createCommand(window,
        // "Remove as Models Folder",
        // CorePluginImages.IMG_MODELS_FOLDER_REMOVE);
        // submenu.add(item);
        // submenu.add(new Separator());
        // }
        else if (selected instanceof ISemanticElement) {
            project = ((ISemanticElement) selected).getProject();
        }

        if (!SemanticProject.isSemanticProject(project)) {
            return;
        }
        if (project != null) {
            Shell shell = getWindow().getShell();
            Action configureAction = new ConfigureDocumentManagerAction(shell, project);
            submenu.add(configureAction);
        }
        menu.appendToGroup(ICommonMenuConstants.GROUP_PORT, submenu);
        menu.appendToGroup(ICommonMenuConstants.GROUP_PORT, new Separator());
    }

    /**
     * Inner class used to perform the configure document manager
     * 
     * @author Mike Henrichs
     * 
     */
    private static class ConfigureDocumentManagerAction extends Action {
        private static final String ACTION_TEXT = "Configure Document Manager...";
        private static final ImageDescriptor ACTION_IMAGE = CorePlugin.getDefault()
                .getImageDescriptor(CorePluginImages.IMG_DOCUMENT_MANAGER);

        private final IProject project;
        private final Shell shell;

        public ConfigureDocumentManagerAction(Shell shell, IProject project) {
            super(ACTION_TEXT, ACTION_IMAGE);
            this.shell = shell;
            this.project = project;
        }

        @Override
        public void run() {
            if (project != null) {
                String pageId = DocumentManagerPropertyPage.ID;
                PreferenceDialog dialog = PreferencesUtil.createPropertyDialogOn(shell, project,
                        pageId, null, Collections.EMPTY_MAP);
                dialog.open();
            }
        }
    }
}
