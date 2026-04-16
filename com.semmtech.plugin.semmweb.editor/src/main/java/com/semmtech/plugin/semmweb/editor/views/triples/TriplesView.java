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

package com.semmtech.plugin.semmweb.editor.views.triples;


import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.commands.IElementUpdater;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.menus.UIElement;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ReifiedStatement;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.reasoner.Derivation;
import com.hp.hpl.jena.shared.ClosedException;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.util.iterator.Filter;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.CorePluginImages;
import com.semmtech.plugin.semmweb.core.actions.IModelVisibilityLabelProvider;
import com.semmtech.plugin.semmweb.core.actions.ModelVisibilityListener;
import com.semmtech.plugin.semmweb.core.actions.ModelVisibilityMenuProvider;
import com.semmtech.plugin.semmweb.core.commands.Commands;
import com.semmtech.plugin.semmweb.core.dialog.ResourceSelectionDialog;
import com.semmtech.plugin.semmweb.core.dnd.ResourceTransfer;
import com.semmtech.plugin.semmweb.core.dnd.ViewerDragAdapter;
import com.semmtech.plugin.semmweb.core.forms.editor.OntologyFormEditor;
import com.semmtech.plugin.semmweb.core.model.IModelProvider;
import com.semmtech.plugin.semmweb.core.model.events.ModelActivatedEvent;
import com.semmtech.plugin.semmweb.core.model.events.ModelChangedEvent;
import com.semmtech.plugin.semmweb.core.model.events.SubModelAddedEvent;
import com.semmtech.plugin.semmweb.core.model.events.SubModelRemovedEvent;
import com.semmtech.plugin.semmweb.core.views.AbstractModelListenerView;
import com.semmtech.plugin.semmweb.editor.viewers.TriplesViewerToolTipSupport;
import com.semmtech.plugin.semmweb.editor.views.sourceprovider.ReificationStateSourceProvider;
import com.semmtech.plugin.semmweb.editor.views.sourceprovider.SelectedURISourceProvider;
import com.semmtech.semantics.sparql.PathUtil;
import com.semmtech.semantics.sparql.QueryBuilder;
import com.semmtech.semantics.util.JenaUtil;
import com.semmtech.semantics.util.iterator.AcceptAllStatementFilter;
import com.semmtech.ui.plugin.EclipseUIPlugin;
import com.semmtech.ui.plugin.jobs.JobWithMonitor;
import com.semmtech.ui.plugin.jobs.Jobs;
import com.semmtech.ui.plugin.viewers.LazyContentProvider;
import com.semmtech.ui.plugin.viewers.LazyTreeContentProvider;
import com.semmtech.ui.plugin.viewers.ViewerSelectionProvider;
import com.semmtech.ui.plugin.widgets.Widgets;


/**
 * View for listing all triples stored within an ontology model, this model is
 * taken from activated editor. To accomplish this an internal IPartListener
 * object is registered to the WorkbenchWindow.
 * 
 * @author Mike Henrichs
 * @author Sander Stolk
 */
public class TriplesView extends AbstractModelListenerView {
    public static final String ID = "com.semmtech.plugin.semmweb.editor.views.triples";

    private static final String PARAM_SHOW_FILTER = "triplesView.showFilter";
    private static final String PARAM_SHOW_REIFICATION = "triplesView.showReification";
    private static final String PARAM_SHOW_BASE_MODEL = "triplesView.showBaseModel";
    private static final String PARAM_SORT_COLUMN = "triplesView.sortColumn";
    private static final String PARAM_SORT_DIRECTION = "triplesView.sortDirection";
    private static final String PARAM_SORT_BY_REIFICATION = "triplesView.sortByReification";
    private static final String PARAM_VISIBLE_MODEL_URIS = "triplesView.visibleModelURIS";
    private static final String PARAM_FILTER_SELECTED_SUBJECTS = "triplesView.filterSelectedSubjects";
    private static final String PARAM_FILTER_SELECTED_PREDICATES = "triplesView.filterSelectedPredicates";
    private static final String PARAM_FILTER_SELECTED_OBJECTS = "triplesView.filterSelectedObjects";
    private static final String PARAM_REIFIED_PREDICATES = "triplesView.reifiedPredicates";

    private final static int DEFAULT_MAX_STATEMENTS = 5000;

    private static Logger logger = Logger.getLogger(TriplesView.class);
    private static TriplesView singleton;
    private static boolean hideStatementPredicate = true;

    private Object mutex = new Object();

    private ViewerSelectionProvider selectionProvider = new ViewerSelectionProvider();

    private TreeViewer treeViewer;
    private TableViewer tableViewer;
    private Tree tree;
    private Table table;
    private TriplesViewerComparator comparator;
    private Label statusLabel;
    private Label warningLimitIcon;
    private Label limitReachedLabel;
    private Composite filterComposite;
    private SashForm topSash;
    private ResourceSelector subjectSelector;
    private ResourceSelector objectSelector;
    private ResourceSelector predicateSelector;
    private Composite container;
    private Composite statusComposite;

    private final List<Statement> allStatements;
    private final List<Statement> filteredStatements;
    private final Multimap<Statement, Statement> reifiedStatements;

    private Statement topStatement;

    private Job refreshJob;
    private Job refreshFilterJob;
    private Job applyFilterJob;

    private Filter<Statement> filter = new AcceptAllStatementFilter();

    private LazyTreeContentProvider treeContentProvider;
    private LazyContentProvider tableContentProvider;
    private TriplesLabelProvider triplesLabelProvider;

    private int limit;
    private String statusText;

    private ModelVisibilityMenuProvider modelVisibilityProvider;

    public TriplesView() {
        singleton = this;
        limit = DEFAULT_MAX_STATEMENTS;

        allStatements = Lists.newArrayList();
        reifiedStatements = HashMultimap.create();
        filteredStatements = Lists.newArrayList();

        Commands.setToggleState(ToggleShowReificationHandler.ID, false);
        Commands.setToggleState(ToggleSortByReificationHandler.ID, false);
    }

    @Override
    public void modelActivated(ModelActivatedEvent event) {
        Widgets.disposeIfExists(topSash);
        resetViewer();
        if (event.getModel() != null) {
            restoreState();
            refreshFromModel();
        }
        else {
            modelVisibilityProvider.setInput(null, null);
        }
    }

    private void restoreState() {
        // No actual retrieval is required since no fields are statically stored
        // within this class; ie. the showFilter is changed into a method, which
        // always checks the current viewstate.

        // Restore the viewer comparator
        if (hasStateParameter(PARAM_SORT_COLUMN)) {
            int column = ((Integer) getStateParameter(PARAM_SORT_COLUMN)).intValue();
            int direction = ((Integer) getStateParameter(PARAM_SORT_DIRECTION)).intValue();
            if (comparator == null) {
                comparator = new TriplesViewerComparator(column);
            }
            comparator.setColumn(column, direction);
        }
        else {
            comparator = null;
        }

        // However some commands may need an update:
        Commands.refreshElements(ToggleShowBaseModelHandler.ID);
        Commands.refreshElements(ToggleShowTriplesFilterHandler.ID);
        Commands.refreshElements(ToggleShowReificationHandler.ID);
        Commands.refreshElements(ToggleSortByReificationHandler.ID);

        ReificationStateSourceProvider.setReificationState(getSite().getWorkbenchWindow(),
                showReification());
    }

    private List<Statement> getReifiedStatements(Model model) {
        List<Statement> result = Lists.newArrayList();

        Var varReifiedStatement = Var.alloc("reifiedStatement");
        Var varSubject = Var.alloc("subject");
        Var varPredicate = Var.alloc("predicate");
        Var varObject = Var.alloc("object");

        QueryBuilder qb = QueryBuilder.createSelect(true);
        qb.addTriplePattern(varReifiedStatement, RDF.type, RDF.Statement);
        qb.addTriplePattern(varReifiedStatement, RDF.subject, varSubject);
        qb.addTriplePattern(varReifiedStatement, RDF.predicate, varPredicate);
        qb.addTriplePattern(varReifiedStatement, RDF.object, varObject);
        qb.addResultVars(varSubject, varPredicate, varObject);

        OntModel currentModel = getOntModel();
        for (ResultSet iter = qb.execSelect(model); iter.hasNext();) {
            QuerySolution qs = iter.next();
            Resource subject = qs.getResource(varSubject.getName());
            Property predicate = JenaUtil.asOntProperty(qs.getResource(varPredicate.getName()),
                    currentModel);
            RDFNode object = qs.get(varObject.getName());
            Statement statement = currentModel.createStatement(subject, predicate, object);
            result.add(statement);
        }

        return result;
    }

