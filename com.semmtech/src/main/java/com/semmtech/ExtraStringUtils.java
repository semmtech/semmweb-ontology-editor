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


import org.apache.commons.lang3.StringUtils;


/**
 * 
 * @author Mike Henrichs
 */
public class ExtraStringUtils extends StringUtils {
    private ExtraStringUtils() {
    }

    /**
     * Surround the value with quotes, or if the value is null returns the
     * default value.
     * 
     * @param value
     *            value to be surrounded by quotes
     * @param def
     *            default value
     * @return
     */
    public static String enquoteOrDefault(String value, String def) {
        return (value == null ? def : "\"" + value + "\"");
    }

    /**
     * <p>
     * Appends a substring only if it is not present at the end of the source
     * string.
     * </p>
     * 
     * <p>
     * A {@code null} source string will return {@code null}. An empty ("")
     * source string will append the search string to the end. A {@code null}
     * search string will return the source string.
     * </p>
     * 
     * <pre>
     * ExtraStringUtils.appendEnd(null, *)      = null
     * ExtraStringUtils.appendEnd("", *)        = *
     * ExtraStringUtils.appendEnd(*, null)      = *
     * ExtraStringUtils.appendEnd("www.domain.com", ".com")   = "www.domain.com"
     * ExtraStringUtils.appendEnd("www.domain", ".com")       = "www.domain.com"
     * ExtraStringUtils.appendEnd("abc", "")    = "abc"
     * </pre>
     * 
     * @param source
     * @param search
     * @return
     */
    public static String appendEnd(String source, String search) {
        if (source == null) {
            return null;
        }
        else if (search == null) {
            return source;
        }
        else if (source.endsWith(search)) {
            return source;
        }
        else {
            return source + search;
        }
    }

    /**
     * <p>
     * Appends a search string only if it is the source string does not start
     * with the given search string.
     * </p>
     * 
     * <p>
     * A {@code null} source string will return {@code null}. An empty ("")
     * source string will append the search string to the beginning. A
     * {@code null} search string will return the source string.
     * </p>
     * 
     * <pre>
     * ExtraStringUtils.appendStart(null, *)      = null
     * ExtraStringUtils.appendStart("", *)        = *
     * ExtraStringUtils.appendStart(*, null)      = *
     * ExtraStringUtils.appendStart("www.domain.com", "www.")   = "www.domain.com"
     * ExtraStringUtils.appendStart("domain.com", "www.")       = "www.domain.com"
     * ExtraStringUtils.appendStart("abc", "")    = "abc"
     * </pre>
     * 
     * @param source
     * @param search
     * @return
     */
    public static String appendStart(String source, String search) {
        if (source == null) {
            return null;
        }
        else if (search == null) {
            return source;
        }
        else if (source.startsWith(search)) {
            return source;
        }
        else {
            return search + source;
        }
    }
}
