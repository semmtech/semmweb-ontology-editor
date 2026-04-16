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

package com.semmtech.ui.plugin.logging;


public class ClassNameToPluginIdMapper {

    public static final String SEMMTECH_ID = "com.semmtech";
    public static final String CRYPTOGRAPHY_ID = "com.semmtech.cryptography";
    public static final String GRAMMARS_ID = "com.semmtech.grammars";
    public static final String JENA_ID = "com.semmtech.jena";
    public static final String LDP_ID = "tech.laces.ldp";
    public static final String LDP_PLUGIN_ID = "com.semmtech.plugin.semmweb.laces.ldp";
    public static final String BRANDING_PLUGIN_ID = "com.semmtech.plugin.semmweb.branding";
    public static final String CORE_PLUGIN_ID = "com.semmtech.plugin.semmweb.core";
    public static final String SEARCH_PLUGIN_ID = "com.semmtech.plugin.semmweb.core.search";
    public static final String DICTIONARY_PLUGIN_ID = "com.semmtech.plugin.semmweb.dictionary";
    public static final String EDITOR_PLUGIN_ID = "com.semmtech.plugin.semmweb.editor";
    public static final String LICENSE_PLUGIN_ID = "com.semmtech.plugin.semmweb.license";
    public static final String PUBLICATION_PLUGIN_ID = "com.semmtech.plugin.semmweb.publication";
    public static final String SPARQL_PLUGIN_ID = "com.semmtech.plugin.semmweb.sparql";
    public static final String SEMANTICS_ID = "com.semmtech.semantics";
    public static final String SEMANTICS_RDFMETA_ID = "com.semmtech.semantics.rdfmeta";
    public static final String UI_PLUGIN_ID = "com.semmtech.ui.plugin";

    public static String retrievePluginId(String className) {

        if (className.startsWith(LDP_ID)) {
            return LDP_ID;
        }
        else if (className.startsWith(CRYPTOGRAPHY_ID)) {
            return CRYPTOGRAPHY_ID;
        }
        else if (className.startsWith(GRAMMARS_ID)) {
            return GRAMMARS_ID;
        }
        else if (className.startsWith(JENA_ID)) {
            return JENA_ID;
        }
        else if (className.startsWith(LDP_PLUGIN_ID)) {
            return LDP_PLUGIN_ID;
        }
        else if (className.startsWith(BRANDING_PLUGIN_ID)) {
            return BRANDING_PLUGIN_ID;
        }
        else if (className.startsWith(CORE_PLUGIN_ID)) {
            return CORE_PLUGIN_ID;
        }
        else if (className.startsWith(SEARCH_PLUGIN_ID)) {
            return SEARCH_PLUGIN_ID;
        }
        else if (className.startsWith(DICTIONARY_PLUGIN_ID)) {
            return DICTIONARY_PLUGIN_ID;
        }
        else if (className.startsWith(EDITOR_PLUGIN_ID)) {
            return EDITOR_PLUGIN_ID;
        }
        else if (className.startsWith(LICENSE_PLUGIN_ID)) {
            return LICENSE_PLUGIN_ID;
        }
        else if (className.startsWith(PUBLICATION_PLUGIN_ID)) {
            return PUBLICATION_PLUGIN_ID;
        }
        else if (className.startsWith(SPARQL_PLUGIN_ID)) {
            return SPARQL_PLUGIN_ID;
        }
        else if (className.startsWith(SEMANTICS_RDFMETA_ID)) {
            return SEMANTICS_RDFMETA_ID;
        }
        else if (className.startsWith(SEMANTICS_ID)) {
            return SEMANTICS_ID;
        }
        else if (className.startsWith(UI_PLUGIN_ID)) {
            return UI_PLUGIN_ID;
        }
        else if (className.startsWith(SEMMTECH_ID)) {
            return SEMMTECH_ID; // must be the last check
        }

        return className;
    }

}
