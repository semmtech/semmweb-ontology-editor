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


import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.ViewPart;
import org.osgi.framework.BundleContext;

import com.google.common.base.Strings;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.function.FunctionRegistry;
import com.hp.hpl.jena.sparql.pfunction.PropertyFunctionRegistry;
import com.semmtech.plugin.semmweb.core.commands.sourceprovider.CoreCommandStateUpdater;
import com.semmtech.plugin.semmweb.core.forms.editor.OntologyFormEditor;
import com.semmtech.plugin.semmweb.core.model.FileModelMakerManager;
import com.semmtech.plugin.semmweb.core.model.IModelProvider;
import com.semmtech.plugin.semmweb.core.model.ModelEventListenerAdapter;
import com.semmtech.plugin.semmweb.core.model.events.ModelActivatedEvent;
import com.semmtech.plugin.semmweb.core.preferences.DocumentManagerPreference;
import com.semmtech.plugin.semmweb.core.sparql.BuildStringFunction;
import com.semmtech.plugin.semmweb.core.sparql.LabelProviderPropertyFunction;
import com.semmtech.plugin.semmweb.core.sparql.NamespaceLabelProviderPropertyFunction;
import com.semmtech.plugin.semmweb.core.sparql.ObjectCountFunction;
import com.semmtech.plugin.semmweb.core.sparql.OntologyLabelProviderPropertyFunction;
import com.semmtech.plugin.semmweb.core.ui.IOpenResourcesProvider;
import com.semmtech.ui.plugin.EclipseUIPlugin;
import com.semmtech.ui.plugin.PartListener;
import com.semmtech.ui.plugin.WindowAdapter;


/**
 * The activator class, which controls the plug-in life cycle
 * 
 * @author Mike Henrichs
 * 
 */
public class CorePlugin extends EclipseUIPlugin implements IPropertyChangeListener {
    private static final Logger logger = Logger.getLogger(CorePlugin.class);

    public static final String PLUGIN_ID = "com.semmtech.plugin.semmweb.core";
    public static final String DEFAULT_EDITOR_ID = OntologyFormEditor.ID;

    private static CorePlugin plugin;
    private static IEditorPart topEditor;

    private final IPartListener commandStateUpdater;
    private final Listener openDocumentListener;

