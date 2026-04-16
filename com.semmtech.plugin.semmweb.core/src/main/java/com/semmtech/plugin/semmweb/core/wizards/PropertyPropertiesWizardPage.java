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

package com.semmtech.plugin.semmweb.core.wizards;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.CorePluginImages;
import com.semmtech.plugin.semmweb.core.dialog.ResourceSelectionDialog;
import com.semmtech.plugin.semmweb.core.model.IModelProvider;
import com.semmtech.semantics.sparql.QueryBuilder;
import com.semmtech.semantics.vocabulary.SEMM;


@SuppressWarnings("unused")
public class PropertyPropertiesWizardPage extends WizardPage {
    private static Logger logger = Logger.getLogger(PropertyPropertiesWizardPage.class);

    private OntModel model;
    private Property property;

    private final LabelProvider labelProvider;
    private Optional<Boolean> semmImported = Optional.absent();

    private Property inverseProperty = null;
    private Property superProperty = null;
    private Resource domainResource = null;
    private Resource rangeResource = null;
    private Resource subjectRoleResource = null;
    private Resource objectRoleResource = null;

    private Button followSuperRadio;
    private Button followInverseRadio;
    private Button noFollowRadio;

    private PropertyWizardPageField domainField;
    private PropertyWizardPageField rangeField;
    private PropertyWizardPageField subjectRoleField;
    private PropertyWizardPageField objectRoleField;

    private List<Resource> properties;

    private Text inverseUriText;

    private Label inverseIconLabel;

    private Label superIconLabel;

    private Text superUriText;

    public PropertyPropertiesWizardPage(String pageName, OntModel model, Property property) {
        super(pageName);
        setTitle("Property");
        setDescription("Specify other properties of the new property.");

        this.model = model;
        this.property = property;

        IModelProvider provider = CorePlugin.getDefault().getActiveModelProvider();
        labelProvider = provider.getLabelProvider();

        initializeProperties();
    }

    private void initializeProperties() {
        if (properties == null) {
            properties = new ArrayList<>(model.listResourcesWithProperty(RDF.type, RDF.Property)
                    .toList());
        }
    }

    private boolean isSemmImported() {
        if (!semmImported.isPresent()) {

            QueryBuilder builder = QueryBuilder.createAsk();

            builder.addTriplePattern(Var.alloc("x"), OWL.imports, SEMM.NAMESPACE);
            boolean ask = builder.execAsk(model);
            semmImported = Optional.fromNullable(Boolean.valueOf(ask));
        }
        return semmImported.get().booleanValue();
    }

