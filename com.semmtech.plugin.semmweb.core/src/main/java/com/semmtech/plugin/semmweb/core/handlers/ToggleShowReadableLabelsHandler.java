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

package com.semmtech.plugin.semmweb.core.handlers;


import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.commands.IElementUpdater;
import org.eclipse.ui.menus.UIElement;

import com.semmtech.plugin.semmweb.core.preferences.LabelsPreference;


public class ToggleShowReadableLabelsHandler extends AbstractHandler implements IElementUpdater {
    public static final String ID = "com.semmtech.plugin.semmweb.core.commands.toggleShowReadableLabels";

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        if (LabelsPreference.showReadableLabels())
            LabelsPreference.setResourceLabelRendering(LabelsPreference.VALUE_SHOW_RESOURCE_QNAMES);
        else
            LabelsPreference.setResourceLabelRendering(LabelsPreference.VALUE_SHOW_READABLE_LABELS);
        return null;
    }

    @Override
    public void updateElement(UIElement element, @SuppressWarnings("rawtypes") Map parameters) {
        element.setChecked(LabelsPreference.showReadableLabels());
    }
}