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

import com.semmtech.plugin.semmweb.dictionary.DictionaryPlugin;
import com.semmtech.plugin.semmweb.dictionary.DictionaryPluginEvent;


/**
 * http://blog.eclipse-tips.com/2009/03/commands-part-6-toggle-radio-menu.html
 * 
 * @author Mike Henrichs
 * 
 */
public class SKOSTopLevelElementsHandler extends AbstractHandler {
    public static final String ID = "com.semmtech.plugin.semmweb.dictionary.commands.skosTopLevelElements";
    private static final Logger logger = Logger.getLogger(SKOSTopLevelElementsHandler.class);

    public static final String STATE_CONCEPTS = "concepts";
    public static final String STATE_CONCEPT_SCHEMES = "conceptSchemes";
    public static final String STATE_COLLECTIONS = "collections";

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        if (HandlerUtil.matchesRadioState(event))
            return null;

        String currentState = event.getParameter(RadioState.PARAMETER_ID);
        if (currentState.equals(STATE_CONCEPT_SCHEMES)) {

        }
        else if (currentState.equals(STATE_CONCEPTS)) {

        }
        else if (currentState.equals(STATE_COLLECTIONS)) {

        }
        HandlerUtil.updateRadioState(event.getCommand(), currentState);
        logger.debug("SKOSCollectionsAsTopHandler -> currentState = " + currentState);

        DictionaryPlugin.getDefault().notifyEvent(
                new DictionaryPluginEvent(DictionaryPluginEvent.SKOS_TOP_ELEMENT_CHANGED));

        return null;
    }

}
