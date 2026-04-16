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
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.util.iterator.UniqueExtendedIterator;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.DC_11;
import com.semmtech.semantics.skos.Concept;
import com.semmtech.semantics.skos.ConceptScheme;
import com.semmtech.semantics.vocabulary.SKOS;


public class ConceptSchemeImpl extends SKOSIndividualImpl implements ConceptScheme {

    public ConceptSchemeImpl(Node node, EnhGraph graph) {
        super(node, graph);
    }

    public ConceptSchemeImpl(Individual ind) {
        super(ind.asNode(), (EnhGraph) ind.getModel());
    }

    @Override
    public ExtendedIterator<Concept> listTopConcepts() {
        List<Concept> list = Lists.newArrayList();
        for (RDFNode node : listPropertyValues(SKOS.hasTopConcept).toList()) {
            list.add(new ConceptImpl(node.as(Individual.class)));
        }
        for (Statement stmt : ((OntModel) getModel()).listStatements(
                new SimpleSelector(null, SKOS.topConceptOf, this.asIndividual())).toList()) {
            list.add(new ConceptImpl(stmt.getSubject().as(Individual.class)));
        }
        return UniqueExtendedIterator.create(list.iterator());
    }

    @Override
    public Concept createConcept() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Concept createConcept(String uri) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ExtendedIterator<Literal> listTitles(String lang) {
        return listPropertyLiterals(DCTerms.title, lang);
    }

    @Override
    public String getCreator() {
        for (Literal creator : listPropertyLiterals(DC_11.creator).toList())
            return creator.getString();
        return null;
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
    public ExtendedIterator<Literal> listNotations() {
        return listPropertyLiterals(SKOS.notation);
    }

}
