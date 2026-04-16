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


import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.FormColors;


public class EditorFormColors extends FormColors {

    public EditorFormColors(Display display) {
        super(display);
    }

    @Override
    protected void initialize() {
        super.initialize();
        initializeResourceEditor();
    }

    private void initializeResourceEditor() {
        createColor(IEditorFormColors.ERROR_BORDER, 255, 0, 0);
        createColor(IEditorFormColors.MODIFY_BORDER, 31, 101, 175);
        createColor(IEditorFormColors.PROPERTY_PART_FOREGROUND, 25, 76, 127);
        createColor(IEditorFormColors.TEXT_BG, 255, 255, 255);
        createColor(IEditorFormColors.WHITE, 255, 255, 255);
        createColor(IEditorFormColors.BLACK, 0, 0, 0);
    }
}
