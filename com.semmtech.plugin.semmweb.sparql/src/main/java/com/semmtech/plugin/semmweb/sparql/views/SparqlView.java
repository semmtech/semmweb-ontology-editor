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

package com.semmtech.plugin.semmweb.sparql.views;


import java.util.List;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.Token;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.handlers.RadioState;
import org.eclipse.ui.themes.ITheme;
import org.eclipse.ui.themes.IThemeManager;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.semmtech.grammars.antlr.sparql.SparqlLexer;
import com.semmtech.grammars.antlr.sparql.SparqlParser;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.dnd.ResourceTransfer;
import com.semmtech.plugin.semmweb.core.model.IModelProvider;
import com.semmtech.plugin.semmweb.core.model.events.ModelActivatedEvent;
import com.semmtech.plugin.semmweb.core.model.events.ModelChangedEvent;
import com.semmtech.plugin.semmweb.core.model.events.NamespacePrefixChangedEvent;
import com.semmtech.plugin.semmweb.core.model.events.SubModelAddedEvent;
import com.semmtech.plugin.semmweb.core.model.events.SubModelRemovedEvent;
import com.semmtech.plugin.semmweb.core.viewers.ModelNodeLabelProvider;
import com.semmtech.plugin.semmweb.core.views.AbstractModelListenerView;
import com.semmtech.plugin.semmweb.sparql.SparqlPlugin;
import com.semmtech.plugin.semmweb.sparql.markup.SparqlRegion;
import com.semmtech.plugin.semmweb.sparql.markup.SparqlRegionAnalyzer;
import com.semmtech.ui.plugin.viewers.TableLabelProvider;


/**
 * TODO: Undo and Redo operation for StyledText:
 * http://sourceforge.net/p/etinyplugins
 * /blog/2013/02/add-undoredo-support-to-your-swt-styledtext-s/
 * 
 * @author Mike Henrichs
 * 
 */
public class SparqlView extends AbstractModelListenerView implements IPropertyChangeListener {
    public static final String ID = "com.semmtech.plugin.semmweb.sparql.views.sparql"; //$NON-NLS-1$

    private static SparqlView singleton;
    private Composite parent;
    private String querystring = "SELECT *\nWHERE {\n\t?s ?p ?o .\n}\nLIMIT 1000\n"; //$NON-NLS-1$
    private List<QuerySolution> solutions;
    private List<String> variableNames;

    private static String target = null;

    private StyledText queryText;
    private Composite resultComposite;
    private Table table;
    private TableViewer viewer;
    private Label resultLabel;

    private TableLabelProvider labelProvider;
    private SparqlResultViewerComparator comparator;

    public SparqlView() {
        singleton = this;
    }

    @Override
    public void init(IViewSite site) throws PartInitException {
        super.init(site);
        SparqlPlugin.getDefault().getPreferenceStore().addPropertyChangeListener(this);
    }

    @Override
    public void dispose() {
        SparqlPlugin.getDefault().getPreferenceStore().removePropertyChangeListener(this);
        super.dispose();
    }

    @Override
    protected void cleanup() {

    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        if (event.getProperty().equals(SparqlViewLayoutHandler.PREFERENCE_NAME)) {
            for (Control child : parent.getChildren()) {
                child.dispose();
            }
            createSparqlContent();
            parent.layout(true, true);
        }
    }

    @Override
    public void createPartControl(Composite parent) {
        super.createPartControl(parent);
        this.parent = parent;
        createSparqlContent();
        setInitialized(true);
    }

    @Override
    public void modelActivated(ModelActivatedEvent event) {
        refreshWithChangedModelInformation();
    }

    @Override
    public void modelChanged(ModelChangedEvent event) {
        refreshWithChangedModelInformation();
    }

    @Override
    public void subModelAdded(SubModelAddedEvent event) {
        refreshWithChangedModelInformation();
    }

    @Override
    public void subModelRemoved(SubModelRemovedEvent event) {
        refreshWithChangedModelInformation();
    }

    @Override
    public void namespacePrefixChanged(NamespacePrefixChangedEvent event) {
        refreshWithChangedModelInformation();
    }

