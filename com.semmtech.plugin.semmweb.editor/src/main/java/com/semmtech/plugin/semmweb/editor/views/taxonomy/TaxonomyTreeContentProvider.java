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

package com.semmtech.plugin.semmweb.editor.views.taxonomy;


import java.util.Collection;
import java.util.List;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.TreeItem;

import com.google.common.collect.Lists;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Resource;
import com.semmtech.plugin.semmweb.core.widgets.trees.OntClassTreeData;
import com.semmtech.plugin.semmweb.core.widgets.trees.TreeData;
import com.semmtech.semantics.util.JenaUtil;
import com.semmtech.ui.plugin.viewers.LazyTreeContentProvider;
import com.semmtech.ui.plugin.widgets.Widgets;


public final class TaxonomyTreeContentProvider extends LazyTreeContentProvider {
    private TreeViewer viewer;
    private OntModel currentModel;
    private TaxonomyViewModel viewModel;
    private final List<Resource> roots;

    public TaxonomyTreeContentProvider() {
        roots = Lists.newArrayList();
    }

    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        this.viewer = null;
        if (viewer instanceof TreeViewer) {
            this.viewer = (TreeViewer) viewer;
        }
        this.currentModel = null;
        if (newInput instanceof OntModel) {
            this.currentModel = (OntModel) newInput;
        }
    }

    public void setRoots(List<Resource> resources) {
        roots.clear();
        roots.addAll(resources);
    }

    public void setRoot(Resource resource) {
        setRoots(Lists.newArrayList(resource));
    }

    public void setViewModel(TaxonomyViewModel viewModel) {
        this.viewModel = viewModel;
    }

    public TaxonomyViewModel getViewModel() {
        return viewModel;
    }

    @Override
    public void updateElement(final Object parent, final int index) {
        if ((roots == null) || (currentModel == null) || (viewModel == null)) {
            return;
        }

        if (parent instanceof OntModel) {
            Resource resource = roots.get(index);
            if (resource == null) {
                return;
            }
            OntClass rootClass = JenaUtil.asOntClass(resource, currentModel);
            if (rootClass == null) {
                return;
            }

            OntClassTreeData rootElement = new OntClassTreeData(rootClass);
            viewer.replace(parent, index, rootElement);
            viewer.setChildCount(rootElement, viewModel.getChildClassCount(rootClass));
        }
        else if (parent instanceof OntClassTreeData) {
            OntClassTreeData parentElement = (OntClassTreeData) parent;
            OntClass child = viewModel.getChildClass(parentElement, index);
            OntClassTreeData childElement = new OntClassTreeData(child);
            childElement.setParent(parentElement);
            viewer.replace(parent, index, childElement);
            viewer.setChildCount(childElement, viewModel.getChildClassCount(child));
        }
    }

    @Override
    public Object getParent(Object element) {
        if (element instanceof OntClassTreeData) {
            OntClassTreeData resourceItem = (OntClassTreeData) element;
            return resourceItem.getParent();
        }
        return null;
    }

    // FIXME: My suggestion is to move this to a LazyResourceTreeContentProvider
    // class, as providing the correct order is part of the job of a
    // LazyTreeContentProvider. (See also InstancesView, PropertiesView.)
    /**
     * Calculates the index in an array of TreeItems at which to insert the
     * given element (adhering to ascending alphabetical order of the text label
     * provided by the viewModel).
     */
    public int getInsertionIndex(Object element, TreeItem[] treeItems) {
        if ((element == null) || (viewModel == null) || !(element instanceof Resource)) {
            return -1;
        }
        if ((treeItems == null) || (treeItems.length == 0)) {
            return 0;
        }

        String elementText = viewModel.getText((Resource) element);
        if (elementText != null) {

            for (int i = 0; i < treeItems.length; i++) {
                Object treeItemData = treeItems[i].getData();
                if (treeItemData == null) {
                    return i;
                }
                if (treeItemData instanceof Resource) {
                    String treeItemText = viewModel.getText((Resource) treeItemData);
                    if (treeItemText != null) {
                        int compare = elementText.compareToIgnoreCase(treeItemText);
                        if (compare < 0) {
                            return i;
                        }
                        if (compare == 0) {
                            return (treeItemData.equals(element)) ? -1 : i;
                        }
                    }
                }
            }

        }

        return treeItems.length;
    }

    public void addRoot(OntClass clazz) {
        if (Widgets.isNullOrDisposedViewer(viewer) || (clazz == null)) {
            return;
        }

        OntClassTreeData element = new OntClassTreeData(clazz);
        int insertionIndex = getInsertionIndex(element, viewer.getTree().getItems());
        if (insertionIndex >= 0) {
            viewer.insert(currentModel, element, insertionIndex);
            viewer.setChildCount(element, viewModel.getChildClassCount(clazz));
            roots.add(insertionIndex, clazz);
        }
    }

    public void removeRoot(OntClass clazz) {
        if (Widgets.isNullOrDisposedViewer(viewer) || (clazz == null)) {
            return;
        }

        viewer.remove(currentModel, new Object[] { new OntClassTreeData(clazz) });
        roots.remove(clazz);
    }

    public void addChild(OntClass child, OntClass parent) {
        if (child == null) {
            return;
        }

        for (TreeItem treeItem : findTreeItems(parent)) {
            if (!hasCachedChildren(treeItem)) {
                treeItem.setItemCount(viewModel.getChildClassCount(parent));
            }
            else {
                OntClassTreeData childData = new OntClassTreeData(child);
                childData.setParent((TreeData) treeItem.getData());
                int insertionIndex = getInsertionIndex(childData, treeItem.getItems());
                if (insertionIndex >= 0) {
                    viewer.insert(treeItem.getData(), childData, insertionIndex);
                    viewer.setChildCount(childData, viewModel.getChildClassCount(child));
                }
            }
        }
    }

    // FIXME: My suggestion is to move this to a ResourceTreeViewer class
    private boolean hasCachedChildren(TreeItem treeItem) {
        if (treeItem.getItemCount() == 0) {
            return false;
        }
        if ((treeItem.getItem(0) == null) || (treeItem.getItem(0).getData() == null)) {
            return false;
        }
        return true;
    }

    public void removeChild(OntClass child, OntClass parent) {
        if (child == null) {
            return;
        }

        for (TreeItem treeItem : findTreeItems(parent)) {
            if (treeItem.getItemCount() > 0) {
                if (!hasCachedChildren(treeItem)) {
                    treeItem.setItemCount(viewModel.getChildClassCount(parent));
                }
                else {
                    OntClassTreeData childData = new OntClassTreeData(child);
                    childData.setParent((TreeData) treeItem.getData());
                    viewer.remove(childData);
                }
            }
        }
    }

    // FIXME: My suggestion is to move this to a ResourceTreeViewer class
    // (See also InstancesView, PropertiesView.)
    public void updateResources(Collection<? extends Resource> resources) {
        if (resources == null) {
            return;
        }

        for (Resource resource : resources) {
            for (TreeItem treeItem : findTreeItems(resource)) {
                Object treeItemData = treeItem.getData();
                viewer.update(treeItemData, null);
            }
        }
    }

    // FIXME: My suggestion is to move this to a ResourceTreeViewer class
    // (See also InstancesView, PropertiesView.)
    /**
     * Returns a list of TreeItems that contain the resource.
     */
    private List<TreeItem> findTreeItems(Resource resource) {
        List<TreeItem> result = Lists.newArrayList();

        if (!Widgets.isNullOrDisposedViewer(viewer) && (resource != null)) {
            result.addAll(findTreeItems(resource, viewer.getTree().getItems()));
        }

        return result;
    }

    // FIXME: My suggestion is to move this to a ResourceTreeViewer class
    // (See also InstancesView, PropertiesView.)
    private List<TreeItem> findTreeItems(Resource resource, TreeItem[] treeItems) {
        List<TreeItem> result = Lists.newArrayList();

        if (!Widgets.isNullOrDisposedViewer(viewer) && (resource != null) && (treeItems != null)) {
            for (TreeItem treeItem : treeItems) {
                Object treeItemData = treeItem.getData();
                if (treeItemData != null) {
                    if ((treeItemData instanceof Resource) && resource.equals(treeItemData)) {
                        result.add(treeItem);
                    }
                    result.addAll(findTreeItems(resource, treeItem.getItems()));
                }
            }
        }

        return result;
    }

}
