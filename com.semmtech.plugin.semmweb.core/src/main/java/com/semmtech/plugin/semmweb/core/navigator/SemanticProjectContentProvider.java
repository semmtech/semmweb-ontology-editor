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

package com.semmtech.plugin.semmweb.core.navigator;


import static com.semmtech.plugin.semmweb.core.preferences.LabelsPreference.PREFERENCE_RESOURCE_LABEL_RENDERING;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.navigator.CommonViewer;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.internal.navigator.SemanticProjectManager;
import com.semmtech.ui.plugin.widgets.Widgets;


public class SemanticProjectContentProvider implements ITreeContentProvider,
        IResourceChangeListener, IPropertyChangeListener {

    private static Logger logger = Logger.getLogger(SemanticProjectContentProvider.class);
    private CommonViewer viewer;

    public SemanticProjectContentProvider() {
        ResourcesPlugin.getWorkspace().addResourceChangeListener(this,
                IResourceChangeEvent.POST_BUILD | IResourceChangeEvent.PRE_REFRESH);
        CorePlugin.getDefault().getPreferenceStore().addPropertyChangeListener(this);
    }

    @Override
    public void dispose() {
        ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
        CorePlugin.getDefault().getPreferenceStore().removePropertyChangeListener(this);
    }

    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        this.viewer = (CommonViewer) viewer;
    }

    @Override
    public Object[] getElements(Object inputElement) {
        return null;
    }

    @Override
    public Object[] getChildren(Object parentElement) {
        Object[] children = null;

        // Get children
        if (parentElement instanceof IProject) {
            IProject project = (IProject) parentElement;
            SemanticProjectManager manager = SemanticProjectManager
                    .getSemanticProjectManager(project);
            ISemanticProject semanticProject = manager.obtainProject();

            children = semanticProject.getChildren();
        }
        else if (parentElement instanceof IParent) {
            children = ((IParent) parentElement).getChildren();
        }

        if (children == null) {
            return null;
        }

        // If a child has a working copy, provide that working copy instead.
        List<Object> result = Lists.newArrayListWithExpectedSize(children.length);

        for (int i = 0; i < children.length; i++) {
            Object child = children[i];

            // if the model is not backed by a resource then it will not shown
            // in the navigator (generally it happen when the model is deleted)
            if (child instanceof IModel) {
                IModel model = (IModel) child;
                if (model.getResource() == null) {
                    continue;
                }
            }

            if (child instanceof IResourceElement) {
                IResourceElement resourceElement = (IResourceElement) child;
                if (resourceElement.hasWorkingCopy()) {
                    child = resourceElement.getWorkingCopy();
                }
            }
            result.add(child);
        }

        return result.toArray();
    }

    @Override
    public Object getParent(Object element) {
        if (element instanceof ISemanticElement) {
            return ((ISemanticElement) element).getParent();
        }
        return null;
    }

    @Override
    public boolean hasChildren(Object element) {
        if (element instanceof IParent) {
            return ((IParent) element).hasChildren();
        }
        return false;
    }

    private void refreshViewer() {
        Display.getDefault().syncExec(new Runnable() {

            @Override
            public void run() {
                TreePath[] treePaths = viewer.getExpandedTreePaths();

                viewer.getControl().setRedraw(false);
                viewer.refresh();
                viewer.setExpandedTreePaths(treePaths);
                expandSemanticElementPaths(viewer, treePaths);
                viewer.getControl().setRedraw(true);
            }
        });
    }

    protected void expandSemanticElementPaths(TreeViewer viewer, TreePath[] treePaths) {
        for (TreePath path : treePaths) {
            // If this path does not contain an ISemanticElement, continue to
            // the next path.
            if (!pathContainsSemanticElement(path)) {
                continue;
            }

            List<Object> expandPath = Lists.newArrayList();

            for (int i = 0; i < path.getSegmentCount(); i++) {
                Object segment = path.getSegment(i);
                if (segment instanceof ISemanticElement) {
                    ISemanticElement element = (ISemanticElement) segment;
                    if (element instanceof IParent) {
                        TreePath curPath = new TreePath(expandPath.toArray());
                        TreeItem parentItem = getTreeItem(curPath, viewer);
                        segment = findEquivalentElement(element, parentItem.getItems());
                    }
                }

                if (segment == null) {
                    break; // Could not find the next to be expanded element
                }
                expandPath.add(segment);
            }

            viewer.expandToLevel(new TreePath(expandPath.toArray()), 1);
        }
    }

    protected boolean pathContainsSemanticElement(TreePath path) {
        for (int i = 0; i < path.getSegmentCount(); i++) {
            Object segment = path.getSegment(i);
            if (segment instanceof ISemanticElement) {
                return true;
            }
        }
        return false;
    }

    private ISemanticElement findEquivalentElement(ISemanticElement element, TreeItem[] treeItems) {
        int elementType = element.getElementType();

        for (TreeItem childItem : treeItems) {
            if (childItem.getData() instanceof ISemanticElement) {
                ISemanticElement childElement = (ISemanticElement) childItem.getData();
                if (childElement.getElementType() == elementType) {
                    if (elementType == ISemanticElement.MODEL) {
                        IResource childResource = ((IModel) childElement).getResource();
                        IResource elementResource = ((IModel) element).getResource();
                        if (Objects.equal(childResource, elementResource)) {
                            return childElement;
                        }
                    }
                    else if (childElement.getElementType() == elementType) {
                        return childElement;
                    }
                }
            }
        }
        return null;
    }

    private TreeItem getTreeItem(TreePath path, TreeViewer viewer) {
        if (Widgets.isNullOrDisposedViewer(viewer)) {
            return null;
        }

        TreeItem result = null;
        TreeItem[] items = viewer.getTree().getItems();

        for (int i = 0; i < path.getSegmentCount(); i++) {
            Object dataToFind = path.getSegment(i);

            result = null;
            if (items == null) {
                return null;
            }

            for (TreeItem candidateItem : items) {
                if (candidateItem.getData() == dataToFind) {
                    result = candidateItem;
                }
            }

            if (result == null) {
                return null;
            }
            items = result.getItems();
        }

        return result;
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        String property = event.getProperty();
        if (PREFERENCE_RESOURCE_LABEL_RENDERING.equals(property)) {
            refreshViewer();
        }
    }

    @Override
    public void resourceChanged(IResourceChangeEvent event) {
        /*
         * When a CLEAN_BUILD is performed the POST_BUILD event is generated
         * after the call to SemanticProjectBuilder.clean(). But we don't want
         * to refresh the tree at this point. We want to refresh the tree after
         * SemanticProjectBuilder.build() method call (that is called after
         * clean with the FULL_BUILD parameter).
         */
        if (event.getBuildKind() != IncrementalProjectBuilder.CLEAN_BUILD) {
            logger.trace("resourceChanged: " + event.getBuildKind());
            refreshViewer();
        }
    }

}
