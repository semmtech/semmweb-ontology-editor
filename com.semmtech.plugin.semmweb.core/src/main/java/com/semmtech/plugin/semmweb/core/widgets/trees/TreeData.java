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

import com.google.common.collect.Lists;


/**
 * This class can be used as a wrapper around data objects used within a Tree.
 * Even if similar data objects are nested, these same objects can be uniquely
 * wrapped within this TreeData class. Using an id, as well as a complete path
 * from the root of the Tree to the current object, each data object is unique
 * identified.
 * 
 * See ResourceTreeData for an example of this.
 * 
 * @author Mike Henrichs
 * 
 */
public class TreeData {

    private final String id;
    private final Object data;
    private TreeData parent = null;
    private List<TreeData> children = Lists.newArrayList();

    public TreeData(String id, Object data) {
        this.id = id;
        this.data = data;
    }

    public String getID() {
        return id;
    }

    public Object getData() {
        return data;
    }

    public TreeData getParent() {
        return parent;
    }

    public void setParent(TreeData parent) {
        this.parent = parent;
    }

    public List<TreeData> getChildren() {
        return children;
    }

    public void addChild(TreeData child) {
        children.add(child);
    }

    public void removeChild(TreeData child) {
        children.remove(child);
    }

    public String getPath() {
        StringBuffer path = new StringBuffer();
        if (parent != null) {
            path.append(String.format("%s >", parent.getPath()));
        }
        path.append(String.format("[%s]", id));
        return path.toString();
    }

    @Override
    public String toString() {
        return String.format("%s: [%s]", id, data.toString());
    }

    private String getHashable() {
        return getPath().toString();
    }

    /**
     * Returns the depth of this element. If element has no parent 0 is
     * returned; otherwise the depth of the parent is incremented with one.
     * 
     * @return
     */
    public int getDepth() {
        if (parent == null) {
            return 0;
        }
        return parent.getDepth() + 1;
    }

    @Override
    public int hashCode() {
        return getHashable().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof TreeData)) {
            return false;
        }
        return equals((TreeData) obj);
    }

    private boolean equals(TreeData other) {
        return getPath().equals(other.getPath());
    }
}
