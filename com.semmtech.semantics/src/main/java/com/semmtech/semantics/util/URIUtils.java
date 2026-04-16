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


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.Normalizer;


public class URIUtils {

    /**
     * Returns a URI friendly local name, which can be combined with a
     * namespace, based on the given name.
     * 
     * This method uses the following rules:TODO
     * 
     * @param name
     * @return
     */
    public static String generateURIResourceName(String name) {
        return generateURIFriendlyName(name, true);
    }

    public static String generateURIResourceName(String ns, String name) {
        String localName = generateURIFriendlyName(name, true);
        return String.format("%s%s", ns, localName);
    }

    public static String generateURIPropertyName(String name) {
        return generateURIFriendlyName(name, false);
    }

    public static String generateURIPropertyName(String ns, String name) {
        String localName = generateURIFriendlyName(name, false);
        return String.format("%s%s", ns, localName);
    }

    public static String generateURIFriendlyName(String name, boolean firstUpper) {
        name = name.trim();
        // The following two lines seperate the accent from the letter; the
        // replace replace the accent with nothing. The expr \\P{M} refers to
        // the letter and \\p{M} to the accent.
        name = Normalizer.normalize(name, Normalizer.Form.NFD);
        name = name.replaceAll("\\p{M}+", "");
        String lettersAndDigits = "";
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (i == 0 && Character.isDigit(c)) {
                lettersAndDigits += "_";
            }
            if (Character.isSpaceChar(c) || Character.isLetterOrDigit(c)) {
                lettersAndDigits += c;
            }
            else if (c == ',') {
                continue;
            }
            else if (Character.isWhitespace(c)) {
                continue;
            }
            else {
                lettersAndDigits += "_";
            }
        }
        String localname = "";
        String[] segments = lettersAndDigits.split(" ");
        for (String segment : segments) {
            if (segment.length() == 0) {
                continue;
            }
            segment = segment.toLowerCase();
            try {
                segment = URLEncoder.encode(segment, "UTF-8");
            }
            catch (UnsupportedEncodingException ex) {
                ex.printStackTrace();
            }
            if (localname.length() > 0 || firstUpper) {
                if (segment.length() == 1) {
                    segment = segment.toUpperCase();
                }
                else {
                    segment = segment.substring(0, 1).toUpperCase()
                            + segment.substring(1, segment.length());
                }
            }
            localname += segment;
        }
        return localname;
    }
}
