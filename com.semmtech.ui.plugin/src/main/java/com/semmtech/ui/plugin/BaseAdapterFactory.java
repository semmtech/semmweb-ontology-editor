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


import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.model.IWorkbenchAdapter3;

import com.semmtech.ui.plugin.viewers.PendingElement;


/**
 * A factory for creating BaseAdapter objects.
 */
public final class BaseAdapterFactory implements IAdapterFactory {

    /** The pending element adapter. */
    private final IWorkbenchAdapter pendingElementAdapter = new IWorkbenchAdapter() {

        @Override
        public Object getParent(Object o) {
            return ((PendingElement) o).getParent();
        }

        @Override
        public String getLabel(Object o) {
            return ((PendingElement) o).getText();
        }

        @Override
        public ImageDescriptor getImageDescriptor(Object object) {
            return BasePlugin.getDefault().getImageDescriptor(BasePluginImages.IMG_PENDING);
        }

        @Override
        public Object[] getChildren(Object o) {
            return null;
        }
    };

    /** The pending element adapter3. */
    private final IWorkbenchAdapter3 pendingElementAdapter3 = new IWorkbenchAdapter3() {

        @Override
        public StyledString getStyledText(Object element) {
            String label = pendingElementAdapter.getLabel(element);
            StyledString styledText = new StyledString();
            if (label != null && label.length() > 0) {
                styledText.append(label);
            }
            return styledText;
        }
    };

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.core.runtime.IAdapterFactory#getAdapter(java.lang.Object,
     * java.lang.Class)
     */
    @SuppressWarnings("rawtypes")
    @Override
    public Object getAdapter(Object adaptableObject, Class adapterType) {
        if (adaptableObject instanceof PendingElement && adapterType == IWorkbenchAdapter.class) {
            return pendingElementAdapter;
        }
        else if (adaptableObject instanceof PendingElement
                && adapterType == IWorkbenchAdapter3.class) {
            return pendingElementAdapter3;
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.core.runtime.IAdapterFactory#getAdapterList()
     */
    @SuppressWarnings("rawtypes")
    @Override
    public Class[] getAdapterList() {
        return new Class[] { IWorkbenchAdapter.class };
    }

}
