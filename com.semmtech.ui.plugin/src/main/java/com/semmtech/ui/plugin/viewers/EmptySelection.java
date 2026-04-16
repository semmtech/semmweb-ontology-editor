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


import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;

import com.google.common.collect.Lists;


public class EmptySelection implements IStructuredSelection {
    private static final List<Object> selected = Lists.newArrayList();

    @Override
    public boolean isEmpty() {
        return selected.isEmpty();
    }

    @Override
    public Object getFirstElement() {
        return null;
    }

    @Override
    public Iterator<Object> iterator() {
        return selected.iterator();
    }

    @Override
    public int size() {
        return selected.size();
    }

    @Override
    public Object[] toArray() {
        return selected.toArray();
    }

    @Override
    public List<Object> toList() {
        return selected;
    }
}
