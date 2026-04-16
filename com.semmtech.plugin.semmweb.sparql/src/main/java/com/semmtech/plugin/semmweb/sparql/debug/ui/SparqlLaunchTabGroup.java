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


import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;

import com.semmtech.plugin.semmweb.sparql.debug.ui.context.ContextLaunchConfigurationTab;
import com.semmtech.plugin.semmweb.sparql.debug.ui.main.MainSparqlLaunchConfigurationTab;
import com.semmtech.plugin.semmweb.sparql.debug.ui.output.OutputSparqlLaunchConfigurationTab;


public class SparqlLaunchTabGroup extends AbstractLaunchConfigurationTabGroup {

    public SparqlLaunchTabGroup() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public void createTabs(ILaunchConfigurationDialog dialog, String mode) {
        ILaunchConfigurationTab[] tabs = new ILaunchConfigurationTab[] {
                new MainSparqlLaunchConfigurationTab(), new OutputSparqlLaunchConfigurationTab(),
                new ContextLaunchConfigurationTab(), new CommonTab() };
        setTabs(tabs);
    }
}
