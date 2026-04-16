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


import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
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
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;

import com.google.common.collect.Lists;
import com.hp.hpl.jena.rdf.model.Resource;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.CorePluginImages;
import com.semmtech.plugin.semmweb.core.dnd.DndUtils;
import com.semmtech.plugin.semmweb.core.dnd.ResourceArrayListTransfer;
import com.semmtech.plugin.semmweb.core.dnd.ResourceTransfer;
import com.semmtech.plugin.semmweb.core.model.ResourceArrayList;
import com.semmtech.plugin.semmweb.core.model.events.ModelActivatedEvent;
import com.semmtech.plugin.semmweb.core.model.events.ModelChangedEvent;
import com.semmtech.plugin.semmweb.core.model.events.NamespacePrefixChangedEvent;
import com.semmtech.plugin.semmweb.core.model.events.SubModelAddedEvent;
import com.semmtech.plugin.semmweb.core.model.events.SubModelRemovedEvent;
import com.semmtech.plugin.semmweb.core.views.AbstractModelListenerView;
import com.semmtech.ui.plugin.util.Selections;
import com.semmtech.ui.plugin.widgets.Widgets;


public class ClipboardView extends AbstractModelListenerView {
    public static final String ID = "favorites_3.view";
    private LabelProvider rdfLabelProvider;
    private List<Resource> resourcelist;
    private TableViewer viewer;
    private DropTargetListener treeDropListener;

    private static ClipboardView singleton;

    public final static String PARAM_ROOTS = "var:roots";
    public final static String PARAM_SORT_ABC = "var:sortABC";
    public final static String PARAM_FILTER_PROPERTIES = "var:filterProperties";
    public final static String PARAM_NAVIGATION_DIRECTION = "var:navigationDirection";

    public ClipboardView() {
        resourcelist = Lists.newArrayList();
        singleton = this;

    }

    /**
     * The content provider class is responsible for providing objects to the
     * view. It can wrap existing objects in adapters or simply return objects
     * as-is. These objects may be sensitive to the current input of the view,
     * or ignore it and always show the same content (like Task List, for
     * example).
     */
    class ViewContentProvider implements IStructuredContentProvider {
        public void inputChanged(Viewer v, Object oldInput, Object newInput) {
        }

        public void dispose() {
        }

        public Object[] getElements(Object parent) {

            return resourcelist.toArray();
        }
    }

    class ViewLabelProvider extends LabelProvider implements ITableLabelProvider {

        public String getColumnText(Object obj, int index) {
            if (rdfLabelProvider != null) {
                return rdfLabelProvider.getText(obj);
            }
            return "<" + getText(obj) + ">";
        }

        public Image getColumnImage(Object obj, int index) {

            if (rdfLabelProvider != null) {
                return rdfLabelProvider.getImage(obj);
            }
            return CorePlugin.getDefault().getImage(CorePluginImages.IMG_RDF_RESOURCE);
        }
    }

    /**
     * This is a callback that will allow us to create the viewer and initialize
     * it.
     */

    public void createPartControl(Composite parent) {
        super.createPartControl(parent);

        treeDropListener = new FavoritesDropTargetListener();
        viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
        viewer.setContentProvider(new ViewContentProvider());
        viewer.setLabelProvider(new ViewLabelProvider());
        // Provide the input to the ContentProvider
        viewer.setInput(resourcelist);
        addListeners();
        addDragSupport();
        createContextMenu();
        // addFilterBar();
        int ops = DND.DROP_COPY | DND.DROP_MOVE;
        DropTarget dropTarget = new DropTarget(viewer.getControl(), ops);

        dropTarget.setTransfer(new Transfer[] { ResourceArrayListTransfer.getInstance(),
                ResourceTransfer.getInstance() });

        dropTarget.addDropListener(treeDropListener);
        setInitialized(true);
        createContextMenu();
    }

    @Override
    public void modelActivated(ModelActivatedEvent event) {
        refresh();
    }

    @Override
    public void modelChanged(ModelChangedEvent event) {
        refresh();
    }

    @Override
    public void subModelAdded(SubModelAddedEvent event) {
        refresh();
    }

    @Override
    public void subModelRemoved(SubModelRemovedEvent event) {
        refresh();
    }

    @Override
    public void namespacePrefixChanged(NamespacePrefixChangedEvent event) {
        refresh();
    }

    private void refresh() {
        // System.out.println("modelchange fired");
        rdfLabelProvider = getLabelProvider();

        viewer.refresh();
    }

    // filter bar
    // private void addFilterBar() {
    // Text searchBar = new Text(getParent(), SWT.SEARCH);
    //
    // }
    //
    // public void createContainer() {
    //
    // if (Widgets.isNullOrDisposed(getParent())) {
    // return;
    // }
    // else if (Widgets.isNullOrDisposed(searchComposite)) {
    // searchComposite = new Composite(getParent(), SWT.NONE);
    // GridData layoutData = new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1);
    // searchComposite.setLayoutData(layoutData);
    // GridLayout layout = new GridLayout(1, false);
    // searchComposite.setLayout(layout);
    //
    // Composite inputComposite = new Composite(searchComposite, SWT.BORDER);
    // layoutData = new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1);
    // inputComposite.setLayoutData(layoutData);
    // layout = new GridLayout(1, false);
    // layout.marginBottom = 1;
    // layout.marginTop = 1;
    // layout.marginWidth = 0;
    // layout.marginHeight = 0;
    // inputComposite.setLayout(layout);
    // inputComposite.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
    //
    // }
    // }

