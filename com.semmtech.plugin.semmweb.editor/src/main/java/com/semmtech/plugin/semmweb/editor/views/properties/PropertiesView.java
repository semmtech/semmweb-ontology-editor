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

package com.semmtech.plugin.semmweb.editor.views.properties;


import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.commands.IElementUpdater;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.handlers.RadioState;
import org.eclipse.ui.menus.UIElement;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.shared.ClosedException;
import com.hp.hpl.jena.vocabulary.OWL;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.CorePluginImages;
import com.semmtech.plugin.semmweb.core.commands.Commands;
import com.semmtech.plugin.semmweb.core.dnd.DndUtils;
import com.semmtech.plugin.semmweb.core.dnd.PropertyArrayListTransfer;
import com.semmtech.plugin.semmweb.core.dnd.PropertyTransfer;
import com.semmtech.plugin.semmweb.core.dnd.ResourceArrayListTransfer;
import com.semmtech.plugin.semmweb.core.dnd.ResourceTransfer;
import com.semmtech.plugin.semmweb.core.forms.editor.OntologyFormEditor;
import com.semmtech.plugin.semmweb.core.model.IModelProvider;
import com.semmtech.plugin.semmweb.core.model.events.ModelActivatedEvent;
import com.semmtech.plugin.semmweb.core.model.events.ModelChangedEvent;
import com.semmtech.plugin.semmweb.core.model.events.NamespacePrefixChangedEvent;
import com.semmtech.plugin.semmweb.core.model.events.SubModelAddedEvent;
import com.semmtech.plugin.semmweb.core.model.events.SubModelRemovedEvent;
import com.semmtech.plugin.semmweb.core.preferences.LabelsPreference;
import com.semmtech.plugin.semmweb.core.preferences.LanguagesPreference;
import com.semmtech.plugin.semmweb.core.viewers.ModelNodeLabelProvider;
import com.semmtech.plugin.semmweb.core.viewers.ResourceViewerToolTipSupport;
import com.semmtech.plugin.semmweb.core.views.AbstractModelListenerView;
import com.semmtech.plugin.semmweb.core.widgets.trees.NamespaceTreeData;
import com.semmtech.plugin.semmweb.core.widgets.trees.PropertyTreeData;
import com.semmtech.plugin.semmweb.core.widgets.trees.TreeData;
import com.semmtech.plugin.semmweb.editor.EditorPlugin;
import com.semmtech.plugin.semmweb.editor.EditorPluginImages;
import com.semmtech.semantics.util.JenaUtil;
import com.semmtech.ui.plugin.DelayedRunnableExecution;
import com.semmtech.ui.plugin.jobs.JobWithMonitor;
import com.semmtech.ui.plugin.util.FontUtil;
import com.semmtech.ui.plugin.util.Selections;
import com.semmtech.ui.plugin.viewers.LazyTreeContentProvider;
import com.semmtech.ui.plugin.viewers.LazyTreeViewer;
import com.semmtech.ui.plugin.viewers.ViewerSelectionProvider;
import com.semmtech.ui.plugin.widgets.Widgets;


/**
 * 
 * @author Sander Stolk
 */
public class PropertiesView extends AbstractModelListenerView implements IPropertyChangeListener {
    public static final String ID = "com.semmtech.plugin.semmweb.editor.views.properties";
    private static final Logger logger = Logger.getLogger(PropertiesView.class);

    public final static String PARAM_VIEWER_EXPANDED_ELEMENTS = "expanded elements";
    public final static String PARAM_PROPERTIES_VIEW_STATE = "hierachical";
    public static final String PARAM_SHOW_INVERSE_PROPERTIES = "showInverseProperties";

    private static PropertiesView singleton;

    /**
     * This class is used by the properties tree viewer as IBaseLabelProvider.
     * 
     * @author Mike Henrichs
     */
    private class PropertiesLabelProvider extends StyledCellLabelProvider {
        private final Styler builtinStyler = new Styler() {
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
            int columnIndex = cell.getColumnIndex();
            Object element = cell.getElement();
            StyledString styledText = new StyledString();
            String text = getColumnText(element, columnIndex);
            if (text != null && text.length() > 0) {
                styledText.append(text);

                if (filter.length() > 0) {
                    int startIndex = text.toLowerCase().indexOf(filter.toLowerCase());
                    while (startIndex > -1) {
                        int length = filter.length();
                        styledText.setStyle(startIndex, length, builtinStyler);
                        startIndex = text.toLowerCase().indexOf(filter.toLowerCase(),
                                startIndex + 1);
                    }
                }
            }

            if (text != null
                    && !text.isEmpty()
                    && CorePlugin.getDefault().getActiveModelProvider() != null
                    && text.equals(CorePlugin.getDefault().getActiveModelProvider().getModelTitle())) {
                styledText.setStyle(0, styledText.getString().length(), localStyler);
            }

            Image columnImage = getColumnImage(element, columnIndex);
            if (columnImage != null) {
                cell.setImage(columnImage);
            }
            cell.setText(styledText.getString());
            cell.setStyleRanges(styledText.getStyleRanges());
            super.update(cell);
        }

