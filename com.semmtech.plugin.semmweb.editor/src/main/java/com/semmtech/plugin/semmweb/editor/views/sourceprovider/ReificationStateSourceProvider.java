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

package com.semmtech.plugin.semmweb.editor.views.sourceprovider;


import java.util.HashMap;
import java.util.Map;

import org.eclipse.ui.AbstractSourceProvider;
import org.eclipse.ui.ISources;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.services.ISourceProviderService;


/**
 * SourceProvider for the state of reification ToggleButton in the TriplesView
 * 
 * @author Simone Rondelli
 */
public class ReificationStateSourceProvider extends AbstractSourceProvider {

    public static final String REIFICATION_STATE = "com.semmtech.plugin.semmweb.editor.views.sourceprovider.reificationState";

    public static final String ENABLED = "ENABLED";

    public static final String DISABLED = "DISABLED";

    public boolean reificationEnabled;

    public ReificationStateSourceProvider() {
        reificationEnabled = false;
    }

    @Override
    public void dispose() {
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Map getCurrentState() {
        Map<String, String> states = new HashMap<>(1);
        states.put(REIFICATION_STATE, reificationEnabled ? ENABLED : DISABLED);
        return states;
    }

    @Override
    public String[] getProvidedSourceNames() {
        return new String[] { REIFICATION_STATE };
    }

    private void setReificationState(boolean enabled) {
        if (enabled == reificationEnabled) {
            return;
        }

        this.reificationEnabled = enabled;
        fireSourceChanged(ISources.WORKBENCH, REIFICATION_STATE, reificationEnabled ? ENABLED
                : DISABLED);
    }

    public static void setReificationState(IWorkbenchWindow window, boolean enabled) {
        ISourceProviderService sourceProviderService = (ISourceProviderService) window
                .getService(ISourceProviderService.class);
        ReificationStateSourceProvider reificationStateProvider = (ReificationStateSourceProvider) sourceProviderService
                .getSourceProvider(ReificationStateSourceProvider.REIFICATION_STATE);

        reificationStateProvider.setReificationState(enabled);
    }
}
