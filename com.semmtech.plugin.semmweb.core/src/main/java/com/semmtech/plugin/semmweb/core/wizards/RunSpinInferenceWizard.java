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


import java.io.IOException;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.topbraid.spin.inference.SPINInferences;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.hp.hpl.jena.ontology.OntDocumentManager;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ModelMaker;
import com.hp.hpl.jena.util.FileUtils;
import com.hp.hpl.jena.vocabulary.OWL;
import com.semmtech.plugin.semmweb.core.io.ModelIOUtils;
import com.semmtech.plugin.semmweb.core.jobs.LoadModelJob;
import com.semmtech.plugin.semmweb.core.jobs.LoadModelJobAdapter;
import com.semmtech.plugin.semmweb.core.util.SemanticProjectUtils;
import com.semmtech.spin.system.SPINImports;


/**
 * 
 * @author Sander Stolk
 */
public class RunSpinInferenceWizard extends SemmtechWizard {
    protected RunSpinInferenceWizardPage modelsPage;

    @Override
    public void addPages() {
        super.addPages();

        modelsPage = new RunSpinInferenceWizardPage();
        addPage(modelsPage);
    }

    @Override
    public boolean performFinish() {
        // create combined input model
        List<OntModel> models = loadModels(modelsPage.getInputModelFiles());
        if (models.isEmpty()) {
            return true;
        }
        OntModel queryModel = combineModels(models);

        // create output model
        OntModel resultsModel = createEmptyModel();

        // create spin model and run its spin rules on it
        OntModel spinModel = SPINImports.createSPINModel(queryModel);

        // adding resultsModel as submodel to spinModel
        spinModel.addSubModel(resultsModel);

        boolean singlePass = true;
        SPINInferences.run(spinModel, resultsModel, null, null, singlePass, null);

        // write output model to filesystem
        String outputName = modelsPage.getOutputModelName();
        IProject outputProject = modelsPage.getOutputModelProject();
        IFile outputFile = outputProject.getFile(outputName);
        IFolder modelsFolder = SemanticProjectUtils.getModelsFolder(outputProject);
        if (modelsFolder != null) {
            // if a models folder is available, we put the file in there.
            outputFile = modelsFolder.getFile(outputName);
        }
        String lang = FileUtils.guessLang(outputName, FileUtils.langTurtle);
        String charsetName = Charsets.UTF_8.name();

        try {
            ModelIOUtils.writeModel(resultsModel, outputFile, lang, null, charsetName, null);
        }
        catch (CoreException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    protected List<OntModel> loadModels(List<IFile> files) {
        final List<OntModel> models = Lists.newArrayList();
        for (IFile file : files) {
            LoadModelJob loadModelJob = new LoadModelJob(file, file.toString(), false, false);
            loadModelJob.setListener(new LoadModelJobAdapter() {
                @Override
                public void modelLoaded(OntModel model) {
                    models.add(model);
                    // System.out.println("========= Loaded input model ==========");
                    // System.out.println(model);
                }
            });
            loadModelJob.schedule();
            try {
                loadModelJob.join();
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return models;
    }

    protected OntModel combineModels(List<? extends Model> models) {
        OntModel unionModel = createEmptyModel();
        for (Model model : models) {
            unionModel.addSubModel(model);
        }
        // System.out.println("========= Created combined model for all input models ==========");
        // System.out.println(unionModel);
        return unionModel;
    }

    protected OntModel createEmptyModel() {
        // Create a new spec that does not automatically process imports
        OntDocumentManager ontDocumentManager = new OntDocumentManager();
        ontDocumentManager.setProcessImports(false);
        ModelMaker modelMaker = ModelFactory.createMemModelMaker();
        OntModelSpec spec = new OntModelSpec(modelMaker, ontDocumentManager, null,
                OWL.FULL_LANG.getURI());

        // Create a new model according to the spec
        return ModelFactory.createOntologyModel(spec);
    }

}