    @Override
    public void createControl(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL
                | GridData.GRAB_VERTICAL));
        composite.setLayout(new GridLayout(4, false));

        Label label = new Label(composite, SWT.NONE);
        label.setText("Please select properties which will be inverse or super properties of the newly created property.");
        GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
        layoutData.heightHint = 25;
        layoutData.horizontalSpan = 4;
        label.setLayoutData(layoutData);

        createSuperPropertyControls(composite);
        createInversePropertyControls(composite);
        new Label(composite, SWT.NONE);

        label = new Label(composite, SWT.HORIZONTAL | SWT.SEPARATOR);
        layoutData = new GridData(GridData.FILL_HORIZONTAL);
        layoutData.horizontalSpan = 4;
        label.setLayoutData(layoutData);

        label = new Label(composite, SWT.WRAP);
        label.setText("Additionaly specify if domain and ranges and/or subject or object roles should follow either the inverse or super property. Or select these values independent of these properties.");
        layoutData = new GridData(GridData.FILL_HORIZONTAL);
        layoutData.heightHint = 35;
        layoutData.verticalAlignment = SWT.TOP;
        layoutData.widthHint = 300;
        layoutData.grabExcessHorizontalSpace = false;
        layoutData.horizontalSpan = 4;
        layoutData.verticalIndent = 6;
        label.setLayoutData(layoutData);

        createFollowControls(composite);

        domainField = new PropertyWizardPageField(composite, "Domain:");
        domainField.setResource(domainResource);
        domainField.setChooseListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                Resource clazz = chooseClass("Select Domain",
                        "Please select a resource below to be used as domain.");
                if (clazz != null) {
                    domainResource = clazz;
                    domainField.setResource(clazz);
                    validatePage();
                }
            }
        });
        rangeField = new PropertyWizardPageField(composite, "Range:");
        rangeField.setResource(rangeResource);
        rangeField.setChooseListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                Resource clazz = chooseClass("Select Range",
                        "Please select a resource below to be used as range.");
                if (clazz != null) {
                    rangeResource = clazz;
                    rangeField.setResource(clazz);
                    validatePage();
                }
            }
        });

        if (isSemmImported()) {
            subjectRoleField = new PropertyWizardPageField(composite, "Subject role:");
            subjectRoleField.setResource(subjectRoleResource);
            subjectRoleField.setChooseListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent event) {
                    Resource clazz = chooseRole("Select Subject Role",
                            "Please select a resource below to be used as subject role.");
                    if (clazz != null) {
                        subjectRoleResource = clazz;
                        subjectRoleField.setResource(clazz);
                        validatePage();
                    }
                }
            });
            objectRoleField = new PropertyWizardPageField(composite, "Object role:");
            objectRoleField.setResource(objectRoleResource);
            objectRoleField.setChooseListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent event) {
                    Resource clazz = chooseRole("Select Subject Role",
                            "Please select a resource below to be used as object role.");
                    if (clazz != null) {
                        objectRoleResource = clazz;
                        objectRoleField.setResource(clazz);
                        validatePage();
                    }
                }
            });
        }
        setControl(composite);
    }

    protected Resource chooseRole(String title, String message) {
        ResourceSelectionDialog dialog = new ResourceSelectionDialog(getShell(), title, message);

        dialog.clearHierarchicalProperties();
        dialog.clearRootResources();
        dialog.clearExcludedResources();

        dialog.addHierarchicalProperties(new Property[] { RDFS.subClassOf });
        dialog.addRootResources(new Resource[] { SEMM.Role });
        dialog.setAllowedResourceTypes(new Resource[] { OWL.Class });
        dialog.setModel(model);

        if (dialog.open() == Window.OK) {
            return dialog.getFirstSelectedResource();
        }

        return null;
    }

    protected Resource chooseClass(String title, String message) {
        ResourceSelectionDialog dialog = new ResourceSelectionDialog(getShell(), title, message);

        dialog.clearHierarchicalProperties();
        dialog.clearRootResources();
        dialog.clearExcludedResources();

        dialog.addHierarchicalProperties(new Property[] { RDF.type, RDFS.subClassOf });
        dialog.addRootResources(new Resource[] { RDFS.Resource });
        dialog.setAllowedResourceTypes(new Resource[] { RDFS.Class, OWL.Class });
        dialog.setModel(model);

        if (dialog.open() == Window.OK) {
            return dialog.getFirstSelectedResource();
        }

        return null;
    }

    private void createInversePropertyControls(Composite parent) {
        Label label = new Label(parent, SWT.NONE);
        GridData layoutData = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        layoutData.widthHint = 110;
        label.setLayoutData(layoutData);
        label.setText("Inverse:");

        inverseIconLabel = new Label(parent, SWT.NONE);
        inverseIconLabel.setLayoutData(new GridData(16, 16));
        inverseIconLabel.setImage(labelProvider.getImage(inverseProperty));

        inverseUriText = new Text(parent, SWT.BORDER);
        inverseUriText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        inverseUriText.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent event) {
                boolean delete = (event.keyCode == SWT.DEL) || (event.keyCode == SWT.BS);
                if (delete) {
                    clearInverseProperty();
                }
                else {
                    MessageDialog
                            .openInformation(getShell(), "Disabled",
                                    "Please use the button next to this text field to choose a inverse property.");
                }
                event.doit = false;
            }
        });
        inverseUriText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        inverseUriText.setText(Strings.nullToEmpty(labelProvider.getText(inverseProperty)));

        MenuManager menuManager = new MenuManager();
        menuManager.setRemoveAllWhenShown(true);
        menuManager.addMenuListener(new IMenuListener() {

            @Override
            public void menuAboutToShow(IMenuManager manager) {
                Action clear = new Action() {
                    @Override
                    public String getText() {
                        return "Clear";
                    }

                    @Override
                    public ImageDescriptor getImageDescriptor() {
                        return CorePlugin.getDefault().getImageDescriptor(
                                CorePluginImages.IMG_REMOVE);
                    }

                    @Override
                    public boolean isEnabled() {
                        return (inverseProperty != null);
                    }

                    @Override
                    public void run() {
                        clearInverseProperty();
                    }
                };
                manager.add(clear);
            }
        });
        Menu menu = menuManager.createContextMenu(inverseUriText);
        inverseUriText.setMenu(menu);

        createChooseButton(parent, new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                Shell shell = getShell();
                String title = "Select Inverse";
                String message = "Please select a inverse property for the new property.";
                ResourceSelectionDialog dialog = new ResourceSelectionDialog(shell, title, message);
                dialog.setModel(model);

                dialog.setRootResources(Arrays.asList(new Resource[] { RDF.Property }));
                dialog.setHierarchicalProperties(Arrays.asList(new Property[] { RDFS.subPropertyOf,
                        RDFS.subClassOf, RDF.type }));
                dialog.setAllowedResourceTypes(Arrays.asList(new Resource[] { RDF.Property }));

                if (dialog.open() == Window.OK) {
                    inverseProperty = dialog.getFirstSelectedResource().as(Property.class);
                    updateInverseProperty();
                }
            }
        });
    }

    private void createSuperPropertyControls(Composite parent) {
        Label label = new Label(parent, SWT.NONE);
        label.setText("Super Property:");

        superIconLabel = new Label(parent, SWT.NONE);
        superIconLabel.setLayoutData(new GridData(16, 16));
        superIconLabel.setImage(labelProvider.getImage(superProperty));

        superUriText = new Text(parent, SWT.BORDER);
        superUriText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        superUriText.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent event) {
                boolean delete = (event.keyCode == SWT.DEL) || (event.keyCode == SWT.BS);
                if (delete) {
                    clearSuperProperty();
                }
                else {
                    MessageDialog
                            .openInformation(getShell(), "Disabled",
                                    "Please use the button next to this text field to choose a super property.");
                }
                event.doit = false;
            }
        });
        superUriText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        superUriText.setText(Strings.nullToEmpty(labelProvider.getText(superProperty)));

        MenuManager menuManager = new MenuManager();
        menuManager.setRemoveAllWhenShown(true);
        menuManager.addMenuListener(new IMenuListener() {

            @Override
            public void menuAboutToShow(IMenuManager manager) {
                Action clear = new Action() {
                    @Override
                    public String getText() {
                        return "Clear";
                    }

                    @Override
                    public ImageDescriptor getImageDescriptor() {
                        return CorePlugin.getDefault().getImageDescriptor(
                                CorePluginImages.IMG_REMOVE);
                    }

                    @Override
                    public boolean isEnabled() {
                        return (superProperty != null);
                    }

                    @Override
                    public void run() {
                        clearSuperProperty();
                    }
                };
                manager.add(clear);
            }
        });
        Menu menu = menuManager.createContextMenu(superUriText);
        superUriText.setMenu(menu);

        createChooseButton(parent, new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                Shell shell = getShell();
                String title = "Select Super Property";
                String message = "Please select a super property for the new property.";
                ResourceSelectionDialog dialog = new ResourceSelectionDialog(shell, title, message);
                dialog.setModel(model);

                dialog.setRootResources(Arrays.asList(new Resource[] { RDF.Property }));
                dialog.setHierarchicalProperties(Arrays.asList(new Property[] { RDFS.subPropertyOf,
                        RDFS.subClassOf, RDF.type }));
                dialog.setAllowedResourceTypes(Arrays.asList(new Resource[] { RDF.Property }));

                if (dialog.open() == Window.OK) {
                    superProperty = dialog.getFirstSelectedResource().as(Property.class);
                    updateSuperProperty();
                }
            }
        });
    }

    private void createFollowControls(Composite parent) {
        Label label = new Label(parent, SWT.NONE);
        label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
        label.setText("Follow:");

        new Label(parent, SWT.NONE);

        Composite followComposite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(3, false);
        layout.verticalSpacing = 3;
        layout.marginWidth = 0;
        followComposite.setLayout(layout);
        followComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));

        noFollowRadio = new Button(followComposite, SWT.RADIO);
        noFollowRadio.setText("none");
        noFollowRadio.setSelection(true);
        noFollowRadio.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                updateFollows();
            }
        });

        followSuperRadio = new Button(followComposite, SWT.RADIO);
        followSuperRadio.setText("super");
        followSuperRadio.setEnabled(superProperty != null);
        followSuperRadio.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                updateFollows();
            }
        });

        followInverseRadio = new Button(followComposite, SWT.RADIO);
        followInverseRadio.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, true, 1, 1));
        followInverseRadio.setText("inverse");
        followInverseRadio.setEnabled(inverseProperty != null);
        followInverseRadio.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                updateFollows();
            }
        });

        new Label(parent, SWT.NONE);
    }

    private Button createChooseButton(Composite parent, SelectionListener listener) {
        Button button = new Button(parent, SWT.BUTTON1);
        button.setText("...");
        button.setLayoutData(new GridData(GridData.END));
        button.addSelectionListener(listener);
        return button;
    }

    private void updateFollows() {
        Resource domainResource = null;
        Resource rangeResource = null;
        Resource subjectRoleResource = null;
        Resource objectRoleResource = null;
        Property followProperty = null;
        boolean inverse = false;
        if (followInverseRadio.getSelection()) {
            followProperty = inverseProperty;
            inverse = true;
        }
        else if (followSuperRadio.getSelection()) {
            followProperty = superProperty;
        }
        if (followProperty != null) {
            if (followProperty.hasProperty(RDFS.range)) {
                if (inverse) {
                    domainResource = followProperty.getPropertyResourceValue(RDFS.range);
                }
                else {
                    rangeResource = followProperty.getPropertyResourceValue(RDFS.range);
                }
            }
            if (followProperty.hasProperty(RDFS.domain)) {
                if (inverse) {
                    rangeResource = followProperty.getPropertyResourceValue(RDFS.domain);
                }
                else {
                    domainResource = followProperty.getPropertyResourceValue(RDFS.domain);
                }
            }
            if (followProperty.hasProperty(SEMM.hasSubjectRole)) {
                if (inverse) {
                    objectRoleResource = followProperty
                            .getPropertyResourceValue(SEMM.hasSubjectRole);
                }
                else {
                    subjectRoleResource = followProperty
                            .getPropertyResourceValue(SEMM.hasSubjectRole);
                }
            }
            if (followProperty.hasProperty(SEMM.hasObjectRole)) {
                if (inverse) {
                    subjectRoleResource = followProperty
                            .getPropertyResourceValue(SEMM.hasObjectRole);
                }
                else {
                    objectRoleResource = followProperty
                            .getPropertyResourceValue(SEMM.hasObjectRole);
                }
            }
        }
        domainField.setResource(domainResource);
        rangeField.setResource(rangeResource);
        if (isSemmImported()) {
            subjectRoleField.setResource(subjectRoleResource);
            objectRoleField.setResource(objectRoleResource);
        }
    }

    @SuppressWarnings("null")
    private void validatePage() {
        String errorMessage = null;
        setErrorMessage(errorMessage);
        setPageComplete((errorMessage == null));
    }

    public void setProperty(Property property) {
        this.property = property;

        Resource superPropertyValue = property.getPropertyResourceValue(RDFS.subPropertyOf);
        superProperty = (superPropertyValue == null) ? null : superPropertyValue.as(Property.class);
        Resource inversePropertyValue = property.getPropertyResourceValue(OWL.inverseOf);
        inverseProperty = (inversePropertyValue == null) ? null : inversePropertyValue
                .as(Property.class);

        domainResource = property.getPropertyResourceValue(RDFS.domain);
        rangeResource = property.getPropertyResourceValue(RDFS.range);

        subjectRoleResource = property.getPropertyResourceValue(SEMM.hasSubjectRole);
        objectRoleResource = property.getPropertyResourceValue(SEMM.hasObjectRole);
    }

    public Property getInverseProperty() {
        return inverseProperty;
    }

    public Property getSuperProperty() {
        return superProperty;
    }

    public void setInverseProperty(Property propery) {
        inverseProperty = propery;
    }

    public void setSuperProperty(Property property) {
        this.superProperty = property;
    }

    public Resource getDomainResource() {
        return domainField.getResource();
    }

    public Resource getRangeResource() {
        return rangeField.getResource();
    }

    public Resource getSubjectRoleResource() {
        if (subjectRoleField != null) {
            return subjectRoleField.getResource();
        }
        return null;
    }

    public Resource getObjectRoleResource() {
        if (objectRoleField != null) {
            return objectRoleField.getResource();
        }
        return null;
    }

    private void updateInverseProperty() {
        inverseUriText.setText(Strings.nullToEmpty(labelProvider.getText(inverseProperty)));
        inverseIconLabel.setImage(labelProvider.getImage(inverseProperty));
        followInverseRadio.setEnabled(inverseProperty != null);
        updateFollows();
        validatePage();
    }

    private void clearInverseProperty() {
        inverseProperty = null;
        updateInverseProperty();
    }

    private void updateSuperProperty() {
        superUriText.setText(Strings.nullToEmpty(labelProvider.getText(superProperty)));
        superIconLabel.setImage(labelProvider.getImage(superProperty));
        followSuperRadio.setEnabled(superProperty != null);
        updateFollows();
        validatePage();
    }

    private void clearSuperProperty() {
        superProperty = null;
        updateSuperProperty();
    }

    /**
     * Inner class
     * 
     * @author Mike Henrichs
     * 
     */
    private class PropertyWizardPageField {
        private SelectionListener chooseListener = new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                MessageDialog.openError(getShell(), "Error", "ChooseListener has not been set!");
            }
        };

        private Resource resource;
        private Text uriText;
        private Label iconLabel;
        private Button chooseButton;
        private String text;

        public PropertyWizardPageField(Composite parent, String text) {
            this.text = text;
            createControl(parent);
        }

        public void setResource(Resource resource) {
            this.resource = resource;
            if (iconLabel != null) {
                iconLabel.setImage(labelProvider.getImage(resource));
            }
            if (uriText != null) {
                uriText.setText(Strings.nullToEmpty(labelProvider.getText(resource)));
            }
        }

        public Resource getResource() {
            return resource;
        }

        public void setChooseListener(SelectionListener listener) {
            if (chooseListener != null) {
                chooseButton.removeSelectionListener(chooseListener);
            }
            chooseButton.addSelectionListener(listener);
            chooseListener = listener;
        }

        private void clearResource() {
            resource = null;
            uriText.setText("");
            iconLabel.setImage(null);
        }

        private void createControl(Composite parent) {
            Label label = new Label(parent, SWT.NONE);
            label.setText(text);

            iconLabel = new Label(parent, SWT.NONE);
            iconLabel.setLayoutData(new GridData(16, 16));

            uriText = new Text(parent, SWT.BORDER);
            uriText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            uriText.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent event) {
                    boolean delete = (event.keyCode == SWT.DEL) || (event.keyCode == SWT.BS);
                    if (delete) {
                        clearResource();
                    }
                    else {
                        MessageDialog.openInformation(getShell(), "Disabled",
                                "Please use the button next to this text field to choose a " + text
                                        + " resource.");
                    }
                    event.doit = false;
                }
            });
            uriText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

            MenuManager menuManager = new MenuManager();
            menuManager.setRemoveAllWhenShown(true);
            menuManager.addMenuListener(new IMenuListener() {

                @Override
                public void menuAboutToShow(IMenuManager manager) {
                    Action clear = new Action() {
                        @Override
                        public String getText() {
                            return "Clear";
                        }

                        @Override
                        public ImageDescriptor getImageDescriptor() {
                            return CorePlugin.getDefault().getImageDescriptor(
                                    CorePluginImages.IMG_REMOVE);
                        }

                        @Override
                        public boolean isEnabled() {
                            return (resource != null);
                        }

                        @Override
                        public void run() {
                            clearResource();
                        }
                    };
                    manager.add(clear);
                }
            });
            Menu menu = menuManager.createContextMenu(uriText);
            uriText.setMenu(menu);

            chooseButton = new Button(parent, SWT.PUSH);
            chooseButton.setText("...");
            chooseButton.setLayoutData(new GridData(GridData.END));
            chooseButton.addSelectionListener(chooseListener);
        }
    }
}
