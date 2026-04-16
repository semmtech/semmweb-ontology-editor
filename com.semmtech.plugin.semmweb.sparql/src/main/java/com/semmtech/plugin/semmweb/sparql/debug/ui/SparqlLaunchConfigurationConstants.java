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

package com.semmtech.plugin.semmweb.sparql.debug.ui;


public interface SparqlLaunchConfigurationConstants {
    public static final String ID_SPARQL_QUERY = "com.semmtech.plugin.semmweb.sparql.sparqlLaunchConfiguration";

    // Main
    public static final String ATTR_QUERY_FILE = "com.semmtech.plugin.semmweb.sparql.debug.queryFile";

    public static final String ATTR_USE_EXTERNAL_MODEL = "com.semmtech.plugin.semmweb.sparql.debug.ATTR_USE_EXTERNAL_MODEL";
    public static final String ATTR_USE_EMPTY_MODEL = "useEmptyModel";
    public static final String ATTR_INPUT_MODEL_FILE = "com.semmtech.plugin.semmweb.sparql.debug.ATTR_INPUT_MODEL_FILE";
    public static final String ATTR_INPUT_MODEL_URL = "com.semmtech.plugin.semmweb.sparql.debug.ATTR_INPUT_MODEL_URL";
    public static final String ATTR_SPARQL_END_POINT = "com.semmtech.plugin.semmweb.sparql.debug.ATTR_SPARQL_END_POINT";

    // Output
    public static final String ATTR_OUTPUT_FOLDER = "com.semmtech.plugin.semmweb.sparql.debug.ATTR_OUTPUT_FOLDER";
    public static final String ATTR_OUTPUT_FILENAME = "com.semmtech.plugin.semmweb.sparql.debug.ATTR_OUTPUT_FILENAME";
    public static final String ATTR_OUTPUT_SYNTAX = "com.semmtech.plugin.semmweb.sparql.debug.ATTR_OUTPUT_SYNTAX";

    public static final String ATTR_USE_DEFAULT_OUTPUT_FILE = "com.semmtech.plugin.semmweb.sparql.debug.ATTR_USE_DEFAULT_OUTPUT_FILE";

    public static final String ATTR_OPEN_FILE = "com.semmtech.plugin.semmweb.sparql.debug.ATTR_OPEN_FILE";

    public static final String ATTR_CUSTOM_CONTEXT = "customContext";
    public static final String ATTR_AUTH_USER = "queryAuthUser";
    public static final String ATTR_AUTH_PASSWORD = "queryAuthPwd";

    public static final String CSV_EXTENSION = ".csv";
    public static final String TXT_EXTENSION = ".txt";
    public static final String SSE_EXTENSION = ".sse";
    public static final String TTL_EXTENSION = ".ttl";
    public static final String RDF_EXTENSION = ".rdf";
    public static final String OWL_EXTENSION = ".owl";
    public static final String N3_EXTENSION = ".n3";
    public static final String NT_EXTENSION = ".nt";
    public static final String XML_EXTENSION = ".xml";
    public static final String TSV_EXTENSION = ".tsv";
    public static final String JSON_EXTENSION = ".json";

}
