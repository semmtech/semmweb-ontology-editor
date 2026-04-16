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


import com.google.common.base.Strings;


/**
 * 
 * @author Sander Stolk
 */
public class OntologySpecValidator implements IOntologySpecValidator {

    protected boolean requiresPublicUri;
    protected boolean requiresPrefix;
    protected boolean requiresAltUrl;

    /** Default constructor that marks only the publicUri as required. */
    public OntologySpecValidator() {
        this(true, false, false);
    }

    public OntologySpecValidator(boolean requiresPublicUri, boolean requiresPrefix,
            boolean requiresAltUrl) {
        this.requiresPublicUri = requiresPublicUri;
        this.requiresPrefix = requiresPrefix;
        this.requiresAltUrl = requiresAltUrl;
    }

    @Override
    public String isValidPublicUri(String uri) {
        if (Strings.isNullOrEmpty(uri)) {
            if (requiresPublicUri) {
                return "The public URI still needs to be specified.";
            }
            return null;
        }

        return URIValidator.validateAbsoluteURI(uri);
    }

    @Override
    public String isValidPrefix(String prefix) {
        if (Strings.isNullOrEmpty(prefix)) {
            if (requiresPrefix) {
                return "The prefix still needs to be specified.";
            }
            return null;
        }

        return PrefixInputValidator.isValidPrefix(prefix);
    }

    @Override
    public String isValidAltUrl(String url) {
        if (Strings.isNullOrEmpty(url)) {
            if (requiresAltUrl) {
                return "The alternate url still needs to be specified.";
            }
            return null;
        }

        // TODO Validate alternate location, which can be a url or a file (by
        // means of regex?)
        return null;
    }
}
