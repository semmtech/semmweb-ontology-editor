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


import java.io.IOException;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;

import com.google.common.base.Strings;
import com.semmtech.plugin.semmweb.core.navigator.ISemanticElement;
import com.semmtech.plugin.semmweb.core.preferences.ModelsFolderPreference;


/**
 * 
 * @author Mike Henrichs
 */
public class ModelsFolderPropertyPage extends AbstractProjectPropertyPage {

    private static Logger logger = Logger.getLogger(ModelsFolderPropertyPage.class);

    public static final String ID = "com.semmtech.plugin.semmweb.pages.modelsFolder";
    private Text pathText;
    private Button browseButton;
    private ModelsFolderPreference preferences;

    public ModelsFolderPropertyPage() {
        super();
    }

    @Override
    protected Control createContents(Composite parent) {
        Composite top = new Composite(parent, SWT.LEFT | SWT.TOP);
        GridLayoutFactory.fillDefaults().numColumns(2).extendedMargins(0, 5, 8, 8).spacing(9, 8)
                .applyTo(top);

        Label label = new Label(top, SWT.WRAP);
        label.setText("Specify the folder within this project which will be used as the source for all models.");
        GridDataFactory.swtDefaults().span(2, 1).hint(580, SWT.DEFAULT).grab(true, false)
                .applyTo(label);

        label = new Label(top, SWT.NONE);
        label.setText("Models Folder:");
        GridDataFactory.fillDefaults().grab(true, false).span(2, 1).applyTo(label);

        pathText = new Text(top, SWT.BORDER);
        GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false)
                .applyTo(pathText);
        if (preferences != null) {
            pathText.setText(Strings.nullToEmpty(preferences.getModelsFolderPath()));
        }

        browseButton = new Button(top, SWT.PUSH);
        browseButton.setText("Browse...");
        browseButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                handleBrowse();
            }
        });
        GridDataFactory.swtDefaults().hint(85, SWT.DEFAULT).applyTo(browseButton);

        return top;
    }

    protected void handleBrowse() {
        ContainerSelectionDialog dialog = new ContainerSelectionDialog(getShell(), getProject(),
                false, "Select a models folder form list below.");
        if (dialog.open() == Window.OK) {
            Object[] result = dialog.getResult();
            if (result.length == 1) {
                pathText.setText(((Path) result[0]).toString());
            }
        }
    }

    @Override
    public void setElement(IAdaptable element) {
        super.setElement(element);
        IProject project = null;
        if (element instanceof IProject) {
            project = getProject();
        }
        else if (element instanceof ISemanticElement) {
            project = ((ISemanticElement) element).getProject();
        }
        if (project != null) {
            preferences = ModelsFolderPreference.fromProject(project);
        }
    }

    @Override
    public boolean performOk() {
        preferences.setModelsFolderPath(pathText.getText());
        try {
            preferences.save();
            rebuild();
        }
        catch (IOException ex) {
            logger.error("Error saving models path preferences!", ex);
        }
        return true;
    }

    @Override
    protected void performDefaults() {
        preferences.restoreToDefaults();
        pathText.setText(Strings.nullToEmpty(preferences.getModelsFolderPath()));

        try {
            preferences.save();
            rebuild();
        }
        catch (IOException ex) {
            logger.error("Error saving models path preferences!", ex);
        }
    }

    private void rebuild() {
        IProject project = getProject();
        try {
            project.build(IncrementalProjectBuilder.CLEAN_BUILD, null);
        }
        catch (CoreException ex) {
            logger.error("Error attempting to rebuild project!", ex);
        }
    }

}
