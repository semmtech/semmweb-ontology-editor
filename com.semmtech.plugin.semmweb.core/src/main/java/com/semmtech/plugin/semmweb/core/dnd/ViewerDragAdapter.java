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

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Tree;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.RDFNode;


/**
 * 
 * @author Sander Stolk
 */
public abstract class ViewerDragAdapter extends DragSourceAdapter {
    protected int columnIndex = -1;

    abstract protected StructuredViewer getViewer();

    /**
     * Retrieves the node to be dragged according on the column. Be aware that
     * this function can is called with a column index of -1 when filler space
     * next to the true content columns is selected (useful for cases in which
     * the selected row should allow dragging on any given location).
     */
    abstract protected RDFNode getSelectedNode(int column);

    abstract protected OntModel getOntModel();

    protected ISelection getSelection() {
        return getViewer().getSelection();
    }

    @Override
    public void dragStart(DragSourceEvent event) {
        columnIndex = getSelectedColumn(event);
        RDFNode selectedResource = getSelectedNode(columnIndex);
        if (selectedResource == null) {
            event.doit = false;
        }
        List<Transfer> transferAgents = DndUtils.getTransferTypes(selectedResource, getOntModel());
        DragSource dragSource = DndUtils.getDragSource(getViewer());
        if (dragSource != null) {
            dragSource.setTransfer(transferAgents.toArray(new Transfer[] {}));
        }
    }

    @Override
    public void dragSetData(DragSourceEvent event) {
        RDFNode selectedResource = getSelectedNode(columnIndex);
        DragSource dragSource = DndUtils.getDragSource(getViewer());
        if (selectedResource != null && dragSource != null) {
            List<Transfer> transferAgents = Arrays.asList(dragSource.getTransfer());
            DndUtils.setNodeAsEventData(event, transferAgents, getOntModel(), selectedResource);
        }
    }

    /**
     * Calculates the selected column by means of the functions
     * getViewerColumnCount and getViewerColumnWidth.
     */
    protected int getSelectedColumn(DragSourceEvent event) {
        int x = event.x;
        int offset = 0;
        int index = -1;

        int columnCount = getViewerColumnCount();
        for (int i = 0; i < columnCount; i++) {
            int columnWidth = getViewerColumnWidth(i);
            if (x < offset + columnWidth) {
                index = i;
                break;
            }
            offset += columnWidth;
        }
        return (index < columnCount) ? index : (columnCount - 1);
    }

    /**
     * Can retrieve the column count for an underlying Tree or Table SWT
     * control. To add support for another SWT control, override this function.
     */
    protected int getViewerColumnCount() {
        StructuredViewer viewer = getViewer();
        if (viewer != null) {
            Control control = viewer.getControl();
            if (control instanceof Tree) {
                return ((Tree) control).getColumnCount();
            }
            if (control instanceof Table) {
                return ((Table) control).getColumnCount();
            }
        }
        return 0;
    }

    /**
     * Can retrieve the column width for an underlying Tree or Table SWT
     * control. To add support for another SWT control, override this function.
     */
    protected int getViewerColumnWidth(int column) {
        StructuredViewer viewer = getViewer();
        if (viewer != null) {
            Control control = viewer.getControl();
            if (control instanceof Tree) {
                return ((Tree) control).getColumn(column).getWidth();
            }
            if (control instanceof Table) {
                return ((Table) control).getColumn(column).getWidth();
            }
        }
        return 0;
    }
}
