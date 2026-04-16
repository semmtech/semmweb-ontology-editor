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

package com.semmtech.plugin.semmweb.core.actions;


import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.OpenWithMenu;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;
import org.eclipse.ui.navigator.ICommonMenuConstants;
import org.eclipse.ui.navigator.ICommonViewerSite;
import org.eclipse.ui.navigator.ICommonViewerWorkbenchSite;
import org.eclipse.ui.services.IServiceLocator;

import com.google.common.collect.Maps;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.CorePluginImages;
import com.semmtech.plugin.semmweb.core.commands.EnabledCommandContributionItem;
import com.semmtech.plugin.semmweb.core.decorators.ImportDecorator;
import com.semmtech.plugin.semmweb.core.handlers.ClearCacheHandler;
import com.semmtech.plugin.semmweb.core.handlers.DeleteSelectedModelsHandler;
import com.semmtech.plugin.semmweb.core.handlers.DisableImportHandler;
import com.semmtech.plugin.semmweb.core.handlers.DummyImplementationHandler;
import com.semmtech.plugin.semmweb.core.handlers.EditImportHandler;
import com.semmtech.plugin.semmweb.core.handlers.EnableImportHandler;
import com.semmtech.plugin.semmweb.core.handlers.OpenSemanticModelHandler;
import com.semmtech.plugin.semmweb.core.handlers.RefreshCacheHandler;
import com.semmtech.plugin.semmweb.core.handlers.RenameModelHandler;
import com.semmtech.plugin.semmweb.core.handlers.RewriteNamespacesHandler;
import com.semmtech.plugin.semmweb.core.handlers.ViewImportHandler;
import com.semmtech.plugin.semmweb.core.navigator.IImport;
import com.semmtech.plugin.semmweb.core.navigator.IImportCollection;
import com.semmtech.plugin.semmweb.core.navigator.IModel;
import com.semmtech.plugin.semmweb.core.navigator.IModelCollection;
import com.semmtech.plugin.semmweb.core.navigator.INamespace;
import com.semmtech.plugin.semmweb.core.navigator.INamespaceCollection;
import com.semmtech.plugin.semmweb.core.navigator.ISemanticElement;
import com.semmtech.plugin.semmweb.core.navigator.SemanticProjectLabelProvider;
import com.semmtech.ui.plugin.EclipseUIPlugin;
import com.semmtech.ui.plugin.navigator.CommonViewerActionProvider;
import com.semmtech.ui.plugin.util.ClipboardUtils;
import com.semmtech.ui.plugin.util.Selections;


public class SemanticProjectActionProvider extends CommonViewerActionProvider {

    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(SemanticProjectActionProvider.class);

    public static final String NEW_MENU_NAME = "semanticProject.new.menu";//$NON-NLS-1$
    public static final String MENU_ADD = "semanticProject.menu.add";

    public static final String MENU_TEAM = "semanticProject.menu.team";
    public static final String MENU_PUBLISH = "semanticProject.menu.publish";
    public static final String MENU_FETCH = "semanticProject.menu.fetch";

    private static final boolean NO_HISTORY = true;

    private IAction newModelAction;
    private IAction importModelAction;
    private IAction pasteModelAction;

    private static IRunCondition modelActionsRunCondition;
    private IWorkbenchWindow window;

    public SemanticProjectActionProvider() {

    }

    @Override
    public void init(ICommonActionExtensionSite site) {
        super.init(site);

        ICommonViewerSite viewerSite = site.getViewSite();
        if (viewerSite instanceof ICommonViewerWorkbenchSite) {
            window = getWindow();
            newModelAction = new NewOntologyFileAction(window);
            newModelAction.setText("RDF/OWL Model");

            importModelAction = new ImportOntologyAction(window);
            pasteModelAction = new PasteModelAction(window);
        }
    }

