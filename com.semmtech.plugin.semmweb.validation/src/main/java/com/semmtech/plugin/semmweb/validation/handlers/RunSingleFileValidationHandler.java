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

package com.semmtech.plugin.semmweb.validation.handlers;


import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import org.topbraid.spin.constraints.ConstraintViolation;

import com.hp.hpl.jena.ontology.OntDocumentManager;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ModelMaker;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.ValidityReport;
import com.hp.hpl.jena.reasoner.ValidityReport.Report;
import com.hp.hpl.jena.reasoner.rulesys.OWLFBRuleReasonerFactory;
import com.hp.hpl.jena.util.FileUtils;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.ReasonerVocabulary;
import com.semmtech.jena.reasoner.rulesys.OWLFBRuleReasoner;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.forms.editor.OntologyFormEditor;
import com.semmtech.plugin.semmweb.core.model.IModelProvider;
import com.semmtech.plugin.semmweb.validation.ValidationPlugin;
import com.semmtech.plugin.semmweb.validation.markers.Markers;
import com.semmtech.spin.constraints.SPINConstraints;


/**
 * 
 * @author Sander Stolk
 */
public class RunSingleFileValidationHandler extends AbstractHandler {
    private static final Logger logger = Logger.getLogger(RunSingleFileValidationHandler.class);
    public static final String ID = "com.semmtech.plugin.semmweb.validation.commands.runSingleFileValidation";

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        logger.debug("execute called!");
        IModelProvider provider = CorePlugin.getDefault().getActiveModelProvider();
        if (provider instanceof OntologyFormEditor) {
            OntologyFormEditor editor = (OntologyFormEditor) provider;
            IResource file = editor.getResource();
            OntModel model = provider.getOntModel();
            if (file != null && model != null) {
                // TODO: Select desired reasoning rather than simply full RDFS
                // Jena RDFS reasoner
                // Reasoner rdfsReasoner =
                // RDFSRuleReasonerFactory.theInstance().create(null);
                // rdfsReasoner.setParameter(ReasonerVocabulary.PROPsetRDFSLevel,
                // ReasonerVocabulary.RDFS_FULL);
                // InfModel infModel = ModelFactory.createInfModel(rdfsReasoner,
                // model);
                // Jena RDFS + OWL reasoner
                Reasoner owlReasoner = new OWLFBRuleReasoner(OWLFBRuleReasonerFactory.theInstance());
                owlReasoner.setParameter(ReasonerVocabulary.PROPderivationLogging, true);
                owlReasoner.setParameter(ReasonerVocabulary.PROPtraceOn, true);
                InfModel infModel = ModelFactory.createInfModel(owlReasoner, model);

                infModel.prepare();

                logger.debug("Reasoner inference executed");

                ValidityReport validity = infModel.validate();

                logger.debug("Reasoner validation report has been created");

                System.out.println("Validation. Model deemed consistent according to reasoner? "
                        + validity.isValid());
                System.out.println("Validation. Model deemed coherent according to reasoner? "
                        + validity.isClean());
                if (!validity.isClean()) {
                    System.out.println("Validation. Conflicts found are:");
                    for (Iterator<Report> i = validity.getReports(); i.hasNext();) {
                        System.out.println(" - " + i.next());
                    }
                }
                Markers.generate(file, validity);

                logger.debug("Reasoner validation report has been printed");

                OntDocumentManager ontDocumentManager = new OntDocumentManager();
                ontDocumentManager.setProcessImports(false);
                ModelMaker modelMaker = ModelFactory.createMemModelMaker();
                OntModelSpec spec = new OntModelSpec(modelMaker, ontDocumentManager, null,
                        OWL.FULL_LANG.getURI());

                // TODO: Select desired constraints
                OntModel constraintsModel = ModelFactory.createOntologyModel(spec);
                String symbolicName = ValidationPlugin.PLUGIN_ID;
                Bundle bundle = Platform.getBundle(symbolicName);
                for (String constraintsModelName : new String[] { "spinrdfs.ttl", "spinowl.ttl",
                        "spinowlrl.ttl", "spinowlrl-all.ttl" }) {

                    OntModel subModel = ModelFactory.createOntologyModel(spec);
                    Enumeration<URL> urls = bundle.findEntries("src/main/resources/models/",
                            constraintsModelName, true);
                    while (urls != null && urls.hasMoreElements()) {
                        URL url = urls.nextElement();
                        try (InputStreamReader isr = new InputStreamReader(url.openStream());) {
                            subModel.read(isr, null, FileUtils.langTurtle);
                        }
                        catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                    constraintsModel.add(subModel);
                }

                logger.debug("SPIN constraint models added");

                List<ConstraintViolation> violations = SPINConstraints.check(infModel,
                        constraintsModel, null, null);

                logger.debug("SPIN constraint models have been checked for violations.");

                Markers.generate(file, violations);

            }
        }
        logger.debug("execute finished.");
        return null;
    }

}
