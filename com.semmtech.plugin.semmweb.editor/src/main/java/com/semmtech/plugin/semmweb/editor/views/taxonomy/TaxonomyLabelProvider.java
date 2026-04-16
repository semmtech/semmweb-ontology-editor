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

package com.semmtech.plugin.semmweb.editor.views.taxonomy;


import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.model.IWorkbenchAdapter;

import com.google.common.base.Strings;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.rdf.model.Resource;
import com.semmtech.plugin.semmweb.core.viewers.ModelNodeLabelProvider;
import com.semmtech.plugin.semmweb.core.viewers.ModelNodeLabelProvider.InspectOrder;
import com.semmtech.ui.plugin.viewers.EmptyStyler;
import com.semmtech.ui.plugin.widgets.Widgets;


/**
 * This provider is used by the Taxonomy view to provide labels for the taxonomy
 * tree.
 * 
 * @author Mike Henrichs
 * 
 */
public final class TaxonomyLabelProvider extends StyledCellLabelProvider implements
        TaxonomyViewModelListener {
    private final LabelProvider labelProvider;
    private Styler activeStyle = new EmptyStyler();
    private boolean showInstanceCount = true;
    private TaxonomyViewModel viewModel;
    private Resource activeResource;
    private TreeViewer viewer;

    public TaxonomyLabelProvider(TreeViewer viewer, LabelProvider labelProvider) {
        this.labelProvider = labelProvider;
        this.viewer = viewer;
    }

    @Override
    public void notifyChange(TaxonomyViewModelEvent event) {
        if (event.getType() == TaxonomyViewModelEvent.INSTANCE_COUNT_CHANGED) {
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

    public void setShowInstanceCount(boolean enabled) {
        showInstanceCount = enabled;
    }

    public void setActiveStyle(Styler activeStyle) {
        this.activeStyle = activeStyle;
    }

    public void setActiveResource(Resource activeResource) {
        this.activeResource = activeResource;
    }

    public Resource getActiveResource() {
        return activeResource;
    }

    public void setViewModel(TaxonomyViewModel viewModel) {
        this.viewModel = viewModel;
        if (viewModel != null) {
            viewModel.addViewModelChangeListener(this);
        }
    }

    public TaxonomyViewModel getViewModel() {
        return viewModel;
    }

    @Override
    public void update(ViewerCell cell) {
        int columnIndex = cell.getColumnIndex();
        Object element = cell.getElement();
        String text = getElementText(element, columnIndex);
        StyledString styled = new StyledString();

        if (text != null) {
            styled.append(text);

            if (activeResource != null) {
                // if (CorePlugin.getDefault().isResourceEditorActive()) {
                // Resource active =
                // CorePlugin.getDefault().getActiveResource();
                if (element instanceof Resource) {
                    Resource current = (Resource) element;
                    if (activeResource.equals(current)) {
                        // if (active.equals(current)) {
                        styled.setStyle(0, text.length(), activeStyle);
                    }
                }
            }
        }

        cell.setText(styled.getString());
        cell.setStyleRanges(styled.getStyleRanges());
        cell.setImage(getElementImage(element, columnIndex));

        super.update(cell);
    }

    private Image getElementImage(Object element, int columnIndex) {
        if (element instanceof Resource) {
            Resource resource = (Resource) element;
            if (labelProvider instanceof ModelNodeLabelProvider) {
                return ((ModelNodeLabelProvider) labelProvider).getImage(resource,
                        InspectOrder.CLASS_PROPERTY_INDIVIDUAL);
            }
            return labelProvider.getImage(resource);
        }
        IWorkbenchAdapter adapter = (IWorkbenchAdapter) Platform.getAdapterManager().getAdapter(
                element, IWorkbenchAdapter.class);
        if (adapter != null) {
            return adapter.getImageDescriptor(element).createImage();
        }
        return null;
    }

    private String getElementText(Object element, int columnIndex) {
        // if (labelProvider == null) {
        // return null;
        // }
        if (viewModel == null) {
            return null;
        }

        if (element instanceof Resource) {
            Resource resource = (Resource) element;
            String text = viewModel.getText(resource);
            if (Strings.isNullOrEmpty(text)) {
                text = labelProvider.getText(resource);
            }

            // Check if instance count should be appended
            if (showInstanceCount && (element instanceof OntClass) && (viewModel != null)) {
                OntClass clazz = (OntClass) element;
                int directInstanceCount = viewModel.getDirectInstanceCount(clazz);
                int indirectInstanceCount = viewModel.getIndirectInstanceCount(clazz);

                if (directInstanceCount > 0 || indirectInstanceCount > 0) {
                    if (indirectInstanceCount > 0) {
                        text += String.format(" (%d+%d)", directInstanceCount,
                                indirectInstanceCount);
                    }
                    else {
                        text += String.format(" (%d)", directInstanceCount);
                    }
                }
            }
            return text;
        }
        // else {
        // IWorkbenchAdapter adapter = (IWorkbenchAdapter)
        // Platform.getAdapterManager()
        // .getAdapter(element, IWorkbenchAdapter.class);
        // if (adapter != null) {
        // return adapter.getLabel(element);
        // }
        // }
        return null;
    }

    @Override
    public int getToolTipDisplayDelayTime(Object object) {
        return 80;
    }

    @Override
    public int getToolTipTimeDisplayed(Object object) {
        return 10000;
    }

    @Override
    public Point getToolTipShift(Object object) {
        return new Point(8, 10);
    }

    @Override
    public String getToolTipText(Object element) {
        if (element instanceof Resource) {
            return ((Resource) element).toString();
        }
        return null;
    }

}
