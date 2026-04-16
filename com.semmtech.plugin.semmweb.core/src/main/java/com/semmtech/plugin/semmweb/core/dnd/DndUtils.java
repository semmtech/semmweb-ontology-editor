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

package com.semmtech.plugin.semmweb.core.dnd;


import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Widget;

import com.google.common.collect.Lists;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.semmtech.plugin.semmweb.core.model.PropertyArrayList;
import com.semmtech.plugin.semmweb.core.model.ResourceArrayList;
import com.semmtech.plugin.semmweb.core.navigator.ISemanticElement;
import com.semmtech.ui.plugin.util.Selections;
import com.semmtech.ui.plugin.widgets.Widgets;


/**
 * 
 * @author Sander Stolk
 */
public class DndUtils {
    public static final String DND_MOUSE_LISTENER = "DndUtils.DND_MOUSE_LISTENER";
    public static final String DND_SELECTION_CHANGED_LISTENER = "DndUtils.DND_SELECTION_CHANGED_LISTENER";

    public interface RDFNodeProvider {
        public RDFNode getRDFNode();
    }

    public interface ViewerSelectionConverter {
        /**
         * Takes a viewer's selection and returns the selection that contains
         * the selected resources/nodes.
         */
        public ISelection convert(ISelection selection);
    }

    /**
     * Retrieves the DragSource added to the viewer. If the viewer has no
     * DragSource, null is returned.
     */
    public static DragSource getDragSource(final Viewer viewer) {
        Control control = getViewerControl(viewer);
        return getDragSource(control);
    }

    /**
     * Retrieves the DropTarget added to the viewer. If the viewer has no
     * DropTarget, null is returned.
     */
    public static DropTarget getDropTarget(final Viewer viewer) {
        Control control = getViewerControl(viewer);
        return getDropTarget(control);
    }

    /**
     * Retrieves the control of the viewer (i.e. the Tree or the Table widget).
     * If the viewer is neither a TreeViewer or a TableViewer, or if its control
     * has been disposed, this function returns null.
     */
    private static Control getViewerControl(final Viewer viewer) {
        if (viewer instanceof TreeViewer) {
            TreeViewer treeViewer = (TreeViewer) viewer;
            return treeViewer.getTree();
        }
        if (viewer instanceof TableViewer) {
            TableViewer tableViewer = (TableViewer) viewer;
            return tableViewer.getTable();
        }
        return null;
    }

    /**
     * Retrieves the DragSource added to the control. If the control has no
     * DragSource, null is returned.
     */
    public static DragSource getDragSource(final Control control) {
        if (!Widgets.isNullOrDisposed(control)) {
            Object data = control.getData(DND.DRAG_SOURCE_KEY);
            if (data instanceof DragSource) {
                return (DragSource) data;
            }
        }
        return null;
    }

    /**
     * Retrieves the DropTarget added to the control. If the control has no
     * DropTarget, null is returned.
     */
    public static DropTarget getDropTarget(final Control control) {
        if (!Widgets.isNullOrDisposed(control)) {
            Object data = control.getData(DND.DROP_TARGET_KEY);
            if (data instanceof DropTarget) {
                return (DropTarget) data;
            }
        }
        return null;
    }

    /**
     * Adds drag support to the specified control with the specified resource as
     * the draggable data.
     */
    public static void addDragSupport(final Control control, final OntResource resource) {
        addDragSupport(control, resource.getOntModel(), resource);
    }

    /**
     * Adds drag support to the specified control with the specified node as the
     * draggable data.
     */
    public static void addDragSupport(final Control control, final OntModel model,
            final RDFNode node) {
        if (node == null) {
            return;
        }

        addDragSupport(control, model, new RDFNodeProvider() {
            @Override
            public RDFNode getRDFNode() {
                return node;
            }
        });
    }

    /**
     * Adds drag support to the specified viewer. The transfer agents of the new
     * DragSource for the viewer will be determined automatically whenever the
     * selection of the viewer changes.
     */
    public static void addDragSupport(final Viewer viewer, final OntModel model) {
        addDragSupport(viewer, null, model, null);
    }

