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
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.semmtech.plugin.semmweb.core.CorePlugin;


public class SkolemizationPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {
    public static final String ID = "com.semmtech.plugin.semmweb.core.preferences.skolemization";

    private boolean skolemizationEnabled;

    private final Listener listener = new Listener() {

        @Override
        public void handleEvent(Event event) {
            Widget widget = event.widget;
            if (event.type == SWT.Selection) {
                if (widget == enabledCheckbox) {
                    skolemizationEnabled = enabledCheckbox.getSelection();
                }
            }
        }
    };

    private Button enabledCheckbox;

    public SkolemizationPreferencePage() {
        super("Skolemization");
        setPreferenceStore(CorePlugin.getDefault().getPreferenceStore());
        setDescription("Sets the skolemization behaviour of the SEMMweb Editor.");

        skolemizationEnabled = SkolemizationPreference.isSkolemizationEnabled();
    }

    @Override
    public void init(IWorkbench workbench) {
    }

    @Override
    protected Control createContents(Composite parent) {
        Composite top = new Composite(parent, SWT.NONE);
        GridLayoutFactory.fillDefaults().applyTo(top);
        GridDataFactory.fillDefaults().applyTo(top);

        enabledCheckbox = new Button(top, SWT.CHECK);
        enabledCheckbox.setText("Apply skolemization upon saving");
        enabledCheckbox.setSelection(skolemizationEnabled);
        enabledCheckbox.addListener(SWT.Selection, listener);

        GridDataFactory.fillDefaults().indent(5, 8).applyTo(enabledCheckbox);

        Label label = new Label(top, SWT.WRAP);
        label.setText("Skolemization is the proces of tagging anonymous resources with an auto-generated URI. By providing anonymous resources with an URI these anonymous resources will become uniquely identifyable allowing version control to track changes made to these resources. When a model is opened the SEMMweb Editor will always store the skolemized data out of sight in order for anonymous resources to stay anonymous.");

        GridDataFactory.fillDefaults().hint(300, SWT.DEFAULT).grab(true, false).applyTo(label);

        return top;
    }

    @Override
    public boolean performOk() {
        SkolemizationPreference.setSkolemizationEnabled(skolemizationEnabled);
        return true;
    }

    @Override
    protected void performDefaults() {
        enabledCheckbox.setSelection(false);
        skolemizationEnabled = false;
        performOk();
    }
}