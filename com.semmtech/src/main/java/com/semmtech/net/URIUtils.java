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

package com.semmtech.net;


import com.google.common.base.Strings;


/**
 * 
 * @author Mike Henrichs
 * @author Simone Rondelli
 */
public final class URIUtils {

    private URIUtils() {

    }

    public static String combineSegments(String... segments) {
        String result = "";
        for (String segment : segments) {
            if (Strings.isNullOrEmpty(segment)) {
                continue;
            }
            if (result.length() > 0 && !result.endsWith("/") && !segment.startsWith("/")) {
                result += "/";
            }
            result += segment;
        }
        return result;
    }

    public static String combineSegments(boolean trailingSlash, String... segments) {
        String result = combineSegments(segments);
        if (trailingSlash && !result.endsWith("/")) {
            result += "/";
        }
        else if (!trailingSlash && result.endsWith("/")) {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }

    public static String normalizeUrl(String altUrl) {
        return altUrl.replace(" ", "%20").replace("\\", "/");
    }
}
