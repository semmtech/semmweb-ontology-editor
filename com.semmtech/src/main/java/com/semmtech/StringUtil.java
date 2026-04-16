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


/**
 * 
 * @author Sander Stolk
 */
public final class StringUtil {

    private StringUtil() {
    }

    /**
     * Returns the absolute position of a character, based on the line index and
     * position within the current line.
     * 
     * @param text
     *            the complete text
     * @param lineIndex
     *            index of the line on which the character is found (1-based)
     * @param positionInLine
     *            index of character on the line (0-based)
     * @return
     */
    public static int getAbsoluteCharPosition(String text, int lineIndex, int positionInLine) {
        int offset = 0;
        String regex = "\n";
        int separatorSize = 1;
        String[] lines = text.split(regex);

        if (lineIndex > lines.length) {
            throw new IndexOutOfBoundsException(
                    "Line number exceeds number of lines in the input text!");
        }

        for (int i = 0; i < lineIndex - 1; i++) {
            offset += lines[i].length() + separatorSize;
        }
        return offset + positionInLine;
    }

    /**
     * Compares two strings and returns true if they're equal. The two strings
     * are considered equal only if they are either both null or both not null
     * and have the same value.
     */
    public static boolean equals(String lh, String rh) {
        if (lh == null && rh == null) {
            return true;
        }
        if (lh != null && lh.equals(rh)) {
            return true;
        }
        return false;
    }
}
