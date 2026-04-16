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


/**
 * This interface describes the viewer for a certain type of resources.
 * <p>
 * This is the workflow to make it working assuming that we want to create a
 * TreeViewer (the process for the TableViewer is the same).
 * 
 * <pre>
 * <code>
 * ResourceTreeViewer treeViewer =  new ResourceTreeViewer(composite, SWT.FULL_SELECTION | SWT.VIRTUAL);
 * ResourceTreeContentProvider contentProvider = new ResourceTreeContentProvider();
 * TaxonomyLabelProvider taxonomyLabelProvider = new TaxonomyLabelProvider(treeViewer, defaultLabelProvider);
 * treeViewer.setContentProvider(contentProvider);
 * treeViewer.setLabelProvider(taxonomyLabelProvider);
 * 
 * //The constructor and the other parameters could change in the different implementation on the AbstractViewModel. 
 * //Note that typically the loading of the ViewModel is performed asynchronously by a Job.
 * TaxonomyViewModel model = new TaxonomyViewModel(currentOntModel, RDFS.Class);
 * model.setCountInstances(true);
 * model.init(); 
 * 
 * //With this operation the viewModel is applied both to the labelProvider and to the contentProvider.
 * //The currentOntModel is automatically set as input of the Viewer so the setInput method is unnecessary
 * //until some different input is needed to make the viewer working. 
 * //Moreover the itemCount of the backing control is automatically set. The programmer can always change the 
 * //count if, for instance, they need to customise the viewer to show only a certain node/nodes
 * treeViewer.setViewModel(model);
 * treeViewer.refresh();
 * </code>
 * </pre>
 * 
 * @author Simone Rondelli
 * 
 */
public interface IResourceViewer {

    public void setLabelProvider(IBaseLabelProvider labelProvider);

    public ResourceLabelProvider getLabelProvider();

    public void setContentProvider(IContentProvider contentProvider);

    public IContentProvider getContentProvider();

    /**
     * The implementation of this method should provide the following operation.
     * <ul>
     * <li>Save an instance of the viewModel inside the viewer
     * <li>Set the OntModel contained inside the viewModel as input of the
     * Viewer
     * <li>Set the viewModel both to the labelProvider and contentProvider
     * <li>Set the itemCount of the backing control
     * <li>In case of TreeViewer the method setRoots of
     * ResourceTreeContentProvider will be called with all possible roots as
     * parameter
     * </ul>
     */
    public void setViewModel(AbstractResourceViewModel viewModel);

    public AbstractResourceViewModel getViewModel();

}