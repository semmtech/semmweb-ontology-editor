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
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.CorePluginImages;
import com.semmtech.plugin.semmweb.core.dialog.RulesFileInputDialog;
import com.semmtech.plugin.semmweb.core.reasoner.RuleConfiguration;
import com.semmtech.plugin.semmweb.core.viewers.TreeContentProvider;
import com.semmtech.ui.plugin.viewers.TableLabelProvider;


public class ReasoningPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {
    public static final String ID = "com.semmtech.plugin.semmweb.core.preferences.reasoning";

    private RuleConfiguration configuration;

    private TreeViewer viewer;
    private Tree tree;
    private Button executeOnIntermediateCheckbox;
    private Button removeButton;
    private Button addButton;

    public ReasoningPreferencePage() {
        super("Reasoning");
    }

    @Override
    public void init(IWorkbench workbench) {
        configuration = ReasoningPreference.getRuleConfiguration();
        if (configuration == null) {
            configuration = new RuleConfiguration();
        }
    }

    @Override
    protected Control createContents(Composite parent) {
        Composite top = new Composite(parent, SWT.LEFT);
        top.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        top.setLayout(new GridLayout());
        boolean show = false;
        if (show) {
            Group ruleConfigurationGroup = new Group(top, SWT.NONE);
            ruleConfigurationGroup.setText("Rule Configuration");
            ruleConfigurationGroup.setLayout(new GridLayout(1, false));
            ruleConfigurationGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1,
                    1));

            Composite ruleConfigurationComposite = new Composite(ruleConfigurationGroup, SWT.NONE);
            ruleConfigurationComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
                    false, 1, 1));
            ruleConfigurationComposite.setBounds(0, 0, 64, 64);
            ruleConfigurationComposite.setLayout(new GridLayout(2, false));

            Label description = new Label(ruleConfigurationComposite, SWT.NONE);
            description.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
            description.setBounds(0, 0, 55, 15);
            description
                    .setText("Provide the rule files (or builtin Jena @includes) and their order.");

            tree = new Tree(ruleConfigurationComposite, SWT.BORDER);
            tree.setHeaderVisible(true);
            tree.setLinesVisible(true);
            {
                GridData data = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
                data.heightHint = 250;
                data.widthHint = 360;
                tree.setLayoutData(data);
            }

            TreeColumn ruleURLColumn = new TreeColumn(tree, SWT.NONE);
            ruleURLColumn.setWidth(350);
            ruleURLColumn.setText("Rule URL");

            Composite buttonComposite = new Composite(ruleConfigurationComposite, SWT.NONE);
            buttonComposite.setLayout(new GridLayout(1, false));
            buttonComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));

            GridData buttonLayoutData = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
            buttonLayoutData.widthHint = 92;

            addButton = new Button(buttonComposite, SWT.NONE);
            addButton.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    RulesFileInputDialog dialog = new RulesFileInputDialog(getShell(),
                            "Rules File", "Select an input file containting the reasoner's rules.");
                    if (dialog.open() == 0) {
                        if (dialog.getFilename() != null) {
                            configuration.addRuleURL(dialog.getFilename());
                            refreshViewer();
                        }
                    }
                }
            });
            addButton.setLayoutData(buttonLayoutData);
            addButton.setBounds(0, 0, 75, 25);
            addButton.setText("Add...");

            removeButton = new Button(buttonComposite, SWT.NONE);
            removeButton.setLayoutData(buttonLayoutData);
            removeButton.setBounds(0, 0, 75, 25);
            removeButton.setText("Remove");
            removeButton.setEnabled(false);
            removeButton.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
                    if (selection.getFirstElement() instanceof String) {
                        configuration.removeRuleURL(selection.getFirstElement().toString());
                    }
                    refreshViewer();
                }
            });

            viewer = new TreeViewer(tree);
            viewer.setContentProvider(new TreeContentProvider() {
                private RuleConfiguration configuration;

                @Override
                public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
                    configuration = null;
                    if (newInput != null && newInput instanceof RuleConfiguration) {
                        configuration = (RuleConfiguration) newInput;
                    }
                }

                @Override
                public Object[] getElements(Object inputElement) {
                    if (configuration != null) {
                        return configuration.getRuleURLs().toArray();
                    }
                    return null;
                }

                @Override
                public boolean hasChildren(Object element) {
                    return false;
                }

                @Override
                public Object[] getChildren(Object parentElement) {
                    return null;
                }
            });
            viewer.setLabelProvider(new TableLabelProvider() {
                @Override
                public Image getColumnImage(Object element, int columnIndex) {
                    if (columnIndex == 0) {
                        return CorePlugin.getDefault().getImage(CorePluginImages.IMG_RULE_FILE);
                    }
                    return null;
                }

                @Override
                public String getColumnText(Object element, int columnIndex) {
                    if (columnIndex == 0) {
                        return element.toString();
                    }
                    return null;
                }
            });
            viewer.addSelectionChangedListener(new ISelectionChangedListener() {

                @Override
                public void selectionChanged(SelectionChangedEvent event) {
                    IStructuredSelection selection = (IStructuredSelection) event.getSelection();
                    if (selection != null) {
                        removeButton.setEnabled(selection.size() > 0);
                    }
                }
            });

            refreshViewer();
        }

        executeOnIntermediateCheckbox = new Button(top, SWT.CHECK);
        executeOnIntermediateCheckbox.setText("Run inference on intermediate results");
        executeOnIntermediateCheckbox.setSelection(ReasoningPreference.executeOnIntermediate());

        return top;
    }

    private void refreshViewer() {
        viewer.setInput(configuration);
        tree.setItemCount(configuration.getRuleURLs().size());
    }

    @Override
    public boolean performOk() {
        ReasoningPreference.setRuleConfiguration(configuration);
        ReasoningPreference.setExecuteOnIntermediate(executeOnIntermediateCheckbox.getSelection());
        return true;
    }
}
