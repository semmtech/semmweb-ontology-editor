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

package com.semmtech.plugin.semmweb.core.wizards;


import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.Wizard;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.CorePluginImages;
import com.semmtech.plugin.semmweb.core.actions.ModelFileAction;
import com.semmtech.plugin.semmweb.core.model.ModelChangesCollection;
import com.semmtech.plugin.semmweb.core.model.OntModelUtils;
import com.semmtech.plugin.semmweb.core.operations.ModelChangeOperation;
import com.semmtech.plugin.semmweb.core.operations.ModelOperation;
import com.semmtech.plugin.semmweb.core.sparql.OntologyLabelProviderPropertyFunction;


/**
 * 
 * @author Sander Stolk
 */
public class ModelNameWizard extends Wizard {

    private static Logger logger = Logger.getLogger(ModelNameWizard.class);

    private static final String WINDOW_TITLE = "Specify Model Names";

    protected final IFile modelFile;
    protected final RenameAction action;

    protected ModelNameWizardPage namePage;

    /*
     * These lists contains the values from already existing models in the
     * project folder. Used to avoid same uri/file/model name.
     */
    protected List<String> modelNames;
    protected List<String> fileNames;
    protected List<String> ontologyURIs;
    protected String suggestedFileName;

    public ModelNameWizard(IFile file) {
        setWindowTitle(WINDOW_TITLE);
        this.modelFile = file;
        this.action = new RenameAction(file);
        this.suggestedFileName = null;
    }

    @Override
    public void addPages() {
        ImageDescriptor banner = CorePlugin.getDefault().getImageDescriptor(
                CorePluginImages.IMG_BANNER_WIZARD_ONTOLOGY);

        namePage = new ModelNameWizardPage(modelFile, action.getModel());
        namePage.setImageDescriptor(banner);
        namePage.setFilenameEditable(canAlterFilename());
        namePage.setFileNames(fileNames);
        namePage.setOntologyURIs(ontologyURIs);
        namePage.setModelNames(modelNames);
        namePage.suggestNewFileName(suggestedFileName);

        addPage(namePage);

    }

    public boolean canAlterFilename() {
        return !action.modelIsOpenInEditor();
    }

    @Override
    public boolean performFinish() {
        action.run();
        return true;
    }

    private class RenameAction extends ModelFileAction {
        public RenameAction(IFile file) {
            this(file, true);
        }

        public RenameAction(IFile file, boolean noHistory) {
            super(file, false, noHistory);
        }

        public boolean modelIsOpenInEditor() {
            if (model == null) {
                obtainModel();
            }
            return (modelProvider != null);
        }

        @Override
        public void run() {
            super.run();
            performRenameFileOperation();
        }

        @Override
        protected ModelOperation getOperation(OntModel model) {
            ModelChangesCollection modelChanges = new ModelChangesCollection();

            if (Strings.isNullOrEmpty(namePage.getOntologyURI())) {
                return null;
            }

            Resource ontology = model.createResource(namePage.getOntologyURI());
            String newName = namePage.getOntologyName();

            if (!newName.equals(OntModelUtils.getName(model))) {
                List<Statement> removeStatements = Lists.newArrayList();
                removeStatements.addAll(OntologyLabelProviderPropertyFunction
                        .getAllNameStatements(ontology));
                modelChanges.remove(removeStatements);

                Statement ontologyStatement = model.createStatement(ontology, RDF.type,
                        OWL.Ontology);
                if (!model.contains(ontologyStatement)) {
                    modelChanges.add(ontologyStatement);
                }
                if (!Strings.isNullOrEmpty(newName)) {
                    // use RDFS.label as default property to set ontology name
                    Property nameProperty = RDFS.label;
                    String language = null;

                    if (removeStatements.size() == 1) {
                        // use the old property to set ontology name instead
                        Statement oldNameStatement = removeStatements.get(0);
                        nameProperty = oldNameStatement.getPredicate();
                        try {
                            language = oldNameStatement.getLanguage();
                        }
                        catch (Exception e) {
                            // no language was present
                        }
                    }

                    if (language == null) {
                        modelChanges.add(model.createStatement(ontology, nameProperty, newName));
                    }
                    else {
                        modelChanges.add(model.createStatement(ontology, nameProperty, newName,
                                language));
                    }
                }
            }
            return new ModelChangeOperation(modelChanges);
        }

        private void performRenameFileOperation() {
            String oldFilename = modelFile.getName();
            String newFilename = namePage.getFilename();

            if (canAlterFilename() && !newFilename.equals(oldFilename)) {
                IContainer fileContainer = modelFile.getParent();
                IFile newFile = fileContainer.getFile(new Path(newFilename));
                try {
                    modelFile.move(newFile.getFullPath(), true, false, null);
                }
                catch (Exception e) {
                    logger.error("Error while renaming fileName from " + oldFilename + " to "
                            + newFilename, e);
                }
            }
        }
    }

    /**
     * List of already existent file names
     */
    public void setFileNames(List<String> fileNames) {
        this.fileNames = fileNames;
    }

    /**
     * List of already existent ontology URIs
     */
    public void setOntologyURIs(List<String> ontologyURIs) {
        this.ontologyURIs = ontologyURIs;
    }

    /**
     * List of already existent model names
     */
    public void setModelNames(List<String> modelNames) {
        this.modelNames = modelNames;
    }

    public void suggestNewFileName(String newFileName) {
        this.suggestedFileName = newFileName;
    }
}
