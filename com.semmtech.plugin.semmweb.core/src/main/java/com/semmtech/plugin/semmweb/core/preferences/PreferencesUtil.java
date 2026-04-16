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

package com.semmtech.plugin.semmweb.core.preferences;


import java.util.Map;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import com.google.common.collect.Maps;


public class PreferencesUtil {
    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(PreferencesUtil.class);

    public static String[] decode(String value, String delimiter) {
        StringTokenizer tokenizer = new StringTokenizer(value, delimiter, true);
        int count = 0;
        int index = -1;
        int nextIndex = value.indexOf(delimiter);
        while (nextIndex >= index) {
            count++;
            index = nextIndex + delimiter.length();
            nextIndex = value.indexOf(delimiter, index);
        }
        String[] elements = new String[count]; // new String[tokenCount];
        boolean prevDelim = true;
        int i = 0;
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            if (token.equals(delimiter)) {
                if (prevDelim)
                    elements[i++] = ""; // / Double delimeters, there is a empty
                                        // string between them
                else
                    prevDelim = true;
            }
            else {
                elements[i++] = token;
                prevDelim = false;
            }
        }
        return elements;
    }

    public static String encode(Object[] values, String delimiter) {
        StringBuffer buffer = new StringBuffer();
        for (Object value : values) {
            if (value == null)
                buffer.append("");
            else
                buffer.append(value.toString());
            buffer.append(delimiter);
        }
        return buffer.toString();
    }

    public static Map<String, String> decodeMap(String inputString) {
        Map<String, String> result = Maps.newLinkedHashMap();
        StringTokenizer rows = new StringTokenizer(inputString, ";", false);
        while (rows.hasMoreTokens()) {
            String row = rows.nextToken();
            String[] values = row.split(">:<");
            if (values.length == 2) {
                String key = values[0].subSequence(1, values[0].length()).toString();
                String value = values[1].subSequence(0, values[1].length() - 1).toString();
                result.put(key, value);
            }
        }
        return result;
    }

    public static String encodeMap(Map<String, String> map) {
        StringBuffer buffer = new StringBuffer();
        for (String key : map.keySet()) {
            if (key.contains("<") || key.contains(">"))
                throw new IllegalArgumentException("One of the keys contains illegal characters: '"
                        + key + "'");
            String value = map.get(key);
            buffer.append(String.format("<%s>:<%s>;", key, value));
        }
        return buffer.toString();
    }

}
