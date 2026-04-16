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

package com.semmtech.plugin.semmweb.editor.views.instances;


import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILazyContentProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.TextStyle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.commands.IElementUpdater;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.menus.UIElement;
import org.eclipse.ui.services.ISourceProviderService;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.XSD;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.commands.Commands;
import com.semmtech.plugin.semmweb.core.dialog.LiteralStatementInputDialog;
import com.semmtech.plugin.semmweb.core.dialog.ResourceStatementInputDialog;
import com.semmtech.plugin.semmweb.core.dialog.StatementInputDialog;
import com.semmtech.plugin.semmweb.core.dnd.DndUtils;
import com.semmtech.plugin.semmweb.core.dnd.OntClassTransfer;
import com.semmtech.plugin.semmweb.core.dnd.PropertyTransfer;
import com.semmtech.plugin.semmweb.core.dnd.ResourceArrayListTransfer;
import com.semmtech.plugin.semmweb.core.dnd.ResourceTransfer;
import com.semmtech.plugin.semmweb.core.forms.editor.OntologyFormEditor;
import com.semmtech.plugin.semmweb.core.handlers.PreferenceStoreToggleHandler;
import com.semmtech.plugin.semmweb.core.model.IModelProvider;
import com.semmtech.plugin.semmweb.core.model.ModelTransaction;
import com.semmtech.plugin.semmweb.core.model.events.ModelActivatedEvent;
import com.semmtech.plugin.semmweb.core.model.events.ModelChangedEvent;
import com.semmtech.plugin.semmweb.core.model.events.NamespacePrefixChangedEvent;
import com.semmtech.plugin.semmweb.core.model.events.SubModelAddedEvent;
import com.semmtech.plugin.semmweb.core.model.events.SubModelRemovedEvent;
import com.semmtech.plugin.semmweb.core.preferences.LabelsPreference;
import com.semmtech.plugin.semmweb.core.preferences.LanguagesPreference;
import com.semmtech.plugin.semmweb.core.viewers.ResourceViewerToolTipSupport;
import com.semmtech.plugin.semmweb.core.views.AbstractModelListenerView;
import com.semmtech.plugin.semmweb.core.widgets.DropComposite;
import com.semmtech.plugin.semmweb.core.widgets.SearchComposite;
import com.semmtech.plugin.semmweb.core.widgets.SearchFilterChangedListener;
import com.semmtech.plugin.semmweb.core.widgets.trees.OntClassTreeData;
import com.semmtech.plugin.semmweb.core.widgets.trees.ResourceTreeData;
import com.semmtech.plugin.semmweb.core.widgets.trees.TreeData;
import com.semmtech.plugin.semmweb.editor.EditorPlugin;
import com.semmtech.plugin.semmweb.editor.preferences.InstancesViewPreference;
import com.semmtech.plugin.semmweb.editor.preferences.InstancesViewPreferenceConstants;
import com.semmtech.plugin.semmweb.editor.views.sourceprovider.EditorViewInputSourceProvider;
import com.semmtech.plugin.semmweb.editor.views.sourceprovider.InstancesViewSourceProvider;
import com.semmtech.plugin.semmweb.editor.views.taxonomy.OWLClassesView;
import com.semmtech.plugin.semmweb.editor.views.taxonomy.RDFSClassesView;
import com.semmtech.ui.plugin.BasePlugin;
import com.semmtech.ui.plugin.BasePluginImages;
import com.semmtech.ui.plugin.jobs.JobWithMonitor;
import com.semmtech.ui.plugin.util.FontUtil;
import com.semmtech.ui.plugin.util.Selections;
import com.semmtech.ui.plugin.viewers.LazyTreeContentProvider;
import com.semmtech.ui.plugin.viewers.PendingElement;
import com.semmtech.ui.plugin.viewers.ViewerSelectionProvider;
import com.semmtech.ui.plugin.widgets.Widgets;


/**
 * This view lists all instances for a selected resource.
 * 
 * @author Mike Henrichs
 * 
 */