    @Override
    public void fillContextMenu(IMenuManager parent) {
        // The paste action is enabled only if the Clipboard contains files.
        pasteModelAction.setEnabled(!ClipboardUtils.getFiles().isEmpty());

        IStructuredSelection selection = Selections.toStructured(getSelection());
        IServiceLocator locator = getServiceLocator();

        if (selection == null || !selection.isEmpty()) {
            initCopyModelsAction(parent, locator, selection);

            initModelCollectionActions(parent, locator, selection);
            initModelActions(parent, locator, selection);

            initImportCollectionActions(parent, locator, selection);
            initImportActions(parent, locator, selection);

            initNamespaceCollectionActions(parent, locator, selection);
            initNamespaceActions(parent, locator, selection);
        }
    }

    protected void initCopyModelsAction(IMenuManager parent, IServiceLocator locator,
            IStructuredSelection selection) {
        if (Selections.hasAllOfTypes(selection, IModelCollection.class, IModel.class)) {
            // This action has been separated from copying models denoted by
            // IImport objects, as copying a selection containing imports
            // alongside models and model collections can be confusing to the
            // user.
            CopyModelAction copyModelAction = new CopyModelAction(window);
            parent.appendToGroup(ICommonMenuConstants.GROUP_EDIT, copyModelAction);
        }
    }

    /**
     * Adds applicable actions to <code>parent</code> if the
     * <code>selection</code> contains IModelCollection objects only.
     */
    protected void initModelCollectionActions(IMenuManager parent, IServiceLocator locator,
            IStructuredSelection selection) {
        if (!Selections.hasAllOfType(selection, IModelCollection.class)) {
            return;
        }
        List<IModelCollection> collections = Selections.retrieveAllAsType(selection,
                IModelCollection.class);

        // (getting some assisting variables)
        IModelCollection soleCollection = null;
        if (collections.size() == 1) {
            soleCollection = collections.get(0);
        }

        if (soleCollection != null) {
            IMenuManager newMenu = new MenuManager("New", NEW_MENU_NAME);
            IMenuManager addMenu = new MenuManager("Add", MENU_ADD);

            newMenu.add(newModelAction);
            addMenu.add(importModelAction);

            parent.appendToGroup(ICommonMenuConstants.GROUP_NEW, newMenu);
            parent.appendToGroup(ICommonMenuConstants.GROUP_NEW, addMenu);
        }

        if (soleCollection != null) {
            parent.appendToGroup(ICommonMenuConstants.GROUP_EDIT, pasteModelAction);

            parent.appendToGroup(ICommonMenuConstants.GROUP_REORGANIZE,
                    DummyImplementationHandler.createCommand(locator, "Move...", null));
        }
    }

    /**
     * Adds applicable actions to <code>parent</code> if the
     * <code>selection</code> contains IModel objects only.
     */
    protected void initModelActions(IMenuManager parent, IServiceLocator locator,
            IStructuredSelection selection) {
        if (!Selections.hasAllOfType(selection, IModel.class)) {
            return;
        }
        List<IModel> models = Selections.retrieveAllAsType(selection, IModel.class);

        // (getting some assisting variables)
        IModel soleModel = null;
        if (models.size() == 1) {
            soleModel = models.get(0);
        }

        if (soleModel != null) {
            IResource resource = soleModel.getResource();
            if (!resource.exists()) {
                return;
            }

            // Open
            CommandContributionItem openSemanticModel = OpenSemanticModelHandler.createCommand(
                    locator, "Open", null, resource.getFullPath().toString());
            parent.appendToGroup(ICommonMenuConstants.GROUP_OPEN, openSemanticModel);

            // Open With
            addOpenWithMenu(parent, resource);

            // Copy URI
            if (parent.find(CopyModelAction.ID) != null) {
                parent.insertBefore(CopyModelAction.ID, new CopyURIActions.CopyModelURIAction(
                        soleModel));
            }
            else {
                parent.appendToGroup(ICommonMenuConstants.GROUP_EDIT,
                        new CopyURIActions.CopyModelURIAction(soleModel));
            }
        }

        if (soleModel != null) {
            // Paste
            parent.appendToGroup(ICommonMenuConstants.GROUP_EDIT, pasteModelAction);
        }

        // Delete
        IContributionItem item = DeleteSelectedModelsHandler.createCommand(locator, "Delete",
                CorePluginImages.IMG_DELETE);
        parent.appendToGroup(ICommonMenuConstants.GROUP_EDIT, item);

        if (soleModel != null) {
            // Move
            IAction moveModelAction = new MoveModelAction(window);
            parent.appendToGroup(ICommonMenuConstants.GROUP_REORGANIZE, moveModelAction);

            // Rename
            parent.appendToGroup(ICommonMenuConstants.GROUP_REORGANIZE,
                    RenameModelHandler.createCommand(locator, "Rename...", null));

            // Target
            parent.appendToGroup(ICommonMenuConstants.GROUP_ADDITIONS, new MenuManager("Publish",
                    MENU_PUBLISH));
            parent.appendToGroup(ICommonMenuConstants.GROUP_ADDITIONS, new MenuManager("Fetch",
                    MENU_FETCH));
        }
    }

