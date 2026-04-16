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


import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;


public final class FontUtil {

    private FontUtil() {
    }

    /**
     * Returns the modified font data which contains the additionalStyle for
     * each element.
     * 
     * @param originalData
     * @param additionalStyle
     * @return
     */
    public static FontData[] getModifiedFontData(FontData[] originalData, int additionalStyle) {
        FontData[] styleData = new FontData[originalData.length];
        for (int i = 0; i < styleData.length; i++) {
            FontData base = originalData[i];
            styleData[i] = new FontData(base.getName(), base.getHeight(), base.getStyle()
                    | additionalStyle);
        }
        return styleData;
    }

    /**
     * Returns the font with the modified style, based on the font of the
     * provided control.
     * 
     * @param control
     *            control containing the original font
     * @param additionalStyle
     *            the modified style bits
     * @return the modified font
     */
    public static Font getModifiedFont(Control control, int additionalStyle) {
        FontData[] fontData = getModifiedFontData(control.getFont().getFontData(), additionalStyle);
        return new Font(Display.getCurrent(), fontData);
    }
}
