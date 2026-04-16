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

package com.semmtech.plugin.semmweb.core.wizards;


import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import com.google.common.collect.Lists;
import com.hp.hpl.jena.ontology.OntModel;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.CorePluginImages;
import com.semmtech.plugin.semmweb.core.dialog.RewriteNamespaceRuleDialog;
import com.semmtech.semantics.util.NamespaceMapping;
import com.semmtech.semantics.util.NamespaceRewriteRule;
import com.semmtech.semantics.util.NamespaceUtil;
import com.semmtech.ui.plugin.viewers.ListContentProvider;
import com.semmtech.ui.plugin.viewers.TableLabelProvider;
import com.semmtech.ui.plugin.widgets.Widgets;
import com.semmtech.ui.plugin.wizard.BaseWizardPage;


public class RewriteNamespacesWizardPage extends BaseWizardPage {

    private static Logger logger = Logger.getLogger(RewriteNamespacesWizardPage.class);

    private static final String PAGE_NAME = "rewriteNamespacesPage";
    private static final String PAGE_TITLE = "Rewrite Namespaces";
    private static final String PAGE_DESCRIPTION = "This page allows you to specify which namespaces you wish to rewrite.";

    protected OntModel ontModel;
    protected List<NamespaceRewriteRule> rules;
    protected List<NamespaceMapping> namespaces;
    private TableViewer viewer;
    private Table table;
    private Button editButton;
    private Button deleteButton;
    private Button checkNowButton;
    private Button checkUsageCheckbox;
    private boolean checkUsage;

    public RewriteNamespacesWizardPage(OntModel ontModel) {
        this(ontModel, null);
    }

    public RewriteNamespacesWizardPage(OntModel ontModel, List<NamespaceRewriteRule> initialRules) {
        super(PAGE_NAME);
        setTitle(PAGE_TITLE);
        setDescription(PAGE_DESCRIPTION);

        this.rules = Lists.newArrayList();
        this.ontModel = ontModel;
        this.checkUsage = true;
        this.namespaces = NamespaceUtil.getNamespaceMappings(ontModel.getBaseModel(), true);

        if (initialRules != null) {
            this.rules.addAll(initialRules);
        }
    }

