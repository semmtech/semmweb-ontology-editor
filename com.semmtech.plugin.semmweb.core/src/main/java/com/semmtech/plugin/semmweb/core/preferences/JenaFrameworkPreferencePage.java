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


import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.semmtech.plugin.semmweb.core.CorePlugin;


public class JenaFrameworkPreferencePage extends FieldEditorPreferencePage implements
        IWorkbenchPreferencePage {

    public JenaFrameworkPreferencePage() {
        super(GRID);
        setPreferenceStore(CorePlugin.getDefault().getPreferenceStore());
        setDescription("Jena Framework preferences");
    }

    @Override
    public void init(IWorkbench workbench) {
    }

    @Override
    protected void createFieldEditors() {
        // / Normally, loading imports during the read() call automatic. To
        // read() an ontology without building the imports closure, call the
        // method setProcessImports( false ) on the document manager object
        // before calling read(). Alternatively, you can set the processImports
        // property in the policy file. You can also be more selective, and
        // ignore only certain URI's when loading the imported documents. To
        // selectively skip certain named imports, call the method
        // addIgnoreImport( String uri ) on the document manager object, or set
        // the ignoreImport property in the policy.
        addField(new BooleanFieldEditor(
                JenaFrameworkPreferenceConstants.PREFERENCE_PROCESS_IMPORTS,
                "&Process Imports preference", getFieldEditorParent()));
    }
}
