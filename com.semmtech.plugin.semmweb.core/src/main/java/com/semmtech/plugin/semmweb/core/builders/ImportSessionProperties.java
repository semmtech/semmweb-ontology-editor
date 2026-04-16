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
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;


/**
 * This class stores the metadeta related to the imports of a certain Model in
 * the session property of of it's related resource.
 * 
 * @author Simone Rondelli
 * 
 */
class ImportSessionProperties {
    private Map<String, ImportInfo> metadataInfo;

    public ImportSessionProperties() {
        metadataInfo = Maps.newHashMap();
    }

    public void addImportInfo(ImportInfo importInfo) {
        metadataInfo.put(importInfo.getImportUri(), importInfo);
    }

    public boolean hasImportedUri(String importUri) {
        return metadataInfo.containsKey(importUri);
    }

    public ImportInfo getImportInfo(String importUri) {
        return metadataInfo.get(importUri);
    }

    public Collection<ImportInfo> listImportInfo() {
        return metadataInfo.values();
    }

    public Collection<String> getDependencies(String importUri) {
        Set<String> dependencies = Sets.newHashSet();

        for (ImportInfo info : metadataInfo.values()) {
            if (info.isDerivedFrom(importUri)) {
                dependencies.add(info.getImportUri());
            }
        }
        return dependencies;
    }

    public void removeImportInfo(String importUri) {

    }
}