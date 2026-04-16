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
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.vocabulary.OWL;
import com.semmtech.plugin.semmweb.core.actions.runconditions.OntologyRunCondition;
import com.semmtech.plugin.semmweb.core.dialog.ResourceSelectionDialog;
import com.semmtech.plugin.semmweb.core.dialog.WorkspaceOntologySpecDialog;
import com.semmtech.plugin.semmweb.core.forms.editor.OntologyFormEditor;
import com.semmtech.plugin.semmweb.core.jena.ontology.WorkspaceDocumentManagerConfiguration;
import com.semmtech.plugin.semmweb.core.jena.ontology.WorkspaceOntologySpec;
import com.semmtech.plugin.semmweb.core.model.IModelProvider;
import com.semmtech.plugin.semmweb.core.model.ModelObtainer;
import com.semmtech.plugin.semmweb.core.navigator.IModel;
import com.semmtech.plugin.semmweb.core.operations.AddImportOperation;
import com.semmtech.plugin.semmweb.core.operations.ModelOperation;
import com.semmtech.plugin.semmweb.core.operations.RemoveImportOperation;
import com.semmtech.plugin.semmweb.core.preferences.DocumentManagerPreference;
import com.semmtech.plugin.semmweb.core.util.SemanticProjectUtils;
import com.semmtech.semantics.sparql.QueryBuilder;
import com.semmtech.ui.plugin.EclipseUIPlugin;


/**
 * 
 * @author Sander Stolk
 */
public class ImportActions {

    private static final Logger logger = Logger.getLogger(ImportActions.class);

    public static class AddImport extends ModelFileAction {
        protected final Resource sourceOntology;
        protected final Resource importOntology;
        protected final IModel model;

        /**
         * See description of the other constructor. This constructor sets the
         * argument <code>noHistory</code> to false.
         */
        public AddImport(IFile file, Resource sourceOntology, Resource importOntology) {
            this(file, null, sourceOntology, importOntology, false);
        }

        /**
         * <p>
         * The argument <code>sourceOntology</code> may be null, which will
         * cause a source ontology to be selected (possibly through additional
         * input of the user). In such a situation, the following three
         * scenarios are possible:
         * <ol>
         * <li>No ontology has been defined in the base model. The user is
         * prompted to create a new ontology. The import will be added to that
         * ontology.</li>
         * <li>One ontology has been defined in the base model. The import will
         * be added to that ontology.</li>
         * <li>Multiple ontologies have been defined in the base model. The user
         * is prompted to selection the desired source ontology from the list of
         * existing ones. The import will be added to that ontology.</li>
         * </ol>
         * </p>
         * <p>
         * The argument <code>importOntology</code> may be null, which will
         * cause a dialog to show up to set the import ontology.
         * </p>
         */
        public AddImport(IFile file, IModel model, Resource sourceOntology,
                Resource importOntology, boolean noHistory) {
            super(file, false, noHistory);
            this.sourceOntology = sourceOntology;
            this.importOntology = importOntology;
            this.model = model;
            addRunCondition(new OntologyRunCondition(OntologyRunCondition.ALLOW_SINGLE
                    | OntologyRunCondition.ALLOW_MULTIPLE, true));
        }

        @Override
        protected ModelOperation getOperation(OntModel ontModel) {
            List<WorkspaceOntologySpec> knownSpecs = null;

            IProject project = file.getProject();
            knownSpecs = SemanticProjectUtils.getKnownSpecs(project);

            return getAddImportOperation(ontModel, sourceOntology, importOntology, knownSpecs,
                    project, model);
        }
    }

    public static class RemoveImport extends ModelFileAction {
        protected final Resource sourceOntology;
        protected final Resource importOntology;

        public RemoveImport(IFile file, Resource sourceOntology, Resource importOntology) {
            this(file, sourceOntology, importOntology, false);
        }

        public RemoveImport(IFile file, Resource sourceOntology, Resource importOntology,
                boolean noHistory) {
            super(file, false, noHistory);
            this.sourceOntology = sourceOntology;
            this.importOntology = importOntology;
        }

        @Override
        protected ModelOperation getOperation(OntModel model) {
            return getRemoveImportOperation(model, sourceOntology, importOntology);
        }
    }

    public static class DraggedModelsImport extends ModelFileAction {

        private List<IFile> sourceFiles;
        private List<String> sourceURIs;

