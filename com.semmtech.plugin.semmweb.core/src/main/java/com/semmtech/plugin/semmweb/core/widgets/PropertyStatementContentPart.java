package com.semmtech.plugin.semmweb.core.widgets;


import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.window.DefaultToolTip;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.eclipse.ui.themes.ITheme;
import org.eclipse.ui.themes.IThemeManager;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.XSD;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.CorePluginImages;
import com.semmtech.plugin.semmweb.core.dialog.ResourceSelectionDialog;
import com.semmtech.plugin.semmweb.core.dialog.URIValidator;
import com.semmtech.plugin.semmweb.core.dnd.DndUtils;
import com.semmtech.plugin.semmweb.core.dnd.LiteralTransfer;
import com.semmtech.plugin.semmweb.core.dnd.PropertyTransfer;
import com.semmtech.plugin.semmweb.core.dnd.ResourceTransfer;
import com.semmtech.plugin.semmweb.core.forms.editor.AbstractModelResourceContent;
import com.semmtech.plugin.semmweb.core.model.DisplayLanguage;
import com.semmtech.plugin.semmweb.core.model.ModelTransaction;
import com.semmtech.plugin.semmweb.core.preferences.LabelsPreference;
import com.semmtech.plugin.semmweb.core.preferences.LanguagesPreference;
import com.semmtech.plugin.semmweb.core.viewers.ModelNodeLabelProvider;
import com.semmtech.plugin.semmweb.core.viewers.ResourceInStatementToolTip;
import com.semmtech.plugin.semmweb.core.viewers.ResourceToolTip;
import com.semmtech.plugin.semmweb.core.viewers.StatementToolTip;
import com.semmtech.ui.plugin.widgets.Widgets;


/**
 * 
 * @author Sander Stolk
 */
public class PropertyStatementContentPart extends AbstractPropertyObjectContentPart {
    private static final Logger logger = Logger.getLogger(PropertyStatementContentPart.class);

    // private FormToolkit toolkit;
    private FormToolkit coloringToolkit;
    private FormColors formColors;

    private Property property;
    private Resource range;
    private RDFNode acceptedNode;
    private RDFNode originalNode;
    private RDFNode currentNode;
    private boolean importedTriple = false;

    private Text text;
    private Label iconLabel;
    // private Composite composite;
    private DefaultToolTip iconToolTip;
    private DefaultToolTip textToolTip;

    private Listener closeListener = null;

    private boolean newStatement = false;
    private boolean ignoreModify = false;
    private boolean modified = false;
    private boolean removed = false;
    private boolean editable = true;

    private ToolBar toolbar;
    private MenuManager menuManager;
    private ToolItem menuButton;

    public PropertyStatementContentPart(AbstractModelResourceContent contentParent,
            Composite parent, FormToolkit toolkit, Property property, Resource range, RDFNode node,
            boolean newStatement) {
        super(contentParent, parent, toolkit);

        this.newStatement = newStatement;
        this.property = property;
        this.range = range;
        if (range == null) {
            this.range = RDFS.Resource;
        }
        this.originalNode = (newStatement ? null : node);
        this.currentNode = node;

        initialize();
        createContent();
    }

    @Override
    public RDFNode getObject() {
        return acceptedNode;
    }

    public void alterContent(Resource range, RDFNode node, boolean newStatement) {
        this.newStatement = newStatement;
        this.range = range;
        if (range == null) {
            this.range = RDFS.Resource;
        }
        this.originalNode = (newStatement ? null : node);
        this.currentNode = node;

        initialize();
        fillContent();
        refresh();
    }

    private void initialize() {
        Resource subject = getResource();
        OntModel model = getModelProvider().getOntModel();
        if (originalNode != null) {
            importedTriple = !model.isInBaseModel(model.createStatement(subject, property,
                    originalNode));
        }
        if (newStatement) {
            importedTriple = false;
        }
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
        if (text != null && !text.isDisposed()) {
            text.setEnabled(editable);
        }
        fillToolBar();
    }

