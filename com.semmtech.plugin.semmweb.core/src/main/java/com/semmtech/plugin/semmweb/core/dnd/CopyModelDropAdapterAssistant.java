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

package com.semmtech.plugin.semmweb.core.dnd;


import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.ui.navigator.CommonDropAdapter;
import org.eclipse.ui.navigator.CommonDropAdapterAssistant;

import com.google.common.collect.Lists;
import com.semmtech.plugin.semmweb.core.actions.PasteModelAction;
import com.semmtech.plugin.semmweb.core.navigator.IModel;
import com.semmtech.plugin.semmweb.core.navigator.IModelCollection;
import com.semmtech.plugin.semmweb.core.navigator.ISemanticElement;


//TODO refactor: this class is very similar to {@link ImportModelDropAdapterAssistant} so could be 
//generalized in something like SemanticResourceDropAssistant
/**
 * Manage the dragging of one or more model(s) from one project (in case the
 * dragged element is an IImportCollection all the submodels of that are
 * considered) into an IModelCollection of another project causing the addition
 * of the dragged models into the dropped IModelCollection.
 * 
 * @author Simone Rondelli
 * 
 */
public class CopyModelDropAdapterAssistant extends CommonDropAdapterAssistant {

    @SuppressWarnings("unused")
    private static Logger logger = Logger.getLogger(CopyModelDropAdapterAssistant.class);

    public CopyModelDropAdapterAssistant() {
    }

    @Override
    public IStatus validateDrop(Object target, int operation, TransferData transferType) {

        // the copy is performed by keeping the CTRL key pressed during the
        // dragging operation
        if (operation != DND.DROP_COPY) {
            return Status.CANCEL_STATUS;
        }

        if (!checkDropType(target)) {
            return Status.CANCEL_STATUS;
        }

        List<ISemanticElement> selectedResources = DndUtils.getDnDSelectedResources();

        if (selectedResources.isEmpty()) {
            return Status.CANCEL_STATUS;
        }

        for (ISemanticElement res : selectedResources) {
            if (!checkDragType(res)) {
                return Status.CANCEL_STATUS;
            }
        }

        return Status.OK_STATUS;
    }

    @Override
    public IStatus handleDrop(CommonDropAdapter aDropAdapter, DropTargetEvent aDropTargetEvent,
            Object aTarget) {

        // should be always an ISemanticElement due to the previous check
        ISemanticElement currentTarget = (ISemanticElement) aDropAdapter.getCurrentTarget();

        if (currentTarget == null || aDropTargetEvent.data == null) {
            return Status.CANCEL_STATUS;
        }

        IProject project = currentTarget.getProject();

        List<String> sourceModelPaths = Lists.newArrayList();

        for (ISemanticElement selectedResource : DndUtils.getDnDSelectedResources()) {
            if (selectedResource instanceof IModel) {
                IModel model = (IModel) selectedResource;
                IFile file = (IFile) model.getResource();
                sourceModelPaths.add(file.getRawLocation().toOSString());
            }
        }

        PasteModelAction.copyModels(project, sourceModelPaths);
        return Status.OK_STATUS;
    }

    private static boolean checkDropType(Object element) {
        return element instanceof IModelCollection;
    }

    private static boolean checkDragType(Object element) {
        return element instanceof IModel;
    }

}
