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


import java.util.ArrayList;

import com.google.common.collect.Lists;
import com.hp.hpl.jena.enhanced.EnhGraph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.util.iterator.UniqueExtendedIterator;
import com.semmtech.semantics.skos.Collection;
import com.semmtech.semantics.skos.Concept;
import com.semmtech.semantics.vocabulary.SKOS;


public class CollectionImpl extends SKOSIndividualImpl implements Collection {

    public CollectionImpl(Node node, EnhGraph graph) {
        super(node, graph);
    }

    public CollectionImpl(Individual ind) {
        super(ind.asNode(), (EnhGraph) ind.getModel());
    }

    @Override
    public ExtendedIterator<Concept> listMembers() {
        ArrayList<Concept> list = Lists.newArrayList();
        for (RDFNode member : listPropertyValues(SKOS.member).toList())
            list.add(new ConceptImpl(member.as(Individual.class)));
        return UniqueExtendedIterator.create(list.iterator());
    }

    @Override
    public ExtendedIterator<Literal> listPrefLabels() {
        return listPropertyLiterals(SKOS.prefLabel);
    }

    @Override
    public String getPrefLabel(String lang) {
        for (RDFNode node : listPropertyValues(SKOS.prefLabel).toList()) {
            Literal literal = node.asLiteral();
            if ((literal.getLanguage() == null && lang == null)
                    || (literal.getLanguage() != null && literal.getLanguage().equals(lang)))
                return literal.getString();
        }
        return null;
    }

    @Override
    public ExtendedIterator<Literal> listAltLabels() {
        return listPropertyLiterals(SKOS.altLabel);
    }

    @Override
    public ExtendedIterator<Literal> listAltLabels(String lang) {
        return listPropertyLiterals(SKOS.altLabel, lang);
    }

    @Override
    public ExtendedIterator<Literal> listScopeNotes() {
        return listPropertyLiterals(SKOS.scopeNote);
    }

    @Override
    public ExtendedIterator<Literal> listHistoryNotes() {
        return listPropertyLiterals(SKOS.historyNote);
    }

    @Override
    public ExtendedIterator<Literal> listExamples() {
        return listPropertyLiterals(SKOS.example);
    }

    @Override
    public ExtendedIterator<Literal> listEditorialNotes() {
        return listPropertyLiterals(SKOS.editorialNote);
    }

    @Override
    public ExtendedIterator<Literal> listDefinitions() {
        return listPropertyLiterals(SKOS.definition);
    }

    @Override
    public ExtendedIterator<Literal> listChangeNotes() {
        return listPropertyLiterals(SKOS.changeNote);
    }

    @Override
    public ExtendedIterator<Literal> listHiddenLabels() {
        return listPropertyLiterals(SKOS.hiddenLabel);
    }

    @Override
    public ExtendedIterator<Literal> listHiddenLabels(String lang) {
        return listPropertyLiterals(SKOS.hiddenLabel, lang);
    }

    @Override
    public ExtendedIterator<Literal> listNotations() {
        return listPropertyLiterals(SKOS.notation);
    }

}
