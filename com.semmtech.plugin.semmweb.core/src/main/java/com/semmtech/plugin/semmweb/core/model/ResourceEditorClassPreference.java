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

package com.semmtech.plugin.semmweb.core.model;


import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;


public class ResourceEditorClassPreference {
    public static final int SETTING_UNKNOWN = 0;
    // public static final int SETTING_HIDDEN = 1;
    public static final int SETTING_SHOW_ON_SUBCLASSES = 2;
    public static final int SETTING_SHOW_ON_INSTANCES = 4;
    public static final int SETTING_SHOW_ALWAYS = SETTING_SHOW_ON_INSTANCES
            | SETTING_SHOW_ON_SUBCLASSES;

    public static final String WIDGET_ALL_PROPERTIES = "AllPropertiesWidget";

    private String classUri;

    private int allPropertiesSetting = SETTING_SHOW_ALWAYS;
    private int restrictionSetting = SETTING_UNKNOWN;
    private int proposedAspectsSetting = SETTING_UNKNOWN;

    private Map<String, Integer> allPropertiesProperties = Maps.newHashMap();
    private Map<String, Integer> qcrProperties = Maps.newHashMap();

    public ResourceEditorClassPreference() {
    }

    public ResourceEditorClassPreference(Resource clazz) {
        this.classUri = clazz.getURI();
    }

    public ResourceEditorClassPreference(String classUri) {
        this.classUri = classUri;
    }

    public String getClassURI() {
        return classUri;
    }

    public void setClassURI(String classUri) {
        this.classUri = classUri;
    }

    public int getAllPropertiesSetting() {
        return allPropertiesSetting;
    }

    public void setAllPropertiesSetting(int allPropertiesSetting) {
        this.allPropertiesSetting = allPropertiesSetting;
    }

    public int getRestrictionsSetting() {
        return restrictionSetting;
    }

    public void setRestrictionsSetting(int restrictionSetting) {
        this.restrictionSetting = restrictionSetting;
    }

    public Map<String, Integer> getAllPropertiesProperties() {
        return allPropertiesProperties;
    }

    public Map<String, Integer> getQCRProperties() {
        return qcrProperties;
    }

    public void setAllPropertiesProperties(Map<String, Integer> allPropertiesProperties) {
        this.allPropertiesProperties = allPropertiesProperties;
    }

    public void setQCRProperties(Map<String, Integer> qcrProperties) {
        this.qcrProperties = qcrProperties;
    }

    public int getPossessedAspectsSetting() {
        return proposedAspectsSetting;
    }

    public void setPossessedAspectsSetting(int proposedAspectsSetting) {
        this.proposedAspectsSetting = proposedAspectsSetting;
    }

    public List<String> getPropertyURIs() {
        return Lists.newArrayList(allPropertiesProperties.keySet());
    }

    public void addPropertySetting(Property property, int setting) {
        allPropertiesProperties.put(property.getURI(), new Integer(setting));
    }

    public void addPropertySetting(String propertyUri, int setting) {
        allPropertiesProperties.put(propertyUri, new Integer(setting));
    }

    public int getPropertySetting(Property property) {
        return getPropertySetting(property.getURI());
    }

    public int getPropertySetting(String propertyUri) {
        if (!allPropertiesProperties.containsKey(propertyUri)) {
            return SETTING_UNKNOWN;
        }
        return allPropertiesProperties.get(propertyUri).intValue();
    }

    public void removePropertySetting(Property property) {
        allPropertiesProperties.remove(property.getURI());
    }

    public List<String> getQCRPropertyURIs() {
        return Lists.newArrayList(qcrProperties.keySet());
    }

    public void addQCRPropertySetting(Property property, int setting) {
        qcrProperties.put(property.getURI(), new Integer(setting));
    }

    public void addQCRPropertySetting(String propertyUri, int setting) {
        qcrProperties.put(propertyUri, new Integer(setting));
    }

    public int getQCRPropertySetting(Property property) {
        return getQCRPropertySetting(property.getURI());
    }

    public int getQCRPropertySetting(String propertyUri) {
        if (!qcrProperties.containsKey(propertyUri)) {
            return SETTING_UNKNOWN;
        }

        return qcrProperties.get(propertyUri).intValue();
    }

    public void removeQCRPropertySetting(Property property) {
        qcrProperties.remove(property.getURI());
    }
}
