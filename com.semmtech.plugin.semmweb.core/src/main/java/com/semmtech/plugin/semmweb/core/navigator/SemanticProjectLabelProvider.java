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


import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

import com.google.common.base.Strings;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.CorePluginImages;
import com.semmtech.plugin.semmweb.core.preferences.DocumentManagerPreference;
import com.semmtech.plugin.semmweb.core.preferences.LabelsPreference;
import com.semmtech.plugin.semmweb.core.util.SemanticProjectUtils;
import com.semmtech.plugin.semmweb.core.util.URIs;


public class SemanticProjectLabelProvider implements ILabelProvider {

    @Override
    public void addListener(ILabelProviderListener listener) {

    }

    @Override
    public void dispose() {

    }

    @Override
    public boolean isLabelProperty(Object element, String property) {
        return false;
    }

    @Override
    public void removeListener(ILabelProviderListener listener) {

    }

    @Override
    public Image getImage(Object element) {
        if (element instanceof IModelCollection) {
            return CorePlugin.getDefault().getImage(CorePluginImages.IMG_MODEL_FOLDER);
        }
        else if (element instanceof IModel) {
            if (((IModel) element).isLocal()) {
                return CorePlugin.getDefault().getImage(CorePluginImages.IMG_MODEL_LOCAL);
            }
            return CorePlugin.getDefault().getImage(CorePluginImages.IMG_MODEL_WEB);
        }
        else if (element instanceof IImportCollection) {
            return CorePlugin.getDefault().getImage(CorePluginImages.IMG_IMPORTS_FOLDER);
        }
        else if (element instanceof INamespaceCollection) {
            return CorePlugin.getDefault().getImage(CorePluginImages.IMG_FOLDER);
        }
        else if (element instanceof IImport) {
            IImport impord = (IImport) element;

            if (impord.isDisabled()) {
                if (impord.isExternal()) {
                    return CorePlugin.getDefault().getImage(
                            CorePluginImages.IMG_ONTOLOGY_WEB_DISABLED);
                }
                else if (impord.isCached()) {
                    return CorePlugin.getDefault().getImage(
                            CorePluginImages.IMG_ONTOLOGY_CACHE_DISABLED);
                }
                else {
                    return CorePlugin.getDefault().getImage(
                            CorePluginImages.IMG_ONTOLOGY_FILE_DISABLED);
                }
            }

            if (impord.isExternal()) {
                return CorePlugin.getDefault().getImage(CorePluginImages.IMG_ONTOLOGY_WEB);
            }
            else if (impord.isCached()) {
                return CorePlugin.getDefault().getImage(CorePluginImages.IMG_ONTOLOGY_CACHE);
            }
            else {
                return CorePlugin.getDefault().getImage(CorePluginImages.IMG_ONTOLOGY_FILE);
            }
        }
        else if (element instanceof INamespace) {
            return CorePlugin.getDefault().getImage(CorePluginImages.IMG_ONTOLOGY);
        }

        return null;
    }

    @Override
    public String getText(Object element) {
        if (element instanceof IModelCollection) {
            return "Models";
        }
        else if (element instanceof IModel) {
            IModel model = (IModel) element;
            String url = model.getLocationURL();
            return getModelLabel(model, url);
        }
        else if (element instanceof IImportCollection) {
            return "Imports";
        }
        else if (element instanceof INamespaceCollection) {
            return "Namespaces";
        }
        else if (element instanceof IImport) {
            IImport immport = (IImport) element;
            IProject project = immport.getProject();
            String pubUri = immport.getURI();
            String altUrl = DocumentManagerPreference.fromProject(project).getAltURL(pubUri, true);

            if (pubUri.equals(altUrl) || immport.isCached()) {
                return pubUri;
            }

            if (URIs.hasFileScheme(altUrl)) {
                IPath fullPath = URIs.createPath(altUrl);

                ISemanticProject semanticProject = immport.getSemanticProject();
                if (semanticProject != null && semanticProject.getProject() != null) {
                    for (IModel model : SemanticProjectUtils
                            .getModels(semanticProject.getProject())) {
                        if (model.hasWorkingCopy()) {
                            model = (IModel) model.getWorkingCopy();
                        }
                        if (model != null) {
                            IResource modelResource = model.getResource();
                            if (modelResource != null
                                    && modelResource.getLocation().toString()
                                            .equals(fullPath.toString())) {
                                return getModelLabel(model, pubUri);
                            }
                        }
                    }
                }
            }
            return pubUri;
        }
        else if (element instanceof INamespace) {
            INamespace namespace = (INamespace) element;
            if (namespace.getPrefix() == null) {
                return String.format("<%s>", namespace.getURI());
            }
            return String.format("%s: <%s>", namespace.getPrefix(), namespace.getURI());

        }
        return null;
    }

    protected String getModelLabel(IModel model, String url) {
        String modelName = null;
        if (model != null) {
            if (LabelsPreference.showReadableLabels()) {
                modelName = model.getName();
            }
            if (Strings.isNullOrEmpty(modelName)) {
                modelName = model.getResource().getName();
            }
        }
        return Strings.isNullOrEmpty(modelName) ? url : modelName;
    }
}
