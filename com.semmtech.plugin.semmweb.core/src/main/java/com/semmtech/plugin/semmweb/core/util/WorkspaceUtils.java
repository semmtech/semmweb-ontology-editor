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


import java.net.URI;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;


/**
 * 
 * @author Simone Rondelli
 */
public class WorkspaceUtils {

    private static Logger logger = Logger.getLogger(WorkspaceUtils.class);

    private final static String FILE_DEVICE_PATH = "file:";

    /**
     * Checks if the passed path is a member in the workspace, in that case an
     * IFile is returned. Note that files can only be members of the workspace
     * if they are stored within a project and are not filtered by resource
     * filters.
     * <p>
     * NB: This method expect to receive the absolute Path of a file, to make it
     * works with relative path we should use the method
     * {@code IWorkspaceRoot.exist()}.
     * 
     * @param file
     *            Absolute path of file
     * @return IResource if the file is inside the workspace, null otherwise
     */
    public static IFile getFileFromAbsolutePath(IPath file) {
        IWorkspace workspace = ResourcesPlugin.getWorkspace();
        IWorkspaceRoot root = workspace.getRoot();
        IFile workspaceFile = root.getFileForLocation(file);
        if (workspaceFile != null && workspaceFile.exists()) {
            return workspaceFile;
        }
        return null;
    }

    /**
     * @see #getFileFromAbsolutePath(IPath)
     */
    public static IFile getFileFromAbsolutePath(String filePath) {
        if (filePath != null) {
            if (filePath.startsWith(FILE_DEVICE_PATH)) {
                filePath = filePath.substring(FILE_DEVICE_PATH.length());
                filePath = StringUtils.stripStart(filePath, "/");
            }
        }
        return getFileFromAbsolutePath(new Path(filePath));
    }

    public static boolean isWorkspaceFile(String filePath) {
        return getFileFromAbsolutePath(filePath) != null;
    }

    /**
     * Checks if the passed URI is contained in the workspace, in that case an
     * IFile is returned.
     * 
     * @throws IllegalArgumentException
     *             Path must include project and resource
     */
    public static IFile getFileFromURI(URI uri) {
        if (!URIs.hasFileScheme(uri)) {
            return null;
        }
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        URI rootUri = root.getLocationURI();
        uri = rootUri.relativize(uri);
        IPath path = new Path(uri.getPath());

        IFile file = root.getFile(path);
        return file.exists() ? file : null;

    }

    /**
     * The passed string is converted to an URI performing the character
     * substitution (space and slash)
     * 
     * @see #getFileFromURI(URI)
     */
    public static IFile getFileFromURI(String uri) {
        return getFileFromURI(URI.create(uri.replace(" ", "%20").replace("\\", "/")));
    }

    /**
     * Enable/Disable the workspace Auto Build
     */
    public static void setWorkspaceAutoBuild(boolean flag) {
        IWorkspace workspace = ResourcesPlugin.getWorkspace();
        IWorkspaceDescription description = workspace.getDescription();
        description.setAutoBuilding(flag);
        try {
            workspace.setDescription(description);
        }
        catch (CoreException e) {
            logger.error("An Error occured while setting the autobuild flag", e);
        }
    }
}
