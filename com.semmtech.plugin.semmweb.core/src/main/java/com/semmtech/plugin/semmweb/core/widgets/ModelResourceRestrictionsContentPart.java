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


import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.ontology.Restriction;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.OWL2;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.CorePluginImages;
import com.semmtech.plugin.semmweb.core.actions.EditRestrictionSuite;
import com.semmtech.plugin.semmweb.core.dnd.PropertyTransfer;
import com.semmtech.plugin.semmweb.core.dnd.ResourceTransfer;
import com.semmtech.plugin.semmweb.core.dnd.ViewerDragAdapter;
import com.semmtech.plugin.semmweb.core.forms.editor.AbstractModelResourceContent;
import com.semmtech.plugin.semmweb.core.model.IModelProvider;
import com.semmtech.plugin.semmweb.core.model.RestrictionUtil;
import com.semmtech.plugin.semmweb.core.resourceviewer.ResourceTreeContentProvider;
import com.semmtech.plugin.semmweb.core.resourceviewer.ResourceTreeViewer;
import com.semmtech.plugin.semmweb.core.viewers.ResourceToolTipContent;
import com.semmtech.plugin.semmweb.core.viewers.RestrictionToolTipContent;
import com.semmtech.semantics.ontology.OntClassUtil;
import com.semmtech.semantics.util.JenaUtil;
import com.semmtech.ui.plugin.DelayedRunnableExecution;
import com.semmtech.ui.plugin.EclipseUIPlugin;
import com.semmtech.ui.plugin.decorators.OverlayImageIcon;
import com.semmtech.ui.plugin.util.Selections;
import com.semmtech.ui.plugin.widgets.Widgets;


/**
 * 
 * @author Sander Stolk
 */
public class ModelResourceRestrictionsContentPart extends AbstractModelResourceContentPart {

    private static final Logger logger = Logger
            .getLogger(ModelResourceRestrictionsContentPart.class);

    private ResourceTreeViewer restrictionsViewer;
    private List<RestrictionColumnName> viewerColumnName;
    private Map<RestrictionColumnName, SearchComposite> viewColumnSearchComposite;
    private ResourceTreeContentProvider restrictionsContentProvider;

    private RestrictionColumnComparator comparator;

    private Image addRestrictionImage;

    private final DelayedRunnableExecution resizeFiltersExecution;
    private final ViewerFilter viewerFilter;

    public enum RestrictionColumnName {
        ON_PROPERTY, CARDINALITY, RESOURCE
    }

    public ModelResourceRestrictionsContentPart(AbstractModelResourceContent contentParent,
            Composite parent, FormToolkit toolkit) {
        super(contentParent, parent, toolkit);

        addRestrictionImage = CorePlugin.getDefault().getDecoratedImage(
                CorePluginImages.IMG_OWL_RESTRICTION, CorePluginImages.IMG_OVERLAY_ADD,
                OverlayImageIcon.TOP_RIGHT);

        resizeFiltersExecution = new DelayedRunnableExecution(new Runnable() {
            @Override
            public void run() {
                resizeFilters(true);
            }
        }, 500);

        viewerFilter = new ViewerFilter() {
            @Override
            public boolean select(Viewer viewer, Object parentElement, Object element) {
                if (viewerColumnName != null && viewColumnSearchComposite != null
                        && !Widgets.isNullOrDisposedViewer(restrictionsViewer)) {
                    ILabelProvider labelProvider = restrictionsViewer.getLabelProvider();
                    if (labelProvider instanceof RestrictionsLabelProvider) {
                        RestrictionsLabelProvider rLabelProvider = (RestrictionsLabelProvider) labelProvider;
                        for (int i = 0; i < viewerColumnName.size(); i++) {
                            RestrictionColumnName columnName = viewerColumnName.get(i);
                            SearchComposite searchComposite = viewColumnSearchComposite
                                    .get(columnName);
                            if (searchComposite != null) {
                                String filter = searchComposite.getFilter();
                                if (!Strings.isNullOrEmpty(filter)) {
                                    filter = filter.toLowerCase();
                                    String text = rLabelProvider.getColumnText(element, i);
                                    if (!Strings.isNullOrEmpty(text)) {
                                        text = text.toLowerCase();
                                    }
                                    if (text == null || !text.contains(filter)) {
                                        return false;
                                    }
                                }
                            }
                        }
                    }

                }
                return true;
            }
        };

        createContent();
    }