    @Override
    public void modelChanged(ModelChangedEvent event) {
        // If a reified statement was added, set it to be the viewer's top item.
        topStatement = null;
        if (!Widgets.isNullOrDisposedViewer(treeViewer)
                || !Widgets.isNullOrDisposedViewer(tableViewer)) {
            Model statementsAdded = event.getModelChanges().getAdditions();
            List<Statement> reifiedStatementsAdded = getReifiedStatements(statementsAdded);
            if (!reifiedStatementsAdded.isEmpty()) {
                topStatement = reifiedStatementsAdded.get(0);
            }
        }

        if ((topStatement != null) && !showReification()) {
            Commands.execute(ToggleShowReificationHandler.ID);
        }
        else {
            refreshFromModel();
        }
    }

    @Override
    public void subModelAdded(SubModelAddedEvent event) {
        if (event.getSubModelURI().equals(IModelProvider.INFERRED_SUBMODEL_URI)) {
            List<String> visibleURIs = Lists.newArrayList(IModelProvider.INFERRED_SUBMODEL_URI);
            setStateParameter(PARAM_VISIBLE_MODEL_URIS, visibleURIs);
            setStateParameter(PARAM_SHOW_BASE_MODEL, new Boolean(false));
            Commands.refreshElements(ToggleShowBaseModelHandler.ID);
        }
        refreshFromModel();
    }

    @Override
    public void subModelRemoved(SubModelRemovedEvent event) {
        refreshFromModel();
    }

    @SuppressWarnings("unchecked")
    private List<Property> getReifiedPredicates() {
        List<Property> reifiedPredicates = Lists.newArrayList();
        if (hasStateParameter(PARAM_REIFIED_PREDICATES)) {
            reifiedPredicates = (List<Property>) getStateParameter(PARAM_REIFIED_PREDICATES);
        }
        return reifiedPredicates;
    }

    private void setReifiedPredicates(List<Property> reifiedPredicates) {
        int columnNumber;

        if (tree != null) {
            columnNumber = tree.getColumnCount();
        }
        else if (table != null) {
            columnNumber = table.getColumnCount();
        }
        else {
            columnNumber = -1;
        }

        // if the sort is applied on a reified property column this column
        // should not exist anymore, in this case I apply the sorting on the
        // first column by default
        if (comparator != null && comparator.getPropertyIndex() < columnNumber) {
            comparator.setColumn(0);
        }
        setStateParameter(PARAM_REIFIED_PREDICATES, reifiedPredicates);
    }

    private void refreshFromModel() {
        synchronized (mutex) {
            LabelProvider labelProvider = getLabelProvider();
            triplesLabelProvider.setLabelProvider(labelProvider);
            triplesLabelProvider.setReifiedStatements(reifiedStatements);
            triplesLabelProvider.setModel(getOntModel());
            triplesLabelProvider.setReifiedPredicates(getReifiedPredicates());
        }
        modelVisibilityProvider.setInput(getModelProvider(), getVisibleModelURIs());
        if (hasModelProvider()) {
            createControls();
            executeRefreshJob();
            updateColumnText();
        }
    }

    @SuppressWarnings("unchecked")
    private List<String> getVisibleModelURIs() {
        if (hasStateParameter(PARAM_VISIBLE_MODEL_URIS)) {
            return (List<String>) getStateParameter(PARAM_VISIBLE_MODEL_URIS);
        }
        return Lists.newArrayList();
    }

    /**
     * Updates the column headers with any additional predicates.
     */
    private void updateColumnText() {
        if (!Widgets.isNullOrDisposed(tree)) {
            for (int i = 0; i < getReifiedPredicates().size(); i++) {
                Property predicate = getReifiedPredicates().get(i);
                int columnIndex = i + 3;
                TreeColumn column = tree.getColumn(columnIndex);
                LabelProvider labelProvider = getLabelProvider();
                String text = labelProvider.getText(predicate);
                if (!Strings.isNullOrEmpty(text)) {
                    column.setText(text);
                }
                else {
                    column.setText(predicate.toString());
                }
            }
        }
    }

    private void refreshViewer() {
        if (showReification()) {
            refreshTreeViewer();
        }
        else {
            refreshTableViewer();
        }
    }

    private void sortStatements() {
        if (comparator != null) {
            comparator.setSortByReification(sortByReification());
            if (allStatements != null) {
                Collections.sort(allStatements, comparator.getInnerComparator());
            }
            if (filteredStatements != null) {
                Collections.sort(filteredStatements, comparator.getInnerComparator());
            }
        }
    }

    private void executeRefreshJob() {
        Jobs.cancelWithJoin(refreshJob);

        statusText = "Loading triples...";

        resetViewer();

        if (hasModelProvider()) {
            // Start the collection and sorting of all model statements:
            IModelProvider provider = getModelProvider();
            refreshJob = new RefreshJob("Refreshing Triples", provider);
            refreshJob.setRule(OntologyFormEditor.MUTEX_RULE);
            refreshJob.setUser(false);
            refreshJob.schedule();
            refreshJob.addJobChangeListener(new JobChangeAdapter() {
                @Override
                public void done(IJobChangeEvent event) {
                    Display.getDefault().asyncExec(new Runnable() {
                        @Override
                        public void run() {
                            if (showFilter()) {
                                executeRefreshFilterJob();
                            }
                            else {
                                refreshViewer();
                                if (topStatement != null) {
                                    snapViewerToStatement(topStatement);
                                }
                            }
                        }
                    });
                }
            });
        }
    }

    private void snapViewerToStatement(Statement statement) {
        if (!Widgets.isNullOrDisposedViewer(tableViewer)) {
            Object treeViewerInput = tableViewer.getInput();
            if (treeViewerInput instanceof List<?>) {
                List<?> list = (List<?>) treeViewerInput;
                int index = list.indexOf(statement);
                if (index >= 0) {
                    tableViewer.getTable().getItem(index);
                    tableViewer.getTable().setTopIndex(index);
                }
            }
        }
        if (!Widgets.isNullOrDisposedViewer(treeViewer)) {
            Object treeViewerInput = treeViewer.getInput();
            if (treeViewerInput instanceof List<?>) {
                List<?> list = (List<?>) treeViewerInput;
                int index = list.indexOf(statement);
                if (index >= 0) {
                    TreeItem treeItem = treeViewer.getTree().getItem(index);
                    treeViewer.getTree().setTopItem(treeItem);
                }
            }
        }
    }

    private void resetViewer() {
        synchronized (mutex) {
            allStatements.clear();
            reifiedStatements.clear();
        }
        refreshViewer();
    }

    private void executeRefreshFilterJob() {
        Jobs.cancelWithJoin(applyFilterJob);

        clearFilterInput();

        if (hasModelProvider()) {
            IModelProvider provider = getModelProvider();
            refreshFilterJob = new FindFilterObjectsJob(provider);
            refreshFilterJob.setRule(OntologyFormEditor.MUTEX_RULE);
            refreshFilterJob.setUser(true);
            refreshFilterJob.schedule();
            refreshFilterJob.addJobChangeListener(new JobChangeAdapter() {
                @Override
                public void done(IJobChangeEvent event) {
                    Display.getDefault().asyncExec(new Runnable() {
                        @Override
                        public void run() {
                            executeApplyFilterJob();
                        }
                    });
                }
            });
        }
    }

    private void clearFilterInput() {
        updateFilter(FilterInput.Empty);
    }

    private void executeApplyFilterJob() {
        Jobs.cancelWithJoin(applyFilterJob);
        applyFilterJob = new ApplyFilterJob();
        applyFilterJob.setRule(OntologyFormEditor.MUTEX_RULE);
        applyFilterJob.setUser(true);
        applyFilterJob.schedule();
        applyFilterJob.addJobChangeListener(new JobChangeAdapter() {
            @Override
            public void done(IJobChangeEvent event) {
                Display.getDefault().asyncExec(new Runnable() {
                    @Override
                    public void run() {
                        refreshViewer();
                    }
                });
            }
        });
    }

    private void createControlsViewer() {
        if (showReification()) {
            createTriplesTree();
        }
        else {
            createTriplesTable();
        }
        getSite().setSelectionProvider(selectionProvider);
        createContextMenu();
        createStatus();
        layoutParent(true, true);
    }

    private void createControls() {
        Widgets.disposeIfExists(topSash);
        topSash = new SashForm(getParent(), SWT.VERTICAL);
        topSash.setSashWidth(6);

        createFilter();

        GridLayout layout = new GridLayout();
        layout.marginWidth = 0;
        layout.marginHeight = 0;

        Widgets.disposeIfExists(container);
        container = new Composite(topSash, SWT.NONE);
        container.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
        container.setLayout(layout);

        createControlsViewer();
    }

