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

package com.semmtech.plugin.semmweb.core.dialog;


import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.semmtech.ui.plugin.dialog.AbstractMessageInputDialog;


public class RadioSelectionInputDialog extends AbstractMessageInputDialog {

    private Map<String, String> options;
    private String selected;

    public RadioSelectionInputDialog(Shell parentShell, String title, String message,
            Map<String, String> options) {
        super(parentShell, title, message);
        this.showErrorMessage = false;
        this.options = options;
    }

    public RadioSelectionInputDialog(Shell parentShell, String title, String message,
            Map<String, String> options, String selected) {
        super(parentShell, title, message);
        this.showErrorMessage = false;
        this.options = options;
        this.selected = selected;
    }

    public String getSelected() {
        return selected;
    }

    @Override
    protected Control createInputArea(Composite parent) {
        Composite composite = (Composite) super.createInputArea(parent);
        {
            GridLayout layout = new GridLayout(1, false);
            layout.marginLeft = 10;
            composite.setLayout(layout);
        }
        for (final String key : options.keySet()) {
            String description = options.get(key);
            Button radio = new Button(composite, SWT.RADIO);
            radio.setText(description);
            if (key.equals(selected))
                radio.setSelection(true);
            radio.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    selected = key;
                }
            });
        }
        return composite;
    }
}
