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

package com.semmtech.plugin.semmweb.core.preferences;


import com.semmtech.jena.readers.JenaReadersUtil;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.ui.plugin.preference.BasePreference;


/**
 * 
 * @author Sander Stolk
 */
public class JenaPreference extends BasePreference {
    public static final String PREFERENCE_CONNECTION_TIMEOUT = "com.semmtech.jena.readers.connectionTimeout";
    public static int DEFAULT_CONNECTION_TIMEOUT = 10000;

    private static final JenaPreference instance = new JenaPreference();

    private JenaPreference() {
        super(CorePlugin.getDefault().getPreferenceStore());
    }

    public static void setConnectionTimeout(int value) {
        instance.getPreferenceStore().setValue(PREFERENCE_CONNECTION_TIMEOUT, value);
        JenaReadersUtil.setConnectionTimeout(value);
    }

    public static int getConnectionTimeout() {
        return instance.getPreferenceStore().getInt(PREFERENCE_CONNECTION_TIMEOUT);
    }

    public static void setDefaults() {
        instance.getPreferenceStore().setDefault(PREFERENCE_CONNECTION_TIMEOUT,
                DEFAULT_CONNECTION_TIMEOUT);
    }
}
