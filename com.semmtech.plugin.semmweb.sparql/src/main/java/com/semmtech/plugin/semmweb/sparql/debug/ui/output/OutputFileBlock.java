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

package com.semmtech.plugin.semmweb.sparql.debug.ui.output;


import java.util.Map;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.google.common.collect.Maps;
import com.semmtech.plugin.semmweb.sparql.SparqlPlugin;
import com.semmtech.plugin.semmweb.sparql.debug.ui.Messages;
import com.semmtech.plugin.semmweb.sparql.debug.ui.ResourceSelectionBlock;
import com.semmtech.plugin.semmweb.sparql.debug.ui.SparqlLaunchConfigurationConstants;


public class OutputFileBlock extends ResourceSelectionBlock {
    private Button openFileCheckButton;
    @SuppressWarnings("unused")
    private String inputFilename;
    private String outputSyntax;
    private Text fileNameText;
    private String defaultOutputFileName;
    @SuppressWarnings("unused")
    private String defaultOutputSyntax;
    private String outputFileName;
    private Map<String, String> syntaxNames;
    private Combo syntaxCombo;
    private ILaunchConfiguration configuration;

    public OutputFileBlock() {
        super(IResource.FOLDER, true, true, false);
    }

    @Override
    protected String getMessage(int type) {
        switch (type) {
        case ERROR_DIRECTORY_NOT_SPECIFIED:
            return Messages.OutputFOFileBlock_DIRECTORY_NOT_SPECIFIED;
        case ERROR_DIRECTORY_DOES_NOT_EXIST:
            return Messages.OutputFOFileBlock_DIRECTORY_DOES_NOT_EXIST;
        case GROUP_NAME:
            return getName();
        case USE_DEFAULT_RADIO:
            return Messages.OutputFileBlock_0;
        case USE_OTHER_RADIO:
            return Messages.OutputFOFileBlock_OTHER_RADIO;
        case DIRECTORY_DIALOG_MESSAGE:
            return Messages.OutputFOFileBlock_DIALOG_MESSAGE;
        case WORKSPACE_DIALOG_MESSAGE:
            return Messages.OutputFOFileBlock_WORKSPACE_DIALOG_MESSAGE;
        case VARIABLES_BUTTON:
            return Messages.OutputFOFileBlock_VARIABLES_BUTTON;
        case FILE_SYSTEM_BUTTON:
            return Messages.OutputFOFileBlock_FILE_SYSTEM_BUTTON;
        case WORKSPACE_BUTTON:
            return Messages.OutputFOFileBlock_WORKSPACE_BUTTON;
        case WORKSPACE_DIALOG_TITLE:
            return Messages.OutputFOFileBlock_WORKSPACE_DIALOG_TITLE;
        }
        return "" + type; //$NON-NLS-1$
    }

