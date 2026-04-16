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
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.semmtech.plugin.semmweb.core.CorePlugin;


public class OntologyFileEditorPreferencePage extends PreferencePage implements
        IWorkbenchPreferencePage {

    public static final String ID = "com.semmtech.plugin.semmweb.core.preferences.ontologyFileEditor";

    private boolean disableAutomaticExplore = false;
    private Button disableAutoExploreCheckbox;

    public OntologyFileEditorPreferencePage() {
        super("Ontology File Editors");
        setPreferenceStore(CorePlugin.getDefault().getPreferenceStore());
        setDescription("Change the settings for semantic RDF/OWL ontology files.");
    }

    @Override
    public void init(IWorkbench workbench) {
        disableAutomaticExplore = OntologyFileEditorPreference.autoExploreDisabled();
    }

    @Override
    protected Control createContents(Composite parent) {
        Composite top = new Composite(parent, SWT.LEFT);
        top.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        GridLayout layout = new GridLayout();
        layout.marginLeft = 0;
        layout.marginWidth = 0;
        top.setLayout(layout);

        disableAutoExploreCheckbox = new Button(top, SWT.CHECK);
        GridData layoutData = new GridData();
        layoutData.horizontalIndent = 5;
        layoutData.verticalIndent = 0;
        disableAutoExploreCheckbox.setLayoutData(layoutData);
        disableAutoExploreCheckbox.setText("Disable automatic Explore");
        disableAutoExploreCheckbox.setSelection(disableAutomaticExplore);

        refresh();
        return top;
    }

    private void refresh() {
        disableAutoExploreCheckbox.setSelection(disableAutomaticExplore);
    }

    @Override
    public boolean performOk() {
        disableAutomaticExplore = disableAutoExploreCheckbox.getSelection();
        updatePreferences();
        refresh();
        return true;
    }

    @Override
    protected void performDefaults() {
        disableAutomaticExplore = false;
        updatePreferences();
        refresh();
    }

    private void updatePreferences() {
        OntologyFileEditorPreference.setAutoExploreDisabled(disableAutomaticExplore);
    }
}
