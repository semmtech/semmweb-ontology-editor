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

package com.semmtech.plugin.utils.jena.schemagen.wizards;


import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

import com.google.common.base.Strings;


public class SchemagenConfigWizardPage extends WizardPage {

    private Text inputText;
    private Button inputButton;
    private Text outputText;
    private Button outputButton;
    private Text classnameText;
    private Text packageText;

    private IResource inputValue;
    private Path outputValue;
    private String classnameValue;
    private String packageValue;

    private final Listener listener = new Listener() {

        @Override
        public void handleEvent(Event event) {
            int type = event.type;
            Widget widget = event.widget;

            if (type == SWT.Selection) {
                if (widget == inputButton) {
                    browseInput();
                }
                else if (widget == outputButton) {
                    browseOutput();
                }
            }
            else if (type == SWT.Modify) {
                if (widget == classnameText) {
                    classnameValue = classnameText.getText();
                }
                if (widget == packageText) {
                    packageValue = packageText.getText();
                }
            }
            validatePage();
        }
    };

    public SchemagenConfigWizardPage() {
        super("importPage");
        setTitle("Export RDF/OWL Model to Java Vocabulary");
        setDescription("This wizard converts an RDF/OWL model into a Java source file.");
    }

    protected void browseInput() {
        Shell shell = getShell();
        ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(shell,
                new WorkbenchLabelProvider(), new BaseWorkbenchContentProvider());
        dialog.setTitle("Input Model Selection");
        dialog.setMessage("Select input RDF/OWL model:");
        dialog.setInput(ResourcesPlugin.getWorkspace().getRoot());
        if (dialog.open() == Dialog.OK) {
            Object[] results = dialog.getResult();
            if (results.length > 0 && results[0] instanceof IResource) {
                inputValue = (IResource) results[0];
            }
            else {
                inputValue = null;
            }
            refresh();
        }
    }

    protected void browseOutput() {
        Shell shell = getShell();
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        ContainerSelectionDialog dialog = new ContainerSelectionDialog(shell, root, false,
                "Select the output container");
        if (dialog.open() == Dialog.OK) {
            Object[] results = dialog.getResult();
            if (results.length > 0) {
                outputValue = (Path) results[0];
            }
            else {
                outputValue = null;
            }
            refresh();
        }
    }

    @Override
    public void createControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        GridLayoutFactory.fillDefaults().margins(4, 6).spacing(5, 5).numColumns(3)
                .applyTo(container);

        // Input
        Label label = new Label(container, SWT.NONE);
        label.setText("Model");
        GridDataFactory.swtDefaults().hint(90, SWT.DEFAULT).applyTo(label);

        inputText = new Text(container, SWT.BORDER);
        inputText.addListener(SWT.Modify, listener);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(inputText);

        inputButton = new Button(container, SWT.PUSH);
        inputButton.setText("Browse...");
        inputButton.addListener(SWT.Selection, listener);
        GridDataFactory.swtDefaults().hint(80, SWT.DEFAULT).applyTo(inputButton);

        // Output
        label = new Label(container, SWT.NONE);
        label.setText("Output");
        GridDataFactory.swtDefaults().hint(90, SWT.DEFAULT).applyTo(label);

        outputText = new Text(container, SWT.BORDER);
        outputText.addListener(SWT.Modify, listener);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(outputText);

        outputButton = new Button(container, SWT.PUSH);
        outputButton.setText("Browse...");
        outputButton.addListener(SWT.Selection, listener);
        GridDataFactory.swtDefaults().hint(80, SWT.DEFAULT).applyTo(outputButton);

        // Classname
        label = new Label(container, SWT.NONE);
        label.setText("Class name");
        GridDataFactory.swtDefaults().hint(90, SWT.DEFAULT).applyTo(label);

        classnameText = new Text(container, SWT.BORDER);
        classnameText.addListener(SWT.Modify, listener);
        GridDataFactory.fillDefaults().span(2, 1).grab(true, false).applyTo(classnameText);

        // Package
        label = new Label(container, SWT.NONE);
        label.setText("Package");
        GridDataFactory.swtDefaults().hint(90, SWT.DEFAULT).applyTo(label);

        packageText = new Text(container, SWT.BORDER);
        packageText.addListener(SWT.Modify, listener);
        GridDataFactory.fillDefaults().span(2, 1).grab(true, false).applyTo(packageText);

        refresh();
        setPageComplete(false);
        setControl(container);
    }

    protected void refresh() {
        String input = null;
        if (inputValue != null) {
            input = inputValue.getFullPath().toString();
        }
        inputText.setText(Strings.nullToEmpty(input));
        String output = null;
        if (outputValue != null) {
            output = outputValue.toString();
        }
        outputText.setText(Strings.nullToEmpty(output));
        classnameText.setText(Strings.nullToEmpty(classnameValue));
        packageText.setText(Strings.nullToEmpty(packageValue));

    }

    protected void validatePage() {
        String error = null;
        if (inputValue == null) {
            error = "Please specify an input";
        }
        else if (outputValue == null) {
            error = "Please specify the output path";
        }
        else if (Strings.isNullOrEmpty(classnameValue)) {
            error = "Please specify a class name";
        }
        else if (Strings.isNullOrEmpty(packageValue)) {
            error = "Please specify a package";
        }
        setErrorMessage(error);
        setPageComplete(error == null);
    }

    public IResource getInput() {
        return inputValue;
    }

    public Path getOutput() {
        return outputValue;
    }

    public String getClassName() {
        return classnameValue;
    }

    public String getPackage() {
        return packageValue;
    }
}
