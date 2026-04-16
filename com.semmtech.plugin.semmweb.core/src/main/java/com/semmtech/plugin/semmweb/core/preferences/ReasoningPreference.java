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


import org.eclipse.jface.preference.IPreferenceStore;

import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.reasoner.RuleConfiguration;


public class ReasoningPreference {
    public static IPreferenceStore getPreferenceStore() {
        return CorePlugin.getDefault().getPreferenceStore();
    }

    public static boolean useInferred() {
        return getPreferenceStore()
                .getBoolean(ReasoningPreferenceConstants.PREFERENCE_USE_INFERRED);
    }

    public static boolean executeOnIntermediate() {
        return getPreferenceStore().getBoolean(
                ReasoningPreferenceConstants.PREFERENCE_EXECUTE_ON_INTERMEDIATE);
    }

    public static void setExecuteOnIntermediate(boolean execute) {
        getPreferenceStore().setValue(
                ReasoningPreferenceConstants.PREFERENCE_EXECUTE_ON_INTERMEDIATE, execute);
    }

    public static RuleConfiguration getRuleConfiguration() {
        String value = getPreferenceStore().getString(
                ReasoningPreferenceConstants.PREFERENCE_RULE_CONFIGURATION);
        if (value == null || value.length() == 0)
            return new RuleConfiguration();

        String[] urls = PreferencesUtil.decode(value,
                ReasoningPreferenceConstants.VALUE_PREFERENCE_DELIMITER);
        RuleConfiguration configuration = new RuleConfiguration(urls);
        return configuration;
    }

    public static void setRuleConfiguration(RuleConfiguration configuration) {
        String value = PreferencesUtil.encode(configuration.getRuleURLs().toArray(),
                ReasoningPreferenceConstants.VALUE_PREFERENCE_DELIMITER);
        getPreferenceStore().setValue(ReasoningPreferenceConstants.PREFERENCE_RULE_CONFIGURATION,
                value);
    }
}
