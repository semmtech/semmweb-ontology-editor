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
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.semmtech.plugin.semmweb.core.widgets.trees.IndividualTreeData;
import com.semmtech.semantics.skos.Concept;
import com.semmtech.semantics.skos.ConceptScheme;


public class ConceptSchemeTreeData extends IndividualTreeData implements ConceptScheme {
    private final ConceptScheme scheme;

    public ConceptSchemeTreeData(ConceptScheme scheme) {
        super(scheme);
        this.scheme = scheme;
    }

    @Override
    public Concept createConcept() {
        return scheme.createConcept();
    }

    @Override
    public Concept createConcept(String uri) {
        return scheme.createConcept(uri);
    }

    @Override
    public String getCreator() {
        return scheme.getCreator();
    }

    @Override
    public ExtendedIterator<Literal> listTitles(String lang) {
        return scheme.listTitles(lang);
    }

    @Override
    public ExtendedIterator<Concept> listTopConcepts() {
        return scheme.listTopConcepts();
    }

    @Override
    public ExtendedIterator<Literal> listChangeNotes() {
        return scheme.listChangeNotes();
    }

    @Override
    public ExtendedIterator<Literal> listDefinitions() {
        return scheme.listDefinitions();
    }

    @Override
    public ExtendedIterator<Literal> listEditorialNotes() {
        return scheme.listEditorialNotes();
    }

    @Override
    public ExtendedIterator<Literal> listExamples() {
        return scheme.listExamples();
    }

    @Override
    public ExtendedIterator<Literal> listHistoryNotes() {
        return scheme.listHistoryNotes();
    }

    @Override
    public ExtendedIterator<Literal> listNotations() {
        return scheme.listNotations();
    }

    @Override
    public ExtendedIterator<Literal> listScopeNotes() {
        return scheme.listScopeNotes();
    }
}
