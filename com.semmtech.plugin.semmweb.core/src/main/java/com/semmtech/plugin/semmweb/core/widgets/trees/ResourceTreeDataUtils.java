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

package com.semmtech.plugin.semmweb.core.widgets.trees;


import java.util.List;

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;

import com.google.common.collect.Lists;


public class ResourceTreeDataUtils {

    public static void setSelection(TreeViewer treeViewer, ResourceTreeData element) {
        List<ResourceTreeData> path = Lists.newArrayList();
        path.add(0, element);
        ResourceTreeData parent = (ResourceTreeData) element.getParent();

        while (parent != null) {
            path.add(0, parent);
            parent = (ResourceTreeData) parent.getParent();
        }

        TreePath treePath = new TreePath(path.toArray());
        treeViewer.setExpandedState(treePath, true);
        treeViewer.setSelection(new StructuredSelection(element), true);
    }
}