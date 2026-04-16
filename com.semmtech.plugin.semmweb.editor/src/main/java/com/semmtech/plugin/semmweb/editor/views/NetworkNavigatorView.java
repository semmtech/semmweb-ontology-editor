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

package com.semmtech.plugin.semmweb.editor.views;


import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.State;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILazyTreeContentProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
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
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.commands.IElementUpdater;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.handlers.RadioState;
import org.eclipse.ui.menus.UIElement;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.CorePluginImages;
import com.semmtech.plugin.semmweb.core.commands.Commands;
import com.semmtech.plugin.semmweb.core.dialog.ResourceSelectionDialog;
import com.semmtech.plugin.semmweb.core.dnd.DndUtils;
import com.semmtech.plugin.semmweb.core.dnd.PropertyArrayListTransfer;
import com.semmtech.plugin.semmweb.core.dnd.PropertyTransfer;
import com.semmtech.plugin.semmweb.core.dnd.ResourceArrayListTransfer;
import com.semmtech.plugin.semmweb.core.dnd.ResourceTransfer;
import com.semmtech.plugin.semmweb.core.handlers.PreferenceStoreToggleHandler;
import com.semmtech.plugin.semmweb.core.model.IModelProvider;
import com.semmtech.plugin.semmweb.core.model.OpenResourceEventAdapter;
import com.semmtech.plugin.semmweb.core.model.OpenResourceEventListener;
import com.semmtech.plugin.semmweb.core.model.PropertyArrayList;
import com.semmtech.plugin.semmweb.core.model.ResourceArrayList;
import com.semmtech.plugin.semmweb.core.model.events.ModelActivatedEvent;
import com.semmtech.plugin.semmweb.core.model.events.ModelChangedEvent;
import com.semmtech.plugin.semmweb.core.model.events.NamespacePrefixChangedEvent;
import com.semmtech.plugin.semmweb.core.model.events.SubModelAddedEvent;
import com.semmtech.plugin.semmweb.core.model.events.SubModelRemovedEvent;
import com.semmtech.plugin.semmweb.core.preferences.LabelsPreference;
import com.semmtech.plugin.semmweb.core.preferences.LanguagesPreference;
import com.semmtech.plugin.semmweb.core.ui.IOpenResourcesProvider;
import com.semmtech.plugin.semmweb.core.viewers.ResourceToolTipContent;
import com.semmtech.plugin.semmweb.core.views.AbstractModelListenerView;
import com.semmtech.plugin.semmweb.core.widgets.DropComposite;
import com.semmtech.plugin.semmweb.core.widgets.trees.ResourceTreeData;
import com.semmtech.plugin.semmweb.core.widgets.trees.TreeData;
import com.semmtech.plugin.semmweb.editor.EditorPlugin;
import com.semmtech.plugin.semmweb.editor.EditorPluginImages;
import com.semmtech.plugin.semmweb.editor.preferences.NetworkNavigatorPreference;
import com.semmtech.ui.plugin.util.Selections;
import com.semmtech.ui.plugin.viewers.LazyTreeContentProvider;
import com.semmtech.ui.plugin.widgets.Widgets;


/**
 * 
 * @author Mike Henrichs
 * @author Sander Stolk
 */
