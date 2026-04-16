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

package com.semmtech.plugin.semmweb.core.builders;


import java.util.Collection;
import java.util.Map;

import com.google.common.collect.Maps;


/**
 * This class stores the metadeta related to the namespaces of a certain Model
 * in the session property of it's related resource.
 * 
 * @author Simone Rondelli
 * 
 */
class NamespaceSessionProperties {

    private Map<String, NamespaceInfo> namespaceInfo;

    public NamespaceSessionProperties() {
        namespaceInfo = Maps.newHashMap();
    }

    public void addNamespaceInfo(NamespaceInfo info) {
        namespaceInfo.put(info.getUri(), info);
    }

    public Collection<NamespaceInfo> listNamespaceInfo() {
        return namespaceInfo.values();
    }

}
