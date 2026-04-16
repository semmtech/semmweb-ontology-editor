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
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.views.navigator.ResourceComparator;

import com.semmtech.plugin.semmweb.core.internal.navigator.SemanticProjectManager;
import com.semmtech.plugin.semmweb.core.navigator.IModel;
import com.semmtech.plugin.semmweb.core.navigator.IModelCollection;
import com.semmtech.plugin.semmweb.core.navigator.ISemanticElement;
import com.semmtech.plugin.semmweb.core.viewers.TreeContentProvider;
import com.semmtech.ui.plugin.dialog.AbstractMessageInputDialog;
import com.semmtech.ui.plugin.util.Selections;
import com.semmtech.ui.plugin.viewers.CommonFilterViewer;


/**
 * This dialog permits to choose a model between the ones in a selcted project.
 * If the parameter exludedModel is given then it won't be shown in the projects
 * list.
 * 
 * @author Simone Rondelli
 */
public class ModelSelectionDialog extends AbstractMessageInputDialog {

    private Composite container;

    private final IModel excludedModel;
    private final IProject rootProject;

    private IModel selectedModel;

    public ModelSelectionDialog(Shell parentShell, IProject rootProject) {
        this(parentShell, rootProject, null, "Select Model", "Select the Model");
    }

    public ModelSelectionDialog(Shell parentShell, IProject rootProject, IModel excludedModel) {
        this(parentShell, rootProject, excludedModel, "Select Model", "Select the Model");
    }

    /**
     * @param rootProject
     *            the project from which the models have to be retrieved
     * @param excludedModel
     *            this model won't be shown in the project list
     */
    public ModelSelectionDialog(Shell parentShell, IProject rootProject, IModel excludedModel,
            String title, String message) {
        super(parentShell, title, message);
        this.excludedModel = excludedModel;
        this.rootProject = rootProject;
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
                selectedModel = Selections.retrieveFirstAsType(selection, IModel.class);
            }
        });

        if (excludedModel != null) {
            viewer.addFilter(new ProjectModelResourceFilter(excludedModel));
        }
        viewer.setComparator(new ResourceComparator(ResourceComparator.NAME));
        viewer.setContentProvider(new ModelTreeContentProvider());
        viewer.setInput(SemanticProjectManager.getSemanticProjectManager(rootProject)
                .obtainProject().getChildrenByType(ISemanticElement.MODEL_COLLECTION).get(0));
        return top;
    }

    /**
     * Filter the models and exclude from the view the one passed as parameter
     */
    private class ProjectModelResourceFilter extends ViewerFilter {

        private IModel excludedModel;

        public ProjectModelResourceFilter(IModel excludedModel) {
            super();
            this.excludedModel = excludedModel;
        }

        @Override
        public boolean select(Viewer viewer, Object parentElement, Object element) {
            return !element.equals(excludedModel);
        }
    }

    private class ModelTreeContentProvider extends TreeContentProvider {
        @Override
        public boolean hasChildren(Object element) {
            if (element instanceof IModelCollection) {
                return true;
            }

            return false;
        }

        @Override
        public Object[] getElements(Object inputElement) {
            if (inputElement instanceof IModelCollection) {
                return ((IModelCollection) inputElement).getChildren();
            }
            return null;
        }
    }

    public IModel getSelectedModel() {
        return selectedModel;
    }
}