        public Image getColumnImage(Object element, int columnIndex) {
            if (columnIndex == 0) {
                if (element instanceof NamespaceTreeData) {
                    return CorePlugin.getDefault().getImage(CorePluginImages.IMG_OWL_ONTOLOGY);
                }
                else if ((element instanceof PropertyTreeData)) {
                    return CorePlugin.getDefault().getImage(CorePluginImages.IMG_RDF_PROPERTY);
                }
                return rdfNodeLabelProvider.getImage(element);
            }
            else if (columnIndex == 1) {
                if ((element instanceof PropertyTreeData)) {
                    if (viewModel.getInversePropertyCount((Property) element) > 0) {
                        return EditorPlugin.getDefault().getImage(
                                EditorPluginImages.IMG_PROPERTY_INVERSE);
                    }
                }
            }
            return null;
        }

        public String getColumnText(Object element, int columnIndex) {
            if (columnIndex == 0) {
                if (element instanceof NamespaceTreeData) {
                    return rdfNodeLabelProvider.getNamespaceText(element);
                }
                else if (element instanceof PropertyTreeData) {
                    return rdfNodeLabelProvider.getText(element);
                }
            }
            else if (columnIndex == 1) {
                if (element instanceof PropertyTreeData) {
                    Property inverseProperty = viewModel.getInverseProperty((Property) element, 0);
                    if (inverseProperty != null) {
                        return rdfNodeLabelProvider.getText(inverseProperty);
                    }
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
    }

    private class PropertiesTreeContentProvider extends LazyTreeContentProvider {

        public int getItemCount() {
            String state = getPresentationState();
            if (state.equals(PropertiesViewPresentationHandler.STATE_BY_NAMESPACE)) {
                return viewModel.getNamespaceCount();
            }
            return viewModel.getRootPropertyCount();
        }

        /**
         * This method is either called when setInput is called, in which case
         * the provided input is given as parent and depending on the number of
         * items set (using setItemCount) indexes 0 through the count -1 are
         * given. Goal of this method is to replace the child with given index
         * within given parent with the actual object. Also provide the number
         * of children the new item has.
         */
        @Override
        public void updateElement(Object parent, int index) {
            if ((currentModel == null) || (viewModel == null)) {
                return;
            }
            String state = getPresentationState();
            if (parent instanceof Model) {
                if (state.equals(PropertiesViewPresentationHandler.STATE_BY_NAMESPACE)) {
                    Resource namespace = viewModel.getNamespace(index);
                    NamespaceTreeData element = new NamespaceTreeData(namespace);
                    viewer.replace(parent, index, element);
                    viewer.setChildCount(element,
                            viewModel.getNamespacePropertyCount(namespace, filter));
                }
                else {
                    Property property = viewModel.getRootProperty(index);
                    PropertyTreeData element = new PropertyTreeData(property);
                    viewer.replace(parent, index, element);
                    viewer.setChildCount(element, viewModel.getChildPropertyCount(property));
                }
            }
            else if (parent instanceof NamespaceTreeData) {
                Resource namespace = ((NamespaceTreeData) parent).asResource();
                Property property = viewModel.getNamespaceProperty(namespace, filter, index);
                PropertyTreeData element = new PropertyTreeData(property);
                element.setParent((NamespaceTreeData) parent);
                viewer.replace(parent, index, element);
                viewer.setChildCount(element, 0);
            }
            else if (parent instanceof PropertyTreeData) {
                if (state.equals(PropertiesViewPresentationHandler.STATE_HIERARCHICAL)) {
                    Property superProperty = (Property) parent;
                    Property subProperty = viewModel.getChildProperty(superProperty, index);
                    PropertyTreeData element = new PropertyTreeData(subProperty);
                    element.setParent((PropertyTreeData) parent);
                    viewer.replace(parent, index, element);
                    viewer.setChildCount(subProperty, viewModel.getChildPropertyCount(subProperty));
                }
            }
        }

        /**
         * Calculates the index in an array of TreeItems at which to insert the
         * given element (adhering to ascending alphabetical order of the text
         * label provided by the viewModel).
         */
        public int getInsertionIndex(Object element, TreeItem[] treeItems) {
            if ((element == null) || (viewModel == null) || !(element instanceof Resource)) {
                return -1;
            }
            if ((treeItems == null) || (treeItems.length == 0)) {
                return 0;
            }

            String elementText = viewModel.getText((Resource) element);
            if (elementText != null) {

                for (int i = 0; i < treeItems.length; i++) {
                    Object treeItemData = treeItems[i].getData();
                    if (treeItemData == null) {
                        return i;
                    }
                    if (treeItemData instanceof Resource) {
                        String treeItemText = viewModel.getText((Resource) treeItemData);
                        if (treeItemText != null) {
                            int compare = elementText.compareToIgnoreCase(treeItemText);
                            if (compare < 0) {
                                return i;
                            }
                            if (compare == 0) {
                                return (treeItemData.equals(element)) ? -1 : i;
                            }
                        }
                    }
                }

            }

            return treeItems.length;
        }

        public void addNamespace(Resource namespace) {
            if (Widgets.isNullOrDisposedViewer(viewer) || (namespace == null)) {
                return;
            }

            NamespaceTreeData element = new NamespaceTreeData(namespace);
            int insertionIndex = getInsertionIndex(element, viewer.getTree().getItems());
            if (insertionIndex >= 0) {
                viewer.insert(currentModel, element, insertionIndex);
                viewer.setChildCount(element,
                        viewModel.getNamespacePropertyCount(namespace, filter));
            }
        }

        public void removeNamespace(Resource namespace) {
            if (Widgets.isNullOrDisposedViewer(viewer) || (namespace == null)) {
                return;
            }

            viewer.remove(currentModel, new Object[] { new NamespaceTreeData(namespace) });
        }

        public void addProperty(Property property, Resource namespace) {
            if (Widgets.isNullOrDisposedViewer(viewer) || (property == null) || (namespace == null)) {
                return;
            }
            NamespaceTreeData namespaceData = new NamespaceTreeData(namespace);
            PropertyTreeData propertyData = new PropertyTreeData(property);
            propertyData.setParent(namespaceData);
            for (TreeItem parentItem : findTreeItems(namespace)) {
                if (parentItem.getData() instanceof NamespaceTreeData) {
                    if (!hasCachedChildren(parentItem)) {
                        parentItem.setItemCount(viewModel.getNamespacePropertyCount(namespace,
                                filter));
                    }
                    else {
                        int insertionIndex = getInsertionIndex(propertyData, parentItem.getItems());
                        if (insertionIndex >= 0) {
                            viewer.insert(namespaceData, propertyData, insertionIndex);
                        }
                    }
                    return;
                }

            }
        }

        // FIXME: My suggestion is to move this to a ResourceTreeViewer class
        private boolean hasCachedChildren(TreeItem treeItem) {
            if (treeItem.getItemCount() == 0) {
                return false;
            }
            if ((treeItem.getItem(0) == null) || (treeItem.getItem(0).getData() == null)) {
                return false;
            }
            return true;
        }

        public void removeProperty(Property property, Resource namespace) {
            if (Widgets.isNullOrDisposedViewer(viewer) || (property == null) || (namespace == null)) {
                return;
            }
            NamespaceTreeData namespaceData = new NamespaceTreeData(namespace);
            PropertyTreeData propertyData = new PropertyTreeData(property);
            propertyData.setParent(namespaceData);
            for (TreeItem parentItem : findTreeItems(namespace)) {
                if (parentItem.getData() instanceof NamespaceTreeData) {
                    if (parentItem.getItemCount() > 0) {
                        if (!hasCachedChildren(parentItem)) {
                            parentItem.setItemCount(viewModel.getNamespacePropertyCount(namespace,
                                    filter));
                        }
                        else {
                            viewer.remove(namespaceData, new Object[] { propertyData });
                        }
                    }
                    return;
                }
            }
        }

        public void addRoot(Property root) {
            if (Widgets.isNullOrDisposedViewer(viewer) || (root == null)) {
                return;
            }

            PropertyTreeData element = new PropertyTreeData(root);
            int insertionIndex = getInsertionIndex(element, viewer.getTree().getItems());
            if (insertionIndex >= 0) {
                viewer.insert(currentModel, element, insertionIndex);
                viewer.setChildCount(element, viewModel.getChildPropertyCount(root));
            }
        }

        public void removeRoot(Property root) {
            if (Widgets.isNullOrDisposedViewer(viewer) || (root == null)) {
                return;
            }

            viewer.remove(currentModel, new Object[] { new PropertyTreeData(root) });
        }

        public void addChild(Property child, Property parent) {
            if (child == null) {
                return;
            }

            for (TreeItem treeItem : findTreeItems(parent)) {
                if (!hasCachedChildren(treeItem)) {
                    treeItem.setItemCount(viewModel.getChildPropertyCount(parent));
                }
                else {
                    PropertyTreeData childData = new PropertyTreeData(child);
                    childData.setParent((TreeData) treeItem.getData());
                    int insertionIndex = getInsertionIndex(childData, treeItem.getItems());
                    if (insertionIndex >= 0) {
                        viewer.insert(treeItem.getData(), childData, insertionIndex);
                        viewer.setChildCount(childData, viewModel.getChildPropertyCount(child));
                    }
                }
            }
        }

        public void removeChild(Property child, Property parent) {
            if (child == null) {
                return;
            }

            for (TreeItem treeItem : findTreeItems(parent)) {
                if (treeItem.getItemCount() > 0) {
                    if (!hasCachedChildren(treeItem)) {
                        treeItem.setItemCount(viewModel.getChildPropertyCount(parent));
                    }
                    else {
                        PropertyTreeData childData = new PropertyTreeData(child);
                        childData.setParent((TreeData) treeItem.getData());
                        viewer.remove(childData);
                    }
                }
            }
        }

        public void updateResources(Collection<? extends Resource> resources) {
            for (Resource resource : resources) {
                for (TreeItem treeItem : findTreeItems(resource)) {
                    Object treeItemData = treeItem.getData();
                    viewer.update(treeItemData, null);
                }
            }
        }

        /**
         * Returns a list of TreeItems that contain the resource.
         */
        private List<TreeItem> findTreeItems(Resource resource) {
            List<TreeItem> result = Lists.newArrayList();

            if (!Widgets.isNullOrDisposedViewer(viewer) && (resource != null)) {
                result.addAll(findTreeItems(resource, viewer.getTree().getItems()));
            }

            return result;
        }

        private List<TreeItem> findTreeItems(Resource resource, TreeItem[] treeItems) {
            List<TreeItem> result = Lists.newArrayList();

            if (!Widgets.isNullOrDisposedViewer(viewer) && (resource != null)
                    && (treeItems != null)) {
                for (TreeItem treeItem : treeItems) {
                    Object treeItemData = treeItem.getData();
                    if (treeItemData != null) {
                        if ((treeItemData instanceof Resource) && resource.equals(treeItemData)) {
                            result.add(treeItem);
                        }
                        result.addAll(findTreeItems(resource, treeItem.getItems()));
                    }
                }
            }

            return result;
        }
    }

    /**
     * Inner class
     * 
     * @author Mike Henrichs
     * 
     */
    private class PropertiesDragSourceListener extends DragSourceAdapter {
        private LazyTreeViewer viewer;

        public PropertiesDragSourceListener(LazyTreeViewer viewer) {
            this.viewer = viewer;
        }

        @Override
        public void dragSetData(DragSourceEvent event) {
            DndUtils.setDragSetData(event, getOntModel(), viewer, null);
        }
    }

    /**
     * Inner class for dealing with storing expanding and collapsing states of
     * elements, which are stored based on the current modelUri.
     * 
     * @author Mike Henrichs
     * 
     */
    private class PropertiesTreeViewerListener implements ITreeViewerListener {
        @Override
        public void treeCollapsed(TreeExpansionEvent event) {
            if (!restoringExpandedElements && !settingViewerInput) {
                List<Object> elements = Lists.newArrayList(event.getTreeViewer()
                        .getExpandedElements());
                elements.remove(event.getElement());
                setStateParameter(PARAM_VIEWER_EXPANDED_ELEMENTS, elements.toArray());
            }
        }

        @Override
        public void treeExpanded(TreeExpansionEvent event) {
            if (!restoringExpandedElements && !settingViewerInput) {
                List<Object> elements = Lists.newArrayList(event.getTreeViewer()
                        .getExpandedElements());
                elements.add(event.getElement());
                setStateParameter(PARAM_VIEWER_EXPANDED_ELEMENTS, elements.toArray());
            }
        }
    }

    private OntModel currentModel;
    private ModelNodeLabelProvider rdfNodeLabelProvider;

    private PropertiesViewModel viewModel;

    private Object mutex = new Object();

    private boolean restoringExpandedElements = false;
    private boolean settingViewerInput = false;

    private LazyTreeViewer viewer;
    private Tree tree;
    private CreateModelViewJob refreshJob;
    private CreateModelViewJob modelChangedJob;
    private boolean modelChangedJobQueued;

    private String filter = "";
    private boolean resettingFilter;
    private final DelayedRunnableExecution filterExecution;

    private Text filterText;
    private Label commandLabel;
    private Composite container;
    private Composite searchComposite;

    // TODO: Re-factor, create separate (private) classes
    private PropertiesTreeContentProvider contentProvider;

    // TODO: dispose?
    private Font boldFont;

    private ViewerSelectionProvider selectionProvider;

    public PropertiesView() {
        singleton = this;
        filterExecution = new DelayedRunnableExecution(new Runnable() {
            @Override
            public void run() {
                applyFilter();
            }
        });
        selectionProvider = new ViewerSelectionProvider();
    }

    private void applyFilter() {
        if (resettingFilter || Widgets.isNullOrDisposed(filterText)
                || Widgets.isNullOrDisposed(commandLabel) || (viewer == null)
                || Widgets.isNullOrDisposed(viewer.getTree())) {
            return;
        }

        if (filterText.getText().equals(filter)) {
            return;
        }

        filter = filterText.getText();
        if (!Strings.isNullOrEmpty(filter)) {
            commandLabel.setImage(EditorPlugin.getDefault().getImage(
                    EditorPluginImages.IMG_FIND_CLEAR));
            refreshViewer();
            viewer.expandAll();
        }
        else {
            commandLabel.setImage(EditorPlugin.getDefault().getImage(EditorPluginImages.IMG_FIND));
            refreshViewer();
        }
    }

    /**
     * Refreshes the content of the treeviewer, using the changed model and the
     * presentation state, a tree is constructed.
     */
    private void executeRefreshJob() {
        Job.getJobManager().cancel(PropertiesView.this);
        modelChangedJob = null;

        // Ignore if the parent composite has been disposed
        if (Widgets.isNullOrDisposed(getParent())) {
            return;
        }

        refreshJob = new CreateModelViewJob();
        refreshJob.setRule(OntologyFormEditor.MUTEX_RULE);
        refreshJob.setUser(false);
        refreshJob.schedule(getModelProvider());
        refreshJob.addJobDoneListener(new ModelJobListener() {
            @Override
            public void done(final PropertiesViewModel newViewModel) {
                Display.getDefault().asyncExec(new Runnable() {
                    @Override
                    public void run() {
                        if (Widgets.isNullOrDisposed(getParent()) || !hasModelProvider()) {
                            return;
                        }

                        viewModel = newViewModel;

                        createContainer();
                        createSearchControls();
                        createPropertiesTree();
                        createContextMenu();

                        layoutParent(true, true);
                        refreshViewer();

                        refreshJob = null;
                        if (modelChangedJobQueued) {
                            executeModelChangedJob();
                        }
                    }
                });
            }
        });
    }

    private void restoreState() {
        // TODO: In case of large tree, this can cause the complete tree to be
        // ge-updated (and as a result packColumns)
        // if (providingModelUri != null) {
        // restoringExpandedElements = true;
        // viewer.expandToLevel(1);
        // if (editorExpandedElements.containsKey(providingModelUri)) {
        // viewer.setExpandedElements(editorExpandedElements.get(providingModelUri));
        // }
        // restoringExpandedElements = false;
        // }

        Object object = getStateParameter(PARAM_VIEWER_EXPANDED_ELEMENTS);
        Object[] paramValue = null;
        if (object != null && object instanceof Object[]) {
            paramValue = (Object[]) object;
        }

        if (currentModel != null && viewer != null) {
            restoringExpandedElements = true;
            viewer.setAutoExpandLevel(0);
            if (paramValue != null) {
                viewer.expandToLevel(2);
                viewer.setExpandedElements(paramValue);
            }
            else {
                if (viewer.getExpandedElements().length > 0) {
                    setStateParameter(PARAM_VIEWER_EXPANDED_ELEMENTS, viewer.getExpandedElements());
                }
            }
            restoringExpandedElements = false;
        }

        Commands.refreshElements(PropertiesViewPresentationHandler.ID);
        Commands.refreshElements(ToggleShowInversePropertiesHandler.ID);
    }

    @Override
    public void createPartControl(Composite parent) {
        super.createPartControl(parent);
        contentProvider = new PropertiesTreeContentProvider();
        getSite().setSelectionProvider(selectionProvider);
        setInitialized(true);
    }

    public void createContainer() {
        if (Widgets.isNullOrDisposed(getParent())) {
            return;
        }
        else if (Widgets.isNullOrDisposed(container)) {
            container = new Composite(getParent(), SWT.NONE);
            GridDataFactory.fillDefaults().grab(true, false).applyTo(container);
            GridLayout layout = new GridLayout(1, false);
            layout.verticalSpacing = 3;
            layout.marginWidth = 0;
            layout.marginHeight = 0;
            container.setLayout(layout);
            container.addControlListener(new ControlAdapter() {
                @Override
                public void controlResized(ControlEvent e) {
                    int newWidth = Math.max(300, container.getSize().x - 20);
                    if (!Widgets.isNullOrDisposed(tree)) {
                        TreeColumn[] treeColumns = tree.getColumns();
                        if (treeColumns.length == 1) {
                            treeColumns[0].setWidth(newWidth);
                        }
                    }
                }
            });
        }
    }

    public void createSearchControls() {
        if (Widgets.isNullOrDisposed(searchComposite)) {
            searchComposite = new Composite(container, SWT.NONE);
            GridData layoutData = new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1);
            searchComposite.setLayoutData(layoutData);
            GridLayout layout = new GridLayout(1, false);
            searchComposite.setLayout(layout);

            Composite inputComposite = new Composite(searchComposite, SWT.BORDER);
            layoutData = new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1);
            inputComposite.setLayoutData(layoutData);
            layout = new GridLayout(3, false);
            layout.marginBottom = 1;
            layout.marginTop = 1;
            layout.marginWidth = 0;
            layout.marginHeight = 0;
            inputComposite.setLayout(layout);
            inputComposite.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));

