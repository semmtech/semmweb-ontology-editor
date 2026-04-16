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

package com.semmtech.semantics.util;


import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;


/**
 * Rewriter used to rewrite namespace URIs.
 * 
 * @author Mike Henrichs
 * 
 */
public class NamespaceRewriter {

    private final Map<String, NamespaceRewriteRule> rules;

    public NamespaceRewriter() {
        this.rules = Maps.newHashMap();
    }

    /**
     * Clears the rules.
     */
    public void clear() {
        rules.clear();
    }

    /**
     * Adds a new rewrite rule to the rewriter.
     * 
     * @param rule
     */
    public void addRule(NamespaceRewriteRule rule) {
        rules.put(rule.getFrom(), rule);
    }

    /**
     * Adds a new rewrite to the rewriter.
     * 
     * @param from
     * @param to
     * @param updatePrefixMap
     */
    public void addRewrite(String from, String to, boolean updatePrefixMap) {
        NamespaceRewriteRule rule = new NamespaceRewriteRule(from, to, updatePrefixMap);
        addRule(rule);
    }

    /**
     * Adds all the given rules to the collection of rules used by the rewriter.
     * 
     * @param rules
     */
    public void addAllRules(Collection<NamespaceRewriteRule> rules) {
        for (NamespaceRewriteRule rule : rules) {
            addRule(rule);
        }
    }

    /**
     * Rewrites the namespace URI of the entire model, based on the rewrite pair
     * added to this rewriter.
     * 
     * @param original
     * @return
     */
    public void rewrite(Model original) {
        List<Statement> statements = original.listStatements().toList();
        List<Statement> rewritten = Lists.newArrayListWithCapacity(statements.size());
        for (Statement stmt : statements) {
            Resource subject = stmt.getSubject();
            Property predicate = stmt.getPredicate();
            RDFNode node = stmt.getObject();
            if (!subject.isAnon()) {
                subject = translate(original, subject);
            }
            if (!predicate.isAnon()) {
                predicate = translate(original, predicate);
            }
            if (node.isResource() && !node.isAnon()) {
                node = translate(original, node.asResource());
            }
            rewritten.add(original.createStatement(subject, predicate, node));
        }
        original.remove(statements);
        original.add(rewritten);
        for (NamespaceRewriteRule rule : rules.values()) {
            if (rule.isUpdatePrefixMap()) {
                String prefix = original.getNsURIPrefix(rule.getFrom());
                if (prefix != null) {
                    original.setNsPrefix(prefix, rule.getTo());
                }
            }
        }
    }

    private Resource translate(Model model, Resource original) {
        String ns = original.getNameSpace();
        String localname = original.getLocalName();
        if (rules.containsKey(ns)) {
            NamespaceRewriteRule rule = rules.get(ns);
            return model.createResource(rule.getTo() + localname);
        }
        return original;
    }

    private Property translate(Model model, Property original) {
        String ns = original.getNameSpace();
        String localname = original.getLocalName();
        if (rules.containsKey(ns)) {
            NamespaceRewriteRule rule = rules.get(ns);
            return model.createProperty(rule.getTo() + localname);
        }
        return original;
    }

    /**
     * Rewrites the namespace URI of the entire model, based on the rewrite pair
     * added to this rewriter and returns the result as a new model.
     * 
     * @param original
     * @param copyPrefixes
     * @return
     */
    public Model rewriteToCopy(Model original, boolean copyPrefixes) {
        Model result = ModelFactory.createDefaultModel();
        List<Statement> statements = original.listStatements().toList();
        for (Statement stmt : statements) {
            Resource subject = stmt.getSubject();
            Property predicate = stmt.getPredicate();
            RDFNode node = stmt.getObject();
            if (!subject.isAnon()) {
                subject = translate(original, subject);
            }
            if (!predicate.isAnon()) {
                predicate = translate(original, predicate);
            }
            if (node.isResource() && !node.isAnon()) {
                node = translate(original, node.asResource());
            }
            result.add(original.createStatement(subject, predicate, node));
        }
        if (copyPrefixes) {
            Map<String, String> prefixMap = original.getNsPrefixMap();
            for (String prefix : prefixMap.keySet()) {
                String ns = prefixMap.get(prefix);
                result.setNsPrefix(prefix, ns);
            }
            for (NamespaceRewriteRule rule : rules.values()) {
                if (rule.isUpdatePrefixMap()) {
                    String prefix = original.getNsURIPrefix(rule.getFrom());
                    original.setNsPrefix(prefix, rule.getTo());
                }
            }
        }
        return result;
    }
}
