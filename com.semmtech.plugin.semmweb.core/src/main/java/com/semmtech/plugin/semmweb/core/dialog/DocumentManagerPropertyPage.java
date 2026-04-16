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
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
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
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.CorePluginImages;
import com.semmtech.plugin.semmweb.core.internal.navigator.SemanticProject;
import com.semmtech.plugin.semmweb.core.internal.navigator.SemanticProjectManager;
import com.semmtech.plugin.semmweb.core.jena.ontology.WorkspaceDocumentManagerConfiguration;
import com.semmtech.plugin.semmweb.core.jena.ontology.WorkspaceOntologySpec;
import com.semmtech.plugin.semmweb.core.jobs.SemanticProjectBuildJob;
import com.semmtech.plugin.semmweb.core.navigator.ImportType;
import com.semmtech.plugin.semmweb.core.preferences.DocumentManagerPreference;
import com.semmtech.plugin.semmweb.core.preferences.ModelsFolderPreference;
import com.semmtech.plugin.semmweb.core.util.ImportURLUtils;
import com.semmtech.plugin.semmweb.core.util.SemanticProjectUtils;
import com.semmtech.plugin.semmweb.core.util.URIs;
import com.semmtech.plugin.semmweb.core.wizards.DownloadOntModelWizard;
import com.semmtech.ui.plugin.util.Selections;
import com.semmtech.ui.plugin.viewers.TableViewerComparator;
import com.semmtech.ui.plugin.widgets.Widgets;


public class DocumentManagerPropertyPage extends AbstractProjectPropertyPage {

    private static Logger logger = Logger.getLogger(DocumentManagerPropertyPage.class);

    public static final String ID = "com.semmtech.plugin.semmweb.publication.pages.documentManager";

    private TableViewer viewer;
    private Table table;
    private Button deleteEntryButton;
    private Button editEntryButton;
    private Button addEntryButton;
    private Button downloadButton;
    private TableViewerComparator viewerComparator;

    private static final int COLUMN_PUBLIC_URI = 0;
    private static final int COLUMN_PREFIX = 1;
    private static final int COLUMN_EXTERNAL_ALT_URL = 2;
    private static final int COLUMN_WORKSPACE_ALT_URL = 3;

    private static IContainer previousContainer;
    private WorkspaceDocumentManagerConfiguration configuration;
    private String preLoadingValue = DocumentManagerPreference.VALUE_PRE_LOADING_RDFOWL;
    private List<WorkspaceOntologySpec> items = Lists.newArrayList();

    private Button processImportsCheckbox;
    private Button alwaysRadio;
    private Button rdfowlRadio;
    private Button neverRadio;

    // private Button resetButton;

    /**
     * Keep track of the ImportType until save. This because the ImportType
     * information are available only after a project rebuild that is done only
     * after save.
     */
    private Map<String, ImportType> editedOntologySpec;

    public DocumentManagerPropertyPage() {
        super();
        editedOntologySpec = Maps.newHashMap();
    }

