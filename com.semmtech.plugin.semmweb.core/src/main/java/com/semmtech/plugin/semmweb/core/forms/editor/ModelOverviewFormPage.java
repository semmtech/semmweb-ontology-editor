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

package com.semmtech.plugin.semmweb.core.forms.editor;


import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ColumnLayout;
import org.eclipse.ui.forms.widgets.ColumnLayoutData;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.themes.ITheme;
import org.eclipse.ui.themes.IThemeManager;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.DatasetFactory;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecException;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.core.BasicPattern;
import com.hp.hpl.jena.sparql.core.TriplePath;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.expr.E_IsBlank;
import com.hp.hpl.jena.sparql.expr.E_LogicalNot;
import com.hp.hpl.jena.sparql.expr.E_NotExists;
import com.hp.hpl.jena.sparql.expr.E_StrLowerCase;
import com.hp.hpl.jena.sparql.expr.Expr;
import com.hp.hpl.jena.sparql.expr.ExprVar;
import com.hp.hpl.jena.sparql.expr.aggregate.AggCountVarDistinct;
import com.hp.hpl.jena.sparql.path.Path;
import com.hp.hpl.jena.sparql.syntax.ElementFilter;
import com.hp.hpl.jena.sparql.syntax.ElementGroup;
import com.hp.hpl.jena.sparql.syntax.ElementNamedGraph;
import com.hp.hpl.jena.sparql.syntax.ElementPathBlock;
import com.hp.hpl.jena.sparql.syntax.ElementSubQuery;
import com.hp.hpl.jena.sparql.syntax.ElementTriplesBlock;
import com.hp.hpl.jena.sparql.syntax.ElementUnion;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.CorePluginImages;
import com.semmtech.plugin.semmweb.core.actions.IModelVisibilityLabelProvider;
import com.semmtech.plugin.semmweb.core.actions.ModelVisibilityListener;
import com.semmtech.plugin.semmweb.core.actions.ModelVisibilityMenuProvider;
import com.semmtech.plugin.semmweb.core.dnd.DndUtils;
import com.semmtech.plugin.semmweb.core.dnd.OntClassTransfer;
import com.semmtech.plugin.semmweb.core.dnd.PropertyArrayListTransfer;
import com.semmtech.plugin.semmweb.core.dnd.PropertyTransfer;
import com.semmtech.plugin.semmweb.core.dnd.ResourceArrayListTransfer;
import com.semmtech.plugin.semmweb.core.dnd.ResourceTransfer;
import com.semmtech.plugin.semmweb.core.handlers.CreateResourceHandler;
import com.semmtech.plugin.semmweb.core.model.OntModelUtils;
import com.semmtech.plugin.semmweb.core.model.events.IModelEvent;
import com.semmtech.plugin.semmweb.core.preferences.LabelsPreference;
import com.semmtech.plugin.semmweb.core.preferences.LanguagesPreference;
import com.semmtech.plugin.semmweb.core.preferences.OntologyFileEditorPreference;
import com.semmtech.plugin.semmweb.core.sparql.LabelProviderPropertyFunction;
import com.semmtech.plugin.semmweb.core.viewers.ModelNodeLabelProvider;
import com.semmtech.plugin.semmweb.core.viewers.ModelNodeLabelProvider.InspectOrder;
import com.semmtech.plugin.semmweb.core.viewers.ResourceViewerToolTipSupport;
import com.semmtech.plugin.semmweb.core.widgets.OntologyEditorFormHeading;
import com.semmtech.semantics.sparql.PathUtil;
import com.semmtech.semantics.util.JenaUtil;
import com.semmtech.ui.plugin.decorators.OverlayImageIcon;
import com.semmtech.ui.plugin.jobs.JobWithMonitor;
import com.semmtech.ui.plugin.viewers.LazyContentProvider;
import com.semmtech.ui.plugin.viewers.PendingElement;
import com.semmtech.ui.plugin.widgets.Sections;
import com.semmtech.ui.plugin.widgets.Widgets;


