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

package com.semmtech.plugin.semmweb.core.util;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import com.google.common.collect.Lists;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.rulesys.GenericRuleReasoner;
import com.hp.hpl.jena.reasoner.rulesys.Rule;
import com.hp.hpl.jena.reasoner.rulesys.Rule.Parser;


/**
 * Util class to be used for working with rules and files.
 * 
 * @author Mike Henrichs
 * 
 */
public final class RulesUtil {
    private static final Logger logger = Logger.getLogger(RulesUtil.class);

    private RulesUtil() {
    }

    /**
     * Returns a list of Rule objects found in the specified rules file; if file
     * is not found null is returned.
     * 
     * @param symbolicName
     * @param path
     * @param filePattern
     * @return
     */
    public static List<Rule> parseRulesFromBundle(String symbolicName, String path,
            String filePattern) {
        try {
            Bundle bundle = Platform.getBundle(symbolicName);
            Enumeration<URL> urls = bundle.findEntries(path, filePattern, true);
            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                Parser parser = Rule.rulesParserFromReader(new BufferedReader(
                        new InputStreamReader(url.openStream())));
                return Rule.parseRules(parser);
            }
        }
        catch (Exception ex) {
            logger.error(String
                    .format("Error trying to parse rules from bundle (symbolicName='%s'; path='%s'; filePattern='%s'): %s",
                            symbolicName, path, filePattern, ex.getMessage()));
        }
        return null;
    }

    /**
     * Returns a string containing the source of the specified file; if file is
     * not found null is returned.
     * 
     * @param symbolicName
     * @param path
     * @param filePattern
     * @return
     */
    public static String getSourceFromBundle(String symbolicName, String path, String filePattern) {
        try {
            Bundle bundle = Platform.getBundle(symbolicName);
            Enumeration<URL> urls = bundle.findEntries(path, filePattern, true);
            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                InputStreamReader reader = new InputStreamReader(url.openStream());
                BufferedReader buffered = new BufferedReader(reader);
                StringBuilder builder = new StringBuilder();
                String line = buffered.readLine();
                while (line != null) {
                    builder.append(line + "\n");
                    line = buffered.readLine();
                }
                return builder.toString();
            }
        }
        catch (Exception ex) {
            logger.error(String
                    .format("Error trying to parse source from bundle (symbolicName='%s'; path='%s'; filePattern='%s'): %s",
                            symbolicName, path, filePattern, ex.getMessage()));
        }
        return null;
    }

    public static Reasoner reasonerFromSource(String source) {
        List<Rule> rules = Lists.newArrayList();
        StringReader reader = new StringReader(source);
        Parser parser = Rule.rulesParserFromReader(new BufferedReader(reader));
        rules = Rule.parseRules(parser);
        return new GenericRuleReasoner(rules);
    }
}