    private void refreshWithChangedModelInformation() {
        MenuManager menuManager = new MenuManager();
        MenuManager prefixManager = new MenuManager(Messages.SparqlView_PrefixesMenu);

        if (hasModelProvider()) {
            final ModelNodeLabelProvider rdfNodeLabelProvider = getLabelProvider();
            labelProvider = new TableLabelProvider() {

                @Override
                public String getColumnText(Object element, int columnIndex) {
                    QuerySolution solution = (QuerySolution) element;
                    String variable = variableNames.get(columnIndex);
                    RDFNode node = solution.get(variable);

                    return rdfNodeLabelProvider.getText(node);
                }

                @Override
                public Image getColumnImage(Object element, int columnIndex) {
                    QuerySolution solution = (QuerySolution) element;
                    String variable = variableNames.get(columnIndex);
                    RDFNode node = solution.get(variable);

                    return rdfNodeLabelProvider.getImage(node);
                }
            };
            comparator = new SparqlResultViewerComparator(labelProvider);
            if (viewer != null) {
                viewer.setLabelProvider(labelProvider);
            }

            final Model model = getOntModel();
            final StringBuilder allPrefixes = new StringBuilder();
            for (String prefix : model.getNsPrefixMap().keySet()) {
                String uri = model.getNsPrefixURI(prefix);
                final String prefixText = String.format("PREFIX %s: <%s>\n", //$NON-NLS-1$
                        prefix, uri);
                allPrefixes.append(prefixText);
                if (Strings.isNullOrEmpty(prefix)) {
                    prefix = Messages.SparqlView_DefaultNamespace;
                }
                prefixManager.add(new Action(prefix) {
                    @Override
                    public void run() {
                        queryText.setText(prefixText + queryText.getText());
                        layoutStyledText();
                    }
                });
            }
            if (allPrefixes.length() > 0) {
                prefixManager.add(new Separator());
                prefixManager.add(new Action(Messages.SparqlView_AllNamespaces) {
                    @Override
                    public void run() {
                        queryText.setText(allPrefixes.toString() + queryText.getText());
                        layoutStyledText();
                    }
                });
            }
        }

        menuManager.add(prefixManager);
        Menu menu = menuManager.createContextMenu(queryText);
        queryText.setMenu(menu);
    }

    protected void layoutStyledText() {
        String sparql = queryText.getText();
        SparqlRegionAnalyzer analyzer = new SparqlRegionAnalyzer();
        List<SparqlRegion> regions = analyzer.analyzeSparql(sparql);
        StyleRange[] ranges = new StyleRange[regions.size()];
        computeStyleRanges(regions).toArray(ranges);
        queryText.setStyleRanges(ranges);
    }

    private static List<StyleRange> computeStyleRanges(List<SparqlRegion> regions) {
        List<StyleRange> styleRanges = Lists.newArrayList();
        for (SparqlRegion region : regions) {
            int start = region.getStart();
            int length = region.getEnd() - start;

            StyleRange range = new StyleRange();
            range.background = Display.getDefault().getSystemColor(SWT.COLOR_WHITE);
            range.foreground = Display.getDefault().getSystemColor(SWT.COLOR_BLACK);

            switch (region.getTokenType()) {
            case SparqlParser.IRI_REF:
                range.foreground = Display.getDefault().getSystemColor(SWT.COLOR_BLUE);
                length += 2;
                break;
            case SparqlParser.SELECT:
            case SparqlParser.ASK:
            case SparqlParser.CONSTRUCT:
            case SparqlParser.DESCRIBE:
            case SparqlParser.WHERE:
            case SparqlParser.FROM:
            case SparqlParser.NAMED:
            case SparqlParser.LIMIT:
            case SparqlParser.BASE:
            case SparqlParser.PREFIX:
            case SparqlParser.OPTIONAL:
            case SparqlParser.FILTER:
            case SparqlParser.ORDER:
            case SparqlParser.OFFSET:
            case SparqlParser.DISTINCT:
            case SparqlParser.REDUCED:
            case SparqlParser.BY:
                range.foreground = Display.getDefault().getSystemColor(SWT.COLOR_DARK_MAGENTA);
                range.fontStyle = SWT.BOLD;
                break;
            case SparqlParser.VAR1:
            case SparqlParser.VAR2:
            case SparqlParser.VARNAME:
            case SparqlParser.PN_PREFIX:
                range.foreground = Display.getDefault().getSystemColor(SWT.COLOR_BLUE);
                length += 1;
                break;
            case SparqlParser.ASTERISK:
                range.foreground = Display.getDefault().getSystemColor(SWT.COLOR_BLUE);
                break;
            case SparqlParser.BLANK_NODE_LABEL:
                range.foreground = Display.getDefault().getSystemColor(SWT.COLOR_GRAY);
                length += 2;
                break;
            case SparqlParser.STRING_LITERAL1:
            case SparqlParser.STRING_LITERAL2:
            case SparqlParser.STRING_LITERAL_LONG1:
            case SparqlParser.STRING_LITERAL_LONG2:
                range.foreground = Display.getDefault().getSystemColor(SWT.COLOR_DARK_GREEN);
                break;
            case SparqlParser.BOUND:
            case SparqlParser.ISIRI:
            case SparqlParser.ISBLANK:
            case SparqlParser.ISLITERAL:
            case SparqlParser.STR:
            case SparqlParser.LANG:
            case SparqlParser.DATATYPE:
            case SparqlParser.SAMETERM:
            case SparqlParser.LANGMATCHES:
            case SparqlParser.REGEX:
                range.foreground = Display.getDefault().getSystemColor(SWT.COLOR_DARK_BLUE);
                range.fontStyle = SWT.BOLD;
                break;
            case SparqlParser.OR:
            case SparqlParser.AND:
            case SparqlParser.NOT:
            case SparqlParser.EQUAL:
            case SparqlParser.NOT_EQUAL:
                range.foreground = Display.getDefault().getSystemColor(SWT.COLOR_DARK_BLUE);
                range.fontStyle = SWT.BOLD;
                break;
            default:
                break;
            }
            range.start = start;
            range.length = length;
            styleRanges.add(range);
        }
        return styleRanges;
    }

