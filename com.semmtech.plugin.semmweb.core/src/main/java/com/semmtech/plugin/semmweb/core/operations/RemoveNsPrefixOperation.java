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

package com.semmtech.plugin.semmweb.core.operations;


import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.semmtech.plugin.semmweb.core.model.events.NamespacePrefixChangedEvent;


/**
 * 
 * @author Sander Stolk
 */
public class RemoveNsPrefixOperation extends ModelOperation {
    private final static String description = "Remove prefix";
    private String prefix;
    private String uri;

    public RemoveNsPrefixOperation(String prefix) {
        super(description);
        this.prefix = prefix;
    }

    @Override
    public boolean execute(OntModel model) {
        Model baseModel = model.getBaseModel();
        if (prefix != null) {
            if (uri == null) {
                uri = baseModel.getNsPrefixURI(prefix);
            }
            if (uri != null) {
                baseModel.removeNsPrefix(prefix);
                model.notifyEvent(new NamespacePrefixChangedEvent(model, prefix, description));
            }
        }
        return true;
    }

    @Override
    public boolean undo(OntModel model) {
        Model baseModel = model.getBaseModel();
        if ((prefix != null) && (uri != null)) {
            baseModel.setNsPrefix(prefix, uri);
            // if (!suppressNotify) {
            // Model model = provider.getOntModel();
            model.notifyEvent(new NamespacePrefixChangedEvent(model, prefix, "Undo of: "
                    + description));
            // }
        }
        return true;
    }
}
