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
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.ui.navigator.CommonDropAdapter;
import org.eclipse.ui.navigator.CommonDropAdapterAssistant;
import org.eclipse.ui.part.IDropActionDelegate;

import com.google.common.collect.Lists;
import com.semmtech.plugin.semmweb.core.actions.DownloadOntModelAction;
import com.semmtech.plugin.semmweb.core.actions.ImportActions;
import com.semmtech.plugin.semmweb.core.actions.ModelFileAction;
import com.semmtech.plugin.semmweb.core.extensionpoint.IPublisher.PublicationInfo;
import com.semmtech.plugin.semmweb.core.navigator.IImportCollection;
import com.semmtech.plugin.semmweb.core.navigator.IModel;
import com.semmtech.plugin.semmweb.core.navigator.IModelCollection;
import com.semmtech.plugin.semmweb.core.navigator.ISemanticElement;
import com.semmtech.plugin.semmweb.core.navigator.ISemanticProject;
import com.semmtech.ui.plugin.util.Selections;


/**
 * Manage the dropping of one or more PublicationInfos to a ISemanticProject or
 * IModelCollection, causing the download of the dragged publication(s) into the
 * project, by means of a wizard.
 * 
 * @author Sander Stolk
 */
public class DownloadModelDropAdapterAssistant extends CommonDropAdapterAssistant implements
        IDropActionDelegate {

    @SuppressWarnings("unused")
    private static Logger logger = Logger.getLogger(DownloadModelDropAdapterAssistant.class);

    public DownloadModelDropAdapterAssistant() {
    }

    @Override
    public IStatus validateDrop(Object target, int operation, TransferData transferType) {

        if (operation != DND.DROP_COPY && operation != DND.DROP_MOVE) {
            return Status.CANCEL_STATUS;
        }

        if (!checkDropType(target)) {
            return Status.CANCEL_STATUS;
        }

        List<PublicationInfo> selectedPublications = Selections.retrieveAllAsType(
                DndUtils.getSelection(), PublicationInfo.class);

        if (selectedPublications.isEmpty()) {
            return Status.CANCEL_STATUS;
        }

        for (PublicationInfo pub : selectedPublications) {
            if (!checkDragType(pub)) {
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

        // gets the models and URIs that have to be downloaded
        List<PublicationInfo> publications = Selections.retrieveAllAsType(DndUtils.getSelection(),
                PublicationInfo.class);
        if (publications.isEmpty()) {
            return Status.OK_STATUS;
        }

        // gets the destination project
        IProject project = getProject(currentTarget);
        if (project == null) {
            return Status.CANCEL_STATUS;
        }

        // download models
        for (PublicationInfo pub : publications) {
            DownloadOntModelAction downloadAction = new DownloadOntModelAction(pub.uri, project,
                    pub.name);
            downloadAction.run();
        }

        return Status.OK_STATUS;
    }

    /**
     * Check if the dragged resource can be downloaded
     */
    private boolean checkDragType(Object selected) {
        return selected instanceof PublicationInfo;
    }

    /**
     * Check if the dropped resource can be downloaded
     */
    private boolean checkDropType(Object target) {
        return target instanceof ISemanticProject || target instanceof IModelCollection;
    }

    @Override
    public boolean run(Object source, Object target) {
        PublicationInfo publication = PublicationTransfer.getInstance().byteArrayToJava(
                (byte[]) source);
        if (target instanceof IProject || target instanceof ISemanticProject
                || target instanceof IModelCollection) {
            // can add publication as new model
            IProject project = getProject(target);
            IFolder modelsFolder = project.getFolder("models");
            String filename = publication.uri.replaceFirst("/versions/[^/]*$", "")
                    .replaceFirst("/$", "").replaceAll(".*/", "");
            if (filename.isEmpty()) {
                filename = publication.name;
            }
            DownloadOntModelAction downloadAction = new DownloadOntModelAction(publication.uri,
                    (modelsFolder == null) ? project : modelsFolder, filename);
            downloadAction.run();
            return true;
        }
        if (target instanceof IModel || target instanceof IImportCollection) {
            // can add publication as import
            IFile destFile = null;
            if (target instanceof IModel) {
                IModel model = (IModel) target;
                destFile = (IFile) model.getResource();
            }
            else if (target instanceof IImportCollection) {
                IImportCollection importCollection = (IImportCollection) target;
                IModel model = (IModel) importCollection.getAncestor(ISemanticElement.MODEL);
                destFile = (IFile) model.getResource();
            }
            if (destFile != null) {
                List<IFile> fileImports = Lists.newArrayList();
                List<String> uriImports = Lists.newArrayList();
                uriImports.add(publication.uri);
                ModelFileAction importAction = new ImportActions.DraggedModelsImport(destFile,
                        fileImports, uriImports);
                importAction.run();
                return true;
            }
        }
        return false;
    }

    private IProject getProject(Object target) {
        if (target instanceof IProject) {
            return (IProject) target;
        }
        if (target instanceof ISemanticProject) {
            ISemanticProject semProject = (ISemanticProject) target;
            return semProject.getProject();
        }
        if (target instanceof IModelCollection) {
            IModelCollection modelCollection = (IModelCollection) target;
            return modelCollection.getProject();
        }
        return null;
    }

}
