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

package com.semmtech.plugin.semmweb.editor.reasoner;


import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.reasoner.ReasonerFactory;
import com.hp.hpl.jena.reasoner.rulesys.GenericRuleReasoner;
import com.hp.hpl.jena.reasoner.rulesys.Rule;


public class TaxonomyReasoner extends GenericRuleReasoner {

    /** Constant: used to indicate default RDFS processing level */
    public static final String DEFAULT_RULES = "default";

    /** The location of the default RDFS rule definitions on the class path */
    protected static final String RULE_FILE = "src/main/resources/rules/taxonomy-view.rules";

    /** The cached rule sets, indexed by processing level */
    protected static Map<String, List<Rule>> ruleSets = Maps.newHashMap();

    /** The rule file names, indexed by processing level */
    protected static Map<String, String> ruleFiles;

    static {
        ruleFiles = Maps.newHashMap();
        ruleFiles.put(DEFAULT_RULES, RULE_FILE);
    }

    public TaxonomyReasoner(ReasonerFactory parent) {
        super(loadRules(DEFAULT_RULES), parent);
        setMode(HYBRID);
        setTransitiveClosureCaching(true);
    }

    public TaxonomyReasoner(ReasonerFactory factory, Resource configuration) {
        this(factory);
        if (configuration != null) {
            // / TODO
            // StmtIterator i = configuration.listProperties();
            // while (i.hasNext()) {
            // Statement st = i.nextStatement();
            // doSetParameter(st.getPredicate(), st.getObject().toString());
            // }
        }
    }

}
