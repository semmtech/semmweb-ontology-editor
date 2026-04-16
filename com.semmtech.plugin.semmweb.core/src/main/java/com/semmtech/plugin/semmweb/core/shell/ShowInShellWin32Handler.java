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

package com.semmtech.plugin.semmweb.core.shell;


import java.io.File;
import java.net.URI;
import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.commands.IElementUpdater;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.menus.UIElement;

import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.CorePluginImages;


public class ShowInShellWin32Handler extends AbstractHandler implements IElementUpdater {

    @Override
    public final Object execute(ExecutionEvent event) throws ExecutionException {
        IStructuredSelection selection = (IStructuredSelection) HandlerUtil
                .getCurrentSelection(event);
        IResource resource = (IResource) Platform.getAdapterManager().getAdapter(
                selection.getFirstElement(), IResource.class);

        URI uri = resource.getLocationURI();
        if (resource.isLinked()) {
            uri = resource.getRawLocationURI();
        }

        ShellWin32Util.showInExplorer(uri, resource.getType());
        return null;
    }

    @Override
    public final void updateElement(UIElement element, @SuppressWarnings("rawtypes") Map parameters) {
        String label = getLabel();
        if (label != null)
            element.setText(label);

        ImageDescriptor iconDescriptor = getImageDescriptor();
        if (iconDescriptor != null)
            element.setIcon(iconDescriptor);
    }

    protected File ensureDirectory(File file) {
        if (file.isDirectory()) {
            return file;
        }
        return file.getParentFile();
    }

    protected String getLabel() {
        return "Show in Explorer";
    }

    protected ImageDescriptor getImageDescriptor() {
        return CorePlugin.getDefault().getImageDescriptor(CorePluginImages.IMG_SHOW_IN_EXPLORER);
    }

}
