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
import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.Lists;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.impl.OntModelImpl;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.util.iterator.UniqueExtendedIterator;
import com.semmtech.semantics.skos.Collection;
import com.semmtech.semantics.skos.Concept;
import com.semmtech.semantics.skos.ConceptScheme;
import com.semmtech.semantics.skos.DictionaryModel;
import com.semmtech.semantics.vocabulary.SKOS;


public class DictionaryModelImpl extends OntModelImpl implements DictionaryModel {

    public DictionaryModelImpl(OntModelSpec spec) {
        super(spec);
    }

    public DictionaryModelImpl(OntModelSpec spec, Model model) {
        super(spec, model);
    }

    @Override
    public ExtendedIterator<ConceptScheme> listConceptSchemes() {
        OntClass conceptScheme = getOntClass(SKOS.ConceptScheme.getURI());
        if (conceptScheme == null)
            return UniqueExtendedIterator.create((new HashSet<ConceptScheme>()).iterator());

        Set<OntClass> classes = conceptScheme.listSubClasses(false).toSet();
        classes.add(conceptScheme);

        Set<ConceptScheme> individuals = new HashSet<>();
        for (OntClass clazz : classes)
            for (Individual scheme : listIndividuals(clazz).toList())
                individuals.add(new ConceptSchemeImpl(scheme));
        return UniqueExtendedIterator.create(individuals.iterator());
    }

    @Override
    public ExtendedIterator<Concept> listConcepts() {
        OntClass conceptClass = getOntClass(SKOS.Concept.getURI());
        if (conceptClass == null)
            return UniqueExtendedIterator.create((new HashSet<Concept>()).iterator());

        Set<OntClass> conceptClasses = conceptClass.listSubClasses(false).toSet();
        conceptClasses.add(conceptClass);

        Set<Concept> individuals = new HashSet<>();
        for (OntClass clazz : conceptClasses)
            for (Individual concept : listIndividuals(clazz).toSet())
                individuals.add(new ConceptImpl(concept));
        return UniqueExtendedIterator.create(individuals.iterator());
    }

    @Override
    public ExtendedIterator<Collection> listCollections() {
        ArrayList<Collection> list = Lists.newArrayList();
        for (Individual collection : listIndividuals(SKOS.Collection).toList())
            list.add(new CollectionImpl(collection));
        return UniqueExtendedIterator.create(list.iterator());
    }

}
