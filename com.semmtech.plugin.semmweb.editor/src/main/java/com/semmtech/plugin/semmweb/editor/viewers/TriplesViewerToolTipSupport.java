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

package com.semmtech.plugin.semmweb.editor.viewers;


import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.semmtech.plugin.semmweb.core.model.IModelProvider;
import com.semmtech.plugin.semmweb.core.viewers.ResourceToolTipContent;


public class TriplesViewerToolTipSupport extends ColumnViewerToolTipSupport {
    private IModelProvider modelProvider;

    protected TriplesViewerToolTipSupport(ColumnViewer viewer, int style,
            IModelProvider modelProvider, boolean manualActivation) {
        super(viewer, style, manualActivation);
        this.modelProvider = modelProvider;
    }

    @Override
    protected Composite createViewerToolTipContentArea(Event event, ViewerCell cell,
            Composite parent) {
        Object element = cell.getElement();
        int columnIndex = cell.getColumnIndex();
        if (modelProvider != null && element instanceof Statement) {
            Statement statement = (Statement) element;
            if (columnIndex == 0) {
                Resource subject = statement.getSubject();
                return new ResourceToolTipContent(parent, modelProvider, subject, SWT.NONE);
            }
            else if (columnIndex == 1) {
                Property predicate = statement.getPredicate();
                return new ResourceToolTipContent(parent, modelProvider, predicate, SWT.NONE);
            }
            else if (columnIndex == 2) {
                RDFNode node = statement.getObject();
                if (node.isResource())
                    return new ResourceToolTipContent(parent, modelProvider, (Resource) node,
                            SWT.NONE);
            }
        }
        return null;
    }

    public static final void enableFor(final ColumnViewer viewer) {
        enableFor(viewer, null);
    }

    @SuppressWarnings("unused")
    public static final void enableFor(final ColumnViewer viewer, IModelProvider modelProvider) {
        new TriplesViewerToolTipSupport(viewer, ToolTip.RECREATE, modelProvider, false);
    }
}
