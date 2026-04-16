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
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.commands.IElementUpdater;
import org.eclipse.ui.menus.UIElement;

import com.semmtech.plugin.semmweb.core.commands.Commands;


public abstract class PreferenceStoreToggleHandler extends AbstractHandler implements
        IElementUpdater {
    protected String commandId;
    protected IPreferenceStore preferenceStore;
    protected String preferenceKey;

    public PreferenceStoreToggleHandler(String commandId, IPreferenceStore preferenceStore,
            String preferenceKey) {
        this.commandId = commandId;
        this.preferenceStore = preferenceStore;
        this.preferenceKey = preferenceKey;
    }

    public PreferenceStoreToggleHandler(IPreferenceStore preferenceStore, String preferenceKey) {
        this.preferenceStore = preferenceStore;
        this.preferenceKey = preferenceKey;
    }

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        boolean newState = !getChecked();
        preferenceStore.setValue(preferenceKey, newState);
        if (commandId != null) {
            Commands.setToggleState(commandId, newState);
        }
        return null;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void updateElement(UIElement element, Map parameters) {
        boolean state = getChecked();
        element.setChecked(state);
        if (commandId != null) {
            Commands.setToggleState(commandId, state);
        }
    }

    public boolean getChecked() {
        boolean checked = preferenceStore.getBoolean(preferenceKey);
        return checked;
    }
}