    @SuppressWarnings("null")
    private void createContent() {
        formColors = getDefaultFormColors();
        coloringToolkit = new FormToolkit(formColors);
        coloringToolkit.setBorderStyle(SWT.WRAP | SWT.MULTI);

        toolkit.paintBordersFor(this);
        coloringToolkit.paintBordersFor(this);
        TableWrapLayout layout = new TableWrapLayout();
        layout.numColumns = 3;
        layout.verticalSpacing = 2;
        layout.topMargin = 2;
        layout.rightMargin = 0;
        layout.leftMargin = 2;
        layout.horizontalSpacing = 4;
        layout.bottomMargin = 4;
        setLayout(layout);

        iconLabel = toolkit.createLabel(this, "");
        iconLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDoubleClick(MouseEvent event) {
                if (acceptedNode == null) {
                    return;
                }
                if (acceptedNode instanceof Resource) {
                    CorePlugin.getDefault().openResource((Resource) acceptedNode);
                }
                else if (acceptedNode.isLiteral()
                        && (acceptedNode.asLiteral().getDatatype() != null)
                        && (acceptedNode.asLiteral().getDatatype().getURI().equals(XSD.anyURI
                                .getURI()))) {
                    try {
                        URI uri = new URI(acceptedNode.asLiteral().getString());
                        openBrowser(uri);
                    }
                    catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

            }
        });

        text = toolkit.createText(this, "", SWT.WRAP | SWT.MULTI);
        TableWrapData textLayoutData = new TableWrapData(TableWrapData.FILL_GRAB,
                TableWrapData.TOP, 1, 1);
        Control editorAreaControl = Widgets.retrieveFirstParentOfType(text, CTabFolder.class);

        if (editorAreaControl == null) {
            logger.error("Unable to retreive the editor Editor Area Control (editorAreaControl == null)");
        }

        for (int i = 0; (i < 3) && (editorAreaControl != null); i++) {
            editorAreaControl = editorAreaControl.getParent();
        }