    /**
     * Adds applicable actions to <code>parent</code> if the
     * <code>selection</code> contains IImportCollection objects only.
     */
    protected void initImportCollectionActions(IMenuManager parent, IServiceLocator locator,
            IStructuredSelection selection) {
        if (!Selections.hasAllOfType(selection, IImportCollection.class)) {
            return;
        }
        List<IImportCollection> collections = Selections.retrieveAllAsType(selection,
                IImportCollection.class);

        // (getting some assisting variables)
        IImportCollection soleCollection = null;
        if (collections.size() == 1) {
            soleCollection = collections.get(0);
        }

        if (soleCollection != null) {
            IMenuManager submenu = new MenuManager("New", NEW_MENU_NAME);

            IModel model = (IModel) soleCollection.getAncestor(ISemanticElement.MODEL);
            IFile modelFile = (IFile) model.getResource();

            Resource sourceOntology = null;
            ModelFileAction action = new ImportActions.AddImport(modelFile, model, sourceOntology,
                    null, NO_HISTORY);
            action.setText("Import");
            action.setImageDescriptor(CorePlugin.getDefault().getImageDescriptor(
                    CorePluginImages.IMG_IMPORT_ONTOLOGY_ADD));
            action.addRunCondition(getModelActionsRunCondition());
            submenu.add(action);

            parent.appendToGroup(ICommonMenuConstants.GROUP_NEW, submenu);
        }

        parent.appendToGroup(ICommonMenuConstants.GROUP_BUILD, DummyImplementationHandler
                .createCommand(locator, "Refresh", CorePluginImages.IMG_REFRESH));
    }

