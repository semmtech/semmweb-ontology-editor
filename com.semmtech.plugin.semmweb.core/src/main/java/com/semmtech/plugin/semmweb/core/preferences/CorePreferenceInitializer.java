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


import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import com.semmtech.plugin.semmweb.core.CorePlugin;


/**
 * Class used to initialize default preference values.
 */
public class CorePreferenceInitializer extends AbstractPreferenceInitializer {

    @Override
    public void initializeDefaultPreferences() {
        IPreferenceStore store = CorePlugin.getDefault().getPreferenceStore();

        store.setDefault(JenaFrameworkPreferenceConstants.PREFERENCE_PROCESS_IMPORTS, true);

        OntologyFileEditorPreference.setDefaults();
        LabelsPreference.setDefaults();
        LanguagesPreference.setDefaults();
        // PublicationMappingsPreference.setDefaults();
        JenaPreference.setDefaults();
        SkolemizationPreference.setDefaults();
    }
}
