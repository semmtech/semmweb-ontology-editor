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


import java.util.Collection;

import com.google.common.collect.Lists;


/**
 * The Class Util.
 * 
 * @author Mike Henrichs
 * @author Simone Rondelli
 */
public final class Util {

    /**
     * Hidden constructor
     */
    private Util() {
    }

    /**
     * Checks if is null or empty.
     * 
     * @param value
     *            the value to be checked
     * @return true if the value is null or an empty String
     */
    public static Boolean isNullOrEmpty(final String value) {
        if (value == null) {
            return true;
        }
        return (value.length() == 0);
    }

    /**
     * First to upper.
     * 
     * @param value
     *            the value of which the first characters needs to be uppercase
     * @return the value which has its first character as upper case
     */
    public static String firstToUpper(final String value) {
        if (isNullOrEmpty(value)) {
            return value;
        }
        char first = value.charAt(0);
        if (Character.isLetter(first)) {
            first = Character.toUpperCase(first);
        }
        return first + value.substring(1, value.length());
    }

    /**
     * First to lower.
     * 
     * @param value
     *            the value
     * @return the string
     */
    public static String firstToLower(final String value) {
        if (isNullOrEmpty(value)) {
            return value;
        }
        char first = value.charAt(0);
        if (Character.isLetter(first)) {
            first = Character.toLowerCase(first);
        }
        return first + value.substring(1, value.length());
    }

    /**
     * Filters the collection using the provided predicate to test if elements
     * should be kept or ignored.
     * 
     * @param <T>
     *            the generic type
     * @param target
     *            the target collection to be filtered
     * @param predicate
     *            the predicate which tests membership
     * @return the collection only containing members according to the predicate
     */
    public static <T> Collection<T> filter(final Collection<T> target, final Predicate<T> predicate) {
        Collection<T> result = Lists.newArrayList();
        for (T item : target) {
            if (predicate.apply(item)) {
                result.add(item);
            }
        }
        return result;
    }
}
