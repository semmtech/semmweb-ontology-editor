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

package com.semmtech.plugin.semmweb.dictionary.model;


import java.util.List;

import com.google.common.collect.Lists;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Resource;


public class DictionaryEntry {
    private Resource resource;
    private List<Literal> labels;
    private List<Literal> comments;

    public DictionaryEntry(Resource resource) {
        this.resource = resource;
        this.labels = Lists.newArrayList();
        this.comments = Lists.newArrayList();
    }

    public Resource getResource() {
        return resource;
    }

    public List<Literal> getLabels() {
        return labels;
    }

    public List<Literal> getOtherLabels(String exclude, String language) {
        List<Literal> otherLabels = Lists.newArrayList();
        for (Literal literal : labels) {
            if (literal.getString().equals(exclude) && literal.getLanguage() != null
                    && literal.getLanguage().equals(language))
                continue;
            otherLabels.add(literal);
        }
        return otherLabels;
    }

    public List<Literal> getComments() {
        return comments;
    }
}
