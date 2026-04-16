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

package com.semmtech.plugin.semmweb.core.preferences;


import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.ui.plugin.preference.BasePreference;


public class EditorPreference extends BasePreference {

    public static final String PREFERENCE_HAS_RUN_BEFORE = "com.semmtech.plugin.semmweb.core.preferences.editor.hasrunbefore";

    private static final EditorPreference instance = new EditorPreference();

    private EditorPreference() {
        super(CorePlugin.getDefault().getPreferenceStore());
    }

    public static boolean hasRunBefore() {

        return instance.getPreferenceStore().getBoolean(PREFERENCE_HAS_RUN_BEFORE);
    }

    public static void setHasRunBefore(Boolean yes) {
        instance.getPreferenceStore().setValue(PREFERENCE_HAS_RUN_BEFORE, yes);
    }

}
