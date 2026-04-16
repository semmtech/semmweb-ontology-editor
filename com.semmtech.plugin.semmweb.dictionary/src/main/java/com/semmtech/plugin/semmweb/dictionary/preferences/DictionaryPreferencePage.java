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

package com.semmtech.plugin.semmweb.dictionary.preferences;


import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.semmtech.plugin.semmweb.dictionary.DictionaryPlugin;


public class DictionaryPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

    private Button autoImportCheckbox;

    public DictionaryPreferencePage() {
        super("Dictionary");
        setPreferenceStore(DictionaryPlugin.getDefault().getPreferenceStore());
        setDescription("These settings are specific dictionary settings.");
    }

    @Override
    public void init(IWorkbench workbench) {

    }

    @Override
    protected Control createContents(Composite parent) {
        Composite top = new Composite(parent, SWT.LEFT);
        top.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        top.setLayout(new GridLayout());

        autoImportCheckbox = new Button(top, SWT.CHECK);
        autoImportCheckbox.setText("Automatically import Dublin Core and SKOS ontologies");
        autoImportCheckbox.setSelection(DictionaryPreference.autoImport());

        return top;
    }

    @Override
    public boolean performOk() {
        DictionaryPreference.setAutoImport(autoImportCheckbox.getSelection());
        return super.performOk();
    }

    @Override
    protected void performDefaults() {
        DictionaryPreference.setDefaults(getPreferenceStore());
        super.performDefaults();
    }

}
