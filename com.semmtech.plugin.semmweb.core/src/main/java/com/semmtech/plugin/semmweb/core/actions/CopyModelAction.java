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


import java.net.URI;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchCommandConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.google.common.collect.Lists;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.CorePluginImages;
import com.semmtech.plugin.semmweb.core.navigator.IImport;
import com.semmtech.plugin.semmweb.core.navigator.IModel;
import com.semmtech.plugin.semmweb.core.navigator.IModelCollection;
import com.semmtech.plugin.semmweb.core.navigator.ISemanticElement;
import com.semmtech.plugin.semmweb.core.preferences.DocumentManagerPreference;
import com.semmtech.ui.plugin.util.ClipboardUtils;
import com.semmtech.ui.plugin.util.Selections;


/**
 * Perform the copy of one or more files related to the selected models in the
 * navigator. IModelCollection, IModel, and IImport are considered.
 * 
 * @author Simone Rondelli
 * 
 */
public class CopyModelAction extends Action {
    public static String ID = "com.semmtech.plugin.semmweb.core.actions.copyModel";

    /**
     * The workbench window this action will run in
     */
    private IWorkbenchWindow window;

    /**
     * This action is called if the selected element(s) aren't instance of
     * IModel or IModelCollection
     */
    private InternalCopyAction defaultCopyAction;

    public CopyModelAction(IWorkbenchWindow window) {
        this.window = window;
        setText("Copy@Ctrl+C");
        setImageDescriptor(CorePlugin.getDefault().getImageDescriptor(CorePluginImages.IMG_COPY));
        setToolTipText("Copy selected resource(s)");
        setAccelerator(SWT.MOD1 | 'C');

        ISharedImages images = PlatformUI.getWorkbench().getSharedImages();
        defaultCopyAction = new InternalCopyAction(window.getShell(), new Clipboard(window
                .getShell().getDisplay()));
        defaultCopyAction.setDisabledImageDescriptor(images
                .getImageDescriptor(ISharedImages.IMG_TOOL_COPY_DISABLED));
        defaultCopyAction
                .setImageDescriptor(images.getImageDescriptor(ISharedImages.IMG_TOOL_COPY));
        defaultCopyAction.setActionDefinitionId(IWorkbenchCommandConstants.EDIT_COPY);
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public void run() {
        ISelection selection = window.getSelectionService().getSelection();
        List<ISemanticElement> selectedElements = Selections.retrieveAllAsType(selection,
                ISemanticElement.class);

        if (selectedElements.isEmpty()
                || !Selections.hasAllOfTypes(selection, IModelCollection.class, IModel.class,
                        IImport.class)) {
            // Perhaps the default copy action is able to perform the copy
            defaultCopyAction.selectionChanged((IStructuredSelection) selection);
            defaultCopyAction.run();
            return;
        }

        List<String> fileNames = Lists.newArrayListWithCapacity(selectedElements.size());

        for (ISemanticElement selected : selectedElements) {
            IFile file = null;

            if (selected instanceof IModelCollection) {
                IModelCollection selectedModelCollection = (IModelCollection) selected;
                for (ISemanticElement semanticElement : selectedModelCollection
                        .getChildrenByType(ISemanticElement.MODEL)) {
                    IModel model = (IModel) semanticElement;
                    file = (IFile) model.getResource();
                }
            }
            else if (selected instanceof IModel) {
                IModel selectedModel = (IModel) selected;
                file = (IFile) selectedModel.getResource();
            }
            else if (selected instanceof IImport) {
                // The default copy action is able to retrieve only the resource
                // of a local import but not a cached import and, in case of web
                // import, an exception is thrown
                IImport selctedImport = (IImport) selected;

                DocumentManagerPreference preferences = DocumentManagerPreference
                        .fromProject(selctedImport.getProject());
                String altUrl = preferences.getAltURL(selctedImport.getURI(), false);
                IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
                IFile[] files = root
                        .findFilesForLocationURI(URI.create(altUrl.replace(" ", "%20")));
                if (files.length > 0) {
                    file = files[0];
                }
            }

            if (file != null) {
                String filename = file.getRawLocation().toOSString();
                if (!fileNames.contains(filename)) {
                    fileNames.add(filename);
                }
            }
        }

        if (!fileNames.isEmpty()) {
            ClipboardUtils.copyFiles(fileNames);
        }
    }
}