        public DraggedModelsImport(IFile destFile, List<IFile> sourceFiles, List<String> sourceURIs) {
            super(destFile, false, false);
            this.sourceFiles = sourceFiles;
            this.sourceURIs = sourceURIs;
            OntologyRunCondition runCondition = new OntologyRunCondition(
                    OntologyRunCondition.ALLOW_SINGLE | OntologyRunCondition.ALLOW_MULTIPLE, true);
            String title = "The model that should add the imports contains no ontology";
            String message = "The model that should import the dragged models does not contain an ontology. To import, however, an ontology is required.";
            runCondition.setNoOntologiesText(title, message);
            addRunCondition(runCondition);
        }

        @Override
        protected ModelOperation getOperation(OntModel destModel) {
            final List<ModelOperation> importOperations = Lists.newArrayList();

            if (destModel == null) {
                return null;
            }

            String destOntologyPrefix = null;
            Resource destOntology = null;

            OntologySelector destOntologySelector = new OntologySelector(destModel);

            String title = "The model to perform the import contains multiple ontologies";
            String message = "The model you wish to perform the import contains multiple ontologies. As each of these ontologies could perform the import, we ask you to select the one you would like to perform the import from the list below.";
            destOntologySelector.setMultipleOntologiesText(title, message);

            destOntologySelector.selectOntology();
            destOntology = destOntologySelector.getOntology();
            destOntologyPrefix = destOntologySelector.getOntologyPrefix();

            if (destOntology == null) {
                return null;
            }

            for (IFile sourceFile : sourceFiles) {
                ModelOperation operation = getDraggedImportOperation(destOntology,
                        destOntologyPrefix, sourceFile);

                if (operation != null) {
                    importOperations.add(operation);
                }
            }

            for (String sourceURI : sourceURIs) {
                if (sourceURI != null) {
                    ModelOperation operation = new AddImportOperation(destOntology.getURI(),
                            destOntologyPrefix, sourceURI, null);

                    importOperations.add(operation);
                }
            }

            // Execute all importsAction inside a single ModelOperation
            return new ModelOperation("Add imports") {

                @Override
                public boolean undo(OntModel model) {
                    boolean res = true;
                    for (ModelOperation op : importOperations) {
                        res &= op.undo(model);
                    }
                    return res;
                }

                @Override
                public boolean execute(OntModel model) {
                    boolean res = true;
                    for (ModelOperation op : importOperations) {
                        res &= op.execute(model);
                    }
                    return res;
                }
            };
        }
    }

    /**
     * <p>
     * The argument <code>sourceOntology</code> may be null, which will cause a
     * source ontology to be selected (possibly through additional input of the
     * user). In such a situation, the following three scenarios are possible:
     * <ol>
     * <li>No ontology has been defined in the base model. The user is prompted
     * to create a new ontology. The import will be added to that ontology.</li>
     * <li>One ontology has been defined in the base model. The import will be
     * added to that ontology.</li>
     * <li>Multiple ontologies have been defined in the base model. The user is
     * prompted to selection the desired source ontology from the list of
     * existing ones. The import will be added to that ontology.</li>
     * </ol>
     * </p>
     * <p>
     * The argument <code>importOntology</code> may be null, which will cause a
     * dialog to show up to set the import ontology.
     * </p>
     */
    public static void addImport(IModelProvider modelProvider, Resource sourceOntology,
            Resource importOntology) {
        if (modelProvider == null) {
            return;
        }

        IProject project = null;
        List<WorkspaceOntologySpec> knownSpecs = null;
        if (modelProvider instanceof OntologyFormEditor) {
            OntologyFormEditor editor = (OntologyFormEditor) modelProvider;
            project = editor.getProject();
            if (project != null) {
                knownSpecs = SemanticProjectUtils.getKnownSpecs(project);
            }
        }

        OntModel ontModel = modelProvider.getOntModel();
        if (ontModel != null) {
            AddImportOperation operation = getAddImportOperation(ontModel, sourceOntology,
                    importOntology, knownSpecs, project, null);
            if (operation != null) {
                operation.setModel(modelProvider);
                modelProvider.performUndoRedoOperation(operation);
            }
        }
    }