    /**
     * Adds drag support to the specified viewer. If
     * <code>selectionConverter</code> is not null, that argument will be used
     * to convert a selection from the viewer to the desired selection of
     * resources/nodes. If <code>transferAgents</code> is null, the transfer
     * agents of the new DragSource for the viewer will be determined
     * automatically whenever the selection of the viewer changes. Otherwise,
     * the specified transfer agents will be set.
     */
    public static void addDragSupport(final Viewer viewer,
            final ViewerSelectionConverter selectionConverter, final OntModel model,
            Transfer[] transferAgents) {

        if (viewer == null || model == null) {
            return;
        }

        final Control control = getViewerControl(viewer);
        if (Widgets.isNullOrDisposed(control)) {
            return;
        }

        removeDragSupport(viewer);

        final DragSource dragSource = new DragSource(control, DND.DROP_COPY | DND.DROP_MOVE);
        dragSource.addDragListener(new DragSourceAdapter() {
            @Override
            public void dragSetData(DragSourceEvent event) {
                setDragSetData(event, model, viewer, selectionConverter);
            }
        });

        if (transferAgents != null) {
            dragSource.setTransfer(transferAgents);
        }
        else {
            final ISelectionChangedListener listener = new ISelectionChangedListener() {
                @Override
                public void selectionChanged(SelectionChangedEvent event) {
                    ISelection selection = viewer.getSelection();
                    if (selection != null && selectionConverter != null) {
                        selection = selectionConverter.convert(selection);
                    }
                    final List<Transfer> transferTypes = getTransferTypes(
                            Selections.toStructured(selection), model);
                    dragSource.setTransfer(transferTypes.toArray(new Transfer[] {}));
                }
            };
            viewer.setData(DND_SELECTION_CHANGED_LISTENER, listener);
            viewer.addSelectionChangedListener(listener);
        }
    }

    public static List<Transfer> getTransferTypes(IStructuredSelection selection, OntModel model) {
        final List<Transfer> transferTypes = Lists.newArrayList();

        if (selection != null) {
            if (selection.size() > 1) {
                if (Selections.hasAllOfType(selection, Property.class)) {
                    transferTypes.add(PropertyArrayListTransfer.getInstance());
                }
                if (Selections.hasAllOfType(selection, Resource.class)) {
                    transferTypes.add(ResourceArrayListTransfer.getInstance());
                }
            }

            RDFNode node = Selections.retrieveFirstAsType(selection, RDFNode.class);
            transferTypes.addAll(getTransferTypes(node, model));
        }

        return transferTypes;
    }

    /**
     * Adds drag support to the specified control with the node provided by
     * <code>nodeProvider</code> as the draggable data. The transfer agents of
     * the new DragSource for the control will be determined automatically
     * whenever a left mouse click occurs on the control.
     */
    public static void addDragSupport(final Control control, final OntModel model,
            final RDFNodeProvider nodeProvider) {
        addDragSupport(control, model, nodeProvider, null);
    }

    /**
     * Adds drag support to the specified control with the node provided by
     * <code>nodeProvider</code> as the draggable data. If
     * <code>transferAgents</code> is null, the transfer agents of the new
     * DragSource for the control will be determined automatically whenever a
     * left mouse click occurs on the control. Otherwise, the specified transfer
     * agents will be set.
     */
    public static void addDragSupport(final Control control, final OntModel model,
            final RDFNodeProvider nodeProvider, final Transfer[] transferAgents) {

        if (nodeProvider == null || model == null || Widgets.isNullOrDisposed(control)) {
            return;
        }

        removeDragSupport(control);

        final DragSource dragSource = new DragSource(control, DND.DROP_COPY | DND.DROP_MOVE);
        dragSource.addDragListener(new DragSourceAdapter() {
            @Override
            public void dragSetData(DragSourceEvent event) {
                setDragSetData(event, dragSource.getTransfer(), model, nodeProvider.getRDFNode());
            }
        });

        if (transferAgents != null) {
            dragSource.setTransfer(transferAgents);
        }
        else {
            final MouseListener listener = new MouseAdapter() {
                @Override
                public void mouseDown(MouseEvent e) {
                    // Set transfer types upon clicking the draggable control.
                    final RDFNode node = nodeProvider.getRDFNode();
                    final List<Transfer> transferTypes = getTransferTypes(node, model);
                    dragSource.setTransfer(transferTypes.toArray(new Transfer[] {}));
                }
            };
            control.setData(DND_MOUSE_LISTENER, listener);
            control.addMouseListener(listener);
        }
    }

