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


import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchCommandConstants;
import org.eclipse.ui.IWorkbenchWindow;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.util.FileUtils;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.CorePluginImages;
import com.semmtech.plugin.semmweb.core.model.ModelObtainer;
import com.semmtech.plugin.semmweb.core.model.OntModelUtils;
import com.semmtech.plugin.semmweb.core.navigator.IModel;
import com.semmtech.plugin.semmweb.core.navigator.ISemanticElement;
import com.semmtech.plugin.semmweb.core.operations.CopyAndMoveFilesOperation;
import com.semmtech.plugin.semmweb.core.operations.CopyAndMoveFilesOperation.DefaultRenameStrategy;
import com.semmtech.plugin.semmweb.core.preferences.ModelsFolderPreference;
import com.semmtech.plugin.semmweb.core.util.SemanticProjectUtils;
import com.semmtech.plugin.semmweb.core.util.WorkspaceUtils;
import com.semmtech.plugin.semmweb.core.wizards.ModelNameWizard;
import com.semmtech.ui.plugin.jobs.JobWithMonitor;
import com.semmtech.ui.plugin.util.ClipboardUtils;
import com.semmtech.ui.plugin.util.Selections;


/**
 * Creates new models, inside the selected Project, from the content of the
 * Clipboard. The action should enabled only if the content of the Clipboard is
 * one or more file, the state (enabled/disabled) is set by
 * {@link SemanticProjectActionProvider} and {@link EditActionProvider} at the
 * moment of the menu creation.
 * 
 * @author Simone Rondelli
 */
public class PasteModelAction extends Action {

    private static Logger logger = Logger.getLogger(PasteModelAction.class);

    /**
     * The workbench window this action will run in
     */
    private IWorkbenchWindow window;

    /**
     * This action is called if the selected node is a Project
     */
    private InternalPasteAction defaultPasteAction;

    public PasteModelAction(IWorkbenchWindow window) {
        this.window = window;
        setText("Paste@Ctrl+V");
        setImageDescriptor(CorePlugin.getDefault().getImageDescriptor(CorePluginImages.IMG_PASTE));
        setToolTipText("Copy selected model(s)");
        setAccelerator(SWT.MOD1 | 'V');

        defaultPasteAction = new InternalPasteAction(window.getShell(), new Clipboard(window
                .getShell().getDisplay()));
        defaultPasteAction.setActionDefinitionId(IWorkbenchCommandConstants.EDIT_PASTE);
    }

    @Override
    public void run() {
        ISelection selection = window.getSelectionService().getSelection();
        List<ISemanticElement> selectedElements = Selections.retrieveAllAsType(selection,
                ISemanticElement.class);

        final IProject project;
        boolean projectRootSelcted = false;

        if (selectedElements.isEmpty()) {
            // Perhaps Project is selected (project isn't instanceof
            // ISemanticElement)
            project = Selections.retrieveFirstAsType(selection, IProject.class);
            projectRootSelcted = true;
        }
        else if (selectedElements.size() == 1) {
            project = selectedElements.get(0).getProject();
        }
        else {
            return;
        }

        List<String> filePaths = ClipboardUtils.getFiles();

        if (filePaths.isEmpty()) {
            return;
        }

        boolean selectionContainsDir = false;

        for (String path : filePaths) {
            File f = new File(path);

            if (f.exists() && f.isDirectory()) {
                selectionContainsDir = true;
                break;
            }
        }

        // the selected element isn't neither ISemanticElement nor IProject
        // maybe is a normal folder
        if (project == null || (selectionContainsDir && projectRootSelcted)) {
            defaultPasteAction.selectionChanged((IStructuredSelection) selection);
            defaultPasteAction.run();
            return;
        }

        // disable pasting of folder inside logical folder
        if (selectionContainsDir) {
            return;
        }

        String modelsPath = ModelsFolderPreference.fromProject(project).getModelsFolderPath();
        IContainer modelsDir = (IFolder) project.findMember(modelsPath);
        IContainer projectDir = project;

        IContainer destDir = modelsDir;

        if (projectRootSelcted) {

            /*
             * There are three possibilities:
             * 
             * -The user has selected only Models: In this case a dialog is
             * shown asking if wants to imports all Models inside the Models
             * folder
             * 
             * -The user has selected only Normal Files: The files are simply
             * pasted on the root of the project
             * 
             * -The user has selected both Models and Normal Files: The user is
             * warned that some files in the selection are Models and putting
             * them into the root folder of the project lead to loss all the
             * Smart Semantic behavior provided by SEMMweb
             */

            boolean modelsFound = false;
            boolean normalFilesFound = false;

            for (String fileName : filePaths) {
                if (FileUtils.guessLang(fileName, null) == null) {
                    normalFilesFound = true;
                }
                else {
                    modelsFound = true;
                }
            }

            // The user has selected only Models
            if (modelsFound && !normalFilesFound) {
                boolean importModels = MessageDialog
                        .openQuestion(
                                window.getShell(),
                                "Paste",
                                "It appears that you are pasting semantic files, do you want to paste these files into the Models folder?");

                if (importModels) {
                    destDir = modelsDir;
                }
                else {
                    destDir = projectDir;
                }
            }
            // The user has selected only Normal Files
            else if (!modelsFound && normalFilesFound) {
                destDir = projectDir;
            }
            // The user has selected both Models and Normal Files
            else if (modelsFound && normalFilesFound) {
                MessageDialog
                        .openWarning(
                                window.getShell(),
                                "Paste",
                                "The selection contains semantic- and non-semantic files, pasting sematic files outside the Models folder will restrict the SEMMweb Editor's sematic behavior.\n"
                                        + "If you wish to benefit from the SEMMweb Editor's capabilities move these files into the Models folder.");
                destDir = projectDir;
            }
        }

        // If the copy/move action has been performed in the models directory we
        // should check if there aren't existing models with the same Model Name
        // or Ontology URI and, in that case, force the user to change them
        if (destDir.equals(modelsDir)) {
            copyModels(project, filePaths);
        }
        else {
            CopyAndMoveFilesOperation copyAndMoveFilesOperation = new CopyAndMoveFilesOperation(
                    window.getShell(), CopyAndMoveFilesOperation.MODALITY_COPY);
            copyAndMoveFilesOperation.copyFiles(filePaths, destDir);
        }
    }

