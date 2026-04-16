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

package com.semmtech.plugin.semmweb.validation.models;


import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import com.hp.hpl.jena.ontology.OntDocumentManager;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ModelMaker;
import com.hp.hpl.jena.util.FileUtils;
import com.hp.hpl.jena.vocabulary.OWL;
import com.semmtech.cryptography.CryptoException;
import com.semmtech.cryptography.CryptoStream;
import com.semmtech.plugin.semmweb.core.jobs.LoadModelJob;
import com.semmtech.plugin.semmweb.core.jobs.LoadModelJobAdapter;
import com.semmtech.plugin.semmweb.validation.ValidationPlugin;


/**
 * 
 * @author Sander Stolk
 */
public class ConstraintsModelFactory {

    private static Logger logger = Logger.getLogger(ConstraintsModelFactory.class);

    protected static final String MODELS_PATH = "src/main/resources/models/";

    public static final String SPIN_RDFS_MODEL = "spinrdfs.ttl";
    public static final String SPIN_OWL_MODEL = "spinowl.ttl";
    public static final String SPIN_OWL_RL_MODEL = "spinowlrl.ttl";
    public static final String SPIN_OWL_RL_ALL_MODEL = "spinowlrl-all.ttl";

    public static final String SEMM_RDFS_MODEL = "semmrdfs.ttl";
    public static final String SEMM_OWL_MODEL = "semmowl.ttl";

    public static final String[] DEFAULT_MODEL_SET = { SPIN_RDFS_MODEL, SPIN_OWL_MODEL,
            SPIN_OWL_RL_MODEL, SPIN_OWL_RL_ALL_MODEL, SEMM_RDFS_MODEL, SEMM_OWL_MODEL };

    public static OntModel create() {
        return create(null, null);
    }

    public static OntModel create(String[] modelSet, IFile[] additionalModels) {
        if (modelSet == null || modelSet.length == 0) {
            modelSet = DEFAULT_MODEL_SET;
        }

        OntDocumentManager ontDocumentManager = new OntDocumentManager();
        ontDocumentManager.setProcessImports(false);
        ModelMaker modelMaker = ModelFactory.createMemModelMaker();
        OntModelSpec spec = new OntModelSpec(modelMaker, ontDocumentManager, null,
                OWL.FULL_LANG.getURI());

        final OntModel constraintsModel = ModelFactory.createOntologyModel(spec);
        String symbolicName = ValidationPlugin.PLUGIN_ID;
        Bundle bundle = Platform.getBundle(symbolicName);

        for (String constraintsModelName : modelSet) {
            OntModel subModel = ModelFactory.createOntologyModel(spec);

            /*
             * During the development of the environment both the .ttl and
             * .ttl.enc models are available. We first check for the existence
             * of the .ttl files (normal file name as defined in the constants)
             * if they exist then we are in development mode and the decrypted
             * version is used. If they don't exists it means that we are in
             * production mode and only the encrypted files are available.
             */
            Enumeration<URL> urls = bundle.findEntries(MODELS_PATH, constraintsModelName, true);

            if (urls == null) {
                bundle.findEntries(MODELS_PATH, constraintsModelName + ".enc", true);
            }

            while (urls != null && urls.hasMoreElements()) {
                URL url = urls.nextElement();

                if (constraintsModelName.endsWith(".enc")) {
                    CryptoStream crypto = new CryptoStream(constraintsModelName.replaceAll(".enc",
                            ""));
                    try (InputStream is = crypto.wrapInputStream(url.openStream());) {
                        subModel.read(is, null, FileUtils.langTurtle);
                    }
                    catch (IOException e) {
                        logger.error("An I/O error occurred while reading the encrypted model: "
                                + constraintsModelName, e);
                    }
                    catch (CryptoException e1) {
                        logger.error("A cryptography error occurred while decrypting the model: "
                                + constraintsModelName, e1);
                    }
                }
                else {
                    try (InputStreamReader isr = new InputStreamReader(url.openStream());) {
                        subModel.read(isr, null, FileUtils.langTurtle);
                    }
                    catch (IOException e) {
                        logger.error("An I/O error occurred while reading the model: "
                                + constraintsModelName, e);
                    }
                }
            }
            constraintsModel.add(subModel);
        }

        if (additionalModels != null) {
            for (IFile additionalModel : additionalModels) {
                try {
                    LoadModelJob loadJob = new LoadModelJob(additionalModel,
                            "Loading additional spin model", true, true);
                    loadJob.setListener(new LoadModelJobAdapter() {
                        @Override
                        public void modelLoaded(OntModel model) {
                            if (model != null) {
                                constraintsModel.add(model);
                            }
                        }
                    });
                    loadJob.schedule();
                    loadJob.join();
                }
                catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }

        return constraintsModel;
    }
}
