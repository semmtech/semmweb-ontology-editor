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


import java.util.Set;

import org.eclipse.jface.viewers.AbstractTableViewer;

import com.google.common.collect.Sets;
import com.semmtech.plugin.semmweb.core.search.elements.ResourceElement;


public class SearchResultTableContentProvider extends SearchResultContentProvider {

    private final Object[] EMPTY_ARR = new Object[0];

    private final AbstractTableViewer tableViewer;
    private Set<ResourceElement> elements;

    public SearchResultTableContentProvider(AbstractTableViewer tableViewer) {
        super(tableViewer);
        this.tableViewer = tableViewer;
    }

    public Object[] getElements(Object inputElement) {
        if (inputElement instanceof OntologySearchResult) {
            return elements.toArray();
        }
        return EMPTY_ARR;
    }

    protected synchronized void initialize(OntologySearchResult result) {
        elements = Sets.newHashSet();

        if (result != null) {
            for (Object object : result.getElements()) {
                ResourceElement element = null;

                if (object instanceof OntologyStatementMatch) {
                    element = getLiteralFromResult((OntologyResourceMatch) object);
                }
                else if (object instanceof OntologyResourceMatch) {
                    element = getResourceFromResult((OntologyResourceMatch) object);
                }

                if (element != null) {
                    elements.add(element);
                }
            }
        }
    }

    public synchronized void elementsChanged(Object[] updatedElements) {
        for (Object object : updatedElements) {
            if (object instanceof OntologyResourceMatch) {
                OntologyResourceMatch match = (OntologyResourceMatch) object;
                ResourceElement element = getResourceFromResult(match);

                if (result.getMatchCount(object) > 0) {
                    if (tableViewer.testFindItem(element) != null) {
                        tableViewer.update(element, null);
                    }
                    else {
                        tableViewer.add(element);
                    }
                }
                else {
                    tableViewer.remove(element);
                }
            }
        }
    }
}
