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


import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.topbraid.spin.model.SPINFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.CorePluginImages;
import com.semmtech.plugin.semmweb.core.actions.EditRestrictionSuite;
import com.semmtech.plugin.semmweb.core.dialog.RadioSelectionInputDialog;
import com.semmtech.plugin.semmweb.core.dialog.ResourceSelectionDialog;
import com.semmtech.plugin.semmweb.core.dnd.PropertyTransfer;
import com.semmtech.plugin.semmweb.core.forms.editor.AbstractModelResourceContent;
import com.semmtech.plugin.semmweb.core.model.ResourceEditorClassPreference;
import com.semmtech.plugin.semmweb.core.model.ResourceEditorClassPreferences;
import com.semmtech.plugin.semmweb.core.preferences.ResourceEditorPreference;
import com.semmtech.plugin.semmweb.core.ui.forms.RestrictionFormColors;
import com.semmtech.plugin.semmweb.core.viewers.ResourceNameComparator;
import com.semmtech.semantics.ontology.OntClassUtil;
import com.semmtech.semantics.ontology.OntResourceUtil;
import com.semmtech.ui.plugin.widgets.Widgets;


/**
 * 
 * @author Sander Stolk
 */
public class ModelResourcePropertiesContentPart extends AbstractModelResourceContentPart {
    private static final Logger logger = Logger.getLogger(ModelResourcePropertiesContentPart.class);

    private final LinkedHashMap<Property, PropertyContentPart> propertyParts;
    private Set<Property> assertedProperties;
    private Set<Property> proposedProperties;
    private Set<Property> properties;

    private Object errorMessage;
    // private Composite parent;
    // private Composite clientComposite;
    private Composite dropRegionComposite;

    private Action addAction;
    private Menu generalPopupMenu;

    private ResourceEditorPreference editorPreference;

    public ModelResourcePropertiesContentPart(AbstractModelResourceContent contentParent,
            Composite parent, FormToolkit toolkit) {
        super(contentParent, parent, toolkit);

        // maintain insertion order for propertyParts rather than access-order
        propertyParts = new LinkedHashMap<>(10, 0.75f, false);

        editorPreference = ResourceEditorPreference.fromProject(contentParent.getProject());

        initialize();
        createContent();
    }

    private void initialize() {
    }

    @Override
    public void dispose() {
        Widgets.disposeIfExists(dropRegionComposite);
        Widgets.disposeIfExists(generalPopupMenu);
        super.dispose();
    }

    private void createContent() {
        logger.debug("create Content called");
        FormColors formColors = new RestrictionFormColors(getDisplay());
        toolkit = new FormToolkit(formColors);
        toolkit.setBorderStyle(SWT.WRAP | SWT.MULTI);

        TableWrapLayout layout = new TableWrapLayout();
        layout.numColumns = 1;
        layout.leftMargin = 15;
        layout.rightMargin = 15;
        layout.topMargin = 10;
        layout.bottomMargin = 10;
        layout.verticalSpacing = 5;
        setLayout(layout);

        generalPopupMenu = createGeneralPopupMenu(this);
        setMenu(generalPopupMenu);
        addAction = new Action("Add Property...") {
            @Override
            public void run() {
                executeAddProperty();
            }
        };
        Image addPropertyImage = CorePlugin.getDefault().getImage(CorePluginImages.IMG_ADD_PLUS);
        ImageDescriptor addImageDescriptor = ImageDescriptor.createFromImage(addPropertyImage);
        addAction.setImageDescriptor(addImageDescriptor);

        createHeading();
        refreshProperties();
        createDropRegion();

        refresh();
    }

