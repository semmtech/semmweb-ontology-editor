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


import org.eclipse.jface.action.IMenuManager;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;
import org.eclipse.ui.navigator.ICommonMenuConstants;
import org.eclipse.ui.navigator.ICommonViewerSite;
import org.eclipse.ui.navigator.ICommonViewerWorkbenchSite;

import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.CorePluginImages;


public class ShellActionProvider extends CommonActionProvider {

    private ShowInShellWin32Action showInExplorerAction;

    public ShellActionProvider() {
    }

    @Override
    public void init(ICommonActionExtensionSite site) {
        ICommonViewerSite viewerSite = site.getViewSite();
        if (viewerSite instanceof ICommonViewerWorkbenchSite) {
            ICommonViewerWorkbenchSite workbench = (ICommonViewerWorkbenchSite) viewerSite;
            showInExplorerAction = new ShowInShellWin32Action("Show in Explorer",
                    workbench.getSelectionProvider());
            showInExplorerAction.setImageDescriptor(CorePlugin.getDefault().getImageDescriptor(
                    CorePluginImages.IMG_SHOW_IN_EXPLORER));

        }
    }

    @Override
    public void fillContextMenu(IMenuManager menu) {
        if (showInExplorerAction.isEnabled()) {
            menu.appendToGroup(ICommonMenuConstants.GROUP_ADDITIONS, showInExplorerAction);
        }
    }
}
