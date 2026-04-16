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


import java.util.List;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ICheckStateProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.extensionpoint.CoreExtensions;
import com.semmtech.plugin.semmweb.core.extensionpoint.IModelValidator;
import com.semmtech.ui.plugin.viewers.TableLabelProvider;


public class ValidatorsPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {
    public static final String ID = "com.semmtech.plugin.semmweb.core.preferences.validators";

    @SuppressWarnings("unused")
    private static final int COLUMN_CHECKBOX = 0;
    private static final int COLUMN_NAME = 1;
    private static final int COLUMN_DESCRIPTION = 2;

    private List<IModelValidator> validators;

    public ValidatorsPreferencePage() {
        super("Validators");
        setPreferenceStore(CorePlugin.getDefault().getPreferenceStore());
        validators = CoreExtensions.findValidators();
    }

    @Override
    public void init(IWorkbench workbench) {
    }

    @Override
    protected Control createContents(Composite parent) {
        Composite top = new Composite(parent, SWT.LEFT);
        top.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        GridLayout layout = new GridLayout();
        layout.marginLeft = 0;
        layout.marginWidth = 0;
        layout.verticalSpacing = 9;
        top.setLayout(layout);

        Label label = new Label(top, SWT.NONE);

        label.setText("Below you can enable or disabled present validators.");

        Table table = new Table(top, SWT.BORDER | SWT.CHECK | SWT.FULL_SELECTION);
        CheckboxTableViewer viewer = new CheckboxTableViewer(table);
        table.setLinesVisible(true);
        table.setHeaderVisible(true);
        GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
        table.setLayoutData(layoutData);

        TableColumn column = new TableColumn(table, SWT.NONE);
        column.setText("");
        column.setWidth(30);
        column.setAlignment(SWT.CENTER);
        column.setResizable(false);

        column = new TableColumn(table, SWT.NONE);
        column.setText("Validator");
        column.setWidth(130);
        column.setAlignment(SWT.LEFT);

        column = new TableColumn(table, SWT.NONE);
        column.setText("Description");
        column.setWidth(230);
        column.setAlignment(SWT.LEFT);

        viewer.setContentProvider(new ArrayContentProvider());
        viewer.setInput(validators);
        viewer.setLabelProvider(new TableLabelProvider() {
            @Override
            public String getColumnText(Object element, int columnIndex) {
                if (!(element instanceof IModelValidator)) {
                    return null;
                }
                IModelValidator validator = (IModelValidator) element;
                if (columnIndex == COLUMN_NAME) {
                    return validator.getName();
                }
                else if (columnIndex == COLUMN_DESCRIPTION) {
                    return validator.getDescription();
                }
                return null;
            }
        });
        viewer.addCheckStateListener(new ICheckStateListener() {

            @Override
            public void checkStateChanged(CheckStateChangedEvent event) {
                Object element = event.getElement();
                if (element instanceof IModelValidator) {
                    IModelValidator validator = (IModelValidator) element;
                    validator.setEnabled(event.getChecked());
                }

            }
        });
        viewer.setCheckStateProvider(new ICheckStateProvider() {

            @Override
            public boolean isGrayed(Object element) {
                return false;
            }

            @Override
            public boolean isChecked(Object element) {
                if (element instanceof IModelValidator) {
                    return ((IModelValidator) element).isEnabled();
                }
                return false;
            }
        });
        return top;
    }
}