    @Override
    protected Control createContents(Composite parent) {
        Composite top = new Composite(parent, SWT.LEFT | SWT.TOP);
        GridLayoutFactory.fillDefaults().numColumns(2).extendedMargins(0, 8, 0, 8).spacing(9, 8)
                .applyTo(top);
        top.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        createImportExportControls(top);

        Label label = new Label(top, SWT.NONE);
        label.setText("Ontologies:");
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

        TableColumn column = new TableColumn(table, SWT.NONE, COLUMN_PUBLIC_URI);
        column.setText("Public URI");
        column.setWidth(200);
        column.setResizable(true);
        column.setMoveable(false);
        column.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                viewerComparator.setColumn(COLUMN_PUBLIC_URI);
                refreshViewer();
            }
        });

        column = new TableColumn(table, SWT.NONE, COLUMN_PREFIX);
        column.setText("Prefix");
        column.setWidth(60);
        column.setResizable(true);
        column.setMoveable(false);
        column.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                viewerComparator.setColumn(COLUMN_PREFIX);
                refreshViewer();
            }
        });

        column = new TableColumn(table, SWT.NONE, COLUMN_EXTERNAL_ALT_URL);
        column.setText("Alternative URL");
        column.setWidth(180);
        column.setResizable(true);
        column.setMoveable(false);
        column.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                viewerComparator.setColumn(COLUMN_EXTERNAL_ALT_URL);
                refreshViewer();
            }
        });

        column = new TableColumn(table, SWT.NONE, COLUMN_WORKSPACE_ALT_URL);
        column.setText("Workspace model");
        column.setWidth(180);
        column.setResizable(true);
        column.setMoveable(false);
        column.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                viewerComparator.setColumn(COLUMN_WORKSPACE_ALT_URL);
                refreshViewer();
            }
        });

        viewer.setContentProvider(new ArrayContentProvider());
        viewer.setLabelProvider(new StyledCellLabelProvider() {

            private String getColumnText(Object element, int columnIndex) {
                if (element instanceof WorkspaceOntologySpec) {
                    WorkspaceOntologySpec item = (WorkspaceOntologySpec) element;
                    String publicUri = item.getPublicURI();

                    if (columnIndex == COLUMN_PUBLIC_URI) {
                        return publicUri;
                    }
                    else if (columnIndex == COLUMN_PREFIX) {
                        return item.getPrefix();
                    }
                    else if (columnIndex == COLUMN_EXTERNAL_ALT_URL) {
                        String altURL = item.getExternalAltURL();
                        if (altURL != null) {
                            ImportType importType = ImportURLUtils
                                    .guessImportTypeExternalUrl(altURL);
                            return ImportURLUtils.getAltUrlText(importType, altURL);
                        }
                        return new String();
                    }
                    else if (columnIndex == COLUMN_WORKSPACE_ALT_URL) {
                        String altURL = item.getWorkspaceAltURL();
                        if (altURL != null) {
                            if (editedOntologySpec.containsKey(publicUri)) {
                                ImportType importType = editedOntologySpec.get(publicUri);
                                if (importType == null) {
                                    // If we couldn't guess the import type,
                                    // display the Alt URL as is.
                                    return altURL;
                                }
                                return ImportURLUtils.getAltUrlText(importType, altURL);
                            }
                            ImportType importType = ImportURLUtils.guessImportTypeWorkspaceUrl(
                                    altURL, getProject());
                            return ImportURLUtils.getAltUrlText(importType, altURL);
                        }
                        return new String();
                    }

                }
                return null;
            }

            private Image getColumnImage(Object element, int columnIndex) {
                if (columnIndex == COLUMN_PREFIX) {
                    return CorePlugin.getDefault().getImage(CorePluginImages.IMG_ONTOLOGY);
                }
                return null;
            }

            @Override
            public void update(ViewerCell cell) {
                Object element = cell.getElement();
                if (element instanceof WorkspaceOntologySpec) {
                    StyledString styledText = new StyledString();
                    String text = getColumnText(element, cell.getColumnIndex());
                    Image image = getColumnImage(element, cell.getColumnIndex());
                    cell.setImage(image);
                    cell.setText(text);
                    cell.setStyleRanges(styledText.getStyleRanges());
                    super.update(cell);
                }
            }
        });
        viewer.addSelectionChangedListener(new ISelectionChangedListener() {

            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                ISelection selection = viewer.getSelection();
                List<WorkspaceOntologySpec> selected = Selections.retrieveAllAsType(selection,
                        WorkspaceOntologySpec.class);

                deleteEntryButton.setEnabled(selected.size() > 0);
                editEntryButton.setEnabled(selected.size() == 1);
                // resetButton.setEnabled(selected.size() > 0);
                downloadButton.setEnabled(selected.size() > 0);
            }
        });

        viewerComparator = new TableViewerComparator() {

            @Override
            public int compare(Viewer viewer, Object e1, Object e2) {
                if (e1 instanceof WorkspaceOntologySpec && e2 instanceof WorkspaceOntologySpec) {
                    WorkspaceOntologySpec o1 = (WorkspaceOntologySpec) e1;
                    WorkspaceOntologySpec o2 = (WorkspaceOntologySpec) e2;
                    return compareItems(o1, o2);
                }
                return 0;
            }

            private int compareItems(WorkspaceOntologySpec o1, WorkspaceOntologySpec o2) {
                int rc = 0;
                if (columnIndex == COLUMN_PUBLIC_URI) {
                    rc = o1.getPublicURI().compareToIgnoreCase(o2.getPublicURI());
                }
                else if (columnIndex == COLUMN_EXTERNAL_ALT_URL) {
                    String o1URL = o1.getExternalAltURL();
                    String o2URL = o2.getExternalAltURL();
                    if (!Strings.isNullOrEmpty(o1URL) && !Strings.isNullOrEmpty(o2URL)) {
                        rc = o1URL.compareToIgnoreCase(o2URL);
                    }
                    else if (!Strings.isNullOrEmpty(o1URL)) {
                        rc = 1;
                    }
                    else {
                        rc = -1;
                    }
                }
                else if (columnIndex == COLUMN_WORKSPACE_ALT_URL) {
                    String o1URL = o1.getWorkspaceAltURL();
                    String o2URL = o2.getWorkspaceAltURL();
                    if (!Strings.isNullOrEmpty(o1URL) && !Strings.isNullOrEmpty(o2URL)) {
                        rc = o1URL.compareToIgnoreCase(o2URL);
                    }
                    else if (!Strings.isNullOrEmpty(o1URL)) {
                        rc = 1;
                    }
                    else {
                        rc = -1;
                    }
                }
                else if (columnIndex == COLUMN_PREFIX) {
                    if (!Strings.isNullOrEmpty(o1.getPrefix())
                            && !Strings.isNullOrEmpty(o2.getPrefix())) {
                        rc = o1.getPrefix().compareToIgnoreCase(o2.getPrefix());
                    }
                    else if (!Strings.isNullOrEmpty(o1.getPrefix())) {
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

        // resetButton = new Button(buttonsComposite, SWT.PUSH);
        // layoutData = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
        // layoutData.verticalIndent = 8;
        // resetButton.setText("Reset");
        // resetButton.setLayoutData(layoutData);
        // resetButton.addListener(SWT.Selection, new Listener() {
        // @Override
        // public void handleEvent(Event event) {
        // resetOntologySpec();
        // }
        // });
        // resetButton.setEnabled(false);

        downloadButton = new Button(buttonsComposite, SWT.NONE);
        layoutData = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
        layoutData.verticalIndent = 8;
        downloadButton.setLayoutData(layoutData);
        downloadButton.setText("Download...");
        downloadButton.setEnabled(false);
        downloadButton.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                ISelection selection = viewer.getSelection();
                for (WorkspaceOntologySpec spec : Selections.retrieveAllAsType(selection,
                        WorkspaceOntologySpec.class)) {
                    downloadNamespace(spec);
                }
            }
        });

        Group preLoadingGroup = new Group(top, SWT.NONE);
        preLoadingGroup.setLayout(new GridLayout(3, false));
        preLoadingGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
        preLoadingGroup.setText("Pre-Loading");

        label = new Label(preLoadingGroup, SWT.NONE);
        label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));
        label.setText("Should namespaces defined above be pre-loaded when the prefix is found in the prefix list?");

        neverRadio = new Button(preLoadingGroup, SWT.RADIO);
        neverRadio.setText("never");
        neverRadio.setSelection(DocumentManagerPreference.VALUE_PRE_LOADING_NEVER
                .equals(preLoadingValue));

        rdfowlRadio = new Button(preLoadingGroup, SWT.RADIO);
        rdfowlRadio.setText("only RDF/OWL namespaces");
        rdfowlRadio.setSelection(DocumentManagerPreference.VALUE_PRE_LOADING_RDFOWL
                .equals(preLoadingValue));

        alwaysRadio = new Button(preLoadingGroup, SWT.RADIO);
        alwaysRadio.setText("all namespaces defined above");
        alwaysRadio.setSelection(DocumentManagerPreference.VALUE_PRE_LOADING_ALLWAYS
                .equals(preLoadingValue));

        Group importsGroup = new Group(top, SWT.NONE);
        importsGroup.setLayout(new GridLayout(1, false));
        importsGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
        importsGroup.setText("Imports");

        processImportsCheckbox = new Button(importsGroup, SWT.CHECK);
        processImportsCheckbox.setText("Automatically process all import ontologies");
        processImportsCheckbox.setSelection(configuration.getProcessImports());

        refreshAll();

        return top;
    }

    // protected void resetOntologySpec() {
    // ISelection selection = viewer.getSelection();
    // List<WorkspaceOntologySpec> selectedItems =
    // Selections.retrieveAllAsType(selection,
    // WorkspaceOntologySpec.class);
    //
    // if (selectedItems.size() == 1) {
    // WorkspaceOntologySpec selected = selectedItems.get(0);
    // String questionText = String.format(
    // "Are you sure you want to reset the ontology for URI <%s>?",
    // selected.getPublicURI());
    // MessageDialog dialog = new MessageDialog(null, "Reset", null,
    // questionText,
    // MessageDialog.QUESTION, new String[] { "Yes", "No" }, 1);
    // if (dialog.open() == Window.OK) {
    // resetAltURL(selected);
    // }
    // }
    // else if (selectedItems.size() > 1) {
    // String questionText =
    // String.format("Are you sure you want to reset these %s URIs?",
    // selectedItems.size());
    // MessageDialog dialog = new MessageDialog(null, "Reset", null,
    // questionText,
    // MessageDialog.QUESTION, new String[] { "Yes", "No" }, 1);
    // if (dialog.open() == 0) {
    // for (WorkspaceOntologySpec selected : selectedItems) {
    // resetAltURL(selected);
    // }
    // }
    // }
    // if (selectedItems.size() > 0) {
    // refreshViewer();
    // }
    // }
    //
    // private void resetAltURL(WorkspaceOntologySpec spec) {
    // String altUrl = null;
    // for (WorkspaceOntologySpec mapping :
    // PublicationMappingsPreference.getPublicationMappings()) {
    // if (spec.getPublicURI().equals(mapping.getPublicURI())) {
    // altUrl = mapping.getAltURL();
    // }
    // }
    // spec.setAltURL(altUrl);
    // }

    private void createImportExportControls(Composite parent) {
        Group policyGroup = new Group(parent, SWT.NONE);
        policyGroup.setText("Document Manager Policy");
        GridLayout layout = new GridLayout(2, false);
        layout.marginHeight = 8;
        layout.horizontalSpacing = 9;
        layout.marginWidth = 6;
        layout.verticalSpacing = 8;
        policyGroup.setLayout(layout);

        policyGroup
                .setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 2, 1));

        Label label = new Label(policyGroup, SWT.WRAP);
        label.setText("All settings for the document manager can be set using an RDF/OWL ontology file. You can directly import or export such a file here:");
        GridData layoutData = new GridData(GridData.FILL, GridData.CENTER, false, false, 2, 1);
        layoutData.minimumWidth = 200;
        layoutData.widthHint = 200;
        label.setLayoutData(layoutData);

        Composite policyFileComposite = new Composite(policyGroup, SWT.NONE);
        layout = new GridLayout(3, false);
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        policyFileComposite.setLayout(layout);
        policyFileComposite.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false,
                2, 1));

        Label spacer = new Label(policyFileComposite, SWT.NONE);
        spacer.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 1));

        Button importButton = new Button(policyFileComposite, SWT.PUSH);
        importButton.setText("Import...");
        layoutData = new GridData(GridData.FILL, GridData.CENTER, false, false);
        layoutData.widthHint = 103;
        importButton.setLayoutData(layoutData);
        importButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                handleImport();
            }
        });

        Button exportButton = new Button(policyFileComposite, SWT.PUSH);
        exportButton.setText("Export...");
        layoutData = new GridData(GridData.FILL, GridData.CENTER, false, false);
        layoutData.widthHint = 103;
        exportButton.setLayoutData(layoutData);
        exportButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                handleExport();
            }
        });
    }

    @Override
    public void setElement(IAdaptable element) {
        super.setElement(element);

        IProject project = getProject();
        if (project != null) {
            DocumentManagerPreference preferences = DocumentManagerPreference.fromProject(project);
            configuration = preferences.getDocumentManagerConfig();
            preLoadingValue = preferences.getPreLoadingValue();
            refreshAll();
        }
    }

    private void refreshAll() {
        if (!Widgets.isNullOrDisposed(neverRadio)) {
            neverRadio.setSelection(DocumentManagerPreference.VALUE_PRE_LOADING_NEVER
                    .equals(preLoadingValue));
            rdfowlRadio.setSelection(DocumentManagerPreference.VALUE_PRE_LOADING_RDFOWL
                    .equals(preLoadingValue));
            alwaysRadio.setSelection(DocumentManagerPreference.VALUE_PRE_LOADING_ALLWAYS
                    .equals(preLoadingValue));
        }
        if (!Widgets.isNullOrDisposed(processImportsCheckbox)) {
            processImportsCheckbox.setSelection(configuration.getProcessImports());
        }
        updateViewer();
    }

    protected void downloadNamespace(WorkspaceOntologySpec spec) {
        if (spec == null) {
            return;
        }

        if (!spec.getPublicURI().startsWith("http://")) {
            return;
        }

        IProject project = getProject();
        if (previousContainer == null) {
            previousContainer = project;
        }

        // MIKE: Corrected this, because previously alt URL was always used even
        // if empty!
        String uri = spec.getPublicURI();
        String altUrl = spec.getExternalAltURL();
        if (!Strings.isNullOrEmpty(altUrl) && !URIs.hasFileScheme(altUrl)) {
            uri = altUrl;
        }
        IContainer container = project;
        String modelsPath = ModelsFolderPreference.fromProject(project).getModelsFolderPath();
        if (!Strings.isNullOrEmpty(modelsPath)) {
            IContainer modelsFolder = (IContainer) project.findMember(modelsPath);
            if (modelsFolder != null) {
                container = modelsFolder;
            }
        }
        DownloadOntModelWizard wizard = new DownloadOntModelWizard(uri, container, spec.getPrefix());
        WizardDialog dialog = new WizardDialog(getShell(), wizard);
        dialog.create();
        if (dialog.open() == Window.OK) {
            if (MessageDialog
                    .openQuestion(
                            getShell(),
                            "Location URI",
                            "Do you wish to set the alternate location of this namespace to the location of the downloaded namespace?")) {
                spec.setWorkspaceAltURL(wizard.getLocationURI());
                previousContainer = wizard.getFolder();
                refreshViewer();
            }
        }
    }

    protected void handleImport() {
        FileSelectionDialog dialog = new FileSelectionDialog(getShell(), "Import Settings",
                "Select the RDF/OWL ontology file containing the policy.");
        dialog.setExtensions(new String[] { "ttl", "rdf", "owl", "nt" });
        if (dialog.open() == Window.OK) {
            String filename = dialog.getFilename();
            try {
                configuration = WorkspaceDocumentManagerConfiguration.read(filename);
                refreshAll();
            }
            catch (IOException ex) {
                MessageDialog.openError(getShell(), "Import Settings",
                        "An error occurred trying to read from the specified policy file.");
            }
        }
    }

    protected void handleExport() {
        MessageDialog.openInformation(getShell(), "TODO", "Not yet implemented.");
    }

    private void updateViewer() {
        if (!Widgets.isNullOrDisposed(table) && viewer != null) {
            items = configuration.listWorkspaceOntologySpecs();
            viewer.setInput(items);
            viewer.setItemCount(items.size());
        }
    }

    private void refreshViewer() {
        table.setSortDirection(viewerComparator.getDirection());
        table.setSortColumn(table.getColumn(viewerComparator.getColumnIndex()));
        viewer.refresh();
    }

    protected boolean duplicatePublicURI(String uri) {
        for (WorkspaceOntologySpec item : items) {
            if (item.getPublicURI().equals(uri)) {
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
        ImportType specImportType = null;
        if (spec != null) {
            String publicURI = spec.getPublicURI();
            if (editedOntologySpec.containsKey(publicURI)) {
                specImportType = editedOntologySpec.get(publicURI);
            }
            else {
                specImportType = getImportType(publicURI);
            }
        }

        boolean done = false;

        while (!done) {
            String title = "Ontology";
            String message = "Add a new namespace by specifying its details.";

            WorkspaceOntologySpecDialog dialog = new WorkspaceOntologySpecDialog(getShell(), title,
                    message);
            if (spec != null) {
                dialog.setOntologySpec(spec);
                if (specImportType != null) {
                    dialog.setImportType(specImportType);
                }
            }

            // Get list of available project namespaces and add to dialog:
            IProject project = getProject();
            List<WorkspaceOntologySpec> options = SemanticProjectUtils.getAllSpecs(project);
            dialog.setPossibleOptions(options, null, false);

            if (dialog.open() != Window.OK) {
                done = true;
            }
            else {
                spec = dialog.getOntologySpec();
                if (original != null && original.equals(spec)) {
                    done = true;
                }
                else {
                    specImportType = dialog.getImportType();
                    String publicUri = spec.getPublicURI();
                    if (duplicatePublicURI(publicUri)
                            && (original == null || !original.getPublicURI().equals(publicUri))) {
                        if (!MessageDialog
                                .openQuestion(
                                        getShell(),
                                        "Duplicate",
                                        String.format(
                                                "The ontology with URI <%s> has already been defined. Unless you specify a different URI, adding the current mapping will be cancelled. Do you wish to specify a different URI?",
                                                publicUri))) {
                            done = true;
                        }
                    }
                    else {
                        if (original != null) {
                            items.remove(original);
                        }
                        items.add(spec);
                        editedOntologySpec.put(spec.getPublicURI(), specImportType);
                        done = true;
                    }
                }
            }
        }
        refreshViewer();
    }

    protected void deleteOntologySpec() {
        ISelection selection = viewer.getSelection();
        List<WorkspaceOntologySpec> specs = Selections.retrieveAllAsType(selection,
                WorkspaceOntologySpec.class);
        if (specs.size() == 1) {
            WorkspaceOntologySpec spec = specs.get(0);
            String questionText = String.format(
                    "Are you sure you want to delete the ontology for URI <%s>?",
                    spec.getPublicURI());
            MessageDialog dialog = new MessageDialog(null, "Delete", null, questionText,
                    MessageDialog.QUESTION, new String[] { "Yes", "No" }, 1);

            if (dialog.open() == Window.OK) {
                items.remove(spec);
                editedOntologySpec.remove(spec.getPublicURI());
                refreshViewer();
            }
        }
        else if (specs.size() > 1) {
            String questionText = String.format("Are you sure you want to delete these %s URIs?",
                    specs.size());
            MessageDialog dialog = new MessageDialog(null, "Delete", null, questionText,
                    MessageDialog.QUESTION, new String[] { "Yes", "No" }, 1);

            if (dialog.open() == Window.OK) {
                items.removeAll(specs);

                for (WorkspaceOntologySpec spec : specs) {
                    editedOntologySpec.remove(spec.getPublicURI());
                }

                refreshViewer();
            }
        }
    }

    protected WorkspaceDocumentManagerConfiguration rebuildConfiguration() {
        WorkspaceDocumentManagerConfiguration config = new WorkspaceDocumentManagerConfiguration();
        config.setProcessImports(processImportsCheckbox.getSelection());
        for (WorkspaceOntologySpec spec : items) {
            config.addOntologySpec(spec);
        }
        return config;
    }

    @Override
    protected void performDefaults() {
        configuration = DocumentManagerPreference.getDefaultConfiguration();
        preLoadingValue = DocumentManagerPreference.VALUE_PRE_LOADING_RDFOWL;

        IProject project = getProject();
        DocumentManagerPreference preferences = DocumentManagerPreference.fromProject(project);
        preferences.setDocumentManagerConfig(configuration);
        preferences.setPreLoading(preLoadingValue);

        try {
            preferences.save();
        }
        catch (IOException ex) {
            logger.error("Error saving the document manager preferences, see inner exception", ex);
        }
        refreshAll();
    }

    @Override
    public boolean performCancel() {
        IProject project = getProject();
        DocumentManagerPreference preferences = DocumentManagerPreference.fromProject(project);
        configuration = preferences.getDocumentManagerConfig();
        editedOntologySpec.clear();
        refreshAll();
        return true;
    }

    @Override
    public boolean performOk() {
        configuration = rebuildConfiguration();
        IProject project = getProject();
        DocumentManagerPreference preferences = DocumentManagerPreference.fromProject(project);
        preferences.setDocumentManagerConfig(configuration);

        if (rdfowlRadio.getSelection()) {
            preferences.setPreLoading(DocumentManagerPreference.VALUE_PRE_LOADING_RDFOWL);
        }
        else if (alwaysRadio.getSelection()) {
            preferences.setPreLoading(DocumentManagerPreference.VALUE_PRE_LOADING_ALLWAYS);
        }
        else {
            preferences.setPreLoading(DocumentManagerPreference.VALUE_PRE_LOADING_NEVER);
        }

        try {
            preferences.save();
            SemanticProjectBuildJob rebuild = new SemanticProjectBuildJob(project);
            rebuild.setUser(false);
            rebuild.schedule();
        }
        catch (IOException ex) {
            logger.error("Error saving the document manager preferences, see inner exception", ex);
            return false;
        }

        editedOntologySpec.clear();
        return true;
    }

    @Override
    protected void performApply() {
        performOk();
        refreshViewer();
    }

    private SemanticProject semanticProject;

    public ImportType getImportType(String publicUri) {
        if (semanticProject == null) {
            semanticProject = SemanticProjectManager.getSemanticProjectManager(getProject())
                    .obtainProject();
        }
        return semanticProject.getImportType(publicUri);
    }
}
