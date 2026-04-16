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

package com.semmtech.plugin.semmweb.core.handlers;


import java.util.List;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.handlers.HandlerUtil;

import com.google.common.collect.Lists;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Resource;
import com.semmtech.plugin.semmweb.core.model.IModelProvider;
import com.semmtech.plugin.semmweb.core.model.ModelTransaction;
import com.semmtech.plugin.semmweb.core.model.ResourceStatements;


public class DeleteSelectedResourceHandler extends SelectedResourceHandler {
    public static final String ID = "com.semmtech.plugin.semmweb.core.commands.deleteSelectedResource";

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        List<Resource> selectedResources = getSelectedResources(event);
        IModelProvider modelProvider = getActiveModelProvider(event);
        OntModel model = getActiveOntModel(event);

        if ((modelProvider != null) && (model != null) && (selectedResources != null)
                && !selectedResources.isEmpty()) {
            // Confirm whether the user truly wants to delete the selected
            // resources
            String questionText = String
                    .format("Are you sure you want to delete the following resources and all the statements in which these resources are used?\n\n%s",
                            getText(selectedResources, event));
            MessageDialog dialog = new MessageDialog(HandlerUtil.getActiveShell(event),
                    "Delete Resources", null, questionText, MessageDialog.QUESTION, new String[] {
                            "Yes", "No" }, 1);
            if (dialog.open() != Window.OK) {
                return null;
            }

            // If some of the resources cannot be deleted, confirm whether the
            // user wants to continue deleting all the other resources anyway.
            List<Resource> importedResources = Lists.newArrayList();
            for (Resource resource : selectedResources) {
                boolean imported = !model.isInBaseModel(resource);
                if (imported) {
                    importedResources.add(resource);
                }
            }
            if (!importedResources.isEmpty()) {
                if (importedResources.size() == selectedResources.size()) {
                    MessageDialog
                            .openInformation(HandlerUtil.getActiveShell(event),
                                    "Cannot Delete Resources",
                                    "None of the selected resources can be deleted. They are not defined in this model.");
                    return null;
                }

                questionText = String
                        .format("The following selected resources cannot be deleted. They are not defined in this model.\n\n%s\nDo you still want to continue deleting the other resources?",
                                getText(importedResources, event));
                dialog = new MessageDialog(HandlerUtil.getActiveShell(event), "Delete Resources",
                        null, questionText, MessageDialog.QUESTION, new String[] { "Yes", "No" }, 1);
                if (dialog.open() != Window.OK) {
                    return null;
                }
            }

            // Delete the resources
            selectedResources.removeAll(importedResources);
            if (!selectedResources.isEmpty()) {
                String transactionDescription = "Model change due to removing of selected resources and all its properties";
                ModelTransaction transaction = modelProvider
                        .createTransaction(transactionDescription);

                String listText = getText(selectedResources, event);
                for (Resource resource : selectedResources) {
                    ResourceStatements.createRemoveResourceStatements(resource, true);
                }

                MessageDialog
                        .openInformation(
                                HandlerUtil.getActiveShell(event),
                                "Delete Resource",
                                String.format(
                                        "The following resources and all their statements have been deleted.\n\n%s",
                                        listText));
                modelProvider.commitTransaction(transaction);
            }
        }
        return null;
    }

    private String getText(List<Resource> list, ExecutionEvent event) {
        String text = "";
        for (Resource resource : list) {
            String name = getActiveModelProvider(event).getLabelProvider().getText(resource);
            text += name + "\n";
        }
        return text;
    }

}
