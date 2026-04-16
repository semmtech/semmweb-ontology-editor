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


import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.dialogs.PropertyPage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

import com.google.common.base.Strings;
import com.semmtech.plugin.semmweb.core.nature.SemanticProject;
import com.semmtech.ui.plugin.widgets.GuiFactory;


public class SemanticProjectPropertyPage extends PropertyPage {

    private static final String EMPTY_VERSION = "unknown";

    private IProject project;

    @Override
    protected Control createContents(Composite parent) {
        Composite container = GuiFactory.getInstance().createComposite(parent, 2);

        IResource resource = (IResource) getElement().getAdapter(IResource.class);

        if (resource != null) {
            if (resource instanceof IProject) {
                project = (IProject) resource;
            }
            else {
                project = resource.getProject();
            }

            Label label = new Label(container, SWT.NONE);
            label.setText("The following versions of the SEMMWeb Editor have supported this project:");
            GridDataFactory.fillDefaults().span(2, 1).applyTo(label);

            label = new Label(container, SWT.NONE);
            label.setText("Original Version:");
            GridDataFactory.fillDefaults().hint(100, SWT.DEFAULT).applyTo(label);

            label = new Label(container, SWT.NONE);
            label.setText(Strings.nullToEmpty(getInitialVersion()));

            label = new Label(container, SWT.NONE);
            label.setText("Latest Version:");
            GridDataFactory.fillDefaults().hint(100, SWT.DEFAULT).applyTo(label);

            label = new Label(container, SWT.NONE);
            label.setText(Strings.nullToEmpty(getLatestVersion()));
        }
        return container;
    }

    private String getInitialVersion() {
        ProjectScope scope = new ProjectScope(project);
        ScopedPreferenceStore store = new ScopedPreferenceStore(scope, SemanticProject.NATURE_ID);
        if (store.contains("initialVersion")) {
            return store.getString("initialVersion");
        }
        return EMPTY_VERSION;
    }

    private String getLatestVersion() {
        ProjectScope scope = new ProjectScope(project);
        ScopedPreferenceStore store = new ScopedPreferenceStore(scope, SemanticProject.NATURE_ID);
        if (store.contains("latestVersion")) {
            return store.getString("latestVersion");
        }
        return EMPTY_VERSION;
    }
}
