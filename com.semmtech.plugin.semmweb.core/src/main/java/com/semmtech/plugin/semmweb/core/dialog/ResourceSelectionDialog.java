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


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ICheckStateProvider;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.TextStyle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.Ontology;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.rulesys.GenericRuleReasoner;
import com.hp.hpl.jena.reasoner.rulesys.Rule;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.CorePluginImages;
import com.semmtech.plugin.semmweb.core.model.IModelProvider;
import com.semmtech.plugin.semmweb.core.viewers.ModelNodeLabelProvider;
import com.semmtech.plugin.semmweb.core.viewers.ResourceNameComparator;
import com.semmtech.plugin.semmweb.core.viewers.ResourceViewerToolTipSupport;
import com.semmtech.plugin.semmweb.core.widgets.SearchComposite;
import com.semmtech.plugin.semmweb.core.widgets.SearchFilterChangedListener;
import com.semmtech.ui.plugin.dialog.AbstractMessageInputDialog;
import com.semmtech.ui.plugin.util.FontUtil;
import com.semmtech.ui.plugin.viewers.LazyTreeContentProvider;


public class ResourceSelectionDialog extends AbstractMessageInputDialog {
    private static Logger logger = Logger.getLogger(ResourceSelectionDialog.class);

    public static final int DEFAULT = 0;
    public static final int CHECKBOXES = 2;

    private boolean multiSelectAllowed = false;
    private boolean hierarchicalViewDisabled = false;

    private SearchComposite searchField;
    private String filterValue = "*";

    private OntModel model;
    private InfModel infModel;
    private Set<Resource> selectedResources = Sets.newLinkedHashSet();
    private TreeViewer classViewer;
    private boolean namespaceViewSelected = true;
    private boolean showCheckboxes = true;

    // Initial values which may be set (used to construct resources)
    private List<Resource> rootResources = new ArrayList<>(
            Arrays.asList(new Resource[] { RDFS.Resource }));
    private List<Resource> excludedResources = Lists.newArrayList();
    private List<Property> hierarchicalProperties = new ArrayList<>(
            Arrays.asList(new Property[] { RDFS.subClassOf }));

    // Initial resources which may be set (instead of the above values)
    private List<Resource> initialResourcesList = null;

    // Either the set constructed using the earlier three fields or initialised
    // with initialResourcesList. This set may be filtered.
    private List<Resource> resourcesList = null;

    // / Using resources either subResource + rootResources or
    // resourcesByNamespaceMap are constructed depending on presentation
    private ArrayList<Resource> actualRootResources = null;
    private Map<Resource, List<Resource>> subResourceMap = null;
    private Map<String, List<Resource>> resourcesByNamespaceMap = null;

    // / Determines which types of resources are allowed to be selected
    // (enabling OK button)
    private List<Resource> allowedResourceTypes = new ArrayList<>(
            Arrays.asList(new Resource[] { RDFS.Class }));

    private ToolItem namespaceItem;
    private ToolItem hierarchicalItem;
    private Tree tree;
    private Font boldFont;

    private ILabelProvider labelProvider;
    private IModelProvider modelProvider;

    /**
     * 
     * @param parentShell
     * @param title
     * @param message
     */
    public ResourceSelectionDialog(Shell parentShell, String title, String message, int style) {
        super(
                parentShell,
                (title == null ? "Select Resource" : title),
                (message == null ? "Please select a resource from the list, optionally using the filter (* = all)"
                        : message));
        this.showErrorMessage = false;
        this.showCheckboxes = (style == CHECKBOXES);
    }

    public ResourceSelectionDialog(Shell parentShell, String title, String message) {
        this(parentShell, title, message, DEFAULT);
    }

