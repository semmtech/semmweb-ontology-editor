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

import tech.laces.ldp.api.message.content.LDPRepositoryInfo;


/**
 * 
 * @author Sander Stolk
 */
public class LDPRepository extends LDPItem {
    public LDPRepositoryInfo fullInfo;

    public LDPRepository(LDPGroup parent, LDPRepositoryInfo repoInfo) {
        this(parent, repoInfo.id, repoInfo.name);
        this.fullInfo = repoInfo;
    }

    public LDPRepository(LDPGroup parent, String id, String name) {
        super(parent.getServer(), parent, id, name);
    }

    public LDPRepository(LDPServer server, LDPRepositoryInfo repoInfo) {
        this(server, repoInfo.id, repoInfo.name);
        this.fullInfo = repoInfo;
    }

    public LDPRepository(LDPServer server, String id, String name) {
        super(server, null, id, name);
    }

    public LDPPublication[] listPublications() {
        return server.listPublications(this);
    }

    @Override
    public String getURL() {
        if (fullInfo != null) {
            return getServer().getServerUrl() + "/" + fullInfo.path;
        }
        return null;
    }

    public static LDPRepository[] createArray(LDPServer server, List<LDPRepositoryInfo> repoInfo) {
        if (server == null || repoInfo == null) {
            return new LDPRepository[0];
        }

        LDPRepository[] repos = new LDPRepository[repoInfo.size()];
        for (int i = 0; i < repoInfo.size(); i++) {
            repos[i] = new LDPRepository(server, repoInfo.get(i));
        }
        return repos;
    }

    public static LDPRepository[] createArray(LDPGroup group, List<LDPRepositoryInfo> repoInfo) {
        if (group == null || repoInfo == null) {
            return new LDPRepository[0];
        }

        LDPRepository[] repos = new LDPRepository[repoInfo.size()];
        for (int i = 0; i < repoInfo.size(); i++) {
            repos[i] = new LDPRepository(group, repoInfo.get(i));
        }
        return repos;
    }
}