    /**
     * Adds applicable actions to <code>parent</code> if the
     * <code>selection</code> contains IImport objects only. Note that Copy and
     * Delete are added here as well, because IImports shouldn't be copied or
     * deleted alongside other kinds of elements.
     */
    protected void initImportActions(IMenuManager parent, IServiceLocator locator,
            IStructuredSelection selection) {
        if (!Selections.hasAllOfType(selection, IImport.class)) {
            return;
        }
        List<IImport> imports = Selections.retrieveAllAsType(selection, IImport.class);

        // (getting some assisting variables)
        boolean allCached = true;
        boolean allExternal = true;
        boolean allHaveResource = true;
        for (IImport immport : imports) {
            if (!immport.isCached()) {
                allCached = false;
            }
            if (!immport.isExternal()) {
                allExternal = false;
            }
            if (immport.getAdapter(IResource.class) == null) {
                allHaveResource = false;
            }
        }

        IImport soleImport = null;
        IResource soleImportResource = null;
        if (imports.size() == 1) {
            soleImport = imports.get(0);
            soleImportResource = (IResource) soleImport.getAdapter(IResource.class);
        }

        boolean disabled = false;
        if (soleImport != null) {
            disabled = soleImport.isDisabled();
            if (soleImport.isWorkspace()) {
                String openLabel = "Open";
                // Open (i.e. Edit)
                EnabledCommandContributionItem editCommand = EditImportHandler.createCommand(
                        locator, openLabel, null);
                parent.appendToGroup(ICommonMenuConstants.GROUP_OPEN, editCommand);

                // Open With
                addOpenWithMenu(parent, soleImportResource, openLabel);
            }
            else {
                // View (i.e. Open in read-only mode)
                CommandContributionItem openImportItem = ViewImportHandler.createCommand(locator,
                        "View", null);
                parent.appendToGroup(ICommonMenuConstants.GROUP_OPEN, openImportItem);

                // Edit
                EnabledCommandContributionItem editCommand = EditImportHandler.createCommand(
                        locator, "Edit", CorePluginImages.IMG_ONTOLOGY_FILE);
                parent.appendToGroup(ICommonMenuConstants.GROUP_OPEN, editCommand);
            }

            // Copy URI
            parent.appendToGroup(ICommonMenuConstants.GROUP_EDIT,
                    new CopyURIActions.CopyImportURIAction(soleImport));
        }

        // Copy
        CopyModelAction copyModelAction = new CopyModelAction(window);
        copyModelAction.setEnabled(allHaveResource);
        parent.appendToGroup(ICommonMenuConstants.GROUP_EDIT, copyModelAction);

        // Delete
        IAction action = new RemoveImportsAction(imports);
        parent.appendToGroup(ICommonMenuConstants.GROUP_EDIT, action);

        // Cache
        if (disabled) {
            IContributionItem item = EnableImportHandler.createCommand(locator, "Enable", null);
            parent.appendToGroup(ICommonMenuConstants.GROUP_PORT, item);
        }
        else {
            IContributionItem item = DisableImportHandler.createCommand(locator, "Disable", null);
            parent.appendToGroup(ICommonMenuConstants.GROUP_PORT, item);
        }

        if (allCached) {
            IMenuManager submenu = new MenuManager("Cache", "importCaches");

            // Cache -> Update
            submenu.add(RefreshCacheHandler.createCommand(locator, "Update",
                    CorePluginImages.IMG_REFRESH));

            // Cache -> Clear
            submenu.add(ClearCacheHandler.createCommand(locator, "Clear", null));

            parent.appendToGroup(ICommonMenuConstants.GROUP_PORT, submenu);
        }
        else if (allExternal) {
            IContributionItem item = RefreshCacheHandler.createCommand(locator, "Cache", null);
            parent.appendToGroup(ICommonMenuConstants.GROUP_PORT, item);
        }

        if (soleImport != null) {
            // Change alternate location
            IAction changeUrlAction = new DocumentManagerActions.ChangeAlternateLocation(
                    soleImport.getProject(), soleImport.getURI());
            parent.appendToGroup(ICommonMenuConstants.GROUP_PORT, changeUrlAction);
        }
    }

    public static class RemoveImportsAction extends ConditionalAction {
        protected final List<IImport> imports;

        public RemoveImportsAction(List<IImport> imports) {
            super();
            this.imports = imports;

            addRunCondition(getDeleteImportsRunCondition(imports));
            addRunCondition(getModelActionsRunCondition());
        }

        @Override
        public String getText() {
            return "Delete";
        }

        @Override
        public ImageDescriptor getImageDescriptor() {
            return CorePlugin.getDefault().getImageDescriptor(CorePluginImages.IMG_DELETE);
        }

        @Override
        public void run() {
            if (!satisfiesRunConditions()) {
                return;
            }

            boolean indirect = false;
            for (IImport immport : imports) {
                if (immport.isDirect()) {
                    IModel model = (IModel) immport.getAncestor(ISemanticElement.MODEL);
                    IFile modelFile = (IFile) model.getResource();
                    ModelFileAction action = new ImportActions.RemoveImport(modelFile, null,
                            ModelFactory.createDefaultModel().createResource(immport.getURI()),
                            NO_HISTORY);
                    action.run();
                }
                else {
                    indirect = true;
                }
            }

            if (indirect) {
                Shell shell = EclipseUIPlugin.getStandardDisplay().getActiveShell();
                MessageDialog.openInformation(shell, "Indirect Imports",
                        "The selection contains indirect imports, which can't be deleted");
            }
        }
    }

