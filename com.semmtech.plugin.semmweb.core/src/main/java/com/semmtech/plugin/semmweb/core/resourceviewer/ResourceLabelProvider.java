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


import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.model.IWorkbenchAdapter;

import com.google.common.base.Strings;
import com.hp.hpl.jena.rdf.model.Resource;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.viewers.ModelNodeLabelProvider;
import com.semmtech.plugin.semmweb.core.viewers.ModelNodeLabelProvider.InspectOrder;
import com.semmtech.ui.plugin.viewers.EmptyStyler;


/**
 * Retrieves label and image from a {@link AbstractResourceViewModel}, if them
 * aren't found in the viewModel are taken from the defaultLabelProvider.
 * <p>
 * Generally the <code>viewModel</code> is set by the {@link IResourceViewer} so
 * the programmer shoulden't be aware about this (same for the
 * {@link AbstractResourceContentProvider}
 * <p>
 * The default implementation doesn't take care of the different columns so the
 * call to {@link #update(ViewerCell)} always use the text/image contained in
 * the {@code viewModel} or provided by the {@code defaultLabelProvider}. The
 * sub classes of this class that wants to consider the columns of the
 * table/tree should override the methods {@link #getColumnImage(Object, int)}
 * and {@link #getColumnText(Object, int)}.
 * 
 * @author Simone Rondelli
 * 
 */
public class ResourceLabelProvider extends StyledCellLabelProvider implements ILabelProvider {

    protected AbstractResourceViewModel viewModel;

    protected final LabelProvider defaultLabelProvider;
    private Resource activeResource;
    private Styler activeStyle;

    /**
     * @param labelProvider
     *            Provides text and images if the ResourceViewModel doesn't
     *            provides them
     */
    public ResourceLabelProvider(LabelProvider defaultLabelProvider) {
        this.defaultLabelProvider = defaultLabelProvider;
        this.activeStyle = new EmptyStyler();
    }

    @Override
    public void update(ViewerCell cell) {
        int columnIndex = cell.getColumnIndex();
        Object element = cell.getElement();
        String text = getColumnText(element, columnIndex);
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
        cell.setImage(getColumnImage(element, columnIndex));

        super.update(cell);
    }

    @Override
    public Image getImage(Object element) {
        if (viewModel != null && element instanceof Resource) {
            Resource resource = (Resource) element;

            String imageKey = viewModel.getImage(resource);

            if (imageKey != null && !"todo.png".equals(imageKey)) {
                return CorePlugin.getDefault().getImage(imageKey);
            }

            if (defaultLabelProvider instanceof ModelNodeLabelProvider) {
                return ((ModelNodeLabelProvider) defaultLabelProvider).getImage(resource,
                        InspectOrder.CLASS_PROPERTY_INDIVIDUAL);
            }
            return defaultLabelProvider.getImage(resource);
        }

        IWorkbenchAdapter adapter = (IWorkbenchAdapter) Platform.getAdapterManager().getAdapter(
                element, IWorkbenchAdapter.class);
        if (adapter != null) {
            return adapter.getImageDescriptor(element).createImage();
        }
        return null;
    }

    @Override
    public String getText(Object element) {
        if (viewModel == null || !(element instanceof Resource)) {
            return null;
        }

        Resource resource = (Resource) element;
        String text = viewModel.getText(resource);

        if (Strings.isNullOrEmpty(text)) {
            text = defaultLabelProvider.getText(resource);
        }

        return text;
    }

    protected Image getColumnImage(Object element, int columnIndex) {
        return getImage(element);
    }

    protected String getColumnText(Object element, int columnIndex) {
        return getText(element);
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

    public void setActiveResource(Resource activeResource) {
        this.activeResource = activeResource;
    }

    public Resource getActiveResource() {
        return activeResource;
    }

    public void setActiveStyle(Styler activeStyle) {
        this.activeStyle = activeStyle;
    }

    public void setViewModel(AbstractResourceViewModel viewModel) {
        this.viewModel = viewModel;
    }

    public AbstractResourceViewModel getViewModel() {
        return viewModel;
    }

    public LabelProvider getDefaultLabelProvider() {
        return defaultLabelProvider;
    }
}
