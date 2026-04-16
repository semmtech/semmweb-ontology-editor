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

package com.semmtech.plugin.semmweb.core.preferences;


import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

import com.fasterxml.jackson.core.type.TypeReference;
import com.semmtech.ui.plugin.preference.BasePreference;


public class ImportNotificationsPreference extends BasePreference {
    private static final String PREFERENCE_QUALIFIER = "warning.settings";
    public static final String PREFERENCE_IGNORE_URIS = "com.semmtech.plugin.semmweb.core.preferences.importNotifications.ignoreUris";

    protected ImportNotificationsPreference(IPreferenceStore store) {
        super(store);
    }

    public Set<String> getIgnoreNotificationURIs() {
        return getValueUsingJSON(PREFERENCE_IGNORE_URIS, new TypeReference<Set<String>>() {
        }, new HashSet<String>());
    }

    public boolean ignoreNotificationForURI(String uri) {
        Set<String> uris = getIgnoreNotificationURIs();
        return uris.contains(uri);
    }

    public void addIgnoreNotificationURI(String uri) {
        Set<String> uris = getIgnoreNotificationURIs();
        if (!uris.contains(uri)) {
            uris.add(uri);
            setIgnoreNotificationsURIs(uris);
        }
    }

    public void setIgnoreNotificationsURIs(Set<String> uris) {
        storeValueUsingJSON(uris, PREFERENCE_IGNORE_URIS);
    }

    public static ImportNotificationsPreference fromProject(IProject project) {
        if (project != null) {
            ProjectScope scope = new ProjectScope(project);
            ScopedPreferenceStore store = new ScopedPreferenceStore(scope, PREFERENCE_QUALIFIER);
            return new ImportNotificationsPreference(store);
        }
        return new ImportNotificationsPreference(new PreferenceStore());
    }

    public static ImportNotificationsPreference fromPreferenceStore(IPreferenceStore store) {
        return new ImportNotificationsPreference(store);
    }
}
