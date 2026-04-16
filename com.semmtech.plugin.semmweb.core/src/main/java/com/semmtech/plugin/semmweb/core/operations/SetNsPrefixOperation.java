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
public class SetNsPrefixOperation extends ModelOperation {
    private final static String description = "Set prefix";
    private String prefix;
    private String oldUri;
    private String newUri;

    public SetNsPrefixOperation(String prefix, String uri) {
        super(description);
        this.prefix = prefix;
        this.newUri = uri;
    }

    @Override
    public boolean execute(OntModel model) {
        Model baseModel = model.getBaseModel();
        if (prefix != null) {
            if (oldUri == null) {
                oldUri = baseModel.getNsPrefixURI(prefix);
            }
            if (newUri != null) {
                baseModel.setNsPrefix(prefix, newUri);
                model.notifyEvent(new NamespacePrefixChangedEvent(model, prefix, description));
            }
        }
        return true;
    }

    @Override
    public boolean undo(OntModel model) {
        Model baseModel = model.getBaseModel();
        if ((prefix != null) && (newUri != null)) {
            if (oldUri != null) {
                baseModel.setNsPrefix(prefix, oldUri);
            }
            else {
                baseModel.removeNsPrefix(prefix);
            }
            model.notifyEvent(new NamespacePrefixChangedEvent(model, prefix, description));
        }
        return true;
    }
}
