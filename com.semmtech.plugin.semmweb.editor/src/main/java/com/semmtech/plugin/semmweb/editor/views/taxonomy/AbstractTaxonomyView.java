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


import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.bag.TreeBag;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.TextStyle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;

import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFList;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.shared.ClosedException;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.XSD;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.actions.IModelVisibilityLabelProvider;
import com.semmtech.plugin.semmweb.core.actions.ModelVisibilityListener;
import com.semmtech.plugin.semmweb.core.actions.ModelVisibilityMenuProvider;
import com.semmtech.plugin.semmweb.core.commands.Commands;
import com.semmtech.plugin.semmweb.core.dialog.LiteralStatementInputDialog;
import com.semmtech.plugin.semmweb.core.dialog.ResourceStatementInputDialog;
import com.semmtech.plugin.semmweb.core.dialog.StatementInputDialog;
import com.semmtech.plugin.semmweb.core.dnd.DndUtils;
import com.semmtech.plugin.semmweb.core.dnd.OntClassTransfer;
import com.semmtech.plugin.semmweb.core.dnd.PropertyTransfer;
import com.semmtech.plugin.semmweb.core.dnd.ResourceTransfer;
import com.semmtech.plugin.semmweb.core.extensionpoint.CoreExtensions;
import com.semmtech.plugin.semmweb.core.extensionpoint.IModelValidator;
import com.semmtech.plugin.semmweb.core.forms.editor.OntologyFormEditor;
import com.semmtech.plugin.semmweb.core.model.IModelProvider;
import com.semmtech.plugin.semmweb.core.model.ModelTransaction;
import com.semmtech.plugin.semmweb.core.model.OpenResourceEventListener;
import com.semmtech.plugin.semmweb.core.model.ResourceStatements;
import com.semmtech.plugin.semmweb.core.model.events.ModelActivatedEvent;
import com.semmtech.plugin.semmweb.core.model.events.ModelChangedEvent;
import com.semmtech.plugin.semmweb.core.model.events.NamespacePrefixChangedEvent;
import com.semmtech.plugin.semmweb.core.model.events.SubModelAddedEvent;
import com.semmtech.plugin.semmweb.core.model.events.SubModelRemovedEvent;
import com.semmtech.plugin.semmweb.core.viewers.ResourceViewerToolTipSupport;
import com.semmtech.plugin.semmweb.core.views.AbstractModelListenerView;
import com.semmtech.plugin.semmweb.core.widgets.DropComposite;
import com.semmtech.plugin.semmweb.core.widgets.trees.ResourceTreeData;
import com.semmtech.plugin.semmweb.core.wizards.CreateClassWizard;
import com.semmtech.plugin.semmweb.core.wizards.CreateResourceWizard;
import com.semmtech.plugin.semmweb.editor.EditorPlugin;
import com.semmtech.semantics.sparql.PathUtil;
import com.semmtech.semantics.sparql.QueryBuilder;
import com.semmtech.semantics.sparql.Triples;
import com.semmtech.semantics.util.JenaUtil;
import com.semmtech.ui.plugin.jobs.JobWithMonitor;
import com.semmtech.ui.plugin.util.FontUtil;
import com.semmtech.ui.plugin.util.Selections;
import com.semmtech.ui.plugin.viewers.LazyTreeViewer;
import com.semmtech.ui.plugin.viewers.ViewerSelectionProvider;
import com.semmtech.ui.plugin.widgets.BusyIndicatorComponent;
import com.semmtech.ui.plugin.widgets.Widgets;


/**
 * 
 * @author Sander Stolk
 * @author Simone Rondelli
 */
