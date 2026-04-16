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


import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.swt.widgets.Composite;

import com.semmtech.ui.plugin.viewers.LazyTreeViewer;


/**
 * This class has the aim to show a tree that represents a certain kind of
 * hierarchy inside a model (like the Classes or Properties).
 * 
 * The information that have to be shown are stored inside the
 * {@link AbstractResourceViewModel}. This model have to be built outside this
 * class and then possed through the
 * {@link #setViewModel(AbstractResourceViewModel)} method. This method deals
 * the passing of the reference of the model to the
 * {@link ResourceLabelProvider} and the {@link ResourceTreeContentProvider}
 * 
 * 
 * @author Simone Rondelli
 */
public class ResourceTreeViewer extends LazyTreeViewer implements IResourceViewer {

    private AbstractResourceViewModel viewModel;
    private ResourceLabelProvider labelProvider;
    private ResourceTreeContentProvider contentProvider;

    /**
     * Create an instance of this class calling setUseHashlookup(true)
     */
    public ResourceTreeViewer(Composite parent, int styles) {
        super(parent, styles);
        setUseHashlookup(true);
    }

    @Override
    public void setLabelProvider(IBaseLabelProvider labelProvider) {
        super.setLabelProvider(labelProvider);
        this.labelProvider = (ResourceLabelProvider) labelProvider;
    }

    @Override
    public ResourceLabelProvider getLabelProvider() {
        return labelProvider;
    }

    @Override
    public void setContentProvider(IContentProvider contentProvider) {
        super.setContentProvider(contentProvider);
        this.contentProvider = (ResourceTreeContentProvider) contentProvider;
    }

    @Override
    public void setViewModel(AbstractResourceViewModel viewModel) {
        this.viewModel = viewModel;
        super.setInput(viewModel.getCurrentModel());

        if (labelProvider != null) {
            labelProvider.setViewModel(viewModel);
        }

        if (contentProvider != null) {
            contentProvider.setViewModel(viewModel);
            contentProvider.setRoots(viewModel.getRoots());
            getTree().setItemCount(contentProvider.getItemCount());
        }
    }

    @Override
    public AbstractResourceViewModel getViewModel() {
        return viewModel;
    }

    public void dispose() {
        getTree().dispose();
        viewModel.close();
        contentProvider.dispose();
        labelProvider.dispose();
    }

}
