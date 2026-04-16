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


import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import com.hp.hpl.jena.ontology.OntResource;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.forms.editor.OntologyFormEditor;
import com.semmtech.plugin.semmweb.core.handlers.RunInferenceHandler;
import com.semmtech.plugin.semmweb.core.model.IModelProvider;


/**
 * 
 * @author Sander Stolk
 */
public abstract class AbstractValidationHandler extends AbstractHandler {
    protected ExecutionEvent event;

    protected IResource file;
    protected IModelProvider provider;
    protected OntResource activeResource;

    // private static Map<IModelProvider, Boolean> runInferencePreferences =
    // Maps.newHashMap();

    /**
     * Method may be overridden by subclasses, as long as they call their super
     * function before all else.
     */
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        this.event = event;

        provider = CorePlugin.getDefault().getActiveModelProvider();
        activeResource = CorePlugin.getDefault().getActiveOpenResource();
        if (provider instanceof OntologyFormEditor) {
            OntologyFormEditor editor = (OntologyFormEditor) provider;
            file = editor.getResource();
        }
        return null;
    }

    public interface JobCreator {
        public Job createJob();
    }

    /**
     * Executes the job provided by <code>jobCreator</code> after the inference
     * submodel has been updated according to the user's wishes.
     */
    public void executeJobWithPreferredInference(final JobCreator jobCreator) {
        if (provider == null) {
            return;
        }

        boolean runInference = false;

        /*
         * Boolean runInferencePreference =
         * runInferencePreferences.get(provider); if (runInferencePreference !=
         * null) { if (runInferencePreference.booleanValue() == true &&
         * provider.isInferredModelOutdated()) { runInference = true; } } else {
         * if (provider.isInferredModelOutdated()) { Shell shell =
         * Display.getDefault().getActiveShell(); String title =
         * "Run inference"; String message =
         * "It appears that no up-to-date inferred model exists at present. Additional knowledge gained through inference can aid validation significantly, resulting in better insights into which semantic problems apply.\n\nWould you like to first run inference before performing validation?"
         * ; MessageDialogWithToggle dialog =
         * MessageDialogWithToggle.openYesNoCancelQuestion( shell, title,
         * message, null, false, null, null); if (dialog.getReturnCode() ==
         * Window.CANCEL) { return; } else if (dialog.getReturnCode() == 1) { //
         * i.e. NO if (dialog.getToggleState()) {
         * runInferencePreferences.put(provider, new Boolean(false)); } } else
         * if (dialog.getReturnCode() == 2) { // i.e. YES if
         * (dialog.getToggleState()) { runInferencePreferences.put(provider, new
         * Boolean(true)); } runInference = true; } } }
         */

        if (!runInference) {
            executeJobCreatorJob(jobCreator);
        }
        else {
            Job inferenceJob = RunInferenceHandler.createJob(provider);
            if (inferenceJob != null) {
                inferenceJob.addJobChangeListener(new JobChangeAdapter() {
                    @Override
                    public void done(IJobChangeEvent event) {
                        if (event.getResult().isOK()) {
                            executeJobCreatorJob(jobCreator);
                        }
                    }
                });
                inferenceJob.schedule();
            }
        }
    }

    private void executeJobCreatorJob(final JobCreator jobCreator) {
        if (jobCreator != null) {
            Job executeJob = jobCreator.createJob();
            if (executeJob != null) {
                executeJob.schedule();
            }
        }
    }
}
