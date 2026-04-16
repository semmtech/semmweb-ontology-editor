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

package com.semmtech.plugin.semmweb.core.util;


import com.hp.hpl.jena.rdf.model.Model;
import com.semmtech.plugin.semmweb.core.dialog.UniqueResourceURIValidator;


/**
 * A Util class for dealing with URI's for resources.
 * 
 * @author Mike Henrichs
 * 
 */
public class ResourceURIUtil {
    public static final int FORMAT_DEFAULT = 0;
    public static final int FORMAT_PROPERTY = 1;

    /**
     * Returns the prefix from a possibly prefixed name. If the name does not
     * have a prefix an empty string is returned.
     * 
     * @param fullname
     * @return
     */
    public static String extractPrefix(String fullname) {
        String prefix = "";
        if (fullname.startsWith("<") && fullname.endsWith(">")) {
            return prefix;
        }
        if (fullname.contains(":")) {
            prefix = fullname.substring(0, fullname.indexOf(":"));
        }
        return prefix;
    }

    /**
     * Returns the localname from a possibly prefixed name.
     * 
     * @param fullname
     * @return
     */
    public static String extractLocalName(String fullname) {
        String localName = fullname;
        if (fullname.startsWith("<") && fullname.endsWith(">")) {
            return fullname.substring(1, fullname.length() - 1);
        }
        if (fullname.contains(":")) {
            localName = fullname.substring(fullname.lastIndexOf(":") + 1);
        }
        return localName;
    }

    public static String generateValidLocalname(String text, int uriFormat) {
        String[] parts = text.split(" ");
        String result = "";
        for (int i = 0; i < parts.length; i++) {
            String part = "";
            for (int j = 0; j < parts[i].length(); j++) {
                char c = parts[i].charAt(j);
                if (c == '_' || c == '-') {
                    part += "_";
                }
                else if (Character.isLetterOrDigit(c)) {
                    part += c;
                }
            }
            if (i == 0 && part.length() > 0 && Character.isDigit(part.charAt(0))) {
                part = "_" + part;
            }
            if (part.length() == 0) {
                part = "";
            }
            if (part.length() > 0) {
                if (i == 0 && uriFormat == FORMAT_PROPERTY) {
                    result += part.substring(0, 1).toLowerCase();
                }
                else {
                    result += part.substring(0, 1).toUpperCase();
                }
                if (part.length() > 1) {
                    result += part.substring(1).toLowerCase();
                }
            }
        }
        return result;
    }

    public static String createURI(Model model, String baseUri, String prefix, String text) {
        UniqueResourceURIValidator resourceValidator = new UniqueResourceURIValidator(model,
                baseUri);
        // / Create an URI from the name
        String uri = generateValidLocalname(text, FORMAT_DEFAULT);
        int index = 1;
        String uniqueUri = uri;
        String prefixedUri = uniqueUri;
        if (!prefixedUri.contains(":")) {
            prefixedUri = prefix + ":" + prefixedUri;
        }
        while (resourceValidator.isValid(prefixedUri) != null) {
            uniqueUri = uri + "_" + index++;
            prefixedUri = uniqueUri;
            if (!prefixedUri.contains(":")) {
                prefixedUri = prefix + ":" + prefixedUri;
            }
        }
        return prefixedUri;
    }

}
