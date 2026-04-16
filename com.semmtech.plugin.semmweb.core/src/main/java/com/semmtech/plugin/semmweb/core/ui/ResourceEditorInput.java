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

package com.semmtech.plugin.semmweb.core.ui;


import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IPersistableElement;

import com.hp.hpl.jena.rdf.model.Resource;


public class ResourceEditorInput implements IResourceEditorInput {
    private Resource resource;
    private String modelUri;

    public ResourceEditorInput(Resource resource, String modelUri) {
        this.resource = resource;
        this.modelUri = modelUri;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Object getAdapter(Class adapter) {
        return null;
    }

    @Override
    public boolean exists() {
        return false;
    }

    @Override
    public ImageDescriptor getImageDescriptor() {
        return null;
    }

    @Override
    public String getName() {
        if (!resource.isAnon()) {
            return resource.getModel().shortForm(resource.getURI());
        }
        return "<" + resource.getId().toString() + ">";
    }

    @Override
    public IPersistableElement getPersistable() {
        return null;
    }

    @Override
    public String getToolTipText() {
        if (!resource.isAnon()) {
            return resource.getURI();
        }
        return "<" + resource.getId().toString() + ">";
    }

    @Override
    public Resource getResource() {
        return resource;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        else if (!(obj instanceof ResourceEditorInput)) {
            return false;
        }
        else {
            return equals((ResourceEditorInput) obj);
        }
    }

    public boolean equals(ResourceEditorInput other) {
        if (other.getResource().isAnon()) {
            if (getResource().isAnon()) {
                return other.getResource().getId().equals(getResource().getId())
                        && other.getModelURI().equals(getModelURI());
            }
            return false;
        }
        else if (other.getResource().getURI() == null) {
            return false;
        }
        else {
            return other.getResource().getURI().equals(getResource().getURI())
                    && other.getModelURI().equals(getModelURI());
        }
    }

    @Override
    public String getURI() {
        return resource.getURI();
    }

    @Override
    public String getModelURI() {
        return modelUri;
    }
}
