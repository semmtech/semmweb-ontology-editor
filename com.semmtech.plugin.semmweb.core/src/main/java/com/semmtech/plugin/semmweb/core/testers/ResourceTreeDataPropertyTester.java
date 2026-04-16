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

package com.semmtech.plugin.semmweb.core.testers;


import org.eclipse.core.expressions.PropertyTester;

import com.semmtech.plugin.semmweb.core.widgets.trees.ResourceTreeData;


public class ResourceTreeDataPropertyTester extends PropertyTester {

    public static final String PROPERTY_HAS_PARENT = "hasParent";
    public static final String PROPERTY_HAS_CHILDREN = "hasChildren";
    public static final String PROPERTY_CHILD_COUNT = "childCount";

    public ResourceTreeDataPropertyTester() {
    }

    @Override
    public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
        if (property.equals(PROPERTY_HAS_PARENT) && receiver instanceof ResourceTreeData) {
            ResourceTreeData data = (ResourceTreeData) receiver;
            return (data.getParent() != null);
        }
        else if (property.equals(PROPERTY_HAS_CHILDREN) && receiver instanceof ResourceTreeData) {
            ResourceTreeData data = (ResourceTreeData) receiver;
            return (data.getChildren().size() > 0);
        }
        else if (property.equals(PROPERTY_CHILD_COUNT) && receiver instanceof ResourceTreeData) {
            ResourceTreeData data = (ResourceTreeData) receiver;
            int count = Integer.parseInt(expectedValue.toString());
            return (data.getChildren().size() == count);
        }
        return false;
    }

}
