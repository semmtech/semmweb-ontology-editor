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

package com.semmtech.plugin.semmweb.core.dialog;


import org.eclipse.jface.dialogs.IInputValidator;


public class PrefixInputValidator implements IInputValidator {
    // [A-Z] | "_" | [a-z] | [#x00C0-#x00D6] | [#x00D8-#x00F6] |
    // [#x00F8-#x02FF] | [#x0370-#x037D] | [#x037F-#x1FFF] | [#x200C-#x200D]
    // | [#x2070-#x218F] | [#x2C00-#x2FEF] | [#x3001-#xD7FF] |
    // [#xF900-#xFDCF] | [#xFDF0-#xFFFD] | [#x10000-#xEFFFF]
    public static boolean isValidNameStartChar(char c) {
        if (c >= 'A' && c <= 'Z')
            return true;
        if (c == '_')
            return true;
        if (c >= 'a' && c <= 'z')
            return true;
        if (c >= 0x00C0 && c <= 0x00D6)
            return true;
        if (c >= 0x00D8 && c <= 0x00F6)
            return true;
        if (c >= 0x00F8 && c <= 0x02FF)
            return true;
        if (c >= 0x0370 && c <= 0x037D)
            return true;
        if (c >= 0x037F && c <= 0x1FFF)
            return true;
        if (c >= 0x200C && c <= 0x200D)
            return true;
        if (c >= 0x2070 && c <= 0x218F)
            return true;
        if (c >= 0x2C00 && c <= 0x2FEF)
            return true;
        if (c >= 0x3001 && c <= 0xD7FF)
            return true;
        if (c >= 0xF900 && c <= 0xFDCF)
            return true;
        if (c >= 0xFDF0 && c <= 0xFFFD)
            return true;
        if (c >= 0x10000 && c <= 0xEFFFF)
            return true;
        return false;
    }

    // nameStartChar | '-' | [0-9] | #x00B7 | [#x0300-#x036F] |
    // [#x203F-#x2040]
    public static boolean isValidNameChar(char c) {
        if (isValidNameStartChar(c)) {
            return true;
        }
        if (c == '-')
            return true;
        if (c >= '0' && c <= '9')
            return true;
        if (c == 0x00B7)
            return true;
        if (c >= 0x0300 && c <= 0x036F)
            return true;
        return false;
    }

    // ( nameStartChar - '_' ) nameChar*
    public static String isValidPrefix(String prefix) {
        if ((prefix == null) || (prefix.length() == 0)) {
            return null;
        }
        char startChar = prefix.charAt(0);
        if (!isValidNameStartChar(startChar)) {
            return "The prefix contains an invalid initial character";
        }
        if (startChar == '_') {
            return "An underscore is not allowed as first character of a prefix.";
        }
        for (int i = 1; i < prefix.length(); i++) {
            if (!isValidNameChar(prefix.charAt(i))) {
                return String.format("The prefix contains an invalid character on position %d.",
                        i + 1);
            }
        }
        return null;
    }

    @Override
    public String isValid(String prefix) {
        return isValidPrefix(prefix);
    }
}