    /**
     * Checks for duplicate Model Name and Ontology URI in the Models folder
     */
    private static class CheckModelConflictsJob extends JobWithMonitor {

        private IProject project;
        private List<String> filesPath;
        private Map<String, String> originalFilenames;
        private Shell shell;

        public CheckModelConflictsJob(Shell shell, IProject project, List<String> filesPath) {
            super("Checking for naming conflicts");
            this.project = project;
            this.filesPath = filesPath;
            this.originalFilenames = Maps.newHashMap();
            this.shell = shell;
        }

        public void setOriginalFilenameMap(Map<String, String> map) {
            this.originalFilenames = map;
        }

        @Override
        protected IStatus run(IProgressMonitor monitor) {
            startMonitorUpdate(monitor, "Checking for naming conflicts", 3 + filesPath.size());

            ArrayList<String> fileNames = Lists.newArrayList();
            Map<String, String> modelNames = Maps.newHashMap();
            Map<String, String> ontologyURIs = Maps.newHashMap();

            updateSubTask("Retrieving filenames...");

            for (IFile file : SemanticProjectUtils.getModelFiles(project)) {
                String filename = file.getName();
                fileNames.add(filename);
            }

            monitor.worked(1);

            updateSubTask("Retrieving model Names...");
            // retrieves all the model name of already existent models. The
            // models pasted right now aren't in the IModel list and their name
            // will be retrieved in the next for cycle
            for (IModel model : SemanticProjectUtils.getModels(project)) {
                String modelName = model.getName();
                String resourceName = model.getResource().getName();
                if (modelName == null) {
                    modelNames.put(resourceName, model.getLocationURL());
                }
                else {
                    modelNames.put(resourceName, modelName);
                }
            }

            monitor.worked(1);

            updateSubTask("Retrieving ontology URIs...");

            for (IFile file : SemanticProjectUtils.getModelFiles(project)) {
                String fileName = file.getName();
                ModelObtainer obtainer = new ModelObtainer(file, false);
                obtainer.run();
                OntModel ontModel = obtainer.getModel();

                if (ontModel != null) {
                    List<Resource> ontologies = OntModelUtils
                            .getOntologies(ontModel.getBaseModel());
                    if (ontologies.size() == 1) {
                        ontologyURIs.put(fileName, ontologies.get(0).getURI());
                    }

                    // Gets the model name for the pasted file
                    if (modelNames.get(fileName) == null) {
                        String modelName = OntModelUtils.getName(ontModel);
                        if (Strings.isNullOrEmpty(modelName)) {
                            modelName = fileName;
                        }
                        modelNames.put(fileName, modelName);
                    }
                }
            }

            monitor.worked(1);

            for (final String filePath : filesPath) {
                updateSubTask("Checking for conflicts on file: " + filePath);

                final IFile file = WorkspaceUtils.getFileFromAbsolutePath(filePath);

                final String fileName = file.getName();
                String modelName = modelNames.get(fileName);
                final String ontologyURI = ontologyURIs.get(fileName);

                final List<String> modelsNameClone = new ArrayList<>(modelNames.values());
                final List<String> ontologyURIsClone = new ArrayList<>(ontologyURIs.values());

                modelsNameClone.remove(modelName);
                ontologyURIsClone.remove(ontologyURI);

                boolean duplicateModelName = modelsNameClone.contains(modelName);
                boolean duplicateOntologyURI = ontologyURIsClone.contains(ontologyURI);
                // MIKE: If the map contains an entry
                final boolean duplicateFileName = originalFilenames.containsKey(fileName);

                if (duplicateOntologyURI) {

                    Display.getDefault().syncExec(new Runnable() {

                        @Override
                        public void run() {
                            boolean continueCopy = MessageDialog.openQuestion(shell, "Duplicate",
                                    "A model with the following Ontology URI already exists in the Models folder:"
                                            + "\n\n    " + ontologyURI
                                            + "\n\nDo you wish to continue with the copy?");
                            if (!continueCopy) {
                                try {
                                    file.delete(true, false, null);
                                }
                                catch (CoreException e) {
                                    e.printStackTrace();
                                    MessageDialog.openError(shell, "Error",
                                            "Error while deleting file: " + file);
                                }
                            }
                        }
                    });
                }

                // The file could have been deleted in case of duplicate
                // Ontology URI
                if (!file.exists()) {
                    updateSubTask("Deleting model: " + filePath);
                    monitor.worked(1);
                    continue;
                }

                if (duplicateModelName || duplicateFileName) {
                    updateSubTask("Conflicts found for model: " + filePath);

                    Display.getDefault().syncExec(new Runnable() {

                        @Override
                        public void run() {
                            ModelNameWizard wizard = new ModelNameWizard(file);
                            wizard.setOntologyURIs(ontologyURIsClone);
                            wizard.setModelNames(modelsNameClone);

                            if (duplicateFileName) {
                                // In this way the wizard will show the original
                                // file name making an error and forcing the
                                // user to change it
                                String originalFileName = originalFilenames.get(fileName);
                                wizard.suggestNewFileName(originalFileName);
                            }

                            WizardDialog dialog = new WizardDialog(shell, wizard);
                            dialog.setBlockOnOpen(true);

                            if (dialog.open() != Window.OK) {
                                updateSubTask("Deleting model with conflicts: " + filePath);
                                try {
                                    file.delete(true, false, null);
                                }
                                catch (CoreException e) {
                                    MessageDialog.openError(shell, "Error",
                                            "Error while deleting file: " + file);
                                }
                            }
                        }
                    });
                }
                monitor.worked(1);
            }
            monitor.done();
            stopMonitorUpdate();
            return Status.OK_STATUS;
        }
    }

