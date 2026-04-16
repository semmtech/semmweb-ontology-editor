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

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.dialog.LanguageInputDialog;
import com.semmtech.plugin.semmweb.core.model.DisplayLanguage;


public class LanguagesPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {
    public static final String ID = "com.semmtech.plugin.semmweb.core.preferences.languages";

    private TableViewer languagesViewer;
    private List<DisplayLanguage> displayLanguages;

    private Button addButton;
    private Button editButton;
    private Button removeButton;
    private Button upButton;
    private Button downButton;

    public LanguagesPreferencePage() {
        super("Languages");
        setPreferenceStore(CorePlugin.getDefault().getPreferenceStore());
        setDescription("Change the order in which languages will be used to deteremine preferred labels.");

        displayLanguages = LanguagesPreference.getDisplayLanguages();
        for (DisplayLanguage lang : displayLanguages) {
            System.out.println("lang.getName() = '" + lang.getName() + "'; lang.getCode() = '"
                    + lang.getCode() + "'");
        }
    }

    @Override
    public void init(IWorkbench workbench) {
    }

    @Override
    protected Control createContents(Composite parent) {
        Composite top = new Composite(parent, SWT.LEFT);
        top.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        ((GridData) top.getLayoutData()).heightHint = 300;
        GridLayout gl_top = new GridLayout(2, false);
        gl_top.horizontalSpacing = 9;
        gl_top.marginWidth = 0;
        top.setLayout(gl_top);

        languagesViewer = new TableViewer(top, SWT.BORDER | SWT.FULL_SELECTION);
        languagesViewer.getControl().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        languagesViewer.setContentProvider(new IStructuredContentProvider() {
            @SuppressWarnings("unchecked")
            @Override
            public Object[] getElements(Object inputElement) {
                if (inputElement instanceof List) {
                    Object[] objects = ((List<DisplayLanguage>) inputElement).toArray();
                    return objects;
                }
                return new Object[0];
            }

            @Override
            public void dispose() {
            }

            @Override
            public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
            }
        });

        Table table = languagesViewer.getTable();
        table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        ((GridData) table.getLayoutData()).verticalIndent = 3;
        ((GridData) table.getLayoutData()).heightHint = 180;
        table.setHeaderVisible(true);
        table.setLinesVisible(true);

        TableColumn column = new TableColumn(table, SWT.NONE);
        column.setText("Name");
        column.setWidth(170);
        column.setResizable(true);
        column.setMoveable(false);

        column = new TableColumn(table, SWT.NONE);
        column.setText("Code");
        column.setWidth(70);
        column.setAlignment(SWT.CENTER);
        column.setResizable(true);
        column.setMoveable(false);

