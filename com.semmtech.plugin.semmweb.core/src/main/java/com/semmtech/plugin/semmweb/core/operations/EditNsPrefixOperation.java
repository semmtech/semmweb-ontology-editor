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


import java.util.List;

import com.google.common.collect.Lists;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.semmtech.plugin.semmweb.core.model.events.NamespacePrefixChangedEvent;


/**
 * 
 * @author Sander Stolk
 */
public class EditNsPrefixOperation extends ModelOperation {
    private final static String description = "Edit prefix";
    private String uri;
    private String oldPrefix;
    private String newPrefix;
    private String oldUri; // prefix could have moved from another uri

    public EditNsPrefixOperation(String uri, String oldPrefix, String newPrefix) {
        super(description);
        this.uri = uri;
        this.oldPrefix = oldPrefix;
        this.newPrefix = newPrefix;
    }

    @Override
    public boolean execute(OntModel model) {
        Model baseModel = model.getBaseModel();
        if (uri != null && newPrefix != null) {
            if (oldUri == null) {
                oldUri = baseModel.getNsPrefixURI(newPrefix);
            }

            baseModel.setNsPrefix(newPrefix, uri);
            baseModel.removeNsPrefix(oldPrefix);

            if (oldPrefix == null) {
                model.notifyEvent(new NamespacePrefixChangedEvent(model, newPrefix, description));
            }
            else {
                List<String> prefixes = Lists.newArrayList(oldPrefix, newPrefix);
                model.notifyEvent(new NamespacePrefixChangedEvent(model, prefixes, description));
            }
        }
        return true;
    }

    @Override
    public boolean undo(OntModel model) {
        Model baseModel = model.getBaseModel();
        if (uri != null && newPrefix != null) {
            if (oldPrefix == null) {
                baseModel.removeNsPrefix(uri);
            }
            else {
                baseModel.setNsPrefix(oldPrefix, uri);
                baseModel.removeNsPrefix(newPrefix);
            }

            if (oldUri != null) {
                baseModel.setNsPrefix(newPrefix, oldUri);
            }

            if (oldPrefix == null) {
                model.notifyEvent(new NamespacePrefixChangedEvent(model, newPrefix, description));
            }
            else {
                List<String> prefixes = Lists.newArrayList(oldPrefix, newPrefix);
                model.notifyEvent(new NamespacePrefixChangedEvent(model, prefixes, description));
            }
        }
        return true;
    }

}
