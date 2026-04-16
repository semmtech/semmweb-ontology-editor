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


import java.util.regex.Pattern;

import com.google.common.base.Strings;


public class NamespaceURIValidator extends WorkspaceOntologySpecValidator {

    private static final String URI_REGEX = "^(https?)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|](/|#)";
    private static final Pattern URI_PATTERN = Pattern.compile(URI_REGEX);

    @Override
    public String isValidPublicUri(String uri) {
        String result = super.isValidPublicUri(uri);
        if (!Strings.isNullOrEmpty(result)) {
            return result;
        }

        if (Strings.isNullOrEmpty(uri)) {
            return "The URI still needs to be specified.";
        }
        if (!uri.endsWith("/") && !uri.endsWith("#")) {
            return "The URI needs to end in a '/' or '#'.";
        }
        if (!URI_PATTERN.matcher(uri).matches()) {
            return "The URI does not appear to be valid.";
        }
        return null;
    }
}
