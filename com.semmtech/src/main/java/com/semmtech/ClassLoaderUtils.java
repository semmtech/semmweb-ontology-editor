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

package com.semmtech;


import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.apache.commons.lang3.StringUtils;


/**
 * 
 * @author Mike Henrichs
 */
public final class ClassLoaderUtils {
    private static String defaultBuildpath = "bin";

    /**
     * Will change the default buildpath for this environment.
     * 
     * @param buildpath
     */
    public static void setDefaultBuildpath(String buildpath) {
        defaultBuildpath = buildpath;
    }

    private ClassLoaderUtils() {

    }

    /**
     * This method returns the path relative to the root of a plugin project.
     * 
     * @param clazz
     *            the class which is part of the project, this class is used to
     *            determine the location of the package folder
     * @param buildFolder
     *            the output folder used, e.g. <code>bin</code>
     * @param relativePath
     *            path relative to the project's root
     * @return the absolute path
     */
    @SuppressWarnings("rawtypes")
    public static String getPluginResource(Class clazz, String buildFolder, String relativePath) {
        String packagePath = clazz.getPackage().getName().replace(".", "/");
        String classPath = clazz.getResource("").toString();
        try {
            classPath = URLDecoder.decode(classPath, "UTF-8");
        }
        catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
        }
        classPath = StringUtils.removeEnd(classPath, packagePath + "/");
        classPath = StringUtils.removeStart(classPath, "file:/");
        classPath = StringUtils.removeEnd(classPath, buildFolder + "/");

        return String.format("%s%s", classPath, relativePath);
    }

    /**
     * This will use "bin" as the build folder
     * 
     * @param clazz
     * @param relativePath
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static String getPluginResource(Class clazz, String relativePath) {
        return getPluginResource(clazz, defaultBuildpath, relativePath);
    }
}
