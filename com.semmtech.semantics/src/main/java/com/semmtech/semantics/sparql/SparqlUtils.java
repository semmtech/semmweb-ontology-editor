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

package com.semmtech.semantics.sparql;


import java.util.Map;

import com.google.common.collect.ImmutableMap;


public class SparqlUtils {

    /**
     * See http://www.w3.org/TR/rdf-sparql-query/#grammarEscapes
     * 
     * @param name
     * @return
     */
    //@formatter:off
    private static final Map<String, String> SPARQL_ESCAPE_SEARCH_REPLACEMENTS = 
            ImmutableMap.<String, String> builder()
                .put("\t", "\\t")
                .put("\n", "\\n")
                .put("\r", "\\r")
                .put("\b", "\\b")
                .put("\f", "\\f")
                .put("\"", "\\\"")
                .put("'", "\\'")
                .put("\\", "\\\\")
            .build();
    //@formatter:on

    /**
     * Escapes the string to avoid conflicts in a SPARQL query.
     * 
     * @param string
     *            The string the we want to escape NOT the entire SPARQL query
     * @return Escaped string
     */
    public static String escape(String string) {
        StringBuffer bufOutput = new StringBuffer(string);
        for (int i = 0; i < bufOutput.length(); i++) {
            String replacement = SPARQL_ESCAPE_SEARCH_REPLACEMENTS.get("" + bufOutput.charAt(i));
            if (replacement != null) {
                bufOutput.deleteCharAt(i);
                bufOutput.insert(i, replacement);
                // advance past the replacement
                i += (replacement.length() - 1);
            }
        }
        return bufOutput.toString();
    }
}