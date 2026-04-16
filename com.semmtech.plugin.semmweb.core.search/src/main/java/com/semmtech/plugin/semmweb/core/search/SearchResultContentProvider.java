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


import java.util.Map;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.google.common.collect.Maps;
import com.hp.hpl.jena.rdf.model.Resource;
import com.semmtech.plugin.semmweb.core.search.elements.LiteralElement;
import com.semmtech.plugin.semmweb.core.search.elements.ResourceElement;


public abstract class SearchResultContentProvider implements IStructuredContentProvider {

    protected OntologySearchResult result;
    protected Map<OntologyResourceMatch, LiteralElement> matchToElements;
    protected Map<Resource, ResourceElement> resourceToElement;
    private final Viewer viewer;

    public SearchResultContentProvider(Viewer viewer) {
        this.viewer = viewer;
    }

    public synchronized void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        if (newInput instanceof OntologySearchResult) {
            result = (OntologySearchResult) newInput;
            internalInitialize(result);
        }
    }

    public void dispose() {
    }

    private synchronized void internalInitialize(OntologySearchResult result) {
        matchToElements = Maps.newHashMap();
        resourceToElement = Maps.newHashMap();
        initialize(result);
    }

    protected abstract void initialize(OntologySearchResult result);

    public abstract void elementsChanged(Object[] updatedElements);

    public synchronized void clear() {
        internalInitialize(result);
        viewer.refresh();
    }

    protected synchronized LiteralElement getLiteralFromResult(OntologyResourceMatch match) {
        LiteralElement literalElement = matchToElements.get(match);

        if (!matchToElements.containsKey(match)) {
            ResourceElement resourceElement = getResourceFromResult(match);

            if (match instanceof OntologyStatementMatch) {
                OntologyStatementMatch stamentMatch = (OntologyStatementMatch) match;
                literalElement = new LiteralElement(resourceElement, stamentMatch.getProperty(),
                        stamentMatch.getLiteral());
                literalElement.setPropertyLabel(stamentMatch.getPropertyID());
                matchToElements.put(match, literalElement);
            }
        }
        return literalElement;
    }

    protected synchronized ResourceElement getResourceFromResult(OntologyResourceMatch match) {
        Resource resource = match.getResource();
        ResourceElement resourceElement = resourceToElement.get(resource);

        if (resourceElement == null) {
            resourceElement = new ResourceElement(match.getFile(), resource);
            resourceElement.setLabel(match.getResourceID());
            resourceToElement.put(resource, resourceElement);
        }
        return resourceElement;
    }
}
