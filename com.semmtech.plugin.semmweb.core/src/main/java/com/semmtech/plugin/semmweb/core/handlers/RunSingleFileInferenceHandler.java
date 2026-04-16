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
import org.eclipse.ui.handlers.HandlerUtil;

import com.hp.hpl.jena.reasoner.rulesys.Rule;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.dialog.RulesFileInputDialog;
import com.semmtech.plugin.semmweb.core.model.IModelProvider;


public class RunSingleFileInferenceHandler extends AbstractHandler {
    private static final Logger logger = Logger.getLogger(RunSingleFileInferenceHandler.class);
    public static final String ID = "com.semmtech.plugin.semmweb.core.commands.runSingleFileInference";

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        logger.debug("execute called!");
        RulesFileInputDialog dialog = new RulesFileInputDialog(HandlerUtil.getActiveShell(event),
                "Rules File", "Select an input file containing the inference rules.");
        if (dialog.open() == 0) {
            String ruleUrl = dialog.getFilename();
            if (ruleUrl != null) {

                IModelProvider provider = CorePlugin.getDefault().getActiveModelProvider();
                if (provider != null) {
                    List<Rule> rules = Rule.rulesFromURL(ruleUrl);
                    logger.debug("inference will be run on " + ruleUrl + " using " + rules.size()
                            + " rules");
                    // Reasoner reasoner = new GenericRuleReasoner(rules);
                    // boolean onCurrent =
                    // ReasoningPreference.executeOnIntermediate();
                    // provider.execute(reasoner, onCurrent);
                }
            }
        }
        return null;
    }

}
