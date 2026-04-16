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

package com.semmtech.plugin.semmweb.core.testers;


import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.State;
import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.RegistryToggleState;
import org.eclipse.ui.services.IServiceLocator;


/**
 * 
 * @author Sander Stolk
 */
public class CommandsPropertyTester extends PropertyTester {
    public static final String TOGGLE_PROPERTY_NAME = "toggle";

    @Override
    public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
        if ((receiver instanceof IServiceLocator) && (args != null) && (args.length > 0)) {
            final IServiceLocator locator = (IServiceLocator) receiver;
            for (Object arg : args) {
                if (!(arg instanceof String)) {
                    return false;
                }

                if (property.equals(TOGGLE_PROPERTY_NAME)) {
                    final String commandId = (String) arg;
                    final ICommandService commandService = (ICommandService) locator
                            .getService(ICommandService.class);
                    final Command command = commandService.getCommand(commandId);
                    final State state = command.getState(RegistryToggleState.STATE_ID);
                    if ((state == null) || !state.getValue().equals(expectedValue)) {
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }

}
