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


import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateProvider;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.TextStyle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.dialogs.PreferencesUtil;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.semmtech.jena.ontology.OntologySpec;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.CorePluginImages;
import com.semmtech.plugin.semmweb.core.dialog.DocumentManagerPropertyPage;
import com.semmtech.plugin.semmweb.core.jena.ontology.WorkspaceDocumentManagerConfiguration;
import com.semmtech.plugin.semmweb.core.preferences.DocumentManagerPreference;
import com.semmtech.ui.plugin.widgets.Widgets;


public class OntModelImportsWizardPage extends WizardPage {
    @SuppressWarnings("unused")
    private static Logger logger = Logger.getLogger(OntModelImportsWizardPage.class);

    private static final String PAGE_NAME = "importsPage";
    private static final String PAGE_TITLE = "Manage Imports";
    private static final String PAGE_DESCRIPTION = "Select ontologies which will be added as imports.";

    private static final int COLUMN_PREFIX = 0;
    private static final int COLUMN_PUBLIC_URI = 1;
    // private static final int COLUMN_ALT_URL = 2;

    private CheckboxTableViewer importsViewer;
    private final List<OntologySpec> localNamespaces;
    private final List<OntologySpec> mandatoryOntologies;
    private final List<OntologySpec> disabledOntologies = ImmutableList.of(OntologySpec.RDF,
            OntologySpec.RDFS, OntologySpec.OWL, OntologySpec.XSD);

    private final Map<String, Boolean> checkedNamespaces;

    private Table table;
    private IProject project;

    protected OntModelImportsWizardPage(IProject project) {
        super(PAGE_NAME);
        setTitle(PAGE_TITLE);
        setDescription(PAGE_DESCRIPTION);

        checkedNamespaces = Maps.newHashMap();
        localNamespaces = Lists.newArrayList();
        mandatoryOntologies = Lists.newArrayList();

        this.project = project;
    }

    public void setProject(IProject project) {
        this.project = project;
        refreshViewer();
    }

    public void addCheckedNamespace(String ontologySpec, boolean checked) {
        checkedNamespaces.put(ontologySpec, new Boolean(checked));
        if (!Widgets.isNullOrDisposedViewer(importsViewer)) {
            importsViewer.refresh();
        }
    }

    public void addMandatoryOntology(OntologySpec ontologySpec) {
        mandatoryOntologies.add(ontologySpec);
        if (!Widgets.isNullOrDisposedViewer(importsViewer)) {
            importsViewer.refresh();
        }
    }

