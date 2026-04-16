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


import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import com.semmtech.plugin.semmweb.core.forms.editor.OntologyFormEditor;
import com.semmtech.plugin.semmweb.core.navigator.IResourceElement;
import com.semmtech.plugin.semmweb.core.navigator.ISemanticElement;
import com.semmtech.plugin.semmweb.core.util.ResourcesUtil;


public class WorkspaceResourcesFilter extends ViewerFilter {

    private boolean showHidden;
    private boolean showClosedProjects;
    private boolean showSemanticElements;
    private String[] extensions;
    private int allowedTypes;

    public WorkspaceResourcesFilter() {
        this(new String[0], false);
    }

    public WorkspaceResourcesFilter(boolean showHidden) {
        this(new String[0], showHidden);
    }

    public WorkspaceResourcesFilter(String[] extensions, boolean showHidden) {
        this(IResource.FILE | IResource.FOLDER | IResource.PROJECT, extensions, showHidden, false,
                true);
    }

    public WorkspaceResourcesFilter(int allowedTypes, String[] extensions, boolean showHidden,
            boolean showClosedProjects, boolean showSemanticElements) {
        this.showHidden = showHidden;
        this.showClosedProjects = showClosedProjects;
        this.showSemanticElements = showSemanticElements;
        this.extensions = extensions;
        this.allowedTypes = allowedTypes;
    }

    public void setShowHidden(boolean show) {
        this.showHidden = show;
    }

    public void setExtensions(String[] extensions) {
        this.extensions = extensions;
    }

    @Override
    public boolean select(Viewer viewer, Object parentElement, Object element) {
        if (element instanceof IProject) {
            IProject project = (IProject) element;
            if (!showClosedProjects && !project.isOpen()) {
                return false;
            }
            return true;
        }

        if (element instanceof ISemanticElement) {
            if (!showSemanticElements) {
                return false;
            }
            ISemanticElement semanticElement = (ISemanticElement) element;
            if (semanticElement.getElementType() < ISemanticElement.MODEL) {
                if (extensions != null) {
                    // The ModelCollection is relevant only when at least one of
                    // the extensions is a model file types.
                    boolean isRelevant = false;
                    for (String extension : extensions) {
                        extension = StringUtils.removeStart(extension, "*.");
                        if (extension.equals("*")) {
                            isRelevant = true;
                        }
                        if (ResourcesUtil.extensionSupportedByEditor(extension,
                                OntologyFormEditor.ID)) {
                            isRelevant = true;
                        }
                    }
                    if (!isRelevant) {
                        return false;
                    }
                }
                return ((allowedTypes & IResource.FOLDER) != 0);
            }
        }
        if (element instanceof IResourceElement) {
            element = ((IResourceElement) element).getResource();
        }
        if (element instanceof IResource) {
            IResource resource = (IResource) element;
            if ((resource.getType() & allowedTypes) == 0) {
                return false;
            }
        }

        if (element instanceof IFolder) {
            IFolder folder = (IFolder) element;
            if (!showHidden && folder.getName().startsWith(".")) {
                return false;
            }
            return true;
        }
        else if (element instanceof IFile) {
            IFile file = (IFile) element;
            String extension = file.getFileExtension();
            String filename = file.getName();
            if (!showHidden && filename.startsWith(".")) {
                return false;
            }
            if (extension != null && extensions != null) {
                for (String ext : extensions) {
                    if (extension.equalsIgnoreCase(ext.replace("*.", ""))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
