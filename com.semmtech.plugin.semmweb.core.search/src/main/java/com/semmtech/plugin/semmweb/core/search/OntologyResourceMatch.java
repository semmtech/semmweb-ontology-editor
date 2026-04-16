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

import com.hp.hpl.jena.rdf.model.Resource;


public class OntologyResourceMatch {
    private final IFile file;
    private final Resource resource;
    private String resourceId;

    public OntologyResourceMatch(IFile file, Resource resource) {
        this.file = file;
        this.resource = resource;
    }

    public IFile getFile() {
        return file;
    }

    public Resource getResource() {
        return resource;
    }

    public String getResourceID() {
        if (resourceId == null) {
            if (resource.isAnon())
                resourceId = String.format("<%s>", resource.getId().toString());
            else
                resourceId = String.format("<%s>", resource.getURI().toString());
        }
        return resourceId;
    }

    public void setResourceID(String id) {
        this.resourceId = id;
    }

    @Override
    public String toString() {
        return String.format("%s -> <%s>", file.getName(), resource);
    }
}