    /**
     * Adds applicable actions to <code>parent</code> if the
     * <code>selection</code> contains INamespaceCollection objects only.
     */
    protected void initNamespaceCollectionActions(IMenuManager parent, IServiceLocator locator,
            IStructuredSelection selection) {
        if (!Selections.hasAllOfType(selection, INamespaceCollection.class)) {
            return;
        }
        List<INamespaceCollection> collections = Selections.retrieveAllAsType(selection,
                INamespaceCollection.class);

        // (getting some assisting variables)
        INamespaceCollection soleCollection = null;
        IModel soleModel = null;
        IFile soleModelFile = null;
        if (collections.size() == 1) {
            soleCollection = collections.get(0);
            soleModel = (IModel) soleCollection.getAncestor(ISemanticElement.MODEL);
            soleModelFile = (IFile) soleModel.getResource();
        }

        if (soleCollection != null) {
            // Add prefixed namespace
            ModelFileAction action = new NamespaceActions.AddPrefix(soleModelFile, null, NO_HISTORY);
            action.setText("Add prefixed namespace");
            action.addRunCondition(getModelActionsRunCondition());
            parent.appendToGroup(ICommonMenuConstants.GROUP_EDIT, action);

            // Rewrite namespaces
            parent.appendToGroup(ICommonMenuConstants.GROUP_EDIT,
                    RewriteNamespacesHandler.createCommand(locator, "Rewrite...", null));
        }
    }

    /**
     * Adds applicable actions to <code>parent</code> if the
     * <code>selection</code> contains INamespace objects only.
     */
    protected void initNamespaceActions(IMenuManager parent, IServiceLocator locator,
            IStructuredSelection selection) {
        if (!Selections.hasAllOfType(selection, INamespace.class)) {
            return;
        }
        List<INamespace> namespaces = Selections.retrieveAllAsType(selection, INamespace.class);

        // (getting some assisting variables)
        boolean allSameModel = true;
        for (INamespace namespace : namespaces) {
            IModel model = (IModel) namespace.getAncestor(ISemanticElement.MODEL);
            if (model == null
                    || !model.equals(namespaces.get(0).getAncestor(ISemanticElement.MODEL))) {
                allSameModel = false;
            }
        }

        INamespace soleNamespace = null;
        if (namespaces.size() == 1) {
            soleNamespace = namespaces.get(0);
        }

        if (soleNamespace != null) {
            ModelFileAction action = null;

            String uri = soleNamespace.getURI();
            String prefix = soleNamespace.getPrefix();

            IModel model = (IModel) soleNamespace.getAncestor(ISemanticElement.MODEL);
            IFile modelFile = (IFile) model.getResource();

            if (prefix == null) {
                // Add prefix
                action = new NamespaceActions.AddPrefix(modelFile, uri, NO_HISTORY);
                action.setText("Add prefix");
                action.addRunCondition(getModelActionsRunCondition());
                parent.appendToGroup(ICommonMenuConstants.GROUP_EDIT, action);
            }
            else {
                // Edit prefix
                action = new NamespaceActions.EditPrefix(modelFile, prefix, NO_HISTORY);
                action.setText("Edit prefix");
                action.addRunCondition(getModelActionsRunCondition());
                parent.appendToGroup(ICommonMenuConstants.GROUP_EDIT, action);

                // Remove prefix
                action = new NamespaceActions.RemovePrefix(modelFile, prefix, NO_HISTORY);
                action.setText("Remove prefix");
                action.addRunCondition(getModelActionsRunCondition());
                parent.appendToGroup(ICommonMenuConstants.GROUP_EDIT, action);
            }

            // Rewrite
            IAction rewriteAction = new NamespaceActions.Rewrite(modelFile, uri, prefix);
            rewriteAction.setText("Rewrite...");
            parent.appendToGroup(ICommonMenuConstants.GROUP_EDIT, rewriteAction);
        }

        if (soleNamespace == null && allSameModel) {
            // Rewrite
            parent.appendToGroup(ICommonMenuConstants.GROUP_EDIT,
                    RewriteNamespacesHandler.createCommand(locator, "Rewrite...", null));
        }
    }

