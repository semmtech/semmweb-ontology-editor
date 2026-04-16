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

package com.semmtech.plugin.semmweb.core.search.ui;


import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.search.ui.text.AbstractTextSearchViewPage;

import com.hp.hpl.jena.rdf.model.Resource;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.search.SearchResultContentProvider;
import com.semmtech.plugin.semmweb.core.search.SearchResultTableContentProvider;
import com.semmtech.plugin.semmweb.core.search.SearchResultTreeContentProvider;
import com.semmtech.plugin.semmweb.core.search.elements.ResourceElement;
import com.semmtech.plugin.semmweb.core.search.ui.OntologySearchLabelProvider.Modality;
import com.semmtech.ui.plugin.widgets.Widgets;


public class OntologySearchResultPage extends AbstractTextSearchViewPage implements IAdaptable {

    @SuppressWarnings("unused")
    private static Logger logger = Logger.getLogger(OntologySearchResultPage.class);

    // private final OntologySearchLabelProvider labelProvider;
    private SearchResultContentProvider searchResultProvider;

    public OntologySearchResultPage() {
        // labelProvider = new OntologySearchLabelProvider();
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Object getAdapter(Class adapter) {
        return null;
    }

    @Override
    protected void elementsChanged(Object[] objects) {
        if (searchResultProvider != null) {
            searchResultProvider.elementsChanged(objects);
        }
    }

    @Override
    protected void clear() {
        if (searchResultProvider != null) {
            searchResultProvider.clear();
        }
    }

    @Override
    protected void configureTreeViewer(TreeViewer viewer) {
        if (Widgets.isNullOrDisposedViewer(viewer)) {
            return;
        }

        viewer.setUseHashlookup(true);
        searchResultProvider = new SearchResultTreeContentProvider(viewer);
        viewer.setContentProvider(searchResultProvider);

        // styledLabelProvider = new
        // DecoratingPatternStyledCellLabelProvider(new
        // SearchResultsLabelProvider(
        // searchResultProvider, viewer),
        // PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator(),
        // null);
        OntologySearchLabelProvider labelProvider = new OntologySearchLabelProvider(
                Modality.TREE_MODE);
        viewer.setLabelProvider(labelProvider);

        // viewer.setSorter(searchResultSorter);
        // Transfer[] dragTypes = new Transfer[] {
        // LocalSelectionTransfer.getTransfer(), FileTransfer.getInstance() };
        // getViewer().addDragSupport(DND.DROP_COPY | DND.DROP_MOVE |
        // DND.DROP_LINK, dragTypes, new TaskDragSourceListener(getViewer()));
        // sortByDialogAction = new SearchResultSortAction(this);
        // toolTip = new TaskListToolTip(viewer.getControl());

    }

    @Override
    protected void configureTableViewer(TableViewer viewer) {
        if (Widgets.isNullOrDisposedViewer(viewer)) {
            return;
        }

        viewer.setUseHashlookup(true);
        searchResultProvider = new SearchResultTableContentProvider(viewer);
        OntologySearchLabelProvider labelProvider = new OntologySearchLabelProvider(
                Modality.TABLE_MODE);
        viewer.setContentProvider(searchResultProvider);
        viewer.setLabelProvider(labelProvider);
    }

    @Override
    protected void handleOpen(OpenEvent event) {
        Object firstElement = ((IStructuredSelection) event.getSelection()).getFirstElement();
        if (firstElement instanceof ResourceElement) {
            IFile file = (IFile) ((ResourceElement) firstElement).getFile();
            Resource resource = ((ResourceElement) firstElement).getResource();
            CorePlugin.getDefault().openResource(file, resource);
        }
        else if (firstElement instanceof IResource) {
            IResource file = (IResource) firstElement;
            if (file instanceof IFile) {
                CorePlugin.getDefault().openModelEditor((IFile) file);
            }
            return;
        }
    }

}