    private void addDragSupport() {
        if (Widgets.isNullOrDisposedViewer(viewer)) {
            return;
        }
        DndUtils.removeDragSupport(viewer);
        final DragSource dragSource = new DragSource(viewer.getTable(), DND.DROP_COPY
                | DND.DROP_MOVE);

        dragSource.addDragListener(new DragSourceAdapter() {
            @Override
            public void dragSetData(DragSourceEvent event) {
                DragSource dragSource = DndUtils.getDragSource(viewer);
                if (dragSource != null) {

                    DndUtils.setDragSetData(event, dragSource.getTransfer(), getOntModel(),
                            viewer.getSelection());
                }
            }
        });
        final ISelectionChangedListener listener = new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {

                final List<Transfer> transferTypes = DndUtils.getTransferTypes(
                        (IStructuredSelection) viewer.getSelection(), getOntModel());
                dragSource.setTransfer(transferTypes.toArray(new Transfer[] {}));
            }
        };
        viewer.setData(DndUtils.DND_SELECTION_CHANGED_LISTENER, listener);
        viewer.addSelectionChangedListener(listener);
    }

    private void addresource(Resource resource) {
        if (!resourcelist.contains(resource)) {
            resourcelist.add(resource);

        }
        else {
            System.out.println("this resource is already on the Clipboard");

        }
    }

    private final class FavoritesDropTargetListener extends DropTargetAdapter {
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

            // this list keeps all of our properties that we have in this thingy
            if (ResourceArrayListTransfer.getInstance().isSupportedType(event.currentDataType)) {
                ResourceArrayList list = (ResourceArrayList) event.data;
                for (Resource resource : list) {
                    addresource(resource);
                }
            }
            else if (ResourceTransfer.getInstance().isSupportedType(event.currentDataType)) {
                Resource resource = (Resource) ResourceTransfer.getInstance().nativeToJava(
                        event.currentDataType);
                // System.out.println(resource);
                addresource(resource);

            }
            viewer.refresh();
        }

    }

    // context menu
    private void createContextMenu() {
        MenuManager menuManager = new MenuManager();
        menuManager.setRemoveAllWhenShown(true);
        menuManager.addMenuListener(new IMenuListener() {

            @Override
            public void menuAboutToShow(IMenuManager manager) {

                IAction removeAction = new Action() {
                    @Override
                    public void run() {
                        resourcelist.removeAll(Selections.toStructured(viewer.getSelection())
                                .toList());
                        refresh();
                    }

                    @Override
                    public String getText() {
                        return String.format("Remove");
                    }

                };
                IAction unselectAction = new Action() {
                    @Override
                    public void run() {
                        viewer.setSelection(StructuredSelection.EMPTY);
                    }

                    @Override
                    public String getText() {
                        return String.format("Deselect");
                    }

                };
                if (singleton != null && singleton.viewer != null) {
                    IStructuredSelection selection = (IStructuredSelection) singleton.viewer
                            .getSelection();
                    if (!selection.isEmpty()) {
                        manager.add(removeAction);
                        manager.add(unselectAction);
                    }
                }

            }
        });

        Menu menu = menuManager.createContextMenu(viewer.getControl());
        viewer.getControl().setMenu(menu);
        getSite().registerContextMenu(menuManager, viewer);

    }

    private void addListeners() {
        // keyboard and mouseclicks

        viewer.getTable().addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {

                // TODO Auto-generated method stub
                if (e.keyCode == SWT.DEL || e.keyCode == SWT.BS) {
                    resourcelist.removeAll(Selections.toStructured(viewer.getSelection()).toList());
                    refresh();
                }
            }

        });

        viewer.getTable().addMouseListener(new MouseAdapter() {

            @Override
            public void mouseDoubleClick(MouseEvent e) {
                // TODO Auto-generated method stub
                viewer.setSelection(StructuredSelection.EMPTY);
            }

        });

    }

    private boolean getABC() {
        Boolean value = (Boolean) getStateParameter(PARAM_SORT_ABC);
        return (value == null) ? false : value.booleanValue();
    }

    private void setABC(boolean showFilter) {
        setStateParameter(PARAM_SORT_ABC, showFilter);
    }

    // definitions the remove buttons
    public static class ToggleFavoritesAlphabeticalHandler extends AbstractHandler {
        public static final String ID = "com.semmtech.plugin.semmweb.editor.commands.toggleFavoritesAlphabetical";

        @Override
        public Object execute(ExecutionEvent event) throws ExecutionException {
            if (singleton != null) {
                singleton.setABC(!singleton.getABC());

                if (singleton.getABC()) {
                    singleton.viewer.setSorter(new ViewerSorter());
                }
                else {
                    singleton.viewer.setSorter(null);
                }

                singleton.refresh();
            }
            return null;
        }
    }

    public static class ClearFavoritesViewHandler extends AbstractHandler {
        public static final String ID = "com.semmtech.plugin.semmweb.editor.commands.clearFavoritesView";

        @Override
        public Object execute(ExecutionEvent event) throws ExecutionException {
            singleton.resourcelist.clear();
            singleton.refresh();
            return null;
        }
    }

    public static class RemoveSelectedFavoriteHandler extends AbstractHandler {
        public static final String ID = "com.semmtech.plugin.semmweb.editor.commands.removeSelectedFavorite";

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
                List<Resource> instanceresourcelist = singleton.resourcelist;
                int size = instanceresourcelist.size();
                for (Object selected : selection.toList()) {
                    instanceresourcelist.remove(selected);

                }
                if (size != instanceresourcelist.size()) {

                    singleton.refresh();
                }
            }
            return null;
        }

    }

    @Override
    public void setFocus() {
        // TODO Auto-generated method stub
    }

    @Override
    protected void cleanup() {
        // TODO Auto-generated method stub
    }

}
