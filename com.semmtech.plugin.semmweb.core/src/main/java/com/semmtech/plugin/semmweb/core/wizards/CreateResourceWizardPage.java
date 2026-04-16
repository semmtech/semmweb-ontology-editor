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


import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.Widget;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.dialog.URIValidator;
import com.semmtech.plugin.semmweb.core.dialog.UniqueResourceURIValidator;
import com.semmtech.plugin.semmweb.core.fieldassist.PrefixContentProposalProvider;
import com.semmtech.plugin.semmweb.core.model.IModelProvider;
import com.semmtech.plugin.semmweb.core.preferences.LabelsPreference;
import com.semmtech.plugin.semmweb.core.util.ResourceURIUtil;
import com.semmtech.plugin.semmweb.core.viewers.ResourceViewerToolTipSupport;
import com.semmtech.plugin.semmweb.core.widgets.trees.ResourceTreeData;
import com.semmtech.ui.plugin.util.Selections;
import com.semmtech.ui.plugin.viewers.LazyTreeContentProvider;
import com.semmtech.ui.plugin.widgets.Widgets;


public class CreateResourceWizardPage extends WizardPage implements Listener {
    private static final int HEADER_WIDTH = 95;

    private static final String PREFIX_EMPTY = "<empty>";
    private static final String PREFIX_BASE = "<base>";
    private static final String PREFIX_NEW = "<new>";

    private static boolean openEditor = true;

    private final OntModel model;
    private final String baseUri;
    private int uriFormat;

    private Model baseModel;
    private List<String> prefixes;

    private Tree tree;
    private TreeViewer treeViewer;
    private Composite container;
    private Composite typeComposite;
    private Composite placeholderComposite;
    private Composite namespaceComposite;
    private Composite uriComposite;
    private Text uriText;
    private Text nameText;
    private Combo ontologyCombo;
    private Button anonymousCheckbox;
    private Button openEditorCheckbox;
    private Button generateUuidCheckbox;

    private URIValidator uriValidator;
    private UniqueResourceURIValidator resourceValidator;

    private boolean generateUuidEnabled = true;
    private boolean generateUuid;
    private boolean anonymousEnabled;
    private boolean isAnonymous;

    private boolean openEditorEnabled;
    private boolean editResourceType;

    private String nameValue = "";
    private String previousUri = "";
    private String uriValue = "";
    private String calculatedUri;

    private String prefixKey;
    private String prefix;
    private String namespaceUri;
    private String uuid;

    private Resource selectedType = null;

    private String preferredUri;
    private boolean ignoreChanges;

    public CreateResourceWizardPage(String pageName, OntModel model) {
        this(pageName, model, RDFS.Resource);
    }

    public CreateResourceWizardPage(String pageName, OntModel model, Resource type) {
        super(pageName);
        setTitle("Resource");
        setDescription("Create a new resource within the ontology.");

        this.model = model;
        this.selectedType = model.getResource(type.getURI());
        this.baseUri = CorePlugin.getDefault().getActiveModelProvider().getBaseURI();
        this.resourceValidator = new UniqueResourceURIValidator(model, baseUri);
        this.uriValidator = new URIValidator(model, baseUri);
    }

