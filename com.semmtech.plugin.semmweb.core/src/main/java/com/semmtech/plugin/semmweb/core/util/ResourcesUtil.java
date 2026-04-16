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


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.filesystem.URIUtil;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.IFileEditorMapping;
import org.eclipse.ui.PlatformUI;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.semmtech.plugin.semmweb.core.nature.SemanticProject;


/**
 * Util class for Eclipse resources.
 * 
 * @author Mike Henrichs
 * 
 */
public final class ResourcesUtil {

    private static Logger logger = Logger.getLogger(ResourcesUtil.class);

    /**
     * Hidden constructor.
     */
    private ResourcesUtil() {
    }

    public static IResource[] listMembers(IContainer container, boolean recursive) {
        List<IResource> list = Lists.newArrayList();
        try {
            for (IResource member : container.members()) {
                if (member.isHidden() || member.isPhantom()) {
                    continue;
                }
                if (member instanceof IContainer && recursive) {
                    list.addAll(Arrays.asList(listMembers((IContainer) member, recursive)));
                }
                else if (!(member instanceof IContainer)) {
                    list.add(member);
                }
            }
        }
        catch (CoreException e) {
            e.printStackTrace();
        }
        IResource[] result = new IResource[list.size()];
        return result;
    }

    /**
     * Returns whether the specified editor supports opening the file extension
     * of the given resource.
     */
    public static boolean supportedByEditor(IResource resource, String editorId) {
        if (resource == null) {
            return false;
        }
        String extension = resource.getFileExtension();
        return extensionSupportedByEditor(extension, editorId);
    }

    /**
     * Returns whether the specified editor supports opening the file extension.
     */
    public static boolean extensionSupportedByEditor(String extension, String editorId) {
        if (!Strings.isNullOrEmpty(extension) && !Strings.isNullOrEmpty(editorId)) {
            IEditorRegistry editorReg = PlatformUI.getWorkbench().getEditorRegistry();
            IFileEditorMapping[] mappings = editorReg.getFileEditorMappings();
            for (IFileEditorMapping mapping : mappings) {
                if (extension.equals(mapping.getExtension())) {
                    for (IEditorDescriptor editorDesc : mapping.getEditors()) {
                        if (editorId.equals(editorDesc.getId())) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Copy the file in the specified folder, and if already exist provide a new
     * filename automatically. The new created file is returned.
     */
    public static IFile copyAndRename(IPath sourcePath, IContainer destFolder)
            throws CoreException, IOException {

        IPath fileName = new Path(sourcePath.lastSegment());

        if (destFolder.getFile(fileName).exists()) {
            fileName = new Path(getDefaultNewFileName(sourcePath.lastSegment(), destFolder));
        }

        IFile newFile = destFolder.getFile(fileName);
        IFile sourceFile = WorkspaceUtils.getFileFromAbsolutePath(sourcePath);

        if (sourceFile == null) {
            try (InputStream is = new FileInputStream(sourcePath.toFile())) {
                newFile.create(is, true, null);
            }
        }
        else {
            sourceFile.copy(newFile.getFullPath(), true, null);
        }

        return newFile;
    }

    public static String getDefaultNewFileName(String filename, IContainer folder) {
        String newFileName = "Copy of " + filename;
        int i = 1;

        while (folder.getFile(new Path(newFileName)).exists()) {
            newFileName = "Copy " + (i++) + " of " + filename;
        }

        return newFileName;
    }

    /**
     * Returns true if the workspace contains at least one project which has the
     * given nature; otherwise false is returned.
     * 
     * @param natureId
     * @return
     */
    public static boolean existsProjects(String natureId) {
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        boolean hasProjects = false;
        try {
            for (IResource member : root.members()) {
                if (member instanceof IProject) {
                    IProject project = (IProject) member;
                    try {
                        if (project.getNature(natureId) != null) {
                            hasProjects = true;
                            break;
                        }
                    }
                    catch (Exception ex) {
                        // the error is likely due to a closed project;
                        // ignore current project is assessment
                    }
                }
            }
        }
        catch (Exception ex) {
            logger.error(
                    String.format(
                            "Error occured trying to determine if workspace contains projects of nature \"%s\".",
                            natureId), ex);
        }
        return hasProjects;
    }

    /**
     * Returns true if the workspace contains at least one project which has the
     * SemanticProject nature; otherwise false is returned.
     * 
     * @return
     */
    public static boolean existsSemanticProjects() {
        return existsProjects(SemanticProject.NATURE_ID);
    }

    /**
     * Tries to return the local file (i.e. on the local file system) given a
     * url (which should be prefixed by "file:///" for such local file urls). If
     * no such local file exists, this function returns null.
     */
    public static File getLocalFile(String url) {
        try {
            String localFilePrefix = "file:///";
            if (url.startsWith(localFilePrefix)) {
                url = url.substring(localFilePrefix.length());
                IFileStore location = EFS.getLocalFileSystem().getStore(URIUtil.toURI(url, true));
                File file = location.toLocalFile(EFS.NONE, null);
                if (file != null && file.exists()) {
                    return file;
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static InputStream getFileUtf8Stream(File file) throws CoreException, IOException {
        if (file == null || !file.exists()) {
            return null;
        }

        IPath path = Path.fromOSString(file.getAbsolutePath());
        IFile iFile = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
        if (iFile != null && iFile.exists()) {
            return getFileUtf8Stream(iFile);
        }

        try (InputStream stream = new FileInputStream(file);) {
            InputStream utf8Stream = getUtf8Stream(stream, null);
            return utf8Stream;
        }
    }

    public static InputStream getFileUtf8Stream(IFile file) throws CoreException, IOException {
        String encoding = file.getCharset();
        try (InputStream stream = file.getContents();) {
            InputStream utf8Stream = getUtf8Stream(stream, encoding);
            return utf8Stream;
        }
    }

    public static InputStream getUtf8Stream(InputStream stream, String encoding)
            throws CoreException, IOException {
        byte[] bytes = IOUtils.toByteArray(stream);
        String content = null;
        if (encoding == null) {
            content = new String(bytes);
        }
        else {
            content = new String(bytes, encoding);
        }
        return IOUtils.toInputStream(content, Charsets.UTF_8.name());
    }
}
