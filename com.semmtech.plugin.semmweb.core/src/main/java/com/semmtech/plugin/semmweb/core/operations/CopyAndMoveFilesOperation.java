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

package com.semmtech.plugin.semmweb.core.operations;


import static com.semmtech.plugin.semmweb.core.dialog.SameFileDialog.STATUS_OVERWRITE;
import static com.semmtech.plugin.semmweb.core.dialog.SameFileDialog.STATUS_OVERWRITE_ALL;
import static com.semmtech.plugin.semmweb.core.dialog.SameFileDialog.STATUS_RENAME;
import static com.semmtech.plugin.semmweb.core.dialog.SameFileDialog.STATUS_SKIP;
import static com.semmtech.plugin.semmweb.core.dialog.SameFileDialog.STATUS_SKIP_ALL;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;

import com.google.common.collect.Lists;
import com.semmtech.plugin.semmweb.core.dialog.SameFileDialog;
import com.semmtech.plugin.semmweb.core.util.ResourcesUtil;
import com.semmtech.plugin.semmweb.core.util.WorkspaceUtils;


/**
 * Provide the copy and move operation on a given set of files/resources.
 * <p>
 * The class tries to guess if the source files are inside the workspace, in
 * that case transforms the IPath into IFile to make possible the copy of the
 * file's metadata.
 * <p>
 * NB: The class assumes to work with absolute file paths. With the relative
 * path the method {@code getWorkspaceFile} cannot find the IFile inside the
 * workspace
 * <p>
 * TODO: Perhaps is better to create different methods for copy and move
 * operation but it requires more code
 * 
 * @author Simone Rondelli
 */
public class CopyAndMoveFilesOperation {

    public static final int MODALITY_COPY = 0;
    public static final int MODALITY_MOVE = 1;

    private static final int OPERATION_OVERWRITE = 0;
    private static final int OPERATION_SKIP = 1;

    private int modality;
    private boolean applyToAll;
    private int defaultOperation;

    private Shell shell;

    private List<String> copiedFilePaths;

    private boolean forceRename;

    private RenamingStrategy renamingStrategy;

    /**
     * @param modality
     *            Decide if is a Copy operation or Move operation
     * 
     * @see {@code CopyAndMoveFilesOperation.MODALITY_COPY}
     * @see {@code CopyAndMoveFilesOperation.MODALITY_MOVE}
     */
    public CopyAndMoveFilesOperation(Shell shell, int modality) {
        this.shell = shell;
        this.applyToAll = false;
        this.forceRename = false;
        this.defaultOperation = 1;
        this.copiedFilePaths = Lists.newArrayList();

        if (modality == MODALITY_COPY || modality == MODALITY_MOVE) {
            this.modality = modality;
        }
        else {
            throw new IllegalArgumentException("The modality does not exist");
        }
    }

    public void copyFiles(List<String> sourceFiles, IContainer destFolder) {
        for (String filePath : sourceFiles) {
            IPath sourcePath = new Path(filePath);
            try {
                if (copyFile(sourcePath, destFolder) && modality == MODALITY_MOVE) {
                    IFile workspaceFile = WorkspaceUtils.getFileFromAbsolutePath(sourcePath);
                    if (workspaceFile == null) {
                        File f = new File(filePath);
                        f.delete();
                    }
                    else {
                        workspaceFile.delete(true, null);
                    }
                }
            }
            catch (IOException | CoreException e) {
                MessageDialog.openError(shell, "Error", "Error during the copy of file "
                        + sourcePath.lastSegment() + ":\n -" + e.getMessage());
            }
        }
    }

    public void copyFiles(IResource[] sourceFiles, IContainer destFolder) {
        for (IResource resource : sourceFiles) {
            IPath filePath = resource.getRawLocation();
            try {
                if (copyFile(filePath, destFolder) && modality == MODALITY_MOVE) {
                    resource.delete(true, null);
                }
            }
            catch (IOException | CoreException e) {
                MessageDialog.openError(
                        shell,
                        "Error",
                        "Error during the copy of file " + filePath.lastSegment() + ":\n -"
                                + e.getMessage());
            }
        }
    }

