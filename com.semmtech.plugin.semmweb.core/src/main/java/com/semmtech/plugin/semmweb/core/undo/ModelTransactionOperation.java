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

package com.semmtech.plugin.semmweb.core.undo;


import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.semmtech.plugin.semmweb.core.model.IModelProvider;
import com.semmtech.plugin.semmweb.core.model.ModelChangesCollection;
import com.semmtech.plugin.semmweb.core.model.ModelTransaction;
import com.semmtech.plugin.semmweb.core.model.events.ModelChangedEvent;


/**
 * 
 * @author Sander Stolk
 */
public class ModelTransactionOperation extends AbstractOperation {
    private IModelProvider provider;
    private ModelChangesCollection modelChanges;
    private String description;

    public ModelTransactionOperation(IModelProvider modelProvider, ModelTransaction transaction,
            String description) {
        super("Model change");
        this.provider = modelProvider;
        this.modelChanges = transaction.getModelChanges();
        this.description = description;
    }

    public boolean isEmpty() {
        return modelChanges.isEmpty();
    }

    @Override
    public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        if ((provider == null) || (provider.getOntModel() == null)) {
            return Status.CANCEL_STATUS;
        }
        OntModel model = provider.getOntModel();

        // The commit of the transaction has already been taken care of by the
        // SemanticModelFormEditor.

        model.notifyEvent(new ModelChangedEvent(model, modelChanges, description));
        return Status.OK_STATUS;
    }

    public static boolean redo(Model model, ModelChangesCollection modelChanges,
            boolean suppressNotify, String description) {
        if (model == null) {
            return false;
        }

        modelChanges.applyToModel(model);

        if (!suppressNotify) {
            model.notifyEvent(new ModelChangedEvent(model, modelChanges, description));
        }
        return true;
    }

    @Override
    public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        if ((provider == null) || (provider.getOntModel() == null)) {
            return Status.CANCEL_STATUS;
        }
        OntModel model = provider.getOntModel();
        boolean success = redo(model, modelChanges, false, description);
        return (success) ? Status.OK_STATUS : Status.CANCEL_STATUS;
    }

    public static boolean undo(Model model, ModelChangesCollection modelChanges,
            boolean suppressNotify, String description) {
        if (model == null) {
            return false;
        }

        modelChanges.revertOnModel(model);

        if (!suppressNotify) {
            model.notifyEvent(new ModelChangedEvent(model, modelChanges.reverseChanges(),
                    "Undo of: " + description));
        }
        return true;
    }

    @Override
    public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        if ((provider == null) || (provider.getOntModel() == null)) {
            return Status.CANCEL_STATUS;
        }
        OntModel model = provider.getOntModel();
        boolean success = undo(model, modelChanges, false, description);
        return (success) ? Status.OK_STATUS : Status.CANCEL_STATUS;
    }

    @Override
    public String toString() {
        String result = "Begin of model transaction operation '" + description + "'\n";
        result += modelChanges.toString();
        result += "End of model transaction operation '" + description + "'\n";
        return result;
    }
}