public class ModelOverviewFormPage extends AbstractOntologyEditorFormPage implements
        IPropertyChangeListener {

    private static final Logger logger = Logger.getLogger(ModelOverviewFormPage.class);
    private static final String BASE_MODEL_GRAPH_ID = "urn:base-model";

    private ScrolledForm scrolledForm;
    private Composite menuComposite;
    private ExploreModelJob exploreJob;
    private Dataset dataset;

    private final List<ResourceOverview> resourceOverviews;
    private final List<String> visibleSubModelUris;

    private boolean disposed;
    private boolean wasActive;
    private ModelVisibilityMenuProvider modelVisibilityProvider;
    private OntologyLink ontologyLink;

    /**
     * 
     * @author Mike Henrichs
     * 
     */
    final class ResourceViewerLabelProvider extends CellLabelProvider {

        private final InspectOrder order;

        public ResourceViewerLabelProvider(InspectOrder order) {
            this.order = order;
        }

        @Override
        public void update(ViewerCell cell) {
            ModelNodeLabelProvider labelProvider = getLabelProvider();
            Object element = cell.getElement();
            if (element instanceof Resource) {
                cell.setText(labelProvider.getText(element));
                cell.setImage(labelProvider.getImage(element, order));
            }
            else if (element instanceof String) {
                cell.setText(element.toString());
                cell.setImage(CorePlugin.getDefault().getImage(CorePluginImages.IMG_APPLICATION));
            }
            else {
                IWorkbenchAdapter adapter = (IWorkbenchAdapter) Platform.getAdapterManager()
                        .getAdapter(element, IWorkbenchAdapter.class);
                if (adapter != null) {
                    cell.setText(adapter.getLabel(element));
                    cell.setImage(adapter.getImageDescriptor(element).createImage());
                }
            }
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

    public ModelOverviewFormPage(FormEditor editor, String id, String title) {
        super(editor, id, title);
        this.resourceOverviews = Lists.newArrayList();
        this.visibleSubModelUris = Lists.newArrayList(getSubModelURIs());
        this.dataset = null;
        initialize();
    }

    private void initialize() {
        CorePlugin.getDefault().getPreferenceStore().addPropertyChangeListener(this);

        resourceOverviews.add(createRdfsClassesOverview());
        resourceOverviews.add(createOwlClassesOverview());
        resourceOverviews.add(createIndividualsOverview());
        resourceOverviews.add(createPropertiesOverview());
        resourceOverviews.add(createRestrictionsOverview());
        resourceOverviews.add(new OpenResourceOverview());
    }

    @Override
    public void dispose() {
        CorePlugin.getDefault().getPreferenceStore().removePropertyChangeListener(this);

        for (ResourceOverview overview : resourceOverviews) {
            overview.dispose();
        }

        super.dispose();
        disposed = true;
    }

    private ResourceTypeOverview createRdfsClassesOverview() {
        List<ByteArrayTransfer> transferTypes = Lists.newArrayList(OntClassTransfer.getInstance(),
                ResourceArrayListTransfer.getInstance(), ResourceTransfer.getInstance(),
                TextTransfer.getInstance());
        ResourceTypeOverview overview = new ResourceTypeOverview("Class", "Classes",
                RDFS.Class.getURI(), CorePluginImages.IMG_RDFS_CLASS,
                InspectOrder.CLASS_PROPERTY_INDIVIDUAL, transferTypes);
        overview.setExcludeInstancesOfType(OWL.Class.getURI());
        return overview;
    }

    private ResourceTypeOverview createOwlClassesOverview() {
        List<ByteArrayTransfer> transferTypes = Lists.newArrayList(OntClassTransfer.getInstance(),
                ResourceArrayListTransfer.getInstance(), ResourceTransfer.getInstance(),
                TextTransfer.getInstance());
        ResourceTypeOverview overview = new ResourceTypeOverview("Class of Individuals",
                "Classes of Individuals", OWL.Class.getURI(), CorePluginImages.IMG_OWL_CLASS,
                InspectOrder.CLASS_PROPERTY_INDIVIDUAL, transferTypes);
        overview.setExcludeInstancesOfType(OWL.Restriction.getURI());
        return overview;
    }

    private ResourceTypeOverview createRestrictionsOverview() {
        List<ByteArrayTransfer> transferTypes = Lists.newArrayList(OntClassTransfer.getInstance(),
                ResourceArrayListTransfer.getInstance(), ResourceTransfer.getInstance(),
                TextTransfer.getInstance());
        ResourceTypeOverview overview = new ResourceTypeOverview("Restriction", "Restrictions",
                OWL.Restriction.getURI(), null, InspectOrder.CLASS_PROPERTY_INDIVIDUAL,
                transferTypes);
        overview.setIncludeAnonymousInstances();
        return overview;
    }

    private ResourceTypeOverview createIndividualsOverview() {
        List<ByteArrayTransfer> transferTypes = Lists.newArrayList(
                ResourceArrayListTransfer.getInstance(), ResourceTransfer.getInstance(),
                TextTransfer.getInstance());
        return new ResourceTypeOverview("Individual", "Individuals", OWL.Thing.getURI(),
                CorePluginImages.IMG_OWL_INDIVIDUAL, InspectOrder.CLASS_PROPERTY_INDIVIDUAL,
                transferTypes);
    }

    private ResourceTypeOverview createPropertiesOverview() {
        List<ByteArrayTransfer> transferTypes = Lists.newArrayList(
                PropertyArrayListTransfer.getInstance(), PropertyTransfer.getInstance(),
                ResourceArrayListTransfer.getInstance(), ResourceTransfer.getInstance(),
                TextTransfer.getInstance());
        ResourceTypeOverview overview = new ResourceTypeOverview("Property", "Properties",
                RDF.Property.getURI(), CorePluginImages.IMG_RDF_PROPERTY,
                InspectOrder.CLASS_PROPERTY_INDIVIDUAL, transferTypes);
        overview.setAnonymousAllowed(false);
        return overview;
    }

    private static class OntologyLink extends Composite {

        private FormToolkit toolkit;
        private StyledText styledText;
        private Resource currentOntology;
        private LabelProvider labelProvider;
        private Label imgLabel;

        OntologyLink(Composite parent, FormToolkit toolkit, LabelProvider labelProvider) {
            super(parent, SWT.NONE);
            this.toolkit = toolkit;
            this.labelProvider = labelProvider;
            toolkit.adapt(this);
        }

        public void createContent() {
            String text = "This page contains several sections that list resources of a specific type, "
                    + "as well as a quick link to the main ontology.";

            GridLayoutFactory.swtDefaults().applyTo(this);

            Label textLabel = toolkit.createLabel(this, text, SWT.WRAP);
            GridDataFactory.fillDefaults().hint(100, SWT.DEFAULT).grab(true, false)
                    .applyTo(textLabel);

            Composite ontologyContent = toolkit.createComposite(this, SWT.NONE);
            GridLayoutFactory.fillDefaults().numColumns(2).applyTo(ontologyContent);

            imgLabel = toolkit.createLabel(ontologyContent, "", SWT.WRAP);
            GridDataFactory.swtDefaults().applyTo(imgLabel);

            styledText = new StyledText(ontologyContent, SWT.LEFT | SWT.WRAP);
            styledText.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseUp(MouseEvent e) {
                    if (currentOntology != null) {
                        CorePlugin.getDefault().openResource(currentOntology);
                    }
                }
            });
            GridDataFactory.swtDefaults().grab(false, false).applyTo(styledText);

        }

        public void updateContent(OntModel model) {
            List<Resource> resources = OntModelUtils.getOntologies(model.getBaseModel());
            currentOntology = null;
            imgLabel.setImage(null);

            if (resources.isEmpty()) {
                String text = "The model contains no ontology resource";
                styledText.setText(text);
                GridDataFactory.swtDefaults().exclude(true).applyTo(imgLabel);
            }
            else if (resources.size() == 1) {
                currentOntology = resources.get(0);

                String ontologyUri = labelProvider.getText(currentOntology);
                ontologyUri = ontologyUri.replace("<", "").replace(">", "");
                ontologyUri += " (open)";

                styledText.setText(ontologyUri);

                StyleRange style = new StyleRange();
                style.underline = true;
                style.underlineStyle = SWT.UNDERLINE_LINK;

                int[] ranges = { ontologyUri.indexOf("open"), 4 };

                styledText.setStyleRanges(ranges, new StyleRange[] { style });
                imgLabel.setImage(labelProvider.getImage(currentOntology));
                GridDataFactory.swtDefaults().applyTo(imgLabel);
            }
            else {
                String text = "Multiple ontology resources exist in the model";
                styledText.setText(text);
                GridDataFactory.swtDefaults().exclude(true).applyTo(imgLabel);
            }
            layout(true, true);
        }
    }

    private abstract class ResourceOverview {
        protected boolean outdated;
        protected Section section;
        protected TableViewer tableViewer;

        protected final List<Resource> resources;

        protected Composite composite;
        protected boolean sectionIsInitiallyExpanded;
        protected Link moreLink;

        protected final String resourceTitlePlural;
        protected final String resourceTitleSingular;

        protected final InspectOrder inspectOrder;

        protected Table table;
        protected int tableHeight = 280;

        public ResourceOverview(final String titleSingular, final String titlePlural,
                final InspectOrder inspectOrder, final List<ByteArrayTransfer> transferTypes) {
            this.resourceTitleSingular = titleSingular;
            this.resourceTitlePlural = titlePlural;
            this.inspectOrder = inspectOrder;
            this.outdated = true;
            this.sectionIsInitiallyExpanded = false;
            this.resources = Lists.newArrayList();
        }

        public void dispose() {
        }

        /**
         * Sets the outdated flag; if outdated the overview may not contain the
         * most recent resources.
         * 
         * @param outdated
         */
        public void setOutdated(boolean outdated) {
            this.outdated = outdated;
        }

        public boolean isOutdated() {
            return outdated;
        }

        public boolean isExpanded() {
            if (!Widgets.isNullOrDisposed(section)) {
                return section.isExpanded();
            }
            return false;
        }

        @SuppressWarnings("unused")
        public void setSectionInitiallyExpanded() {
            sectionIsInitiallyExpanded = true;
        }

        public void setPending(String status) {
            if (Widgets.isNullOrDisposedViewer(tableViewer)) {
                return;
            }
            tableViewer.setInput(Lists.newArrayList(new PendingElement(status)));
            tableViewer.setItemCount(1);
        }

        @SuppressWarnings("unused")
        public String getTitleSingular() {
            return resourceTitleSingular;
        }

        public String getTitlePlural() {
            return resourceTitlePlural;
        }

        public void createFormContent() {
            createSection(resourceTitlePlural, sectionIsInitiallyExpanded);

            ToolBarManager toolBarManager = Sections.createToolbarManager(section);

            addActionsToSection(toolBarManager);

        }

        public void informUpdating() {
            if (!Widgets.isNullOrDisposed(section)) {
                Display.getDefault().asyncExec(new Runnable() {
                    @Override
                    public void run() {
                        if (!resources.isEmpty()) {
                            String status = String.format("Updating %s %s...", resources.size(),
                                    StringUtils.lowerCase(resourceTitlePlural));
                            setPending(status);
                        }
                    }
                });
            }
        }

        protected void addActionsToSection(ToolBarManager toolBarManager) {
        }

        public void createTable() {
            composite = createSectionClientComposite(section);
            GridLayoutFactory.fillDefaults().numColumns(1).spacing(SWT.DEFAULT, 0)
                    .extendedMargins(2, 5, 0, 10).applyTo(composite);

            toolkit.createLabel(composite, String.format(
                    "This section shows all %s defined within this semantic file.",
                    StringUtils.lowerCase(resourceTitlePlural)));

            tableViewer = new TableViewer(composite, SWT.MULTI | SWT.VIRTUAL | SWT.BORDER);
            tableViewer.setUseHashlookup(true);

            table = tableViewer.getTable();
            GridDataFactory.fillDefaults().grab(true, false).hint(SWT.DEFAULT, tableHeight)
                    .indent(0, 10).applyTo(table);

            table.addKeyListener(new KeyAdapter() {

                private final FindTopIndexExecution execution = new FindTopIndexExecution();

                class FindTopIndexExecution implements Runnable {
                    private static final int MAX_DELAY = 550;
                    private static final int CHECK_INTERVAL = 10;
                    private final ModelNodeLabelProvider labelProvider = getLabelProvider();

                    private String sequence;
                    private long lastRelease;
                    private boolean running;
                    private boolean started;

                    public FindTopIndexExecution() {
                        clear();
                    }

                    public boolean isRunning() {
                        return running;
                    }

                    public void start() {
                        if (!started) {
                            started = true;
                            Display.getDefault().timerExec(CHECK_INTERVAL, this);
                        }
                    }

                    public void addKey(char key) {
                        lastRelease = Calendar.getInstance().getTimeInMillis();
                        sequence += new String(new char[] { key }).toLowerCase();
                    }

                    public void clear() {
                        sequence = "";
                    }

                    @Override
                    public void run() {
                        long now = Calendar.getInstance().getTimeInMillis();
                        if (now - lastRelease > MAX_DELAY) {
                            findTopIndex();
                        }
                        else {
                            Display.getDefault().timerExec(CHECK_INTERVAL, this);
                        }
                    }

                    private void findTopIndex() {
                        running = true;
                        int index = 0;
                        for (int i = 0; i < sequence.length(); i++) {
                            for (Resource resource : resources.subList(index, resources.size())) {
                                String name = labelProvider.getText(resource).toLowerCase();
                                if (i >= name.length()) {
                                    continue;
                                }
                                if (name.charAt(i) >= sequence.charAt(i)) {
                                    break;
                                }
                                index++;
                            }
                        }
                        final int topIndex = index;
                        Display.getDefault().syncExec(new Runnable() {
                            @Override
                            public void run() {
                                table.deselectAll();
                                table.select(topIndex);
                                table.setTopIndex((topIndex > 0) ? topIndex - 1 : topIndex);
                            }
                        });
                        clear();
                        running = false;
                        started = false;
                    }
                }

                @Override
                public void keyReleased(KeyEvent e) {
                    // Do nothing if execution is already running, a mouse
                    // button was pressed, or CTRL was pressed.
                    if (execution.isRunning() || (e.keyCode & SWT.BUTTON_MASK) != 0
                            || e.keyCode == SWT.CTRL) {
                        return;
                    }
                    execution.addKey(e.character);
                    execution.start();
                }
            });

            tableViewer.setLabelProvider(new ResourceViewerLabelProvider(inspectOrder));
            tableViewer.setContentProvider(new LazyContentProvider() {

                Object[] elements;

                @Override
                public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
                    elements = new Object[0];
                    if (newInput instanceof Collection<?>) {
                        elements = ((Collection<?>) newInput).toArray();
                    }
                }

                @Override
                public void updateElement(int index) {
                    if (index < elements.length) {
                        tableViewer.replace(elements[index], index);
                    }
                }
            });
            tableViewer.addDoubleClickListener(new IDoubleClickListener() {
                @Override
                public void doubleClick(DoubleClickEvent event) {
                    IStructuredSelection selection = (IStructuredSelection) tableViewer
                            .getSelection();
                    Resource resource = (Resource) selection.getFirstElement();
                    CorePlugin.getDefault().openResource(resource);
                }
            });
            ResourceViewerToolTipSupport.enableFor(tableViewer, ModelOverviewFormPage.this);

            DndUtils.addDragSupport(tableViewer, getOntModel());
        }

        private void createSection(String title, boolean expanded) {
            section = toolkit.createSection(scrolledForm.getBody(), ExpandableComposite.TWISTIE
                    | ExpandableComposite.TITLE_BAR);
            section.setLayoutData(new ColumnLayoutData());
            TableWrapLayout layout = new TableWrapLayout();
            layout.leftMargin = 0;
            layout.horizontalSpacing = 1;
            layout.verticalSpacing = 1;
            section.setLayout(layout);
            section.setText(title);
            section.setExpanded(expanded);
            toolkit.paintBordersFor(section);
            section.addExpansionListener(new ExpansionAdapter() {

                @Override
                public void expansionStateChanged(ExpansionEvent e) {
                    boolean expanded = e.getState();
                    if (expanded && outdated) {
                        startExploreJob();
                    }
                }
            });
        }

        private Composite createSectionClientComposite(Section section) {
            Composite clientComposite = toolkit.createComposite(section, SWT.NONE);
            TableWrapLayout layout = new TableWrapLayout();
            layout.leftMargin = 1;
            layout.rightMargin = 1;
            layout.topMargin = 1;
            layout.bottomMargin = 1;
            layout.verticalSpacing = 0;
            layout.horizontalSpacing = 0;
            clientComposite.setLayout(layout);

            TableWrapData layoutData = new TableWrapData(TableWrapData.FILL_GRAB,
                    TableWrapData.FILL_GRAB, 1, 1);
            layoutData.indent = 0;
            clientComposite.setLayoutData(layoutData);

            section.setClient(clientComposite);
            toolkit.paintBordersFor(clientComposite);
            return clientComposite;
        }

        public void clear() {
            synchronized (resources) {
                resources.clear();
                outdated = true;
            }
        }

        public Job createRefreshJob(final boolean forceOpen) {
            Job refreshJob = new Job("__update" + resourceTitlePlural) {

                @Override
                protected IStatus run(IProgressMonitor monitor) {
                    Display.getDefault().asyncExec(new Runnable() {
                        @Override
                        public void run() {
                            refresh();
                            refreshForm();
                            if (forceOpen && !section.isExpanded()) {
                                section.setExpanded(true);
                            }
                        }
                    });
                    return Status.OK_STATUS;
                }
            };
            refreshJob.setSystem(true);
            refreshJob.schedule();
            return refreshJob;
        }

        public void refresh() {
            Widgets.disposeIfExists(moreLink);
            synchronized (resources) {
                if (Widgets.isNullOrDisposedViewer(tableViewer)) {
                    return;
                }
                tableViewer.setInput(resources);
                tableViewer.setItemCount(resources.size());
            }
        }

        public void updateHeading() {
            String heading = getHeading();
            updateHeading(heading);
        }

        abstract protected String getHeading();

        private void updateHeading(final String heading) {
            Display.getDefault().asyncExec(new Runnable() {

                @Override
                public void run() {
                    if (Widgets.isNullOrDisposed(section)) {
                        return;
                    }
                    section.setText(heading);
                }
            });
        }

        abstract public void findResources(final IProgressMonitor monitor);
    }

    private class OpenResourceOverview extends ResourceOverview {
        public int count = 0;

        public OpenResourceOverview() {
            super("Open resource", "Open resources", InspectOrder.CLASS_PROPERTY_INDIVIDUAL, Lists
                    .newArrayList(ResourceArrayListTransfer.getInstance(),
                            ResourceTransfer.getInstance(), TextTransfer.getInstance()));
            tableHeight = 140;
            // setSectionInitiallyExpanded();
        }

        @Override
        protected String getHeading() {
            String title = resourceTitlePlural;
            count = getOpenResources().size();
            if (count > 0) {
                title = String.format("%s (%s)", resourceTitlePlural, count);
            }
            return title;
        }

        public int getCount() {
            return count;
        }

        @Override
        public void findResources(IProgressMonitor monitor) {
            synchronized (resources) {
                resources.clear();
                resources.addAll(getOpenResources());
            }
            outdated = false;
        }
    }

    private class ResourceTypeOverview extends ResourceOverview {
        protected final String resourceTypeURI;
        protected final String addImageLocation;

        protected int baseResourcesCount;
        protected int importResourcesCount;

        protected boolean anonymousAllowed;
        protected String excludeInstancesOfTypeURI;
        protected boolean includeAnonymousInstances;
        protected Menu menu;
        protected MenuItem showAnonymousItem;

        public ResourceTypeOverview(final String titleSingular, final String titlePlural,
                final String resourceTypeURI, final String addImageLocation,
                final InspectOrder inspectOrder, final List<ByteArrayTransfer> transferTypes) {
            super(titleSingular, titlePlural, inspectOrder, transferTypes);
            this.resourceTypeURI = resourceTypeURI;
            this.addImageLocation = addImageLocation;
            this.anonymousAllowed = true;
            this.includeAnonymousInstances = false;
        }

        @Override
        public void dispose() {
            // for some strange reason the menu isn't disposed and this cause a
            // memory leak since the selection listener of the menu item
            // contains a reference to this class
            menu.dispose();
        }

        public void setAnonymousAllowed(boolean allowed) {
            this.anonymousAllowed = allowed;
        }

        public void setExcludeInstancesOfType(String typeURI) {
            this.excludeInstancesOfTypeURI = typeURI;
        }

        public void setIncludeAnonymousInstances() {
            includeAnonymousInstances = true;
        }

        @Override
        public void clear() {
            baseResourcesCount = 0;
            importResourcesCount = 0;
            super.clear();
        }

        @Override
        protected void addActionsToSection(ToolBarManager toolBarManager) {
            if (addImageLocation != null) {
                // Action: Create new resource
                Map<String, String> paramMap = Maps.newHashMap();
                paramMap.put(CreateResourceHandler.PARAMETER_RESOURCE_TYPE_URI, resourceTypeURI);
                if (!anonymousAllowed) {
                    paramMap.put(CreateResourceHandler.PARAMETER_ANONYMOUS_ALLOWED, "false");
                }
                Action createAction = CreateResourceHandler.createAction(getSite(), "New "
                        + resourceTitleSingular, paramMap);
                Image addImage = CorePlugin.getDefault().getDecoratedImage(addImageLocation,
                        CorePluginImages.IMG_OVERLAY_ADD, OverlayImageIcon.TOP_RIGHT);
                Sections.addToolbarAction(toolBarManager,
                        ImageDescriptor.createFromImage(addImage), createAction);
            }

            // Action: Create menu with option show/hide anonymous resources
            menu = new Menu(section);
            menu.addListener(SWT.Show, new Listener() {
                @Override
                public void handleEvent(Event event) {
                    if (event.text != null) {
                        menu.setVisible(true);
                    }
                }
            });

            showAnonymousItem = new MenuItem(menu, SWT.CHECK);
            showAnonymousItem.setText("Show anonymous");
            showAnonymousItem.setSelection(includeAnonymousInstances);
            showAnonymousItem.addSelectionListener(new SelectionListener() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    includeAnonymousInstances = !includeAnonymousInstances;
                    showAnonymousItem.setSelection(includeAnonymousInstances);
                    outdated = true;
                    startExploreJob();
                }

                @Override
                public void widgetDefaultSelected(SelectionEvent e) {
                }
            });

            Action menuAction = new Action() {
                @Override
                public void run() {
                    Event event = new Event();
                    event.type = SWT.Show;
                    event.button = SWT.BUTTON2;
                    event.text = "";
                    menu.notifyListeners(SWT.Show, event);
                }
            };
            menuAction.setToolTipText("View Menu");

            Sections.addToolbarAction(toolBarManager,
                    CorePlugin.getDefault().getImageDescriptor(CorePluginImages.IMG_DROPDOWN_MENU),
                    menuAction);

        }

        @Override
        protected String getHeading() {
            String title = resourceTitlePlural;
            updateCounts();
            if (baseResourcesCount > 0 && importResourcesCount == 0) {
                title = String.format("%s (%s)", resourceTitlePlural, baseResourcesCount);
            }
            else if (baseResourcesCount > 0 || importResourcesCount > 0) {
                title = String.format("%s (%s+%s)", resourceTitlePlural, baseResourcesCount,
                        importResourcesCount);
            }
            return title;
        }

        private void updateCounts() {
            baseResourcesCount = getCount(BASE_MODEL_GRAPH_ID, dataset);
            importResourcesCount = 0;
            for (String importUri : visibleSubModelUris) {
                importResourcesCount += getCount(importUri, dataset);
            }
        }

        protected int getCount(final String graphId, final Dataset dataset) {
            if (dataset == null) {
                return 0;
            }
            Query query = createCountQuery(Lists.newArrayList(graphId));
            QueryExecution execution = QueryExecutionFactory.create(query, dataset);
            try {
                ResultSet result = execution.execSelect();
                while (result.hasNext()) {
                    QuerySolution solution = result.next();
                    RDFNode node = solution.get("count");
                    int count = 0;
                    if (node != null && node.isLiteral()) {
                        count = node.asLiteral().getInt();
                    }
                    return count;
                }
            }
            catch (QueryExecException ex) {
                logger.error("Exception thrown during execution of counting query", ex);
            }
            return 0;
        }

        @Override
        public void findResources(final IProgressMonitor monitor) {
            List<String> graphUris = Lists.newArrayList();
            if (baseResourcesCount > 0) {
                graphUris.add(BASE_MODEL_GRAPH_ID);
            }
            if (importResourcesCount > 0) {
                for (String graphUri : visibleSubModelUris) {
                    graphUris.add(graphUri);
                }
            }
            synchronized (resources) {
                resources.clear();
                resources.addAll(retrieveResources(monitor, graphUris));
            }
            outdated = false;
        }

        private List<OntResource> retrieveResources(final IProgressMonitor monitor,
                List<String> graphUris) {
            List<OntResource> found = Lists.newArrayList();
            if (graphUris == null || graphUris.isEmpty()) {
                return found;
            }

            OntModel ontModel = getOntModel();

            Query query = createResourcesQuery(graphUris);
            QueryExecution execution = QueryExecutionFactory.create(query, dataset);
            ResultSet result = execution.execSelect();
            while (result.hasNext()) {
                QuerySolution solution = result.next();
                Resource resource = solution.getResource("resource");
                found.add(JenaUtil.asOntResource(resource, ontModel));
                if (monitor.isCanceled()) {
                    return found;
                }
            }
            return found;
        }

        private Query createCountQuery(List<String> graphUris) {
            Query query = QueryFactory.create();
            query.setQuerySelectType();
            Expr aggregate = query.allocAggregate(new AggCountVarDistinct(new ExprVar("resource")));
            query.getProject().add(Var.alloc("count"), aggregate);
            constructQueryPattern(query, graphUris, false);
            return query;
        }

        private Query createResourcesQuery(List<String> graphUris) {
            Query query = QueryFactory.create();
            query.setQuerySelectType();
            query.setDistinct(true);
            query.addResultVar("resource");
            constructQueryPattern(query, graphUris, true);
            return query;
        }

        /** Expects graphUris to be non-empty */
        private void constructQueryPattern(Query query, List<String> graphUris, boolean sortLabels) {
            Query resourcesQuery = QueryFactory.create();
            resourcesQuery.setQuerySelectType();
            resourcesQuery.setDistinct(true);
            resourcesQuery.addResultVar("resource");

            ElementGroup inner = new ElementGroup();
            ElementPathBlock pathElement = new ElementPathBlock();
            Path instanceOfPath = PathUtil.getPath(PathUtil.IS_INSTANCE_OF);
            pathElement.addTriplePath(new TriplePath(Var.alloc("resource"), instanceOfPath,
                    NodeFactory.createURI(resourceTypeURI)));
            inner.addElement(pathElement);

            ElementUnion graphUnion = new ElementUnion();
            for (String graphUri : graphUris) {
                ElementNamedGraph graphElement = new ElementNamedGraph(
                        NodeFactory.createURI(graphUri), new ElementTriplesBlock(
                                BasicPattern.wrap(Lists.newArrayList(new Triple(Var
                                        .alloc("resource"), RDF.type.asNode(), Var.alloc("?x"))))));
                graphUnion.addElement(graphElement);
            }
            inner.addElement(graphUnion);

            if (!Strings.isNullOrEmpty(excludeInstancesOfTypeURI)) {
                ElementPathBlock filterElement = new ElementPathBlock();
                filterElement.addTriplePath(new TriplePath(Var.alloc("resource"), instanceOfPath,
                        NodeFactory.createURI(excludeInstancesOfTypeURI)));
                inner.addElement(new ElementFilter(new E_NotExists(filterElement)));
            }
            if (!includeAnonymousInstances) {
                inner.addElement(new ElementFilter(new E_LogicalNot(new E_IsBlank(new ExprVar(
                        "resource")))));
            }
            resourcesQuery.setQueryPattern(inner);

            ElementGroup outer = new ElementGroup();
            ElementSubQuery subElement = new ElementSubQuery(resourcesQuery);
            outer.addElement(subElement);
            if (sortLabels) {
                ElementTriplesBlock pattern = new ElementTriplesBlock();
                pattern.addTriple(new Triple(Var.alloc("resource"), LabelProviderPropertyFunction
                        .asNode(), Var.alloc("?x")));
                pattern.addTriple(new Triple(Var.alloc("?x"), RDF.first.asNode(), Var.alloc("text")));
                pattern.addTriple(new Triple(Var.alloc("?x"), RDF.rest.asNode(), Var.alloc("?y")));
                pattern.addTriple(new Triple(Var.alloc("?y"), RDF.first.asNode(), Var
                        .alloc("image")));
                pattern.addTriple(new Triple(Var.alloc("?y"), RDF.rest.asNode(), RDF.nil.asNode()));
                outer.addElement(pattern);

                Expr sort = new E_StrLowerCase(new ExprVar("text"));
                query.addOrderBy(sort, Query.ORDER_ASCENDING);
            }

            query.setQueryPattern(outer);
        }
    }

    @Override
    protected void createScrolledFormContent(ScrolledForm scrolledForm) {
        this.scrolledForm = scrolledForm;

        // Get the proper heading font
        IThemeManager themeManager = PlatformUI.getWorkbench().getThemeManager();
        ITheme currentTheme = themeManager.getCurrentTheme();
        FontRegistry fontRegistry = currentTheme.getFontRegistry();
        Font headingFont = fontRegistry.get(OntologyEditorFormHeading.HEADER_FONT);

        scrolledForm.setText("Model Overview");
        // scrolledForm.setImage(CorePlugin.getDefault().getImage(CorePluginImages.IMG_ONTOLOGY_FILE));
        scrolledForm.setFont(headingFont);
        ColumnLayout layout = new ColumnLayout();
        layout.maxNumColumns = 3;
        layout.horizontalSpacing = 10;
        layout.leftMargin = 10;
        layout.rightMargin = 10;
        scrolledForm.getBody().setLayout(layout);

        Section ontologySection = toolkit.createSection(scrolledForm.getBody(),
                ExpandableComposite.EXPANDED | ExpandableComposite.TITLE_BAR
                        | ExpandableComposite.COMPACT);
        ontologySection.setLayoutData(new ColumnLayoutData(100, 100));
        ontologySection.setText("Ontology");

        ontologyLink = new OntologyLink(ontologySection, toolkit, getLabelProvider());
        ontologyLink.createContent();
        ontologySection.setClient(ontologyLink);

        toolkit.decorateFormHeading(scrolledForm.getForm());

        // Action: Refresh
        Action refreshAction = new Action() {
            @Override
            public void run() {
                refresh(true);
            }
        };
        refreshAction.setText("Refresh");
        refreshAction.setImageDescriptor(CorePlugin.getDefault().getImageDescriptor(
                CorePluginImages.IMG_REFRESH));
        scrolledForm.getToolBarManager().add(refreshAction);

        menuComposite = scrolledForm.getBody();

        modelVisibilityProvider = new ModelVisibilityMenuProvider();
        modelVisibilityProvider.setInput(this, visibleSubModelUris);
        modelVisibilityProvider.setMenuItemLabelProvider(new IModelVisibilityLabelProvider() {

            @Override
            public String getText(String uri) {
                String shortForm = getOntModel().shortForm(uri);
                if (shortForm.endsWith(":")) {
                    shortForm = shortForm.substring(0, shortForm.length() - 1);
                }
                return shortForm;
            }
        });
        modelVisibilityProvider.setChangeListener(new ModelVisibilityListener() {

            @Override
            public void visibleModelsChanged(List<String> visibleUris) {
                visibleSubModelUris.clear();
                visibleSubModelUris.addAll(visibleUris);
                refresh(true);
            }
        });

        Action menuAction = new Action() {
            private static final String EVENT_SHOW_MENU = "showMenu";

            @Override
            public void run() {
                Event event = new Event();
                event.type = SWT.Show;
                event.button = SWT.BUTTON2;
                event.text = EVENT_SHOW_MENU;

                Menu menu = createButtonMenu(menuComposite);
                menu.notifyListeners(SWT.Show, event);
            }

            private Menu createButtonMenu(final Composite menuComposite) {
                MenuManager manager = new MenuManager();
                manager.addMenuListener(modelVisibilityProvider);

                final Menu menu = manager.createContextMenu(menuComposite);
                menu.addListener(SWT.Show, new Listener() {
                    @Override
                    public void handleEvent(Event event) {
                        if (EVENT_SHOW_MENU.equals(event.text)) {
                            menu.setVisible(true);
                        }
                    }
                });
                return menu;
            }
        };

        menuAction.setToolTipText("View Menu");
        ImageDescriptor descriptor = CorePlugin.getDefault().getImageDescriptor(
                CorePluginImages.IMG_DROPDOWN_MENU);
        menuAction.setImageDescriptor(descriptor);
        scrolledForm.getToolBarManager().add(menuAction);
        scrolledForm.getToolBarManager().update(true);

        for (ResourceOverview overview : resourceOverviews) {
            overview.createFormContent();
            overview.createTable();
        }
        setPageInitialized(true);
    }

    /**
     * Creates a new ExploreModelJob and schedules the execution of this job.
     */
    private void startExploreJob() {
        if (!isActive()) {
            return;
        }
        if (exploreJob != null) {
            if (!exploreJob.cancel()) {
                try {
                    exploreJob.join();
                }
                catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }
        exploreJob = new ExploreModelJob();
        exploreJob.schedule();
    }

    @Override
    public void notifyEvent(IModelEvent event) {
        if (dataset != null) {
            dataset = null;
        }
        // TODO: Maybe make selective - only if imports have changed?
        modelVisibilityProvider.setInput(this, visibleSubModelUris);

        getSite().getShell().getDisplay().asyncExec(new Runnable() {

            @Override
            public void run() {
                startExplore(!OntologyFileEditorPreference.autoExploreDisabled());
            }
        });

    }

    private void startExplore(boolean explore) {
        if (isActive()) {
            clearOverviews();
            refresh();
            if (getOntModel() != null && explore) {
                startExploreJob();
            }
        }
        else {
            for (ResourceOverview overview : resourceOverviews) {
                overview.setOutdated(true);
            }
        }
    }

    private void clearOverviews() {
        for (ResourceOverview overview : resourceOverviews) {
            overview.clear();
        }
    }

    private void refresh() {
        refresh(false);
    }

    private void refresh(boolean changed) {
        if (changed) {
            for (ResourceOverview overview : resourceOverviews) {
                overview.setOutdated(true);
            }
            startExploreJob();
        }
        for (ResourceOverview overview : resourceOverviews) {
            overview.refresh();
        }
        refreshForm();
    }

    public void refreshForm() {
        if (!Widgets.isNullOrDisposed(scrolledForm)) {
            ontologyLink.updateContent(getOntModel());
            scrolledForm.layout();
            scrolledForm.reflow(true);
        }
    }

    public boolean isDisposed() {
        return disposed;
    }

    /**
     * This job gathers the classes, properties and individuals from the model.
     * 
     * @author Mike Henrichs
     * 
     */
    private class ExploreModelJob extends JobWithMonitor {

        private final List<ResourceOverview> outdatedOverviews;

        public ExploreModelJob() {
            super("Exploring Model");
            this.outdatedOverviews = Lists.newArrayList();
            for (ResourceOverview overview : resourceOverviews) {
                if (overview.isOutdated()) {
                    outdatedOverviews.add(overview);
                }
            }
        }

        @Override
        protected IStatus run(final IProgressMonitor monitor) {
            try {
                startMonitorUpdate(monitor, "Exploring Model", 4 + outdatedOverviews.size());
                Display.getDefault().syncExec(new Runnable() {
                    @Override
                    public void run() {
                        if (isDisposed()) {
                            return;
                        }
                        for (ResourceOverview overview : outdatedOverviews) {
                            overview.setPending("Exploring...");
                        }
                        refreshForm();
                    }
                });

                // Create the dataset if not done already
                if (dataset == null) {
                    updateSubTask("Creating Dataset...");
                    dataset = DatasetFactory.createMem();
                    synchronized (dataset) {
                        if (dataset == null) {
                            // Could happen that due to racing the dataset is
                            // set to null
                            dataset = DatasetFactory.createMem();
                        }
                        dataset.setDefaultModel(getOntModel());
                        dataset.addNamedModel(BASE_MODEL_GRAPH_ID, getBaseModel());
                        for (String uri : getSubModelURIs()) {
                            Model submodel = getSubModel(uri);
                            if (submodel == null) {
                                submodel = ModelFactory.createDefaultModel();
                            }
                            dataset.addNamedModel(uri, submodel);
                        }
                    }
                }
                addWorked(1);
                if (monitor.isCanceled()) {
                    return Status.CANCEL_STATUS;
                }

                final List<Job> jobs = Lists.newArrayList();
                for (ResourceOverview overview : outdatedOverviews) {
                    if (isDisposed()) {
                        return Status.CANCEL_STATUS;
                    }

                    boolean forceOpen = false;
                    if (overview instanceof OpenResourceOverview) {
                        final OpenResourceOverview openResourceOverview = (OpenResourceOverview) overview;
                        if (!openResourceOverview.isExpanded()
                                && openResourceOverview.getCount() == 0
                                && !getOpenResources().isEmpty()) {
                            forceOpen = true;
                        }
                    }

                    overview.updateHeading();
                    if (!overview.isExpanded() && !forceOpen) {
                        continue;
                    }
                    updateSubTask(String.format("Refreshing %s...",
                            StringUtils.lowerCase(overview.getTitlePlural())));
                    overview.findResources(monitor);
                    overview.informUpdating();
                    jobs.add(overview.createRefreshJob(forceOpen));
                    addWorked(1);
                    if (monitor.isCanceled()) {
                        return Status.CANCEL_STATUS;
                    }
                }
                addWorked(2);
                if (monitor.isCanceled()) {
                    return Status.CANCEL_STATUS;
                }

                if (!jobs.isEmpty()) {
                    Display.getDefault().asyncExec(new Runnable() {

                        @Override
                        public void run() {
                            refreshForm();
                        }
                    });
                    for (Job job : jobs) {
                        job.join();
                        if (monitor.isCanceled()) {
                            return Status.CANCEL_STATUS;
                        }
                    }
                }
                addWorked(1);
            }
            catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            finally {
                stopMonitorUpdate();
                monitor.done();
            }
            return Status.OK_STATUS;
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        String property = event.getProperty();
        if (property.equals(LabelsPreference.PREFERENCE_RESOURCE_LABEL_RENDERING)
                || property.equals(LabelsPreference.PREFERENCE_ALWAYS_SHOW_ONTOLOGY_URI)) {
            for (ResourceOverview overview : resourceOverviews) {
                overview.setOutdated(true);
            }
            startExploreJob();
            refresh();
        }
        else if (property.equals(LanguagesPreference.PREFERENCE_DISPLAY_LANGUAGES)) {
            refresh();
        }
    }

    @Override
    public void resourceOpened(OntResource resource) {
        super.resourceOpened(resource);
        refreshOpenResources();
    }

    @Override
    public void resourcesClosed(List<OntResource> resources) {
        super.resourcesClosed(resources);
        refreshOpenResources();
    }

    private void refreshOpenResources() {
        for (ResourceOverview resourceOverview : resourceOverviews) {
            if (resourceOverview instanceof OpenResourceOverview) {
                resourceOverview.setOutdated(true);
            }
        }
        startExploreJob();
        refresh();
    }

    @Override
    public void setActive(boolean active) {
        super.setActive(active);

        if (!active || wasActive) {
            // the part is becoming inactive or the part is already active!
            wasActive = active;
            return;
        }

        wasActive = active;

        boolean refreshRequired = false;
        for (ResourceOverview resourceOverview : resourceOverviews) {
            if (resourceOverview.isOutdated()) {
                refreshRequired = true;
            }
        }
        if (refreshRequired) {
            startExploreJob();
            refresh();
        }
    }

}
