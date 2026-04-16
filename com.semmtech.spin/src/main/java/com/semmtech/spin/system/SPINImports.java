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

package com.semmtech.spin.system;


import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.topbraid.spin.arq.EvalFunction;
import org.topbraid.spin.arq.PropertyChainHelperPFunction;
import org.topbraid.spin.system.SPINModuleRegistry;
import org.topbraid.spin.util.JenaUtil;
import org.topbraid.spin.vocabulary.SP;
import org.topbraid.spin.vocabulary.SPIN;
import org.topbraid.spin.vocabulary.SPL;

import com.google.common.collect.Lists;
import com.hp.hpl.jena.ontology.OntDocumentManager;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ModelMaker;
import com.hp.hpl.jena.sparql.function.FunctionRegistry;
import com.hp.hpl.jena.sparql.pfunction.PropertyFunctionRegistry;
import com.hp.hpl.jena.util.FileUtils;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.semmtech.spin.Activator;
import com.semmtech.spin.vocabulary.SPIF;


/**
 * 
 * @author Sander Stolk
 */
public class SPINImports {
    private static final String MODELS_FOLDER = "src/main/resources/models/";

    private static final String[] requiredSystemImports = new String[] { SP.BASE_URI,
            SPIN.BASE_URI, SPL.BASE_URI, SPIF.BASE_URI };

    public static OntModel createSPINModel() {
        return createSPINModel(null);
    }

    public static OntModel createSPINModel(Model inputModel) {
        List<String> missingSystemImports = Lists.newArrayList();
        for (String requiredSystemImport : requiredSystemImports) {
            if (inputModel == null || !containsSystemImport(inputModel, requiredSystemImport)) {
                missingSystemImports.add(requiredSystemImport);
            }
        }

        OntModel result = null;

        if (missingSystemImports.isEmpty() && inputModel instanceof OntModel) {
            // No need to add extra sub models to the inputModel
            result = (OntModel) inputModel;
        }
        else {
            // Create a new spec that does not automatically process imports
            OntDocumentManager ontDocumentManager = new OntDocumentManager();
            ontDocumentManager.setProcessImports(false);
            ModelMaker modelMaker = ModelFactory.createMemModelMaker();
            OntModelSpec spec = new OntModelSpec(modelMaker, ontDocumentManager, null,
                    OWL.FULL_LANG.getURI());

            // Create a new model wrapping the constraintsModel
            result = ModelFactory.createOntologyModel(spec);
            if (inputModel != null) {
                result.addSubModel(inputModel);
            }

            // Ensure SP, SPIN, and SPL are all included in the spinModel
            for (String missingSystemImport : missingSystemImports) {
                ensureImported(missingSystemImport, result);
            }
        }

        // Register all functions and templates in the spinModel
        SPINModuleRegistry.get().reset();
        SPINModuleRegistry.get().registerAll(result, null);

        // Normally the following is part of SPINModuleRegistry.get().init(),
        // but that function will also load in SPL and SPIN models again.
        FunctionRegistry.get().put(SPIN.eval.getURI(), new EvalFunction());
        PropertyFunctionRegistry.get().put("http://topbraid.org/spin/owlrl#propertyChainHelper",
                PropertyChainHelperPFunction.class);

        return result;
    }

    private static boolean containsSystemImport(Model model, String importURI) {
        return model.contains(model.createResource(importURI), RDF.type, OWL.Ontology);
    }

    private static void ensureImported(String baseURI, OntModel model) {
        if (!containsSystemImport(model, baseURI)) {
            Model importModel = SPINImports.getModel(baseURI);
            if (importModel != null) {
                model.addSubModel(importModel);
            }
        }
    }

    /**
     * Can return the SP, SPL, SPIN, or SPIF model on request. If another model
     * was requested or the model could not be read, null is returned.
     */
    public static Model getModel(String baseURI) {
        String filename = getResourceFilename(baseURI);
        if (filename == null || filename.isEmpty()) {
            return null;
        }

        URL url = SPINImports.class.getClassLoader().getResource(MODELS_FOLDER + filename);
        if (url == null) {
            /*
             * The following code exists only for Unit Tests that are run from
             * other projects. As the class loader will in such cases exist in
             * the other project, the only way to obtain the models correctly is
             * to try to obtain the File by moving out of the one project and
             * into the SPIN project.
             */
            try {
                url = new File("../" + Activator.BUNDLE_ID + "/" + MODELS_FOLDER + filename)
                        .toURI().toURL();
            }
            catch (MalformedURLException e) {
                e.printStackTrace();
            }
            if (url == null) {
                return null;
            }
        }

        Model model = JenaUtil.createDefaultModel();
        try (InputStreamReader isr = new InputStreamReader(url.openStream());) {
            model.read(isr, null, FileUtils.langTurtle);
            return model;
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String getResourceFilename(String baseURI) {
        if (baseURI == null) {
            return null;
        }

        if (baseURI.equals(SP.BASE_URI)) {
            return "sp.ttl";
        }
        if (baseURI.equals(SPL.BASE_URI)) {
            return "spl.ttl";
        }
        if (baseURI.equals(SPIN.BASE_URI)) {
            return "spin.ttl";
        }
        if (baseURI.equals(SPIF.BASE_URI)) {
            return "spif.ttl";
        }

        return null;
    }
}
