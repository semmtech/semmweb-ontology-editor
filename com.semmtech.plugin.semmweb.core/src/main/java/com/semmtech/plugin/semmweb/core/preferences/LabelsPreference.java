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


public class LabelsPreference extends BasePreference {
    public static final String PREFERENCE_RESOURCE_LABEL_RENDERING = "com.semmtech.plugin.semmweb.core.preferences.semmweb.resourceLabelRendering";
    public static final String PREFERENCE_ALWAYS_SHOW_ONTOLOGY_URI = "com.semmtech.plugin.semmweb.core.preferences.semmweb.alwaysShowOntologyURI";
    public static final String VALUE_SHOW_RESOURCE_QNAMES = "valueShowResourceQnames";
    public static final String VALUE_SHOW_READABLE_LABELS = "valueShowReadableLabels";

    private static final LabelsPreference instance = new LabelsPreference();

    private LabelsPreference() {
        super(CorePlugin.getDefault() != null ? CorePlugin.getDefault().getPreferenceStore()
                : new PreferenceStore());
    }

    public static void setResourceLabelRendering(String value) {
        instance.getPreferenceStore().setValue(PREFERENCE_RESOURCE_LABEL_RENDERING, value);
    }

    public static boolean showResourceQNames() {
        String preference = instance.getPreferenceStore().getString(
                PREFERENCE_RESOURCE_LABEL_RENDERING);
        return preference.equals(VALUE_SHOW_RESOURCE_QNAMES);
    }

    public static boolean showReadableLabels() {
        String preference = instance.getPreferenceStore().getString(
                PREFERENCE_RESOURCE_LABEL_RENDERING);
        return preference.equals(VALUE_SHOW_READABLE_LABELS);
    }

    public static void setAlwaysShowOntologyUri(boolean show) {
        instance.getPreferenceStore().setValue(PREFERENCE_ALWAYS_SHOW_ONTOLOGY_URI, show);
    }

    public static boolean alwaysShowOntologyUri() {
        return instance.getPreferenceStore().getBoolean(PREFERENCE_ALWAYS_SHOW_ONTOLOGY_URI);
    }

    public static void setDefaults() {
        instance.getPreferenceStore().setDefault(PREFERENCE_RESOURCE_LABEL_RENDERING,
                VALUE_SHOW_RESOURCE_QNAMES);
        instance.getPreferenceStore().setDefault(PREFERENCE_ALWAYS_SHOW_ONTOLOGY_URI, false);
    }
}
