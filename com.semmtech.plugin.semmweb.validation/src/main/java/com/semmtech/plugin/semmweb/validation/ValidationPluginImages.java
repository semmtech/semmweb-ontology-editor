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

package com.semmtech.plugin.semmweb.validation;


/**
 * 
 * @author Sander Stolk
 */
public class ValidationPluginImages {
    public static final String ICON_PATH = "icons/";

    private static String icon(String filename) {
        return ICON_PATH + filename;
    }

    public static final String IMG_VALIDATE = icon("validate.png");

    public static final String IMG_VALIDATE_RDFS_CLASS = icon("class_validate.png");
    public static final String IMG_VALIDATE_RDFS_CLASS_HIERARCHY = icon("class_hierarchy_validate.png");

    public static final String IMG_VALIDATE_OWL_CLASS = icon("class_of_individuals_validate.png");
    public static final String IMG_VALIDATE_OWL_CLASS_HIERARCHY = icon("class_of_individuals_hierarchy_validate.png");

    public static final String IMG_VALIDATE_INSTANCES = icon("individuals_validate.png");
    public static final String IMG_VALIDATE_INDIVIDUALS = icon("individuals_validate.png");
}
