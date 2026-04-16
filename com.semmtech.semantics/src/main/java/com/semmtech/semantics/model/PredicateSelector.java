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

package com.semmtech.semantics.model;


import java.util.Collection;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;


public class PredicateSelector extends SimpleSelector {
    private final List<Property> predicates;

    public PredicateSelector(Property predicate) {
        this.predicates = ImmutableList.of(predicate);
    }

    public PredicateSelector(Property[] predicates) {
        this.predicates = ImmutableList.copyOf(predicates);
    }

    public PredicateSelector(Collection<Property> predicates) {
        this.predicates = ImmutableList.copyOf(predicates);
    }

    @Override
    public boolean selects(Statement s) {
        return predicates.contains(s.getPredicate());
    }

    public static PredicateSelector of(Property... predicates) {
        return new PredicateSelector(predicates);
    }
}
