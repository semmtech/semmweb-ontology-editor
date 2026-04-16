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
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.util.iterator.UniqueExtendedIterator;
import com.semmtech.semantics.skos.Collection;
import com.semmtech.semantics.skos.Concept;
import com.semmtech.semantics.skos.ConceptScheme;
import com.semmtech.semantics.vocabulary.SKOS;


public class ConceptImpl extends SKOSIndividualImpl implements Concept {

    public ConceptImpl(Node node, EnhGraph graph) {
        super(node, graph);
    }

    public ConceptImpl(Individual ind) {
        super(ind.asNode(), (EnhGraph) ind.getModel());
    }

    @Override
    public ExtendedIterator<Concept> listNarrowerConcepts() {
        List<Concept> concepts = Lists.newArrayList();
        for (RDFNode node : listPropertyValues(SKOS.narrower).toList()) {
            concepts.add(new ConceptImpl(node.as(Individual.class)));
        }
        return UniqueExtendedIterator.create(concepts.iterator());
    }

    @Override
    public ExtendedIterator<Concept> listBroaderConcepts() {
        List<Concept> concepts = Lists.newArrayList();
        for (RDFNode node : listPropertyValues(SKOS.broader).toList()) {
            concepts.add(new ConceptImpl(node.as(Individual.class)));
        }
        return UniqueExtendedIterator.create(concepts.iterator());
    }

    @Override
    public ExtendedIterator<Concept> listRelatedConcepts() {
        List<Concept> concepts = Lists.newArrayList();
        for (RDFNode node : listPropertyValues(SKOS.related).toList()) {
            concepts.add(new ConceptImpl(node.as(Individual.class)));
        }
        return UniqueExtendedIterator.create(concepts.iterator());
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
    public ExtendedIterator<Literal> listHiddenLabels() {
        return listPropertyLiterals(SKOS.hiddenLabel);
    }

    @Override
    public ExtendedIterator<Literal> listHiddenLabels(String lang) {
        return listPropertyLiterals(SKOS.hiddenLabel, lang);
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

    @Override
    public boolean isTopConcept() {
        if (hasProperty(SKOS.topConceptOf))
            return true;
        return false;
    }

    @Override
    public ExtendedIterator<ConceptScheme> listSchemes() {
        List<ConceptScheme> schemes = Lists.newArrayList();
        for (RDFNode node : listPropertyValues(SKOS.inScheme).toList()) {
            if (node.isResource()) {
                schemes.add(new ConceptImpl(node.as(Individual.class)).as(ConceptScheme.class));
            }
        }
        return UniqueExtendedIterator.create(schemes.iterator());
    }

    /**
     * TODO!
     */
    @Override
    public ExtendedIterator<Collection> listCollections() {
        List<Collection> collections = Lists.newArrayList();
        // / TODO: the skos:member property is defined as 'has member'.
        return UniqueExtendedIterator.create(collections.iterator());
    }

    @Override
    public boolean isTopConceptOf(Resource scheme) {
        for (RDFNode other : listPropertyValues(SKOS.topConceptOf).toList()) {
            if (other.isResource() && !other.isAnon() && !scheme.isAnon())
                return ((Resource) other).getURI().equals(scheme.getURI());
            else if (other.isResource() && other.isAnon() && scheme.isAnon())
                return ((Resource) other).getId().equals(scheme.getId());
        }
        return false;
    }
}