    public static List<Transfer> getTransferTypes(final RDFNode node, final OntModel model) {
        final List<Transfer> transferTypes = Lists.newArrayList();

        if (node != null) {
            transferTypes.add(TextTransfer.getInstance());

            if (node.isLiteral()) {
                transferTypes.add(LiteralTransfer.getInstance());
            }
            else if (node.isResource()) {
                OntResource selected = model.getOntResource(node.asResource());

                transferTypes.add(ResourceTransfer.getInstance());
                if (selected.hasRDFType(RDFS.Class) || selected.hasRDFType(OWL.Class)) {
                    transferTypes.add(OntClassTransfer.getInstance());
                }
                if (selected.hasRDFType(RDF.Property)) {
                    transferTypes.add(PropertyTransfer.getInstance());
                }
            }
        }

        return transferTypes;
    }

    /** Argument selectionConverter can be null. */
    public static void setDragSetData(final DragSourceEvent event, final OntModel model,
            final Viewer viewer, final ViewerSelectionConverter selectionConverter) {
        DragSource dragSource = DndUtils.getDragSource(viewer);
        if (dragSource != null) {
            ISelection selection = viewer.getSelection();
            if (selection != null && selectionConverter != null) {
                selection = selectionConverter.convert(selection);
            }
            setDragSetData(event, dragSource.getTransfer(), model, selection);
        }
    }

    public static void setDragSetData(final DragSourceEvent event, final Transfer[] transferAgents,
            final OntModel model, ISelection selection) {

        if (event == null || transferAgents == null || model == null || selection == null) {
            if (event != null) {
                event.doit = false;
            }
            return;
        }

        final List<Transfer> transferTypes = Arrays.asList(transferAgents);
        final IStructuredSelection structuredSelection = Selections.toStructured(selection);
        if (structuredSelection == null || structuredSelection.isEmpty()) {
            event.doit = false;
            return;
        }

        if (setArrayListAsEventData(event, transferTypes, model, structuredSelection)) {
            return;
        }

        Object element = structuredSelection.getFirstElement();
        if (element instanceof RDFNode) {
            if (setNodeAsEventData(event, transferTypes, model, (RDFNode) element)) {
                return;
            }
        }

        event.doit = false;
    }

    public static void setDragSetData(final DragSourceEvent event, final Transfer[] transferAgents,
            final OntModel model, final RDFNode node) {

        if (event == null || transferAgents == null || model == null || node == null) {
            if (event != null) {
                event.doit = false;
            }
            return;
        }

        final List<Transfer> transferTypes = Arrays.asList(transferAgents);
        if (!setNodeAsEventData(event, transferTypes, model, node)) {
            event.doit = false;
        }
    }

    public static boolean setArrayListAsEventData(final DragSourceEvent event,
            final List<Transfer> transferTypes, final OntModel model, IStructuredSelection selection) {

        if (event == null || transferTypes == null || model == null || selection == null) {
            return false;
        }

        if (transferTypes.contains(PropertyArrayListTransfer.getInstance())
                && PropertyArrayListTransfer.getInstance().isSupportedType(event.dataType)
                && Selections.hasAllOfType(selection, Property.class)) {
            List<Property> list = Selections.retrieveAllAsType(selection, Property.class);
            PropertyArrayList propertyArrayList = new PropertyArrayList(list);
            event.data = propertyArrayList;
            return true;
        }
        if (transferTypes.contains(ResourceArrayListTransfer.getInstance())
                && ResourceArrayListTransfer.getInstance().isSupportedType(event.dataType)
                && Selections.hasAllOfType(selection, Resource.class)) {
            List<Resource> list = Selections.retrieveAllAsType(selection, Resource.class);
            ResourceArrayList resourceArrayList = new ResourceArrayList(list);
            event.data = resourceArrayList;
            return true;
        }

        return false;
    }