    private static boolean addSpecToDocumentManager(WorkspaceOntologySpec spec, IProject project) {
        if (project != null) {
            DocumentManagerPreference preferences = DocumentManagerPreference.fromProject(project);
            if (preferences != null) {
                WorkspaceDocumentManagerConfiguration configuration = preferences
                        .getDocumentManagerConfig();
                if (configuration != null) {

                    configuration.addOntologySpec(spec);
                    preferences.setDocumentManagerConfig(configuration);
                    try {
                        preferences.save();
                        return true;
                    }
                    catch (IOException ex) {
                        logger.error(
                                "Error saving the document manager preferences, see inner exception",
                                ex);
                    }
                }
            }
        }
        return false;
    }

    /**
     * <p>
     * The argument <code>sourceOntology</code> may be null, which will cause a
     * source ontology to be selected (possibly through additional input of the
     * user).
     * </p>
     * <p>
     * The argument <code>importOntology</code> may be null, which will cause a
     * dialog to show up to set the import ontology.
     * </p>
     */
    private static AddImportOperation getAddImportOperation(OntModel ontModel,
            Resource sourceOntology, Resource importOntology,
            List<WorkspaceOntologySpec> knownSpecs, IProject project, IModel model) {
        if (ontModel == null) {
            return null;
        }

        List<WorkspaceOntologySpec> knownSpecOptions = Lists.newArrayList();
        if (knownSpecs != null) {
            knownSpecOptions.addAll(knownSpecs);
            List<String> importedURIs = getImportedURIs(ontModel.getBaseModel());
            List<WorkspaceOntologySpec> redundantSpecs = Lists.newArrayList();
            for (WorkspaceOntologySpec spec : knownSpecs) {
                if (spec != null && !Strings.isNullOrEmpty(spec.getPublicURI())) {
                    if (importedURIs.contains(spec.getPublicURI())) {
                        redundantSpecs.add(spec);
                    }
                }
            }
            knownSpecOptions.removeAll(redundantSpecs);
            if (knownSpecOptions.isEmpty()) {
                knownSpecOptions = null;
            }
        }

        String sourceOntologyPrefix = null;

        if (sourceOntology == null) {

            String multipleOntologiesTitle = "Ontology that should import";
            String multipleOntologiesMessage = "Please select the ontology you would like to perform the import.";

            OntologySelector sourceOntologySelector = new OntologySelector(ontModel);
            sourceOntologySelector.setMultipleOntologiesText(multipleOntologiesTitle,
                    multipleOntologiesMessage);
            sourceOntologySelector.selectOntology();
            sourceOntology = sourceOntologySelector.getOntology();
            sourceOntologyPrefix = sourceOntologySelector.getOntologyPrefix();

            if (sourceOntology == null) {
                return null;
            }
        }

        if (importOntology != null) {
            String uri = importOntology.getURI();
            return new AddImportOperation(sourceOntology, uri, null);
        }

        Shell shell = EclipseUIPlugin.getStandardDisplay().getActiveShell();

        String title = "Add Import";
        String message = "Please enter the details of the ontology that needs to be imported.";
        WorkspaceOntologySpecDialog dialog = new WorkspaceOntologySpecDialog(shell, title, message);

        if (knownSpecOptions != null) {
            dialog.setPossibleOptions(knownSpecOptions, model, false);
        }

        if (dialog.open() == Window.OK) {
            String prefix = dialog.getPrefix();
            String uri = dialog.getPublicURI();
            if (Strings.isNullOrEmpty(uri)) {
                return null;
            }

            if (!Strings.isNullOrEmpty(dialog.getAltURL()) && project != null) {
                addSpecToDocumentManager(dialog.getOntologySpec(), project);
            }

            if (!Strings.isNullOrEmpty(sourceOntologyPrefix)) {
                return new AddImportOperation(sourceOntology.getURI(), sourceOntologyPrefix, uri,
                        prefix);
            }
            return new AddImportOperation(sourceOntology, uri, prefix);
        }

        return null;
    }

