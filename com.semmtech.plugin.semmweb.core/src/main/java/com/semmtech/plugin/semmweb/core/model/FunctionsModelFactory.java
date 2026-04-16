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


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;

import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import com.hp.hpl.jena.ontology.OntDocumentManager;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ModelMaker;
import com.hp.hpl.jena.util.FileUtils;
import com.hp.hpl.jena.vocabulary.OWL;
import com.semmtech.plugin.semmweb.core.CorePlugin;


/**
 * 
 * @author Sander Stolk
 */
public class FunctionsModelFactory {
    protected static final String MODELS_PATH = "src/main/resources/models/";

    public static final String SEMM_OWL_FUNCTIONS_MODEL = "semmowl-f.ttl";

    public static final String[] DEFAULT_MODEL_SET = { SEMM_OWL_FUNCTIONS_MODEL };

    public static OntModel create() {
        return create(null);
    }

    public static OntModel create(String[] modelSet) {
        if (modelSet == null || modelSet.length == 0) {
            modelSet = DEFAULT_MODEL_SET;
        }

        OntDocumentManager ontDocumentManager = new OntDocumentManager();
        ontDocumentManager.setProcessImports(false);
        ModelMaker modelMaker = ModelFactory.createMemModelMaker();
        OntModelSpec spec = new OntModelSpec(modelMaker, ontDocumentManager, null,
                OWL.FULL_LANG.getURI());

        OntModel functionsModel = ModelFactory.createOntologyModel(spec);
        String symbolicName = CorePlugin.PLUGIN_ID;
        Bundle bundle = Platform.getBundle(symbolicName);
        if (bundle != null) {
            for (String modelName : modelSet) {
                OntModel subModel = ModelFactory.createOntologyModel(spec);
                Enumeration<URL> urls = bundle.findEntries(MODELS_PATH, modelName, true);
                while (urls != null && urls.hasMoreElements()) {
                    URL url = urls.nextElement();
                    try (InputStreamReader isr = new InputStreamReader(url.openStream());) {
                        subModel.read(isr, null, FileUtils.langTurtle);
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                functionsModel.add(subModel);
            }
        }
        else {
            for (String modelName : modelSet) {
                File file = new File(MODELS_PATH + modelName);
                if (file.exists()) {
                    OntModel subModel = ModelFactory.createOntologyModel(spec);
                    try (FileInputStream fis = new FileInputStream(file);) {
                        subModel.read(fis, null, FileUtils.langTurtle);
                        functionsModel.add(subModel);
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return functionsModel;
    }
}
