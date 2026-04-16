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


import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.views.navigator.ResourceComparator;

import com.semmtech.plugin.semmweb.core.viewers.ProjectResourceFilter;
import com.semmtech.ui.plugin.dialog.AbstractMessageInputDialog;
import com.semmtech.ui.plugin.util.Selections;
import com.semmtech.ui.plugin.viewers.CommonFilterViewer;


/**
 * This dialog permits to choose a project between the ones in the workspace. If
 * the parameter excludedProject is given then it won't be shown in the projects
 * list.
 * 
 * @author Simone Rondelli
 */
public class ProjectSelectionDialog extends AbstractMessageInputDialog {

    private Composite container;

    private final IProject excludedProject;

    private IProject selectedProject;

    public ProjectSelectionDialog(Shell parentShell) {
        this(parentShell, null);
    }

    /**
     * @param excludedProject
     *            this project won't be shown in the project list
     */
    public ProjectSelectionDialog(Shell parentShell, IProject excludedProject) {
        this(parentShell, excludedProject, "Select Project", "Select the Project");

    }

    /**
     * @param excludedProject
     *            this project won't be shown in the project list
     */
    public ProjectSelectionDialog(Shell parentShell, IProject excludedProject, String title,
            String message) {
        super(parentShell, title, message);
        this.excludedProject = excludedProject;
    }

    @Override
    protected Control createInputArea(Composite parent) {
        if (container == null) {
            container = (Composite) super.createInputArea(parent);
        }
        return createControls(container);
    }

    private Control createControls(Composite parent) {
        Composite top = new Composite(parent, SWT.NULL);

        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 8;
        top.setLayout(layout);

        Label label = new Label(top, SWT.WRAP);
        GridData layoutData = new GridData(GridData.FILL, GridData.BEGINNING, false, false, 2, 1);
        layoutData.heightHint = 5;
        layoutData.widthHint = 500;
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
                selectedProject = Selections.retrieveFirstAsType(selection, IProject.class);
            }
        });

        viewer.addFilter(new ProjectResourceFilter(excludedProject));
        viewer.setComparator(new ResourceComparator(ResourceComparator.NAME));
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        viewer.setInput(root);
        return top;
    }

    public IProject getSelectedProject() {
        return selectedProject;
    }
}