    private void refreshTreeViewer() {
        if (Widgets.isNullOrDisposedViewer(treeViewer)) {
            return;
        }

        int size = 0;
        List<Statement> statements;
        if (showFilter()) {
            size = filteredStatements.size();
            statements = filteredStatements;
        }
        else {
            size = allStatements.size();
            statements = allStatements;
        }

        Widgets.disposeIfExists(warningLimitIcon);
        Widgets.disposeIfExists(limitReachedLabel);

        if (size > limit) {
            warningLimitIcon = new Label(statusComposite, SWT.NONE);
            warningLimitIcon.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false,
                    false));
            warningLimitIcon.setImage(CorePlugin.getDefault()
                    .getImage(CorePluginImages.IMG_WARNING));
            warningLimitIcon
                    .setToolTipText(String.format("Only showing top %s statements!", limit));

            limitReachedLabel = new Label(statusComposite, SWT.NONE);
            limitReachedLabel.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false,
                    false));
            limitReachedLabel.setForeground(Display.getDefault()
                    .getSystemColor(SWT.COLOR_DARK_GRAY));
            limitReachedLabel.setText(String.format("%s shown", limit));

            statusComposite.layout(true, true);
            treeViewer.setInput(statements.subList(0, limit));
            tree.setItemCount(limit);
        }
        else {
            treeViewer.setInput(statements);
            tree.setItemCount(size);
        }
        if (showFilter() && (allStatements.size() != filteredStatements.size())) {
            statusLabel.setText(String.format("%s; Filtered: %s statements", statusText,
                    filteredStatements.size()));
        }
        else {
            statusLabel.setText(statusText);
        }
        if (comparator != null) {
            tree.setSortDirection(comparator.getDirection());
            tree.setSortColumn(tree.getColumn(comparator.getPropertyIndex()));
        }
    }

    private void refreshTableViewer() {
        if (Widgets.isNullOrDisposedViewer(tableViewer)) {
            return;
        }

        List<Statement> statements;
        if (showFilter()) {
            statements = filteredStatements;
        }
        else {
            statements = allStatements;
        }

        Widgets.disposeIfExists(warningLimitIcon);
        Widgets.disposeIfExists(limitReachedLabel);

        table.setItemCount(statements.size());
        tableViewer.setInput(statements);

        if (showFilter() && (allStatements.size() != filteredStatements.size())) {
            statusLabel.setText(String.format("%s; Filtered: %s statements", statusText,
                    filteredStatements.size()));
        }
        else {
            statusLabel.setText(statusText);
        }
        if (comparator != null) {
            table.setSortDirection(comparator.getDirection());
            table.setSortColumn(table.getColumn(comparator.getPropertyIndex()));
        }
    }

    @SuppressWarnings("unchecked")
    private void updateFilter(FilterInput input) {
        if (!Widgets.isNullOrDisposed(subjectSelector)) {
            subjectSelector.setInput(input.getSubjects());
            if (hasStateParameter(PARAM_FILTER_SELECTED_SUBJECTS)) {
                List<RDFNode> subjects = (List<RDFNode>) getStateParameter(PARAM_FILTER_SELECTED_SUBJECTS);
                subjectSelector.setSelection(subjects);
            }
        }
        if (!Widgets.isNullOrDisposed(predicateSelector)) {
            predicateSelector.setInput(input.getPredicates());
            if (hasStateParameter(PARAM_FILTER_SELECTED_PREDICATES)) {
                List<RDFNode> predicates = (List<RDFNode>) getStateParameter(PARAM_FILTER_SELECTED_PREDICATES);
                predicateSelector.setSelection(predicates);
            }
        }
        if (!Widgets.isNullOrDisposed(objectSelector)) {
            objectSelector.setInput(input.getObjects());
            if (hasStateParameter(PARAM_FILTER_SELECTED_OBJECTS)) {
                List<RDFNode> objects = (List<RDFNode>) getStateParameter(PARAM_FILTER_SELECTED_OBJECTS);
                objectSelector.setSelection(objects);
            }
        }
    }

    private class TripleTableContentProvider extends LazyContentProvider {
        private Object input;

        @Override
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
            this.input = newInput;
        }

        @Override
        public void updateElement(int index) {
            Object child = ((List<?>) input).get(index);
            if (child instanceof Statement) {
                tableViewer.replace(child, index);
            }
        }
    }

    private class TriplesTreeContentProvider extends LazyTreeContentProvider {
        private List<Statement> getSubStatements(Statement statement) {
            List<Statement> subs = Lists.newArrayList();
            for (Statement sub : reifiedStatements.get(statement)) {
                if (!hideStatementPredicate) {
                    subs.add(sub);
                    continue;
                }
                if (sub.getPredicate().equals(RDF.subject)
                        || sub.getPredicate().equals(RDF.predicate)
                        || sub.getPredicate().equals(RDF.object)) {
                    continue;
                }
                subs.add(sub);
            }
            return subs;
        }

        @Override
        public void updateElement(Object parent, int index) {
            if (parent instanceof List<?>) {
                Object child = ((List<?>) parent).get(index);
                if (child instanceof Statement) {
                    Statement statement = (Statement) child;
                    treeViewer.replace(parent, index, statement);
                    int childCount = 0;
                    if (reifiedStatements.containsKey(statement)) {
                        childCount = getSubStatements(statement).size();
                    }
                    treeViewer.setChildCount(statement, childCount);

                    if ((topStatement != null) && statement.equals(topStatement)) {
                        treeViewer.setSelection(new StructuredSelection(statement));
                        treeViewer.expandToLevel(statement, 1);
                    }
                }
            }
            else if (parent instanceof Statement) {
                Statement statement = (Statement) parent;
                List<Statement> children = getSubStatements(statement);
                Statement child = children.get(index);
                treeViewer.replace(parent, index, child);
            }
        }
    }

    @Override
    public void createPartControl(Composite parent) {
        super.createPartControl(parent);

        modelVisibilityProvider = new ModelVisibilityMenuProvider();
        modelVisibilityProvider.setChangeListener(new ModelVisibilityListener() {

            @Override
            public void visibleModelsChanged(List<String> visibleUris) {
                setStateParameter(PARAM_VISIBLE_MODEL_URIS, visibleUris);
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
                Model subModel = getSubModel(uri);
                if (subModel != null) {
                    int count = subModel.listStatements().toList().size();
                    return String.format("%s (%d)", shortForm, count);
                }
                return uri;
            }
        });
        IActionBars actionBars = getViewSite().getActionBars();
        IMenuManager actionBarManager = actionBars.getMenuManager();
        actionBarManager.addMenuListener(modelVisibilityProvider);

        triplesLabelProvider = new TriplesLabelProvider();
        tableContentProvider = new TripleTableContentProvider();
        treeContentProvider = new TriplesTreeContentProvider();

        setInitialized(true);
    }

    private void createContextMenu() {
        MenuManager menuManager = new MenuManager();
        Menu menu = null;
        if (showReification()) {
            menu = menuManager.createContextMenu(tree);
            tree.setMenu(menu);
            getSite().registerContextMenu(menuManager, treeViewer);
        }
        else {
            menu = menuManager.createContextMenu(table);
            table.setMenu(menu);
            getSite().registerContextMenu(menuManager, tableViewer);
        }

    }

    private void createStatus() {
        Widgets.disposeIfExists(statusComposite);

        statusComposite = new Composite(container, SWT.LEFT);
        statusComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        GridLayout layout = new GridLayout(3, false);
        layout.marginWidth = 8;
        layout.marginHeight = 0;
        layout.marginTop = 1;
        layout.marginBottom = 4;
        statusComposite.setLayout(layout);

        statusLabel = new Label(statusComposite, SWT.NONE);
        statusLabel.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
    }

    protected void setLimit(int limit) {
        if (limit < 0) {
            limit = 0;
        }
        this.limit = limit;
        refreshViewer();
    }

    protected int getLimit() {
        return limit;
    }

    private class TriplesDragAdapter extends ViewerDragAdapter {
        private final StructuredViewer viewer;

        public TriplesDragAdapter(StructuredViewer viewer) {
            this.viewer = viewer;
        }

        @Override
        protected RDFNode getSelectedNode(int column) {
            return getSelectedResource(getSelection(), column);
        }

        @Override
        protected OntModel getOntModel() {
            return TriplesView.this.getOntModel();
        }

        @Override
        protected StructuredViewer getViewer() {
            return viewer;
        }

    }

    private void createTriplesTree() {
        Widgets.disposeIfExists(tree);
        Widgets.disposeIfExists(table);

        treeViewer = new TreeViewer(container, SWT.VIRTUAL | SWT.MULTI | SWT.FULL_SELECTION);
        tree = treeViewer.getTree();
        treeViewer.getControl().setLayoutData(
                new GridData(GridData.FILL, GridData.FILL, true, true, 1, 1));

        createTreeViewerColumn("Subject", 0, 270);
        createTreeViewerColumn("Predicate", 1, 270);
        createTreeViewerColumn("Object", 2, 320);
        int columnIndex = 3;
        for (Property predicate : getReifiedPredicates()) {
            LabelProvider labelProvider = getLabelProvider();
            String text = labelProvider.getText(predicate);
            if (Strings.isNullOrEmpty(text)) {
                text = predicate.toString();
            }
            TreeViewerColumn reifiedColum = createTreeViewerColumn(text, columnIndex, 320);
            reifiedColum.getColumn().setMoveable(true);
            columnIndex++;
        }

        treeViewer.setContentProvider(treeContentProvider);
        treeViewer.setLabelProvider(triplesLabelProvider);
        treeViewer.addDragSupport(DND.DROP_COPY | DND.DROP_MOVE, new Transfer[] { ResourceTransfer
                .getInstance() }, new TriplesDragAdapter(treeViewer));
        treeViewer.setUseHashlookup(true);
        treeViewer.getTree().addMouseListener(new MouseAdapter() {

            private int columnIndex = -1;

            @Override
            public void mouseDoubleClick(MouseEvent event) {
                Resource resource = getSelectedResource(treeViewer.getSelection(), columnIndex);
                if (resource != null) {
                    CorePlugin.getDefault().openResource(resource);
                }
            }

            @Override
            public void mouseDown(MouseEvent event) {
                Point pt = new Point(event.x, event.y);
                ViewerCell cell = treeViewer.getCell(pt);

                if (cell == null) {
                    return;
                }

                cell.getElement();
                columnIndex = cell.getColumnIndex();

                // if it's right button click
                if (event.button == 3) {
                    Resource resource = getSelectedResource(treeViewer.getSelection(), columnIndex);
                    SelectedURISourceProvider.create(getSite().getWorkbenchWindow())
                            .setSelectedURI(resource == null ? null : resource.getURI());

                }
            }
        });

        TriplesViewerToolTipSupport.enableFor(treeViewer, getModelProvider());

        selectionProvider.updateViewer(treeViewer);

        tree.setLinesVisible(true);
        tree.setHeaderVisible(true);
    }

    private void createTriplesTable() {
        Widgets.disposeIfExists(tree);
        Widgets.disposeIfExists(table);

        tableViewer = new TableViewer(container, SWT.VIRTUAL | SWT.MULTI | SWT.FULL_SELECTION);
        table = tableViewer.getTable();
        table.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 1));

        createTableViewerColumn("Subject", 0, 270);
        createTableViewerColumn("Predicate", 1, 270);
        createTableViewerColumn("Object", 2, 320);

        tableViewer.setContentProvider(tableContentProvider);
        tableViewer.setLabelProvider(triplesLabelProvider);
        tableViewer.addDragSupport(DND.DROP_COPY | DND.DROP_MOVE, new Transfer[] { ResourceTransfer
                .getInstance() }, new TriplesDragAdapter(tableViewer));
        tableViewer.setUseHashlookup(true);
        table.addMouseListener(new MouseAdapter() {

            private int columnIndex = -1;

            @Override
            public void mouseDoubleClick(MouseEvent event) {
                Resource resource = getSelectedResource(tableViewer.getSelection(), columnIndex);

                if (resource != null) {
                    CorePlugin.getDefault().openResource(resource);
                }
            }

            @Override
            public void mouseDown(MouseEvent event) {
                Point pt = new Point(event.x, event.y);
                ViewerCell cell = tableViewer.getCell(pt);
                if (cell == null) {
                    return;
                }

                columnIndex = cell.getColumnIndex();

                // if it's right button click
                if (event.button == 3) {
                    Resource resource = getSelectedResource(tableViewer.getSelection(), columnIndex);
                    SelectedURISourceProvider.create(getSite().getWorkbenchWindow())
                            .setSelectedURI(resource == null ? null : resource.getURI());
                }
            }
        });

        TriplesViewerToolTipSupport.enableFor(tableViewer, getModelProvider());

        selectionProvider.updateViewer(tableViewer);

        table.setLinesVisible(true);
        table.setHeaderVisible(true);
    }

    private Resource getSelectedResource(ISelection sel, int columnIndex) {
        IStructuredSelection selection = (IStructuredSelection) sel;
        if (columnIndex < 0) {
            return null;
        }
        if (!(selection.getFirstElement() instanceof Statement)) {
            return null;
        }
        Statement statement = (Statement) selection.getFirstElement();
        Resource resource = null;
        if (columnIndex == 0) {
            resource = statement.getSubject();
        }
        else if (columnIndex == 1) {
            resource = statement.getPredicate();
        }
        else if (columnIndex == 2) {
            if (statement.getObject().isResource()) {
                resource = statement.getResource();
            }
        }
        else {
            int reifiedColumnIndex = columnIndex - 3;
            List<Property> reifiedProperties = getReifiedPredicates();

            if (reifiedProperties.size() > reifiedColumnIndex) {
                Property p = reifiedProperties.get(reifiedColumnIndex);
                if (statement.isReified()) {

                    for (Statement stmt : reifiedStatements.get(statement)) {
                        if (stmt != null && stmt.getPredicate().equals(p)) {
                            if (stmt.getObject().isResource()) {
                                resource = stmt.getResource();
                            }
                        }
                    }

                    // Statement stmt = statement.getProperty(p);
                    //
                    // if (stmt != null && stmt.getObject().isResource()) {
                    // resource = stmt.getResource();
                    // }

                }
                // Statement reifiedStatement =
                // reifiedStatements.get(statement);
                // if (reifiedStatement != null) {
                //
                // }

            }

            // reifiedStatements.get(statement);
        }

        return resource;
    }

    private void createFilter() {
        Widgets.disposeIfExists(filterComposite);

        if (showFilter()) {
            filterComposite = new Composite(topSash, SWT.NONE);
            GridLayout layout = new GridLayout(1, false);

            filterComposite.setLayout(layout);
            filterComposite.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

            SashForm sashForm = new SashForm(filterComposite, SWT.HORIZONTAL);
            sashForm.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 1));
            sashForm.setSashWidth(8);

            FillLayout groupLayout = new FillLayout();
            groupLayout.marginHeight = 9;
            groupLayout.marginWidth = 8;

            Group subjectGroup = new Group(sashForm, SWT.NONE);
            subjectGroup.setText("Subject");
            subjectGroup.setLayout(groupLayout);

            Group predicateGroup = new Group(sashForm, SWT.NONE);
            predicateGroup.setText("Predicate");
            predicateGroup.setLayout(groupLayout);

            Group objectGroup = new Group(sashForm, SWT.NONE);
            objectGroup.setText("Object");
            objectGroup.setLayout(groupLayout);

            subjectSelector = new ResourceSelector(subjectGroup, SWT.NONE);
            predicateSelector = new ResourceSelector(predicateGroup, SWT.NONE);
            objectSelector = new ResourceSelector(objectGroup, SWT.NONE);

            // Add listeners which will store the selection to the viewstate
            subjectSelector.addFilterChangedListener(new IFilterChangedListener() {
                @Override
                public void filterChanged() {
                    List<RDFNode> subjects = subjectSelector.getSelectedNodes();
                    setStateParameter(PARAM_FILTER_SELECTED_SUBJECTS, subjects);
                }
            });
            predicateSelector.addFilterChangedListener(new IFilterChangedListener() {
                @Override
                public void filterChanged() {
                    List<RDFNode> predicates = predicateSelector.getSelectedNodes();
                    setStateParameter(PARAM_FILTER_SELECTED_PREDICATES, predicates);
                }
            });
            objectSelector.addFilterChangedListener(new IFilterChangedListener() {
                @Override
                public void filterChanged() {
                    List<RDFNode> objects = objectSelector.getSelectedNodes();
                    setStateParameter(PARAM_FILTER_SELECTED_OBJECTS, objects);
                }
            });

            Composite buttonComposite = new Composite(filterComposite, SWT.NONE);
            layout = new GridLayout(3, false);

            layout.marginHeight = 0;
            layout.marginWidth = 0;

            buttonComposite.setLayout(layout);
            buttonComposite
                    .setLayoutData(new GridData(SWT.RIGHT, SWT.BEGINNING, true, false, 2, 1));

            Link clearFilterButton = new Link(buttonComposite, SWT.PUSH);
            clearFilterButton.setText("<a>Clear</a>");
            clearFilterButton.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    subjectSelector.reset();
                    predicateSelector.reset();
                    objectSelector.reset();
                    executeApplyFilterJob();
                }
            });
            clearFilterButton.setLayoutData(new GridData(GridData.END, GridData.CENTER, false,
                    false, 1, 1));

            Link applyFilterButton = new Link(buttonComposite, SWT.PUSH);
            applyFilterButton.setText("<a>Apply</a>");
            applyFilterButton.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    executeApplyFilterJob();
                }
            });
            applyFilterButton.setLayoutData(new GridData(GridData.END, GridData.CENTER, false,
                    false, 1, 1));
        }
    }

    private class ApplyFilterJob extends JobWithMonitor {

        public ApplyFilterJob() {
            super("Applying Filter to Triples");
        }

        @Override
        protected IStatus run(IProgressMonitor monitor) {
            try {
                startMonitorUpdate(monitor, "Applying filter to triples", 1);
                // Are no triples to be shown?
                if (subjectSelector.isNoneSelected() || predicateSelector.isNoneSelected()
                        || objectSelector.isNoneSelected()) {
                    synchronized (mutex) {
                        filteredStatements.clear();
                    }
                }
                // Are all triples to be shown?
                else if (subjectSelector.isAllSelected() && predicateSelector.isAllSelected()
                        && objectSelector.isAllSelected()) {
                    synchronized (mutex) {
                        filteredStatements.clear();
                        filteredStatements.addAll(allStatements);
                    }
                }
                // What subset of triples is to be shown? Apply a filter.
                else {
                    final List<RDFNode> selectedSubjects = subjectSelector.getSelectedNodes();
                    final List<RDFNode> selectedPredicates = predicateSelector.getSelectedNodes();
                    final List<RDFNode> selectedObjects = objectSelector.getSelectedNodes();
                    List<Statement> result = Lists.newArrayListWithExpectedSize(allStatements
                            .size());
                    if (subjectSelector.isAllSelected()) {
                        if (predicateSelector.isAllSelected()) {
                            // Filter only on object
                            for (Statement stmt : allStatements) {
                                if (!selectedObjects.contains(stmt.getObject())) {
                                    continue;
                                }
                                result.add(stmt);
                            }
                        }
                        else if (objectSelector.isAllSelected()) {
                            // Filter only on predicate
                            for (Statement stmt : allStatements) {
                                if (!selectedPredicates.contains(stmt.getPredicate())) {
                                    continue;
                                }
                                result.add(stmt);
                            }
                        }
                        else {
                            // Filter on predicate and object
                            for (Statement stmt : allStatements) {
                                if (!selectedPredicates.contains(stmt.getPredicate())) {
                                    continue;
                                }
                                if (!selectedObjects.contains(stmt.getObject())) {
                                    continue;
                                }
                                result.add(stmt);
                            }
                        }
                    }
                    else if (predicateSelector.isAllSelected()) {
                        if (objectSelector.isAllSelected()) {
                            // Filter only on subject
                            for (Statement stmt : allStatements) {
                                if (!selectedSubjects.contains(stmt.getSubject())) {
                                    continue;
                                }
                                result.add(stmt);
                            }
                        }
                        else {
                            // Filter on subject and object
                            for (Statement stmt : allStatements) {
                                if (!selectedSubjects.contains(stmt.getSubject())) {
                                    continue;
                                }
                                if (!selectedObjects.contains(stmt.getObject())) {
                                    continue;
                                }
                                result.add(stmt);
                            }
                        }
                    }
                    else {
                        // Filter on all three elements
                        result = Lists.newArrayListWithExpectedSize(allStatements.size());
                        for (Statement stmt : allStatements) {
                            if (!selectedSubjects.contains(stmt.getSubject())) {
                                continue;
                            }
                            if (!selectedPredicates.contains(stmt.getPredicate())) {
                                continue;
                            }
                            if (!selectedObjects.contains(stmt.getObject())) {
                                continue;
                            }
                            result.add(stmt);
                        }
                    }
                    synchronized (mutex) {
                        filteredStatements.clear();
                        filteredStatements.addAll(Lists.newArrayList(result));
                    }
                }
                monitor.worked(1);
            }
            finally {
                stopMonitorUpdate();
                monitor.done();
            }
            return Status.OK_STATUS;
        }
    }

    private class FindFilterObjectsJob extends JobWithMonitor {
        private final FilterInput localResult;
        private final OntModel ontModel;

        public FindFilterObjectsJob(IModelProvider provider) {
            super("Finding Filter Objects");
            LabelProvider labelProvider = provider.getLabelProvider();
            this.localResult = new FilterInput(labelProvider);
            this.ontModel = provider.getOntModel();
        }

        private void scanModel(Model model) {
            Var subjectVariable = Var.alloc("subject");
            Var predicateVariable = Var.alloc("predicate");
            Var objectVariable = Var.alloc("object");

            // Subjects:
            Triple triple = Triple.create(subjectVariable, predicateVariable, objectVariable);
            QueryBuilder builder = QueryBuilder.createSelect(true).addTriplePattern(triple)
                    .addResultVars(subjectVariable, predicateVariable, objectVariable);

            for (ResultSet iter = builder.execSelect(model); iter.hasNext();) {
                QuerySolution solution = iter.next();
                Resource resource = solution.getResource(subjectVariable.getName());
                Resource predicate = solution.getResource(predicateVariable.getName());
                RDFNode object = solution.get(objectVariable.getName());

                localResult.addSubject(JenaUtil.asOntResource(resource, ontModel));
                localResult.addPredicate(JenaUtil.asProperty(predicate, ontModel));
                localResult.addObject(object);
            }
        }

        @Override
        protected IStatus run(IProgressMonitor monitor) {

            try {
                startMonitorUpdate(monitor, "Searching for filter objects", 1);

                if (showBaseModel()) {
                    scanModel(ontModel.getBaseModel());
                }
                List<String> visibleModelUris = getVisibleModelURIs();
                if (visibleModelUris.size() > 0) {
                    for (String uri : visibleModelUris) {
                        Model subModel = getSubModel(uri);
                        String shortForm = ontModel.shortForm(uri);
                        updateSubTask(String.format("Retrieving filter input from '%s'...",
                                shortForm));

                        scanModel(subModel);
                    }
                }
                Display.getDefault().syncExec(new Runnable() {

                    @Override
                    public void run() {
                        updateFilter(localResult);
                    }
                });

                monitor.worked(1);
            }
            finally {
                stopMonitorUpdate();
                monitor.done();
            }
            return Status.OK_STATUS;
        }
    }

    private TreeViewerColumn createTreeViewerColumn(String title, final int index, int bound) {
        final TreeViewerColumn viewerColumn = new TreeViewerColumn(treeViewer, SWT.NONE);
        final TreeColumn column = viewerColumn.getColumn();
        column.setText(title);
        column.setWidth(bound);
        column.setResizable(true);
        column.setMoveable(false);
        column.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (comparator == null) {
                    comparator = new TriplesViewerComparator(index);
                }
                else {
                    comparator.setColumn(index);
                }
                setStateParameter(PARAM_SORT_COLUMN, new Integer(index));
                setStateParameter(PARAM_SORT_DIRECTION, new Integer(comparator.getDirection()));
                sortStatements();
                refreshViewer();
            }
        });
        return viewerColumn;
    }

    private TableViewerColumn createTableViewerColumn(String title, final int index, int bound) {
        final TableViewerColumn viewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
        final TableColumn column = viewerColumn.getColumn();
        column.setText(title);
        column.setWidth(bound);
        column.setResizable(true);
        column.setMoveable(false);
        column.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (comparator == null) {
                    comparator = new TriplesViewerComparator(index);
                }
                else {
                    comparator.setColumn(index);
                }
                setStateParameter(PARAM_SORT_COLUMN, new Integer(index));
                setStateParameter(PARAM_SORT_DIRECTION, new Integer(comparator.getDirection()));
                sortStatements();
                refreshViewer();
            }
        });
        return viewerColumn;
    }

    @Override
    public void setFocus() {
        if (showReification() && !Widgets.isNullOrDisposed(tree)) {
            tree.setFocus();
        }
        else if (!Widgets.isNullOrDisposed(table)) {
            table.setFocus();
        }
    }

    protected Statement getSelectedStatement() {
        if (showReification()) {
            if (treeViewer != null && treeViewer.getSelection() != null
                    && treeViewer.getSelection() instanceof IStructuredSelection) {
                IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();
                if (selection.size() == 1) {
                    return (Statement) selection.getFirstElement();
                }
            }
        }
        else {
            if (tableViewer != null && tableViewer.getSelection() != null
                    && tableViewer.getSelection() instanceof IStructuredSelection) {
                IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();
                if (selection.size() == 1) {
                    return (Statement) selection.getFirstElement();
                }
            }
        }
        return null;
    }

    protected void changePredicateProperties() {
        ResourceSelectionDialog dialog = new ResourceSelectionDialog(getSite().getShell(),
                "Properties",
                "Select the properties which you would like show for reified statements.",
                ResourceSelectionDialog.CHECKBOXES);

        OntModel model = getOntModel();

        dialog.clearAll();
        dialog.setModel(model);
        dialog.setHierarchicalProperties(Lists.newArrayList(RDFS.subClassOf, RDF.type,
                RDFS.subPropertyOf));
        dialog.setAllowedResourceTypes(new Resource[] { RDF.Property });
        dialog.setHierarchicalViewDisabled(true);
        dialog.setMultiSelectAllowed(true);

        List<Resource> reifiedPredicatesAndSupersInUse = Lists.newArrayList();
        Var varReifiedStatement = Var.alloc("reifiedStatement");
        Var varProperty = Var.alloc("property");
        Var varSelfOrSuperProperty = Var.alloc("selfOrSuperProperty");
        Var varObject = Var.alloc("object");
        QueryBuilder qb = QueryBuilder.createSelect(true);
        qb.addTriplePattern(varReifiedStatement, RDF.type, RDF.Statement);
        qb.addTriplePattern(varReifiedStatement, varProperty, varObject);
        qb.addTriplePattern(varProperty, PathUtil.subPropertyOfAny, varSelfOrSuperProperty);
        qb.addResultVar(varSelfOrSuperProperty);
        for (ResultSet iter = qb.execSelect(model); iter.hasNext();) {
            Resource resource = iter.next().getResource(varSelfOrSuperProperty.getName());
            reifiedPredicatesAndSupersInUse.add(resource);
        }
        // Subject, predicate, object, and type properties are unnecessary.
        reifiedPredicatesAndSupersInUse.remove(RDF.subject);
        reifiedPredicatesAndSupersInUse.remove(RDF.predicate);
        reifiedPredicatesAndSupersInUse.remove(RDF.object);
        reifiedPredicatesAndSupersInUse.remove(RDF.type);

        // If no properties can be selected, best to not show the
        // ResourceSelectionDialog at all.
        if (reifiedPredicatesAndSupersInUse.isEmpty()) {
            MessageDialog
                    .openConfirm(
                            getSite().getShell(),
                            "There are no properties to be selected.",
                            "None of the reified statements in the model act as subject in another statement. As such, there are no reified properties that can be selected at present.");
            List<Property> emptyPredicateList = Lists.newArrayList();
            setReifiedPredicates(emptyPredicateList);
            refreshViewer();
            return;
        }

        dialog.setResources(reifiedPredicatesAndSupersInUse);

        List<Property> reifiedPredicates = getReifiedPredicates();
        List<Resource> selectedResources = Lists.transform(reifiedPredicates,
                new Function<Property, Resource>() {
                    @Override
                    public Resource apply(Property property) {
                        return property.asResource();
                    }
                });
        dialog.setSelectedResources(selectedResources);

        if (dialog.open() == Window.OK) {
            reifiedPredicates.clear();
            for (Resource property : dialog.getSelectedResources()) {
                System.out.println("" + property.getURI());
                reifiedPredicates.add(property.as(Property.class));
            }
            setReifiedPredicates(reifiedPredicates);
            refreshViewer();
        }

        sortStatements();
    }

    public static class RefreshViewHandler extends AbstractHandler {
        public static final String ID = "com.semmtech.plugin.semmweb.editor.commands.refreshTriplesView";

        @Override
        public Object execute(ExecutionEvent event) throws ExecutionException {
            if (singleton != null && singleton.isInitialized()) {
                singleton.refreshFromModel();
            }
            return null;
        }
    }

    public static class OpenSubjectHandler extends AbstractHandler {
        public static final String ID = "com.semmtech.plugin.semmweb.editor.commands.triples.openSubject";

        @Override
        public Object execute(ExecutionEvent event) throws ExecutionException {
            if (singleton != null && singleton.isInitialized()) {
                singleton.openSubject();
            }
            return null;
        }
    }

    public static class OpenPredicateHandler extends AbstractHandler {
        public static final String ID = "com.semmtech.plugin.semmweb.editor.commands.triples.openPredicate";

        @Override
        public Object execute(ExecutionEvent event) throws ExecutionException {
            if (singleton != null && singleton.isInitialized()) {
                singleton.openPredicate();
            }
            return null;
        }
    }

    public static class OpenObjectHandler extends AbstractHandler {
        public static final String ID = "com.semmtech.plugin.semmweb.editor.commands.triples.openObject";

        @Override
        public Object execute(ExecutionEvent event) throws ExecutionException {
            if (singleton != null && singleton.isInitialized()) {
                singleton.openObject();
            }
            return null;
        }
    }

    public static class ChangeStatementLimitHandler extends AbstractHandler {
        public static final String ID = "com.semmtech.plugin.semmweb.editor.commands.triples.changeStatementLimit";

        public static final String PARAMETER_LIMIT_SIZE = "limitSize";
        public static final String PARAMETER_ENTER_CUSTOM = "enterCustom";

        @Override
        public Object execute(ExecutionEvent event) throws ExecutionException {
            if (singleton != null && singleton.isInitialized()) {
                boolean custom = Boolean.parseBoolean(event.getParameter(PARAMETER_ENTER_CUSTOM));
                if (custom) {
                    Shell shell = HandlerUtil.getActiveShell(event);
                    int limit = singleton.getLimit();
                    InputDialog dialog = new InputDialog(shell, "Enter Limit",
                            "Enter the maximum number of statements shown in the table.",
                            Integer.toString(limit), null);
                    if (dialog.open() == Window.OK) {
                        String value = dialog.getValue();
                        try {
                            int size = Integer.valueOf(value);
                            if (size > 10000) {
                                if (MessageDialog
                                        .openConfirm(
                                                shell,
                                                "Limit Size",
                                                "The specified value may cause the table to require a large amount of rendering, are you sure you want to continue?")) {
                                    singleton.setLimit(size);
                                }
                            }
                            else {
                                singleton.setLimit(size);
                            }
                        }
                        catch (NumberFormatException ex) {
                            logger.debug("NumberFormatException setting TriplesView limit: "
                                    + ex.getMessage());
                        }
                    }
                }
                else {
                    singleton.setLimit(Integer.parseInt(event.getParameter(PARAMETER_LIMIT_SIZE)));
                }
            }
            return null;
        }
    }

    public static class ChangePredicatePropertiesHandler extends AbstractHandler {
        public static final String ID = "com.semmtech.plugin.semmweb.editor.commands.triples.changePredicateProperties";

        @Override
        public Object execute(ExecutionEvent event) throws ExecutionException {
            if (singleton != null && singleton.isInitialized()) {
                singleton.changePredicateProperties();
                singleton.refreshFromModel();
            }
            return null;
        }
    }

    public static class ToggleShowBaseModelHandler extends AbstractHandler implements
            IElementUpdater {
        public static final String ID = "com.semmtech.plugin.semmweb.editor.commands.triples.toggleShowBaseModel";

        @Override
        public Object execute(ExecutionEvent event) throws ExecutionException {
            if (singleton != null) {
                singleton.toggleShowBaseModel();
            }
            return null;
        }

        @Override
        public void updateElement(UIElement element, @SuppressWarnings("rawtypes") Map parameters) {
            if (singleton != null) {
                element.setChecked(singleton.showBaseModel());
            }
        }
    }

    public static class ToggleShowReificationHandler extends AbstractHandler implements
            IElementUpdater {
        public static final String ID = "com.semmtech.plugin.semmweb.editor.commands.toggleShowReification";

        @Override
        public Object execute(ExecutionEvent event) throws ExecutionException {
            if (singleton != null) {
                singleton.toggleReification();
                Commands.setToggleState(ID, singleton.showReification());
            }
            return null;
        }

        @Override
        public void updateElement(UIElement element, @SuppressWarnings("rawtypes") Map parameters) {
            if (singleton != null) {
                boolean showReification = singleton.showReification();
                element.setChecked(showReification);
                Commands.setToggleState(ID, showReification);
            }
        }
    }

    public static class ToggleSortByReificationHandler extends AbstractHandler implements
            IElementUpdater {

        public static final String ID = "com.semmtech.plugin.semmweb.editor.commands.sortByReification";

        @Override
        public Object execute(ExecutionEvent event) throws ExecutionException {
            if (singleton != null) {
                singleton.toggleSortByReification();
                Commands.setToggleState(ID, singleton.sortByReification());
            }
            return null;
        }

        @Override
        public void updateElement(UIElement element, @SuppressWarnings("rawtypes") Map parameters) {
            if (singleton != null) {
                boolean sortByReification = singleton.sortByReification();
                element.setChecked(sortByReification);
                Commands.setToggleState(ID, sortByReification);
            }
        }

    }

    public static class ToggleShowTriplesFilterHandler extends AbstractHandler implements
            IElementUpdater {
        public static final String ID = "com.semmtech.plugin.semmweb.editor.commands.toggleShowTriplesFilter";

        @Override
        public Object execute(ExecutionEvent event) throws ExecutionException {
            if (singleton != null) {
                singleton.toggleFilter();
            }
            return null;
        }

        @Override
        public void updateElement(UIElement element, @SuppressWarnings("rawtypes") Map parameters) {
            if (singleton != null) {
                element.setChecked(singleton.showFilter());
            }
        }
    }

    public static class CopyURIHandler extends AbstractHandler {
        @Override
        public Object execute(ExecutionEvent event) throws ExecutionException {
            if (singleton != null) {
                String uri = SelectedURISourceProvider.create(
                        singleton.getSite().getWorkbenchWindow()).getSelectedURI();

                Display display = EclipseUIPlugin.getStandardDisplay();
                Clipboard clipboard = new Clipboard(display);
                clipboard.setContents(new Object[] { uri },
                        new Transfer[] { TextTransfer.getInstance() });
                clipboard.dispose();
            }
            return null;
        }
    }

    public static class ShowDerivationHandler extends AbstractHandler {
        @Override
        public Object execute(ExecutionEvent event) throws ExecutionException {
            if (singleton != null) {
                Statement selected = singleton.getSelectedStatement();
                IModelProvider provider = singleton.getModelProvider();
                if (selected != null && provider != null) {
                    Iterator<Derivation> derivations = provider.getDerivation(selected);
                    if (derivations != null) {
                        String text = null;
                        while (derivations.hasNext()) {
                            StringWriter sw = new StringWriter();
                            PrintWriter pw = new PrintWriter(sw);
                            derivations.next().printTrace(pw, true);
                            String derivationText = sw.getBuffer().toString();
                            if (text == null) {
                                text = derivationText;
                            }
                            else {
                                text += "\n\n" + derivationText;
                            }
                        }
                        if (text != null) {
                            Shell shell = singleton.getSite().getShell();
                            MessageDialog.openInformation(shell, "Derivation", text);
                        }
                    }
                }
            }
            return null;
        }
    }

    private class RefreshJob extends JobWithMonitor {
        private final IModelProvider provider;
        private final OntModel ontModel;

        public RefreshJob(String name, IModelProvider provider) {
            super(name);
            this.provider = provider;
            this.ontModel = provider.getOntModel();
        }

        @Override
        protected IStatus run(final IProgressMonitor monitor) {
            String labelText;
            synchronized (mutex) {

                try {
                    startMonitorUpdate(monitor, "Collecting Statements", 6);

                    List<Statement> filtered = Lists.newArrayList();
                    if (provider != null && ontModel != null) {
                        updateSubTask("Counting statements...");
                        Model inferredModel = provider
                                .getSubModel(IModelProvider.INFERRED_SUBMODEL_URI);

                        int baseCount = ontModel.getBaseModel().listStatements().toList().size();
                        int modelCount = ontModel.listStatements().toList().size(); // includes
                                                                                    // inferred
                        int inferredCount = 0;
                        if (inferredModel != null) {
                            inferredCount = inferredModel.listStatements().toList().size();
                        }

                        int baseReifiedCount = 0;
                        int modelReifiedCount = 0; // includes inferred
                        for (ReifiedStatement reif : ontModel.getBaseModel()
                                .listReifiedStatements().toList()) {
                            baseReifiedCount++;
                            baseCount += reif.listProperties().toList().size();
                        }
                        for (ReifiedStatement reif : ontModel.listReifiedStatements().toList()) {
                            modelReifiedCount++;
                            modelCount += reif.listProperties().toList().size();
                        }
                        if (inferredModel != null) {
                            for (ReifiedStatement reif : inferredModel.listReifiedStatements()
                                    .toList()) {
                                inferredCount += reif.listProperties().toList().size();
                            }
                        }

                        int importCount = 0;
                        for (String subModelURI : provider.getSubModelURIs()) {
                            if (!subModelURI.equals(IModelProvider.INFERRED_SUBMODEL_URI)) {
                                Model subModel = provider.getSubModel(subModelURI);
                                if (subModel != null) {
                                    importCount += subModel.size();
                                    for (ReifiedStatement reif : subModel.listReifiedStatements()
                                            .toList()) {
                                        importCount += reif.listProperties().toList().size();
                                    }
                                }
                            }
                        }

                        String text = String
                                .format("Base model: %d statements%s; Model: %d statements (%s%d imported%s)",
                                        baseCount, (baseReifiedCount > 0 ? " (" + baseReifiedCount
                                                + " reified)" : ""), modelCount,
                                        (modelReifiedCount > 0 ? "" + modelReifiedCount
                                                + " reified, " : ""), importCount,
                                        (inferredCount > 0 ? ", " + inferredCount + " inferred"
                                                : ""));
                        labelText = text;
                        monitor.worked(2);

                        int expectedSize = 0;
                        if (showBaseModel()) {
                            expectedSize += baseCount;
                        }
                        filtered = new ArrayList<>(expectedSize);
                        monitor.worked(1);

                        if (monitor.isCanceled()) {
                            return Status.CANCEL_STATUS;
                        }

                        int count = 0;
                        updateSubTask(String.format("Adding %s statements...", expectedSize));
                        if (showBaseModel()) {
                            ExtendedIterator<Statement> iter = ontModel.getBaseModel()
                                    .listStatements().filterKeep(filter);
                            while (iter.hasNext()) {
                                filtered.add(iter.next());
                                updateSubTask(String.format("Adding %s statements... %s added",
                                        expectedSize, ++count));
                                if (monitor.isCanceled()) {
                                    return Status.CANCEL_STATUS;
                                }
                            }
                            // Reified Statements from basemodel
                            // for (ReifiedStatement reif :
                            // ontModel.getBaseModel()
                            // .listReifiedStatements().toList()) {
                            // ExtendedIterator<Statement> reifiedIter =
                            // reif.listProperties()
                            // .filterKeep(filter);
                            // while (reifiedIter.hasNext()) {
                            // Statement stmt = reifiedIter.next();
                            // filtered.add(stmt);
                            // updateSubTask(String.format("Adding %s statements... %s added",
                            // expectedSize, ++count));
                            // if (monitor.isCanceled()) {
                            // return Status.CANCEL_STATUS;
                            // }
                            // }
                            // }
                        }
                        List<String> visibleModelUris = getVisibleModelURIs();
                        if (visibleModelUris.size() > 0) {
                            for (String uri : visibleModelUris) {
                                Model subModel = getSubModel(uri);
                                if (subModel != null) {
                                    ExtendedIterator<Statement> iter = subModel.listStatements()
                                            .filterKeep(filter);
                                    while (iter.hasNext()) {
                                        Statement statement = iter.next();
                                        filtered.add(statement);
                                        updateSubTask(String.format(
                                                "Adding %s statements... %s added", expectedSize,
                                                ++count));
                                        if (monitor.isCanceled()) {
                                            return Status.CANCEL_STATUS;
                                        }
                                    }
                                }
                            }
                        }
                        monitor.worked(1);
                        if (monitor.isCanceled()) {
                            return Status.CANCEL_STATUS;
                        }

                        // FIXME: Refactor the two loops into a single method...
                        if (showReification()) {
                            updateSubTask("Adding reified statements...");
                            if (showBaseModel()) {
                                for (ReifiedStatement reified : ontModel.getBaseModel()
                                        .listReifiedStatements().toList()) {
                                    if (monitor.isCanceled()) {
                                        return Status.CANCEL_STATUS;
                                    }
                                    Statement statement = reified.getStatement();
                                    if (!filtered.contains(statement)) {
                                        filtered.add(statement);
                                    }
                                    // Get all triples in which the reified
                                    // statement is
                                    // the subject
                                    for (Statement hide : reified.listProperties().toSet()) {
                                        reifiedStatements.put(statement, hide);
                                        while (filtered.contains(hide)) {
                                            filtered.remove(hide);
                                        }
                                    }
                                    // Get all triples in which the reified
                                    // statement is
                                    // the object
                                    for (Statement hide : ontModel.listStatements(null, null,
                                            reified).toList()) {
                                        reifiedStatements.put(statement, hide);
                                        while (filtered.contains(hide)) {
                                            filtered.remove(hide);
                                        }
                                    }
                                }
                            }
                            if (visibleModelUris.size() > 0) {
                                for (String uri : visibleModelUris) {
                                    Model subModel = getSubModel(uri);
                                    for (ReifiedStatement reified : subModel
                                            .listReifiedStatements().toList()) {
                                        if (monitor.isCanceled()) {
                                            return Status.CANCEL_STATUS;
                                        }
                                        Statement statement = reified.getStatement();
                                        if (!filtered.contains(statement)) {
                                            filtered.add(statement);
                                        }
                                        // Get all triples in which the reified
                                        // statement is
                                        // the subject
                                        for (Statement hide : reified.listProperties().toSet()) {
                                            reifiedStatements.put(statement, hide);
                                            while (filtered.contains(hide)) {
                                                filtered.remove(hide);
                                            }
                                        }
                                        // Get all triples in which the reified
                                        // statement is
                                        // the object
                                        for (Statement hide : ontModel.listStatements(null, null,
                                                reified).toList()) {
                                            reifiedStatements.put(statement, hide);
                                            while (filtered.contains(hide)) {
                                                filtered.remove(hide);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        monitor.worked(1);
                        if (monitor.isCanceled()) {
                            return Status.CANCEL_STATUS;
                        }
                        if (comparator != null) {
                            updateSubTask(String.format("Sorting %s triples...", filtered.size()));
                            Collections.sort(filtered, comparator.getInnerComparator());
                        }
                        monitor.worked(1);
                    }
                    else {
                        labelText = "No model opened";
                    }
                    synchronized (allStatements) {
                        allStatements.clear();
                        allStatements.addAll(filtered);
                    }
                    statusText = labelText;
                }
                catch (ClosedException ex) {
                    monitor.setCanceled(true);
                    logger.warn("The model has been closed during the Triples View build query execution!");
                }
                catch (Exception ex) {
                    logger.warn(
                            "During refreshing PropertiesView an exception was thrown: "
                                    + ex.getMessage(), ex);
                }
                finally {
                    stopMonitorUpdate();
                    monitor.done();

                    if (monitor.isCanceled()) {
                        return Status.CANCEL_STATUS;
                    }
                }
            }
            return Status.OK_STATUS;
        }
    }

    class TriplesViewerComparator extends ViewerComparator {
        private int propertyIndex;
        private static final int DESCENDING = SWT.DOWN;
        private static final int ASCENDING = SWT.UP;
        private boolean sortByReification;

        private int direction = DESCENDING;
        private Comparator<Statement> inner;

        public TriplesViewerComparator(int column) {
            this.propertyIndex = column;
            this.direction = ASCENDING;
            this.sortByReification = false;

            IModelProvider provider = CorePlugin.getDefault().getActiveModelProvider();
            Preconditions.checkNotNull(provider);

            inner = new Comparator<Statement>() {
                @Override
                public int compare(Statement s1, Statement s2) {
                    return compareStatements(s1, s2);
                }
            };
        }

        public Comparator<Statement> getInnerComparator() {
            return inner;
        }

        public int getDirection() {
            return direction;
        }

        public int getPropertyIndex() {
            return propertyIndex;
        }

        public void setColumn(int column, int direction) {
            this.propertyIndex = column;
            this.direction = direction;
        }

        public void setColumn(int column) {
            if (column == this.propertyIndex) {
                // Same column as last sort; toggle the direction
                direction = (direction == ASCENDING) ? DESCENDING : ASCENDING;
            }
            else {
                // New column; do an ascending sort
                this.propertyIndex = column;
                this.direction = ASCENDING;
            }
        }

        protected int compareStatements(Statement s1, Statement s2) {
            if (sortByReification) {
                if (reifiedStatements.containsKey(s1) && !reifiedStatements.containsKey(s2)) {
                    return -1;
                }
                else if (reifiedStatements.containsKey(s2) && !reifiedStatements.containsKey(s1)) {
                    return 1;
                }
            }

            StyledString styledString1 = triplesLabelProvider.getElementText(s1, propertyIndex);
            StyledString styledString2 = triplesLabelProvider.getElementText(s2, propertyIndex);
            String name1 = (styledString1 != null) ? styledString1.getString() : "";
            String name2 = (styledString2 != null) ? styledString2.getString() : "";

            // For special situations where either name is null or empty
            if (Strings.isNullOrEmpty(name1)) {
                if (Strings.isNullOrEmpty(name2)) {
                    return 0;
                }
                return 1;
            }
            if (Strings.isNullOrEmpty(name2)) {
                return -1;
            }

            int rc = name1.compareToIgnoreCase(name2);

            // If descending order, flip the direction
            if (direction == DESCENDING) {
                rc = -rc;
            }
            return rc;
        }

        @Override
        public int compare(Viewer viewer, Object e1, Object e2) {
            Statement s1 = (Statement) e1;
            Statement s2 = (Statement) e2;
            return compareStatements(s1, s2);
        }

        public void setSortByReification(boolean sortByReification) {
            this.sortByReification = sortByReification;
        }
    }

    public void openSubject() {
        Statement selected = getSelectedStatement();
        if (selected != null) {
            Resource subject = selected.getSubject();
            openResource(subject);
        }
    }

    private boolean showReification() {
        if (hasStateParameter(PARAM_SHOW_REIFICATION)) {
            return ((Boolean) getStateParameter(PARAM_SHOW_REIFICATION)).booleanValue();
        }
        return false;
    }

    private boolean sortByReification() {
        if (hasStateParameter(PARAM_SORT_BY_REIFICATION)) {
            return ((Boolean) getStateParameter(PARAM_SORT_BY_REIFICATION)).booleanValue();
        }
        return false;
    }

    public void toggleReification() {
        boolean newValue = !showReification();
        setStateParameter(PARAM_SHOW_REIFICATION, new Boolean(newValue));
        refreshFromModel();

        ReificationStateSourceProvider
                .setReificationState(getSite().getWorkbenchWindow(), newValue);
    }

    public void toggleSortByReification() {
        boolean newValue = !sortByReification();
        setStateParameter(PARAM_SORT_BY_REIFICATION, new Boolean(newValue));

        // if the sorting has not been applied i sort by default the first
        // column
        if (comparator == null) {
            comparator = new TriplesViewerComparator(0);
        }
        comparator.setColumn(0, TriplesViewerComparator.ASCENDING);

        sortStatements();
        refreshFromModel();

    }

    public void toggleShowBaseModel() {
        boolean newValue = !showBaseModel();
        setStateParameter(PARAM_SHOW_BASE_MODEL, new Boolean(newValue));
        refreshFromModel();
    }

    private boolean showBaseModel() {
        if (hasStateParameter(PARAM_SHOW_BASE_MODEL)) {
            return ((Boolean) getStateParameter(PARAM_SHOW_BASE_MODEL)).booleanValue();
        }
        return true;
    }

    public void toggleFilter() {
        boolean newValue = !showFilter();
        setStateParameter(PARAM_SHOW_FILTER, new Boolean(newValue));
        filteredStatements.clear();
        filteredStatements.addAll(allStatements);

        createControls();
        refreshViewer();

        if (showFilter()) {
            executeRefreshFilterJob();
        }
    }

    private boolean showFilter() {
        if (hasStateParameter(PARAM_SHOW_FILTER)) {
            return ((Boolean) getStateParameter(PARAM_SHOW_FILTER)).booleanValue();
        }
        return false;
    }

    public void openObject() {
        Statement selected = getSelectedStatement();
        if (selected != null && selected.getObject().isResource()) {
            Resource object = selected.getObject().asResource();
            openResource(object);
        }
    }

    public void openPredicate() {
        Statement selected = getSelectedStatement();
        if (selected != null) {
            Resource property = selected.getPredicate();
            openResource(property);
        }
    }

    @Override
    protected void cleanup() {
        allStatements.clear();
        filteredStatements.clear();
        reifiedStatements.clear();

        refreshJob = null;
        refreshFilterJob = null;
        applyFilterJob = null;
    }
}
