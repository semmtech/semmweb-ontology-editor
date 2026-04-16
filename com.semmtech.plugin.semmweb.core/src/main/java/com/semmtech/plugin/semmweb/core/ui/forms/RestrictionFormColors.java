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

package com.semmtech.plugin.semmweb.core.ui.forms;


import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;


public class RestrictionFormColors extends EditorFormColors {

    public RestrictionFormColors(Display display) {
        super(display);
    }

    @Override
    protected void initialize() {
        super.initialize();
        // / Just not white:
        // background = new Color(Display.getDefault(), 242, 246, 249);
        background = new Color(Display.getDefault(), 255, 255, 255);
        createColor(IRestrictionFormColors.HEADER_FOREGROUND, 80, 80, 80);
    }

    /**
     * Allocates colors for the following keys: BORDER, SEPARATOR and TITLE.
     */
    @Override
    protected void initializeColorTable() {
        // TODO Auto-generated method stub
        super.initializeColorTable();
    }

    /**
     * Allocates additional colors for the form header, namely background
     * gradients, bottom separator keylines and DND highlights.
     */
    @Override
    protected void initializeFormHeaderColors() {
        // TODO Auto-generated method stub
        super.initializeFormHeaderColors();
    }

    /**
     * Allocates colors for the section tool bar (all the keys that start with
     * TB).
     */
    @Override
    public void initializeSectionToolBarColors() {
        // TODO Auto-generated method stub
        super.initializeSectionToolBarColors();
    }
}