    private void resizeFilters(boolean relayout) {
        if (viewerColumnName != null && viewColumnSearchComposite != null
                && !Widgets.isNullOrDisposedViewer(restrictionsViewer)) {
            for (int i = 0; i < viewerColumnName.size(); i++) {
                RestrictionColumnName columnName = viewerColumnName.get(i);
                TreeColumn treeColumn = restrictionsViewer.getTree().getColumn(i);
                SearchComposite searchComposite = viewColumnSearchComposite.get(columnName);
                if (!Widgets.isNullOrDisposed(treeColumn)
                        && !Widgets.isNullOrDisposed(searchComposite)) {
                    int filterWidth = treeColumn.getWidth();
                    if (i == 0) {
                        filterWidth++; // include left border px
                    }
                    else if (i < viewerColumnName.size() - 1) {
                        filterWidth--; // exclude border px
                    }
                    GridDataFactory.fillDefaults().hint(filterWidth, SWT.DEFAULT)
                            .applyTo(searchComposite);
                    if (relayout) {
                        Widgets.layoutControlUpToScrollableParent(searchComposite);
                    }
                }
            }
        }
    }

    private void applyFilters() {
        if (Widgets.isNullOrDisposedViewer(restrictionsViewer)) {
            return;
        }
        RestrictionsViewModel viewModel = (RestrictionsViewModel) restrictionsViewer.getViewModel();
        if (viewModel == null) {
            return;
        }

        boolean filterEnabled = false;
        for (SearchComposite searchComposite : viewColumnSearchComposite.values()) {
            if (!Strings.isNullOrEmpty(searchComposite.getFilter())) {
                filterEnabled = true;
            }
        }
        viewModel.applyFilter((filterEnabled) ? viewerFilter : null);
        restrictionsContentProvider.setRoots(viewModel.getRoots());
        restrictionsViewer.setViewModel(viewModel);
        restrictionsViewer.refresh();
    }

    private void createContent() {
        viewerColumnName = Lists.newArrayList();
        viewerColumnName.add(RestrictionColumnName.ON_PROPERTY);
        viewerColumnName.add(RestrictionColumnName.CARDINALITY);
        viewerColumnName.add(RestrictionColumnName.RESOURCE);

        GridLayoutFactory.fillDefaults().extendedMargins(15, 15, 10, 10).spacing(5, 5)
                .applyTo(this);
        GridDataFactory.swtDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(this);

        Menu generalPopupMenu = createGeneralPopupMenu(this);

        // Description
        String description = "All restrictions on every instance of the current class are shown below."
                + "\nTogether, these restrictions determine the behaviour of the instances.";
        Label label = toolkit.createLabel(this, description, SWT.WRAP);
        GridDataFactory.swtDefaults().align(SWT.FILL, SWT.TOP).grab(true, false).applyTo(label);
        label.setMenu(generalPopupMenu);

        Composite filterComposite = toolkit.createComposite(this, SWT.NONE);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP).grab(true, false)
                .applyTo(filterComposite);
        GridLayoutFactory.fillDefaults().numColumns(viewerColumnName.size()).spacing(1, 0)
                .applyTo(filterComposite);

        viewColumnSearchComposite = Maps.newHashMap();
        SearchFilterChangedListener filterChangedListener = new SearchFilterChangedListener() {
            @Override
            public void filterChanged(String filter) {
                applyFilters();
            }
        };
        for (RestrictionColumnName columnName : viewerColumnName) {
            SearchComposite searchComposite = new SearchComposite(filterComposite, SWT.NONE);
            GridDataFactory.fillDefaults().grab(false, false).applyTo(searchComposite);
            viewColumnSearchComposite.put(columnName, searchComposite);
            searchComposite.addSearchFilterChangedListener(filterChangedListener);
        }