            @SuppressWarnings("unused")
            Label label = new Label(inputComposite, SWT.NONE);

            filterText = new Text(inputComposite, SWT.NONE);
            filterText.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
            if (filter != null && filter.length() > 0) {
                filterText.setText(filter);
            }
            else {
                filterText.setText("Find");
            }
            filterText.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_GRAY));
            filterText.addModifyListener(new ModifyListener() {
                @Override
                public void modifyText(ModifyEvent e) {
                    if (!resettingFilter) {
                        filterExecution.start();
                    }
                }
            });
            filterText.addKeyListener(new KeyAdapter() {
                @Override
                public void keyReleased(KeyEvent e) {
                    if (e.keyCode == SWT.CR) {
                        filterExecution.abort();
                        applyFilter();
                    }
                }
            });
            filterText.addFocusListener(new FocusAdapter() {

                @Override
                public void focusGained(FocusEvent e) {
                    filterText.setText(filter);
                    filterText.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_BLACK));
                }

                @Override
                public void focusLost(FocusEvent e) {
                    filterExecution.abort();
                    applyFilter();

                    if (Strings.isNullOrEmpty(filter)) {
                        resettingFilter = true;
                        filterText.setText("Find");
                        filterText.setForeground(Display.getDefault()
                                .getSystemColor(SWT.COLOR_GRAY));
                        resettingFilter = false;
                    }
                }
            });

            commandLabel = new Label(inputComposite, SWT.NONE);
            commandLabel.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
            commandLabel.setImage(EditorPlugin.getDefault().getImage(EditorPluginImages.IMG_FIND));
            commandLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseDown(MouseEvent e) {
                    filterExecution.abort();
                    filterText.setText("");
                    applyFilter();
                    filterText.setFocus();
                }
            });
        }
    }

    public void createPropertiesTree() {
        if (tree != null && !tree.isDisposed()) {
            tree.dispose();
        }

        viewer = new LazyTreeViewer(container, SWT.VIRTUAL | SWT.MULTI);
        viewer.setUseHashlookup(true);
        viewer.setContentProvider(contentProvider);
        viewer.addTreeListener(new PropertiesTreeViewerListener());
        viewer.setLabelProvider(new PropertiesLabelProvider());
        ResourceViewerToolTipSupport.enableFor(viewer, getModelProvider());

        // getSite().setSelectionProvider(viewer);

        final DragSource dndSource = new DragSource(viewer.getTree(), DND.DROP_MOVE | DND.DROP_COPY);
        dndSource.addDragListener(new PropertiesDragSourceListener(viewer));
        viewer.addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                boolean isProperty = false;

                if (Selections.hasAllOfType(event.getSelection(), Property.class)) {
                    isProperty = true;
                }
                if (isProperty) {
                    Transfer[] transferTypes = new Transfer[] {
                            PropertyArrayListTransfer.getInstance(),
                            PropertyTransfer.getInstance(),
                            ResourceArrayListTransfer.getInstance(),
                            ResourceTransfer.getInstance(), TextTransfer.getInstance() };
                    dndSource.setTransfer(transferTypes);
                }
                else {
                    dndSource.setTransfer(new Transfer[] {});
                }
            }
        });

        viewer.addDoubleClickListener(new IDoubleClickListener() {
            @Override
            public void doubleClick(DoubleClickEvent event) {
                ISelection selection = event.getViewer().getSelection();
                Resource selected = Selections.retrieveFirstAsType(selection, Resource.class);
                if (selected != null) {
                    CorePlugin.getDefault().openResource(selected);
                }
            }
        });

        tree = viewer.getTree();

        if (boldFont == null) {
            FontData[] boldFontData = FontUtil.getModifiedFontData(viewer.getTree().getFont()
                    .getFontData(), SWT.BOLD);
            boldFont = new Font(Display.getCurrent(), boldFontData);
        }

        tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        boolean showInverses = showInverseProperty();

        TreeColumn columnProperty = new TreeColumn(tree, SWT.NONE, 0);
        columnProperty.setText("Property");
        columnProperty.setWidth(showInverses ? 300 : Math.max(300, container.getSize().x - 20));
        columnProperty.setMoveable(false);

        if (showInverses) {
            TreeColumn columnInverse = new TreeColumn(tree, SWT.NONE, 1);
            columnInverse.setText("Inverse");
            columnInverse.setWidth(300);
            columnInverse.setMoveable(false);
        }
        tree.setHeaderVisible(showInverses);
        tree.setLinesVisible(showInverses);

        selectionProvider.updateViewer(viewer);

        container.layout(true, true);
    }

    /**
     * Creates and registers a contextmenu to the properties view. This context
     * menu can then be extended from the plugin.xml using the menu id
     * popup:com.semmtech.plugin.semmweb.editor.views.properties
     */
    private void createContextMenu() {
        MenuManager menuManager = new MenuManager();
        Menu menu = menuManager.createContextMenu(viewer.getControl());
        viewer.getControl().setMenu(menu);
        getSite().registerContextMenu(menuManager, viewer);
    }

    @Override
    public void init(IViewSite site) throws PartInitException {
        super.init(site);
        EditorPlugin.getDefault().getPreferenceStore().addPropertyChangeListener(this);
        CorePlugin.getDefault().getPreferenceStore().addPropertyChangeListener(this);
    }

    @Override
    public void dispose() {
        EditorPlugin.getDefault().getPreferenceStore().removePropertyChangeListener(this);
        CorePlugin.getDefault().getPreferenceStore().addPropertyChangeListener(this);
        super.dispose();
    }

    @Override
    public void setFocus() {
        if (viewer != null) {
            Control control = viewer.getControl();
            if (control != null && !control.isDisposed()) {
                control.setFocus();
            }
        }
    }

    @Override
    public void modelActivated(ModelActivatedEvent event) {
        refreshWithChangedModelInformation();
    }

    private void executeModelChangedJob() {
        if ((currentModel == null) || (viewer == null) || (Widgets.isNullOrDisposed(tree))) {
            modelChangedJobQueued = false;
            return;
        }

        if (refreshJob != null || modelChangedJob != null) {
            modelChangedJobQueued = true;
            return;
        }

        modelChangedJobQueued = false;

        modelChangedJob = new CreateModelViewJob();
        modelChangedJob.setRule(OntologyFormEditor.MUTEX_RULE);
        modelChangedJob.setUser(false);
        modelChangedJob.schedule(getModelProvider());
        modelChangedJob.addJobDoneListener(new ModelJobListener() {
            @Override
            public void done(final PropertiesViewModel newViewModel) {
                Display.getDefault().asyncExec(new Runnable() {
                    @Override
                    public void run() {
                        if ((currentModel == null) || (viewer == null)
                                || (Widgets.isNullOrDisposed(tree))) {
                            modelChangedJob = null;
                            modelChangedJobQueued = false;
                            return;
                        }

                        synchronized (mutex) {

                            PropertiesViewModel oldViewModel = viewModel;
                            viewModel = newViewModel;

                            logger.trace("Determining the delta for the new viewModel");

                            DeltaPropertiesViewModel relationsRemoved = oldViewModel
                                    .difference(newViewModel);
                            DeltaPropertiesViewModel relationsAdded = newViewModel
                                    .difference(oldViewModel);

                            logger.trace("Determining the delta for the new viewModel has finished");
                            logger.trace("Adjusting the TreeViewer");

                            String state = getPresentationState();
                            if (state.equals(PropertiesViewPresentationHandler.STATE_BY_NAMESPACE)) {
                                // Removed namespaces
                                for (Resource namespace : relationsRemoved.getNamespaces()) {
                                    contentProvider.removeNamespace(namespace);
                                }

                                // Added namespaces
                                for (Resource namespace : relationsAdded.getNamespaces()) {
                                    contentProvider.addNamespace(namespace);
                                }

                                int namespaceCount = newViewModel.getNamespaceCount();
                                for (int i = 0; i < namespaceCount; i++) {
                                    Resource namespace = newViewModel.getNamespace(i);
                                    // Removed properties
                                    for (Property property : relationsRemoved
                                            .getNamespaceProperties(namespace)) {
                                        contentProvider.removeProperty(property, namespace);
                                    }

                                    // Added properties
                                    for (Property property : relationsAdded
                                            .getNamespaceProperties(namespace)) {
                                        if (!Strings.isNullOrEmpty(filter)
                                                || newViewModel.getText(property).toLowerCase()
                                                        .contains(filter.toLowerCase())) {
                                            contentProvider.addProperty(property, namespace);
                                        }
                                    }
                                }
                            }
                            else {
                                // Removed roots
                                for (Property rootProperty : relationsRemoved.getRootProperties()) {
                                    contentProvider.removeRoot(rootProperty);
                                }

                                // Added roots
                                for (Property rootProperty : relationsAdded.getRootProperties()) {
                                    contentProvider.addRoot(rootProperty);
                                }

                                // Removed childOfs
                                StmtIterator statementIter = relationsRemoved.listStatements(null,
                                        PropertiesViewModel.Vocabulary.isChildOf, (RDFNode) null);
                                while (statementIter.hasNext()) {
                                    Statement statement = statementIter.next();
                                    Property child = JenaUtil.asOntProperty(statement.getSubject(),
                                            currentModel);
                                    Property parent = JenaUtil.asOntProperty(statement.getObject()
                                            .asResource(), currentModel);
                                    contentProvider.removeChild(child, parent);
                                }

                                // Added childOfs
                                statementIter = relationsAdded.listStatements(null,
                                        PropertiesViewModel.Vocabulary.isChildOf, (RDFNode) null);
                                while (statementIter.hasNext()) {
                                    Statement statement = statementIter.next();
                                    Property child = JenaUtil.asOntProperty(statement.getSubject(),
                                            currentModel);
                                    Property parent = JenaUtil.asOntProperty(statement.getObject()
                                            .asResource(), currentModel);
                                    contentProvider.addChild(child, parent);
                                }
                            }

                            Set<Resource> resourcesToUpdate = Sets.newHashSet();

                            // Altered texts
                            Set<Resource> resourcesWithPropertyAltered = relationsRemoved
                                    .listSubjectsWithProperty(PropertiesViewModel.Vocabulary.text)
                                    .toSet();
                            Set<Resource> resourcesWithPropertyAdded = relationsAdded
                                    .listSubjectsWithProperty(PropertiesViewModel.Vocabulary.text)
                                    .toSet();
                            resourcesWithPropertyAltered.retainAll(resourcesWithPropertyAdded);
                            resourcesToUpdate.addAll(resourcesWithPropertyAltered);

                            boolean showInverses = showInverseProperty();
                            if (showInverses) {
                                // Removed inverses & added inverses
                                ResIterator resourceIter = relationsRemoved
                                        .listSubjectsWithProperty(OWL.inverseOf);
                                while (resourceIter.hasNext()) {
                                    resourcesToUpdate.add(resourceIter.next());
                                }
                                resourceIter = relationsAdded
                                        .listSubjectsWithProperty(OWL.inverseOf);
                                while (resourceIter.hasNext()) {
                                    resourcesToUpdate.add(resourceIter.next());
                                }
                            }

                            contentProvider.updateResources(resourcesToUpdate);

                            logger.trace("Adjusting the TreeViewer has finished");
                        }

                        modelChangedJob = null;
                        if (modelChangedJobQueued) {
                            executeModelChangedJob();
                        }
                    }
                });
            }
        });
    }

    @Override
    public void modelChanged(ModelChangedEvent event) {
        // TODO: Perhaps only obtain new view model if the statements
        // added/removed contain predicates that may truly alter the view model.

        executeModelChangedJob();
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
        Widgets.disposeIfExists(container);

        rdfNodeLabelProvider = getLabelProvider();
        currentModel = getOntModel();

        if (hasModelProvider()) {
            executeRefreshJob();
        }
    }

    private void refreshViewer() {
        if (Widgets.isNullOrDisposedViewer(viewer)) {
            return;
        }
        Tree tree = viewer.getTree();
        tree.setRedraw(false);
        viewer.setInput(currentModel);
        if (currentModel != null && viewModel != null) {
            tree.setItemCount(contentProvider.getItemCount());
            viewer.refresh();
            restoreState();
        }
        tree.setRedraw(true);
    }

    private void collapseAll() {
        if (!Widgets.isNullOrDisposedViewer(viewer)) {
            viewer.collapseAll();
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        String property = event.getProperty();
        if (property.equals(LabelsPreference.PREFERENCE_RESOURCE_LABEL_RENDERING)
                || property.equals(LanguagesPreference.PREFERENCE_DISPLAY_LANGUAGES)) {

            // Changes to these properties also require the model to be
            // refreshed!
            refreshWithChangedModelInformation();
        }
    }

    public static class RefreshViewHandler extends AbstractHandler {
        public static final String ID = "com.semmtech.plugin.semmweb.editor.commands.refreshPropertiesView";

        @Override
        public Object execute(ExecutionEvent event) throws ExecutionException {
            if (singleton != null && singleton.isInitialized()) {
                singleton.executeRefreshJob();
            }
            return null;
        }
    }

    public static class PropertiesViewPresentationHandler extends AbstractHandler implements
            IElementUpdater {
        public static final String ID = "com.semmtech.plugin.semmweb.editor.commands.propertiesViewPresentation";
        public static final String STATE_BY_NAMESPACE = "byNamespace";
        public static final String STATE_HIERARCHICAL = "hierarchical";

        @Override
        public Object execute(ExecutionEvent event) throws ExecutionException {
            if (HandlerUtil.matchesRadioState(event)) {
                return null;
            }
            String currentState = event.getParameter(RadioState.PARAMETER_ID);
            HandlerUtil.updateRadioState(event.getCommand(), currentState);

            if (singleton != null && singleton.isInitialized()) {
                singleton.setStateParameter(PropertiesView.PARAM_PROPERTIES_VIEW_STATE,
                        currentState);
                singleton.refreshViewer();
            }

            return null;
        }

        @Override
        public void updateElement(UIElement element, @SuppressWarnings("rawtypes") Map parameters) {
            if (singleton != null && singleton.isInitialized()) {
                String state = singleton.getPresentationState();
                if (!Strings.isNullOrEmpty(state)) {
                    Command command = Commands.getCommand(ID);
                    try {
                        HandlerUtil.updateRadioState(command, state);
                    }
                    catch (ExecutionException e) {
                        logger.error(
                                String.format("Error while refreshing command with id %s", ID), e);
                    }
                }
            }
        }
    }

    /**
     * Returns the selected presentation state for the properties view
     * 
     * @return
     */
    private String getPresentationState() {
        String state = (String) getStateParameter(PARAM_PROPERTIES_VIEW_STATE);

        if (Strings.isNullOrEmpty(state)) {
            return PropertiesViewPresentationHandler.STATE_BY_NAMESPACE;
        }

        return state;
    }

    private boolean showInverseProperty() {
        if (hasStateParameter(PARAM_SHOW_INVERSE_PROPERTIES)) {
            return ((Boolean) getStateParameter(PARAM_SHOW_INVERSE_PROPERTIES)).booleanValue();
        }
        return false;
    }

    // public static class EditPropertiesReasoningHandler extends
    // AbstractHandler {
    // public static final String ID =
    // "com.semmtech.plugin.semmweb.editor.commands.editPropertiesReasoning";
    //
    // @Override
    // public Object execute(ExecutionEvent event) throws ExecutionException {
    // PreferenceDialog dialog = PreferencesUtil.createPreferenceDialogOn(
    // HandlerUtil.getActiveShell(event), PropertiesReasoningPreferencePage.ID,
    // null,
    // null);
    // dialog.open();
    // return null;
    // }
    // }

    public static class ToggleShowInversePropertiesHandler extends AbstractHandler implements
            IElementUpdater {
        public static final String ID = "com.semmtech.plugin.semmweb.editor.commands.toggleShowInverseProperties";

        @Override
        public Object execute(ExecutionEvent event) throws ExecutionException {
            // Changes to these properties only requires the viewer to be
            // refreshed!
            if (singleton != null && singleton.isInitialized()) {
                singleton.setStateParameter(PARAM_SHOW_INVERSE_PROPERTIES,
                        !singleton.showInverseProperty());
                singleton.createPropertiesTree();
                singleton.createContextMenu();
                singleton.refreshViewer();
            }
            return null;
        }

        @Override
        public void updateElement(UIElement element, @SuppressWarnings("rawtypes") Map parameters) {
            if (singleton != null && singleton.isInitialized()) {
                boolean state = singleton.showInverseProperty();
                element.setChecked(state);
                Commands.setToggleState(ID, state);
            }
        }
    }

    private interface ModelJobListener {
        public void done(final PropertiesViewModel newViewModel);
    }

    private class CreateModelViewJob extends JobWithMonitor {
        private IModelProvider modelProvider;
        private final List<ModelJobListener> listeners;

        public CreateModelViewJob() {
            super("Refreshing Properties");
            listeners = Lists.newArrayList();
        }

        public void schedule(IModelProvider modelProvider) {
            this.modelProvider = modelProvider;
            schedule();
        }

        public void addJobDoneListener(ModelJobListener listener) {
            if (!listeners.contains(listener)) {
                listeners.add(listener);
            }
        }

        @Override
        public boolean belongsTo(Object family) {
            return family.equals(PropertiesView.this);
        }

        @Override
        protected IStatus run(IProgressMonitor monitor) {
            PropertiesViewModel result = null;

            if (monitor.isCanceled()) {
                return Status.CANCEL_STATUS;
            }

            synchronized (mutex) {
                settingViewerInput = true;
            }

            try {
                startMonitorUpdate(monitor, "Refreshing Properties", 1);

                OntModel currentModel = modelProvider.getOntModel();
                if (currentModel != null) {
                    logger.trace("CreateModelViewJob will create the new viewModel");
                    result = PropertiesViewModel.create(currentModel);
                    logger.trace("CreateModelViewJob has created the new viewModel");
                }

                addWorked(1);
            }
            catch (ClosedException ex) {
                monitor.setCanceled(true);
                logger.warn("The model has been closed during the Properties View build query execution!");
            }
            finally {
                monitor.done();
                stopMonitorUpdate();

                synchronized (mutex) {
                    settingViewerInput = false;
                }

                if (monitor.isCanceled()) {
                    return Status.CANCEL_STATUS;
                }

                for (ModelJobListener listener : listeners) {
                    listener.done(result);
                }
            }
            return Status.OK_STATUS;
        }
    }

    @Override
    protected void cleanup() {
        if (viewModel != null) {
            viewModel.close();
            viewModel = null;
        }
    }

    public static class CollapseAllHandler extends AbstractHandler {

        @Override
        public Object execute(ExecutionEvent event) throws ExecutionException {
            if (singleton != null && singleton.isInitialized()) {
                singleton.collapseAll();
            }
            return null;
        }
    }
}
