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

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.semmtech.plugin.semmweb.core.CorePluginImages;
import com.semmtech.plugin.semmweb.core.extensionpoint.IPublisher.VersioningMode;
import com.semmtech.ui.plugin.decorators.OverlayImageIcon;


public final class CoreResourcePropertiesManager {

    public CoreResourcePropertiesManager() {
    }

    // Source Location
    public static String getSourceLocation(IResource resource) {
        return getPersistentProperty(resource, CoreResourceProperties.SOURCE_LOCATION_PROPERTY);
    }

    public static void setSourceLocation(IResource resource, String location) {
        addPersistentProperty(resource, CoreResourceProperties.SOURCE_LOCATION_PROPERTY, location);
    }

    public static void removeSourceLocation(IResource resource) {
        removePersistentProperty(resource, CoreResourceProperties.SOURCE_LOCATION_PROPERTY);
    }

    public static boolean hasSourceLocation(IResource resource) {
        return !Strings.isNullOrEmpty(getSourceLocation(resource));
    }

    // Source Version
    public static String getSourceVersion(IResource resource) {
        return getPersistentProperty(resource, CoreResourceProperties.SOURCE_VERSION_PROPERTY);
    }

    public static void setSourceVersion(IResource resource, String version) {
        addPersistentProperty(resource, CoreResourceProperties.SOURCE_VERSION_PROPERTY, version);
    }

    public static void removeSourceVersion(IResource resource) {
        removePersistentProperty(resource, CoreResourceProperties.SOURCE_VERSION_PROPERTY);
    }

    public static boolean hasSourceVersion(IResource resource) {
        return !Strings.isNullOrEmpty(getSourceVersion(resource));
    }

    // Source Versioning method
    public static VersioningMode getSourceVersioningmethod(IResource resource) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String jsonString = getPersistentProperty(resource,
                    CoreResourceProperties.SOURCE_VERSIONINGMETHOD_PROPERTY);
            if (jsonString != null) {
                VersioningMode method = mapper.readValue(jsonString, VersioningMode.class);
                return method;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void setSourceVersioningmethod(IResource resource, VersioningMode mode) {
        ObjectWriter writer = new ObjectMapper().writer().withDefaultPrettyPrinter();
        try {
            String jsonString = writer.writeValueAsString(mode);
            addPersistentProperty(resource,
                    CoreResourceProperties.SOURCE_VERSIONINGMETHOD_PROPERTY, jsonString);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void removeSourceVersioningmethod(IResource resource) {
        removePersistentProperty(resource, CoreResourceProperties.SOURCE_VERSIONINGMETHOD_PROPERTY);
    }

    public static boolean hasSourceVersioningmethod(IResource resource) {
        VersioningMode vm = getSourceVersioningmethod(resource);
        return (vm != null);
    }

    // Modified
    public static boolean isModified(IResource resource) {
        boolean modified = false;
        String value = getPersistentProperty(resource, CoreResourceProperties.MODIFIED);
        if (value != null) {
            modified = Boolean.parseBoolean(value);
        }
        return modified;
    }

    public static void setModified(IResource resource, boolean value) {
        addPersistentProperty(resource, CoreResourceProperties.MODIFIED, Boolean.toString(value));
    }

    /**
     * Adds the persistence property of the given resource
     * 
     * @param resource
     * @param key
     * @param value
     */
    private static void addPersistentProperty(IResource resource, QualifiedName key, String value) {
        try {
            resource.setPersistentProperty(key, value);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void removePersistentProperty(IResource resource, QualifiedName key) {
        try {
            resource.setPersistentProperty(key, null);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Returns the value of the given persistence property of the resource
     * 
     * @param resource
     * @param key
     * @return
     */
    private static String getPersistentProperty(IResource resource, QualifiedName key) {
        try {
            return resource.getPersistentProperty(key);
        }
        catch (CoreException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static Map<String, Integer> findDecorationImageForResource(IResource resource) {
        Map<String, Integer> decorations = Maps.newLinkedHashMap();
        if (hasSourceLocation(resource) && isModified(resource)) {
            decorations.put(CorePluginImages.IMG_OVERLAY_REPOSITORY_MODIFIED,
                    OverlayImageIcon.BOTTOM_RIGHT);
        }
        else if (hasSourceLocation(resource)) {
            decorations.put(CorePluginImages.IMG_OVERLAY_REPOSITORY, OverlayImageIcon.BOTTOM_RIGHT);
        }
        return decorations;
    }
}
