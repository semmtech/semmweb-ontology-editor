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


public class OntologyFileEditorPreference extends BasePreference {
    public static final String PREFERENCE_DISABLE_AUTO_EXPLORE = "com.semmtech.plugin.semmweb.core.preferences.disableAutoExplore";

    private static final OntologyFileEditorPreference instance = new OntologyFileEditorPreference();

    private OntologyFileEditorPreference() {
        super(CorePlugin.getDefault().getPreferenceStore());
    }

    public static void setAutoExploreDisabled(boolean disabled) {
        instance.getPreferenceStore().setValue(PREFERENCE_DISABLE_AUTO_EXPLORE, disabled);
    }

    public static boolean autoExploreDisabled() {
        return instance.getPreferenceStore().getBoolean(PREFERENCE_DISABLE_AUTO_EXPLORE);
    }

    public static void setDefaults() {
        instance.getPreferenceStore().setDefault(PREFERENCE_DISABLE_AUTO_EXPLORE, false);
    }
}
