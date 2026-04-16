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


import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;

import com.google.common.collect.Lists;
import com.semmtech.plugin.semmweb.sparql.SparqlPlugin;
import com.semmtech.plugin.semmweb.sparql.SparqlPluginImages;
import com.semmtech.plugin.semmweb.sparql.debug.ui.Messages;


public class MainSparqlLaunchConfigurationTab extends AbstractLaunchConfigurationTab {
    private final InputModelBlock inputModelBlock;
    private final QueryFileBlock queryFileBlock;

    public MainSparqlLaunchConfigurationTab() {
        IResource[] resourceContext = getContext();
        IFile queryFile = getQueryInput(resourceContext);

        inputModelBlock = new InputModelBlock(null);
        queryFileBlock = new QueryFileBlock(queryFile);
    }

    @Override
    public void createControl(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setFont(parent.getFont());
        GridLayout layout = new GridLayout();
        layout.numColumns = 1;
        composite.setLayout(layout);
        setControl(composite);

        inputModelBlock.createControl(composite);
        queryFileBlock.createControl(composite);
    }

    private static IResource[] getContext() {
        IWorkbenchPage page = SparqlPlugin.getActivePage();
        List<Object> resources = Lists.newArrayList();
        if (page != null) {
            // use selections to find the project
            ISelection selection = page.getSelection();
            if (selection != null && !selection.isEmpty()
                    && selection instanceof IStructuredSelection) {
                IStructuredSelection ss = (IStructuredSelection) selection;
                for (Iterator<?> iter = ss.iterator(); iter.hasNext();) {
                    Object element = iter.next();
                    if (element instanceof IResource)
                        resources.add(element);
                }
                return resources.toArray(new IResource[0]);
            }
            // use current editor to find the project
            IEditorPart part = page.getActiveEditor();
            if (part != null) {
                IEditorInput input = part.getEditorInput();
                IFile file = (IFile) input.getAdapter(IFile.class);
                if (file != null)
                    return new IResource[] { file };
            }
        }
        return new IResource[0];
    }

    private IFile getQueryInput(IResource[] context) {
        for (IResource resource : context) {
            if (resource instanceof IFile
                    && ("sparql".equalsIgnoreCase(resource.getFileExtension()))) //$NON-NLS-1$ //$NON-NLS-2$
                return (IFile) resource;
        }
        return null;
    }

    @Override
    public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
        inputModelBlock.setDefaults(configuration);
        queryFileBlock.setDefaults(configuration);
    }

    @Override
    public void initializeFrom(ILaunchConfiguration configuration) {
        inputModelBlock.initializeFrom(configuration);
        queryFileBlock.initializeFrom(configuration);
    }

    @Override
    public void performApply(ILaunchConfigurationWorkingCopy configuration) {
        inputModelBlock.performApply(configuration);
        queryFileBlock.performApply(configuration);
    }

    @Override
    public String getName() {
        return Messages.XSLMainTab_TabName;
    }

    @Override
    public Image getImage() {
        return SparqlPlugin.getDefault().getImage(SparqlPluginImages.IMG_SPARQL_ICON);
    }

    @Override
    public void setLaunchConfigurationDialog(ILaunchConfigurationDialog dialog) {
        inputModelBlock.setLaunchConfigurationDialog(dialog);
        queryFileBlock.setLaunchConfigurationDialog(dialog);
    }
}
