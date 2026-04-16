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

package com.semmtech.ui.plugin.navigator;


import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;
import org.eclipse.ui.navigator.ICommonViewerSite;
import org.eclipse.ui.navigator.ICommonViewerWorkbenchSite;
import org.eclipse.ui.services.IServiceLocator;

import com.semmtech.ui.plugin.viewers.EmptySelection;


public class CommonViewerActionProvider extends CommonActionProvider {

    private ISelectionProvider selectionProvider;
    private IWorkbenchWindow window;
    private ICommonViewerSite viewerSite;
    private ICommonViewerWorkbenchSite workbench;

    public CommonViewerActionProvider() {
        super();
    }

    @Override
    public void init(ICommonActionExtensionSite site) {
        viewerSite = site.getViewSite();
        if (viewerSite instanceof ICommonViewerWorkbenchSite) {
            workbench = (ICommonViewerWorkbenchSite) viewerSite;
            selectionProvider = workbench.getSelectionProvider();
            window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        }
    }

    protected ICommonViewerSite getViewerSite() {
        return viewerSite;
    }

    protected ICommonViewerWorkbenchSite getSite() {
        return workbench;
    }

    protected ISelection getSelection() {
        if (selectionProvider != null) {
            return selectionProvider.getSelection();
        }
        return new EmptySelection();
    }

    protected boolean isEmptySelection() {
        return getSelection().isEmpty();
    }

    protected ISelectionProvider getSelectionProvider() {
        return selectionProvider;
    }

    protected IWorkbenchWindow getWindow() {
        return window;
    }

    protected IServiceLocator getServiceLocator() {
        return window;
    }

    protected CommandContributionItem createCommand(String id, String commandId, String label) {
        CommandContributionItemParameter param = new CommandContributionItemParameter(window, id,
                commandId, SWT.PUSH);
        param.label = label;
        return new CommandContributionItem(param);
    }
}
