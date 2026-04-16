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
import com.google.common.collect.Lists;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.jena.ontology.WorkspaceOntologySpec;
import com.semmtech.ui.plugin.preference.BasePreference;


public class PublicationMappingsPreference extends BasePreference {

    public static final String PREFERENCE_PUBLICATION_MAPPINGS = "com.semmtech.plugin.semmweb.core.preferences.publicationMappings";

    private static final PublicationMappingsPreference instance = new PublicationMappingsPreference();

    private PublicationMappingsPreference() {
        super(CorePlugin.getDefault().getPreferenceStore());
    }

    public static final List<WorkspaceOntologySpec> DEFAULT_MAPPINGS = createDefaults();

    private static List<WorkspaceOntologySpec> createDefaults() {
        List<WorkspaceOntologySpec> defaults = Lists.newArrayList();

        defaults.add(WorkspaceOntologySpec.SKOS);
        defaults.add(WorkspaceOntologySpec.FOAF);

        return defaults;
    }

    public static List<WorkspaceOntologySpec> getPublicationMappings() {
        return instance.getValueUsingJSON(PREFERENCE_PUBLICATION_MAPPINGS,
                new TypeReference<List<WorkspaceOntologySpec>>() {
                }, new ArrayList<WorkspaceOntologySpec>());
    }

    public static void setPublicationMappings(List<WorkspaceOntologySpec> mappings) {
        instance.storeValueUsingJSON(mappings, PREFERENCE_PUBLICATION_MAPPINGS);
    }

    public static void setDefaults() {
        instance.setDefaultUsingJSON(PREFERENCE_PUBLICATION_MAPPINGS, DEFAULT_MAPPINGS);
    }
}
