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


import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import com.google.common.collect.Lists;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.ValidityReport;
import com.hp.hpl.jena.reasoner.rulesys.OWLFBRuleReasonerFactory;
import com.hp.hpl.jena.reasoner.rulesys.RDFSRuleReasonerFactory;
import com.hp.hpl.jena.vocabulary.ReasonerVocabulary;
import com.semmtech.jena.reasoner.rulesys.OWLFBRuleReasoner;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.MinimalSubclassValidator;
import com.semmtech.plugin.semmweb.core.forms.editor.OntologyFormEditor;
import com.semmtech.plugin.semmweb.core.model.IModelProvider;
import com.semmtech.plugin.semmweb.core.model.OntModelUtils;
import com.semmtech.plugin.semmweb.validation.markers.Markers;
import com.semmtech.ui.plugin.jobs.JobWithMonitor;
import com.semmtech.ui.plugin.jobs.Jobs;
import com.semmtech.ui.plugin.jobs.ResultJob;


/**
 * 
 * @author Sander Stolk
 */
public class RunModelValidationHandler extends AbstractHandler {
    private static Logger logger = Logger.getLogger(RunModelValidationHandler.class);
    public static final String ID = "com.semmtech.plugin.semmweb.validation.commands.runModelValidation";

    public static final String PARAMETER_REASONER_URI = "reasonerURI";

    private static final String PROBLEMS_VIEW_ID = "org.eclipse.ui.views.ProblemView";

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        logger.debug("execute called!");
        IModelProvider provider = CorePlugin.getDefault().getActiveModelProvider();
        if (provider instanceof OntologyFormEditor) {
            OntologyFormEditor editor = (OntologyFormEditor) provider;
            IResource file = editor.getResource();
            if (file != null) {
                // Create model that inference should work on (i.e. leave out
                // owl-implied and any already created inferred models).
                List<String> excludedSubmodels = Lists.newArrayList(
                        MinimalSubclassValidator.MODEL_URI, IModelProvider.INFERRED_SUBMODEL_URI);
                OntModel rawModel = OntModelUtils.createWithoutSubModels(provider,
                        excludedSubmodels);

                // Jena reasoner
                Reasoner reasoner = null;
                String reasonerURI = getReasonerURI(event);
                if (reasonerURI != null) {
                    if (!proceedWithReasoner(reasonerURI)) {
                        return null;
                    }
                    if (reasonerURI.equals(OWLFBRuleReasonerFactory.URI)) {
                        reasoner = createOWLReasoner();
                    }
                    else if (reasonerURI.equals(RDFSRuleReasonerFactory.URI)) {
                        reasoner = createRDFSReasoner();
                    }
                }
                if (reasoner == null) {
                    // falling back to using the default reasoner
                    reasoner = createDefaultReasoner();
                }

                // Run validation job with the selected reasoner.
                if (rawModel != null && reasoner != null) {
                    RunModelValidationJob job = new RunModelValidationJob(file, provider, rawModel,
                            reasoner);
                    job.setUser(true);
                    job.schedule();
                }
            }
        }
        return null;
    }

    protected String getReasonerURI(ExecutionEvent event) {
        return event.getParameter(PARAMETER_REASONER_URI);
    }

    protected Reasoner createDefaultReasoner() {
        return createRDFSReasoner();
    }

    protected Reasoner createRDFSReasoner() {
        Reasoner reasoner = RDFSRuleReasonerFactory.theInstance().create(null);
        reasoner.setParameter(ReasonerVocabulary.PROPsetRDFSLevel, ReasonerVocabulary.RDFS_FULL);
        return reasoner;
    }

    protected Reasoner createOWLReasoner() {
        Reasoner reasoner = new OWLFBRuleReasoner(OWLFBRuleReasonerFactory.theInstance());
        return reasoner;
    }

    protected boolean proceedWithReasoner(String reasonerURI) {
        if (reasonerURI.equals(OWLFBRuleReasonerFactory.URI)) {
            String title = "Proceed with OWL model validation?";
            String message = "You have opted for model validation using the OWL reasoner. In order to do so, the reasoner first has to run OWL inference. Even for small models, OWL inference can be a CPU and memory intensive task, which may take a long time or not finish at all. Are you sure you wish to continue?";
            return MessageDialog
                    .openQuestion(Display.getCurrent().getActiveShell(), title, message);
        }
        return true;
    }

    protected class RunModelValidationJob extends JobWithMonitor {
        protected final IResource file;
        protected final IModelProvider provider;
        protected final Model rawModel;
        protected final Reasoner reasoner;

        public RunModelValidationJob(IResource file, IModelProvider provider, Model rawModel,
                Reasoner reasoner) {
            super("Model Validation");
            this.file = file;
            this.provider = provider;
            this.rawModel = rawModel;
            this.reasoner = reasoner;
        }

        @Override
        protected IStatus run(IProgressMonitor monitor) {
            startMonitorUpdate(monitor, "Running validation on the model.", 2);

            long startTime = System.currentTimeMillis();

            updateSubTask("Creating a new inferred model to validate.");
            // Get inferred model
            InferenceJob infJob = new InferenceJob();
            infJob.setSystem(true);
            infJob.schedule();
            final InfModel infModel = (InfModel) Jobs.getResult(infJob, monitor);
            if (infModel == null) {
                monitor.done();
                stopMonitorUpdate();
                return Status.CANCEL_STATUS;
            }
            addWorked(1);

            updateSubTask("Validating the inferred model.");
            // Run validation on model
            ReasonerValidationJob valJob = new ReasonerValidationJob(infModel);
            valJob.setSystem(true);
            valJob.schedule();
            final ValidityReport validity = (ValidityReport) Jobs.getResult(valJob, monitor);
            if (validity == null) {
                monitor.done();
                stopMonitorUpdate();
                return Status.CANCEL_STATUS;
            }
            addWorked(1);

            updateSubTask("Displaying the results.");
            Display.getDefault().syncExec(new Runnable() {
                @Override
                public void run() {
                    Markers.generate(file, validity);

                    // Open the Problems view
                    try {
                        CorePlugin.getActivePage().showView(PROBLEMS_VIEW_ID);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            long endTime = System.currentTimeMillis();
            logger.debug(String.format("Validating the model took %d milliseconds.", endTime
                    - startTime));

            monitor.done();
            stopMonitorUpdate();
            return Status.OK_STATUS;
        }

        private class InferenceJob extends ResultJob {
            public InferenceJob() {
                super("Inference");
            }

            @Override
            protected IStatus run(IProgressMonitor monitor) {
                InfModel infModel = ModelFactory.createInfModel(reasoner, rawModel);
                infModel.prepare();
                returnResult(infModel);
                return Status.OK_STATUS;
            }
        }

        private class ReasonerValidationJob extends ResultJob {
            protected final InfModel infModel;

            public ReasonerValidationJob(InfModel infModel) {
                super("Validation");
                this.infModel = infModel;
            }

            @Override
            protected IStatus run(IProgressMonitor monitor) {
                ValidityReport validity = infModel.validate();
                returnResult(validity);
                return Status.OK_STATUS;
            }
        }

    }

}
