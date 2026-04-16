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


import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

import com.google.common.base.Strings;
import com.semmtech.ui.plugin.preference.BasePreference;


/**
 * 
 * @author Mike Henrichs
 */
public final class ModelsFolderPreference extends BasePreference {
    private static final String PREFERENCE_QUALIFIER = "modelsFolder.project";

    public static final String PREFERENCE_MODELS_FOLDER_PATH = "com.semmtech.plugin.semmweb.core.preferences.modelsFolderPath";

    protected ModelsFolderPreference(IPreferenceStore store) {
        super(store);
        setDefaults();
    }

    public void setDefaults() {
        getPreferenceStore().setDefault(PREFERENCE_MODELS_FOLDER_PATH, "models/");
    }

    public void restoreToDefaults() {
        getPreferenceStore().setToDefault(PREFERENCE_MODELS_FOLDER_PATH);
    }

    /**
     * @return The models folder name eg: models/
     */
    public String getModelsFolderPath() {
        String path = getPreferenceStore().getString(PREFERENCE_MODELS_FOLDER_PATH);
        if (!Strings.isNullOrEmpty(path) && !path.endsWith("/")) {
            path += "/";
        }
        return path;
    }

    public void setModelsFolderPath(String path) {
        getPreferenceStore().setValue(PREFERENCE_MODELS_FOLDER_PATH, path);
    }

    /**
     * Returns the DocumentManagerPreference for the given project; or if the
     * project is null based on the CorePlugin preference store.
     * 
     * @param project
     * @return
     */
    public static ModelsFolderPreference fromProject(IProject project) {
        if (project != null) {
            ProjectScope scope = new ProjectScope(project);
            IPreferenceStore store = new ScopedPreferenceStore(scope, PREFERENCE_QUALIFIER);
            return new ModelsFolderPreference(store);
        }
        return null;
    }

}
