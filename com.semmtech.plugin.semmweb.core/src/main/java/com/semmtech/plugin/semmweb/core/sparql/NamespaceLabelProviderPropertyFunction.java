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

package com.semmtech.plugin.semmweb.core.sparql;


import com.google.common.base.Strings;
import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.shared.PrefixMapping;
import com.hp.hpl.jena.sparql.engine.ExecutionContext;


/**
 * 
 * @author Sander Stolk
 */
public class NamespaceLabelProviderPropertyFunction extends LabelProviderPropertyFunction {

    public static String getURI() {
        return CorePropertyFunctions.NS + "namespaceLabelProvider";
    }

    public static Node asNode() {
        return NodeFactory.createURI(getURI());
    }

    @Override
    protected String getText(Node node, ExecutionContext context) {
        Graph graph = context.getActiveGraph();
        PrefixMapping prefixMapping = graph.getPrefixMapping();

        String uri = node.isLiteral() ? node.getLiteralLexicalForm() : node.getURI();
        String prefix = prefixMapping.getNsURIPrefix(uri);
        if (!Strings.isNullOrEmpty(prefix)) {
            return prefix;
        }
        return ("<" + uri + ">");
    }
}
