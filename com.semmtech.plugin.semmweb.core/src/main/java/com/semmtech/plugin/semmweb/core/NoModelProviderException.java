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

package com.semmtech.plugin.semmweb.core;


/**
 * The class <code>NoModelProviderException</code> indicates that no model
 * provider has been found.
 * 
 * @author Mike Henrichs
 */
public class NoModelProviderException extends Exception {
    private static final long serialVersionUID = -6918290750478739924L;

    public NoModelProviderException() {
        super();
    }

    public NoModelProviderException(String message) {
        super(message);
    }

    public NoModelProviderException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoModelProviderException(Throwable cause) {
        super(cause);
    }
}