public class NetworkNavigatorView extends AbstractModelListenerView implements
        IPropertyChangeListener {
    public static final String ID = "com.semmtech.plugin.semmweb.editor.views.networkNavigator";

    public final static String PARAM_ROOTS = "var:roots";
    public final static String PARAM_SHOW_FILTER = "var:showFilter";
    public final static String PARAM_FILTER_PROPERTIES = "var:filterProperties";
    public final static String PARAM_NAVIGATION_DIRECTION = "var:navigationDirection";

    private final static OpenResourceEventListener openResourceEventListener = new OpenResourceEventAdapter() {
        @Override
        public void resourceActivated(OntResource resource) {
            if (singleton != null && resource != null) {
                singleton.updateViewerWithResource();
            }
        }
    };

    private final class NavigatorTreeContentProvider extends LazyTreeContentProvider {
        private Map<Resource, List<Statement>> childrenMap;

        @Override
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
            childrenMap = Maps.newHashMap();
        }

        private List<Statement> findChildren(Resource resource) {
            if (!childrenMap.containsKey(resource)) {
                Map<Property, Property> inverses = Maps.newHashMap();
                List<Statement> children = Lists.newArrayList();

                // Retrieve inverse properties
                if (showInverses) {
                    for (Statement stmt : baseModel.listStatements(null, OWL.inverseOf,
                            (RDFNode) null).toSet()) {
                        if (!stmt.getObject().isResource()) {
                            continue;
                        }
                        Property left = stmt.getSubject().as(Property.class);
                        Property right = stmt.getResource().as(Property.class);
                        if (!inverses.containsKey(left)) {
                            inverses.put(left, right);
                        }
                        if (!inverses.containsKey(right)) {
                            inverses.put(right, left);
                        }
                    }
                }
                if (isNavigatingForwards()) {
                    for (Statement stmt : baseModel.listStatements(resource, null, (RDFNode) null)
                            .toSet()) {
                        if (getShowFilter() && !getFilterProperties().isEmpty()
                                && !getFilterProperties().contains(stmt.getPredicate())) {
                            continue;
                        }
                        children.add(stmt);
                    }
                    for (Statement stmt : baseModel.listStatements(null, null, resource).toSet()) {
                        Property property = stmt.getPredicate();
                        if (!inverses.containsKey(property)) {
                            continue;
                        }
                        Property inverse = inverses.get(property);
                        if (getShowFilter() && !getFilterProperties().isEmpty()
                                && !getFilterProperties().contains(inverse)) {
                            continue;
                        }
                        if (!baseModel.contains(stmt.getResource(), inverse, stmt.getSubject())) {
                            children.add(baseModel.createStatement(stmt.getResource(), inverse,
                                    stmt.getSubject()));
                        }
                    }
                }
                else if (isNavigatingBackwards()) {
                    for (Statement stmt : baseModel.listStatements(null, null, resource).toSet()) {
                        if (getShowFilter() && !getFilterProperties().isEmpty()
                                && !getFilterProperties().contains(stmt.getPredicate())) {
                            continue;
                        }
                        children.add(stmt);
                    }
                    for (Statement stmt : baseModel.listStatements(resource, null, (RDFNode) null)
                            .toSet()) {
                        Property property = stmt.getPredicate();
                        if (!inverses.containsKey(property)) {
                            continue;
                        }
                        Property inverse = inverses.get(property);
                        if (getShowFilter() && !getFilterProperties().isEmpty()
                                && !getFilterProperties().contains(inverse)) {
                            continue;
                        }
                        if (!baseModel.contains(stmt.getResource(), inverse, stmt.getSubject())) {
                            children.add(baseModel.createStatement(stmt.getResource(), inverse,
                                    stmt.getSubject()));
                        }
                    }
                }
                Collections.sort(children, new StatementComparator());
                childrenMap.put(resource, children);
            }
            return childrenMap.get(resource);
        }

        @Override
        public void updateElement(Object parent, int index) {
            if (parent == null) {
                return;
            }
            if (parent instanceof Model) {
                Resource root = getRoots().get(index);
                ResourceTreeData childElement = new ResourceTreeData(root);
                viewer.replace(parent, index, childElement);
                int childCount = findChildren(root).size();
                viewer.setChildCount(childElement, childCount);
                viewer.expandToLevel(1);
            }
            else if (parent instanceof TreeData) {
                TreeData parentElement = (TreeData) parent;
                RDFNode subject = null;
                if (parentElement.getData() instanceof Resource) {
                    subject = (Resource) parentElement.getData();
                }
                else if (parentElement.getData() instanceof Statement) {
                    if (isNavigatingForwards()) {
                        subject = ((Statement) parentElement.getData()).getObject();
                    }
                    else if (isNavigatingBackwards()) {
                        subject = ((Statement) parentElement.getData()).getSubject();
                    }
                }

                if (subject == null || !subject.isResource()) {
                    return;
                }

                Resource resource = subject.asResource();
                List<Statement> children = findChildren(resource);
                Statement child = children.get(index);

                // TreeData is required to prevent the lazy tree update to
                // update the wrong statement in the tree
                TreeData childElement = new TreeData(child.toString(), child);
                childElement.setParent(parentElement);
                viewer.replace(parent, index, childElement);
                int childCount = 0;
                if (isNavigatingForwards()) {
                    if (child.getObject().isResource()) {
                        childCount = findChildren(child.getObject().asResource()).size();
                    }
                }
                else if (isNavigatingBackwards()) {
                    if (child.getSubject().isResource()) {
                        childCount = findChildren(child.getSubject().asResource()).size();
                    }
                }
                viewer.setChildCount(childElement, childCount);
            }
        }
    }

    private final class NavigatorLabelProvider extends StyledCellLabelProvider {

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
                    if (treeData.getData() instanceof Statement) {
                        Statement stmt = (Statement) treeData.getData();
                        String predicate = rdfLabelProvider.getText(stmt.getPredicate());
                        if (isNavigatingForwards()) {
                            String object = rdfLabelProvider.getText(stmt.getObject());
                            styledText.append(String.format("%s: %s", predicate, object));
                            styledText.setStyle(0, predicate.length() + 1, predicateStyler);
                            image = rdfLabelProvider.getImage(stmt.getObject());
                        }
                        else if (isNavigatingBackwards()) {
                            String subject = rdfLabelProvider.getText(stmt.getSubject());
                            String object = rdfLabelProvider.getText(stmt.getObject());
                            styledText.append(String
                                    .format("%s %s: %s", subject, predicate, object));
                            styledText.setStyle(subject.length() + 1, predicate.length() + 1,
                                    predicateStyler);
                            image = rdfLabelProvider.getImage(stmt.getSubject());
                        }
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

    private final class NavigatorDropTargetListener extends DropTargetAdapter {
        @Override
        public void dragEnter(DropTargetEvent event) {
            for (int i = 0; i < event.dataTypes.length; i++) {
                if (ResourceArrayListTransfer.getInstance().isSupportedType(event.dataTypes[i])) {
                    event.currentDataType = event.dataTypes[i];
                    break;
                }
            }
        }

        @Override
        public void drop(DropTargetEvent event) {
            List<Resource> roots = getRoots();
            boolean dropChangedRoots = false;
            if (ResourceArrayListTransfer.getInstance().isSupportedType(event.currentDataType)) {
                ResourceArrayList list = (ResourceArrayList) event.data;
                if (event.detail == DND.DROP_COPY) {
                    for (Resource resource : list) {
                        if (!roots.contains(resource)) {
                            roots.add(resource);
                        }
                    }
                }
                else if (event.detail == DND.DROP_MOVE) {
                    roots = Lists.newArrayList(list);
                }
                dropChangedRoots = true;
            }
            else if (ResourceTransfer.getInstance().isSupportedType(event.currentDataType)) {
                Resource resource = (Resource) ResourceTransfer.getInstance().nativeToJava(
                        event.currentDataType);
                if (resource != null) {
                    if (event.detail == DND.DROP_COPY) {
                        if (!roots.contains(resource)) {
                            roots.add(resource);
                        }
                    }
                    else if (event.detail == DND.DROP_MOVE) {
                        roots = Lists.newArrayList(resource);
                    }
                    dropChangedRoots = true;
                }
            }
            setRoots(roots);

            if (dropChangedRoots) {
                boolean linkEnabled = Commands.getToggleState(ToggleLinkWithEditorHandler.ID);
                if (linkEnabled) {
                    Commands.execute(ToggleLinkWithEditorHandler.ID);
                }
                refresh();
            }
        }
    }

    private final class StatementComparator implements Comparator<Statement> {
        @Override
        public int compare(Statement s1, Statement s2) {
            if (!s1.getObject().isResource() && s2.getObject().isResource()) {
                return -1;
            }
            else if (s1.getObject().isResource() && !s2.getObject().isResource()) {
                return 1;
            }
            else {
                // Compare predicates
                String p1 = rdfLabelProvider.getText(s1.getPredicate());
                String p2 = rdfLabelProvider.getText(s2.getPredicate());

                if (p1 == null) {
                    p1 = "";
                }
                if (p2 == null) {
                    p2 = "";
                }

                int rc = p1.compareToIgnoreCase(p2);
                if (rc != 0) {
                    return rc;
                }

                // Compare objects
                String o1 = rdfLabelProvider.getText(s1.getObject());
                String o2 = rdfLabelProvider.getText(s2.getObject());

                if (o1 == null) {
                    o1 = "";
                }
                if (o2 == null) {
                    o2 = "";
                }

                rc = o1.compareToIgnoreCase(o2);
                if (rc != 0) {
                    return rc;
                }
            }
            return 0;
        }
    }

    private static final class NavigatorViewerToolTipSupport extends ColumnViewerToolTipSupport {
        private IModelProvider modelProvider;

        protected NavigatorViewerToolTipSupport(ColumnViewer viewer, int style,
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
            new NavigatorViewerToolTipSupport(viewer, ToolTip.RECREATE, modelProvider, false);
        }
    }

    private static boolean showInverses = true;

    private LabelProvider rdfLabelProvider;
    private Composite treeComposite;
    private TreeViewer viewer;
    private Tree tree;

    private IBaseLabelProvider treeLabelProvider;
    private ILazyTreeContentProvider treeContentProvider;
    private DropTargetListener treeDropListener;
    private IDoubleClickListener treeDoubleClickListener;

    private OntModel baseModel;
    private static NetworkNavigatorView singleton;
    private Composite filterComposite;

    private DropComposite dropComposite;

    public NetworkNavigatorView() {
        singleton = this;
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

        treeLabelProvider = new NavigatorLabelProvider();
        treeContentProvider = new NavigatorTreeContentProvider();
        treeDropListener = new NavigatorDropTargetListener();
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

                            if (isNavigatingForwards()) {
                                if (stmt.getObject().isResource()) {
                                    resource = stmt.getResource();
                                }
                            }
                            else if (isNavigatingBackwards()) {
                                resource = stmt.getSubject();
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

    protected void createNavigatorTree() {
        clearNavigatorTree();
        if (!Widgets.isNullOrDisposed(dropComposite)) {
            dropComposite.dispose();
        }

        treeComposite = new Composite(getParent(), SWT.NONE);
        GridLayout layout = new GridLayout(1, false);
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        treeComposite.setLayout(layout);
        treeComposite.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

        viewer = new TreeViewer(treeComposite, SWT.VIRTUAL | SWT.MULTI);
        viewer.setUseHashlookup(true);
        viewer.setLabelProvider(treeLabelProvider);
        viewer.setContentProvider(treeContentProvider);
        // int oprations = DND.DROP_COPY | DND.DROP_MOVE;
        // viewer.addDropSupport(oprations, new Transfer[] {
        // ResourceArrayListTransfer.getInstance(),
        // ResourceTransfer.getInstance() }, treeDropListener);
        viewer.addDoubleClickListener(treeDoubleClickListener);
        NavigatorViewerToolTipSupport.enableFor(viewer, getModelProvider());

        GridData layoutData = new GridData(GridData.FILL, GridData.FILL, true, true);
        tree = viewer.getTree();
        tree.setLayoutData(layoutData);
        createContextMenu();

    }

    private void createContextMenu() {
        MenuManager menuManager = new MenuManager();
        menuManager.setRemoveAllWhenShown(true);
        menuManager.addMenuListener(new IMenuListener() {

            @Override
            public void menuAboutToShow(IMenuManager manager) {
                IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
                TreeData item = Selections.retrieveFirstAsType(selection, TreeData.class);

                if (item == null || !(item.getData() instanceof Statement)) {
                    return;
                }

                Statement stmt = (Statement) item.getData();
                Resource resource = null;
                if (isNavigatingForwards()) {
                    if (stmt.getObject().isResource()) {
                        resource = stmt.getResource();
                    }
                }
                else if (isNavigatingBackwards()) {
                    resource = stmt.getSubject();
                }

                if (resource == null) {
                    return;
                }

                final Resource target = resource;
                IAction focusAction = new Action() {
                    @Override
                    public void run() {
                        setRoots(Lists.newArrayList(target));
                        refresh();
                    }

                    @Override
                    public String getText() {
                        return String.format("Focus on '%s'", rdfLabelProvider.getText(target));
                    }

                };
                manager.add(focusAction);

                manager.add(new Separator());

                IAction openAction = new Action() {
                    @Override
                    public void run() {
                        CorePlugin.getDefault().openResource(target);
                    }

                    @Override
                    public String getText() {
                        return String.format("Open '%s'", rdfLabelProvider.getText(target));
                    }

                };
                manager.add(openAction);
            }
        });

        Menu menu = menuManager.createContextMenu(viewer.getControl());
        viewer.getControl().setMenu(menu);
        getSite().registerContextMenu(menuManager, viewer);

    }

    protected void createPropertiesFilter() {
        if (!Widgets.isNullOrDisposed(filterComposite)) {
            filterComposite.dispose();
        }

        filterComposite = new Composite(getParent(), SWT.NONE);
        GridLayout layout = new GridLayout(4, false);
        layout.marginHeight = 0;
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
                            if (!properties.contains(property)) {
                                properties.add(property);
                            }
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
                            if (!properties.contains(property)) {
                                properties.add(property);
                            }
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
                dialog.setModel(baseModel);
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
        baseModel = getOntModel();

        Commands.refreshElements(ToggleShowPropertiesFilterHandler.ID);
        Commands.refreshElements(NavigatorDirectionHandler.ID);

        clear();
        if (hasModelProvider()) {
            IModelProvider provider = getModelProvider();
            if (provider instanceof IOpenResourcesProvider) {
                IOpenResourcesProvider openResourcesProvider = (IOpenResourcesProvider) provider;
                openResourcesProvider.addOpenResourceEventListener(openResourceEventListener);
            }
            refresh();
        }
    }

    private void clear() {
        clearNavigatorTree();
        if (!Widgets.isNullOrDisposed(filterComposite)) {
            filterComposite.dispose();
        }
        if (!Widgets.isNullOrDisposed(dropComposite)) {
            dropComposite.dispose();
        }
    }

    private void refresh() {
        boolean isEmpty = getRoots().isEmpty();
        if (!isEmpty) {
            if (getShowFilter()) {
                createPropertiesFilter();
            }
            createNavigatorTree();
            createDropRegion();
        }
        else {
            clearNavigatorTree();
            createDropRegion();
        }
        if (!isEmpty) {
            refreshViewer();
        }
        layoutParent(true, true);
    }

    private void clearNavigatorTree() {
        if (!Widgets.isNullOrDisposed(treeComposite)) {
            treeComposite.dispose();
        }
    }

    private void createDropRegion() {
        Widgets.disposeIfExists(dropComposite);
        if (Widgets.isNullOrDisposed(getParent())) {
            return;
        }

        boolean small = !Widgets.isNullOrDisposed(treeComposite);
        String instruction = (small) ? "Drop a resource onto this area"
                : "Drop a resource into this view";
        String tooltip = "Drop one or more resources onto this area to navigate through these resources.\nBy holding down the CTRL-key you can add additional resources to the list.";
        dropComposite = new DropComposite(getParent(), instruction, tooltip, small);
        dropComposite.setDefaultGridLayoutData(small);

        DropTarget dropTarget = new DropTarget(dropComposite, DND.DROP_MOVE | DND.DROP_COPY);
        dropTarget.setTransfer(new Transfer[] { ResourceArrayListTransfer.getInstance(),
                ResourceTransfer.getInstance() });
        dropTarget.addDropListener(treeDropListener);
    }

    private void refreshViewer() {
        boolean isEmpty = getRoots().isEmpty();
        if (baseModel != null && !isEmpty) {
            addDragSupport();
            if (!Widgets.isNullOrDisposedViewer(viewer)) {
                viewer.setInput(baseModel);
                tree.setItemCount(getRoots().size());
                viewer.expandToLevel(2);
            }
        }
    }

    private void addDragSupport() {
        if (Widgets.isNullOrDisposedViewer(viewer)) {
            return;
        }
        DndUtils.removeDragSupport(viewer);
        final DragSource dragSource = new DragSource(viewer.getTree(), DND.DROP_COPY
                | DND.DROP_MOVE);
        dragSource.addDragListener(new DragSourceAdapter() {
            @Override
            public void dragSetData(DragSourceEvent event) {
                DragSource dragSource = DndUtils.getDragSource(viewer);
                if (dragSource != null) {
                    StructuredSelection selectionToDrag = new StructuredSelection(
                            getResourcesToDrag());
                    DndUtils.setDragSetData(event, dragSource.getTransfer(), getOntModel(),
                            selectionToDrag);
                }
            }
        });
        final ISelectionChangedListener listener = new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                StructuredSelection selectionToDrag = new StructuredSelection(getResourcesToDrag());
                final List<Transfer> transferTypes = DndUtils.getTransferTypes(selectionToDrag,
                        getOntModel());
                dragSource.setTransfer(transferTypes.toArray(new Transfer[] {}));
            }
        };
        viewer.setData(DndUtils.DND_SELECTION_CHANGED_LISTENER, listener);
        viewer.addSelectionChangedListener(listener);
    }

    private List<Resource> getResourcesToDrag() {
        List<Resource> resourcesToDrag = Lists.newArrayList();
        if (viewer == null) {
            return resourcesToDrag;
        }

        ISelection selection = viewer.getSelection();
        List<TreeData> selectedTreeData = Selections.retrieveAllAsType(selection, TreeData.class);

        for (TreeData treeData : selectedTreeData) {
            if (treeData.getData() instanceof Resource) {
                resourcesToDrag.add((Resource) treeData.getData());
            }
            else if (treeData.getData() instanceof Statement) {
                Statement statement = (Statement) treeData.getData();
                if (isNavigatingForwards()) {
                    if (statement.getObject().isResource()) {
                        resourcesToDrag.add(statement.getObject().asResource());
                    }
                }
                else if (isNavigatingBackwards()) {
                    resourcesToDrag.add(statement.getSubject());
                }
            }
        }
        return resourcesToDrag;
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

    public static class ClearNetworkNavigatorHandler extends AbstractHandler {
        public static final String ID = "com.semmtech.plugin.semmweb.editor.commands.clearNetworkNavigator";

        @Override
        public Object execute(ExecutionEvent event) throws ExecutionException {
            if (singleton != null) {
                singleton.setRoots(null);
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
                List<Resource> roots = singleton.getRoots();
                int size = roots.size();
                for (Object selected : selection.toList()) {
                    if (selected instanceof ResourceTreeData) {
                        roots.remove(((ResourceTreeData) selected).getData());
                    }
                }
                if (size != roots.size()) {
                    singleton.setRoots(roots);
                    singleton.clear();
                    singleton.refresh();
                }
            }
            return null;
        }

    }

    public static class ToggleShowPropertiesFilterHandler extends AbstractHandler implements
            IElementUpdater {
        public static final String ID = "com.semmtech.plugin.semmweb.editor.commands.toggleShowPropertiesFilter";

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

    public static class NavigatorDirectionHandler extends AbstractHandler implements
            IElementUpdater {
        public static final String ID = "com.semmtech.plugin.semmweb.editor.commands.navigatorDirection";

        public static final String STATE_FORWARDS = "forwards";
        public static final String STATE_BACKWARDS = "backwards";

        @Override
        public Object execute(ExecutionEvent event) throws ExecutionException {
            if (HandlerUtil.matchesRadioState(event)) {
                return null;
            }

            String currentState = event.getParameter(RadioState.PARAMETER_ID);
            if (singleton != null) {
                singleton.setNavigationDirection(currentState);
            }
            HandlerUtil.updateRadioState(event.getCommand(), currentState);
            if (singleton != null) {
                singleton.refresh();
            }
            return null;
        }

        @Override
        public void updateElement(UIElement element, @SuppressWarnings("rawtypes") Map parameters) {
            if (singleton != null) {
                String currentState = singleton.getNavigationDirection();

                Command command = Commands.getCommand(ID);
                State state = command.getState(RadioState.STATE_ID);
                if (!currentState.equals(state)) {
                    state.setValue(currentState);
                }
            }
        }
    }

    public static class ToggleLinkWithEditorHandler extends PreferenceStoreToggleHandler {
        public static final String ID = "com.semmtech.plugin.semmweb.editor.commands.navigator.toggleLinkWithEditor";

        public ToggleLinkWithEditorHandler() {
            super(ID, EditorPlugin.getDefault().getPreferenceStore(),
                    NetworkNavigatorPreference.PREFERENCE_LINK_WITH_EDITOR);
        }

        @Override
        public Object execute(ExecutionEvent event) throws ExecutionException {
            Object result = super.execute(event);
            if (singleton != null) {
                singleton.updateViewerWithResource();
            }
            return result;
        }
    }

    @Override
    public void partActivated(IWorkbenchPart part) {
        updateViewerWithResource();
    }

    protected void updateViewerWithResource() {
        if (NetworkNavigatorPreference.isLinkedWithEditor()) {
            if (CorePlugin.getDefault().isOpenResourceActive()) {
                Resource resource = CorePlugin.getDefault().getActiveOpenResource();
                setRoots(Lists.newArrayList(resource));
            }
            refresh();
        }
    }

    @Override
    public void setFocus() {
        // TODO Auto-generated method stub
    }

    private void setRoots(List<Resource> roots) {
        setStateParameter(PARAM_ROOTS, roots);
    }

    private List<Resource> getRoots() {
        @SuppressWarnings("unchecked")
        List<Resource> value = (List<Resource>) getStateParameter(PARAM_ROOTS);
        if (value == null) {
            value = Lists.newArrayList();
        }
        return value;
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

    private void setNavigationDirection(String direction) {
        setStateParameter(PARAM_NAVIGATION_DIRECTION, direction);
    }

    private String getNavigationDirection() {
        String value = (String) getStateParameter(PARAM_NAVIGATION_DIRECTION);
        if (value == null || !value.equals(NavigatorDirectionHandler.STATE_BACKWARDS)) {
            value = NavigatorDirectionHandler.STATE_FORWARDS;
        }
        return value;
    }

    private boolean isNavigatingForwards() {
        return getNavigationDirection().equals(NavigatorDirectionHandler.STATE_FORWARDS);
    }

    private boolean isNavigatingBackwards() {
        return getNavigationDirection().equals(NavigatorDirectionHandler.STATE_BACKWARDS);
    }

    @Override
    protected void cleanup() {
        // TODO Auto-generated method stub
    }
}
