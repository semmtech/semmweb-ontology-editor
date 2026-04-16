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
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
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
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.google.common.base.Strings;
import com.semmtech.jena.ontology.OntologySpec;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.CorePluginImages;
import com.semmtech.plugin.semmweb.core.dialog.WorkspaceOntologySpecDialog;
import com.semmtech.plugin.semmweb.core.dialog.WorkspaceOntologySpecValidator;
import com.semmtech.plugin.semmweb.core.jena.ontology.WorkspaceOntologySpec;
import com.semmtech.ui.plugin.util.Selections;
import com.semmtech.ui.plugin.viewers.TableLabelProvider;
import com.semmtech.ui.plugin.viewers.TableViewerComparator;


public class PublicationMappingsPreferencePage extends PreferencePage implements
        IWorkbenchPreferencePage {

    private static final int COLUMN_PUBLICATION_URI = 0;
    private static final int COLUMN_LOCATION_URL = 1;

    private static final String DEFAULT_DIALOG_TITLE = "Mapping";

    private Table table;
    private TableViewer viewer;
    private Button addEntryButton;
    private Button editEntryButton;
    private Button deleteEntryButton;
    private TableViewerComparator viewerComparator;
    private List<WorkspaceOntologySpec> mappings;

    @Override
    protected Control createContents(Composite parent) {
        Composite top = new Composite(parent, SWT.LEFT);
        GridLayoutFactory.fillDefaults().numColumns(2).extendedMargins(0, 8, 0, 8).spacing(9, 8)
                .applyTo(top);
        top.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        Label label = new Label(top, SWT.NONE);
        label.setText("Mappings:");
        label.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 2, 1));

        table = new Table(top, SWT.FULL_SELECTION | SWT.BORDER | SWT.MULTI);
        viewer = new TableViewer(table);
        table.setLinesVisible(true);
        table.setHeaderVisible(true);

        GridData layoutData = new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL);
        layoutData.grabExcessHorizontalSpace = true;
        layoutData.grabExcessVerticalSpace = true;
        layoutData.heightHint = 180;
        table.setLayoutData(layoutData);

        TableColumn column = new TableColumn(table, SWT.NONE, COLUMN_PUBLICATION_URI);
        column.setText("Publication URI");
        column.setWidth(200);
        column.setResizable(true);
        column.setMoveable(false);
        column.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                viewerComparator.setColumn(COLUMN_PUBLICATION_URI);
                refreshViewer();
            }
        });

        column = new TableColumn(table, SWT.NONE, COLUMN_LOCATION_URL);
        column.setText("Location URL");
        column.setWidth(180);
        column.setResizable(true);
        column.setMoveable(false);
        column.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                viewerComparator.setColumn(COLUMN_LOCATION_URL);
                refreshViewer();
            }
        });

        viewer.setContentProvider(new ArrayContentProvider());
        viewer.setLabelProvider(new TableLabelProvider() {
            @Override
            public String getColumnText(Object element, int columnIndex) {
                if (element instanceof OntologySpec) {
                    OntologySpec item = (OntologySpec) element;
                    if (columnIndex == COLUMN_PUBLICATION_URI) {
                        return item.getPublicURI();
                    }
                    else if (columnIndex == COLUMN_LOCATION_URL) {
                        return item.getAltURL();
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
        viewer.addSelectionChangedListener(new ISelectionChangedListener() {

            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                ISelection selection = viewer.getSelection();
                List<OntologySpec> selected = Selections.retrieveAllAsType(selection,
                        OntologySpec.class);
                deleteEntryButton.setEnabled(selected.size() > 0);
                editEntryButton.setEnabled(selected.size() == 1);
            }
        });

        viewerComparator = new TableViewerComparator() {

            @Override
            public int compare(Viewer viewer, Object e1, Object e2) {
                if (e1 instanceof OntologySpec && e2 instanceof OntologySpec) {
                    OntologySpec o1 = (OntologySpec) e1;
                    OntologySpec o2 = (OntologySpec) e2;
                    return compareItems(o1, o2);
                }
                return 0;
            }

            private int compareItems(OntologySpec o1, OntologySpec o2) {
                int rc = 0;
                if (columnIndex == COLUMN_PUBLICATION_URI) {
                    rc = o1.getPublicURI().compareToIgnoreCase(o2.getPublicURI());
                }
                else if (columnIndex == COLUMN_LOCATION_URL) {
                    if (!Strings.isNullOrEmpty(o1.getAltURL())
                            && !Strings.isNullOrEmpty(o2.getAltURL())) {
                        rc = o1.getAltURL().compareToIgnoreCase(o2.getAltURL());
                    }
                    else if (!Strings.isNullOrEmpty(o1.getAltURL())) {
                        rc = 1;
                    }
                    else {
                        rc = -1;
                    }
                }
                if (direction == DESCENDING) {
                    rc = -rc;
                }
                return rc;
            }
        };
        viewer.setComparator(viewerComparator);

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDoubleClick(MouseEvent e) {
                editOntologySpec();
            }
        });

        // Define the three buttos to the left of the table
        Composite buttonsComposite = new Composite(top, SWT.LEFT | SWT.TOP);
        GridLayout layout = new GridLayout(1, false);
        layout.horizontalSpacing = 0;
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        buttonsComposite.setLayout(layout);
        buttonsComposite.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));

        addEntryButton = new Button(buttonsComposite, SWT.PUSH);
        addEntryButton.setText("Add...");
        layoutData = new GridData(GridData.FILL_HORIZONTAL);
        layoutData.minimumWidth = 92;
        addEntryButton.setLayoutData(layoutData);
        addEntryButton.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                addOntologySpec();
            }
        });

        editEntryButton = new Button(buttonsComposite, SWT.PUSH);
        editEntryButton.setText("Edit...");
        editEntryButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        editEntryButton.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                editOntologySpec();
            }
        });
        editEntryButton.setEnabled(false);

        deleteEntryButton = new Button(buttonsComposite, SWT.PUSH);
        deleteEntryButton.setText("Delete");
        deleteEntryButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        deleteEntryButton.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                deleteOntologySpec();
            }
        });
        deleteEntryButton.setEnabled(false);

        refreshViewer();

        return top;
    }

    protected void refreshViewer() {
        viewer.setInput(mappings);
    }

    protected boolean duplicatePublicURI(String uri) {
        for (WorkspaceOntologySpec mapping : mappings) {
            if (mapping.getPublicURI().equals(uri)) {
                return true;
            }
        }
        return false;
    }

    protected void addOntologySpec() {
        modifyOntology(null);
    }

    protected void editOntologySpec() {
        ISelection selection = viewer.getSelection();
        WorkspaceOntologySpec selected = Selections.retrieveFirstAsType(selection,
                WorkspaceOntologySpec.class);
        modifyOntology(selected);
    }

    protected void modifyOntology(WorkspaceOntologySpec original) {
        WorkspaceOntologySpec spec = original;
        boolean done = false;

        while (!done) {
            String title = DEFAULT_DIALOG_TITLE;
            String message = "Add a new publication mapping by specifying its details.";

            WorkspaceOntologySpecDialog dialog = new WorkspaceOntologySpecDialog(getShell(), title,
                    message, new WorkspaceOntologySpecValidator(), new String[] {
                            "Publication URI", "Location URL" });
            dialog.setHidePrefix(true);
            dialog.setForceAltURL(true);

            if (spec != null) {
                dialog.setPublicURI(spec.getPublicURI());
                dialog.setPrefix(spec.getPrefix());
                dialog.setExternalAltURL(spec.getExternalAltURL());
                dialog.setWorkspaceAltURL(spec.getWorkspaceAltURL());
            }
            if (dialog.open() == 0) {
                String publicUri = dialog.getPublicURI();
                spec = new WorkspaceOntologySpec(publicUri);
                String prefix = dialog.getPrefix();
                if (!Strings.isNullOrEmpty(prefix)) {
                    spec.setPrefix(prefix);
                }
                String altUrl = dialog.getAltURL();
                if (dialog.hasAltURL() && !Strings.isNullOrEmpty(altUrl)) {
                    spec.setAltURL(altUrl);
                }
                if (original != null && original.equals(spec)) {
                    done = true;
                }
                if (duplicatePublicURI(publicUri)
                        && (original == null || !original.getPublicURI().equals(publicUri))) {
                    if (!MessageDialog
                            .openQuestion(
                                    getShell(),
                                    "Duplicate",
                                    String.format(
                                            "The ontology with URI <%s> has already been defined. Do you wish to specify a different URI?",
                                            publicUri))) {
                        done = true;
                    }
                }
                else {
                    if (original != null) {
                        mappings.remove(original);
                    }
                    mappings.add(spec);
                    done = true;
                }
            }
            else {
                done = true;
            }
        }
        refreshViewer();
    }

    protected void deleteOntologySpec() {
        ISelection selection = viewer.getSelection();
        List<OntologySpec> specs = Selections.retrieveAllAsType(selection, OntologySpec.class);
        if (specs.size() == 1) {
            OntologySpec spec = specs.get(0);
            String questionText = String.format(
                    "Are you sure you want to delete the ontology for URI <%s>?",
                    spec.getPublicURI());
            MessageDialog dialog = new MessageDialog(null, "Delete", null, questionText,
                    MessageDialog.QUESTION, new String[] { "Yes", "No" }, 1);
            if (dialog.open() == 0) {
                mappings.remove(spec);
                refreshViewer();
            }
        }
        else if (specs.size() > 1) {
            String questionText = String.format("Are you sure you want to delete these %s URIs?",
                    specs.size());
            MessageDialog dialog = new MessageDialog(null, "Delete", null, questionText,
                    MessageDialog.QUESTION, new String[] { "Yes", "No" }, 1);
            if (dialog.open() == 0) {
                mappings.removeAll(specs);
                refreshViewer();
            }
        }
    }

    @Override
    public void init(IWorkbench workbench) {
        mappings = PublicationMappingsPreference.getPublicationMappings();
    }

    @Override
    protected void performApply() {
        PublicationMappingsPreference.setPublicationMappings(mappings);
    }

    @Override
    protected void performDefaults() {
        mappings = PublicationMappingsPreference.DEFAULT_MAPPINGS;
        PublicationMappingsPreference.setPublicationMappings(mappings);

        refreshViewer();
    }
}
