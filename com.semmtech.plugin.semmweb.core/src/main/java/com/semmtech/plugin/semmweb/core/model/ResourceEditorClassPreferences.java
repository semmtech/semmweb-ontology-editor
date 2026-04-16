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


import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;


public class ResourceEditorClassPreferences extends
        LinkedHashMap<String, ResourceEditorClassPreference> {

    private static final long serialVersionUID = 9064630380339742809L;

    public ResourceEditorClassPreferences() {
    }

    public boolean containsClassURI(String classUri) {
        return containsKey(classUri);
    }

    public List<String> getClassURIs() {
        String[] array = new String[keySet().size()];
        keySet().toArray(array);
        List<String> uris = new ArrayList<>(Arrays.asList(array));
        return uris;
    }

    public ResourceEditorClassPreference getPreference(Resource clazz) {
        return get(clazz.getURI());
    }

    public ResourceEditorClassPreference getPreference(String classUri) {
        return get(classUri);
    }

    public void addPreference(ResourceEditorClassPreference pref) {
        put(pref.getClassURI(), pref);
    }

    public void addPropertySetting(Resource clazz, Property property, int setting) {
        ResourceEditorClassPreference pref = null;
        if (!containsClassURI(clazz.getURI())) {
            pref = new ResourceEditorClassPreference(clazz);
        }
        else {
            pref = getPreference(clazz);
        }
        pref.addPropertySetting(property, setting);
        addPreference(pref);
    }

    public void addQCRPropertySetting(Resource clazz, Property property, int setting) {
        ResourceEditorClassPreference pref = null;
        if (!containsClassURI(clazz.getURI()))
            pref = new ResourceEditorClassPreference(clazz);
        else
            pref = getPreference(clazz);
        pref.addQCRPropertySetting(property, setting);
        addPreference(pref);
    }

    public int getPropertySetting(Resource clazz, Property property) {
        if (containsClassURI(clazz.getURI()))
            return getPreference(clazz).getPropertySetting(property);
        return ResourceEditorClassPreference.SETTING_UNKNOWN;
    }

    public int getAllPropertiesSetting(Resource clazz) {
        if (containsClassURI(clazz.getURI()))
            return getPreference(clazz).getAllPropertiesSetting();
        return ResourceEditorClassPreference.SETTING_UNKNOWN;
    }

    public void setAllPropertiesSetting(Resource clazz, int setting) {
        if (containsClassURI(clazz.getURI()))
            getPreference(clazz).setAllPropertiesSetting(setting);
    }

    public int getPossessedAspectsSetting(Resource clazz) {
        if (containsClassURI(clazz.getURI()))
            return getPreference(clazz).getPossessedAspectsSetting();
        return ResourceEditorClassPreference.SETTING_UNKNOWN;
    }

    public void setProposedAspectsSetting(Resource clazz, int setting) {
        if (containsClassURI(clazz.getURI())) {
            getPreference(clazz).setPossessedAspectsSetting(setting);
        }
        else {
            ResourceEditorClassPreference pref = new ResourceEditorClassPreference(clazz);
            pref.setPossessedAspectsSetting(setting);
            addPreference(pref);
        }
    }

    public int getRestrictionSettings(Resource clazz) {
        if (containsClassURI(clazz.getURI()))
            return getPreference(clazz).getRestrictionsSetting();
        return ResourceEditorClassPreference.SETTING_UNKNOWN;
    }

    public void setRestrictionSettings(Resource clazz, int setting) {
        if (containsClassURI(clazz.getURI())) {
            getPreference(clazz).setRestrictionsSetting(setting);
        }
        else {
            ResourceEditorClassPreference pref = new ResourceEditorClassPreference(clazz);
            pref.setRestrictionsSetting(setting);
            addPreference(pref);
        }
    }

    public boolean hasPropertySettings(Resource clazz) {
        if (containsClassURI(clazz.getURI()))
            return (getPreference(clazz).getPropertyURIs().size() > 0);
        return false;
    }

    public boolean hasPropertySetting(Resource clazz, Property property) {
        if (containsClassURI(clazz.getURI()))
            return (getPreference(clazz).getPropertySetting(property) != ResourceEditorClassPreference.SETTING_UNKNOWN);
        return false;
    }

    public void removePropertySetting(Resource clazz, Property property) {
        if (containsClassURI(clazz.getURI()))
            getPreference(clazz).removePropertySetting(property);
    }
}
