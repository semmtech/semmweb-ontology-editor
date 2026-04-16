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

package com.semmtech.plugin.semmweb.core.search.elements;


import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Property;


public class LiteralElement extends ResourceElement {
    private ResourceElement parent;
    private final Literal literal;
    private final Property property;
    private String propertyLabel;
    private String literalLabel;

    public LiteralElement(ResourceElement resourceElement, Property property, Literal literal) {
        super(resourceElement.getFile(), resourceElement.getResource());
        this.parent = resourceElement;
        this.property = property;
        this.literal = literal;
        resourceElement.addLiteralElement(this);
    }

    public ResourceElement getResourceElement() {
        return parent;
    }

    public Property getProperty() {
        return property;
    }

    public Literal getLiteral() {
        return literal;
    }

    public String getPropertyLabel() {
        if (propertyLabel == null) {
            return String.format("<%s>", property.getURI());
        }
        return propertyLabel;
    }

    public void setPropertyLabel(String propertyLabel) {
        this.propertyLabel = propertyLabel;
    }

    public String getLiteralLabel() {
        if (literalLabel == null) {
            return literal.getString();
        }
        return literalLabel;
    }

    public void setLiteralLabel(String literalLabel) {
        this.literalLabel = literalLabel;
    }

    @Override
    public int hashCode() {
        return String.format("%s;%s;%s;%s;", file.getName(), getResource().toString(),
                getProperty().toString(), literal.toString()).hashCode();
    }

    @Override
    public String getLabel() {
        if (label == null) {
            label = String.format("%s: %s", getPropertyLabel(), getLiteralLabel());
        }
        return super.getLabel();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof LiteralElement)) {
            return false;
        }
        return equals((LiteralElement) obj);
    }

    public boolean equals(LiteralElement other) {
        if (!super.equals(other)) {
            return false;
        }
        return property.equals(other.getProperty()) && literal.equals(other.getLiteral());
    }
}