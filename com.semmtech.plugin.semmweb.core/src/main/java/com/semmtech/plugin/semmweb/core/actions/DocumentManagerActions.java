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


import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.part.FileEditorInput;

import com.google.common.collect.Lists;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.cache.CacheManager;
import com.semmtech.plugin.semmweb.core.dialog.WorkspaceOntologySpecDialog;
import com.semmtech.plugin.semmweb.core.forms.editor.OntologyFormEditor;
import com.semmtech.plugin.semmweb.core.internal.navigator.SemanticProjectManager;
import com.semmtech.plugin.semmweb.core.jena.ontology.WorkspaceDocumentManagerConfiguration;
import com.semmtech.plugin.semmweb.core.jena.ontology.WorkspaceOntologySpec;
import com.semmtech.plugin.semmweb.core.jobs.SemanticProjectBuildJob;
import com.semmtech.plugin.semmweb.core.navigator.IImport;
import com.semmtech.plugin.semmweb.core.navigator.IImportCollection;
import com.semmtech.plugin.semmweb.core.navigator.IModel;
import com.semmtech.plugin.semmweb.core.navigator.ISemanticElement;
import com.semmtech.plugin.semmweb.core.preferences.DocumentManagerPreference;
import com.semmtech.plugin.semmweb.core.util.ImportURLUtils;
import com.semmtech.ui.plugin.EclipseUIPlugin;


/**
 * 
 * @author Sander Stolk
 */
public class DocumentManagerActions {

    private static final Logger logger = Logger.getLogger(DocumentManagerActions.class);

    public static class ChangeAlternateLocation extends Action {
        protected final IProject project;
        protected final String uri;

        public ChangeAlternateLocation(IProject project, String uri) {
            this.project = project;
            this.uri = uri;
        }

        @Override
        public String getText() {
            return "Change alternate location";
        }

        @Override
        public void run() {
            DocumentManagerPreference preferences = DocumentManagerPreference.fromProject(project);
            WorkspaceOntologySpec originalSpec = preferences.getOntologySpec(uri);
            if (originalSpec == null) {
                originalSpec = new WorkspaceOntologySpec(uri);
            }

            // ask the user to input an alt url
            Shell shell = EclipseUIPlugin.getStandardDisplay().getActiveShell();

            String title = "Change alternate location";
            String message = "Please set the alternate location(s) here for the ontology with the selected URI.";
            WorkspaceOntologySpecDialog dialog = new WorkspaceOntologySpecDialog(shell, title,
                    message);
            dialog.setOntologySpec(originalSpec);
            dialog.setHideOntologyOptions(true);
            dialog.setEnableURI(false);
            dialog.setHidePrefix(true);
            dialog.setImportType(ImportURLUtils.guessImportType(uri, project));

            if (dialog.open() != Window.OK) {
                // if operation was cancelled, simply return
                return;
            }
            // get spec as set by the user
            WorkspaceOntologySpec newSpec = dialog.getOntologySpec();
            if (newSpec.equals(originalSpec)) {
                // alt locations haven't changed
                return;
            }

            WorkspaceDocumentManagerConfiguration config = preferences.getDocumentManagerConfig();
            config.addOntologySpec(newSpec);
            preferences.setDocumentManagerConfig(config);

            try {
                // remove the cache mapping if exists
                CacheManager.fromProject(project).removeFile(uri);
                preferences.save();

                SemanticProjectBuildJob rebuild = new SemanticProjectBuildJob(project);
                rebuild.setUser(false);
                rebuild.schedule();

                rebuild.addJobChangeListener(new JobChangeAdapter() {

                    private List<IModel> findAffectedModels() {
                        SemanticProjectManager manager = SemanticProjectManager
                                .getSemanticProjectManager(project);
                        List<IModel> modelsToCheck = Lists.newArrayList();
                        for (ISemanticElement element : manager.getModelCollection().getChildren()) {
                            if (!(element instanceof IModel)) {
                                continue;
                            }
                            IModel model = (IModel) element;
                            List<ISemanticElement> children = model
                                    .getChildrenByType(ISemanticElement.IMPORT_COLLECTION);
                            if (!children.isEmpty()) {
                                IImportCollection collection = (IImportCollection) children.get(0);
                                for (ISemanticElement child : collection
                                        .getChildrenByType(ISemanticElement.IMPORT)) {
                                    IImport immport = (IImport) child;
                                    if (immport.getURI().equals(uri) && model.hasWorkingCopy()) {
                                        modelsToCheck.add(model);
                                        break;
                                    }
                                }
                            }
                        }
                        return modelsToCheck;
                    }

                    private void notifyAffected(final IModel model) {
                        Display.getDefault().asyncExec(new Runnable() {

                            private OntologyFormEditor findOntologyFormEditor(boolean bringToTop) {
                                IResource resource = (IResource) model.getAdapter(IResource.class);
                                if (resource != null) {
                                    for (IWorkbenchPage page : CorePlugin.getDefault()
                                            .getWorkbench().getActiveWorkbenchWindow().getPages()) {
                                        FileEditorInput editorInput = new FileEditorInput(
                                                (IFile) resource);
                                        IEditorPart editor = page.findEditor(editorInput);
                                        if (editor instanceof OntologyFormEditor) {
                                            OntologyFormEditor formEditor = (OntologyFormEditor) editor;
                                            if (bringToTop) {
                                                page.bringToTop(formEditor);
                                            }
                                            return formEditor;
                                        }
                                    }
                                }
                                return null;
                            }

                            /**
                             * TODO: Maybe refactor the code below into a
                             * central location
                             */
                            @Override
                            public void run() {
                                Shell shell = Display.getDefault().getActiveShell();
                                OntologyFormEditor formEditor = findOntologyFormEditor(true);

                                if (formEditor != null) {
                                    if (MessageDialog
                                            .openQuestion(
                                                    shell,
                                                    "Import Location Changed",
                                                    "Location of one of the model's imports has changed. Relaod the model to propagate this change into this model?")) {
                                        formEditor.readModelFromEditor();
                                    }
                                }
                            }
                        });
                    }

                    @Override
                    public void done(IJobChangeEvent event) {
                        // TODO: Check which opened editors have a reference to
                        // the model. Maybe update this to the meta model that
                        // Simone is implementing

                        // If a model is open and depends on this import then
                        // ask user if he wants to re-open (close and open) it
                        // again in order for the changed location to be
                        // propagated into this model.

                        // First find all models which import the URI and have a
                        // working copy open
                        List<IModel> affectedModels = findAffectedModels();

                        if (affectedModels.size() > 0) {
                            // Ask user for each model if the model should be
                            // reloaded.
                            for (IModel model : affectedModels) {
                                notifyAffected(model);
                            }
                        }
                    }
                });
            }
            catch (IOException ex) {
                logger.error("Error saving the document manager preferences", ex);
            }
        }
    }
}
