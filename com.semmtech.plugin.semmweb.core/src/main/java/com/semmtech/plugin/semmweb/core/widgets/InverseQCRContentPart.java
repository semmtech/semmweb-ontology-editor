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
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import com.google.common.base.Optional;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.ontology.Restriction;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.OWL2;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.CorePluginImages;
import com.semmtech.plugin.semmweb.core.dialog.ResourceSelectionDialog;
import com.semmtech.plugin.semmweb.core.dnd.ResourceTransfer;
import com.semmtech.plugin.semmweb.core.forms.editor.AbstractModelResourceContent;
import com.semmtech.plugin.semmweb.core.forms.editor.OntologyFormEditor;
import com.semmtech.plugin.semmweb.core.model.ModelTransaction;
import com.semmtech.plugin.semmweb.core.ui.PartAdapter;
import com.semmtech.plugin.semmweb.core.ui.forms.RestrictionFormColors;
import com.semmtech.plugin.semmweb.core.viewers.CardinalityCellEditor;
import com.semmtech.semantics.ontology.OntClassUtil;
import com.semmtech.semantics.ontology.OntResourceUtil;
import com.semmtech.semantics.sparql.PathUtil;
import com.semmtech.semantics.sparql.QueryBuilder;
import com.semmtech.semantics.util.JenaUtil;
import com.semmtech.ui.plugin.decorators.OverlayImageIcon;
import com.semmtech.ui.plugin.viewers.LazyTreeContentProvider;
import com.semmtech.ui.plugin.viewers.TableLabelProvider;


/**
 * 
 * @author Sander Stolk
 */
public class InverseQCRContentPart extends AbstractModelResourceContentPart {
    private final Property leftProperty;
    private final Property rightProperty;
    private SingleQCRContentPart left;
    private SingleQCRContentPart right;

    private static Map<String, Boolean> expandedStates = Maps.newHashMap();
    private static Map<String, Boolean> showOwnerClassStates = Maps.newHashMap();

    public InverseQCRContentPart(AbstractModelResourceContent contentParent, Composite parent,
            FormToolkit toolkit, Property leftProperty, Property rightProperty) {
        super(contentParent, parent, toolkit);
        this.leftProperty = leftProperty;
        this.rightProperty = rightProperty;
        createFormContent();
    }

