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


import java.io.File;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ModelMaker;


public final class FileModelMakerManager {
    private final Logger logger = Logger.getLogger(FileModelMakerManager.class);
    private final Map<String, ModelMaker> modelMakers = Maps.newHashMap();

    private static final String DEFAULT_QUALIFIER = ".maker";

    private static FileModelMakerManager instance;

    private FileModelMakerManager() {

    }

    public ModelMaker getModelMaker(IProject project) {
        return getModelMaker(project, DEFAULT_QUALIFIER);
    }

    public ModelMaker getModelMaker(IProject project, String qualifier) {
        String root = String.format("%s/", ResourcesPlugin.getWorkspace().getRoot().getLocation()
                .toString());
        if (project != null) {
            root = String.format("%s/", project.getLocation().toString());
        }
        if (!Strings.isNullOrEmpty(qualifier)) {
            root = String.format("%s%s/", root, qualifier);
        }
        return getModelMaker(root);
    }

    public ModelMaker getModelMaker(IFolder folder) {
        return getModelMaker(folder.getLocation().toOSString());
    }

    private ModelMaker getModelMaker(String root) {
        if (!modelMakers.containsKey(root)) {
            File folder = new File(root);
            if (!folder.exists()) {
                folder.mkdir();
            }
            logger.trace(String.format("Creating ModelMaker in root \"%s\"", root));
            ModelMaker maker = ModelFactory.createFileModelMaker(root);
            modelMakers.put(root, maker);
            return maker;
        }
        return modelMakers.get(root);
    }

    /**
     * Recreates all ModelMakers
     */
    public void resetManagers() {
        for (String path : modelMakers.keySet()) {
            ModelMaker maker = ModelFactory.createFileModelMaker(path);
            modelMakers.put(path, maker);
        }
    }

    public static FileModelMakerManager getInstance() {
        if (instance == null) {
            instance = new FileModelMakerManager();
        }
        return instance;
    }
}
