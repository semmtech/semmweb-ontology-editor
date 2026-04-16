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

package com.semmtech.semantics.skos;


import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;


/**
 * The fundamental element of the SKOS vocabulary is the concept. Concepts are
 * the units of thought [WillpowerGlossary]—ideas, meanings, or (categories of)
 * objects and events—which underlie many knowledge organization systems
 * [SKOS-UCR]. As such, concepts exist in the mind as abstract entities which
 * are independent of the terms used to label them.
 * 
 * @author Mike Henrichs
 * 
 */
public interface Concept extends Individual {
    public ExtendedIterator<Concept> listNarrowerConcepts();

    public ExtendedIterator<Concept> listBroaderConcepts();

    public ExtendedIterator<Concept> listRelatedConcepts();

    public ExtendedIterator<Literal> listPrefLabels();

    public String getPrefLabel(String lang);

    public ExtendedIterator<Literal> listAltLabels();

    public ExtendedIterator<Literal> listAltLabels(String lang);

    public ExtendedIterator<Literal> listHiddenLabels();

    public ExtendedIterator<Literal> listHiddenLabels(String lang);

    // / Notations:
    public ExtendedIterator<Literal> listNotations();

    // / Documentation:
    public ExtendedIterator<Literal> listScopeNotes();

    public ExtendedIterator<Literal> listHistoryNotes();

    public ExtendedIterator<Literal> listExamples();

    public ExtendedIterator<Literal> listEditorialNotes();

    public ExtendedIterator<Literal> listDefinitions();

    public ExtendedIterator<Literal> listChangeNotes();

    public boolean isTopConcept();

    public boolean isTopConceptOf(Resource scheme);

    public ExtendedIterator<ConceptScheme> listSchemes();

    public ExtendedIterator<Collection> listCollections();
}
