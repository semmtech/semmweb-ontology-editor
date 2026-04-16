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

package com.semmtech.plugin.semmweb.core.model;


import com.semmtech.plugin.semmweb.core.CorePluginImages;


/**
 * Needs to be refactored, or placed within a different package.
 * 
 * @author Mike Henrichs
 * 
 */
public class DisplayLanguage {
    public static final DisplayLanguage LANGUAGE_NONE = new DisplayLanguage(null, "None",
            CorePluginImages.IMG_XSD);
    public static final DisplayLanguage LANGUAGE_NEDERLANDS = new DisplayLanguage("nl",
            "Nederlands", CorePluginImages.IMG_FLAG_NL);
    public static final DisplayLanguage LANGUAGE_ENGLISH = new DisplayLanguage("en", "English",
            CorePluginImages.IMG_FLAG_GB);

    private String code;
    private String name;
    private String imageKey;

    public DisplayLanguage() {
    }

    public DisplayLanguage(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public DisplayLanguage(String code, String name, String imageKey) {
        this.code = code;
        this.name = name;
        this.imageKey = imageKey;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageKey() {
        return imageKey;
    }

    public void setImageKey(String key) {
        this.imageKey = key;
    }

    @Override
    public String toString() {
        return String.format("%s (= %s)", name, code);
    }
}
