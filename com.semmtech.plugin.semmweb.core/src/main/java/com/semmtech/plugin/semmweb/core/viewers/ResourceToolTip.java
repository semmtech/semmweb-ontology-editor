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


import org.eclipse.jface.window.DefaultToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;

import com.hp.hpl.jena.rdf.model.Resource;
import com.semmtech.plugin.semmweb.core.model.IModelProvider;


public class ResourceToolTip extends DefaultToolTip {
    protected Resource resource;
    protected IModelProvider modelProvider;

    public ResourceToolTip(Control control) {
        super(control);
    }

    public void setModelProvider(IModelProvider modelProvider) {
        this.modelProvider = modelProvider;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    @Override
    protected Composite createToolTipContentArea(Event event, Composite parent) {
        if (resource != null) {
            if (modelProvider != null) {
                return new ResourceToolTipContent(parent, modelProvider, resource, SWT.NONE);
            }
            return new ResourceToolTipContent(parent, resource, SWT.NONE);
        }
        return null;
    }
}
