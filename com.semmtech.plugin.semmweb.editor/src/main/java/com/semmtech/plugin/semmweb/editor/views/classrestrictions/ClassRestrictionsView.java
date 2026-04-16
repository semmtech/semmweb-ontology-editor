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

package com.semmtech.plugin.semmweb.editor.views.classrestrictions;


import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILazyTreeContentProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.TextStyle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.commands.IElementUpdater;
import org.eclipse.ui.menus.UIElement;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.Restriction;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.OWL2;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.CorePluginImages;
import com.semmtech.plugin.semmweb.core.actions.EditRestrictionSuite;
import com.semmtech.plugin.semmweb.core.commands.Commands;
import com.semmtech.plugin.semmweb.core.dialog.CardinalityInputDialog;
import com.semmtech.plugin.semmweb.core.dialog.ResourceSelectionDialog;
import com.semmtech.plugin.semmweb.core.dnd.OntClassTransfer;
import com.semmtech.plugin.semmweb.core.dnd.PropertyArrayListTransfer;
import com.semmtech.plugin.semmweb.core.dnd.PropertyTransfer;
import com.semmtech.plugin.semmweb.core.model.IModelProvider;
import com.semmtech.plugin.semmweb.core.model.ModelTransaction;
import com.semmtech.plugin.semmweb.core.model.PropertyArrayList;
import com.semmtech.plugin.semmweb.core.model.ResourceStatements;
import com.semmtech.plugin.semmweb.core.model.RestrictionResource;
import com.semmtech.plugin.semmweb.core.model.RestrictionStatements;
import com.semmtech.plugin.semmweb.core.model.RestrictionsModel;
import com.semmtech.plugin.semmweb.core.model.events.ModelActivatedEvent;
import com.semmtech.plugin.semmweb.core.model.events.ModelChangedEvent;
import com.semmtech.plugin.semmweb.core.model.events.NamespacePrefixChangedEvent;
import com.semmtech.plugin.semmweb.core.model.events.SubModelAddedEvent;
import com.semmtech.plugin.semmweb.core.model.events.SubModelRemovedEvent;
import com.semmtech.plugin.semmweb.core.preferences.LabelsPreference;
import com.semmtech.plugin.semmweb.core.preferences.LanguagesPreference;
import com.semmtech.plugin.semmweb.core.viewers.ResourceToolTipContent;
import com.semmtech.plugin.semmweb.core.views.AbstractModelListenerView;
import com.semmtech.plugin.semmweb.core.widgets.DropComposite;
import com.semmtech.plugin.semmweb.core.widgets.trees.TreeData;
import com.semmtech.plugin.semmweb.core.wizards.CreateRestrictionWizard;
import com.semmtech.plugin.semmweb.editor.EditorPlugin;
import com.semmtech.plugin.semmweb.editor.EditorPluginImages;
import com.semmtech.ui.plugin.decorators.OverlayImageIcon;
import com.semmtech.ui.plugin.viewers.LazyTreeContentProvider;
import com.semmtech.ui.plugin.widgets.Widgets;


/**
 * 
 * @author Mike Henrichs
 * @author Sander Stolk
 */
