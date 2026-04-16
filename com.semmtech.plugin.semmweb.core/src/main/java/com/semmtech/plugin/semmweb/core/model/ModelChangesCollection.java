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

package com.semmtech.plugin.semmweb.core.model;


import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ModelMaker;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.util.FileUtils;
import com.semmtech.io.StringOutputStream;


/**
 * 
 * @author Sander Stolk
 */
public class ModelChangesCollection {
    private static final Logger logger = Logger.getLogger(ModelChangesCollection.class);

    public final static String GRAPH_ADDITIONS_ID = "graph-additions";
    public final static String GRAPH_REMOVALS_ID = "graph-removals";

    private final Model additions;
    private final Model removals;

    public ModelChangesCollection() {
        this(null, null);
    }

    public ModelChangesCollection(List<Statement> added, List<Statement> removed) {
        ModelMaker maker = ModelFactory.createMemModelMaker();
        additions = maker.createFreshModel();
        removals = maker.createFreshModel();

        if (added != null) {
            add(added);
        }
        if (removed != null) {
            remove(removed);
        }
    }

    public Model getAdditions() {
        return additions;
    }

    public Model getRemovals() {
        return removals;
    }

    public void add(Statement statement) {
        if (removals.contains(statement)) {
            removals.remove(statement);
        }
        else if (!additions.contains(statement)) {
            additions.add(statement);
        }
    }

    public void add(List<Statement> statements) {
        for (Statement statement : statements) {
            add(statement);
        }
    }

    public void remove(Statement statement) {
        // TODO: Create and update index(es)
        if (additions.contains(statement)) {
            additions.remove(statement);
        }
        else if (!removals.contains(statement)) {
            removals.add(statement);
        }
    }

    public void remove(List<Statement> statements) {
        for (Statement s : statements) {
            remove(s);
        }
    }

    public List<Statement> getAdded() {
        return additions.listStatements().toList();
    }

    public List<Statement> getRemoved() {
        return removals.listStatements().toList();
    }

    public void applyToModel(Model model) {
        model.add(additions);
        model.remove(removals);
    }

    public ModelChangesCollection reverseChanges() {
        return new ModelChangesCollection(getRemoved(), getAdded());
    }

    public void revertOnModel(Model model) {
        model.add(removals);
        model.remove(additions);
    }

    public boolean isEmpty() {
        return additions.isEmpty() && removals.isEmpty();
    }

    public int size() {
        return (int) additions.size() + (int) removals.size();
    }

    public List<Statement> listStatementsAdded(Resource subject, Property predicate, RDFNode object) {
        return additions.listStatements(subject, predicate, object).toList();
    }

    public List<Statement> listStatementsRemoved(Resource subject, Property predicate,
            RDFNode object) {

        return removals.listStatements(subject, predicate, object).toList();
    }

    @Override
    public String toString() {
        try (StringOutputStream stream = new StringOutputStream()) {
            stream.write("Additions:\n");
            additions.write(stream, FileUtils.langTurtle);
            stream.write("Removals:\n");
            removals.write(stream, FileUtils.langTurtle);
            return stream.toString();
        }
        catch (IOException e) {
            logger.error(e.getMessage(), e);
            return super.toString();
        }
    }
}
