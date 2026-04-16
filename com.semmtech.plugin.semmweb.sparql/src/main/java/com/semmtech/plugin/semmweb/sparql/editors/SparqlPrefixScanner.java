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


import org.eclipse.jface.text.rules.RuleBasedScanner;


public class SparqlPrefixScanner extends RuleBasedScanner {

    public SparqlPrefixScanner(ColorManager manager) {
        // IToken prefix =
        // new Token(
        // new TextAttribute(manager.getColor(ISparqlColorConstants.PREFIX)));
        //
        // IRule[] rules = new IRule[1];
        //
        // // Add rule for double quotes
        // rules[0] = new SingleLineRule("PREFIX", ".", prefix);
        //
        // setRules(rules);
    }
}
