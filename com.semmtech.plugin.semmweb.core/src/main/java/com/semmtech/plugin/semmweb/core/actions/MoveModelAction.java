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

package com.semmtech.plugin.semmweb.core.actions;


import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.google.common.collect.Lists;
import com.semmtech.plugin.semmweb.core.dialog.ProjectSelectionDialog;
import com.semmtech.plugin.semmweb.core.navigator.IModel;
import com.semmtech.plugin.semmweb.core.operations.CopyAndMoveFilesOperation;
import com.semmtech.plugin.semmweb.core.preferences.ModelsFolderPreference;
import com.semmtech.ui.plugin.util.Selections;


/**
 * Perform the move of a model from one project to another. The model is put
 * inside the models folder of the target project.
 * 
 * @author Sander Stolk
 * @author Simone Rondelli
 * 
 */
public class MoveModelAction extends Action {

    private static Logger logger = Logger.getLogger(MoveModelAction.class);

    /**
     * The workbench window this action will run in
     */
    private IWorkbenchWindow window;

    public MoveModelAction(IWorkbenchWindow window) {
        this.window = window;
        setText("Move...");
        setToolTipText("Move selected resource(s)");
    }

    @Override
    public void run() {
        ISelection selection = window.getSelectionService().getSelection();
        List<IModel> selectedModels = Selections.retrieveAllAsType(selection, IModel.class);

        if (selectedModels.isEmpty()) {
            return;
        }

        ProjectSelectionDialog dialog = new ProjectSelectionDialog(window.getShell(),
                selectedModels.get(0).getProject(), "Move Models",
                "Select the Project in which you want to move the model");

        if (dialog.open() == Window.OK) {
            IProject destProject = dialog.getSelectedProject();
            String destModelsPath = ModelsFolderPreference.fromProject(destProject)
                    .getModelsFolderPath();
            IFolder destModelsDir = (IFolder) destProject.findMember(destModelsPath);

            List<IResource> resources = new ArrayList<>(selectedModels.size());

            for (IModel model : selectedModels) {
                resources.add(model.getResource());
            }

            CopyAndMoveFilesOperation copyAndMoveFilesOperation = new CopyAndMoveFilesOperation(
                    window.getShell(), CopyAndMoveFilesOperation.MODALITY_MOVE);
            copyAndMoveFilesOperation.copyFiles(resources.toArray(new IResource[] {}),
                    destModelsDir);

            List<IEditorReference> toBeClosedEditors = Lists.newArrayList();
            IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                    .getActivePage();

            // get editors to be closed
            for (IEditorReference editorRef : activePage.getEditorReferences()) {
                try {
                    IFile file = (IFile) editorRef.getEditorInput().getAdapter(IFile.class);

                    if (resources.contains(file)) {
                        toBeClosedEditors.add(editorRef);
                    }
                }
                catch (PartInitException e) {
                    logger.error("Error while getting Editor Input", e);
                }
            }

            // close the editors
            activePage.closeEditors(
                    toBeClosedEditors.toArray(new IEditorReference[toBeClosedEditors.size()]),
                    false);

        }
    }
}
