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

package com.semmtech.plugin.semmweb.sparql.wizards;


import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.dialogs.PreferencesUtil;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.semmtech.jena.ontology.OntologySpec;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.CorePluginImages;
import com.semmtech.plugin.semmweb.core.dialog.DocumentManagerPropertyPage;
import com.semmtech.plugin.semmweb.core.preferences.DocumentManagerPreference;
import com.semmtech.ui.plugin.viewers.TableLabelProvider;
import com.semmtech.ui.plugin.wizard.BaseWizardPage;


public class QueryPrefixesWizardPage extends BaseWizardPage {

    private static final int PREFIX_COLUMN = 0;
    private static final int URI_COLUMN = 1;

    private static final String PAGE_NAME = "queryPrefixesPage";
    private static final String PAGE_TITLE = "Query Prefixes";
    private static final String PAGE_DESCRIPTION = "This page will help you select the known prefixes.";

    private TableViewer viewer;
    private Map<OntologySpec, Boolean> namespaces;
    private IProject project;

    protected QueryPrefixesWizardPage(IProject project) {
        super(PAGE_NAME);
        setTitle(PAGE_TITLE);
        setDescription(PAGE_DESCRIPTION);

        this.project = project;

    }

    public void setProject(IProject project) {
        this.project = project;
    }

    public IProject getProject() {
        return project;
    }

    @Override
    public void createControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NULL);

        container.setLayout(new GridLayout(1, false));

        Label label = new Label(container, SWT.NULL);
        label.setText("Please select the prefixes which you would like to use in your query.");
        label.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));

        label = new Label(container, SWT.NONE);
        GridData layoutData = new GridData(GridData.FILL, GridData.BEGINNING, false, false, 1, 1);
        layoutData.heightHint = 3;
        label.setLayoutData(layoutData);

        viewer = CheckboxTableViewer.newCheckList(container, SWT.BORDER | SWT.SINGLE
                | SWT.FULL_SELECTION);

        Table table = viewer.getTable();
        table.setHeaderVisible(true);
        table.setLinesVisible(true);

        layoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
        layoutData.verticalIndent = 3;
        table.setLayoutData(layoutData);

        TableColumn column = new TableColumn(table, SWT.NONE, PREFIX_COLUMN);
        column.setText("Prefix");
        column.setWidth(100);
        column.setResizable(true);
        column.setMoveable(false);

        column = new TableColumn(table, SWT.NONE, URI_COLUMN);
        column.setText("Namespace URI");
        column.setWidth(210 + 160);
        column.setResizable(true);
        column.setMoveable(false);

        viewer.setContentProvider(new ArrayContentProvider());
        viewer.setLabelProvider(new TableLabelProvider() {
            @Override
            public String getColumnText(Object element, int columnIndex) {
                if (element instanceof OntologySpec) {
                    OntologySpec spec = (OntologySpec) element;
                    switch (columnIndex) {
                    case PREFIX_COLUMN:
                        return spec.getPrefix();
                    case URI_COLUMN:
                        return spec.getPublicURI();
                    default:
                        return null;
                    }
                }
                return null;
            }

            @Override
            public Image getColumnImage(Object element, int columnIndex) {
                if (columnIndex == 0) {
                    return CorePlugin.getDefault().getImage(CorePluginImages.IMG_ONTOLOGY);
                }
                return null;
            }
        });

        viewer.addFilter(new ViewerFilter() {

            @Override
            public boolean select(Viewer viewer, Object parentElement, Object element) {
                if (element instanceof OntologySpec) {
                    OntologySpec spec = (OntologySpec) element;
                    return !Strings.isNullOrEmpty(spec.getPrefix());
                }
                return false;
            }
        });

        table.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                for (TableItem item : viewer.getTable().getItems()) {
                    if (item.getChecked()) {
                        OntologySpec spec = (OntologySpec) item.getData();
                        namespaces.put(spec, new Boolean(true));
                    }
                }
            }
        });

        Link link = new Link(container, SWT.NONE);
        layoutData = new GridData();
        layoutData.verticalIndent = 4;
        link.setLayoutData(layoutData);
        link.setText("Click <a>here</a> to configure document manager.");
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

        // also if seems useless without this call the document manager isn't
        // shown
        refreshViewer();

        for (TableItem item : viewer.getTable().getItems()) {
            OntologySpec spec = (OntologySpec) item.getData();
            if (namespaces.get(spec).booleanValue()) {
                item.setChecked(true);
            }
        }

        setControl(container);
    }

    void refreshViewer() {
        if (viewer != null && !viewer.getControl().isDisposed()) {
            namespaces = Maps.newHashMap();
            if (project != null) {
                DocumentManagerPreference preferences = DocumentManagerPreference
                        .fromProject(project);
                for (OntologySpec spec : preferences.getDocumentManagerConfig().listOntologySpecs()) {
                    boolean checked = false;
                    if (spec.equals(OntologySpec.RDF)) {
                        checked = true;
                    }
                    if (spec.equals(OntologySpec.RDFS)) {
                        checked = true;
                    }
                    if (spec.equals(OntologySpec.OWL)) {
                        checked = true;
                    }
                    namespaces.put(spec, new Boolean(checked));
                }
            }

            List<OntologySpec> unordered = Lists.newArrayList(namespaces.keySet());
            Collections.sort(unordered, new Comparator<OntologySpec>() {
                @Override
                public int compare(OntologySpec s1, OntologySpec s2) {
                    if (s1.getPrefix() == null && s2.getPrefix() == null) {
                        return 0;
                    }
                    if (s1.getPrefix() == null) {
                        return 1;
                    }
                    if (s2.getPrefix() == null) {
                        return -1;
                    }
                    return s1.getPrefix().compareTo(s2.getPrefix());
                }
            });
            viewer.setInput(unordered);
        }
    }

    public List<OntologySpec> getSelectedPrefixes() {
        List<OntologySpec> selectedNamespaces = Lists.newArrayList();
        for (OntologySpec spec : namespaces.keySet()) {
            if (namespaces.get(spec).booleanValue()) {
                selectedNamespaces.add(spec);
            }
        }
        return selectedNamespaces;
    }
}