    public static void copyModels(IProject project, List<String> modelPaths) {
        IFolder modelsDir = SemanticProjectUtils.getModelsFolder(project);
        Shell shell = CorePlugin.getActiveWorkbenchShell();

        final Map<String, String> originalFilenames = Maps.newHashMap();
        CopyAndMoveFilesOperation copyAndMoveFilesOperation = new CopyAndMoveFilesOperation(shell,
                CopyAndMoveFilesOperation.MODALITY_COPY);
        copyAndMoveFilesOperation.setForceRename(true);

        copyAndMoveFilesOperation.setRenamingStrategy(new DefaultRenameStrategy() {

            @Override
            public String rename(String oldFileName, IContainer folder) {
                String newFileName = super.rename(oldFileName, folder);
                originalFilenames.put(newFileName, oldFileName);
                return newFileName;
            }
        });

        copyAndMoveFilesOperation.copyFiles(modelPaths, modelsDir);

        try {
            modelsDir.refreshLocal(IResource.DEPTH_ONE, null);
        }
        catch (CoreException e) {
            logger.error(e.getMessage(), e);
        }

        CheckModelConflictsJob job = new CheckModelConflictsJob(shell, project,
                copyAndMoveFilesOperation.getCopiedFilePaths());
        job.setOriginalFilenameMap(originalFilenames);
        job.setUser(true);
        job.schedule();
    }
}