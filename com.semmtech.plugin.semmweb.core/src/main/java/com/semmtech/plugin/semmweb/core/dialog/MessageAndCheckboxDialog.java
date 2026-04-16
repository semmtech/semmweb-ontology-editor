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


import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;


public class MessageAndCheckboxDialog extends MessageDialog {
    private String checkboxMessage;
    private boolean checkboxChecked;

    public MessageAndCheckboxDialog(Shell parentShell, String title, String message,
            String checkboxMessage) {
        super(parentShell, title, null, message, MessageDialog.NONE, new String[] { "Yes", "No" },
                1);
        this.checkboxMessage = checkboxMessage;
        this.checkboxChecked = false;
    }

    public MessageAndCheckboxDialog(Shell parentShell, String dialogTitle, Image dialogTitleImage,
            String dialogMessage, int dialogImageType, String[] dialogButtonLabels,
            int defaultIndex, String checkboxMessage, boolean defaultCheckboxChecked) {
        super(parentShell, dialogTitle, dialogTitleImage, dialogMessage, dialogImageType,
                dialogButtonLabels, defaultIndex);
        this.checkboxMessage = checkboxMessage;
        this.checkboxChecked = defaultCheckboxChecked;
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite container = (Composite) super.createDialogArea(parent);

        final Button checkbox = new Button(container, SWT.CHECK);
        checkbox.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        checkbox.setText(checkboxMessage);
        checkbox.setSelection(checkboxChecked);
        checkbox.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                checkboxChecked = checkbox.getSelection();
            }
        });
        return container;
    }

    public void setCheckboxMessage(String checkboxMessage) {
        this.checkboxMessage = checkboxMessage;
    }

    public String getCheckboxMessage() {
        return checkboxMessage;
    }

    public void setCheckboxValue(boolean value) {
        checkboxChecked = value;
    }

    public boolean getCheckboxValue() {
        return checkboxChecked;
    }

    /**
     * Convenience method for creating a dialog with a Information icon and an
     * OK button only.
     * 
     * @param parentShell
     * @param title
     * @param message
     * @param checkboxMessage
     * @return
     */
    public static MessageAndCheckboxDialog createInformation(Shell parentShell, String title,
            String message, String checkboxMessage) {
        return new MessageAndCheckboxDialog(parentShell, title, null, message,
                SWT.ICON_INFORMATION, new String[] { "OK" }, 0, checkboxMessage, false);
    }
}