    /**
     * Returns a list of properties which are proposed based on preferences
     * 
     * @return
     */
    private void findProposedProperties() {
        proposedProperties = Sets.newHashSet();
        OntModel ontModel = getModelProvider().getOntModel();

        // / Lists all types (and their superClasses)
        Set<Resource> types = getResource().listRDFTypes(false).toSet();
        ResourceEditorClassPreferences preferences = ResourceEditorPreference.fromProject(
                contentParent.getProject()).getResourceEditorPreferences();
        for (Resource type : types) {
            if (!preferences.containsClassURI(type.getURI())) {
                continue;
            }
            ResourceEditorClassPreference pref = preferences.getPreference(type);
            Map<String, Integer> properties = pref.getAllPropertiesProperties();
            for (String propertyUri : properties.keySet()) {
                int setting = properties.get(propertyUri).intValue();
                if ((setting & ResourceEditorClassPreference.SETTING_SHOW_ON_INSTANCES) != 0) {
                    proposedProperties.add(ontModel.getProperty(propertyUri));
                }
            }
        }

        if (OntResourceUtil.isClass(getResource())) {
            for (ExtendedIterator<OntClass> iter = OntClassUtil.listSuperClasses(getResource().as(
                    OntClass.class)); iter.hasNext();) {
                OntClass superClass = iter.next();
                if (!preferences.containsClassURI(superClass.getURI())) {
                    continue;
                }
                ResourceEditorClassPreference pref = preferences.getPreference(superClass);
                Map<String, Integer> properties = pref.getAllPropertiesProperties();
                for (String propertyUri : properties.keySet()) {
                    Integer setting = properties.get(propertyUri);
                    if ((setting & ResourceEditorClassPreference.SETTING_SHOW_ON_SUBCLASSES) != 0) {
                        proposedProperties.add(ontModel.createProperty(propertyUri));
                    }
                }
            }
        }
    }

    /**
     * Returns a list of all properties used in statements having resource as
     * their subject
     * 
     * @return
     */
    private void findAssertedProperties() {
        assertedProperties = Sets.newHashSet();
        Resource resource = getResource();
        for (Statement statement : resource.listProperties().toList()) {
            assertedProperties.add(statement.getPredicate());
        }
    }

    private void createHeading() {
        DropTarget dropTarget = new DropTarget(this, DND.DROP_MOVE | DND.DROP_COPY);
        dropTarget.setTransfer(new Transfer[] { PropertyTransfer.getInstance() });
        dropTarget.addDropListener(this);

        String labelText = "You can add additional properties to this resource"
                + " by dragging properties onto this section.";
        Label label = toolkit.createLabel(this, labelText, SWT.WRAP);
        TableWrapData layoutData = new TableWrapData(TableWrapData.FILL, TableWrapData.TOP, 1, 1);
        label.setLayoutData(layoutData);
        label.setMenu(generalPopupMenu);
    }

    private Menu createGeneralPopupMenu(Control control) {
        MenuManager menuManager = new MenuManager();
        Menu menu = menuManager.createContextMenu(control);
        menuManager.setRemoveAllWhenShown(true);
        menuManager.addMenuListener(new IMenuListener() {
            @Override
            public void menuAboutToShow(IMenuManager manager) {
                manager.add(addAction);
            }
        });
        return menu;
    }

