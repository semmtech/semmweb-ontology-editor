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

package com.semmtech.ui.plugin.ide;


import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.part.DrillDownComposite;
import org.eclipse.ui.views.navigator.ResourceComparator;


/**
 * Group composite for selecting resources from the workspace.
 * 
 * @author Mike Henrichs
 * 
 */
public class WorkspaceResourceSelectionGroup extends Composite {

    private TreeViewer treeViewer;
    private Label messageLabel;

    public WorkspaceResourceSelectionGroup(Composite parent, int style) {
        super(parent, style);
        createContents();
    }

    protected void createContents() {
        GridLayout layout = new GridLayout();
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        setLayout(layout);

        GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
        setLayoutData(layoutData);

        messageLabel = new Label(this, SWT.WRAP);
        messageLabel.setText("");
        messageLabel.setFont(this.getFont());

        DrillDownComposite drillDown = new DrillDownComposite(this, SWT.BORDER);
        layoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
        layoutData.heightHint = 240;
        drillDown.setLayoutData(layoutData);

        treeViewer = new TreeViewer(drillDown, SWT.NONE);
        drillDown.setChildTree(treeViewer);

        treeViewer.setContentProvider(new WorkbenchContentProvider());
        // treeViewer.setLabelProvider(WorkbenchLabelProvider.getDecoratingWorkbenchLabelProvider());
        treeViewer.setLabelProvider(new WorkbenchLabelProvider());
        treeViewer.setComparator(new ResourceComparator(ResourceComparator.NAME));
        treeViewer.setUseHashlookup(true);

        // This has to be done after the viewer has been laid out
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        treeViewer.setInput(root);

        Dialog.applyDialogFont(this);
    }

    public void setMessage(String message) {
        messageLabel.setText(message);
        layout(true, true);
    }

    public void addFilter(ViewerFilter filter) {
        treeViewer.addFilter(filter);
        treeViewer.refresh();
    }
}
