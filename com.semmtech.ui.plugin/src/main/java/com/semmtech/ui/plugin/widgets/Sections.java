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

package com.semmtech.ui.plugin.widgets;


import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.forms.widgets.Section;


public final class Sections {
    private Sections() {
    }

    public static ToolBarManager addToolbarAction(Section section, ImageDescriptor imageDescriptor,
            Action action) {
        ToolBarManager toolBarManager = createToolbarManager(section);
        addToolbarAction(toolBarManager, imageDescriptor, action);
        return toolBarManager;
    }

    public static ToolBarManager createToolbarManager(Section section) {
        ToolBarManager toolBarManager = new ToolBarManager(SWT.FLAT);
        ToolBar toolbar = toolBarManager.createControl(section);
        toolbar.setCursor(Display.getDefault().getSystemCursor(SWT.CURSOR_HAND));
        section.setTextClient(toolbar);
        return toolBarManager;
    }

    public static void addToolbarAction(ToolBarManager toolBarManager,
            ImageDescriptor imageDescriptor, Action action) {
        action.setImageDescriptor(imageDescriptor);
        toolBarManager.add(action);
        toolBarManager.update(true);
    }
}
