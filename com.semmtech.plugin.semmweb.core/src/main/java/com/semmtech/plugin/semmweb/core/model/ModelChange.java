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


import com.hp.hpl.jena.rdf.model.Statement;


/**
 * 
 * @author Sander Stolk
 */
public class ModelChange {
    public enum StatementOperation {
        ADD, REMOVE
    }

    private StatementOperation operation;
    private Statement statement;

    public ModelChange(StatementOperation operation, Statement statement) {
        this.operation = operation;
        this.statement = statement;
    }

    public StatementOperation getMethod() {
        return operation;
    }

    public Statement getStatement() {
        return statement;
    }

    @Override
    public String toString() {
        if (operation == StatementOperation.ADD) {
            return "Add statement: " + statement;
        }
        else if (operation == StatementOperation.REMOVE) {
            return "Remove statement: " + statement;
        }
        return "";
    }
}