    public static boolean setNodeAsEventData(final DragSourceEvent event,
            final List<Transfer> transferTypes, final OntModel model, final RDFNode node) {

        if (event == null || transferTypes == null || model == null || node == null) {
            return false;
        }

        if (node.isLiteral()) {
            Literal selected = node.asLiteral();

            if (transferTypes.contains(LiteralTransfer.getInstance())
                    && LiteralTransfer.getInstance().isSupportedType(event.dataType)) {
                event.data = selected;
                return true;
            }
            if (transferTypes.contains(TextTransfer.getInstance())
                    && TextTransfer.getInstance().isSupportedType(event.dataType)) {
                event.data = selected.getLexicalForm();
                return true;
            }
        }
        else if (node.isResource()) {
            OntResource selected = model.getOntResource(node.asResource());

            if (transferTypes.contains(OntClassTransfer.getInstance())
                    && OntClassTransfer.getInstance().isSupportedType(event.dataType)) {
                event.data = selected.as(OntClass.class);
                return true;
            }
            if (transferTypes.contains(PropertyTransfer.getInstance())
                    && PropertyTransfer.getInstance().isSupportedType(event.dataType)) {
                event.data = selected.as(Property.class);
                return true;
            }
            if (transferTypes.contains(ResourceTransfer.getInstance())
                    && ResourceTransfer.getInstance().isSupportedType(event.dataType)) {
                event.data = selected;
                return true;
            }
            if (transferTypes.contains(TextTransfer.getInstance())
                    && TextTransfer.getInstance().isSupportedType(event.dataType)) {
                event.data = selected.isAnon() ? selected.getId() : selected.getURI();
                return true;
            }
        }

        return false;
    }

    /**
     * Removes the DragSource from the specified viewer. Listeners that have
     * been added to the viewer by either the DragSource or DndUtils will be
     * removed and disposed as well.
     */
    public static void removeDragSupport(final Viewer viewer) {
        if (viewer == null) {
            return;
        }

        Control control = getViewerControl(viewer);
        removeDragSupport(control);

        // Remove our own DND selection changed listener on the viewer
        Object data = viewer.getData(DND_SELECTION_CHANGED_LISTENER);
        if (data != null) {
            viewer.setData(DND_SELECTION_CHANGED_LISTENER, null);
            if (data instanceof ISelectionChangedListener) {
                ISelectionChangedListener listener = (ISelectionChangedListener) data;
                viewer.removeSelectionChangedListener(listener);
            }
        }
    }

    /**
     * Removes the DragSource from the specified control. Listeners that have
     * been added to the control by either the DragSource or DndUtils will be
     * removed and disposed as well.
     */
    public static void removeDragSupport(final Control control) {
        if (Widgets.isNullOrDisposed(control)) {
            return;
        }

        // Check if drag support exists on the control
        DragSource dragSource = getDragSource(control);
        if (dragSource == null) {
            return;
        }
        // Remove the drag support on the control
        control.setData(DND.DRAG_SOURCE_KEY, null);

        dragSource.setTransfer(new Transfer[] {});

        for (DragSourceListener listener : dragSource.getDragListeners()) {
            dragSource.removeDragListener(listener);
        }
        dragSource.setDragSourceEffect(null);
        dragSource.dispose();

        // Ensure listeners on the control are cleaned up as well
        removeListeners(control, SWT.Dispose, DragSource.class);
        removeListeners(control, SWT.DragDetect, DragSource.class);

        // Remove our own DND mouse listener on the control
        Object data = control.getData(DND_MOUSE_LISTENER);
        if (data != null) {
            control.setData(DND_MOUSE_LISTENER, null);
            if (data instanceof MouseListener) {
                MouseListener listener = (MouseListener) data;
                control.removeMouseListener(listener);
            }
        }
    }

    private static void removeListeners(Widget widget, int eventType, Class<?> listenersFromClass) {
        for (Listener listener : widget.getListeners(eventType)) {
            Class<?> listenerClass = listener.getClass();
            if (listenerClass.equals(listenersFromClass)) {
                widget.removeListener(eventType, listener);
            }
            Class<?> enclosingClass = listener.getClass().getEnclosingClass();
            if (enclosingClass != null && enclosingClass.equals(listenersFromClass)) {
                widget.removeListener(eventType, listener);
            }
        }
    }

    /**
     * Returns the currently dragged selection.
     * 
     * @return An ISelection or null if nothing is currently dragged.
     */
    public static ISelection getSelection() {
        LocalSelectionTransfer transfer = LocalSelectionTransfer.getTransfer();
        if (transfer == null) {
            return null;
        }
        return transfer.getSelection();
    }

    /**
     * Returns the selected ISemanticElement from the Drag&Drop operations
     * (LocalSelectionTransfer).
     * 
     * @return List of selected ISemanticElement or an empty list if nothing is
     *         selected
     */
    public static List<ISemanticElement> getDnDSelectedResources() {
        return Selections.retrieveAllAsType(getSelection(), ISemanticElement.class);
    }

}
