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


import org.apache.log4j.Logger;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.menus.CommandContributionItemParameter;
import org.eclipse.ui.services.IServiceLocator;

import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.commands.EnabledCommandContributionItem;
import com.semmtech.plugin.semmweb.core.util.ProjectUtils;
import com.semmtech.plugin.semmweb.core.util.SemanticProjectUtils;
import com.semmtech.ui.plugin.util.ClipboardUtils;


/**
 * Paste a Project folder contained into the Clipboard in the workspace
 * 
 * @author Simone Rondelli
 * 
 */
public class PasteProjectHandler extends AbstractHandler {

    private final static Logger logger = Logger.getLogger(PasteProjectHandler.class);

    public static final String ID = "com.semmtech.plugin.semmweb.core.pasteProject";

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        Shell shell = HandlerUtil.getActiveShell(event);

        for (String file : ClipboardUtils.getFiles()) {
            if (SemanticProjectUtils.isSemanticProjectDir(file)) {
                try {
                    IPath externalProjectPath = new Path(file);
                    String projectName = externalProjectPath.lastSegment();

                    if (ProjectUtils.isProjectInWorkspacePath(projectName)) {
                        String title = "New Project Name";
                        String message = "Select a new name for the project " + projectName;

                        ProjectNameValidator validator = new ProjectNameValidator();
                        String suggestedName = "Copy of " + projectName;

                        int i = 1;
                        while (validator.isValid(suggestedName) != null) {
                            suggestedName = "Copy " + i + " of " + projectName;
                            i++;
                        }

                        InputDialog projectNameDialog = new InputDialog(shell, title, message,
                                suggestedName, new ProjectNameValidator());

                        if (projectNameDialog.open() == Window.OK) {
                            projectName = projectNameDialog.getValue();
                        }
                        else {
                            continue;
                        }
                    }

                    IWorkspace workspace = ResourcesPlugin.getWorkspace();

                    // Copy the project into workspace
                    IPath workspaceProjectPath = workspace.getRoot().getLocation()
                            .append(projectName);

                    IFileStore source = EFS.getLocalFileSystem().getStore(externalProjectPath);
                    IFileStore destination = EFS.getLocalFileSystem()
                            .getStore(workspaceProjectPath);

                    source.copy(destination, EFS.OVERWRITE, null);

                    // load Project Description
                    IProjectDescription description;
                    description = workspace.loadProjectDescription(externalProjectPath
                            .append(IProjectDescription.DESCRIPTION_FILE_NAME));

                    // set the new project name if changed
                    description.setName(projectName);

                    // use the project description to create a project
                    IProject project = workspace.getRoot().getProject(projectName);
                    project.create(description, null);
                    project.open(null);
                    // ensure the new project name takes effect
                    project.move(description, true, null);
                }
                catch (Exception e) {
                    String message = "An error occurred while importing project " + file;
                    logger.error(message, e);
                    MessageDialog.openError(shell, "Import Project Error",
                            message + "\n-" + e.getMessage());
                }

            }
        }
        return null;
    }

    /**
     * Check if there are projects in the clipboard and these project aren't in
     * the actual workspace
     */
    public static boolean commandEnabled() {
        for (String file : ClipboardUtils.getFiles()) {
            if (SemanticProjectUtils.isSemanticProjectDir(file)) {
                return true;
            }
        }
        return false;
    }

    public static EnabledCommandContributionItem createCommand(IServiceLocator serviceLocator,
            String label, String imageKey) {

        CommandContributionItemParameter param = new CommandContributionItemParameter(
                serviceLocator, "importSemanticProject", ID, SWT.PUSH);

        param.label = label;
        param.icon = CorePlugin.getDefault().getImageDescriptor(imageKey);

        return new EnabledCommandContributionItem(param);
    }

    private class ProjectNameValidator implements IInputValidator {

        @Override
        public String isValid(String newText) {
            if (ProjectUtils.isProjectInWorkspacePath(newText)) {
                return "There already exists a project named " + newText;
            }

            return null;
        }
    }
}
