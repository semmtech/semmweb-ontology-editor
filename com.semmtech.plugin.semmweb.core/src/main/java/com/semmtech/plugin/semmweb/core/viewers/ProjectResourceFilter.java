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


import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.Viewer;


/**
 * Filter the projects and exclude from the view the one passed as parameter
 * 
 * @author Simone Rondelli
 */
public class ProjectResourceFilter extends WorkspaceResourcesFilter {

    private IProject excludedProject;

    public ProjectResourceFilter() {
        this(null);
    }

    public ProjectResourceFilter(IProject excludedProject) {
        super(IResource.PROJECT, null, false, false, true);
        this.excludedProject = excludedProject;
    }

    @Override
    public boolean select(Viewer viewer, Object parentElement, Object element) {
        if (element.equals(excludedProject)) {
            return false;
        }
        return super.select(viewer, parentElement, element);
    }
}