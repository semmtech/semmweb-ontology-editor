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

package com.semmtech.plugin.semmweb.core.widgets;


import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Display;

import com.hp.hpl.jena.ontology.OntResource;
import com.semmtech.plugin.semmweb.core.resourceviewer.AbstractResourceViewModel;
import com.semmtech.plugin.semmweb.core.resourceviewer.ResourceLabelProvider;
import com.semmtech.plugin.semmweb.core.resourceviewer.ResourceViewModelEvent;
import com.semmtech.plugin.semmweb.core.resourceviewer.ResourceViewModelListener;
import com.semmtech.ui.plugin.widgets.Widgets;


/**
 * This provider is used by the Taxonomy view to provide labels for the taxonomy
 * tree.
 * 
 * @author Mike Henrichs
 * 
 */
public class TaxonomyLabelProvider extends ResourceLabelProvider implements
        ResourceViewModelListener {

    private boolean showInstanceCount = true;
    private TreeViewer viewer;
    private TaxonomyViewModel taxonomyViewModel;

    public TaxonomyLabelProvider(TreeViewer viewer, LabelProvider labelProvider) {
        super(labelProvider);
        this.viewer = viewer;
    }

    @Override
    public void notifyChange(ResourceViewModelEvent event) {
        if (event.getData() == TaxonomyViewModel.INSTANCE_COUNT_CHANGED_EVENT) {
            Display.getDefault().asyncExec(new Runnable() {
                @Override
                public void run() {
                    if (!Widgets.isNullOrDisposedViewer(viewer)) {
                        viewer.refresh(true);
                    }
                }
            });
        }
    }

    @Override
    public void setViewModel(AbstractResourceViewModel viewModel) {
        super.setViewModel(viewModel);
        viewModel.addViewModelChangeListener(this);

        if (viewModel instanceof TaxonomyViewModel) {
            taxonomyViewModel = (TaxonomyViewModel) viewModel;
        }
    }

    @Override
    protected String getColumnText(Object element, int columnIndex) {
        String text = super.getColumnText(element, columnIndex);

        if (text == null) {
            return null;
        }

        // Check if instance count should be appended
        if (showInstanceCount && (element instanceof OntResource) && taxonomyViewModel != null
                && taxonomyViewModel.getCountInstances()) {

            TaxonomyViewModel taxonomyViewModel = (TaxonomyViewModel) viewModel;
            OntResource clazz = (OntResource) element;

            int directInstanceCount = taxonomyViewModel.getDirectInstanceCount(clazz);
            int indirectInstanceCount = taxonomyViewModel.getIndirectInstanceCount(clazz);

            if (directInstanceCount > 0 || indirectInstanceCount > 0) {
                if (indirectInstanceCount > 0) {
                    text += String.format(" (%d+%d)", directInstanceCount, indirectInstanceCount);
                }
                else {
                    text += String.format(" (%d)", directInstanceCount);
                }
            }
        }
        return text;
    }

}
