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

package com.semmtech.plugin.semmweb.sparql.debug.ui;


import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorPart;

import com.google.common.collect.Lists;


public class SparqlLaunchShortcut implements ILaunchShortcut {

    @Override
    public void launch(ISelection selection, String mode) {
        if (selection instanceof StructuredSelection) {
            System.out.println("launch -> selection.toString()" + selection.toString());
        }
    }

    @Override
    public void launch(IEditorPart editor, String mode) {
        System.out.println("launch -> editor.toString()" + editor.toString());
    }

    protected void launch(String sparql, String mode) {
        List<ILaunchConfiguration> configs = getCandidates(getConfigurationType());
        if (configs != null) {
            ILaunchConfiguration config = null;
            int count = configs.size();
            if (count == 1) {
                config = configs.get(0);
            }
            else if (count > 1) {
                config = chooseConfiguration(configs);
                if (config == null) {
                    return;
                }
            }
            if (config == null) {
                config = createConfiguration(sparql);
            }
            if (config != null) {
                DebugUITools.launch(config, mode);
            }
        }
    }

    private ILaunchConfiguration createConfiguration(String sparql) {
        ILaunchConfiguration config = null;
        try {
            ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
            ILaunchConfigurationType configType = manager
                    .getLaunchConfigurationType(SparqlLaunchConfigurationConstants.ID_SPARQL_QUERY);
            ILaunchConfigurationWorkingCopy copy = configType.newInstance(null,
                    manager.generateLaunchConfigurationName("TODO_Prefix"));
            // copy.setAttribute(SparqlLaunchConfigurationConstants.ATTR_TARGET_URI,
            // "temp");
            config = copy.doSave();
        }
        catch (CoreException ex) {
            ex.printStackTrace();
        }
        return config;
    }

    private ILaunchConfigurationType getConfigurationType() {
        ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
        return manager
                .getLaunchConfigurationType(SparqlLaunchConfigurationConstants.ID_SPARQL_QUERY);
    }

    private ILaunchConfiguration chooseConfiguration(List<ILaunchConfiguration> configs) {
        // TODO
        return configs.get(0);
    }

    private List<ILaunchConfiguration> getCandidates(ILaunchConfigurationType type) {
        List<ILaunchConfiguration> candidates = Lists.newArrayList();
        try {
            ILaunchConfiguration[] configs = DebugPlugin.getDefault().getLaunchManager()
                    .getLaunchConfigurations(type);
            for (ILaunchConfiguration config : configs) {
                if (config.getAttribute(SparqlLaunchConfigurationConstants.ATTR_QUERY_FILE, "")
                        .equals("TODO")) {
                    candidates.add(config);
                }
            }
        }
        catch (CoreException ex) {
            ex.printStackTrace();
        }
        return candidates;
    }
}
