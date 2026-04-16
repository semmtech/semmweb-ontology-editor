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


import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.semmtech.plugin.semmweb.core.CorePlugin;


public class LabelsPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {
    public static final String ID = "com.semmtech.plugin.semmweb.core.preferences.labels";
    private RadioGroupFieldEditor resourceLabels;

    // private Button ontologyUriCheckbox;

    public LabelsPreferencePage() {
        super("Labels");
        setPreferenceStore(CorePlugin.getDefault().getPreferenceStore());
        setDescription("Change the settings for preseting labels for resources.");
    }

    @Override
    public void init(IWorkbench workbench) {
    }

    @Override
    protected Control createContents(Composite parent) {
        Composite top = new Composite(parent, SWT.LEFT);
        top.setLayout(new GridLayout());

        resourceLabels = new RadioGroupFieldEditor(
                LabelsPreference.PREFERENCE_RESOURCE_LABEL_RENDERING, "Resource Labels", 1,
                new String[][] {
                        { "Show resource qnames with prefixes",
                                LabelsPreference.VALUE_SHOW_RESOURCE_QNAMES },
                        { "Show human readable rdfs:labels",
                                LabelsPreference.VALUE_SHOW_READABLE_LABELS } }, top, true);
        resourceLabels.setPage(this);
        resourceLabels.setPreferenceStore(getPreferenceStore());
        resourceLabels.load();

        // ontologyUriCheckbox = new Button(top, SWT.CHECK);
        // GridData layoutData = new GridData();
        // layoutData.verticalIndent = 2;
        // ontologyUriCheckbox.setText("Always show ontology URI instead of name.");
        // ontologyUriCheckbox.setLayoutData(layoutData);
        // ontologyUriCheckbox.setSelection(LabelsPreference.alwaysShowOntologyUri());

        return top;
    }

    @Override
    protected void performDefaults() {
        resourceLabels.loadDefault();
        // LabelsPreference.setAlwaysShowOntologyUri(false);
        // ontologyUriCheckbox.setSelection(false);
        super.performDefaults();
    }

    @Override
    public boolean performOk() {
        resourceLabels.store();
        // LabelsPreference.setAlwaysShowOntologyUri(ontologyUriCheckbox.getSelection());
        return super.performOk();
    }
}
