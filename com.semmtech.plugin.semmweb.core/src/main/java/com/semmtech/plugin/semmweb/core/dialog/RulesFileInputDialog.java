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
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

import com.semmtech.ui.plugin.dialog.AbstractMessageInputDialog;


public class RulesFileInputDialog extends AbstractMessageInputDialog {

    static class ExtensionViewerFilter extends ViewerFilter {

        String filter = "";

        public ExtensionViewerFilter(String filter) {
            this.filter = filter;
        }

        @Override
        public boolean select(Viewer viewer, Object parentElement, Object element) {
            if (element instanceof IContainer)
                return true;
            if (element instanceof IResource) {
                if (filter.length() > 0)
                    return (((IResource) element).getName().endsWith(filter));
                return true;
            }
            return false;
        }

    }

    private String filename = null;
    private Text filenameText;

    public RulesFileInputDialog(Shell parentShell, String title, String message) {
        super(parentShell, title, message);
    }

    @Override
    protected Control createInputArea(Composite parent) {
        Composite top = (Composite) super.createInputArea(parent);

        Group inputGroup = new Group(top, SWT.NONE);
        inputGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1));
        inputGroup.setLayout(new GridLayout(3, true));
        inputGroup.setText("Rules File");

        filenameText = new Text(inputGroup, SWT.BORDER);
        if (filename != null)
            filenameText.setText(filename);
        filenameText.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 3, 1));
        filenameText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                if (e.getSource() instanceof Text)
                    filename = ((Text) e.getSource()).getText();
            }
        });

        Button workspaceButton = new Button(inputGroup, SWT.PUSH);
        workspaceButton.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
        workspaceButton.setText("Workspace...");
        // workspaceButton.setEnabled(false);
        workspaceButton.addListener(SWT.Selection, new Listener() {

            @Override
            public void handleEvent(Event event) {
                ElementTreeSelectionDialog elementSelector = new ElementTreeSelectionDialog(
                        getShell(), new WorkbenchLabelProvider(),
                        new BaseWorkbenchContentProvider());
                elementSelector.addFilter(new ExtensionViewerFilter(".rules"));
                elementSelector.setInput(ResourcesPlugin.getWorkspace().getRoot());
                elementSelector.setTitle("Title");
                elementSelector.setMessage("Message");
                elementSelector.setAllowMultiple(false);
                // elementSelector.setImage(image);
                elementSelector.open();

                if (elementSelector.getReturnCode() == Window.OK) {
                    IFile f = (IFile) elementSelector.getFirstResult();
                    updateFilename(f.getLocation().toOSString());
                }
            }
        });

        Button fileSystemButton = new Button(inputGroup, SWT.PUSH);
        fileSystemButton.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
        fileSystemButton.setText("File System...");
        fileSystemButton.addListener(SWT.Selection, new Listener() {

            @Override
            public void handleEvent(Event event) {
                FileDialog dialog = new FileDialog(getShell(), SWT.OPEN);
                dialog.setFilterExtensions(new String[] { "*.rules" });
                String rulesFilename = dialog.open();
                if (rulesFilename != null)
                    updateFilename(rulesFilename);
            }
        });

        Button openFilesButton = new Button(inputGroup, SWT.PUSH);
        openFilesButton.setEnabled(false);
        openFilesButton.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
        openFilesButton.setText("Open Files...");

        return top;
    }

    private void updateFilename(String filename) {
        this.filename = filename;
        filenameText.setText(filename);
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFilename() {
        return filename;
    }
}
