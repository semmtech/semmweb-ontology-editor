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
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.model.IModelProvider;


public class ClearInferenceResultsHandler extends AbstractHandler {
    private static final Logger logger = Logger.getLogger(ClearInferenceResultsHandler.class);
    public static final String ID = "com.semmtech.plugin.semmweb.core.commands.clearInferenceResults";

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        logger.debug("execute called!");
        IModelProvider provider = CorePlugin.getDefault().getActiveModelProvider();
        if (provider != null) {
            provider.clearInferredModel();
        }
        return null;
    }

}