    @Override
    public void createControl(Composite parent) {
        container = new Composite(parent, SWT.NONE);
        container.setLayout(new GridLayout(2, false));

        Label label = new Label(container, SWT.NONE);
        if (LabelsPreference.showReadableLabels()) {
            label.setText("Please select the type of resource you would like to create, and provide a name for this resource.");
        }
        else {
            label.setText("Please select the type of resource you would like to create, and provide a URI for this resource.");
        }
        GridDataFactory.fillDefaults().span(2, 1).applyTo(label);
        Label spacer = new Label(container, SWT.NONE);
        GridDataFactory.swtDefaults().span(2, 1).hint(SWT.DEFAULT, 4).applyTo(spacer);

        placeholderComposite = new Composite(container, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(1).margins(0, 0).applyTo(placeholderComposite);
        GridDataFactory.fillDefaults().grab(true, false).span(2, 1).applyTo(placeholderComposite);

        // Resource Type
        if (!editResourceType) {
            createResourceTypeLabels();
        }
        else {
            createResourceTypeControls();
        }

        // Namespace
        createNamespaceCombo();

        if (LabelsPreference.showReadableLabels()) {
            // Name
            label = new Label(container, SWT.NONE);
            label.setText("Name:");
            GridDataFactory.swtDefaults().hint(HEADER_WIDTH, SWT.DEFAULT).applyTo(label);

            nameText = new Text(container, SWT.BORDER);
            nameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            nameText.addListener(SWT.Modify, this);
            nameText.setFocus();
            nameText.setSelection(nameText.getText().length());
        }
        else {
            // URI
            createURIControls();
        }

        spacer = new Label(container, SWT.NONE);
        GridDataFactory.swtDefaults().span(2, 1).hint(SWT.DEFAULT, 3).applyTo(spacer);
        Label offset = new Label(container, SWT.NONE);
        GridDataFactory.swtDefaults().hint(HEADER_WIDTH, SWT.DEFAULT).applyTo(offset);
        Composite checkboxComposite = new Composite(container, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(1).spacing(0, 4).margins(0, 0)
                .applyTo(checkboxComposite);
        GridDataFactory.fillDefaults().applyTo(checkboxComposite);

        // Checkboxes
        generateUuidCheckbox = new Button(checkboxComposite, SWT.CHECK);
        generateUuidCheckbox.setText("Generate unique identifier");
        generateUuidCheckbox.setSelection(generateUuid);
        generateUuidCheckbox.addListener(SWT.Selection, this);

        anonymousCheckbox = new Button(checkboxComposite, SWT.CHECK);
        anonymousCheckbox.setText("Make resource anonymous");
        anonymousCheckbox.setSelection(isAnonymous);
        anonymousCheckbox.addListener(SWT.Selection, this);

        openEditorCheckbox = new Button(checkboxComposite, SWT.CHECK);
        openEditorCheckbox.setText("Open resource after 'Finish'");
        openEditorCheckbox.setSelection(openEditor);
        if (!openEditorEnabled) {
            openEditorCheckbox.setSelection(false);
            openEditorCheckbox.setEnabled(false);
        }
        openEditorCheckbox.addListener(SWT.Selection, this);

        updateControls();
        setPageComplete(false);
        setControl(container);
    }

    private void createResourceTypeComposite() {
        Widgets.disposeIfExists(typeComposite);
        typeComposite = new Composite(placeholderComposite, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(2).spacing(5, 0).margins(0, 0)
                .applyTo(typeComposite);
        GridDataFactory.fillDefaults().grab(true, false).span(2, 1).applyTo(typeComposite);
    }

    private void createResourceTypeControls() {
        createResourceTypeComposite();

        Label label = new Label(typeComposite, SWT.NONE);
        label.setText("Resource type:");
        GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.TOP).indent(0, 5)
                .hint(HEADER_WIDTH, 25).applyTo(label);

        tree = new Tree(typeComposite, SWT.BORDER | SWT.SINGLE | SWT.FULL_SELECTION | SWT.VIRTUAL);
        tree.setLinesVisible(true);
        tree.setHeaderVisible(true);

        GridDataFactory.fillDefaults().hint(SWT.DEFAULT, 90).grab(true, false).applyTo(tree);

        TreeColumn treeColumn = new TreeColumn(tree, SWT.LEFT);
        treeColumn.setText("Resource");
        treeColumn.setWidth(180);

        treeColumn = new TreeColumn(tree, SWT.LEFT);
        treeColumn.setText("rdf:type");
        treeColumn.setWidth(100);

        treeColumn = new TreeColumn(tree, SWT.LEFT);
        treeColumn.setText("rdfs:label");
        treeColumn.setWidth(110);

        treeViewer = new TreeViewer(tree);
        treeViewer.setContentProvider(new LazyTreeContentProvider() {
            @Override
            public void updateElement(Object parent, int index) {
                if (parent instanceof Model) {
                    Model model = (Model) parent;
                    ResourceTreeData element = new ResourceTreeData(selectedType);
                    treeViewer.replace(parent, index, element);
                    // TODO: Basic count, is this robust enough?
                    int itemCount = model
                            .listStatements(new SimpleSelector(null, RDFS.subClassOf, selectedType))
                            .toList().size();
                    treeViewer.setChildCount(element, itemCount);
                }
                else if (parent instanceof ResourceTreeData) {
                    Resource superClass = (Resource) parent;
                    List<Resource> resources = Lists.newArrayList();
                    for (Statement statement : model.listStatements(
                            new SimpleSelector(null, RDFS.subClassOf, superClass)).toList()) {
                        Resource subClass = statement.getSubject();
                        if (!resources.contains(subClass)) {
                            resources.add(subClass);
                        }
                    }
                    // TODO: Order subclasses
                    Resource resource = null;
                    if (index < resources.size()) {
                        resource = resources.get(index);
                    }

                    if (resource != null) {
                        ResourceTreeData element = new ResourceTreeData(resource);
                        element.setParent((ResourceTreeData) parent);
                        treeViewer.replace(parent, index, element);
                        int itemCount = model
                                .listStatements(new SimpleSelector(null, RDFS.subClassOf, element))
                                .toList().size();
                        treeViewer.setChildCount(element, itemCount);
                    }
                }
            }
        });

        IModelProvider provider = CorePlugin.getDefault().getActiveModelProvider();
        Preconditions.checkNotNull(provider);
        final LabelProvider labelProvider = provider.getLabelProvider();
        treeViewer.setLabelProvider(new CellLabelProvider() {

            @Override
            public void update(ViewerCell cell) {
                int columnIndex = cell.getColumnIndex();
                cell.setText(getElementText(cell.getElement(), columnIndex));
                cell.setImage(getElementImage(cell.getElement(), columnIndex));
            }

            public Image getElementImage(Object element, int columnIndex) {
                if (columnIndex == 0) {
                    return labelProvider.getImage(element);
                }
                return null;
            }

            public String getElementText(Object element, int columnIndex) {
                if (element instanceof Resource) {
                    Resource resource = (Resource) element;
                    switch (columnIndex) {
                    case 0:
                        return labelProvider.getText(resource);
                    case 1:
                        Resource type = resource.getPropertyResourceValue(RDF.type);
                        return (type == null) ? "" : labelProvider.getText(type);
                    case 2:
                        String label = "";
                        Statement labelStatement = resource.getProperty(RDFS.label);
                        if (labelStatement != null && labelStatement.getObject() != null) {
                            label = labelProvider.getText(labelStatement.getObject());
                        }
                        return label;
                    default:
                        return null;
                    }
                }
                return null;
            }

            @Override
            public int getToolTipDisplayDelayTime(Object object) {
                return 80;
            }

            @Override
            public int getToolTipTimeDisplayed(Object object) {
                return 10000;
            }

            @Override
            public Point getToolTipShift(Object object) {
                return new Point(8, 10);
            }

            @Override
            public String getToolTipText(Object element) {
                if (element instanceof Resource) {
                    return ((Resource) element).toString();
                }
                return null;
            }
        });

        treeViewer.setUseHashlookup(true);
        treeViewer.setInput(model);
        ResourceViewerToolTipSupport.enableFor(treeViewer, CorePlugin.getDefault()
                .getActiveModelProvider());

        tree.setItemCount(1);
        treeViewer.expandToLevel(2);
        treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                if (Selections.hasFirstOfType(event.getSelection(), Resource.class)) {
                    selectedType = Selections.retrieveFirstAsType(event.getSelection(),
                            Resource.class);
                    validatePage();
                }
            }
        });
        ISelection selection = new StructuredSelection(new ResourceTreeData(selectedType));
        treeViewer.setSelection(selection);
    }

    private void createResourceTypeLabels() {
        createResourceTypeComposite();

        Label label = new Label(typeComposite, SWT.NONE);
        label.setText("Resource type:");
        GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.CENTER).indent(0, 5)
                .hint(HEADER_WIDTH, 25).applyTo(label);

        IModelProvider provider = CorePlugin.getDefault().getActiveModelProvider();
        Preconditions.checkNotNull(provider);
        final LabelProvider labelProvider = provider.getLabelProvider();

        Composite inner = new Composite(typeComposite, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(3).margins(0, 4).applyTo(inner);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(inner);

        label = new Label(inner, SWT.NONE);
        label.setImage(labelProvider.getImage(selectedType));
        GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.CENTER).applyTo(label);

        label = new Label(inner, SWT.NONE);
        label.setText(labelProvider.getText(selectedType));
        GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.CENTER).applyTo(label);

        Link editLink = new Link(inner, SWT.NONE);
        GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.CENTER).applyTo(editLink);
        editLink.setText(" (<a>Edit</a>)");
        editLink.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                createResourceTypeControls();
                resizeShell();
            }
        });
    }

    private void createNamespaceCombo() {
        namespaceComposite = new Composite(container, SWT.NONE);
        GridLayoutFactory.swtDefaults().numColumns(2).margins(0, 0).applyTo(namespaceComposite);

        Label namespaceLabel = new Label(namespaceComposite, SWT.NONE);
        namespaceLabel.setText("Namespace:");
        GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.CENTER)
                .hint(HEADER_WIDTH, SWT.DEFAULT).applyTo(namespaceLabel);

        ontologyCombo = new Combo(namespaceComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
        ontologyCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        ontologyCombo.addListener(SWT.Selection, this);

        updateControls();
        refreshNamespaces();
    }

    private void createURIControls() {
        uriComposite = new Composite(container, SWT.NONE);
        GridLayoutFactory.swtDefaults().numColumns(2).margins(0, 0).applyTo(uriComposite);

        Label uriLabel = new Label(uriComposite, SWT.NONE);
        uriLabel.setText("URI:");
        GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.CENTER)
                .hint(HEADER_WIDTH, SWT.DEFAULT).applyTo(uriLabel);

        uriText = new Text(uriComposite, SWT.BORDER);
        uriText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        uriText.setEnabled(!isAnonymous && !generateUuid);
        uriText.addListener(SWT.Modify, this);

        Model sourceModel = baseModel;
        if (baseModel == null) {
            sourceModel = model.getBaseModel();
        }
        IContentProposalProvider proposalProvider = new PrefixContentProposalProvider(sourceModel);
        String alfabet = "abcdefghijklmnopqrstuvwxyz";
        char[] activationChars = (alfabet + alfabet.toUpperCase()).toCharArray();
        ContentProposalAdapter adapter = new ContentProposalAdapter(uriText,
                new TextContentAdapter(), proposalProvider, KeyStroke.getInstance(SWT.CTRL,
                        SWT.SPACE), activationChars);
        adapter.setEnabled(true);
        adapter.setPopupSize(new Point(300, 150));
        adapter.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);

        uriText.setFocus();
    }

    protected void updateControls() {
        if (!Widgets.isNullOrDisposed(namespaceComposite)) {
            boolean isVisible = generateUuid
                    || (LabelsPreference.showReadableLabels() && !generateUuid && !isAnonymous);
            GridDataFactory.fillDefaults().hint(SWT.DEFAULT, (isVisible ? SWT.DEFAULT : 0))
                    .span(2, 1).applyTo(namespaceComposite);
        }

        if (!Widgets.isNullOrDisposed(uriComposite)) {
            boolean isVisible = !LabelsPreference.showReadableLabels();
            GridDataFactory.fillDefaults().hint(SWT.DEFAULT, (isVisible ? SWT.DEFAULT : 0))
                    .span(2, 1).applyTo(uriComposite);

            boolean newState = (!isAnonymous && !generateUuid);
            if (newState != uriText.getEnabled()) {
                if (newState && !Strings.isNullOrEmpty(previousUri)) {
                    uriValue = previousUri;
                    uriText.setText(uriValue);
                }
                else {
                    if (!Strings.isNullOrEmpty(uriValue)) {
                        previousUri = uriValue;
                    }
                    else {
                        previousUri = "";
                    }
                    updateGeneratedURI();
                }
                ignoreChanges = true;
                ignoreChanges = false;
                uriText.setEnabled(newState);
            }

            if (isAnonymous && !Widgets.isNullOrDisposed(uriText) && uriText.isVisible()) {
                uriText.setText("");
            }
        }

        if (!Widgets.isNullOrDisposed(anonymousCheckbox)) {
            anonymousCheckbox.setEnabled(!generateUuid && anonymousEnabled);
        }

        if (!Widgets.isNullOrDisposed(generateUuidCheckbox)) {
            generateUuidCheckbox.setEnabled(!isAnonymous && generateUuidEnabled);
        }
    }

    private void updateGeneratedURI() {
        if (Strings.isNullOrEmpty(uuid)) {
            return;
        }
        if (prefix != null) {
            uriValue = String.format("%s:r%s", prefix, uuid);
            if (uriText != null) {
                uriText.setText(uriValue);
            }
        }
        else if (!Strings.isNullOrEmpty(namespaceUri)) {
            uriValue = String.format("%sr%s", namespaceUri, uuid);
            if (uriText != null) {
                uriText.setText(uriValue);
            }
        }
    }

    private void refreshNamespaces() {
        prefixes = Lists.newArrayList();
        if (baseModel == null) {
            baseModel = model;
        }
        prefixes = Lists.newArrayList(baseModel.getNsPrefixMap().keySet());
        Collections.sort(prefixes);
        if (!Strings.isNullOrEmpty(preferredUri) && !preferredUri.equals(baseUri)
                && baseModel.getNsURIPrefix(preferredUri) == null) {
            prefixes.add(0, PREFIX_NEW);
        }
        if (!Strings.isNullOrEmpty(baseUri) && baseModel.getNsURIPrefix(baseUri) == null) {
            prefixes.add(0, PREFIX_BASE);
        }

        String[] namespaceItems = new String[prefixes.size()];
        int selected = 0;
        for (int i = 0; i < prefixes.size(); i++) {
            String prefix = prefixes.get(i);
            if (PREFIX_EMPTY.equals(prefix)) {
                namespaceItems[i] = "";
            }
            else if (PREFIX_NEW.equals(prefix)) {
                namespaceItems[i] = String.format("<%s>", preferredUri);
                selected = i;
            }
            else if (PREFIX_BASE.equals(prefix)) {
                namespaceItems[i] = String.format("<%s>", baseUri);
                selected = i;
            }
            else {
                String uri = baseModel.getNsPrefixURI(prefix);
                if (uri.equals(preferredUri)) {
                    selected = i;
                }
                namespaceItems[i] = String.format("%s: <%s>", prefix, uri);
            }
        }
        ontologyCombo.setItems(namespaceItems);
        ontologyCombo.select(selected);

        String item = prefixes.get(selected);
        prefix = null;
        if (!PREFIX_BASE.equals(item) && !PREFIX_NEW.equals(item)) {
            prefix = item;
            namespaceUri = model.getNsPrefixURI(prefix);
        }
        else if (PREFIX_BASE.equals(item)) {
            namespaceUri = baseUri;
        }
        else if (PREFIX_NEW.equals(item)) {
            namespaceUri = preferredUri;
        }
    }

    /**
     * Validates the current wizard page, and informs user of invalid input(s).
     */
    void validatePage() {
        setPageComplete(isPageComplete());
    }

    @Override
    public boolean isPageComplete() {
        String uri = getURI();
        if (!Strings.isNullOrEmpty(uri)) {
            String errorMessage = null;
            // uri = "<" + uri + ">";
            if (!isAnonymous && uriValidator.isValid(uri) != null) {
                errorMessage = uriValidator.isValid(uri);
            }
            else if (!isAnonymous && resourceValidator.isValid(uri) != null) {
                errorMessage = "Resource with URI already exists!";
            }
            else if (LabelsPreference.showReadableLabels() && Strings.isNullOrEmpty(nameValue)
                    && !generateUuid) {
                errorMessage = "Resource must at least have a name!";
            }
            setErrorMessage(errorMessage);
            return (errorMessage == null);
        }
        else if (isAnonymous) {
            setErrorMessage(null);
            return true;
        }
        return false;
    }

    public void setPreferredNamespace(String uri) {
        preferredUri = uri;

        uriValidator = new URIValidator(model, uri);
        resourceValidator = new UniqueResourceURIValidator(model, uri);

        ignoreChanges = true;
        Model sourceModel = baseModel;
        if (baseModel == null) {
            sourceModel = model.getBaseModel();
        }
        prefix = sourceModel.getNsURIPrefix(uri);
        namespaceUri = uri;
        calculatedUri = null;
        if (!Widgets.isNullOrDisposed(uriText) && uriText.isEnabled()) {
            String oldUri = getURI();
            if (oldUri == null) {
                uriText.setText(new String());
            }
            else {
                int removeLastPosition = -1;
                int hashIndex = oldUri.lastIndexOf("#");
                if (hashIndex > removeLastPosition) {
                    removeLastPosition = hashIndex;
                }
                int slashIndex = oldUri.lastIndexOf("/");
                if (slashIndex > removeLastPosition) {
                    removeLastPosition = slashIndex;
                }
                int colonIndex = oldUri.lastIndexOf(":");
                if (colonIndex > removeLastPosition) {
                    removeLastPosition = colonIndex;
                }
                if (removeLastPosition >= 0) {
                    uriText.setText(oldUri.substring(removeLastPosition + 1));
                }
                // uriText.setText((prefix == null) ? "" : prefix + ":");
                uriText.setSelection(uriText.getText().length());
            }
        }
        if (!Widgets.isNullOrDisposed(ontologyCombo)) {
            refreshNamespaces();
        }
        ignoreChanges = false;
    }

    public String getLocalName() {
        if (generateUuid) {

        }
        return null;
    }

    public void setBaseModel(Model baseModel) {
        this.baseModel = baseModel;
    }

    /** Returns an absolute URI; never a prefixed version. */
    public String getURI() {
        if (isAnonymous) {
            return null;
        }
        if (calculatedUri == null) {
            String internalUri = null;
            boolean isAbsolute = true;
            if (generateUuid) {
                internalUri = String.format("%sr%s", namespaceUri, uuid);
            }
            else if (LabelsPreference.showReadableLabels()) {
                String localname = ResourceURIUtil.generateValidLocalname(nameValue, uriFormat);
                if (!Strings.isNullOrEmpty(localname)) {
                    internalUri = String.format("%s%s", namespaceUri,
                            Strings.nullToEmpty(localname));
                }
            }
            else if (uriValue.startsWith("<") && uriValue.endsWith(">")) {
                internalUri = uriValue.substring(1, uriValue.length() - 1);
            }
            else if (!uriValue.startsWith("<") && !uriValue.endsWith(">")) {
                internalUri = model.expandPrefix(uriValue);
            }
            else {
                isAbsolute = false;
            }

            if (internalUri != null && !Strings.isNullOrEmpty(internalUri)) {
                String expandedUri = internalUri;
                if (!isAbsolute) {
                    expandedUri = model.expandPrefix(internalUri);
                }
                if (expandedUri.equals(internalUri)) {
                    try {
                        isAbsolute = new URI(internalUri).isAbsolute();
                    }
                    catch (Exception ex) {
                        return null;
                    }
                    if (!isAbsolute && preferredUri != null) {
                        internalUri = String.format("%s%s", preferredUri, internalUri);
                    }
                }
                if (LabelsPreference.showReadableLabels()) {
                    int index = 1;
                    String fullUri = String.format("<%s>", internalUri);
                    while (resourceValidator.isValid(fullUri) != null) {
                        fullUri = String.format("<%s>", internalUri + "_" + index++);
                    }
                    internalUri = fullUri.substring(1, fullUri.length() - 1);
                }
                else {
                    internalUri = String.format("%s", internalUri);
                }
                calculatedUri = internalUri;
            }
        }
        return calculatedUri;
    }

    @Override
    public String getName() {
        return nameValue;
    }

    public Resource getType() {
        return selectedType;
    }

    public boolean openResourceEditor() {
        return openEditor;
    }

    public void setOpendEditorOnFinish(boolean enabled) {
        this.openEditorEnabled = enabled;
    }

    public void setGenerateUUID(boolean generateUuid) {
        this.generateUuid = generateUuid;
        if (generateUuid) {
            uuid = UUID.randomUUID().toString();
        }
    }

    public void setGenerateUUIDEnabled(boolean enabled) {
        this.generateUuidEnabled = enabled;
    }

    public void setAnonymous(boolean isAnonymous) {
        this.isAnonymous = isAnonymous;
    }

    public boolean isAnonymous() {
        return isAnonymous;
    }

    public void setAnonymousEnabled(boolean enabled) {
        this.anonymousEnabled = enabled;
    }

    public void setURIFormat(int uriFormat) {
        this.uriFormat = uriFormat;
    }

    /**
     * Handler method, which is called by almost all widgets (except the tree).
     */
    @Override
    public void handleEvent(Event event) {
        Widget widget = event.widget;
        if (widget == null) {
            return;
        }
        if (widget.equals(openEditorCheckbox) && event.type == SWT.Selection) {
            openEditor = openEditorCheckbox.getSelection();
            return;
        }
        if (widget.equals(generateUuidCheckbox) && event.type == SWT.Selection) {
            generateUuid = generateUuidCheckbox.getSelection();
            uuid = UUID.randomUUID().toString();
            calculatedUri = null;
        }
        else if (widget.equals(anonymousCheckbox) && event.type == SWT.Selection) {
            isAnonymous = ((Button) event.widget).getSelection();
            calculatedUri = null;
        }
        else if (widget.equals(nameText) && event.type == SWT.Modify) {
            nameValue = nameText.getText();
            calculatedUri = null;
        }
        else if (widget.equals(uriText) && event.type == SWT.Modify) {
            // First correct the entered value
            while (uriText.getText().startsWith(" ")) {
                uriText.setText(uriText.getText().trim());
            }

            if (uriText.getText().contains(" ")) {
                uriText.setText(uriText.getText().replace(" ", "_"));
                if (uriText.getText().startsWith("<") && uriText.getText().endsWith(">")) {
                    uriText.setSelection(uriText.getText().length() - 1);
                }
                else {
                    uriText.setSelection(uriText.getText().length());
                }
            }
            uriValue = uriText.getText();
            calculatedUri = null;
            if (ignoreChanges) {
                return;
            }
        }
        else if (widget.equals(ontologyCombo) && event.type == SWT.Selection) {
            int selectedIndex = ontologyCombo.getSelectionIndex();
            prefixKey = prefixes.get(selectedIndex);
            prefix = null;
            if (!PREFIX_BASE.equals(prefixKey) && !PREFIX_NEW.equals(prefixKey)) {
                prefix = prefixKey;
                namespaceUri = model.getNsPrefixURI(prefix);
            }
            else if (PREFIX_BASE.equals(prefixKey)) {
                namespaceUri = baseUri;
            }
            else if (PREFIX_NEW.equals(prefixKey)) {
                namespaceUri = preferredUri;
            }
            calculatedUri = null;
            updateGeneratedURI();
        }
        else {
            return;
        }
        if (event.type != SWT.Modify) {
            updateControls();
        }
        resizeShell();
        validatePage();
    }

    /**
     * Layouts all children of the shell, redraws and packs.
     */
    private void resizeShell() {
        getShell().layout(true, true);
        getShell().redraw();
        getShell().pack();
    }
}
