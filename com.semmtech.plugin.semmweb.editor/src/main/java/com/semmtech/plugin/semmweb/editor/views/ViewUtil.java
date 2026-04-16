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

package com.semmtech.plugin.semmweb.editor.views;


import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;
import org.eclipse.ui.services.IServiceLocator;


public class ViewUtil {

    /**
     * Creates a IContributionItem from the given command Id; with the given
     * label.
     * 
     * @param commandID
     *            CommandId of the command
     * @param label
     *            Label of the new menu item
     * @return
     */
    public static IContributionItem createContributionItem(IServiceLocator serviceLocator,
            String commandID, String label) {
        return createContributionItem(serviceLocator, commandID, label, null);
    }

    public static IContributionItem createContributionItem(IServiceLocator serviceLocator,
            String commandID, String label, ImageDescriptor image) {
        CommandContributionItemParameter parameters = new CommandContributionItemParameter(
                serviceLocator, null, commandID, SWT.PUSH);
        parameters.label = label;
        if (image != null)
            parameters.icon = image;
        return new CommandContributionItem(parameters);
    }
}