    /**
     * Copy the sourceFile inside the destination folder. In case of file with
     * the same name of the sourceFile exist in the destFolder a dialog with the
     * overwrite, skip and rename options will be shown
     * 
     * @return The absolute path of the copied file, null if nothing has been
     *         copied
     * @throws IOException
     * @throws CoreException
     */
    private boolean copyFile(IPath sourceFile, IContainer destFolder) throws IOException,
            CoreException {
        IPath workspacePath = new Path(sourceFile.lastSegment());
        IFile workspaceFile = destFolder.getFile(workspacePath);

        if (workspaceFile.exists()) {
            if (applyToAll) {
                if (defaultOperation == OPERATION_OVERWRITE) {
                    return doOverwrite(sourceFile, workspaceFile);
                }
            }
            else {
                if (forceRename) {
                    String newFileName;

                    if (renamingStrategy == null) {
                        renamingStrategy = new DefaultRenameStrategy();
                    }

                    newFileName = renamingStrategy.rename(sourceFile.lastSegment(), destFolder);

                    return doRename(sourceFile, newFileName, destFolder);
                }

                SameFileDialog sameFileDialog = new SameFileDialog(shell, sourceFile, destFolder);

                if (sameFileDialog.open() == Window.OK) {
                    switch (sameFileDialog.getStatus()) {
                    case STATUS_OVERWRITE: {
                        return doOverwrite(sourceFile, workspaceFile);
                    }
                    case STATUS_OVERWRITE_ALL: {
                        applyToAll = true;
                        defaultOperation = OPERATION_OVERWRITE;
                        return doOverwrite(sourceFile, workspaceFile);
                    }
                    case STATUS_SKIP: {
                        break;
                    }
                    case STATUS_SKIP_ALL: {
                        applyToAll = true;
                        defaultOperation = OPERATION_SKIP;
                        break;
                    }
                    case STATUS_RENAME: {
                        String newFileName = sameFileDialog.getNewFileName();
                        return doRename(sourceFile, newFileName, destFolder);
                    }
                    }
                }
            }
        }
        else {
            return doCopyFile(sourceFile, destFolder);
        }

        // no operation has been performed
        return false;
    }

    private boolean doCopyFile(IPath sourcePath, IContainer destFolder) throws IOException,
            CoreException {

        IFile newFile = destFolder.getFile(new Path(sourcePath.lastSegment()));
        IFile sourceFile = WorkspaceUtils.getFileFromAbsolutePath(sourcePath);

        if (sourceFile == null) {
            try (InputStream is = new FileInputStream(sourcePath.toFile())) {
                newFile.create(is, true, null);
            }
        }
        else {
            sourceFile.copy(newFile.getFullPath(), true, null);
        }

        copiedFilePaths.add(newFile.getLocation().toOSString());
        return true;
    }

    private boolean doOverwrite(IPath sourcePath, IFile destFile) throws CoreException, IOException {

        IFile sourceFile = WorkspaceUtils.getFileFromAbsolutePath(sourcePath);

        if (sourceFile == null) {
            try (InputStream is = new FileInputStream(sourcePath.toFile())) {
                destFile.setContents(is, true, false, null);
            }
        }
        else {
            String tmpFileName = "." + destFile.getLocation().lastSegment();
            IFile tmpFile = destFile.getParent().getFile(new Path(tmpFileName));
            destFile.move(tmpFile.getFullPath(), true, null);

            try {
                sourceFile.copy(destFile.getFullPath(), true, null);
                tmpFile.delete(true, null);
            }
            catch (Exception ex) {
                destFile.delete(true, null);
                tmpFile.move(destFile.getFullPath(), true, null);
                return false;
            }
        }

        copiedFilePaths.add(destFile.getLocation().toOSString());
        return true;
    }

    private boolean doRename(IPath sourcePath, String newFileName, IContainer destFolder)
            throws CoreException, IOException {

        IFile newFile = destFolder.getFile(new Path(newFileName));
        IFile sourceFile = WorkspaceUtils.getFileFromAbsolutePath(sourcePath);

        if (sourceFile == null) {
            try (InputStream is = new FileInputStream(sourcePath.toFile())) {
                newFile.create(is, true, null);
            }
        }
        else {
            sourceFile.copy(newFile.getFullPath(), true, null);
        }

        copiedFilePaths.add(newFile.getLocation().toOSString());
        return true;
    }

    /**
     * Gets the absolute path of the successfully copied files
     * 
     * @return
     */
    public List<String> getCopiedFilePaths() {
        return copiedFilePaths;
    }

    public void setForceRename(boolean forceRename) {
        this.forceRename = forceRename;
    }

    public void setRenamingStrategy(RenamingStrategy renamingStrategy) {
        this.renamingStrategy = renamingStrategy;
    }

    public interface RenamingStrategy {
        public String rename(String oldFileName, IContainer folder);
    }

    public static class DefaultRenameStrategy implements RenamingStrategy {

        @Override
        public String rename(String oldFileName, IContainer folder) {
            return ResourcesUtil.getDefaultNewFileName(oldFileName, folder);
        }
    }
}
