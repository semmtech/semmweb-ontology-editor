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

package com.semmtech.plugin.semmweb.core.handlers;


import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.handlers.HandlerUtil;

import com.google.common.collect.Lists;
import com.hp.hpl.jena.reasoner.rulesys.Rule;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.model.IModelProvider;
import com.semmtech.plugin.semmweb.core.preferences.ReasoningPreference;
import com.semmtech.plugin.semmweb.core.reasoner.RuleConfiguration;


public class RunConfigurationInferenceHandler extends AbstractHandler {
    private static final Logger logger = Logger.getLogger(RunConfigurationInferenceHandler.class);
    public static final String ID = "com.semmtech.plugin.semmweb.core.commands.runConfigurationInference";

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        logger.debug("execute called!");
        RuleConfiguration configuration = ReasoningPreference.getRuleConfiguration();
        if (configuration == null) {
            logger.debug("configuration == null");
            MessageDialog
                    .openInformation(
                            HandlerUtil.getActiveShell(event),
                            "Run Inference",
                            "No rule configuration has been provided; please create a rule configuration before running inference.");
            RuleConfigurationHandler handler = new RuleConfigurationHandler();
            handler.execute(event);
        }
        else {
            IModelProvider provider = CorePlugin.getDefault().getActiveModelProvider();
            if (provider != null) {
                List<Rule> rules = Lists.newArrayList();
                for (String url : configuration.getRuleURLs()) {
                    rules.addAll(Rule.rulesFromURL(url));
                }
                logger.debug("inference will be run using " + rules.size() + " rules");
                // Reasoner reasoner = new GenericRuleReasoner(rules);
                // boolean onCurrent =
                // ReasoningPreference.executeOnIntermediate();
                // provider.execute(reasoner, onCurrent);
            }
        }
        return null;
    }

}
