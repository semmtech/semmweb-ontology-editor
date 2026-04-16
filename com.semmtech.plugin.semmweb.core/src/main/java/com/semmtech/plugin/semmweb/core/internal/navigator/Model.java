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


import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;

import com.google.common.base.Strings;
import com.semmtech.plugin.semmweb.core.navigator.IModel;
import com.semmtech.plugin.semmweb.core.navigator.IResourceElement;
import com.semmtech.plugin.semmweb.core.preferences.ModelsFolderPreference;


public class Model extends SemanticElement implements IModel {

    private String name;
    private String url;
    private long localTimeStamp;
    private Model workingCopy;
    private Model persistedCopy;

    public Model(SemanticElement parent) {
        super(parent);
        localTimeStamp = IResource.NULL_STAMP;
    }

    @Override
    public String getId() {
        if (getResource() == null) {
            return new String();
        }
        return getResource().getFullPath().toString();
    }

    @Override
    public boolean isLocal() {
        if (!Strings.isNullOrEmpty(url)) {
            return !url.startsWith("http:");
        }
        return false;
    }

    @Override
    public String getLocationURL() {
        return url;
    }

    public void setLocationURL(String url) {
        this.url = url;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int getElementType() {
        return MODEL;
    }

    @Override
    public IResource getResource() {
        IProject project = getProject();
        if (project != null) {
            String modelsPath = ModelsFolderPreference.fromProject(project).getModelsFolderPath();
            IFile file = (IFile) project.findMember(modelsPath + getLocationURL());
            return file;
        }
        return null;
    }

    @Override
    public boolean hasWorkingCopy() {
        return (workingCopy != null);
    }

    @Override
    public IResourceElement getWorkingCopy() {
        return workingCopy;
    }

    public void setWorkingCopy(Model model) {
        workingCopy = model;
        if (model != null) {
            model.persistedCopy = this;
        }
    }

    @Override
    public boolean isWorkingCopy() {
        return (persistedCopy != null);
    }

    @Override
    public IResourceElement getPersistedCopy() {
        return persistedCopy;
    }

    protected void setPersistedCopy(Model model) {
        persistedCopy = model;
        if (model != null) {
            model.workingCopy = this;
        }
    }

    public long getLocalTimeStamp() {
        return localTimeStamp;
    }

    public void setLocalTimeStamp(long stamp) {
        localTimeStamp = stamp;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Object getAdapter(Class adapter) {
        if (adapter == IResource.class) {
            return getResource();
        }
        return super.getAdapter(adapter);
    }

}