    public void refreshProperties() {
        // Find the proposed and actually asserted properties
        findAssertedProperties();
        findProposedProperties();

        OntModel model = getModelProvider().getOntModel();
        properties = Sets.newHashSet(assertedProperties);
        for (Property p : proposedProperties) {
            properties.add(model.getProperty(p.getURI()));
        }

        // Clear all existing parts no longer in use
        List<Property> clearProperties = Lists.newArrayList();
        if (propertyParts.size() > 0) {
            for (Property p : propertyParts.keySet()) {
                if (!properties.contains(p)) {
                    clearProperties.add(p);
                }
            }
            for (Property p : clearProperties) {
                propertyParts.get(p).dispose();
                propertyParts.remove(p);
            }
        }

        // Find any properties which have a restriction for this class
        Resource resource = getResource();
        List<Property> ordered = Lists.newArrayList(properties);
        Collections.sort(ordered, new Comparator<Property>() {
            ResourceNameComparator nameComparator = new ResourceNameComparator(getModelProvider()
                    .getLabelProvider());

            @Override
            public int compare(Property p1, Property p2) {
                if (p1.equals(p2)) {
                    return 0;
                }

                // Order the properties based on whether they were asserted
                if (assertedProperties.contains(p1) && !assertedProperties.contains(p2)) {
                    return -1;
                }
                if (!assertedProperties.contains(p1) && assertedProperties.contains(p2)) {
                    return 1;
                }

                // Order the properties based on previous ordering
                if (propertyParts.keySet().contains(p1) && !propertyParts.keySet().contains(p2)) {
                    return -1;
                }
                if (!propertyParts.keySet().contains(p1) && propertyParts.keySet().contains(p2)) {
                    return 1;
                }
                if (propertyParts.keySet().contains(p1) && propertyParts.keySet().contains(p2)) {
                    for (Property p : propertyParts.keySet()) {
                        if (p.equals(p1)) {
                            return -1;
                        }
                        if (p.equals(p2)) {
                            return 1;
                        }
                    }
                }

                // Order the properties based on their names.
                return nameComparator.compare(p1, p2);
            }

        });

        List<Property> currentOrderingPropertyParts = Lists.newArrayList(propertyParts.keySet());
        for (int i = 0; i < ordered.size(); i++) {
            Property property = ordered.get(i);
            int nextOrderedPartIndex = currentOrderingPropertyParts.indexOf(property);
            if (nextOrderedPartIndex < 0) {
                nextOrderedPartIndex = currentOrderingPropertyParts.size();
            }
            List<Property> orderingPropertyParts = Lists.newArrayList(currentOrderingPropertyParts);
            for (int j = i; j < nextOrderedPartIndex; j++) {
                Property p = orderingPropertyParts.get(j);
                propertyParts.get(p).dispose();
                propertyParts.remove(p);
                currentOrderingPropertyParts.remove(p);
            }
        }

        for (Property property : ordered) {
            PropertyContentPart part = null;
            // If no part exists yet for this property, create one.
            if (!propertyParts.containsKey(property)) {
                part = new PropertyContentPart(contentParent, this, toolkit, property);
                TableWrapData layoutData = new TableWrapData(TableWrapData.FILL, TableWrapData.TOP,
                        1, 1);
                layoutData.indent = 0;
                layoutData.grabHorizontal = true;
                layoutData.grabVertical = true;
                part.setLayoutData(layoutData);

                redraw();
                propertyParts.put(property, part);
            }
            part = propertyParts.get(property);

            boolean hideRestrictions = ResourceEditorPreference.fromProject(
                    contentParent.getProject()).hideRestrictionStatements();

            // See what existing objects have become obsolete and which still
            // need to be created.
            List<RDFNode> previousObjects = part.listCurrentObjects();
            List<RDFNode> currentObjects = Lists.newArrayList();
            for (Statement statement : resource.listProperties(property).toSet()) {
                currentObjects.add(statement.getObject());
            }
            List<RDFNode> obsoleteObjects = Lists.newArrayList(previousObjects);
            obsoleteObjects.removeAll(currentObjects);
            List<RDFNode> newObjects = Lists.newArrayList(currentObjects);
            newObjects.removeAll(previousObjects);

            // Remove all obsoleteObjects in the current part
            part.discardObjects(obsoleteObjects);

            // Create the currentObjects in the current part (or update if
            // already existent)
            boolean updatedParts = false;
            for (RDFNode object : currentObjects) {
                boolean partCreated = false;
                boolean isRestriction = false;
                @SuppressWarnings("unused")
                boolean noTypes = false;

                if (!newObjects.contains(object)) {
                    // Update the existing AbstractPropertyObjectContentPart
                    updatedParts = part.updateObject(object) || updatedParts;
                }
                else {
                    // Create new AbstractPropertyObjectContentPart
                    if (object.isResource()) {
                        Resource objectResource = object.asResource();
                        boolean isList = (objectResource.hasProperty(RDF.type) && RDF.List
                                .equals(objectResource.getPropertyResourceValue(RDF.type)))
                                || (objectResource.hasProperty(RDF.first) && objectResource
                                        .hasProperty(RDF.rest));

                        if (!partCreated && isList && !RDF.nil.equals(objectResource)) {
                            part.createPropertyList(objectResource);
                            partCreated = true;
                        }

                        if (!partCreated && SPINFactory.asQuery(objectResource) != null) {
                            part.createQueryContentPart(objectResource, false);
                            partCreated = true;
                        }

                        if (!getModelProvider().getLabelProvider().hasType(objectResource)) {
                            noTypes = true;
                        }
                        else if (getModelProvider().getLabelProvider().isInstanceOf(objectResource,
                                OWL.Restriction)) {
                            isRestriction = true;
                        }

                        // boolean duplicate = false;
                        // if (object.isAnon() && resource.isAnon())
                        // duplicate = object.getId().equals(resource.getId());
                        // else if (!object.isAnon() && !resource.isAnon())
                        // duplicate =
                        // object.getURI().equals(resource.getURI());

                        // / TODO: getResource always returns the active
                        // resource;
                        // this causes errors with the anonymous (loop)
                        // if (isNestedAnonymous && !duplicate) {
                        // logger.info(String.format("About to create a AnonymousResourcePart for object \"%s\"",
                        // object.toString()));
                        // part.createAnonymousResource(object);
                        // partCreated = true;
                        // }
                    }

                    if (!partCreated
                            && (!isRestriction || !property.equals(RDFS.subClassOf) || !hideRestrictions)) {
                        part.createPropertyStatementContentPart(object, false);
                        partCreated = true;
                    }
                }
            }
            if (!obsoleteObjects.isEmpty() || updatedParts) {
                part.refresh();
            }
        }
    }

