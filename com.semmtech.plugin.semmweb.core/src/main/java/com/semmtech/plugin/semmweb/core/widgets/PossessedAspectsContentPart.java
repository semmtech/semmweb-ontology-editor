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

package com.semmtech.plugin.semmweb.core.widgets;


import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.ontology.Restriction;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.QuerySolutionMap;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFList;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.expr.E_IsURI;
import com.hp.hpl.jena.sparql.expr.ExprVar;
import com.hp.hpl.jena.sparql.syntax.ElementFilter;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.CorePluginImages;
import com.semmtech.plugin.semmweb.core.dialog.RadioSelectionInputDialog;
import com.semmtech.plugin.semmweb.core.dialog.ResourceSelectionDialog;
import com.semmtech.plugin.semmweb.core.dnd.ResourceTransfer;
import com.semmtech.plugin.semmweb.core.forms.editor.AbstractModelResourceContent;
import com.semmtech.plugin.semmweb.core.model.ModelTransaction;
import com.semmtech.plugin.semmweb.core.model.PossessedAspect;
import com.semmtech.plugin.semmweb.core.util.ResourceURIUtil;
import com.semmtech.plugin.semmweb.core.viewers.IOpenDialogBoxListener;
import com.semmtech.plugin.semmweb.core.viewers.ResourceComboBoxCellEditor;
import com.semmtech.plugin.semmweb.core.viewers.ResourceTextDialogCellEditor;
import com.semmtech.semantics.sparql.PathUtil;
import com.semmtech.semantics.sparql.QueryBuilder;
import com.semmtech.semantics.sparql.Triples;
import com.semmtech.semantics.vocabulary.SEMM;
import com.semmtech.ui.plugin.decorators.OverlayImageIcon;
import com.semmtech.ui.plugin.viewers.LazyTreeContentProvider;
import com.semmtech.ui.plugin.viewers.StructuredContentProvider;
import com.semmtech.ui.plugin.viewers.TableLabelProvider;


/**
 * 
 * @author Sander Stolk
 */
public class PossessedAspectsContentPart extends AbstractModelResourceContentPart {
    private static final Logger logger = Logger.getLogger(PossessedAspectsContentPart.class);

    private static final String PROPERTY_OPTIONAL = "optional";
    private static final String PROPERTY_ASPECT = "aspect";
    private static final String PROPERTY_VALUE = "value";
    private static final String PROPERTY_SCALE = "scale";
    private static final String PROPERTY_CLASS = "class";
    private static final int OWNER_CLASS_COLUMN_WIDTH = 150;

    private static boolean showOwnerClass = false;

    private List<PossessedAspect> possessedAspectElements;
    private Map<Resource, Resource> specializedRolesByClass;
    private Map<Resource, Boolean> specializedRoles;
    private Map<Resource, Resource> aspectScales;
    private Map<Resource, Resource> aspectRestrictions;
    private Multimap<Resource, Resource> valueRestrictions;
    private Map<Resource, Resource> mandatoryPossessorRestrictions;

    // private Composite clientComposite;
    // private FormColors formColors;
    // private FormToolkit toolkit;
    private PossessedAspectTreeViewer viewer;
    private TreeColumn ownerClassColumn;
    private Action addAction;
    private Menu generalPopupMenu;

    public PossessedAspectsContentPart(AbstractModelResourceContent contentParent,
            Composite parent, FormToolkit toolkit) {
        super(contentParent, parent, toolkit);

        possessedAspectElements = Lists.newArrayList();
        specializedRoles = Maps.newHashMap();
        mandatoryPossessorRestrictions = Maps.newHashMap();
        aspectRestrictions = Maps.newHashMap();
        valueRestrictions = HashMultimap.create();
        aspectScales = Maps.newHashMap();

        createContent();
    }

    private Menu createGeneralPopupMenu(Control control) {
        MenuManager menuManager = new MenuManager();
        Menu menu = menuManager.createContextMenu(control);
        menuManager.setRemoveAllWhenShown(true);
        menuManager.addMenuListener(new IMenuListener() {
            @Override
            public void menuAboutToShow(IMenuManager manager) {
                manager.add(addAction);
            }
        });
        return menu;
    }

    private void createContent() {
        TableWrapLayout layout = new TableWrapLayout();
        layout.numColumns = 1;
        layout.leftMargin = 15;
        layout.rightMargin = 15;
        layout.topMargin = 10;
        layout.bottomMargin = 10;
        layout.verticalSpacing = 5;
        setLayout(layout);

        generalPopupMenu = createGeneralPopupMenu(this);
        setMenu(generalPopupMenu);
        addAction = new Action("Add Aspect...") {
            @Override
            public void run() {
                executeAddAspect();
            }
        };
        Image addPropertyImage = CorePlugin.getDefault().getImage(CorePluginImages.IMG_ADD_PLUS);
        ImageDescriptor addImageDescriptor = ImageDescriptor.createFromImage(addPropertyImage);
        addAction.setImageDescriptor(addImageDescriptor);

        // Description
        String description = "You can add a new possessed aspect by clicking on the Add button "
                + "or by dragging an aspect class onto an empty row within the table.";
        Label label = toolkit.createLabel(this, description, SWT.WRAP);
        label.setLayoutData(new TableWrapData(TableWrapData.FILL, TableWrapData.TOP, 1, 1));
        label.setMenu(generalPopupMenu);

        // Table
        Tree tree = new Tree(this, SWT.BORDER | SWT.FULL_SELECTION | SWT.VIRTUAL);
        tree.setLinesVisible(true);
        tree.setHeaderVisible(true);
        TableWrapData layoutData = new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.TOP, 1,
                1);
        layoutData.grabHorizontal = true;
        layoutData.heightHint = 120;
        tree.setLayoutData(layoutData);

        ownerClassColumn = new TreeColumn(tree, SWT.NONE, 0);
        ownerClassColumn.setText("Possessor");
        ownerClassColumn.setWidth(showOwnerClass ? OWNER_CLASS_COLUMN_WIDTH : 0);
        ownerClassColumn.setResizable(showOwnerClass);

        TreeColumn aspectColumn = new TreeColumn(tree, SWT.NONE, 1);
        aspectColumn.setText("Aspect");
        aspectColumn.setWidth(160);

        TreeColumn valueColumn = new TreeColumn(tree, SWT.NONE, 2);
        valueColumn.setText("Value(s)");
        valueColumn.setWidth(200);

        TreeColumn textColumn = new TreeColumn(tree, SWT.NONE, 3);
        textColumn.setText("Scale");
        textColumn.setWidth(110);

        TreeColumn optionalColumn = new TreeColumn(tree, SWT.NONE, 4);
        optionalColumn.setAlignment(SWT.CENTER);
        optionalColumn.setText("Optional");
        optionalColumn.setWidth(70);

