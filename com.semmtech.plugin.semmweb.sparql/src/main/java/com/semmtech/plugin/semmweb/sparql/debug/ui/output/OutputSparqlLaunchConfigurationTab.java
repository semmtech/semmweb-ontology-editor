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

package com.semmtech.plugin.semmweb.sparql.debug.ui.output;


import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import com.semmtech.plugin.semmweb.sparql.SparqlPlugin;
import com.semmtech.plugin.semmweb.sparql.SparqlPluginImages;


public class OutputSparqlLaunchConfigurationTab extends AbstractLaunchConfigurationTab {
    private final OutputFileBlock outputFileBlock;

    public OutputSparqlLaunchConfigurationTab() {
        outputFileBlock = new OutputFileBlock();
    }

    @Override
    public void createControl(Composite parent) {
        Composite comp = new Composite(parent, SWT.NONE);
        comp.setFont(parent.getFont());
        GridLayout layout = new GridLayout();
        layout.numColumns = 1;
        comp.setLayout(layout);
        setControl(comp);

        outputFileBlock.createControl(comp);
    }

    @Override
    public void dispose() {
        outputFileBlock.dispose();
    }

    @Override
    public void activated(ILaunchConfigurationWorkingCopy workingCopy) {
        outputFileBlock.activated(workingCopy);
    }

    @Override
    public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
        outputFileBlock.setDefaults(configuration);
    }

    @Override
    public void initializeFrom(ILaunchConfiguration configuration) {
        outputFileBlock.initializeFrom(configuration);
    }

    @Override
    public void performApply(ILaunchConfigurationWorkingCopy configuration) {
        outputFileBlock.performApply(configuration);
    }

    @Override
    public void setLaunchConfigurationDialog(ILaunchConfigurationDialog dialog) {
        outputFileBlock.setLaunchConfigurationDialog(dialog);
    }

    @Override
    public boolean isValid(ILaunchConfiguration configuration) {
        return outputFileBlock.isValid(configuration);
    }

    @Override
    public String getName() {
        return "Ouput";
    }

    @Override
    public Image getImage() {
        return SparqlPlugin.getDefault().getImage(SparqlPluginImages.IMG_OUTPUT);
    }

}
