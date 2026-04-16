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

package com.semmtech.ui.plugin.layouts;


import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;

import com.semmtech.ui.plugin.widgets.Widgets;


/**
 * 
 * @author Sander Stolk
 */
public class SwitchLayout extends StackLayout {
    @Override
    protected Point computeSize(Composite composite, int wHint, int hHint, boolean flushCache) {
        if (Widgets.isNullOrDisposed(topControl)) {
            return new Point(0, 0);
        }

        Point size = topControl.computeSize(wHint, hHint, flushCache);

        int width = size.x + 2 * marginWidth;
        int height = size.y + 2 * marginHeight;

        if (wHint != SWT.DEFAULT) {
            width = wHint;
        }
        if (hHint != SWT.DEFAULT) {
            height = hHint;
        }

        return new Point(width, height);
    }
}
