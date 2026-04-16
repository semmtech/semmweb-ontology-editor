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
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import com.google.common.collect.Lists;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.CorePluginImages;
import com.semmtech.plugin.semmweb.core.forms.editor.OntologyFormEditor;
import com.semmtech.plugin.semmweb.core.navigator.ISemanticElement;
import com.semmtech.plugin.semmweb.core.preferences.ImportNotificationsPreference;
import com.semmtech.ui.plugin.viewers.StructuredContentProvider;
import com.semmtech.ui.plugin.viewers.TableLabelProvider;


public class ImportNotificationsPropertyPage extends AbstractProjectPropertyPage {

    private static Logger logger = Logger.getLogger(ImportNotificationsPropertyPage.class);

    public static final String ID = "com.semmtech.plugin.semmweb.core.pages.importNotifications";

    private final Set<String> ignoredUris;
    private ImportNotificationsPreference preferences;

    private TableViewer viewer;
    private Button removeButton;
    private Button addButton;
    private Button removeAllButton;

    public ImportNotificationsPropertyPage() {
        super();
        ignoredUris = new HashSet<>();
    }

    @Override
    protected Control createContents(Composite parent) {
        Composite top = new Composite(parent, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(2).margins(0, 8).spacing(9, 8).applyTo(top);

        Label label = new Label(top, SWT.WRAP);
        label.setText("Import notifications are by default given before an import statement is processed. On the dialog of such a notification the user can view the ontology being imported and the location which will be accessed, additionally the user can select an alternate location before any data is retrieved.");
        GridDataFactory.swtDefaults().span(2, 1).hint(580, SWT.DEFAULT).grab(true, false)
                .applyTo(label);

        label = new Label(top, SWT.NONE);
        label.setText("The list below represents all ontology URIs for which notifications are ignored");
        GridDataFactory.fillDefaults().grab(true, false).span(2, 1).applyTo(label);

        viewer = new TableViewer(top, SWT.MULTI | SWT.FULL_SELECTION | SWT.BORDER);
        Table table = viewer.getTable();
        GridDataFactory.fillDefaults().grab(true, false).hint(SWT.DEFAULT, 250).applyTo(table);
        table.setLinesVisible(true);
        table.setHeaderVisible(true);

        TableColumn column = new TableColumn(table, SWT.NONE);
        column.setWidth(320);
        column.setResizable(true);
        column.setText("Ontology URI");

        viewer.setLabelProvider(new TableLabelProvider() {
            @Override
            public String getColumnText(Object element, int columnIndex) {
                if (columnIndex == 0) {
                    if (element instanceof String) {
                        return element.toString();
                    }
                }
                return null;
            }

            @Override
            public Image getColumnImage(Object element, int columnIndex) {
                return CorePlugin.getDefault().getImage(CorePluginImages.IMG_OWL_ONTOLOGY);
            }
        });
        viewer.setContentProvider(new StructuredContentProvider() {
            @Override
            public Object[] getElements(Object inputElement) {
                if (inputElement instanceof Collection<?>) {
                    return ((Collection<?>) inputElement).toArray();
                }
                return null;
            }
        });
        viewer.addSelectionChangedListener(new ISelectionChangedListener() {

            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
                removeButton.setEnabled(selection.size() > 0);
            }
        });

        Composite buttonComposite = new Composite(top, SWT.NONE);
        GridLayoutFactory.fillDefaults().margins(0, 0).spacing(SWT.DEFAULT, 4)
                .applyTo(buttonComposite);
        GridDataFactory.fillDefaults().align(SWT.END, SWT.BEGINNING).applyTo(buttonComposite);

        addButton = new Button(buttonComposite, SWT.PUSH);
        GridData buttonData = new GridData(GridData.CENTER, GridData.BEGINNING, false, false, 1, 1);
        buttonData.widthHint = 85;
        addButton.setText("Add...");
        addButton.setLayoutData(buttonData);
        addButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                WorkspaceOntologySpecDialog dialog = new WorkspaceOntologySpecDialog(getShell(),
                        "Add Ignore URI",
                        "Please provide the URI of an ontology to be added to the ignore list");
                dialog.setHideAltURL(true);
                dialog.setHidePrefix(true);
                if (dialog.open() == Window.OK) {
                    String uri = dialog.getPublicURI();
                    ignoredUris.add(uri);
                    refresh();
                }
            }
        });

        removeButton = new Button(buttonComposite, SWT.PUSH);
        removeButton.setText("Remove");
        removeButton.setLayoutData(buttonData);
        removeButton.setEnabled(false);
        removeButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (MessageDialog.openConfirm(getShell(), "Remove URIs",
                        "Are you sure you want to remove the selected URIs from the ignore list?")) {
                    IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
                    for (Iterator<?> iter = selection.iterator(); iter.hasNext();) {
                        String uri = (String) iter.next();
                        ignoredUris.remove(uri);
                    }
                    refresh();
                }
            }
        });

        removeAllButton = new Button(buttonComposite, SWT.PUSH);
        removeAllButton.setText("Remove All");
        removeAllButton.setLayoutData(buttonData);
        removeAllButton.setEnabled(ignoredUris.size() > 0);
        removeAllButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (MessageDialog.openConfirm(getShell(), "Remove URIs",
                        "Are you sure you want to remove all the URIs from the ignore list?")) {
                    ignoredUris.clear();
                    refresh();
                }
            }
        });

        Link clearSessionLink = new Link(top, SWT.NONE);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).span(2, 1)
                .applyTo(clearSessionLink);
        clearSessionLink
                .setText("Any ignored URIs stored for this session can be cleared <a>here</a>");
        clearSessionLink.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                OntologyFormEditor.clearIgnoredSessionURIs();
                MessageDialog.openInformation(getShell(), "Clear Session",
                        "All ontology URIs stored for this session have been cleared.");
            }
        });

        refresh();

        return top;
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
            preferences = ImportNotificationsPreference.fromProject(project);
            ignoredUris.clear();
            ignoredUris.addAll(preferences.getIgnoreNotificationURIs());
        }
    }

    private void refresh() {
        List<String> input = Lists.newArrayList(ignoredUris);
        Collections.sort(input);
        viewer.setInput(input);
        IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
        removeButton.setEnabled(false);
        if (selection != null) {
            removeButton.setEnabled(selection.size() > 0);
        }
        removeAllButton.setEnabled(ignoredUris.size() > 0);
    }

    @Override
    public boolean performOk() {
        updatePreference();
        refresh();

        return true;
    }

    @Override
    protected void performDefaults() {
        ignoredUris.clear();

        updatePreference();
        refresh();
    }

    private void updatePreference() {
        preferences.setIgnoreNotificationsURIs(ignoredUris);
        try {
            preferences.save();
        }
        catch (IOException ex) {
            logger.error("Error saving import notifications preferences!", ex);
        }
    }
}
