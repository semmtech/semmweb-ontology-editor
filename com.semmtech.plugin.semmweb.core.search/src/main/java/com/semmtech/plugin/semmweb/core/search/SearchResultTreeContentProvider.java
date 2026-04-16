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
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.ITreeContentProvider;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.semmtech.plugin.semmweb.core.search.elements.LiteralElement;
import com.semmtech.plugin.semmweb.core.search.elements.ResourceElement;


public class SearchResultTreeContentProvider extends SearchResultContentProvider implements
        ITreeContentProvider {

    private final AbstractTreeViewer treeViewer;
    private Multimap<Object, Object> elements;

    public SearchResultTreeContentProvider(AbstractTreeViewer viewer) {
        super(viewer);
        treeViewer = viewer;
    }

    public Object[] getElements(Object inputElement) {
        return getChildren(inputElement);
    }

    protected synchronized void initialize(OntologySearchResult result) {
        elements = LinkedHashMultimap.create();

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
                    insert(element, false);
                }
            }
        }
    }

    private void insert(Object child, boolean refreshViewer) {
        Object parent = getParent(child);
        while (parent != null) {
            if (insertChild(parent, child)) {
                if (refreshViewer) {
                    treeViewer.add(parent, child);
                }
            }
            else {
                if (refreshViewer) {
                    treeViewer.refresh(parent);
                }
                return;
            }
            child = parent;
            parent = getParent(parent);
        }
        if (insertChild(result, child)) {
            if (refreshViewer) {
                treeViewer.add(result, child);
            }
        }
    }

    /**
     * Adds the child to the parent.
     * 
     * @param parent
     *            the parent
     * @param child
     *            the child
     * @return <code>true</code> if this set did not already contain the
     *         specified element
     */
    private boolean insertChild(Object parent, Object child) {
        return elements.put(parent, child);
    }

    private void remove(Object element, boolean refreshViewer) {
        // precondition here: fResult.getMatchCount(child) <= 0

        if (hasChildren(element)) {
            if (refreshViewer) {
                treeViewer.refresh(element);
            }
        }
        else {
            if (!hasMatches(element)) {
                elements.removeAll(element);
                Object parent = getParent(element);
                if (parent != null) {
                    removeFromSiblings(element, parent);
                    remove(parent, refreshViewer);
                }
                else {
                    removeFromSiblings(element, result);
                    if (refreshViewer) {
                        treeViewer.refresh();
                    }
                }
            }
            else {
                if (refreshViewer) {
                    treeViewer.refresh(element);
                }
            }
        }
    }

    private boolean hasMatches(Object element) {
        return result.getMatchCount(element) > 0;
    }

    private void removeFromSiblings(Object element, Object parent) {
        elements.remove(parent, element);
    }

    public Object[] getChildren(Object parentElement) {
        return elements.get(parentElement).toArray();
    }

    public boolean hasChildren(Object element) {
        return getChildren(element).length > 0;
    }

    public synchronized void elementsChanged(Object[] updatedElements) {
        for (Object object : updatedElements) {
            ResourceElement element = null;

            if (object instanceof OntologyStatementMatch) {
                element = getLiteralFromResult((OntologyResourceMatch) object);
            }
            else if (object instanceof OntologyResourceMatch) {
                element = getResourceFromResult((OntologyResourceMatch) object);
            }

            if (element != null) {
                if (result.getMatchCount(object) > 0) {
                    insert(element, true);
                }
                else {
                    matchToElements.remove(object);
                    resourceToElement.remove(element.getResource());
                    remove(element, true);
                }
            }
        }

    }

    public Object getParent(Object element) {
        if (element instanceof IProject) {
            return null;
        }
        else if (element instanceof IFile) {
            return ((IFile) element).getProject();
        }
        else if (element instanceof LiteralElement) {
            return ((LiteralElement) element).getResourceElement();
        }
        else if (element instanceof ResourceElement) {
            return ((ResourceElement) element).getFile();
        }
        return null;
    }

}
