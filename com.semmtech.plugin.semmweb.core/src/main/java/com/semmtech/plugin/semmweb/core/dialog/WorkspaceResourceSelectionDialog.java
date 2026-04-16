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


import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;

import com.google.common.collect.Lists;
import com.semmtech.plugin.semmweb.core.navigator.IResourceElement;
import com.semmtech.ui.plugin.dialog.AbstractMessageInputDialog;
import com.semmtech.ui.plugin.viewers.CommonFilterViewer;


public class WorkspaceResourceSelectionDialog extends AbstractMessageInputDialog {

    private final List<ViewerFilter> filters;
    private CommonFilterViewer viewer;
    private final List<IResource> selectedResources;
    private boolean allowMultiple;
    private ViewerComparator comparator;
    private ISelectionStatusValidator validator;

    public WorkspaceResourceSelectionDialog(Shell parentShell, String title, String message) {
        super(parentShell, title, message);
        this.filters = Lists.newArrayList();
        this.selectedResources = Lists.newArrayList();
    }

    @Override
    protected Control createInputArea(Composite parent) {
        Composite container = (Composite) super.createInputArea(parent);

        GridLayout layout = new GridLayout(2, false);
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        layout.marginTop = 0;
        container.setLayout(layout);

        Composite inner = new Composite(container, SWT.NONE);
        inner.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 2, 1));

        layout = new GridLayout();
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        layout.marginTop = 0;

        inner.setLayout(layout);

        Composite viewerComposite = new Composite(inner, SWT.BORDER);
        GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
        layoutData.heightHint = 240;
        viewerComposite.setLayoutData(layoutData);
        GridLayoutFactory.fillDefaults().applyTo(viewerComposite);

        viewer = new CommonFilterViewer(viewerComposite, (allowMultiple ? SWT.MULTI : SWT.SINGLE));
        GridDataFactory.fillDefaults().grab(true, true).applyTo(viewer.getTree());
        viewer.setUseHashlookup(true);

        viewer.addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                IStructuredSelection selection = (IStructuredSelection) event.getSelection();
                selectedResources.clear();
                if (!selection.isEmpty()) {
                    for (Object selected : selection.toArray()) {
                        if (selected instanceof IResourceElement) {
                            IResourceElement resourceElement = (IResourceElement) selected;
                            selectedResources.add(resourceElement.getResource());
                        }
                        else if (selected instanceof IResource) {
                            selectedResources.add((IResource) selected);
                        }
                    }
                }
                validateInput();
            }
        });
        viewer.addDoubleClickListener(new IDoubleClickListener() {
            @Override
            public void doubleClick(DoubleClickEvent event) {
                if (getOKButton().isEnabled()) {
                    okPressed();
                }
            }
        });
        for (ViewerFilter filter : filters) {
            viewer.addFilter(filter);
        }
        if (comparator != null) {
            viewer.setComparator(comparator);
        }

        // This has to be done after the viewer has been laid out
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        viewer.setInput(root);

        return container;
    }

    public void setInitialSelection(IResource resource) {
        // TODO: Implement
    }

    public void setComparator(ViewerComparator comparator) {
        this.comparator = comparator;
    }

    public void setValidator(ISelectionStatusValidator validator) {
        this.validator = validator;
    }

    public void addFilter(ViewerFilter filter) {
        filters.add(filter);
    }

    public void clearFilters() {
        filters.clear();
    }

    public IResource getSelectedResource() {
        if (selectedResources.size() > 0) {
            return selectedResources.get(0);
        }
        return null;
    }

    public IResource[] getSelectedResources() {
        IResource[] result = new IResource[selectedResources.size()];
        selectedResources.toArray(result);
        return result;
    }

    public void setAllowMultiple(boolean allow) {
        this.allowMultiple = allow;
    }

    @Override
    protected void validateInput() {
        if (validator != null) {
            IStatus status = validator.validate(selectedResources.toArray());
            String errorMessage = null;
            if (!status.isOK()) {
                errorMessage = status.getMessage();
            }
            setErrorMessage(errorMessage);
        }
    }
}
