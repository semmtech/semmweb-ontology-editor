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
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.ui.navigator.CommonDropAdapter;
import org.eclipse.ui.navigator.CommonDropAdapterAssistant;

import com.google.common.collect.Lists;
import com.semmtech.plugin.semmweb.core.actions.ImportActions;
import com.semmtech.plugin.semmweb.core.actions.ModelFileAction;
import com.semmtech.plugin.semmweb.core.actions.SemanticProjectActionProvider;
import com.semmtech.plugin.semmweb.core.navigator.IImport;
import com.semmtech.plugin.semmweb.core.navigator.IImportCollection;
import com.semmtech.plugin.semmweb.core.navigator.IModel;
import com.semmtech.plugin.semmweb.core.navigator.ISemanticElement;
import com.semmtech.ui.plugin.util.Selections;


//TODO refactor: this class is very similar to {@link CopyModelDropAdapterAssistant} so could be 
//generalized in something like SemanticResourceDropAssistant
/**
 * Manage the dragging of one or more models to an IModel or an
 * IImportCollection causing the import of dragged model(s) into the dropped
 * model.
 * 
 * @author Simone Rondelli
 */
public class ImportModelDropAdapterAssistant extends CommonDropAdapterAssistant {

    @SuppressWarnings("unused")
    private static Logger logger = Logger.getLogger(ImportModelDropAdapterAssistant.class);

    public ImportModelDropAdapterAssistant() {
    }

    @Override
    public IStatus validateDrop(Object target, int operation, TransferData transferType) {

        if (operation != DND.DROP_MOVE) {
            return Status.CANCEL_STATUS;
        }

        if (!checkDropType(target)) {
            return Status.CANCEL_STATUS;
        }

        List<ISemanticElement> selectedResources = DndUtils.getDnDSelectedResources();

        if (selectedResources.isEmpty()) {
            return Status.CANCEL_STATUS;
        }

        ISemanticElement semanticTarget = (ISemanticElement) target;
        for (ISemanticElement res : selectedResources) {

            if (!checkDragType(res) || res.equals(semanticTarget)) {
                return Status.CANCEL_STATUS;
            }
        }

        return Status.OK_STATUS;
    }

    @Override
    public IStatus handleDrop(CommonDropAdapter aDropAdapter, DropTargetEvent aDropTargetEvent,
            Object aTarget) {

        Object currentTarget = aDropAdapter.getCurrentTarget();

        if (currentTarget == null || aDropTargetEvent.data == null) {
            return Status.CANCEL_STATUS;
        }

        // gets the models and URIs that have to be imported
        List<IFile> sourceModelFiles = Lists.newArrayList();
        for (IModel model : Selections.retrieveAllAsType(DndUtils.getSelection(), IModel.class)) {
            sourceModelFiles.add((IFile) model.getResource());
        }

        List<String> sourceOntologyURIs = Lists.newArrayList();
        for (IImport immport : Selections.retrieveAllAsType(DndUtils.getSelection(), IImport.class)) {
            sourceOntologyURIs.add(immport.getURI());
        }

        if (sourceModelFiles.isEmpty() && sourceOntologyURIs.isEmpty()) {
            return Status.OK_STATUS;
        }

        if (!SemanticProjectActionProvider.getModelActionsRunCondition().isSatisfied(null)) {
            return Status.CANCEL_STATUS;
        }

        // gets the destination Model
        IFile destFile;

        if (currentTarget instanceof IModel) {
            IModel model = (IModel) currentTarget;
            destFile = (IFile) model.getResource();
        }
        else if (currentTarget instanceof IImportCollection) {
            IImportCollection importCollection = (IImportCollection) currentTarget;
            IModel model = (IModel) importCollection.getAncestor(ISemanticElement.MODEL);
            destFile = (IFile) model.getResource();
        }
        else {
            return Status.CANCEL_STATUS;
        }

        ModelFileAction importAction = new ImportActions.DraggedModelsImport(destFile,
                sourceModelFiles, sourceOntologyURIs);
        importAction.run();

        return Status.OK_STATUS;
    }

    /**
     * Check if the dragged resource can be used as import
     */
    private boolean checkDragType(Object selected) {
        return selected instanceof IModel || selected instanceof IImport;
    }

    /**
     * Check if the dropped resource can accept an import
     */
    private boolean checkDropType(Object target) {
        return target instanceof IModel || target instanceof IImportCollection;
    }
}
