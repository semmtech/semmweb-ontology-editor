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

package com.semmtech.plugin.utils.colortheme.commands;


import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import com.github.eclipsecolortheme.ColorTheme;
import com.github.eclipsecolortheme.ColorThemeManager;


public class SwitchColorthemeHandler extends AbstractHandler {

    public static final String ID = "com.semmtech.plugin.utils.colortheme.commands.switchTheme";

    public static final String PARAMETER_THEME_ID = "com.semmtech.plugin.utils.colortheme.themeId";

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        String themeId = event.getParameter(PARAMETER_THEME_ID);
        ColorThemeManager manager = new ColorThemeManager();
        if (themeId != null && themeId.length() > 0) {
            String selectedTheme = "Default";
            for (ColorTheme theme : manager.getThemes()) {
                if (theme.getId().equals(themeId)) {
                    selectedTheme = theme.getName();
                    break;
                }
            }
            if (selectedTheme != null) {
                System.out.println(String.format("Applied theme \"%s\"", selectedTheme));
                manager.applyTheme(selectedTheme);
            }
        }
        return null;
    }
}
