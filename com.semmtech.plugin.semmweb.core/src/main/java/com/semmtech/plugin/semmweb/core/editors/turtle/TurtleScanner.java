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

package com.semmtech.plugin.semmweb.core.editors.turtle;


import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;


/**
 * See Turtle Grammar:
 * http://www.w3.org/TeamSubmission/turtle/#sec-grammar-grammar
 * 
 * @author Mike Henrichs
 * 
 */
public class TurtleScanner extends RuleBasedScanner {

    public TurtleScanner(ColorManager manager) {
        IToken prefix = new Token(new TextAttribute(manager.getColor(TurtleColorConstants.PREFIX)));

        IRule[] rules = new IRule[2];
        rules[0] = new SingleLineRule("@prefix", " .", prefix);
        rules[1] = new WhitespaceRule(new TurtleWhitespaceDetector());

        setRules(rules);
    }
}
