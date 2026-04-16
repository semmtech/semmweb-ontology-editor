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


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.RDFS;


public class DictionaryModel {
    private Map<DictionaryLabelKey, DictionaryEntry> keyEntries = Maps.newHashMap();
    private Map<Resource, DictionaryEntry> entries = Maps.newHashMap();
    private Model model;
    private List<String> languages;

    public DictionaryModel() {
        this.model = null;
    }

    public DictionaryModel(Model model, List<String> languages) {
        this.model = model;
        this.languages = languages;
        createKeyEntries();
    }

    private void createKeyEntries() {
        if (model != null) {
            for (Statement labelStatement : model.listStatements(
                    new SimpleSelector(null, RDFS.label, (RDFNode) null)).toList()) {
                Literal label = labelStatement.getLiteral();
                String language = label.getLanguage();
                if (language == null)
                    language = "";
                if (languages.contains(language)) {
                    DictionaryLabelKey key = new DictionaryLabelKey(labelStatement.getSubject()
                            .getURI(), label.getString(), label.getLanguage());
                    keyEntries.put(key, null);
                }
            }
        }
    }

    public List<DictionaryLabelKey> listLabelKeys() {
        List<DictionaryLabelKey> keys = new ArrayList<>(keyEntries.keySet());
        Collections.sort(keys, new Comparator<DictionaryLabelKey>() {
            @Override
            public int compare(DictionaryLabelKey key0, DictionaryLabelKey key1) {
                return key0.getLabel().compareToIgnoreCase(key1.getLabel());
            }
        });
        return keys;
    }

    public DictionaryEntry getEntry(DictionaryLabelKey key) {
        DictionaryEntry entry = keyEntries.get(key);
        if (entry == null && model != null) {
            Resource resource = model.getResource(key.getUri());
            if (!entries.containsKey(resource)) {
                entry = new DictionaryEntry(resource);
                for (Statement statement : model.listStatements(
                        new SimpleSelector(resource, RDFS.label, (RDFNode) null)).toList())
                    entry.getLabels().add(statement.getLiteral());
                for (Statement statement : model.listStatements(
                        new SimpleSelector(resource, RDFS.comment, (RDFNode) null)).toList())
                    entry.getComments().add(statement.getLiteral());
                entries.put(resource, entry);
            }
            entry = entries.get(resource);
            keyEntries.put(key, entry);
        }
        return entry;
    }
}
