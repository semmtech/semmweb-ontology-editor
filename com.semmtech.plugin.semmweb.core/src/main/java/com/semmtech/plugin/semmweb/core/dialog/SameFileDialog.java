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

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.semmtech.plugin.semmweb.core.operations.CopyAndMoveFilesOperation;
import com.semmtech.plugin.semmweb.core.util.ResourcesUtil;
import com.semmtech.ui.plugin.dialog.AbstractMessageInputDialog;


/**
 * This Dialog is shown in case of file with the same name. The provided options
 * are:
 * 
 * <ul>
 * <li>Overwrite</li>
 * <li>Skip</li>
 * <li>Rename</li>
 * </ul>
 * 
 * <p>
 * In case of Overwrite and Skip the Apply to all option is available.
 * <p>
 * NB: The dialog does not implements the 'copy/move' logic, it just provide
 * some value that can be used by the class that provide the logic (ex:
 * {@link CopyAndMoveFilesOperation})
 * 
 * @author Simone Rondelli
 * 
 */
public class SameFileDialog extends AbstractMessageInputDialog {

    public static final int STATUS_OVERWRITE = 0;
    public static final int STATUS_OVERWRITE_ALL = 1;
    public static final int STATUS_SKIP = 2;
    public static final int STATUS_SKIP_ALL = 3;
    public static final int STATUS_RENAME = 4;

    private Composite container;
    private IPath sourceFile;
    private IContainer destFolder;

    private Button overwriteRadio;
    private Button skipRadio;

    private Button applyToAll;

    private Button renameRadio;
    private Text newFileNameTxt;

    private int status;
    private String newFileName;

    public SameFileDialog(Shell parentShell, IPath sourceFile, IContainer destFolder) {
        super(parentShell, "File Name Conflict", "The file " + sourceFile.lastSegment()
                + " already exists in the target folder", false);
        this.sourceFile = sourceFile;
        this.destFolder = destFolder;
    }

    @Override
    protected Control createInputArea(Composite parent) {
        if (container == null) {
            container = (Composite) super.createInputArea(parent);
        }
        return createControls(container);
    }

    private Control createControls(Composite parent) {
        Composite top = new Composite(parent, SWT.NONE);

        GridLayoutFactory.fillDefaults().numColumns(2).spacing(10, 5).applyTo(top);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(top);

        GridDataFactory chkLayout = GridDataFactory.fillDefaults().grab(false, false)
                .indent(10, SWT.DEFAULT).span(2, 1);

        overwriteRadio = new Button(top, SWT.RADIO);
        overwriteRadio.setText("Overwrite");
        overwriteRadio.setSelection(true);
        chkLayout.copy().indent(10, 10).applyTo(overwriteRadio);

        skipRadio = new Button(top, SWT.RADIO);
        skipRadio.setText("Skip");
        chkLayout.applyTo(skipRadio);

        renameRadio = new Button(top, SWT.RADIO);
        renameRadio.setText("Rename");
        renameRadio.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                newFileNameTxt.setEnabled(renameRadio.getSelection());
                applyToAll.setEnabled(!renameRadio.getSelection());
            }
        });
        chkLayout.applyTo(renameRadio);

        Label label = new Label(top, SWT.WRAP);
        label.setText("New File Name:");
        GridDataFactory.fillDefaults().span(1, 1).indent(28, 5).align(SWT.BEGINNING, SWT.CENTER)
                .applyTo(label);

        newFileNameTxt = new Text(top, SWT.SINGLE | SWT.BORDER);
        newFileNameTxt.setText(ResourcesUtil.getDefaultNewFileName(sourceFile.lastSegment(),
                destFolder));
        newFileNameTxt.setEnabled(false);
        GridDataFactory.fillDefaults().span(1, 1).indent(SWT.DEFAULT, 5).grab(true, false)
                .applyTo(newFileNameTxt);

        applyToAll = new Button(top, SWT.CHECK);
        applyToAll.setText("Apply to all subsequent model");
        GridDataFactory.fillDefaults().span(2, 1).indent(10, 30).applyTo(applyToAll);

        return top;
    }

    public String getNewFileName() {
        return newFileName;
    }

    @Override
    protected void okPressed() {
        if (!renameRadio.getSelection()) {
            setStatus();
            super.okPressed();
            return;
        }

        String newFileName = newFileNameTxt.getText();

        File realFile = destFolder.getFile(new Path(newFileName)).getRawLocation().toFile();

        if (realFile.exists()) {
            MessageDialog.openWarning(getShell(), "File Name",
                    "There's already exist a file called " + newFileName
                            + " in the selected folder.");
            return;
        }
        setStatus();
        super.okPressed();
    }

    private void setStatus() {
        if (overwriteRadio.getSelection()) {
            status = applyToAll.getSelection() ? STATUS_OVERWRITE_ALL : STATUS_OVERWRITE;
        }
        else if (skipRadio.getSelection()) {
            status = applyToAll.getSelection() ? STATUS_SKIP_ALL : STATUS_SKIP;
        }
        else {
            status = STATUS_RENAME;
            newFileName = newFileNameTxt.getText();
        }
    }

    public int getStatus() {
        return status;
    }
}
