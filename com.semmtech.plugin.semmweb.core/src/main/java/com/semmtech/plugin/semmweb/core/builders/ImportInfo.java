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


import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

import com.google.common.base.Preconditions;
import com.semmtech.plugin.semmweb.core.navigator.ImportType;


/**
 * 
 * @author Simone Rondelli
 */
class ImportInfo {
    private final String importUri;
    private final List<String> derivedFrom;
    private final ImportType type;
    // private final String modelUri;

    private final boolean direct;

    public ImportInfo(String importUri, String modelUri, List<String> derivedFrom, ImportType type) {
        Preconditions.checkNotNull(importUri);
        Preconditions.checkNotNull(type);
        Preconditions.checkArgument(!CollectionUtils.isEmpty(derivedFrom));
        this.importUri = importUri;
        this.derivedFrom = derivedFrom;
        this.type = type;
        // this.modelUri = modelUri;

        if (modelUri != null) {
            direct = this.derivedFrom.remove(modelUri);
        }
        else {
            direct = false;
        }
    }

    public String getImportUri() {
        return importUri;
    }

    public List<String> getDerivedFrom() {
        return derivedFrom;
    }

    public boolean isDerivedFrom(String uri) {
        return derivedFrom.contains(uri);
    }

    public ImportType getType() {
        return type;
    }

    public boolean isDirectImport() {
        return direct;
    }
}