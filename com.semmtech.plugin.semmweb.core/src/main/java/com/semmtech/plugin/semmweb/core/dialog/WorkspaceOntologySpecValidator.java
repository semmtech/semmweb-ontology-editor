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
public class WorkspaceOntologySpecValidator extends OntologySpecValidator implements
        IWorkspaceOntologySpecValidator {

    protected boolean requiresExternalAltUrl;
    protected boolean requiresWorkspaceAltUrl;

    /** Default constructor that marks only the publicUri as required. */
    public WorkspaceOntologySpecValidator() {
        this(true, false, false, false);
    }

    public WorkspaceOntologySpecValidator(boolean requiresPublicUri, boolean requiresPrefix,
            boolean requiresExternalAltUrl, boolean requiresWorkspaceAltUrl) {
        super(requiresPublicUri, requiresPrefix, requiresExternalAltUrl || requiresWorkspaceAltUrl);
        this.requiresExternalAltUrl = requiresExternalAltUrl;
        this.requiresWorkspaceAltUrl = requiresWorkspaceAltUrl;
    }

    @Override
    public String isValidAltUrl(String url) {
        if (Strings.isNullOrEmpty(url)) {
            if (requiresExternalAltUrl) {
                return "The alternative URL still needs to be specified.";
            }
            return null;
        }

        String extUrlMessage = isValidExternalAltUrl(url);
        String wsUrlMessage = isValidWorkspaceAltUrl(url);
        if (extUrlMessage == null || wsUrlMessage == null) {
            return null;
        }
        return extUrlMessage;
    }

    @Override
    public String isValidExternalAltUrl(String url) {
        if (Strings.isNullOrEmpty(url)) {
            if (requiresExternalAltUrl) {
                return "The alternative URL still needs to be specified.";
            }
            return null;
        }

        // Further validation on the input is currently not required as the
        // WorkspaceOntologySpecDialog already ensures the user enters correct
        // urls.
        return null;
    }

    @Override
    public String isValidWorkspaceAltUrl(String url) {
        if (Strings.isNullOrEmpty(url)) {
            if (requiresWorkspaceAltUrl) {
                return "The workspace model still needs to be specified.";
            }
            return null;
        }

        // Further validation on the input is currently not required as the
        // WorkspaceOntologySpecDialog already ensures the user enters correct
        // urls.
        return null;
    }
}
