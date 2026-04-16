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

package com.semmtech.plugin.semmweb.core.wizards;


import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;


/**
 * The "New" wizard page allows setting the container for the new file as well
 * as the file name. The page will only accept file name without the extension
 * OR with the extension that matches the expected one (owl).
 */

public class InitializeEditorWizardPage extends WizardPage {
    private static final String PAGE_NAME = "initializePage";
    private static final String PAGE_TITLE = "Initialize Editor";
    private static final String PAGE_DESCRIPTION = "This wizard helps you configure this Editor to your wishes.";

    private String lastName;
    private String firstName;
    private String email;
    private Text lastnameText;
    private Text firstnameText;
    private Text emailText;

    public InitializeEditorWizardPage() {
        super(PAGE_NAME);
        setTitle(PAGE_TITLE);
        setDescription(PAGE_DESCRIPTION);
    }

    /**
     * @see IDialogPage#createControl(Composite)
     */
    public void createControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NULL);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 8;
        container.setLayout(layout);

        Label label = new Label(container, SWT.WRAP);
        GridData layoutData = new GridData(GridData.FILL, GridData.BEGINNING, false, false, 2, 1);
        layoutData.widthHint = 450;
        label.setLayoutData(layoutData);
        label.setText("In order to annotate created ontologies, please provide your personal information below.");

        label = new Label(container, SWT.NONE);
        layoutData = new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false, 2, 1);
        layoutData.heightHint = 2;
        label.setLayoutData(layoutData);

        label = new Label(container, SWT.NONE);
        label.setText("Surname:");
        layoutData = new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 1, 1);
        layoutData.widthHint = 90;
        label.setLayoutData(layoutData);

        lastnameText = new Text(container, SWT.BORDER);
        lastnameText.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 1));
        lastnameText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                lastName = lastnameText.getText();
                validateInput();
            }
        });
        lastnameText.setText((lastName == null) ? "" : lastName);

        label = new Label(container, SWT.NONE);
        label.setText("First name:");
        layoutData = new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 1, 1);
        label.setLayoutData(layoutData);

        firstnameText = new Text(container, SWT.BORDER);
        firstnameText
                .setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 1));
        firstnameText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                firstName = firstnameText.getText();
                validateInput();
            }
        });
        firstnameText.setText((firstName == null) ? "" : firstName);

        label = new Label(container, SWT.NONE);
        label.setText("Email:");
        layoutData = new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 1, 1);
        label.setLayoutData(layoutData);

        emailText = new Text(container, SWT.BORDER);
        emailText.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 1));
        emailText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                email = emailText.getText();
                validateInput();
            }
        });
        emailText.setText((email == null) ? "" : email);

        setPageComplete(true);
        setControl(container);
    }

    @SuppressWarnings("null")
    protected void validateInput() {
        String errorMessage = null;

        setErrorMessage(errorMessage);
        setPageComplete(errorMessage == null);
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
        if (lastnameText != null && !lastnameText.isDisposed())
            lastnameText.setText(lastName);
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
        if (firstnameText != null && !firstnameText.isDisposed())
            firstnameText.setText(firstName);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
        if (emailText != null && !emailText.isDisposed())
            emailText.setText(email);
    }
}