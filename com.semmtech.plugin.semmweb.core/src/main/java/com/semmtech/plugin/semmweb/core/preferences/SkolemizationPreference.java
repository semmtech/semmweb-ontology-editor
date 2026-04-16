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


import org.eclipse.jface.preference.PreferenceStore;

import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.ui.plugin.preference.BasePreference;


public class SkolemizationPreference extends BasePreference {

    public static final String PREFERENCE_SKOLEMIZATION_ENABLED = "com.semmtech.plugin.semmweb.core.preferences.semmweb.skolemizationEnabled";

    private static final SkolemizationPreference instance = new SkolemizationPreference();

    private SkolemizationPreference() {
        super(CorePlugin.getDefault() != null ? CorePlugin.getDefault().getPreferenceStore()
                : new PreferenceStore());
    }

    public static void setSkolemizationEnabled(boolean enabled) {
        instance.getPreferenceStore().setValue(PREFERENCE_SKOLEMIZATION_ENABLED, enabled);
    }

    public static boolean isSkolemizationEnabled() {
        return instance.getPreferenceStore().getBoolean(PREFERENCE_SKOLEMIZATION_ENABLED);
    }

    public static void setDefaults() {
        instance.getPreferenceStore().setDefault(PREFERENCE_SKOLEMIZATION_ENABLED, false);
    }
}