    private void createFormContent() {
        FormColors formColors = new RestrictionFormColors(Display.getCurrent());
        toolkit = new FormToolkit(formColors);
        toolkit.setBorderStyle(SWT.WRAP | SWT.MULTI);

        TableWrapLayout layout = new TableWrapLayout();
        layout.leftMargin = 15;
        layout.topMargin = 0;
        layout.bottomMargin = 0;
        layout.rightMargin = 15;
        layout.horizontalSpacing = 0;
        layout.verticalSpacing = 0;
        layout.numColumns = 3;
        setLayout(layout);

        left = new SingleQCRContentPart(contentParent, this, toolkit, leftProperty, rightProperty);
        left.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.TOP, 1, 1));

        Label seperator = toolkit.createLabel(this, "", SWT.NONE);
        TableWrapData layoutData = new TableWrapData(TableWrapData.LEFT, TableWrapData.TOP, 1, 1);
        layoutData.indent = 5;
        seperator.setLayoutData(layoutData);

        right = new SingleQCRContentPart(contentParent, this, toolkit, rightProperty, leftProperty);
        right.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.TOP, 1, 1));
    }

    public void updateWithModelInformation() {
        left.modelChanged();
        right.modelChanged();
    }

    /**
     * This inner class represents a single side of the actual
     * inverseQCRFormPart.
     * 
     * @author Mike Henrichs
     */
    private class SingleQCRContentPart extends AbstractModelResourceContentPart {
        private static final String PROPERTY_CLASS = "class";
        private static final String PROPERTY_CARDINALITY = "cardinality";
        private static final String PROPERTY_RESOURCE = "resource";

        private static final boolean SHOW_OWNER_CLASS_DEFAULT = false;
        private static final boolean EXPANDED_STATE_DEFAULT = true;
        private static final int OWNER_CLASS_COLUMN_WIDTH = 150;

        private Property property;
        private Property inverseProperty;
        private Resource range = OWL.Thing;
        private Map<Restriction, OntClass> restrictionsOwners;
        private Multimap<Resource, Restriction> onClassRestrictions;
        private Set<Resource> suggestions;

        private Section section;
        private QualifiedRestrictionTreeViewer viewer;
        private Link suggestionsLink;

        private IPartListener partListener;
        private boolean requiredRefresh;
        private LabelProvider labelProvider;
        private TreeColumn classColumn;
        private IAction toggleAction;
        private ICellEditorValidator cardinalityValidator;

        public SingleQCRContentPart(AbstractModelResourceContent contentParent, Composite parent,
                FormToolkit toolkit, Property property, Property inverseProperty) {
            super(contentParent, parent, toolkit);
            this.property = property;
            this.inverseProperty = inverseProperty;
            createContent();
        }

        @Override
        public void dispose() {
            if (partListener != null) {
                CorePlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getPartService()
                        .removePartListener(partListener);
            }
            super.dispose();
        }

        private void createContent() {
            GridLayoutFactory.fillDefaults().margins(0, 0).spacing(0, 0).applyTo(this);

            FormColors formColors = new RestrictionFormColors(Display.getCurrent());
            FormToolkit toolkit = new FormToolkit(formColors);
            toolkit.setBorderStyle(SWT.WRAP | SWT.MULTI);

            section = toolkit.createSection(this, ExpandableComposite.TWISTIE
                    | ExpandableComposite.TITLE_BAR);
            // section.setLayoutData(new GridData(GridData.FILL,
            // GridData.BEGINNING, true, false, 1, 1));
            section.addExpansionListener(new ExpansionAdapter() {
                @Override
                public void expansionStateChanged(ExpansionEvent event) {
                    expandedStates.put(property.getURI(), Boolean.valueOf(section.isExpanded()));
                    refresh();
                }
            });

            TableWrapLayout layout = new TableWrapLayout();
            layout.leftMargin = 0;
            layout.horizontalSpacing = 1;
            layout.verticalSpacing = 1;

            section.setLayout(layout);
            section.setText(getModelProvider().getLabelProvider().getText(property));

            boolean expandedState = EXPANDED_STATE_DEFAULT;
            if (!expandedStates.containsKey(property.getURI())) {
                expandedStates.put(property.getURI(), new Boolean(expandedState));
            }
            else {
                expandedState = expandedStates.get(property.getURI());
            }

            section.setExpanded(expandedState);
            section.addExpansionListener(new ExpansionAdapter() {
                @Override
                public void expansionStateChanged(ExpansionEvent e) {
                    boolean expandedState = section.isExpanded();
                    expandedStates.put(property.getURI(), new Boolean(expandedState));
                    refresh();
                }
            });

            // Create the toolbar for this section
            boolean showOwnerClass = SHOW_OWNER_CLASS_DEFAULT;
            if (!showOwnerClassStates.containsKey(property.getURI())) {
                showOwnerClassStates.put(property.getURI(), new Boolean(showOwnerClass));
            }
            else {
                showOwnerClass = showOwnerClassStates.get(property.getURI());
            }

            ToolBarManager toolBarManager = new ToolBarManager(SWT.FLAT);
            ToolBar toolbar = toolBarManager.createControl(section);
            toolbar.setCursor(Display.getDefault().getSystemCursor(SWT.CURSOR_HAND));

            toggleAction = new Action("Show owner class", IAction.AS_CHECK_BOX) {
                @Override
                public void run() {
                    boolean checked = isChecked();
                    setShowOwnerClass(checked);
                }
            };
            toggleAction.setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(
                    CorePlugin.PLUGIN_ID, CorePluginImages.IMG_SUPER_CLASS));
            toggleAction.setChecked(showOwnerClass);
            toolBarManager.add(toggleAction);

            toolBarManager.update(true);
            section.setTextClient(toolbar);

            Composite composite = toolkit.createComposite(section);
            layout = new TableWrapLayout();
            layout.leftMargin = 0;
            layout.topMargin = 0;
            layout.bottomMargin = 18;
            layout.leftMargin = 6;
            layout.horizontalSpacing = 8;
            layout.verticalSpacing = 6;
            layout.numColumns = 1;
            composite.setLayout(layout);
            composite.setLayoutData(new TableWrapData(TableWrapData.FILL, TableWrapData.TOP, 1, 1));
            section.setClient(composite);

            DropTarget dropTarget = new DropTarget(composite, DND.DROP_MOVE | DND.DROP_COPY);
            dropTarget.setTransfer(new Transfer[] { ResourceTransfer.getInstance() });
            dropTarget.addDropListener(new DropTargetAdapter() {
                @Override
                public void drop(DropTargetEvent event) {
                    if (ResourceTransfer.getInstance().isSupportedType(event.currentDataType)) {
                        Resource onClass = (Resource) event.data;
                        ModelTransaction transaction = getModelProvider().createTransaction(
                                "Created a QCR");
                        Set<Restriction> newRestrictions = createQualifiedCardinalityRestrictionsStatements(
                                onClass, Cardinality.one());
                        onClassRestrictions.putAll(onClass, newRestrictions);
                        getModelProvider().commitTransaction(transaction);
                    }
                }
            });

            labelProvider = getModelProvider().getLabelProvider();
            Label label = new Label(composite, SWT.WRAP);
            TableWrapData layoutData = new TableWrapData(TableWrapData.LEFT, TableWrapData.TOP, 1,
                    1);
            label.setLayoutData(layoutData);
            label.setText(String.format(
                    "This section shows restrictions for the current resource on the property %s.",
                    labelProvider.getText(property)));

            viewer = new QualifiedRestrictionTreeViewer(composite, SWT.BORDER | SWT.FULL_SELECTION
                    | SWT.VIRTUAL);
            viewer.setUseHashlookup(true);

            Tree tree = viewer.getTree();
            tree.setLinesVisible(true);
            tree.setHeaderVisible(true);
            layoutData = new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.TOP, 1, 1);
            layoutData.heightHint = 120;
            tree.setLayoutData(layoutData);

            classColumn = new TreeColumn(tree, SWT.NONE, 0);
            classColumn.setWidth(showOwnerClass ? OWNER_CLASS_COLUMN_WIDTH : 0);
            classColumn.setResizable(showOwnerClass);
            classColumn.setText("Class");

            TreeColumn cardinalityColumn = new TreeColumn(tree, SWT.NONE, 1);
            cardinalityColumn.setWidth(77);
            cardinalityColumn.setResizable(false);
            cardinalityColumn.setAlignment(SWT.CENTER);
            cardinalityColumn.setText("Cardinality");

            TreeColumn onClassColumn = new TreeColumn(tree, SWT.NONE, 2);
            onClassColumn.setWidth(150);
            onClassColumn.setText(labelProvider.getText(property));

            CellEditor[] editors = new CellEditor[3];
            CellEditor cardinalityEditor = new CardinalityCellEditor(tree);
            cardinalityValidator = new ICellEditorValidator() {

                @Override
                public String isValid(Object value) {
                    String errorMessage = null;
                    if (value instanceof Cardinality) {
                        QualifiedRestrictionElement selected = viewer.getSelectedElement();
                        if (selected != null) {
                            if (selected.isInherited()) {
                                Cardinality cardinality = (Cardinality) value;
                                Cardinality initial = getCardinality(selected.getRestrictions());
                                if (!Cardinality.isStricter(cardinality, initial)) {
                                    errorMessage = "Due to an inherited restriction, the entered cardinality '"
                                            + cardinality.toString()
                                            + "' is not valid. Please ensure that the value you enter is stricter then the inherited cardinality of '"
                                            + initial.toString() + "'.";
                                }
                            }
                        }
                    }
                    return errorMessage;
                }

            };
            cardinalityEditor.setValidator(cardinalityValidator);
            editors[1] = cardinalityEditor;

            viewer.setColumnProperties(new String[] { PROPERTY_CLASS, PROPERTY_CARDINALITY,
                    PROPERTY_RESOURCE });
            viewer.setCellEditors(editors);
            viewer.setCellModifier(new ICellModifier() {
                @Override
                public void modify(Object object, String columnProperty, Object value) {
                    if (columnProperty.equals(PROPERTY_CARDINALITY)) {
                        if (!(object instanceof TreeItem)) {
                            return;
                        }
                        TreeItem item = (TreeItem) object;
                        if (item.getData() == null
                                || !(item.getData() instanceof QualifiedRestrictionElement)) {
                            return;
                        }

                        // / Check before applying
                        String errorMessage = cardinalityValidator.isValid(value);
                        if (errorMessage != null) {
                            MessageDialog
                                    .openError(getShell(), "Invalid Cardinality", errorMessage);
                            return;
                        }

                        QualifiedRestrictionElement element = (QualifiedRestrictionElement) item
                                .getData();
                        Resource onClass = element.getOnClass();
                        Cardinality cardinality = (Cardinality) value;
                        Cardinality oldCardinality = getCardinality(element.getRestrictions());
                        if (!cardinality.equals(oldCardinality)) {
                            ModelTransaction transaction = getModelProvider().createTransaction(
                                    "Modified cardinality");
                            createModifyCardinalityStatements(onClass, cardinality);
                            getModelProvider().commitTransaction(transaction);
                        }
                    }
                }

                @Override
                public Object getValue(Object object, String property) {
                    QualifiedRestrictionElement element = (QualifiedRestrictionElement) object;
                    if (property.equals(PROPERTY_CARDINALITY)) {
                        return getCardinality(element.getRestrictions());
                    }
                    return null;
                }

                @Override
                public boolean canModify(Object object, String property) {
                    QualifiedRestrictionElement element = (QualifiedRestrictionElement) object;
                    boolean isLocal = element.getOwnerClass().getURI()
                            .equals(getResource().getURI());
                    if (property.equals(PROPERTY_CARDINALITY) && isLocal) {
                        return true;
                    }
                    return false;
                }
            });
            viewer.setContentProvider(new LazyTreeContentProvider() {
                @Override
                public void updateElement(Object parent, int index) {
                    if (parent instanceof SingleQCRContentPart) {
                        List<Resource> onClasses = Lists.newArrayList(onClassRestrictions.keySet());
                        Resource onClass = onClasses.get(index);

                        List<Restriction> restrictions = Lists.newArrayList(onClassRestrictions
                                .get(onClass));

                        // / This two phase step is required to determine if a
                        // mix of local and non-local exist;
                        boolean localFound = false;
                        boolean nonLocalFound = false;
                        boolean overruling = false;
                        for (Restriction restriction : restrictions) {
                            OntClass owner = restrictionsOwners.get(restriction);
                            if (owner.getURI().equals(getResource().getURI())) {
                                localFound = true;
                            }
                            else {
                                nonLocalFound = true;
                            }
                        }
                        if (localFound && nonLocalFound) {
                            overruling = true;
                            // / Remove any non-local restrictions
                            for (int i = restrictions.size() - 1; i >= 0; i--) {
                                Restriction restriction = restrictions.get(i);
                                OntClass owner = restrictionsOwners.get(restriction);
                                if (!owner.getURI().equals(getResource().getURI())) {
                                    restrictions.remove(i);
                                }
                            }
                        }

                        QualifiedRestrictionElement element = new QualifiedRestrictionElement(
                                JenaUtil.asOntClass(getResource(), getModelProvider().getOntModel()),
                                onClass, Sets.newHashSet(restrictions));
                        element.setInherited(nonLocalFound && !localFound);
                        element.setOverruling(overruling);
                        int childCount = 0;
                        boolean showOwnerClass = showOwnerClassStates.get(property.getURI());
                        if (nonLocalFound && showOwnerClass) {
                            childCount = 1;
                        }

                        viewer.replace(parent, index, element);
                        viewer.setChildCount(element, childCount);
                    }
                    else if (parent instanceof QualifiedRestrictionElement) {
                        QualifiedRestrictionElement element = (QualifiedRestrictionElement) parent;
                        OntClass ownerClass = element.getOwnerClass();
                        Resource onClass = element.getOnClass();

                        if (element.isInherited()) {
                            Set<Restriction> restrictions = element.getRestrictions();
                            OntClass superClass = findSuperClassForRestrictions(restrictions,
                                    ownerClass);
                            boolean inherited = false;
                            for (Restriction restriction : restrictions) {
                                OntClass owner = restrictionsOwners.get(restriction);
                                if (!owner.getURI().equals(superClass.getURI())) {
                                    inherited = true;
                                    break;
                                }
                            }
                            QualifiedRestrictionElement childElement = new QualifiedRestrictionElement(
                                    superClass, onClass, restrictions);
                            childElement.setInherited(inherited);

                            viewer.replace(parent, index, childElement);
                            viewer.setChildCount(childElement, ((inherited) ? 1 : 0));
                        }
                        else if (element.isOverruling()) {
                            // / TODO: Refactor - Totaal niet leesbaar!
                            OntClass childClass = null;
                            List<Restriction> allRestrictions = Lists
                                    .newArrayList(onClassRestrictions.get(onClass));
                            // / Remove local restrictions of this element
                            allRestrictions.removeAll(element.getRestrictions());

                            for (ExtendedIterator<OntClass> iter = ownerClass
                                    .listSuperClasses(false); iter.hasNext() && childClass == null;) {
                                OntClass superClass = iter.next();
                                if (superClass.isRestriction() || superClass.isAnon()) {
                                    continue;
                                }
                                for (Restriction restriction : allRestrictions) {
                                    if (restrictionsOwners.get(restriction).equals(superClass)) {
                                        childClass = superClass;
                                        break;
                                    }
                                }
                            }

                            // / Refactor - Directly copied
                            if (childClass != null) {
                                // / This two phase step is required to
                                // determine if a mix of local and non-local
                                // exist;
                                boolean localFound = false;
                                boolean nonLocalFound = false;
                                boolean overruling = false;
                                for (Restriction restriction : allRestrictions) {
                                    OntClass owner = restrictionsOwners.get(restriction);
                                    if (owner.equals(childClass)) {
                                        localFound = true;
                                    }
                                    else {
                                        nonLocalFound = true;
                                    }
                                }
                                if (localFound && nonLocalFound) {
                                    overruling = true;
                                    // / Remove any non-local restrictions
                                    for (int i = allRestrictions.size() - 1; i >= 0; i--) {
                                        Restriction restriction = allRestrictions.get(i);
                                        OntClass owner = restrictionsOwners.get(restriction);
                                        if (!owner.equals(childClass)) {
                                            allRestrictions.remove(i);
                                        }
                                    }
                                }

                                QualifiedRestrictionElement childElement = new QualifiedRestrictionElement(
                                        childClass, onClass, Sets.newHashSet(allRestrictions));
                                element.setInherited(nonLocalFound && !localFound);
                                element.setOverruling(overruling);
                                int childCount = 0;
                                if (nonLocalFound) {
                                    childCount = 1;
                                }

                                viewer.replace(parent, index, childElement);
                                viewer.setChildCount(childElement, childCount);
                            }
                        }
                    }
                }
            });
            viewer.setLabelProvider(new TableLabelProvider() {
                @Override
                public String getColumnText(Object object, int columnIndex) {
                    if (!(object instanceof QualifiedRestrictionElement)) {
                        return null;
                    }

                    QualifiedRestrictionElement element = (QualifiedRestrictionElement) object;
                    Resource onClass = element.getOnClass();
                    LabelProvider labelProvider = getModelProvider().getLabelProvider();
                    if (columnIndex == 0) {
                        return labelProvider.getText(element.getOwnerClass());
                    }
                    if (columnIndex == 1) {
                        return getCardinality(element.getRestrictions()).toString();
                    }
                    else if (columnIndex == 2) {
                        return labelProvider.getText(onClass);
                    }
                    return null;
                }

                @Override
                public Image getColumnImage(Object object, int columnIndex) {
                    if (!(object instanceof QualifiedRestrictionElement)) {
                        return null;
                    }

                    QualifiedRestrictionElement element = (QualifiedRestrictionElement) object;
                    Resource onClass = element.getOnClass();
                    LabelProvider labelProvider = getModelProvider().getLabelProvider();
                    if (columnIndex == 0) {
                        return labelProvider.getImage(element.getOwnerClass());
                    }
                    else if (columnIndex == 2) {
                        Image baseImage = labelProvider.getImage(onClass);
                        if (element.isInherited() && baseImage != null) {
                            OverlayImageIcon icon = new OverlayImageIcon(baseImage, CorePlugin
                                    .getDefault());
                            icon.addImageDecoration(CorePluginImages.IMG_OVERLAY_IMPORTED,
                                    OverlayImageIcon.BOTTOM_LEFT);
                            return icon.createImage();
                        }
                        return baseImage;
                    }
                    return null;
                }
            });

            suggestionsLink = new Link(composite, SWT.NONE);
            layoutData = new TableWrapData(TableWrapData.FILL, TableWrapData.MIDDLE, 1, 1);
            layoutData.heightHint = 20;
            suggestionsLink.setLayoutData(layoutData);
            suggestionsLink.addListener(SWT.Selection, new Listener() {
                @Override
                public void handleEvent(Event event) {
                    addSuggestion();
                }
            });

            partListener = new PartAdapter() {
                @Override
                public void partActivated(IWorkbenchPart part) {
                    if (part instanceof OntologyFormEditor) {
                        boolean expandedState = expandedStates.get(property.getURI())
                                .booleanValue();
                        if (getEditor() == part && section.isExpanded() != expandedState) {
                            section.setExpanded(expandedState);
                        }

                        boolean showOwnerClass = showOwnerClassStates.get(property.getURI())
                                .booleanValue();
                        if (showOwnerClass != toggleAction.isChecked()) {
                            toggleAction.setChecked(showOwnerClass);
                            setShowOwnerClass(showOwnerClass);
                        }

                        refresh();
                    }
                }
            };
            CorePlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getPartService()
                    .addPartListener(partListener);

            createPopupMenu();

            refreshWithModelInformation();
        }

        private void createPopupMenu() {
            MenuManager menuManager = new MenuManager();
            Menu menu = menuManager.createContextMenu(viewer.getControl());
            menuManager.setRemoveAllWhenShown(true);
            menuManager.addMenuListener(new IMenuListener() {
                @Override
                public void menuAboutToShow(IMenuManager manager) {
                    final QualifiedRestrictionElement selected = viewer.getSelectedElement();
                    IAction addAction = new Action("Add...") {
                        @Override
                        public void run() {
                            addOnClass();
                        }
                    };
                    manager.add(addAction);

                    if (suggestions.size() > 0) {
                        IAction suggestionAction = new Action("Suggestions...") {
                            @Override
                            public void run() {
                                addSuggestion();
                            }
                        };
                        manager.add(suggestionAction);
                    }

                    if (selected != null && !selected.isInherited() && viewer.isSelectedLocal()) {
                        IAction removeAction = new Action("Remove") {
                            @Override
                            public void run() {
                                removeOnClass(selected);
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

        private Set<OntClass> listPath(OntClass superClass, OntClass subClass) {
            Set<OntClass> subClasses = OntClassUtil.listSubClasses(superClass, false).toSet();
            Set<OntClass> superClasses = OntClassUtil.listSuperClasses(subClass, false).toSet();
            subClasses.add(superClass);
            superClasses.add(subClass);
            return Sets.intersection(subClasses, superClasses);
        }

        private OntClass findSuperClassForRestrictions(Set<Restriction> restrictions, OntClass clazz) {
            Set<OntClass> shortestPath = null;
            OntClass ownerShortestPath = null;
            for (Restriction restriction : restrictions) {
                OntClass owner = restrictionsOwners.get(restriction);
                Set<OntClass> path = listPath(owner, clazz);
                if (shortestPath == null || path.size() < shortestPath.size()) {
                    shortestPath = path;
                    ownerShortestPath = owner;
                }
            }
            OntClass superClass = null;
            for (ExtendedIterator<OntClass> iter = clazz.listSuperClasses(true); iter.hasNext()
                    && superClass == null;) {
                OntClass c = iter.next();
                if (shortestPath != null && shortestPath.contains(c)) {
                    superClass = c;
                }
            }
            if (superClass == null && shortestPath != null
                    && shortestPath.contains(ownerShortestPath)) {
                superClass = ownerShortestPath;
            }
            return superClass;
        }

        @Override
        public void refresh() {
            if (section.isExpanded() && requiredRefresh) {
                refreshViewer();
                refreshSuggestions();
                requiredRefresh = false;
            }
        }

        private void refreshViewer() {
            // / TableViewer
            findRestrictions();
            viewer.setInput(this);
            viewer.getTree().setItemCount(onClassRestrictions.keySet().size());
        }

        private void refreshSuggestions() {
            // / SuggestionLink
            findSuggestions();
            suggestions.removeAll(onClassRestrictions.keySet());
            String suggestion = "No suggestions found";
            if (suggestions.size() == 1) {
                suggestion = "Found one suggestion, click <a>here</a> to view it";
            }
            else if (suggestions.size() > 1) {
                suggestion = "Found " + suggestions.size()
                        + " suggestions, click <a>here</a> to view them";
            }
            suggestionsLink.setText(suggestion);
        }

        public void refreshWithModelInformation() {
            if (!section.isExpanded()) {
                requiredRefresh = true;
            }
            else {
                refreshViewer();
                refreshSuggestions();
            }
        }

        /**
         * Method will refreshViewer - if the section is expanded; otherwise
         * requiredRefresh will be set to true (signaling a deferred
         * refreshViewer).
         */
        public void modelChanged() {
            refreshWithModelInformation();
        }

        /**
         * Finds any relevant restrictions for the current property; and
         * initializes all structures required by other controls.
         */
        protected void findRestrictions() {
            // / Clear
            restrictionsOwners = Maps.newLinkedHashMap();
            onClassRestrictions = HashMultimap.create();

            // / Find restrictions on this class for the current property.
            if (OntResourceUtil.isClass(getResource())) {
                Map<Restriction, OntClass> allRestrictions = OntClassUtil
                        .listRestrictionsByClass(JenaUtil.asOntClass(getResource()));
                for (Restriction restriction : allRestrictions.keySet()) {
                    if ((restriction == null) || (restriction.getOnProperty() == null)
                            || !restriction.getOnProperty().equals(property)) {
                        continue;
                    }

                    if (restriction.hasProperty(OWL.someValuesFrom)) {
                        restrictionsOwners.put(restriction, allRestrictions.get(restriction));
                        onClassRestrictions.put(
                                restriction.getPropertyResourceValue(OWL.someValuesFrom),
                                restriction);
                    }
                    else if (restriction.hasProperty(OWL2.onClass)) {
                        restrictionsOwners.put(restriction, allRestrictions.get(restriction));
                        onClassRestrictions.put(restriction.getPropertyResourceValue(OWL2.onClass),
                                restriction);
                    }
                }
            }
        }

        public void findSuggestions() {
            suggestions = Sets.newHashSet();
            OntModel ontModel = getModelProvider().getOntModel();
            Resource resource = getResource();
            for (Statement statement : ontModel.listStatements(null, OWL2.onClass, resource)
                    .toSet()) {
                Restriction restriction = statement.getSubject().as(Restriction.class);
                if (!restriction.onProperty(inverseProperty)) {
                    continue;
                }
                for (OntClass suggestion : restriction.listSubClasses(false).toSet()) {
                    if (!suggestion.isRestriction()) {
                        suggestions.add(suggestion);
                    }
                }
            }
        }

        private Cardinality getCardinality(Collection<Restriction> restrictions) {
            Cardinality cardinality = Cardinality.unbounded();
            for (Restriction restriction : restrictions) {
                if (restriction.hasProperty(OWL.someValuesFrom)) {
                    cardinality.setMin(1);
                }
                else if (restriction.hasProperty(OWL2.qualifiedCardinality)) {
                    cardinality.setMin(restriction.getProperty(OWL2.qualifiedCardinality)
                            .getLiteral().getInt());
                    cardinality.setMax(restriction.getProperty(OWL2.qualifiedCardinality)
                            .getLiteral().getInt());
                    cardinality.setUnbounded(false);
                }
                else if (restriction.hasProperty(OWL2.minQualifiedCardinality)) {
                    cardinality.setMin(restriction.getProperty(OWL2.minQualifiedCardinality)
                            .getLiteral().getInt());
                }
                else if (restriction.hasProperty(OWL2.maxQualifiedCardinality)) {
                    cardinality.setMax(restriction.getProperty(OWL2.maxQualifiedCardinality)
                            .getLiteral().getInt());
                    cardinality.setUnbounded(false);
                }
            }
            return cardinality;
        }

        /**
         * TODO: Refactor to more generic place; SEMMModel?
         * 
         * @param onClass
         * @param cardinality
         * @param supressNotify
         * @return
         */
        protected void createQualifiedCardinalityRestrictions(Resource onClass,
                Cardinality cardinality) {
            String transactionDescription = "Due to the creation of part restrictions";
            ModelTransaction transaction = getModelProvider().createTransaction(
                    transactionDescription);
            createQualifiedCardinalityRestrictionsStatements(onClass, cardinality);
            getModelProvider().commitTransaction(transaction);
        }

        private Set<Restriction> createQualifiedCardinalityRestrictionsStatements(Resource onClass,
                Cardinality cardinality) {
            Set<Restriction> additions = Sets.newHashSet();
            OntModel ontModel = getModelProvider().getOntModel();
            OntResource ontResource = getResource();

            if (!cardinality.getUnbounded() && cardinality.getMin() == cardinality.getMax()) {
                Restriction restriction = ontModel.createRestriction(property);
                ontResource.addProperty(RDFS.subClassOf, restriction);
                restriction.addProperty(OWL2.onClass, onClass);
                restriction.addProperty(OWL2.qualifiedCardinality,
                        ontModel.createTypedLiteral(cardinality.getMin()));
                additions.add(restriction);
            }
            else {
                Restriction minRestriction = ontModel.createRestriction(property);
                ontResource.addProperty(RDFS.subClassOf, minRestriction);
                minRestriction.addProperty(OWL2.onClass, onClass);
                minRestriction.addProperty(OWL2.minQualifiedCardinality,
                        ontModel.createTypedLiteral(cardinality.getMin()));
                additions.add(minRestriction);
                if (!cardinality.getUnbounded()) {
                    Restriction maxRestriction = ontModel.createRestriction(property);
                    ontResource.addProperty(RDFS.subClassOf, maxRestriction);
                    maxRestriction.addProperty(OWL2.onClass, onClass);
                    maxRestriction.addProperty(OWL2.maxQualifiedCardinality,
                            ontModel.createTypedLiteral(cardinality.getMax()));
                    additions.add(maxRestriction);
                }
            }
            return additions;
        }

        /**
         * TODO: Refactor to more generic place; SEMMModel?
         * 
         * @param onClass
         */
        @SuppressWarnings("unused")
        protected void removeQualifiedCardinalityRestrictions(Resource onClass) {
            String transactionDescription = "Due to removal of part";
            ModelTransaction transaction = getModelProvider().createTransaction(
                    transactionDescription);
            createRemoveQualifiedCardinalityRestrictionsStatements(onClass);
            getModelProvider().commitTransaction(transaction);
        }

        private void createRemoveQualifiedCardinalityRestrictionsStatements(Resource onClass) {
            OntModel model = getModelProvider().getOntModel();

            Var varS = Var.alloc("s");
            Triple t1 = new Triple(getResource().asNode(), RDFS.subClassOf.asNode(), varS);
            QueryBuilder qb = QueryBuilder.createSelect(true).addTriplePattern(t1)
                    .addResultVar(varS);

            List<Statement> removeStatements = Lists.newArrayList();
            for (ResultSet iter = qb.execSelect(model); iter.hasNext();) {
                Resource superClass = iter.next().getResource(varS.getName());
                if (isRestriction(superClass) && superClass.hasProperty(OWL.onProperty, property)
                        && superClass.hasProperty(OWL2.onClass, onClass)) {
                    removeStatements.addAll(superClass.listProperties().toList());
                    removeStatements.add(model.createStatement(getResource(), RDFS.subClassOf,
                            superClass));
                }
            }
            model.remove(removeStatements);
        }

        private boolean isRestriction(Resource resource) {
            Triple pattern = new Triple(resource.asNode(),
                    PathUtil.getNode(PathUtil.IS_INSTANCE_OF), OWL.Restriction.asNode());
            QueryBuilder query = QueryBuilder.createAsk().addTriplePattern(pattern);
            return query.execAsk(resource.getModel());
        }

        private void createModifyCardinalityStatements(Resource onClass, Cardinality cardinality) {
            createRemoveQualifiedCardinalityRestrictionsStatements(onClass);
            createQualifiedCardinalityRestrictionsStatements(onClass, cardinality);
        }

        private Set<Resource> selectSuggestions() {
            String title = "Select Class";
            String message = "Please select a class from the list.";
            ResourceSelectionDialog dialog = new ResourceSelectionDialog(getShell(), title, message);
            dialog.setHierarchicalViewSelected(false);
            dialog.setModel(getModelProvider().getOntModel());
            dialog.setMultiSelectAllowed(true);

            List<Resource> resources = Lists.newArrayList(suggestions);
            dialog.setRootResources(resources);
            dialog.clearHierarchicalProperties();

            if (dialog.open() == Window.OK) {
                return dialog.getSelectedResources();
            }
            return Sets.newHashSet();
        }

        private Optional<Resource> selectOnClassResource() {
            String title = "Select Class";
            String message = "Please select a class from the list.";
            ResourceSelectionDialog dialog = new ResourceSelectionDialog(getShell(), title, message);
            dialog.setHierarchicalViewSelected(false);
            dialog.setModel(getModelProvider().getOntModel());

            dialog.setRootResources(Arrays.asList(new Resource[] { range }));
            dialog.setHierarchicalProperties(Arrays.asList(new Property[] { RDFS.subClassOf }));
            dialog.setAllowedResourceTypes(Arrays.asList(new Resource[] { RDFS.Class, OWL.Class }));
            dialog.addExcludedResource(OWL.Nothing);
            for (Resource onClass : onClassRestrictions.keySet()) {
                dialog.addExcludedResource(onClass);
            }

            if (dialog.open() == Window.OK) {
                Resource onClass = dialog.getFirstSelectedResource();
                return Optional.fromNullable(onClass);
            }
            return Optional.absent();
        }

        public void addSuggestion() {
            Set<Resource> resources = selectSuggestions();
            if (resources.size() > 0) {
                String transactionDescription = "Due to creation of restrictions from suggestions";
                ModelTransaction transaction = getModelProvider().createTransaction(
                        transactionDescription);
                for (Resource resource : resources) {
                    createQualifiedCardinalityRestrictionsStatements(resource,
                            Cardinality.unbounded());
                }
                getModelProvider().commitTransaction(transaction);
            }
        }

        public void addOnClass() {
            Optional<Resource> onClass = selectOnClassResource();
            if (onClass.isPresent()) {
                createQualifiedCardinalityRestrictions(onClass.get(), Cardinality.unbounded());
            }
        }

        public void removeOnClass(final QualifiedRestrictionElement selected) {
            Resource onClass = selected.getOnClass();

            String transactionDescription = "Due to removal of QCRs";
            ModelTransaction transaction = getModelProvider().createTransaction(
                    transactionDescription);
            createRemoveQualifiedCardinalityRestrictionsStatements(onClass);
            onClassRestrictions.removeAll(onClass);
            getModelProvider().commitTransaction(transaction);
        }

        public void setShowOwnerClass(boolean checked) {
            showOwnerClassStates.put(property.getURI(), new Boolean(checked));
            classColumn.setWidth((checked) ? OWNER_CLASS_COLUMN_WIDTH : 0);
            classColumn.setResizable(checked);

            viewer.getTree().update();

            refreshViewer();
        }
    }

    /**
     * Inner presentation class used by the QualifiedRestrictionTreeViewer.
     * 
     * @author Mike Henrichs
     * 
     */
    private class QualifiedRestrictionElement {
        private Resource onClass;
        private OntClass ownerClass;
        private Set<Restriction> restrictions;
        private boolean inherited;
        private boolean overruling;

        public QualifiedRestrictionElement(OntClass ownerClass, Resource onClass,
                Set<Restriction> restrictions) {
            this.ownerClass = ownerClass;
            this.onClass = onClass;
            this.restrictions = restrictions;
        }

        public boolean isInherited() {
            return inherited;
        }

        public void setInherited(boolean inherited) {
            this.inherited = inherited;
        }

        public boolean isOverruling() {
            return overruling;
        }

        public void setOverruling(boolean overruling) {
            this.overruling = overruling;
        }

        public Resource getOnClass() {
            return onClass;
        }

        public OntClass getOwnerClass() {
            return ownerClass;
        }

        public Set<Restriction> getRestrictions() {
            return restrictions;
        }
    }

    /**
     * Inner tree viewer class; which exposes a number of restriction specific
     * methods.
     * 
     * @author Mike Henrichs
     * 
     */
    private class QualifiedRestrictionTreeViewer extends TreeViewer {

        public QualifiedRestrictionTreeViewer(Composite parent, int style) {
            super(parent, style);
        }

        public QualifiedRestrictionElement getSelectedElement() {
            IStructuredSelection selection = (IStructuredSelection) getSelection();
            if (selection.size() == 1) {
                return (QualifiedRestrictionElement) selection.getFirstElement();
            }
            return null;
        }

        public boolean isSelectedLocal() {
            IStructuredSelection selection = (IStructuredSelection) getSelection();
            if (selection.size() == 1) {
                QualifiedRestrictionElement selected = (QualifiedRestrictionElement) selection
                        .getFirstElement();
                return selected.getOwnerClass().getURI().equals(getResource().getURI());
            }
            return false;
        }
    }

}
