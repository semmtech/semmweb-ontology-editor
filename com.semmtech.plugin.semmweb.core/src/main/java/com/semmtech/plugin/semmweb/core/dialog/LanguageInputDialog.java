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

import org.eclipse.jface.dialogs.IDialogConstants;
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

import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.CorePluginImages;
import com.semmtech.plugin.semmweb.core.widgets.ImageCombo;
import com.semmtech.ui.plugin.dialog.AbstractMessageInputDialog;


/**
 * OntologyInputDialog allows users to provide information about an ontology,
 * like name, URI, prefix, and possibly an location URL.
 * 
 * @author Mike Henrichs
 * 
 */
public class LanguageInputDialog extends AbstractMessageInputDialog {

    private static final String DIALOG_TITLE = "New Language";
    private static final String DIALOG_MESSAGE = "Please specify the name and code for the new language below.";
    private static final int DIALOG_LABEL_WIDTH = 100;

    private List<String> flagKeys;

    private String name;
    private String code;
    private String imageKey;

    private Text codeText;
    private Button okButton;
    private Text languageText;
    private ImageCombo imageCombo;

    /**
     * @wbp.parser.constructor
     */
    public LanguageInputDialog(Shell parentShell) {
        this(parentShell, DIALOG_TITLE, DIALOG_MESSAGE, null, null, CorePluginImages.IMG_XSD);
    }

    public LanguageInputDialog(Shell parentShell, String title, String message) {
        this(parentShell, title, message, null, null, CorePluginImages.IMG_XSD);
    }

    public LanguageInputDialog(Shell parentShell, String title, String message, String name,
            String code, String imageKey) {
        super(parentShell, title, message);
        this.name = name;
        this.code = code;
        this.imageKey = imageKey;

        this.flagKeys = CorePluginImages.getAllFlagKeys();
    }

    @Override
    protected Control createInputArea(Composite parent) {
        Composite composite = (Composite) super.createInputArea(parent);
        GridLayout gridLayout = (GridLayout) composite.getLayout();
        gridLayout.marginTop = 8;
        gridLayout.numColumns = 2;

        Label label = new Label(composite, SWT.NONE);
        label.setLayoutData(new GridData(DIALOG_LABEL_WIDTH, 20));
        label.setText("Name:");

        languageText = new Text(composite, SWT.BORDER);
        languageText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        languageText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                name = languageText.getText();
                validate();
            }
        });
        if (name != null) {
            languageText.setText(name);
        }

        label = new Label(composite, SWT.NONE);
        label.setLayoutData(new GridData(DIALOG_LABEL_WIDTH, 20));
        label.setText("Code:");

        codeText = new Text(composite, SWT.SINGLE | SWT.BORDER);
        codeText.setLayoutData(new GridData());
        ((GridData) codeText.getLayoutData()).widthHint = 60;
        codeText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                code = codeText.getText();
                validate();
            }
        });
        if (code != null) {
            codeText.setText(code);
        }

        label = new Label(composite, SWT.NONE);
        {
            GridData data = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
            data.heightHint = 20;
            label.setLayoutData(data);
        }
        label.setText("Image:");

        imageCombo = new ImageCombo(composite, SWT.READ_ONLY | SWT.BORDER);
        imageCombo.add(CorePlugin.getDefault().getImage(CorePluginImages.IMG_XSD), "xsd.png");
        for (String flagKey : flagKeys) {
            String flagName = flagKey.replace(CorePluginImages.FLAG_PATH, "");
            imageCombo.add(CorePlugin.getDefault().getImage(flagKey), flagName);
        }
        int index = 0;
        if (flagKeys.contains(imageKey)) {
            index = flagKeys.indexOf(imageKey) + 1;
        }
        imageCombo.select(index);
        imageCombo.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                int index = imageCombo.getSelectionIndex();
                if (index == 0) {
                    imageKey = CorePluginImages.IMG_XSD;
                }
                else {
                    imageKey = flagKeys.get(index - 1);
                }
            }
        });

        applyDialogFont(composite);
        validate();

        return composite;
    }

    protected void validate() {
        if (okButton != null) {
            okButton.setEnabled(name != null && name.length() > 0 && code != null
                    && code.length() > 0);
        }
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        // / Create OK and Cancel buttons by default
        okButton = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
        createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);

        validate();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getImageKey() {
        return imageKey;
    }

    public void setImageKey(String imageKey) {
        this.imageKey = imageKey;
    }
}
