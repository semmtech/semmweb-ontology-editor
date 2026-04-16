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

package com.semmtech.plugin.semmweb.core.wizards;


/**
 * Listener used together with the {@link CheckNamespaceUsageOperation}.
 * 
 * @author Mike Henrichs
 * 
 */
public interface ICheckNamespaceUsageListener {
    /**
     * Called if the given URI is used in at least another model.
     * 
     * @param errorUri
     *            The URI on which the check failed
     * @return true if the error should not stop any further checks
     */
    boolean resumeOnError(String errorUri);

    void checkCompleted();

}