    public static IRunCondition getModelActionsRunCondition() {
        if (modelActionsRunCondition == null) {
            modelActionsRunCondition = new IRunCondition() {
                private boolean ignore = false;

                @Override
                public boolean isSatisfied(IAction action) {
                    if (ignore) {
                        return true;
                    }

                    Shell shell = EclipseUIPlugin.getStandardDisplay().getActiveShell();
                    String title = "Can't undo model operations triggered from this view.";
                    String message = "You are about to make a change to the model. Any such changes triggered from the Project Navigator will not be undoable.";
                    String toggleMessage = "Don't show this message again.";
                    boolean toggleState = false;
                    MessageDialogWithToggle dialog = MessageDialogWithToggle.openOkCancelConfirm(
                            shell, title, message, toggleMessage, toggleState, null, null);
                    ignore = dialog.getToggleState();

                    return (dialog.getReturnCode() == Window.OK);
                }
            };
        }
        return modelActionsRunCondition;
    }

    public static IRunCondition getDeleteImportsRunCondition(final List<IImport> imports) {
        return new IRunCondition() {
            @Override
            public boolean isSatisfied(IAction action) {
                boolean workspaceImportExists = false;

                Shell shell = EclipseUIPlugin.getStandardDisplay().getActiveShell();

                for (IImport immport : imports) {
                    if (immport.isWorkspace()) {
                        workspaceImportExists = true;
                    }

                    if (!immport.isDirect()) {
                        if (imports.size() == 1) {
                            new DeleteImportAction(immport).run();
                            return false;
                        }

                        MessageDialog.openInformation(shell, "Indirect Imports",
                                "The selection contains indirect imports, which can't be deleted.");
                        return false;

                    }
                }

                if (!workspaceImportExists) {
                    return true;
                }

                String title = "This action will delete import statements only";
                String message = "You are about to delete import statements in one or more models. Any existing working copies in your workspace of the imported ontologies will still exist afterwards and, if your intention is to remove these as well, will need to be deleted manually.";
                return MessageDialog.openConfirm(shell, title, message);
            }
        };
    }

    private void addOpenWithMenu(IMenuManager aMenu, IResource selected) {
        addOpenWithMenu(aMenu, selected, "Open");
    }

    private void addOpenWithMenu(IMenuManager aMenu, IResource selected, String openViewLabel) {

        if (selected == null || selected.getType() != IResource.FILE) {
            return;
        }

        // Create a menu flyout.
        IMenuManager submenu = new MenuManager(openViewLabel + " With",
                ICommonMenuConstants.GROUP_OPEN_WITH);

        IContributionItem openWithMenu = new OpenWithMenu(getSite().getPage(), selected);

        submenu.add(new GroupMarker(ICommonMenuConstants.GROUP_TOP));
        submenu.add(openWithMenu);
        submenu.add(new GroupMarker(ICommonMenuConstants.GROUP_ADDITIONS));

        // Add the submenu.
        if (submenu.getItems().length > 2 && submenu.isEnabled()) {
            aMenu.appendToGroup(ICommonMenuConstants.GROUP_OPEN, submenu);
        }
    }

    private final static class DeleteImportAction extends Action {

        private final IImport immport;
        private final Map<String, String> importUriToLabel;

        DeleteImportAction(IImport immport) {
            this.immport = immport;
            this.importUriToLabel = Maps.newHashMap();

            if (!(immport.getParent() instanceof IImportCollection)) {
                return;
            }

            IImportCollection importCollection = (IImportCollection) immport.getParent();
            SemanticProjectLabelProvider labelProvider = new SemanticProjectLabelProvider();
            ImportDecorator decorator = new ImportDecorator();

            for (ISemanticElement se : importCollection.getChildrenByType(ISemanticElement.IMPORT)) {
                IImport i = (IImport) se;
                String label = labelProvider.getText(i);
                importUriToLabel.put(i.getURI(), decorator.decorateText(label, i));
            }
        }

        @Override
        public void run() {
            Shell shell = EclipseUIPlugin.getStandardDisplay().getActiveShell();
            String title = "No ontologies to remove import from";
            String message = "There are no ontologies in the model from which the specified import can be removed. "
                    + "Any imports performed by ontologies defined in other, imported models can't be removed in this model. "
                    + "The import you wish to remove is imported by the following ontology:\n\n";

            for (String uri : immport.getImportedByOntologyURIs()) {
                String label = importUriToLabel.get(uri);

                if (label == null) {
                    label = uri;
                }

                message += "- " + label;
            }

            MessageDialog.openInformation(shell, title, message);
        }

    }
}