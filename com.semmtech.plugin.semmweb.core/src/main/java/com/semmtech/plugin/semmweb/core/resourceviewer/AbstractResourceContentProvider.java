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

package com.semmtech.plugin.semmweb.core.resourceviewer;


import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.hp.hpl.jena.ontology.OntModel;


/**
 * Generic content provider for the {@link IResourceViewer}. It stores an
 * instance of the {@link AbstractResourceViewModel} that will be used by
 * subclasses to provides elements to the viewer.
 * <p>
 * The the <code>currentModel</code> is set in the
 * {@link #inputChanged(Viewer, Object, Object)} method that is called after the
 * call to {@link Viewer#setInput(Object)}.
 * <p>
 * Generally the <code>viewModel</code> is set by the {@link IResourceViewer} so
 * the programmer shoulden't be aware about this (same for the
 * {@link ResourceLabelProvider}
 * 
 * @author Simone Rondelli
 * 
 */
public abstract class AbstractResourceContentProvider implements IContentProvider {

    protected AbstractResourceViewModel viewModel;
    protected OntModel currentModel;

    public AbstractResourceContentProvider() {
        // ParameterizedType pt = (ParameterizedType)
        // this.getClass().getGenericSuperclass();
        // viewerType = (Class<T>) pt.getActualTypeArguments()[0];
    }

    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        this.currentModel = null;

        if (newInput instanceof OntModel) {
            this.currentModel = (OntModel) newInput;
        }

        viewerChanged(viewer);
    }

    public OntModel getCurrentModel() {
        return currentModel;
    }

    public AbstractResourceViewModel getViewModel() {
        return viewModel;
    }

    // TODO maybe is better to set the viewModel inside inputChanged method
    // after the setInput call passing the viewModel instead of the
    // currentModel. Than the currentModel could be gotten from the viewModel
    // itself
    public void setViewModel(AbstractResourceViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void dispose() {

    }

    public abstract int getItemCount();

    /**
     * When the Viewer changes this method is called to permit to the subclasses
     * to store an instance of it
     */
    public abstract void viewerChanged(Viewer viewer);

}
