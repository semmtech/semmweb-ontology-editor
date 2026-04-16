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
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.URLDecoder;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;

import com.google.common.base.Strings;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.shared.JenaException;
import com.hp.hpl.jena.util.FileUtils;
import com.semmtech.io.StringOutputStream;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.CorePluginImages;
import com.semmtech.plugin.semmweb.core.jobs.LoadModelJob;
import com.semmtech.plugin.semmweb.core.resources.CoreResourcePropertiesManager;
import com.semmtech.plugin.semmweb.core.util.ResourcesUtil;


/**
 * 
 * @author Sander Stolk
 */
public class DownloadOntModelWizard extends Wizard implements IImportWizard {

    private static final Logger logger = Logger.getLogger(DownloadOntModelWizard.class);

    private DownloadOntModelWizardPage importPage;

    private IStructuredSelection selection;
    private IContainer container;
    private String url;
    private String filename;

    private String locationUri;

    private boolean trackSource = true;

    public DownloadOntModelWizard() {
        this(null, null, null);
    }

    public DownloadOntModelWizard(String url, IContainer container, String filename) {
        super();
        setWindowTitle("Download RDF/OWL Ontology File from Web");
        setNeedsProgressMonitor(true);
        this.url = url;
        this.container = container;
        this.filename = filename;
    }

    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        this.selection = selection;
        if (!ResourcesUtil.existsSemanticProjects()) {
            MessageDialog
                    .openInformation(
                            getShell(),
                            "Project Required",
                            "It seems that no Semantic Projects exist yet in your workspace.\n"
                                    + "Please create a Semantic Project before adding a new Semantic File.");
        }
    }

    @Override
    public void addPages() {
        Shell shell = getShell();
        if (shell != null) {
            shell.setImage(CorePlugin.getDefault().getImage(CorePluginImages.IMG_SEMMTECH_ICON));
        }

        if (container == null) {
            importPage = new DownloadOntModelWizardPage(selection, url, filename);
        }
        else {
            importPage = new DownloadOntModelWizardPage(container, url, filename);
        }
        addPage(importPage);
    }

    @Override
    public boolean performFinish() {
        IRunnableWithProgress operation = new IRunnableWithProgress() {
            @Override
            public void run(IProgressMonitor monitor) throws InvocationTargetException {
                try {
                    doFinish(monitor);
                }
                catch (Throwable e) {
                    throw new InvocationTargetException(e);
                }
                finally {
                    monitor.done();
                }
            }
        };
        try {
            getContainer().run(true, false, operation);
        }
        catch (InterruptedException e) {
            return false;
        }
        catch (InvocationTargetException e) {
            e.printStackTrace();
            Throwable realException = e.getTargetException();

            if (realException instanceof JenaException
                    && (realException.getCause() instanceof SSLException || realException
                            .getCause() instanceof SSLHandshakeException)) {
                String message = "The ontology could not be retrieved, because the certificate of the URL could not be satisfactorily authenticated.\n\nThis problem is likely due to (A) an expired certificate at the URL or (B) the use of a certification authority at the URL that is unrecognised by your Java installation. Option B is often caused by an out-of-date 'cacerts' file in your Java installation, which may be remedied by replacing it with a more up-to-date version of the file.";
                if (!Strings.isNullOrEmpty(realException.getMessage())) {
                    message += "\n\n" + realException.getMessage();
                }
                logger.error(message, realException);
                MessageDialog.openError(getShell(), "Certificate Error", message);
            }
            else if (realException instanceof UnsupportedEncodingException) {
                String message = "An encoding error occurred while downloading the model. ";
                if (!Strings.isNullOrEmpty(realException.getMessage())) {
                    message += "\n\n" + realException.getMessage();
                }
                logger.error(message, realException);
                MessageDialog.openError(getShell(), "Encoding Error", message);
            }
            else if (realException instanceof IOException) {
                String message = "An I/O error occurred while downloading the model. ";
                if (!Strings.isNullOrEmpty(realException.getMessage())) {
                    message += "\n\n" + realException.getMessage();
                }
                logger.error(message, realException);
                MessageDialog.openError(getShell(), "I/O Error", message);
            }
            else if (realException instanceof Exception) {
                String message = "A generic error occurred while downloading the model. ";
                if (!Strings.isNullOrEmpty(realException.getMessage())) {
                    message += "\n\n" + realException.getMessage();
                }
                logger.error(message, realException);
                message += "\n\nPlease make sure the model is available at the web address provided.";
                MessageDialog.openError(getShell(), "Error", message);
            }

            return false;
        }
        return true;
    }

    private void doFinish(IProgressMonitor monitor) throws Throwable {
        String url = importPage.getURL();
        String filename = importPage.getFilename();
        container = importPage.getFolder();
        Model importedModel = null;

        // TODO: We might be able to download the ontology as a file
        // without first loading it into a model and writing triples.
        // (if url contains the required file extension or if MIME-type of
        // returned content is already suitable)

        OntModel ontModel = ModelFactory.createOntologyModel();
        // via LoadModelJob instead of model.read() in order to have a
        // unified way of reading in models from the Web
        importedModel = LoadModelJob.loadSubModel(url, url, ontModel);

        String writeLang = FileUtils.guessLang(filename);
        if (writeLang == null || filename.endsWith(".owl")) {
            writeLang = FileUtils.langXMLAbbrev;
        }

        try (StringOutputStream stringStream = new StringOutputStream("UTF-8")) {
            importedModel.write(stringStream, writeLang);
            String content = stringStream.toString();

            if (content != null) {
                final IFile file = container.getFile(new Path(filename));

                locationUri = URLDecoder.decode(file.getLocation().toString(), "UTF-8");
                locationUri = locationUri.replace("\\", "/");
                locationUri = String.format("file:///%s", locationUri);

                try (InputStream stream = new ByteArrayInputStream(content.getBytes())) {
                    if (file.exists()) {
                        file.setContents(stream, true, true, monitor);
                    }
                    else {
                        file.create(stream, true, monitor);
                    }
                    stream.close();

                    if (trackSource) {
                        // source location is either the url or the base of the
                        // model in case it appears to be a more specific
                        // location than the url (e.g., adding "/versions/1")
                        String base = importedModel.getNsPrefixURI("");
                        String sourceLocation = url;
                        if (base != null && base.startsWith(url)) {
                            sourceLocation = base;
                        }
                        CoreResourcePropertiesManager.setSourceLocation(file, sourceLocation);
                    }
                }
            }
        }
    }

    public void setTrackSourceLocation(boolean trackSource) {
        this.trackSource = trackSource;
    }

    public String getLocationURI() {
        return locationUri;
    }

    public IContainer getFolder() {
        return container;
    }
}
