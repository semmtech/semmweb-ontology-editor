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
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
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
import org.eclipse.swt.widgets.Text;

import com.google.common.base.Strings;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.CorePluginImages;
import com.semmtech.plugin.semmweb.core.dialog.ResourceSelectionDialog;
import com.semmtech.plugin.semmweb.core.model.IModelProvider;
import com.semmtech.semantics.vocabulary.SEMM;


public class ClassPropertiesWizardPage extends WizardPage {

    private OntModel model;
    @SuppressWarnings("unused")
    private Resource resource;

    // private URIValidator uriValidator;
    // private UniqueResourceURIValidator resourceValidator;
    private final LabelProvider labelProvider;

    private Resource initialSuperClass;
    private ClassWizardPageField superField;

    private List<Resource> properties;

    public ClassPropertiesWizardPage(String pageName, OntModel model, Resource resource,
            Resource superClass) {
        super(pageName);
        setTitle("Property");
        setDescription("Specify properties of the new class.");

        this.model = model;
        this.resource = resource;
        this.initialSuperClass = superClass;

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

    @Override
    public void createControl(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL
                | GridData.GRAB_VERTICAL));
        composite.setLayout(new GridLayout(4, false));

        Label label = new Label(composite, SWT.NONE);
        label.setText("Please specify the class to which the newly created class is directly subordinate to.");
        GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
        layoutData.heightHint = 21;
        layoutData.verticalAlignment = SWT.TOP;
        layoutData.widthHint = 300;
        layoutData.grabExcessHorizontalSpace = false;
        layoutData.horizontalSpan = 4;
        layoutData.verticalIndent = 6;
        label.setLayoutData(layoutData);

        superField = new ClassWizardPageField(composite, "Super class:");
        superField.setChooseListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                Resource clazz = chooseClass("Select super class",
                        "Please select a resource below to be used as super class.");
                if (clazz != null) {
                    superField.setResource(clazz);
                    validatePage();
                }
            }
        });
        superField.setResource(initialSuperClass);

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

    @SuppressWarnings("null")
    private void validatePage() {
        String errorMessage = null;
        setErrorMessage(errorMessage);
        setPageComplete((errorMessage == null));
    }

    public void setClass(Resource resource) {
        this.resource = resource;
    }

    public Resource getSuperClass() {
        return superField.getResource();
    }

    /**
     * Inner class
     * 
     * @author Mike Henrichs
     * 
     */
    private class ClassWizardPageField {
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

        public ClassWizardPageField(Composite parent, String text) {
            this.text = text;
            createControl(parent);
        }

        private void clearResource() {
            resource = null;
            uriText.setText("");
            iconLabel.setImage(null);
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

        private void createControl(Composite parent) {
            Label label = new Label(parent, SWT.NONE);
            label.setText(text);
            GridDataFactory.swtDefaults().hint(90, SWT.DEFAULT).applyTo(label);

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
