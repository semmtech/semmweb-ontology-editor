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


import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;
import org.eclipse.ui.services.IServiceLocator;

import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.extensionpoint.CoreExtensions;
import com.semmtech.plugin.semmweb.core.extensionpoint.IModelDeletionListener;
import com.semmtech.plugin.semmweb.core.jena.ontology.WorkspaceDocumentManagerConfiguration;
import com.semmtech.plugin.semmweb.core.model.IModelProvider;
import com.semmtech.plugin.semmweb.core.navigator.IModel;
import com.semmtech.plugin.semmweb.core.preferences.DocumentManagerPreference;
import com.semmtech.ui.plugin.util.Selections;


public class DeleteSelectedModelsHandler extends AbstractHandler {

    private static Logger logger = Logger.getLogger(DeleteSelectedModelsHandler.class);

    public static final String ID = "com.semmtech.plugin.semmweb.core.commands.deleteSemanticModel";

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        Shell shell = HandlerUtil.getActiveShell(event);
        ISelection selection = HandlerUtil.getCurrentSelection(event);
        List<IModel> selectedModels = Selections.retrieveAllAsType(selection, IModel.class);

        if (selectedModels.isEmpty()) {
            return null;
        }

        if (!MessageDialog.openConfirm(shell, "Delete Model",
                "Are you sure you want to delete the selected models from your project?")) {
            return null;
        }

        models: for (IModel model : selectedModels) {
            IFile file = (IFile) model.getResource();
            IProject project = file.getProject();

            // check if the model is opened in the editor
            // TODO ask to the user if wants close the model directly
            IModelProvider modelProvider = CorePlugin.getDefault().getModelProvider(file);
            if (modelProvider != null) {
                MessageDialog.openWarning(shell, "Model Opened", "The model " + file.getName()
                        + " is opened in the editor. Close the model before deleting it.");
                continue;
            }

            // This have to be executed before the document manager because the
            // listeners shoulden't change the state of the Model
            for (IModelDeletionListener deletionListener : CoreExtensions.findDeletionListener()) {
                if (!deletionListener.deleteModel(model)) {
                    continue models;
                }
            }

            // TODO: Check if the parameter can be changed to a more
            // explicit value, rather than a relative path
            String altUrl = String.format("file:///%s", file.getLocation());

            DocumentManagerPreference preference = DocumentManagerPreference.fromProject(project);
            List<String> uris = preference.listReferringSpecs(altUrl);
            if (!uris.isEmpty()) {
                MessageDialog
                        .openInformation(
                                shell,
                                "Referred Model",
                                "The model is referred to by the document manager. Deleting this model may therefore cause other models to no longer be interpreted correctly.");

                WorkspaceDocumentManagerConfiguration config = preference
                        .getDocumentManagerConfig();

                for (String uri : uris) {
                    config.setWorkspaceAltURL(uri, null);
                }

                preference.setDocumentManagerConfig(config);

                try {
                    preference.save();
                }
                catch (IOException ex) {
                    logger.error("Error while saving preferences", ex);
                }
            }

            try {
                file.delete(true, null);
            }
            catch (CoreException ex) {
                String message = "Unable to delete the selected model file. It might be that a process is still using it.\n"
                        + "Cleaning the project might resolve this issue. If not, try to restart the SEMMweb Editor.";
                MessageDialog.openError(shell, "Error", message);
            }

        }
        return null;
    }

    public static CommandContributionItem createCommand(IServiceLocator serviceLocator,
            String label, String imageKey) {
        CommandContributionItemParameter param = new CommandContributionItemParameter(
                serviceLocator, null, DeleteSelectedModelsHandler.ID, SWT.PUSH);
        param.label = label;
        param.icon = CorePlugin.getDefault().getImageDescriptor(imageKey);
        return new CommandContributionItem(param);
    }

}
