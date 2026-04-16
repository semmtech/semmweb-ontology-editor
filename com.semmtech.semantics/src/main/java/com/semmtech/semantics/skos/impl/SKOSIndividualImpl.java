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

package com.semmtech.semantics.skos.impl;


import java.util.List;

import com.google.common.collect.Lists;
import com.hp.hpl.jena.enhanced.EnhGraph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.ontology.impl.IndividualImpl;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.util.iterator.UniqueExtendedIterator;


public abstract class SKOSIndividualImpl extends IndividualImpl {

    public SKOSIndividualImpl(Node node, EnhGraph graph) {
        super(node, graph);
    }

    /**
     * 
     * @param property
     * @return
     */
    protected ExtendedIterator<Literal> listPropertyLiterals(Property property) {
        List<Literal> literals = Lists.newArrayList();
        for (RDFNode node : listPropertyValues(property).toList())
            literals.add(node.asLiteral());
        return UniqueExtendedIterator.create(literals.iterator());
    }

    /**
     * 
     * @param property
     * @param lang
     * @return
     */
    protected ExtendedIterator<Literal> listPropertyLiterals(Property property, String lang) {
        List<Literal> literals = Lists.newArrayList();
        for (RDFNode node : listPropertyValues(property).toList()) {
            Literal literal = node.asLiteral();
            if ((literal.getLanguage() == null && lang == null)
                    || (literal.getLanguage() != null && literal.getLanguage().equals(lang)))
                literals.add(node.asLiteral());
        }
        return UniqueExtendedIterator.create(literals.iterator());
    }
}
