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

import com.fasterxml.jackson.core.type.TypeReference;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.model.Creator;
import com.semmtech.ui.plugin.preference.BasePreference;


public class CreatorPreference extends BasePreference {
    public static final String PREFERENCE_CREATORS = "com.semmtech.plugin.semmweb.core.preferences.creator.creators";

    private static final CreatorPreference instance = new CreatorPreference();

    private CreatorPreference() {
        super(CorePlugin.getDefault().getPreferenceStore());
    }

    public static List<Creator> getCreators() {
        return instance.getValueUsingJSON(PREFERENCE_CREATORS, new TypeReference<List<Creator>>() {
        }, new ArrayList<Creator>());
    }

    public static void setCreators(List<Creator> creators) {
        instance.storeValueUsingJSON(creators, PREFERENCE_CREATORS);
    }
}
