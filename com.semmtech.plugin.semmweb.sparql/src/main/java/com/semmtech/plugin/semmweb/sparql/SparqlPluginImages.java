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

package com.semmtech.plugin.semmweb.sparql;


public class SparqlPluginImages {
    public static final String ICON_PATH = "icons/";
    public static final String COMMANDS_PATH = "icons/commands";

    /**
     * Returns the correct path to the given icon filename. This allows for
     * different folders of icons to be switched.
     * 
     * @param filename
     *            Filename of the icon for which the path should be retrieved
     * @return Returns the path of the given icon filename, relative to the
     *         plug-in.
     */
    private static String icon(String filename) {
        return ICON_PATH + filename;
    }

    private static String commands(String filename) {
        return COMMANDS_PATH + filename;
    }

    public static final String IMG_SPARQL_ICON = icon("sparql.png");
    public static final String IMG_SPARQL_FILE_ADD = icon("sparql_file-new_add.png");
    public static final String IMG_OUTPUT = icon("xslt_output.gif");

    public static final String IMG_EXECUTE_SPARQL = commands("execute_sparql.png");
    public static final String IMG_CONTEXT = icon("configure.gif");
}
