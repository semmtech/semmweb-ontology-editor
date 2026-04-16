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


import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.window.Window;
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
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.views.navigator.ResourceComparator;

import com.google.common.base.Strings;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.navigator.IResourceElement;
import com.semmtech.plugin.semmweb.core.viewers.WorkspaceResourcesFilter;
import com.semmtech.ui.plugin.dialog.AbstractMessageInputDialog;
import com.semmtech.ui.plugin.widgets.Widgets;


public class FileSelectionDialog extends AbstractMessageInputDialog {

    private String[] extensions;
    private String filename;
    private Text fileText;

    protected FileSelectionDialog(Shell parentShell, String title, String message) {
        super(parentShell, title, message);
        this.showErrorMessage = false;
        this.extensions = new String[0];
    }

    @SuppressWarnings("unused")
    @Override
    protected Control createInputArea(Composite parent) {
        Composite container = (Composite) super.createInputArea(parent);

        GridLayout layout = new GridLayout(2, false);
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        layout.marginTop = 8;

        container.setLayout(layout);

        Label label = new Label(container, SWT.NONE);
        label.setText("File");
        GridData layoutData = new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 1, 1);
        layoutData.widthHint = 60;
        label.setLayoutData(layoutData);

        fileText = new Text(container, SWT.BORDER);
        layoutData = new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 1);
        fileText.setLayoutData(layoutData);
        fileText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                filename = fileText.getText();
                validateInput();
            }
        });
        fileText.setText(Strings.nullToEmpty(filename));

        Composite buttonComposite = new Composite(container, SWT.NONE);

        layout = new GridLayout(3, false);
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        layout.marginTop = 0;

        buttonComposite.setLayout(layout);
        buttonComposite.setLayoutData(new GridData(GridData.END, GridData.BEGINNING, true, false,
                2, 1));

        new Label(buttonComposite, SWT.NONE);

        GridData buttonData = new GridData(GridData.CENTER, GridData.CENTER, false, false, 1, 1);
        buttonData.widthHint = 106;

        Button workspaceButton = new Button(buttonComposite, SWT.PUSH);
        workspaceButton.setText("Workspace...");
        workspaceButton.setLayoutData(buttonData);
        workspaceButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                handleWorkspaceResource();
            }
        });

        Button filesystemButton = new Button(buttonComposite, SWT.PUSH);
        filesystemButton.setText("File System...");
        filesystemButton.setLayoutData(buttonData);
        filesystemButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                handleExternalFile();
            }
        });

        applyDialogFont(container);
        return container;
    }

    public void setExtensions(String[] extensions) {
        this.extensions = extensions;
    }

    protected void handleExternalFile() {
        FileDialog dialog = new FileDialog(getShell());
        if (!Strings.isNullOrEmpty(filename)) {
            File file = new File(filename);
            if (file.exists()) {
                dialog.setFilterPath(file.getParent());
            }
        }

        String[] filterExtensions = new String[] { "*.*" };

        if (extensions.length > 0) {
            filterExtensions = new String[extensions.length];
            for (int i = 0; i < extensions.length; i++) {
                filterExtensions[i] = "*." + extensions[i]; //$NON-NLS-1$
            }
        }
        dialog.setFilterExtensions(filterExtensions);
        setFilename(dialog.open());
    }

    protected void handleWorkspaceResource() {
        WorkspaceResourceSelectionDialog dialog = new WorkspaceResourceSelectionDialog(getShell(),
                getText(), "Select a resource from the workspace below");

        dialog.addFilter(new WorkspaceResourcesFilter(extensions, false));
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
            setFilename(file.getLocation().toFile().getAbsolutePath());
        }
    }

    public void setFilename(String filename) {
        this.filename = filename;
        if (!Widgets.isNullOrDisposed(fileText)) {
            fileText.setText(Strings.nullToEmpty(filename));
        }
        validateInput();
    }

    @Override
    protected void validateInput() {
        String errorMessage = null;
        if (Strings.isNullOrEmpty(filename)) {
            errorMessage = "Filename cannot be null or empty";
        }
        setErrorMessage(errorMessage);
    }

    public String getFilename() {
        return filename;
    }
}
