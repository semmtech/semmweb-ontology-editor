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


import java.text.Collator;
import java.util.Comparator;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

import com.semmtech.plugin.semmweb.core.internal.navigator.NamespaceComparator;


public class SemanticNodeSorter extends ViewerSorter {
    private final Comparator<INamespace> comparator = new NamespaceComparator();

    public SemanticNodeSorter() {

    }

    public SemanticNodeSorter(Collator collator) {
        super(collator);
    }

    @Override
    public int compare(Viewer viewer, Object e1, Object e2) {
        if (e1 instanceof INamespace && e2 instanceof INamespace) {
            return comparator.compare((INamespace) e1, (INamespace) e2);
        }
        return super.compare(viewer, e1, e2);
    }

}
