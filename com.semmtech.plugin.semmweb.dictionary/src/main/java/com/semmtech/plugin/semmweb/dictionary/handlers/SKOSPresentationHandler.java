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

package com.semmtech.plugin.semmweb.dictionary.handlers;


import org.apache.log4j.Logger;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.handlers.RadioState;


/**
 * See
 * http://blog.eclipse-tips.com/2009/03/commands-part-6-toggle-radio-menu.html
 * 
 * @author Mike Henrichs
 * 
 */
public class SKOSPresentationHandler extends AbstractHandler {
    public static final String ID = "com.semmtech.plugin.semmweb.dictionary.commands.skosPresentation";
    private static final Logger logger = Logger.getLogger(SKOSPresentationHandler.class);

    public static final String STATE_ALPHABETICAL = "alphabetical";
    public static final String STATE_HIERARCHICAL = "hierarchical";

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        // Command command = event.getCommand();
        // boolean oldValue = HandlerUtil.toggleCommandState(command);
        // logger.debug("SKOSPresentationHandler -> oldValue = " + oldValue);
        // return null;

        if (HandlerUtil.matchesRadioState(event)) {
            return null;
        }

        String currentState = event.getParameter(RadioState.PARAMETER_ID);
        if (currentState.equals(STATE_ALPHABETICAL)) {

        }
        else if (currentState.equals(STATE_HIERARCHICAL)) {

        }

        HandlerUtil.updateRadioState(event.getCommand(), currentState);
        logger.debug("SKOSPresentationHandler -> currentState = " + currentState);

        return null;
    }

}
