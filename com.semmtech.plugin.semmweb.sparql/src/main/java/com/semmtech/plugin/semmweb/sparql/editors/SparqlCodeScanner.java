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

package com.semmtech.plugin.semmweb.sparql.editors;


import java.util.List;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWhitespaceDetector;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

import com.google.common.collect.Lists;


public class SparqlCodeScanner extends RuleBasedScanner {

    private static final class PrefixRule implements IRule {
        private final String PREFIX_CODE = "PREFIX";
        private final IToken token;

        public PrefixRule(IToken token) {
            this.token = token;
        }

        @Override
        public IToken evaluate(ICharacterScanner scanner) {
            int character = scanner.read();
            for (int i = 0; i < PREFIX_CODE.length(); i++) {
                if (PREFIX_CODE.charAt(i) != character) {
                    scanner.unread();
                    return Token.UNDEFINED;
                }
                character = scanner.read();
            }
            scanner.unread();
            return token;
        }
    }

    @SuppressWarnings("unused")
    private static final class KeywordRule implements IRule {
        private final IToken token;

        public KeywordRule(IToken token) {
            this.token = token;
        }

        private boolean isKeyword(ICharacterScanner scanner, String keyword) {
            int character = scanner.read();
            for (int i = 0; i < keyword.length(); i++) {
                if (keyword.charAt(i) != character) {
                    scanner.unread();
                    return false;
                }
                character = scanner.read();
            }
            scanner.unread();
            return true;
        }

        @Override
        public IToken evaluate(ICharacterScanner scanner) {
            for (String keyword : KEYWORDS) {
                if (isKeyword(scanner, keyword)) {
                    return token;
                }
            }
            return Token.UNDEFINED;
        }
    }

    private class WhitespaceDetector implements IWhitespaceDetector {

        @Override
        public boolean isWhitespace(char c) {
            return (c == ' ' || c == '\t' || c == '\n' || c == '\r');
        }
    }

    @SuppressWarnings("unused")
    private class KeywordDetector implements IWordDetector {
        private final String keyword;

        public KeywordDetector(String keyword) {
            this.keyword = keyword;
        }

        @Override
        public boolean isWordStart(char c) {
            return (keyword.charAt(0) == c);
        }

        @Override
        public boolean isWordPart(char c) {
            return (keyword.indexOf(c) > 0);
        }

    }

    static String[] KEYWORDS = { "BASE", "PREFIX", "SELECT", "ASK", "CONSTRUCT", "DESCRIBE",
            "ORDER BY", "LIMIT", "OFFSET", "DISTINCT", "REDUCED", "FROM", "FROM NAMED", "WHERE",
            "GRAPH", "OPTIONAL", "FILTER", "UNION", "a", "STR", "LANG", "LANGMATCHES", "DATATYPE",
            "BOUND", "sameTerm", "isURI", "isIRI", "isLiteral", "regex", "TRUE", "FALSE" };

    public SparqlCodeScanner(ColorManager manager) {
        super();
        initialize();
    }

    private void initialize() {
        List<IRule> rules = createRules();
        if (rules != null) {
            IRule[] result = new IRule[rules.size()];
            rules.toArray(result);
            setRules(result);
        }
    }

    protected List<IRule> createRules() {
        List<IRule> rules = Lists.newArrayList();

        IToken token = new Token(new TextAttribute(Display.getDefault().getSystemColor(
                SWT.COLOR_DARK_MAGENTA), Display.getDefault().getSystemColor(SWT.COLOR_WHITE),
                SWT.BOLD));

        rules.add(new PrefixRule(token));
        // rules.add(new KeywordRule(token));
        // for (String keyword : KEYWORDS)
        // rules.add(new WordRule(new KeywordDetector(keyword), token, true));

        rules.add(new WhitespaceRule(new WhitespaceDetector()));

        return rules;
    }

}
