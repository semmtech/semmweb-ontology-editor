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

package com.semmtech.plugin.semmweb.core.commands.sourceprovider;


import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.services.ISourceProviderService;

import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.ui.PartAdapter;


public class CoreCommandStateUpdater extends PartAdapter {

    public CoreCommandStateUpdater() {
        super();
    }

    @Override
    public void partActivated(IWorkbenchPart part) {
        refreshModelProviderState();
    }

    @Override
    public void partDeactivated(IWorkbenchPart part) {
        refreshModelProviderState();
    }

    @Override
    public void partClosed(IWorkbenchPart part) {
        refreshModelProviderState();
    }

    @SuppressWarnings("static-method")
    private void refreshModelProviderState() {
        IWorkbenchWindow window = CorePlugin.getActiveWorkbenchWindow();
        ISourceProviderService sourceProviderService = (ISourceProviderService) window
                .getService(ISourceProviderService.class);
        CoreCommandState commandStateService = (CoreCommandState) sourceProviderService
                .getSourceProvider(CoreCommandState.MODEL_PROVIDER_ACTIVE_STATE);

        // ICommandService commandService = (ICommandService)
        // window.getService(ICommandService.class);
        // Command saveCommand =
        // commandService.getCommand("org.eclipse.ui.commands.save");
        // saveCommand.setEnabled(true);
        // commandService.refreshElements("org.eclipse.ui.commands.save", null);

        commandStateService.updateState();
    }
}