        int editorAreaWidth = editorAreaControl.computeSize(SWT.DEFAULT, SWT.DEFAULT).x;
        textLayoutData.maxWidth = editorAreaWidth
                - (105 + ResourceSidebar.WIDTH + ResourceSidebar.BORDER_WIDTH);
        text.setLayoutData(textLayoutData);
        text.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                if (!ignoreModify && !modified) {
                    text.setForeground(formColors.getColor(MODIFY_FOREGROUND));
                    setBorderColor(formColors.getColor(MODIFY_BORDER));
                    modified = true;
                    text.redraw();
                    fillToolBar();
                }
                // Check whether the text control requires resizing
                int currentWidth = text.getSize().x;
                int currentHeight = text.getSize().y;
                if ((currentWidth > 5) && (currentHeight != 0)) {
                    int preferredHeight = text.computeSize(currentWidth - 5, SWT.DEFAULT).y;
                    // The last return in the text is not taken into account by
                    // the above computeSize. As such, the code below takes care
                    // of just that.
                    int charCount = text.getCharCount();
                    if (charCount > 0) {
                        char lastChar = text.getTextChars()[charCount - 1];
                        if ((lastChar == '\r') || (lastChar == '\n')) {
                            preferredHeight += text.getLineHeight();
                        }
                    }
                    // Set the new preferred height if it does not match the
                    // current height.
                    if (currentHeight != preferredHeight) {
                        TableWrapData data = (TableWrapData) text.getLayoutData();
                        data.heightHint = preferredHeight;
                        // text.pack(true);
                        // parent.layout(true, true);
                        Widgets.layoutControlUpToScrollableParent(text);
                    }
                }
            }
        });
        text.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent event) {
                int key = event.keyCode;
                if (key == SWT.ESC) {
                    cancelEdit();
                }
                else if (((event.stateMask & SWT.SHIFT) != SWT.SHIFT)
                        && (key == SWT.CR || key == SWT.KEYPAD_CR)) {
                    confirmEdit();
                    event.doit = false;
                }
                else {
                    if (LabelsPreference.showReadableLabels() && !isLiteralStatement()) {
                        if ((key >= 97 && key <= 122) || (key >= 48 && key <= 57)) {
                            if ((event.stateMask & (SWT.CTRL | SWT.ALT)) == 0) {
                                /*
                                 * Editing the field is disabled anyway, but
                                 * when entering only alphanumeric values,
                                 * present a message as to why.
                                 */
                                MessageDialog
                                        .openInformation(getShell(), "Edit",
                                                "Text field cannot be modified, due to the display of human readable labels.");
                            }
                        }
                    }
                }
            }
        });
        // text.setEnabled(!importedTriple && editable);

        // Change font; to a font which is able to display other (Unicode)
        // characters
        // TODO: Does this cause a memory leak if not disposed and used every
        // single instance??
        IThemeManager themeManager = PlatformUI.getWorkbench().getThemeManager();
        ITheme currentTheme = themeManager.getCurrentTheme();
        FontRegistry fontRegistry = currentTheme.getFontRegistry();
        text.setFont(fontRegistry.get("com.semmtech.plugin.semmweb.core.resourceEditorFont"));

        DropTarget dropTarget = new DropTarget(this, DND.DROP_MOVE | DND.DROP_COPY);
        dropTarget.setTransfer(new Transfer[] { ResourceTransfer.getInstance(),
                PropertyTransfer.getInstance(), LiteralTransfer.getInstance(),
                TextTransfer.getInstance() });
        dropTarget.addDropListener(new DropTargetAdapter() {
            @Override
            public void drop(DropTargetEvent event) {
                if (importedTriple) {
                    MessageDialog
                            .openInformation(
                                    getShell(),
                                    "Imported Triple",
                                    "This triple has been imported, and thus cannot be changed without reasserting it within the current model.");
                    return;
                }
                if (ResourceTransfer.getInstance().isSupportedType(event.currentDataType)
                        || PropertyTransfer.getInstance().isSupportedType(event.currentDataType)) {

                    boolean update = true;
                    if (isLiteralStatement()) {
                        update = MessageDialog
                                .openQuestion(
                                        getShell(),
                                        "Change Type",
                                        "You dragged a resource onto a literal statement, do you wish to change the type of statement to a resource statement?");
                    }

                    if (update) {
                        range = null;
                        currentNode = (Resource) event.data;
                    }
                    else {
                        return;
                    }
                }
                else if (LiteralTransfer.getInstance().isSupportedType(event.currentDataType)
                        || TextTransfer.getInstance().isSupportedType(event.currentDataType)) {
                    boolean update = true;
                    if (!isLiteralStatement()) {
                        update = MessageDialog
                                .openQuestion(
                                        getShell(),
                                        "Change Type",
                                        "You dragged a literal onto a resource statement, do you wish to change the type of statement to a literal statement?");
                    }

                    if (update) {
                        if (LiteralTransfer.getInstance().isSupportedType(event.currentDataType)) {
                            range = RDFS.Literal;
                            currentNode = (Literal) event.data;
                        }
                        else {
                            currentNode = getModelProvider().getOntModel().createLiteral(
                                    event.data.toString());
                        }
                    }
                    else {
                        return;
                    }
                }
                updateField(currentNode, false);
                confirmEdit();
                refresh();
            }
        });

        DndUtils.addDragSupport(iconLabel, getModelProvider().getOntModel(),
                new DndUtils.RDFNodeProvider() {
                    @Override
                    public RDFNode getRDFNode() {
                        return currentNode;
                    }
                });

        createToolBar();
        fillContent();
    }

    private void fillContent() {
        if (isLiteralStatement()) {
            StatementToolTip statementToolTip = new StatementToolTip(iconLabel);
            statementToolTip.setModelProvider(getModelProvider());
            iconToolTip = statementToolTip;
        }
        else {
            ResourceInStatementToolTip resourceToolTip = new ResourceInStatementToolTip(iconLabel);
            resourceToolTip.setModelProvider(getModelProvider());
            iconToolTip = resourceToolTip;
        }
        iconToolTip.setShift(new Point(8, 10));

        text.setEnabled(!importedTriple && editable);

        if (isLiteralStatement()) {
            textToolTip = new DefaultToolTip(text);
        }
        else {
            ResourceToolTip resourceToolTip = new ResourceToolTip(text);
            resourceToolTip.setModelProvider(getModelProvider());
            textToolTip = resourceToolTip;
        }
        textToolTip.setShift(new Point(8, 10));

        if (!newStatement) {
            updateField(originalNode, true);
        }
        else {
            updateField(currentNode, false);
        }
        acceptedNode = originalNode;
    }

    private void browseForResource() {
        ResourceSelectionDialog dialog = new ResourceSelectionDialog(getShell(), "Select Resource",
                "Select a resource from the list below.");
        dialog.clearHierarchicalProperties();
        dialog.clearRootResources();
        dialog.clearExcludedResources();

        dialog.addHierarchicalProperties(new Property[] { RDF.type, RDFS.subClassOf });
        dialog.addRootResources(new Resource[] { range });
        dialog.setAllowedResourceTypes(new Resource[] { range });
        dialog.setModel(getModelProvider().getOntModel());

        if (dialog.open() == Window.OK) {
            Resource resource = dialog.getFirstSelectedResource();
            if (resource != null) {
                currentNode = resource;
                updateField(currentNode, false);
                confirmEdit();
                refresh();
            }
        }
    }

    private void deleteStatement() {
        if (MessageDialog.openQuestion(getShell(), "Delete",
                "Are you sure you want to delete this statement?")) {
            hideStatement();
            removed = true;
            updateModel();
        }
    }

    private void openBrowser(URI uri) {
        try {
            final IWebBrowser browser = PlatformUI.getWorkbench().getBrowserSupport()
                    .createBrowser(SWT.MOZILLA, null, null, null);
            String url = uri.toASCIIString(); // String.format("http://sindice.com/search?q=%s&nq=&fq=",
                                              // URLEncoder.encode("physical object",
                                              // "UTF-8"));
            logger.debug("Opening browser with URL = '" + url + "'");
            browser.openURL(uri.toURL());
        }
        catch (PartInitException e) {
            e.printStackTrace();
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void refresh() {
        super.refresh();
    }

    @Override
    public void dispose() {
        formColors.dispose();
        coloringToolkit.dispose();
        super.dispose();
    }

    private void createToolBar() {
        toolbar = new ToolBar(this, SWT.HORIZONTAL | SWT.FLAT);
        toolbar.setLayoutData(new TableWrapData(TableWrapData.LEFT, TableWrapData.TOP, 1, 1));
        toolkit.adapt(toolbar);
        toolkit.paintBordersFor(toolbar);

        fillToolBar();

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

    protected boolean isLiteralStatement() {
        if (range != null
                && !range.isAnon()
                && (range.equals(RDFS.Literal)
                        || range.getURI().equals(
                                "http://www.w3.org/1999/02/22-rdf-syntax-ns#PlainLiteral") || range
                        .getURI().equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#XMLLiteral"))) {
            return true;
        }
        else if (currentNode != null && currentNode.isLiteral()) {
            return true;
        }
        return false;
    }

    private void fillToolBar() {
        menuManager = new MenuManager();
        if (modified) {
            Action doneAction = new Action() {
                @Override
                public void run() {
                    confirmEdit();
                }
            };
            doneAction.setText("Done");
            doneAction.setToolTipText("Confirm the changes to this "
                    + (isLiteralStatement() ? "literal" : "resource"));
            menuManager.add(doneAction);

            Action undoAction = new Action() {
                @Override
                public void run() {
                    cancelEdit();
                }
            };
            undoAction.setText("Undo");
            undoAction.setToolTipText("Undo changes to this "
                    + (isLiteralStatement() ? "literal" : "resource"));
            undoAction.setImageDescriptor(CorePlugin.getDefault().getImageDescriptor(
                    CorePluginImages.IMG_UNDO));
            menuManager.add(undoAction);
            menuManager.add(new Separator());
        }

        if (acceptedNode != null && acceptedNode.isResource()) {
            Action showAction = new Action() {
                @Override
                public void run() {
                    CorePlugin.getDefault().openResource(acceptedNode.asResource());
                }
            };
            showAction.setText("Open");
            showAction.setToolTipText("Open '"
                    + getModelProvider().getLabelProvider().getText(acceptedNode) + "' in editor");
            menuManager.add(showAction);
            menuManager.add(new Separator());
        }
        if (importedTriple) {
            Action reassertAction = new Action() {
                @Override
                public void run() {
                    reassertTriple();
                }
            };
            reassertAction.setText("Reassert");
            reassertAction.setImageDescriptor(CorePlugin.getDefault().getImageDescriptor(
                    CorePluginImages.IMG_REASSERT_TRIPLE));
            menuManager.add(reassertAction);
        }
        else if (!importedTriple && isLiteralStatement()) {
            // / Languages Menu
            MenuManager languagesMenu = new MenuManager("Language");
            for (final DisplayLanguage language : LanguagesPreference.getDisplayLanguages()) {
                Action languageAction = new Action() {
                    @Override
                    public void run() {
                        if (language.getCode() == null || language.getCode().length() == 0) {
                            executeChangeLanguage(null);
                        }
                        else {
                            executeChangeLanguage(language.getCode());
                        }
                    }
                };
                languageAction.setText(language.getName());
                languageAction.setImageDescriptor(CorePlugin.getDefault().getImageDescriptor(
                        language.getImageKey()));
                languagesMenu.add(languageAction);
            }

            // / Datatype Menu
            Action stringAction = new Action() {
                @Override
                public void run() {
                    executeChangeDatatype(XSD.xstring);
                }
            };
            stringAction.setText("String");
            stringAction.setImageDescriptor(CorePlugin.getDefault().getImageDescriptor(
                    CorePluginImages.IMG_XSD_STRING));

            Action integerAction = new Action() {
                @Override
                public void run() {
                    executeChangeDatatype(XSD.integer);
                }
            };
            integerAction.setText("Integer");
            integerAction.setImageDescriptor(CorePlugin.getDefault().getImageDescriptor(
                    CorePluginImages.IMG_XSD_INTEGER));

            Action doubleAction = new Action() {
                @Override
                public void run() {
                    executeChangeDatatype(XSD.xdouble);
                }
            };
            doubleAction.setText("Double");
            doubleAction.setImageDescriptor(CorePlugin.getDefault().getImageDescriptor(
                    CorePluginImages.IMG_XSD_DOUBLE));

            Action floatAction = new Action() {
                @Override
                public void run() {
                    executeChangeDatatype(XSD.xfloat);
                }
            };
            floatAction.setText("Float");
            floatAction.setImageDescriptor(CorePlugin.getDefault().getImageDescriptor(
                    CorePluginImages.IMG_XSD_FLOAT));

            Action dateAction = new Action() {
                @Override
                public void run() {
                    executeChangeDatatype(XSD.date);
                }
            };
            dateAction.setText("Date");
            dateAction.setImageDescriptor(CorePlugin.getDefault().getImageDescriptor(
                    CorePluginImages.IMG_XSD_DATE));

            Action timeAction = new Action() {
                @Override
                public void run() {
                    executeChangeDatatype(XSD.dateTime);
                }
            };
            timeAction.setText("DateTime");
            timeAction.setImageDescriptor(CorePlugin.getDefault().getImageDescriptor(
                    CorePluginImages.IMG_XSD_DATETIME));

            Action uriAction = new Action() {
                @Override
                public void run() {
                    executeChangeDatatype(XSD.anyURI);
                }
            };
            uriAction.setText("AnyURI");
            uriAction.setImageDescriptor(CorePlugin.getDefault().getImageDescriptor(
                    CorePluginImages.IMG_XSD_ANYURI));

            MenuManager datatypeMenu = new MenuManager("Datatype");
            datatypeMenu.add(stringAction);
            datatypeMenu.add(integerAction);
            datatypeMenu.add(floatAction);
            datatypeMenu.add(dateAction);
            datatypeMenu.add(timeAction);
            datatypeMenu.add(uriAction);

            menuManager.add(languagesMenu);
            menuManager.add(datatypeMenu);
        }
        else if (!importedTriple && !isLiteralStatement() && editable) {
            Action browseAction = new Action() {
                @Override
                public void run() {
                    browseForResource();
                }
            };
            browseAction.setText("Browse...");
            browseAction.setToolTipText("Browse for a resource to be used as object");
            menuManager.add(browseAction);
        }
        menuManager.add(new Separator());
        if (isLiteralStatement() && currentNode != null && currentNode.isLiteral()) {
            final String lexicalForm = currentNode.asLiteral().getLexicalForm();
            final String fromLangauge = currentNode.asLiteral().getLanguage();

            String languageSuffix = "";
            if (fromLangauge != null && fromLangauge.length() > 0) {
                languageSuffix = " (" + fromLangauge + ")";
            }

            MenuManager toolsMenu = new MenuManager("Tools");
            Action sindiceAction = new Action() {
                @Override
                public void run() {
                    try {
                        URI uri = new URI("http", "sindice.com", "/search",
                                "q=" + lexicalForm + "", "");
                        openBrowser(uri);
                    }
                    catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                }
            };
            sindiceAction.setText("Sindice");
            sindiceAction.setImageDescriptor(CorePlugin.getDefault().getImageDescriptor(
                    CorePluginImages.IMG_SINDICE_LOGO));
            toolsMenu.add(sindiceAction);

            Action wikiAction = new Action() {
                @Override
                public void run() {
                    try {
                        String language = fromLangauge;
                        if (language == null || language.length() == 0) {
                            language = "en";
                        }
                        URI uri = new URI("http", language + ".wikipedia.org", "/wiki/"
                                + lexicalForm, "", "");
                        openBrowser(uri);
                    }
                    catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                }
            };
            wikiAction.setText("Wikipedia" + languageSuffix);
            wikiAction.setImageDescriptor(CorePlugin.getDefault().getImageDescriptor(
                    CorePluginImages.IMG_WIKIPEDIA_SEARCH));
            toolsMenu.add(wikiAction);

            Action googleAction = new Action() {
                @Override
                public void run() {
                    try {
                        String langauge = fromLangauge;
                        if (langauge == null || langauge.length() == 0) {
                            langauge = "en";
                        }
                        URI uri = new URI("http", "www.google." + langauge, "/search", "q="
                                + lexicalForm, "");
                        openBrowser(uri);
                    }
                    catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                }
            };
            googleAction.setText("Google" + languageSuffix);
            googleAction.setImageDescriptor(CorePlugin.getDefault().getImageDescriptor(
                    CorePluginImages.IMG_GOOGLE_LOGO));
            toolsMenu.add(googleAction);

            MenuManager translateMenu = new MenuManager("Google Translate");
            for (final DisplayLanguage language : LanguagesPreference.getDisplayLanguages()) {
                if (language.getCode() == null) {
                    continue;
                }
                Action translateAction = new Action() {
                    @Override
                    public void run() {
                        try {
                            String langauge = fromLangauge;
                            if (langauge == null || langauge.length() == 0) {
                                langauge = "auto";
                            }
                            URI uri = new URI("http", "translate.google.com", "/", "", langauge
                                    + "/" + language.getCode() + "/" + lexicalForm);
                            openBrowser(uri);
                        }
                        catch (URISyntaxException e) {
                            e.printStackTrace();
                        }
                    }
                };
                translateAction.setText("to " + language.getName());
                translateAction.setImageDescriptor(CorePlugin.getDefault().getImageDescriptor(
                        language.getImageKey()));
                translateMenu.add(translateAction);
            }

            toolsMenu.add(translateMenu);
            menuManager.add(toolsMenu);
        }
        menuManager.add(new Separator());
        if (!importedTriple) {
            Action deleteAction = new Action() {
                @Override
                public void run() {
                    deleteStatement();
                }
            };
            deleteAction.setText("Delete");
            deleteAction.setToolTipText("Delete this statement from model");
            deleteAction.setImageDescriptor(CorePlugin.getDefault().getImageDescriptor(
                    CorePluginImages.IMG_STATEMENT_DELETE));
            menuManager.add(deleteAction);
        }
        boolean includeReify = false;
        if (includeReify) {
            Action reifyAction = new Action() {
                @Override
                public void run() {
                    reifyTriple();
                }
            };
            reifyAction.setText("Reify");
            reifyAction.setImageDescriptor(CorePlugin.getDefault().getImageDescriptor(
                    CorePluginImages.IMG_REIFY_TRIPLE));
            menuManager.add(reifyAction);
        }

    }

    protected void changeDatatype(Resource datatype) {
        changeLanguage(null);
        OntModel model = getModelProvider().getOntModel();
        String value = null;
        if (currentNode != null) {
            value = currentNode.asLiteral().getString();
        }
        if (datatype == null) {
            currentNode = model.createLiteral(value);
        }
        else {
            currentNode = model.createTypedLiteral(value, datatype.getURI());
        }
    }

    protected void executeChangeDatatype(Resource datatype) {
        changeDatatype(datatype);
        boolean save = !modified;
        updateField(currentNode, false);
        if (save) {
            confirmEdit();
        }
    }

    protected void changeLanguage(String lang) {
        OntModel model = getModelProvider().getOntModel();
        String value = null;
        if (currentNode != null) {
            value = currentNode.asLiteral().getString();
            if (lang == null) {
                currentNode = model.createLiteral(value);
            }
            else {
                currentNode = model.createLiteral(value, lang);
            }
        }
        else {
            value = text.getText().trim();
            // / TODO: refactor; is also used in validate
            String regex = "(\\{\\@)(\\w+\\-?\\w*)(\\}$)";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(value);
            if (matcher.find()) {
                value = value.replaceAll(regex, "");
            }
            if (lang != null) {
                text.setText(value.trim() + " {@" + lang + "}");
            }
        }
        if (lang == null) {
            currentNode = model.createLiteral(value);
        }
        else {
            currentNode = model.createLiteral(value, lang);
        }
    }

    protected void executeChangeLanguage(String lang) {
        changeLanguage(lang);
        boolean save = !modified;
        updateField(currentNode, false);
        if (save) {
            confirmEdit();
        }
    }

    public void reassertTriple() {
        if (importedTriple) {
            OntModel model = getModelProvider().getOntModel();
            Resource subject = getResource();
            model.add(model.createStatement(subject, property, originalNode));
            updateModel();
            importedTriple = false;
            text.setEnabled(true);
            fillToolBar();
        }
    }

    public void reifyTriple() {
        MessageDialog.openInformation(getShell(), "TODO", "TODO: Needs to be implemented!");
    }

    @Override
    public boolean setFocus() {
        return text.setFocus();
    }

    protected void confirmEdit() {
        if (validateCurrentValue()) {
            acceptedNode = currentNode;
            finishEdit();
            updateModel();
        }
        else {
            text.setForeground(formColors.getColor(ERROR_FOREGROUND));
            setBorderColor(formColors.getColor(ERROR_BORDER));
            text.redraw();
        }
    }

    public void createUpdatePropertyStatementStatements() {
        createUpdatePropertyStatementStatements(getResource(), property, originalNode, acceptedNode);
    }

    private void createUpdatePropertyStatementStatements(Resource subject, Property property,
            RDFNode originalNode, RDFNode acceptedNode) {
        if ((subject != null) && (property != null)) {
            OntModel model = getModelProvider().getOntModel();
            if (originalNode != null) {
                model.remove(subject, property, originalNode);
            }
            if (!removed && acceptedNode != null) {
                subject.addProperty(property, acceptedNode);
            }
        }
    }

    public void updateModel() {
        String transactionDescription = "Change due to commit in StatementFormPart";
        ModelTransaction transaction = getModelProvider().createTransaction(transactionDescription);
        createUpdatePropertyStatementStatements();
        getModelProvider().commitTransaction(transaction);

        if (!removed && acceptedNode != null) {
            originalNode = acceptedNode;
        }
        newStatement = false;
    }

    protected void cancelEdit() {
        if (newStatement || acceptedNode == null) {
            hideStatement();
        }
        finishEdit();
    }

    public void setCloseListener(Listener listener) {
        closeListener = listener;
    }

    /**
     * TODO: This is quick-and-dirty!! But it works!
     */
    private void hideStatement() {
        setVisible(false);
        {
            TableWrapData data = new TableWrapData();
            data.maxHeight = 0;
            setLayoutData(data);
        }
        if (closeListener != null) {
            Event event = new Event();
            event.data = this;
            closeListener.handleEvent(event);
        }
        refresh();
    }

    protected void finishEdit() {
        if (text.isDisposed()) {
            return;
        }
        updateField(acceptedNode, true);
        text.setForeground(formColors.getColor(DEFAULT_FOREGROUND));
        setBorderColor(formColors.getColor(DEFAULT_BORDER));
        modified = false;
        fillToolBar();
        refresh();
    }

    private boolean validateCurrentValue() {
        String value = text.getText();
        OntModel model = getModelProvider().getOntModel();
        value = value.trim();
        if (isLiteralStatement()) {
            String datatypeUri = null;
            if (currentNode != null && currentNode.isLiteral()) {
                datatypeUri = currentNode.asLiteral().getDatatypeURI();
            }

            String regex = "(\\{\\@)(\\w+\\-?\\w*)(\\}$)";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(value);
            String lang = null;
            if (matcher.find()) {
                lang = matcher.group(2);
                value = value.replaceAll(regex, "");
                value = value.trim();
            }
            if (lang != null) {
                currentNode = model.createLiteral(value, lang);
            }
            else if (datatypeUri != null) {
                currentNode = model.createTypedLiteral(value, datatypeUri);
            }
            else {
                currentNode = model.createLiteral(value);
            }
            return true;
        }

        if (!LabelsPreference.showReadableLabels()) {
            // TODO: For test purposes just accept any changes for restrictions
            if (currentNode != null
                    && currentNode.asResource().hasProperty(RDF.type, OWL.Restriction)) {
                return true;
            }

            // TODO: Manual check if URI is a valid resource
            String baseUri = CorePlugin.getDefault().getActiveModelProvider().getBaseURI();
            URIValidator validator = new URIValidator(model, baseUri);
            String validation = validator.isValid(value);
            if (validation != null) {
                return false;
            }
            String uri = value;
            if (uri.startsWith("<") && uri.endsWith(">")) {
                uri = uri.substring(1, uri.length() - 1);
            }
            else {
                uri = model.expandPrefix(uri);
            }
            Resource resource = model.createResource(uri);
            currentNode = resource;
            return true;
        }
        return true;
    }

    private void updateField(RDFNode node, boolean ignoreNotify) {
        if (text.isDisposed()) {
            return;
        }

        ModelNodeLabelProvider labelProvider = getModelProvider().getLabelProvider();
        ignoreModify = ignoreNotify;
        text.setEditable(true);
        if (node != null) {
            Resource subject = getResource();
            OntModel model = getModelProvider().getOntModel();
            Statement statement = model.createStatement(subject, property, node);

            String textValue = labelProvider.getText(node);
            if (textValue != null) {
                text.setText(textValue);
            }
            iconLabel.setImage(labelProvider.getImage(node));

            if ((iconToolTip instanceof ResourceInStatementToolTip)
                    && (textToolTip instanceof ResourceToolTip)) {
                if (node.isResource()) {
                    if (LabelsPreference.showReadableLabels()) {
                        text.setEditable(false);
                    }
                    ((ResourceInStatementToolTip) iconToolTip).setResourceInStatement(statement,
                            ResourceInStatementToolTip.OBJECT);
                    ((ResourceToolTip) textToolTip).setResource(node.asResource());
                }
                else {
                    ((ResourceInStatementToolTip) iconToolTip).setResourceInStatement(null,
                            ResourceInStatementToolTip.OBJECT);
                    ((ResourceToolTip) textToolTip).setResource(null);
                }
                ((ResourceInStatementToolTip) iconToolTip).setModelProvider(getModelProvider());
                ((ResourceToolTip) textToolTip).setModelProvider(getModelProvider());
            }
            else if (iconToolTip instanceof StatementToolTip) {
                ((StatementToolTip) iconToolTip).setStatement(statement);
                ((StatementToolTip) iconToolTip).setModelProvider(getModelProvider());
            }
        }
        else {
            text.setText("");
            if (isLiteralStatement()) {
                iconLabel.setImage(CorePlugin.getDefault().getImage(CorePluginImages.IMG_XSD));
            }
            else if (range != null) {
                iconLabel.setImage(CorePlugin.getDefault().getImage(
                        labelProvider.getInstanceImageKey(range)));
            }
            else {
                iconLabel.setImage(CorePlugin.getDefault().getImage(
                        CorePluginImages.IMG_OWL_INDIVIDUAL));
            }
        }
        if (!(textToolTip instanceof ResourceToolTip)) {
            String literalToolTipText = "In cases where this literal consists of text, line breaks can be added to its contents by pressing Shift+Enter.\n"
                    + "Furthermore, the language of the text can be specified by either using the dropdown menu behind the text box or\n"
                    + "by appending the text with a language tag (e.g. {@en} for the English language).";
            textToolTip.setText(literalToolTipText);
        }
        ignoreModify = false;
    }

    private void setBorderColor(Color color) {
        setBorderColor(color.getRed(), color.getGreen(), color.getBlue());
    }

    private void setBorderColor(int r, int g, int b) {
        formColors.createColor(IFormColors.BORDER, r, g, b);
        formColors.setBackground(formColors.getColor(BACKGROUND));
        redraw();
        layout(true, true);
    }

}