    private void createDropRegion() {
        logger.debug("create dropreagion called");
        String tooltip = "Drop a property onto this area to add it to this resource.";
        String instruction = "Drop a property onto this area or use <a>this link</a> to add one";
        if (!Widgets.isNullOrDisposed(dropRegionComposite)) {
            dropRegionComposite.dispose();
            Widgets.layoutControlUpToScrollableParent(dropRegionComposite);
        }
        dropRegionComposite = new Composite(getParent(), SWT.NONE);
        dropRegionComposite.setLayoutData(new GridData(GridData.FILL, SWT.BOTTOM, true, false));

        DropTarget dropTarget = new DropTarget(dropRegionComposite, DND.DROP_MOVE | DND.DROP_COPY);
        dropTarget.setTransfer(new Transfer[] { PropertyTransfer.getInstance() });
        dropTarget.addDropListener(this);

        TableWrapLayout layout = new TableWrapLayout();
        layout.leftMargin = 11;
        layout.bottomMargin = 4;
        layout.horizontalSpacing = 7;
        layout.numColumns = 2;
        dropRegionComposite.setLayout(layout);
        dropRegionComposite.setToolTipText(tooltip);

        Label icon = new Label(dropRegionComposite, SWT.NONE);
        icon.setToolTipText(tooltip);
        icon.setImage(CorePlugin.getDefault().getImage(CorePluginImages.IMG_DROP_INTO));
        icon.setLayoutData(new TableWrapData(TableWrapData.LEFT, TableWrapData.BOTTOM, 1, 1));

        Link link = new Link(dropRegionComposite, SWT.NONE);
        link.setText(instruction);
        link.setToolTipText(tooltip);
        link.setLayoutData(new TableWrapData(TableWrapData.LEFT, TableWrapData.BOTTOM, 1, 1));
        link.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                executeAddProperty();
            }
        });
    }

    private Resource browseForProperty(Resource dialogRange) {
        ResourceSelectionDialog dialog = new ResourceSelectionDialog(getShell(), "Select Property",
                "Select a property from the list below.");
        dialog.clearHierarchicalProperties();
        dialog.clearRootResources();
        dialog.clearExcludedResources();
        dialog.addHierarchicalProperties(new Property[] { RDF.type, RDFS.subClassOf });
        dialog.addRootResources(new Resource[] { dialogRange });
        dialog.setAllowedResourceTypes(new Resource[] { dialogRange });

        dialog.setModel(getModelProvider().getOntModel());

        if (dialog.open() == Window.OK) {
            return dialog.getFirstSelectedResource();
        }
        return null;
    }

    @Override
    public void drop(DropTargetEvent event) {

        logger.debug("drop called!");
        if (errorMessage != null) {
            MessageDialog.openWarning(getShell(), "Invalid Property",
                    "The property cannot be added to this resource. " + errorMessage);
        }
        else if (PropertyTransfer.getInstance().isSupportedType(event.currentDataType)) {
            Property property = (Property) event.data;
            logger.debug("drop -> property = " + property.getURI());

            addPropertyPart(property);
        }
    }

    private void addPropertyPart(Property property) {
        logger.debug("addPropertyPart called!");
        boolean hasPropertyPart = propertyParts.containsKey(property);
        boolean hasDomain = property.hasProperty(RDFS.domain);
        boolean isDomain = false;
        boolean isDomainSubClass = false;
        boolean isDomainInstance = false;

        OntModel model = getModelProvider().getOntModel();
        Resource saveResource = (getResource().isAnon()) ? model.createResource(getResource()
                .getId()) : model.createResource(getResource().getURI());
        OntResource resource = model.getOntResource(saveResource);

        OntClass domain = null;
        OntClass resourceAsClass = null;
        OntClass typeClass = null;

        if (editorPreference.checkDomainRestrictions() && hasDomain && !hasPropertyPart) {
            if (resource.isClass()) {
                if (resource.isAnon()) {
                    resourceAsClass = resource.asClass();
                }
                else {
                    resourceAsClass = model.getOntClass(resource.getURI());
                }
            }

            Set<OntClass> typeClasses = new HashSet<>();
            if (resource.hasProperty(RDF.type)) {
                StmtIterator listpropertiesIterator = resource.listProperties(RDF.type);
                while (listpropertiesIterator.hasNext()) {
                    Statement nextStmt = listpropertiesIterator.next();
                    RDFNode typeNode = nextStmt.getObject();
                    if (typeNode.isResource()) {
                        OntResource ontResource = model.getOntResource(typeNode.asResource());
                        if (ontResource.isClass()) {
                            OntClass ontClass = ontResource.asClass();
                            typeClasses.add(ontClass);
                            typeClasses.addAll(ontClass.listSuperClasses().toSet());
                        }
                    }
                }

                // MIKE: For some reason the listSuperClasses used on
                // typeClasses did not propagate correctly;
                // for instance a individual of a type which is a sub-sub-class
                // of owl:Thing, did not register
                // as a sub-class of rdfs:Resource (or even owl:Thing) - the
                // loop before forces the subClassOf
                // property to be checked again, until no new superClasses have
                // been found.

                boolean done = false;
                while (!done) {
                    int before = typeClasses.size();
                    for (OntClass clazz : Sets.newHashSet(typeClasses)) {
                        typeClasses.addAll(clazz.listSuperClasses().toSet());
                    }
                    done = (before == typeClasses.size());
                }
            }
            Resource domainResource = property.getPropertyResourceValue(RDFS.domain);
            domain = domainResource.as(OntClass.class);

            Set<OntClass> classes = (resourceAsClass != null) ? resourceAsClass.listSuperClasses()
                    .toSet() : new HashSet<OntClass>();
            Set<OntClass> domainClasses = domain.listSubClasses().toSet();

            if (resourceAsClass != null) {
                classes.add(resourceAsClass);
            }

            domainClasses.add(domain);

            isDomain = (resourceAsClass != null) ? domain.equals(resourceAsClass) : false;
            isDomainSubClass = (Sets.intersection(domainClasses, classes).size() > 0);
            isDomainInstance = (Sets.intersection(domainClasses, typeClasses).size() > 0);

            logger.debug("drop -> isDomain = " + isDomain);
            logger.debug("drop -> isDomainSubClass = " + isDomainSubClass);
            logger.debug("drop -> isDomainInstance = " + isDomainInstance);
        }

        // if the domain restrictions not have to be checked the creation is
        // forced by default
        boolean forceCreate = !editorPreference.checkDomainRestrictions();
        boolean createRestriction = false;

        if (editorPreference.checkDomainRestrictions() && !hasPropertyPart && hasDomain
                && !isDomainInstance) {
            LabelProvider labelProvider = getModelProvider().getLabelProvider();
            String domainName = labelProvider.getText(domain);
            String propertyName = labelProvider.getText(property);
            String message = null;
            String defaultOption = "nothing";

            if (isDomain || isDomainSubClass) {
                message = "The property " + propertyName
                        + " you dragged onto this resource has the domain " + domainName
                        + ", and this resource is " + ((isDomainSubClass) ? "a sub class of " : "")
                        + "" + domainName + ".\n\nWhat whould you like to do?";
                defaultOption = "restriction";
            }
            else {
                message = "The property " + propertyName
                        + " you dragged onto this resource has the domain " + domainName
                        + ". This resource is an instance of " + labelProvider.getText(typeClass)
                        + ", which does not appears to be a (sub class of) " + domainName
                        + ". \n\nWhat whould you like to do?";
                defaultOption = "nothing";
            }

            Map<String, String> options = Maps.newLinkedHashMap();
            options.put("restriction", "Create a restriction on property " + propertyName);
            options.put("create", "Still add property, possibly resulting in an invalid model");
            options.put("nothing", "Do nothing");

            RadioSelectionInputDialog dialog = new RadioSelectionInputDialog(getShell(),
                    "Drop Property", message, options, defaultOption);
            if (dialog.open() == Window.OK) {
                String selected = dialog.getSelected();
                if (selected.equals("create")) {
                    forceCreate = true;
                }
                else if (selected.equals("restriction")) {
                    createRestriction = true;
                }
            }
        }

        if (!hasPropertyPart) {
            if (forceCreate || !hasDomain || isDomainInstance) {
                PropertyContentPart part = new PropertyContentPart(contentParent, this, toolkit,
                        property);
                TableWrapData layoutData = new TableWrapData(TableWrapData.FILL, TableWrapData.TOP,
                        1, 1);
                layoutData.indent = 0;
                layoutData.grabHorizontal = true;
                layoutData.grabVertical = true;
                part.setLayoutData(layoutData);

                part.setRestrictionsOnly(false);
                // managedForm.addPart(part);
                propertyParts.put(property, part);
                hasPropertyPart = true;
                refresh();
            }
            else if (createRestriction) {
                EditRestrictionSuite.createRestriction(getModelProvider(), getShell(), property,
                        getResource());
            }
        }
        else {
            PropertyContentPart part = propertyParts.get(property);
            if ((part != null) && !part.isEmpty()) {
                Resource range = property.getPropertyResourceValue(RDFS.range);
                if (range != null
                        && (model.contains(range, RDF.type, RDFS.Class)
                                || model.contains(range, RDF.type, OWL.Class) || model.contains(
                                range, RDF.type, RDF.Property))) {
                    part.createEmptyStatement(); // createEmptyStatement creates
                                                 // Browse statements
                }
                else {
                    part.createNewLiteralStatement(); // createNewLiteralStatement
                                                      // creates Literal
                                                      // statements
                }
            }
            refresh();
        }
    }

    public void executeAddProperty() {
        Resource resource = browseForProperty(RDF.Property);
        if (resource != null) {
            Property property = getResource().getModel().createProperty(resource.getURI());
            addPropertyPart(property);
        }
    }

    /**
     * Refreshes the composite (layout every descendant).
     */
    public void refresh() {
        logger.debug("(" + getResource().toString() + ") refresh called!");
        super.refresh();
    }

}
