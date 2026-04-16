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

package com.semmtech.plugin.semmweb.core.dialog;


import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.navigator.ResourceComparator;

import com.google.common.base.Strings;
import com.semmtech.plugin.semmweb.core.navigator.ISemanticElement;
import com.semmtech.plugin.semmweb.core.util.SemanticProjectUtils;
import com.semmtech.plugin.semmweb.core.viewers.WorkspaceResourcesFilter;
import com.semmtech.ui.plugin.dialog.AbstractMessageInputDialog;
import com.semmtech.ui.plugin.util.Selections;
import com.semmtech.ui.plugin.viewers.CommonFilterViewer;


public class ImportExternalFileDialog extends AbstractMessageInputDialog {

    public static final int ACTION_COPY = 1;
    public static final int ACTION_LINK = 2;

    private IContainer container;
    private String filename;
    private int selectedAction = ACTION_COPY;
    private Text filenameText;

    public ImportExternalFileDialog(Shell parentShell, String title) {
        super(parentShell, title, null);
        this.showErrorMessage = true;
    }

    private IContainer getContainerFromSelection(ISelection selection) {
        IStructuredSelection structuredSelection = Selections.toStructured(selection);
        if (structuredSelection == null || structuredSelection.isEmpty()) {
            return null;
        }

        Object selected = structuredSelection.getFirstElement();
        if (selected != null) {
            if (selected instanceof IProject) {
                return SemanticProjectUtils.getModelsFolder((IProject) selected);
            }
            else if (selected instanceof IContainer) {
                return (IContainer) selected;
            }
            else if (selected instanceof IResource) {
                IResource resource = (IResource) selected;
                return resource.getParent();
            }
            else if (selected instanceof ISemanticElement) {
                ISemanticElement node = (ISemanticElement) selected;
                IProject project = node.getProject();
                if (project != null) {
                    return SemanticProjectUtils.getModelsFolder(project);
                }
            }
        }
        return null;
    }

    @Override
    protected Control createInputArea(Composite parent) {
        Composite top = (Composite) super.createInputArea(parent);

        GridLayout layout = new GridLayout();
        layout.marginHeight = 0;
        layout.marginWidth = 3;
        top.setLayout(layout);

        Label label = new Label(top, SWT.NONE);
        label.setText("Select the project location and action for the imported file");
        GridData layoutData = new GridData(SWT.BEGINNING, SWT.BEGINNING, true, true);
        label.setLayoutData(layoutData);

        label = new Label(top, SWT.NONE);
        label.setText("Location:");
        layoutData = new GridData(SWT.BEGINNING, SWT.BEGINNING, true, true);
        layoutData.verticalIndent = 3;
        label.setLayoutData(layoutData);

        Composite viewerComposite = new Composite(top, SWT.BORDER);
        layoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
        layoutData.heightHint = 180;
        layoutData.widthHint = 330;
        viewerComposite.setLayoutData(layoutData);
        GridLayoutFactory.fillDefaults().applyTo(viewerComposite);

        CommonFilterViewer viewer = new CommonFilterViewer(viewerComposite, SWT.SINGLE);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(viewer.getTree());
        viewer.setUseHashlookup(true);
        viewer.addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                container = getContainerFromSelection(event.getSelection());
                validateInput();
            }
        });
        viewer.addFilter(new WorkspaceResourcesFilter(IResource.PROJECT, null, false, false, true));
        viewer.setComparator(new ResourceComparator(ResourceComparator.NAME));
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        viewer.setInput(root);
        viewer.expandAll(); // eliminate the arrows in front of projects

        Composite filenameComposite = new Composite(top, SWT.NONE);
        layout = new GridLayout(2, false);
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        layout.marginBottom = 4;
        filenameComposite.setLayout(layout);
        layoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
        filenameComposite.setLayoutData(layoutData);

        label = new Label(filenameComposite, SWT.NONE);
        label.setText("Filename:");
        layoutData = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
        layoutData.widthHint = 66;
        label.setLayoutData(layoutData);

        filenameText = new Text(filenameComposite, SWT.BORDER);
        layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        filenameText.setLayoutData(layoutData);
        filenameText.setText(Strings.nullToEmpty(filename));
        filenameText.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                filename = filenameText.getText();
                validateInput();
            }
        });

        label = new Label(top, SWT.NONE);
        label.setText("Specify how the file should be imported into the workspace");
        layoutData = new GridData(SWT.BEGINNING, SWT.BEGINNING, true, true, 2, 1);
        layoutData.verticalIndent = 3;
        label.setLayoutData(layoutData);

        Composite radioComposite = new Composite(top, SWT.NONE);
        GridLayout radioLayout = new GridLayout();
        radioLayout.marginHeight = 0;
        radioLayout.marginTop = 2;
        radioLayout.verticalSpacing = 6;
        radioComposite.setLayout(radioLayout);
        layoutData = new GridData(GridData.FILL, GridData.FILL, true, false, 2, 1);
        radioComposite.setLayoutData(layoutData);

        GridData radioData = new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 1, 1);
        radioData.horizontalIndent = 12;

        Button copyRadio = new Button(radioComposite, SWT.RADIO);
        copyRadio.setText("Copy file");
        copyRadio.setLayoutData(radioData);
        copyRadio.setSelection((selectedAction & ACTION_COPY) == 1);
        copyRadio.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                selectedAction = ACTION_COPY;
            }
        });

        Button linkRadio = new Button(radioComposite, SWT.RADIO);
        linkRadio.setText("Link to file");
        linkRadio.setLayoutData(radioData);
        linkRadio.setSelection((selectedAction & ACTION_LINK) == 1);
        linkRadio.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                selectedAction = ACTION_LINK;
            }
        });

        applyDialogFont(top);
        return top;
    }

    @Override
    protected void validateInput() {
        String errorMessage = null;
        if (container == null) {
            errorMessage = "Please select a location";
        }
        else if (Strings.isNullOrEmpty(filename)) {
            errorMessage = "Filename cannot be empty";
        }
        else if (fileExists()) {
            errorMessage = "File already exists at location";
        }
        setErrorMessage(errorMessage);
    }

    private boolean fileExists() {
        if (container != null && !Strings.isNullOrEmpty(filename)) {
            if (container instanceof IProject) {
                return ((IProject) container).getFile(filename).exists();
            }
            else if (container instanceof IFolder) {
                return ((IFolder) container).getFile(filename).exists();
            }
        }
        return false;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFilename() {
        return filename;
    }

    public IContainer getContainer() {
        return container;
    }

    public int getAction() {
        return selectedAction;
    }
}
