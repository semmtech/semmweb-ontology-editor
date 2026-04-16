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

package com.semmtech.plugin.semmweb.core.preferences;


import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.TextStyle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.hp.hpl.jena.ontology.OntDocumentManager;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.ProfileRegistry;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ModelMaker;
import com.hp.hpl.jena.rdf.model.ModelReader;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.rulesys.GenericRuleReasoner;
import com.hp.hpl.jena.util.FileUtils;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.CorePluginImages;
import com.semmtech.plugin.semmweb.core.dialog.ResourceSelectionDialog;
import com.semmtech.plugin.semmweb.core.model.IModelProvider;
import com.semmtech.plugin.semmweb.core.model.OntDocumentManagerRegistry;
import com.semmtech.plugin.semmweb.core.model.ResourceEditorClassPreference;
import com.semmtech.plugin.semmweb.core.model.ResourceEditorClassPreferences;
import com.semmtech.plugin.semmweb.core.util.RulesUtil;
import com.semmtech.plugin.semmweb.core.viewers.ModelNodeLabelProvider;
import com.semmtech.plugin.semmweb.core.viewers.TreeContentProvider;
import com.semmtech.ui.plugin.decorators.OverlayImageIcon;
import com.semmtech.ui.plugin.util.FontUtil;


/**
 * @deprecated Now the property is used instead -> ResourceEditorPropertyPage
 * 
 * @author Simone Rondelli
 */