public class ClassRestrictionsView extends AbstractModelListenerView implements
        IPropertyChangeListener {

    private static final Logger logger = Logger.getLogger(ClassRestrictionsView.class);

    public static final String ID = "com.semmtech.plugin.semmweb.editor.views.classRestrictions";

    public final static String PARAM_ROOT_CLASS = "var:rootClass";
    public final static String PARAM_SHOW_FILTER = "var:showFilter";
    public final static String PARAM_FILTER_PROPERTIES = "var:filterProperties";

    private static ImageDescriptor IMAGE_CARDINALITY_NONE = CorePlugin.getDefault()
            .getImageDescriptor(CorePluginImages.IMG_CARDINALITY_NONE);
    private static ImageDescriptor IMAGE_CARDINALITY_OPTIONAL = CorePlugin.getDefault()
            .getImageDescriptor(CorePluginImages.IMG_CARDINALITY_OPTIONAL);
    private static ImageDescriptor IMAGE_CARDINALITY_ONE = CorePlugin.getDefault()
            .getImageDescriptor(CorePluginImages.IMG_CARDINALITY_ONE);
    private static ImageDescriptor IMAGE_CARDINALITY_SOME = CorePlugin.getDefault()
            .getImageDescriptor(CorePluginImages.IMG_CARDINALITY_SOME);
    private static ImageDescriptor IMAGE_CARDINALITY_UNBOUNDED = CorePlugin.getDefault()
            .getImageDescriptor(CorePluginImages.IMG_CARDINALITY_UNBOUNDED);
    private static ImageDescriptor IMAGE_DELETE = CorePlugin.getDefault().getImageDescriptor(
            CorePluginImages.IMG_DELETE);

    private ImageDescriptor addRestrictionImage;

    private Vector<ClassRestrictionItem> restrictions;

    /**
     * 
     * @author Mike Henrichs
     * 
     */
    private final class ClassRestrictionsContentProvider extends LazyTreeContentProvider {

        @Override
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
            restrictions = new Vector<>();
        }

        private List<ClassRestrictionItem> findRestrictions(OntClass clazz) {
            if (!restrictions.isEmpty()) {
                return restrictions;
            }
            Set<Property> seenProperties = Sets.newHashSet();
            if (restrictions.isEmpty()) {
                Set<OntClass> superClasses = clazz.listSuperClasses(false).toSet();
                for (OntClass superClass : superClasses) {
                    if (!superClass.hasProperty(RDF.type, OWL.Restriction)) {
                        continue;
                    }
                    Restriction restriction = superClass.as(Restriction.class);
                    if (!restriction.hasProperty(OWL.onProperty)) {
                        continue;
                    }
                    Property onProperty = restriction.getOnProperty();
                    if (getShowFilter() && !getFilterProperties().isEmpty()
                            && !getFilterProperties().contains(onProperty)) {
                        continue;
                    }

                    boolean inherited = !model.contains(clazz, RDFS.subClassOf, superClass);
                    ClassRestrictionItem value = null;
                    for (ClassRestrictionItem item : restrictions) {
                        if (item.containsRestrictionsOfSameSet(restriction)) {
                            value = item;
                            value.addRestriction(restriction, inherited);
                            break;
                        }
                    }
                    if (value == null) {
                        value = new ClassRestrictionItem(clazz, onProperty);
                        value.addRestriction(restriction, inherited);
                        restrictions.add(value);
                        seenProperties.add(onProperty);
                    }
                }
                // Disabled: Adds all filter properties to restrictions (unclear
                // if actually asserted or based on filter)
                // for (Resource resource : filterProperties) {
                // Property property = resource.as(Property.class);
                // if (seenProperties.contains(property)) {
                // continue;
                // }
                // restrictionsTable.put(clazz, property, new
                // ClassRestrictionItem(clazz, property));
                // }
            }

            return Lists.newArrayList(restrictions);
        }

        @Override
        public void updateElement(Object parent, int index) {
            if (parent == null) {
                return;
            }
            if (parent instanceof Model) {
                OntClass root = getRootClass();
                if (root == null) {
                    return;
                }
                String id = root.isAnon() ? root.getId().toString() : root.getURI();
                TreeData childElement = new TreeData(id, root);
                viewer.replace(parent, index, childElement);
                int childCount = findRestrictions(root).size();
                viewer.setChildCount(childElement, childCount);
                viewer.expandToLevel(1);
            }
            else if (parent instanceof TreeData) {
                TreeData parentElement = (TreeData) parent;
                OntClass clazz = null;
                if (parentElement.getData() instanceof OntClass) {
                    clazz = (OntClass) parentElement.getData();
                }
                if (clazz == null) {
                    return;
                }

                List<ClassRestrictionItem> restrictions = findRestrictions(clazz);
                Collections.sort(restrictions, new ClassRestrictionItemComparator());
                if (index < restrictions.size()) {
                    ClassRestrictionItem restriction = restrictions.get(index);
                    // Each tree element requires a unique String, otherwise
                    // updating one tree element will cause to update the other
                    // as well.
                    // Such an update of the second item will in turn cause an
                    // update of the first item, leading to an infinite update
                    // loop.
                    // As such, childElementId contains most properties of its
                    // ClassRestrictionItem
                    String childElementId = restriction.getOnProperty().getURI();
                    if (restriction.getOnClass() != null) {
                        childElementId += "&ONCLASS=" + restriction.getOnClass().getURI();
                    }
                    else if (restriction.getValue() != null) {
                        childElementId += "&VALUE=" + restriction.getValue().toString();
                    }
                    childElementId += "&MIN=" + restriction.getMin() + "&MAX="
                            + restriction.getMax();

                    TreeData childElement = new TreeData(childElementId, restriction);
                    childElement.setParent(parentElement);
                    viewer.replace(parent, index, childElement);
                    viewer.setChildCount(childElement, 0);
                }
            }
        }
    }

    private final class ClassRestrictionItemComparator implements Comparator<ClassRestrictionItem> {

        @Override
        public int compare(ClassRestrictionItem i1, ClassRestrictionItem i2) {
            if (i1 == null && i2 == null) {
                return 0;
            }
            else if (i1 == null) {
                return 1;
            }
            else if (i2 == null) {
                return -1;
            }
            String name1 = rdfLabelProvider.getText(i1.getOnProperty());
            String name2 = rdfLabelProvider.getText(i2.getOnProperty());
            int result = name1.compareTo(name2);
            if (result != 0) {
                return result;
            }
            if (i1.getOnClass() == null) {
                return 1;
            }
            if (i2.getOnClass() == null) {
                return -1;
            }
            name1 = rdfLabelProvider.getText(i1.getOnClass());
            name2 = rdfLabelProvider.getText(i2.getOnClass());
            result = name1.compareTo(name2);
            if (result != 0) {
                return result;
            }

            if (i1.getMin() != i2.getMin()) {
                return (i1.getMin() < i2.getMin()) ? -1 : 1;
            }
            if (i1.getMax() != i2.getMax()) {
                return (i1.getMax() < i2.getMax()) ? -1 : 1;
            }
            return 0;
        }

    }

    /**
     * Class used to represent a combination of restrictions based on a single
     * property.
     * 
     * @author Mike Henrichs
     * 
     */
    private final class ClassRestrictionItem {
        private Property onProperty;
        private OntClass parent;
        private OntClass onClass;
        private RDFNode value;
        private int min;
        private int max = -1;
        private boolean minCardinalityInherited;
        private boolean maxCardinalityInherited;

        private Set<Restriction> restrictions;

        public ClassRestrictionItem(OntClass parent, Property onProperty) {
            this.onProperty = onProperty;
            this.parent = parent;
            this.restrictions = new HashSet<>();
        }

        public boolean containsRestrictionsOfSameSet(Restriction restriction) {
            if (restriction.onProperty(onProperty) == false) {
                return false;
            }

            OntClass restrictionOnClass = RestrictionResource.getOnClass(restriction);
            // if ((restrictionOnClass == null) != (onClass == null)) {
            // return false;
            // }

            if (restrictionOnClass != null) {
                if (restrictionOnClass.equals(onClass) == false) {
                    return false;
                }
            }

            RDFNode restrictionValue = RestrictionResource.getValue(restriction);
            // if ((restrictionValue == null) != (value == null)) {
            // return false;
            // }
            if (restrictionValue != null) {
                if (restrictionValue.equals(value) == false) {
                    return false;
                }
            }

            return true;
        }

        public boolean isInherited() {
            return (minCardinalityInherited || maxCardinalityInherited);
        }

        public void addRestriction(Restriction restriction, boolean inherited) {
            if (!restriction.getOnProperty().equals(onProperty)) {
                return;
            }

            // Min
            int curMin = RestrictionResource.getMinCardinality(restriction);
            if (curMin >= min) {
                if (restrictions.isEmpty() || (curMin > min) || (inherited == false)) {
                    minCardinalityInherited = inherited;
                }
                min = curMin;
            }

            // Max
            int curMax = RestrictionResource.getMaxCardinality(restriction);
            if (max == -1 || (curMax >= 0 && curMax <= max)) {
                if (restrictions.isEmpty() || (curMax < max) || (inherited == false)) {
                    maxCardinalityInherited = inherited;
                }
                max = curMax;
            }

            // OnClass
            onClass = RestrictionResource.getOnClass(restriction);

            // Value
            value = RestrictionResource.getValue(restriction);

            restrictions.add(restriction);
        }

        public Set<Restriction> getRestrictions() {
            return restrictions;
        }

        // public boolean hasRestrictions() {
        // return (restrictions.size() > 0);
        // }

        public Property getOnProperty() {
            return onProperty;
        }

        /**
         * Returns the lower bound of the restriction.
         * 
         * @return
         */
        public int getMin() {
            return min;
        }

        /**
         * Returns the upper bound of the restriction.
         * 
         * @return
         */
        public int getMax() {
            return max;
        }

        public void setCardinality(int min, int max) {
            this.min = min;
            this.max = max;

            IModelProvider modelProvider = getModelProvider();
            if (modelProvider != null) {
                ModelTransaction transaction = modelProvider.createTransaction("Set cardinality");
                RestrictionsModel restrictionsModel = new RestrictionsModel(getOntModel());
                List<Restriction> restrictionsList = Lists.newArrayList();
                restrictionsList.addAll(restrictions);
                restrictionsModel.setCardinality(restrictionsList, min, max, getRootClass());
                modelProvider.commitTransaction(transaction);
            }
        }

        /**
         * Returns the class.
         * 
         * @return
         */
        public OntClass getOnClass() {
            return onClass;
        }

        public void setOnClass(OntClass onClass) {
            this.onClass = onClass;

            IModelProvider modelProvider = getModelProvider();
            if (modelProvider == null) {
                return;
            }

            ModelTransaction transaction = modelProvider.createTransaction("Set onClass");
            createOnClassStatements(onClass);
            modelProvider.commitTransaction(transaction);
        }

        private void createOnClassStatements(OntClass onClass) {
            boolean modified = false;
            if (onClass != null) {
                for (Restriction restriction : restrictions) {
                    if (restriction.hasProperty(OWL2.onClass)) {
                        restriction.setPropertyValue(OWL2.onClass, onClass);
                        modified = true;
                    }
                    if (restriction.hasProperty(OWL.allValuesFrom)) {
                        restriction.setPropertyValue(OWL.allValuesFrom, onClass);
                        modified = true;
                    }
                    if (restriction.hasProperty(OWL.someValuesFrom)) {
                        restriction.setPropertyValue(OWL.someValuesFrom, onClass);
                        modified = true;
                    }
                }
                if (!modified) {
                    for (Restriction restriction : restrictions) {
                        model.remove(parent, RDFS.subClassOf, restriction);
                        restriction.remove();
                    }
                    Restriction restriction = model.createRestriction(onProperty);
                    if (min == 1 && max == -1) {
                        restriction.addProperty(OWL.someValuesFrom, onClass);
                    }
                    else if (min > 0 && min == max) {
                        restriction.addProperty(OWL2.onClass, onClass);
                        restriction.addProperty(OWL2.qualifiedCardinality, Integer.toString(min),
                                XSDDatatype.XSDnonNegativeInteger);
                    }
                    else {
                        restriction.addProperty(OWL2.onClass, onClass);
                        restriction.addProperty(OWL2.minQualifiedCardinality,
                                model.createTypedLiteral(min));
                        if (max >= 0) {
                            model.add(parent, RDFS.subClassOf, restriction);
                            restriction = model.createRestriction(onProperty);
                            restriction.addProperty(OWL2.onClass, onClass);
                            restriction.addProperty(OWL2.maxQualifiedCardinality,
                                    Integer.toString(max), XSDDatatype.XSDnonNegativeInteger);
                        }
                    }
                    model.add(parent, RDFS.subClassOf, restriction);
                    modified = true;
                }
            }
            else {
                for (Restriction restriction : restrictions) {
                    if (restriction.hasProperty(OWL2.onClass)) {
                        if (restriction.hasProperty(OWL2.minQualifiedCardinality)) {
                            restriction.removeAll(OWL2.onClass);
                            restriction.removeAll(OWL2.minQualifiedCardinality);
                            restriction.addProperty(OWL.minCardinality, Integer.toString(min),
                                    XSDDatatype.XSDnonNegativeInteger);
                        }
                        if (restriction.hasProperty(OWL2.maxQualifiedCardinality)) {
                            restriction.removeAll(OWL2.onClass);
                            restriction.removeAll(OWL2.maxQualifiedCardinality);
                            restriction.addProperty(OWL.maxCardinality, Integer.toString(max),
                                    XSDDatatype.XSDnonNegativeInteger);
                        }
                        if (restriction.hasProperty(OWL2.qualifiedCardinality)) {
                            restriction.removeAll(OWL2.onClass);
                            restriction.removeAll(OWL2.qualifiedCardinality);
                            restriction.addProperty(OWL.cardinality, Integer.toString(max),
                                    XSDDatatype.XSDnonNegativeInteger);
                        }
                        modified = true;
                    }
                    if (restriction.hasProperty(OWL.allValuesFrom)) {
                        restriction.remove();
                        // FIXME: if schizophrenic, we should retain the other
                        // restriction kinds
                        modified = true;
                    }
                    if (restriction.hasProperty(OWL.someValuesFrom)) {
                        restriction.removeAll(OWL.someValuesFrom);
                        restriction.addProperty(OWL.minCardinality, Integer.toString(1),
                                XSDDatatype.XSDnonNegativeInteger);
                        modified = true;
                    }
                }
            }
        }

        /**
         * Returns a value attached to the restriction.
         * 
         * @return
         */
        public RDFNode getValue() {
            return value;
        }

        private void createSetValueStatements() {
            boolean modified = false;
            for (Restriction restriction : restrictions) {
                if (restriction.hasProperty(OWL.hasValue)) {
                    restriction.setPropertyValue(OWL.hasValue, value);
                    modified = true;
                }
            }
            if (!modified) {
                Restriction restriction = model.createRestriction(onProperty);
                restriction.addProperty(OWL.hasValue, value);
                model.add(parent, RDFS.subClassOf, restriction);
                modified = true;
            }
        }

        @SuppressWarnings("unused")
        public void setValue(RDFNode value) {
            this.value = value;
            IModelProvider modelProvider = getModelProvider();
            if (modelProvider != null) {
                ModelTransaction transaction = modelProvider.createTransaction("Set value");

                createSetValueStatements();

                modelProvider.commitTransaction(transaction);
            }
        }

        public void delete() {
            IModelProvider modelProvider = getModelProvider();
            if (modelProvider != null) {
                ModelTransaction transaction = modelProvider.createTransaction("Delete");

                List<Restriction> restrictionsList = Lists.newArrayList();
                restrictionsList.addAll(restrictions);
                RestrictionStatements.createRemoveRestrictionStatements(restrictionsList);

                modelProvider.commitTransaction(transaction);
            }
        }
    }

    private final class ClassRestrictionsLabelProvider extends StyledCellLabelProvider {

        private final Styler predicateStyler = new Styler() {
            @Override
            public void applyStyles(TextStyle textStyle) {
                textStyle.foreground = Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GRAY);
            }
        };

        @Override
        public void update(ViewerCell cell) {
            Object element = cell.getElement();
            Image image = null;
            StyledString styledText = new StyledString();

            if (rdfLabelProvider != null) {
                if (element instanceof RDFNode) {
                    String text = rdfLabelProvider.getText(element);
                    if (text != null) {
                        styledText.append(text);
                    }
                    image = rdfLabelProvider.getImage(element);
                }
                else if (element instanceof TreeData) {
                    TreeData treeData = (TreeData) element;
                    if (treeData.getData() instanceof ClassRestrictionItem) {
                        ClassRestrictionItem restriction = (ClassRestrictionItem) treeData
                                .getData();
                        boolean inherited = restriction.isInherited();
                        Property onProperty = restriction.getOnProperty();
                        String propertyName = rdfLabelProvider.getText(onProperty);
                        if (propertyName != null) {
                            int max = restriction.getMax();
                            OntClass onClass = restriction.getOnClass();
                            RDFNode value = restriction.getValue();

                            String text = propertyName;

                            if (value == null) {
                                text += String.format(" [%s,%s]", restriction.getMin(),
                                        (max < 0) ? "n" : max);

                                if (onClass != null) {
                                    text += ": " + rdfLabelProvider.getText(onClass);
                                }
                            }
                            else {
                                text += " -> " + rdfLabelProvider.getText(value);
                            }

                            styledText.append(text);

                            if (inherited) {
                                styledText.setStyle(0, styledText.length(), predicateStyler);
                            }
                        }
                        image = rdfLabelProvider.getImage(onProperty);
                    }
                    else if (treeData.getData() instanceof RDFNode) {
                        RDFNode node = (RDFNode) treeData.getData();
                        String text = rdfLabelProvider.getText(node);
                        if (text != null) {
                            styledText.append(text);
                        }
                        image = rdfLabelProvider.getImage(node);
                    }
                }
            }
            cell.setText(styledText.getString());
            cell.setStyleRanges(styledText.getStyleRanges());
            cell.setImage(image);
            super.update(cell);
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
                return "resource";
            }
            else if (element instanceof TreeData) {
                Object data = ((TreeData) element).getData();
                if (data instanceof Statement) {
                    return rdfLabelProvider.getText(((Statement) data).getObject());
                }
            }
            return null;
        }
    }

    private class ClassDropTargetListener extends DropTargetAdapter {
        private ClassRestrictionItem target;

        @Override
        public void dragOver(DropTargetEvent event) {
            target = null;
            if (!(event.item instanceof TreeItem)) {
                return;
            }
            TreeItem item = (TreeItem) event.item;
            if (!(item.getData() instanceof TreeData)) {
                return;
            }
            TreeData data = (TreeData) item.getData();
            if (data.getData() instanceof ClassRestrictionItem) {
                target = (ClassRestrictionItem) data.getData();
            }
        }

        private boolean containsRestrictionOnProperty(List<ClassRestrictionItem> restrictionItems,
                Property onProperty) {
            for (ClassRestrictionItem item : restrictionItems) {
                if (onProperty.equals(item.getOnProperty())) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public void drop(DropTargetEvent event) {
            if (OntClassTransfer.getInstance().isSupportedType(event.currentDataType)) {
                OntClass clazz = (OntClass) OntClassTransfer.getInstance().nativeToJava(
                        event.currentDataType);

                if (clazz == null) {
                    return;
                }

                if (target == null) {
                    return;
                }

                if (target.getOnClass() == null) {
                    target.setOnClass(clazz);
                }
                else if (MessageDialog
                        .openQuestion(
                                getSite().getShell(),
                                "Overwrite",
                                String.format("Do you wish to overwrite the qualification of the restriction on property?"))) {

                    target.setOnClass(clazz);
                }
                refresh();
            }
            else if (PropertyTransfer.getInstance().isSupportedType(event.currentDataType)) {
                Property property = (Property) PropertyTransfer.getInstance().nativeToJava(
                        event.currentDataType);
                if (property != null) {
                    // Create a new restriction
                    OntClass clazz = null;
                    if (event.item == null) {
                        clazz = getRootClass();
                    }
                    else {
                        TreeItem item = (TreeItem) event.item;
                        TreeData data = (TreeData) item.getData();
                        if (data.getData() instanceof OntClass) {
                            clazz = (OntClass) data.getData();
                        }
                    }
                    if (clazz != null) {
                        if (containsRestrictionOnProperty(restrictions, property)) {
                            MessageDialog.openInformation(getSite().getShell(),
                                    "Create Restriction",
                                    "The class already has a restriction on the given property.");
                        }
                        else {
                            createRestrictionOnProperty(property, clazz);
                        }
                    }
                }
            }
        }
    }

    private Restriction createRestrictionOnPropertyStatements(Property property) {
        Restriction restriction = model.createRestriction(property);
        restriction.addProperty(OWL.minCardinality, Integer.toString(0),
                XSDDatatype.XSDnonNegativeInteger);
        return restriction;
    }

    private void createRestrictionOnProperty(Property property, Resource subclass) {
        IModelProvider modelProvider = getModelProvider();
        ModelTransaction transaction = modelProvider.createTransaction("Creation of restriction");

        Restriction restriction = createRestrictionOnPropertyStatements(property);
        ResourceStatements.createResourceAsSubclassStatements(subclass, restriction);

        modelProvider.commitTransaction(transaction);
    }

    private static final class CardinalityAction extends Action {
        private final int min;
        private final int max;
        private final Viewer viewer;

        public CardinalityAction(Viewer viewer, String text, ImageDescriptor image, int min, int max) {
            setText(text);
            setImageDescriptor(image);
            this.viewer = viewer;
            this.min = min;
            this.max = max;
        }

        @Override
        public void run() {
            IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
            if (selection.isEmpty()) {
                return;
            }
            if (!(selection.getFirstElement() instanceof TreeData)) {
                return;
            }
            TreeData data = (TreeData) selection.getFirstElement();
            if (data.getData() instanceof ClassRestrictionItem) {
                ClassRestrictionItem item = (ClassRestrictionItem) data.getData();
                item.setCardinality(min, max);
            }
        }
    }

    private static final class CustomCardinalityAction extends Action {
        private final Viewer viewer;

        public CustomCardinalityAction(Viewer viewer) {
            setText("Cardinality...");
            this.viewer = viewer;
        }

        @Override
        public void run() {

            // get selected data
            IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
            if (selection.isEmpty()) {
                return;
            }
            if (!(selection.getFirstElement() instanceof TreeData)) {
                return;
            }
            TreeData data = (TreeData) selection.getFirstElement();
            ClassRestrictionItem item = (ClassRestrictionItem) data.getData();

            // create dialog
            Shell shell = EditorPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow()
                    .getShell();
            CardinalityInputDialog dialog = new CardinalityInputDialog(shell);
            dialog.setMin(item.getMin());
            dialog.setMax(item.getMax());

            // set min and max values
            if (dialog.open() == Window.OK) {
                int min = dialog.getMin();
                int max = dialog.getMax();
                item.setCardinality(min, max);
            }
        }
    }

    private static final class ClassViewerToolTipSupport extends ColumnViewerToolTipSupport {
        private IModelProvider modelProvider;

        protected ClassViewerToolTipSupport(ColumnViewer viewer, int style,
                IModelProvider modelProvider, boolean manualActivation) {
            super(viewer, style, manualActivation);
            this.modelProvider = modelProvider;
        }

        @Override
        protected Composite createViewerToolTipContentArea(Event event, ViewerCell cell,
                Composite parent) {
            if (cell != null) {
                Object element = cell.getElement();
                if (element instanceof Resource) {
                    return new ResourceToolTipContent(parent, modelProvider, (Resource) element,
                            SWT.NONE);
                }
                else if (element instanceof TreeData) {
                    TreeData treeData = (TreeData) element;
                    if (treeData.getData() instanceof Statement) {
                        Statement stmt = (Statement) treeData.getData();
                        if (stmt.getObject().isResource()) {
                            return new ResourceToolTipContent(parent, modelProvider, stmt
                                    .getObject().asResource(), SWT.NONE);
                        }
                    }
                }
            }
            return super.createViewerToolTipContentArea(event, cell, parent);
        }

        public static final void enableFor(final ColumnViewer viewer) {
            enableFor(viewer, null);
        }

        @SuppressWarnings("unused")
        public static final void enableFor(final ColumnViewer viewer, IModelProvider modelProvider) {
            new ClassViewerToolTipSupport(viewer, ToolTip.RECREATE, modelProvider, false);
        }
    }

    private LabelProvider rdfLabelProvider;
    private Composite treeComposite;
    private TreeViewer viewer;
    private Tree tree;

    private IBaseLabelProvider treeLabelProvider;
    private ILazyTreeContentProvider treeContentProvider;
    private DropTargetListener treeDropListener;
    private IDoubleClickListener treeDoubleClickListener;

    private OntModel model;
    private static ClassRestrictionsView singleton;
    private Composite filterComposite;
    private DropComposite dropComposite;

    public ClassRestrictionsView() {
        singleton = this;
        restrictions = new Vector<>();
    }

    @Override
    public void init(IViewSite site) throws PartInitException {
        super.init(site);
        CorePlugin.getDefault().getPreferenceStore().addPropertyChangeListener(this);
    }

    @Override
    public void dispose() {
        CorePlugin.getDefault().getPreferenceStore().removePropertyChangeListener(this);
        super.dispose();
    }

    @Override
    public void createPartControl(Composite parent) {
        super.createPartControl(parent);

        GridLayout layout = new GridLayout(1, false);
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        layout.verticalSpacing = 0;
        parent.setLayout(layout);

        treeLabelProvider = new ClassRestrictionsLabelProvider();
        treeContentProvider = new ClassRestrictionsContentProvider();
        treeDropListener = new ClassDropTargetListener();
        treeDoubleClickListener = new IDoubleClickListener() {

            @Override
            public void doubleClick(DoubleClickEvent event) {
                ISelection selection = event.getSelection();
                if (selection instanceof IStructuredSelection && !selection.isEmpty()) {
                    Object selected = ((IStructuredSelection) selection).getFirstElement();
                    Resource resource = null;
                    if (selected instanceof Resource) {
                        resource = (Resource) selected;
                    }
                    else if (selected instanceof TreeData) {
                        TreeData treeData = (TreeData) selected;
                        if (treeData.getData() instanceof Statement) {
                            Statement stmt = (Statement) treeData.getData();
                            if (stmt.getObject().isResource()) {
                                resource = stmt.getResource();
                            }
                        }
                    }
                    if (resource != null) {
                        CorePlugin.getDefault().openResource(resource);
                    }
                }
            }
        };

        setInitialized(true);
    }

    private OntClass getSelectedClass() {
        IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
        if (selection.isEmpty()) {
            return null;
        }
        if (!(selection.getFirstElement() instanceof TreeData)) {
            return null;
        }
        TreeData data = (TreeData) selection.getFirstElement();
        if (data.getData() instanceof OntClass) {
            return (OntClass) data.getData();
        }
        return null;
    }

    private ClassRestrictionItem getSelectedRestriction() {
        IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
        if (selection.isEmpty()) {
            return null;
        }
        if (!(selection.getFirstElement() instanceof TreeData)) {
            return null;
        }
        TreeData data = (TreeData) selection.getFirstElement();
        if (data.getData() instanceof ClassRestrictionItem) {
            return (ClassRestrictionItem) data.getData();
        }
        return null;
    }

    private void createContextMenu() {
        if (viewer != null) {
            MenuManager menuManager = new MenuManager();
            menuManager.setRemoveAllWhenShown(true);
            menuManager.addMenuListener(new IMenuListener() {
                @Override
                public void menuAboutToShow(IMenuManager manager) {
                    final OntClass selectedClass = getSelectedClass();
                    final ClassRestrictionItem selectedRestriction = getSelectedRestriction();

                    if (selectedRestriction != null) {
                        RDFNode value = selectedRestriction.getValue();
                        Resource clazz = selectedRestriction.getOnClass();
                        final Resource resource;

                        if (value != null && value.isResource()) {
                            resource = value.asResource();
                        }
                        else {
                            resource = clazz;
                        }

                        if (resource != null) {
                            manager.add(new Action("Open") {
                                @Override
                                public void run() {
                                    CorePlugin.getDefault().openResource(resource);
                                }
                            });
                            manager.add(new Separator());
                        }

                        manager.add(new Action("Edit...") {
                            @Override
                            public void run() {
                                List<Restriction> relatedRestrictions = Lists.newArrayList();
                                relatedRestrictions.addAll(selectedRestriction.getRestrictions());
                                EditRestrictionSuite.editRestriction(getModelProvider(),
                                        getViewSite().getShell(), relatedRestrictions,
                                        getRootClass().asResource());
                            }
                        });
                        manager.add(new Separator());
                        manager.add(new CardinalityAction(viewer, "None", IMAGE_CARDINALITY_NONE,
                                0, 0));
                        manager.add(new CardinalityAction(viewer, "Optional",
                                IMAGE_CARDINALITY_OPTIONAL, 0, 1));
                        manager.add(new CardinalityAction(viewer, "One", IMAGE_CARDINALITY_ONE, 1,
                                1));
                        manager.add(new CardinalityAction(viewer, "Some", IMAGE_CARDINALITY_SOME,
                                1, -1));
                        manager.add(new CardinalityAction(viewer, "Unbounded",
                                IMAGE_CARDINALITY_UNBOUNDED, 0, -1));
                        manager.add(new Separator());
                        manager.add(new CustomCardinalityAction(viewer));
                        manager.add(new Separator());
                        if (!selectedRestriction.isInherited()) {
                            if (selectedRestriction.getOnClass() != null) {
                                manager.add(new Action("Clear Qualification") {
                                    @Override
                                    public void run() {
                                        selectedRestriction.setOnClass(null);
                                    }
                                });
                            }
                            manager.add(new Action("Delete", IMAGE_DELETE) {
                                @Override
                                public void run() {
                                    selectedRestriction.delete();
                                }
                            });
                        }
                    }
                    else if (selectedClass != null) {
                        if (addRestrictionImage == null) {
                            Image baseImage = CorePlugin.getDefault().getImage(
                                    CorePluginImages.IMG_OWL_RESTRICTION);
                            OverlayImageIcon icon = new OverlayImageIcon(baseImage, CorePlugin
                                    .getDefault());
                            icon.addImageDecoration(CorePluginImages.IMG_OVERLAY_ADD,
                                    OverlayImageIcon.TOP_RIGHT);
                            addRestrictionImage = ImageDescriptor.createFromImage(icon
                                    .createImage());
                        }
                        manager.add(new Action("Add...", addRestrictionImage) {
                            @Override
                            public void run() {
                                CreateRestrictionWizard wizard = new CreateRestrictionWizard(
                                        "New Restriction", getModelProvider(), null);

                                wizard.setAnonymousAllowed(true);
                                wizard.setAllowOpenEditorOnFinish(false);
                                wizard.setLabelProperty(RDFS.label);
                                wizard.setOnProperty(null);
                                wizard.setAnonymous(true);
                                wizard.setSuppressNotify(true);
                                wizard.setValidator(null);

                                Shell shell = getSite().getShell();
                                WizardDialog dialog = new WizardDialog(shell, wizard);
                                dialog.create();
                                dialog.showPage(wizard
                                        .getPage(CreateRestrictionWizard.RESTRICTIONS_PAGE_NAME));

                                IModelProvider modelProvider = getModelProvider();
                                if (modelProvider != null) {
                                    String transactionDescription = "Due to creation of a new restriction";
                                    ModelTransaction transaction = modelProvider
                                            .createTransaction(transactionDescription);
                                    if (dialog.open() != Window.OK) {
                                        modelProvider.abortTransaction(transaction);
                                    }
                                    else {
                                        // Add subClassOf relations of the
                                        // current
                                        // resource to the new restrictions
                                        ResourceStatements.createResourceAsSubclassStatements(
                                                selectedClass, wizard.getRestrictions());

                                        modelProvider.commitTransaction(transaction);
                                    }
                                }
                            }
                        });
                    }
                }
            });

            Menu menu = menuManager.createContextMenu(viewer.getControl());
            viewer.getControl().setMenu(menu);
            getSite().registerContextMenu(menuManager, viewer);
        }
    }

    protected void createClassRestrictionTree() {
        if (!Widgets.isNullOrDisposed(dropComposite)) {
            dropComposite.dispose();
        }
        if (!Widgets.isNullOrDisposed(treeComposite)) {
            treeComposite.dispose();
        }
        // else {
        treeComposite = new Composite(getParent(), SWT.NONE);
        GridLayout layout = new GridLayout(1, false);
        layout.marginHeight = 3;
        layout.marginWidth = 0;
        treeComposite.setLayout(layout);
        treeComposite.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

        viewer = new TreeViewer(treeComposite, SWT.VIRTUAL | SWT.MULTI);
        viewer.setUseHashlookup(true);
        viewer.setLabelProvider(treeLabelProvider);
        viewer.setContentProvider(treeContentProvider);
        viewer.addDoubleClickListener(treeDoubleClickListener);
        int operations = DND.DROP_COPY | DND.DROP_MOVE;
        viewer.addDropSupport(operations, new Transfer[] { OntClassTransfer.getInstance(),
                PropertyTransfer.getInstance() }, treeDropListener);
        ClassViewerToolTipSupport.enableFor(viewer, getModelProvider());
        viewer.addDoubleClickListener(new IDoubleClickListener() {

            @Override
            public void doubleClick(DoubleClickEvent event) {
                if (event.getSelection() instanceof IStructuredSelection) {
                    IStructuredSelection selection = (IStructuredSelection) event.getSelection();
                    if (selection.size() > 0 && selection.getFirstElement() instanceof TreeData) {
                        TreeData selectedTreeData = (TreeData) selection.getFirstElement();
                        if (selectedTreeData.getData() instanceof ClassRestrictionItem) {
                            ClassRestrictionItem selectedElement = (ClassRestrictionItem) selectedTreeData
                                    .getData();

                            List<Restriction> relatedRestrictions = Lists.newArrayList();
                            relatedRestrictions.addAll(selectedElement.getRestrictions());
                            EditRestrictionSuite.editRestriction(getModelProvider(), getViewSite()
                                    .getShell(), relatedRestrictions, getRootClass().asResource());
                        }
                    }
                }
            }
        });

        createContextMenu();

        GridData layoutData = new GridData(GridData.FILL, GridData.FILL, true, true);
        tree = viewer.getTree();
        tree.setLayoutData(layoutData);
        // }
        // final TreeEditor editor = new TreeEditor(tree);
        // // The editor must have the same size as the cell and must
        // // not be any smaller than 50 pixels.
        // editor.horizontalAlignment = SWT.LEFT;
        // editor.grabHorizontal = true;
        // editor.minimumWidth = 50;
        //
        // tree.addSelectionListener(new SelectionAdapter() {
        // public void widgetSelected(SelectionEvent e) {
        // // Clean up any previous editor control
        // Control oldEditor = editor.getEditor();
        // if (oldEditor != null)
        // oldEditor.dispose();
        //
        // // Identify the selected row
        // TreeItem item = (TreeItem) e.item;
        // if (item == null)
        // return;
        //
        // // The control that will be the editor must be a child of the
        // // Tree
        // Text newEditor = new Text(tree, SWT.NONE);
        // newEditor.setText(item.getText());
        // newEditor.addModifyListener(new ModifyListener() {
        // public void modifyText(ModifyEvent e) {
        // Text text = (Text) editor.getEditor();
        // editor.getItem().setText(text.getText());
        // }
        // });
        // newEditor.selectAll();
        // newEditor.setFocus();
        // editor.setEditor(newEditor, item);
        // }
        // });
    }

    protected void createPropertiesFilter() {
        if (!Widgets.isNullOrDisposed(filterComposite)) {
            filterComposite.dispose();
        }

        filterComposite = new Composite(getParent(), SWT.NONE);
        GridLayout layout = new GridLayout(4, false);
        layout.marginHeight = 3;
        layout.marginWidth = 5;
        layout.marginBottom = 2;
        layout.marginTop = 4;
        layout.horizontalSpacing = 2;
        filterComposite.setLayout(layout);
        filterComposite.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));

        DropTarget dropTarget = new DropTarget(filterComposite, DND.DROP_MOVE | DND.DROP_COPY);
        dropTarget.setTransfer(new Transfer[] { PropertyArrayListTransfer.getInstance(),
                PropertyTransfer.getInstance() });
        dropTarget.addDropListener(new DropTargetAdapter() {

            @Override
            public void dragEnter(DropTargetEvent event) {
                for (int i = 0; i < event.dataTypes.length; i++) {
                    if (PropertyArrayListTransfer.getInstance().isSupportedType(event.dataTypes[i])) {
                        event.currentDataType = event.dataTypes[i];
                        break;
                    }
                }
            }

            @Override
            public void drop(DropTargetEvent event) {
                List<Resource> properties = getFilterProperties();

                if (PropertyArrayListTransfer.getInstance().isSupportedType(event.currentDataType)) {
                    PropertyArrayList list = (PropertyArrayList) PropertyArrayListTransfer
                            .getInstance().nativeToJava(event.currentDataType);
                    if (list != null) {
                        if (event.detail != DND.DROP_COPY) {
                            properties = Lists.newArrayList();
                        }
                        for (Property property : list) {
                            properties.add(property);
                        }
                        setFilterProperties(properties);
                        refresh();
                    }
                }
                else if (PropertyTransfer.getInstance().isSupportedType(event.currentDataType)) {
                    Property property = (Property) PropertyTransfer.getInstance().nativeToJava(
                            event.currentDataType);
                    if (property != null) {
                        if (event.detail == DND.DROP_COPY) {
                            properties.add(property);
                        }
                        else {
                            properties = Lists.newArrayList(property.asResource());
                        }
                        setFilterProperties(properties);
                        refresh();
                    }
                }
            }
        });

        Label icon = new Label(filterComposite, SWT.NONE);
        icon.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
        if (getFilterProperties().size() == 1) {
            icon.setImage(CorePlugin.getDefault().getImage(CorePluginImages.IMG_RDF_PROPERTY));
        }
        else if (getFilterProperties().size() > 1) {
            icon.setImage(EditorPlugin.getDefault().getImage(EditorPluginImages.IMG_PROPERTIES));
        }
        else {
            icon.setImage(EditorPlugin.getDefault()
                    .getImage(EditorPluginImages.IMG_FILTER_PROPERTY));
        }
        Label label = new Label(filterComposite, SWT.NONE);

        GridData layoutData = new GridData(GridData.FILL, GridData.CENTER, true, false);
        layoutData.horizontalIndent = 4;
        label.setLayoutData(layoutData);
        if (getFilterProperties().isEmpty()) {
            label.setText("No property filter selected");
        }
        else if (getFilterProperties().size() == 1) {
            label.setText(String.format("Filtering relations on property '%s'",
                    rdfLabelProvider.getText(getFilterProperties().get(0))));
        }
        else {
            label.setText(String.format("Filtering relations on %s properties",
                    getFilterProperties().size()));
        }

        Link link = new Link(filterComposite, SWT.NONE);
        link.setText("<a>Edit</a>");
        link.setLayoutData(new GridData(GridData.END, GridData.CENTER, true, false));
        link.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                ResourceSelectionDialog dialog = new ResourceSelectionDialog(getSite().getShell(),
                        "Properties", "Select the properties on which you would like to filter.",
                        ResourceSelectionDialog.CHECKBOXES);

                dialog.clearAll();
                dialog.setModel(model);
                dialog.setHierarchicalProperties(Lists.newArrayList(RDFS.subClassOf, RDF.type,
                        RDFS.subPropertyOf));
                dialog.setRootResources(Lists.newArrayList(RDF.Property));
                dialog.setAllowedResourceTypes(new Resource[] { RDF.Property });
                dialog.setMultiSelectAllowed(true);
                dialog.setSelectedResources(getFilterProperties());

                if (dialog.open() == Window.OK) {
                    List<Resource> properties = Lists.newArrayList();
                    for (Resource property : dialog.getSelectedResources()) {
                        System.out.println("" + property.getURI());
                        properties.add(property.as(Property.class));
                    }
                    setFilterProperties(properties);
                    refresh();
                }
            }
        });

        if (!getFilterProperties().isEmpty()) {
            Link clearLink = new Link(filterComposite, SWT.NONE);
            clearLink.setText("<a>Clear</a>");
            layoutData = new GridData(GridData.END, GridData.CENTER, false, false);
            layoutData.horizontalIndent = 4;
            clearLink.setLayoutData(layoutData);
            clearLink.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    setFilterProperties(null);
                    refresh();
                }
            });
        }
    }

    @Override
    public void setFocus() {
        if (!Widgets.isNullOrDisposed(tree)) {
            tree.setFocus();
        }
    }

    @Override
    public void modelActivated(ModelActivatedEvent event) {
        refreshWithChangedModelInformation();
    }

    @Override
    public void modelChanged(ModelChangedEvent event) {
        refreshWithChangedModelInformation();
    }

    @Override
    public void subModelAdded(SubModelAddedEvent event) {
        refreshWithChangedModelInformation();
    }

    @Override
    public void subModelRemoved(SubModelRemovedEvent event) {
        refreshWithChangedModelInformation();
    }

    @Override
    public void namespacePrefixChanged(NamespacePrefixChangedEvent event) {
        refreshWithChangedModelInformation();
    }

    private void refreshWithChangedModelInformation() {
        rdfLabelProvider = getLabelProvider();
        model = getOntModel();

        Commands.refreshElements(ToggleShowPropertiesFilterHandler.ID);

        clear();
        if (hasModelProvider()) {
            refresh();
        }
    }

    private void clear() {
        clearClassRestrictionTree();
        if (!Widgets.isNullOrDisposed(filterComposite)) {
            filterComposite.dispose();
        }
        if (!Widgets.isNullOrDisposed(dropComposite)) {
            dropComposite.dispose();
        }
    }

    private void refresh() {
        boolean isEmpty = (getRootClass() == null);
        if (!isEmpty) {
            if (getShowFilter()) {
                createPropertiesFilter();
            }
            createClassRestrictionTree();
            createDropRegion();
        }
        else {
            clearClassRestrictionTree();
            createDropRegion();
        }
        if (!isEmpty) {
            refreshViewer();
        }
        layoutParent(true, false);
    }

    private void clearClassRestrictionTree() {
        if (!Widgets.isNullOrDisposed(treeComposite)) {
            treeComposite.dispose();
        }
    }

    private void createDropRegion() {
        Widgets.disposeIfExists(dropComposite);
        if (Widgets.isNullOrDisposed(getParent())) {
            return;
        }

        boolean small = !Widgets.isNullOrDisposed(tree);
        dropComposite = new DropComposite(getParent(), "class", small);
        dropComposite.setDefaultGridLayoutData(small);

        DropTarget dropTarget = new DropTarget(dropComposite, DND.DROP_MOVE | DND.DROP_COPY);
        dropTarget.setTransfer(new Transfer[] { OntClassTransfer.getInstance() });
        dropTarget.addDropListener(new DropTargetAdapter() {

            @Override
            public void drop(DropTargetEvent event) {
                if (OntClassTransfer.getInstance().isSupportedType(event.currentDataType)) {
                    OntClass clazz = (OntClass) OntClassTransfer.getInstance().nativeToJava(
                            event.currentDataType);
                    if (clazz == null) {
                        return;
                    }
                    // Retrieve class from model
                    try {
                        OntModel infModel = getOntModel();
                        clazz = infModel.getOntClass(clazz.getURI());
                    }
                    catch (Exception ex) {
                        logger.error("Error during retrieval of inferred class", ex);
                    }
                    setRootClass(clazz);
                    refresh();
                }
            }
        });
    }

    private void refreshViewer() {
        boolean isEmpty = (getRootClass() == null);
        if (model != null && !isEmpty) {
            viewer.setInput(model);
            tree.setItemCount(1);
            viewer.expandToLevel(2);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        String property = event.getProperty();
        if (property.equals(LabelsPreference.PREFERENCE_RESOURCE_LABEL_RENDERING)
                || property.equals(LabelsPreference.PREFERENCE_ALWAYS_SHOW_ONTOLOGY_URI)
                || property.equals(LanguagesPreference.PREFERENCE_DISPLAY_LANGUAGES)) {
            refreshViewer();
        }
    }

    public static class ClearClassRestrictionsHandler extends AbstractHandler {
        public static final String ID = "com.semmtech.plugin.semmweb.editor.commands.clearClassRestrictions";

        @Override
        public Object execute(ExecutionEvent event) throws ExecutionException {
            if (singleton != null) {
                singleton.setRootClass(null);
                singleton.clear();
                singleton.refresh();
            }
            return null;
        }
    }

    public static class RemoveSelectedRootHandler extends AbstractHandler {
        public static final String ID = "com.semmtech.plugin.semmweb.editor.commands.removeSelectedRoot";

        @Override
        public Object execute(ExecutionEvent event) throws ExecutionException {
            if (singleton != null) {
                if (singleton.viewer == null) {
                    return null;
                }
                IStructuredSelection selection = (IStructuredSelection) singleton.viewer
                        .getSelection();
                if (selection == null || selection.isEmpty()) {
                    return null;
                }
                singleton.setRootClass(null);
                singleton.clear();
            }
            return null;
        }

    }

    public static class ToggleShowPropertiesFilterHandler extends AbstractHandler implements
            IElementUpdater {
        public static final String ID = "com.semmtech.plugin.semmweb.editor.commands.classRestrictions.toggleShowPropertiesFilter";

        @Override
        public Object execute(ExecutionEvent event) throws ExecutionException {
            if (singleton != null) {
                singleton.setShowFilter(!singleton.getShowFilter());
                singleton.clear();
                singleton.refresh();
            }
            return null;
        }

        @Override
        public void updateElement(UIElement element, @SuppressWarnings("rawtypes") Map parameters) {
            if (singleton != null) {
                element.setChecked(singleton.getShowFilter());
            }
        }
    }

    private void setRootClass(OntClass clazz) {
        setStateParameter(PARAM_ROOT_CLASS, clazz);
    }

    private OntClass getRootClass() {
        return (OntClass) getStateParameter(PARAM_ROOT_CLASS);
    }

    private void setShowFilter(boolean showFilter) {
        setStateParameter(PARAM_SHOW_FILTER, showFilter);
    }

    private boolean getShowFilter() {
        Boolean value = (Boolean) getStateParameter(PARAM_SHOW_FILTER);
        return (value == null) ? false : value.booleanValue();
    }

    private void setFilterProperties(List<Resource> properties) {
        setStateParameter(PARAM_FILTER_PROPERTIES, properties);
    }

    private List<Resource> getFilterProperties() {
        @SuppressWarnings("unchecked")
        List<Resource> value = (List<Resource>) getStateParameter(PARAM_FILTER_PROPERTIES);
        if (value == null) {
            value = Lists.newArrayList();
        }
        return value;
    }

    @Override
    protected void cleanup() {
        restrictions.clear();
    }
}
