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

package com.semmtech.plugin.semmweb.core.search;


import org.eclipse.core.resources.IFile;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;


public class OntologyStatementMatch extends OntologyResourceMatch {

    private final Property property;
    private String propertyId;
    private final Literal literal;

    public OntologyStatementMatch(IFile file, Resource resource, Property predicate, Literal literal) {
        super(file, resource);
        this.property = predicate;
        this.literal = literal;
    }

    public Property getProperty() {
        return property;
    }

    public Literal getLiteral() {
        return literal;
    }

    public String getPropertyID() {
        if (propertyId == null)
            propertyId = String.format("<%s>", property.getURI());
        return propertyId;
    }

    public void setPropertyID(String id) {
        this.propertyId = id;
    }

    @Override
    public String toString() {
        return String.format("%s -> ( <%s> <%s> \"%s\" )", getFile().getName(), getResource(),
                property, literal.getString());
    }
}
