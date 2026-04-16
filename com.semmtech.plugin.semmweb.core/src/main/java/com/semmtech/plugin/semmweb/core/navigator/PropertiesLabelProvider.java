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

package com.semmtech.plugin.semmweb.core.navigator;


import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.navigator.IDescriptionProvider;


public class PropertiesLabelProvider extends LabelProvider implements IDescriptionProvider {

    public Image getImage(Object element) {
        if (element instanceof PropertiesTreeData)
            return PlatformUI.getWorkbench().getSharedImages()
                    .getImage(ISharedImages.IMG_OBJS_INFO_TSK);
        return null;
    }

    public String getText(Object element) {
        if (element instanceof PropertiesTreeData) {
            PropertiesTreeData data = (PropertiesTreeData) element;
            return data.getName() + " = " + data.getValue(); //$NON-NLS-1$
        }
        return null;
    }

    public String getDescription(Object anElement) {
        if (anElement instanceof PropertiesTreeData) {
            PropertiesTreeData data = (PropertiesTreeData) anElement;
            return "Property: " + data.getName(); //$NON-NLS-1$
        }
        return null;
    }

}