    private static AddImportOperation getDraggedImportOperation(Resource destOntology,
            String destOntologyPrefix, IFile sourceFile) {

        if (destOntology == null || sourceFile == null) {
            return null;
        }

        ModelObtainer modelObtainer = new ModelObtainer(sourceFile, false);
        modelObtainer.run();
        OntModel sourceModel = modelObtainer.getModel();

        Resource sourceOntology = null;
        String sourceOntologyPrefix = null;

        // Get the ontology that is to act as import
        OntologySelector sourceOntologySelector = new OntologySelector(sourceModel);

        String title = "The model to act as import does not contain an ontology";
        String message = "The model you wish to import does not contain an ontology. As an import is a reference to an ontology, the import of the present model cannot be achieved.";
        sourceOntologySelector.setNoOntologiesText(title, message);

        title = "The model to act as import contains multiple ontologies";
        message = "The model you wish to import contains multiple ontologies. As each of these ontologies could act as import, we ask you to select the one you would like to act as import from the list below.";
        sourceOntologySelector.setMultipleOntologiesText(title, message);

        sourceOntologySelector.selectOntology();
        sourceOntology = sourceOntologySelector.getOntology();
        sourceOntologyPrefix = sourceOntologySelector.getOntologyPrefix();

        if (sourceOntology == null) {
            return null;
        }

        AddImportOperation operation = new AddImportOperation(destOntology.getURI(),
                destOntologyPrefix, sourceOntology.getURI(), sourceOntologyPrefix);
        operation.setAlternateFile(sourceFile);
        return operation;
    }

    private static List<String> getImportedURIs(Model model) {
        List<String> importURIs = Lists.newArrayList();
        Var varOntology = Var.alloc("ontology");
        Var varImport = Var.alloc("import");
        QueryBuilder qb = QueryBuilder.createSelect(true)
                .addTriplePattern(varOntology, OWL.imports, varImport).addFilterIsURI(varImport)
                .addOrderBy(varImport, Query.ORDER_ASCENDING).addResultVar(varImport);
        for (ResultSet iter = qb.execSelect(model); iter.hasNext();) {
            importURIs.add(iter.next().getResource(varImport.getName()).getURI());
        }
        return importURIs;
    }

    public static void removeImport(IModelProvider modelProvider, Resource sourceOntology,
            Resource importOntology) {
        if (modelProvider != null) {
            OntModel model = modelProvider.getOntModel();
            if (model != null) {
                RemoveImportOperation operation = getRemoveImportOperation(model, sourceOntology,
                        importOntology);
                if (operation != null) {
                    operation.setModel(modelProvider);
                    modelProvider.performUndoRedoOperation(operation);
                }
            }
        }
    }

    private static RemoveImportOperation getRemoveImportOperation(OntModel model,
            Resource sourceOntology, Resource importOntology) {
        if (importOntology == null) {
            return null;
        }

        if (sourceOntology == null) {
            Shell shell = EclipseUIPlugin.getStandardDisplay().getActiveShell();

            // TODO refactor using OntologySelector
            List<Resource> ontologies = getOntologiesThatImport(importOntology,
                    model.getBaseModel());
            if (ontologies.isEmpty()) {
                // information dialog stating that import can't be removed
                String title = "No ontologies to remove import from";
                String message = "There are no ontologies in the model from which the specified import can be removed. Any imports performed by ontologies defined in other, imported models can't be removed in this model, which seems to be the case for the import you wish to remove.";
                MessageDialog.openInformation(shell, title, message);
            }
            else if (ontologies.size() == 1) {
                sourceOntology = ontologies.get(0);
            }
            else {
                // allow the user to select one of the ontologies
                String title = "Ontology to remove import from";
                String message = "Please select the ontology you would like to remove the import from.";
                ResourceSelectionDialog dialog = new ResourceSelectionDialog(shell, title, message);
                dialog.setModel(model);
                dialog.setAllowedResourceTypes(new Resource[0]);
                dialog.setResources(ontologies);
                dialog.setHierarchicalViewDisabled(true);
                dialog.setMultiSelectAllowed(false);
                if (dialog.open() == Window.OK) {
                    sourceOntology = dialog.getFirstSelectedResource();
                }
            }
            if (sourceOntology == null) {
                return null;
            }
        }

        return new RemoveImportOperation(sourceOntology, importOntology);
    }

    private static List<Resource> getOntologiesThatImport(Resource immport, Model model) {
        List<Resource> ontologies = Lists.newArrayList();
        Var varOntology = Var.alloc("ontology");
        QueryBuilder qb = QueryBuilder.createSelect(true)
                .addTriplePattern(varOntology, OWL.imports, immport).addFilterIsURI(varOntology)
                .addOrderBy(varOntology, Query.ORDER_ASCENDING);
        for (ResultSet iter = qb.execSelect(model); iter.hasNext();) {
            ontologies.add(iter.next().getResource(varOntology.getName()));
        }
        return ontologies;
    }
}
