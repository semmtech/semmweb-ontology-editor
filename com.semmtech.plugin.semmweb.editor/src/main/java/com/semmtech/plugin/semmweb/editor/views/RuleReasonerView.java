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

package com.semmtech.plugin.semmweb.editor.views;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.TextStyle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ResourceTransfer;
import org.eclipse.ui.themes.ITheme;
import org.eclipse.ui.themes.IThemeManager;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.impl.ModelCom;
import com.hp.hpl.jena.rdf.model.impl.StatementImpl;
import com.hp.hpl.jena.reasoner.Derivation;
import com.hp.hpl.jena.reasoner.TriplePattern;
import com.hp.hpl.jena.reasoner.ValidityReport;
import com.hp.hpl.jena.reasoner.ValidityReport.Report;
import com.hp.hpl.jena.reasoner.rulesys.ClauseEntry;
import com.hp.hpl.jena.reasoner.rulesys.Functor;
import com.hp.hpl.jena.reasoner.rulesys.GenericRuleReasoner;
import com.hp.hpl.jena.reasoner.rulesys.Rule;
import com.hp.hpl.jena.reasoner.rulesys.Rule.Parser;
import com.hp.hpl.jena.reasoner.rulesys.RuleDerivation;
import com.hp.hpl.jena.reasoner.rulesys.impl.LPInterpreter;
import com.hp.hpl.jena.util.PrintUtil;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.dialog.RulesFileInputDialog;
import com.semmtech.plugin.semmweb.core.handlers.PreferenceStoreToggleHandler;
import com.semmtech.plugin.semmweb.core.model.IModelProvider;
import com.semmtech.plugin.semmweb.core.model.events.ModelActivatedEvent;
import com.semmtech.plugin.semmweb.core.model.events.ModelChangedEvent;
import com.semmtech.plugin.semmweb.core.model.events.NamespacePrefixChangedEvent;
import com.semmtech.plugin.semmweb.core.model.events.SubModelAddedEvent;
import com.semmtech.plugin.semmweb.core.model.events.SubModelRemovedEvent;
import com.semmtech.plugin.semmweb.core.viewers.SimpleRDFNodeLabelProvider;
import com.semmtech.plugin.semmweb.core.views.AbstractModelListenerView;
import com.semmtech.plugin.semmweb.editor.EditorPlugin;
import com.semmtech.plugin.semmweb.editor.EditorPluginImages;
import com.semmtech.plugin.semmweb.editor.preferences.RuleReasonerViewPreference;
import com.semmtech.plugin.semmweb.editor.preferences.RuleReasonerViewPreferenceConstants;
import com.semmtech.ui.plugin.viewers.LazyTreeContentProvider;
import com.semmtech.ui.plugin.viewers.TableLabelProvider;


public class RuleReasonerView extends AbstractModelListenerView implements IPropertyChangeListener {
    public static final String ID = "com.semmtech.plugin.semmweb.editor.views.reasoners";
    private static final Logger logger = Logger.getLogger(RuleReasonerView.class);

    private static RuleReasonerView singleton;

    private StyledText styledText;

    private Label statusLabel;
    private InfModel infModel;
    private OntModel baseModel;
    private Map<Statement, List<RuleDerivation>> derivations = Maps.newHashMap();
    private LabelProvider labelProvider;
    private GenericRuleReasoner reasoner;
    private List<Rule> parsedRules = null;
    private String filename = null;
    private TreeViewer resultViewer;
    private Parser parser;
    private CheckboxTreeViewer rulesViewer;
    private CTabItem rulesTabItem;
    private CTabItem sourceTabItem;
    private CTabFolder tabFolder;
    private Font font;
    private SashForm sashForm;
    private StyledText logText;
    private StringBuffer executeLog;
    private CTabItem logTabItem;
    private List<TreeStatement> visibleTreeStatements;
    private TreeStatementViewerComparator comparator = null;

    public RuleReasonerView() {
        singleton = this;
    }

    @Override
    public void createPartControl(Composite parent) {
        super.createPartControl(parent);

        baseModel = getOntModel();
        labelProvider = new SimpleRDFNodeLabelProvider();
        parent.setLayout(new GridLayout(1, false));
        sashForm = new SashForm(parent, SWT.NONE);
        sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        IThemeManager themeManager = PlatformUI.getWorkbench().getThemeManager();
        ITheme currentTheme = themeManager.getCurrentTheme();
        FontRegistry fontRegistry = currentTheme.getFontRegistry();
        font = fontRegistry.get("org.eclipse.jface.textfont");

        tabFolder = new CTabFolder(sashForm, SWT.BORDER | SWT.BOTTOM);
        tabFolder.setSelectionBackground(Display.getCurrent().getSystemColor(
                SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));

        createSourceTab();
        createRulesTab();
        createLogTab();
        createResultControls();

        statusLabel = new Label(parent, SWT.NONE);
        statusLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        statusLabel.setText("");

        tabFolder.setSelection(sourceTabItem);

        setInitialized(true);
    }

    @Override
    public void init(IViewSite site) throws PartInitException {
        super.init(site);
        EditorPlugin.getDefault().getPreferenceStore().addPropertyChangeListener(this);
    }

    @Override
    public void dispose() {
        EditorPlugin.getDefault().getPreferenceStore().removePropertyChangeListener(this);
        super.dispose();
    }

