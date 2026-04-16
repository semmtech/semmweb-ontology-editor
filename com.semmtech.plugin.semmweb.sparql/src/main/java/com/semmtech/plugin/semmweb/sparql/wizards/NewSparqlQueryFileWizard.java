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

package com.semmtech.plugin.semmweb.sparql.wizards;


import java.io.ByteArrayInputStream;
import java.lang.reflect.InvocationTargetException;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.IPageChangeProvider;
import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

import com.semmtech.jena.ontology.OntologySpec;
import com.semmtech.plugin.semmweb.core.wizards.SemmtechWizard;
import com.semmtech.plugin.semmweb.sparql.SparqlQueryType;


public class NewSparqlQueryFileWizard extends SemmtechWizard implements INewWizard,
        IPageChangedListener {
    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(NewSparqlQueryFileWizard.class);

    private static final String WINDOW_TITLE = "New SPARQL Query File";

    public static final String ID = "com.semmtech.plugin.semmweb.sparql.wizards.newQueryFile";

    private QueryTypeWizardPage typePage;
    private QuerySelectWizardPage selectPage;
    private QueryPrefixesWizardPage prefixesPage;
    private QueryFileWizardPage filePage;

    private IStructuredSelection selection;

    /**
     * Constructor for NewOntModelWizard.
     */
    public NewSparqlQueryFileWizard() {
        super();
        setWindowTitle(WINDOW_TITLE);
        setNeedsProgressMonitor(true);
    }

    @Override
    public void addPages() {
        setShellImage();

        typePage = new QueryTypeWizardPage();
        selectPage = new QuerySelectWizardPage();

        IProject project = null;
        if (selection != null && !selection.isEmpty()) {
            Object selected = selection.getFirstElement();
            if (selected instanceof IProject) {
                project = (IProject) selected;
            }
            else if (selected instanceof IResource) {
                IResource resource = (IResource) selected;
                project = resource.getProject();
            }
        }
        prefixesPage = new QueryPrefixesWizardPage(project);
        filePage = new QueryFileWizardPage(selection);

        addPage(typePage);
        addPage(selectPage);
        addPage(filePage);
        addPage(prefixesPage);

        // ImageDescriptor banner =
        // CorePlugin.getDefault().getImageDescriptor(CorePluginImages.IMG_BANNER_WIZARD_ONTOLOGY);
        // annotationPage.setImageDescriptor(banner);
        // uriPage.setImageDescriptor(banner);
        // importsPage.setImageDescriptor(banner);
        // filePage.setImageDescriptor(banner);

        ((IPageChangeProvider) getContainer()).addPageChangedListener(this);
    }

    @Override
    public IWizardPage getNextPage(IWizardPage page) {
        if (page.equals(typePage) && typePage.getSelectedType() == SparqlQueryType.SELECT) {
            return selectPage;
        }
        else if (page.equals(typePage)) {
            return filePage;
        }
        return super.getNextPage(page);
    }

    @Override
    public IWizardPage getPreviousPage(IWizardPage page) {
        if (page.equals(filePage) && typePage.getSelectedType() == SparqlQueryType.SELECT) {
            return selectPage;
        }
        else if (page.equals(filePage)) {
            return typePage;
        }
        return super.getPreviousPage(page);
    }

    /**
     * This method is called when 'Finish' button is pressed in the wizard. We
     * will create an operation and run it using wizard as execution context.
     */
    @Override
    public boolean performFinish() {
        IRunnableWithProgress op = new IRunnableWithProgress() {
            @Override
            public void run(IProgressMonitor monitor) throws InvocationTargetException {
                try {
                    monitor.beginTask("Creating SPARQL Query File", 1);
                    StringBuilder query = new StringBuilder();

                    monitor.subTask("Adding prefixes...");
                    for (OntologySpec spec : prefixesPage.getSelectedPrefixes()) {
                        query.append(String.format("PREFIX %s: <%s>\n", spec.getPrefix(),
                                spec.getPublicURI()));
                    }
                    query.append("\n");
                    monitor.worked(1);

                    switch (typePage.getSelectedType()) {
                    case SELECT:
                        query.append(String.format("SELECT%s *\n",
                                (selectPage.isDistinct() ? " DISTINCT" : "")));
                        query.append(String.format("WHERE {\n\t?s ?p ?o .\n}\n"));
                        if (selectPage.isLimit()) {
                            query.append(String.format("LIMIT %s\n", selectPage.getLimitSize()));
                        }
                        break;
                    case ASK:
                        query.append(String.format("ASK\n"));
                        query.append(String.format("WHERE {\n\t?s ?p ?o .\n}\n"));
                        break;
                    case CONSTRUCT:
                        query.append(String
                                .format("CONSTRUCT\n{\n# Insert creation statements here...\n}\n"));
                        query.append(String.format("WHERE {\n\t?s ?p ?o .\n}\n"));
                        break;
                    case DESCRIBE:
                        query.append(String.format("DESCRIBE ?s\n"));
                        query.append(String.format("WHERE {\n\t?s ?p ?o .\n}\n"));
                        break;
                    default:
                        break;
                    }

                    monitor.subTask("Writing query to file...");
                    IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
                    IContainer container = (IContainer) root.findMember(new Path(filePage
                            .getFolder()));
                    final IFile file = container.getFile(new Path(filePage.getFilename()));
                    String content = query.toString();
                    file.create(new ByteArrayInputStream(content.getBytes()), true, monitor);
                    monitor.worked(1);

                    monitor.subTask("Opening file for editing...");
                    getShell().getDisplay().asyncExec(new Runnable() {
                        @Override
                        public void run() {
                            IWorkbenchPage page = PlatformUI.getWorkbench()
                                    .getActiveWorkbenchWindow().getActivePage();
                            try {
                                IDE.openEditor(page, file, true);
                            }
                            catch (PartInitException e) {
                            }
                        }
                    });
                    monitor.worked(1);
                }
                catch (CoreException e) {
                    throw new InvocationTargetException(e);
                }
                finally {
                    monitor.done();
                }
            }
        };
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

    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        this.selection = selection;
    }

    @Override
    public void pageChanged(PageChangedEvent event) {
        Object page = event.getSelectedPage();

        if (page == prefixesPage) {
            IProject project = filePage.getProject();
            if (project != null && !project.equals(prefixesPage.getProject())) {
                prefixesPage.setProject(project);
                prefixesPage.refreshViewer();
            }
        }
    }
}