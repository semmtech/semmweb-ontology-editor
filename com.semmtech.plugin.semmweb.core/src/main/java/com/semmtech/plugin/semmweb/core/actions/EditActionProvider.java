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


import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchCommandConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.DeleteResourceAction;
import org.eclipse.ui.ide.ResourceSelectionUtil;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;
import org.eclipse.ui.navigator.ICommonMenuConstants;
import org.eclipse.ui.navigator.ICommonViewerWorkbenchSite;

import com.semmtech.ui.plugin.util.ClipboardUtils;


public class EditActionProvider extends CommonActionProvider {

    private CopyModelAction copyAction;
    private PasteModelAction pasteAction;
    private DeleteResourceAction deleteAction;

    private IWorkbenchWindow window;

    public EditActionProvider() {
    }

    @Override
    public void init(ICommonActionExtensionSite viewerSite) {
        if (viewerSite.getViewSite() instanceof ICommonViewerWorkbenchSite) {
            ICommonViewerWorkbenchSite workbench = (ICommonViewerWorkbenchSite) viewerSite
                    .getViewSite();
            window = workbench.getWorkbenchWindow();

            copyAction = new CopyModelAction(window);
            pasteAction = new PasteModelAction(window);

            // code 'stole' from class
            // org.eclipse.ui.internal.navigator.resources.actions.EditActionGroup
            ISharedImages images = PlatformUI.getWorkbench().getSharedImages();

            IShellProvider is = new IShellProvider() {

                @Override
                public Shell getShell() {
                    return window.getShell();
                }
            };

            deleteAction = new DeleteResourceAction(is);
            deleteAction.setDisabledImageDescriptor(images
                    .getImageDescriptor(ISharedImages.IMG_TOOL_DELETE_DISABLED));
            deleteAction.setImageDescriptor(images
                    .getImageDescriptor(ISharedImages.IMG_TOOL_DELETE));
            deleteAction.setActionDefinitionId(IWorkbenchCommandConstants.EDIT_DELETE);
        }
    }

    @Override
    public void fillContextMenu(IMenuManager menu) {
        IStructuredSelection selection = (IStructuredSelection) getContext().getSelection();
        boolean anyResourceSelected = !selection.isEmpty()
                && ResourceSelectionUtil.allResourcesAreOfType(selection, IResource.PROJECT
                        | IResource.FOLDER | IResource.FILE);

        // The paste action is enabled only if the Clipboard contains files.
        pasteAction.setEnabled(!ClipboardUtils.getFiles().isEmpty());

        menu.appendToGroup(ICommonMenuConstants.GROUP_EDIT, copyAction);
        menu.appendToGroup(ICommonMenuConstants.GROUP_EDIT, pasteAction);

        if (anyResourceSelected) {
            deleteAction.selectionChanged(selection);
            // menu.insertAfter(pasteAction.getId(), deleteAction);
            menu.appendToGroup(ICommonMenuConstants.GROUP_EDIT, deleteAction);
        }
    }
}
