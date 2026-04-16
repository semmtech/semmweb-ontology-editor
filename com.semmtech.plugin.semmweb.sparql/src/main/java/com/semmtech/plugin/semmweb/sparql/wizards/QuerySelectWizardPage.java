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

package com.semmtech.plugin.semmweb.sparql.wizards;


import org.eclipse.swt.SWT;
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

import com.semmtech.ui.plugin.wizard.BaseWizardPage;


public class QuerySelectWizardPage extends BaseWizardPage {

    private static final String PAGE_NAME = "querySelectPage";
    private static final String PAGE_TITLE = "SELECT Settings";
    private static final String PAGE_DESCRIPTION = "This page will help you setup the SELECT query.";

    private String limitSize = "500";
    private boolean isLimit = true;
    private boolean isDistinct = false;
    private Text limitText;
    private Button limitCheckbox;
    private Button distinctCheckbox;

    class WidgetListener extends SelectionAdapter implements ModifyListener {
        @Override
        public void modifyText(ModifyEvent e) {
            limitSize = limitText.getText();
        }

        @Override
        public void widgetSelected(SelectionEvent e) {
            isLimit = limitCheckbox.getSelection();
            isDistinct = distinctCheckbox.getSelection();

            limitText.setEnabled(isLimit);
        }
    }

    protected QuerySelectWizardPage() {
        super(PAGE_NAME);
        setTitle(PAGE_TITLE);
        setDescription(PAGE_DESCRIPTION);
    }

    @Override
    public void createControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NULL);

        container.setLayout(new GridLayout(1, false));

        Label label = new Label(container, SWT.NULL);
        label.setText("Below you can specify additional settings for the SELECT query.");
        label.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));

        label = new Label(container, SWT.NONE);
        GridData layoutData = new GridData(GridData.FILL, GridData.BEGINNING, false, false, 1, 1);
        layoutData.heightHint = 3;
        label.setLayoutData(layoutData);

        WidgetListener listener = new WidgetListener();

        layoutData = new GridData(GridData.BEGINNING, GridData.CENTER, false, false);
        layoutData.horizontalIndent = 8;

        distinctCheckbox = new Button(container, SWT.CHECK);
        distinctCheckbox.setText("DISTINCT");
        distinctCheckbox.setLayoutData(layoutData);
        distinctCheckbox.setSelection(isDistinct);
        distinctCheckbox.addSelectionListener(listener);

        Composite limitComposite = new Composite(container, SWT.NONE);
        GridLayout layout = new GridLayout(2, false);
        layout.marginHeight = 0;
        layout.marginTop = 2;
        layout.marginWidth = 0;
        limitComposite.setLayout(layout);
        layoutData = new GridData(GridData.FILL, GridData.BEGINNING, false, false);
        layoutData.horizontalIndent = 8;
        limitComposite.setLayoutData(layoutData);

        limitCheckbox = new Button(limitComposite, SWT.CHECK);
        limitCheckbox.setText("LIMIT: ");
        limitCheckbox
                .setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
        limitCheckbox.setSelection(isLimit);
        limitCheckbox.addSelectionListener(listener);

        limitText = new Text(limitComposite, SWT.BORDER);
        if (limitSize != null) {
            limitText.setText(limitSize);
        }
        limitText.setEnabled(isLimit);
        limitText.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
        limitText.addModifyListener(listener);

        setControl(container);
    }

    public boolean isLimit() {
        return isLimit;
    }

    public String getLimitSize() {
        return limitSize;
    }

    public boolean isDistinct() {
        return isDistinct;
    }
}
