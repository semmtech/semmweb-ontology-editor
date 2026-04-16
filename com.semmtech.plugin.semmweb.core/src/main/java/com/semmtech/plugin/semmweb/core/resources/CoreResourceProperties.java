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

package com.semmtech.plugin.semmweb.core.resources;


import java.util.Map;

import org.eclipse.core.runtime.QualifiedName;

import com.google.common.collect.Maps;


public final class CoreResourceProperties {
    private static final String QUALIFIER = "com.semmtech.plugin.semmweb.core.resources";

    public static final QualifiedName SOURCE_LOCATION_PROPERTY = createQualifiedName("resourceLocationProperty");
    public static final QualifiedName SOURCE_VERSION_PROPERTY = createQualifiedName("resourceVersionProperty");
    public static final QualifiedName SOURCE_VERSIONINGMETHOD_PROPERTY = createQualifiedName("resourceVersioningmethodProperty");
    public static final QualifiedName MODIFIED = createQualifiedName("Modified");

    private static CoreResourceProperties instance;
    private static Map<String, QualifiedName> properties;

    private CoreResourceProperties() {
    }

    private static QualifiedName createQualifiedName(String localName) {
        if (properties == null)
            properties = Maps.newHashMap();
        properties.put(localName, new QualifiedName(QUALIFIER, localName));
        return properties.get(localName);
    }

    public QualifiedName getQualifiedName(String localName) {
        if (properties == null)
            return null;
        if (properties.containsKey(localName))
            return properties.get(localName);
        return null;
    }

    public static CoreResourceProperties getInstance() {
        if (instance == null)
            instance = new CoreResourceProperties();
        return instance;
    }
}
