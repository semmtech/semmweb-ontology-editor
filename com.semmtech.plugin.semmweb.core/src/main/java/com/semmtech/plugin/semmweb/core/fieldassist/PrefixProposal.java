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

package com.semmtech.plugin.semmweb.core.fieldassist;


import org.eclipse.jface.fieldassist.IContentProposal;


public class PrefixProposal implements IContentProposal {
    private final String prefix;
    private final String namespaceUri;

    public PrefixProposal(String prefix, String namespaceUri) {
        this.prefix = prefix;
        this.namespaceUri = namespaceUri;
    }

    @Override
    public String getContent() {
        return String.format("%s:", prefix);
    }

    @Override
    public int getCursorPosition() {
        return getContent().length();
    }

    @Override
    public String getLabel() {
        return String.format("%s:", prefix);
    }

    @Override
    public String getDescription() {
        return String.format("<%s>", namespaceUri);
    }

}
