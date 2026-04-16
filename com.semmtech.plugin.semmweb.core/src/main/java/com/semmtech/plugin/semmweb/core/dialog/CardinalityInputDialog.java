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


import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.semmtech.plugin.semmweb.core.widgets.CardinalitySpinner;
import com.semmtech.ui.plugin.dialog.AbstractMessageInputDialog;


public class CardinalityInputDialog extends AbstractMessageInputDialog {
    private CardinalitySpinner spinner;
    private int initialMinCardinality = -1;
    private int initialMaxCardinality = -1;

    public CardinalityInputDialog(Shell parentShell) {
        super(parentShell, "Cardinality",
                "Please provide an upper and lower bound for the cardinality using the widget below.");
    }

    @Override
    protected Control createInputArea(Composite parent) {
        Composite composite = (Composite) super.createInputArea(parent);
        GridLayout layout = new GridLayout(2, false);
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        layout.marginTop = 12;
        composite.setLayout(layout);
        composite.setLayoutData(new GridData());

        Label label = new Label(composite, SWT.NONE);
        label.setText("Cardinality: ");

        spinner = new CardinalitySpinner(composite, SWT.BORDER, false);
        if (initialMinCardinality >= 0) {
            spinner.setMin(initialMinCardinality);
        }
        if (initialMaxCardinality >= 0) {
            spinner.setMax(initialMaxCardinality);
        }

        applyDialogFont(composite);

        return composite;
    }

    public void setMin(int min) {
        initialMinCardinality = min;
    }

    public void setMax(int max) {
        initialMaxCardinality = max;
    }

    public int getMin() {
        return spinner.getMin();
    }

    public int getMax() {
        return spinner.getUnbounded() ? -1 : spinner.getMax();
    }
}
