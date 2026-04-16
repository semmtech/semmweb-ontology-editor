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

package com.semmtech.plugin.semmweb.dictionary;


/**
 * Constant class standardizing the location of various icons used throughout
 * the interface.
 * 
 * @author Mike Henrichs
 * 
 */
public class DictionaryPluginImages {
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

    public static final String IMG_ADD = icon("add.gif");
    public static final String IMG_DELETE = icon("delete.gif");

    public static final String IMG_ARROW_DOWN = icon("arrow_down.gif");
    public static final String IMG_ARROW_DOWN_WO_TAIL = icon("arrow_down_without_tail.gif");

    public static final String IMG_TAG_BLUE = icon("tag_blue.png");
    public static final String IMG_TAG_BLUE_ADD = icon("tag_blue_add.png");
    public static final String IMG_TAG_BLUE_DELETE = icon("tag_blue_delete.png");

    public static final String IMG_FLAT = icon("flat.gif");
    public static final String IMG_HIERARCHICAL = icon("hierarchical.gif");

    public static final String IMG_CONCEPT_SCHEME = icon("conceptScheme.png");
    public static final String IMG_CONCEPT = icon("concept.png");
    public static final String IMG_TOP_CONCEPT = icon("topConcept.png");
    public static final String IMG_COLLECTION = icon("collection.gif");

}
