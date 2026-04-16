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

package com.semmtech.plugin.semmweb.core.widgets.trees;


import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;


/**
 * 
 * @author Sander Stolk
 */
public class PropertyTreeData extends ResourceTreeData implements Property {
    private final Property property;

    public PropertyTreeData(Property property) {
        super(property);
        this.property = property;
    }

    @Override
    public boolean isProperty() {
        return true;
    }

    @Override
    public int getOrdinal() {
        return property.getOrdinal();
    }

    @Override
    public String getNameSpace() {
        return property.getNameSpace();
    }

    @Override
    public Property inModel(Model m) {
        return property.inModel(m);
    }

    @Override
    public String getLocalName() {
        return property.getLocalName();
    }

}
