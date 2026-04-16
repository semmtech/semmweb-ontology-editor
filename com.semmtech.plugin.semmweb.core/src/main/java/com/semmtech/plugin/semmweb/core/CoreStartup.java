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

package com.semmtech.plugin.semmweb.core;


import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.NotEnabledException;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPreferenceConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.internal.IPreferenceConstants;
import org.eclipse.ui.internal.Workbench;
import org.eclipse.ui.internal.ide.IDEWorkbenchMessages;
import org.eclipse.ui.internal.ide.dialogs.CleanDialog;
import org.eclipse.ui.internal.util.PrefUtil;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.progress.IProgressConstants;
import org.eclipse.ui.progress.IProgressConstants2;
import org.eclipse.ui.services.IServiceLocator;
import org.topbraid.spin.vocabulary.SP;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.semmtech.jena.readers.JenaReadersUtil;
import com.semmtech.plugin.semmweb.core.builders.SemanticProjectBuilder;
import com.semmtech.plugin.semmweb.core.extensionpoint.IKnowledgeLevelListener;
import com.semmtech.plugin.semmweb.core.model.Person;
import com.semmtech.plugin.semmweb.core.preferences.EditorPreference;
import com.semmtech.plugin.semmweb.core.preferences.JenaPreference;
import com.semmtech.plugin.semmweb.core.preferences.UserPreference;
import com.semmtech.plugin.semmweb.core.wizards.InitializeEditorWizard;


/**
 * This class is used by the extension point org.eclipse.ui.startup
 * 
 * @author Mike Henrichs
 * 
 */
@SuppressWarnings("restriction")
public class CoreStartup implements IStartup {
    private final Logger logger = Logger.getLogger(CoreStartup.class);

    @Override
    public void earlyStartup() {
        logger.debug("earlyStartup() called");

        initialStartup();
        configureLogger();
        logger.debug("Charset.defaultCharset = " + Charset.defaultCharset().toString());
        SP.getURI(); // Force SP.init() to run, adding its personalities to
                     // BuiltinPersonalities.model.
        checkConnectionTimeoutPreference();
        forceBuild();
        disableStoreState();
        registerProjectCloseListener();
        setWelcomePageSettings();
        checkUserPreference();
    }

    private void initialStartup() {

        if (!EditorPreference.hasRunBefore()) {
            Display.getDefault().syncExec(new Runnable() {
                @Override
                public void run() {
                    IServiceLocator serviceLocator = PlatformUI.getWorkbench();
                    ICommandService commandService = (ICommandService) serviceLocator
                            .getService(ICommandService.class);

                    // Lookup commmand with its ID
                    Command command = commandService
                            .getCommand("org.eclipse.equinox.p2.ui.sdk.update");
                    try {
                        command.executeWithChecks(new ExecutionEvent());
                    }
                    catch (ExecutionException | NotDefinedException | NotEnabledException
                            | NotHandledException e) {

                        logger.error("There was an error trying to execute the initial update", e);
                    }

                }
            });

            EditorPreference.setHasRunBefore(true);
        }

    }

    /**
     * Configure log4j to print the log in a file located in the installation
     * directory.
     * 
     * NB: Although there is the configureLogger() method in CorePlugin if this
     * code is put there doesn't work
     */
    private void configureLogger() {
        if (!LoggerConfiguration.init()) {
            Display.getDefault().syncExec(new Runnable() {

                @Override
                public void run() {
                    MessageDialog.openError(Display.getDefault().getActiveShell(), "Logger Error",
                            "An error occurred while configuring the logger.");
                }
            });
        }
    }

    /**
     * Sets the default setting for "Restore editor state on startup" to false.
     * And also set the value of the "Close editors on exit" to true.
     */
    private void disableStoreState() {
        try {
            IPreferenceStore store = Workbench.getInstance().getPreferenceStore();
            store.setDefault(IPreferenceConstants.USE_IPERSISTABLE_EDITORS, false);

            store = PlatformUI.getPreferenceStore();
            store.setValue(IWorkbenchPreferenceConstants.CLOSE_EDITORS_ON_EXIT, true);
        }
        catch (Exception ex) {
            logger.error("Setting the persistence of editors appeared to have encountered an error");
        }

    }

    private void registerProjectCloseListener() {

        ResourcesPlugin.getWorkspace().addResourceChangeListener(new IResourceChangeListener() {
            @Override
            public void resourceChanged(final IResourceChangeEvent event) {
                if (event.getType() == IResourceChangeEvent.PRE_CLOSE) {
                    if (event.getResource() instanceof IProject) {
                        Display.getDefault().syncExec(new Runnable() {

                            @Override
                            public void run() {
                                IProject project = (IProject) event.getResource();
                                IWorkbenchPage page = CorePlugin.getActivePage();
                                List<IEditorReference> closingRefs = Lists.newArrayList();
                                if (page != null && page.getEditorReferences() != null) {
                                    for (IEditorReference reference : page.getEditorReferences()) {
                                        IEditorInput input = null;
                                        try {
                                            input = reference.getEditorInput();
                                        }
                                        catch (Exception ex) {
                                            ex.printStackTrace();
                                        }
                                        if (input instanceof FileEditorInput) {
                                            FileEditorInput fileInput = (FileEditorInput) input;
                                            fileInput.getFile().getProject();
                                            if (project.equals(fileInput.getFile().getProject())) {
                                                closingRefs.add(reference);
                                            }
                                        }
                                    }
                                }
                                if (page != null && !closingRefs.isEmpty()) {
                                    IEditorReference[] refs = new IEditorReference[closingRefs
                                            .size()];
                                    closingRefs.toArray(refs);
                                    page.closeEditors(refs, true);
                                }
                            }
                        });

                    }
                }
            }
        });
    }

