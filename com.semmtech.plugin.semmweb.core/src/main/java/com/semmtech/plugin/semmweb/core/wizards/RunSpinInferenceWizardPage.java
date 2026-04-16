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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.views.navigator.ResourceComparator;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.dialog.WorkspaceResourceSelectionDialog;
import com.semmtech.plugin.semmweb.core.navigator.IResourceElement;
import com.semmtech.plugin.semmweb.core.viewers.ProjectResourceFilter;
import com.semmtech.plugin.semmweb.core.viewers.WorkspaceResourcesFilter;
import com.semmtech.semantics.util.FileUtils;
import com.semmtech.ui.plugin.wizard.BaseWizardPage;


/**
 * 
 * @author Sander Stolk
 */
public class RunSpinInferenceWizardPage extends BaseWizardPage {

    protected final static String PAGE_NAME = "Spin Inference";
    protected final static String PAGE_DESCRIPTION = "This wizard is used to run SPIN inference (or rules) over a model consisting of one or more input models. The inferred triples are written to the defined output model.";

    protected class WorkspaceModelInput {
        public Text text;
        public Button button;
        public IFile file;
    }

    protected final List<WorkspaceModelInput> workspaceModelInputs;
    protected IProject outputModelProject;

    protected Text outputModelNameText;
    protected Text outputModelProjectText;

    public RunSpinInferenceWizardPage() {
        super(PAGE_NAME);
        setDescription(PAGE_DESCRIPTION);
        workspaceModelInputs = Lists.newArrayList();
    }

    public List<IFile> getInputModelFiles() {
        List<IFile> result = Lists.newArrayList();
        for (WorkspaceModelInput wmi : workspaceModelInputs) {
            if (wmi != null && wmi.file != null) {
                result.add(wmi.file);
            }
        }
        return result;
    }

    public String getOutputModelName() {
        return outputModelNameText.getText();
    }

    public IProject getOutputModelProject() {
        return outputModelProject;
    }

    @Override
    public void createControl(Composite parent) {
        Composite outerComposite = new Composite(parent, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(2).applyTo(outerComposite);

        Label inputModelsLabel = new Label(outerComposite, SWT.NONE);
        inputModelsLabel.setText("Input models:");
        GridDataFactory.fillDefaults().span(2, 1).applyTo(inputModelsLabel);

        for (int i = 0; i < 5; i++) {
            WorkspaceModelInput wmi = createWorkspaceModelControls(outerComposite);
            workspaceModelInputs.add(wmi);
        }

        Label outputModelLabel = new Label(outerComposite, SWT.NONE);
        outputModelLabel.setText("Output model (name and project):");
        GridDataFactory.fillDefaults().span(2, 1).applyTo(outputModelLabel);

        outputModelNameText = new Text(outerComposite, SWT.BORDER);
        GridDataFactory.fillDefaults().grab(true, false).span(2, 1).applyTo(outputModelNameText);
        outputModelNameText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                validatePage();
            }
        });

        outputModelProjectText = new Text(outerComposite, SWT.BORDER);
        GridData layoutData = new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 1);
        outputModelProjectText.setLayoutData(layoutData);
        outputModelProjectText.setEditable(false);

        Button button = new Button(outerComposite, SWT.PUSH);
        button.setText("Project...");
        layoutData = new GridData(SWT.RIGHT, GridData.CENTER, false, false, 1, 1);
        layoutData.widthHint = 103;
        button.setLayoutData(layoutData);
        button.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                handleWorkspaceResource(outputModelProjectText);
            }
        });

        setControl(outerComposite);
    }

    protected void handleWorkspaceResource(final Text outputText) {
        IProject project = getWorkspaceProject();
        outputModelProject = project;
        if (project == null) {
            outputText.setText(new String());
        }
        else {
            outputText.setText(project.toString());
        }
        validatePage();
    }

    protected WorkspaceModelInput createWorkspaceModelControls(Composite parent) {
        final WorkspaceModelInput result = new WorkspaceModelInput();

        Text text = new Text(parent, SWT.BORDER);
        GridData layoutData = new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 1);
        text.setLayoutData(layoutData);
        text.setEditable(false);
        result.text = text;

        Button button = new Button(parent, SWT.PUSH);
        button.setText("Model...");
        layoutData = new GridData(SWT.RIGHT, GridData.CENTER, false, false, 1, 1);
        layoutData.widthHint = 103;
        button.setLayoutData(layoutData);
        button.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                handleWorkspaceResource(result);
            }
        });
        result.button = button;

        return result;
    }

    protected void handleWorkspaceResource(final WorkspaceModelInput wmi) {
        IFile file = getWorkspaceResource();
        wmi.file = file;
        if (file == null) {
            wmi.text.setText(new String());
        }
        else {
            wmi.text.setText(file.toString());
        }
        validatePage();
    }

    private static final String[][] EXTENSIONS = FileUtils.getFileDialogFormats(true, true);

    protected IProject getWorkspaceProject() {
        WorkspaceResourceSelectionDialog dialog = new WorkspaceResourceSelectionDialog(getShell(),
                "Select project", "Select a project from the workspace below");

        dialog.addFilter(new ProjectResourceFilter());
        dialog.setComparator(new ResourceComparator(ResourceComparator.NAME));
        dialog.setAllowMultiple(false);

        if (dialog.open() == Window.OK) {
            IProject project = (IProject) dialog.getSelectedResource();
            return project;
        }
        return null;
    }

    protected IFile getWorkspaceResource() {
        WorkspaceResourceSelectionDialog dialog = new WorkspaceResourceSelectionDialog(getShell(),
                "Select model", "Select a model from the workspace below");

        dialog.addFilter(new WorkspaceResourcesFilter(EXTENSIONS[0], false));
        dialog.setValidator(new ISelectionStatusValidator() {
            @Override
            public IStatus validate(Object[] selection) {
                if (selection.length == 0) {
                    return new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, ""); //$NON-NLS-1$
                }
                for (int i = 0; i < selection.length; i++) {
                    if (!(selection[i] instanceof IFile)
                            && !(selection[i] instanceof IResourceElement)) {
                        return new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, ""); //$NON-NLS-1$
                    }
                }
                return new Status(IStatus.OK, CorePlugin.PLUGIN_ID, ""); //$NON-NLS-1$
            }
        });
        dialog.setComparator(new ResourceComparator(ResourceComparator.NAME));
        dialog.setAllowMultiple(false);

        if (dialog.open() == Window.OK) {
            IFile file = (IFile) dialog.getSelectedResource();
            return file;
        }
        return null;
    }

    protected void validatePage() {
        boolean complete = true;

        List<IFile> files = getInputModelFiles();
        if (files.isEmpty()) {
            complete = false;
        }

        if (Strings.isNullOrEmpty(getOutputModelName())) {
            complete = false;
        }

        if (getOutputModelProject() == null) {
            complete = false;
        }

        setPageComplete(complete);
    }
}
