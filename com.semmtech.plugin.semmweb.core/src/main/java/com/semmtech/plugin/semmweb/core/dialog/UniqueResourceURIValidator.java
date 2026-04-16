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

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;


/**
 * Validates whether the given URI is already known within the model as a
 * Resource.
 * 
 * @author Mike Henrichs
 * 
 */
public class UniqueResourceURIValidator implements IInputValidator {

    private final Model model;
    @SuppressWarnings("unused")
    private final String baseUri;

    public UniqueResourceURIValidator(Model model, String baseUri) {
        this.model = model;
        this.baseUri = baseUri;
    }

    /**
     * The given uri can be either a fully expanded uri (like
     * 'http://www.w3.org/2002/07/owl#Thing') or the short form variant (like
     * 'owl:Thing'). If the model already contains a resource with the given
     * uri, an errorMessage will be returned, otherwise null will be returned.
     */
    @Override
    public String isValid(String uri) {
        String expandedUri = null;
        if (uri.startsWith("<") && uri.endsWith(">")) {
            // angular brackets signal an absolute uri
            expandedUri = uri.substring(1, uri.length() - 1);
        }
        else {
            expandedUri = model.expandPrefix(uri);
        }

        Resource resource = model.createResource(expandedUri);
        boolean exists = model.containsResource(resource);
        if (exists) {
            return String.format("Resource with URI '%s' already exists!", uri);
        }
        return null;
    }

}
