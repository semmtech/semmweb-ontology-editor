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

package com.semmtech.plugin.semmweb.sparql.markup;


import java.util.List;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.Token;

import com.google.common.collect.Lists;
import com.semmtech.StringUtil;
import com.semmtech.grammars.antlr.sparql.SparqlLexer;


public class SparqlRegionAnalyzer {

    public List<SparqlRegion> analyzeSparql(String sparql) {
        List<SparqlRegion> positions = Lists.newArrayList();
        ANTLRStringStream input = new ANTLRStringStream(sparql);
        SparqlLexer lex = new SparqlLexer(input);
        Token token = lex.nextToken();

        while (token != Token.EOF_TOKEN) {
            if (token.getChannel() == 0) {
                int line = token.getLine();
                int start = StringUtil.getAbsoluteCharPosition(sparql, line,
                        token.getCharPositionInLine());
                int end = start + token.getText().length();
                positions.add(new SparqlRegion(token.getType(), start, end));
            }
            token = lex.nextToken();
        }
        return positions;
    }
}
