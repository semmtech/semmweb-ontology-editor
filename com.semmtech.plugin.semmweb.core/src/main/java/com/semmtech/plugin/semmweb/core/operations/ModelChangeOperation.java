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


import com.hp.hpl.jena.ontology.OntModel;
import com.semmtech.plugin.semmweb.core.model.ModelChangesCollection;
import com.semmtech.plugin.semmweb.core.model.events.ModelChangedEvent;


/**
 * 
 * @author Sander Stolk
 */
public class ModelChangeOperation extends ModelOperation {
    private final static String description = "Model change";
    private final ModelChangesCollection modelChanges;

    public ModelChangeOperation(ModelChangesCollection modelChanges) {
        super(description);
        this.modelChanges = modelChanges;
    }

    @Override
    public boolean execute(OntModel model) {
        if (!modelChanges.isEmpty()) {
            modelChanges.applyToModel(model);
            model.notifyEvent(new ModelChangedEvent(model, modelChanges, description));
        }
        return true;
    }

    @Override
    public boolean undo(OntModel model) {
        if (!modelChanges.isEmpty()) {
            modelChanges.revertOnModel(model);
            model.notifyEvent(new ModelChangedEvent(model, modelChanges, "Undo of: " + description));
        }
        return true;
    }

}
