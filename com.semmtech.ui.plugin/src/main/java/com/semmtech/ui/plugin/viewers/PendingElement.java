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

package com.semmtech.ui.plugin.viewers;


public class PendingElement {
    private final String text;
    private final Object parent;

    public PendingElement() {
        this(null);
    }

    public PendingElement(String text) {
        this(null, text);
    }

    public PendingElement(Object parent) {
        this(parent, Messages.PendingElement_PendingLabel);
    }

    public PendingElement(Object parent, String text) {
        this.text = text;
        this.parent = parent;
    }

    public String getText() {
        return text;
    }

    public Object getParent() {
        return parent;
    }
}
