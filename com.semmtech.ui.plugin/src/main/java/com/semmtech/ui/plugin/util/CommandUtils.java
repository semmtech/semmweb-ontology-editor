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

package com.semmtech.ui.plugin.util;


import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.NotEnabledException;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.services.IServiceLocator;


public class CommandUtils {

    private static final Logger logger = Logger.getLogger(CommandUtils.class);

    public static Object execute(String commandId, Map<String, String> parameters) {
        // Obtain IServiceLocator implementer, e.g. from
        // PlatformUI.getWorkbench():
        IServiceLocator serviceLocator = PlatformUI.getWorkbench();
        // or a site from within a editor or view:
        // IServiceLocator serviceLocator = getSite();

        ICommandService commandService = (ICommandService) serviceLocator
                .getService(ICommandService.class);

        try {
            // Lookup commmand with its ID
            Command command = commandService.getCommand(commandId);

            // Optionally pass a ExecutionEvent instance, default no-param arg
            // creates blank event
            if (parameters == null) {
                return command.executeWithChecks(new ExecutionEvent());
            }

            return command.executeWithChecks(new ExecutionEvent(command, parameters, null, null));
        }
        catch (ExecutionException | NotDefinedException | NotEnabledException | NotHandledException e) {
            logger.error("An error occured while executing the command: " + commandId, e);
        }
        return null;
    }

    public static Object execute(String commandId) {
        return execute(commandId, null);
    }
}