public abstract class AbstractTaxonomyView extends AbstractModelListenerView implements
        IPropertyChangeListener, IClassHierarchyProvider {
    private static final Logger logger = Logger.getLogger(AbstractTaxonomyView.class);

    protected final static String PARAM_VIEWER_EXPANDED_ELEMENTS = "expanded elements";
    protected final static String PARAM_CURRENT_ROOT_RESOURCES = "var:currentRootResources";
    protected final static String PARAM_SHOW_TOP_CLASSES = "var:showTopClasses";
    protected final static String PARAM_SHOW_INSTANCE_COUNT = "var:showInstanceCount";
    protected final static String PARAM_SHOW_BASE_MODEL = "var:showBaseModel";
    protected final static String PARAM_VISIBLE_MODEL_URIS = "triplesView.visibleModelURIS";

    protected final static String SHOW_ALL_TOP_CLASSES_STATE = "all";
    protected final static String SHOW_ALL_TOP_PARENTS_STATE = "allParents";
    protected final static String SHOW_SELECTED_RESOURCES_STATE = "selectedResources";

    /**
     * Inner class used to provide the tree viewer with labels.
     * 
     * @author Mike Henrichs
     * 
     */
    private class TaxonomyTreeViewerListener implements ITreeViewerListener {
        @Override
        public void treeCollapsed(TreeExpansionEvent event) {
            if (!restoringExpandedElements && !settingViewerInput) {
                List<Object> elements = Lists.newArrayList(event.getTreeViewer()
                        .getExpandedElements());
                elements.remove(event.getElement());
                setStateParameter(PARAM_VIEWER_EXPANDED_ELEMENTS, elements.toArray());
            }
        }

        @Override
        public void treeExpanded(TreeExpansionEvent event) {
            if (!restoringExpandedElements && !settingViewerInput) {
                List<Object> elements = Lists.newArrayList(event.getTreeViewer()
                        .getExpandedElements());
                elements.add(event.getElement());
                setStateParameter(PARAM_VIEWER_EXPANDED_ELEMENTS, elements.toArray());
            }
        }
    }

    private String title;
    private OntModel currentModel;

    private TaxonomyViewModel viewModel;

    private Object mutex = new Object();

    private boolean restoringExpandedElements = false;
    private boolean settingViewerInput = false;

    private TaxonomyTreeContentProvider contentProvider;
    private final List<Resource> currentRootResources;
    private ResourceTreeData draggedResourceData = null;
    private boolean draggedFromTaxonomy = false;
    private boolean droppedOntoTaxonomy = false;

    private boolean showInstanceCount;
    protected LazyTreeViewer viewer;
    private Tree tree;
    private final Map<OntClass, TreeBag<OntResource>> classHierarchy;
    private Font boldFont;
    private DropComposite dropComposite;

    private ModelVisibilityMenuProvider modelVisibilityProvider;

    private CreateModelViewJob refreshJob;
    private CreateModelViewJob modelChangedJob;
    private boolean modelChangedJobQueued;

    private ViewerSelectionProvider selectionProvider;
    private OpenResourceEventListener openResourceListener;

    private DragSource dndSource;

    private BusyIndicatorComponent busyIndicator;

    public AbstractTaxonomyView(String title) {
        this.title = title;
        classHierarchy = Maps.newHashMap();
        currentRootResources = Lists.newArrayList();
        selectionProvider = new ViewerSelectionProvider();
        openResourceListener = new OpenResourceListener(this);

    }

    /**
     * Returns the class type shown by the taxonomy, which needs to be an
     * instance of rdfs:Class.
     */
    abstract public Resource getTaxonomyResourceType();

    /**
     * Returns the default root resource of the taxonomy, which needs to be an
     * instance of the type returned by the function
     * <code>getTaxonomyResourceType</code>.
     */
    abstract public Resource getTaxonomyDefaultRoot();

    /**
     * Returns whether the default root resource returned by the function
     * <code>getTaxonomyDefaultRoot</code> should be available/selectable via
     * the view's option menu.
     */
    abstract boolean isTaxonomyDefaultRootSelectable();

    @Override
    public void init(IViewSite site) throws PartInitException {
        super.init(site);
        CorePlugin.getDefault().getPreferenceStore().addPropertyChangeListener(this);
        EditorPlugin.getDefault().getPreferenceStore().addPropertyChangeListener(this);

    }

    @Override
    public void dispose() {
        cleanup();
        boldFont.dispose();
        EditorPlugin.getDefault().getPreferenceStore().removePropertyChangeListener(this);
        CorePlugin.getDefault().getPreferenceStore().removePropertyChangeListener(this);
        getSite().setSelectionProvider(null);
        super.dispose();
    }

    @Override
    public void setFocus() {
        if (!Widgets.isNullOrDisposed(tree)) {
            tree.setFocus();
        }
    }

    @Override
    public void createPartControl(Composite parent) {
        super.createPartControl(parent);

        modelVisibilityProvider = new ModelVisibilityMenuProvider(true);
        modelVisibilityProvider.setChangeListener(new ModelVisibilityListener() {

            @Override
            public void visibleModelsChanged(List<String> visibleUris) {
                setStateParameter(PARAM_VISIBLE_MODEL_URIS, visibleUris);
                if (getShowTopClasses().equals(SHOW_SELECTED_RESOURCES_STATE)) {
                    setShowTopClasses(true, true, false);
                }
                executeRefreshJob();
            }
        });
        modelVisibilityProvider.setMenuItemLabelProvider(new IModelVisibilityLabelProvider() {

            @Override
            public String getText(String uri) {
                String shortForm = getOntModel().shortForm(uri);
                if (shortForm.endsWith(":")) {
                    if (shortForm.length() > 1) {
                        shortForm = shortForm.substring(0, shortForm.length() - 1);
                    }
                }
                return shortForm;
            }
        });
        IActionBars actionBars = getViewSite().getActionBars();
        IMenuManager actionBarManager = actionBars.getMenuManager();
        actionBarManager.addMenuListener(modelVisibilityProvider);
        actionBarManager.addMenuListener(new TaxonomyDefaultRootMenuProvider());

        GridLayoutFactory.fillDefaults().numColumns(1).spacing(0, 0).applyTo(parent);
        boldFont = FontUtil.getModifiedFont(getParent(), SWT.BOLD);

        contentProvider = new TaxonomyTreeContentProvider();
        contentProvider.setRoots(currentRootResources);
        if (tree != null) {
            tree.setItemCount(currentRootResources.size());
        }
        getSite().setSelectionProvider(selectionProvider);
        setInitialized(true);
    }

    private void createControls() {
        // dispose all
        clearControls();

        // create all
        createTaxonomyTree();
        createDropRegion();
        // refresh layout
        layoutParent(true, true);
    }

    private void createContextMenu() {
        if (viewer != null && !Widgets.isNullOrDisposed(viewer.getControl())) {
            MenuManager menuManager = new MenuManager();
            Menu menu = menuManager.createContextMenu(viewer.getControl());
            if (viewer != null) {
                viewer.getControl().setMenu(menu);
            }
            getSite().registerContextMenu(menuManager, viewer);
        }
    }

    @Override
    public void modelActivated(ModelActivatedEvent event) {
        refreshFromModel();
    }

    @Override
    public void subModelAdded(SubModelAddedEvent event) {
        refreshFromModel();
    }

    @Override
    public void subModelRemoved(SubModelRemovedEvent event) {
        refreshFromModel();
    }

    @Override
    public void namespacePrefixChanged(NamespacePrefixChangedEvent event) {
        refreshFromModel();
    }

    private void refreshFromModel() {
        clearControls();

        currentModel = getOntModel();
        classHierarchy.clear();

        modelVisibilityProvider.setInput(getModelProvider(), getVisibleModelURIs());
        if (hasModelProvider()) {
            restoreState();
            executeRefreshJob();
        }
    }

    private void executeModelChangedJob() {
        if ((currentModel == null) || (viewer == null) || (Widgets.isNullOrDisposed(tree))) {
            modelChangedJobQueued = false;
            return;
        }
        if (Widgets.isNullOrDisposedViewer(viewer)) {
            return;
        }

        if (refreshJob != null || modelChangedJob != null) {
            modelChangedJobQueued = true;
            return;
        }

        modelChangedJobQueued = false;

        modelChangedJob = new CreateModelViewJob();
        modelChangedJob.setRule(OntologyFormEditor.MUTEX_RULE);
        modelChangedJob.setUser(false);
        modelChangedJob.schedule(getModelProvider());
        modelChangedJob.addJobDoneListener(new ModelJobListener() {
            @Override
            public void done(final TaxonomyViewModel newViewModel) {
                Display.getDefault().asyncExec(new Runnable() {
                    @Override
                    public void run() {
                        if ((currentModel == null) || (viewer == null)
                                || (Widgets.isNullOrDisposed(tree))) {
                            modelChangedJob = null;
                            modelChangedJobQueued = false;
                            return;
                        }

                        try {
                            synchronized (mutex) {

                                TaxonomyViewModel oldViewModel = viewModel;
                                viewModel = newViewModel;

                                contentProvider.setViewModel(viewModel);
                                TaxonomyLabelProvider taxonomyLabelProvider = (TaxonomyLabelProvider) viewer
                                        .getLabelProvider();
                                taxonomyLabelProvider.setViewModel(viewModel);

                                logger.trace("Determining the delta for the new viewModel");

                                DeltaTaxonomyViewModel relationsRemoved = oldViewModel
                                        .difference(newViewModel);
                                DeltaTaxonomyViewModel relationsAdded = newViewModel
                                        .difference(oldViewModel);

                                logger.trace("Determining the delta for the new viewModel has finished");
                                logger.trace("Adjusting the TreeViewer");

                                // Removed roots
                                for (OntClass rootClass : relationsRemoved.getRootClasses()) {
                                    if (currentRootResources.contains(rootClass)) {
                                        currentRootResources.remove(rootClass);
                                    }
                                    contentProvider.removeRoot(rootClass);
                                }

                                // Added roots
                                if (!getShowTopClasses().equals(SHOW_SELECTED_RESOURCES_STATE)) {
                                    boolean parentsOnly = (getShowTopClasses()
                                            .equals(SHOW_ALL_TOP_PARENTS_STATE));
                                    for (OntClass rootClass : relationsAdded.getRootClasses()) {
                                        if (!parentsOnly
                                                || (newViewModel.getChildClassCount(rootClass) > 0)) {
                                            if (!currentRootResources.contains(rootClass)) {
                                                currentRootResources.add(rootClass);
                                            }

                                            contentProvider.addRoot(rootClass);
                                        }
                                    }
                                }

                                // Removed childOfs
                                StmtIterator statementIter = relationsRemoved.listStatements(null,
                                        TaxonomyViewModel.Vocabulary.isChildOf, (RDFNode) null);
                                while (statementIter.hasNext()) {
                                    Statement statement = statementIter.next();
                                    OntClass parent = JenaUtil.asOntClass(statement.getObject()
                                            .asResource(), currentModel);
                                    OntClass child = JenaUtil.asOntClass(statement.getSubject()
                                            .asResource(), currentModel);

                                    contentProvider.removeChild(child, parent);
                                }

                                // Added childOfs
                                statementIter = relationsAdded.listStatements(null,
                                        TaxonomyViewModel.Vocabulary.isChildOf, (RDFNode) null);
                                while (statementIter.hasNext()) {
                                    Statement statement = statementIter.next();
                                    OntClass parent = JenaUtil.asOntClass(statement.getObject()
                                            .asResource(), currentModel);
                                    OntClass child = JenaUtil.asOntClass(statement.getSubject()
                                            .asResource(), currentModel);

                                    contentProvider.addChild(child, parent);
                                }

                                Set<Resource> resourcesToUpdate = Sets.newHashSet();

                                // Altered texts
                                Set<Resource> resourcesWithPropertyAltered = relationsRemoved
                                        .listSubjectsWithProperty(TaxonomyViewModel.Vocabulary.text)
                                        .toSet();
                                Set<Resource> resourcesWithPropertyAdded = relationsAdded
                                        .listSubjectsWithProperty(TaxonomyViewModel.Vocabulary.text)
                                        .toSet();
                                resourcesWithPropertyAltered.retainAll(resourcesWithPropertyAdded);
                                resourcesToUpdate.addAll(resourcesWithPropertyAltered);

                                // Altered direct instance counts
                                resourcesWithPropertyAltered = relationsRemoved
                                        .listSubjectsWithProperty(
                                                TaxonomyViewModel.Vocabulary.directInstanceCount)
                                        .toSet();
                                resourcesWithPropertyAdded = relationsAdded
                                        .listSubjectsWithProperty(
                                                TaxonomyViewModel.Vocabulary.directInstanceCount)
                                        .toSet();
                                resourcesWithPropertyAltered.retainAll(resourcesWithPropertyAdded);
                                resourcesToUpdate.addAll(resourcesWithPropertyAltered);

                                // Altered indirect instance counts
                                resourcesWithPropertyAltered = relationsRemoved
                                        .listSubjectsWithProperty(
                                                TaxonomyViewModel.Vocabulary.indirectInstanceCount)
                                        .toSet();
                                resourcesWithPropertyAdded = relationsAdded
                                        .listSubjectsWithProperty(
                                                TaxonomyViewModel.Vocabulary.indirectInstanceCount)
                                        .toSet();
                                resourcesWithPropertyAltered.retainAll(resourcesWithPropertyAdded);
                                resourcesToUpdate.addAll(resourcesWithPropertyAltered);

                                contentProvider.updateResources(resourcesToUpdate);

                                logger.trace("Adjusting the TreeViewer has finished");
                            }
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                        finally {
                            modelChangedJob = null;
                            if (modelChangedJobQueued) {
                                executeModelChangedJob();
                            }
                        }
                    }

                });
            }
        });
    }

    @Override
    public void modelChanged(ModelChangedEvent event) {
        // TODO: Perhaps only obtain new view model if the statements
        // added/removed contain predicates that may truly alter the view model.
        executeModelChangedJob();
    }

    private void clearControls() {
        Widgets.disposeIfExists(tree);
        Widgets.disposeIfExists(dropComposite);
    }

    @Override
    public void partActivated(IWorkbenchPart part) {
        super.partActivated(part);

        if (part instanceof OntologyFormEditor) {
            OntologyFormEditor editor = (OntologyFormEditor) part;
            editor.addOpenResourceEventListener(openResourceListener);
        }
    }

    public void updateViewerWithActiveResource() {
        if (Widgets.isNullOrDisposedViewer(viewer) || (currentRootResources.isEmpty())) {
            return;
        }

        // the resource can be null if has been closed
        Resource resource = CorePlugin.getDefault().getActiveOpenResource();
        TaxonomyLabelProvider taxonomyLabelProvider = (TaxonomyLabelProvider) viewer
                .getLabelProvider();
        Resource previousActiveResource = taxonomyLabelProvider.getActiveResource();

        if (resource != null && resource.equals(previousActiveResource)) {
            return;
        }

        taxonomyLabelProvider.setActiveResource(resource);

        if (hasModelProvider()) {

            List<Resource> viewerElementsToUpdate = Lists.newArrayList();
            if (resource != null) {
                Model model = getOntModel();

                for (Resource currentRootResource : currentRootResources) {
                    Set<List<Resource>> paths = findPaths(model, currentRootResource, resource);
                    for (List<Resource> path : paths) {
                        setExpandedState(path, true);
                    }
                }

                viewerElementsToUpdate.add(resource);
            }

            if (previousActiveResource != null) {
                viewerElementsToUpdate.add(previousActiveResource);
            }
            contentProvider.updateResources(viewerElementsToUpdate);
        }
    }

    private void setExpandedState(List<Resource> path, boolean state) {
        for (int i = 0; i < path.size(); i++) {
            ResourceTreeData item = ResourceTreeData.createItemFromPath(path.subList(0, i + 1));
            viewer.setExpandedState(item, state);
        }
    }

    private void executeRefreshJob() {
        Job.getJobManager().cancel(AbstractTaxonomyView.this);
        modelChangedJob = null;

        // Ignore if the parent composite has been disposed
        if (Widgets.isNullOrDisposed(getParent())) {
            return;
        }

        Widgets.disposeIfExists(busyIndicator);
        busyIndicator = new BusyIndicatorComponent(getParent(), "Refreshing...");
        GridDataFactory.fillDefaults().grab(true, false).indent(5, 5).applyTo(busyIndicator);
        busyIndicator.startAnimation();
        layoutParent(true, true);

        refreshJob = new CreateModelViewJob();
        refreshJob.setRule(OntologyFormEditor.MUTEX_RULE);
        refreshJob.setUser(false);
        refreshJob.schedule(getModelProvider());
        refreshJob.addJobDoneListener(new ModelJobListener() {
            @Override
            public void done(final TaxonomyViewModel newViewModel) {
                Display.getDefault().asyncExec(new Runnable() {
                    @Override
                    public void run() {
                        if (Widgets.isNullOrDisposed(getParent()) || !hasModelProvider()) {
                            return;
                        }

                        viewModel = newViewModel;
                        contentProvider = new TaxonomyTreeContentProvider();
                        contentProvider.setViewModel(viewModel);

                        createControls();
                        createContextMenu();
                        layoutParent(true, true);
                        refreshViewer(true);
                        createContextMenu();

                        refreshJob = null;
                        if (modelChangedJobQueued) {
                            executeModelChangedJob();
                        }
                    }
                });
            }
        });
    }

    private void createTaxonomyTree() {
        Widgets.disposeIfExists(tree);
        if (Widgets.isNullOrDisposed(getParent())) {
            return;
        }

        Widgets.disposeIfExists(busyIndicator);

        viewer = new LazyTreeViewer(getParent(), SWT.VIRTUAL);
        viewer.setUseHashlookup(true);

        dndSource = new DragSource(viewer.getTree(), DND.DROP_MOVE | DND.DROP_COPY);
        dndSource.setTransfer(new Transfer[] { OntClassTransfer.getInstance(),
                ResourceTransfer.getInstance(), TextTransfer.getInstance() });
        dndSource.addDragListener(new DragSourceAdapter() {

            @Override
            public void dragSetData(DragSourceEvent event) {
                draggedResourceData = Selections.retrieveFirstAsType(viewer.getSelection(),
                        ResourceTreeData.class);
                DndUtils.setDragSetData(event, getOntModel(), viewer, null);
                draggedFromTaxonomy = true;
            }

            @Override
            public void dragFinished(DragSourceEvent event) {
                draggedFromTaxonomy = false;
                droppedOntoTaxonomy = false;
            }
        });

        viewer.addDropSupport(DND.DROP_COPY | DND.DROP_MOVE,
                new Transfer[] { PropertyTransfer.getInstance(), OntClassTransfer.getInstance() },
                new ViewerDropAdapter(viewer) {

                    private int dropLocation;
                    private ResourceTreeData targetData;
                    private boolean dropPerformed = false;

                    @Override
                    public void dragOver(DropTargetEvent event) {
                        dropLocation = determineLocation(event);
                        Object target = determineTarget(event);
                        if (target instanceof ResourceTreeData) {
                            targetData = (ResourceTreeData) target;
                        }
                        else {
                            targetData = null;
                        }
                        if (dropLocation == LOCATION_AFTER) {
                            event.feedback = DND.FEEDBACK_EXPAND | DND.FEEDBACK_SCROLL;
                        }
                    }

                    @Override
                    public void drop(DropTargetEvent event) {
                        if (!hasModelProvider()) {
                            return;
                        }

                        if (PropertyTransfer.getInstance().isSupportedType(event.currentDataType)) {
                            if (dropLocation == LOCATION_ON) {
                                Property property = (Property) PropertyTransfer.getInstance()
                                        .nativeToJava(event.currentDataType);
                                Resource dropped = targetData;

                                if (dropped != null) {
                                    OntModel model = getOntModel();
                                    Shell shell = getSite().getShell();
                                    String title = "Create Statement";
                                    String message = "Create statement with given subject and predicate.";
                                    StatementInputDialog dialog = null;
                                    Resource range = null;
                                    for (Statement rangeStatement : model
                                            .listStatements(
                                                    new SimpleSelector(property, RDFS.range,
                                                            (RDFNode) null)).toList()) {
                                        if (rangeStatement.getObject() != null
                                                && !rangeStatement.getObject().isLiteral()) {
                                            range = (Resource) rangeStatement.getObject();
                                            break;
                                        }
                                    }
                                    if (range == null) {
                                        dialog = new LiteralStatementInputDialog(shell, title,
                                                message);
                                        ((LiteralStatementInputDialog) dialog)
                                                .setDatatypeVisible(true);
                                        ((LiteralStatementInputDialog) dialog)
                                                .setLanguageVisible(true);
                                    }
                                    else if (range.equals(RDFS.Literal)) {
                                        dialog = new LiteralStatementInputDialog(shell, title,
                                                message);
                                    }
                                    else if (range.equals(XSD.nonNegativeInteger)) {
                                        dialog = new LiteralStatementInputDialog(shell, title,
                                                message);
                                        ((LiteralStatementInputDialog) dialog)
                                                .setDatatypeVisible(true);
                                        ((LiteralStatementInputDialog) dialog)
                                                .setLanguageVisible(false);
                                        ((LiteralStatementInputDialog) dialog)
                                                .setDatatype(XSD.nonNegativeInteger);
                                    }
                                    else {
                                        dialog = new ResourceStatementInputDialog(shell, title,
                                                message);
                                        ((ResourceStatementInputDialog) dialog)
                                                .setAllowedResourceType(range);
                                    }
                                    dialog.setModel(model);
                                    dialog.setSubject(dropped);
                                    dialog.setProperties(Arrays.asList(property));
                                    dialog.setSelectedProperty(0);

                                    if (dialog.open() == Window.OK) {
                                        Statement statement = dialog.createStatement();
                                        if (statement != null) {
                                            String transactionDescription = "Added new statement due to drop of property on resource";
                                            ModelTransaction transaction = getModelProvider()
                                                    .createTransaction(transactionDescription);
                                            model.add(statement);
                                            getModelProvider().commitTransaction(transaction);
                                        }
                                    }
                                }
                            }
                        }
                        else if (OntClassTransfer.getInstance().isSupportedType(
                                event.currentDataType)) {
                            Resource dragged = (Resource) OntClassTransfer.getInstance()
                                    .nativeToJava(event.currentDataType);
                            Resource parent = null;
                            if (dropLocation == LOCATION_BEFORE) {
                                parent = (Resource) targetData.getParent();
                            }
                            else if (dropLocation == LOCATION_AFTER) {
                                parent = (Resource) targetData.getParent();
                            }
                            else if (dropLocation == LOCATION_ON) {
                                parent = targetData;
                            }

                            if (parent != null && !parent.equals(dragged)) {
                                droppedOntoTaxonomy = true;

                                String transactionDescription = "Added subClassOf statement, due to drop of resource within taxonomy";
                                ModelTransaction transaction = getModelProvider()
                                        .createTransaction(transactionDescription);

                                ResourceStatements.createResourceAsSubclassStatements(dragged,
                                        parent);

                                if (draggedFromTaxonomy && droppedOntoTaxonomy) {
                                    if (event.detail == DND.DROP_MOVE
                                            && draggedResourceData != null) {
                                        Resource dragParent = (Resource) draggedResourceData
                                                .getParent();
                                        if ((dragParent != null)
                                                && ((dragged.equals(draggedResourceData) == false) || (parent
                                                        .equals(dragParent) == false))) {

                                            ResourceStatements
                                                    .createRemoveResourceAsSubclassStatements(
                                                            draggedResourceData, dragParent);
                                        }
                                    }
                                }
                                getModelProvider().commitTransaction(transaction);
                            }
                        }
                    }

                    @Override
                    public boolean performDrop(Object data) {
                        return dropPerformed;
                    }

                    @Override
                    public boolean validateDrop(Object target, int operation,
                            TransferData transferType) {
                        if (PropertyTransfer.getInstance().isSupportedType(transferType)) {
                            return true;
                        }
                        else if (OntClassTransfer.getInstance().isSupportedType(transferType)) {
                            return true;
                        }
                        return false;
                    }
                });

        viewer.setContentProvider(contentProvider);
        viewer.setLabelProvider(createTaxonomyLabelProvider());
        viewer.addTreeListener(new TaxonomyTreeViewerListener());
        viewer.addDoubleClickListener(new TaxonomyDoubleClickListener());
        ResourceViewerToolTipSupport.enableFor(viewer, getModelProvider());

        tree = viewer.getTree();
        tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        tree.setItemCount(currentRootResources.size());

        selectionProvider.updateViewer(viewer);

        layoutParent(true, true);
    }

    private void createDropRegion() {
        Widgets.disposeIfExists(dropComposite);
        if (Widgets.isNullOrDisposed(getParent())) {
            return;
        }

        boolean small = !Widgets.isNullOrDisposed(tree);
        dropComposite = new DropComposite(getParent(), "class", small);
        dropComposite.setDefaultGridLayoutData(small);

        DropTarget dropTarget = new DropTarget(dropComposite, DND.DROP_MOVE | DND.DROP_COPY);
        dropTarget.setTransfer(new Transfer[] { OntClassTransfer.getInstance() });
        dropTarget.addDropListener(new DropTargetAdapter() {

            @Override
            public void drop(DropTargetEvent event) {
                if (OntClassTransfer.getInstance().isSupportedType(event.currentDataType)) {
                    OntClass clazz = (OntClass) OntClassTransfer.getInstance().nativeToJava(
                            event.currentDataType);
                    if (clazz != null) {
                        changeRootResource(clazz);
                    }
                }
            }
        });
    }

    private TaxonomyLabelProvider createTaxonomyLabelProvider() {
        LabelProvider labelProvider = getLabelProvider();
        TaxonomyLabelProvider result = new TaxonomyLabelProvider(viewer, labelProvider);
        if (CorePlugin.getDefault().isOpenResourceActive()) {
            result.setActiveResource(CorePlugin.getDefault().getActiveOpenResource());
        }
        result.setActiveStyle(new Styler() {
            @Override
            public void applyStyles(TextStyle textStyle) {
                textStyle.font = boldFont;
                textStyle.foreground = Display.getCurrent().getSystemColor(SWT.COLOR_BLACK);
            }
        });
        result.setShowInstanceCount(getShowInstanceCount());
        result.setViewModel(viewModel);
        return result;
    }

    protected void setShowInstanceCount(boolean enabled) {
        setStateParameter(PARAM_SHOW_INSTANCE_COUNT, new Boolean(enabled));
        if (showInstanceCount != enabled) {
            showInstanceCount = enabled;
            if (!Widgets.isNullOrDisposedViewer(viewer)
                    && !Widgets.isNullOrDisposed(viewer.getTree())) {
                viewer.setLabelProvider(createTaxonomyLabelProvider());
                refreshViewer(true);
            }
        }
        Commands.refreshElements(getToggleInstanceCountHandlerID());
    }

    protected boolean getShowInstanceCount() {
        return showInstanceCount;
    }

    protected void toggleShowBaseModel() {
        boolean newValue = !showBaseModel();
        setStateParameter(PARAM_SHOW_BASE_MODEL, new Boolean(newValue));
        refreshFromModel();
    }

    protected boolean showBaseModel() {
        if (hasStateParameter(PARAM_SHOW_BASE_MODEL)) {
            return ((Boolean) getStateParameter(PARAM_SHOW_BASE_MODEL)).booleanValue();
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    protected List<String> getVisibleModelURIs() {
        if (hasStateParameter(PARAM_VISIBLE_MODEL_URIS)) {
            return (List<String>) getStateParameter(PARAM_VISIBLE_MODEL_URIS);
        }

        // By default, show all submodels that are imported
        List<String> result = Lists.newArrayList();
        IModelProvider modelProvider = getModelProvider();
        if (modelProvider != null) {
            OntModel model = modelProvider.getOntModel();
            result.addAll(modelProvider.getSubModelURIs());
            // Only enable rdf, rdfs, owl when they are imported rather than
            // only preloaded by our editor.
            List<String> rdfOwlURIs = Lists.newArrayList(RDF.getURI(), RDFS.getURI(), OWL.getURI());
            for (String uri : rdfOwlURIs) {
                if (!importExistsOutsideOfSubmodels(model.createResource(uri), rdfOwlURIs,
                        modelProvider)) {
                    result.remove(uri);
                }
            }
        }
        // By default, don't show the inferred model.
        result.remove(IModelProvider.INFERRED_SUBMODEL_URI);
        return result;
    }

    private boolean importExistsOutsideOfSubmodels(Resource ontology, List<String> submodels,
            IModelProvider provider) {
        List<String> existingSubmodels = provider.getSubModelURIs();
        Model baseModel = provider.getBaseModel();
        if (baseModel != null && baseModel.contains(null, OWL.imports, ontology)) {
            return true;
        }

        for (String existingSubmodel : existingSubmodels) {
            if (!submodels.contains(existingSubmodel)) {
                Model subModel = provider.getSubModel(existingSubmodel);
                if (subModel != null && subModel.contains(null, OWL.imports, ontology)) {
                    return true;
                }
            }
        }
        return false;
    }

    protected void refreshViewer() {
        refreshViewer(false);
    }

    protected void refreshViewer(boolean restoreExpandedElements) {
        if (Widgets.isNullOrDisposedViewer(viewer)) {
            return;
        }

        if (!getShowTopClasses().equals(SHOW_SELECTED_RESOURCES_STATE)) {
            boolean parentsOnly = (getShowTopClasses().equals(SHOW_ALL_TOP_PARENTS_STATE));
            List<OntClass> rootClasses = viewModel.getRootClasses(parentsOnly);
            currentRootResources.clear();
            currentRootResources.addAll(Lists.transform(rootClasses,
                    new Function<OntClass, Resource>() {
                        @Override
                        public Resource apply(OntClass clazz) {
                            return clazz.asResource();
                        }
                    }));
        }

        tree.setRedraw(false);
        viewer.setInput(currentModel);
        contentProvider.setRoots(currentRootResources);

        tree.setItemCount((currentModel == null) ? 0 : currentRootResources.size());
        if (restoreExpandedElements) {
            restoreExpandedElements();
        }
        else {
            viewer.expandToLevel(2);
        }
        tree.setRedraw(true);
    }

    public void restoreState() {
        restoreCurrentRootResources();
        restoreShowTopClasses();
        restoreShowInstanceCount();
    }

    private void restoreCurrentRootResources() {
        Object object = getStateParameter(PARAM_CURRENT_ROOT_RESOURCES);
        List<?> paramValue = null;
        if (object != null && object instanceof List) {
            paramValue = (List<?>) object;
        }
        if (paramValue != null) {
            currentRootResources.clear();
            for (Object rootResource : paramValue) {
                if (rootResource instanceof Resource) {
                    currentRootResources.add((Resource) rootResource);
                }
            }
        }
        else {
            currentRootResources.clear();
            currentRootResources.add(getTaxonomyDefaultRoot());
        }
    }

    private void restoreShowTopClasses() {
        setShowTopClasses(getShowTopClasses(), false);
    }

    private void restoreExpandedElements() {
        Object object = getStateParameter(PARAM_VIEWER_EXPANDED_ELEMENTS);
        Object[] paramValue = null;
        if (object != null && object instanceof Object[]) {
            paramValue = (Object[]) object;
        }

        if (currentModel != null && viewer != null) {
            restoringExpandedElements = true;
            viewer.setAutoExpandLevel(2);
            viewer.expandToLevel(2);
            if (paramValue != null) {
                viewer.setExpandedElements(paramValue);
            }
            else {
                if (viewer.getExpandedElements().length > 0) {
                    setStateParameter(PARAM_VIEWER_EXPANDED_ELEMENTS, viewer.getExpandedElements());
                }
            }
            restoringExpandedElements = false;
        }
    }

    private void restoreShowInstanceCount() {
        Object object = getStateParameter(PARAM_SHOW_INSTANCE_COUNT);
        Boolean paramValue = null;
        if (object != null && object instanceof Boolean) {
            paramValue = (Boolean) object;
        }
        if (paramValue != null) {
            showInstanceCount = paramValue.booleanValue();
        }
        else {
            showInstanceCount = true;
        }
        Commands.refreshElements(getToggleInstanceCountHandlerID());
        setStateParameter(PARAM_SHOW_INSTANCE_COUNT, new Boolean(showInstanceCount));
    }

    abstract protected String getToggleInstanceCountHandlerID();

    protected Set<List<Resource>> findPaths(Model model, Resource from, Resource to) {
        return findPaths(model, from, to, new HashSet<Resource>());
    }

    protected Set<List<Resource>> findPaths(Model model, Resource from, Resource to,
            Set<Resource> visited) {
        Set<List<Resource>> paths = Sets.newLinkedHashSet();
        List<Property> predicates = ImmutableList.of(RDFS.subClassOf); // ,
                                                                       // RDF.type);
        for (Statement stmt : to.listProperties().toSet()) {
            if (!predicates.contains(stmt.getPredicate())) {
                continue;
            }
            if (!stmt.getObject().isResource()) {
                continue;
            }
            Resource superClass = stmt.getResource();
            if (visited.contains(superClass)) {
                continue;
            }
            if (superClass.equals(from)) {
                List<Resource> path = Lists.newArrayList(superClass, to);
                paths.add(path);
            }
            else {
                Set<Resource> copyOfVisited = Sets.newHashSet(visited);
                copyOfVisited.add(superClass);
                Set<List<Resource>> subPaths = findPaths(model, from, superClass, copyOfVisited);
                for (List<Resource> subPath : subPaths) {
                    if (subPath.size() == 0) {
                        continue;
                    }
                    subPath.add(to);
                    paths.add(subPath);
                }
            }
        }
        return paths;
    }

    abstract public List<String> getPropertyChangesTriggeringViewerRefresh();

    abstract public List<String> getPropertyChangesTriggeringCompleteRefresh();

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        String property = event.getProperty();
        if (getPropertyChangesTriggeringViewerRefresh().contains(property)) {
            if (hasModelProvider()) {
                createControls();
                createContextMenu();
                refreshViewer(true);
            }
        }
        else if (getPropertyChangesTriggeringCompleteRefresh().contains(property)) {
            refreshFromModel();
        }
    }

    /**
     * Adds the element to the list of expanded elements for the current
     * provider Note: The element is only added to the list; the viewer still
     * needs to be refreshed, and the expand states should be restored.
     * 
     * @param element
     */
    @SuppressWarnings("unused")
    private void expandElement(ResourceTreeData element) {
        int count = 0;
        Object[] newExpandedElements = new Object[1];

        Object object = getStateParameter(PARAM_VIEWER_EXPANDED_ELEMENTS);
        if (object != null && object instanceof Object[]) {
            Object[] paramValue = (Object[]) object;
            count = paramValue.length;
            newExpandedElements = Arrays.copyOf(paramValue, count + 1);
        }
        newExpandedElements[count] = element;
        setStateParameter(PARAM_VIEWER_EXPANDED_ELEMENTS, newExpandedElements);
    }

    protected String getShowTopClasses() {
        String value = (String) getStateParameter(PARAM_SHOW_TOP_CLASSES);
        if (Strings.isNullOrEmpty(value)) {

            value = SHOW_ALL_TOP_PARENTS_STATE;
            for (IModelValidator validator : CoreExtensions.findValidators()) {
                String name = validator.getName();
                if (validator.isEnabled() && name.equals("Subclass Hierarchy")) {
                    value = SHOW_SELECTED_RESOURCES_STATE;
                }
            }
        }
        return value;
    }

    protected void setShowTopClasses(boolean enabled, boolean parentsOnly, boolean performRefresh) {
        String value = null;
        if (enabled == false) {
            value = SHOW_SELECTED_RESOURCES_STATE;
        }
        else if (parentsOnly == true) {
            value = SHOW_ALL_TOP_PARENTS_STATE;
        }
        else {
            value = SHOW_ALL_TOP_CLASSES_STATE;
        }
        setShowTopClasses(value, performRefresh);
    }

    protected void setShowTopClasses(String state, boolean performRefresh) {
        setStateParameter(PARAM_SHOW_TOP_CLASSES, state);
        if (performRefresh && hasModelProvider()) {
            if (SHOW_ALL_TOP_CLASSES_STATE.equals(state)) {
                executeRefreshJob();
            }
            else {
                refreshViewer();
            }
        }
    }

    protected void changeRootResource(Resource resource) {
        changeRootResources(Lists.newArrayList(resource));
    }

    protected void changeRootResources(List<Resource> resources) {
        for (Resource resource : resources) {
            if (resource != null) {
                if (resource.isAnon()) {
                    String title = "Only non-anonymous nodes can be set as root";
                    String message = "This view only accepts non-anonymous nodes for its root. Such nodes have a URI to identify them with. The resource you have dropped onto this region does not have a URI, however.";
                    MessageDialog.openInformation(getSite().getShell(), title, message);
                    return;
                }
                OntResource ontResource = JenaUtil.asOntResource(resource, currentModel);
                if (!ontResource.hasRDFType(getTaxonomyResourceType())) {
                    String title = "Only classes of a certain type can be set as root";
                    String message = String.format(
                            "This view only accepts classes of the following type as root: %s.",
                            getTaxonomyResourceType().getURI());
                    MessageDialog.openInformation(getSite().getShell(), title, message);
                    return;
                }
            }
        }

        currentRootResources.clear();
        currentRootResources.addAll(resources);
        setStateParameter(PARAM_CURRENT_ROOT_RESOURCES, Lists.newArrayList(currentRootResources));
        setShowTopClasses(false, false, false);
        contentProvider.setRoots(currentRootResources);

        refreshViewer();
    }

    /**
     * Function to create a sub class of selected resource
     */
    public void createSubClass(Resource superClass) {
        IModelProvider modelProvider = getModelProvider();
        if (modelProvider == null || superClass == null) {
            return;
        }

        OntModel model = getOntModel();

        if (model != null) {
            Resource type = model.getProperty(superClass, RDF.type).getObject().asResource();
            // TODO: What if superClass has multiple types??
            CreateClassWizard wizard = new CreateClassWizard("New Sub Class", modelProvider, type);
            wizard.setSuperClass(superClass);
            wizard.setAnonymousAllowed(true);
            wizard.setSuppressNotify(true);

            Shell parentShell = getSite().getShell();
            WizardDialog dialog = new WizardDialog(parentShell, wizard);
            dialog.create();

            String transactionDescription = "Due to the creation of a new sub class";
            ModelTransaction transaction = modelProvider.createTransaction(transactionDescription);
            if (dialog.open() != Window.OK) {
                modelProvider.abortTransaction(transaction);
            }
            else {
                Resource resource = wizard.getResource();

                // TODO: move this out of this function
                // Expand the super class within the taxonomy tree
                // if (singleton != null && singleton.isInitialized()) {
                // singleton.expandElement(selected);
                // }

                modelProvider.commitTransaction(transaction);

                if (wizard.openResourceEditor()) {
                    openResource(resource);
                }
            }
        }
    }

    /**
     * Function to create a complement class of selected resource
     */
    public void createComplementClass(Resource complementClass) {
        IModelProvider modelProvider = getModelProvider();
        if (modelProvider == null || complementClass == null) {
            return;
        }

        OntModel model = getOntModel();

        if (model != null && complementClass.hasProperty(RDF.type, OWL.Class)) {
            List<OntClass> superClasses = JenaUtil.asOntClass(complementClass, model)
                    .listSuperClasses(true).toList();
            Resource type = model.getResource(OWL.Class.getURI());
            CreateClassWizard wizard = new CreateClassWizard("New Complement Class", modelProvider,
                    type);
            wizard.setAnonymousAllowed(true);
            wizard.setSuppressNotify(true);

            Shell parentShell = getSite().getShell();
            WizardDialog dialog = new WizardDialog(parentShell, wizard);
            dialog.create();

            String transactionDescription = "Due to the creation of a new complement class";
            ModelTransaction transaction = modelProvider.createTransaction(transactionDescription);
            if (dialog.open() != Window.OK) {
                modelProvider.abortTransaction(transaction);
            }
            else {
                Resource resource = wizard.getResource();
                // TODO: move outside of function
                // Expand the super class within the taxonomy tree
                // if (singleton != null && singleton.isInitialized()) {
                // singleton.expandElement(selected);
                // }

                createComplementClassStatements(resource, complementClass, superClasses);

                modelProvider.commitTransaction(transaction);
                if (wizard.openResourceEditor()) {
                    openResource(resource);
                }
            }
        }
    }

    private void createComplementClassStatements(Resource resource, Resource complement,
            List<OntClass> superClasses) {
        ResourceStatements.createResourceAsComplementClassStatements(resource, complement);
        if (superClasses != null) {
            for (OntClass superClass : superClasses) {
                ResourceStatements.createResourceAsSubclassStatements(resource, superClass);
            }
        }
    }

    public void createIntersectionClass(List<Resource> members) {
        IModelProvider modelProvider = getModelProvider();
        if (modelProvider == null || members == null || members.size() < 2) {
            return;
        }

        OntModel model = getOntModel();
        if (model != null) {
            Resource type = model.getResource(OWL.Class.getURI());
            CreateClassWizard wizard = new CreateClassWizard("New Intersection Class",
                    modelProvider, type);
            wizard.setAnonymousAllowed(true);
            wizard.setSuppressNotify(true);

            Shell parentShell = getSite().getShell();
            WizardDialog dialog = new WizardDialog(parentShell, wizard);
            dialog.create();

            String transactionDescription = "Due to the creation of a new intersection class";
            ModelTransaction transaction = modelProvider.createTransaction(transactionDescription);
            if (dialog.open() != Window.OK) {
                modelProvider.abortTransaction(transaction);
            }
            else {
                Resource resource = wizard.getResource();

                createIntersectionClassStatements(resource, members);

                modelProvider.commitTransaction(transaction);
                if (wizard.openResourceEditor()) {
                    openResource(resource);
                }
            }
        }
    }

    private void createIntersectionClassStatements(Resource resource, List<Resource> listMembers) {
        if (resource != null) {
            Model model = resource.getModel();
            ResourceStatements.createResourceAsSubclassStatements(resource, OWL.Thing);
            if ((listMembers != null) && (listMembers.size() > 0)) {
                RDFList list = model.createList(listMembers.iterator());
                ResourceStatements.createResourceAsIntersectionClassStatements(resource, list);
            }
        }
    }

    public void createUnionClass(List<Resource> members) {
        IModelProvider modelProvider = getModelProvider();
        if (modelProvider == null || members == null || members.size() < 2) {
            return;
        }

        OntModel model = getOntModel();
        if (model != null) {
            Resource type = model.getResource(OWL.Class.getURI());
            CreateClassWizard wizard = new CreateClassWizard("New Union Class", modelProvider, type);
            wizard.setAnonymousAllowed(true);
            wizard.setSuppressNotify(true);

            Shell parentShell = getSite().getShell();
            WizardDialog dialog = new WizardDialog(parentShell, wizard);
            dialog.create();

            String transactionDescription = "Due to the creation of a new union class";
            ModelTransaction transaction = modelProvider.createTransaction(transactionDescription);
            if (dialog.open() != Window.OK) {
                modelProvider.abortTransaction(transaction);
            }
            else {
                Resource resource = wizard.getResource();

                createUnionClassStatements(resource, members);

                modelProvider.commitTransaction(transaction);
                if (wizard.openResourceEditor()) {
                    openResource(resource);
                }
            }
        }
    }

    private void createUnionClassStatements(Resource resource, List<Resource> listMembers) {
        if (resource != null) {
            Model model = resource.getModel();
            ResourceStatements.createResourceAsSubclassStatements(resource, OWL.Thing);
            if ((listMembers != null) && (listMembers.size() > 0)) {
                RDFList list = model.createList(listMembers.iterator());
                ResourceStatements.createResourceAsUnionClassStatements(resource, list);
            }
        }
    }

    public void createEquivalentClass(Resource equivalentClass) {
        IModelProvider modelProvider = getModelProvider();
        if (modelProvider == null || equivalentClass == null) {
            return;
        }

        OntModel model = getOntModel();

        if (model != null && equivalentClass.hasProperty(RDF.type, OWL.Class)) {
            List<OntClass> superClasses = JenaUtil.asOntClass(equivalentClass, model)
                    .listSuperClasses(true).toList();
            Resource type = model.getResource(OWL.Class.getURI());
            CreateClassWizard wizard = new CreateClassWizard("New Equivalent Class", modelProvider,
                    type);
            wizard.setAnonymousAllowed(true);
            wizard.setSuppressNotify(true);

            Shell parentShell = getSite().getShell();
            WizardDialog dialog = new WizardDialog(parentShell, wizard);
            dialog.create();

            String transactionDescription = "Due to the creation of a new equivalent class";
            ModelTransaction transaction = modelProvider.createTransaction(transactionDescription);
            if (dialog.open() != Window.OK) {
                modelProvider.abortTransaction(transaction);
            }
            else {
                Resource resource = wizard.getResource();
                // TODO: move outside the function
                // / Expand the super class within the taxonomy
                // tree
                // if (singleton != null && singleton.isInitialized()) {
                // singleton.expandElement(selected);
                // }

                createEquivalentClassStatements(resource, equivalentClass, superClasses);

                modelProvider.commitTransaction(transaction);
                if (wizard.openResourceEditor()) {
                    openResource(resource);
                }
            }
        }
    }

    private void createEquivalentClassStatements(Resource resource, Resource equivalent,
            List<OntClass> superClasses) {
        ResourceStatements.createResourceAsEquivalentClassStatements(resource, equivalent);
        for (OntClass superClass : superClasses) {
            ResourceStatements.createResourceAsSubclassStatements(resource, superClass);
        }
    }

    public void createSiblingClass(Resource siblingClass, Resource superClass) {
        IModelProvider modelProvider = getModelProvider();
        if (modelProvider == null || superClass == null) {
            return;
        }

        OntModel model = getOntModel();

        if (model != null) {
            Resource type = model.getProperty(superClass, RDF.type).getObject().asResource();
            // TODO: What if superClass has multiple types??
            CreateClassWizard wizard = new CreateClassWizard("New Sibling Class", modelProvider,
                    type);
            wizard.setSuperClass(superClass);
            wizard.setAnonymousAllowed(true);
            wizard.setSuppressNotify(true);

            Shell parentShell = getSite().getShell();
            WizardDialog dialog = new WizardDialog(parentShell, wizard);
            dialog.create();

            String transactionDescription = "Due to the creation of a new sibling class";
            ModelTransaction transaction = modelProvider.createTransaction(transactionDescription);
            if (dialog.open() != Window.OK) {
                modelProvider.abortTransaction(transaction);
            }
            else {
                Resource resource = wizard.getResource();
                modelProvider.commitTransaction(transaction);
                if (wizard.openResourceEditor()) {
                    openResource(resource);
                }
            }
        }
    }

    public void createInstance(Resource type) {
        IModelProvider modelProvider = getModelProvider();
        if (modelProvider == null || type == null) {
            return;
        }

        OntModel model = getOntModel();

        if (model != null) {
            CreateResourceWizard wizard = new CreateResourceWizard("New Instance", modelProvider,
                    type);
            wizard.setAnonymousAllowed(true);
            Shell parentShell = getSite().getShell();
            WizardDialog dialog = new WizardDialog(parentShell, wizard);
            dialog.create();

            ModelTransaction transaction = modelProvider
                    .createTransaction("Created a new instance");
            if (dialog.open() != Window.OK) {
                modelProvider.abortTransaction(transaction);
            }
            else {
                if (wizard.openResourceEditor()) {
                    openResource(wizard.getResource());
                }
                modelProvider.commitTransaction(transaction);
            }
        }
    }

    public void removeSubClass(Resource subClass, Resource superClass) {
        IModelProvider modelProvider = getModelProvider();
        if (modelProvider == null || subClass == null || superClass == null) {
            return;
        }

        OntModel model = getOntModel();

        if (model != null) {
            String uri = subClass.getURI();
            String questionText = String
                    .format("Are you sure you want to remove the sub class '%s' from its super class?",
                            uri);
            MessageDialog dialog = new MessageDialog(getSite().getShell(), "Remove Sub Class",
                    null, questionText, MessageDialog.QUESTION, new String[] { "Yes", "No" }, 1);

            if (dialog.open() == Window.OK) {
                String transactionDescription = "Due to the removal of a subClassOf triple";
                ModelTransaction transaction = modelProvider
                        .createTransaction(transactionDescription);

                ResourceStatements.createRemoveResourceAsSubclassStatements(subClass, superClass);

                modelProvider.commitTransaction(transaction);
            }
        }
    }

    /**
     * Lists the direct superclasses of a given class.
     * 
     * @param clazz
     *            The class of which its direct superclasses are to be returned.
     * @return A list of direct superclasses of the class at the URI of
     *         <code>clazz</code>.
     */
    public Set<OntClass> listDirectSuperClasses(OntClass clazz) {
        Set<OntClass> superClasses = new HashSet<>();
        if (clazz.isAnon() || clazz.getOntModel() == null) {
            return superClasses;
        }

        OntModel model = clazz.getOntModel();

        Var varS = Var.alloc("superClass");
        Triple t1 = Triples.create(clazz, RDFS.subClassOf, varS);
        Triple t2 = Triples.create(varS, PathUtil.isInstanceOf, getTaxonomyResourceType());

        QueryBuilder qb = QueryBuilder.createSelect(true);
        qb.addTriplePatterns(t1, t2).addResultVar(varS);

        for (ResultSet iter = qb.execSelect(model); iter.hasNext();) {
            QuerySolution querySolution = iter.next();
            RDFNode node = querySolution.get(varS.getName());
            if (node.isAnon() == false) {
                OntClass superClass = model.getOntClass(node.asResource().getURI());
                if (superClass != null) {
                    superClasses.add(superClass);
                }
            }
        }

        return superClasses;
    }

    /**
     * Lists the direct subclasses of a given class.
     * 
     * @param clazz
     *            The class of which its direct subclasses are to be returned.
     * @return A list of direct subclasses of the class at the URI of
     *         <code>clazz</code>.
     */
    public Set<OntClass> listDirectSubClasses(OntClass clazz) {
        Set<OntClass> subClasses = new HashSet<>();
        if (clazz.isAnon() || clazz.getOntModel() == null) {
            return subClasses;
        }

        OntModel model = clazz.getOntModel();

        Var varS = Var.alloc("subClass");
        Triple t1 = Triples.create(varS, RDFS.subClassOf, clazz);
        Triple t2 = Triples.create(varS, PathUtil.isInstanceOf, getTaxonomyResourceType());

        QueryBuilder qb = QueryBuilder.createSelect(true);
        qb.addTriplePatterns(t1, t2).addResultVar(varS);

        for (ResultSet iter = qb.execSelect(model); iter.hasNext();) {
            QuerySolution querySolution = iter.next();
            RDFNode node = querySolution.get(varS.getName());
            if (node.isAnon() == false) {
                OntClass subClass = model.getOntClass(node.asResource().getURI());
                if (subClass != null) {
                    subClasses.add(subClass);
                }
            }
        }

        return subClasses;
    }

    public List<OntResource> listVisibleSuperClasses(OntClass clazz) {
        Set<OntResource> superClasses = new HashSet<>();
        for (OntClass superClass : listDirectSuperClasses(clazz)) {
            addClassWhenVisible(superClass, superClasses, false);
        }
        List<OntResource> result = Lists.newArrayList();
        result.addAll(superClasses);
        return result;
    }

    @Override
    public List<OntResource> listVisibleSubClasses(OntClass clazz) {
        Set<OntResource> subClasses = new HashSet<>();
        for (OntClass subClass : listDirectSubClasses(clazz)) {
            addClassWhenVisible(subClass, subClasses, false);
        }
        List<OntResource> result = Lists.newArrayList();
        result.addAll(subClasses);
        return result;
    }

    private void addClassWhenVisible(OntClass clazz, Set<OntResource> classes, boolean showAnonymous) {
        if (!showAnonymous && clazz.isAnon()) {
            return;
        }
        classes.add(clazz.as(OntResource.class));
    }

    @Override
    public List<OntResource> listInstances(OntClass clazz, List<OntResource> exclude) {
        List<OntResource> instances = Lists.newArrayList();
        if (clazz.isClass()) {
            for (ExtendedIterator<? extends OntResource> iter = clazz.listInstances(true); iter
                    .hasNext();) {
                OntResource instance = iter.next();
                if (instance.isAnon()) {
                    continue;
                }
                if (exclude.contains(instance)) {
                    continue;
                }
                instances.add(instance);
            }
        }
        return instances;
    }

    @Override
    public TreeBag<OntResource> getChildren(OntClass clazz) {
        return classHierarchy.get(clazz);
    }

    @Override
    public boolean containsClass(OntClass clazz) {
        return classHierarchy.containsKey(clazz);
    }

    @Override
    public void putChildren(OntClass clazz, TreeBag<OntResource> children) {
        classHierarchy.put(clazz, children);
    }

    @Override
    public void removeChildren(OntClass clazz) {
        classHierarchy.remove(clazz);
    }

    @Override
    public void removeChild(OntResource child) {
        for (OntClass clazz : classHierarchy.keySet()) {
            TreeBag<OntResource> children = classHierarchy.get(clazz);
            children.remove(child);
        }
    }

    private interface ModelJobListener {
        public void done(final TaxonomyViewModel newViewModel);
    }

    private class CreateModelViewJob extends JobWithMonitor {
        private IModelProvider modelProvider;
        private final List<ModelJobListener> listeners;

        public CreateModelViewJob() {
            super(String.format("Refreshing %s", title));
            listeners = Lists.newArrayList();
        }

        public void schedule(IModelProvider modelProvider) {
            this.modelProvider = modelProvider;
            schedule();
        }

        public void addJobDoneListener(ModelJobListener listener) {
            if (!listeners.contains(listener)) {
                listeners.add(listener);
            }
        }

        @Override
        public boolean belongsTo(Object family) {
            return family.equals(AbstractTaxonomyView.this);
        }

        @Override
        protected IStatus run(IProgressMonitor monitor) {
            TaxonomyViewModel result = null;

            if (monitor.isCanceled()) {
                return Status.CANCEL_STATUS;
            }

            synchronized (mutex) {
                settingViewerInput = true;
            }

            try {
                startMonitorUpdate(monitor, "Refreshing Taxonomy View Model", 1);

                OntModel currentModel = modelProvider.getOntModel();
                if (currentModel != null) {
                    logger.trace("CreateModelViewJob will create the new viewModel");

                    // First create a new union model that includes only those
                    // submodels that are to be shown
                    OntModel unionModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
                    if (showBaseModel()) {
                        unionModel.addSubModel(currentModel.getBaseModel(), false);
                    }
                    for (String uri : getVisibleModelURIs()) {
                        Model visibleSubModel = modelProvider.getSubModel(uri);
                        if (visibleSubModel != null) {
                            unionModel.addSubModel(visibleSubModel, false);
                        }
                    }

                    // Create the view model using the unionModel as input
                    result = TaxonomyViewModel.create(currentModel, unionModel,
                            getTaxonomyResourceType(),
                            getShowTopClasses().equals(SHOW_ALL_TOP_CLASSES_STATE),
                            showInstanceCount);
                    logger.trace("CreateModelViewJob has created the new viewModel");
                }
                addWorked(1);
            }
            catch (ClosedException ex) {
                monitor.setCanceled(true);
                logger.warn("The model has been closed during the Taxonomy View build query execution!");
            }
            finally {
                monitor.done();
                stopMonitorUpdate();

                synchronized (mutex) {
                    settingViewerInput = false;
                }

                if (monitor.isCanceled()) {
                    return Status.CANCEL_STATUS;
                }

                for (ModelJobListener listener : listeners) {
                    listener.done(result);
                }
            }
            return Status.OK_STATUS;
        }
    }

    private class TaxonomyDefaultRootMenuProvider implements IMenuListener {
        public static final String GROUP_INITIAL = "initialGroup";
        public static final String ACTION_ID = "rootResource";

        @Override
        public void menuAboutToShow(IMenuManager manager) {
            if (getTaxonomyDefaultRoot() == null) {
                return;
            }

            for (IContributionItem item : manager.getItems()) {
                if (item instanceof IMenuManager) {
                    IMenuManager submenu = (IMenuManager) item;
                    if (submenu.find(GROUP_INITIAL) != null) {
                        manager = submenu;
                        break;
                    }
                }
            }
            // manager = manager.findMenuUsingPath(MENU_CHANGE_ROOT);
            if (manager.find(GROUP_INITIAL) == null) {
                return;
            }

            if (!isTaxonomyDefaultRootSelectable()) {
                if (manager.find(ACTION_ID) != null) {
                    manager.remove(ACTION_ID);
                }
                return;
            }

            if (manager.find(ACTION_ID) != null) {
                return;
            }

            manager.prependToGroup(GROUP_INITIAL, new Action() {
                private final Resource rootResource = getTaxonomyDefaultRoot();

                @Override
                public String getId() {
                    return ACTION_ID;
                }

                @Override
                public void run() {
                    changeRootResource(rootResource);
                }

                @Override
                public String getText() {
                    return getLabelProvider().getText(rootResource);
                }

                @Override
                public ImageDescriptor getImageDescriptor() {
                    Image image = getLabelProvider().getImage(rootResource);
                    return (image == null) ? null : ImageDescriptor.createFromImage(image);
                }
            });
        }
    }

    @Override
    protected void cleanup() {
        if (viewModel != null) {
            viewModel.close();
        }

        if (contentProvider != null) {
            contentProvider.dispose();
        }

        currentRootResources.clear();
        classHierarchy.clear();

        Widgets.disposeIfExists(busyIndicator);

        refreshJob = null;
        modelChangedJob = null;
        contentProvider = null;
        viewer = null;
        viewModel = null;
    }

    protected void collapseAll() {
        if (!Widgets.isNullOrDisposedViewer(viewer)) {
            viewer.collapseAll();
        }
    }

}