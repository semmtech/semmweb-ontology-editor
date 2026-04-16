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

package com.semmtech.plugin.semmweb.core.commands;


import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.Parameterization;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.core.commands.State;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.handlers.RegistryToggleState;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;
import org.eclipse.ui.services.IServiceLocator;

import com.google.common.collect.Lists;
import com.semmtech.plugin.semmweb.core.CorePlugin;


public class Commands {
    public static void setToggleState(String id, boolean enabled) {
        ICommandService commandService = (ICommandService) CorePlugin.getActiveWorkbenchWindow()
                .getService(ICommandService.class);
        Command createCommand = commandService.getCommand(id);

        Boolean newStateValue = new Boolean(enabled);

        State state = createCommand.getState(RegistryToggleState.STATE_ID);
        if (state == null) {
            State newState = new State();
            newState.setValue(newStateValue);
            createCommand.addState(RegistryToggleState.STATE_ID, newState);
        }
        else {
            state.setValue(newStateValue);
        }
    }

    public static boolean getToggleState(String id) {
        ICommandService commandService = (ICommandService) CorePlugin.getActiveWorkbenchWindow()
                .getService(ICommandService.class);
        Command createCommand = commandService.getCommand(id);

        State state = createCommand.getState(RegistryToggleState.STATE_ID);
        if (state != null) {
            Object stateValue = state.getValue();
            if (stateValue instanceof Boolean) {
                return ((Boolean) stateValue).booleanValue();
            }
        }
        return false;
    }

    public static void refreshElements(String id) {
        ICommandService commandService = (ICommandService) CorePlugin.getActiveWorkbenchWindow()
                .getService(ICommandService.class);
        commandService.refreshElements(id, null);
    }

    public static boolean isEnabled(String id) {
        try {
            ICommandService commandService = (ICommandService) CorePlugin
                    .getActiveWorkbenchWindow().getService(ICommandService.class);
            Command command = commandService.getCommand(id);
            return command.isEnabled();
        }
        catch (Exception ex) {
            return false;
        }
    }

    public static Object execute(String id, Map<String, String> parameters) {
        try {
            List<Parameterization> param = Lists.newArrayList();

            ICommandService commandService = (ICommandService) CorePlugin
                    .getActiveWorkbenchWindow().getService(ICommandService.class);
            Command command = commandService.getCommand(id);

            if (parameters != null) {
                for (String key : parameters.keySet()) {
                    param.add(new Parameterization(command.getParameter(key), parameters.get(key)));
                }
            }

            ParameterizedCommand parameterizedCommand = new ParameterizedCommand(command,
                    param.toArray(new Parameterization[0]));
            IHandlerService handlerService = (IHandlerService) CorePlugin
                    .getActiveWorkbenchWindow().getService(IHandlerService.class);
            Object result = handlerService.executeCommand(parameterizedCommand, null);
            commandService.refreshElements(id, null);
            return result;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static Object execute(String id) {
        return execute(id, null);
    }

    public static IContributionItem createContributionItem(IServiceLocator serviceLocator,
            String commandID, String label) {
        return createContributionItem(serviceLocator, commandID, label, null);
    }

    public static IContributionItem createContributionItem(IServiceLocator serviceLocator,
            String commandID, String label, ImageDescriptor image) {
        CommandContributionItemParameter parameters = new CommandContributionItemParameter(
                serviceLocator, null, commandID, SWT.PUSH);
        parameters.label = label;
        if (image != null) {
            parameters.icon = image;
        }
        return new CommandContributionItem(parameters);
    }

    public static Command getCommand(String commandId) {
        ICommandService commandService = (ICommandService) CorePlugin.getActiveWorkbenchWindow()
                .getService(ICommandService.class);
        return commandService.getCommand(commandId);
    }
}