public class InstancesView extends AbstractModelListenerView implements ISelectionListener,
        IPropertyChangeListener {
    public static final String ID = "com.semmtech.plugin.semmweb.editor.views.instances";
    private static final Logger logger = Logger.getLogger(InstancesView.class);

    public final static String PARAM_SELECTED_RESOURCE = "var:selectedResource";
    public final static String PARAM_SHOW_FILTER = "var:showFilter";
    public final static String PARAM_FILTER = "var:filter";
    public final static String PARAM_LINK_WITH_RDFS_CLASSES_VIEW = "var:linkWithRDFSClassesView";
    public final static String PARAM_LINK_WITH_OWL_CLASSES_VIEW = "var:linkWithOWLClassesView";
    public final static String PARAM_GROUP_BY_DIRECT_TYPE = "var:groupByDirectType";

    private final class InstancesContentProvider extends LazyTreeContentProvider implements
            ILazyContentProvider {

        public int getItemCount() {
            if (groupByDirectType) {
                return viewModel.getDirectTypeCount();
            }
            String currentFilter = showFilter ? filter : null;
            return viewModel.getInstanceCount(currentFilter);
        }

        // Method for TableViewer
        @Override
        public void updateElement(int index) {
            if ((currentModel == null) || (viewModel == null)) {
                return;
            }

            String currentFilter = showFilter ? filter : null;
            Resource resource = viewModel.getInstance(currentFilter, index);
            tableViewer.replace(resource, index);
        }

        // Method for TreeViewer
        @Override
        public void updateElement(Object parent, int index) {
            if ((currentModel == null) || (viewModel == null)) {
                return;
            }

            String currentFilter = showFilter ? filter : null;

            if (parent instanceof Model) {
                OntClass directType = viewModel.getDirectType(index);
                OntClassTreeData element = new OntClassTreeData(directType);
                treeViewer.replace(parent, index, element);
                treeViewer.setChildCount(element,
                        viewModel.getDirectInstanceCount(directType, currentFilter));
            }

            else if (parent instanceof OntClassTreeData) {
                OntClass directType = (OntClass) parent;
                Resource instance = viewModel.getDirectInstance(directType, currentFilter, index);
                ResourceTreeData element = new ResourceTreeData(instance);
                element.setParent((TreeData) parent);
                treeViewer.replace(parent, index, element);
                treeViewer.setChildCount(element, 0);
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

        public void addDirectType(OntClass type) {
            if (!groupByDirectType) {
                throw new RuntimeException(
                        "Cannot add a direct type to the Instances viewer when it not set to group by direct type.");
            }
            if (Widgets.isNullOrDisposedViewer(treeViewer) || (type == null)) {
                return;
            }
            OntClassTreeData typeData = new OntClassTreeData(type);
            int insertionIndex = getInsertionIndex(typeData, treeViewer.getTree().getItems());
            if (insertionIndex >= 0) {
                String currentFilter = showFilter ? filter : null;
                treeViewer.insert(currentModel, typeData, insertionIndex);
                treeViewer.setChildCount(typeData,
                        viewModel.getDirectInstanceCount(type, currentFilter));
            }
        }

        public void removeDirectType(OntClass type) {
            if (!groupByDirectType) {
                throw new RuntimeException(
                        "Cannot remove a direct type from the Instances viewer when it not set to group by direct type.");
            }
            if (Widgets.isNullOrDisposedViewer(treeViewer) || (type == null)) {
                return;
            }
            OntClassTreeData typeData = new OntClassTreeData(type);
            treeViewer.remove(currentModel, new Object[] { typeData });
        }

        public void addInstance(Resource instance, OntClass type) {
            if (!groupByDirectType) {
                throw new RuntimeException(
                        "Cannot add an instance to the Instances viewer when it not set to group by direct type. The TableViewer refreshes completely rather than using a selective refresh.");
            }
            if (Widgets.isNullOrDisposedViewer(treeViewer) || (instance == null) || (type == null)) {
                return;
            }

            OntClassTreeData typeData = new OntClassTreeData(type);
            ResourceTreeData instanceData = new ResourceTreeData(instance);
            instanceData.setParent(typeData);
            for (TreeItem rootItem : treeViewer.getTree().getItems()) {
                Object rootItemData = rootItem.getData();
                if ((rootItemData != null) && typeData.equals(rootItemData)) {
                    if (!hasCachedChildren(rootItem)) {
                        String currentFilter = showFilter ? filter : null;
                        rootItem.setItemCount(viewModel.getDirectInstanceCount(type, currentFilter));
                    }
                    else {

                        int insertionIndex = getInsertionIndex(instanceData, rootItem.getItems());
                        if (insertionIndex >= 0) {
                            treeViewer.insert(typeData, instanceData, insertionIndex);
                        }

                    }
                    return;
                }
            }
        }

        public void removeInstance(Resource instance, OntClass type) {
            if (!groupByDirectType) {
                throw new RuntimeException(
                        "Cannot remove an instance from the Instances viewer when it not set to group by direct type. The TableViewer refreshes completely rather than using a selective refresh.");
            }
            if (Widgets.isNullOrDisposedViewer(treeViewer) || (instance == null) || (type == null)) {
                return;
            }

            OntClassTreeData typeData = new OntClassTreeData(type);
            ResourceTreeData instanceData = new ResourceTreeData(instance);
            instanceData.setParent(typeData);
            for (TreeItem rootItem : treeViewer.getTree().getItems()) {
                Object rootItemData = rootItem.getData();
                if ((rootItemData != null) && typeData.equals(rootItemData)) {
                    if (rootItem.getItemCount() > 0) {
                        if (!hasCachedChildren(rootItem)) {
                            String currentFilter = showFilter ? filter : null;
                            rootItem.setItemCount(viewModel.getDirectInstanceCount(type,
                                    currentFilter));
                        }
                        else {
                            treeViewer.remove(typeData, new Object[] { instanceData });
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

        // FIXME: My suggestion is to move this to a ResourceTreeViewer class
        // (See also InstancesView, PropertiesView.)
        public void updateResources(Collection<? extends Resource> resources) {
            if (!groupByDirectType) {
                throw new RuntimeException(
                        "Cannot yet update resources in the TableViewer. The TableViewer refreshes completely rather than using a selective refresh.");
            }

            if (resources == null) {
                return;
            }

            for (Resource resource : resources) {
                for (TreeItem treeItem : findTreeItems(resource)) {
                    Object treeItemData = treeItem.getData();
                    treeViewer.update(treeItemData, null);
                }
            }
        }

        // FIXME: My suggestion is to move this to a ResourceTreeViewer class
        // (See also InstancesView, PropertiesView.)
        /**
         * Returns a list of TreeItems that contain the resource.
         */
        private List<TreeItem> findTreeItems(Resource resource) {
            List<TreeItem> result = Lists.newArrayList();

            if (!Widgets.isNullOrDisposedViewer(treeViewer) && (resource != null)) {
                result.addAll(findTreeItems(resource, treeViewer.getTree().getItems()));
            }

            return result;
        }

        // FIXME: My suggestion is to move this to a ResourceTreeViewer class
        // (See also InstancesView, PropertiesView.)
        private List<TreeItem> findTreeItems(Resource resource, TreeItem[] treeItems) {
            List<TreeItem> result = Lists.newArrayList();

            if (!Widgets.isNullOrDisposedViewer(treeViewer) && (resource != null)
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

    private final class InstancesLabelProvider extends StyledCellLabelProvider {
        private final Styler filterStyler = new Styler() {
            @Override
            public void applyStyles(TextStyle textStyle) {
                textStyle.font = boldFont;
                textStyle.foreground = Display.getCurrent().getSystemColor(SWT.COLOR_BLACK);
            }
        };

        @Override
        public void update(ViewerCell cell) {
            int columnIndex = cell.getColumnIndex();
            Object element = cell.getElement();
            String text = Strings.nullToEmpty(getElementText(element, columnIndex));
            StyledString styledText = new StyledString();
            styledText.append(text);
            if (showFilter
                    && !Strings.isNullOrEmpty(filter)
                    && ((!(element instanceof ResourceTreeData)) || !(((ResourceTreeData) element)
                            .getData() instanceof OntClass))) {
                int startIndex = text.toLowerCase().indexOf(filter.toLowerCase());
                while (startIndex > -1) {
                    int length = filter.length();
                    styledText.setStyle(startIndex, length, filterStyler);
                    startIndex = text.toLowerCase().indexOf(filter.toLowerCase(), startIndex + 1);
                }
            }
            cell.setText(styledText.getString());
            cell.setStyleRanges(styledText.getStyleRanges());
            cell.setImage(getElementImage(cell.getElement(), columnIndex));

            super.update(cell);
        }

        private Image getElementImage(Object element, int columnIndex) {
            if (labelProvider == null) {
                return null;
            }
            if (element instanceof Resource) {
                Resource resource = (Resource) element;
                return labelProvider.getImage(resource);
            }
            if (element instanceof PendingElement) {
                return BasePlugin.getDefault().getImage(BasePluginImages.IMG_PENDING);
            }
            return null;
        }

        private String getElementText(Object element, int columnIndex) {
            if (viewModel == null) {
                return null;
            }

            if (element instanceof PendingElement) {
                return "Searching...";
            }

            if (element instanceof Resource) {
                Resource resource = (Resource) element;
                String text = viewModel.getText(resource);

                if ((element instanceof OntClassTreeData) && (text != null)) {
                    String currentFilter = showFilter ? filter : null;
                    int directInstanceCount = viewModel.getDirectInstanceCount(resource,
                            currentFilter);
                    text += String.format(" (%s)", directInstanceCount);

                }
                return text;
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
                Resource resource = (Resource) element;
                return resource.toString();
            }
            return null;
        }
    }

    private static InstancesView singleton;
    private static boolean linkWithRDFSClassesView;
    private static boolean linkWithOWLClassesView;
    private static boolean groupByDirectType;
    private static boolean showFilter;

    private Object mutex = new Object();
    private Resource selectedResource;
    private OntModel currentModel;
    private InstancesViewModel viewModel;
    private LabelProvider labelProvider;
    private String filter;

    private TreeViewer treeViewer;
    private Tree tree;
    private TableViewer tableViewer;
    private Table table;
    private DropComposite dropComposite;
    private Composite overviewComposite;
    private Composite searchComposite;
    private SearchComposite searchControl;
    private Label overviewLabel;
    private Font boldFont;

    private CreateModelViewJob refreshJob;
    private CreateModelViewJob modelChangedJob;
    private boolean modelChangedJobQueued;
    private InstancesContentProvider contentProvider;
    private ViewerSelectionProvider selectionProvider = new ViewerSelectionProvider();

    @SuppressWarnings("static-access")
    public InstancesView() {
        this.singleton = this;
    }

    private class InstancesViewerDoubleClickListener implements IDoubleClickListener {
        private final Viewer viewer;

        public InstancesViewerDoubleClickListener(Viewer viewer) {
            this.viewer = viewer;
        }

        @Override
        public void doubleClick(DoubleClickEvent event) {
            ISelection selection = viewer.getSelection();
            if (selection instanceof IStructuredSelection) {
                Object selected = ((IStructuredSelection) selection).getFirstElement();
                if (selected instanceof Resource) {
                    Resource resource = (Resource) selected;
                    openResource(resource);
                }
            }
        }
    }

    private class InstancesViewerDropAdapter extends ViewerDropAdapter {
        private int dropLocation;
        private Resource targetItem;

        protected InstancesViewerDropAdapter(Viewer viewer) {
            super(viewer);
        }

        @Override
        public boolean validateDrop(Object target, int operation, TransferData transferType) {
            if (PropertyTransfer.getInstance().isSupportedType(transferType)) {
                return true;
            }
            return false;
        }

        @Override
        public void drop(DropTargetEvent event) {
            dropLocation = this.determineLocation(event);
            Object target = determineTarget(event);
            if (target instanceof Resource) {
                targetItem = (Resource) target;
            }
            else {
                targetItem = null;
            }
            super.drop(event);
        }

        @Override
        public boolean performDrop(Object data) {
            if (targetItem != null) {
                if (data instanceof Property) {
                    Property property = (Property) data;
                    Resource resource = null;

                    switch (dropLocation) {
                    case LOCATION_ON:
                        resource = targetItem;
                        break;
                    case LOCATION_BEFORE:
                    case LOCATION_AFTER:
                    case LOCATION_NONE:
                        break;
                    }
                    if (resource != null) {
                        Model model = resource.getModel();
                        Shell shell = EditorPlugin.getDefault().getWorkbench()
                                .getActiveWorkbenchWindow().getShell();
                        String title = "Create Statement";
                        String message = "Create statement with given instance and predicate.";
                        StatementInputDialog dialog = null;

                        Resource range = null;
                        for (Statement rangeStatement : model.listStatements(
                                new SimpleSelector(property, RDFS.range, (RDFNode) null)).toList()) {
                            if (rangeStatement.getObject() != null
                                    && !rangeStatement.getObject().isLiteral()) {
                                range = (Resource) rangeStatement.getObject();
                                break;
                            }
                        }
                        if (range != null && range.equals(RDFS.Literal)) {
                            dialog = new LiteralStatementInputDialog(shell, title, message);
                        }
                        else if (range != null && range.equals(XSD.nonNegativeInteger)) {
                            dialog = new LiteralStatementInputDialog(shell, title, message);
                            ((LiteralStatementInputDialog) dialog).setDatatypeVisible(true);
                            ((LiteralStatementInputDialog) dialog).setLanguageVisible(false);
                            ((LiteralStatementInputDialog) dialog)
                                    .setDatatype(XSD.nonNegativeInteger);
                        }
                        else if (range == null) {
                            dialog = new LiteralStatementInputDialog(shell, title, message);
                            ((LiteralStatementInputDialog) dialog).setDatatypeVisible(true);
                            ((LiteralStatementInputDialog) dialog).setLanguageVisible(true);
                        }
                        else {
                            dialog = new ResourceStatementInputDialog(shell, title, message);
                            ((ResourceStatementInputDialog) dialog).setAllowedResourceType(range);
                        }
                        dialog.setModel(model);
                        dialog.setSubject(resource);
                        dialog.setProperties(Arrays.asList(property));
                        dialog.setSelectedProperty(0);

                        IModelProvider modelProvider = getModelProvider();
                        if ((modelProvider != null) && (dialog.open() == 0)) {
                            Statement statement = dialog.createStatement();
                            if (statement != null) {
                                String transactionDescription = "Added new statement due to drop of property on instances";
                                ModelTransaction transaction = modelProvider
                                        .createTransaction(transactionDescription);
                                model.add(statement);
                                modelProvider.commitTransaction(transaction);
                            }
                        }
                        return true;
                    }
                }
            }
            return false;
        }
    }

    private class InstancesViewerDragSourceAdapter extends DragSourceAdapter {
        private final Viewer viewer;

        public InstancesViewerDragSourceAdapter(Viewer viewer) {
            this.viewer = viewer;
        }

        @Override
        public void dragSetData(DragSourceEvent event) {
            DndUtils.setDragSetData(event, getOntModel(), viewer, null);
        }
    }

    @Override
    public void createPartControl(Composite parent) {
        super.createPartControl(parent);

        FontData[] boldFontData = FontUtil.getModifiedFontData(getParent().getFont().getFontData(),
                SWT.BOLD);
        boldFont = new Font(Display.getCurrent(), boldFontData);

        GridLayout layout = new GridLayout(1, false);
        layout.verticalSpacing = 0;
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        parent.setLayout(layout);

        contentProvider = new InstancesContentProvider();

        getSite().getWorkbenchWindow().getSelectionService().addPostSelectionListener(this);

        getSite().setSelectionProvider(selectionProvider);

        IWorkbenchPart activePart = getSite().getPage().getActivePart();
        ISelection currentSelection = getSite().getPage().getSelection();
        if (activePart != null && currentSelection != null) {
            selectionChanged(activePart, currentSelection);
        }

        setInitialized(true);
    }

    private void createContextMenu() {
        // Creates and registers a context menu to the view. This
        // context menu can then be extended from the plugin.xml
        // using the menu id
        // popup:com.semmtech.plugin.semmweb.editor.views.instances

        Viewer currentViewer = (groupByDirectType) ? treeViewer : tableViewer;

        if (!Widgets.isNullOrDisposedViewer(currentViewer)) {
            MenuManager menuManager = new MenuManager();
            Menu menu = menuManager.createContextMenu(currentViewer.getControl());
            currentViewer.getControl().setMenu(menu);
            getSite().registerContextMenu(menuManager, currentViewer);
        }
    }

    @Override
    public void init(IViewSite site) throws PartInitException {
        super.init(site);
        EditorPlugin.getDefault().getPreferenceStore().addPropertyChangeListener(this);
        CorePlugin.getDefault().getPreferenceStore().addPropertyChangeListener(this);
    }

    @Override
    public void dispose() {
        getSite().getWorkbenchWindow().getSelectionService().removePostSelectionListener(this);
        CorePlugin.getDefault().getPreferenceStore().removePropertyChangeListener(this);
        EditorPlugin.getDefault().getPreferenceStore().removePropertyChangeListener(this);
        super.dispose();
    }

    @Override
    public void setFocus() {
        if (groupByDirectType) {
            if (!Widgets.isNullOrDisposed(tree)) {
                tree.setFocus();
            }
        }
        else if (!Widgets.isNullOrDisposed(table)) {
            table.setFocus();
        }
    }

    /**
     * Respond to selection changes of any selection providers.
     */
    @Override
    public void selectionChanged(IWorkbenchPart part, ISelection selection) {
        // Ignore if the change is due to a close of the Instances View
        if (Widgets.isNullOrDisposed(getParent())) {
            return;
        }

        if (linkWithRDFSClassesView && (part instanceof RDFSClassesView)) {
            adjustViewToResourceSelection(selection);
        }
        else if (linkWithOWLClassesView && (part instanceof OWLClassesView)) {
            adjustViewToResourceSelection(selection);
        }
    }

    private void createInstancesViewer() {
        if (groupByDirectType) {
            createInstancesTree();
        }
        else {
            createInstancesTable();
        }
    }

    private void createInstancesTree() {
        Widgets.disposeIfExists(tree);

        treeViewer = new TreeViewer(getParent(), SWT.VIRTUAL | SWT.MULTI);
        tree = treeViewer.getTree();
        tree.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 1));

        setPropertiesViewer(treeViewer);
    }

    private void createInstancesTable() {
        Widgets.disposeIfExists(table);

        tableViewer = new TableViewer(getParent(), SWT.VIRTUAL | SWT.MULTI);
        table = tableViewer.getTable();
        table.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 1));

        setPropertiesViewer(tableViewer);
    }

    private void setPropertiesViewer(ColumnViewer viewer) {
        viewer.addDropSupport(DND.DROP_MOVE | DND.DROP_COPY, new Transfer[] { PropertyTransfer
                .getInstance() }, new InstancesViewerDropAdapter(viewer));
        viewer.addDragSupport(DND.DROP_MOVE | DND.DROP_COPY, new Transfer[] {
                ResourceArrayListTransfer.getInstance(), ResourceTransfer.getInstance() },
                new InstancesViewerDragSourceAdapter(viewer));
        viewer.addDoubleClickListener(new InstancesViewerDoubleClickListener(viewer));

        viewer.setContentProvider(contentProvider);
        viewer.setLabelProvider(new InstancesLabelProvider());
        viewer.setUseHashlookup(true);

        ResourceViewerToolTipSupport.enableFor(viewer, getModelProvider());
        selectionProvider.updateViewer(viewer);
    }

    private void createDropRegion() {
        Widgets.disposeIfExists(dropComposite);
        if (Widgets.isNullOrDisposed(getParent())) {
            return;
        }

        boolean small = !Widgets.isNullOrDisposed(tree) || !Widgets.isNullOrDisposed(table);
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
                    if (clazz != null) {
                        if (linkWithRDFSClassesView) {
                            Commands.execute(ToggleLinkWithRDFSClassesViewSelectionHandler.ID);
                        }
                        if (linkWithOWLClassesView) {
                            Commands.execute(ToggleLinkWithOWLClassesViewSelectionHandler.ID);
                        }
                        setInput(clazz);
                        refreshWithChangedModelInformation();
                    }
                }
            }
        });
    }

    private void setInput(Resource resource) {
        selectedResource = resource;
        setStateParameter(PARAM_SELECTED_RESOURCE, selectedResource);

        // get the source provider service
        ISourceProviderService sourceProviderService = (ISourceProviderService) getSite()
                .getWorkbenchWindow().getService(ISourceProviderService.class);
        // get the required service
        EditorViewInputSourceProvider viewInputProvider = (EditorViewInputSourceProvider) sourceProviderService
                .getSourceProvider(EditorViewInputSourceProvider.INPUT_INSTANCES_VIEW);
        // set the input state correctly
        IStructuredSelection inputSelection = (selectedResource == null) ? null
                : new StructuredSelection(selectedResource);
        viewInputProvider.setState(EditorViewInputSourceProvider.INPUT_INSTANCES_VIEW,
                inputSelection);
    }

    private void createOverview() {
        Widgets.disposeIfExists(overviewComposite);

        synchronized (mutex) {
            overviewComposite = new Composite(getParent(), SWT.NONE);
            GridLayout layout = new GridLayout(2, false);
            layout.marginLeft = 6;
            layout.horizontalSpacing = 2;
            layout.marginHeight = 6;
            overviewComposite.setLayout(layout);
            overviewComposite.setLayoutData(new GridData(GridData.FILL, SWT.TOP, true, false));

            if (selectedResource == null || labelProvider == null) {
                return;
            }

            Label icon = new Label(overviewComposite, SWT.NONE);
            icon.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
            icon.setImage(labelProvider.getImage(selectedResource));

            overviewLabel = new Label(overviewComposite, SWT.NONE);
            overviewLabel.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
            overviewLabel.setText(getOverviewLabelText());
        }
    }

    private String getOverviewLabelText() {
        StringBuilder text = new StringBuilder();
        if (labelProvider != null) {
            String name = labelProvider.getText(selectedResource);
            text.append(String.format("%s has ", name));
        }

        int count = viewModel.getInstanceCount(null);
        int direct = viewModel.getDirectInstanceCount(selectedResource, null);
        int indirect = count - direct;

        if (count == 0) {
            text.append("no instances");
        }
        else if (count == direct) {
            text.append(String.format("%s instances", count));
        }
        else {
            text.append(String.format("%s instances (%s direct, %s indirect)", count, direct,
                    indirect));
        }
        return text.toString();
    }

    private void createFilter() {
        Widgets.disposeIfExists(searchControl);
        Widgets.disposeIfExists(searchComposite);

        if (showFilter) {
            searchComposite = new Composite(getParent(), SWT.NONE);
            FillLayout layout = new FillLayout();
            layout.marginHeight = 5;
            layout.marginWidth = 6;
            searchComposite.setLayout(layout);
            searchComposite.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true,
                    false));

            searchControl = new SearchComposite(searchComposite, SWT.NONE);
            searchControl.setFilter(filter);
            searchControl.addSearchFilterChangedListener(new SearchFilterChangedListener() {

                @Override
                public void filterChanged(String value) {
                    filter = value;
                    setStateParameter(PARAM_FILTER, filter);
                    createOverview();
                    createInstancesViewer();
                    refreshInstancesViewer();
                    createDropRegion();
                    layoutParent(true, true);
                }
            });
        }
    }

    @Override
    public void modelActivated(ModelActivatedEvent event) {
        if (groupByDirectType) {
            if (!Widgets.isNullOrDisposedViewer(treeViewer)) {
                tree.setItemCount(0);
            }
        }
        else {
            if (!Widgets.isNullOrDisposedViewer(tableViewer)) {
                table.setItemCount(0);
            }
        }
        refreshWithChangedModelInformation();
    }

    private void executeModelChangedJob() {
        Viewer currentViewer = (groupByDirectType) ? treeViewer : tableViewer;
        if (Widgets.isNullOrDisposedViewer(currentViewer) || (currentModel == null)
                || (selectedResource == null)) {
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
        modelChangedJob.schedule(getModelProvider(), selectedResource);
        modelChangedJob.addJobDoneListener(new ModelJobListener() {
            @Override
            public void done(final InstancesViewModel newViewModel) {
                Display.getDefault().asyncExec(new Runnable() {
                    @Override
                    public void run() {
                        Viewer currentViewer = (groupByDirectType) ? treeViewer : tableViewer;
                        if (Widgets.isNullOrDisposedViewer(currentViewer) || (currentModel == null)) {
                            modelChangedJob = null;
                            modelChangedJobQueued = false;
                            return;
                        }

                        try {
                            synchronized (mutex) {

                                InstancesViewModel oldViewModel = viewModel;
                                viewModel = newViewModel;

                                logger.trace("ModelChangedJob will adjust the viewer");

                                if (groupByDirectType) {
                                    logger.trace("Determining the delta for the new viewModel");

                                    DeltaInstancesViewModel relationsRemoved = oldViewModel
                                            .difference(newViewModel);
                                    DeltaInstancesViewModel relationsAdded = newViewModel
                                            .difference(oldViewModel);

                                    logger.trace("Determining the delta for the new viewModel has finished");

                                    Set<Resource> resourcesToUpdate = Sets.newHashSet();

                                    // Removed direct types
                                    for (OntClass directType : relationsRemoved.getDirectTypes()) {
                                        contentProvider.removeDirectType(directType);
                                    }

                                    // Added direct types
                                    for (OntClass directType : relationsAdded.getDirectTypes()) {
                                        contentProvider.addDirectType(directType);
                                    }

                                    int newDirectTypeCount = newViewModel.getDirectTypeCount();
                                    for (int i = 0; i < newDirectTypeCount; i++) {
                                        OntClass directType = newViewModel.getDirectType(i);
                                        boolean countModified = false;

                                        // Removed instances
                                        for (Resource instance : relationsRemoved
                                                .getDirectInstances(directType)) {
                                            contentProvider.removeInstance(instance, directType);
                                            countModified = true;
                                        }

                                        // Added instances
                                        for (Resource instance : relationsAdded
                                                .getDirectInstances(directType)) {
                                            String currentFilter = (showFilter) ? filter : null;
                                            if (Strings.isNullOrEmpty(currentFilter)
                                                    || newViewModel.getText(instance).toLowerCase()
                                                            .contains(filter.toLowerCase())) {
                                                contentProvider.addInstance(instance, directType);
                                                countModified = true;
                                            }
                                        }

                                        // Update direct type as its instance
                                        // count is off
                                        if (countModified) {
                                            resourcesToUpdate.add(directType);
                                        }
                                    }

                                    // Altered texts
                                    Set<Resource> resourcesWithPropertyAltered = relationsRemoved
                                            .listSubjectsWithProperty(
                                                    InstancesViewModel.Vocabulary.text).toSet();
                                    Set<Resource> resourcesWithPropertyAdded = relationsAdded
                                            .listSubjectsWithProperty(
                                                    InstancesViewModel.Vocabulary.text).toSet();
                                    resourcesWithPropertyAltered
                                            .retainAll(resourcesWithPropertyAdded);
                                    resourcesToUpdate.addAll(resourcesWithPropertyAltered);

                                    contentProvider.updateResources(resourcesToUpdate);
                                }
                                else if (!Widgets.isNullOrDisposedViewer(tableViewer)) {
                                    /*
                                     * For a fast, lazy TableViewer, it's best
                                     * to simply reset the input. The current
                                     * selection should not be attempted to be
                                     * carried over to the new input, as those
                                     * resources probably no longer exist at the
                                     * same indices.
                                     */
                                    tableViewer.getTable().deselectAll();
                                    tableViewer.setInput(currentModel);
                                    tableViewer.setItemCount(contentProvider.getItemCount());
                                }

                                if (!Widgets.isNullOrDisposed(overviewLabel)) {
                                    overviewLabel.setText(getOverviewLabelText());
                                }

                                logger.trace("ModelChangedJob has adjusted the viewer");
                            }
                        }
                        catch (Exception e) {
                            modelChangedJob = null;
                            modelChangedJobQueued = false;
                            executeRefreshJob();
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
        clear();
        synchronized (mutex) {
            labelProvider = getLabelProvider();
            currentModel = getOntModel();
        }

        restoreStateVariables();

        if (hasModelProvider()) {
            createDropRegion();
            layoutParent(true, true);
            if (selectedResource != null && selectedResource.listProperties().hasNext()) {
                executeRefreshJob();
            }
        }
    }

    private void restoreStateVariables() {
        restoreLinkWithRDFSClassesView();
        restoreLinkWithOWLClassesView();
        restoreGroupByDirectType();
        restoreFilter();
        restoreShowFilter();
        restoreSelectedResource();
    }

    private void restoreLinkWithRDFSClassesView() {
        Object object = getStateParameter(PARAM_LINK_WITH_RDFS_CLASSES_VIEW);
        Boolean paramValue = null;
        if (object != null && object instanceof Boolean) {
            paramValue = (Boolean) object;
        }
        if (paramValue != null) {
            linkWithRDFSClassesView = paramValue.booleanValue();
            Commands.refreshElements(ToggleLinkWithRDFSClassesViewSelectionHandler.ID);
        }
        setStateParameter(PARAM_LINK_WITH_RDFS_CLASSES_VIEW, new Boolean(linkWithRDFSClassesView));
    }

    private void restoreLinkWithOWLClassesView() {
        Object object = getStateParameter(PARAM_LINK_WITH_OWL_CLASSES_VIEW);
        Boolean paramValue = null;
        if (object != null && object instanceof Boolean) {
            paramValue = (Boolean) object;
        }
        if (paramValue != null) {
            linkWithOWLClassesView = paramValue.booleanValue();
            Commands.refreshElements(ToggleLinkWithOWLClassesViewSelectionHandler.ID);
        }
        setStateParameter(PARAM_LINK_WITH_OWL_CLASSES_VIEW, new Boolean(linkWithOWLClassesView));
    }

    private void restoreGroupByDirectType() {
        Object object = getStateParameter(PARAM_GROUP_BY_DIRECT_TYPE);
        Boolean paramValue = null;
        if (object != null && object instanceof Boolean) {
            paramValue = (Boolean) object;
        }
        if (paramValue != null) {
            groupByDirectType = paramValue.booleanValue();
            Commands.refreshElements(ToggleGroupByDirectTypeHandler.ID);
        }
        setStateParameter(PARAM_GROUP_BY_DIRECT_TYPE, new Boolean(groupByDirectType));
    }

    private void restoreFilter() {
        Object object = getStateParameter(PARAM_FILTER);
        String paramValue = null;
        if (object != null && object instanceof String) {
            paramValue = (String) object;
        }
        filter = paramValue;
    }

    private void restoreShowFilter() {
        Object object = getStateParameter(PARAM_SHOW_FILTER);
        Boolean paramValue = null;
        if (object != null && object instanceof Boolean) {
            paramValue = (Boolean) object;
        }
        if (paramValue != null) {
            showFilter = paramValue.booleanValue();
        }
        else {
            showFilter = false;
        }
        Commands.refreshElements(ToggleShowInstancesFilterHandler.ID);
        setStateParameter(PARAM_SHOW_FILTER, new Boolean(showFilter));
    }

    private void restoreSelectedResource() {
        Object object = getStateParameter(PARAM_SELECTED_RESOURCE);
        Resource paramValue = null;

        if (object instanceof Resource) {
            paramValue = (Resource) object;
        }
        setInput(paramValue);
    }

    private void clear() {
        Widgets.disposeIfExists(searchControl);
        Widgets.disposeIfExists(searchComposite);
        Widgets.disposeIfExists(table);
        Widgets.disposeIfExists(tree);
        Widgets.disposeIfExists(overviewComposite);
        Widgets.disposeIfExists(dropComposite);
    }

    private void executeRefreshJob() {
        Job.getJobManager().cancel(InstancesView.this);
        modelChangedJob = null;

        // Ignore if the parent composite has been disposed
        if (Widgets.isNullOrDisposed(getParent())) {
            return;
        }

        clear();

        if (selectedResource == null) {
            createDropRegion();
            layoutParent(true, true);
            return;
        }
        createFilter();
        createInstancesViewer();
        createDropRegion();

        Viewer currentViewer = (groupByDirectType) ? treeViewer : tableViewer;
        if (!Widgets.isNullOrDisposedViewer(currentViewer)) {
            currentViewer.setInput(ImmutableList.of(new PendingElement(null, "Searching...")));
        }
        layoutParent(true, true);

        refreshJob = new CreateModelViewJob();
        refreshJob.setRule(OntologyFormEditor.MUTEX_RULE);
        refreshJob.setUser(false);
        refreshJob.schedule(getModelProvider(), selectedResource);
        refreshJob.addJobDoneListener(new ModelJobListener() {
            @Override
            public void done(final InstancesViewModel newViewModel) {
                Display.getDefault().asyncExec(new Runnable() {
                    @Override
                    public void run() {
                        if (Widgets.isNullOrDisposed(getParent()) || !hasModelProvider()) {
                            return;
                        }

                        viewModel = newViewModel;

                        clear();
                        createFilter();
                        createOverview();
                        createInstancesViewer();
                        refreshInstancesViewer();
                        createDropRegion();
                        createContextMenu();
                        layoutParent(true, true);

                        refreshJob = null;
                        if (modelChangedJobQueued) {
                            executeModelChangedJob();
                        }
                    }
                });
            }
        });
    }

    private void refreshInstancesViewer() {
        if (groupByDirectType) {
            refreshTreeViewer();
        }
        else {
            refreshTableViewer();
        }
    }

    private void refreshTreeViewer() {
        if (Widgets.isNullOrDisposedViewer(treeViewer)) {
            return;
        }
        treeViewer.setInput(currentModel);
        if (currentModel != null && viewModel != null) {
            treeViewer.getTree().setItemCount(contentProvider.getItemCount());
            if (showFilter && !Strings.isNullOrEmpty(filter) && filter.length() >= 2) {
                treeViewer.expandToLevel(2);
            }
        }
    }

    private void refreshTableViewer() {
        if (Widgets.isNullOrDisposedViewer(tableViewer)) {
            return;
        }
        tableViewer.setInput(currentModel);
        if (currentModel != null && viewModel != null) {
            tableViewer.getTable().setItemCount(contentProvider.getItemCount());
        }
    }

    private void collapseAll() {
        if (!Widgets.isNullOrDisposedViewer(treeViewer)) {
            treeViewer.collapseAll();
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        String property = event.getProperty();
        if (property.equals(LabelsPreference.PREFERENCE_ALWAYS_SHOW_ONTOLOGY_URI)
                || property.equals(LanguagesPreference.PREFERENCE_DISPLAY_LANGUAGES)
                || property.equals(LabelsPreference.PREFERENCE_RESOURCE_LABEL_RENDERING)
                || property
                        .equals(InstancesViewPreferenceConstants.PREFERENCE_SHOW_DIRECT_INSTANCES)) {
            executeRefreshJob();
        }
    }

    public static class ToggleShowDirectInstancesHandler extends PreferenceStoreToggleHandler {
        public static final String ID = "com.semmtech.plugin.semmweb.editor.commands.toggleShowDirectInstances";

        public ToggleShowDirectInstancesHandler() {
            super(InstancesViewPreference.getPreferenceStore(),
                    InstancesViewPreferenceConstants.PREFERENCE_SHOW_DIRECT_INSTANCES);
        }
    }

    public static class ToggleGroupByDirectTypeHandler extends AbstractHandler implements
            IElementUpdater {
        public static final String ID = "com.semmtech.plugin.semmweb.editor.commands.groupByDirectType";

        @Override
        public Object execute(ExecutionEvent event) throws ExecutionException {
            if (singleton != null) {
                groupByDirectType = !groupByDirectType;
                singleton.setStateParameter(PARAM_GROUP_BY_DIRECT_TYPE, new Boolean(
                        groupByDirectType));
                singleton.clear();
                if (singleton.currentModel != null) {
                    if (singleton.selectedResource != null) {
                        singleton.createFilter();
                        singleton.createOverview();
                        singleton.createInstancesViewer();
                        singleton.refreshInstancesViewer();
                        singleton.createContextMenu();
                    }
                    singleton.createDropRegion();
                    singleton.layoutParent(true, true);
                }
                InstancesViewSourceProvider.setGroupByType(
                        HandlerUtil.getActiveWorkbenchWindow(event), groupByDirectType);
            }
            return null;
        }

        @Override
        public void updateElement(UIElement element, @SuppressWarnings("rawtypes") Map parameters) {
            if (singleton != null) {
                element.setChecked(groupByDirectType);
            }
        }
    }

    public static class ToggleShowInstancesFilterHandler extends AbstractHandler implements
            IElementUpdater {
        public static final String ID = "com.semmtech.plugin.semmweb.editor.commands.toggleShowInstancesFilter";

        @Override
        public Object execute(ExecutionEvent event) throws ExecutionException {
            if (singleton != null) {
                showFilter = !showFilter;
                singleton.setStateParameter(PARAM_SHOW_FILTER, new Boolean(showFilter));
                singleton.executeRefreshJob();
            }
            return null;
        }

        @Override
        public void updateElement(UIElement element, @SuppressWarnings("rawtypes") Map parameters) {
            if (singleton != null) {
                element.setChecked(showFilter);
            }
        }
    }

    public static class ClearAllInstancesHandler extends AbstractHandler {
        public static final String ID = "com.semmtech.plugin.semmweb.editor.commands.clearAllInstances";

        @Override
        public Object execute(ExecutionEvent event) throws ExecutionException {
            if (singleton != null) {
                singleton.setInput(null);
                singleton.refreshWithChangedModelInformation();
            }
            return null;
        }
    }

    public void adjustViewToResourceSelection(ISelection selection) {
        if (!Selections.hasFirstOfType(selection, Resource.class)) {
            return;
        }
        Resource selected = Selections.retrieveFirstAsType(selection, Resource.class);
        boolean changed = false;
        if (selectedResource == null) {
            changed = true;
        }
        else {
            if (!selectedResource.isAnon() && !selected.isAnon()) {
                changed = !selectedResource.getURI().equals(selected.getURI());
            }
            else if (selectedResource.isAnon() && selected.isAnon()) {
                changed = !selectedResource.getId().equals(selected.getId());
            }
            else {
                changed = true;
            }
        }
        if (changed) {
            setInput(selected);
            refreshWithChangedModelInformation();
        }
    }

    public static class ToggleLinkWithRDFSClassesViewSelectionHandler extends AbstractHandler
            implements IElementUpdater {
        public static final String ID = "com.semmtech.plugin.semmweb.editor.commands.toggleLinkWithRDFSClassesViewSelection";

        @Override
        public Object execute(ExecutionEvent event) throws ExecutionException {
            if (singleton != null) {
                linkWithRDFSClassesView = !linkWithRDFSClassesView;
                singleton.setStateParameter(PARAM_LINK_WITH_RDFS_CLASSES_VIEW, new Boolean(
                        linkWithRDFSClassesView));
                if (linkWithRDFSClassesView) {
                    ISelection selection = singleton.getSite().getWorkbenchWindow()
                            .getSelectionService().getSelection(RDFSClassesView.ID);
                    singleton.adjustViewToResourceSelection(selection);
                }
            }
            return null;
        }

        @Override
        public void updateElement(UIElement element, @SuppressWarnings("rawtypes") Map parameters) {
            if (singleton != null) {
                element.setChecked(linkWithRDFSClassesView);
            }
        }
    }

    public static class ToggleLinkWithOWLClassesViewSelectionHandler extends AbstractHandler
            implements IElementUpdater {
        public static final String ID = "com.semmtech.plugin.semmweb.editor.commands.toggleLinkWithOWLClassesViewSelection";

        @Override
        public Object execute(ExecutionEvent event) throws ExecutionException {
            if (singleton != null) {
                linkWithOWLClassesView = !linkWithOWLClassesView;
                singleton.setStateParameter(PARAM_LINK_WITH_OWL_CLASSES_VIEW, new Boolean(
                        linkWithOWLClassesView));
                if (linkWithOWLClassesView) {
                    ISelection selection = singleton.getSite().getWorkbenchWindow()
                            .getSelectionService().getSelection(OWLClassesView.ID);
                    singleton.adjustViewToResourceSelection(selection);
                }
            }
            return null;
        }

        @Override
        public void updateElement(UIElement element, @SuppressWarnings("rawtypes") Map parameters) {
            if (singleton != null) {
                element.setChecked(linkWithOWLClassesView);
            }
        }
    }

    private interface ModelJobListener {
        public void done(final InstancesViewModel newViewModel);
    }

    private class CreateModelViewJob extends JobWithMonitor {
        private IModelProvider modelProvider;
        private Resource resource;
        private final List<ModelJobListener> listeners;

        public CreateModelViewJob() {
            super("Refreshing Instances");
            listeners = Lists.newArrayList();
        }

        public void schedule(IModelProvider modelProvider, Resource selectedResource) {
            this.modelProvider = modelProvider;
            this.resource = selectedResource;
            schedule();
        }

        public void addJobDoneListener(ModelJobListener listener) {
            if (!listeners.contains(listener)) {
                listeners.add(listener);
            }
        }

        @Override
        public boolean belongsTo(Object family) {
            return family.equals(InstancesView.this);
        }

        @Override
        protected IStatus run(IProgressMonitor monitor) {
            InstancesViewModel result = null;

            if (monitor.isCanceled()) {
                return Status.CANCEL_STATUS;
            }

            try {
                startMonitorUpdate(monitor, "Refreshing Instances view", 1);

                OntModel currentModel = modelProvider.getOntModel();
                if (currentModel != null) {
                    logger.trace("CreateModelViewJob will create the new viewModel");
                    result = InstancesViewModel.create(currentModel, resource);
                    logger.trace("CreateModelViewJob has created the new viewModel");
                }

                addWorked(1);
            }
            finally {
                monitor.done();
                stopMonitorUpdate();

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
        selectedResource = null;
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