        viewer = new PossessedAspectTreeViewer(tree);
        viewer.setUseHashlookup(true);
        viewer.setLabelProvider(new TableLabelProvider() {
            @Override
            public Image getColumnImage(Object element, int columnIndex) {
                if (!(element instanceof PossessedAspect)) {
                    return null;
                }
                LabelProvider labelProvider = getModelProvider().getLabelProvider();
                PossessedAspect possessed = (PossessedAspect) element;

                if (columnIndex == 0) {
                    return labelProvider.getImage(possessed.getPossessor());
                }
                else if (columnIndex == 1) {
                    Image baseImage = labelProvider.getImage(possessed.getAspect());
                    if (!possessed.getPossessor().equals(getResource())) {
                        OverlayImageIcon icon = new OverlayImageIcon(baseImage, CorePlugin
                                .getDefault());
                        icon.addImageDecoration(CorePluginImages.IMG_OVERLAY_IMPORTED,
                                OverlayImageIcon.BOTTOM_LEFT);
                        return icon.createImage();
                    }
                    return baseImage;
                }
                else if (columnIndex == 2) {
                    if (possessed.getValue() != null) {
                        Resource value = possessed.getValue();
                        return labelProvider.getImage(value);
                    }
                }
                else if (columnIndex == 3) {
                    if (possessed.getScale() != null) {
                        Resource scale = possessed.getScale();
                        return labelProvider.getImage(scale);
                    }
                }
                return null;
            }

            @Override
            public String getColumnText(Object element, int columnIndex) {
                if (!(element instanceof PossessedAspect)) {
                    return null;
                }
                LabelProvider labelProvider = getModelProvider().getLabelProvider();
                PossessedAspect possessed = (PossessedAspect) element;
                Resource aspect = possessed.getAspect();

                if (columnIndex == 0) {
                    return labelProvider.getText(possessed.getPossessor());
                }
                else if (columnIndex == 1) {
                    return labelProvider.getText(aspect);
                }
                else if (columnIndex == 2) {
                    if (possessed.getValue() != null) {
                        Resource value = possessed.getValue();
                        if (value.hasProperty(SEMM.hasValue)) {
                            return value.getProperty(SEMM.hasValue).getString();
                        }
                        else if (value.hasProperty(RDFS.label)) {
                            return value.getProperty(RDFS.label).getString();
                        }
                        else {
                            return labelProvider.getText(value);
                        }
                    }
                }
                else if (columnIndex == 3) {
                    if (possessed.getScale() != null) {
                        Resource scale = possessed.getScale();
                        if (scale.hasProperty(RDFS.label)) {
                            return scale.getProperty(RDFS.label).getString();
                        }
                        return labelProvider.getText(scale);
                    }
                    else if (aspectScales.containsKey(aspect) && aspectScales.get(aspect) == null) {
                        return "n/a";
                    }
                    else if (possessed.getValue() == null) {
                        return "n/a";
                    }
                    else if (possessed.getValue().hasProperty(RDF.type, OWL.Class)
                            || possessed.getValue().hasProperty(RDF.type, RDF.List)) {
                        return "n/a";
                    }
                }
                else if (columnIndex == 4) {
                    if (possessed.isOptional()) {
                        return "yes";
                    }
                    return "no";
                }
                return null;
            }
        });
        viewer.setContentProvider(new LazyTreeContentProvider() {
            @Override
            public void updateElement(Object parent, int index) {
                if (parent instanceof PossessedAspectsContentPart) {
                    PossessedAspect element = possessedAspectElements.get(index);

                    int childCount = 0;
                    if (!element.getPossessor().getURI().equals(getResource().getURI())) {
                        childCount = 1;
                    }

                    viewer.replace(parent, index, element);
                    viewer.setChildCount(element, childCount);
                }
                else if (parent instanceof PossessedAspect) {
                    PossessedAspect childElement = (PossessedAspect) parent;
                    int childCount = 0;

                    viewer.replace(parent, index, childElement);
                    viewer.setChildCount(childElement, childCount);
                }
                super.updateElement(parent, index);
            }
        });

        CellEditor[] editors = new CellEditor[5];

        final ResourceTextDialogCellEditor valueEditor = new ResourceTextDialogCellEditor(tree);
        valueEditor.setOpenDialogBoxListener(new IOpenDialogBoxListener() {
            @Override
            public Object openDialogBox(Control cellEditorWindow) {
                String title = "Select Value";
                String message = "Please select a class, list or value from the list below.";
                ResourceSelectionDialog dialog = new ResourceSelectionDialog(cellEditorWindow
                        .getShell(), title, message);
                dialog.setModel(getModelProvider().getOntModel());
                dialog.setHierarchicalViewSelected(false);

                dialog.setRootResources(Arrays.asList(new Resource[] { SEMM.Qualification,
                        RDF.List, OWL.Thing }));
                dialog.setHierarchicalProperties(Arrays.asList(new Property[] { RDFS.subClassOf,
                        RDF.type }));
                dialog.setAllowedResourceTypes(Arrays.asList(new Resource[] { SEMM.Qualification,
                        RDF.List, OWL.Class }));

                if (dialog.open() == Window.OK) {
                    return dialog.getFirstSelectedResource();
                }
                return null;
            }
        });
        valueEditor.setLabelProvider(new LabelProvider() {
            @Override
            public String getText(Object element) {
                if (element instanceof Resource) {
                    Resource resource = (Resource) element;
                    logger.debug("Providing label for value '" + resource.getURI() + "'");
                    if (resource.hasProperty(SEMM.hasValue)) {
                        return resource.getProperty(SEMM.hasValue).getString();
                    }
                    else if (resource.hasProperty(RDFS.label)) {
                        return resource.getProperty(RDFS.label).getString();
                    }
                    else {
                        return getModelProvider().getLabelProvider().getText(element);
                    }
                }
                else if (element instanceof String) {
                    return element.toString();
                }
                return null;
            }
        });
        editors[2] = valueEditor;

