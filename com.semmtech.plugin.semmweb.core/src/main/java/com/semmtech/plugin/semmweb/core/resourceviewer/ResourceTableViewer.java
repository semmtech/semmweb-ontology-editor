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
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Composite;


public class ResourceTableViewer extends TableViewer implements IResourceViewer {

    private AbstractResourceViewModel viewModel;
    private ResourceLabelProvider labelProvider;
    private ResourceTableContentProvider contentProvider;

    /**
     * Create an instance of this class calling setUseHashlookup(true)
     */
    public ResourceTableViewer(Composite parent, int styles) {
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
        this.contentProvider = (ResourceTableContentProvider) contentProvider;
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
            getTable().setItemCount(contentProvider.getItemCount());
        }
    }

    @Override
    public AbstractResourceViewModel getViewModel() {
        return viewModel;
    }

}
