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

package com.semmtech.ui.plugin.preference;


import java.util.Map;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;

import com.google.common.collect.Maps;


/**
 * Preference stored which is stored within memory.
 * 
 * @author Mike Henrichs
 * 
 */
public class MemPreferenceStore implements IPreferenceStore {

    private Map<String, String> defaults = Maps.newHashMap();
    private Map<String, String> preferences = Maps.newHashMap();

    @Override
    public void addPropertyChangeListener(IPropertyChangeListener listener) {

    }

    @Override
    public boolean contains(String name) {
        return preferences.containsKey(name);
    }

    @Override
    public void firePropertyChangeEvent(String name, Object oldValue, Object newValue) {

    }

    @Override
    public boolean getBoolean(String name) {
        if (preferences.containsKey(name)) {
            return Boolean.parseBoolean(preferences.get(name));
        }
        return getDefaultBoolean(name);
    }

    @Override
    public boolean getDefaultBoolean(String name) {
        if (defaults.containsKey(name)) {
            return Boolean.parseBoolean(defaults.get(name));
        }
        return IPreferenceStore.BOOLEAN_DEFAULT_DEFAULT;
    }

    @Override
    public double getDefaultDouble(String name) {
        if (defaults.containsKey(name)) {
            return Double.parseDouble(defaults.get(name));
        }
        return IPreferenceStore.DOUBLE_DEFAULT_DEFAULT;
    }

    @Override
    public float getDefaultFloat(String name) {
        if (defaults.containsKey(name)) {
            return Float.parseFloat(defaults.get(name));
        }
        return IPreferenceStore.FLOAT_DEFAULT_DEFAULT;
    }

    @Override
    public int getDefaultInt(String name) {
        if (defaults.containsKey(name)) {
            return Integer.parseInt(defaults.get(name));
        }
        return IPreferenceStore.INT_DEFAULT_DEFAULT;
    }

    @Override
    public long getDefaultLong(String name) {
        if (defaults.containsKey(name)) {
            return Long.parseLong(defaults.get(name));
        }
        return IPreferenceStore.LONG_DEFAULT_DEFAULT;
    }

    @Override
    public String getDefaultString(String name) {
        if (defaults.containsKey(name)) {
            return defaults.get(name);
        }
        return IPreferenceStore.STRING_DEFAULT_DEFAULT;
    }

    @Override
    public double getDouble(String name) {
        if (preferences.containsKey(name)) {
            return Double.parseDouble(preferences.get(name));
        }
        return getDefaultDouble(name);
    }

    @Override
    public float getFloat(String name) {
        if (preferences.containsKey(name)) {
            return Float.parseFloat(preferences.get(name));
        }
        return getDefaultFloat(name);
    }

    @Override
    public int getInt(String name) {
        if (preferences.containsKey(name)) {
            return Integer.parseInt(preferences.get(name));
        }
        return getDefaultInt(name);
    }

    @Override
    public long getLong(String name) {
        if (preferences.containsKey(name)) {
            return Long.parseLong(preferences.get(name));
        }
        return getDefaultLong(name);
    }

    @Override
    public String getString(String name) {
        if (preferences.containsKey(name)) {
            return preferences.get(name);
        }
        return getDefaultString(name);
    }

    @Override
    public boolean isDefault(String name) {
        if (preferences.containsKey(name) && defaults.containsKey(name)) {
            return preferences.get(name).equals(defaults.get(name));
        }
        return false;
    }

    @Override
    public boolean needsSaving() {
        return false;
    }

    @Override
    public void putValue(String name, String value) {
        preferences.put(name, value);
    }

    @Override
    public void removePropertyChangeListener(IPropertyChangeListener listener) {

    }

    @Override
    public void setDefault(String name, double value) {
        defaults.put(name, Double.toString(value));
    }

    @Override
    public void setDefault(String name, float value) {
        defaults.put(name, Float.toString(value));
    }

    @Override
    public void setDefault(String name, int value) {
        defaults.put(name, Integer.toString(value));
    }

    @Override
    public void setDefault(String name, long value) {
        defaults.put(name, Long.toString(value));
    }

    @Override
    public void setDefault(String name, String defaultObject) {
        defaults.put(name, defaultObject);
    }

    @Override
    public void setDefault(String name, boolean value) {
        defaults.put(name, Boolean.toString(value));
    }

    @Override
    public void setToDefault(String name) {
        if (defaults.containsKey(name)) {
            preferences.put(name, defaults.get(name));
        }
    }

    @Override
    public void setValue(String name, double value) {
        preferences.put(name, Double.toString(value));
    }

    @Override
    public void setValue(String name, float value) {
        preferences.put(name, Float.toString(value));
    }

    @Override
    public void setValue(String name, int value) {
        preferences.put(name, Integer.toString(value));

    }

    @Override
    public void setValue(String name, long value) {
        preferences.put(name, Long.toString(value));
    }

    @Override
    public void setValue(String name, String value) {
        preferences.put(name, value);
    }

    @Override
    public void setValue(String name, boolean value) {
        preferences.put(name, Boolean.toString(value));
    }
}