        languagesViewer.setLabelProvider(new ITableLabelProvider() {

            @Override
            public void addListener(ILabelProviderListener listener) {
            }

            @Override
            public void dispose() {
            }

            @Override
            public boolean isLabelProperty(Object element, String property) {
                return false;
            }

            @Override
            public void removeListener(ILabelProviderListener listener) {
            }

            @Override
            public Image getColumnImage(Object element, int columnIndex) {
                if (element instanceof DisplayLanguage) {
                    DisplayLanguage language = (DisplayLanguage) element;
                    if (language.getImageKey() != null && columnIndex == 0) {
                        return CorePlugin.getDefault().getImage(language.getImageKey());
                    }
                }
                return null;
            }

            @Override
            public String getColumnText(Object element, int columnIndex) {
                if (element instanceof DisplayLanguage) {
                    DisplayLanguage language = (DisplayLanguage) element;
                    if (columnIndex == 0) {
                        return language.getName();
                    }
                    else if (columnIndex == 1) {
                        if (language.getCode() == null)
                            return "<null>";
                        else if (language.getCode().length() == 0)
                            return "<empty>";
                        else
                            return language.getCode();
                    }
                }
                return null;
            }

        });
        languagesViewer.setInput(displayLanguages);
        languagesViewer.addSelectionChangedListener(new ISelectionChangedListener() {

            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                IStructuredSelection selection = (IStructuredSelection) event.getSelection();
                if (selection.size() > 0) {
                    int index = languagesViewer.getTable().getSelectionIndex();
                    editButton.setEnabled(true);
                    removeButton.setEnabled(true);
                    upButton.setEnabled(index > 0);
                    downButton.setEnabled(index < (displayLanguages.size() - 1));
                }
            }
        });

        Composite buttonComposite = new Composite(top, SWT.NONE);
        buttonComposite.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
        GridLayout gl_buttonComposite = new GridLayout(1, false);
        gl_buttonComposite.marginWidth = 0;
        gl_buttonComposite.marginHeight = 3;
        buttonComposite.setLayout(gl_buttonComposite);

        addButton = new Button(buttonComposite, SWT.PUSH);
        addButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        ((GridData) addButton.getLayoutData()).minimumWidth = 85;
        addButton.setText("Add...");
        addButton.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                LanguageInputDialog dialog = new LanguageInputDialog(getShell());
                if (dialog.open() == Window.OK) {
                    DisplayLanguage language = new DisplayLanguage(dialog.getCode(), dialog
                            .getName(), dialog.getImageKey());
                    displayLanguages.add(language);
                    languagesViewer.setInput(displayLanguages);
                }
            }
        });

        editButton = new Button(buttonComposite, SWT.PUSH);
        editButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        ((GridData) editButton.getLayoutData()).minimumWidth = 85;
        editButton.setText("Edit...");
        editButton.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                LanguageInputDialog dialog = new LanguageInputDialog(getShell());
                int index = languagesViewer.getTable().getSelectionIndex();
                DisplayLanguage selected = displayLanguages.get(index);

                dialog.setName(selected.getName());
                dialog.setCode(selected.getCode());
                dialog.setImageKey(selected.getImageKey());
                if (dialog.open() == Window.OK) {
                    displayLanguages.remove(index);

                    DisplayLanguage language = new DisplayLanguage(dialog.getCode(), dialog
                            .getName(), dialog.getImageKey());
                    displayLanguages.add(index, language);
                    languagesViewer.setInput(displayLanguages);
                }
            }
        });

        removeButton = new Button(buttonComposite, SWT.PUSH);
        removeButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        ((GridData) removeButton.getLayoutData()).minimumWidth = 85;
        removeButton.setText("Remove");
        removeButton.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                int index = languagesViewer.getTable().getSelectionIndex();
                DisplayLanguage selected = displayLanguages.get(index);
                if (MessageDialog.openConfirm(getShell(), "Remove Language",
                        "Are you sure you want to delete the language " + selected.getName())) {
                    displayLanguages.remove(index);
                    languagesViewer.setInput(displayLanguages);
                }
            }
        });

        upButton = new Button(buttonComposite, SWT.PUSH);
        upButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        ((GridData) upButton.getLayoutData()).minimumWidth = 85;
        ((GridData) upButton.getLayoutData()).verticalIndent = 8;
        upButton.setText("Up");
        upButton.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                int index = languagesViewer.getTable().getSelectionIndex();
                int newIndex = index - 1;

                DisplayLanguage element = displayLanguages.get(index);
                displayLanguages.remove(index);
                displayLanguages.add(newIndex, element);
                languagesViewer.setInput(displayLanguages);

                upButton.setEnabled(newIndex > 0);
                downButton.setEnabled(newIndex < (displayLanguages.size() - 1));
            }
        });

        downButton = new Button(buttonComposite, SWT.PUSH);
        downButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        ((GridData) downButton.getLayoutData()).minimumWidth = 85;
        downButton.setText("Down");
        downButton.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                int index = languagesViewer.getTable().getSelectionIndex();
                int newIndex = index + 1;

                DisplayLanguage element = displayLanguages.get(index);
                displayLanguages.remove(index);
                displayLanguages.add(newIndex, element);
                languagesViewer.setInput(displayLanguages);

                upButton.setEnabled(newIndex > 0);
                downButton.setEnabled(newIndex < (displayLanguages.size() - 1));
            }
        });

        // addButton.setEnabled(false);
        editButton.setEnabled(false);
        removeButton.setEnabled(false);
        upButton.setEnabled(false);
        downButton.setEnabled(false);

        return top;
    }

    @Override
    public boolean performOk() {
        LanguagesPreference.setDisplayLanguages(displayLanguages);
        return super.performOk();
    }

    @Override
    protected void performDefaults() {
        displayLanguages = LanguagesPreference.DEFAULT_LANGUAGES;
        languagesViewer.setInput(displayLanguages);
        super.performDefaults();
    }

}