@Deprecated
public class ResourceEditorPreferencePage_old extends PreferencePage implements
        IWorkbenchPreferencePage {
    public static final String ID = "com.semmtech.plugin.semmweb.core.preferences.resourceEditor";

    private static final String PRESENTATION_FLAT = "flatPresentation";
    private static final String PRESENTATION_HIERARCHY = "hierarchyPresentation";
    private String presentation = PRESENTATION_HIERARCHY;

    private Set<Resource> roots = Sets.newHashSet();
    private Multimap<Resource, Resource> subClasses = HashMultimap.create();
    private Multimap<Resource, Resource> directSuperClasses = HashMultimap.create();
    private Multimap<Resource, Resource> superClasses = HashMultimap.create();

    private LabelProvider labelProvider = null;
    private TreeViewer classesViewer;
    private TreeViewer settingsViewer;
    private OntModel taxonomyModel;

    private ResourceEditorClassPreferences preferences;

    private Button hideRestrictionCheckbox;
    private Button checkDomainRestrictionsCheckbox;
    private Text filterText;
    private ToolItem hierarchyItem;
    private ToolItem flatItem;

    private boolean hasRestrictions;
    private boolean hasPossessedAspects;
    private Set<String> qcrPropertyUris;

    private Font boldFont;

    public ResourceEditorPreferencePage_old() {
        super("Resource Editor");
        setPreferenceStore(CorePlugin.getDefault().getPreferenceStore());
        setDescription("Change the settings for the Resource Editor.");

        createTaxonomyModel();

        IModelProvider provider = CorePlugin.getDefault().getActiveModelProvider();
        if (provider != null) {
            labelProvider = provider.getLabelProvider();
        }
        else {
            labelProvider = new ModelNodeLabelProvider(taxonomyModel);
        }

        roots.add(RDFS.Resource);
        buildHierarchy(RDFS.Resource, false);

        // preferences =
        // ResourceEditorPreference.getResourceEditorPreferences();
    }

    public void createTaxonomyModel() {
        // Get label provider
        IModelProvider provider = CorePlugin.getDefault().getActiveModelProvider();

        // TODO: Refactor; use SPARQL; and make project based!!
        ModelMaker maker = ModelFactory.createMemModelMaker();
        OntDocumentManager manager = OntDocumentManagerRegistry.getInstance().getDocumentManager();
        OntModelSpec spec = new OntModelSpec(maker, maker, manager, null, ProfileRegistry.OWL_LANG);
        Reasoner reasoner = new GenericRuleReasoner(RulesUtil.parseRulesFromBundle(
                CorePlugin.PLUGIN_ID, "src/main/resources/rules/", "basic-taxonomy.rules"));
        spec.setReasoner(reasoner);

        taxonomyModel = null;
        if (provider != null && provider.getOntModel() != null) {
            taxonomyModel = ModelFactory.createOntologyModel(spec, provider.getOntModel());
        }
        else {
            taxonomyModel = ModelFactory.createOntologyModel(spec);
        }

        for (String uri : Arrays.asList(new String[] { RDF.getURI(),
                RDFS.getURI().substring(0, RDFS.getURI().length() - 1), OWL.getURI() })) {
            if (taxonomyModel.hasLoadedImport(uri)) {
                continue;
            }
            String url = manager.doAltURLMapping(uri);
            taxonomyModel.addSubModel(maker.getModel(url, new ModelReader() {
                @Override
                public Model readModel(Model toRead, String URL) {
                    String lang = FileUtils.guessLang(URL);
                    toRead.read(URL, lang);
                    return toRead;
                }
            }));
            taxonomyModel.addLoadedImport(uri);
        }
    }

    private void buildHierarchy(Resource resource, boolean allowAnonymous) {
        for (Resource child : taxonomyModel.listResourcesWithProperty(RDFS.subClassOf, resource)
                .toSet()) {
            if (!allowAnonymous && child.isAnon()) {
                continue;
            }
            subClasses.put(resource, child);
            superClasses.put(child, resource);
            directSuperClasses.put(child, resource);
            superClasses.putAll(child, superClasses.get(resource));
            buildHierarchy(child, allowAnonymous);
        }
    }

    protected OntModel getOntModel() {
        IModelProvider provider = CorePlugin.getDefault().getActiveModelProvider();
        if (provider != null) {
            return provider.getOntModel();
        }
        return null;
    }

    @Override
    public void init(IWorkbench workbench) {
    }

    @SuppressWarnings("unused")
    @Override
    protected Control createContents(Composite parent) {
        Composite top = new Composite(parent, SWT.LEFT);
        GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
        layoutData.heightHint = 800;
        top.setLayoutData(layoutData);
        GridLayout layout = new GridLayout(1, false);
        layout.marginWidth = 0;
        top.setLayout(layout);

        Group settingsGroup = new Group(top, SWT.SHADOW_ETCHED_IN);
        settingsGroup.setText("Classes");
        layoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
        layoutData.heightHint = 300;
        settingsGroup.setLayoutData(layoutData);
        layout = new GridLayout(2, false);
        layout.marginWidth = 5;
        settingsGroup.setLayout(layout);

        filterText = new Text(settingsGroup, SWT.BORDER);
        filterText.setText("*");
        layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
        layoutData.heightHint = 13;
        filterText.setLayoutData(layoutData);
        filterText.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                String filter = filterText.getText();
                if (filter.length() == 0 || filter.equals("*")) {
                    hierarchyItem.setEnabled(true);
                }
                else {
                    presentation = PRESENTATION_FLAT;
                    hierarchyItem.setSelection(false);
                    flatItem.setSelection(true);
                    hierarchyItem.setEnabled(false);
                }
                refreshTypesViewer();
                refreshSettingsViewer();
            }
        });

        new Label(settingsGroup, SWT.NONE);

        Composite treeComposite = new Composite(settingsGroup, SWT.BORDER);

        layout = new GridLayout(1, false);
        layout.verticalSpacing = 1;
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        treeComposite.setLayout(layout);
        treeComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));

        createClassesToolbar(treeComposite);
        createClassesTree(treeComposite);

        Composite composite = new Composite(settingsGroup, SWT.NONE);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));

        layout = new GridLayout(1, false);
        layout.verticalSpacing = 4;
        layout.marginWidth = 3;
        layout.marginHeight = 0;
        composite.setLayout(layout);

        Label label = new Label(settingsGroup, SWT.NONE);
        layoutData = new GridData(SWT.LEFT, SWT.BOTTOM, false, false, 1, 1);
        layoutData.verticalIndent = 3;
        label.setLayoutData(layoutData);
        label.setText("Settings:");
        new Label(settingsGroup, SWT.NONE);

        createSettingsTree(settingsGroup);

        Composite propertyButtonComposite = new Composite(settingsGroup, SWT.NONE);

        layout = new GridLayout(1, false);
        layout.marginHeight = 0;
        layout.verticalSpacing = 4;
        layout.marginWidth = 3;
        propertyButtonComposite.setLayout(layout);
        propertyButtonComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));

        checkDomainRestrictionsCheckbox = new Button(top, SWT.CHECK);
        checkDomainRestrictionsCheckbox
                .setText("Check domain restrictions in 'All Properties' section");
        // TODO to avoid errors, if the class is needed again uncomment
        // checkDomainRestrictionsCheckbox.setSelection(ResourceEditorPreference
        // .checkDomainRestrictions());
        checkDomainRestrictionsCheckbox.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false,
                2, 1));

        hideRestrictionCheckbox = new Button(top, SWT.CHECK);
        hideRestrictionCheckbox
                .setText("Hide statements which contain restrictions from 'All Properties' section");
        // TODO to avoid errors, if the class is needed again uncomment
        // hideRestrictionCheckbox.setSelection(ResourceEditorPreference.hideRestrictionStatements());
        hideRestrictionCheckbox.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false, 2, 1));

        refreshTypesViewer();
        refreshSettingsViewer();

        return top;
    }

    private void createClassesToolbar(Composite parent) {
        ToolBar toolBar = new ToolBar(parent, SWT.FLAT | SWT.RIGHT);
        toolBar.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));

        flatItem = new ToolItem(toolBar, SWT.CHECK);
        flatItem.setImage(CorePlugin.getDefault().getImage(CorePluginImages.IMG_FLAT));
        flatItem.setToolTipText("Show classes in a list");
        flatItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (!presentation.equals(PRESENTATION_FLAT)) {
                    presentation = PRESENTATION_FLAT;
                    refreshTypesViewer();
                    hierarchyItem.setSelection(false);
                    flatItem.setSelection(true);
                }
            }
        });

        hierarchyItem = new ToolItem(toolBar, SWT.CHECK);
        hierarchyItem.setSelection(true);
        hierarchyItem.setToolTipText("Show classes in a taxonomy");
        hierarchyItem.setImage(CorePlugin.getDefault().getImage(CorePluginImages.IMG_HIERARCHICAL));
        hierarchyItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (!presentation.equals(PRESENTATION_HIERARCHY)) {
                    presentation = PRESENTATION_HIERARCHY;
                    refreshTypesViewer();
                    flatItem.setSelection(false);
                    hierarchyItem.setSelection(true);
                }
            }
        });
    }

    private void createClassesTree(Composite parent) {
        Tree classesTree = new Tree(parent, SWT.NONE);
        GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
        layoutData.heightHint = 300;
        classesTree.setLayoutData(layoutData);

        TreeColumn column = new TreeColumn(classesTree, SWT.NONE, 0);
        column.setText("Resource");
        column.setWidth(250);
        column.setMoveable(false);

        classesViewer = new TreeViewer(classesTree);
        classesViewer.setContentProvider(new TreeContentProvider() {

            @Override
            public Object[] getElements(Object inputElement) {
                if (inputElement instanceof Model) {
                    String filter = filterText.getText();
                    if (presentation.equals(PRESENTATION_HIERARCHY)) {
                        return roots.toArray();
                    }
                    else if (filter.length() == 0 || filter.equals("*")) {
                        return subClasses.keySet().toArray();
                    }
                    else {
                        List<Resource> filtered = Lists.newArrayList();
                        for (Resource resource : subClasses.keySet()) {
                            String resourceName = labelProvider.getText(resource);
                            if (resourceName.toLowerCase().contains(filter.toLowerCase())) {
                                filtered.add(resource);
                            }
                        }
                        return filtered.toArray();
                    }
                }
                return new Object[0];
            }

            @Override
            public Object[] getChildren(Object parentElement) {
                if (parentElement instanceof Resource) {
                    if (presentation.equals(PRESENTATION_HIERARCHY)) {
                        Resource parent = (Resource) parentElement;
                        if (subClasses.containsKey(parent)) {
                            return subClasses.get(parent).toArray();
                        }
                    }
                }
                return new Object[0];
            }

            @Override
            public boolean hasChildren(Object element) {
                if (element instanceof Resource) {
                    if (presentation.equals(PRESENTATION_HIERARCHY)) {
                        Resource resource = (Resource) element;
                        if (subClasses.containsKey(resource)) {
                            return (subClasses.get(resource).size() > 0);
                        }
                    }
                }
                return false;
            }
        });
        classesViewer.setLabelProvider(new LabelProvider() {
            @Override
            public Image getImage(Object element) {
                if (element instanceof Resource) {
                    return labelProvider.getImage(element);
                }
                return null;
            }

            @Override
            public String getText(Object element) {
                if (element instanceof Resource) {
                    return labelProvider.getText(element);
                }
                return null;
            }
        });
        classesViewer.setComparator(new ViewerComparator() {
            @Override
            public int compare(Viewer viewer, Object e1, Object e2) {
                if (e1 instanceof Resource && e2 instanceof Resource) {
                    Resource r1 = (Resource) e1;
                    Resource r2 = (Resource) e2;
                    return labelProvider.getText(r1).compareToIgnoreCase(labelProvider.getText(r2));
                }
                return super.compare(viewer, e1, e2);
            }
        });
        classesViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                getSelectedClass();
                refreshSettingsViewer();
                settingsViewer.getTree().deselectAll();
            }
        });
    }

    private void refreshTypesViewer() {
        classesViewer.setInput(taxonomyModel);
        classesViewer.expandToLevel(2);
    }

    private void createSettingsTree(Composite parent) {
        Tree propertiesTree = new Tree(parent, SWT.BORDER);
        GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
        layoutData.heightHint = 110;
        propertiesTree.setLayoutData(layoutData);

        // / Item is still highlighted; disabled for now untill better
        // unselection method is found
        // propertiesTree.addListener(SWT.EraseItem, new Listener() {
        // @Override
        // public void handleEvent(Event event) {
        // SettingItem item = (SettingItem)event.item.getData();
        // boolean isInherited =
        // (item.isInherited(getSelectedClass().getURI()));
        // if (isInherited && (event.detail & SWT.SELECTED) != 0)
        // event.detail &= ~SWT.SELECTED;
        // }
        // });

        TreeColumn column = new TreeColumn(propertiesTree, SWT.NONE, 0);
        column.setText("Property");
        column.setWidth(420);
        column.setMoveable(false);

        settingsViewer = new TreeViewer(propertiesTree);
        if (boldFont == null) {
            FontData[] boldFontData = FontUtil.getModifiedFontData(settingsViewer.getTree()
                    .getFont().getFontData(), SWT.BOLD);
            boldFont = new Font(Display.getCurrent(), boldFontData);
        }

        settingsViewer.setContentProvider(new TreeContentProvider() {
            ResourceEditorClassPreference pref;

            @Override
            public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
                pref = null;
                if (newInput instanceof ResourceEditorClassPreference) {
                    pref = (ResourceEditorClassPreference) newInput;
                }
            }

            @Override
            public Object[] getElements(Object inputElement) {
                if (inputElement instanceof ResourceEditorClassPreference) {
                    Resource clazz = getSelectedClass();
                    Set<SettingItem> items = Sets.newLinkedHashSet();

                    hasRestrictions = false;
                    hasPossessedAspects = false;
                    qcrPropertyUris = Sets.newHashSet();

                    items.add(new AllPropertiesSettingItem(clazz.getURI(),
                            ResourceEditorClassPreference.SETTING_SHOW_ALWAYS));

                    if (preferences.getPreference(clazz) != null) {
                        ResourceEditorClassPreference preference = preferences.getPreference(clazz);
                        if (preference.getRestrictionsSetting() != ResourceEditorClassPreference.SETTING_UNKNOWN) {
                            items.add(new RestrictionsSettingItem(clazz.getURI(), preference
                                    .getRestrictionsSetting()));
                            hasRestrictions = true;
                        }
                        if (preference.getPossessedAspectsSetting() != ResourceEditorClassPreference.SETTING_UNKNOWN) {
                            items.add(new PossessedAspectsSettingItem(clazz.getURI(), preference
                                    .getPossessedAspectsSetting()));
                            hasPossessedAspects = true;
                        }
                        for (String propertyUri : preference.getQCRPropertyURIs()) {
                            items.add(new QCRPropertySettingItem(clazz.getURI(), propertyUri,
                                    preference.getQCRPropertySetting(propertyUri)));
                            qcrPropertyUris.add(propertyUri);
                        }
                    }

                    List<Resource> rootClasses = Lists.newArrayList(clazz);
                    boolean done = false;
                    while (!done) {
                        List<Resource> nextRoots = Lists.newArrayList();
                        for (Resource rootClass : rootClasses) {
                            for (Resource superClass : directSuperClasses.get(rootClass)) {
                                if (preferences.getPreference(superClass) != null) {
                                    ResourceEditorClassPreference preference = preferences
                                            .getPreference(superClass);
                                    if (!hasRestrictions
                                            && preference.getRestrictionsSetting() != ResourceEditorClassPreference.SETTING_UNKNOWN) {
                                        items.add(new RestrictionsSettingItem(superClass.getURI(),
                                                preference.getRestrictionsSetting()));
                                        hasRestrictions = true;
                                    }
                                    if (!hasPossessedAspects
                                            && preference.getPossessedAspectsSetting() != ResourceEditorClassPreference.SETTING_UNKNOWN) {
                                        items.add(new PossessedAspectsSettingItem(superClass
                                                .getURI(), preference.getPossessedAspectsSetting()));
                                        hasPossessedAspects = true;
                                    }
                                    for (String propertyUri : preference.getQCRPropertyURIs()) {
                                        if (qcrPropertyUris.contains(propertyUri)) {
                                            continue;
                                        }
                                        items.add(new QCRPropertySettingItem(superClass.getURI(),
                                                propertyUri, preference
                                                        .getQCRPropertySetting(propertyUri)));
                                        qcrPropertyUris.add(propertyUri);
                                    }
                                }
                                nextRoots.add(superClass);
                            }
                        }
                        if (nextRoots.size() == 0) {
                            done = true;
                        }
                        else {
                            rootClasses = Lists.newArrayList(nextRoots);
                        }
                    }
                    return items.toArray();
                }
                return null;
            }

            @Override
            public boolean hasChildren(Object element) {
                if (element instanceof ResourceEditorClassPreferences) {
                    return true;
                }
                else if (element instanceof AllPropertiesSettingItem) {
                    // / Check properties set on current class
                    if (pref.getPropertyURIs().size() > 0) {
                        return true;
                    }

                    // / Check if any of super classes have any properties!
                    Resource clazz = getSelectedClass();
                    for (Resource superClass : superClasses.get(clazz)) {
                        if (preferences.hasPropertySettings(superClass)) {
                            return true;
                        }
                    }
                }
                return false;
            }

            @Override
            public Object[] getChildren(Object parentElement) {
                if (parentElement instanceof AllPropertiesSettingItem) {
                    List<PropertySettingItem> properties = Lists.newArrayList();
                    Resource clazz = getSelectedClass();
                    for (Resource superClass : superClasses.get(clazz)) {
                        String superUri = superClass.getURI();
                        if (!preferences.containsClassURI(superUri)) {
                            continue;
                        }
                        for (String propertyUri : preferences.getPreference(superClass)
                                .getPropertyURIs()) {
                            Property property = taxonomyModel.getProperty(propertyUri);
                            int setting = preferences.getPropertySetting(superClass, property);
                            properties.add(new PropertySettingItem(superUri, propertyUri, setting));
                        }
                    }

                    // / Local preferences of selected class
                    String classUri = clazz.getURI();
                    for (String propertyUri : pref.getPropertyURIs()) {
                        Property property = taxonomyModel.getProperty(propertyUri);
                        int setting = preferences.getPropertySetting(clazz, property);
                        properties.add(new PropertySettingItem(classUri, propertyUri, setting));
                    }
                    return properties.toArray();
                }
                return null;
            }
        });
        settingsViewer.setLabelProvider(new StyledCellLabelProvider() {
            private final Styler builtinStyler = new Styler() {
                @Override
                public void applyStyles(TextStyle textStyle) {
                    textStyle.foreground = Display.getCurrent().getSystemColor(SWT.COLOR_GRAY);
                }
            };

            /**
             * This paint method ensures that inherited items keep the gray
             * foreground color.
             */
            @Override
            protected void paint(Event event, Object element) {
                if (isInheritedItem(element)) {
                    GC gc = event.gc;
                    gc.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));
                }
                super.paint(event, element);
            }

            /**
             * Determines if the provided element is an inherited SettingItem.
             * 
             * @param element
             *            element to be checked if inherited SettingItem
             * @return true if the element is an inherited SettingItem; false
             *         otherwise.
             */
            private boolean isInheritedItem(Object element) {
                if (element instanceof SettingItem) {
                    SettingItem item = (SettingItem) element;
                    String classUri = getSelectedClass().getURI();
                    return item.isInherited(classUri);
                }
                return false;
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

                Image columnImage = getColumnImage(element, columnIndex);
                if (isInheritedItem(element)) {
                    styledText.setStyle(0, styledText.length(), builtinStyler);
                    OverlayImageIcon icon = new OverlayImageIcon(columnImage, CorePlugin
                            .getDefault());
                    icon.addImageDecoration(CorePluginImages.IMG_OVERLAY_IMPORTED,
                            OverlayImageIcon.BOTTOM_LEFT);
                    columnImage = icon.createImage();
                }

                if (columnImage != null) {
                    cell.setImage(columnImage);
                }
                cell.setText(styledText.getString());
                cell.setStyleRanges(styledText.getStyleRanges());

                super.update(cell);
            }

            private Image getColumnImage(Object element, int columnIndex) {
                if (columnIndex == 0) {
                    if (element instanceof PropertySettingItem) {
                        PropertySettingItem item = (PropertySettingItem) element;
                        Property property = taxonomyModel.getProperty(item.getPropertyUri());

                        return labelProvider.getImage(property);
                    }
                    else if (element instanceof AllPropertiesSettingItem) {
                        return CorePlugin.getDefault().getImage(
                                CorePluginImages.IMG_SECTION_ALL_PROPERTIES);
                    }
                    else if (element instanceof QCRPropertySettingItem) {
                        return CorePlugin.getDefault().getImage(CorePluginImages.IMG_SECTION_QCR);
                    }
                    else if (element instanceof PossessedAspectsSettingItem) {
                        return CorePlugin.getDefault().getImage(CorePluginImages.IMG_APPLICATION);
                    }
                    else if (element instanceof RestrictionsSettingItem) {
                        return CorePlugin.getDefault().getImage(
                                CorePluginImages.IMG_SECTION_RESTRICTIONS);
                    }
                }
                return null;
            }

            private String getSuffixFromSetting(int setting) {
                String suffix = " (unknown)";
                if (setting == ResourceEditorClassPreference.SETTING_SHOW_ALWAYS) {
                    suffix = " (always)";
                }
                else if (setting == ResourceEditorClassPreference.SETTING_SHOW_ON_INSTANCES) {
                    suffix = " (instances only)";
                }
                else if (setting == ResourceEditorClassPreference.SETTING_SHOW_ON_SUBCLASSES) {
                    suffix = " (sub-classes only)";
                }
                return suffix;
            }

            private String getColumnText(Object element, int columnIndex) {
                if (columnIndex == 0) {
                    if (element instanceof AllPropertiesSettingItem) {
                        return "All Properties"; // / No settable value suffix
                    }
                    else if (element instanceof RestrictionsSettingItem) {
                        RestrictionsSettingItem item = (RestrictionsSettingItem) element;
                        String selectedUri = getSelectedClass().getURI();
                        int setting = item.getSetting();

                        String inherited = "";
                        if (!selectedUri.equals(item.getClassUri())) {
                            Resource clazz = taxonomyModel.getResource(item.getClassUri());
                            inherited = String.format(" [%s]", labelProvider.getText(clazz));
                        }
                        return "Restrictions" + getSuffixFromSetting(setting) + inherited;
                    }
                    else if (element instanceof QCRPropertySettingItem) {
                        QCRPropertySettingItem item = (QCRPropertySettingItem) element;
                        String selectedUri = getSelectedClass().getURI();
                        int setting = item.getSetting();

                        String inherited = "";
                        if (!selectedUri.equals(item.getClassUri())) {
                            Resource clazz = taxonomyModel.getResource(item.getClassUri());
                            inherited = String.format(" [%s]", labelProvider.getText(clazz));
                        }
                        Resource property = getOntModel().getResource(item.getPropertyUri());

                        return "Widget for '" + labelProvider.getText(property) + "'"
                                + getSuffixFromSetting(setting) + inherited;
                    }
                    else if (element instanceof PossessedAspectsSettingItem) {
                        PossessedAspectsSettingItem item = (PossessedAspectsSettingItem) element;
                        String selectedUri = getSelectedClass().getURI();
                        int setting = item.getSetting();

                        String inherited = "";
                        if (!selectedUri.equals(item.getClassUri())) {
                            Resource clazz = taxonomyModel.getResource(item.getClassUri());
                            inherited = String.format(" [%s]", labelProvider.getText(clazz));
                        }
                        return "Possessed Aspects" + getSuffixFromSetting(setting) + inherited;
                    }
                    else if (element instanceof PropertySettingItem) {
                        String selectedUri = getSelectedClass().getURI();
                        PropertySettingItem item = (PropertySettingItem) element;
                        Property property = taxonomyModel.getProperty(item.getPropertyUri());
                        int setting = item.getSetting();

                        String inherited = "";
                        if (!selectedUri.equals(item.getClassUri())) {
                            Resource clazz = taxonomyModel.getResource(item.getClassUri());
                            inherited = String.format(" [%s]", labelProvider.getText(clazz));
                        }
                        return labelProvider.getText(property) + getSuffixFromSetting(setting)
                                + inherited;
                    }

                }
                return null;
            }
        });

        MenuManager manager = new MenuManager();
        IMenuListener listener = new IMenuListener() {
            @Override
            public void menuAboutToShow(IMenuManager m) {
                if (settingsViewer.getSelection() instanceof StructuredSelection) {
                    Resource selectedClass = getSelectedClass();
                    SettingItem item = getSelectedSettingItem();

                    boolean isInherited = item.isInherited(selectedClass.getURI());
                    boolean isAllPropertiesSelected = (item instanceof AllPropertiesSettingItem);
                    boolean isPropertySettingSelected = (item instanceof PropertySettingItem);
                    // boolean isQCRPropertySelected = (item instanceof
                    // QCRPropertySettingItem);

                    Image baseImage;
                    Map<String, Integer> imageKeys = Maps.newLinkedHashMap();
                    imageKeys.put(CorePluginImages.IMG_OVERLAY_ADD, OverlayImageIcon.TOP_RIGHT);

                    OverlayImageIcon icon;
                    MenuManager addMenu = new MenuManager("Add");
                    if (isAllPropertiesSelected || isPropertySettingSelected) {
                        IAction addDefaultProperty = new Action() {
                            @Override
                            public void run() {
                                createPropertyPreference();
                            }
                        };
                        // TODO: Check for memory leaks!
                        // IDEA: Create a Table (guava) with the imageKey and
                        // overlayKey as row and column; and the resulting
                        // image as the table value
                        baseImage = CorePlugin.getDefault().getImage(
                                CorePluginImages.IMG_RDF_PROPERTY);
                        icon = new OverlayImageIcon(baseImage, CorePlugin.getDefault(), imageKeys);
                        addDefaultProperty.setImageDescriptor(ImageDescriptor.createFromImage(icon
                                .createImage()));
                        addDefaultProperty.setText("Default Property");
                        addMenu.add(addDefaultProperty);
                        addMenu.add(new Separator());
                    }

                    IAction addRestrictionWidget = new Action() {
                        @Override
                        public void run() {
                            createRestrictionsPreference();
                        }
                    };
                    addRestrictionWidget.setText("Restrictions");
                    addRestrictionWidget.setEnabled(!hasRestrictions);
                    baseImage = CorePlugin.getDefault().getImage(
                            CorePluginImages.IMG_SECTION_RESTRICTIONS);
                    icon = new OverlayImageIcon(baseImage, CorePlugin.getDefault(), imageKeys);
                    addRestrictionWidget.setImageDescriptor(ImageDescriptor.createFromImage(icon
                            .createImage()));
                    addMenu.add(addRestrictionWidget);

                    IAction addPossessedAspectWidget = new Action() {
                        @Override
                        public void run() {
                            createPossessedAspectsPreference();
                        }
                    };
                    addPossessedAspectWidget.setText("Possessed Aspects");
                    addPossessedAspectWidget.setEnabled(!hasPossessedAspects);
                    baseImage = CorePlugin.getDefault().getImage(CorePluginImages.IMG_APPLICATION);
                    icon = new OverlayImageIcon(baseImage, CorePlugin.getDefault(), imageKeys);
                    addPossessedAspectWidget.setImageDescriptor(ImageDescriptor
                            .createFromImage(icon.createImage()));
                    addMenu.add(addPossessedAspectWidget);

                    IAction addQCR = new Action() {
                        @Override
                        public void run() {
                            createQCRPropertyPreference();
                        }
                    };
                    addQCR.setText("QCR Property...");
                    baseImage = CorePlugin.getDefault().getImage(CorePluginImages.IMG_SECTION_QCR);
                    icon = new OverlayImageIcon(baseImage, CorePlugin.getDefault(), imageKeys);
                    addQCR.setImageDescriptor(ImageDescriptor.createFromImage(icon.createImage()));
                    addMenu.add(addQCR);

                    m.add(addMenu);
                    m.add(new Separator());

                    if (!isAllPropertiesSelected) {
                        MenuManager settingMenu = new MenuManager("Visiblity");

                        int setting = item.getSetting();
                        IAction alwaysAction = new Action() {
                            @Override
                            public void run() {
                                updateSetting(ResourceEditorClassPreference.SETTING_SHOW_ALWAYS);
                            }
                        };
                        alwaysAction.setText("Always");
                        alwaysAction
                                .setChecked(setting == ResourceEditorClassPreference.SETTING_SHOW_ALWAYS);
                        alwaysAction.setEnabled(!isInherited);

                        IAction subClassAction = new Action() {
                            @Override
                            public void run() {
                                updateSetting(ResourceEditorClassPreference.SETTING_SHOW_ON_SUBCLASSES);
                            }
                        };
                        subClassAction.setText("Only Sub-classes");
                        subClassAction
                                .setChecked(setting == ResourceEditorClassPreference.SETTING_SHOW_ON_SUBCLASSES);
                        subClassAction.setEnabled(!isInherited);

                        IAction instanceAction = new Action() {
                            @Override
                            public void run() {
                                updateSetting(ResourceEditorClassPreference.SETTING_SHOW_ON_INSTANCES);
                            }
                        };
                        instanceAction.setText("Only instances");
                        instanceAction
                                .setChecked(setting == ResourceEditorClassPreference.SETTING_SHOW_ON_INSTANCES);
                        instanceAction.setEnabled(!isInherited);

                        // IAction hideAction = new Action() {
                        // @Override
                        // public void run() {
                        // updateSetting(ResourceEditorClassPreference.SETTING_HIDDEN);
                        // }
                        // };
                        // hideAction.setText("Hidden");
                        // hideAction.setChecked(setting ==
                        // ResourceEditorClassPreference.SETTING_HIDDEN);
                        // hideAction.setEnabled(!isInherited);

                        settingMenu.add(alwaysAction);
                        settingMenu.add(subClassAction);
                        settingMenu.add(instanceAction);
                        // settingMenu.add(hideAction);

                        m.add(settingMenu);
                        m.add(new Separator());
                    }

                    IAction removeSetting = new Action() {
                        @Override
                        public void run() {
                            removeSetting();
                        }
                    };
                    removeSetting.setText("Remove");
                    removeSetting.setImageDescriptor(ImageDescriptor.createFromImage(CorePlugin
                            .getDefault().getImage(CorePluginImages.IMG_DELETE)));
                    removeSetting.setEnabled(!isAllPropertiesSelected && !isInherited);
                    m.add(removeSetting);
                }
            }
        };
        manager.addMenuListener(listener);
        manager.setRemoveAllWhenShown(true);
        Menu menu = manager.createContextMenu(settingsViewer.getControl());
        settingsViewer.getControl().setMenu(menu);
    }

    private void refreshSettingsViewer() {
        Resource clazz = getSelectedClass();
        if (clazz != null) {
            ResourceEditorClassPreference pref = preferences.getPreference(clazz.getURI());
            if (pref == null) {
                pref = new ResourceEditorClassPreference(clazz);
            }
            settingsViewer.setInput(pref);
            settingsViewer.expandToLevel(2);
        }
    }

    private Resource getSelectedClass() {
        StructuredSelection selection = (StructuredSelection) classesViewer.getSelection();
        if (selection != null && selection.getFirstElement() instanceof Resource) {
            return (Resource) selection.getFirstElement();
        }
        return null;
    }

    private SettingItem getSelectedSettingItem() {
        StructuredSelection selection = (StructuredSelection) settingsViewer.getSelection();
        if (selection != null && selection.getFirstElement() instanceof SettingItem) {
            return (SettingItem) selection.getFirstElement();
        }
        return null;
    }

    @Override
    public boolean performOk() {
        // TODO to avoid errors, if the class is needed again uncomment
        // ResourceEditorPreference.setResourceEditorPreferences(preferences);
        // ResourceEditorPreference.setCheckDomainRestrictions(checkDomainRestrictionsCheckbox
        // .getSelection());
        // ResourceEditorPreference.setHideRestrictionStatements(hideRestrictionCheckbox
        // .getSelection());
        return super.performOk();
    }

    @Override
    protected void performDefaults() {
        preferences = ResourceEditorPreference.DEFAULT_RESOURCE_EDITOR_PREFERENCES;
        // TODO to avoid errors, if the class is needed again uncomment
        // ResourceEditorPreference.setCheckDomainRestrictions(true);
        // ResourceEditorPreference.setHideRestrictionStatements(true);
        // ResourceEditorPreference.setResourceEditorPreferences(preferences);

        refreshTypesViewer();
        refreshSettingsViewer();

        super.performDefaults();
    }

    private void createRestrictionsPreference() {
        Resource clazz = getSelectedClass();
        preferences.setRestrictionSettings(clazz,
                ResourceEditorClassPreference.SETTING_SHOW_ON_SUBCLASSES);
        refreshSettingsViewer();
    }

    private void createPossessedAspectsPreference() {
        Resource clazz = getSelectedClass();

        preferences.setProposedAspectsSetting(clazz,
                ResourceEditorClassPreference.SETTING_SHOW_ON_SUBCLASSES);
        refreshSettingsViewer();
    }

    private void createQCRPropertyPreference() {
        ResourceSelectionDialog dialog = new ResourceSelectionDialog(getShell(), "Select Property",
                "Select a property from the list below.");
        dialog.setModel(getOntModel());
        dialog.setRootResources(Arrays.asList(new Resource[] { RDF.Property }));
        dialog.setHierarchicalProperties(Arrays.asList(new Property[] { RDFS.subPropertyOf,
                RDFS.subClassOf, RDF.type }));
        dialog.setAllowedResourceTypes(Arrays.asList(new Resource[] { RDF.Property }));

        if (dialog.open() == Window.OK) {
            Property property = dialog.getFirstSelectedResource().as(Property.class);
            if (!qcrPropertyUris.contains(property.getURI())) {
                Resource clazz = getSelectedClass();
                preferences.addQCRPropertySetting(clazz, property,
                        ResourceEditorClassPreference.SETTING_SHOW_ALWAYS);
                refreshSettingsViewer();
            }
        }

    }

    private void createPropertyPreference() {
        ResourceSelectionDialog dialog = new ResourceSelectionDialog(getShell(), "Select Property",
                "Select a property from the list below.");
        dialog.setModel(getOntModel());
        dialog.setRootResources(Arrays.asList(new Resource[] { RDF.Property }));
        dialog.setHierarchicalProperties(Arrays.asList(new Property[] { RDFS.subPropertyOf,
                RDFS.subClassOf, RDF.type }));
        dialog.setAllowedResourceTypes(Arrays.asList(new Resource[] { RDF.Property }));

        if (dialog.open() == Window.OK) {
            Property property = dialog.getFirstSelectedResource().as(Property.class);
            if (property != null) {
                Resource clazz = getSelectedClass();
                preferences.addPropertySetting(clazz, property,
                        ResourceEditorClassPreference.SETTING_SHOW_ALWAYS);
                refreshSettingsViewer();
            }
        }
    }

    private void removeSetting() {
        Resource clazz = getSelectedClass();
        SettingItem item = getSelectedSettingItem();
        if (item == null || item.isInherited(clazz.getURI())) {
            return;
        }
        if (item instanceof AllPropertiesSettingItem) {
            return;
        }

        if (item instanceof PropertySettingItem) {
            Property property = taxonomyModel.getProperty(((PropertySettingItem) item)
                    .getPropertyUri());
            if (property != null) {
                if (MessageDialog
                        .openConfirm(getShell(), "Remove Property",
                                "Are you sure you want to remove the selected default property from the class?")) {
                    preferences.removePropertySetting(clazz, property);
                    refreshSettingsViewer();
                }
            }
        }
        else if (item instanceof QCRPropertySettingItem) {
            Property property = taxonomyModel.getProperty(((QCRPropertySettingItem) item)
                    .getPropertyUri());
            if (property != null) {
                if (MessageDialog.openConfirm(getShell(), "Remove QCR Widget",
                        "Are you sure you want to remove the QCR widget from the class?")) {
                    preferences.getPreference(clazz).removeQCRPropertySetting(property);
                    refreshSettingsViewer();
                }
            }
        }
        else if (item instanceof RestrictionsSettingItem) {
            if (MessageDialog.openConfirm(getShell(), "Remove Restrictions",
                    "Are you sure you want to remove the restrictions widget from this class?")) {
                preferences.getPreference(clazz).setRestrictionsSetting(
                        ResourceEditorClassPreference.SETTING_UNKNOWN);
                refreshSettingsViewer();
            }
        }
        else if (item instanceof PossessedAspectsSettingItem) {
            if (MessageDialog
                    .openConfirm(getShell(), "Remove Possessed Aspects",
                            "Are you sure you want to remove the possessed aspects widget from this class?")) {
                preferences.getPreference(clazz).setPossessedAspectsSetting(
                        ResourceEditorClassPreference.SETTING_UNKNOWN);
                refreshSettingsViewer();
            }
        }
    }

    private void updateSetting(int setting) {
        Resource clazz = getSelectedClass();
        SettingItem item = getSelectedSettingItem();
        if (item == null || item instanceof AllPropertiesSettingItem) {
            return;
        }
        ResourceEditorClassPreference preference = preferences.getPreference(clazz);
        if (preference == null) {
            preference = new ResourceEditorClassPreference(clazz);
            preferences.addPreference(preference);
        }
        if (item instanceof PropertySettingItem) {
            preference.addPropertySetting(((PropertySettingItem) item).getPropertyUri(), setting);
        }
        if (item instanceof QCRPropertySettingItem) {
            preference.addQCRPropertySetting(((QCRPropertySettingItem) item).getPropertyUri(),
                    setting);
        }
        if (item instanceof RestrictionsSettingItem) {
            preference.setRestrictionsSetting(setting);
        }
        if (item instanceof PossessedAspectsSettingItem) {
            preference.setPossessedAspectsSetting(setting);
        }
        refreshSettingsViewer();
    }

    /**
     * Internal class for representing a settable aspect
     * 
     * @author Mike Henrichs
     * 
     */
    private abstract class SettingItem {
        private String classUri;
        private int setting;

        public SettingItem(String classUri, int setting) {
            this.classUri = classUri;
            this.setting = setting;
        }

        public boolean isInherited(String classUri) {
            return !this.classUri.equals(classUri);
        }

        public String getClassUri() {
            return classUri;
        }

        public int getSetting() {
            return setting;
        }
    }

    /**
     * Internal class for representing the settable item for 'All Properties'
     * 
     * @author Mike Henrichs
     * 
     */
    private class AllPropertiesSettingItem extends SettingItem {

        public AllPropertiesSettingItem(String classUri, int setting) {
            super(classUri, setting);
        }
    }

    /**
     * Internal class for representing the settable item for a property within
     * 'All Properties'
     * 
     * @author Mike Henrichs
     * 
     */
    private class PropertySettingItem extends SettingItem {
        private String propertyUri;

        public PropertySettingItem(String classUri, String propertyUri, int setting) {
            super(classUri, setting);
            this.propertyUri = propertyUri;
        }

        public String getPropertyUri() {
            return propertyUri;
        }
    }

    /**
     * Internal class for representing the settable item for 'QCR Widget'
     * 
     * @author Mike Henrichs
     * 
     */
    private class QCRPropertySettingItem extends SettingItem {
        private String propertyUri;

        public QCRPropertySettingItem(String classUri, String propertyUri, int setting) {
            super(classUri, setting);
            this.propertyUri = propertyUri;
        }

        public String getPropertyUri() {
            return propertyUri;
        }
    }

    /**
     * Internal class for representing the settable item for 'Proposed Aspect
     * Widget'
     * 
     * @author Mike Henrichs
     * 
     */
    private class PossessedAspectsSettingItem extends SettingItem {

        public PossessedAspectsSettingItem(String classUri, int setting) {
            super(classUri, setting);
        }
    }

    /**
     * Internal class for representing the settable item for 'Restriction
     * Widget'
     * 
     * @author Mike Henrichs
     * 
     */
    private class RestrictionsSettingItem extends SettingItem {

        public RestrictionsSettingItem(String classUri, int setting) {
            super(classUri, setting);
        }
    }
}
