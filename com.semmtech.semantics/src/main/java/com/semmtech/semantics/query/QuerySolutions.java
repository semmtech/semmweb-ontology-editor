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

package com.semmtech.semantics.query;


import com.google.common.base.Preconditions;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.sparql.core.Var;


public class QuerySolutions {

    /**
     * Returns the lexical form for the Literal bound to the given variable name
     * within the query solution ; if the variable is not bound, or if the
     * lexical value is null, the default value is returned.
     * 
     * @param solution
     * @param varName
     * @param defaultValue
     * @return
     */
    public static String getLexicalForm(QuerySolution solution, String varName, String defaultValue) {
        Preconditions.checkNotNull(solution, "Solution cannot be null");
        Preconditions.checkNotNull(varName, "Variable name cannot be null");

        RDFNode node = solution.get(varName);
        if (node == null) {
            return defaultValue;
        }
        Preconditions.checkState(!node.isResource(), "Binded node cannot be a Resource");

        String lexical = node.asLiteral().getLexicalForm();
        if (lexical == null) {
            return defaultValue;
        }
        return lexical;
    }

    public static String getLexicalForm(QuerySolution solution, Var var, String defaultValue) {
        Preconditions.checkNotNull(var, "Variable cannot be null");

        return getLexicalForm(solution, var.getName(), defaultValue);
    }

    /**
     * Returns the lexical form for the Literal bound to the given variable name
     * within the query solution; if the variable is not bound, or if the
     * lexical value is null, null is returned.
     * 
     * @param solution
     * @param varName
     * @return
     */
    public static String getLexicalForm(QuerySolution solution, String varName) {
        return getLexicalForm(solution, varName, null);
    }

    public static String getLexicalForm(QuerySolution solution, Var var) {
        Preconditions.checkNotNull(var, "Variable cannot be null");

        return getLexicalForm(solution, var.getName(), null);
    }

    public static boolean getBoolean(QuerySolution solution, String varName, boolean defaulValue) {
        Preconditions.checkNotNull(solution, "Solution cannot be null");
        Preconditions.checkNotNull(varName, "Variable name cannot be null");

        RDFNode node = solution.get(varName);
        if (node == null) {
            return defaulValue;
        }
        Preconditions.checkState(!node.isResource(), "Binded node cannot be a Resource");

        return node.asLiteral().getBoolean();
    }
}