    @Override
    public void createControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NULL);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 9;
        layout.verticalSpacing = 9;
        container.setLayout(layout);

        createControlHeading(container);

        viewer = new TableViewer(container, SWT.BORDER | SWT.FULL_SELECTION);
        viewer.setLabelProvider(new TableLabelProvider() {

            @Override
            public String getColumnText(Object element, int columnIndex) {
                if (element instanceof NamespaceRewriteRule) {
                    NamespaceRewriteRule rule = (NamespaceRewriteRule) element;
                    if (columnIndex == 0) {
                        return rule.getFrom().toString();
                    }
                    else if (columnIndex == 1) {
                        return rule.getTo().toString();
                    }
                    else if (columnIndex == 2) {
                        return rule.isUpdatePrefixMap() ? "updated" : "";
                    }
                }
                return null;
            }

            @Override
            public Image getColumnImage(Object element, int columnIndex) {
                if (columnIndex == 0) {
                    return CorePlugin.getDefault().getImage(CorePluginImages.IMG_NAMESPACE);
                }
                return null;
            }
        });
        viewer.setContentProvider(new ListContentProvider());
        viewer.addSelectionChangedListener(new ISelectionChangedListener() {

            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                NamespaceRewriteRule selected = getSelectedRule();

                editButton.setEnabled(selected != null);
                deleteButton.setEnabled(selected != null);
            }
        });

        table = viewer.getTable();

        GridData layoutData = new GridData(GridData.FILL, GridData.BEGINNING, true, false, 1, 1);
        layoutData.heightHint = 120;
        layoutData.verticalIndent = 3;
        table.setLayoutData(layoutData);

        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDoubleClick(MouseEvent e) {
                if (getSelectedRule() != null) {
                    editRule();
                }
            }
        });

        TableColumn originalColumn = new TableColumn(table, SWT.NONE, 0);
        originalColumn.setWidth(200);
        originalColumn.setText("Original");

        TableColumn newColumn = new TableColumn(table, SWT.NONE, 1);
        newColumn.setWidth(200);
        newColumn.setText("New");

        TableColumn updatePrefixColumn = new TableColumn(table, SWT.NONE, 2);
        updatePrefixColumn.setWidth(70);
        updatePrefixColumn.setText("Prefix");

        MenuManager manager = new MenuManager();
        manager.setRemoveAllWhenShown(true);
        manager.addMenuListener(new IMenuListener() {

            @Override
            public void menuAboutToShow(IMenuManager manager) {
                NamespaceRewriteRule selected = getSelectedRule();
                manager.add(new Action("Add...") {
                    @Override
                    public void run() {
                        addRule();
                    }
                });
                if (selected != null) {
                    manager.add(new Separator());
                    manager.add(new Action("Edit...") {
                        @Override
                        public void run() {
                            editRule();
                        }
                    });
                    manager.add(new Separator());
                    manager.add(new Action("Delete") {
                        @Override
                        public void run() {
                            deleteRule();
                        }

                        @Override
                        public ImageDescriptor getImageDescriptor() {
                            return CorePlugin.getDefault().getImageDescriptor(
                                    CorePluginImages.IMG_DELETE);
                        }
                    });
                }
            }
        });
        Menu contextMenu = manager.createContextMenu(table);
        table.setMenu(contextMenu);

        Composite buttonsComposite = new Composite(container, SWT.NONE);
        layout = new GridLayout(1, false);
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        layoutData = new GridData(GridData.CENTER, GridData.BEGINNING, false, false, 1, 1);
        layoutData.verticalIndent = 3;
        buttonsComposite.setLayout(layout);
        buttonsComposite.setLayoutData(layoutData);

        GridData buttonLayout = new GridData(GridData.FILL, GridData.BEGINNING, false, false, 1, 1);
        buttonLayout.widthHint = 80;
        Button addButton = new Button(buttonsComposite, SWT.PUSH);
        addButton.setText("Add...");
        addButton.setLayoutData(buttonLayout);
        addButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                addRule();
            }
        });

        editButton = new Button(buttonsComposite, SWT.PUSH);
        editButton.setText("Edit...");
        editButton.setLayoutData(buttonLayout);
        editButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                editRule();
            }
        });
        editButton.setEnabled(false);

        deleteButton = new Button(buttonsComposite, SWT.PUSH);
        deleteButton.setText("Delete");
        deleteButton.setLayoutData(buttonLayout);
        deleteButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                deleteRule();
            }
        });
        deleteButton.setEnabled(false);

        boolean subModelsExist = ontModel.listSubModels().hasNext();
        if (subModelsExist) {
            Label separator = new Label(container, SWT.SEPARATOR | SWT.HORIZONTAL);
            layoutData = new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1);
            layoutData.verticalIndent = 3;
            separator.setLayoutData(layoutData);

            Composite checkComposite = new Composite(container, SWT.NONE);
            layout = new GridLayout(2, false);
            layout.marginHeight = 0;
            layout.marginWidth = 0;
            layoutData = new GridData(GridData.FILL, GridData.BEGINNING, true, false, 2, 1);
            checkComposite.setLayout(layout);
            checkComposite.setLayoutData(layoutData);

            checkUsageCheckbox = new Button(checkComposite, SWT.CHECK);
            layoutData = new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 1);
            layoutData.horizontalIndent = 7;
            checkUsageCheckbox.setLayoutData(layoutData);
            checkUsageCheckbox.setText("Check if namespace uris are used outside of base model");
            checkUsageCheckbox.setSelection(checkUsage);
            checkUsageCheckbox.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    checkUsage = checkUsageCheckbox.getSelection();
                }
            });

            checkNowButton = new Button(checkComposite, SWT.PUSH);
            layoutData = new GridData(GridData.END, GridData.CENTER, true, false, 1, 1);
            layoutData.widthHint = 100;
            checkNowButton.setLayoutData(layoutData);
            checkNowButton.setText("Check Now...");
            checkNowButton.setSelection(true);
            checkNowButton.setEnabled(rules.size() > 0);
            checkNowButton.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    checkUsage();
                }
            });
        }

        refreshViewer();
        setPageComplete(validatePage());
        setControl(container);
    }

    protected void createControlHeading(Composite parent) {
        Label label = new Label(parent, SWT.WRAP);
        label.setText("The table below lists all namespaces which will be rewritten. Use the buttons to modify this list.");
        GridData layoutData = new GridData(GridData.FILL, GridData.BEGINNING, true, false, 2, 1);
        layoutData.widthHint = 400;
        label.setLayoutData(layoutData);
    }

    public boolean isCheckUsage() {
        return checkUsage;
    }

    protected void checkUsage() {
        List<String> uris = Lists.newArrayList();
        for (NamespaceRewriteRule rule : rules) {
            uris.add(rule.getFrom());
        }
        CheckNamespaceUsageOperation operation = new CheckNamespaceUsageOperation(ontModel, uris);
        operation.addCheckNamespaceUsageListener(new ICheckNamespaceUsageListener() {
            private boolean errors = false;

            @Override
            public boolean resumeOnError(String errorUri) {
                errors = true;
                MessageDialog.openWarning(
                        getShell(),
                        "Namespace Usage",
                        String.format(
                                "Namespace <%s> is also used in at least one of the base models imports. Rewriting the uri of this namespace will potentially result in invalid models and/or loss of information!",
                                errorUri));
                return true;
            }

            @Override
            public void checkCompleted() {
                if (!errors) {
                    MessageDialog
                            .openInformation(
                                    getShell(),
                                    "Namespace Usage",
                                    "All rewrites specified do not appear to be used within a model outside of the base model. Note that only the models imported by the base model are checked.");
                }
            }
        });
        try {
            getContainer().run(false, false, operation);
        }
        catch (InterruptedException e) {
            logger.error("Error occured during checking usage", e);
        }
        catch (InvocationTargetException e) {
            logger.error("Error occured during checking usage", e);
        }
    }

    protected void deleteRule() {
        NamespaceRewriteRule selected = getSelectedRule();
        if (selected != null) {
            rules.remove(selected);
            refreshViewer();
            setPageComplete(validatePage());
        }
    }

    protected NamespaceRewriteRule openRuleDialog(NamespaceRewriteRule original) {
        RewriteNamespaceRuleDialog dialog = new RewriteNamespaceRuleDialog(getShell(),
                "Rewrite Rule",
                "Specify which original namespace URI should be rewritten, and the new URI which should be used.");
        dialog.setNamespaces(namespaces);
        if (original != null) {
            dialog.setRule(original);
        }
        if (dialog.open() == Window.OK) {
            return dialog.getRule();
        }
        return null;
    }

    protected NamespaceRewriteRule getUniqueRule(NamespaceRewriteRule original) {
        NamespaceRewriteRule unique = openRuleDialog(original);
        boolean done = false;
        while (unique != null && !done) {
            String uri = unique.getFrom();
            done = true;
            for (NamespaceRewriteRule other : rules) {
                if (other.equals(original)) {
                    continue;
                }
                String otherUri = other.getFrom();
                if (otherUri.equals(uri)) {
                    if (MessageDialog
                            .open(WARNING,
                                    getShell(),
                                    "Duplicate URI",
                                    "There already exists a rewrite rule for the given URI, please modify the original namespace URI",
                                    Window.OK | Window.CANCEL)) {
                        unique = openRuleDialog(unique);
                        done = false;
                    }
                    else {
                        unique = null;
                    }
                }
            }
        }
        return unique;
    }

    protected void editRule() {
        NamespaceRewriteRule rule = getSelectedRule();
        NamespaceRewriteRule modified = getUniqueRule(rule);
        if (modified != null) {
            int index = rules.indexOf(rule);
            rules.remove(rule);
            rules.add(index, modified);
            refreshViewer();
            setPageComplete(validatePage());
        }
    }

    protected NamespaceRewriteRule getSelectedRule() {
        IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
        if (selection.getFirstElement() != null
                && selection.getFirstElement() instanceof NamespaceRewriteRule) {
            return (NamespaceRewriteRule) selection.getFirstElement();
        }
        return null;
    }

    protected void addRule() {
        NamespaceRewriteRule rule = getUniqueRule(null);
        if (rule != null) {
            rules.add(rule);
            refreshViewer();
            setPageComplete(validatePage());
        }
    }

    protected void refreshViewer() {
        if (!Widgets.isNullOrDisposed(checkNowButton)) {
            checkNowButton.setEnabled(rules.size() > 0);
        }

        if (!Widgets.isNullOrDisposedViewer(viewer)) {
            viewer.setInput(rules);
        }
    }

    protected boolean validatePage() {
        clearErrorMessage();
        // String errorMessage = null;
        // if (errorMessage != null) {
        // setErrorMessage(errorMessage);
        // }
        return (!rules.isEmpty());
    }

    public List<NamespaceRewriteRule> getRules() {
        return rules;
    }
}
