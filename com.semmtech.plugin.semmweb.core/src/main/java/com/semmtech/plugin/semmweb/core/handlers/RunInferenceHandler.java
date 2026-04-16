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

package com.semmtech.plugin.semmweb.core.handlers;


import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.rulesys.OWLFBRuleReasonerFactory;
import com.hp.hpl.jena.reasoner.rulesys.RDFSRuleReasonerFactory;
import com.hp.hpl.jena.vocabulary.ReasonerVocabulary;
import com.semmtech.jena.reasoner.rulesys.OWLFBRuleReasoner;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.MinimalSubclassValidator;
import com.semmtech.plugin.semmweb.core.model.IModelProvider;
import com.semmtech.plugin.semmweb.core.model.OntModelUtils;
import com.semmtech.plugin.semmweb.core.wizards.InferenceWizard;
import com.semmtech.ui.plugin.jobs.JobWithMonitor;
import com.semmtech.ui.plugin.jobs.Jobs;
import com.semmtech.ui.plugin.jobs.ResultJob;


/**
 * 
 * @author Sander Stolk
 */
public class RunInferenceHandler extends AbstractHandler {
    private static Logger logger = Logger.getLogger(RunInferenceHandler.class);
    public static final String ID = "com.semmtech.plugin.semmweb.core.commands.runInference";

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        logger.debug("execute called!");
        IModelProvider provider = CorePlugin.getDefault().getActiveModelProvider();
        if (provider != null) {
            RunInferenceJob job = createJob(provider);
            if (job != null) {
                job.schedule();
            }
        }
        return null;
    }

    public static RunInferenceJob createJob(IModelProvider provider) {
        String selectedReasoner = null;

        // select parameters via wizard
        Shell parentShell = CorePlugin.getActiveWorkbenchShell();
        InferenceWizard wizard = new InferenceWizard();
        WizardDialog dialog = new WizardDialog(parentShell, wizard);
        if (dialog.open() == Window.OK) {
            selectedReasoner = wizard.getReasonerURI();
        }

        // validate all required parameters are present
        if (Strings.isNullOrEmpty(selectedReasoner)) {
            return null;
        }

        // clear current inferred model
        provider.clearInferredModel();

        // create model that inference should work on (i.e. leave out
        // owl-implied)
        List<String> excludedSubmodels = Lists.newArrayList(MinimalSubclassValidator.MODEL_URI);
        OntModel rawModel = OntModelUtils.createWithoutSubModels(provider, excludedSubmodels);

        // select reasoner
        Reasoner reasoner = null;
        if (selectedReasoner == RDFSRuleReasonerFactory.URI) {
            // Jena RDFS reasoner
            reasoner = RDFSRuleReasonerFactory.theInstance().create(null);
            reasoner.setParameter(ReasonerVocabulary.PROPsetRDFSLevel, ReasonerVocabulary.RDFS_FULL);
            // RDFS_FULL rules specified in RDFSRuleReasoner.FULL_RULE_FILE
            reasoner.setParameter(ReasonerVocabulary.PROPderivationLogging, true);
        }
        else if (selectedReasoner == OWLFBRuleReasonerFactory.URI) {
            // Jena RDFS + OWL reasoner
            reasoner = new OWLFBRuleReasoner(OWLFBRuleReasonerFactory.theInstance());
            reasoner.setParameter(ReasonerVocabulary.PROPderivationLogging, true);
        }

        // run inference job with the selected reasoner
        if (reasoner != null) {
            RunInferenceJob job = new RunInferenceJob(provider, rawModel, reasoner);
            job.setUser(true);
            return job;
        }
        return null;
    }

    protected static class RunInferenceJob extends JobWithMonitor {
        protected final IModelProvider provider;
        protected final Model rawModel;
        protected final Reasoner reasoner;

        public RunInferenceJob(IModelProvider provider, Model rawModel, Reasoner reasoner) {
            super("Inference");
            this.provider = provider;
            this.rawModel = rawModel;
            this.reasoner = reasoner;
        }

        @Override
        protected IStatus run(IProgressMonitor monitor) {
            startMonitorUpdate(monitor, "Running inference on the model.", 3);

            // Running inference
            long startTime = System.currentTimeMillis();

            InferenceJob infJob = new InferenceJob();
            infJob.setSystem(true);
            infJob.schedule();
            final InfModel infModel = (InfModel) Jobs.getResult(infJob, monitor);
            if (infModel == null) {
                monitor.done();
                stopMonitorUpdate();
                return Status.CANCEL_STATUS;
            }

            long endTime = System.currentTimeMillis();
            logger.debug(String.format("Creating the inferred model took %d milliseconds.", endTime
                    - startTime));
            addWorked(1);

            // Opening inferred model
            startTime = System.currentTimeMillis();

            OpenJob openJob = new OpenJob(infModel);
            openJob.setSystem(true);
            openJob.schedule();
            final InfModel openedModel = (InfModel) Jobs.getResult(openJob, monitor);
            if (openedModel != infModel) {
                monitor.done();
                stopMonitorUpdate();
                return Status.CANCEL_STATUS;
            }

            endTime = System.currentTimeMillis();
            logger.debug(String.format("Opening the inferred model took %d milliseconds.", endTime
                    - startTime));
            addWorked(1);

            // Set open, inferred model on the modelprovider
            Display.getDefault().syncExec(new Runnable() {
                @Override
                public void run() {
                    provider.setInferredModel(openedModel);
                }
            });

            addWorked(1);

            monitor.done();
            stopMonitorUpdate();
            return Status.OK_STATUS;
        }

        private class InferenceJob extends ResultJob {
            public InferenceJob() {
                super("Running inference");
            }

            @Override
            protected IStatus run(IProgressMonitor monitor) {
                InfModel infModel = ModelFactory.createInfModel(reasoner, rawModel);
                infModel.prepare();
                returnResult(infModel);
                return Status.OK_STATUS;
            }
        }

        private class OpenJob extends ResultJob {
            private Model model;

            public OpenJob(Model model) {
                super("Opening inference model");
                this.model = model;
            }

            @Override
            protected IStatus run(IProgressMonitor monitor) {
                StmtIterator iter = model.listStatements();
                while (iter.hasNext()) {
                    iter.next();
                    // Statement s = iter.next();
                    // System.out.println(s);
                    // System.out.println("------- Derivation -------");
                    // Iterator<Derivation> derivations = ((InfModel)
                    // model).getDerivation(s);
                    // while (derivations.hasNext()) {
                    // System.out.println(derivations.next());
                    // }
                    // System.out.println("--------------------------");
                }
                returnResult(model);
                return Status.OK_STATUS;
            }
        }

    }

}
