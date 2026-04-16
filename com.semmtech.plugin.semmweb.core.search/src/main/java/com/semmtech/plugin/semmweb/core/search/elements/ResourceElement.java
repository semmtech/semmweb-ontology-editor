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


import java.util.Set;

import org.eclipse.core.resources.IResource;

import com.google.common.collect.Sets;
import com.hp.hpl.jena.rdf.model.Resource;


public class ResourceElement {
    protected final IResource file;
    protected final Resource resource;
    protected String label;
    protected Set<LiteralElement> literalElements;

    public ResourceElement(IResource file, Resource resource) {
        this.file = file;
        this.resource = resource;
        this.literalElements = Sets.newHashSet();
    }

    public Resource getResource() {
        return resource;
    }

    public IResource getFile() {
        return file;
    }

    public void addLiteralElement(LiteralElement e) {
        literalElements.add(e);
    }

    public Set<LiteralElement> getLiteralElements() {
        return literalElements;
    }

    public boolean removeLiteralElement(LiteralElement element) {
        return literalElements.remove(element);
    }

    public String getLabel() {
        if (label == null) {
            if (resource.isAnon()) {
                label = String.format("<%s>", resource.getId().toString());
            }
            else {
                label = String.format("<%s>", resource.getURI());
            }
        }
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public int hashCode() {
        return String.format("%s;%s;", file.getName(), resource.toString()).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof ResourceElement)) {
            return false;
        }
        return equals((ResourceElement) obj);
    }

    public boolean equals(ResourceElement other) {
        return resource.equals(other.getResource());
    }
}