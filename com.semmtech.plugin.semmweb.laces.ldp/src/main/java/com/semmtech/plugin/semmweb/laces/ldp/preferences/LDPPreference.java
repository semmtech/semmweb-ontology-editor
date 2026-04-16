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

package com.semmtech.plugin.semmweb.laces.ldp.preferences;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Lists;
import com.semmtech.plugin.semmweb.laces.ldp.LDPPlugin;
import com.semmtech.plugin.semmweb.laces.ldp.model.LDPServer;
import com.semmtech.ui.plugin.preference.BasePreference;


/**
 * 
 * @author Mike Henrichs
 * @author Sander Stolk
 */
public class LDPPreference extends BasePreference {
    public static final String PREFERENCE_LDP_SERVERS = "com.semmtech.plugin.semmweb.laces.ldp.preferences.servers";
    public static final String LDP_SERVER_URL = "https://hub.laces.tech";

    private static final LDPPreference instance = new LDPPreference();

    private LDPPreference() {
        super(LDPPlugin.getDefault().getPreferenceStore());
    }

    public static List<LDPServer> getServers() {
        return instance.getValueUsingJSON(PREFERENCE_LDP_SERVERS,
                new TypeReference<List<LDPServer>>() {
                }, new ArrayList<LDPServer>());
    }

    public static void setServers(Collection<LDPServer> servers) {
        instance.storeValueUsingJSON(servers, PREFERENCE_LDP_SERVERS);
    }

    public static void setDefaults() {
        List<LDPServer> servers = Lists.newArrayList();
        servers.add(new LDPServer(LDP_SERVER_URL, "", ""));
        instance.setDefaultUsingJSON(PREFERENCE_LDP_SERVERS, servers);
    }
}
