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


import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;
import org.eclipse.ui.navigator.ICommonMenuConstants;
import org.eclipse.ui.navigator.ICommonViewerWorkbenchSite;

import com.semmtech.plugin.semmweb.core.CorePluginImages;
import com.semmtech.plugin.semmweb.core.commands.EnabledCommandContributionItem;
import com.semmtech.plugin.semmweb.core.handlers.PasteProjectHandler;


/**
 * Provides the action for the context menu of the white area in the Navigator
 * 
 * @author Simone Rondelli
 */
public class EmptySelectionActionProvider extends CommonActionProvider {

    private IWorkbenchWindow window;
    private Action preferencesAction;

    @Override
    public void init(ICommonActionExtensionSite aSite) {
        if (aSite.getViewSite() instanceof ICommonViewerWorkbenchSite) {
            window = ((ICommonViewerWorkbenchSite) aSite.getViewSite()).getWorkbenchWindow();

            preferencesAction = new Action("Preferences") {
                @Override
                public void run() {
                    PreferenceDialog dialog = PreferencesUtil.createPreferenceDialogOn(
                            window.getShell(), null, null, null);
                    dialog.open();
                }
            };
        }
    }

    @Override
    public void fillContextMenu(IMenuManager menu) {
        EnabledCommandContributionItem pasteProjectCommand = PasteProjectHandler.createCommand(
                window, "Paste Project", CorePluginImages.IMG_PASTE);
        pasteProjectCommand.setEnabled(PasteProjectHandler.commandEnabled());
        menu.insertAfter(ICommonMenuConstants.GROUP_EDIT, pasteProjectCommand);

        menu.insertAfter(ICommonMenuConstants.GROUP_PROPERTIES, preferencesAction);
    }
}
