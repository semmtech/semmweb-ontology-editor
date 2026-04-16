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

package com.semmtech.plugin.semmweb.core.util;


import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.swt.graphics.Image;

import com.google.common.base.Strings;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.CorePluginImages;
import com.semmtech.plugin.semmweb.core.cache.CacheManager;
import com.semmtech.plugin.semmweb.core.internal.navigator.SemanticProjectManager;
import com.semmtech.plugin.semmweb.core.navigator.ISemanticProject;
import com.semmtech.plugin.semmweb.core.navigator.ImportType;
import com.semmtech.plugin.semmweb.core.preferences.DocumentManagerPreference;


/**
 * Utilities to manage the alternate URL
 * 
 * @author Simone Rondelli
 */
// TODO should be moved inside the the SemanticProject
public class ImportURLUtils {

    /**
     * Retrieve a nice formatted alt url (if exists) for the given public url
     * 
     * @see ImportURLUtils#getAltUrlText(ImportType, String)
     */
    public static String getAltUrlText(IProject project, String publicUri, boolean uriIfAbsent) {
        String altUrl = DocumentManagerPreference.fromProject(project).getAltURL(publicUri,
                uriIfAbsent);

        if (altUrl == null) {
            return "";
        }

        ISemanticProject semanticProject = SemanticProjectManager
                .getSemanticProjectManager(project).obtainProject();

        ImportType type = semanticProject.getImportType(publicUri);

        return getAltUrlText(type, altUrl);
    }

    /**
     * Generate a name for the alternate URL:
     * 
     * <ul>
     * <li>cached file: .cache</li>
     * <li>workspace file: relative_worspace_path/filename</li>
     * <li>web reference: altUrl</li>
     * <li>hd reference: file:///absolute_path/filename</li>
     * </ul>
     */
    public static String getAltUrlText(ImportType type, String altUrl) {
        if (!Strings.isNullOrEmpty(altUrl)) {
            switch (type) {
            case HD_REFERENCE:
                return "file:///" + URIs.makePrettyURL(altUrl);
            case WEB_REFERENCE:
                return URIs.makePrettyURL(altUrl);
            case CACHE:
                return ".cache";
            case WORKSPACE:
                IFile file = WorkspaceUtils.getFileFromURI(altUrl);
                String path = file.getFullPath().toString();
                while (path.startsWith("/")) {
                    path = path.substring(1);
                }
                return path;
            default:
            }
        }
        return new String();
    }

    public static Image getAltUrlIcon(IProject project, String publicUri) {
        ISemanticProject semanticProject = SemanticProjectManager
                .getSemanticProjectManager(project).obtainProject();

        ImportType type = semanticProject.getImportType(publicUri);

        return getAltUrlIcon(type);
    }

    public static Image getAltUrlIcon(ImportType type) {
        switch (type) {
        case HD_REFERENCE:
            return CorePlugin.getDefault().getImage(CorePluginImages.IMG_WEB);
        case WEB_REFERENCE:
            return CorePlugin.getDefault().getImage(CorePluginImages.IMG_WEB);
        case CACHE:
            return CorePlugin.getDefault().getImage(CorePluginImages.IMG_ONTOLOGY_CACHE);
        case WORKSPACE:
            return CorePlugin.getDefault().getImage(CorePluginImages.IMG_SEMANTIC_FILE);
        }
        return null;
    }

    public static ImportType guessImportType(String publicUri, IProject project) {
        String altUrl = DocumentManagerPreference.fromProject(project).getAltURL(publicUri, true);
        CacheManager manager = CacheManager.fromProject(project);

        if (!URIs.isLocalFile(altUrl)) {
            return ImportType.WEB_REFERENCE;
        }

        if (manager.cacheFolderExists() && manager.isInCache(publicUri, altUrl)) {
            return ImportType.CACHE;
        }

        IFile workspaceFile;

        try {
            workspaceFile = WorkspaceUtils.getFileFromURI(altUrl);
        }
        catch (IllegalArgumentException ex) {
            workspaceFile = null;
        }

        if (workspaceFile == null) {
            return ImportType.HD_REFERENCE;
        }

        return ImportType.WORKSPACE;
    }

    public static ImportType guessImportTypeExternalUrl(String altUrl) {
        if (!URIs.isLocalFile(altUrl)) {
            return ImportType.WEB_REFERENCE;
        }
        return ImportType.HD_REFERENCE;
    }

    /**
     * The argument <code>project</code> is optional. If it's set to null,
     * caches can not be checked, meaning the returned import type won't show up
     * as cached.
     */
    public static ImportType guessImportTypeWorkspaceUrl(String altUrl, IProject project) {
        if (project != null) {
            DocumentManagerPreference pref = DocumentManagerPreference.fromProject(project);
            CacheManager manager = CacheManager.fromProject(project);
            for (String publicUri : pref.listReferringSpecs(altUrl)) {
                if (pref.getAltURL(publicUri, true).equals(altUrl)) {
                    if (manager.cacheFolderExists() && manager.isInCache(publicUri, altUrl)) {
                        return ImportType.CACHE;
                    }
                }
            }
        }

        IFile workspaceFile = null;
        try {
            workspaceFile = WorkspaceUtils.getFileFromURI(altUrl);
        }
        catch (IllegalArgumentException ex) {
        }
        if (workspaceFile != null) {
            return ImportType.WORKSPACE;
        }
        return ImportType.HD_REFERENCE; // fail safe
    }
}
