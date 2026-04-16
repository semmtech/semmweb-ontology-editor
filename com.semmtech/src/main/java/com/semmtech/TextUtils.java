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

package com.semmtech;


import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 
 * @author Mike Henrichs
 */
public final class TextUtils {
    private static final Pattern HTML_PATTERN = Pattern.compile("\\<(/?)((\\s*)(\\w))(.*?)\\>");

    private TextUtils() {

    }

    public static String stripHTML(String input) {
        Matcher matcher = HTML_PATTERN.matcher(input);
        String stripped = matcher.replaceAll("");
        stripped = stripped.replace("#&nbsp;", " ");
        stripped = stripped.replace("&nbsp;", " ");
        stripped = stripped.replace("&lt;", "<");
        stripped = stripped.replace("&gt;", ">");
        stripped = stripped.trim();
        return stripped;
    }
}