    private void checkUserPreference() {
        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                checkCreator();
                checkKnowledgeLevel();
            }
        });

        Job refreshWorkspace = new RefreshWorkspaceRootJob();
        refreshWorkspace.schedule();
    }

    private void checkCreator() {
        Person person = UserPreference.getPerson();
        if (person == null) {

            InitializeEditorWizard wizard = new InitializeEditorWizard();
            Shell activeShell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
            WizardDialog dialog = new WizardDialog(activeShell, wizard);
            dialog.create();
            dialog.open();
        }
    }

    private void checkKnowledgeLevel() {
        String knowlegdgeLevel = UserPreference.getKnowledgeLevel();
        if (Strings.isNullOrEmpty(knowlegdgeLevel)
                || knowlegdgeLevel.equals(UserPreference.KNOWLEDGE_LEVEL_UNKOWN)) {
            setKnowledgeLevel(UserPreference.KNOWLEDGE_LEVEL_EXPERT);
        }
    }

    private static void setKnowledgeLevel(final String knowledgeLevel) {
        UserPreference.setKnowledgeLevel(knowledgeLevel);

        final List<IKnowledgeLevelListener> managers = Lists.newArrayList();
        String KNOWLEDGE_LEVELS_ID = "knowledgeLevels";
        IConfigurationElement[] config = Platform.getExtensionRegistry()
                .getConfigurationElementsFor(CorePlugin.PLUGIN_ID, KNOWLEDGE_LEVELS_ID);
        try {
            for (IConfigurationElement element : config) {
                Object object = element.createExecutableExtension("class");
                if (object instanceof IKnowledgeLevelListener)
                    managers.add((IKnowledgeLevelListener) object);
            }
        }
        catch (CoreException ex) {
            ex.printStackTrace();
        }

        for (IKnowledgeLevelListener manager : managers) {
            manager.updateKnowledgeLevel(knowledgeLevel);
        }
    }

    private class RefreshWorkspaceRootJob extends Job {
        public RefreshWorkspaceRootJob() {
            super("Refreshing Workspace");
        }

        @Override
        protected IStatus run(IProgressMonitor monitor) {
            IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
            try {
                monitor.beginTask("Refreshing Workspace", 1);
                Thread.sleep(1000);
                root.refreshLocal(IResource.DEPTH_INFINITE, monitor);
                monitor.worked(1);
            }
            catch (CoreException e) {
                e.printStackTrace();
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
            monitor.done();
            return Status.OK_STATUS;
        }
    }

    private static void setWelcomePageSettings() {
        PlatformUI.getPreferenceStore().setValue(IWorkbenchPreferenceConstants.SHOW_INTRO, true);
        PrefUtil.savePrefs();
    }

    private static void checkConnectionTimeoutPreference() {
        int timeout = JenaPreference.getConnectionTimeout();
        JenaReadersUtil.setConnectionTimeout(timeout);
    }

    /**
     * Perform a CLEAN and BUILD of the workspace. I've got the code from the
     * {@link CleanDialog} class
     */
    private void forceBuild() {
        Job buildJob = new Job(IDEWorkbenchMessages.GlobalBuildAction_jobTitle) {
            protected IStatus run(IProgressMonitor monitor) {
                monitor.beginTask("Full workspace build", 100);
                try {
                    monitor.subTask("Pre workspace build");
                    Map<String, String> args = Maps.newHashMap();
                    args.put(SemanticProjectBuilder.PRE_BUILD_PARAM, "true");

                    for (IProject prj : ResourcesPlugin.getWorkspace().getRoot().getProjects()) {
                        prj.build(IncrementalProjectBuilder.FULL_BUILD,
                                SemanticProjectBuilder.BUILDER_ID, args, monitor);
                    }

                    monitor.subTask("Full workspace build");
                    ResourcesPlugin.getWorkspace().build(IncrementalProjectBuilder.FULL_BUILD,
                            monitor);
                }
                catch (CoreException e) {
                    return e.getStatus();
                }
                finally {
                    monitor.done();
                }
                return Status.OK_STATUS;
            }

            public boolean belongsTo(Object family) {
                return ResourcesPlugin.FAMILY_MANUAL_BUILD == family;
            }
        };
        buildJob.setUser(false);
        buildJob.setProperty(IProgressConstants2.SHOW_IN_TASKBAR_ICON_PROPERTY, Boolean.TRUE);
        buildJob.setProperty(IProgressConstants.PROPERTY_IN_DIALOG, Boolean.FALSE);
        buildJob.schedule();
    }
}
