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


import java.io.FileInputStream;
import java.net.URI;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.menus.CommandContributionItemParameter;
import org.eclipse.ui.services.IServiceLocator;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.cache.CacheManager;
import com.semmtech.plugin.semmweb.core.commands.Commands;
import com.semmtech.plugin.semmweb.core.commands.EnabledCommandContributionItem;
import com.semmtech.plugin.semmweb.core.jena.ontology.WorkspaceDocumentManagerConfiguration;
import com.semmtech.plugin.semmweb.core.navigator.IImport;
import com.semmtech.plugin.semmweb.core.preferences.DocumentManagerPreference;
import com.semmtech.plugin.semmweb.core.preferences.ModelsFolderPreference;
import com.semmtech.ui.plugin.util.Selections;


/**
 * Open the editor on the model of the selected import. There are three
 * possibilities:
 * 
 * <ul>
 * <li>The import is a local file: The editor is opened in the same way of the
 * project model.</li>
 * <li>The import is cached: The file in cache is copied in the project folder
 * and then opened.</li>
 * <li>The import is remote: The model is downloaded and then opened in the
 * editor</li>
 * </ul>
 * 
 * @author Simone Rondelli
 * 
 */
public class EditImportHandler extends AbstractHandler {

    private static Logger logger = Logger.getLogger(EditImportHandler.class);

    public static final String ID = "com.semmtech.plugin.semmweb.core.commands.editImport";
    public static final String PARAMETER_URL = "url";

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        Shell shell = HandlerUtil.getActiveShell(event);
        ISelection selection = HandlerUtil.getCurrentSelection(event);
        IImport immport = Selections.retrieveFirstAsType(selection, IImport.class);

        if (immport == null) {
            return null;
        }

        final IProject project = immport.getProject();
        DocumentManagerPreference preferences = DocumentManagerPreference.fromProject(project);

        // if the import is a web reference I have to download it in the Project
        // folder and then open it in the editor
        if (immport.isWebReference()) {

            boolean downloadModel = MessageDialog
                    .openQuestion(shell, "Download Model",
                            "The model has to be downloaded in order to edit it. Would you like to download it now?");

            if (!downloadModel) {
                return null;
            }

            Map<String, String> parameters = Maps.newHashMap();
            parameters.put(ImportPublicURIHandler.PARAMETER_PUBLIC_URI, immport.getURI());
            parameters.put(ImportPublicURIHandler.PARAMETER_FILENAME, null);

            // show the dialog that perform the download of a web model and put
            // the reference of the downloaded file in the Document Manager
            Commands.execute(ImportPublicURIHandler.ID, parameters);
        }
        // if the model is cached I have to copy it from the cache to the
        // project folder and then open it in the editor
        else if (immport.isCached()) {
            String message = "The model that you are trying to edit is located in cache. "
                    + "The model has to be moved into the Project folder. Do you want to move it now?\n\n"
                    + "If so, please provide a name for the Model file.";

            String altUrl = preferences.getAltURL(immport.getURI(), false);
            final String fileExtension = Files.getFileExtension(altUrl);

            final IPath modelsPath = new Path(ModelsFolderPreference.fromProject(project)
                    .getModelsFolderPath());

            InputDialog copyCacheDialog = new InputDialog(shell, "Cached Model", message, "model."
                    + fileExtension, new IInputValidator() {

                @Override
                public String isValid(String newText) {
                    if (!Files.getFileExtension(newText).equalsIgnoreCase(fileExtension)) {
                        return "The extension of the file must be ." + fileExtension;
                    }

                    IFile newFile = project.getFile(modelsPath.append(newText));

                    if (newFile.exists()) {
                        return "There already exists a file called " + newText
                                + " in the Project folder.\nPlease choose a different file name.";
                    }

                    return null;
                }
            });

            int res = copyCacheDialog.open();

            if (res == Window.OK) {
                String newFileName = copyCacheDialog.getValue();

                IFile newFile = project.getFile(modelsPath.append(newFileName));

                try (FileInputStream fis = new FileInputStream(altUrl.replaceAll("file:///", ""))) {
                    newFile.create(fis, true, null);

                    String publicUri = immport.getURI();
                    CacheManager.fromProject(project).removeFile(publicUri);

                    WorkspaceDocumentManagerConfiguration config = preferences
                            .getDocumentManagerConfig();
                    config.setWorkspaceAltURL(publicUri, "file:///"
                            + newFile.getLocation().toString());
                    preferences.setDocumentManagerConfig(config);
                    preferences.save();
                }
                catch (Exception e) {
                    String msg = "An error occurred while copying file from cache to Project folder: "
                            + e.getMessage();
                    logger.error(msg, e);
                    MessageDialog.openError(shell, "Error", msg);
                }

            }
            else {
                return null;
            }
        }

        String altUrl = preferences.getAltURL(immport.getURI(), false);

        if (Strings.isNullOrEmpty(altUrl)) {
            return null;
        }

        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        IFile[] files = root.findFilesForLocationURI(URI.create(altUrl.replace(" ", "%20")));

        if (files.length != 0) {
            CorePlugin.getDefault().openModelEditor(files[0]);
        }

        return null;
    }

    public static EnabledCommandContributionItem createCommand(IServiceLocator serviceLocator,
            String label, String imageKey) {
        CommandContributionItemParameter commandParam = new CommandContributionItemParameter(
                serviceLocator, "editImport", ID, SWT.PUSH);

        commandParam.icon = CorePlugin.getDefault().getImageDescriptor(imageKey);
        commandParam.label = label;

        return new EnabledCommandContributionItem(commandParam);
    }

}
