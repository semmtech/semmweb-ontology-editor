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
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.OWL;
import com.semmtech.plugin.semmweb.core.model.ModelChangesCollection;
import com.semmtech.plugin.semmweb.core.model.events.ModelChangedEvent;


/**
 * 
 * @author Sander Stolk
 */
public class RemoveImportOperation extends ModelOperation {
    private final static String description = "Remove import";

    private final Resource sourceOntology;
    private final Resource importOntology;

    public RemoveImportOperation(Resource ontology, Resource importOntology) {
        super(description);
        this.sourceOntology = ontology;
        this.importOntology = importOntology;
    }

    @Override
    public boolean execute(OntModel model) {
        if ((sourceOntology == null) || (importOntology == null)) {
            return false;
        }

        Statement importStatement = model.createStatement(sourceOntology, OWL.imports,
                importOntology);
        model.remove(importStatement);

        ModelChangesCollection modelChanges = new ModelChangesCollection();
        modelChanges.remove(importStatement);
        model.notifyEvent(new ModelChangedEvent(model, modelChanges, description));
        return true;
    }

    @Override
    public boolean undo(OntModel model) {
        if ((sourceOntology == null) || (importOntology == null)) {
            return false;
        }

        Statement importStatement = model.createStatement(sourceOntology, OWL.imports,
                importOntology);
        model.add(importStatement);

        ModelChangesCollection modelChanges = new ModelChangesCollection();
        modelChanges.add(importStatement);
        model.notifyEvent(new ModelChangedEvent(model, modelChanges, description));
        return true;
    }
}