        restrictionsViewer = new ResourceTreeViewer(this, SWT.FULL_SELECTION | SWT.BORDER
                | SWT.VIRTUAL);

        Tree tree = restrictionsViewer.getTree();
        tree.setHeaderVisible(true);
        tree.setLinesVisible(true);

        for (int i = 0; i < viewerColumnName.size(); i++) {
            RestrictionColumnName columnName = viewerColumnName.get(i);
            final TreeColumn column = new TreeColumn(tree, SWT.NONE, i);

            if (columnName == RestrictionColumnName.ON_PROPERTY) {
                column.setText("On Property");
                column.setWidth(160);
            }

            if (columnName == RestrictionColumnName.CARDINALITY) {
                column.setText("Cardinality");
                column.setWidth(85);
            }

            if (columnName == RestrictionColumnName.RESOURCE) {
                column.setText("Resource");
                column.setWidth(200);
            }

            column.addSelectionListener(getSelectionAdapter(column, i));
            column.addControlListener(new ControlAdapter() {
                @Override
                public void controlResized(ControlEvent e) {
                    resizeFiltersExecution.start();
                }
            });
        }
        resizeFilters(false);

        RestrictionsLabelProvider labelProvider = new RestrictionsLabelProvider(getModelProvider()
                .getLabelProvider(), viewerColumnName);

        comparator = new RestrictionColumnComparator(labelProvider, viewerColumnName);
        comparator.setColumnIndex(viewerColumnName.indexOf(RestrictionColumnName.ON_PROPERTY));

        restrictionsContentProvider = new ResourceTreeContentProvider();

        restrictionsViewer.setContentProvider(restrictionsContentProvider);
        restrictionsViewer.setLabelProvider(labelProvider);
        restrictionsViewer.addDoubleClickListener(new DoubleClickListener());
        restrictionsViewer.addDragSupport(DND.DROP_COPY | DND.DROP_MOVE,
                new Transfer[] { ResourceTransfer.getInstance() },
                new RestrictionsViewerDragAdapter());

        GridDataFactory.swtDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(tree);

        @SuppressWarnings("unused")
        RestrictionsViewerToolTipSupport tooltip = new RestrictionsViewerToolTipSupport(
                restrictionsViewer, ToolTip.RECREATE, getModelProvider(), false, viewerColumnName);

        DropTarget dropTarget = new DropTarget(this, DND.DROP_MOVE | DND.DROP_COPY);
        dropTarget.setTransfer(new Transfer[] { PropertyTransfer.getInstance() });
        dropTarget.addDropListener(this);

        ForegroundColorPreserver preserver = new ForegroundColorPreserver(Display.getCurrent()
                .getSystemColor(SWT.COLOR_DARK_GRAY));
        preserver.applyTo(restrictionsViewer);

        createPopupMenu();