    /**
     * The constructor of this core plug-in
     */
    public CorePlugin() {
        super(PLUGIN_ID);
        commandStateUpdater = new CoreCommandStateUpdater();
        openDocumentListener = new OpenDocumentListener();
    }

    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
        configureLogger();
        addListeners();
        trackTopEditor();
        registerFunctions();
        registerPropertyFunctions();
        logger.debug("SEMMweb CorePlugin started");
    }

    private void configureLogger() {
        // the logger is configured again in CoreStartup.configurelogger
        LoggerConfiguration.init();
    }

    private void addListeners() {
        getWorkbench().addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(IWorkbenchWindow window) {
                window.getPartService().addPartListener(commandStateUpdater);
                Display display = window.getShell().getDisplay();

                if (display != null) {
                    display.addListener(SWT.OpenDocument, openDocumentListener);
                }
            }
        });
    }

    private void removeListeners() {
        try {
            IWorkbenchWindow window = getActiveWorkbenchWindow();
            if (window != null) {
                IPartService service = window.getPartService();
                if (service != null) {
                    service.removePartListener(commandStateUpdater);
                }
                Shell shell = window.getShell();
                Display display = shell.getDisplay();
                if (display != null) {
                    display.removeListener(SWT.OpenDocument, openDocumentListener);
                }
            }
        }
        catch (Exception ex) {
            logger.error("Error while unregistering listeners", ex);
        }
    }

    protected static void registerFunctions() {
        FunctionRegistry.get().put(ObjectCountFunction.getURI(), ObjectCountFunction.class);
        FunctionRegistry.get().put(BuildStringFunction.getURI(), BuildStringFunction.class);
    }

    protected static void registerPropertyFunctions() {
        PropertyFunctionRegistry.get().put(LabelProviderPropertyFunction.getURI(),
                LabelProviderPropertyFunction.class);
        PropertyFunctionRegistry.get().put(NamespaceLabelProviderPropertyFunction.getURI(),
                NamespaceLabelProviderPropertyFunction.class);
        PropertyFunctionRegistry.get().put(OntologyLabelProviderPropertyFunction.getURI(),
                OntologyLabelProviderPropertyFunction.class);
    }

    private void trackTopEditor() {
        getWorkbench().addWindowListener(new IWindowListener() {
            @Override
            public void windowOpened(IWorkbenchWindow window) {
                final IWorkbenchPage page = window.getActivePage();
                if (page != null) {
                    page.addPartListener((IPartListener) new PartListener() {
                        @Override
                        public void partBroughtToTop(final IWorkbenchPart part) {

                            // Workaround to solve: IP0005-431
                            //
                            // It close immediately the editor after open if the
                            // user selected 'cancel' option
                            if (part instanceof OntologyFormEditor) {
                                if (((OntologyFormEditor) part).forceClose()) {
                                    // Launching the close of the page in the
                                    // async call maybe avoid the error "Blocked
                                    // recursive attempt to close part while
                                    // still in the middle of activating it"
                                    Display.getDefault().asyncExec(new Runnable() {

                                        @Override
                                        public void run() {
                                            page.closeEditor((IEditorPart) part, false);
                                        }
                                    });

                                    return;
                                }
                            }

                            if (part instanceof IEditorPart) {
                                topEditor = (IEditorPart) part;
                            }
                        }

                        @Override
                        public void partClosed(IWorkbenchPart part) {
                            if (topEditor != null) {
                                if ((part instanceof IEditorPart) && (part.equals(topEditor))) {
                                    topEditor = null;
                                }
                            }
                        }
                    });
                }
            }

            @Override
            public void windowActivated(IWorkbenchWindow window) {
            }

            @Override
            public void windowDeactivated(IWorkbenchWindow window) {
            }

            @Override
            public void windowClosed(IWorkbenchWindow window) {
            }

        });
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        removeListeners();
        plugin = null;
        super.stop(context);
        logger.debug("SEMMweb CorePlugin stopped");
    }

    /**
     * Returns the shared singleton instance.
     * 
     * @return the shared instance
     */
    public static CorePlugin getDefault() {
        return plugin;
    }

    /**
     * Returns the active editor.
     * 
     * @return the active editor, or null if no editor is active
     */
    public IEditorPart getActiveEditor() {
        IWorkbench workbench = getWorkbench();
        if (workbench == null) {
            return null;
        }
        IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
        if (window == null) {
            return null;
        }
        IWorkbenchPage page = window.getActivePage();
        if (page == null) {
            return null;
        }
        return page.getActiveEditor();
    }

    /**
     * Returns the top editor, which is the open editor that is currently on top
     * regardless of whether it is currently visible (i.e. a maximized view can
     * hide the editor area, as it were).
     * 
     * @return the top editor, or null if no editor is opened
     */
    public IEditorPart getTopEditor() {
        return topEditor;
    }

    public OntModel getWorkingCopyModel(IFile file) {
        IModelProvider modelProvider = getModelProvider(file);
        if (modelProvider != null && modelProvider.isModelLoaded()) {
            return modelProvider.getOntModel();
        }
        return null;
    }

    /**
     * Returns the active IModelProvider.
     * 
     * @return the active IModelProvider, or null if no provider is currently
     *         active
     */
    public IModelProvider getActiveModelProvider() {
        IEditorPart editor = getTopEditor();
        if (editor instanceof IModelProvider) {
            return (IModelProvider) editor;
        }
        return null;
    }

    public IModelProvider getModelProvider(IFile file) {
        IEditorInput input = new FileEditorInput(file);
        IModelProvider result = null;

        // First search for the IModelProvider with the default editor ID
        result = getModelProvider(input, CorePlugin.DEFAULT_EDITOR_ID);
        if (result != null) {
            return result;
        }

        // If none such editor is available, search for any IModelProvider
        result = getModelProvider(input, null);
        return result;
    }

    public IModelProvider getModelProvider(final IEditorInput input, final String id) {
        IModelProvider result = null;
        int matchFlags = 0;

        if (input != null) {
            matchFlags |= IWorkbenchPage.MATCH_INPUT;
        }
        if (!Strings.isNullOrEmpty(id)) {
            matchFlags |= IWorkbenchPage.MATCH_ID;
        }
        final int flags = matchFlags;

        // Find the IModelProvider editor.
        final IModelProvider[] modelProvider = new IModelProvider[1];
        modelProvider[0] = null;
        Display.getDefault().syncExec(new Runnable() {
            @Override
            public void run() {
                if (getActivePage() == null) {
                    return;
                }
                IEditorReference[] editorReferences = getActivePage().findEditors(input, id, flags);
                for (IEditorReference editorReference : editorReferences) {
                    IEditorPart editor = editorReference.getEditor(false);
                    if (editor instanceof IModelProvider) {
                        modelProvider[0] = (IModelProvider) editor;
                    }
                }
            }
        });
        result = modelProvider[0];

        if (result != null) {
            // Ensure the IModelProvider still exists and is not being disposed.
            if (!ModelProviderRegistry.exists(modelProvider[0].getModelURI())) {
                return null;
            }
        }

        return result;
    }

    public boolean isOpenResourceActive() {
        return (getActiveOpenResource() != null);
    }

    public OntResource getActiveOpenResource() {
        IWorkbenchPart part = getTopEditor();
        if (part instanceof IOpenResourcesProvider) {
            IOpenResourcesProvider resourcesProvider = (IOpenResourcesProvider) part;
            return resourcesProvider.getActiveOpenResource();
        }
        return null;
    }

    public static void showStatusMessage(final String message) {
        showStatusMessage(message, null);
    }

    public static void showStatusMessage(final String message, final Image image) {
        Display.getDefault().syncExec(new Runnable() {
            @Override
            public void run() {
                IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
                IWorkbenchPart part = window.getActivePage().getActivePart();
                if (part instanceof EditorPart) {
                    EditorPart editor = (EditorPart) part;
                    editor.getEditorSite().getActionBars().getStatusLineManager()
                            .setMessage(image, message);
                }
                else if (part instanceof ViewPart) {
                    ViewPart view = (ViewPart) part;
                    view.getViewSite().getActionBars().getStatusLineManager()
                            .setMessage(image, message);
                }
            }
        });
    }

    public void openResource(final Resource resource) {
        Display.getDefault().syncExec(new Runnable() {
            @Override
            public void run() {
                IModelProvider modelProvider = getActiveModelProvider();
                if (modelProvider != null) {
                    openResource(modelProvider, resource);
                }
            }
        });
    }

    private void openResource(final IModelProvider modelProvider, final Resource resource) {
        if (modelProvider instanceof OntologyFormEditor) {
            openResource((OntologyFormEditor) modelProvider, resource);
        }
    }

    private void openResource(final OntologyFormEditor editor, final Resource resource) {
        editor.openResource(resource);
    }

    private class DeferredResourceOpenHandler extends ModelEventListenerAdapter {
        private final IModelProvider modelProvider;
        private final Resource resource;

        public DeferredResourceOpenHandler(final IModelProvider modelProvider,
                final Resource resource) {
            this.modelProvider = modelProvider;
            this.resource = resource;
        }

        @Override
        public void modelActivated(ModelActivatedEvent event) {
            openResource(modelProvider, resource);
            CorePlugin.getDefault().getActiveModelProvider().removeModelEventListener(this);
        }
    }

    public IEditorPart openModelEditor(final IFile file) {
        final IEditorPart[] result = new IEditorPart[] { null };
        Display.getDefault().syncExec(new Runnable() {
            @Override
            public void run() {
                try {
                    IEditorInput input = new FileEditorInput(file);
                    result[0] = CorePlugin.getActivePage().openEditor(input, DEFAULT_EDITOR_ID);
                }
                catch (PartInitException ex) {
                    logger.error("Error occured trying to open file " + file.getName(), ex);
                }
            }
        });
        return result[0];
    }

    public void openResource(final IFile file, final Resource resource) {
        Display.getDefault().syncExec(new Runnable() {
            @Override
            public void run() {
                try {
                    IEditorInput input = new FileEditorInput(file);
                    IWorkbenchPage page = getActivePage();
                    boolean alreadyOpen = false;
                    for (IEditorReference ref : page.getEditorReferences()) {
                        if (ref.getEditorInput().equals(input)
                                && ref.getId().equals(DEFAULT_EDITOR_ID)) {
                            alreadyOpen = true;
                            break;
                        }
                    }
                    IEditorPart editor = page.openEditor(input, DEFAULT_EDITOR_ID);
                    IModelProvider modelProvider = (IModelProvider) editor;
                    if (!alreadyOpen) {
                        DeferredResourceOpenHandler opener = new DeferredResourceOpenHandler(
                                modelProvider, resource);
                        getActiveModelProvider().addModelEventListener(opener);
                    }
                    else {
                        openResource((IModelProvider) editor, resource);
                    }
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    public static void showStatusErrorMessage(final String message) {
        showStatusMessage(message, null);
    }

    public static void showStatusErrorMessage(final String message, final Image image) {
        Display.getDefault().syncExec(new Runnable() {
            @Override
            public void run() {
                IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
                IWorkbenchPart part = window.getActivePage().getActivePart();
                if (part instanceof EditorPart) {
                    EditorPart editor = (EditorPart) part;
                    editor.getEditorSite().getActionBars().getStatusLineManager()
                            .setErrorMessage(image, message);
                }
                else if (part instanceof ViewPart) {
                    ViewPart view = (ViewPart) part;
                    view.getViewSite().getActionBars().getStatusLineManager()
                            .setErrorMessage(image, message);
                }
            }
        });
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        String property = event.getProperty();
        if (property.equals(DocumentManagerPreference.PREFERENCE_PRE_LOADING)
                || property.equals(DocumentManagerPreference.PREFERENCE_DOCUMENT_MANAGER_CONFIG)) {
            logger.debug("Document Manager properties have changed, resetting ModelMaker");

            FileModelMakerManager.getInstance().resetManagers();
        }
    }

    /**
     * Get the active workbench window from the workbench.
     * 
     * @return the active workbench window
     */
    public static IWorkbenchWindow getActiveWorkbenchWindow() {
        return getDefault().getWorkbench().getActiveWorkbenchWindow();
    }

    /**
     * Get the shell of the active workbench window.
     * 
     * @return the active workbench shell
     */
    public static Shell getActiveWorkbenchShell() {
        IWorkbenchWindow window = getActiveWorkbenchWindow();
        if (window != null) {
            return window.getShell();
        }
        return null;
    }

    /**
     * Get the active page of the active workbench window.
     * 
     * @return the active page
     */
    public static IWorkbenchPage getActivePage() {
        IWorkbenchWindow window = getActiveWorkbenchWindow();
        if (window != null) {
            return window.getActivePage();
        }
        return null;
    }

}
