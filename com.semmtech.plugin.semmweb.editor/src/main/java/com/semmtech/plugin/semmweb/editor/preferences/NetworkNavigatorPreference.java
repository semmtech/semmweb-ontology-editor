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

package com.semmtech.plugin.semmweb.editor.preferences;


import com.semmtech.plugin.semmweb.editor.EditorPlugin;
import com.semmtech.ui.plugin.preference.BasePreference;


public class NetworkNavigatorPreference extends BasePreference {

    public static final String PREFERENCE_LINK_WITH_EDITOR = "com.semmtech.plugin.semmweb.editor.preferences.linkNavigatorWithEditor";

    private static final NetworkNavigatorPreference instance = new NetworkNavigatorPreference();

    protected NetworkNavigatorPreference() {
        super(EditorPlugin.getDefault().getPreferenceStore());
    }

    public static boolean isLinkedWithEditor() {
        return instance.getPreferenceStore().getBoolean(PREFERENCE_LINK_WITH_EDITOR);
    }
}