    private void createSparqlContent() {
        int style = getLayoutState().equals(SparqlViewLayoutHandler.STATE_VERTICAL) ? SWT.VERTICAL
                : SWT.HORIZONTAL;
        SashForm sashForm = new SashForm(parent, style);

        Composite queryComposite = new Composite(sashForm, SWT.NONE);
        FillLayout fillLayout = new FillLayout(SWT.VERTICAL);
        queryComposite.setLayout(fillLayout);

        IThemeManager themeManager = PlatformUI.getWorkbench().getThemeManager();
        ITheme currentTheme = themeManager.getCurrentTheme();
        FontRegistry fontRegistry = currentTheme.getFontRegistry();
        Font font = fontRegistry.get("org.eclipse.jface.textfont"); //$NON-NLS-1$

        TextViewer viewer = new TextViewer(queryComposite, SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);

        queryText = viewer.getTextWidget();
        queryText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                querystring = ((StyledText) e.getSource()).getText();
                layoutStyledText();
            }
        });
        queryText.setMargins(3, 3, 3, 3);
        queryText.setFont(font);

        DropTarget target = new DropTarget(queryText, DND.DROP_DEFAULT | DND.DROP_MOVE
                | DND.DROP_COPY | DND.DROP_LINK);
        target.setTransfer(new Transfer[] { ResourceTransfer.getInstance(),
                TextTransfer.getInstance() });
        target.addDropListener(new DropTargetAdapter() {
            @Override
            public void dragEnter(DropTargetEvent e) {
                if (e.detail == DND.DROP_DEFAULT) {
                    e.detail = DND.DROP_COPY;
                }
            }

            @Override
            public void dragOperationChanged(DropTargetEvent e) {
                if (e.detail == DND.DROP_DEFAULT) {
                    e.detail = DND.DROP_COPY;
                }
            }

            @Override
            public void drop(DropTargetEvent e) {
                String text = null;
                if (ResourceTransfer.getInstance().isSupportedType(e.currentDataType)) {
                    Resource resource = (Resource) e.data;
                    if (resource != null && !resource.isAnon()) {
                        text = String.format("<%s>", resource.getURI()); //$NON-NLS-1$
                    }
                }
                else if (TextTransfer.getInstance().isSupportedType(e.currentDataType)) {
                    text = (String) e.data;
                }
                queryText.insert(text);
                layoutStyledText();
            }
        });

        GridLayout gridLayout = new GridLayout(1, false);
        gridLayout.marginWidth = 0;
        gridLayout.marginHeight = 0;
        gridLayout.marginBottom = 4;

        GridData layoutData = new GridData(GridData.FILL_BOTH);

        resultComposite = new Composite(sashForm, SWT.NONE);
        resultComposite.setLayout(gridLayout);
        resultComposite.setLayoutData(layoutData);

        refreshViewer();
    }

    private void setResult(List<String> variableNames, List<QuerySolution> solutions) {
        this.variableNames = variableNames;
        this.solutions = solutions;
        refreshViewer();
    }

    private void refreshViewer() {
        if (querystring != null) {
            queryText.setText(querystring);
            layoutStyledText();
        }
        if (table != null) {
            table.dispose();
            resultLabel.dispose();
        }
        if (variableNames != null) {
            GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);

            table = new Table(resultComposite, SWT.NONE);
            table.setLayoutData(layoutData);

            viewer = new TableViewer(table);
            table.setLinesVisible(true);
            table.setHeaderVisible(true);

            int index = 0;
            for (String variable : variableNames) {
                TableViewerColumn viewerColumn = new TableViewerColumn(viewer, SWT.NONE);
                final TableColumn column = viewerColumn.getColumn();
                column.setText(String.format("?%s", variable)); //$NON-NLS-1$
                column.setResizable(true);
                column.setMoveable(false);
                final int columnIndex = index++;
                column.addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        if (comparator != null) {
                            comparator.setColumn(columnIndex);
                            int dir = comparator.getDirection();
                            viewer.getTable().setSortDirection(dir);
                            viewer.getTable().setSortColumn(column);
                            viewer.refresh();
                        }
                    }
                });
                column.setWidth(250);
            }

            viewer.setContentProvider(new ArrayContentProvider());
            viewer.setLabelProvider(labelProvider);
            viewer.setComparator(comparator);
            viewer.setInput(solutions);
            viewer.setItemCount(solutions.size());

            layoutData = new GridData(GridData.FILL, GridData.CENTER, false, false, 1, 1);
            layoutData.horizontalIndent = 5;

            resultLabel = new Label(resultComposite, SWT.NONE);
            resultLabel
                    .setText(String.format(Messages.SparqlView_FoundSolutions, solutions.size()));
            resultLabel.setLayoutData(layoutData);
        }
        parent.layout(true, true);
    }

    @Override
    public void setFocus() {
        queryText.setFocus();
    }

    private String getQueryString() {
        return querystring;
    }

    private String getLayoutState() {
        if (SparqlPlugin.getDefault().getPreferenceStore()
                .getString(SparqlViewLayoutHandler.PREFERENCE_NAME).length() > 0) {
            return SparqlPlugin.getDefault().getPreferenceStore()
                    .getString(SparqlViewLayoutHandler.PREFERENCE_NAME);
        }
        return SparqlViewLayoutHandler.STATE_VERTICAL;
    }

    public static class SparqlViewLayoutHandler extends AbstractHandler {
        public static final String ID = "com.semmtech.plugin.semmweb.sparql.commands.sparqlViewLayout"; //$NON-NLS-1$
        public static final String PREFERENCE_NAME = "com.semmtech.plugin.semmweb.sparql.commands.sparqlViewLayout"; //$NON-NLS-1$
        public static final String STATE_VERTICAL = "vertical"; //$NON-NLS-1$
        public static final String STATE_HORIZONTAL = "horizontal"; //$NON-NLS-1$

        @Override
        public Object execute(ExecutionEvent event) throws ExecutionException {
            if (HandlerUtil.matchesRadioState(event)) {
                return null;
            }
            String currentState = event.getParameter(RadioState.PARAMETER_ID);
            HandlerUtil.updateRadioState(event.getCommand(), currentState);
            SparqlPlugin.getDefault().getPreferenceStore().setValue(PREFERENCE_NAME, currentState);
            return null;
        }
    }

    private static class ExecuteSparqlQueryJob extends Job {
        public static int SELECT_QUERY = 1;
        public static int ASK_QUERY = 2;
        public static int CONSTRUCT_QUERY = 3;
        public static int DESCRIBE_QUERY = 4;

        private String querystring;
        private Model model;
        private List<QuerySolution> solutions;
        private List<String> variableNames;
        private int type;

        public ExecuteSparqlQueryJob(String querystring, Model model) {
            super(Messages.SparqlView_ExecuteQuery);
            this.querystring = querystring;
            this.model = model;
        }

        @Override
        protected IStatus run(IProgressMonitor monitor) {
            monitor.beginTask(Messages.SparqlView_ExecuteQuery_2, 3);
            monitor.subTask(Messages.SparqlView_CreatingQuery);
            Query query = QueryFactory.create(querystring);
            monitor.worked(1);
            QueryExecution execution = null;
            if (Strings.isNullOrEmpty(target)) {
                monitor.subTask(Messages.SparqlView_RunningQueryLocal);
                execution = QueryExecutionFactory.create(query, model);
            }
            else {
                monitor.subTask(String.format(Messages.SparqlView_RunningQueryOnFile, target));
                execution = QueryExecutionFactory.sparqlService(target, query);
            }

            ANTLRStringStream input = new ANTLRStringStream(querystring);
            SparqlLexer lex = new SparqlLexer(input);
            for (Token token = lex.nextToken(); token != Token.EOF_TOKEN && type == 0; token = lex
                    .nextToken()) {
                switch (token.getType()) {
                case SparqlParser.ASK:
                    type = ASK_QUERY;
                    break;
                case SparqlParser.SELECT:
                    type = SELECT_QUERY;
                    break;
                case SparqlParser.CONSTRUCT:
                    type = CONSTRUCT_QUERY;
                    break;
                case SparqlParser.DESCRIBE:
                    type = DESCRIBE_QUERY;
                    break;
                default:
                    continue;
                }
            }

            if (type == SELECT_QUERY) {
                ResultSet result = execution.execSelect();
                solutions = Lists.newArrayList();
                while (result.hasNext()) {
                    solutions.add(result.next());
                }
                variableNames = result.getResultVars();
                monitor.worked(1);

                monitor.subTask(String.format(Messages.SparqlView_RetrievingSolutions,
                        solutions.size()));
                Display.getDefault().syncExec(new Runnable() {
                    @Override
                    public void run() {
                        if (singleton == null) {
                            return;
                        }
                        singleton.setResult(variableNames, solutions);
                    }
                });
            }
            else if (type == ASK_QUERY) {
                final boolean result = execution.execAsk();
                Display.getDefault().syncExec(new Runnable() {
                    @Override
                    public void run() {
                        MessageDialog.openInformation(singleton.getSite().getShell(),
                                Messages.SparqlView_AskQueryTitle, Messages.SparqlView_AskAnswer
                                        + result);
                    }
                });
            }
            else if (type == DESCRIBE_QUERY) {

            }
            else if (type == CONSTRUCT_QUERY) {

            }
            execution.close();
            monitor.worked(1);
            monitor.done();
            return Status.OK_STATUS;
        }
    }

    public static class ExecuteSparqlQueryHandler extends AbstractHandler {
        public static final String ID = "com.semmtech.plugin.semmweb.sparql.commands.executeSparqlQuery"; //$NON-NLS-1$

        @Override
        public Object execute(ExecutionEvent event) throws ExecutionException {
            String querystring = singleton.getQueryString();
            IModelProvider provider = CorePlugin.getDefault().getActiveModelProvider();
            if (provider == null) {
                return null;
            }
            Model model = provider.getOntModel();

            ExecuteSparqlQueryJob job = new ExecuteSparqlQueryJob(querystring, model);
            job.setUser(true);
            job.schedule();

            return null;
        }
    }

    public class SparqlResultViewerComparator extends ViewerComparator {
        private int propertyIndex;
        private static final int DESCENDING = 1;
        private int direction = DESCENDING;
        private TableLabelProvider labelProvider;

        public SparqlResultViewerComparator(TableLabelProvider labelProvider) {
            this.propertyIndex = 0;
            direction = DESCENDING;
            this.labelProvider = labelProvider;
        }

        public int getDirection() {
            return direction == 1 ? SWT.DOWN : SWT.UP;
        }

        public void setColumn(int column) {
            if (column == this.propertyIndex) {
                // Same column as last sort; toggle the direction
                direction = 1 - direction;
            }
            else {
                // New column; do an ascending sort
                this.propertyIndex = column;
                direction = DESCENDING;
            }
        }

        @Override
        public int compare(Viewer viewer, Object e1, Object e2) {
            QuerySolution s1 = (QuerySolution) e1;
            QuerySolution s2 = (QuerySolution) e2;

            int rc = 0;
            String text1 = labelProvider.getColumnText(s1, propertyIndex);
            String text2 = labelProvider.getColumnText(s2, propertyIndex);
            rc = text1.compareTo(text2);

            // If descending order, flip the direction
            if (direction == DESCENDING) {
                rc = -rc;
            }
            return rc;
        }
    }

    public static class UpdateSparqlTargetHandler extends AbstractHandler {
        public static final String ID = "com.semmtech.plugin.semmweb.sparql.commands.updateSparqlTarget"; //$NON-NLS-1$

        public static final String PARAMETER_TAGRET_URI = "targetUri"; //$NON-NLS-1$

        @Override
        public Object execute(ExecutionEvent event) throws ExecutionException {
            target = event.getParameter(PARAMETER_TAGRET_URI);
            System.out.println("Target has been set to \"" + target + "\""); //$NON-NLS-1$ //$NON-NLS-2$
            return null;
        }
    }

}