    private void createResultControls() {
        Composite treeComposite = new Composite(sashForm, SWT.BORDER);
        {
            GridLayout layout = new GridLayout(1, false);
            layout.verticalSpacing = 0;
            layout.horizontalSpacing = 0;
            treeComposite.setLayout(layout);
        }

        Tree resultTree = new Tree(treeComposite, SWT.BORDER | SWT.VIRTUAL | SWT.FULL_SELECTION
                | SWT.MULTI);
        resultTree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        resultTree.setLinesVisible(true);
        resultTree.setHeaderVisible(true);

        resultViewer = new TreeViewer(resultTree);

        createTableViewerColumn("rdf:subject", 0, 220);
        createTableViewerColumn("rdf:predicate", 1, 175);
        createTableViewerColumn("rdf:object", 2, 350);
        createTableViewerColumn("Origin", 3, 100);

        resultViewer.setUseHashlookup(true);
        sashForm.setWeights(new int[] { 1, 1 });

        resultViewer.setContentProvider(new LazyTreeContentProvider() {

            @Override
            public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
                derivations = Maps.newHashMap();
            }

            @Override
            public void updateElement(Object parent, int index) {
                if (parent instanceof InfModel) {
                    TreeStatement element = visibleTreeStatements.get(index);
                    Statement statement = element.getStatement();
                    resultViewer.replace(parent, index, element);
                    List<RuleDerivation> derivs = findDerivation(statement);
                    resultViewer.setChildCount(element, derivs.size());
                }
                else if (parent instanceof TreeStatement) {
                    Statement statement = ((TreeStatement) parent).getStatement();
                    if (derivations.containsKey(statement)) {
                        RuleDerivation deriv = derivations.get(statement).get(index);
                        TreeRuleDerivation element = new TreeRuleDerivation(deriv);
                        resultViewer.replace(parent, index, element);
                        resultViewer.setChildCount(element, deriv.getMatches().size());
                    }
                }
                else if (parent instanceof TreeRuleDerivation) {
                    RuleDerivation deriv = ((TreeRuleDerivation) parent).getDerivation();
                    Triple triple = deriv.getMatches().get(index);

                    if (triple != null && triple.getSubject() != null
                            && triple.getPredicate() != null && triple.getObject() != null) {
                        Statement statement = StatementImpl
                                .toStatement(triple, (ModelCom) infModel);
                        int type = TreeStatement.STATEMENT_ASSERTED;
                        if (baseModel.isInBaseModel(statement)) {
                            type = TreeStatement.STATEMENT_ASSERTED;
                        }
                        else if (baseModel.listStatements().toSet().contains(statement)) {
                            type = TreeStatement.STATEMENT_IMPORTED;
                        }
                        else {
                            type = TreeStatement.STATEMENT_INFERRED;
                        }
                        TreeStatement element = new TreeStatement(statement, type);
                        resultViewer.replace(parent, index, element);
                        List<RuleDerivation> derivs = findDerivation(statement);
                        resultViewer.setChildCount(element, derivs.size());
                    }
                    else {
                        TreeTriple element = new TreeTriple(triple);
                        resultViewer.replace(parent, index, element);
                        resultViewer.setChildCount(element, 0);
                    }
                }
            }
        });
        resultViewer.setLabelProvider(new TableLabelProvider() {
            @Override
            public String getColumnText(Object element, int columnIndex) {
                if (element instanceof TreeStatement) {
                    Statement statement = ((TreeStatement) element).getStatement();
                    if (labelProvider == null) {
                        return null;
                    }

                    if (columnIndex == 0) {
                        return labelProvider.getText(statement.getSubject());
                    }
                    else if (columnIndex == 1) {
                        return labelProvider.getText(statement.getPredicate());
                    }
                    else if (columnIndex == 2) {
                        RDFNode object = statement.getObject();
                        if (object.isResource()) {
                            return labelProvider.getText(object.asResource());
                        }
                        else if (object.isLiteral()) {
                            return labelProvider.getText(object.asLiteral());
                        }
                    }
                    else if (columnIndex == 3) {
                        return ((TreeStatement) element).getOrigin();
                    }
                }
                else if (element instanceof TreeTriple) {
                    Triple triple = ((TreeTriple) element).getTriple();
                    if (columnIndex == 0) {
                        if (triple == null) {
                            return "<empty>";
                        }
                        return triple.toString();
                    }
                }
                else if (element instanceof TreeRuleDerivation) {
                    RuleDerivation deriv = ((TreeRuleDerivation) element).getDerivation();
                    Rule rule = deriv.getRule();
                    if (columnIndex == 0) {
                        return rule.toString();
                    }
                    if (columnIndex == 1) {

                    }
                    if (columnIndex == 2) {

                    }
                }
                return null;
            }

            @Override
            public Image getColumnImage(Object element, int columnIndex) {
                if (element instanceof TreeStatement) {
                    if (columnIndex == 0) {
                        TreeStatement item = (TreeStatement) element;
                        if (item.getStatementType() == TreeStatement.STATEMENT_ASSERTED) {
                            return EditorPlugin.getDefault().getImage(
                                    EditorPluginImages.IMG_TRIPLE_ASSERTED);
                        }
                        else if (item.getStatementType() == TreeStatement.STATEMENT_IMPORTED) {
                            return EditorPlugin.getDefault().getImage(
                                    EditorPluginImages.IMG_TRIPLE_IMPORTED);
                        }
                        else if (item.getStatementType() == TreeStatement.STATEMENT_INFERRED) {
                            return EditorPlugin.getDefault().getImage(
                                    EditorPluginImages.IMG_TRIPLE_INFERRED);
                        }
                    }
                }
                else if (element instanceof TreeRuleDerivation) {
                    if (columnIndex == 0) {
                        return EditorPlugin.getDefault().getImage(EditorPluginImages.IMG_RULE);
                    }
                }
                return null;
            }
        });
        resultViewer.getTree().addListener(SWT.PaintItem, new Listener() {
            @Override
            public void handleEvent(Event event) {
                TreeItem item = (TreeItem) event.item;
                if (item.getData() instanceof TreeStatement) {
                    TreeStatement element = (TreeStatement) item.getData();
                    if (element.getStatementType() == TreeStatement.STATEMENT_ASSERTED) {
                        item.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
                    }
                    else if (element.getStatementType() == TreeStatement.STATEMENT_IMPORTED) {
                        item.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
                    }
                    else {
                        item.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GRAY));
                    }
                }
            }
        });
    }

    private void createLogTab() {
        logTabItem = new CTabItem(tabFolder, SWT.NONE);
        logTabItem.setText("Logging");

        Composite logComposite = new Composite(tabFolder, SWT.NONE);
        logTabItem.setControl(logComposite);
        {
            GridLayout layout = new GridLayout(1, false);
            layout.marginWidth = 0;
            layout.marginHeight = 0;
            logComposite.setLayout(layout);
        }

        logText = new StyledText(logComposite, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL
                | SWT.READ_ONLY);
        logText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        logText.setFont(font);
        logText.setTopMargin(2);
        logText.setRightMargin(2);
        logText.setBottomMargin(2);
        logText.setLeftMargin(2);

        executeLog = new StringBuffer();

        Logger.getRootLogger().addAppender(new AppenderSkeleton() {
            private SimpleDateFormat dateFormat = new SimpleDateFormat("k:mm:ss");

            @Override
            public boolean requiresLayout() {
                return false;
            }

            @Override
            public void close() {
            }

            @Override
            protected void append(LoggingEvent event) {
                if (event.getLoggerName().equals(LPInterpreter.class.getName())) {
                    executeLog.append(dateFormat.format(new Date(event.timeStamp)) + " - "
                            + event.getRenderedMessage() + "\n");
                }
            }
        });
    }

    private void createRulesTab() {
        rulesTabItem = new CTabItem(tabFolder, SWT.NONE);
        rulesTabItem.setText("Rules");

        Composite composite = new Composite(tabFolder, SWT.NONE);
        rulesTabItem.setControl(composite);
        FillLayout fl_composite = new FillLayout(SWT.HORIZONTAL);
        fl_composite.marginWidth = 4;
        fl_composite.marginHeight = 4;
        composite.setLayout(fl_composite);

        Tree tree = new Tree(composite, SWT.BORDER | SWT.FULL_SELECTION); // |
                                                                          // SWT.CHECK);
        tree.setLinesVisible(true);
        tree.setHeaderVisible(true);

        // tree.addListener(SWT.Selection, new Listener() {
        // public void handleEvent(Event event) {
        // if (event.detail == SWT.CHECK) {
        // TreeItem item = (TreeItem) event.item;
        // boolean checked = item.getChecked();
        // checkItems(item, checked);
        // checkPath(item.getParentItem(), checked, false);
        // }
        // }
        //
        // void checkPath(TreeItem item, boolean checked, boolean grayed) {
        // if (item == null) return;
        // if (grayed) {
        // checked = true;
        // } else {
        // int index = 0;
        // TreeItem[] items = item.getItems();
        // while (index < items.length) {
        // TreeItem child = items[index];
        // if (child.getGrayed() || checked != child.getChecked()) {
        // checked = grayed = true;
        // break;
        // }
        // index++;
        // }
        // }
        // item.setChecked(checked);
        // item.setGrayed(grayed);
        // checkPath(item.getParentItem(), checked, grayed);
        // }
        //
        // void checkItems(TreeItem item, boolean checked) {
        // item.setGrayed(false);
        // item.setChecked(checked);
        // TreeItem[] items = item.getItems();
        // for (int i = 0; i < items.length; i++) {
        // checkItems(items[i], checked);
        // }
        // }
        // });

        rulesViewer = new CheckboxTreeViewer(tree);

        TreeColumn column = new TreeColumn(tree, SWT.NONE);
        column.setText("Name");
        column.setWidth(150);
        column.setMoveable(false);

        column = new TreeColumn(tree, SWT.NONE);
        column.setText("Rule");
        column.setWidth(350);
        column.setMoveable(false);

        column = new TreeColumn(tree, SWT.NONE);
        column.setText("Type");
        column.setWidth(100);
        column.setMoveable(false);

        // rulesViewer.setCheckStateProvider(new ICheckStateProvider() {
        //
        // @Override
        // public boolean isGrayed(Object element) {
        // return false;
        // }
        //
        // @Override
        // public boolean isChecked(Object element) {
        // return true;
        // }
        // });
        rulesViewer.setContentProvider(new ITreeContentProvider() {

            @Override
            public void dispose() {
            }

            @Override
            public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

            }

            @Override
            public Object[] getElements(Object inputElement) {
                if (inputElement instanceof List<?>) {
                    return ((List<?>) inputElement).toArray();
                }
                return null;
            }

            @Override
            public Object[] getChildren(Object parentElement) {
                if (parentElement instanceof Rule) {
                    logger.debug("getChildren called with Rule: " + parentElement.toString());
                    Rule rule = (Rule) parentElement;
                    RuleHead head = new RuleHead(rule, rule.getHead());
                    RuleBody body = new RuleBody(rule, rule.getBody());
                    if (!rule.isAxiom()) {
                        return new Object[] { body, head };
                    }
                    return new Object[] { head };
                }
                else if (parentElement instanceof RuleHead) {
                    logger.debug("getChildren called with RuleHead: " + parentElement.toString());
                    RuleHead head = (RuleHead) parentElement;
                    return head.getHead().toArray();
                }
                else if (parentElement instanceof RuleBody) {
                    logger.debug("getChildren called with RuleBody: " + parentElement.toString());
                    RuleBody body = (RuleBody) parentElement;
                    return body.getBody().toArray();
                }
                return null;
            }

            @Override
            public Object getParent(Object element) {
                return null;
            }

            @Override
            public boolean hasChildren(Object element) {
                Object[] children = getChildren(element);
                if (children == null || children.length == 0) {
                    return false;
                }
                return true;
            }

        });
        rulesViewer.expandAll();

        FontData[] boldFontData = getModifiedFontData(
                rulesViewer.getTree().getFont().getFontData(), SWT.BOLD);
        Font boldFont = new Font(Display.getCurrent(), boldFontData);

        rulesViewer.setLabelProvider(new RulesLabelProvider(boldFont));

        // / Create context menu
        // MenuManager menuManager = new MenuManager();
        // IAction selectAllAction = new Action() {
        // @Override
        // public void run() {
        // selectAllRules();
        // }
        // };
        // selectAllAction.setText("Select All");
        // selectAllAction.setImageDescriptor(EditorPlugin.getDefault().imageDescriptorFromPlugin(EditorPlugin.PLUGIN_ID,
        // EditorPluginImages.IMG_SELECTION_CHECK_ALL));
        //
        // IAction selectNoneAction = new Action() {
        // @Override
        // public void run() {
        // selectNoRules();
        // }
        // };
        // selectNoneAction.setText("Select None");
        // selectNoneAction.setImageDescriptor(EditorPlugin.getDefault().imageDescriptorFromPlugin(EditorPlugin.PLUGIN_ID,
        // EditorPluginImages.IMG_SELECTION_CHECK_NONE));
        //
        // menuManager.add(selectAllAction);
        // menuManager.add(selectNoneAction);
        //
        // Menu menu = menuManager.createContextMenu(rulesViewer.getControl());
        // rulesViewer.getControl().setMenu(menu);
        // getSite().registerContextMenu(menuManager, rulesViewer);
    }

    private void createSourceTab() {
        sourceTabItem = new CTabItem(tabFolder, SWT.NONE);
        sourceTabItem.setText("Source");

        Composite textComposite = new Composite(tabFolder, SWT.BORDER);
        sourceTabItem.setControl(textComposite);
        {
            GridLayout layout = new GridLayout(1, false);
            layout.verticalSpacing = 4;
            layout.marginWidth = 0;
            layout.marginHeight = 0;
            layout.horizontalSpacing = 0;
            textComposite.setLayout(layout);
        }

        // / SWT.BORDER |SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL
        styledText = new StyledText(textComposite, SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
        styledText.setTopMargin(2);
        styledText.setRightMargin(2);
        styledText.setBottomMargin(2);
        styledText.setLeftMargin(2);
        styledText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        styledText.setFont(font);

        // / Create context menu
        MenuManager menuManager = new MenuManager();
        IAction selectAllAction = new Action() {
            @Override
            public void run() {
                IModelProvider provider = CorePlugin.getDefault().getActiveModelProvider();
                if (provider != null) {
                    OntModel model = provider.getOntModel();
                    if (model == null) {
                        return;
                    }

                    StringBuffer prefixes = new StringBuffer();
                    for (String prefix : model.getNsPrefixMap().keySet()) {
                        prefixes.append("@prefix " + prefix + ": <" + model.getNsPrefixURI(prefix)
                                + "> .\n");
                    }

                    styledText.setText(prefixes.toString() + "\n" + styledText.getText());
                }
            }
        };
        selectAllAction.setText("Copy Prefixes");

        menuManager.add(selectAllAction);

        Menu menu = menuManager.createContextMenu(styledText);
        styledText.setMenu(menu);

        DropTarget dropTarget = new DropTarget(styledText, DND.DROP_MOVE | DND.DROP_COPY);
        dropTarget.setTransfer(new Transfer[] { ResourceTransfer.getInstance() });
        dropTarget.addDropListener(new DropTargetAdapter() {
            @Override
            public void drop(DropTargetEvent event) {
                if (ResourceTransfer.getInstance().isSupportedType(event.currentDataType)) {
                    IResource[] resources = (IResource[]) event.data;
                    if (resources.length > 0) {
                        logger.debug("resource[0].getLocation() = '"
                                + resources[0].getLocation().toOSString() + "'");
                        readRulesFromFile(resources[0].getLocation().toOSString());
                    }
                }
            }
        });
    }

    protected void selectNoRules() {
        for (TreeItem item : rulesViewer.getTree().getItems()) {
            item.setChecked(false);
        }
    }

    protected void selectAllRules() {
        for (TreeItem item : rulesViewer.getTree().getItems()) {
            item.setChecked(true);
        }
    }

    private void createTableViewerColumn(String title, int index, int bound) {
        final TreeViewerColumn viewerColumn = new TreeViewerColumn(resultViewer, SWT.NONE);
        final TreeColumn column = viewerColumn.getColumn();
        column.setText(title);
        column.setWidth(bound);
        column.setResizable(true);
        column.setMoveable(false);
        column.addSelectionListener(getSelectionAdapater(column, index));
    }

    private SelectionAdapter getSelectionAdapater(final TreeColumn column, final int index) {
        SelectionAdapter selectionAdapter = new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (comparator == null) {
                    comparator = new TreeStatementViewerComparator();
                }
                comparator.setColumn(index);
                int dir = comparator.getDirection();

                resultViewer.getTree().setSortDirection(dir);
                resultViewer.getTree().setSortColumn(column);
                // viewer.refresh();
                refreshResultViewer();
            }
        };
        return selectionAdapter;
    }

    @Override
    public void setFocus() {
        styledText.setFocus();
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
        baseModel = getOntModel();

        labelProvider = new SimpleRDFNodeLabelProvider();

        if (reasoner != null && baseModel != null) {
            infModel = ModelFactory.createInfModel(reasoner, baseModel);
            refreshResultViewer();
        }
    }

    private void openRules() {
        RulesFileInputDialog dialog = new RulesFileInputDialog(getSite().getShell(), "Rules File",
                "Select an input file containting the reasoner's rules.");
        if (dialog.open() == 0) {
            clear();
            if (dialog.getFilename() != null) {
                readRulesFromFile(dialog.getFilename());
            }
        }
    }

    private void readRulesFromFile(String filename) {
        this.filename = filename;
        setMessage("Reading rules...");
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            StringBuffer fileData = new StringBuffer(1000);
            char[] buf = new char[1024];
            int numRead = 0;
            while ((numRead = reader.read(buf)) != -1) {
                String readData = String.valueOf(buf, 0, numRead);
                fileData.append(readData);
                buf = new char[1024];
            }
            styledText.setText(fileData.toString());
            tabFolder.setSelection(sourceTabItem);
            setMessage("Done reading");
        }
        catch (Exception ex) {
            setErrorMessage("ERROR: " + ex.getMessage());
        }
    }

    private void save() {
        if (filename != null) {
            setMessage("Saving...");
            try (BufferedWriter out = new BufferedWriter(new FileWriter(filename))) {
                out.write(styledText.getText());
                setMessage("Saved");
            }
            catch (IOException e) {
                setErrorMessage("ERROR: " + e.getMessage());
            }
        }
    }

    private void saveAs() {
        FileDialog dialog = new FileDialog(getSite().getShell(), SWT.SAVE);
        if (filename != null) {
            dialog.setFileName(filename);
            dialog.setOverwrite(true);
        }
        dialog.setFilterExtensions(new String[] { "*.rules", "*.*" });
        String saveFilename = dialog.open();
        if (saveFilename != null) {

            filename = saveFilename;
            save();

        }
    }

    private String parseRules() {
        String errorMessage = null;
        setMessage("Parsing...");
        try {
            StringReader reader = new StringReader(styledText.getText());
            parser = Rule.rulesParserFromReader(new BufferedReader(reader));
            // / TODO: Check prefixes
            parsedRules = Rule.parseRules(parser);
            reasoner = new GenericRuleReasoner(parsedRules);
            setMessage("Parsed " + parsedRules.size() + " rules");
            rulesViewer.setInput(parsedRules);
            rulesViewer.getTree().setItemCount(parsedRules.size());
            tabFolder.setSelection(rulesTabItem);
        }
        catch (Exception ex) {
            errorMessage = "ERROR: " + ex.getMessage();
            MessageDialog.openError(getSite().getShell(), "Error Parsing Rules",
                    "The following error ocurred during parsing:\n" + ex.getMessage());
            setErrorMessage(errorMessage);
        }
        return errorMessage;
    }

    private List<Rule> getActiveRules() {
        List<Rule> rules = Lists.newArrayList();
        for (TreeItem item : rulesViewer.getTree().getItems()) {
            // if (item.getChecked())
            rules.add((Rule) item.getData());
        }
        return rules;
    }

    private void executeRules() {
        logText.setText("");
        executeLog = new StringBuffer();

        List<Rule> activeRules = getActiveRules();
        if (activeRules.size() > 0) {
            setMessage("Executing...");
            Date start = new Date();
            reasoner = new GenericRuleReasoner(activeRules);
            reasoner.setTraceOn(RuleReasonerViewPreference.getTraceOn());
            reasoner.setDerivationLogging(RuleReasonerViewPreference.getDerivationLogging());

            baseModel = null;
            if (hasModelProvider()) {
                baseModel = getOntModel();
            }
            if (baseModel != null) {
                infModel = ModelFactory.createInfModel(reasoner, baseModel);
            }
            else {
                baseModel = ModelFactory.createOntologyModel();
                baseModel.setNsPrefixes(parser.getPrefixMap());
                infModel = ModelFactory.createInfModel(reasoner, baseModel);
            }
            refreshResultViewer();

            long execution = (new Date().getTime() - start.getTime());
            setMessage("Finished");
            executeLog.append("---\nExecution took " + execution + " ms.");
            logText.setText(executeLog.toString());
            if (RuleReasonerViewPreference.getTraceOn()) {
                tabFolder.setSelection(logTabItem);
            }
        }
    }

    public void validateModel() {
        if (infModel != null) {
            executeLog = new StringBuffer();
            logText.setText("");
            setMessage("Validating...");
            long start = new Date().getTime();
            ValidityReport validity = infModel.validate();

            long execution = (new Date().getTime() - start);
            if (validity.isValid()) {
                executeLog.append("OK\n");
                setMessage("Model is valid");
            }
            else {
                executeLog.append("Conflicts:\n");
                for (Iterator<Report> i = validity.getReports(); i.hasNext();) {
                    ValidityReport.Report report = i.next();
                    executeLog.append("\t" + report + "\n");
                }
                setMessage("Model contains conflict, see Log");
            }
            executeLog.append("---\nValidation took " + execution + " ms.");
            logText.setText(executeLog.toString());
            tabFolder.setSelection(logTabItem);
        }
    }

    private void clear() {
        filename = null;
        styledText.setText("");
        logText.setText("");

        rulesViewer.setInput(Lists.newArrayList());
        rulesViewer.getTree().setItemCount(0);

        infModel = null;
        refreshResultViewer();

        setMessage("Cleared");
        tabFolder.setSelection(sourceTabItem);
    }

    private void setMessage(String message) {
        statusLabel.setText(message);
    }

    private void setErrorMessage(String message) {
        statusLabel.setText(message);
    }

    public static class ParseRulesHandler extends AbstractHandler {
        public static final String ID = "com.semmtech.plugin.semmweb.editor.commands.parseRules";

        @Override
        public Object execute(ExecutionEvent event) throws ExecutionException {
            if (singleton != null && singleton.isInitialized()) {
                singleton.parseRules();
            }
            return null;
        }

    }

    public static class ExecuteRulesHandler extends AbstractHandler {
        public static final String ID = "com.semmtech.plugin.semmweb.editor.commands.executeRules";

        @Override
        public Object execute(ExecutionEvent event) throws ExecutionException {
            if (singleton != null && singleton.isInitialized()) {
                singleton.executeRules();
            }
            return null;
        }

    }

    public static class ValidateModelHandler extends AbstractHandler {
        public static final String ID = "com.semmtech.plugin.semmweb.editor.commands.validateModel";

        @Override
        public Object execute(ExecutionEvent event) throws ExecutionException {
            if (singleton != null && singleton.isInitialized()) {
                singleton.validateModel();
            }
            return null;
        }

    }

    public static class ClearRulesHandler extends AbstractHandler {
        public static final String ID = "com.semmtech.plugin.semmweb.editor.commands.clearRules";

        @Override
        public Object execute(ExecutionEvent event) throws ExecutionException {
            if (singleton != null && singleton.isInitialized()) {
                singleton.clear();
            }
            return null;
        }

    }

    public static class OpenRulesFileHandler extends AbstractHandler {
        public static final String ID = "com.semmtech.plugin.semmweb.editor.commands.openRulesFile";

        @Override
        public Object execute(ExecutionEvent event) throws ExecutionException {
            if (singleton != null && singleton.isInitialized()) {
                singleton.openRules();
            }
            return null;
        }
    }

    public static class SaveRulesHandler extends AbstractHandler {
        public static final String ID = "com.semmtech.plugin.semmweb.editor.commands.saveRules";

        @Override
        public Object execute(ExecutionEvent event) throws ExecutionException {
            if (singleton != null && singleton.isInitialized()) {
                singleton.save();
            }
            return null;
        }
    }

    public static class SaveAsRulesHandler extends AbstractHandler {
        public static final String ID = "com.semmtech.plugin.semmweb.editor.commands.saveAsRules";

        @Override
        public Object execute(ExecutionEvent event) throws ExecutionException {
            if (singleton != null && singleton.isInitialized()) {
                singleton.saveAs();
            }
            return null;
        }
    }

    public class RuleBody {
        private Rule parent;
        private List<ClauseEntry> body;

        public RuleBody(Rule rule, ClauseEntry[] body) {
            this.body = new ArrayList<>(Arrays.asList(body));
            this.parent = rule;
        }

        public Rule getParent() {
            return parent;
        }

        public List<ClauseEntry> getBody() {
            return body;
        }

        @Override
        public int hashCode() {
            return String.format("HEADOF-" + parent.toString()).hashCode();
        }

        private boolean equals(RuleBody other) {
            return parent.equals(other.getParent());
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            else if (!(obj instanceof RuleBody)) {
                return false;
            }
            else {
                return equals((RuleBody) obj);
            }
        }

        @Override
        public String toString() {
            return "RuleBody of: " + parent.toString();
        }
    }

    public class RuleHead {
        private Rule parent;
        private List<ClauseEntry> head;

        public RuleHead(Rule rule, ClauseEntry[] head) {
            this.head = new ArrayList<>(Arrays.asList(head));
            this.parent = rule;
        }

        public Rule getParent() {
            return parent;
        }

        public List<ClauseEntry> getHead() {
            return head;
        }

        @Override
        public int hashCode() {
            return String.format("BODYOF-" + parent.toString()).hashCode();
        }

        private boolean equals(RuleHead other) {
            return parent.equals(other.getParent());
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            else if (!(obj instanceof RuleHead)) {
                return false;
            }
            else {
                return equals((RuleHead) obj);
            }
        }

        @Override
        public String toString() {
            return "RuleHead of: " + parent.toString();
        }
    }

    public static class TreeRuleDerivation {
        private static int LATEST_ID = 1;
        private int id;
        private RuleDerivation derivation;

        public TreeRuleDerivation(RuleDerivation derivation) {
            this.derivation = derivation;
            this.id = LATEST_ID++;
        }

        public RuleDerivation getDerivation() {
            return derivation;
        }

        private boolean equals(TreeRuleDerivation other) {
            return id == other.id;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            else if (!(obj instanceof TreeRuleDerivation)) {
                return false;
            }
            else {
                return equals((TreeRuleDerivation) obj);
            }
        }
    }

    public static class TreeStatement {
        private static int LATEST_ID = 1;
        private int id;
        private Statement statement;
        private int type = STATEMENT_ASSERTED;

        public static final int STATEMENT_ASSERTED = 1;
        public static final int STATEMENT_IMPORTED = 2;
        public static final int STATEMENT_INFERRED = 4;

        public TreeStatement(Statement statement, int statementType) {
            this.statement = statement;
            this.id = LATEST_ID++;
            this.type = statementType;
        }

        public TreeStatement(Statement statement, boolean imported, boolean inferred) {
            this.statement = statement;
            this.id = LATEST_ID++;
        }

        public int getStatementType() {
            return type;
        }

        public String getOrigin() {
            if (type == STATEMENT_IMPORTED) {
                return "imported";
            }
            else if (type == STATEMENT_INFERRED) {
                return "inferred";
            }
            return "asserted";
        }

        public Statement getStatement() {
            return statement;
        }

        private boolean equals(TreeStatement other) {
            return id == other.id;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            else if (!(obj instanceof TreeStatement)) {
                return false;
            }
            else {
                return equals((TreeStatement) obj);
            }
        }
    }

    public static class TreeTriple {
        private static int LATEST_ID = 1;
        private int id;
        private Triple triple;

        public TreeTriple(Triple triple) {
            this.triple = triple;
            this.id = LATEST_ID++;
        }

        public Triple getTriple() {
            return triple;
        }

        private boolean equals(TreeTriple other) {
            return id == other.id;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            else if (!(obj instanceof TreeTriple)) {
                return false;
            }
            else {
                return equals((TreeTriple) obj);
            }
        }
    }

    public class RulesLabelProvider extends StyledCellLabelProvider {

        private final Styler builtinStyler;

        public RulesLabelProvider(final Font boldFont) {
            builtinStyler = new Styler() {
                @Override
                public void applyStyles(TextStyle textStyle) {
                    textStyle.font = boldFont;
                    textStyle.foreground = Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE);
                }
            };
        }

        @Override
        public void update(ViewerCell cell) {
            Object element = cell.getElement();
            int columnIndex = cell.getColumnIndex();

            StyledString styledText = new StyledString();
            String text = getColumnText(element, columnIndex);
            if (text != null && text.length() > 0) {
                styledText.append(text);
            }

            if (text != null && columnIndex == 1) {
                String[] builtins = new String[] { "tableAll", "strConcat", "uriConcat", "regex",
                        "noValue", "makeTemp" };
                for (String builtin : builtins) {
                    int startIndex = text.indexOf(builtin + "(");
                    while (startIndex > -1) {
                        int length = builtin.length();
                        styledText.setStyle(startIndex, length, builtinStyler);
                        startIndex = text.indexOf(builtin, startIndex + 1);
                    }
                }
            }

            Image columnImage = getColumnImage(element, columnIndex);
            if (columnImage != null) {
                cell.setImage(columnImage);
            }
            cell.setText(styledText.getString());
            cell.setStyleRanges(styledText.getStyleRanges());
            super.update(cell);
        }

        public Image getColumnImage(Object element, int columnIndex) {
            // / TODO: dependent on body or head and type of rule
            if (columnIndex == 0) {
                if (element instanceof Rule) {
                    Rule rule = (Rule) element;
                    if (rule.isBackward()) {
                        return EditorPlugin.getDefault().getImage(
                                EditorPluginImages.IMG_RULE_BACKWARD);
                    }
                    return EditorPlugin.getDefault().getImage(EditorPluginImages.IMG_RULE_FORWARD);
                }
                else if (element instanceof RuleHead) {
                    Rule rule = ((RuleHead) element).getParent();
                    if (rule.isBackward()) {
                        return EditorPlugin.getDefault().getImage(
                                EditorPluginImages.IMG_HEAD_BACKWARD);
                    }
                    return EditorPlugin.getDefault().getImage(EditorPluginImages.IMG_HEAD_FORWARD);
                }
                else if (element instanceof Functor) {
                    return EditorPlugin.getDefault().getImage(EditorPluginImages.IMG_RULE_FUNCTOR);
                }
                else if (element instanceof TriplePattern) {
                    return EditorPlugin.getDefault().getImage(EditorPluginImages.IMG_RULE_TRIPLE);
                }
                else {
                    return null;
                }
            }
            return null;
        }

        private String getText(ClauseEntry entry) {
            if (entry instanceof Functor) {
                return ((Functor) entry).toString();
            }
            else if (entry instanceof TriplePattern) {
                TriplePattern triple = (TriplePattern) entry;
                return "(" + (PrintUtil.print(triple.getSubject())) + " "
                        + (PrintUtil.print(triple.getPredicate())) + " "
                        + (PrintUtil.print(triple.getObject())) + ")";
            }
            else if (entry instanceof Rule) {
                return ((Rule) entry).toString();
            }
            return "";
        }

        private String getText(List<ClauseEntry> entries) {
            String result = "";
            for (ClauseEntry entry : entries) {
                result += ((result == "") ? "" : ", ") + getText(entry);
            }
            return result;
        }

        public String getColumnText(Object element, int columnIndex) {
            if (columnIndex == 0) {
                if (element instanceof Rule) {
                    return ((Rule) element).getName();
                }
                else if (element instanceof RuleHead) {
                    return "head";
                }
                else if (element instanceof RuleBody) {
                    return "body";
                }
            }
            else if (columnIndex == 1) {
                if (element instanceof Rule) {
                    return ((Rule) element).toString();
                }
                else if (element instanceof ClauseEntry) {
                    return getText((ClauseEntry) element);
                }
                else if (element instanceof RuleHead) {
                    return getText(((RuleHead) element).getHead());
                }
                else if (element instanceof RuleBody) {
                    return getText(((RuleBody) element).getBody());
                }
                else {
                    return element.toString();
                }
            }
            else if (columnIndex == 2) {
                if (element instanceof Rule) {
                    Rule rule = (Rule) element;
                    if (rule.isAxiom()) {
                        return "axiom";
                    }
                    else if (rule.isBackward()) {
                        return "backward";
                    }
                    else if (rule.isMonotonic()) {
                        return "monotonic";
                    }
                }
                else if (element instanceof Functor) {
                    return "functor";
                }
                else if (element instanceof TriplePattern) {
                    return "triple";
                }
                else if (element instanceof RuleHead) {
                    return "head";
                }
                else if (element instanceof RuleBody) {
                    return "body";
                }
            }
            return null;
        }

    }

    public static FontData[] getModifiedFontData(FontData[] originalData, int additionalStyle) {
        FontData[] styleData = new FontData[originalData.length];
        for (int i = 0; i < styleData.length; i++) {
            FontData base = originalData[i];
            styleData[i] = new FontData(base.getName(), base.getHeight(), base.getStyle()
                    | additionalStyle);
        }
        return styleData;
    }

    public static boolean sameDerivations(RuleDerivation derivA, RuleDerivation derivB) {
        if (derivA == null || derivB == null) {
            return false;
        }
        Rule ruleA = derivA.getRule();
        Rule ruleB = derivA.getRule();

        if (ruleA == null || ruleB == null) {
            return false;
        }

        if (!ruleA.equals(ruleB)) {
            return false;
        }

        if (!derivA.getConclusion().equals(derivB.getConclusion())) {
            return false;
        }

        for (Triple t : derivA.getMatches()) {
            if (!derivB.getMatches().contains(t)) {
                return false;
            }
        }

        return true;
    }

    private List<RuleDerivation> findDerivation(Statement statement) {
        if (!derivations.containsKey(statement)) {
            List<RuleDerivation> derivs = Lists.newArrayList();
            for (Iterator<Derivation> iter = infModel.getDerivation(statement); iter.hasNext();) {
                RuleDerivation deriv = (RuleDerivation) iter.next();
                boolean duplicate = false;
                for (RuleDerivation d : derivs) {
                    duplicate = sameDerivations(deriv, d);
                    if (duplicate) {
                        break;
                    }
                }
                if (!duplicate) {
                    derivs.add(deriv);
                }
            }
            derivations.put(statement, derivs);
            return derivs;
        }
        return derivations.get(statement);
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        String property = event.getProperty();
        logger.debug("propertyChange of " + property);
        if (property.equals(RuleReasonerViewPreferenceConstants.PREFERENCE_SHOW_BASE_MODEL)
                || property
                        .equals(RuleReasonerViewPreferenceConstants.PREFERENCE_SHOW_IMPORTED_MODEL)) {
            refreshResultViewer();
        }
    }

    private void refreshResultViewer() {
        try {
            visibleTreeStatements = Lists.newArrayList();
            if (infModel != null) {
                for (Statement statement : infModel.listStatements().toList()) {
                    int type = TreeStatement.STATEMENT_INFERRED;
                    boolean visible = true;
                    if (baseModel.isInBaseModel(statement)) {
                        type = TreeStatement.STATEMENT_ASSERTED;
                        visible = RuleReasonerViewPreference.showBaseModel();
                    }
                    else if (baseModel.listStatements().toSet().contains(statement)) {
                        type = TreeStatement.STATEMENT_IMPORTED;
                        visible = RuleReasonerViewPreference.showImportedModel();
                    }
                    else {
                        type = TreeStatement.STATEMENT_INFERRED;
                        visible = true;
                    }
                    if (visible) {
                        TreeStatement element = new TreeStatement(statement, type);
                        visibleTreeStatements.add(element);
                    }
                }
                if (comparator != null) {
                    Collections.sort(visibleTreeStatements, comparator.getInnerComparator());
                }
                resultViewer.setInput(infModel);
            }
            else {
                resultViewer.setInput(null);
            }
            resultViewer.getTree().setItemCount(visibleTreeStatements.size());
        }
        catch (Exception ex) {
            MessageDialog.openError(getSite().getShell(), "Error", ex.getMessage());
        }
    }

    public static class ToggleTraceOnHandler extends PreferenceStoreToggleHandler {
        public static final String ID = "com.semmtech.plugin.semmweb.editor.commands.ruleReasoner.toggleTraceOn";

        public ToggleTraceOnHandler() {
            super(RuleReasonerViewPreference.getPreferenceStore(),
                    RuleReasonerViewPreferenceConstants.PREFERENCE_REASONER_TRACE_ON);
        }
    }

    public static class ToggleDerivationLoggingHandler extends PreferenceStoreToggleHandler {
        public static final String ID = "com.semmtech.plugin.semmweb.editor.commands.ruleReasoner.toggleDerivationLogging";

        public ToggleDerivationLoggingHandler() {
            super(RuleReasonerViewPreference.getPreferenceStore(),
                    RuleReasonerViewPreferenceConstants.PREFERENCE_REASONER_DERIVATION_LOGGING);
        }
    }

    public static class ToggleShowBaseModelHandler extends PreferenceStoreToggleHandler {
        public static final String ID = "com.semmtech.plugin.semmweb.editor.commands.ruleReasoner.toggleShowBaseModel";

        public ToggleShowBaseModelHandler() {
            super(RuleReasonerViewPreference.getPreferenceStore(),
                    RuleReasonerViewPreferenceConstants.PREFERENCE_SHOW_BASE_MODEL);
        }
    }

    public static class ToggleShowImportedModelHandler extends PreferenceStoreToggleHandler {
        public static final String ID = "com.semmtech.plugin.semmweb.editor.commands.ruleReasoner.toggleShowImportedModel";

        public ToggleShowImportedModelHandler() {
            super(RuleReasonerViewPreference.getPreferenceStore(),
                    RuleReasonerViewPreferenceConstants.PREFERENCE_SHOW_IMPORTED_MODEL);
        }
    }

    class TreeStatementViewerComparator extends ViewerComparator {
        private int propertyIndex;
        private static final int DESCENDING = 1;
        private int direction = DESCENDING;
        private Comparator<TreeStatement> inner;
        private LabelProvider labelProvider;

        public TreeStatementViewerComparator() {
            this.propertyIndex = 0;
            direction = DESCENDING;

            labelProvider = new SimpleRDFNodeLabelProvider();
            inner = new Comparator<TreeStatement>() {
                @Override
                public int compare(TreeStatement t1, TreeStatement t2) {
                    return compareStatements(t1, t2);
                }
            };
        }

        public Comparator<TreeStatement> getInnerComparator() {
            return inner;
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

        protected int compareStatements(TreeStatement t1, TreeStatement t2) {
            Statement s1 = t1.getStatement();
            Statement s2 = t2.getStatement();
            String name1;
            String name2;
            int rc = 0;
            switch (propertyIndex) {
            case 0: // / rdf:subject
                name1 = labelProvider.getText(s1.getSubject());
                name2 = labelProvider.getText(s2.getSubject());
                rc = name1.compareToIgnoreCase(name2);
                break;
            case 1: // / rdf:predicate
                name1 = labelProvider.getText(s1.getPredicate());
                name2 = labelProvider.getText(s2.getPredicate());
                rc = name1.compareToIgnoreCase(name2);
                break;
            case 2: // / rdf:object
                name1 = labelProvider.getText(s1.getObject());
                name2 = labelProvider.getText(s2.getObject());
                rc = name1.compareToIgnoreCase(name2);
                break;
            case 3: // / Origin
                name1 = t1.getOrigin();
                name2 = t2.getOrigin();
                rc = name1.compareToIgnoreCase(name2);
                break;
            default:
                rc = 0;
            }
            // If descending order, flip the direction
            if (direction == DESCENDING) {
                rc = -rc;
            }
            return rc;
        }

        @Override
        public int compare(Viewer viewer, Object e1, Object e2) {
            TreeStatement s1 = (TreeStatement) e1;
            TreeStatement s2 = (TreeStatement) e2;
            return compareStatements(s1, s2);
        }
    }

    @Override
    protected void cleanup() {
        if (derivations != null) {
            derivations.clear();
        }

        if (parsedRules != null) {
            parsedRules.clear();
        }

        if (visibleTreeStatements != null) {
            visibleTreeStatements.clear();
        }
    }

}
