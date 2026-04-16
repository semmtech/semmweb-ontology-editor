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

package com.semmtech.plugin.semmweb.core.model.events;


import java.util.List;

import com.google.common.collect.Lists;
import com.hp.hpl.jena.rdf.model.Model;


/**
 * 
 * @author Sander Stolk
 */
public class NamespacePrefixChangedEvent implements IModelEvent {
    private final Model model;
    private final String title;
    private final List<String> changedPrefixes;

    public NamespacePrefixChangedEvent(Model model, String prefix, String title) {
        this(model, Lists.newArrayList(prefix), title);
    }

    public NamespacePrefixChangedEvent(Model model, List<String> prefixes, String title) {
        this.model = model;
        this.title = title;
        this.changedPrefixes = prefixes;
    }

    @Override
    public Model getModel() {
        return model;
    }

    @Override
    public String getTitle() {
        return title;
    }

    public List<String> getChangedPrefixes() {
        return changedPrefixes;
    }
}
