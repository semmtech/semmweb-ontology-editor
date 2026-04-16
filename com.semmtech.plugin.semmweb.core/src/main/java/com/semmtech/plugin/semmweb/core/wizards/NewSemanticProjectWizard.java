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


import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;
import org.eclipse.ui.ide.undo.CreateProjectOperation;
import org.eclipse.ui.ide.undo.WorkspaceUndoUtil;
import org.eclipse.ui.part.ISetSelectionTarget;

import com.google.common.collect.Lists;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.CorePluginImages;
import com.semmtech.plugin.semmweb.core.cache.CacheManager;
import com.semmtech.plugin.semmweb.core.nature.SemanticProject;
import com.semmtech.plugin.semmweb.core.preferences.ModelsFolderPreference;


public class NewSemanticProjectWizard extends Wizard implements INewWizard {

    private static Logger logger = Logger.getLogger(NewSemanticProjectWizard.class);

    private static final String WINDOW_TITLE = "New Semantic Project";

    public static final String ID = "com.semmtech.plugin.semmweb.core.wizards.newSemanticProject";

    private WizardNewProjectCreationPage mainPage;

    private IProject project;

    public NewSemanticProjectWizard() {
        super();
        setWindowTitle(WINDOW_TITLE);
        setNeedsProgressMonitor(true);
    }

    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {

    }

    @Override
    public void addPages() {
        super.addPages();

        mainPage = new WizardNewProjectCreationPage("basicNewProjectPage") { //$NON-NLS-1$

            public void createControl(Composite parent) {
                super.createControl(parent);
                Dialog.applyDialogFont(getControl());
            }

        };
        mainPage.setTitle("Create a Semantic Project");
        mainPage.setDescription("Provide a name and location for the new semantic project");
        mainPage.setImageDescriptor(CorePlugin.getDefault().getImageDescriptor(
                CorePluginImages.IMG_BANNER_WIZARD_SEMANTIC_PROJECT));

        addPage(mainPage);
    }

    /**
     * Creates a new project resource with the selected name.
     * <p>
     * In normal usage, this method is invoked after the user has pressed Finish
     * on the wizard; the enablement of the Finish button implies that all
     * controls on the pages currently contain valid values.
     * </p>
     * <p>
     * Note that this wizard caches the new project once it has been
     * successfully created; subsequent invocations of this method will answer
     * the same project resource without attempting to create it again.
     * </p>
     * 
     * @return the created project resource, or <code>null</code> if the project
     *         was not created
     */
    private IProject createNewProject() {
        if (project != null) {
            return project;
        }

        // get a project handle
        final IProject projectHandle = mainPage.getProjectHandle();

        // get a project descriptor
        URI location = null;
        if (!mainPage.useDefaults()) {
            location = mainPage.getLocationURI();
        }

        IWorkspace workspace = ResourcesPlugin.getWorkspace();
        final IProjectDescription description = workspace.newProjectDescription(projectHandle
                .getName());

        description.setLocationURI(location);
        String[] previousNatures = description.getNatureIds();
        String[] newNatures = new String[previousNatures.length + 1];
        System.arraycopy(previousNatures, 0, newNatures, 0, previousNatures.length);
        newNatures[previousNatures.length] = SemanticProject.NATURE_ID;
        description.setNatureIds(newNatures);

        // Create the new project operation
        IRunnableWithProgress op = new IRunnableWithProgress() {
            public void run(IProgressMonitor monitor) throws InvocationTargetException {
                CreateProjectOperation op = new CreateProjectOperation(description,
                        "Creating semantic project");
                try {
                    // see bug
                    // https://bugs.eclipse.org/bugs/show_bug.cgi?id=219901
                    // directly execute the operation so that the undo state is
                    // not preserved. Making this undoable resulted in too many
                    // accidental file deletions.
                    op.execute(monitor, WorkspaceUndoUtil.getUIInfoAdapter(getShell()));
                    // } catch (ExecutionException e) {
                    // throw new InvocationTargetException(e);
                }
                catch (ExecutionException e) {
                    e.printStackTrace();
                }

                project = projectHandle.getProject();
                try {
                    IProjectNature nature = project.getNature(SemanticProject.NATURE_ID);
                    if (nature != null) {
                        nature.configure();
                    }
                    try {
                        project.setDefaultCharset("UTF-8", monitor);
                    }
                    catch (CoreException ex) {
                        logger.warn(
                                "Unable to set the default charset for the workspace root, see inner exception",
                                ex);
                    }
                    project.open(monitor);
                    String modelsFolder = ModelsFolderPreference.fromProject(project)
                            .getModelsFolderPath();
                    IFolder folder = project.getFolder(modelsFolder);
                    folder.create(true, true, null);
                }
                catch (CoreException ex) {
                    ex.printStackTrace();
                }
            }
        };

        // Run project creation operation
        try {
            getContainer().run(true, true, op);

        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        catch (InvocationTargetException e) {
            Throwable t = e.getTargetException();
            if (t instanceof ExecutionException && t.getCause() instanceof CoreException) {
                CoreException cause = (CoreException) t.getCause();
                cause.printStackTrace();
            }
            else {
                e.printStackTrace();
            }
        }

        return project;
    }

    public IProject getNewProject() {
        return project;
    }

    @Override
    public boolean performFinish() {
        createNewProject();

        if (project == null) {
            return false;
        }

        try {
            CacheManager.initNewProject(project);
        }
        catch (Exception e) {
            String message = "Error while creating the default alternatives in the Document Manager of project:"
                    + project.getName();
            logger.error(message, e);
        }

        // IWorkingSet[] workingSets = mainPage.getSelectedWorkingSets();
        // getWorkbench().getWorkingSetManager().addToWorkingSets(newProject,
        // workingSets);
        //
        // updatePerspective();
        selectAndReveal(project);

        return true;
    }

    @SuppressWarnings("static-method")
    protected void selectAndReveal(IResource newResource) {
        selectAndReveal(newResource, CorePlugin.getActiveWorkbenchWindow());
    }

    public static void selectAndReveal(IResource resource, IWorkbenchWindow window) {
        // validate the input
        if (window == null || resource == null) {
            return;
        }
        IWorkbenchPage page = window.getActivePage();
        if (page == null) {
            return;
        }

        // get all the view and editor parts
        List<IWorkbenchPart> parts = Lists.newArrayList();
        IWorkbenchPartReference refs[] = page.getViewReferences();
        for (int i = 0; i < refs.length; i++) {
            IWorkbenchPart part = refs[i].getPart(false);
            if (part != null) {
                parts.add(part);
            }
        }
        refs = page.getEditorReferences();
        for (int i = 0; i < refs.length; i++) {
            if (refs[i].getPart(false) != null) {
                parts.add(refs[i].getPart(false));
            }
        }

        final ISelection selection = new StructuredSelection(resource);
        Iterator<IWorkbenchPart> itr = parts.iterator();
        while (itr.hasNext()) {
            IWorkbenchPart part = itr.next();

            // get the part's ISetSelectionTarget implementation
            ISetSelectionTarget target = null;
            if (part instanceof ISetSelectionTarget) {
                target = (ISetSelectionTarget) part;
            }
            else {
                target = (ISetSelectionTarget) part.getAdapter(ISetSelectionTarget.class);
            }

            if (target != null) {
                // select and reveal resource
                final ISetSelectionTarget finalTarget = target;
                window.getShell().getDisplay().asyncExec(new Runnable() {

                    public void run() {
                        finalTarget.selectReveal(selection);
                    }
                });
            }
        }
    }
}
