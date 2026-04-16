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

package com.semmtech.plugin.semmweb.core.operations;


import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.hp.hpl.jena.ontology.OntModel;
import com.semmtech.plugin.semmweb.core.model.IModelProvider;


/**
 * 
 * @author Sander Stolk
 */
public abstract class ModelOperation extends AbstractOperation {
    protected IModelProvider modelProvider;
    protected OntModel ontModel;

    public ModelOperation(String label) {
        super(label);
    }

    public void setModel(IModelProvider provider) {
        modelProvider = provider;
    }

    public void setModel(OntModel model) {
        ontModel = model;
    }

    private OntModel getModel() {
        if (modelProvider != null) {
            ontModel = modelProvider.getOntModel();
        }
        return ontModel;
    }

    @Override
    final public IStatus execute(IProgressMonitor monitor, IAdaptable info)
            throws ExecutionException {
        OntModel model = getModel();
        if (model == null) {
            return Status.CANCEL_STATUS;
        }
        boolean result = execute(model);
        return (result) ? Status.OK_STATUS : Status.CANCEL_STATUS;
    }

    @Override
    final public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        OntModel model = getModel();
        if (model == null) {
            return Status.CANCEL_STATUS;
        }
        boolean result = redo(model);
        return (result) ? Status.OK_STATUS : Status.CANCEL_STATUS;
    }

    @Override
    final public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        OntModel model = getModel();
        if (model == null) {
            return Status.CANCEL_STATUS;
        }
        boolean result = undo(model);
        return (result) ? Status.OK_STATUS : Status.CANCEL_STATUS;
    }

    abstract public boolean execute(OntModel model);

    public boolean redo(OntModel model) {
        return execute(model);
    }

    abstract public boolean undo(OntModel model);
}
