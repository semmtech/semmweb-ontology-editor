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

package com.semmtech.plugin.semmweb.core;


import java.util.Map;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.internal.win32.OS;
import org.eclipse.swt.widgets.Display;

import com.google.common.collect.Maps;


/**
 * This class containing defined RGB colors.
 * 
 * @author Mike Henrichs
 * 
 */
@SuppressWarnings("restriction")
public class Colors {
    public static final RGB RGB_SEMMTECH_BLUE = new RGB(31, 101, 175);
    public static final RGB RGB_SEMMTECH_LIGHT_BLUE = new RGB(25, 105, 187);

    public static final int BLACK = 0;
    public static final int SEMMTECH_BLUE = 1;
    public static final int SEMMTECH_LIGHT_BLUE = 2;
    public static final int SWT_LINK_BLUE = 3;
    public static final int LIGHT_GRAY = 4;
    public static final int DARK_GRAY = 5;

    private static final RGB RGB_SWT_LINK_BLUE = new RGB(0, 51, 153);
    private static final RGB RGB_LIGHT_GRAY = new RGB(175, 175, 175);
    private static final RGB RGB_DARK_GRAY = new RGB(100, 100, 100);

    private static final Map<Integer, Color> colorMapping = Maps.newHashMap();

    private Colors() {
    }

    public static Color getColor(int color) {
        Integer colorInteger = new Integer(color);

        if (colorMapping.get(colorInteger) == null) {
            RGB rgb = new RGB(0, 0, 0);

            switch (color) {
            case SEMMTECH_BLUE:
                rgb = RGB_SEMMTECH_BLUE;
                break;
            case SEMMTECH_LIGHT_BLUE:
                rgb = RGB_SEMMTECH_LIGHT_BLUE;
                break;
            case LIGHT_GRAY:
                rgb = RGB_LIGHT_GRAY;
                break;
            case DARK_GRAY:
                rgb = RGB_DARK_GRAY;
                break;
            case SWT_LINK_BLUE:
                colorMapping.put(colorInteger, getSwtLinkColor());
                return colorMapping.get(colorInteger);
            default:
                color = 0;
                break;
            }
            if (colorMapping.get(colorInteger) == null) {
                Color c = new Color(Display.getDefault(), rgb);
                colorMapping.put(colorInteger, c);
            }
        }

        return colorMapping.get(colorInteger);
    }

    private static Color getSwtLinkColor() {
        Color linkColor;
        if (!OS.IsWinCE && OS.WIN32_VERSION >= OS.VERSION(4, 10)) {
            linkColor = Color.win32_new(Display.getDefault(), OS.GetSysColor(OS.COLOR_HOTLIGHT));
        }
        else {
            linkColor = new Color(Display.getDefault(), RGB_SWT_LINK_BLUE);
        }
        return linkColor;
    }
}
