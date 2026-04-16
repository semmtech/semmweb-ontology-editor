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

package com.semmtech.plugin.semmweb.sparql.debug.ui.main;


import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.variables.IStringVariableManager;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

import com.semmtech.plugin.semmweb.sparql.debug.ui.Messages;
import com.semmtech.plugin.semmweb.sparql.debug.ui.ResourceSelectionBlock;
import com.semmtech.plugin.semmweb.sparql.debug.ui.SparqlLaunchConfigurationConstants;


public class InputModelBlock extends ResourceSelectionBlock {

    private String inputFile;
    private String inputUrl;
    private boolean useExternal;
    private boolean isSparqlEndPoint;

    @SuppressWarnings("unused")
    private IFile defaultFile;
    private Button localFileRadio;
    private Button externalUrlRadio;
    private Button sparqlEndPointCheckbox;
    private Text urlText;
    private Link urlLink;
    private Button emptyModelRadio;
    private boolean useEmptyModel;

    public InputModelBlock(IFile defaultFile) {
        super(IResource.FILE, false);
        setFileExtensions(new String[] { "rdf", "ttl", "n3", "owl", "nt" });
        this.defaultFile = defaultFile;
    }

    public InputModelBlock() {
        this(null);
    }

    @Override
    public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
        configuration.setAttribute(SparqlLaunchConfigurationConstants.ATTR_INPUT_MODEL_URL,
                (String) null);
        configuration.setAttribute(SparqlLaunchConfigurationConstants.ATTR_INPUT_MODEL_FILE,
                (String) null);
        configuration.setAttribute(SparqlLaunchConfigurationConstants.ATTR_SPARQL_END_POINT, false);
        configuration.setAttribute(SparqlLaunchConfigurationConstants.ATTR_USE_EXTERNAL_MODEL,
                false);
        configuration.setAttribute(SparqlLaunchConfigurationConstants.ATTR_USE_EMPTY_MODEL, false);
    }

    @Override
    public void initializeFrom(ILaunchConfiguration configuration) {
        setLaunchConfiguration(configuration);
        try {
            inputUrl = configuration.getAttribute(
                    SparqlLaunchConfigurationConstants.ATTR_INPUT_MODEL_URL, (String) null);
            inputFile = configuration.getAttribute(
                    SparqlLaunchConfigurationConstants.ATTR_INPUT_MODEL_FILE, (String) null);
            isSparqlEndPoint = configuration.getAttribute(
                    SparqlLaunchConfigurationConstants.ATTR_SPARQL_END_POINT, false);
            useExternal = configuration.getAttribute(
                    SparqlLaunchConfigurationConstants.ATTR_USE_EXTERNAL_MODEL, false);
            useEmptyModel = configuration.getAttribute(
                    SparqlLaunchConfigurationConstants.ATTR_USE_EMPTY_MODEL, false);

            localFileRadio.setSelection(!useExternal && !useEmptyModel);
            externalUrlRadio.setSelection(useExternal);
            emptyModelRadio.setSelection(useEmptyModel);
            sparqlEndPointCheckbox.setSelection(isSparqlEndPoint);

            if (inputFile != null) {
                resourceText.setText(inputFile);
            }
            if (inputUrl != null) {
                urlText.setText(inputUrl);
            }

            handleUseExternalChange();
        }
        catch (CoreException ex) {
            setErrorMessage(String.format("Exception occured during reading configuration: %s", ex
                    .getStatus().getMessage()));
            ex.printStackTrace();
        }
    }

    @Override
    public void performApply(ILaunchConfigurationWorkingCopy configuration) {
        configuration.setAttribute(SparqlLaunchConfigurationConstants.ATTR_INPUT_MODEL_URL,
                inputUrl);
        configuration.setAttribute(SparqlLaunchConfigurationConstants.ATTR_INPUT_MODEL_FILE,
                inputFile);
        configuration.setAttribute(SparqlLaunchConfigurationConstants.ATTR_SPARQL_END_POINT,
                isSparqlEndPoint);
        configuration.setAttribute(SparqlLaunchConfigurationConstants.ATTR_USE_EXTERNAL_MODEL,
                useExternal);
        configuration.setAttribute(SparqlLaunchConfigurationConstants.ATTR_USE_EMPTY_MODEL,
                useEmptyModel);
    }

    @Override
    public void createControl(Composite parent) {
        Composite group = createContainer(parent);
        setControl(group);
        createContents(group);
    }

    protected void handleUseExternalChange() {
        useExternal = externalUrlRadio.getSelection();
        useEmptyModel = emptyModelRadio.getSelection();
        if (useExternal) {
            inputFile = null;
            resourceText.setText("");
        }
        else if (useEmptyModel) {
            inputFile = null;
            resourceText.setText("");
            inputUrl = null;
            urlText.setText("");
        }
        else {
            inputUrl = null;
            urlText.setText("");
        }
        resourceText.setEnabled(!useExternal && !useEmptyModel);
        fOpenFilesButton.setEnabled(!useExternal && !useEmptyModel);
        fVariablesButton.setEnabled(!useExternal && !useEmptyModel);
        fWorkspaceButton.setEnabled(!useExternal && !useEmptyModel);
        fFileSystemButton.setEnabled(!useExternal && !useEmptyModel);

        urlText.setEnabled(useExternal && !useEmptyModel);
        urlLink.setEnabled(useExternal && !useEmptyModel);
        sparqlEndPointCheckbox.setEnabled(useExternal && !useEmptyModel);
    }

    @SuppressWarnings("unused")
    @Override
    protected void createContents(Composite parent) {

        SelectionListener radioListener = new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                handleUseExternalChange();
            }
        };

        localFileRadio = new Button(parent, SWT.RADIO);
        localFileRadio.setText("Local RDF/XML Ontology file");
        localFileRadio.addSelectionListener(radioListener);
        GridDataFactory.fillDefaults().span(2, 1).indent(SWT.DEFAULT, 5).applyTo(localFileRadio);

        GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
        layoutData.horizontalSpan = 2;
        localFileRadio.setLayoutData(layoutData);

        Composite fileComposite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(2, false);
        layout.marginWidth = 0;
        layout.marginLeft = 23;
        layout.marginHeight = 0;
        fileComposite.setLayout(layout);
        layoutData = new GridData(GridData.FILL_HORIZONTAL);
        layoutData.horizontalSpan = 2;
        fileComposite.setLayoutData(layoutData);

        Label label = new Label(fileComposite, SWT.NONE);
        label.setText("Ontology File");

        resourceText = new Text(fileComposite, SWT.SINGLE | SWT.BORDER);
        resourceText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        resourceText.setFont(parent.getFont());
        resourceText.addModifyListener(widgetListener);

        Composite buttonComp = new Composite(fileComposite, SWT.NONE);

        layout = new GridLayout(4, false);
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        buttonComp.setLayout(layout);
        GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_END);
        gd.horizontalSpan = 2;
        buttonComp.setLayoutData(gd);
        buttonComp.setFont(parent.getFont());

        fWorkspaceButton = createPushButton(buttonComp, getMessage(WORKSPACE_BUTTON), null);
        fWorkspaceButton.addSelectionListener(widgetListener);
        fFileSystemButton = createPushButton(buttonComp, getMessage(FILE_SYSTEM_BUTTON), null);
        fFileSystemButton.addSelectionListener(widgetListener);
        fVariablesButton = createPushButton(buttonComp, getMessage(VARIABLES_BUTTON), null);
        fVariablesButton.addSelectionListener(widgetListener);
        fOpenFilesButton = createPushButton(buttonComp, getMessage(OPENFILES_BUTTON), null);
        fOpenFilesButton.addSelectionListener(widgetListener);

        externalUrlRadio = new Button(parent, SWT.RADIO);
        externalUrlRadio.setText("External RDF/XML file or SPARQL end-point");
        externalUrlRadio.addSelectionListener(radioListener);

        layoutData = new GridData(GridData.FILL_HORIZONTAL);
        layoutData.horizontalSpan = 2;
        localFileRadio.setLayoutData(layoutData);

        Composite urlComposite = new Composite(parent, SWT.NONE);
        layout = new GridLayout(2, false);
        layout.marginWidth = 0;
        layout.marginLeft = 23;
        layout.marginHeight = 0;
        urlComposite.setLayout(layout);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalSpan = 2;
        urlComposite.setLayoutData(gd);

        urlLink = new Link(urlComposite, SWT.NONE);
        urlLink.setText("<a>Model URL</a>");
        urlLink.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {

            }
        });

        urlText = new Text(urlComposite, SWT.SINGLE | SWT.BORDER);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalSpan = 1;
        urlText.setLayoutData(gd);
        urlText.setFont(parent.getFont());
        urlText.addModifyListener(widgetListener);

        new Label(urlComposite, SWT.NONE);

        sparqlEndPointCheckbox = new Button(urlComposite, SWT.CHECK);
        sparqlEndPointCheckbox.setText("SPARQL End-Point");
        sparqlEndPointCheckbox.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                isSparqlEndPoint = sparqlEndPointCheckbox.getSelection();
            }
        });

        emptyModelRadio = new Button(parent, SWT.RADIO);
        emptyModelRadio.setText("Empty model");
        emptyModelRadio.addSelectionListener(radioListener);
        GridDataFactory.fillDefaults().span(2, 1).indent(SWT.DEFAULT, 5).applyTo(emptyModelRadio);

    }

    @Override
    public String getName() {
        return "InputModelBlock_Name";
    }

    @SuppressWarnings("unused")
    @Override
    protected void textModified() {
        IPath path = null;
        inputFile = null;
        inputUrl = null;

        if (useExternal) {
            inputUrl = urlText.getText();
        }
        else {
            inputFile = getText();
            if (inputFile.indexOf("${") >= 0) //$NON-NLS-1$
            {
                IStringVariableManager manager = VariablesPlugin.getDefault()
                        .getStringVariableManager();
                try {
                    manager.validateStringVariables(inputFile);
                    path = new Path(manager.performStringSubstitution(inputFile));
                }
                catch (CoreException e) {
                }
            }
            else if (inputFile.length() > 0) {
                path = new Path(inputFile);
            }
        }
    }

    @Override
    protected String getMessage(int type) {
        switch (type) {
        case ERROR_DIRECTORY_NOT_SPECIFIED:
            return Messages.InputFileBlock_DIRECTORY_NOT_SPECIFIED;
        case ERROR_DIRECTORY_DOES_NOT_EXIST:
            return Messages.InputFileBlock_DIRECTORY_DOES_NOT_EXIST;
        case GROUP_NAME:
            return "Input Model";
        case USE_DEFAULT_RADIO:
            return Messages.InputFileBlock_DEFAULT_RADIO;
        case USE_OTHER_RADIO:
            return Messages.InputFileBlock_OTHER_RADIO;
        case DIRECTORY_DIALOG_MESSAGE:
            return Messages.InputFileBlock_DIALOG_MESSAGE;
        case WORKSPACE_DIALOG_MESSAGE:
            return Messages.InputFileBlock_WORKSPACE_DIALOG_MESSAGE;
        case VARIABLES_BUTTON:
            return Messages.InputFileBlock_VARIABLES_BUTTON;
        case FILE_SYSTEM_BUTTON:
            return Messages.InputFileBlock_FILE_SYSTEM_BUTTON;
        case WORKSPACE_BUTTON:
            return Messages.InputFileBlock_WORKSPACE_BUTTON;
        case WORKSPACE_DIALOG_TITLE:
            return Messages.InputFileBlock_WORKSPACE_DIALOG_TITLE;
        case OPENFILES_BUTTON:
            return Messages.InputFileBlock_OPENFILES_BUTTON;
        case OPENFILES_DIALOG_TITLE:
            return Messages.InputFileBlock_OPENFILES_DIALOG;
        }
        return "" + type;
    }

    @Override
    protected void updateResourceText(boolean useDefault) {
    }
}
