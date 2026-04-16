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
import com.semmtech.semantics.skos.Collection;
import com.semmtech.semantics.skos.Concept;


public class CollectionTreeData extends IndividualTreeData implements Collection {
    private final Collection collection;

    public CollectionTreeData(Collection collection) {
        super(collection);
        this.collection = collection;
    }

    @Override
    public String getPrefLabel(String lang) {
        return collection.getPrefLabel(lang);
    }

    @Override
    public ExtendedIterator<Literal> listAltLabels() {
        return collection.listAltLabels();
    }

    @Override
    public ExtendedIterator<Literal> listAltLabels(String lang) {
        return collection.listAltLabels(lang);
    }

    @Override
    public ExtendedIterator<Literal> listHiddenLabels() {
        return collection.listHiddenLabels();
    }

    @Override
    public ExtendedIterator<Literal> listHiddenLabels(String lang) {
        return collection.listHiddenLabels(lang);
    }

    @Override
    public ExtendedIterator<Concept> listMembers() {
        return collection.listMembers();
    }

    @Override
    public ExtendedIterator<Literal> listPrefLabels() {
        return collection.listPrefLabels();
    }

    @Override
    public ExtendedIterator<Literal> listChangeNotes() {
        return collection.listChangeNotes();
    }

    @Override
    public ExtendedIterator<Literal> listDefinitions() {
        return collection.listDefinitions();
    }

    @Override
    public ExtendedIterator<Literal> listEditorialNotes() {
        return collection.listEditorialNotes();
    }

    @Override
    public ExtendedIterator<Literal> listExamples() {
        return collection.listExamples();
    }

    @Override
    public ExtendedIterator<Literal> listHistoryNotes() {
        return collection.listHistoryNotes();
    }

    @Override
    public ExtendedIterator<Literal> listScopeNotes() {
        return collection.listScopeNotes();
    }

    @Override
    public ExtendedIterator<Literal> listNotations() {
        return collection.listNotations();
    }
}