    @Override
    protected Control createInputArea(Composite parent) {
        if (boldFont == null) {
            FontData[] boldFontData = FontUtil.getModifiedFontData(parent.getFont().getFontData(),
                    SWT.BOLD);
            boldFont = new Font(Display.getCurrent(), boldFontData);
        }

        Composite composite = (Composite) super.createInputArea(parent);
        GridLayout layout = new GridLayout(1, true);
        layout.marginWidth = 0;
        composite.setLayout(layout);
        composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        searchField = new SearchComposite(composite, SWT.NONE);
        searchField.addSearchFilterChangedListener(new SearchFilterChangedListener() {

            @Override
            public void filterChanged(String value) {
                filterValue = value;
                resourcesList = null;
                if (value.length() == 0 || value.equals("*")) {
                    if (!hierarchicalViewDisabled)
                        hierarchicalItem.setEnabled(true);
                }
                else {
                    namespaceViewSelected = true;
                    hierarchicalItem.setSelection(false);
                    namespaceItem.setSelection(true);
                    hierarchicalItem.setEnabled(false);
                }
                refreshViewer();
            }
        });
        searchField.setFilter(filterValue);
        GridDataFactory.fillDefaults().applyTo(searchField);

        Label matchingLabel = new Label(composite, SWT.NONE);
        matchingLabel.setText("Matching resources:");
        GridData layoutData = new GridData(SWT.FILL, SWT.TOP, true, false);
        layoutData.verticalIndent = 4;
        matchingLabel.setLayoutData(layoutData);

        Composite inner = new Composite(composite, SWT.BORDER);
        layout = new GridLayout(1, false);
        layout.verticalSpacing = 0;
        layout.horizontalSpacing = 0;
        layout.marginLeft = 0;
        layout.marginRight = 0;
        layout.marginTop = 0;
        layout.marginBottom = 0;
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        inner.setLayout(layout);

        layoutData = new GridData(SWT.FILL, SWT.TOP, true, true);
        layoutData.horizontalIndent = 0;
        layoutData.verticalIndent = 0;
        inner.setLayoutData(layoutData);

        createToolBar(inner);

        IModelProvider provider = CorePlugin.getDefault().getActiveModelProvider();
        Preconditions.checkState(provider != null || model != null);
        if (labelProvider == null) {
            if (provider != null) {
                labelProvider = provider.getLabelProvider();
            }
            else if (model != null) {
                labelProvider = new ModelNodeLabelProvider(model);
            }
        }

        if (showCheckboxes && multiSelectAllowed)
            classViewer = new CheckboxTreeViewer(inner, SWT.VIRTUAL | SWT.MULTI);
        else if (showCheckboxes)
            classViewer = new CheckboxTreeViewer(inner, SWT.VIRTUAL);
        else if (multiSelectAllowed)
            classViewer = new TreeViewer(inner, SWT.VIRTUAL | SWT.MULTI);
        else
            classViewer = new TreeViewer(inner, SWT.VIRTUAL);

        tree = classViewer.getTree();
        layoutData = new GridData(SWT.FILL, SWT.TOP, true, false);
        layoutData.widthHint = 420;
        layoutData.verticalIndent = 2;
        layoutData.horizontalIndent = 0;
        layoutData.heightHint = 200;
        tree.setLayoutData(layoutData);
        tree.addListener(SWT.EraseItem, new Listener() {
            @Override
            public void handleEvent(Event event) {
                Object value = event.item.getData();
                if (value == null)
                    return;
                // MIKE: Cancel the selection of a namespace in the tree
                boolean isNamespace = (value instanceof String);
                if (isNamespace && (event.detail & SWT.SELECTED) != 0)
                    event.detail &= ~SWT.SELECTED;
            }
        });

        classViewer.setContentProvider(new LazyTreeContentProvider() {
            @Override
            public void updateElement(Object parent, int index) {
                if (parent instanceof OntModel) {
                    // Set the ontology at index as the child element of parent
                    if (namespaceViewSelected) {
                        List<String> namespaces = new ArrayList<>(resourcesByNamespaceMap.keySet());
                        Collections.sort(namespaces, new Comparator<String>() {
                            @Override
                            public int compare(String s1, String s2) {
                                // Ensure sorting is done on the text that's
                                // actually displayed for these namespaces.
                                if (s1.equals(s2)) {
                                    return 0;
                                }
                                String text1 = getTextNamespace(s1);
                                String text2 = getTextNamespace(s2);
                                int textCompare = text1.compareTo(text2);
                                if (textCompare == 0) {
                                    return s1.compareTo(s2);
                                }
                                return textCompare;
                            }
                        });
                        String namespace = namespaces.get(index);
                        classViewer.replace(parent, index, namespace);
                        classViewer.setChildCount(namespace, resourcesByNamespaceMap.get(namespace)
                                .size());
                    }
                    else {
                        Collections.sort(actualRootResources, new ResourceNameComparator(
                                labelProvider));
                        Resource element = actualRootResources.get(index);
                        classViewer.replace(parent, index, element);
                        Preconditions.checkState(subResourceMap.containsKey(element));
                        classViewer.setChildCount(element, subResourceMap.get(element).size());
                    }
                }
                else if (parent instanceof String) {
                    String namespace = (String) parent;
                    List<Resource> localResources = resourcesByNamespaceMap.get(namespace);
                    Collections.sort(localResources, new ResourceNameComparator(labelProvider));
                    Resource element = localResources.get(index);
                    classViewer.replace(parent, index, element);
                    classViewer.setChildCount(element, 0);
                }
                else if (parent instanceof Resource) {
                    List<Resource> subResources = subResourceMap.get(parent);
                    Collections.sort(subResources, new ResourceNameComparator(labelProvider));
                    Resource element = subResources.get(index);
                    classViewer.replace(parent, index, element);
                    Preconditions.checkState(subResourceMap.containsKey(element));
                    // if (!subResourceMap.containsKey(element)) {
                    // if (infModel == null)
                    // infModel = ModelFactory.createInfModel(getReasoner(),
                    // model);
                    // subResourceMap.put(element,
                    // infModel.listResourcesWithProperty(hierarchicalProperty,
                    // (RDFNode)element).toList());
                    // }
                    classViewer.setChildCount(element, subResourceMap.get(element).size());
                }
            }
        });
        classViewer.setLabelProvider(new StyledCellLabelProvider() {
            private final Styler filterStyler = new Styler() {
                @Override
                public void applyStyles(TextStyle textStyle) {
                    textStyle.font = boldFont;
                    textStyle.foreground = Display.getCurrent().getSystemColor(SWT.COLOR_BLACK);
                }
            };

            private final Styler localStyler = new Styler() {
                @Override
                public void applyStyles(TextStyle textStyle) {
                    textStyle.foreground = Display.getCurrent().getSystemColor(SWT.COLOR_GRAY);
                }
            };

            @Override
            public void update(ViewerCell cell) {
                Object element = cell.getElement();
                StyledString styledText = new StyledString();
                String text = getElementText(element);

                if (text != null && text.length() > 0) {
                    styledText.append(text);
                }

                if (text != null && filterValue.length() > 0 && !filterValue.equals("*")) {
                    int startIndex = text.toLowerCase().indexOf(filterValue.toLowerCase());
                    while (startIndex > -1) {
                        int length = filterValue.length();
                        styledText.setStyle(startIndex, length, filterStyler);
                        startIndex = text.toLowerCase().indexOf(filterValue.toLowerCase(),
                                startIndex + 1);
                    }
                }

                if (text != null
                        && CorePlugin.getDefault().getActiveModelProvider() != null
                        && text.equals(CorePlugin.getDefault().getActiveModelProvider()
                                .getModelTitle())) {
                    styledText.setStyle(0, styledText.getString().length(), localStyler);
                }

                cell.setImage(getElementImage(element));
                cell.setText(styledText.getString());
                cell.setStyleRanges(styledText.getStyleRanges());
                super.update(cell);
            }

            public String getElementText(Object element) {
                if (element instanceof Ontology) {
                    Ontology ontology = (Ontology) element;
                    String namespaceUri = ontology.getURI();
                    if (!namespaceUri.endsWith("#") && !namespaceUri.endsWith("/"))
                        namespaceUri += "#";
                    String prefix = model.getNsURIPrefix(namespaceUri);
                    if (prefix != null && prefix.length() > 0)
                        return prefix;
                }
                else if (element instanceof String) {
                    return getTextNamespace((String) element);
                }

                return labelProvider.getText(element);
            }

            public Image getElementImage(Object element) {
                if (element instanceof String)
                    return CorePlugin.getDefault().getImage(CorePluginImages.IMG_OWL_ONTOLOGY);
                return labelProvider.getImage(element);
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
                if (element instanceof Resource)
                    return ((Resource) element).toString();
                return null;
            }
        });

        classViewer.setUseHashlookup(true);
        if (!showCheckboxes) {
            classViewer.addDoubleClickListener(new IDoubleClickListener() {
                @Override
                public void doubleClick(DoubleClickEvent event) {
                    if (classViewer.getSelection() instanceof IStructuredSelection
                            && ((IStructuredSelection) classViewer.getSelection())
                                    .getFirstElement() instanceof Resource)
                        okPressed();
                }
            });
            classViewer.addSelectionChangedListener(new ISelectionChangedListener() {
                @Override
                public void selectionChanged(SelectionChangedEvent event) {
                    if (multiSelectAllowed) {
                        selectedResources = getResourcesNotShown(selectedResources);
                    }
                    else {
                        selectedResources = Sets.newLinkedHashSet();
                    }
                    boolean allAllowed = true;
                    if (event.getSelection() instanceof IStructuredSelection) {
                        IStructuredSelection selection = (IStructuredSelection) event
                                .getSelection();
                        int count = selection.size();
                        if ((multiSelectAllowed && count > 0)
                                || (!multiSelectAllowed && count == 1))
                            for (Object selected : selection.toArray()) {
                                if (selected instanceof Resource) {
                                    Resource resource = (Resource) selected;
                                    if (isAllowed(resource)) {
                                        selectedResources.add(resource);
                                    }
                                    else {
                                        allAllowed = false;
                                    }
                                }
                            }
                    }
                    getOKButton().setEnabled((selectedResources.size() > 0) && allAllowed);
                }
            });
        }
        else {
            ((CheckboxTreeViewer) classViewer).addCheckStateListener(new ICheckStateListener() {

                @Override
                public void checkStateChanged(CheckStateChangedEvent event) {
                    CheckboxTreeViewer checkboxTree = (CheckboxTreeViewer) classViewer;
                    boolean checked = event.getChecked();
                    checkboxTree.setSubtreeChecked(event.getElement(), checked);
                    selectedResources = getResourcesNotShown(selectedResources);
                    for (Object element : checkboxTree.getCheckedElements()) {
                        if (element instanceof Resource) {
                            Resource resource = (Resource) element;
                            if (isAllowed(resource))
                                selectedResources.add(resource);
                        }
                    }
                }
            });
            ((CheckboxTreeViewer) classViewer).setCheckStateProvider(new ICheckStateProvider() {

                @Override
                public boolean isGrayed(Object element) {
                    return false;
                }

                @Override
                public boolean isChecked(Object element) {
                    if (element instanceof Resource)
                        return selectedResources.contains(element);
                    return false;
                }
            });
        }

        ResourceViewerToolTipSupport.enableFor(classViewer, modelProvider);

        refreshViewer();

        searchField.setFocus();

        return composite;
    }

