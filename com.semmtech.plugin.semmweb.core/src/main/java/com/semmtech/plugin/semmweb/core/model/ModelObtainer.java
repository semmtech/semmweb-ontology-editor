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

package com.semmtech.plugin.semmweb.core.model;


import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;

import com.hp.hpl.jena.ontology.OntModel;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.jobs.ILoadModelErrorHandler;
import com.semmtech.plugin.semmweb.core.jobs.LoadModelJob;
import com.semmtech.plugin.semmweb.core.jobs.LoadModelJob.ModelMetaData;
import com.semmtech.plugin.semmweb.core.jobs.LoadModelJobAdapter;


/**
 * Calling run obtains the Model, the ModelMetadata and the ModelProvider (if
 * possible).
 * 
 * @author Sander Stolk
 */
public class ModelObtainer {
    private static Logger logger = Logger.getLogger(ModelObtainer.class);

    private IFile file;
    private boolean loadImports;
    private IModelProvider modelProvider;
    private OntModel model;
    private LoadModelJob.ModelMetaData modelMetaData;

    public ModelObtainer(IFile file, boolean loadImports) {
        this.file = file;
        this.loadImports = loadImports;
    }

    public void run() {
        modelProvider = CorePlugin.getDefault().getModelProvider(file);
        if (modelProvider != null) {
            model = modelProvider.getOntModel();
        }
        else {
            LoadModelJob job = new LoadModelJob(file, "Load Model from " + file.getName(), true,
                    !loadImports);
            job.setSystem(true);
            job.setErrorHandler(new ILoadModelErrorHandler() {
                @Override
                public void error(Stage stage, String message, Throwable exception) {
                    logger.warn(String.format("An error occurred while loading file %s: %s",
                            file.getName(), message));
                }
            });
            job.setListener(new LoadModelJobAdapter() {
                @Override
                public void modelLoaded(OntModel input, ModelMetaData metaData) {
                    model = input;
                    modelMetaData = metaData;
                }
            });
            job.schedule();
            try {
                job.join();
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public OntModel getModel() {
        return model;
    }

    public LoadModelJob.ModelMetaData getModelMetaData() {
        return modelMetaData;
    }

    public IModelProvider getModelProvider() {
        return modelProvider;
    }
}
