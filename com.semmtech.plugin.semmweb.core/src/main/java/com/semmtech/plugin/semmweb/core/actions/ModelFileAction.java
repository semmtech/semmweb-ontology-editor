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


import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.semmtech.plugin.semmweb.core.io.ModelIOUtils;
import com.semmtech.plugin.semmweb.core.jobs.LoadModelJob;
import com.semmtech.plugin.semmweb.core.model.IModelProvider;
import com.semmtech.plugin.semmweb.core.model.ModelObtainer;
import com.semmtech.plugin.semmweb.core.operations.ModelOperation;


/**
 * 
 * @author Sander Stolk
 */
public class ModelFileAction extends ConditionalAction {

    private static Logger logger = Logger.getLogger(ModelFileAction.class);

    protected final IFile file;
    protected final boolean requiresImports;
    protected final boolean noHistory;

    protected IModelProvider modelProvider;
    protected OntModel model;
    protected LoadModelJob.ModelMetaData modelMetaData;

    public ModelFileAction(IFile file, boolean requiresImports, boolean noHistory) {
        super();
        this.file = file;
        this.requiresImports = requiresImports;
        this.noHistory = noHistory;
    }

    @Override
    public void run() {
        if (!satisfiesRunConditions()) {
            return;
        }

        obtainModel();
        if (model == null) {
            return;
        }

        ModelOperation operation = getOperation(model);
        execute(operation);
    }

    protected ModelOperation getOperation(OntModel model) {
        return null;
    }

    protected void execute(ModelOperation operation) {
        if (operation == null) {
            return;
        }
        if (modelProvider == null && model == null) {
            obtainModel();
        }
        if (modelProvider == null && model == null) {
            return;
        }

        if (modelProvider != null) {
            if (!noHistory) {
                operation.setModel(modelProvider);
                modelProvider.performUndoRedoOperation(operation);
            }
            else {
                operation.execute(modelProvider.getOntModel());
            }
        }
        else if (model != null) {
            operation.setModel(model);
            operation.execute(model);

            saveModel();
        }
    }

    protected void saveModel() {
        if (model == null) {
            return;
        }

        Model baseModel = model.getBaseModel();
        try {
            String serializationLang = modelMetaData.serializationLanguage;
            String baseUri = modelMetaData.baseUri;
            String encoding = modelMetaData.encoding;
            ModelIOUtils.writeModel(baseModel, file, serializationLang, baseUri, encoding, null);
            file.getParent().refreshLocal(1, null);
        }
        catch (Exception ex) {
            logger.error("Error during saving of model", ex);
        }
    }

    protected void obtainModel() {
        if (model == null) {
            ModelObtainer modelObtainer = new ModelObtainer(file, requiresImports);
            modelObtainer.run();
            modelProvider = modelObtainer.getModelProvider();
            model = modelObtainer.getModel();
            modelMetaData = modelObtainer.getModelMetaData();
        }
    }

    public IFile getFile() {
        return file;
    }

    public OntModel getModel() {
        if (model != null) {
            return model;
        }
        obtainModel();
        return model;
    }
}
