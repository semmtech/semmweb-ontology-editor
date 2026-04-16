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


import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import com.semmtech.plugin.semmweb.core.preferences.ModelsFolderPreference;


public class ModelsFolderFilter extends ViewerFilter {

    public ModelsFolderFilter() {

    }

    @Override
    public boolean select(Viewer viewer, Object parentElement, Object element) {
        if (element instanceof IFolder) {
            IFolder folder = (IFolder) element;
            IProject project = folder.getProject();
            String folderPath = folder.getFullPath().toString();
            String modelsFolder = ModelsFolderPreference.fromProject(project).getModelsFolderPath();
            while (modelsFolder.endsWith("/")) {
                modelsFolder = StringUtils.removeEnd(modelsFolder, "/");
            }
            modelsFolder = StringUtils.join("/", project.getName(), "/", modelsFolder);
            return !folderPath.equals(modelsFolder);
        }
        return true;
    }
}
