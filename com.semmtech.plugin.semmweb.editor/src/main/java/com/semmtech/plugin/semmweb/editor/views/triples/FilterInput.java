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

package com.semmtech.plugin.semmweb.editor.views.triples;


import java.util.List;

import org.apache.commons.collections4.bag.TreeBag;
import org.eclipse.jface.viewers.LabelProvider;

import com.google.common.collect.Lists;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.semmtech.plugin.semmweb.core.viewers.ResourceNameComparator;


public final class FilterInput {
    public static final FilterInput Empty = new FilterInput();

    private final TreeBag<Resource> subjects;
    private final TreeBag<Property> predicates;
    private final TreeBag<RDFNode> objects;

    private FilterInput() {
        this(new LabelProvider());
    }

    public FilterInput(LabelProvider labelProvider) {
        ResourceNameComparator comparator = new ResourceNameComparator(labelProvider);
        subjects = new TreeBag<>(comparator);
        predicates = new TreeBag<>(comparator);
        objects = new TreeBag<>(comparator);
    }

    public void clear() {
        subjects.clear();
        predicates.clear();
        objects.clear();
    }

    public void addSubject(Resource subject) {
        subjects.add(subject);
    }

    public void addPredicate(Property predicate) {
        predicates.add(predicate);
    }

    public void addObject(RDFNode object) {
        objects.add(object);
    }

    public List<Resource> getSubjects() {
        return Lists.newArrayList(subjects.uniqueSet());
    }

    public List<Property> getPredicates() {
        return Lists.newArrayList(predicates.uniqueSet());
    }

    public List<RDFNode> getObjects() {
        return Lists.newArrayList(objects.uniqueSet());
    }
}
