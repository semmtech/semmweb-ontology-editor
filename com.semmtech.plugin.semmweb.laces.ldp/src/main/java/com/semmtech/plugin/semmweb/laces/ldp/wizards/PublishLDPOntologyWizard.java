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

package com.semmtech.plugin.semmweb.laces.ldp.wizards;


import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.semmtech.ExtraStringUtils;
import com.semmtech.plugin.semmweb.core.extensionpoint.IPublishErrorHandler;
import com.semmtech.plugin.semmweb.core.extensionpoint.IPublisher;
import com.semmtech.plugin.semmweb.core.extensionpoint.IPublisher.VersioningMode;
import com.semmtech.plugin.semmweb.core.model.OntModelUtils;
import com.semmtech.plugin.semmweb.core.resources.CoreResourcePropertiesManager;
import com.semmtech.plugin.semmweb.core.wizards.PublicationLocationWizardPage;
import com.semmtech.plugin.semmweb.core.wizards.SemmtechWizard;
import com.semmtech.semantics.util.NamespaceRewriteRule;
import com.semmtech.semantics.util.NamespaceRewriter;
import com.semmtech.ui.plugin.util.Selections;


/**
 * 
 * @author Sander Stolk
 * @author Mike Henrichs
 */
public class PublishLDPOntologyWizard extends SemmtechWizard implements INewWizard {

    @SuppressWarnings("unused")
    private static Logger logger = Logger.getLogger(PublishLDPOntologyWizard.class);

    public static final String ID = "com.semmtech.plugin.semmweb.publication.wizards.publishOntology";
    private static final String WINDOW_TITLE = "Publish Ontology";

    private PublicationLocationWizardPage locationPage;
    private ShareOntologyWizardPage sharePage;
    private IFile ontologyFile;
    private OntModel ontologyModel;

    public PublishLDPOntologyWizard(IFile ontologyFile, OntModel ontologyModel) {
        super();
        setWindowTitle(WINDOW_TITLE);
        setNeedsProgressMonitor(true);

        this.ontologyFile = ontologyFile;
        this.ontologyModel = ontologyModel;
    }

    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        ontologyFile = Selections.retrieveFirstAsType(selection, IFile.class);
        // TODO: read in ontologyModel
    }

    @Override
    public void addPages() {
        String tgtLocation = CoreResourcePropertiesManager.getSourceLocation(ontologyFile);
        VersioningMode vm = CoreResourcePropertiesManager.getSourceVersioningmethod(ontologyFile);

        setShellImage();

        if (tgtLocation != null) {
            locationPage = new PublicationLocationWizardPage(tgtLocation, vm);
        }
        else {
            locationPage = new PublicationLocationWizardPage();
        }
        sharePage = new ShareOntologyWizardPage(ontologyModel);
        String filename = ontologyFile.getName();
        String extension = "." + ontologyFile.getFileExtension();

        // For default path use all but the first segments from the owl:Ontology
        // URI, if available. Otherwise, default to filename.
        String path = ExtraStringUtils.appendEnd(StringUtils.removeEnd(filename, extension), "/");
        String ontologyModelURI = OntModelUtils.getURI(ontologyModel);
        if (ontologyModelURI != null) {
            int indexDoubleSlash = ontologyModelURI.indexOf("//");
            if (indexDoubleSlash >= 0) {
                int indexNextSlash = ontologyModelURI.indexOf('/', indexDoubleSlash + 2);
                if (indexNextSlash >= 0 && ontologyModelURI.length() > indexNextSlash + 1) {
                    path = ontologyModelURI.substring(indexNextSlash + 1);
                }
            }
        }

        locationPage.setDefaultPath(path);

        addPage(locationPage);
        addPage(sharePage);

        /*
         * if (CoreResourcePropertiesManager.hasSourceLocation(ontologyFile)) {
         * locationPage.setPublicationLocation(CoreResourcePropertiesManager
         * .getSourceLocation(ontologyFile)); locationPage.setOverwrite(true); }
         */
    }

    @Override
    public IWizardPage getNextPage(IWizardPage page) {
        IWizardPage nextPage = super.getNextPage(page);

        if (page == locationPage) {
            sharePage.setPublicationLocation(locationPage.getOntologyURI());
        }

        return nextPage;
    }

    @Override
    public boolean performFinish() {
        IRunnableWithProgress op = new PublishOntology();

        try {
            getContainer().run(true, false, op);
        }
        catch (InterruptedException e) {
            return false;
        }
        catch (InvocationTargetException e) {
            Throwable realException = e.getTargetException();
            MessageDialog.openError(getShell(), "Error", realException.getMessage());
            return false;
        }
        return true;
    }

    private final class PublishOntology implements IRunnableWithProgress {
        @Override
        public void run(IProgressMonitor monitor) throws InvocationTargetException {
            try {
                monitor.beginTask("Sharing Ontology File", 3);

                IPublisher publisher = locationPage.getPublisher();
                String ontologyUri = locationPage.getOntologyURI();
                // boolean overwrite = locationPage.getOverwrite();

                monitor.subTask("Rewriting namespace URIs...");

                List<NamespaceRewriteRule> rules = sharePage.getRules();
                Model baseModel = ontologyModel.getBaseModel();
                NamespaceRewriter rewriter = new NamespaceRewriter();
                for (NamespaceRewriteRule rule : rules) {
                    rewriter.addRule(rule);
                }
                rewriter.rewrite(baseModel);

                monitor.worked(1);
                monitor.subTask("Publishing data...");

                String ontologyUriVersioned = null;
                Model metadataModel = ontologyModel; // TODO
                VersioningMode versioningMode = locationPage.getVersioningMode();
                Object versioningModeSettings = locationPage.getCustomVersionLabel();
                ontologyUriVersioned = publisher.publishModel(ontologyModel, ontologyUri,
                        metadataModel, versioningMode, versioningModeSettings,
                        new IPublishErrorHandler() {
                            @Override
                            public void error(final String message) {
                                Display.getDefault().syncExec(new Runnable() {
                                    @Override
                                    public void run() {
                                        MessageDialog.openError(getShell(), "Publishing Ontology",
                                                message);
                                    }
                                });
                            }
                        });

                if (ontologyUriVersioned != null) {
                    // successful publication; set resources properties
                    CoreResourcePropertiesManager.setSourceLocation(ontologyFile, ontologyUri);
                    CoreResourcePropertiesManager.setSourceVersioningmethod(ontologyFile,
                            versioningMode);
                    CoreResourcePropertiesManager.setSourceVersion(ontologyFile,
                            ontologyUriVersioned);
                }

                monitor.worked(1);
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
            finally {
                monitor.done();
            }
        }
    }

}
