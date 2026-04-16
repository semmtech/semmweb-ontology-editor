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

package com.semmtech.plugin.semmweb.core.internal.navigator;


import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.PlatformObject;

import com.google.common.collect.Lists;
import com.semmtech.plugin.semmweb.core.navigator.IResourceElement;
import com.semmtech.plugin.semmweb.core.navigator.ISemanticElement;
import com.semmtech.plugin.semmweb.core.navigator.ISemanticProject;


public abstract class SemanticElement extends PlatformObject implements ISemanticElement {

    protected ISemanticElement parent;
    protected final List<ISemanticElement> children;

    protected SemanticElement(SemanticElement parent) {
        this.parent = parent;
        this.children = Lists.newArrayList();
        if (parent != null) {
            parent.addChild(this);
        }
    }

    @Override
    public IProject getProject() {
        ISemanticProject semanticProject = getSemanticProject();
        return semanticProject.getProject();
    }

    @Override
    public ISemanticProject getSemanticProject() {
        return (ISemanticProject) getAncestor(SEMANTIC_PROJECT);
    }

    public String[] getPath() {
        String[] parentPath = new String[0];
        ISemanticElement parent = getParent();
        if (parent != null) {
            parentPath = parent.getPath();
        }

        String[] path = new String[parentPath.length + 1];
        System.arraycopy(parentPath, 0, path, 0, parentPath.length);
        path[path.length - 1] = getId();

        return path;
    }

    @Override
    public String getElementName() {
        return "";
    }

    @Override
    public ISemanticElement getAncestor(int ancestorType) {
        ISemanticElement element = this;
        while (element != null) {
            if (element.getElementType() == ancestorType) {
                return element;
            }

            // If the current element is a working copy of another
            // IResourceElement, the ancestors will be provided by that other
            // IResourceElement instead.
            if (element instanceof IResourceElement) {
                IResourceElement resourceElement = (IResourceElement) element;
                if (resourceElement.isWorkingCopy()) {
                    IResourceElement persistedElement = resourceElement.getPersistedCopy();
                    if (persistedElement instanceof ISemanticElement) {
                        element = (ISemanticElement) persistedElement;
                    }
                    else {
                        return null;
                    }
                }
            }

            element = element.getParent();
        }
        return null;
    }

    @Override
    public ISemanticElement getParent() {
        return parent;
    }

    public ISemanticElement[] getChildren() {
        int size = children.size();
        ISemanticElement[] array = new ISemanticElement[size];
        children.toArray(array);
        return array;
    }

    public List<ISemanticElement> getChildrenByType(int childType) {
        List<ISemanticElement> list = Lists.newArrayListWithCapacity(children.size());
        for (ISemanticElement child : children) {
            if (child.getElementType() == childType) {
                list.add(child);
            }
        }
        return list;
    }

    public boolean hasChildren() {
        return !children.isEmpty();
    }

    protected void addChild(ISemanticElement child) {
        children.add(child);
    }

    @Override
    public boolean equals(Object other) {
        if (other == null || !(other instanceof SemanticElement)) {
            return false;
        }
        SemanticElement otherElement = (SemanticElement) other;
        if (otherElement.getElementType() != getElementType()) {
            return false;
        }
        return Arrays.equals(getPath(), otherElement.getPath());
    }

    @Override
    public String toString() {
        return Arrays.toString(getPath());
    }
}
