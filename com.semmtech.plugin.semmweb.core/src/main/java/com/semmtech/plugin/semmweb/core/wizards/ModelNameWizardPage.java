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


import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Resource;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.dialog.NamespaceURIValidator;
import com.semmtech.plugin.semmweb.core.model.OntModelUtils;
import com.semmtech.plugin.semmweb.core.sparql.OntologyLabelProviderPropertyFunction;
import com.semmtech.ui.plugin.widgets.Widgets;
import com.semmtech.ui.plugin.wizard.BaseWizardPage;


/**
 * 
 * @author Sander Stolk
 */
public class ModelNameWizardPage extends BaseWizardPage {
    private static final String PAGE_NAME = "modelNamePage";
    private static final String PAGE_TITLE = "Specify model names.";
    private static final String PAGE_DESCRIPTION = "On this page you can specify the identification of your model, which includes the name and URI of the ontology.";

    protected IFile modelFile;

    protected Composite container;
    protected Text filenameText;
    protected Text nameText;
    protected Text namespaceUriText;

    protected boolean filenameEditable = false;

    protected final IProject project;

    protected String originalFileName;
    protected String fileName = "";
    protected String modelName = "";
    protected String ontologyUri = "";

    /*
     * These lists contains the values from already existing models in the
     * project folder. Used to avoid same uri/file/model name.
     */
    protected List<String> modelNames;
    protected List<String> fileNames;
    protected List<String> ontologyURIs;
    protected String suggestedFileName;

    protected List<Resource> ontologies;

    protected ModelNameWizardPage(IFile file, OntModel model) {
        super(PAGE_NAME);
        setTitle(PAGE_TITLE);
        setDescription(PAGE_DESCRIPTION);

        this.modelFile = file;
        this.project = file.getProject();

        originalFileName = file.getName();
        fileName = file.getName();

        if (model != null) {
            modelName = OntModelUtils.getName(model);

            ontologies = OntModelUtils.getOntologies(model.getBaseModel());

            if (ontologies.size() == 1) {
                ontologyUri = ontologies.get(0).getURI();
            }
        }

        modelNames = Lists.newArrayList();
        fileNames = Lists.newArrayList();
        ontologyURIs = Lists.newArrayList();
        this.suggestedFileName = null;
    }

    @Override
    public void createControl(Composite parent) {
        container = new Composite(parent, SWT.NULL);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 8;
        container.setLayout(layout);

        if (ontologies != null && ontologies.size() <= 1) {
            Label label = new Label(container, SWT.NONE);
            GridData layoutData = new GridData(GridData.FILL, GridData.CENTER, false, false, 1, 1);
            layoutData.widthHint = 90;
            label.setLayoutData(layoutData);
            label.setText("Ontology name:");

            nameText = new Text(container, SWT.BORDER);
            nameText.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 1));
            nameText.setText(modelName == null ? "" : modelName);
            nameText.addModifyListener(new ModifyListener() {
                @Override
                public void modifyText(ModifyEvent e) {
                    modelName = nameText.getText();

                    setPageComplete(validatePage());
                }
            });

            nameText.setEditable(ontologies.isEmpty()
                    || OntologyLabelProviderPropertyFunction
                            .getAllNameStatements(ontologies.get(0)).size() <= 1);

            if (!nameText.getEditable()) {
                label = new Label(container, SWT.NONE);
                label.setText("");
                layoutData = new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 1, 1);
                layoutData.widthHint = 95;
                layoutData.verticalIndent = 3;
                label.setLayoutData(layoutData);

