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

package com.semmtech.plugin.semmweb.core.internal;


import java.util.Map;

import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.AbstractSourceProvider;
import org.eclipse.ui.PlatformUI;

import com.google.common.collect.Maps;


public class CoreExpressionSourceProvider extends AbstractSourceProvider {

    public CoreExpressionSourceProvider() {
    }

    @Override
    public void dispose() {

    }

    @Override
    public Map<Object, Object> getCurrentState() {
        Map<Object, Object> result = Maps.newHashMap();
        result.put("com.semmtech.plugin.semmweb.core.workbench", PlatformUI.getWorkbench());
        result.put("com.semmtech.plugin.semmweb.core.platform", Platform.class);
        return result;
    }

    @Override
    public String[] getProvidedSourceNames() {
        return new String[] { "com.semmtech.plugin.semmweb.core.workbench",
                "com.semmtech.plugin.semmweb.core.platform" };
    }

}
