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

package com.semmtech.plugin.semmweb.core.handlers;


import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISourceProvider;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.services.ISourceProviderService;

import com.google.common.collect.Lists;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.model.IModelProvider;


public abstract class SelectedResourceHandler extends AbstractHandler {

    /**
     * Returns the active model provider.
     * 
     * @param event
     * @return
     */
    protected IModelProvider getActiveModelProvider(ExecutionEvent event) {
        IEditorPart editorPart = HandlerUtil.getActiveEditor(event);
        if (editorPart instanceof IModelProvider) {
            return (IModelProvider) editorPart;
        }
        return null;
    }

    /**
     * Returns the OntModel of the current model provider.
     * 
     * @param event
     * @return
     */
    protected OntModel getActiveOntModel(ExecutionEvent event) {
        IModelProvider provider = getActiveModelProvider(event);
        if (provider != null) {
            return provider.getOntModel();
        }
        return null;
    }

    protected Model getActiveBaseModel(ExecutionEvent event) {
        IModelProvider provider = getActiveModelProvider(event);
        if (provider != null) {
            return provider.getBaseModel();
        }
        return null;
    }

    /**
     * Returns the Model URI of the active model provider.
     * 
     * @param event
     * @return
     */
    protected String getActiveModelURI(ExecutionEvent event) {
        IModelProvider provider = getActiveModelProvider(event);
        if (provider != null) {
            return provider.getModelURI();
        }
        return null;
    }

    /**
     * Returns the structured selection.
     * 
     * @param event
     * @return
     */
    protected IStructuredSelection getStructuredSelection(ExecutionEvent event) {
        ISelection selection = HandlerUtil.getCurrentSelection(event);
        if (selection instanceof IStructuredSelection) {
            return (IStructuredSelection) selection;
        }
        return null;
    }

    /**
     * Returns the structured selection.
     * 
     * @param event
     * @return
     */
    protected IStructuredSelection getStructuredSelection(ExecutionEvent event, String variable) {
        // Get the source provider service
        ISourceProviderService sourceProviderService = (ISourceProviderService) HandlerUtil
                .getActiveWorkbenchWindow(event).getService(ISourceProviderService.class);
        // Get the provider service
        ISourceProvider providerService = sourceProviderService.getSourceProvider(variable);
        // Get the structured selection
        Object result = providerService.getCurrentState().get(variable);
        if (result instanceof IStructuredSelection) {
            return (IStructuredSelection) result;
        }
        return null;
    }

    /**
     * 
     * @param event
     * @return
     */
    protected Resource getSelectedResource(ExecutionEvent event) {
        IStructuredSelection selection = getStructuredSelection(event);
        return getResource(selection);
    }

    /**
     * 
     * @param event
     * @return
     */
    protected Resource getProvidedResource(ExecutionEvent event, String variable) {
        IStructuredSelection selection = getStructuredSelection(event, variable);
        return getResource(selection);
    }

    private Resource getResource(IStructuredSelection selection) {
        if (selection != null) {
            Object firstElement = selection.getFirstElement();

            if (firstElement instanceof Resource) {
                return (Resource) firstElement;
            }
        }
        return null;
    }

    /**
     * 
     * @param event
     * @return
     */
    protected List<Resource> getSelectedResources(ExecutionEvent event) {
        IStructuredSelection selection = getStructuredSelection(event);
        return getProvidedResources(selection);
    }

    protected List<Resource> getProvidedResources(ExecutionEvent event, String variable) {
        IStructuredSelection selection = getStructuredSelection(event, variable);
        return getProvidedResources(selection);
    }

    private List<Resource> getProvidedResources(IStructuredSelection selection) {
        List<Resource> resources = Lists.newArrayList();
        for (Object element : selection.toList()) {
            if (element instanceof Resource) {
                resources.add((Resource) element);
            }
        }
        return resources;
    }

    /**
     * Open the resource in a new resource form editor.
     * 
     * @param event
     * @param resource
     */
    protected void openResource(ExecutionEvent event, Resource resource) {
        CorePlugin.getDefault().openResource(resource);
    }

    @Override
    public abstract Object execute(ExecutionEvent event) throws ExecutionException;
}
