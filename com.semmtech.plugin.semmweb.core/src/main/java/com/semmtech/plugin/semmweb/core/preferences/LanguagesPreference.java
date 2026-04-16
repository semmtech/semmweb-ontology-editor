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


import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.preference.PreferenceStore;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Lists;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.CorePluginImages;
import com.semmtech.plugin.semmweb.core.model.DisplayLanguage;
import com.semmtech.ui.plugin.preference.BasePreference;


public class LanguagesPreference extends BasePreference {
    public static final String PREFERENCE_DISPLAY_LANGUAGES = "com.semmtech.plugin.semmweb.core.preferences.displayLanguages";

    public static final List<DisplayLanguage> DEFAULT_LANGUAGES = createDefaults();

    private static final LanguagesPreference instance = new LanguagesPreference();

    private LanguagesPreference() {
        super(CorePlugin.getDefault() != null ? CorePlugin.getDefault().getPreferenceStore()
                : new PreferenceStore());
    }

    private static List<DisplayLanguage> createDefaults() {
        List<DisplayLanguage> defaults = Lists.newArrayList();

        defaults.add(new DisplayLanguage("", "Empty", CorePluginImages.IMG_XSD));
        defaults.add(new DisplayLanguage("en", "English", CorePluginImages.IMG_FLAG_GB));
        defaults.add(new DisplayLanguage("nl", "Nederlands", CorePluginImages.IMG_FLAG_NL));
        defaults.add(new DisplayLanguage("de", "Deutsch", CorePluginImages.IMG_FLAG_DE));
        defaults.add(new DisplayLanguage("fr", "Francais", CorePluginImages.IMG_FLAG_FR));
        defaults.add(new DisplayLanguage("pt", "Portuguese", CorePluginImages.IMG_FLAG_PT));

        return defaults;
    }

    public static List<DisplayLanguage> getDisplayLanguages() {
        return instance.getValueUsingJSON(PREFERENCE_DISPLAY_LANGUAGES,
                new TypeReference<List<DisplayLanguage>>() {
                }, new ArrayList<DisplayLanguage>());
    }

    public static void setDisplayLanguages(List<DisplayLanguage> languages) {
        instance.storeValueUsingJSON(languages, PREFERENCE_DISPLAY_LANGUAGES);
    }

    public static void setDefaults() {
        instance.setDefaultUsingJSON(PREFERENCE_DISPLAY_LANGUAGES, DEFAULT_LANGUAGES);
    }
}
