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

package com.semmtech.plugin.semmweb.laces.ldp.model;


import java.util.List;

import tech.laces.ldp.LDPv4Client;
import tech.laces.ldp.api.message.content.LDPPublicationInfo;


/**
 * 
 * @author Sander Stolk
 */
public class LDPPublication extends LDPItem {
    public LDPPublicationInfo fullInfo;
    protected String uri;

    public LDPPublication(LDPRepository parent, LDPPublicationInfo pubInfo) {
        this(parent, pubInfo.id, pubInfo.name, pubInfo.uri);
        fullInfo = pubInfo;
    }

    public LDPPublication(LDPRepository parent, String id, String name, String uri) {
        super(parent.getServer(), parent, id, name);
        // URI may need turning a relative path into a full
        if (!uri.startsWith("http")) {
            uri = LDPv4Client.DEFAULT_SERVER_URL.replace("https", "http") + uri;
        }
        this.uri = uri;
    }

    public String getURI() {
        return uri;
    }

    @Override
    public String getURL() {
        return uri;
    }

    public String getURLwithoutVersionSegment() {
        int versionsIndex = uri.lastIndexOf("/versions/");
        return uri.substring(0, versionsIndex == -1 ? uri.length() : versionsIndex);
    }

    public static LDPPublication[] createArray(LDPRepository repo, List<LDPPublicationInfo> pubInfo) {
        if (repo == null || pubInfo == null) {
            return new LDPPublication[0];
        }

        LDPPublication[] pubs = new LDPPublication[pubInfo.size()];
        for (int i = 0; i < pubInfo.size(); i++) {
            pubs[i] = new LDPPublication(repo, pubInfo.get(i));
        }
        return pubs;
    }
}
