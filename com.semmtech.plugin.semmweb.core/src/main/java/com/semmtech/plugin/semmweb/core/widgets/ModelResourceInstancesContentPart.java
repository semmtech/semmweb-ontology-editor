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


import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Resource;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.CorePluginImages;
import com.semmtech.plugin.semmweb.core.dnd.DndUtils;
import com.semmtech.plugin.semmweb.core.dnd.OntClassTransfer;
import com.semmtech.plugin.semmweb.core.dnd.ResourceTransfer;
import com.semmtech.plugin.semmweb.core.forms.editor.AbstractModelResourceContent;
import com.semmtech.plugin.semmweb.core.resourceviewer.AbstractResourceContentProvider;
import com.semmtech.plugin.semmweb.core.resourceviewer.IResourceViewer;
import com.semmtech.plugin.semmweb.core.resourceviewer.ResourceLabelProvider;
import com.semmtech.plugin.semmweb.core.resourceviewer.ResourceTableContentProvider;
import com.semmtech.plugin.semmweb.core.resourceviewer.ResourceTableViewer;
import com.semmtech.plugin.semmweb.core.resourceviewer.ResourceTreeContentProvider;
import com.semmtech.plugin.semmweb.core.resourceviewer.ResourceTreeViewer;
import com.semmtech.ui.plugin.EclipseUIPlugin;
import com.semmtech.ui.plugin.util.Selections;
import com.semmtech.ui.plugin.widgets.Widgets;


/**
 * 
 * @author Sander Stolk
 * @author Simone Rondelli
 */
public class ModelResourceInstancesContentPart extends AbstractModelResourceContentPart {

    private SashForm parent;

    private ResourceTreeViewer taxonomyViewer;
    private ResourceTreeContentProvider taxonomyContentProvider;
    private TaxonomyLabelProvider taxonomyLabelProvider;
    private TaxonomyViewModel taxonomyViewModel;

    private ColumnViewer instancesViewer;
    private AbstractResourceContentProvider instancesContentProvider;
    private InstancesLabelProvider instancesLabelProvider;
    private InstancesViewModel instancesViewModel;
    private Label instancesOverviewLabel;
    private boolean groupInstances = false;

    /**
     * The reference to this item is useful to make the item disabled to avoid
     * the user modification to the InstancesView while is still loading.
     */
    private ToolItem groupingItem;

    private Section instancesSection;

    private Section taxonomySection;

    public ModelResourceInstancesContentPart(AbstractModelResourceContent contentParent,
            Composite parent, FormToolkit toolkit) {
        super(contentParent, parent, toolkit);
        createContent();
    }

    private void createContent() {
        GridLayoutFactory.fillDefaults().extendedMargins(15, 15, 10, 10).applyTo(this);
        parent = new SashForm(this, SWT.HORIZONTAL);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(parent);
        GridLayoutFactory.fillDefaults().applyTo(parent);

        createTaxonomyView();
        createInstancesView();

        refresh();
    }

    public void createTaxonomyView() {
        taxonomySection = toolkit.createSection(parent, ExpandableComposite.TWISTIE
                | ExpandableComposite.TITLE_BAR);
        taxonomySection.setText("Taxonomy");
        taxonomySection.setExpanded(true);
        toolkit.paintBordersFor(taxonomySection);

        GridDataFactory.fillDefaults().grab(true, true).applyTo(taxonomySection);

        Composite composite = createSectionClientComposite(taxonomySection);
        createToolBar(composite);

        taxonomyViewer = new ResourceTreeViewer(composite, SWT.FULL_SELECTION | SWT.VIRTUAL);

        taxonomyContentProvider = new ResourceTreeContentProvider();

        taxonomyLabelProvider = new TaxonomyLabelProvider(taxonomyViewer, getModelProvider()
                .getLabelProvider());

        taxonomyViewer.setContentProvider(taxonomyContentProvider);
        taxonomyViewer.setLabelProvider(taxonomyLabelProvider);
        taxonomyViewer.addSelectionChangedListener(new ISelectionChangedListener() {

            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                synchronized (getModelProvider().getOntModel()) {
                    Resource resource = Selections.retrieveFirstAsType(event.getSelection(),
                            Resource.class);
                    setGroupingItemEnabled(false);

                    if (resource != null) {
                        InstancesViewRefreshJob instancesRefreshJob = new InstancesViewRefreshJob(
                                ModelResourceInstancesContentPart.this, resource);
                        instancesRefreshJob.schedule();
                    }
                }
            }
        });

