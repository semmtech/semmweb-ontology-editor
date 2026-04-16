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

import tech.laces.ldp.api.message.content.LDPGroupInfo;


/**
 * 
 * @author Sander Stolk
 */
public class LDPGroup extends LDPItem {
    public LDPGroupInfo fullInfo;

    public LDPGroup(LDPServer server, LDPGroupInfo groupInfo) {
        this(server, groupInfo.id, groupInfo.name);
        this.fullInfo = groupInfo;
    }

    public LDPGroup(LDPServer server, String id, String name) {
        super(server, null, id, name);
    }

    public LDPGroup[] listSubGroups() {
        return server.listGroups(this);
    }

    public LDPRepository[] listRepositories() {
        return server.listRepositories(this);
    }

    @Override
    public String getURL() {
        if (fullInfo != null) {
            return getServer().getServerUrl() + "/" + fullInfo.path;
        }
        return null;
    }

    public static LDPGroup[] createArray(LDPServer server, List<LDPGroupInfo> groupInfo) {
        if (server == null || groupInfo == null) {
            return new LDPGroup[0];
        }

        LDPGroup[] groups = new LDPGroup[groupInfo.size()];
        for (int i = 0; i < groupInfo.size(); i++) {
            groups[i] = new LDPGroup(server, groupInfo.get(i));
        }
        return groups;
    }
}