    @Override
    protected void createCheckboxAndText(Composite parent) {
        Composite syntaxComposite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(2, false);
        layout.marginWidth = 0;
        syntaxComposite.setLayout(layout);
        syntaxComposite.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));

        Label label = new Label(syntaxComposite, SWT.NONE);
        label.setText("Output Syntax:");

        syntaxCombo = new Combo(syntaxComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
        syntaxCombo.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false,
                1, 1));
        syntaxCombo.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                updateLaunchConfigurationDialog();
            }
        });
        syntaxNames = Maps.newLinkedHashMap();
        syntaxNames.put("Comma separated values (*.csv)",
                SparqlLaunchConfigurationConstants.CSV_EXTENSION);
        syntaxNames.put("JSON (*.json)", SparqlLaunchConfigurationConstants.JSON_EXTENSION);
        syntaxNames.put("RDF/XML (*.rdf)", SparqlLaunchConfigurationConstants.RDF_EXTENSION);
        syntaxNames.put("SSE (*.sse)", SparqlLaunchConfigurationConstants.SSE_EXTENSION);
        syntaxNames.put("Text (*.txt)", SparqlLaunchConfigurationConstants.TXT_EXTENSION);
        syntaxNames.put("Tab separated values (*.tsv)",
                SparqlLaunchConfigurationConstants.TSV_EXTENSION);
        syntaxNames.put("Turtle (*.ttl)", SparqlLaunchConfigurationConstants.TTL_EXTENSION);
        syntaxNames.put("XML (*.xml)", SparqlLaunchConfigurationConstants.XML_EXTENSION);

        String[] items = new String[syntaxNames.keySet().size()];
        syntaxNames.keySet().toArray(items);
        syntaxCombo.setItems(items);
        syntaxCombo.select(0);

        if (showDefault) {
            useDefaultCheckButton = createCheckButton(parent, getMessage(USE_DEFAULT_RADIO));
            GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
            gd.horizontalSpan = 2;
            useDefaultCheckButton.setLayoutData(gd);
            useDefaultCheckButton.addSelectionListener(widgetListener);
        }

        Composite specificFileComp = new Composite(parent, SWT.NONE);
        layout = new GridLayout(2, false);
        layout.marginWidth = 0;
        if (showDefault) {
            layout.marginLeft = 20;
        }
        else {
            layout.marginLeft = 0;
        }
        layout.marginHeight = 0;
        specificFileComp.setLayout(layout);
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalSpan = 2;
        specificFileComp.setLayoutData(gd);

        label = new Label(specificFileComp, SWT.NONE);
        label.setText(Messages.OutputFileBlock_1);

        fileNameText = new Text(specificFileComp, SWT.SINGLE | SWT.BORDER);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalSpan = showDefault ? 1 : 2;
        fileNameText.setLayoutData(gd);
        fileNameText.setFont(parent.getFont());
        fileNameText.addModifyListener(widgetListener);

        if (showDefault) {
            label = new Label(specificFileComp, SWT.NONE);
            label.setText(Messages.OutputFileBlock_7);
        }

        resourceText = new Text(specificFileComp, SWT.SINGLE | SWT.BORDER);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalSpan = showDefault ? 1 : 2;
        resourceText.setLayoutData(gd);
        resourceText.setFont(parent.getFont());
        resourceText.addModifyListener(widgetListener);
    }

    @Override
    protected void createButtons(Composite parent) {
        Composite checkComposite = new Composite(parent, SWT.NONE);
        checkComposite.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));
        GridLayout gl = new GridLayout();
        gl.marginWidth = 0;
        checkComposite.setLayout(gl);

        openFileCheckButton = createCheckButton(checkComposite, Messages.OutputFileBlock_8);
        GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
        gd.horizontalSpan = 1;
        openFileCheckButton.setLayoutData(gd);
        openFileCheckButton.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
            }

            @Override
            public void widgetSelected(SelectionEvent e) {
                if (openFileCheckButton.getSelection()) {
                    updateLaunchConfigurationDialog();
                }
            }
        });

        Composite buttonComp = new Composite(parent, SWT.TOP);
        GridLayout layout = new GridLayout(3, false);
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        buttonComp.setLayout(layout);
        gd = new GridData(GridData.HORIZONTAL_ALIGN_END | GridData.VERTICAL_ALIGN_BEGINNING);
        gd.horizontalSpan = 1;
        buttonComp.setLayoutData(gd);
        buttonComp.setFont(parent.getFont());

        fWorkspaceButton = createPushButton(buttonComp, getMessage(WORKSPACE_BUTTON), null);
        fWorkspaceButton.addSelectionListener(widgetListener);

        fFileSystemButton = createPushButton(buttonComp, getMessage(FILE_SYSTEM_BUTTON), null);
        fFileSystemButton.addSelectionListener(widgetListener);

        fVariablesButton = createPushButton(buttonComp, getMessage(VARIABLES_BUTTON), null);
        fVariablesButton.addSelectionListener(widgetListener);
    }

    @Override
    public String getName() {
        return Messages.OutputFileBlock_9;
    }

    @SuppressWarnings("unused")
    @Override
    public void initializeFrom(ILaunchConfiguration configuration) {
        try {
            outputSyntax = configuration.getAttribute(
                    SparqlLaunchConfigurationConstants.ATTR_OUTPUT_SYNTAX, ".csv");
            for (int i = 0; i < syntaxCombo.getItems().length; i++) {
                String key = syntaxCombo.getItem(i);
                if (syntaxNames.get(key).equals(outputSyntax)) {
                    syntaxCombo.select(i);
                    break;
                }
            }

            inputFilename = configuration.getAttribute(
                    SparqlLaunchConfigurationConstants.ATTR_INPUT_MODEL_FILE, ""); //$NON-NLS-1$
            String inputUrl = configuration.getAttribute(
                    SparqlLaunchConfigurationConstants.ATTR_INPUT_MODEL_URL, ""); //$NON-NLS-1$
            updateDefaultOutputFile();

            boolean useDefault = configuration.getAttribute(
                    SparqlLaunchConfigurationConstants.ATTR_USE_DEFAULT_OUTPUT_FILE, true);
            useDefaultCheckButton.setSelection(useDefault);

            outputFileName = configuration.getAttribute(
                    SparqlLaunchConfigurationConstants.ATTR_OUTPUT_FILENAME, defaultOutputFileName);
            resource = configuration.getAttribute(
                    SparqlLaunchConfigurationConstants.ATTR_OUTPUT_FOLDER, defaultResource);

            updateResourceText(useDefault);

            boolean openFileOnCompletion = configuration.getAttribute(
                    SparqlLaunchConfigurationConstants.ATTR_OPEN_FILE, true);
            openFileCheckButton.setSelection(openFileOnCompletion);

        }
        catch (CoreException e) {
            SparqlPlugin.log(e);
        }
    }

    @Override
    public void performApply(ILaunchConfigurationWorkingCopy configuration) {
        boolean useDefault = useDefaultCheckButton.getSelection();
        String outputSyntax = syntaxNames.get(syntaxCombo.getText());
        String outputFile = resourceText.getText();
        String outputFileName = fileNameText.getText();
        boolean openFileOnCompletion = openFileCheckButton.getSelection();

        configuration.setAttribute(SparqlLaunchConfigurationConstants.ATTR_USE_DEFAULT_OUTPUT_FILE,
                useDefault);
        configuration.setAttribute(SparqlLaunchConfigurationConstants.ATTR_OUTPUT_SYNTAX,
                outputSyntax);
        configuration.setAttribute(SparqlLaunchConfigurationConstants.ATTR_OUTPUT_FOLDER,
                outputFile);
        configuration.setAttribute(SparqlLaunchConfigurationConstants.ATTR_OUTPUT_FILENAME,
                outputFileName);
        configuration.setAttribute(SparqlLaunchConfigurationConstants.ATTR_OPEN_FILE,
                openFileOnCompletion);

    }

    @Override
    public void deactivated(ILaunchConfigurationWorkingCopy workingCopy) {
        super.deactivated(workingCopy);
    }

    @Override
    public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
        configuration.setAttribute(SparqlLaunchConfigurationConstants.ATTR_USE_DEFAULT_OUTPUT_FILE,
                true);
        configuration.setAttribute(SparqlLaunchConfigurationConstants.ATTR_OUTPUT_SYNTAX, ".csv");
        configuration.setAttribute(SparqlLaunchConfigurationConstants.ATTR_OUTPUT_FOLDER,
                (String) null);
        configuration.setAttribute(SparqlLaunchConfigurationConstants.ATTR_OUTPUT_FILENAME,
                (String) null);
        configuration.setAttribute(SparqlLaunchConfigurationConstants.ATTR_OPEN_FILE, true);
    }

    @Override
    protected void updateResourceText(boolean useDefault) {
        fileNameText.setEnabled(!useDefault);
        if (useDefault) {
            fileNameText.setText(defaultOutputFileName == null ? "" : defaultOutputFileName); //$NON-NLS-1$
        }
        else {
            fileNameText.setText(outputFileName == null ? defaultOutputFileName : outputFileName);
        }
        super.updateResourceText(useDefault);
    }

    private void updateDefaultOutputFile() {
        // TODO
        // try {
        // IPath path =
        // XSLTRuntime.defaultOutputFileForInputFile(inputFilename);
        // // determine whether this path exists in the workspace
        // IFile[] files = ResourcesPlugin.getWorkspace().getRoot()
        // .findFilesForLocation(path);
        // if (files.length > 0) {// inside workspace
        // IPath p = new Path(files[0].getProject().getName());
        // p.append(files[0].getParent().getProjectRelativePath());
        //				defaultResource = "${workspace_loc:/" + p.toString() + "}"; //$NON-NLS-1$//$NON-NLS-2$
        // } else {// outside workspace
        // IPath p = path.removeLastSegments(1);
        // defaultResource = p.toOSString();
        // }
        // defaultOutputFileName = path.lastSegment();
        // } catch (CoreException e) {
        // // do nothing
        // }
    }

    @Override
    public boolean isValid(ILaunchConfiguration config) {
        setErrorMessage(null);
        setMessage(null);
        // if variables are present, we cannot resolve the directory
        // String workingDirPath = getText();
        //		if (workingDirPath.indexOf("${") >= 0) //$NON-NLS-1$
        // {
        // IStringVariableManager manager = VariablesPlugin.getDefault()
        // .getStringVariableManager();
        // try {
        // manager.validateStringVariables(workingDirPath);
        // if (mustExist) {
        // String path = manager
        // .performStringSubstitution(workingDirPath);
        // validateResource(path);
        // }
        // } catch (CoreException e) {
        // setErrorMessage(e.getMessage());
        // return false;
        // }
        // } else if (mustExist && workingDirPath.length() > 0) {
        // return validateResource(workingDirPath);
        // } else if (required && workingDirPath.length() == 0) {
        // setErrorMessage(getMessage(ERROR_DIRECTORY_NOT_SPECIFIED));
        // }
        return true;
    }

    @Override
    protected void setLaunchConfiguration(ILaunchConfiguration config) {
        configuration = config;
    }

    @Override
    protected ILaunchConfiguration getLaunchConfiguration() {
        return configuration;
    }
}
