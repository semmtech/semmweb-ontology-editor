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


import java.net.URI;
import java.net.URISyntaxException;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.IInputValidator;

import com.hp.hpl.jena.rdf.model.Model;
import com.semmtech.plugin.semmweb.core.util.ResourceURIUtil;


/**
 * RFC2396: A Uniform Resource Identifier (URI) is a compact string of
 * characters for identifying an abstract or physical resource. This document
 * defines the generic syntax of URI, including both absolute and relative
 * forms, and guidelines for their use; it revises and replaces the generic
 * definitions in RFC 1738 and RFC 1808.
 * 
 * See http://www.ietf.org/rfc/rfc2396.txt
 * 
 * @author Mike Henrichs
 * 
 */
public class URIValidator implements IInputValidator {
    private final Model model;
    private final String baseUri;
    private Logger logger = Logger.getLogger(URIValidator.class);

    public URIValidator(Model model, String baseUri) {
        this.model = model;
        this.baseUri = baseUri;
    }

    public static String validateAbsoluteURI(String uri) {
        return validateAbsoluteURI(uri, null);
    }

    public static String validateAbsoluteURI(String uri, String baseUri) {
        try {
            URI uriObject = new URI(uri);
            if (uriObject.isAbsolute()) {
                return null;
            }
            else if (baseUri == null) {
                return "Relative URIs are not allowed.";
            }
            uriObject = new URI(String.format("%s%s", baseUri, uri));
        }
        catch (URISyntaxException ex) {
            return ex.getMessage();
        }
        return null;
    }

    private String validatePrefixedURI(String uri) {
        if (!uri.contains(":"))
            return "URI must contain a prefix!";

        String expandedUri = model.expandPrefix(uri);

        try {
            @SuppressWarnings("unused")
            URI uriObject = new URI(expandedUri);
        }
        catch (URISyntaxException ex) {
            // logger.debug("URI is NOT valid");
            return ex.getMessage();
        }

        String qname = model.qnameFor(expandedUri);
        if (qname != null) {
            // logger.debug("URI is valid");
            return null;
        }

        String extractedPrefix = ResourceURIUtil.extractPrefix(uri);
        String mappedPrefixNs = model.getNsPrefixURI(extractedPrefix);

        if (mappedPrefixNs == null || mappedPrefixNs.length() == 0) {
            if (extractedPrefix.equals("")) {
                // logger.debug("URI is NOT valid");
                return String
                        .format("Cannot construct a valid qname for '%s', probably because no default namespace has been defined!",
                                expandedUri);
            }
            // logger.debug("URI is NOT valid");
            return String
                    .format("Cannot construct a valid qname for '%s', probably because the namespace mapping for prefix '%s' is undefined!",
                            expandedUri, extractedPrefix);
        }
        // logger.debug("URI is valid");
        return null;
    }

    @Override
    public String isValid(String uri) {
        if (uri.length() == 0) {
            logger.debug("URI is NOT valid");
            return "The URI cannot be empty!";
        }
        if (uri.startsWith("<") && uri.endsWith(">")) {
            // angular brackets signal an absolute uri
            return validateAbsoluteURI(uri.substring(1, uri.length() - 1), baseUri);
        }
        if (!uri.equals(model.expandPrefix(uri))) {
            // uri was already prefixed
            return validatePrefixedURI(uri);
        }
        String prefixed = model.qnameFor(uri);
        if (prefixed == null) {
            // uri couldn't be prefixed
            return validateAbsoluteURI(uri, baseUri);
        }
        // uri has been prefixed
        return validatePrefixedURI(uri);
    }

}
