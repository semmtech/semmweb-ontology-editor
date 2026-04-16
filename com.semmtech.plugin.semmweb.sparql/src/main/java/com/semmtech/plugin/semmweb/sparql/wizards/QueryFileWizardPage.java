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

package com.semmtech.plugin.semmweb.sparql.wizards;


import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.navigator.ResourceComparator;

import com.google.common.base.Strings;
import com.semmtech.plugin.semmweb.core.viewers.WorkspaceResourcesFilter;
import com.semmtech.ui.plugin.util.Selections;
import com.semmtech.ui.plugin.viewers.CommonFilterViewer;
import com.semmtech.ui.plugin.widgets.Widgets;
import com.semmtech.ui.plugin.wizard.BaseWizardPage;


public class QueryFileWizardPage extends BaseWizardPage {

    private static final String PAGE_NAME = "queryFilePage";
    private static final String PAGE_TITLE = "Query Filename";
    private static final String PAGE_DESCRIPTION = "This page will help you select the location of the query file.";

    private IContainer container;
    private String filename;
    private Text filenameText;

    protected QueryFileWizardPage(ISelection selection) {
        super(PAGE_NAME);
        setTitle(PAGE_TITLE);
        setDescription(PAGE_DESCRIPTION);
        container = Selections.retrieveFirstAsType(selection, IContainer.class);
    }

    @Override
    public void createControl(Composite parent) {
        Composite top = new Composite(parent, SWT.NULL);

        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 8;
        top.setLayout(layout);

        Label label = new Label(top, SWT.WRAP);
        GridData layoutData = new GridData(GridData.FILL, GridData.BEGINNING, false, false, 2, 1);
        layoutData.widthHint = 500;
        label.setLayoutData(layoutData);
        label.setText("Specify the location of the SPARQL query file.");

        label = new Label(top, SWT.NONE);
        layoutData = new GridData(GridData.FILL, GridData.BEGINNING, false, false, 2, 1);
        layoutData.heightHint = 5;
        label.setLayoutData(layoutData);

        layoutData = new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 2, 1);
        layoutData.widthHint = 95;
        label = new Label(top, SWT.NONE);
        label.setLayoutData(layoutData);
        label.setText("Location:");

        Composite viewerComposite = new Composite(top, SWT.BORDER);
        layoutData = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
        layoutData.heightHint = 180;
        viewerComposite.setLayoutData(layoutData);
        GridLayoutFactory.fillDefaults().applyTo(viewerComposite);

        CommonFilterViewer viewer = new CommonFilterViewer(viewerComposite, SWT.SINGLE);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(viewer.getTree());
        viewer.setUseHashlookup(true);
        viewer.addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                ISelection selection = event.getSelection();
                container = Selections.retrieveFirstAsType(selection, IContainer.class);
                setPageComplete(validatePage());
            }
        });
        viewer.addFilter(new WorkspaceResourcesFilter(IResource.FOLDER | IResource.PROJECT, null,
                false, false, false));
        viewer.setComparator(new ResourceComparator(ResourceComparator.NAME));
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        viewer.setInput(root);

        if (container != null) {
            viewer.setSelection(new StructuredSelection(container), true);
        }

        label = new Label(top, SWT.NONE);
        label.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 1, 1));
        label.setText("Filename:");

        filename = filename == null ? "query.sparql" : filename;
        filenameText = new Text(top, SWT.BORDER);
        filenameText.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 1,
                1));
        filenameText.setText(filename);
        filenameText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                filename = filenameText.getText();
                setPageComplete(validatePage());
            }
        });

        setPageComplete(validatePage());

        setControl(top);
    }

    private boolean validatePage() {
        String errorMessage = null;
        if (container == null) {
            errorMessage = "Please selection a container";
        }
        else if (Strings.isNullOrEmpty(filename)) {
            errorMessage = "Filename cannot be empty";
        }
        else {
            IFile file = container.getFile(new Path(filename));
            if (file.exists()) {
                errorMessage = "File already exists";
            }
        }
        setErrorMessage(errorMessage);
        return (errorMessage == null);
    }

    public IProject getProject() {
        if (container != null) {
            if (container instanceof IProject) {
                return (IProject) container;
            }
            container.getProject();
        }
        return null;
    }

    public String getFolder() {
        if (container != null) {
            return container.getFullPath().toString();
        }
        return null;
    }

    public void setFilename(String filename) {
        this.filename = filename;
        if (!Widgets.isNullOrDisposed(filenameText)) {
            filenameText.setText(filename);
        }
    }

    public String getFilename() {
        String result = filename;
        if (!Strings.isNullOrEmpty(result)) {
            if (!StringUtils.contains(result, '.')) {
                result += ".sparql";
            }
        }
        return result;
    }
}
