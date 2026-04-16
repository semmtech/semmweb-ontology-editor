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


import java.util.List;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelChangedListener;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;


/**
 * This adapter class provides default implementations for the methods described
 * by the ModelChangedListener interface.
 * 
 * @author Mike Henrichs
 * 
 */
public class ModelChangedAdapter implements ModelChangedListener {

    @Override
    public void addedStatement(Statement statement) {
    }

    @Override
    public void addedStatements(Statement[] statements) {
    }

    @Override
    public void addedStatements(List<Statement> statements) {
    }

    @Override
    public void addedStatements(StmtIterator statements) {
    }

    @Override
    public void addedStatements(Model model) {
    }

    @Override
    public void removedStatement(Statement statement) {
    }

    @Override
    public void removedStatements(Statement[] statements) {
    }

    @Override
    public void removedStatements(List<Statement> statements) {
    }

    @Override
    public void removedStatements(StmtIterator statements) {
    }

    @Override
    public void removedStatements(Model model) {
    }

    @Override
    public void notifyEvent(Model model, Object event) {
    }

}
