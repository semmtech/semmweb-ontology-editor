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

package com.semmtech.plugin.semmweb.core.handlers;


import org.apache.log4j.Logger;
import org.eclipse.ui.statushandlers.AbstractStatusHandler;
import org.eclipse.ui.statushandlers.StatusAdapter;


/**
 * TODO: See http://wiki.eclipse.org/Platform_UI_Error_Handling The intended
 * goal of the status handler is to prevent "scary" error dialogs to popup to
 * the user, but if possible log them in a separate log file (log4j
 * FileAppender).
 * 
 * @author Mike Henrichs
 * 
 */
public class CoreStatusHandler extends AbstractStatusHandler {
    private static Logger logger = Logger.getLogger(CoreStatusHandler.class);

    public CoreStatusHandler() {

    }

    @Override
    public void handle(StatusAdapter statusAdapter, int style) {
        logger.debug("Handling something: { status: { code: \""
                + statusAdapter.getStatus().getCode() + "\", message: \""
                + statusAdapter.getStatus().getMessage() + "\" }, style: " + style + "} ");
    }

}
