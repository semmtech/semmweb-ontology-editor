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
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.google.common.base.Strings;
import com.semmtech.ui.plugin.widgets.Widgets;


/**
 * The "New" wizard page allows setting the container for the new file as well
 * as the file name. The page will only accept file name without the extension
 * OR with the extension that matches the expected one (owl).
 */

public class OntologyNamespaceWizardPage extends WizardPage {
    private static final String PAGE_NAME = "namespacePage";
    private static final String PAGE_TITLE = "Ontology Namespace";
    private static final String PAGE_DESCRIPTION = "This wizard creates an ontology that can be shared using SEMMweb.";

    private boolean useDefault = true;

    private String namespaceUri;
    private String prefix;
    private boolean namespaceLocked;
    private Text prefixText;
    private Text namespaceUriText;
    private Button defaultCheckbox;
    private Label uriLabel;
    private Label prefixLabel;

    /**
     * Constructor for SampleNewWizardPage.
     * 
     * @param pageName
     */
    public OntologyNamespaceWizardPage() {
        super(PAGE_NAME);
        setTitle(PAGE_TITLE);
        setDescription(PAGE_DESCRIPTION);
        this.namespaceUri = "";
        this.prefix = "";
    }

    /**
     * @see IDialogPage#createControl(Composite)
     */
    @Override
    public void createControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NULL);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 8;
        container.setLayout(layout);

        Label label = new Label(container, SWT.WRAP);
        GridData layoutData = new GridData(GridData.FILL, GridData.BEGINNING, false, false, 2, 1);
        layoutData.widthHint = 500;
        label.setLayoutData(layoutData);
        label.setText("Every ontology should be created within a unique namespace. Below you can provide a namespace URI and a short prefix.");

        label = new Label(container, SWT.NONE);
        layoutData = new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false, 2, 1);
        layoutData.heightHint = 6;
        label.setLayoutData(layoutData);

        defaultCheckbox = new Button(container, SWT.CHECK);
        layoutData = new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false, 2, 1);
        defaultCheckbox.setLayoutData(layoutData);
        defaultCheckbox.setText(" Use default namespace and prefix");
        defaultCheckbox.setSelection(useDefault);
        defaultCheckbox.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                useDefault = defaultCheckbox.getSelection();
                uriLabel.setEnabled(!useDefault);
                namespaceUriText.setEnabled(!useDefault);
                prefixLabel.setEnabled(!useDefault);
                prefixText.setEnabled(!useDefault);
            }
        });

        uriLabel = new Label(container, SWT.NONE);
        uriLabel.setText("Namespace URI:");
        layoutData = new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false, 1, 1);
        layoutData.widthHint = 95;
        layoutData.verticalIndent = 3;
        uriLabel.setLayoutData(layoutData);
        uriLabel.setEnabled(!useDefault);

        namespaceUriText = new Text(container, SWT.BORDER);
        layoutData = new GridData(GridData.FILL, GridData.BEGINNING, true, false, 1, 1);
        layoutData.verticalIndent = 3;
        namespaceUriText.setLayoutData(layoutData);
        namespaceUriText.setText(Strings.nullToEmpty(namespaceUri));
        namespaceUriText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                namespaceUri = namespaceUriText.getText().toLowerCase();
                setPageComplete(validatePage());
            }
        });
        namespaceUriText.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                if (!Strings.isNullOrEmpty(namespaceUri) && !namespaceUri.endsWith("/")
                        && !namespaceUri.endsWith("#")) {
                    namespaceUri += "/";
                }
                namespaceUriText.setText(namespaceUri != null ? namespaceUri : "");
            }
        });
        namespaceUriText.setEnabled(!useDefault);
        namespaceUriText.setEditable(!namespaceLocked);

        prefixLabel = new Label(container, SWT.NONE);
        prefixLabel.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false,
                false, 1, 1));
        prefixLabel.setText("Prefix:");
        prefixLabel.setEnabled(!useDefault);

        prefixText = new Text(container, SWT.BORDER);
        layoutData = new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false, 1, 1);
        layoutData.widthHint = 50;
        prefixText.setLayoutData(layoutData);
        prefixText.setText(prefix == null ? "" : prefix);
        prefixText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                prefix = prefixText.getText().toLowerCase();
                setPageComplete(validatePage());
            }
        });
        prefixText.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                prefixText.setText(prefix != null ? prefix : "");
            }
        });
        prefixText.setEnabled(!useDefault);

        label = new Label(container, SWT.NONE);
        layoutData = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL);
        layoutData.horizontalSpan = 2;
        layoutData.heightHint = 6;
        label.setLayoutData(layoutData);

        setPageComplete(validatePage());
        setControl(container);
    }

    private boolean validatePage() {
        String errorMessage = null;
        if (Strings.isNullOrEmpty(namespaceUri)) {
            errorMessage = "Namespace cannot be empty";
        }

        setErrorMessage(errorMessage);
        return (errorMessage == null);
    }

    public void suggestPrefix(String prefix) {
        if (prefixText != null && !prefixText.isDisposed() && Strings.isNullOrEmpty(this.prefix)) {
            prefixText.setText(prefix == null ? "" : prefix);
            this.prefix = prefix;
        }
    }

    public String getPrefix() {
        return (prefix == null ? "" : prefix);
    }

    public void suggestNamespaceURI(String namespaceUri) {

        if (namespaceUriText != null && !namespaceUriText.isDisposed()
                && Strings.isNullOrEmpty(this.namespaceUri)) {
            namespaceUriText.setText(namespaceUri);
            this.namespaceUri = namespaceUri;
        }
    }

    public String getNamespaceURI() {
        return namespaceUri;
    }

    public boolean getNamespaceLocked() {
        return namespaceLocked;
    }

    public void setNamespaceLocked(boolean namespaceLocked) {
        this.namespaceLocked = namespaceLocked;
        if (!Widgets.isNullOrDisposed(namespaceUriText)) {
            namespaceUriText.setEditable(!namespaceLocked);
        }
    }

}