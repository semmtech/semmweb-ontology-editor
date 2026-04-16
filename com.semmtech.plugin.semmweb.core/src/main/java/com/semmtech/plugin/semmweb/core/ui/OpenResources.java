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

import com.google.common.collect.Lists;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.Resource;


/**
 * 
 * @author Sander Stolk
 */
public class OpenResources {
    // Sorted in the order they were opened, from first opened to last opened
    // resource.
    private final List<OntResource> openedResources;
    // Sorted in the order they were viewed, with the last viewed resource last
    // in the list.
    private final List<OntResource> viewedResources;

    public OpenResources() {
        openedResources = Lists.newArrayList();
        viewedResources = Lists.newArrayList();
    }

    public void clear() {
        openedResources.clear();
        viewedResources.clear();
    }

    public void opened(OntResource resource) {
        viewed(resource);
    }

    public void viewed(OntResource resource) {
        if (!openedResources.contains(resource)) {
            openedResources.add(resource);
        }
        int curIndex = viewedResources.indexOf(resource);
        if (curIndex >= 0) {
            viewedResources.remove(curIndex);
        }
        viewedResources.add(resource);
    }

    public void closed(OntResource resource) {
        openedResources.remove(resource);
        viewedResources.remove(resource);
    }

    public List<OntResource> getOpenResources(IOpenResourcesProvider.Attribute on,
            IOpenResourcesProvider.Order order) {
        List<OntResource> result = Lists.newArrayList();
        if (on == IOpenResourcesProvider.Attribute.OPENED) {
            result.addAll(openedResources);
            if (order == IOpenResourcesProvider.Order.DESC) {
                result = Lists.reverse(result);
            }
        }
        else if (on == IOpenResourcesProvider.Attribute.VIEWED) {
            result.addAll(viewedResources);
            if (order == IOpenResourcesProvider.Order.DESC) {
                result = Lists.reverse(result);
            }
        }
        return result;
    }

    public OntResource getLastViewed() {
        if (viewedResources.isEmpty()) {
            return null;
        }
        return viewedResources.get(viewedResources.size() - 1);
    }

    public OntResource getLastOpened() {
        if (openedResources.isEmpty()) {
            return null;
        }
        return openedResources.get(openedResources.size() - 1);
    }

    public boolean contains(Resource resource) {
        if (resource == null) {
            return false;
        }
        return openedResources.contains(resource);
    }
}
