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


import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.google.common.base.Strings;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.ui.plugin.widgets.Widgets;


/**
 * 
 * @author Sander Stolk
 */
public class JenaPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

    public static final String ID = "com.semmtech.plugin.semmweb.core.preferences.jena";

    private Text connectionTimeoutText;

    public JenaPreferencePage() {
        super("Jena");
        setPreferenceStore(CorePlugin.getDefault().getPreferenceStore());
        setDescription("Change the settings for Jena, which reads and writes models.");
    }

    @Override
    public void init(IWorkbench workbench) {
    }

    @Override
    protected Control createContents(Composite parent) {
        Composite top = new Composite(parent, SWT.LEFT);
        GridDataFactory.fillDefaults().grab(false, false).applyTo(top);
        GridLayoutFactory.fillDefaults().numColumns(2).margins(5, 5).applyTo(top);

        Label label = new Label(top, SWT.NONE);
        GridDataFactory.fillDefaults().applyTo(label);
        label.setText("Connection timeout when reading in models (in milliseconds):");
        connectionTimeoutText = new Text(top, SWT.NONE);
        connectionTimeoutText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                String text = connectionTimeoutText.getText();
                if (!Strings.isNullOrEmpty(text)) {
                    String validText = new String();
                    for (int i = 0; i < text.length(); i++) {
                        char c = text.charAt(i);
                        if (Character.isDigit(c)) {
                            validText = validText + c;
                        }
                    }
                    if (!validText.equals(text)) {
                        connectionTimeoutText.setText(validText);
                    }
                }
            }
        });
        GridDataFactory.fillDefaults().hint(50, SWT.DEFAULT).applyTo(connectionTimeoutText);

        refresh();
        return top;
    }

    private void refresh() {
        int timeout = JenaPreference.getConnectionTimeout();
        connectionTimeoutText.setText(String.format("%d", timeout));
    }

    @Override
    public boolean performOk() {
        updatePreferences();
        refresh();
        return true;
    }

    @Override
    protected void performDefaults() {
        int timeout = JenaPreference.DEFAULT_CONNECTION_TIMEOUT;
        connectionTimeoutText.setText(String.format("%d", timeout));

        updatePreferences();
        refresh();
    }

    private void updatePreferences() {
        if (!Widgets.isNullOrDisposed(connectionTimeoutText)) {
            try {
                int timeout = Integer.parseInt(connectionTimeoutText.getText());
                if (timeout < 0) {
                    timeout = 0;
                }
                JenaPreference.setConnectionTimeout(timeout);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
