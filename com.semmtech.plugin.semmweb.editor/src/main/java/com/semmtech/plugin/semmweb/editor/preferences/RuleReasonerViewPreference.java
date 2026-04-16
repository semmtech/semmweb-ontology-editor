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


import org.eclipse.jface.preference.IPreferenceStore;

import com.semmtech.plugin.semmweb.editor.EditorPlugin;


public class RuleReasonerViewPreference {
    public static IPreferenceStore getPreferenceStore() {
        return EditorPlugin.getDefault().getPreferenceStore();
    }

    public static boolean showBaseModel() {
        return getPreferenceStore().getBoolean(
                RuleReasonerViewPreferenceConstants.PREFERENCE_SHOW_BASE_MODEL);
    }

    public static void setShowBaseModel(boolean show) {
        getPreferenceStore().setValue(
                RuleReasonerViewPreferenceConstants.PREFERENCE_SHOW_BASE_MODEL, show);
    }

    public static boolean showImportedModel() {
        return getPreferenceStore().getBoolean(
                RuleReasonerViewPreferenceConstants.PREFERENCE_SHOW_IMPORTED_MODEL);
    }

    public static void setShowImportedModel(boolean show) {
        getPreferenceStore().setValue(
                RuleReasonerViewPreferenceConstants.PREFERENCE_SHOW_IMPORTED_MODEL, show);
    }

    public static boolean getTraceOn() {
        return getPreferenceStore().getBoolean(
                RuleReasonerViewPreferenceConstants.PREFERENCE_REASONER_TRACE_ON);
    }

    public static boolean getDerivationLogging() {
        return getPreferenceStore().getBoolean(
                RuleReasonerViewPreferenceConstants.PREFERENCE_REASONER_DERIVATION_LOGGING);
    }
}
