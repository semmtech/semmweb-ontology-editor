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

package com.semmtech.plugin.semmweb.core.navigator;


import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.ide.ResourceUtil;
import org.eclipse.ui.navigator.ILinkHelper;
import org.eclipse.ui.part.FileEditorInput;

import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.util.ResourcesUtil;
import com.semmtech.plugin.semmweb.core.util.SemanticProjectUtils;


public class ResourceLinkHelper implements ILinkHelper {

    @Override
    public IStructuredSelection findSelection(IEditorInput anInput) {
        IFile file = ResourceUtil.getFile(anInput);
        if (file != null) {
            // Find the corresponding IModel
            IProject project = file.getProject();
            if (project != null
                    && ResourcesUtil.supportedByEditor(file, CorePlugin.DEFAULT_EDITOR_ID)) {
                List<IModel> models = SemanticProjectUtils.getModels(project);
                for (IModel model : models) {
                    if (file.equals(model.getResource())) {
                        if (model.hasWorkingCopy()) {
                            return new StructuredSelection(model.getWorkingCopy());
                        }
                        return new StructuredSelection(model);
                    }
                }
            }
            // Find the corresponding file
            return new StructuredSelection(file);
        }
        return StructuredSelection.EMPTY;
    }

    @Override
    public void activateEditor(IWorkbenchPage aPage, IStructuredSelection aSelection) {
        if (aSelection == null || aSelection.isEmpty())
            return;
        Object element = aSelection.getFirstElement();
        if (element instanceof IAdaptable) {
            IAdaptable adaptable = (IAdaptable) element;
            element = adaptable.getAdapter(IResource.class);
        }
        if (element instanceof IFile) {
            IEditorInput fileInput = new FileEditorInput((IFile) element);
            IEditorPart editor = null;
            if ((editor = aPage.findEditor(fileInput)) != null)
                aPage.bringToTop(editor);
        }
    }

}
