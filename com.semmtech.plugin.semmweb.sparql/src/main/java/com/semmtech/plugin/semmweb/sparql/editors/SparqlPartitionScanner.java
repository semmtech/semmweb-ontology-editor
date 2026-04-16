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

package com.semmtech.plugin.semmweb.sparql.editors;


import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.Token;


public class SparqlPartitionScanner extends RuleBasedPartitionScanner {

    public final static String SPARQL_PROLOGUE = "__sparql_prologue";
    public final static String SPARQL_QUERY = "__sparql_query";

    public static String[] SPARQL_PARTITION_TYPES = new String[] { SPARQL_PROLOGUE, SPARQL_QUERY };

    @SuppressWarnings("unused")
    public SparqlPartitionScanner() {
        super();
        IToken prologue = new Token(SPARQL_PROLOGUE);
        IToken query = new Token(SPARQL_QUERY);

        IPredicateRule[] rules = new IPredicateRule[2];

        rules[0] = new EndOfLineRule("PREFIX", prologue);
        rules[1] = new EndOfLineRule("BASE", prologue);

        setPredicateRules(rules);
    }
}