        taxonomyViewer.addDoubleClickListener(new OpenResourceDoubleClickListener());
        taxonomyViewer.addDragSupport(DND.DROP_MOVE | DND.DROP_COPY,
                new Transfer[] { OntClassTransfer.getInstance(), ResourceTransfer.getInstance(),
                        TextTransfer.getInstance() }, new InstancesViewerDragSourceAdapter(
                        taxonomyViewer, getModelProvider().getOntModel()));

        GridDataFactory.fillDefaults().grab(true, true).hint(SWT.DEFAULT, 300)
                .applyTo(taxonomyViewer.getTree());

    }

    public void createInstancesView() {
        instancesSection = toolkit.createSection(parent, ExpandableComposite.TWISTIE
                | ExpandableComposite.TITLE_BAR);
        instancesSection.setText("Instances");
        instancesSection.setExpanded(true);
        toolkit.paintBordersFor(instancesSection);

        GridDataFactory.fillDefaults().grab(true, true).applyTo(instancesSection);

        final Composite composite = createSectionClientComposite(instancesSection);
        ToolBar toolBar = createToolBar(composite);
        groupingItem = new ToolItem(toolBar, SWT.CHECK);
        groupingItem.setEnabled(false);
        groupingItem.setImage(CorePlugin.getDefault().getImage(CorePluginImages.IMG_GROUPING));
        groupingItem.setToolTipText("Group by Direct Type");
        groupingItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                switchInstancesModality(composite);
            }
        });

        instancesOverviewLabel = createOverviewLabel(composite, getResource());

        instancesContentProvider = new ResourceTableContentProvider();
        instancesLabelProvider = new InstancesLabelProvider(getModelProvider().getLabelProvider());

        instancesViewer = new ResourceTableViewer(composite, SWT.VIRTUAL | SWT.FULL_SELECTION);
        instancesViewer.setContentProvider(instancesContentProvider);
        instancesViewer.setLabelProvider(instancesLabelProvider);
        instancesViewer.addDoubleClickListener(new OpenResourceDoubleClickListener());
        instancesViewer.addDragSupport(DND.DROP_MOVE | DND.DROP_COPY,
                new Transfer[] { ResourceTransfer.getInstance() },
                new InstancesViewerDragSourceAdapter(instancesViewer, getModelProvider()
                        .getOntModel()));

        GridDataFactory.fillDefaults().grab(true, true).hint(SWT.DEFAULT, 300)
                .applyTo(instancesViewer.getControl());
    }

    /**
     * Change the visualization in the instances view from table to tree and
     * vice versa.
     */
    private void switchInstancesModality(Composite parent) {
        groupInstances = !groupInstances;
        Widgets.disposeIfExists(instancesViewer.getControl());

        if (groupInstances) {
            instancesViewer = new ResourceTreeViewer(parent, SWT.VIRTUAL | SWT.FULL_SELECTION);
            instancesContentProvider = new ResourceTreeContentProvider();
        }
        else {
            instancesViewer = new ResourceTableViewer(parent, SWT.VIRTUAL | SWT.FULL_SELECTION);
            instancesContentProvider = new ResourceTableContentProvider();
        }

        instancesViewer.setContentProvider(instancesContentProvider);
        instancesViewer.setLabelProvider(instancesLabelProvider);
        GridDataFactory.fillDefaults().grab(true, true).hint(SWT.DEFAULT, 300)
                .applyTo(instancesViewer.getControl());

        refreshInstances();
    }

    @Override
    public void refresh() {
        TaxonomyViewRefreshJob taxonomyRefreshJob = new TaxonomyViewRefreshJob(this);
        taxonomyRefreshJob.schedule();

        InstancesViewRefreshJob instancesRefreshJob = new InstancesViewRefreshJob(this,
                getSelectedTaxonomyResource());
        instancesRefreshJob.schedule();
    }

    private void refreshTaxonomy() {
        if (Widgets.isNullOrDisposedViewer(taxonomyViewer)) {
            return;
        }

        Tree taxonomyTree = taxonomyViewer.getTree();
        taxonomyTree.setRedraw(false);
        taxonomyViewer.setViewModel(taxonomyViewModel);
        taxonomyContentProvider.setRoot(getResource());
        taxonomyTree.setItemCount(1);
        taxonomyViewer.expandToLevel(2);
        taxonomyViewer.refresh();
        taxonomyTree.setRedraw(true);
        taxonomySection.layout();
    }

    private void refreshInstances() {
        if (Widgets.isNullOrDisposedViewer(instancesViewer)) {
            return;
        }

        Control control = instancesViewer.getControl();
        control.setRedraw(false);

        ResourceLabelProvider resourceLabelProvider = null;

        if (instancesViewer instanceof IResourceViewer) {
            IResourceViewer resourceViewer = (IResourceViewer) instancesViewer;
            resourceViewer.setViewModel(instancesViewModel);
            resourceLabelProvider = resourceViewer.getLabelProvider();
        }

        LabelProvider defaultLabelProvider = null;

        if (resourceLabelProvider != null) {
            defaultLabelProvider = resourceLabelProvider.getDefaultLabelProvider();
        }

        instancesViewer.refresh();
        instancesOverviewLabel.setText(getOverviewLabelText(defaultLabelProvider,
                getSelectedTaxonomyResource()));
        control.setRedraw(true);
        instancesSection.layout();
    }

    private String getOverviewLabelText(LabelProvider labelProvider, Resource selectedResource) {
        StringBuilder text = new StringBuilder();

        if (labelProvider != null) {
            String name = labelProvider.getText(selectedResource);
            text.append(String.format("%s has ", name));
        }

        int count = instancesViewModel.getChildCount();
        int direct = instancesViewModel.getRootsCount();
        int indirect = count - direct;

        if (count == 0) {
            text.append("no instances");
        }
        else if (count == direct) {
            text.append(String.format("%s instances", count));
        }
        else {
            text.append(String.format("%s instances (%s direct, %s indirect)", count, direct,
                    indirect));
        }
        return text.toString();
    }

    private Composite createSectionClientComposite(Section section) {
        Composite clientComposite = toolkit.createComposite(section, SWT.NONE | SWT.BORDER);
        GridLayoutFactory.fillDefaults().spacing(0, 0).margins(0, 0).extendedMargins(0, 0, 0, 0)
                .applyTo(clientComposite);
        GridDataFactory.fillDefaults().grab(true, false).hint(SWT.DEFAULT, 300).indent(0, 0)
                .applyTo(clientComposite);

        section.setClient(clientComposite);
        toolkit.paintBordersFor(clientComposite);
        return clientComposite;
    }

    private ToolBar createToolBar(Composite inputArea) {
        ToolBar toolBar = new ToolBar(inputArea, SWT.HORIZONTAL | SWT.FLAT | SWT.RIGHT);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false)
                .applyTo(toolBar);
        toolBar.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
        return toolBar;
    }

    private Label createOverviewLabel(Composite parent, Resource resource) {
        Composite instancesOverviewComposite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(2, false);
        layout.marginLeft = 6;
        layout.horizontalSpacing = 2;
        layout.marginHeight = 6;
        instancesOverviewComposite.setLayout(layout);
        instancesOverviewComposite.setLayoutData(new GridData(GridData.FILL, SWT.TOP, true, false));
        instancesOverviewComposite.setBackground(Display.getCurrent()
                .getSystemColor(SWT.COLOR_GRAY));

        LabelProvider labelProvider = getModelProvider().getLabelProvider();

        Label icon = new Label(instancesOverviewComposite, SWT.NONE);
        icon.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

        Label overviewLabel = new Label(instancesOverviewComposite, SWT.NONE);
        overviewLabel.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

        if (resource != null && labelProvider != null) {
            icon.setImage(labelProvider.getImage(resource));
        }
        return overviewLabel;
    }

    private static final class TaxonomyViewRefreshJob extends Job {

        private final ModelResourceInstancesContentPart view;
        private TaxonomyViewModel taxonomyViewModel;

        public TaxonomyViewRefreshJob(final ModelResourceInstancesContentPart view) {
            super("Refreshing Taxonomy View");
            this.view = view;

            addJobChangeListener(new JobChangeAdapter() {
                @Override
                public void done(IJobChangeEvent event) {
                    view.setTaxonomyViewModel(taxonomyViewModel);
                    EclipseUIPlugin.getStandardDisplay().asyncExec(new Runnable() {
                        @Override
                        public void run() {
                            view.refreshTaxonomy();
                        }
                    });
                }
            });
        }

        @Override
        protected IStatus run(IProgressMonitor monitor) {
            taxonomyViewModel = new TaxonomyViewModel(view.getModelProvider().getOntModel(),
                    view.getResource());
            taxonomyViewModel.setCountInstances(true);
            taxonomyViewModel.init();
            return Status.OK_STATUS;
        }
    }

    private static final class InstancesViewRefreshJob extends Job {

        private InstancesViewModel instancesViewModel;
        private ModelResourceInstancesContentPart view;
        private Resource selected;

        public InstancesViewRefreshJob(final ModelResourceInstancesContentPart view,
                Resource selected) {
            super("Refreshing Instances View");
            this.view = view;
            this.selected = selected;

            addJobChangeListener(new JobChangeAdapter() {
                @Override
                public void done(IJobChangeEvent event) {
                    view.setInstancesViewModel(instancesViewModel);
                    EclipseUIPlugin.getStandardDisplay().asyncExec(new Runnable() {
                        @Override
                        public void run() {
                            view.refreshInstances();
                            view.setGroupingItemEnabled(true);
                        }
                    });
                }
            });
        }

        @Override
        protected IStatus run(IProgressMonitor monitor) {
            OntModel ontModel = view.getModelProvider().getOntModel();
            synchronized (ontModel) {
                instancesViewModel = new InstancesViewModel(ontModel);
                instancesViewModel.setResourceType(selected);
                instancesViewModel.init();
            }
            return Status.OK_STATUS;
        }
    }

    private static class OpenResourceDoubleClickListener implements IDoubleClickListener {

        @Override
        public void doubleClick(DoubleClickEvent event) {
            Resource resource = Selections
                    .retrieveFirstAsType(event.getSelection(), Resource.class);

            if (resource != null) {
                CorePlugin.getDefault().openResource(resource);
            }
        }
    }

    private static class InstancesViewerDragSourceAdapter extends DragSourceAdapter {
        private final Viewer viewer;
        private final OntModel ontModel;

        public InstancesViewerDragSourceAdapter(Viewer viewer, OntModel ontModel) {
            this.viewer = viewer;
            this.ontModel = ontModel;
        }

        @Override
        public void dragSetData(DragSourceEvent event) {
            DndUtils.setDragSetData(event, ontModel, viewer, null);
        }
    }

    public void setTaxonomyViewModel(TaxonomyViewModel taxonomyViewModel) {
        this.taxonomyViewModel = taxonomyViewModel;
    }

    public void setInstancesViewModel(InstancesViewModel instancesViewModel) {
        this.instancesViewModel = instancesViewModel;
    }

    public void setGroupingItemEnabled(boolean enabled) {
        if (!Widgets.isNullOrDisposed(groupingItem)) {
            groupingItem.setEnabled(enabled);
        }
    }

    public Resource getSelectedTaxonomyResource() {
        Resource selected = Selections.retrieveFirstAsType(taxonomyViewer.getSelection(),
                Resource.class);

        if (selected == null) {
            selected = getResource();
        }
        return selected;
    }

    @Override
    public void dispose() {
        if (taxonomyViewModel != null) {
            taxonomyViewModel.close();
            taxonomyViewModel = null;
        }

        if (instancesViewModel != null) {
            instancesViewModel.close();
            instancesViewModel = null;
        }

        super.dispose();
    }
}
