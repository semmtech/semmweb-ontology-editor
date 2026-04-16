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


import java.util.Collection;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;

import com.google.common.collect.Sets;
import com.semmtech.plugin.semmweb.core.cache.CacheManager;
import com.semmtech.plugin.semmweb.core.navigator.IImport;
import com.semmtech.plugin.semmweb.core.navigator.ImportType;
import com.semmtech.plugin.semmweb.core.preferences.DocumentManagerPreference;
import com.semmtech.plugin.semmweb.core.util.WorkspaceUtils;


public class Import extends SemanticElement implements IImport {

    private ImportType importType;
    private String uri;
    private boolean direct;
    private final Set<String> importedByOntologyURIs;

    public Import(SemanticElement parent) {
        super(parent);
        importedByOntologyURIs = Sets.newHashSet();
    }

    @Override
    public String getId() {
        return getURI();
    }

    @Override
    public boolean isDisabled() {
        // The list of disabled imports is stored within a project's preference
        IProject project = getProject();
        return DocumentManagerPreference.fromProject(project).isDisabledImport(uri);
    }

    public void setDisabled(boolean disabled) {
        IProject project = getProject();
        DocumentManagerPreference.fromProject(project).setDisabledImport(uri, disabled);
    }

    @Override
    public ImportType getImportType() {
        return importType;
    }

    @Override
    public String getURI() {
        return uri;
    }

    public void setURI(String importUri) {
        this.uri = importUri;
    }

    public void setImportType(ImportType importType) {
        this.importType = importType;
    }

    @Override
    public boolean isDirect() {
        return direct;
    }

    public void setDirect(boolean direct) {
        this.direct = direct;
    }

    @Override
    public int hashCode() {
        return uri.hashCode();
    }

    @Override
    public Set<String> getImportedByOntologyURIs() {
        return importedByOntologyURIs;
    }

    public void setImportedByOntologyUris(Collection<String> uris) {
        importedByOntologyURIs.clear();
        importedByOntologyURIs.addAll(uris);
    }

    public void addImportedByOntologyUri(String uri) {
        importedByOntologyURIs.add(uri);
    }

    @Override
    public int getElementType() {
        return IMPORT;
    }

    @Override
    public String getElementName() {
        return "Import";
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Object getAdapter(Class adapter) {
        if (adapter == IResource.class) {
            // Global cache imports are not adapted to IResource yet, because
            // some of our code assumes an IResource is, in fact, also an IFile,
            // but this is not necessarily the case. TODO: Ensure IResources
            // aren't carelessly cast to an IFile.
            if (isLocalCache()) {
                return CacheManager.fromProject(getProject()).getCacheFile(uri);
            }
            else if (isWorkspace()) {
                String altUrl = DocumentManagerPreference.fromProject(getProject()).getAltURL(uri,
                        true);
                return WorkspaceUtils.getFileFromURI(altUrl);
            }
        }
        return super.getAdapter(adapter);
    }

    @Override
    public boolean isWorkspace() {
        return getImportType() == ImportType.WORKSPACE;
    }

    @Override
    public boolean isLocalCache() {
        return getImportType() == ImportType.CACHE;
    }

    @Override
    public boolean isHdReference() {
        return getImportType() == ImportType.HD_REFERENCE;
    }

    @Override
    public boolean isWebReference() {
        return getImportType() == ImportType.WEB_REFERENCE;
    }

    @Override
    public boolean isCached() {
        return isLocalCache();
    }

    @Override
    public boolean isExternal() {
        return isHdReference() || isWebReference();
    }

}
