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

package com.semmtech.plugin.semmweb.core.viewers;


import java.util.Comparator;

import org.eclipse.jface.viewers.ILabelProvider;

import com.hp.hpl.jena.rdf.model.RDFNode;


/**
 * 
 * @author Mike Henrichs
 * 
 */
public class ResourceNameComparator implements Comparator<RDFNode> {
    ILabelProvider labelProvider = null;

    public ResourceNameComparator(ILabelProvider provider) {
        this.labelProvider = provider;
    }

    /**
     * Compares the labels of each of the two resources.
     */
    @Override
    public int compare(RDFNode resource1, RDFNode resource2) {
        if (labelProvider == null) {
            return 0;
        }
        String name1 = labelProvider.getText(resource1);
        String name2 = labelProvider.getText(resource2);
        if (name1 == null) {
            name1 = "";
        }
        if (name2 == null) {
            name2 = "";
        }
        return name1.compareToIgnoreCase(name2);
    }
}
