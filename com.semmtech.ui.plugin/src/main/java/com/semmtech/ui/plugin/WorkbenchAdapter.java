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

package com.semmtech.ui.plugin;


import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.model.IWorkbenchAdapter2;
import org.eclipse.ui.model.IWorkbenchAdapter3;


public abstract class WorkbenchAdapter implements IWorkbenchAdapter, IWorkbenchAdapter2,
        IWorkbenchAdapter3 {

    @Override
    public StyledString getStyledText(Object element) {
        return null;
    }

    @Override
    public RGB getForeground(Object element) {
        return null;
    }

    @Override
    public RGB getBackground(Object element) {
        return null;
    }

    @Override
    public FontData getFont(Object element) {
        return null;
    }

    @Override
    public Object[] getChildren(Object o) {
        return null;
    }

    @Override
    public ImageDescriptor getImageDescriptor(Object object) {
        return null;
    }

    @Override
    public String getLabel(Object o) {
        return null;
    }

    @Override
    public Object getParent(Object o) {
        return null;
    }
}
