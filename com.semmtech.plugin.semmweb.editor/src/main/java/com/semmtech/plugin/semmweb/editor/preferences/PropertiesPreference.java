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


public class PropertiesPreference {
    public static IPreferenceStore getPreferenceStore() {
        return EditorPlugin.getDefault().getPreferenceStore();
    }

    // public static List<Resource> getHiddenClassResources(Model model) {
    // if (model == null)
    // return Lists.newArrayList();
    // List<Resource> resources = Lists.newArrayList();
    // for (String uri : getHiddenClassUris()) {
    // resources.add(model.createResource(uri));
    // }
    // return resources;
    // }
    //
    // public static List<String> getHiddenClassUris() {
    // String[] uris =
    // PreferencesUtil.decode(getPreferenceStore().getString(TaxonomyPreferenceConstants.PREFERENCE_HIDDEN_CLASSES),
    // TaxonomyPreferenceConstants.VALUE_PREFERENCE_DELIMITER);
    // return new ArrayList<>(Arrays.asList(uris));
    // }
    //
    // public static void setHiddenClassUris(List<String> uris) {
    // getPreferenceStore().setValue(TaxonomyPreferenceConstants.PREFERENCE_HIDDEN_CLASSES,
    // PreferencesUtil.encode(uris.toArray(),
    // TaxonomyPreferenceConstants.VALUE_PREFERENCE_DELIMITER));
    // }
    //
    // public static boolean showInstances() {
    // return
    // getPreferenceStore().getBoolean(TaxonomyPreferenceConstants.PREFERENCE_SHOW_INSTANCES);
    // }
    //
    // public static void setShowInstances(boolean show) {
    // getPreferenceStore().setValue(TaxonomyPreferenceConstants.PREFERENCE_SHOW_INSTANCES,
    // show);
    // }
    //
    // public static boolean showInstanceCount() {
    // return
    // getPreferenceStore().getBoolean(TaxonomyPreferenceConstants.PREFERENCE_SHOW_INSTANCE_COUNT);
    // }
    //
    // public static void setShowInstanceCount(boolean show) {
    // getPreferenceStore().setValue(TaxonomyPreferenceConstants.PREFERENCE_SHOW_INSTANCE_COUNT,
    // show);
    // }
}
