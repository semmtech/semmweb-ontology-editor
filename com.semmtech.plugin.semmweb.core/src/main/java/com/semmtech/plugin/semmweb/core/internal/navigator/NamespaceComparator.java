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


import java.util.Comparator;

import com.semmtech.plugin.semmweb.core.navigator.INamespace;


public class NamespaceComparator implements Comparator<INamespace> {

    @Override
    public int compare(INamespace ns1, INamespace ns2) {
        if ((ns1.getPrefix() != null && ns2.getPrefix() != null)) {
            int result = ns1.getPrefix().compareTo(ns2.getPrefix());
            if (result == 0) {
                return ns1.getURI().compareTo(ns2.getURI());
            }
            return result;
        }
        else if (ns1.getPrefix() != null) {
            return -1;
        }
        else if (ns2.getPrefix() != null) {
            return 1;
        }
        return ns1.getURI().compareTo(ns2.getURI());
    }
}
