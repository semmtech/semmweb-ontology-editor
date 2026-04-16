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

package com.semmtech.plugin.semmweb.core.viewers;


import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;

import com.hp.hpl.jena.rdf.model.Resource;
import com.semmtech.plugin.semmweb.core.model.IModelProvider;


public class ResourceViewerToolTipSupport extends ColumnViewerToolTipSupport {

    private static Logger logger = Logger.getLogger(ResourceViewerToolTipSupport.class);

    private IModelProvider modelProvider;

    protected ResourceViewerToolTipSupport(ColumnViewer viewer, int style,
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
                Resource res = (Resource) element;

                if (res.getModel() == null) {
                    String msg = "Tried to create a tooltip for the resource %s which isn't associated to any Model";
                    msg = String.format(msg, res.getURI());
                    logger.warn(msg);
                }
                else {
                    return new ResourceToolTipContent(parent, modelProvider, (Resource) element,
                            SWT.NONE);
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
        new ResourceViewerToolTipSupport(viewer, ToolTip.RECREATE, modelProvider, false);
    }
}