                Link link = new Link(container, SWT.NONE);
                link.setText("The ontology contains multiple labels that indicate its name. To edit these labels, click <a>here</a> to open the ontology resource in an editor.");
                link.addListener(SWT.Selection, new Listener() {
                    @Override
                    public void handleEvent(Event event) {
                        CorePlugin.getDefault().openResource(modelFile, ontologies.get(0));
                        getWizard().getContainer().getShell().close();
                    }
                });
                layoutData = new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 1);
                layoutData.widthHint = 95;
                layoutData.verticalIndent = 3;
                link.setLayoutData(layoutData);
            }

            label = new Label(container, SWT.NONE);
            label.setText("Ontology URI:");
            layoutData = new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 1, 1);
            layoutData.widthHint = 95;
            layoutData.verticalIndent = 3;
            label.setLayoutData(layoutData);

            namespaceUriText = new Text(container, SWT.BORDER);
            layoutData = new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 1);
            layoutData.verticalIndent = 3;
            namespaceUriText.setLayoutData(layoutData);
            namespaceUriText.setText(!Strings.isNullOrEmpty(ontologyUri) ? ontologyUri : "http://");
            namespaceUriText.addModifyListener(new ModifyListener() {
                @Override
                public void modifyText(ModifyEvent e) {
                    ontologyUri = namespaceUriText.getText().toLowerCase();
                    if (ontologyUri.equals("http://")) {
                        ontologyUri = null;
                    }
                    setPageComplete(validatePage());
                }
            });
            namespaceUriText.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    if (namespaceUriText.getEditable()) {
                        if (!Strings.isNullOrEmpty(ontologyUri) && !ontologyUri.endsWith("/")
                                && !ontologyUri.endsWith("#")) {
                            ontologyUri += "/";
                        }
                        namespaceUriText.setText(!Strings.isNullOrEmpty(ontologyUri) ? ontologyUri
                                : "http://");
                    }
                }
            });
            namespaceUriText.setEditable(ontologies.isEmpty());
        }

        Label label = new Label(container, SWT.NONE);
        label.setText("Filename:");
        GridDataFactory.swtDefaults().hint(65, SWT.DEFAULT).align(SWT.BEGINNING, SWT.CENTER)
                .applyTo(label);

        filenameText = new Text(container, SWT.BORDER);
        filenameText.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 1));

        if (suggestedFileName != null) {
            fileName = suggestedFileName;
        }

        filenameText.setText(fileName == null ? "" : fileName);
        filenameText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                fileName = filenameText.getText();
                setPageComplete(validatePage());
            }
        });
        filenameText.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                updateExtension();
            }
        });
        filenameText.setEditable(filenameEditable);

        setPageComplete(validatePage());
        setControl(container);
    }

    private void updateExtension() {
        if (fileName.lastIndexOf(".") > 0) {
            fileName = fileName.substring(0, fileName.lastIndexOf("."));
        }
        if (fileName.length() > 0) {
            fileName = String.format("%s.%s", fileName, modelFile.getFileExtension());
            filenameText.setText(fileName);
        }
    }

    protected boolean validatePage() {
        String errorMessage = null;

        if (!Widgets.isNullOrDisposed(nameText) && nameText.getEditable()) {
            if (Strings.isNullOrEmpty(modelName)) {
                errorMessage = "Model name cannot be empty";
            }
            else if (modelNames != null && modelNames.contains(modelName)) {
                errorMessage = "The specified Model Name already exists in the models folder";
            }
        }

        if (errorMessage == null
                && (!Widgets.isNullOrDisposed(namespaceUriText) && namespaceUriText.getEditable())) {
            NamespaceURIValidator namespaceUriValidator = new NamespaceURIValidator();
            errorMessage = namespaceUriValidator.isValidPublicUri(ontologyUri);

            // if (!checkOntologyUri(ontologyUri)) {
            // errorMessage =
            // "The specified Ontology URI already exists in the models folder";
            // }
        }

        if (errorMessage == null
                && (!Widgets.isNullOrDisposed(filenameText) && filenameText.getEditable())) {
            if (Strings.isNullOrEmpty(fileName)) {
                errorMessage = "Filename cannot be empty";
            }
            else if (!fileName.equals(originalFileName)) {
                IContainer fileContainer = modelFile.getParent();
                IFile file = fileContainer.getFile(new Path(fileName));
                if (file.exists() && !file.equals(modelFile)) {
                    errorMessage = "File already exists in the models folder";
                }
            }
        }

        setErrorMessage(errorMessage);
        return (errorMessage == null);
    }

    public void setFilenameEditable(boolean enabled) {
        filenameEditable = enabled;
        if (!Widgets.isNullOrDisposed(filenameText)) {
            filenameText.setEditable(filenameEditable);
        }
    }

    public String getFilename() {
        return fileName;
    }

    public String getOntologyName() {
        return modelName;
    }

    public String getOntologyURI() {
        return ontologyUri;
    }

    /**
     * List of already existent file names
     */
    public void setFileNames(List<String> fileNames) {
        this.fileNames = fileNames;
    }

    /**
     * List of already existent ontology URIs
     */
    public void setOntologyURIs(List<String> ontologyURIs) {
        this.ontologyURIs = ontologyURIs;
    }

    /**
     * List of already existent model names
     */
    public void setModelNames(List<String> modelNames) {
        this.modelNames = modelNames;
    }

    /**
     * Provides a file name, useful if we want force user to change filename
     */
    public void suggestNewFileName(String newFileName) {
        this.suggestedFileName = newFileName;
    }
}
