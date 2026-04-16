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

package com.semmtech.ui.plugin.dialog;


import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.semmtech.ui.plugin.widgets.GuiFactory;


public class ExtendedMessageDialog extends MessageDialog {

    public ExtendedMessageDialog(Shell parentShell, String dialogTitle, Image dialogTitleImage,
            String dialogMessage, int dialogImageType, String[] dialogButtonLabels, int defaultIndex) {
        super(parentShell, dialogTitle, dialogTitleImage, dialogMessage, dialogImageType,
                dialogButtonLabels, defaultIndex);
    }

    protected void resizeShell() {
        Shell shell = getShell();
        shell.layout(true, true);
        Point size = shell.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
        shell.setSize(size);
    }

    @Override
    protected Control createMessageArea(Composite composite) {
        Composite container = (Composite) super.createMessageArea(composite);

        Label spacer = new Label(container, SWT.NONE);
        GridData layoutData = new GridData();
        layoutData.heightHint = 0;
        layoutData.verticalIndent = 0;
        spacer.setLayoutData(layoutData);

        Composite inner = new Composite(container, SWT.NONE);
        inner.setLayout(GuiFactory.createGridLayout(1, false, GuiFactory.GRID_NO_MARGINS));
        createPostMessageArea(inner);

        return container;
    }

    protected Control createPostMessageArea(Composite parent) {
        return parent;
    }
}
