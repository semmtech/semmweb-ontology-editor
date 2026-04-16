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

package com.semmtech.plugin.semmweb.core.handlers;


import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import com.google.common.base.Strings;
import com.hp.hpl.jena.rdf.model.Resource;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.actions.ImportActions;
import com.semmtech.plugin.semmweb.core.model.IModelProvider;


public class AddImportHandler extends AbstractHandler {
    public static final String ID = "com.semmtech.plugin.semmweb.core.commands.addImport";

    public static final String PARAMETER_ONTOLOGY_URI = "ontologyURI";

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        IModelProvider modelProvider = CorePlugin.getDefault().getActiveModelProvider();
        if (modelProvider == null) {
            return null;
        }

        Resource sourceOntology = null;
        String sourceOntologyURI = getOntologyURI(event);
        if (!Strings.isNullOrEmpty(sourceOntologyURI)) {
            sourceOntology = modelProvider.getOntModel().createResource(sourceOntologyURI);
        }
        ImportActions.addImport(modelProvider, sourceOntology, null);
        return null;
    }

    private String getOntologyURI(ExecutionEvent event) {
        return event.getParameter(PARAMETER_ONTOLOGY_URI);
    }
}
