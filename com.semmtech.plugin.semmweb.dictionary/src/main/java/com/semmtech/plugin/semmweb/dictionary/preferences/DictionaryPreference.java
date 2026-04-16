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

package com.semmtech.plugin.semmweb.dictionary.preferences;


import org.eclipse.jface.preference.IPreferenceStore;

import com.semmtech.plugin.semmweb.core.CorePlugin;


public class DictionaryPreference {

    private static IPreferenceStore getPreferenceStore() {
        return CorePlugin.getDefault().getPreferenceStore();
    }

    public static boolean autoImport() {
        return getPreferenceStore()
                .getBoolean(DictionaryPreferenceConstants.PREFERENCE_AUTO_IMPORT);
    }

    public static void setAutoImport(boolean autoImport) {
        getPreferenceStore().setValue(DictionaryPreferenceConstants.PREFERENCE_AUTO_IMPORT,
                autoImport);
    }

    public static void setDefaults(IPreferenceStore store) {
        store.setDefault(DictionaryPreferenceConstants.PREFERENCE_AUTO_IMPORT, true);
    }
}
