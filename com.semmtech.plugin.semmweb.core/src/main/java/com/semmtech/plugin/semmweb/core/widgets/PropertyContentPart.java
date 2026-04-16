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


import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.topbraid.spin.vocabulary.SP;

import com.google.common.collect.Lists;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.rulesys.GenericRuleReasoner;
import com.hp.hpl.jena.reasoner.rulesys.Rule;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.CorePluginImages;
import com.semmtech.plugin.semmweb.core.dialog.ResourceSelectionDialog;
import com.semmtech.plugin.semmweb.core.dnd.LiteralTransfer;
import com.semmtech.plugin.semmweb.core.dnd.ResourceArrayListTransfer;
import com.semmtech.plugin.semmweb.core.dnd.ResourceTransfer;
import com.semmtech.plugin.semmweb.core.forms.editor.AbstractModelResourceContent;
import com.semmtech.plugin.semmweb.core.model.ModelTransaction;
import com.semmtech.plugin.semmweb.core.model.ResourceArrayList;
import com.semmtech.plugin.semmweb.core.model.ResourceEditorClassPreferences;
import com.semmtech.plugin.semmweb.core.model.ResourceStatements;
import com.semmtech.plugin.semmweb.core.preferences.ResourceEditorPreference;
import com.semmtech.plugin.semmweb.core.ui.forms.EditorFormColors;
import com.semmtech.plugin.semmweb.core.ui.forms.IEditorFormColors;
import com.semmtech.plugin.semmweb.core.util.RulesUtil;
import com.semmtech.plugin.semmweb.core.viewers.ModelNodeLabelProvider;
import com.semmtech.plugin.semmweb.core.wizards.CreateResourceWizard;
import com.semmtech.semantics.util.JenaUtil;
import com.semmtech.ui.plugin.decorators.OverlayImageIcon;


/**
 * 
 * @author Sander Stolk
 */
public class PropertyContentPart extends AbstractModelResourceContentPart {
    private static final Logger logger = Logger.getLogger(PropertyContentPart.class);
    private Property property;
    private Resource range = RDFS.Resource;
    private Resource domain = null;
    private boolean restrictionsOnly = false;

    // protected FormToolkit toolkit;
    protected FormColors formColors;

    // private Composite parent;
    private ExpandableComposite expandableComposite;
    private Composite clientComposite;
    private Composite textComposite;
    private ToolBar toolbar;
    private ToolItem menuButton;
    private MenuManager menuManager;

    @SuppressWarnings("unused")
    private ResourceEditorClassPreferences preferences;
    private List<Resource> localTypes;
    private List<AbstractPropertyObjectContentPart> parts;
    private List<Statement> statements;
    private Label textLabel;

    private final List<RDFNode> discardObjectsOnRefresh;

    public PropertyContentPart(AbstractModelResourceContent contentParent, Composite parent,
            FormToolkit toolkit, Property property) {
        super(contentParent, parent, toolkit);
        this.property = property;

        discardObjectsOnRefresh = Lists.newArrayList();

        initialize();
        createContent();
    }

    private void initialize() {
        Resource subject = getResource();
        OntModel model = getModelProvider().getOntModel();

        // TODO: Not optimal costs are great! Use a generalized object for
        // storing hierarchy information
        List<Rule> rules = RulesUtil.parseRulesFromBundle(CorePlugin.PLUGIN_ID,
                "src/main/resources/rules/", "basic-taxonomy.rules");
        Reasoner reasoner = new GenericRuleReasoner(rules);
        InfModel hierarchyModel = ModelFactory.createInfModel(reasoner, model);
        OntModel ontModel = ModelFactory.createOntologyModel(OntModelSpec.RDFS_MEM_RDFS_INF,
                hierarchyModel);

        preferences = ResourceEditorPreference.fromProject(contentParent.getProject())
                .getResourceEditorPreferences();

        localTypes = Lists.newArrayList();
        if (subject.hasProperty(RDF.type)) {
            StmtIterator listpropertiesIterator = subject.listProperties(RDF.type);
            while (listpropertiesIterator.hasNext()) {
                Statement nextStmt = listpropertiesIterator.next();
                RDFNode typeNode = nextStmt.getObject();
                if (typeNode.isResource()) {
                    localTypes.add(typeNode.asResource());
                    OntResource ontResource = ontModel.getOntResource(typeNode.asResource());
                    if (ontResource.isClass()) {
                        for (OntClass c : (ontResource.as(OntClass.class)).listSuperClasses()
                                .toList()) {
                            localTypes.add(c.asResource());
                        }
                    }
                }
            }
        }

        parts = Lists.newArrayList();
        if (property.hasProperty(RDFS.range)) {
            range = property.getPropertyResourceValue(RDFS.range);
        }
        else {
            range = RDFS.Resource;
        }

        if (property.hasProperty(RDFS.domain)) {
            domain = property.getPropertyResourceValue(RDFS.domain);
        }
        else {
            domain = RDFS.Resource;
        }
    }

