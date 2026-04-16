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

package com.semmtech.plugin.semmweb.core;


import java.io.BufferedReader;
import java.io.StringReader;
import java.util.List;

import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.reasoner.rulesys.GenericRuleReasoner;
import com.hp.hpl.jena.reasoner.rulesys.Rule;
import com.hp.hpl.jena.reasoner.rulesys.Rule.Parser;
import com.semmtech.plugin.semmweb.core.extensionpoint.AbstractModelProcessor;
import com.semmtech.plugin.semmweb.core.extensionpoint.IModelProcessor;
import com.semmtech.plugin.semmweb.core.util.RulesUtil;


@SuppressWarnings("unused")
public class LiteralGeneralizationProcessor extends AbstractModelProcessor implements
        IModelProcessor {

    public static final String ID = "com.semmtech.plugin.semmweb.core.processor.literalGeneralization";

    public LiteralGeneralizationProcessor() {
        super(ID, CorePlugin.getDefault().getPreferenceStore(), true);
    }

    @Override
    public void processModel(Model model) {
        String source = RulesUtil.getSourceFromBundle(CorePlugin.PLUGIN_ID,
                "src/main/resources/rules/", "literal-generalizations.rules");
        StringReader reader = new StringReader(source);
        Parser parser = Rule.rulesParserFromReader(new BufferedReader(reader));
        List<Rule> rules = Rule.parseRules(parser);
        GenericRuleReasoner reasoner = new GenericRuleReasoner(rules);

        synchronized (model) {
            InfModel inf = ModelFactory.createInfModel(reasoner, model);
            model.removeAll();
            model.add(inf);
        }
    }

    @Override
    public String getName() {
        return "Literal Generalization";
    }

    @Override
    public String getDescription() {
        return "Proccesses model and generalizes all literals.";
    }
}
