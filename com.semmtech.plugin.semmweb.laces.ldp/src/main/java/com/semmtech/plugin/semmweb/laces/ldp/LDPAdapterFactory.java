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

package com.semmtech.plugin.semmweb.laces.ldp;


import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.model.IWorkbenchAdapter;

import com.semmtech.plugin.semmweb.laces.ldp.model.LDPGroup;
import com.semmtech.plugin.semmweb.laces.ldp.model.LDPPublication;
import com.semmtech.plugin.semmweb.laces.ldp.model.LDPRepository;
import com.semmtech.plugin.semmweb.laces.ldp.model.LDPServer;
import com.semmtech.ui.plugin.WorkbenchAdapter;


public class LDPAdapterFactory implements IAdapterFactory {
    private IWorkbenchAdapter serverAdapter = new WorkbenchAdapter() {
        @Override
        public String getLabel(Object o) {
            return ((LDPServer) o).getServerUrl();
        }

        @Override
        public ImageDescriptor getImageDescriptor(Object object) {
            return LDPPlugin.getDefault().getImageDescriptor(LDPPluginImages.IMG_LDP_SERVER);
        }
    };

    private IWorkbenchAdapter groupAdapter = new WorkbenchAdapter() {
        @Override
        public String getLabel(Object o) {
            return o.toString();
        }

        @Override
        public ImageDescriptor getImageDescriptor(Object object) {
            return LDPPlugin.getDefault().getImageDescriptor(LDPPluginImages.IMG_LDP_GROUP);
        }
    };

    private IWorkbenchAdapter repositoryAdapter = new WorkbenchAdapter() {
        @Override
        public String getLabel(Object o) {
            return o.toString();
        }

        @Override
        public ImageDescriptor getImageDescriptor(Object object) {
            return LDPPlugin.getDefault().getImageDescriptor(LDPPluginImages.IMG_LDP_REPOSITORY);
        }
    };

    private IWorkbenchAdapter publicationAdapter = new WorkbenchAdapter() {
        @Override
        public String getLabel(Object o) {
            return o.toString();
        }

        @Override
        public ImageDescriptor getImageDescriptor(Object object) {
            return LDPPlugin.getDefault().getImageDescriptor(LDPPluginImages.IMG_LDP_PUBLICATION);
        }
    };

    @SuppressWarnings("rawtypes")
    @Override
    public Object getAdapter(Object adaptableObject, Class adapterType) {
        if (adapterType == IWorkbenchAdapter.class && adaptableObject instanceof LDPServer) {
            return serverAdapter;
        }
        else if (adapterType == IWorkbenchAdapter.class && adaptableObject instanceof LDPGroup) {
            return groupAdapter;
        }
        else if (adapterType == IWorkbenchAdapter.class && adaptableObject instanceof LDPRepository) {
            return repositoryAdapter;
        }
        else if (adapterType == IWorkbenchAdapter.class
                && adaptableObject instanceof LDPPublication) {
            return publicationAdapter;
        }
        return null;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Class[] getAdapterList() {
        return new Class[] { IWorkbenchAdapter.class };
    }
}