    private String getTextNamespace(String namespace) {
        String prefix = model.getNsURIPrefix(namespace);
        if (prefix != null && prefix.length() > 0)
            return prefix;
        else if (CorePlugin.getDefault().getActiveModelProvider() != null)
            return CorePlugin.getDefault().getActiveModelProvider().getModelTitle();
        else
            return String.format("<" + namespace + ">");
    }

    private Set<Resource> getResourcesNotShown(Set<Resource> resources) {
        Set<Resource> result = Sets.newHashSet();
        if (resourcesList == null) {
            result.addAll(resources);
        }
        else {
            for (Resource resource : resources) {
                if (!resourcesList.contains(resource)) {
                    result.add(resource);
                }
            }
        }
        return result;
    }

    @SuppressWarnings("unused")
    private void createToolBar(Composite inputArea) {
        ToolBar toolBar = new ToolBar(inputArea, SWT.HORIZONTAL | SWT.FLAT | SWT.RIGHT);
        toolBar.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));

        if (showCheckboxes) {
            ToolItem checkNoneItem = new ToolItem(toolBar, SWT.PUSH);
            checkNoneItem.setImage(CorePlugin.getDefault()
                    .getImage(CorePluginImages.IMG_CHECK_NONE));
            checkNoneItem.setToolTipText("Uncheck All");
            checkNoneItem.addListener(SWT.Selection, new Listener() {
                @Override
                public void handleEvent(Event event) {
                    CheckboxTreeViewer checkboxTree = (CheckboxTreeViewer) classViewer;
                    for (TreeItem item : tree.getItems()) {
                        checkboxTree.setSubtreeChecked(item.getData(), false);
                        if (item.getData() instanceof Resource) {
                            selectedResources.remove(item.getData());
                        }
                    }
                }
            });

            ToolItem checkAllItem = new ToolItem(toolBar, SWT.PUSH);
            checkAllItem.setImage(CorePlugin.getDefault().getImage(CorePluginImages.IMG_CHECK_ALL));
            checkAllItem.setToolTipText("Check All");
            checkAllItem.addListener(SWT.Selection, new Listener() {
                @Override
                public void handleEvent(Event event) {
                    CheckboxTreeViewer checkboxTree = (CheckboxTreeViewer) classViewer;
                    for (TreeItem item : tree.getItems()) {
                        checkboxTree.setSubtreeChecked(item.getData(), true);
                    }
                    for (Object element : checkboxTree.getCheckedElements()) {
                        if (element instanceof Resource) {
                            Resource resource = (Resource) element;
                            if (isAllowed(resource))
                                selectedResources.add(resource);
                        }
                    }
                }
            });

            new ToolItem(toolBar, SWT.SEPARATOR);

        }

        namespaceItem = new ToolItem(toolBar, SWT.CHECK);
        namespaceItem.setImage(CorePlugin.getDefault().getImage(CorePluginImages.IMG_FLAT));
        namespaceItem.setSelection(namespaceViewSelected);
        namespaceItem.setToolTipText("View resources grouped by namespace");
        namespaceItem.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                namespaceViewSelected = true;
                refreshViewer();
                hierarchicalItem.setSelection(false);
                namespaceItem.setSelection(true);
            }
        });

        hierarchicalItem = new ToolItem(toolBar, SWT.CHECK);
        hierarchicalItem.setImage(CorePlugin.getDefault().getImage(
                CorePluginImages.IMG_HIERARCHICAL));
        hierarchicalItem.setSelection(!namespaceViewSelected);
        hierarchicalItem.setEnabled(!hierarchicalViewDisabled);
        hierarchicalItem.setToolTipText("View resources hierarchically");
        hierarchicalItem.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                namespaceViewSelected = false;
                refreshViewer();
                namespaceItem.setSelection(false);
                hierarchicalItem.setSelection(true);
            }
        });
    }

    public void setNamespaceViewSelected(boolean selected) {
        this.namespaceViewSelected = selected;
    }

    public void setHierarchicalViewSelected(boolean selected) {
        this.namespaceViewSelected = !selected;
    }

    public void setResources(List<Resource> resources) {
        this.initialResourcesList = new ArrayList<>(resources);
    }

    public void setModelProvider(IModelProvider modelProvider) {
        this.modelProvider = modelProvider;
    }

    public void setLabelProvider(ILabelProvider labelProvider) {
        this.labelProvider = labelProvider;
    }

    public void clearHierarchicalProperties() {
        this.hierarchicalProperties.clear();
    }

    public void setHierarchicalProperties(List<Property> properties) {
        this.hierarchicalProperties = new ArrayList<>(properties);
    }

    public void addHierarchicalProperty(Property property) {
        this.hierarchicalProperties.add(property);
    }

    public void addHierarchicalProperties(Property[] properties) {
        addHierarchicalProperties(Arrays.asList(properties));
    }

    public void addHierarchicalProperties(List<Property> properties) {
        this.hierarchicalProperties.addAll(properties);
    }

    public void clearRootResources() {
        this.rootResources.clear();
    }

    public void setRootResources(List<Resource> resources) {
        this.rootResources = new ArrayList<>(resources);
    }

    public void addRootResource(Resource resource) {
        this.rootResources.add(resource);
    }

    public void removeRootResource(Resource resource) {
        this.rootResources.remove(resource);
    }

    public void addRootResources(Resource[] resources) {
        addRootResources(Arrays.asList(resources));
    }

    public void addRootResources(List<Resource> resources) {
        this.rootResources.addAll(resources);
    }

    public void setAllowedResourceTypes(List<Resource> resources) {
        this.allowedResourceTypes = new ArrayList<>(resources);
    }

    public void setAllowedResourceTypes(Resource[] resources) {
        this.allowedResourceTypes = new ArrayList<>(Arrays.asList(resources));
    }

    public void clearAll() {
        clearExcludedResources();
        clearHierarchicalProperties();
        clearRootResources();
        clearSelectedResource();
    }

    private void clearSelectedResource() {
        this.selectedResources.clear();
    }

    public void clearExcludedResources() {
        this.excludedResources.clear();
    }

    public void setExcludedResources(List<Resource> resources) {
        this.excludedResources = new ArrayList<>(resources);
    }

    public void addExcludedResource(Resource resource) {
        this.excludedResources.add(resource);
    }

    public void removeExcludedResource(Resource resource) {
        this.excludedResources.remove(resource);
    }

    public void addExcludedResources(Resource[] resources) {
        addExcludedResources(Arrays.asList(resources));
    }

    public void addExcludedResources(List<Resource> resources) {
        this.excludedResources.addAll(resources);
    }

    public boolean isMultiSelectAllowed() {
        return multiSelectAllowed;
    }

    public void setMultiSelectAllowed(boolean allow) {
        this.multiSelectAllowed = allow;
    }

    public boolean isHierarchicalViewDisabled() {
        return hierarchicalViewDisabled;
    }

    public void setHierarchicalViewDisabled(boolean disabled) {
        this.hierarchicalViewDisabled = disabled;
    }

    public void setModel(OntModel model) {
        this.model = model;
    }

    public Resource getFirstSelectedResource() { // SelectedResource() {
        if (selectedResources.size() > 0)
            return (Resource) selectedResources.toArray()[0];
        return null;
    }

    public Set<Resource> getSelectedResources() {
        return selectedResources;
    }

    public void setSelectedResources(List<Resource> selected) {
        selectedResources = Sets.newHashSet(selected);
    }

    public void addSelectedResource(Resource resource) {
        selectedResources.add(resource);
    }

    public void addSelectedResources(List<Resource> resources) {
        selectedResources.addAll(resources);
    }

    private void refreshViewer() {
        Preconditions.checkArgument(hierarchicalProperties.size() > 0);
        Preconditions.checkArgument(rootResources.size() > 0);
        Preconditions.checkNotNull(model);

        if (infModel == null) {
            infModel = ModelFactory.createInfModel(getReasoner(), model);
        }

        // / Construct the list of actual resources
        if (resourcesList == null) {
            resourcesList = Lists.newArrayList();
            if (initialResourcesList != null) {
                BusyIndicator.showWhile(Display.getCurrent(), new Runnable() {
                    @Override
                    public void run() {
                        for (Resource resource : initialResourcesList) {
                            String resourceName = labelProvider.getText(resource);
                            if (filterValue.length() == 0 || filterValue.equals("*")) {
                                resourcesList.add(resource);
                            }
                            else if (resourceName.toLowerCase().contains(filterValue.toLowerCase())) {
                                resourcesList.add(resource);
                            }
                        }
                        for (Resource excluded : excludedResources) {
                            resourcesList.remove(excluded);
                        }
                    }
                });
            }
            else {
                subResourceMap = Maps.newHashMap();

                BusyIndicator.showWhile(Display.getCurrent(), new Runnable() {

                    @Override
                    public void run() {
                        for (Resource root : rootResources) {
                            String resourceName = labelProvider.getText(root);
                            if (filterValue.length() == 0 || filterValue.equals("*")) {
                                resourcesList.add(root);
                            }
                            else if (resourceName.toLowerCase().contains(filterValue.toLowerCase())) {
                                resourcesList.add(root);
                            }
                            findRelatedResources(root);
                        }
                        for (Resource excluded : excludedResources) {
                            resourcesList.remove(excluded);
                        }
                    }
                });
            }

            List<Resource> updated = new ArrayList<>(resourcesList.size());
            for (Resource resource : resourcesList) {
                String uri = resource.getURI();
                if (resource.getModel() == null)
                    updated.add(infModel.getResource(uri));
                else
                    updated.add(resource);
            }
            resourcesList = new ArrayList<>(updated);
        }

        if (allowedResourceTypes.size() > 0) {
            BusyIndicator.showWhile(Display.getCurrent(), new Runnable() {
                @Override
                public void run() {
                    List<Resource> allAllowed = new ArrayList<>(allowedResourceTypes);
                    for (Resource allowed : allAllowed)
                        findAllowedSubClasses(allowed);
                    for (Resource allowed : allowedResourceTypes)
                        logger.debug("[allowed] -> " + allowed.getURI());
                }
            });
        }

        if (namespaceViewSelected) {
            // Show a busy indicator while the runnable is executed; to display
            // this a sleep instructed is added.
            BusyIndicator.showWhile(Display.getCurrent(), new Runnable() {
                @Override
                public void run() {
                    updateResourcesByNamespace();
                }
            });
            classViewer.setInput(model);
            classViewer.getTree().setItemCount(resourcesByNamespaceMap.size());
        }
        else {
            // Show a busy indicator while the runnable is executed; to display
            // this a sleep instructed is added.
            BusyIndicator.showWhile(Display.getCurrent(), new Runnable() {
                @Override
                public void run() {
                    updateResourcesHierarchy();
                }
            });
            classViewer.setInput(model);
            classViewer.getTree().setItemCount(actualRootResources.size());
        }

        // Update the state of the selected resources
        if (showCheckboxes) {

        }

        classViewer.expandToLevel(2);
    }

    private void findRelatedResources(Resource resource) {

        if (!subResourceMap.containsKey(resource)) {
            List<Resource> subResources = Lists.newArrayList();
            for (Property prop : hierarchicalProperties) {
                for (Resource child : infModel.listResourcesWithProperty(prop, resource).toSet()) {
                    if (child.isAnon())
                        continue;
                    if (!subResources.contains(child))
                        subResources.add(child);
                }
            }
            for (Resource sub : subResources) {
                if (!resourcesList.contains(sub)) {
                    String resourceName = labelProvider.getText(sub);
                    if (filterValue.length() == 0 || filterValue.equals("*"))
                        resourcesList.add(sub);
                    else if (resourceName.toLowerCase().contains(filterValue.toLowerCase()))
                        resourcesList.add(sub);
                }
            }
            subResourceMap.put(resource, subResources);
            for (Resource sub : subResources) {
                findRelatedResources(sub);
            }
        }
    }

    protected void findAllowedSubClasses(Resource allowed) {
        for (Resource child : infModel.listResourcesWithProperty(RDFS.subClassOf, allowed).toSet()) {
            if (!allowedResourceTypes.contains(child)) {
                allowedResourceTypes.add(child);
                findAllowedSubClasses(child);
            }
        }
    }

    /**
     * Is used when presentation is hierarchical
     */
    private void updateResourcesHierarchy() {
        actualRootResources = Lists.newArrayList();
        for (Resource r : rootResources) {
            if (resourcesList.contains(r))
                actualRootResources.add(r);
        }
    }

    /**
     * Used when presentation is flat
     */
    private void updateResourcesByNamespace() {
        resourcesByNamespaceMap = Maps.newHashMap();
        for (Resource r : resourcesList) {
            Preconditions.checkState(!r.isAnon());
            if (!isAllowed(r))
                continue;
            String namespace = r.getNameSpace();
            Preconditions.checkState(namespace != null);
            if (!resourcesByNamespaceMap.containsKey(namespace))
                resourcesByNamespaceMap.put(namespace, new ArrayList<Resource>());
            resourcesByNamespaceMap.get(namespace).add(r);
        }
    }

    private boolean isAllowed(Resource selected) {
        if (allowedResourceTypes.size() == 0)
            return true;
        if (selected.getModel() == null)
            selected = model.getResource(selected.getURI());

        Resource selectedType = null;
        // / TODO: Has no model
        if (selected.hasProperty(RDF.type))
            selectedType = selected.getPropertyResourceValue(RDF.type);

        // / TODO: This will result in resource without a type to be always
        // shown (at least when they are part of the hierarchy and/or filter)
        if (selectedType == null)
            return true;

        return allowedResourceTypes.contains(selectedType);
    }

    private static Reasoner getReasoner() {
        List<Rule> rules = Lists.newArrayList();
        rules.addAll(Rule
                .parseRules(""
                        +

                        "[ subClassesOfThing: (?C rdf:type owl:Class) -> [  (?C rdfs:subClassOf owl:Thing) <- noValue(?C rdfs:subClassOf ?X) ] ]"
                        + "[ removeThingSubThing: (owl:Thing rdfs:subClassOf owl:Thing) -> remove(0) ]"
                        +

                        ""));
        return new GenericRuleReasoner(rules);
    }
}
