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

package com.semmtech.plugin.semmweb.laces.ldp;


public class LDPPluginImages {
    /**
     * Relative location of folder containing icons.
     */
    public static final String ICON_PATH = "icons/";

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

    public static final String IMG_LDP_SERVER = icon("artifact_repo_obj.gif");
    public static final String IMG_LDP_GROUP = icon("group.gif");
    public static final String IMG_LDP_REPOSITORY = icon("artifact_repo_obj.gif");
    public static final String IMG_LDP_PUBLICATION = icon("ontology.png");
}