    public boolean isEmpty() {
        return parts.isEmpty();
    }

    public void setRestrictionsOnly(boolean restrictionsOnly) {
        this.restrictionsOnly = restrictionsOnly;
    }

    public Property getProperty() {
        return property;
    }

    protected String getPropertyTitle() {
        return getModelProvider().getLabelProvider().getText(getProperty());
    }

    protected void updatePropertyTitle(int count) {
        if (textLabel != null && !textLabel.isDisposed()) {
            if (count > 0) {
                textLabel.setText(String.format("%s (%d)", getPropertyTitle(), count));
            }
            else {
                textLabel.setText(getPropertyTitle());
            }
        }
    }

    private void createContent() {
        GridLayoutFactory.fillDefaults().margins(0, 0).spacing(0, 0).applyTo(this);

        formColors = new EditorFormColors(Display.getCurrent());
        // toolkit = new FormToolkit(formColors);

        setMenu(null);
        expandableComposite = toolkit.createExpandableComposite(this, ExpandableComposite.TITLE_BAR
                | ExpandableComposite.LEFT_TEXT_CLIENT_ALIGNMENT);
        GridDataFactory.fillDefaults().grab(true, false).indent(0, 0).applyTo(expandableComposite);
        TableWrapLayout layout = new TableWrapLayout();
        layout.topMargin = 1;
        layout.rightMargin = 1;
        layout.bottomMargin = 1;
        layout.leftMargin = 1;
        layout.verticalSpacing = 1;
        layout.horizontalSpacing = 1;
        expandableComposite.setLayout(layout);

        expandableComposite.setExpanded(true);
        expandableComposite.marginHeight = 0;
        expandableComposite.marginWidth = 0;
        expandableComposite.clientVerticalSpacing = 0;
        expandableComposite.titleBarTextMarginWidth = 0;
        expandableComposite.descriptionVerticalSpacing = 0;

        textComposite = toolkit.createComposite(expandableComposite, SWT.NONE);
        layout = new TableWrapLayout();
        layout.numColumns = 2;
        layout.topMargin = 1;
        layout.rightMargin = 4;
        layout.bottomMargin = 5;
        layout.leftMargin = 1;
        layout.verticalSpacing = 1;
        layout.horizontalSpacing = 0;
        textComposite.setLayout(layout);

        TableWrapData layoutData = new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.TOP);
        layoutData.indent = 0;
        textComposite.setLayoutData(layoutData);

        toolkit.paintBordersFor(textComposite);

        textLabel = toolkit.createLabel(textComposite, getPropertyTitle(), SWT.NONE);
        textLabel.setLayoutData(new TableWrapData(TableWrapData.LEFT, TableWrapData.MIDDLE));
        textLabel.setForeground(formColors.getColor(IEditorFormColors.PROPERTY_PART_FOREGROUND));

