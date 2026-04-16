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

package com.semmtech.semantics.util;


import java.util.Comparator;


/**
 * Comparator used to compare instances of {@link NamespaceMapping}
 * 
 * @author Mike Henrichs
 * 
 */
public class NamespaceMappingComparator implements Comparator<NamespaceMapping> {

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    @Override
    public int compare(NamespaceMapping ns1, NamespaceMapping ns2) {
        if (!ns1.hasPrefix() && ns2.hasPrefix()) {
            return -1;
        }
        else if (ns1.hasPrefix() && !ns2.hasPrefix()) {
            return 1;
        }
        else if (ns1.hasPrefix()) {
            String p1 = ns1.getPrefix();
            String p2 = ns2.getPrefix();

            int result = p1.compareToIgnoreCase(p2);
            if (result != 0) {
                return result;
            }
        }
        String uri1 = ns1.getURI();
        String uri2 = ns2.getURI();

        return uri1.compareToIgnoreCase(uri2);
    }
}
