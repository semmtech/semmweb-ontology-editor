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


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFLanguages;
import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.semmtech.jena.skolem.Skolemizer;
import com.semmtech.ui.plugin.jobs.Jobs;


public class AnonymizeWizard extends Wizard implements IExportWizard {

    private static Logger logger = Logger.getLogger(SkolemizeWizard.class);

    private WizardNewFileCreationPage filePage;

    private IStructuredSelection selection;
    private IFile file;
    private boolean aborted;

    public AnonymizeWizard(IFile file) {
        setWindowTitle("Anonymize Model");
        setNeedsProgressMonitor(true);

        this.file = file;
    }

    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        this.selection = selection;
    }

    @Override
    public void addPages() {

        filePage = new WizardNewFileCreationPage("filePage", selection);
        filePage.setTitle("Anonymized File");
        filePage.setDescription("Specify the target location and filename of the de-skolemized model.");

        addPage(filePage);
    }

    @Override
    public boolean performFinish() {
        IPath containerPath = filePage.getContainerFullPath();
        final String filename = filePage.getFileName();

        IPath path = containerPath.append(filename);
        final IFile target = ResourcesPlugin.getWorkspace().getRoot().getFile(path);

        IRunnableWithProgress operation = new IRunnableWithProgress() {
            private int worked = 0;
            private String activeTask;
            private long interval = 750;
            private Job updateJob;

            @Override
            public void run(final IProgressMonitor monitor) throws InvocationTargetException,
                    InterruptedException {

                updateJob = new Job("__updateJob") {

                    @Override
                    public IStatus run(IProgressMonitor inner) {
                        while (!inner.isCanceled()) {
                            if (worked > 0) {
                                monitor.worked(worked);
                                worked = 0;
                            }
                            monitor.subTask(activeTask);
                            try {
                                Thread.sleep(interval);
                            }
                            catch (InterruptedException e) {
                                return Status.OK_STATUS;
                            }
                        }
                        return Status.OK_STATUS;
                    }
                };
                updateJob.setSystem(true);
                updateJob.schedule();

                int workload = 3;
                monitor.beginTask("Anonymizing Model", workload);

                activeTask = "Reading input model...";
                Model model = ModelFactory.createDefaultModel();
                Lang lang = RDFLanguages.filenameToLang(file.getName(), Lang.RDFXML);
                try {
                    RDFDataMgr.read(model, file.getContents(), lang);
                }
                catch (CoreException ex) {
                    logger.error("Error trying to read the input to de-skolemization process", ex);
                    abort(monitor);
                }
                worked += 1;
                if (monitor.isCanceled()) {
                    abort(monitor);
                    return;
                }

                activeTask = "De-skolemizing resources...";
                Skolemizer skolemizer = new Skolemizer();
                skolemizer.setKeepSkolemIRIs(true);
                Model deskolemized = skolemizer.deskolemize(model);
                worked += 1;

                activeTask = "Saving de-skolemized model...";
                try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
                    Lang format = RDFLanguages.filenameToLang(filename, Lang.RDFXML);
                    RDFDataMgr.write(output, deskolemized, format);
                    InputStream input = new ByteArrayInputStream(output.toByteArray());
                    try {
                        target.create(input, true, monitor);

                        target.refreshLocal(IResource.DEPTH_INFINITE, monitor);
                    }
                    catch (CoreException ex) {
                        logger.error("Unable to create the target resource", ex);
                        abort(monitor);
                    }
                }
                catch (IOException ex) {
                    logger.error("Error occured with outputting deskolemized model to stream", ex);
                }
                worked += 1;

                if (monitor.isCanceled()) {
                    abort(monitor);
                    return;
                }

                aborted = false;
                Jobs.cancelWithJoin(updateJob);
                monitor.done();
            }

            private void abort(IProgressMonitor monitor) {
                logger.info("De-skolemization aborted");
                Jobs.cancelWithJoin(updateJob);
                monitor.done();
                aborted = true;
            }
        };
        try {
            aborted = false;
            getContainer().run(true, true, operation);

            Job refresh = new Job("Refreshing Project") {

                @Override
                protected IStatus run(IProgressMonitor monitor) {
                    // try {
                    // container.refreshLocal(IResource.DEPTH_INFINITE,
                    // monitor);
                    // IFile file =
                    // container.getFile(Path.fromOSString(filename));
                    // file.refreshLocal(IResource.DEPTH_ONE, monitor);
                    //
                    // }
                    // catch (CoreException ex) {
                    // logger.error("Error occured trying to refresh container!");
                    // ex.printStackTrace();
                    // }
                    return Status.OK_STATUS;
                }

            };
            refresh.addJobChangeListener(new JobChangeAdapter() {
                @Override
                public void done(IJobChangeEvent event) {
                    // final IFile file =
                    // container.getFile(Path.fromOSString(filename));
                    // if (file != null) {
                    // Display.getDefault().syncExec(new Runnable() {
                    // @Override
                    // public void run() {
                    // if (!file.exists()) {
                    // return;
                    // }
                    // IWorkbench workbench =
                    // Activator.getDefault().getWorkbench();
                    // if (workbench == null) {
                    // return;
                    // }
                    // IWorkbenchWindow window =
                    // workbench.getActiveWorkbenchWindow();
                    // if (window == null) {
                    // return;
                    // }
                    // IWorkbenchPage page = window.getActivePage();
                    // try {
                    // IDE.openEditor(page, file, true);
                    //
                    // }
                    // catch (PartInitException ex) {
                    // ex.printStackTrace();
                    // }
                    // }
                    // });
                    // }
                }
            });
            refresh.setUser(true);
            refresh.schedule();

        }
        catch (InvocationTargetException ex) {
            logger.error("Error occured during the conversion execution");
            ex.printStackTrace();
        }
        catch (InterruptedException ex) {
            logger.error("Error occured during the conversion execution");
            ex.printStackTrace();
        }
        return !aborted;
    }

}
