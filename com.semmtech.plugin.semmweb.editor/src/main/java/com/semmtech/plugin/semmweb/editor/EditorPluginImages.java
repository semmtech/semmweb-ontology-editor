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

package com.semmtech.plugin.semmweb.editor;


public class EditorPluginImages {

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

    public static final String IMG_HIERARCHICAL = icon("hierarchical.gif");
    public static final String IMG_FLAT = icon("flat.gif");

    public static final String IMG_IMPORT = icon("import.gif");

    public static final String IMG_TAXONOMY_ERROR = icon("taxonomy_error.png");

    public static final String IMG_SELECTION_CHECK_ALL = icon("tab_selection_true.gif");
    public static final String IMG_SELECTION_CHECK_NONE = icon("tab_selection_false.gif");

    public static final String IMG_FIND = icon("find.gif");
    public static final String IMG_FIND_CLEAR = icon("find-clear.gif");

    public static final String IMG_RULE = icon("page.png");
    public static final String IMG_RULE_FUNCTOR = icon("rule_functor.png");
    public static final String IMG_RULE_TRIPLE = icon("rule_triple.png");
    public static final String IMG_HEAD_FORWARD = icon("arrow_right.png");
    public static final String IMG_HEAD_BACKWARD = icon("arrow_left.png");
    public static final String IMG_RULE_FORWARD = icon("rule_forward.png");
    public static final String IMG_RULE_BACKWARD = icon("rule_backward.png");
    public static final String IMG_RULE_DERIVATION = icon("rule_derivation.png");

    public static final String IMG_TRIPLE_ASSERTED = icon("triple.png");
    public static final String IMG_TRIPLE_IMPORTED = icon("triple_imported.png");
    public static final String IMG_TRIPLE_INFERRED = icon("triple_inferred.png");

    public static final String IMG_PROPERTY_INVERSE = icon("inverse_property.png");
    public static final String IMG_PROPERTIES = icon("properties.png");

    public static final String IMG_FILTER_PROPERTY = icon("filter_property.png");

    public static final String IMG_DROP_INTO = icon("staging.png");

}