        final ResourceComboBoxCellEditor scaleEditor = new ResourceComboBoxCellEditor(tree);
        scaleEditor.setContentProvider(new StructuredContentProvider() {
            private PossessedAspect possessedAspect;

            @Override
            public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
                possessedAspect = null;
                if (newInput instanceof PossessedAspect) {
                    logger.debug("inputChanged to a PossessedAspect '" + newInput.toString() + "'");
                    this.possessedAspect = (PossessedAspect) newInput;

                }
                else if (newInput == null) {
                    logger.debug("inputChanged to null");
                }
                else {
                    logger.debug("inputChanged to '" + newInput.toString() + "'");
                }
            }

            @Override
            public Object[] getElements(Object inputElement) {
                if (possessedAspect == null) {
                    return new Object[0];
                }
                else if (possessedAspect.getValue() == null) {
                    return new Object[0];
                }
                else {
                    Resource aspect = possessedAspect.getAspect();
                    Resource scale = aspectScales.get(aspect);
                    OntModel model = getModelProvider().getOntModel();
                    List<Resource> instances = Lists.newArrayList();
                    for (Resource instance : model.listSubjectsWithProperty(RDF.type, scale)
                            .toList()) {
                        logger.debug("Adding scale instance '" + instance.getURI() + "' to list");
                        instances.add(instance);
                    }

                    return instances.toArray();
                }
            }
        });
        scaleEditor.setLabelProvider(new LabelProvider() {
            @Override
            public String getText(Object element) {
                if (element instanceof Resource) {
                    Resource instance = (Resource) element;
                    logger.debug("Providing label for instance '" + instance.getURI() + "'");
                    if (instance.hasProperty(RDFS.label)) {
                        return instance.getProperty(RDFS.label).getString();
                    }
                    return getModelProvider().getLabelProvider().getText(element);
                }
                return null;
            }
        });
        editors[3] = scaleEditor;
        editors[4] = new CheckboxCellEditor(tree);

        viewer.setColumnProperties(new String[] { PROPERTY_CLASS, PROPERTY_ASPECT, PROPERTY_VALUE,
                PROPERTY_SCALE, PROPERTY_OPTIONAL });
        viewer.setCellEditors(editors);
        viewer.setCellModifier(new ICellModifier() {

            @Override
            public void modify(Object element, String property, Object value) {
                TreeItem item = (TreeItem) element;
                if (item == null || item.getData() == null
                        || !(item.getData() instanceof PossessedAspect)) {
                    return;
                }

                PossessedAspect possessedAspect = (PossessedAspect) item.getData();
                if (property.equals(PROPERTY_VALUE)) {
                    changeValue(possessedAspect, value);
                }
                else if (property.equals(PROPERTY_SCALE) && possessedAspect.getValue() == null) {
                    MessageDialog.openInformation(getShell(), "Unknown Value",
                            "Value is required, please set a value before setting the scale!");
                }
                else if (property.equals(PROPERTY_SCALE)) {
                    changeScale(possessedAspect, value);
                }
                else if (property.equals(PROPERTY_OPTIONAL)) {
                    toggleOptionality(possessedAspect);
                }
            }

            @Override
            public Object getValue(Object element, String property) {
                PossessedAspect possessedAspect = (PossessedAspect) element;
                if (property.equals(PROPERTY_OPTIONAL)) {
                    return possessedAspect.isOptional();
                }
                else if (property.equals(PROPERTY_VALUE)) {
                    // Input is changed, set current possessedAspect
                    valueEditor.setInput(possessedAspect);
                    return possessedAspect.getValue();
                }
                else if (property.equals(PROPERTY_SCALE)) {
                    // Input is changed, set current possessedAspect
                    scaleEditor.setInput(possessedAspect);
                    return possessedAspect.getScale();
                }
                return null;
            }

            @Override
            public boolean canModify(Object element, String property) {
                if (element instanceof PossessedAspect) {
                    Resource aspect = ((PossessedAspect) element).getAspect();
                    boolean hasScale = false;
                    boolean hasValue = (((PossessedAspect) element).getValue() != null);
                    // If the aspect has a scale (which is not null) set to true
                    if (aspectScales.containsKey(aspect) && aspectScales.get(aspect) != null) {
                        hasScale = true;
                    }
                    if (property.equals(PROPERTY_VALUE) || property.equals(PROPERTY_OPTIONAL)
                            || property.equals(PROPERTY_SCALE) && hasScale && hasValue) {
                        return true;
                    }
                }
                return false;
            }
        });

        tree.setItemCount(0);

        DropTarget tableTarget = new DropTarget(tree, DND.DROP_MOVE | DND.DROP_COPY);
        tableTarget.setTransfer(new Transfer[] { ResourceTransfer.getInstance() });
        tableTarget.addDropListener(new DropTargetAdapter() {
            private boolean validAspectResource;
            private boolean validValueResource = true;

            @Override
            public void dragEnter(DropTargetEvent event) {
                validAspectResource = false;
                if (ResourceTransfer.getInstance().isSupportedType(event.currentDataType)) {
                    Resource resource = (Resource) ResourceTransfer.getInstance().nativeToJava(
                            event.currentDataType);

                    if (resource != null) {
                        String sparql = String
                                .format("PREFIX rdfs: <%s> PREFIX semm: <%s> ASK { ?resource rdfs:subClassOf+ semm:Aspect }",
                                        RDFS.getURI(), SEMM.getURI());
                        QueryExecution execution = QueryExecutionFactory.create(sparql,
                                getModelProvider().getOntModel());
                        QuerySolutionMap bindings = new QuerySolutionMap();
                        bindings.add("resource", resource);
                        execution.setInitialBinding(bindings);
                        validAspectResource = execution.execAsk();
                    }
                }
            }

            @Override
            public void dragOver(DropTargetEvent event) {
                TreeItem item = (TreeItem) event.item;
                if (item == null && !validAspectResource) {
                    event.detail = DND.DROP_NONE;
                }
                else if (item == null && validAspectResource) {
                    event.detail = DND.DROP_MOVE;
                }
                else if (validValueResource) {
                    event.detail = DND.DROP_COPY;
                }
            }

            private void changeValueToNewList(String mode, PossessedAspect possessedAspect,
                    Resource value, Resource oldValue) {
                ModelTransaction transaction = getModelProvider().createTransaction(
                        "Created a new list");
                createChangeValueToNewListStatements(mode, possessedAspect, value, oldValue);
                getModelProvider().commitTransaction(transaction);
            }

            private void createChangeValueToNewListStatements(String mode,
                    PossessedAspect possessedAspect, Resource value, Resource oldValue) {
                OntModel model = getModelProvider().getOntModel();
                if ("overwrite".equals(mode)) {
                    createChangeValueStatements(possessedAspect, value);
                }
                else if ("createList".equals(mode)) {
                    Resource tail = model.createResource();
                    tail.addProperty(RDF.type, RDF.List);
                    tail.addProperty(RDF.first, oldValue);
                    tail.addProperty(RDF.rest, RDF.nil);

                    Resource list = model.createResource();
                    list.addProperty(RDF.type, RDF.List);
                    list.addProperty(RDF.first, value);
                    list.addProperty(RDF.rest, tail);

                    createChangeValueStatements(possessedAspect, list);
                }
                else if ("appendToList".equals(mode)) {
                    Resource list = model.createResource();
                    list.addProperty(RDF.type, RDF.List);
                    list.addProperty(RDF.first, value);
                    list.addProperty(RDF.rest, oldValue);

                    createChangeValueStatements(possessedAspect, list);
                }
                else if ("createUnion".equals(mode)) {
                    Resource tail = model.createResource();
                    tail.addProperty(RDF.type, RDF.List);
                    tail.addProperty(RDF.first, oldValue);
                    tail.addProperty(RDF.rest, RDF.nil);

                    Resource list = model.createResource();
                    list.addProperty(RDF.type, RDF.List);
                    list.addProperty(RDF.first, value);
                    list.addProperty(RDF.rest, tail);

                    Resource union = model.createResource();
                    union.addProperty(RDF.type, OWL.Class);
                    union.addProperty(OWL.unionOf, list);

                    createChangeValueStatements(possessedAspect, union);
                }
                else if ("appendToUnion".equals(mode)) {
                    Resource tail = oldValue.getPropertyResourceValue(OWL.unionOf);
                    Resource list = model.createResource();
                    list.addProperty(RDF.type, RDF.List);
                    list.addProperty(RDF.first, value);
                    list.addProperty(RDF.rest, tail);

                    Resource union = model.createResource();
                    union.addProperty(RDF.type, OWL.Class);
                    union.addProperty(OWL.unionOf, list);

                    createChangeValueStatements(possessedAspect, union);
                }
            }

            @Override
            public void drop(DropTargetEvent event) {
                TreeItem item = (TreeItem) event.item;
                if (item == null && validAspectResource) {
                    if (ResourceTransfer.getInstance().isSupportedType(event.currentDataType)) {
                        createPossessedAspect((Resource) event.data, true);
                    }
                }
                else if (item != null && possessedAspectElements.size() > 0 && validValueResource) {
                    Tree tree = item.getParent();
                    int index = -1;
                    for (int i = 0; i < tree.getItemCount(); i++) {
                        if (tree.getItem(i) == item) {
                            index = i;
                            break;
                        }
                    }
                    if (index > -1
                            && ResourceTransfer.getInstance()
                                    .isSupportedType(event.currentDataType)) {
                        PossessedAspect possessedAspect = possessedAspectElements.get(index);
                        Resource value = (Resource) event.data;
                        Resource oldValue = possessedAspect.getValue();

                        boolean duplicateValue = false;
                        boolean oldList = false;

                        // Check if oldValue already is a list, and if the list
                        // already contains the new value
                        // NOTE: Lists are anonymous and thus loose the rdf:type
                        // property (check for rdf:first and rdf:rest).
                        if (oldValue != null
                                && (oldValue.hasProperty(RDF.type, RDF.List) || (oldValue
                                        .hasProperty(RDF.first) && oldValue.hasProperty(RDF.rest)))) {
                            oldList = true;
                            RDFList list = oldValue.as(RDFList.class);
                            duplicateValue = list.contains(value);
                        }
                        else if (oldValue != null
                                && (oldValue.hasProperty(RDF.type, OWL.Class) && oldValue
                                        .hasProperty(OWL.unionOf))) {
                            RDFList list = oldValue.getPropertyResourceValue(OWL.unionOf).as(
                                    RDFList.class);
                            duplicateValue = list.contains(value);
                        }

                        if (!duplicateValue && oldValue != null && !oldValue.equals(value)) {
                            LabelProvider labelProvider = getModelProvider().getLabelProvider();
                            String aspectName = labelProvider.getText(possessedAspect.getAspect());
                            String oldValueName = labelProvider.getText(oldValue);
                            String valueName = labelProvider.getText(value);
                            String message = "The possessed aspect " + aspectName
                                    + " already has the value " + oldValueName
                                    + ". What would you like to do with the new value " + valueName
                                    + "?";

                            Map<String, String> options = Maps.newLinkedHashMap();
                            options.put("overwrite", String.format(
                                    "Overwrite old value %s with new value %s", oldValueName,
                                    valueName));
                            if (!oldList) {
                                options.put("createList", String.format(
                                        "Create a new list containing both %s and %s",
                                        oldValueName, valueName));
                            }
                            else {
                                options.put("appendToList", String.format(
                                        "Append value %s to list %s", valueName, oldValueName));
                            }
                            if (value.hasProperty(RDF.type, OWL.Class)
                                    && oldValue.hasProperty(RDF.type, OWL.Class)) {
                                if (!oldValue.hasProperty(OWL.unionOf)) {
                                    options.put(
                                            "createUnion",
                                            String.format(
                                                    "Create a new union-class containing the classes %s and %s",
                                                    oldValueName, valueName));
                                }
                                else {
                                    options.put("appendToUnion", String.format(
                                            "Append class %s to union-class %s", valueName,
                                            oldValueName));
                                }
                            }
                            options.put("keep", String.format("Keep old value %s", oldValueName));

                            RadioSelectionInputDialog dialog = new RadioSelectionInputDialog(
                                    getShell(), "Aspect Value", message, options, "keep");
                            if (dialog.open() == Window.OK) {
                                String selected = dialog.getSelected();
                                changeValueToNewList(selected, possessedAspect, value, oldValue);
                            }
                        }
                        else if (oldValue == null) {
                            changeValue(possessedAspect, value);
                        }
                    }
                }
            }
        });

        createPopupMenu();

        refresh();
    }

    @SuppressWarnings("static-method")
    public boolean getShowOwnerClass() {
        return showOwnerClass;
    }

    public void setShowOwnerClass(boolean checked) {
        showOwnerClass = checked;
        ownerClassColumn.setWidth((checked) ? OWNER_CLASS_COLUMN_WIDTH : 0);
        ownerClassColumn.setResizable(checked);
        viewer.getTree().update();
        refreshViewer();
    }

    public void executeAddAspect() {
        addAspectFromDialog();
    }

    private void createPopupMenu() {
        MenuManager menuManager = new MenuManager();
        Menu menu = menuManager.createContextMenu(viewer.getControl());
        menuManager.setRemoveAllWhenShown(true);
        menuManager.addMenuListener(new IMenuListener() {
            @Override
            public void menuAboutToShow(IMenuManager manager) {
                final PossessedAspect selected = viewer.getSelectedElement();
                boolean isInherited = false;

                if (selected != null) {
                    isInherited = !selected.getPossessor().getURI().equals(getResource().getURI());
                }

                manager.add(addAction);

                if (selected != null && !isInherited) {
                    IAction removeAction = new Action("Remove") {
                        @Override
                        public void run() {
                            removeSelectedAspects();
                        }
                    };
                    removeAction.setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(
                            CorePlugin.PLUGIN_ID, CorePluginImages.IMG_DELETE));
                    manager.add(new Separator());
                    manager.add(removeAction);
                }
            }
        });
        viewer.getControl().setMenu(menu);
    }

    @Override
    public void refresh() {
        refreshViewer();
    }

    protected void refreshViewer() {
        findPossessedAspects();
        for (PossessedAspect possessedAspect : possessedAspectElements) {
            findQuantificationScale(possessedAspect.getAspect());
        }
        viewer.setInput(this);
        viewer.getTree().setItemCount(possessedAspectElements.size());
    }

    /**
     * Returns a list of all the possessed aspects attached to the current
     * object. The list is empty if the current class has no possessed aspects.
     * TODO: Refactor and use SPARQL
     * 
     * @return list of all the possessed aspects attached to the current object.
     */
    public void findPossessedAspects() {
        // Finally check for any possessed aspects in model
        // Possessed aspect are found using a some/all values from restriction
        // on property SEMM.hasPossessedAspect
        // First find all possessor restrictions (onProperty:
        // hasPossessedAspect; types: someValuesFrom, QCR)

        OntModel model = getModelProvider().getOntModel();

        // Possessed aspect are found using a some/all values from restriction
        // on property SEMM.hasPossessedAspect
        // First find all possessor restrictions (onProperty:
        // hasPossessedAspect; types: someValuesFrom, QCR)
        specializedRoles = Maps.newHashMap();
        mandatoryPossessorRestrictions = Maps.newHashMap();
        specializedRolesByClass = Maps.newHashMap();

        // Create triple patterns
        Var superVariable = Var.alloc("superClass");
        // Get each direct or indirect Superclass (?superClass) that is not a
        // restriction and is not anonymous.

        ElementFilter filter = new ElementFilter(new E_IsURI(new ExprVar(superVariable.getName())));
        QueryBuilder queryBuilder = QueryBuilder.createSelect(true).addTriplePattern(getResource(),
                PathUtil.subClassOfAny, superVariable);
        queryBuilder.addFilterPattern(
                Triples.create(superVariable, PathUtil.isInstanceOf, OWL.Restriction), true)
                .addFilterPattern(filter);
        queryBuilder.addResultVar(superVariable);

        for (ResultSet superIter = queryBuilder.execSelect(model); superIter.hasNext();) {
            List<Resource> possibleSpecializedRoles = Lists.newArrayList();
            // Find possessor restrictions (inherited)

            Resource superClass = superIter.next().getResource(superVariable.getName());
            Var restrictionVariable = Var.alloc("directRestriction");
            // Get each direct superclass of s (?directRestriction) that is a
            // restriction and has {onProperty: SEMM.hasPossessedAspect}
            queryBuilder = QueryBuilder.createSelect(true)
                    .addTriplePattern(superClass, RDFS.subClassOf, restrictionVariable)
                    .addTriplePattern(restrictionVariable, PathUtil.isInstanceOf, OWL.Restriction)
                    .addTriplePattern(restrictionVariable, OWL.onProperty, SEMM.hasPossessedAspect)
                    .addResultVar(restrictionVariable);

            for (ResultSet restrictionIter = queryBuilder.execSelect(model); restrictionIter
                    .hasNext();) {
                Resource restriction = restrictionIter.next().getResource(
                        restrictionVariable.getName());
                if (restriction.hasProperty(OWL.allValuesFrom)) {
                    Resource optionalityUnionOfClass = restriction
                            .getPropertyResourceValue(OWL.allValuesFrom);
                    if (optionalityUnionOfClass.hasProperty(OWL.unionOf)) {
                        RDFList optionalList = optionalityUnionOfClass.getPropertyResourceValue(
                                OWL.unionOf).as(RDFList.class);
                        for (int i = 0; i < optionalList.size(); i++) {
                            Resource specializedRole = optionalList.get(i).asResource();
                            possibleSpecializedRoles.add(specializedRole);
                        }
                    }
                }
                else if (restriction.hasProperty(OWL.someValuesFrom)) {
                    Resource specializedRole = restriction
                            .getPropertyResourceValue(OWL.someValuesFrom);
                    mandatoryPossessorRestrictions.put(specializedRole, restriction);
                }
            }
            for (Resource specializedRole : possibleSpecializedRoles) {
                boolean optional = !mandatoryPossessorRestrictions.containsKey(specializedRole);
                specializedRoles.put(specializedRole, new Boolean(optional));
                specializedRolesByClass.put(specializedRole, superClass);
            }
        }

        // Check if a specialized role exists which is overwritten by other
        // specialized roles; in this case hide the first specialized role
        List<Resource> overwritten = Lists.newArrayList();
        for (Resource specializedRole : specializedRoles.keySet()) {
            if (specializedRole.canAs(OntClass.class)) {
                Set<OntClass> subClasses = specializedRole.as(OntClass.class).listSubClasses(false)
                        .toSet();
                for (OntClass subClass : subClasses) {
                    if (subClass.equals(getResource())) {
                        continue;
                    }
                    if (specializedRoles.containsKey(subClass)) {
                        overwritten.add(specializedRole);
                    }
                }
            }
        }
        for (Resource overwrite : overwritten) {
            specializedRoles.remove(overwrite);
            specializedRolesByClass.remove(overwrite);
            mandatoryPossessorRestrictions.remove(overwrite);
        }

        // Next create the PossessedAspectElements needed for the part.
        // Immediately find any value restrictions on the possessed aspects
        // e.g. PossessedAspectColorByBeetle - (Color, (1,1), Red).
        possessedAspectElements = Lists.newArrayList();

        aspectRestrictions = Maps.newHashMap();
        valueRestrictions = HashMultimap.create();
        for (Resource specializedRole : specializedRoles.keySet()) {
            Resource ownerClass = specializedRolesByClass.get(specializedRole);

            Resource aspect = null;
            Resource value = null;
            Resource scale = null;

            Var variable = Var.alloc("superClass");
            // Get all direct superclasses (?superClass) of specializedRole
            queryBuilder = QueryBuilder.createSelect(true).addTriplePatterns(
                    new Triple[] { Triples.create(specializedRole, RDFS.subClassOf, variable),
                            Triples.create(variable, PathUtil.isInstanceOf, OWL.Restriction) });

            for (ResultSet iter = queryBuilder.execSelect(model); iter.hasNext();) {
                Resource restriction = iter.next().getResource(variable.getName());
                if (restriction.hasProperty(OWL.onProperty, SEMM.isRoleOf)
                        && restriction.hasProperty(OWL.allValuesFrom)) {
                    aspect = restriction.getPropertyResourceValue(OWL.allValuesFrom);
                    aspectRestrictions.put(specializedRole, restriction);
                }
                else if (restriction.hasProperty(OWL.onProperty, SEMM.isQualifiedAs)
                        && restriction.hasProperty(OWL.allValuesFrom)) {
                    // / Check if the class is used als a oneOf class; at
                    // thus the oneOf list should be used not the class
                    // / NOTE: Lists are anonymous and thus loose the
                    // rdf:type property (check for rdf:first and rdf:rest).
                    value = restriction.getPropertyResourceValue(OWL.allValuesFrom);
                    valueRestrictions.put(specializedRole, restriction);
                    // / Should this be done here or in part! Strictly
                    // speaking the value is the class with oneOf.
                    // / Yes this is done here - oneOf always has a list,
                    // and this list is the value
                    if (value.hasProperty(RDF.type, OWL.Class) && value.hasProperty(OWL.oneOf)) {
                        value = value.getPropertyResourceValue(OWL.oneOf);
                    }
                }
                else if (restriction.hasProperty(OWL.onProperty, SEMM.isQualifiedAs)
                        && restriction.hasProperty(OWL.hasValue)) {
                    value = restriction.getPropertyResourceValue(OWL.hasValue);
                    valueRestrictions.put(specializedRole, restriction);
                    if (value.hasProperty(SEMM.hasScale)) {
                        scale = value.getPropertyResourceValue(SEMM.hasScale);
                    }
                }
            }
            if (aspect != null) {
                // / Put it all together and create a PossessedAspect object!
                PossessedAspect possessedAspect = new PossessedAspect(ownerClass, aspect);
                possessedAspect.setSpecializedRole(specializedRole);
                boolean optional = specializedRoles.get(specializedRole).booleanValue();
                possessedAspect.setOptional(optional);
                possessedAspect.setValue(value);
                possessedAspect.setScale(scale);
                possessedAspect.setPossessorRestriction(mandatoryPossessorRestrictions
                        .get(specializedRole));
                possessedAspect.setAspectRestriction(aspectRestrictions.get(specializedRole));
                possessedAspect.addValueRestrictions(valueRestrictions.get(specializedRole));

                possessedAspectElements.add(possessedAspect);
            }
        }
    }

    private void findQuantificationScale(Resource aspect) {
        Resource scale = null;
        StringBuilder sparql = new StringBuilder();
        sparql.append("PREFIX rdf: <" + RDF.getURI() + ">\n");
        sparql.append("PREFIX rdfs: <" + RDFS.getURI() + ">\n");
        sparql.append("PREFIX owl: <" + OWL.getURI() + ">\n");
        sparql.append("PREFIX semm: <" + SEMM.getURI() + ">\n");
        sparql.append("SELECT ?scale\n");
        sparql.append("WHERE { \n");
        sparql.append("?aspect rdfs:subClassOf* ?r .\n");
        sparql.append("?r rdf:type owl:Restriction .\n");
        sparql.append("?r owl:onProperty semm:isQuantifiedOnScale .\n");
        sparql.append("?r owl:allValuesFrom ?scale .\n");
        sparql.append("}");

        OntModel model = getModelProvider().getOntModel();
        QueryExecution execution = QueryExecutionFactory.create(sparql.toString(), model);
        QuerySolutionMap bindings = new QuerySolutionMap();
        bindings.add("aspect", aspect);
        execution.setInitialBinding(bindings);
        ResultSet result = execution.execSelect();
        while (result.hasNext()) {
            QuerySolution solution = result.next();
            RDFNode node = solution.get("scale");
            if (node.isAnon() || !node.isResource()) {
                continue;
            }
            if (scale != null) {
                logger.warn("The current aspect '" + model.shortForm(aspect.getURI())
                        + "' already has a scale; Will be updated. TODO!");
            }
            scale = model.getResource(node.asResource().getURI());
        }
        aspectScales.put(aspect, scale);
    }

    private void createRemoveAspectsStatements(PossessedAspect selected) {
        Preconditions.checkNotNull(selected);

        Resource specializedRole = selected.getSpecializedRole();

        createRemovePossessorRestrictionStatements(selected);
        createRemoveAspectRestrictionStatements(selected);
        createRemoveValueRestrictionsStatements(selected);
        createRemoveSpecializedRoleStatements(specializedRole);
    }

    private void removeSelectedAspects() {
        PossessedAspect selected = viewer.getSelectedElement();

        String transactionDescription = "Due to removal of a possessed aspect";
        ModelTransaction transaction = getModelProvider().createTransaction(transactionDescription);

        if (selected != null) {
            Resource specializedRole = selected.getSpecializedRole();
            createRemoveAspectsStatements(selected);
            specializedRoles.remove(specializedRole);
        }
        createUpdateOptionalityUnionStatements();

        getModelProvider().commitTransaction(transaction);
    }

    private void createRemoveSpecializedRoleStatements(Resource specializedRole) {
        OntModel model = getModelProvider().getOntModel();
        if (specializedRole != null) {
            model.remove(specializedRole.listProperties());
        }
    }

    private void createRemoveAspectRestrictionStatements(PossessedAspect possessedAspect) {
        OntModel model = getModelProvider().getOntModel();
        Resource aspectRestriction = possessedAspect.getAspectRestriction();
        if (aspectRestriction != null) {
            Resource specializedRole = possessedAspect.getSpecializedRole();
            model.remove(aspectRestriction.listProperties().toList());
            model.remove(model.createStatement(specializedRole, RDFS.subClassOf, aspectRestriction));
        }
    }

    private void createRemovePossessorRestrictionStatements(PossessedAspect possessedAspect) {
        OntModel model = getModelProvider().getOntModel();
        Resource possessorRestriction = possessedAspect.getPossessorRestriction();
        if (possessorRestriction != null) {
            Resource specializedRole = possessedAspect.getSpecializedRole();
            model.remove(possessorRestriction.listProperties().toList());
            model.remove(model.createStatement(specializedRole, RDFS.subClassOf,
                    possessorRestriction));
        }
    }

    private void createRemoveValueRestrictionsStatements(PossessedAspect possessedAspect) {
        OntModel model = getModelProvider().getOntModel();
        Resource specializedRole = possessedAspect.getSpecializedRole();
        for (Resource valueRestriction : possessedAspect.getValueRestrictions()) {
            // TODO: Check whether a deeper removal is required
            model.remove(valueRestriction.listProperties().toList());
            model.remove(model.createStatement(specializedRole, RDFS.subClassOf, valueRestriction));
        }
    }

    private void createPossessedAspect(Resource aspect, boolean optional) {
        String transactionDescription = "Due to creation of a possessed aspect";
        ModelTransaction transaction = getModelProvider().createTransaction(transactionDescription);
        createPossessedAspectStatements(aspect, optional);
        getModelProvider().commitTransaction(transaction);
    }

    private void createPossessedAspectStatements(Resource aspect, boolean optional) {
        OntResource possessor = getResource();
        OntModel model = getModelProvider().getOntModel();

        // Since all information about specializedRole has been deleted based
        // on previous committed possessed aspects, all information needs to be
        // recreated!
        // Create the specialized role class and the restriction on the aspect
        // as the isRoleOf (aspectRestriction)
        String namespace = possessor.getNameSpace();
        String specializedRoleUri = namespace + "PossessedAspect" + aspect.getLocalName() + "By"
                + possessor.getLocalName();
        Resource specializedRole = model.createResource(specializedRoleUri);
        specializedRole.addProperty(RDF.type, OWL.Class);
        specializedRole.addProperty(RDFS.subClassOf, SEMM.PossessedAspect);
        specializedRoles.put(specializedRole, new Boolean(optional));

        Restriction aspectRestrictionResource = model.createRestriction(SEMM.isRoleOf);
        aspectRestrictionResource.addProperty(OWL.allValuesFrom, aspect);
        specializedRole.addProperty(RDFS.subClassOf, aspectRestrictionResource);

        createUpdateOptionalityUnionStatements();
    }

    private PossessedAspect createDuplicateAspectStatements(PossessedAspect original,
            boolean optional) {
        Resource aspect = original.getAspect();
        OntResource possessor = getResource();
        OntModel model = getModelProvider().getOntModel();

        Resource previousRole = original.getSpecializedRole();
        specializedRoles.remove(previousRole);
        specializedRolesByClass.remove(previousRole);
        mandatoryPossessorRestrictions.remove(previousRole);

        // Create the specialized role class and the restriction on the aspect
        // as the isRoleOf (aspectRestriction)
        String namespace = possessor.getNameSpace();
        String specializedRoleUri = namespace + "PossessedAspect" + aspect.getLocalName() + "By"
                + possessor.getLocalName();
        Resource specializedRole = model.createResource(specializedRoleUri);
        specializedRole.addProperty(RDF.type, OWL.Class);
        specializedRole.addProperty(RDFS.subClassOf, previousRole);
        specializedRoles.put(specializedRole, new Boolean(optional));

        Restriction aspectRestrictionResource = model.createRestriction(SEMM.isRoleOf);
        aspectRestrictionResource.addProperty(OWL.allValuesFrom, aspect);
        specializedRole.addProperty(RDFS.subClassOf, aspectRestrictionResource);

        PossessedAspect duplicate = new PossessedAspect(possessor, aspect);
        duplicate.setSpecializedRole(specializedRole);
        duplicate.setAspectRestriction(aspectRestrictionResource);

        if (!optional) {
            Restriction possessorRestriction = model.createRestriction(SEMM.hasPossessedAspect);
            possessorRestriction.addProperty(OWL.someValuesFrom, specializedRole);
            possessor.addProperty(RDFS.subClassOf, possessorRestriction);
            duplicate.setPossessorRestriction(possessorRestriction);
        }

        duplicate.setOptional(optional);
        duplicate.setScale(original.getScale());
        duplicate.setValue(original.getValue());
        duplicate.addValueRestrictions(original.getValueRestrictions());

        // Create new optionalityRestriction and optionalityUnionOfClass
        createUpdateOptionalityUnionStatements();

        return duplicate;
    }

    private void createUpdateOptionalityUnionStatements() {
        OntModel model = getModelProvider().getOntModel();
        OntResource possessor = getResource();

        // Removed the global fields; therefor this code searches for the
        // unionOfClass
        for (Statement stmt : model.listStatements(possessor, RDFS.subClassOf, (RDFNode) null)
                .toList()) {
            if (!stmt.getObject().isResource()) {
                continue;
            }
            if (stmt.getResource().hasProperty(RDF.type, OWL.Restriction)) {
                Restriction restriction = stmt.getResource().as(Restriction.class);
                if (!restriction.onProperty(SEMM.hasPossessedAspect)) {
                    continue;
                }
                if (!restriction.hasProperty(OWL.allValuesFrom)) {
                    continue;
                }

                Resource unionOfClass = restriction.getPropertyResourceValue(OWL.allValuesFrom);
                model.remove(restriction, RDF.type, OWL.Restriction);
                model.remove(restriction, OWL.onProperty, SEMM.hasPossessedAspect);
                model.remove(restriction, OWL.allValuesFrom, unionOfClass);
                model.remove(possessor, RDFS.subClassOf, restriction);
            }
        }

        if (specializedRoles.size() > 0) {
            Resource unionOfClass = model.createResource();
            unionOfClass.addProperty(RDF.type, OWL.Class);
            RDFNode[] members = new RDFNode[specializedRoles.size()];
            specializedRoles.keySet().toArray(members);
            RDFList unionList = model.createList(members);
            unionOfClass.addProperty(OWL.unionOf, unionList);

            Resource optionalityRestriction = model.createResource();
            optionalityRestriction.addProperty(RDF.type, OWL.Restriction);
            optionalityRestriction.addProperty(OWL.onProperty, SEMM.hasPossessedAspect);
            optionalityRestriction.addProperty(OWL.allValuesFrom, unionOfClass);
            possessor.addProperty(RDFS.subClassOf, optionalityRestriction);
        }
    }

    private void toggleOptionality(PossessedAspect possessedAspect) {
        String transactionDescription = "Due to toggling of optionality of possessed aspect";
        ModelTransaction transaction = getModelProvider().createTransaction(transactionDescription);
        createToggleOptionalityStatements(possessedAspect);
        getModelProvider().commitTransaction(transaction);
    }

    private void createToggleOptionalityStatements(PossessedAspect possessedAspect) {
        OntModel model = getModelProvider().getOntModel();
        Resource possessor = getResource();

        boolean inherited = !possessedAspect.getPossessor().equals(getResource());
        boolean makeMandatory = possessedAspect.isOptional();

        // Keep track of the inherited possessed aspect
        if (inherited && !makeMandatory) {
            if (!MessageDialog
                    .openQuestion(
                            getShell(),
                            "Optional Possessed Aspect",
                            "You are about to change an inherited possessed aspect from mandatory to optional, which results in an incorrect model.\n\nDo you wish to continue?")) {
                return;
            }
        }
        if (inherited) {
            possessedAspect = createDuplicateAspectStatements(possessedAspect, !makeMandatory);
        }

        Resource specializedRole = possessedAspect.getSpecializedRole();
        if (makeMandatory) {
            Resource mandatoryRestriction = model.createResource();
            mandatoryRestriction.addProperty(RDF.type, OWL.Restriction);
            mandatoryRestriction.addProperty(OWL.onProperty, SEMM.hasPossessedAspect);
            mandatoryRestriction.addProperty(OWL.someValuesFrom, specializedRole);
            possessor.addProperty(RDFS.subClassOf, mandatoryRestriction);
        }
        else if (mandatoryPossessorRestrictions.containsKey(specializedRole)) {
            Resource mandatoryRestriction = mandatoryPossessorRestrictions.get(specializedRole);
            model.remove(mandatoryRestriction, RDF.type, OWL.Restriction);
            model.remove(mandatoryRestriction, OWL.onProperty, SEMM.hasPossessedAspect);
            model.remove(mandatoryRestriction, OWL.someValuesFrom,
                    possessedAspect.getSpecializedRole());
            model.remove(possessor, RDFS.subClassOf, mandatoryRestriction);
        }
    }

    /**
     * Creates all required triples for a new value restriction
     * 
     * @param possessedAspect
     * @param object
     * @param suppressNotify
     */
    private void changeValue(PossessedAspect possessedAspect, Object object) {
        String transactionDescription = "Due to change of value";
        ModelTransaction transaction = getModelProvider().createTransaction(transactionDescription);
        createChangeValueStatements(possessedAspect, object);
        getModelProvider().commitTransaction(transaction);
    }

    private void createChangeValueStatements(PossessedAspect possessedAspect, Object object) {
        OntModel model = getModelProvider().getOntModel();
        String namespace = getResource().getNameSpace();
        String prefix = model.getNsURIPrefix(namespace);

        Resource aspect = possessedAspect.getAspect();
        // In inherited first duplicate the possessed aspect
        boolean inherited = !possessedAspect.getPossessor().equals(getResource());
        if (inherited) {
            possessedAspect = createDuplicateAspectStatements(possessedAspect,
                    possessedAspect.isOptional());
        }

        Resource specializedRole = possessedAspect.getSpecializedRole();
        Resource scale = possessedAspect.getScale();
        Resource scaleClass = aspectScales.get(aspect);
        String scaleLabel = null;

        // Update label if not set
        if (scale != null) {
            if (scale.hasProperty(RDFS.label)) {
                scaleLabel = scale.getProperty(RDFS.label).getString();
            }
            else {
                scaleLabel = scale.getLocalName();
            }
        }

        createRemoveValueRestrictionsStatements(possessedAspect);

        if (object instanceof Resource) {
            Resource value = (Resource) object;
            Restriction valueRestriction = model.createRestriction(SEMM.isQualifiedAs);

            Property prop = OWL.hasValue;
            if (value.hasProperty(RDF.type, OWL.Class) || value.hasProperty(RDF.type, RDFS.Class)) {
                prop = OWL.allValuesFrom;
            }
            else if (value.hasProperty(RDF.type, RDF.List)) {
                Resource list = value;
                value = model.createResource();
                value.addProperty(RDF.type, OWL.Class);
                value.addProperty(OWL.oneOf, list);
                prop = OWL.allValuesFrom;
            }
            valueRestriction.addProperty(prop, value);
            specializedRole.addProperty(RDFS.subClassOf, valueRestriction);
        }
        else if ((object instanceof String) && (object.toString().length() > 0)) {
            String label = object + ((scaleLabel != null) ? " " + scaleLabel : "");
            String uri = ResourceURIUtil.createURI(model, null, prefix, label);
            uri = model.expandPrefix(uri);

            Resource value = model.createResource(uri);
            value.addProperty(RDFS.label, label);
            value.addProperty(SEMM.hasValue, model.createTypedLiteral(object));
            value.addProperty(SEMM.isQualificationOf, aspect);

            if (scale != null) {
                value.addProperty(SEMM.hasScale, scale);
            }
            if (scaleClass != null) {
                value.addProperty(RDF.type, SEMM.Quantification);
            }
            else {
                value.addProperty(RDF.type, SEMM.Qualification);
            }

            Restriction valueRestriction = model.createRestriction(SEMM.isQualifiedAs);
            valueRestriction.addProperty(OWL.hasValue, value);
            specializedRole.addProperty(RDFS.subClassOf, valueRestriction);
        }
    }

    private void changeScale(PossessedAspect possessedAspect, Object object) {
        String transactionDescription = "Due to change of scale";
        ModelTransaction transaction = getModelProvider().createTransaction(transactionDescription);
        createChangeScaleStatements(possessedAspect, object);
        getModelProvider().commitTransaction(transaction);
    }

    private void createChangeScaleStatements(PossessedAspect possessedAspect, Object object) {
        OntModel model = getModelProvider().getOntModel();
        String namespace = getResource().getNameSpace();
        String prefix = model.getNsURIPrefix(namespace);

        Resource specializedRole = possessedAspect.getSpecializedRole();
        Resource aspect = possessedAspect.getAspect();
        Resource value = possessedAspect.getValue();
        Resource scaleClass = aspectScales.get(aspect);
        Resource scale = null;
        String scaleLabel = null;

        createRemoveValueRestrictionsStatements(possessedAspect);

        if (object instanceof Resource) {
            scale = (Resource) object;
        }
        else if ((object instanceof String) && (object.toString().length() > 0)) {
            scaleLabel = object.toString();
            String scaleUri = ResourceURIUtil.createURI(model, null, prefix, scaleLabel);
            scaleUri = model.expandPrefix(scaleUri);
            scale = model.createResource(scaleUri);
            scale.addProperty(RDFS.label, model.createTypedLiteral(scaleLabel));
            scale.addProperty(RDF.type, scaleClass);
        }
        // Update label if not set
        if (scale != null) {
            if (scale.hasProperty(RDFS.label)) {
                scaleLabel = scale.getProperty(RDFS.label).getString();
            }
            else {
                scaleLabel = scale.getLocalName();
            }
        }
        String valueText = value.getProperty(SEMM.hasValue).getLiteral().getLexicalForm();
        String label = valueText + ((scaleLabel != null) ? " " + scaleLabel : "");
        String uri = ResourceURIUtil.createURI(model, null, prefix, label);
        uri = model.expandPrefix(uri);

        Resource modified = model.createResource(uri);
        modified.addProperty(RDFS.label, label);
        modified.addProperty(SEMM.hasValue, value.getProperty(SEMM.hasValue).getLiteral());
        modified.addProperty(SEMM.isQualificationOf, aspect);

        if (scale != null) {
            modified.addProperty(SEMM.hasScale, scale);
        }
        if (scaleClass != null) {
            modified.addProperty(RDF.type, SEMM.Quantification);
        }
        else {
            modified.addProperty(RDF.type, SEMM.Qualification);
        }

        Restriction valueRestriction = model.createRestriction(SEMM.isQualifiedAs);
        valueRestriction.addProperty(OWL.hasValue, modified);
        specializedRole.addProperty(RDFS.subClassOf, valueRestriction);
    }

    public void addAspectFromDialog() {
        String title = "Select Aspect";
        String message = "Please select an aspect.";
        ResourceSelectionDialog dialog = new ResourceSelectionDialog(getShell(), title, message);
        dialog.setHierarchicalViewSelected(true);
        dialog.setModel(getModelProvider().getOntModel());

        dialog.setRootResources(Arrays.asList(new Resource[] { SEMM.Aspect }));
        dialog.setHierarchicalProperties(Arrays.asList(new Property[] { RDFS.subClassOf }));
        dialog.setAllowedResourceTypes(Arrays.asList(new Resource[] { OWL.Class }));

        if (dialog.open() == Window.OK) {
            createPossessedAspect(dialog.getFirstSelectedResource(), true);
        }
    }

    /**
     * Inner tree viewer for showing possessed aspects.
     * 
     * @author Mike Henrichs
     * 
     */
    private class PossessedAspectTreeViewer extends TreeViewer {

        public PossessedAspectTreeViewer(Tree tree) {
            super(tree);
        }

        public PossessedAspectTreeViewer(Composite parent, int style) {
            super(parent, style);
        }

        public PossessedAspect getSelectedElement() {
            IStructuredSelection selection = (IStructuredSelection) getSelection();
            if (selection.size() == 1) {
                return (PossessedAspect) selection.getFirstElement();
            }
            return null;
        }
    }

}
