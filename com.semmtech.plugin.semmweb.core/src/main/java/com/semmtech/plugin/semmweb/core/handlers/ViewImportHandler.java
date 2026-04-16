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

package com.semmtech.plugin.semmweb.core.handlers;


import java.io.File;
import java.net.URI;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;
import org.eclipse.ui.services.IServiceLocator;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.commands.Commands;
import com.semmtech.plugin.semmweb.core.forms.editor.OntologyFormEditor;
import com.semmtech.plugin.semmweb.core.navigator.IImport;
import com.semmtech.plugin.semmweb.core.preferences.DocumentManagerPreference;
import com.semmtech.plugin.semmweb.core.util.SemanticProjectUtils;
import com.semmtech.plugin.semmweb.core.util.WorkspaceUtils;
import com.semmtech.ui.plugin.util.Selections;


public class ViewImportHandler extends AbstractHandler {

    public static final String ID = "com.semmtech.plugin.semmweb.core.commands.viewImport";

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        ISelection selection = HandlerUtil.getCurrentSelection(event);
        IImport immport = Selections.retrieveFirstAsType(selection, IImport.class);

        if (immport == null) {
            return null;
        }

        String filePath = null;

        if (immport.isWebReference()) {

            boolean downloadModel = MessageDialog
                    .openQuestion(HandlerUtil.getActiveShell(event), "Download Model",
                            "The model has to be downloaded in order to view it. Would you like to download it now?");

            if (!downloadModel) {
                return null;
            }

            Map<String, String> parameters = Maps.newHashMap();
            parameters.put(CacheModelHandler.PARAMETER_MODEL_URI, immport.getURI());
            parameters
                    .put(CacheModelHandler.PARAMETER_PROJECT_NAME, immport.getProject().getName());
            parameters.put(CacheModelHandler.PARAMETER_TEMPORARY, "true");

            IFile cachedFile = (IFile) Commands.execute(CacheModelHandler.ID, parameters);

            if (cachedFile != null) {
                filePath = cachedFile.getFullPath().toString();
            }
        }
        else {
            DocumentManagerPreference preferences = DocumentManagerPreference.fromProject(immport
                    .getProject());
            String altUrl = preferences.getAltURL(immport.getURI(), false);

            if (Strings.isNullOrEmpty(altUrl)) {
                return null;
            }

            IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
            IFile[] files = root.findFilesForLocationURI(URI.create(altUrl.replace(" ", "%20")));
            if (files.length > 0) {
                filePath = files[0].getFullPath().toString();
            }
            else {
                try {
                    IProject project = immport.getProject();
                    IFolder modelsFolder = SemanticProjectUtils.getModelsFolder(project);
                    File destDir = new File(modelsFolder.getLocation().toOSString(), ".tmp");
                    if (!destDir.exists()) {
                        destDir.mkdir();
                    }
                    if (destDir.exists() && destDir.isDirectory()) {
                        File file = new File(altUrl.replace("file:///", ""));
                        FileUtils.copyFileToDirectory(file, destDir);
                        File tempFile = new File(destDir, FilenameUtils.getName(altUrl));
                        modelsFolder.getFolder(".tmp").refreshLocal(IResource.DEPTH_INFINITE, null);
                        filePath = WorkspaceUtils
                                .getFileFromAbsolutePath(tempFile.getAbsolutePath()).getFullPath()
                                .toString();
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        Map<String, String> parameters = Maps.newHashMap();
        parameters.put(OpenSemanticModelHandler.PARAMETER_FILE_PATH, filePath);
        Object editorPart = Commands.execute(OpenSemanticModelHandler.ID, parameters);
        if (editorPart == null) {
            Shell shell = Display.getDefault().getActiveShell();
            String title = "Could not open the model";
            String message = "Unfortunately, the model could not be opened.";
            MessageDialog.openInformation(shell, title, message);
        }
        else if (editorPart instanceof OntologyFormEditor) {
            OntologyFormEditor ontologyFormEditor = (OntologyFormEditor) editorPart;
            ontologyFormEditor.setReadOnly(true);
            ontologyFormEditor.setCustomTitle(immport.getURI());
        }

        return null;
    }

    public static CommandContributionItem createCommand(IServiceLocator serviceLocator,
            String label, String imageKey) {
        CommandContributionItemParameter commandParam = new CommandContributionItemParameter(
                serviceLocator, "clearCache", ID, SWT.PUSH);

        commandParam.icon = CorePlugin.getDefault().getImageDescriptor(imageKey);
        commandParam.label = label;

        return new CommandContributionItem(commandParam);
    }

}
