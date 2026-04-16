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

package com.semmtech.plugin.semmweb.laces.ldp.handlers;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.FileUtils;
import com.semmtech.io.StringOutputStream;
import com.semmtech.plugin.semmweb.core.decorators.PublishedResourceFileDecorator;
import com.semmtech.plugin.semmweb.core.jobs.LoadModelJob;
import com.semmtech.plugin.semmweb.core.navigator.IModel;
import com.semmtech.plugin.semmweb.core.resources.CoreResourcePropertiesManager;
import com.semmtech.ui.plugin.util.Selections;


/**
 * 
 * @author Sander Stolk
 */
public class UpdateOntologyHandler extends AbstractHandler {
    public static final String ID = "com.semmtech.plugin.semmweb.laces.ldp.commands.updateOntology";

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        ISelection selection = HandlerUtil.getCurrentSelection(event);
        IModel model = Selections.retrieveFirstAsType(selection, IModel.class);
        IFile file;

        if (model != null) {
            file = (IFile) model.getResource();
        }
        else {
            file = Selections.retrieveFirstAsType(selection, IFile.class);
        }

        if (file == null) {
            return null;
        }

        if (!CoreResourcePropertiesManager.hasSourceLocation(file)) {
            return null;
        }

        boolean update = true;
        if (CoreResourcePropertiesManager.isModified(file)) {
            Shell shell = HandlerUtil.getActiveShell(event);
            update = MessageDialog
                    .openQuestion(
                            shell,
                            "Update Ontology",
                            "The file appears to be modified locally. Are you sure you want to overwrite these changes by retrieving the latest version of this ontology from the Laces LDP?");
        }
        if (update) {
            UpdateOntologyJob job = new UpdateOntologyJob(file);
            job.setUser(true);
            job.schedule();
        }
        return null;
    }

    class UpdateOntologyJob extends Job {
        private final IFile file;

        public UpdateOntologyJob(IFile file) {
            super("Updating Ontology");
            this.file = file;
        }

        @Override
        protected IStatus run(IProgressMonitor monitor) {
            monitor.beginTask("Updating Ontology", 3);

            monitor.subTask("Contacting server...");

            String sourceLocation = CoreResourcePropertiesManager.getSourceLocation(file);

            // via LoadModelJob instead of model.read() in order to have a
            // unified way of reading in models from the Web
            Model importedModel = null;
            OntModel ontModel = ModelFactory.createOntologyModel();
            try {
                importedModel = LoadModelJob.loadSubModel(sourceLocation, sourceLocation, ontModel);
            }
            catch (Throwable e1) {
                e1.printStackTrace();
            }

            if (importedModel == null || importedModel.size() == 0) {
                Display.getDefault().syncExec(new Runnable() {
                    @Override
                    public void run() {
                        Shell shell = Display.getDefault().getActiveShell();
                        MessageDialog.openError(shell, "Updating Ontology",
                                "Failed to retrieve the latest version of the ontology.");
                    }
                });
                monitor.worked(1);
            }
            else {
                monitor.subTask("Updating file content and attributes...");

                // String etag = null;
                // if (response.getHeaders().containsKey(HttpHeaders.ETAG)) {
                // etag =
                // response.getHeaders().get(HttpHeaders.ETAG).get(0).replace("\"",
                // "");
                // }

                String turtle = null;
                String writeLang = FileUtils.langTurtle;
                try (StringOutputStream stringStream = new StringOutputStream("UTF-8")) {
                    importedModel.write(stringStream, writeLang);
                    turtle = stringStream.toString();

                    String encoding = file.getCharset();
                    file.setContents(new ByteArrayInputStream(turtle.getBytes(encoding)),
                            IResource.FORCE | IResource.KEEP_HISTORY, monitor);

                    // CoreResourcePropertiesManager.setSourceETag(file, etag);
                    CoreResourcePropertiesManager.setModified(file, false);

                    // String targetLocation =
                    // CoreResourcePropertiesManager.getTargetLocation(file);

                    // if (sourceLocation.equalsIgnoreCase(targetLocation)) {
                    // CoreResourcePropertiesManager.setTargetETag(file, etag);
                    // }

                    Display.getDefault().syncExec(new Runnable() {
                        @Override
                        public void run() {
                            PublishedResourceFileDecorator.refreshAll();
                        }
                    });
                }
                catch (CoreException e) {
                    e.printStackTrace();
                }
                catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                monitor.worked(1);
            }
            monitor.done();
            return Status.OK_STATUS;
        }
    }

}
