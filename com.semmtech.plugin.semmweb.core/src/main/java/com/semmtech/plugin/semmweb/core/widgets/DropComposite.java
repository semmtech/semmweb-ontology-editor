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

package com.semmtech.plugin.semmweb.core.widgets;


import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.CorePluginImages;


public class DropComposite extends Composite {

    public DropComposite(Composite parent, String dropElementName, boolean small) {
        super(parent, SWT.NONE);

        String instruction = (small) ? String.format("Drop a %s onto this area", dropElementName)
                : String.format("Drop a %s into this view", dropElementName);
        String tooltip = String.format("Drop a %s onto this area to view and modify its behavior.",
                dropElementName);

        initComposite(instruction, tooltip, small);
    }

    public DropComposite(Composite parent, String instruction, String tooltip, boolean small) {
        super(parent, SWT.NONE);
        initComposite(instruction, tooltip, small);
    }

    private void initComposite(String instruction, String tooltip, boolean small) {
        setToolTipText(tooltip);

        if (small) {
            GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(false).spacing(7, 0)
                    .extendedMargins(11, 9, 5, 5).applyTo(this);
        }
        else {
            GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(false).margins(11, 9)
                    .spacing(7, 0).applyTo(this);
        }

        Label icon = new Label(this, SWT.NONE);
        icon.setToolTipText(tooltip);
        icon.setImage(CorePlugin.getDefault().getImage(CorePluginImages.IMG_DROP_INTO));

        Label label = new Label(this, SWT.NONE);
        label.setText(instruction);
        label.setToolTipText(tooltip);
        GridDataFactory.fillDefaults().align(GridData.BEGINNING, GridData.END).grab(false, false)
                .applyTo(label);
    }

    public void setDefaultGridLayoutData(boolean small) {
        if (small) {
            GridDataFactory.fillDefaults().align(GridData.FILL, GridData.FILL).grab(true, false)
                    .minSize(SWT.DEFAULT, 30).applyTo(this);
        }
        else {
            GridDataFactory.fillDefaults().align(GridData.FILL, GridData.FILL).grab(true, true)
                    .applyTo(this);
        }
    }

}