    @Override
    public void createControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NULL);
        container.setLayout(new GridLayout(1, false));

        Label label = new Label(container, SWT.NULL);
        label.setText("Please select the ontologies which you would like to import into your ontology.");
        label.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));

        label = new Label(container, SWT.NONE);
        GridData layoutData = new GridData(GridData.FILL, GridData.BEGINNING, false, false, 1, 1);
        layoutData.heightHint = 3;
        label.setLayoutData(layoutData);

        importsViewer = CheckboxTableViewer.newCheckList(container, SWT.BORDER | SWT.SINGLE
                | SWT.FULL_SELECTION);

        table = importsViewer.getTable();
        table.setHeaderVisible(true);
        table.setLinesVisible(true);

        layoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
        layoutData.verticalIndent = 3;
        table.setLayoutData(layoutData);

        TableColumn column = new TableColumn(table, SWT.NONE, COLUMN_PREFIX);
        column.setText("Prefix");
        column.setWidth(100);
        column.setResizable(true);
        column.setMoveable(false);

        column = new TableColumn(table, SWT.NONE, COLUMN_PUBLIC_URI);
        column.setText("Namespace URI");
        column.setWidth(210 + 160);
        column.setResizable(true);
        column.setMoveable(false);

        // column = new TableColumn(table, SWT.NONE, COLUMN_ALT_URL);
        // column.setText("Location URL");
        // column.setWidth(160);
        // column.setResizable(true);
        // column.setMoveable(false);

        importsViewer.setContentProvider(new ArrayContentProvider());
        importsViewer.setLabelProvider(new StyledCellLabelProvider() {
            private final Styler mandatoryStyler = new Styler() {
                @Override
                public void applyStyles(TextStyle textStyle) {
                    textStyle.foreground = Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GRAY);
                }
            };

            @Override
            public void update(ViewerCell cell) {
                OntologySpec ontologySpec = (OntologySpec) cell.getElement();
                boolean isMandatory = mandatoryOntologies.contains(ontologySpec);
                int columnIndex = cell.getColumnIndex();
                String text = getColumnText(ontologySpec, columnIndex);
                cell.setText(text);
                if (text != null && isMandatory) {
                    cell.setStyleRanges(new StyledString(text, mandatoryStyler).getStyleRanges());
                }
                else {
                    cell.setStyleRanges(null);
                }
                cell.setImage(getColumnImage(ontologySpec, columnIndex));
                super.update(cell);
            }

            public String getColumnText(Object element, int columnIndex) {
                if (element instanceof OntologySpec) {
                    OntologySpec spec = (OntologySpec) element;
                    switch (columnIndex) {
                    case COLUMN_PREFIX:
                        String prefix = spec.getPrefix();
                        if (prefix != null) {
                            return String.format("%s:", prefix);
                        }
                        return prefix;
                    case COLUMN_PUBLIC_URI:
                        return spec.getPublicURI();
                        // case COLUMN_ALT_URL:
                        // ImportURLUtils.getAltUrlText(project,
                        // spec.getPublicURI(), false);
                    default:
                        return null;
                    }
                }
                return null;
            }

            public Image getColumnImage(Object element, int columnIndex) {
                if (element instanceof OntologySpec) {
                    switch (columnIndex) {
                    case COLUMN_PREFIX:
                        return CorePlugin.getDefault().getImage(CorePluginImages.IMG_ONTOLOGY);
                    case COLUMN_PUBLIC_URI:
                        // case COLUMN_ALT_URL:
                    default:
                        return null;
                    }
                }
                return null;
            }
        });
        importsViewer.setCheckStateProvider(new ICheckStateProvider() {

            @Override
            public boolean isChecked(Object element) {
                if (element instanceof OntologySpec) {
                    OntologySpec spec = (OntologySpec) element;
                    String ns = spec.getPublicURI();
                    boolean checked = false;
                    if (checkedNamespaces.containsKey(ns)) {
                        checked = checkedNamespaces.get(ns).booleanValue();
                    }
                    if (mandatoryOntologies.contains(spec)) {
                        checked = true;
                    }
                    return checked;
                }
                return false;
            }

            @Override
            public boolean isGrayed(Object element) {
                return false;
            }

        });
        table.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                for (TableItem item : importsViewer.getTable().getItems()) {
                    OntologySpec spec = (OntologySpec) item.getData();
                    boolean checked = item.getChecked();
                    if (mandatoryOntologies.contains(spec) && !checked) {
                        e.doit = false;
                        item.setChecked(true);
                    }
                    else {
                        checkedNamespaces.put(spec.getPublicURI(), new Boolean(checked));
                    }
                }
            }
        });

        Link link = new Link(container, SWT.NONE);
        layoutData = new GridData();
        layoutData.verticalIndent = 4;
        link.setLayoutData(layoutData);
        link.setText("Click <a>here</a> to configure the project's document manager.");
        link.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                IProject project = getProject();
                String pageId = DocumentManagerPropertyPage.ID;
                Shell shell = getShell();
                PreferenceDialog dialog = PreferencesUtil.createPropertyDialogOn(shell, project,
                        pageId, null, Collections.EMPTY_MAP);
                dialog.open();
                dialog.close();
            }
        });

        refreshViewer();
        setControl(container);
    }

    private IProject getProject() {
        return project;
    }

    private void refreshViewer() {
        if (importsViewer != null && !Widgets.isNullOrDisposed(table)) {
            localNamespaces.clear();
            if (project != null) {
                DocumentManagerPreference preferences = DocumentManagerPreference
                        .fromProject(project);
                WorkspaceDocumentManagerConfiguration config = preferences
                        .getDocumentManagerConfig();
                for (OntologySpec spec : config.listOntologySpecs()) {
                    if (disabledOntologies.contains(spec)) {
                        continue;
                    }
                    localNamespaces.add(spec);
                }
            }
            Collections.sort(localNamespaces, new Comparator<OntologySpec>() {
                @Override
                public int compare(OntologySpec s1, OntologySpec s2) {
                    if (s1.getPrefix() == null && s2.getPrefix() == null) {
                        return 0;
                    }
                    else if (s1.getPrefix() == null) {
                        return -1;
                    }
                    else if (s2.getPrefix() == null) {
                        return 1;
                    }
                    return s1.getPrefix().compareTo(s2.getPrefix());
                }
            });
            importsViewer.setInput(localNamespaces);
        }
    }

    public List<OntologySpec> getImports() {
        List<OntologySpec> selectedNamespaces = Lists.newArrayList();
        for (OntologySpec spec : localNamespaces) {
            String ns = spec.getPublicURI();
            boolean checked = false;
            if (checkedNamespaces.containsKey(ns)) {
                checked = checkedNamespaces.get(ns).booleanValue();
            }
            if (mandatoryOntologies.contains(spec)) {
                checked = true;
            }
            if (checked) {
                selectedNamespaces.add(spec);
            }
        }
        return selectedNamespaces;
    }

    public void clearMandatory() {
        mandatoryOntologies.clear();
        if (!Widgets.isNullOrDisposedViewer(importsViewer)) {
            importsViewer.refresh(true);
        }
    }
}
