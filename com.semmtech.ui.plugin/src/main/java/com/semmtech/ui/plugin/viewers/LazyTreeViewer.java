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

package com.semmtech.ui.plugin.viewers;


/*******************************************************************************
 * Copyright (c) 2010 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
import java.util.Collection;
import java.util.Set;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Widget;

import com.google.common.collect.Sets;


/**
 * Use this class when you have to deal with tree that contains an infinite data
 * structure. The aim of this class is to avoid the interface hang on when the
 * method setExpandedElements is called
 * 
 * THX GOOGLE <3: https://eclipse.googlesource.com/tmf/org.eclipse.xtext/+/
 * cd0a55895bc2188d46c52d9bad3c45479b8f4841
 * /plugins/org.eclipse.xtext.ui/src/org/
 * eclipse/xtext/ui/editor/outline/LazyTreeViewer.java
 * 
 * @author koehnlein - Initial contribution and API
 */
public class LazyTreeViewer extends TreeViewer {
    public LazyTreeViewer(Composite parent, int styles) {
        super(parent, styles);
    }

    @Override
    public void setExpandedElements(Object[] elements) {
        assertElementsNotNull(elements);
        if (checkBusy()) {
            return;
        }

        // NB: the main difference is the use of a Set in place of a Map
        Set<Object> expandedElements = Sets.newHashSet();
        for (int i = 0; i < elements.length; ++i) {
            Object element = elements[i];
            // Ensure item exists for element. This will materialize items for
            // each element and their parents, if possible. This is important
            // to support expanding of inner tree nodes without necessarily
            // expanding their parents.
            internalExpand(element, false);
            expandedElements.add(element);
        }
        // this will traverse all existing items, and create children for
        // elements that need to be expanded. If the tree contains multiple
        // equal elements, and those are in the set of elements to be expanded,
        // only the first item found for each element will be expanded.
        internalSetExpanded(expandedElements, getControl());
    }

    protected void internalSetExpanded(Collection<?> expandedElements, Widget widget) {
        Item[] items = getChildren(widget);
        for (int i = 0; i < items.length; i++) {
            Item item = items[i];
            Object data = item.getData();
            if (data != null) {
                // remove the element to avoid an infinite loop
                // if the same element appears on a child item
                boolean expanded = expandedElements.remove(data);
                if (expanded != getExpanded(item)) {
                    if (expanded) {
                        createChildren(item);
                    }
                    setExpanded(item, expanded);
                }
                if (expandedElements.size() > 0) {
                    internalSetExpanded(expandedElements, item);
                }
            }
        }
    }
}