        FontData[] fontData = textLabel.getFont().getFontData();
        fontData[0].setStyle(SWT.BOLD);
        final Font boldFont = new Font(Display.getCurrent(), fontData);
        textLabel.setFont(boldFont);
        textLabel.addDisposeListener(new DisposeListener() {
            @Override
            public void widgetDisposed(DisposeEvent e) {
                boldFont.dispose();
            }
        });
        textLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDoubleClick(MouseEvent e) {
                expandableComposite.setExpanded(!expandableComposite.isExpanded());
                refresh();
            }
        });

        expandableComposite.setTextClient(textComposite);

        clientComposite = toolkit.createComposite(expandableComposite, SWT.NONE);

        layout = new TableWrapLayout();
        layout.verticalSpacing = 1;
        layout.horizontalSpacing = 1;
        layout.leftMargin = 5;
        layout.topMargin = 0;
        layout.bottomMargin = 1;
        layout.rightMargin = 1;
        clientComposite.setLayout(layout);

        layoutData = new TableWrapData(TableWrapData.LEFT, TableWrapData.TOP);
        layoutData.align = 0;
        layoutData.grabVertical = true;
        layoutData.grabHorizontal = true;
        clientComposite.setLayoutData(layoutData);

        expandableComposite.setClient(clientComposite);
        toolkit.paintBordersFor(clientComposite);

        DropTarget dropTarget = new DropTarget(expandableComposite, DND.DROP_MOVE | DND.DROP_COPY);
        dropTarget.setTransfer(new Transfer[] { ResourceTransfer.getInstance(),
                ResourceArrayListTransfer.getInstance(), LiteralTransfer.getInstance() });
        dropTarget.addDropListener(new DropTargetAdapter() {
            private boolean invalidResource;
            private boolean requiresRestriction = false;

            @Override
            public void drop(DropTargetEvent event) {
                if (ResourceArrayListTransfer.getInstance().isSupportedType(event.currentDataType)) {
                    ResourceArrayList list = (ResourceArrayList) event.data;

                    // Create the statements, which will automatically
                    // update/refresh the part.
                    String transactionDescription = "Due to multiple drops on a property";
                    ModelTransaction transaction = getModelProvider().createTransaction(
                            transactionDescription);
                    ResourceStatements.createResourcePropertyStatements(getResource(),
                            getProperty(), list);
                    getModelProvider().commitTransaction(transaction);
                }
                else if (ResourceTransfer.getInstance().isSupportedType(event.currentDataType)) {
                    if (!invalidResource) {
                        if (requiresRestriction || restrictionsOnly) {
                            // / TODO
                        }
                        else {
                            PropertyStatementContentPart part = createPropertyStatementContentPart(
                                    (Resource) event.data, true);
                            part.confirmEdit();
                        }
                    }
                }
                else if (LiteralTransfer.getInstance().isSupportedType(event.currentDataType)) {
                    Literal literal = (Literal) event.data;
                    PropertyStatementContentPart part = createPropertyStatementContentPart(literal,
                            true);
                    part.confirmEdit();
                }
                refresh();
            }
        });

        createToolBar();
    }

    public PropertyStatementContentPart createPropertyStatementContentPart(RDFNode node,
            boolean newStatement) {
        return createPropertyStatementContentPart(node, range, newStatement);
    }

    public PropertyStatementContentPart createPropertyStatementContentPart(RDFNode node,
            Resource allowedRange, boolean newStatement) {

        if (allowedRange == null) {
            allowedRange = range;
        }

        PropertyStatementContentPart part = (PropertyStatementContentPart) getPartToBeDiscarded(PropertyStatementContentPart.class);
        if (part != null) {
            discardObjectsOnRefresh.remove(part.getObject());
            part.alterContent(allowedRange, node, newStatement);
        }
        else {
            part = new PropertyStatementContentPart(contentParent, clientComposite, toolkit,
                    property, allowedRange, node, newStatement);
            part.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.TOP));

            part.setCloseListener(new Listener() {
                @Override
                public void handleEvent(Event event) {
                    if (event.data != null) {
                        PropertyStatementContentPart part = (PropertyStatementContentPart) event.data;
                        part.dispose();
                        parts.remove(part);
                        updatePropertyTitle(parts.size());
                    }
                }
            });
            parts.add(part);
            updatePropertyTitle(parts.size());
            if (newStatement) {
                part.setFocus();
            }
        }
        return part;
    }

    public PropertyListContentPart createPropertyList(Resource list) {
        PropertyListContentPart part = (PropertyListContentPart) getPartToBeDiscarded(PropertyListContentPart.class);
        if (part != null) {
            discardObjectsOnRefresh.remove(part.getObject());
            part.alterContent(list);
        }
        else {
            part = new PropertyListContentPart(contentParent, clientComposite, toolkit, property,
                    list);
            part.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.TOP));

            part.setCloseListener(new Listener() {
                @Override
                public void handleEvent(Event event) {
                    if (event.data != null) {
                        PropertyListContentPart part = (PropertyListContentPart) event.data;
                        part.dispose();
                        parts.remove(part);
                        updatePropertyTitle(parts.size());
                    }
                }
            });
            parts.add(part);
            updatePropertyTitle(parts.size());
        }
        return part;
    }

    public QueryContentPart createQueryContentPart(Resource query, boolean newQuery) {
        return createQueryContentPart(query, range, newQuery);
    }

    public QueryContentPart createQueryContentPart(Resource query, Resource range, boolean newQuery) {
        QueryContentPart part = (QueryContentPart) getPartToBeDiscarded(QueryContentPart.class);
        if (part != null) {
            discardObjectsOnRefresh.remove(part.getObject());
            part.alterContent(query);
        }
        else {
            part = new QueryContentPart(contentParent, clientComposite, toolkit, property, query);
            part.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.TOP));

            // TODO: Why doesn't setCloseListener work?
            // part.setCloseListener(new Listener() {
            // @Override
            // public void handleEvent(Event event) {
            // if (event.data != null) {
            // QueryContentPart part = (QueryContentPart) event.data;
            // part.dispose();
            // parts.remove(part);
            // updatePropertyTitle(parts.size());
            // }
            // }
            // });
            parts.add(part);
            updatePropertyTitle(parts.size());
        }
        return part;
    }

    public List<RDFNode> listCurrentObjects() {
        List<RDFNode> result = Lists.newArrayList();
        for (AbstractPropertyObjectContentPart part : parts) {
            if (part.getObject() != null) {
                result.add(part.getObject());
            }
        }
        return result;
    }

    public boolean updateObject(RDFNode node) {
        boolean updated = false;
        for (AbstractPropertyObjectContentPart part : parts) {
            if (part.getObject() != null && part.getObject().equals(node)) {
                updated = part.updateContent() || updated;
            }
        }
        return updated;
    }

    public void discardObjects(List<RDFNode> objects) {
        discardObjectsOnRefresh.addAll(objects);
    }

    private void createToolBar() {
        toolbar = new ToolBar(textComposite, SWT.HORIZONTAL | SWT.FLAT);

        TableWrapData layoutData = new TableWrapData(TableWrapData.LEFT, TableWrapData.TOP, 1, 1);
        layoutData.indent = 3;
        toolbar.setLayoutData(layoutData);
        toolkit.adapt(toolbar);
        toolkit.paintBordersFor(toolbar);

        menuManager = createGeneralMenuManager();
        menuButton = new ToolItem(toolbar, SWT.FLAT);
        menuButton.setImage(CorePlugin.getDefault().getImage(CorePluginImages.IMG_ARROW_DOWN));
        menuButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Shell shell = getShell();
                Menu menu = menuManager.createContextMenu(shell);
                shell.setMenu(menu);
                final ToolItem toolItem = (ToolItem) e.widget;
                final ToolBar toolBar = toolItem.getParent();
                Point point = toolBar.toDisplay(new Point(e.x, e.y + toolItem.getBounds().height));
                menu.setLocation(point.x, point.y);
                menu.setVisible(true);
            }
        });
    }

    private MenuManager createGeneralMenuManager() {
        final ModelNodeLabelProvider provider = getModelProvider().getLabelProvider();
        MenuManager manager = new MenuManager();

        if (!localTypes.contains(domain)) {
            Action warningAction = new Action() {
                @Override
                public void run() {
                    MessageDialog
                            .openInformation(
                                    getShell(),
                                    "Illegal Domain",
                                    "The domain for the current property '"
                                            + provider.getText(property)
                                            + "' is set to '"
                                            + provider.getText(domain)
                                            + "'. However the current resource does not appear to be a member of that particular domain!");
                }
            };
            warningAction.setText("Warning");
            warningAction.setImageDescriptor(ImageDescriptor.createFromImage(CorePlugin
                    .getDefault().getImage(CorePluginImages.IMG_WARNING_BIG)));
            manager.add(warningAction);
            manager.add(new Separator());
        }

        MenuManager addMenu = new MenuManager("Add");
        manager.add(addMenu);

        if (checkAllowed()) {
            boolean menuSeparatorNeeded = false;
            if (!isLiteralStatement() || range.equals(RDFS.Resource)) {
                if (menuSeparatorNeeded == true) {
                    addMenu.add(new Separator());
                }

                String imageKey = provider.getInstanceImageKey(range);

                // Create empty resource statement
                Action emptyAddAction = new Action() {
                    @Override
                    public void run() {
                        createEmptyStatement();
                    }
                };
                emptyAddAction.setText("Resource");
                if (imageKey != null) {
                    Image baseImage = CorePlugin.getDefault().getImage(imageKey);
                    OverlayImageIcon icon = new OverlayImageIcon(baseImage, CorePlugin.getDefault());
                    icon.addImageDecoration(CorePluginImages.IMG_OVERLAY_ADD,
                            OverlayImageIcon.TOP_RIGHT);
                    Image image = icon.createImage();
                    emptyAddAction.setImageDescriptor(ImageDescriptor.createFromImage(image));
                }
                addMenu.add(emptyAddAction);

                // Browse resource action
                Action addAction = new Action() {
                    @Override
                    public void run() {
                        browseForObject(range);
                    }
                };

                addAction.setText("Browse...");
                if (imageKey != null) {
                    addAction.setImageDescriptor(ImageDescriptor.createFromImage(CorePlugin
                            .getDefault().getImage(imageKey)));
                }
                addMenu.add(addAction);

                // Create new resource action
                Action createAction = new Action() {
                    @Override
                    public void run() {
                        createResource(range);
                    }
                };
                createAction.setText("Create...");
                if (imageKey != null) {
                    Image baseImage = CorePlugin.getDefault().getImage(imageKey);
                    OverlayImageIcon icon = new OverlayImageIcon(baseImage, CorePlugin.getDefault());
                    icon.addImageDecoration(CorePluginImages.IMG_OVERLAY_ADD,
                            OverlayImageIcon.TOP_RIGHT);
                    Image image = icon.createImage();
                    createAction.setImageDescriptor(ImageDescriptor.createFromImage(image));
                }
                addMenu.add(createAction);

                menuSeparatorNeeded = true;
            }

            if (range.equals(RDF.List)) {
                if (menuSeparatorNeeded == true) {
                    addMenu.add(new Separator());
                }

                Action createListAction = new Action() {
                    @Override
                    public void run() {
                        Resource list = getModelProvider().getOntModel().createResource();
                        createPropertyList(list);
                        refresh();
                    }
                };
                createListAction.setText("Empty List");
                createListAction.setImageDescriptor(CorePlugin.getDefault().getImageDescriptor(
                        CorePluginImages.IMG_RDF_LIST_ADD));
                addMenu.add(createListAction);

                menuSeparatorNeeded = true;
            }

            if (range.equals(SP.Command)
                    || JenaUtil.asOntClass(range, getModelProvider().getOntModel()).hasSuperClass(
                            SP.Command, false)) {
                if (menuSeparatorNeeded == true) {
                    addMenu.add(new Separator());
                }

                Action createQueryAction = new Action() {
                    @Override
                    public void run() {
                        createNewQuery();
                    }
                };
                createQueryAction.setText("New Query");
                createQueryAction.setImageDescriptor(CorePlugin.getDefault().getImageDescriptor(
                        CorePluginImages.IMG_SPARQL));
                addMenu.add(createQueryAction);

                menuSeparatorNeeded = true;
            }

            if (isLiteralStatement() || range.equals(RDFS.Resource)) {
                if (menuSeparatorNeeded == true) {
                    addMenu.add(new Separator());
                }

                Action createLiteralStatementAction = new Action() {
                    @Override
                    public void run() {
                        createNewLiteralStatement();
                    }
                };
                createLiteralStatementAction.setText("Literal");
                createLiteralStatementAction.setImageDescriptor(CorePlugin.getDefault()
                        .getImageDescriptor(CorePluginImages.IMG_XSD_ADD));
                addMenu.add(createLiteralStatementAction);

                menuSeparatorNeeded = true;
            }
        }

        MenuManager toolMenu = new MenuManager("Tools");
        Action infoAction = new Action() {
            @Override
            public void run() {
                CorePlugin.getDefault().openResource(property);
            }
        };
        infoAction.setText("Property Details");
        toolMenu.add(infoAction);
        manager.add(toolMenu);
        manager.add(new Separator());

        Action showAction = new Action() {
            @Override
            public void run() {
                expandableComposite.setExpanded(true);
                refresh();
            }
        };
        showAction.setText("Show");
        manager.add(showAction);

        Action hideAction = new Action() {
            @Override
            public void run() {
                expandableComposite.setExpanded(false);
                refresh();
            }
        };
        hideAction.setText("Hide");
        manager.add(hideAction);

        manager.add(new Separator());
        Action removeAll = new Action() {
            @Override
            public void run() {
                deleteAllStatements();
            }
        };
        removeAll.setText("Delete All");
        removeAll.setImageDescriptor(ImageDescriptor.createFromImage(CorePlugin.getDefault()
                .getImage(CorePluginImages.IMG_MULTIPLE_STATEMENTS_DELETE)));
        manager.add(removeAll);

        return manager;
    }

    protected void createMultipleStatements() {
        int count = 3;
        for (int i = 0; i < count; i++) {
            createPropertyStatementContentPart(null, false);
            refresh();
        }
    }

    @SuppressWarnings("unused")
    private void createAnonymous(Resource range) {
        ResourceSelectionDialog dialog = new ResourceSelectionDialog(getShell(), "Select Type",
                "Select the type for nested resource.");
        dialog.clearHierarchicalProperties();
        dialog.clearRootResources();
        dialog.clearExcludedResources();

        dialog.addHierarchicalProperties(new Property[] { RDFS.subClassOf, RDF.type });
        dialog.addRootResources(new Resource[] { range });
        dialog.setAllowedResourceTypes(new Resource[] { RDFS.Class, OWL.Class });

        dialog.setModel(getModelProvider().getOntModel());

        if (dialog.open() == Window.OK) {
            Resource type = dialog.getFirstSelectedResource();

            String transactionDescription = "Changed due to creation of a nested anonymous";
            ModelTransaction transaction = getModelProvider().createTransaction(
                    transactionDescription);
            Resource subject = getResource();

            Resource anonymous = createAnonymousResourceStatements(type, getResource());

            AnonymousResourceContentPart part = new AnonymousResourceContentPart(contentParent,
                    clientComposite, toolkit, property, anonymous);
            part.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.TOP));
            refresh();

            getModelProvider().commitTransaction(transaction);
        }
    }

    private Resource createAnonymousResourceStatements(Resource type, Resource subject) {
        Resource anonymous = getModelProvider().getOntModel().createResource();
        anonymous.addProperty(RDF.type, type);
        anonymous.addProperty(RDFS.label, "Nested Anonymous");
        anonymous.addProperty(RDFS.comment,
                "This is a nested anonymous resource created to test...");
        subject.addProperty(property, anonymous);
        return anonymous;
    }

    private boolean isLiteralStatement() {
        if (range == null) {
            return false;
        }
        if (range.isAnon()) {
            return false;
        }
        if (range.equals(RDFS.Literal)
                || range.getURI().equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#PlainLiteral")
                || range.getURI().equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#XMLLiteral")) {
            return true;
        }
        return false;
    }

    @SuppressWarnings("static-method")
    private boolean checkAllowed() {
        return true;
    }

    public void createEmptyStatement() {
        createPropertyStatementContentPart(null, false);
        refresh();
    }

    private void createResource(Resource type) {
        CreateResourceWizard wizard = new CreateResourceWizard("New Resource", getModelProvider(),
                type);
        wizard.setAnonymousAllowed(false);
        wizard.setAllowOpenEditorOnFinish(false);
        wizard.setLabelProperty(RDFS.label);

        WizardDialog dialog = new WizardDialog(getShell(), wizard);
        dialog.create();

        String transactionDescription = "Created a new resource and added it to the property";
        ModelTransaction transaction = getModelProvider().createTransaction(transactionDescription);
        if (dialog.open() != Window.OK) {
            getModelProvider().abortTransaction(transaction);
        }
        else {
            PropertyStatementContentPart propertyStatement = createPropertyStatementContentPart(
                    wizard.getResource(), false);
            propertyStatement.createUpdatePropertyStatementStatements();

            getModelProvider().commitTransaction(transaction);
            refresh();
        }
    }

    private void browseForObject(Resource dialogRange) {
        ResourceSelectionDialog dialog = new ResourceSelectionDialog(getShell(), "Select Resource",
                "Select a resource from the list below.");
        dialog.clearHierarchicalProperties();
        dialog.clearRootResources();
        dialog.clearExcludedResources();
        dialog.addHierarchicalProperties(new Property[] { RDF.type, RDFS.subClassOf });
        dialog.addRootResources(new Resource[] { dialogRange });
        dialog.setAllowedResourceTypes(new Resource[] { dialogRange });

        dialog.setModel(getModelProvider().getOntModel());

        if (dialog.open() == Window.OK) {
            Resource resource = dialog.getFirstSelectedResource();
            if (resource != null) {
                PropertyStatementContentPart newPart = createPropertyStatementContentPart(resource,
                        true);
                newPart.confirmEdit();
                refresh();
            }
        }
    }

    public int findStatements(boolean refresh) {
        if (statements == null || refresh) {
            statements = Lists.newArrayList();
            Resource subject = getResource();
            Property predicate = getProperty();
            for (Statement statement : subject.listProperties(predicate).toList()) {
                statements.add(statement);
            }
        }
        return statements.size();
    }

    private void deleteAllStatements() {
        List<Statement> statements = getResource().listProperties(property).toList();
        int count = statements.size();
        if (count > 0) {
            String message = "Are you sure you want to delete all asserted statements for property "
                    + getModelProvider().getLabelProvider().getText(property) + "?";
            if (MessageDialog.openQuestion(getShell(), "Delete All", message)) {
                String transactionDescription = "Remove all statements";
                ModelTransaction transaction = getModelProvider().createTransaction(
                        transactionDescription);
                getModelProvider().getOntModel().remove(statements);

                // Remove form parts
                for (int i = parts.size() - 1; i >= 0; i--) {
                    AbstractPropertyObjectContentPart part = parts.get(i);
                    part.dispose();
                    parts.remove(i);
                }

                getModelProvider().commitTransaction(transaction);
                refresh();
            }
        }
    }

    public void createNewLiteralStatement() {
        createPropertyStatementContentPart(null, RDFS.Literal, true);
        refresh();
    }

    public void createNewQuery() {
        createQueryContentPart(null, true);
        refresh();
    }

    public AnonymousResourceContentPart createAnonymousResource(Resource anonymous) {
        AnonymousResourceContentPart part = (AnonymousResourceContentPart) getPartToBeDiscarded(AnonymousResourceContentPart.class);
        if (part != null) {
            discardObjectsOnRefresh.remove(part.getObject());
            part.alterContent(anonymous);
        }
        else {
            part = new AnonymousResourceContentPart(contentParent, clientComposite, toolkit,
                    property, anonymous);
            part.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.TOP));

            // part.setCloseListener(new Listener() {
            // @Override
            // public void handleEvent(Event event) {
            // if (event.data != null) {
            // PropertyStatementContentPart part =
            // (PropertyStatementContentPart)
            // event.data;
            // part.dispose();
            // parts.remove(part);
            // updatePropertyTitle(parts.size());
            // }
            // }
            // });
            parts.add(part);
            updatePropertyTitle(parts.size());
        }
        return part;
    }

    private AbstractPropertyObjectContentPart getPartToBeDiscarded(Class<?> partClass) {
        for (AbstractPropertyObjectContentPart part : parts) {
            if (discardObjectsOnRefresh.contains(part.getObject()) && partClass.isInstance(part)) {
                return part;
            }
        }
        return null;
    }

    /**
     * Refreshes the composite (layout every descendant).
     */
    @Override
    public void refresh() {
        List<AbstractPropertyObjectContentPart> partsToDispose = Lists.newArrayList();
        for (AbstractPropertyObjectContentPart part : parts) {
            if (discardObjectsOnRefresh.contains(part.getObject())) {
                partsToDispose.add(part);
            }
        }
        for (AbstractPropertyObjectContentPart part : partsToDispose) {
            parts.remove(part);
            part.dispose();
        }
        discardObjectsOnRefresh.clear();
        updatePropertyTitle(parts.size());

        logger.debug("(" + getResource().toString() + ") refresh called!");
        super.refresh();
    }
}
