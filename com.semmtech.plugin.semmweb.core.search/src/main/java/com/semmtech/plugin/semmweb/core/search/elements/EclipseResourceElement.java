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


import org.eclipse.core.resources.IResource;

import com.hp.hpl.jena.rdf.model.Resource;


public class EclipseResourceElement {
    private final IResource eclipseResource;
    protected String label;

    public EclipseResourceElement(IResource eclipseResource) {
        this.eclipseResource = eclipseResource;
    }

    public IResource getFile() {
        return eclipseResource;
    }

    public ResourceElement createChild(Resource resource) {
        return new ResourceElement(getFile(), resource);
    }

    public String getLabel() {
        if (label == null) {
            label = eclipseResource.getName();
        }
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public int hashCode() {
        return String.format("%s;", eclipseResource.getName()).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof EclipseResourceElement)) {
            return false;
        }
        return equals((EclipseResourceElement) obj);
    }

    public boolean equals(EclipseResourceElement other) {
        return eclipseResource.equals(other.getFile());
    }
}