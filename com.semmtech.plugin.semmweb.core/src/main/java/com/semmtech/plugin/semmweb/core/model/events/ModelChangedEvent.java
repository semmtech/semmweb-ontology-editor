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

package com.semmtech.plugin.semmweb.core.model.events;


import com.hp.hpl.jena.rdf.model.Model;
import com.semmtech.plugin.semmweb.core.model.ModelChangesCollection;


/**
 * 
 * @author Sander Stolk
 * @author Mike Henrichs
 */
public class ModelChangedEvent implements IModelEvent {
    private final Model model;
    private final String title;
    private final ModelChangesCollection modelChanges;

    /**
     * @param modelChanges
     *            the model changes for the event with type MODEL_CHANGED
     * @param title
     *            a textual string
     */
    public ModelChangedEvent(Model model, ModelChangesCollection modelChanges, String title) {
        this.model = model;
        this.title = title;
        this.modelChanges = modelChanges;
    }

    @Override
    public Model getModel() {
        return model;
    }

    @Override
    public String getTitle() {
        return title;
    }

    public ModelChangesCollection getModelChanges() {
        return modelChanges;
    }
}