        refresh();
    }

    private void createPopupMenu() {
        Tree tree = restrictionsViewer.getTree();
        MenuManager menuManager = new MenuManager();
        Menu menu = menuManager.createContextMenu(tree);
        menuManager.setRemoveAllWhenShown(true);
        menuManager.addMenuListener(new IMenuListener() {
            @Override
            public void menuAboutToShow(IMenuManager manager) {
                Action addAction = new CreateRestrictionAction("Add Restriction...", null);
                addAction.setImageDescriptor(ImageDescriptor.createFromImage(addRestrictionImage));
                manager.add(addAction);

                Restriction selectedRestriction = getSelectedRestriction();

                if (selectedRestriction == null) {
                    return;
                }

                RestrictionsViewModel viewModel = (RestrictionsViewModel) restrictionsViewer
                        .getViewModel();

                boolean isInherited = viewModel.isInherited(selectedRestriction);
                Resource ownerClass = viewModel.getInheritedFrom(selectedRestriction);
                Resource qualifiedResource = RestrictionUtil
                        .getQualifiedResource(selectedRestriction);
                Resource onProperty = selectedRestriction.getPropertyResourceValue(OWL.onProperty);

                if (isInherited) {
                    manager.add(new CreateRestrictionAction("Narrow Down Restriction...",
                            selectedRestriction));
                }

                manager.add(new Separator());

                if (isInherited) {
                    manager.add(new OpenResourceAction("Open Owner Class", ownerClass));
                }

                if (onProperty != null) {
                    manager.add(new OpenResourceAction("Open Property", onProperty));
                }

                if (qualifiedResource != null && qualifiedResource.isResource()) {
                    manager.add(new OpenResourceAction("Open Resource", qualifiedResource));
                }

                manager.add(new OpenResourceAction("Open Restriction", selectedRestriction));

                if (!isInherited) {
                    manager.add(new Separator());
                    manager.add(new EditResourceAction(selectedRestriction));

                    manager.add(new Separator());
                    manager.add(new DeleteRestrictionAction(selectedRestriction));
                }

            }
        });
        tree.setMenu(menu);
    }

    private Menu createGeneralPopupMenu(Control control) {
        MenuManager menuManager = new MenuManager();
        Menu menu = menuManager.createContextMenu(control);
        menuManager.setRemoveAllWhenShown(true);
        menuManager.addMenuListener(new IMenuListener() {
            @Override
            public void menuAboutToShow(IMenuManager manager) {
                Action addAction = new CreateRestrictionAction("Add Restriction...", null);
                addAction.setImageDescriptor(ImageDescriptor.createFromImage(addRestrictionImage));
                manager.add(addAction);
            }
        });
        return menu;
    }

    protected Restriction getSelectedRestriction() {
        final OntResource selected = Selections.retrieveFirstAsType(
                restrictionsViewer.getSelection(), OntResource.class);

        if (selected == null) {
            return null;
        }

        final Restriction selectedRestriction = JenaUtil.asRestriction(selected);

        if (selectedRestriction == null) {
            return null;
        }

        return selectedRestriction;
    }

    public void drop(DropTargetEvent event) {
        logger.debug("drop called!");
        if (PropertyTransfer.getInstance().isSupportedType(event.currentDataType)) {
            Property property = (Property) event.data;
            logger.debug("drop -> property.getURI() = '" + property.getURI() + "'");
            logger.debug("drop -> property.hasProperty(RDFS.domain) = "
                    + property.hasProperty(RDFS.domain));
            EditRestrictionSuite.createRestriction(getModelProvider(), getShell(), property,
                    getResource());
        }
    }

    @Override
    public void refresh() {
        if (Widgets.isNullOrDisposedViewer(restrictionsViewer)) {
            return;
        }

        EclipseUIPlugin.getStandardDisplay().asyncExec(new Runnable() {
            @Override
            public void run() {
                OntModel currentModel = getModelProvider().getOntModel();
                OntResource root = currentModel.getOntResource(getResource());

                RestrictionsViewModel restrictionViewModel = new RestrictionsViewModel(
                        currentModel, JenaUtil.asOntClass(root));
                restrictionViewModel.init();
                restrictionViewModel.setComparator(comparator);

                Tree tree = restrictionsViewer.getTree();
                tree.setRedraw(false);
                restrictionsViewer.setViewModel(restrictionViewModel);
                restrictionsViewer.refresh();
                tree.setRedraw(true);
                layout();

                applyFilters();
            }
        });
    }

    private SelectionAdapter getSelectionAdapter(final TreeColumn column, final int index) {
        SelectionAdapter selectionAdapter = new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                comparator.setColumnIndex(index);
                Tree tree = restrictionsViewer.getTree();
                tree.setSortDirection(comparator.getDirection());
                tree.setSortColumn(tree.getColumn(index));

                // if the element is not collapsed an Exception can be thrown
                // during the viewer refresh
                Object[] expandedElements = restrictionsViewer.getVisibleExpandedElements();
                restrictionsViewer.collapseAll();

                RestrictionsViewModel viewModel = (RestrictionsViewModel) restrictionsViewer
                        .getViewModel();

                viewModel.setNeedsSort(true);
                restrictionsContentProvider.setRoots(viewModel.getRoots());

                restrictionsViewer.refresh();
                restrictionsViewer.setExpandedElements(expandedElements);
            }
        };
        return selectionAdapter;
    }

    /**
     * TODO inefficient way to get this information
     */
    private List<Restriction> getRelatedRestriction(Restriction res) {
        List<Restriction> localRelated = Lists.newArrayList();
        OntClass clazz = JenaUtil.asOntClass(getResource());
        if (clazz != null) {
            Map<Restriction, OntClass> restrictions = OntClassUtil.listRestrictionsByClass(clazz);

            for (Restriction localRestriction : restrictions.keySet()) {
                if (Objects.equals(res.getOnProperty(), localRestriction.getOnProperty())
                        && Objects.equals(res.getPropertyResourceValue(OWL2.onClass),
                                localRestriction.getPropertyResourceValue(OWL2.onClass))
                        && Objects.equals(res.getPropertyValue(OWL.hasValue),
                                localRestriction.getPropertyValue(OWL.hasValue))) {
                    localRelated.add(localRestriction);
                }
            }
            localRelated.remove(res);
        }
        return localRelated;
    }

    private static class RestrictionsViewerToolTipSupport extends ColumnViewerToolTipSupport {
        private IModelProvider modelProvider;
        private final List<RestrictionColumnName> viewerColumnName;
        private final ResourceTreeViewer viewer;

        protected RestrictionsViewerToolTipSupport(ResourceTreeViewer viewer, int style,
                IModelProvider modelProvider, boolean manualActivation,
                List<RestrictionColumnName> viewerColumnName) {
            super(viewer, style, manualActivation);
            this.modelProvider = modelProvider;
            this.viewerColumnName = viewerColumnName;
            this.viewer = viewer;
        }

        @Override
        protected Composite createViewerToolTipContentArea(Event event, ViewerCell cell,
                Composite parent) {

            if (cell == null) {
                return null;
            }

            int columnIndex = cell.getColumnIndex();
            OntResource item = (OntResource) cell.getElement();
            Restriction restriction = JenaUtil.asRestriction(item);

            if (viewerColumnName.get(columnIndex).equals(RestrictionColumnName.ON_PROPERTY)
                    || viewerColumnName.get(columnIndex).equals(RestrictionColumnName.CARDINALITY)) {

                // Owner Class Section
                RestrictionsViewModel viewModel = (RestrictionsViewModel) viewer.getViewModel();
                Resource ownerClass = viewModel.getInheritedFrom(restriction);

                RestrictionToolTipContent tooltip = new RestrictionToolTipContent(parent,
                        modelProvider, restriction, ownerClass, viewer.getLabelProvider());

                return tooltip;
            }
            if (viewerColumnName.get(columnIndex).equals(RestrictionColumnName.RESOURCE)) {
                Resource qualifiedResource = RestrictionUtil.getQualifiedResource(restriction);
                if (qualifiedResource != null) {
                    return new ResourceToolTipContent(parent, modelProvider, qualifiedResource,
                            SWT.NONE);
                }
            }

            IBaseLabelProvider labelProvider = viewer.getLabelProvider();
            if (labelProvider instanceof RestrictionsLabelProvider) {
                RestrictionsLabelProvider restrictionsLabelProvider = (RestrictionsLabelProvider) labelProvider;
                Object element = cell.getElement();

                setText(restrictionsLabelProvider.getColumnText(element, columnIndex));
                setImage(restrictionsLabelProvider.getColumnImage(element, columnIndex));
                if (getText(event) == null && getImage(event) == null) {
                    return null;
                }
            }

            return super.createViewerToolTipContentArea(event, cell, parent);
        }
    }

    private class CreateRestrictionAction extends Action {

        private Restriction superRestriction;

        public CreateRestrictionAction(String text, Restriction restriction) {
            super(text);
            this.superRestriction = restriction;
        }

        @Override
        public void run() {
            if (superRestriction == null) {
                EditRestrictionSuite.createRestriction(getModelProvider(), getShell(), null,
                        getResource());
            }
            else {
                EditRestrictionSuite.createNarrowRestriction(getModelProvider(), getShell(),
                        superRestriction, getResource());
            }
        }
    }

    private class EditResourceAction extends Action {
        private Restriction toEditRes;

        public EditResourceAction(Restriction res) {
            super("Edit");
            this.toEditRes = res;
        }

        @Override
        public void run() {
            List<Restriction> relatedRestriction = getRelatedRestriction(toEditRes);
            EditRestrictionSuite.editRestrictionAnonymousCheck(getModelProvider(), getShell(),
                    toEditRes, getResource(), relatedRestriction);
        }
    }

    private class DeleteRestrictionAction extends Action {
        private Restriction toDeleteRes;

        public DeleteRestrictionAction(Restriction toDeleteRes) {
            super("Delete");
            setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(CorePlugin.PLUGIN_ID,
                    CorePluginImages.IMG_DELETE));
            this.toDeleteRes = toDeleteRes;
        }

        @Override
        public void run() {
            EditRestrictionSuite.removeRestrictionAsSuperclass(getModelProvider(), getShell(),
                    toDeleteRes, getResource());
        }

    }

    private static class OpenResourceAction extends Action {

        private Resource toOpenRes;

        public OpenResourceAction(String title, Resource res) {
            super(title);
            this.toOpenRes = res;
        }

        @Override
        public void run() {
            CorePlugin.getDefault().openResource(toOpenRes);
        }
    }

    private class DoubleClickListener implements IDoubleClickListener {

        public DoubleClickListener() {
        }

        @Override
        public void doubleClick(DoubleClickEvent event) {
            final Restriction selectedRestriction = getSelectedRestriction();
            RestrictionsViewModel viewModel = (RestrictionsViewModel) restrictionsViewer
                    .getViewModel();
            boolean isInherited = viewModel.isInherited(selectedRestriction);

            if (isInherited) {
                EditRestrictionSuite.createNarrowRestriction(getModelProvider(), getShell(),
                        selectedRestriction, getResource());
            }
            else {
                List<Restriction> relatedRestriction = getRelatedRestriction(selectedRestriction);
                EditRestrictionSuite.editRestrictionAnonymousCheck(getModelProvider(), getShell(),
                        selectedRestriction, getResource(), relatedRestriction);
            }
        }

    }

    /**
     * Preserve the font colour of the highlighted line. Note that if the
     * foreground colour of the row is the same as the background colour, then
     * the text will essentially disappear.
     * 
     * http://www.dzone.com/snippets/javaswt-preserve-font-colour
     */
    public class ForegroundColorPreserver {

        private Color color;

        public ForegroundColorPreserver(Color color) {
            this.color = color;
        }

        /**
         * Preserve the table font color by adding a paint listener to redraw
         * the viewer item text in the expected color.
         * 
         * Note that the row will be hard to read or illegible if the foreground
         * Color is close to the background color.
         * 
         * @param tree
         *            the table to paint.
         */
        public void applyTo(Viewer viewer) {
            viewer.getControl().addListener(SWT.PaintItem, new Listener() {
                public void handleEvent(Event event) {
                    ForegroundColorPreserver.this.handleEvent(event);
                }
            });
        }

        /*
         * Repaint the text. Note the one pixel jog to the right. This may
         * change in a subsequent SWT release.
         */
        protected void handleEvent(Event event) {
            if ((event.detail & SWT.SELECTED) != SWT.SELECTED) {
                return;
            }

            TreeItem item = (TreeItem) event.item;

            Object element = item.getData();

            if (!(element instanceof OntResource)) {
                return;
            }

            Restriction selected = JenaUtil.asRestriction((OntResource) element);

            boolean isInherited = ((RestrictionsViewModel) restrictionsViewer.getViewModel())
                    .isInherited(selected);
            boolean isInBaseModel = restrictionsViewer.getViewModel().getCurrentModel()
                    .getBaseModel().contains(null, RDFS.subClassOf, selected);

            if ((isInherited || !isInBaseModel)) {

                event.gc.setForeground(color);
                Rectangle rect = item.getTextBounds(event.index);
                // Out-by-one x pixel!
                event.gc.drawString(item.getText(event.index), rect.x, rect.y + 1, true);
            }
        }
    }

    /**
     * Comparator for the column of the Restriction Viewer. The comparison is
     * performed on multiple columns with this order:
     * <ol>
     * <li>ON_PROPERTY</li>
     * <li>RESOURCE</li>
     * <li>CARDINALITY</li>
     * </ol>
     * 
     * @author Simone Rondelli
     */
    private static final class RestrictionColumnComparator implements Comparator<OntResource> {

        private final RestrictionsLabelProvider labelProvider;
        private int columnIndex;
        private boolean descending;

        private List<RestrictionColumnName> columns;

        public RestrictionColumnComparator(RestrictionsLabelProvider labelProvider,
                List<RestrictionColumnName> columns) {
            this.labelProvider = labelProvider;
            this.columnIndex = -1;
            this.descending = true;
            this.columns = columns;
        }

        public void setColumnIndex(int columnIndex) {
            if (this.columnIndex == columnIndex) {
                descending = !descending;
            }
            else {
                this.columnIndex = columnIndex;
                descending = true;
            }
        }

        public int getDirection() {
            return descending ? SWT.UP : SWT.DOWN;
        }

        @Override
        public int compare(OntResource o1, OntResource o2) {
            if (columnIndex == -1) {
                return 0;
            }

            Restriction res1 = JenaUtil.asRestriction(o1);
            Restriction res2 = JenaUtil.asRestriction(o2);

            List<RestrictionColumnName> columnSortingOrder = Lists.newArrayList(
                    RestrictionColumnName.ON_PROPERTY, RestrictionColumnName.RESOURCE,
                    RestrictionColumnName.CARDINALITY);

            RestrictionColumnName currentColumn = columns.get(columnIndex);

            int sortIndex = columnIndex;
            columnSortingOrder.remove(currentColumn);
            int result = 0;

            do {

                String label1 = Strings.nullToEmpty(labelProvider.getColumnText(res1, sortIndex));
                String label2 = Strings.nullToEmpty(labelProvider.getColumnText(res2, sortIndex));

                if (descending) {
                    result = label1.compareToIgnoreCase(label2);
                }
                else {
                    result = label2.compareToIgnoreCase(label1);
                }

                if (columnSortingOrder.isEmpty()) {
                    break;
                }

                RestrictionColumnName columnName = columnSortingOrder.remove(0);
                sortIndex = columns.indexOf(columnName);

                // if the elements on the current column are equals the order
                // will be performed on the elements on the next column
                // following the order defined in columnSortingOrder
            } while (result == 0);

            return result;
        }
    }

    private class RestrictionsViewerDragAdapter extends ViewerDragAdapter {
        @Override
        protected StructuredViewer getViewer() {
            return restrictionsViewer;
        }

        @Override
        protected RDFNode getSelectedNode(int column) {
            if (column == -1 || column >= viewerColumnName.size()) {
                return null;
            }
            RestrictionColumnName columnName = viewerColumnName.get(column);
            if (columnName == null) {
                return null;
            }

            Restriction restriction = getSelectedRestriction();
            if (columnName.equals(RestrictionColumnName.ON_PROPERTY)) {
                return restriction.getPropertyResourceValue(OWL.onProperty);
            }
            if (columnName.equals(RestrictionColumnName.CARDINALITY)) {
                return restriction;
            }
            if (columnName.equals(RestrictionColumnName.RESOURCE)) {
                return RestrictionUtil.getQualifiedResource(restriction);
            }

            return null;
        }

        @Override
        protected OntModel getOntModel() {
            IModelProvider modelProvider = ModelResourceRestrictionsContentPart.this
                    .getModelProvider();
            if (modelProvider != null) {
                return modelProvider.getOntModel();
            }
            return null;
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        if (!Widgets.isNullOrDisposedViewer(restrictionsViewer)) {
            restrictionsViewer.dispose();
        }
    }

}
