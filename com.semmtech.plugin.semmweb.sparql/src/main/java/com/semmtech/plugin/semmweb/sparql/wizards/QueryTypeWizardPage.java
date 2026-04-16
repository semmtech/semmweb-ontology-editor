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
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.semmtech.plugin.semmweb.sparql.SparqlQueryType;
import com.semmtech.ui.plugin.wizard.BaseWizardPage;


public class QueryTypeWizardPage extends BaseWizardPage {

    private static final String PAGE_NAME = "queryTypePage";
    private static final String PAGE_TITLE = "Query Type";
    private static final String PAGE_DESCRIPTION = "This page will help you select the type of the SPARQL query.";
    private Button askRadio;
    private Button selectRadio;
    private Button describeRadio;
    private Button constructRadio;

    private SparqlQueryType selectedType = SparqlQueryType.SELECT;

    protected QueryTypeWizardPage() {
        super(PAGE_NAME);
        setTitle(PAGE_TITLE);
        setDescription(PAGE_DESCRIPTION);

    }

    @Override
    public void createControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NULL);

        container.setLayout(new GridLayout(1, false));

        Label label = new Label(container, SWT.NULL);
        label.setText("Please select the type of SPARQL query would like to create.");
        label.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));

        label = new Label(container, SWT.NONE);
        GridData layoutData = new GridData(GridData.FILL, GridData.BEGINNING, false, false, 1, 1);
        layoutData.heightHint = 3;
        label.setLayoutData(layoutData);

        SelectionAdapter radioListener = new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (askRadio.getSelection()) {
                    selectedType = SparqlQueryType.ASK;
                }
                else if (selectRadio.getSelection()) {
                    selectedType = SparqlQueryType.SELECT;
                }
                else if (describeRadio.getSelection()) {
                    selectedType = SparqlQueryType.DESCRIBE;
                }
                else if (constructRadio.getSelection()) {
                    selectedType = SparqlQueryType.CONSTRUCT;
                }
                else {
                    selectedType = SparqlQueryType.SELECT;
                }
            }
        };

        layoutData = new GridData(GridData.BEGINNING, GridData.CENTER, false, false);
        layoutData.horizontalIndent = 8;

        askRadio = new Button(container, SWT.RADIO);
        askRadio.setText("ASK");
        askRadio.setSelection(selectedType == SparqlQueryType.ASK);
        askRadio.addSelectionListener(radioListener);
        askRadio.setLayoutData(layoutData);

        selectRadio = new Button(container, SWT.RADIO);
        selectRadio.setText("SELECT");
        selectRadio.setSelection(selectedType == SparqlQueryType.SELECT);
        selectRadio.addSelectionListener(radioListener);
        selectRadio.setLayoutData(layoutData);

        describeRadio = new Button(container, SWT.RADIO);
        describeRadio.setText("DESCRIBE");
        describeRadio.setSelection(selectedType == SparqlQueryType.DESCRIBE);
        describeRadio.addSelectionListener(radioListener);
        describeRadio.setLayoutData(layoutData);

        constructRadio = new Button(container, SWT.RADIO);
        constructRadio.setText("CONSTRUCT");
        constructRadio.setSelection(selectedType == SparqlQueryType.CONSTRUCT);
        constructRadio.addSelectionListener(radioListener);
        constructRadio.setLayoutData(layoutData);

        setControl(container);
    }

    public void setSelectedType(SparqlQueryType selected) {
        this.selectedType = selected;
        switch (selected) {
        case ASK:
            askRadio.setSelection(true);
            break;
        case SELECT:
            selectRadio.setSelection(true);
            break;
        case DESCRIBE:
            describeRadio.setSelection(true);
            break;
        case CONSTRUCT:
            constructRadio.setSelection(true);
            break;
        default:
            selectRadio.setSelection(true);
            break;
        }
    }

    public SparqlQueryType getSelectedType() {
        return selectedType;
    }
}
