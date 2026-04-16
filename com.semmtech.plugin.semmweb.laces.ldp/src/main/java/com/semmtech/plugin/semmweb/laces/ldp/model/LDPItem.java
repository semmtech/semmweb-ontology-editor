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


/**
 * 
 * @author Sander Stolk
 */
public abstract class LDPItem {
    protected LDPServer server;
    protected LDPItem parent;
    protected String id;
    protected String name;

    protected LDPItem(LDPServer server, String id, String name) {
        this(server, null, id, name);
    }

    protected LDPItem(LDPServer server, LDPItem parent, String id, String name) {
        this.server = server;
        this.id = id;
        this.name = name;
        this.parent = parent;
    }

    public LDPServer getServer() {
        return server;
    }

    public String getID() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LDPItem getParent() {
        return parent;
    }

    public String toString() {
        return name;
    }

    public String getURL() {
        return null;
    }
}
