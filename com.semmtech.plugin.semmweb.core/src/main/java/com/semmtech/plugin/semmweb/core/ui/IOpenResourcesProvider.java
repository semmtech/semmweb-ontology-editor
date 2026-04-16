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


import java.util.List;

import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.Resource;
import com.semmtech.plugin.semmweb.core.model.OpenResourceEventListener;


/**
 * 
 * @author Sander Stolk
 */
public interface IOpenResourcesProvider {
    public enum Attribute {
        OPENED, VIEWED
    }

    public enum Order {
        ASC, DESC
    }

    public OntResource getActiveOpenResource();

    public boolean containsOpenResource(Resource resource);

    public OntResource getLastOpenedResource();

    public OntResource getLastViewedResource();

    public List<OntResource> getOpenResources();

    public List<OntResource> getOpenResources(Attribute on, Order order);

    public void addOpenResourceEventListener(OpenResourceEventListener listener);

    public void removeOpenResourceEventListener(OpenResourceEventListener listener);
}
