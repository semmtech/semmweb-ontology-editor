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

package com.semmtech.plugin.semmweb.dictionary.widgets;


import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.semmtech.plugin.semmweb.core.widgets.trees.IndividualTreeData;
import com.semmtech.semantics.skos.Collection;
import com.semmtech.semantics.skos.Concept;
import com.semmtech.semantics.skos.ConceptScheme;


public class ConceptTreeData extends IndividualTreeData implements Concept {
    private final Concept concept;

    public ConceptTreeData(Concept concept) {
        super(concept);
        this.concept = concept;
    }

    @Override
    public String getPrefLabel(String lang) {
        return concept.getPrefLabel(lang);
    }

    @Override
    public ExtendedIterator<Literal> listAltLabels() {
        return concept.listAltLabels();
    }

    @Override
    public ExtendedIterator<Literal> listAltLabels(String lang) {
        return concept.listAltLabels(lang);
    }

    @Override
    public ExtendedIterator<Concept> listBroaderConcepts() {
        return concept.listBroaderConcepts();
    }

    @Override
    public ExtendedIterator<Literal> listHiddenLabels() {
        return concept.listHiddenLabels();
    }

    @Override
    public ExtendedIterator<Literal> listHiddenLabels(String lang) {
        return concept.listHiddenLabels(lang);
    }

    @Override
    public ExtendedIterator<Concept> listNarrowerConcepts() {
        return concept.listNarrowerConcepts();
    }

    @Override
    public ExtendedIterator<Literal> listPrefLabels() {
        return concept.listPrefLabels();
    }

    @Override
    public ExtendedIterator<Concept> listRelatedConcepts() {
        return concept.listRelatedConcepts();
    }

    @Override
    public ExtendedIterator<Literal> listChangeNotes() {
        return concept.listChangeNotes();
    }

    @Override
    public ExtendedIterator<Literal> listDefinitions() {
        return concept.listDefinitions();
    }

    @Override
    public ExtendedIterator<Literal> listEditorialNotes() {
        return concept.listEditorialNotes();
    }

    @Override
    public ExtendedIterator<Literal> listExamples() {
        return concept.listExamples();
    }

    @Override
    public ExtendedIterator<Literal> listHistoryNotes() {
        return concept.listHistoryNotes();
    }

    @Override
    public ExtendedIterator<Literal> listNotations() {
        return concept.listNotations();
    }

    @Override
    public ExtendedIterator<Literal> listScopeNotes() {
        return concept.listScopeNotes();
    }

    @Override
    public boolean isTopConcept() {
        return concept.isTopConcept();
    }

    @Override
    public boolean isTopConceptOf(Resource scheme) {
        return concept.isTopConceptOf(scheme);
    }

    @Override
    public ExtendedIterator<Collection> listCollections() {
        return concept.listCollections();
    }

    @Override
    public ExtendedIterator<ConceptScheme> listSchemes() {
        return concept.listSchemes();
    }
}
