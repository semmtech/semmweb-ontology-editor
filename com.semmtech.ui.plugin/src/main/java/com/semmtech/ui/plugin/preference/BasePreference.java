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


import java.io.IOException;
import java.io.StringWriter;

import org.apache.log4j.Logger;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;


public abstract class BasePreference {
    private static final Logger logger = Logger.getLogger(BasePreference.class);

    private final IPreferenceStore store;

    protected BasePreference(IPreferenceStore store) {
        this.store = store;
    }

    protected IPreferenceStore getPreferenceStore() {
        return store;
    }

    public void save() throws IOException {
        if (getPreferenceStore() instanceof ScopedPreferenceStore) {
            ((ScopedPreferenceStore) getPreferenceStore()).save();
        }
    }

    /**
     * Retrieves a object from the local preference store using the provided
     * preference key; the value retrieved is decoded using JSON based on the
     * provided valueTypeRef.
     * 
     * @param preference
     *            the name of the preference
     * @param valueTypeRef
     * @return
     */
    protected <T extends Object> T getValueUsingJSON(String preference,
            TypeReference<T> valueTypeRef, T defaultValue) {
        ObjectMapper mapper = new ObjectMapper();
        String json = store.getString(preference);
        if (Strings.isNullOrEmpty(json)) {
            return defaultValue;
        }
        try {
            return mapper.readValue(json, valueTypeRef);
        }
        catch (Exception ex) {
            logger.error("An error occurred while reading the preference: " + preference, ex);
        }
        return defaultValue;
    }

    /**
     * Stores the value by encoding the value using JSON; the resuled JSON
     * string is stored in the preference store using the provided preference
     * name.
     * 
     * @param value
     *            the value to be stored in the local preference store
     * @param preference
     *            the name of the preference
     */
    protected <T extends Object> void storeValueUsingJSON(T value, String preference) {
        ObjectMapper mapper = new ObjectMapper();
        StringWriter writer = new StringWriter();
        try {
            mapper.writeValue(writer, value);
            String json = writer.toString();
            store.setValue(preference, json);
        }
        catch (Exception ex) {
            logger.error("An error occurred while storing the preference: " + preference, ex);
        }
    }

    protected <T extends Object> void setDefaultUsingJSON(String preference, T value) {
        ObjectMapper mapper = new ObjectMapper();
        StringWriter writer = new StringWriter();
        try {
            mapper.writeValue(writer, value);
            String json = writer.toString();
            store.setDefault(preference, json);
        }
        catch (Exception ex) {
            logger.error("An error occurred while storing the default value for preference: "
                    + preference, ex);
        }
    }
}
