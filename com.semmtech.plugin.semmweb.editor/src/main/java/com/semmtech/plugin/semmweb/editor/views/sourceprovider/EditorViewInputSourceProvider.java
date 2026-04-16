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

package com.semmtech.plugin.semmweb.editor.views.sourceprovider;


import java.util.Map;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.AbstractSourceProvider;
import org.eclipse.ui.ISources;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;


public class EditorViewInputSourceProvider extends AbstractSourceProvider {

    public static final String INPUT_INSTANCES_VIEW = "com.semmtech.plugin.semmweb.editor.views.sourceprovider.inputInstancesView";

    private final Map<String, IStructuredSelection> states;

    public EditorViewInputSourceProvider() {
        states = Maps.newHashMap();
    }

    @Override
    public void dispose() {
    }

    @Override
    public Map<String, IStructuredSelection> getCurrentState() {
        return states;
    }

    @Override
    public String[] getProvidedSourceNames() {
        return new String[] { INPUT_INSTANCES_VIEW };
    }

    public void setState(String inputName, IStructuredSelection inputValue) {
        if (!Strings.isNullOrEmpty(inputName)) {
            IStructuredSelection currentValue = states.get(inputName);
            if (currentValue == null && inputValue == null) {
                return;
            }
            if (currentValue != null && inputValue != null && inputValue.equals(currentValue)) {
                return;
            }
            states.put(inputName, inputValue);
            fireSourceChanged(ISources.WORKBENCH, inputName, inputValue);
        }
    }
}
