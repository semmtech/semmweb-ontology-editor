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

package com.semmtech.plugin.semmweb.core.internal.navigator;


import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;

import com.google.common.base.Objects;
import com.semmtech.plugin.semmweb.core.navigator.IModelCollection;
import com.semmtech.plugin.semmweb.core.navigator.IResourceElement;
import com.semmtech.plugin.semmweb.core.navigator.ISemanticElement;
import com.semmtech.plugin.semmweb.core.preferences.ModelsFolderPreference;


public class ModelCollection extends SemanticElement implements IModelCollection {

    public ModelCollection(SemanticElement parent) {
        super(parent);
    }

    @Override
    public int getElementType() {
        return MODEL_COLLECTION;
    }

    @Override
    public String getId() {
        return getElementName();
    }

    @Override
    public String getElementName() {
        return "Models";
    }

    public void clear() {
        children.clear();
    }

    public ISemanticElement getChild(IResource resource) {
        for (ISemanticElement child : getChildren()) {
            if (child instanceof IResourceElement) {
                IResourceElement resourceElement = (IResourceElement) child;
                IResource otherRes = resourceElement.getResource();
                if (Objects.equal(otherRes, resource)) {
                    return child;
                }
            }
        }
        return null;
    }

    public void removeChild(ISemanticElement element) {
        children.remove(element);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Object getAdapter(Class adapter) {
        if (adapter == IResource.class || adapter == IContainer.class) {
            try {
                IProject project = getProject();
                ModelsFolderPreference prefs = ModelsFolderPreference.fromProject(project);
                String modelsFolder = prefs.getModelsFolderPath();
                IResource resource = project.findMember(modelsFolder);

                if (adapter == IResource.class) {
                    return resource;
                }
                else if (adapter == IContainer.class) {
                    return resource.getAdapter(IContainer.class);
                }
            }
            catch (Exception e) {
                // Failed to get a physical models folder
            }
        }
        return super.getAdapter(adapter);
    }
}
