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
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.semmtech.ui.plugin.dialog.ExtendedMessageDialog;


public class MessageComboDialog extends ExtendedMessageDialog {

    private final String comboMessage;
    private final String[] options;
    private final int defaultOptionIndex;
    private final SelectionListener[] listeners;
    private int selectedIndex;

    public MessageComboDialog(Shell parentShell, String dialogTitle, Image dialogTitleImage,
            String dialogMessage, int dialogImageType, String[] dialogButtonLabels,
            int defaultIndex, String comboMessage, String[] options, int defaultOptionIndex) {
        super(parentShell, dialogTitle, dialogTitleImage, dialogMessage, dialogImageType,
                dialogButtonLabels, defaultIndex);
        this.comboMessage = comboMessage;
        this.options = options;
        this.listeners = new SelectionListener[options.length];
        this.defaultOptionIndex = defaultOptionIndex;
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite container = (Composite) super.createDialogArea(parent);

        Label label = new Label(container, SWT.NONE);
        label.setText(comboMessage);

        final Combo combo = new Combo(container, SWT.READ_ONLY);
        combo.setItems(options);
        combo.select(defaultOptionIndex);
        combo.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                selectedIndex = combo.getSelectionIndex();
                SelectionListener listener = listeners[selectedIndex];
                if (listener != null) {
                    listener.widgetSelected(new SelectionEvent(new Event()));
                }
            }
        });

        return container;
    }

    public void setSelectionListener(int index, SelectionListener listener) {
        if (index >= listeners.length) {
            throw new IndexOutOfBoundsException();
        }
        listeners[index] = listener;
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public static MessageComboDialog createInformation(Shell parentShell, String title,
            String message, String comboMessage, String[] options, int defaultOptionIndex) {
        return new MessageComboDialog(parentShell, title, null, message, SWT.ICON_INFORMATION,
                new String[] { "OK" }, 0, comboMessage, options, defaultOptionIndex);
    }

    public static MessageComboDialog createError(Shell parentShell, String title, String message,
            String comboMessage, String[] options, int defaultOptionIndex) {
        return new MessageComboDialog(parentShell, title, null, message, SWT.ICON_ERROR,
                new String[] { "OK" }, 0, comboMessage, options, defaultOptionIndex);
    }

}
