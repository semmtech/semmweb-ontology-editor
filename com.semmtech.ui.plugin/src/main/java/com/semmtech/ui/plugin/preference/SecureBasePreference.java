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
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.SecurePreferencesFactory;
import org.eclipse.equinox.security.storage.StorageException;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;


/**
 * Save the preferences into an encrypted storage located in the user's home
 * (.eclipse\org.eclipse.equinox.security\secure_storage). These preferences are
 * global and visible by all workspaces.
 * 
 * A primitive vent mechanism has been implemented since seems that it isn't
 * provided by the secure storage interface.
 * 
 * @author Simone Rondelli
 * 
 */
public class SecureBasePreference {

    private static final Logger logger = Logger.getLogger(SecureBasePreference.class);

    private ISecurePreferences store;

    // TODO: maybe is better to implement a global mechanism
    private List<ISecurePreferenceChangeListener> listeners;

    protected SecureBasePreference(String pluginId) {
        ISecurePreferences preferences = SecurePreferencesFactory.getDefault();
        store = preferences.node(pluginId);
        listeners = Lists.newArrayList();
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
     * @throws StorageException
     */
    protected <T extends Object> T getValueUsingJSON(String preference,
            TypeReference<T> valueTypeRef, T defaultValue) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String json = store.get(preference, null);
            if (Strings.isNullOrEmpty(json)) {
                return defaultValue;
            }

            return mapper.readValue(json, valueTypeRef);
        }
        catch (Exception ex) {
            logger.error("An error occurred while reading the encrypted preference: " + preference,
                    ex);
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
            store.put(preference, json, true);

            SecurePreferenceChangeEvent event = new SecurePreferenceChangeEvent(preference, value);

            for (ISecurePreferenceChangeListener listener : listeners) {
                listener.preferenceChange(event);
            }
        }
        catch (Exception ex) {
            logger.error("An error occurred while storing the encrypted preference: " + preference,
                    ex);
        }
    }

    public void flush() throws IOException {
        store.flush();
    }

    public void addListener(ISecurePreferenceChangeListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public boolean removeListener(ISecurePreferenceChangeListener listener) {
        return listeners.remove(listener);
    }

}
