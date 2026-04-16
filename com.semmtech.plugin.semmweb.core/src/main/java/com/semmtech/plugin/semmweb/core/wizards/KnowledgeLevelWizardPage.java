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


import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import com.semmtech.plugin.semmweb.core.preferences.UserPreference;


public class KnowledgeLevelWizardPage extends WizardPage {
    private static final String PAGE_NAME = "knowlegdeLevelPage";
    private static final String PAGE_TITLE = "Knowlegde Level";
    private static final String PAGE_DESCRIPTION = "This page helps you define what semantic level you have.";

    private String knowledgeLevel;
    private Button beginnerButton;
    private Button intermediateButton;
    private Button expertButton;

    public KnowledgeLevelWizardPage() {
        super(PAGE_NAME);
        setTitle(PAGE_TITLE);
        setDescription(PAGE_DESCRIPTION);
    }

    @Override
    public void createControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NULL);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 8;
        container.setLayout(layout);

        Label label = new Label(container, SWT.WRAP);
        GridData layoutData = new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false, 2,
                1);
        layoutData.widthHint = 450;
        label.setLayoutData(layoutData);
        label.setText("Please specify the level at which the SEMMweb Editor will present you with RDF/OWL information.");

        Group levelGroup = new Group(container, SWT.NONE);
        layoutData = new GridData(GridData.FILL, GridData.BEGINNING, true, false, 2, 1);
        layoutData.verticalIndent = 3;
        levelGroup.setText("Semantic Level");
        levelGroup.setLayoutData(layoutData);
        layout = new GridLayout(1, false);
        levelGroup.setLayout(layout);

        beginnerButton = new Button(levelGroup, SWT.RADIO);
        beginnerButton.setText("Beginner");
        beginnerButton
                .setToolTipText("Select this level if you are just starting with RDF/OWL. The Editor will support try to minimize the use of RDF/OWL terminology and use terms taken form the natural language to support you.");
        beginnerButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                updateKnowledgeLevel();
                validateInput();
            }
        });
        beginnerButton.setSelection(knowledgeLevel != null
                && knowledgeLevel.equals(UserPreference.KNOWLEDGE_LEVEL_BEGINNER));

        intermediateButton = new Button(levelGroup, SWT.RADIO);
        intermediateButton.setText("Intermediate");
        intermediateButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                updateKnowledgeLevel();
                validateInput();
            }
        });
        intermediateButton.setSelection(knowledgeLevel != null
                && knowledgeLevel.equals(UserPreference.KNOWLEDGE_LEVEL_INTERMEDIATE));

        expertButton = new Button(levelGroup, SWT.RADIO);
        expertButton.setText("Expert");
        expertButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                updateKnowledgeLevel();
                validateInput();
            }
        });
        expertButton.setSelection(knowledgeLevel != null
                && knowledgeLevel.equals(UserPreference.KNOWLEDGE_LEVEL_EXPERT));

        setPageComplete(knowledgeLevel != null);
        setControl(container);
    }

    protected void updateKnowledgeLevel() {
        if (beginnerButton.getSelection()) {
            knowledgeLevel = UserPreference.KNOWLEDGE_LEVEL_BEGINNER;
        }
        else if (intermediateButton.getSelection()) {
            knowledgeLevel = UserPreference.KNOWLEDGE_LEVEL_INTERMEDIATE;
        }
        else if (expertButton.getSelection()) {
            knowledgeLevel = UserPreference.KNOWLEDGE_LEVEL_EXPERT;
        }
        else {
            knowledgeLevel = UserPreference.KNOWLEDGE_LEVEL_UNKOWN;
        }
    }

    protected void validateInput() {
        String errorMessage = null;

        if (knowledgeLevel == null) {
            errorMessage = "Please select a semantic level.";
        }
        setErrorMessage(errorMessage);
        setPageComplete(errorMessage == null);
    }

    public String getKnowledgeLevel() {
        return knowledgeLevel;
    }

    public void setKnowledgeLevel(String knowledgeLevel) {
        this.knowledgeLevel = knowledgeLevel;
        if (beginnerButton != null && !beginnerButton.isDisposed()) {
            beginnerButton.setSelection((knowledgeLevel
                    .equals(UserPreference.KNOWLEDGE_LEVEL_BEGINNER)));
        }
        if (intermediateButton != null && !intermediateButton.isDisposed()) {
            intermediateButton.setSelection((knowledgeLevel
                    .equals(UserPreference.KNOWLEDGE_LEVEL_INTERMEDIATE)));
        }
        if (expertButton != null && !expertButton.isDisposed()) {
            expertButton
                    .setSelection((knowledgeLevel.equals(UserPreference.KNOWLEDGE_LEVEL_EXPERT)));
        }
    }
}
