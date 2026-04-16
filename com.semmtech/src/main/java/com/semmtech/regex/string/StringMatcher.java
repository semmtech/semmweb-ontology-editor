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

package com.semmtech.regex.string;


/**
 * The Class StringMatcher contains methods to match String values using the
 * methods.
 * 
 * @author Simone Rondelli
 */
public class StringMatcher {
    /**
     * Returns true if the string matches exactly "true" or "True".
     * 
     * @param value
     *            value to be matched
     * @return true if value contains "true" or "True"; otherwise false
     */
    public final static boolean isTrue(final String value) {
        return value.matches("[tT]rue");
    }

    /**
     * Returns true if the string matches exactly "true" or "True" or "yes" or
     * "Yes".
     * 
     * @param value
     *            value to be matched
     * @return true if value contains "true", "True", "yes" or "Yes"; otherwise
     *         false
     */
    public final static boolean isTrueOrYes(final String value) {
        return value.matches("[tT]rue|[yY]es");

    }
}
