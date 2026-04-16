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


import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.window.Window;
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
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.model.IWorkbenchAdapter;

import com.google.common.base.Strings;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.CorePluginImages;
import com.semmtech.plugin.semmweb.core.dialog.CreatorInputDialog;
import com.semmtech.plugin.semmweb.core.model.Creator;
import com.semmtech.plugin.semmweb.core.preferences.CreatorPreference;
import com.semmtech.plugin.semmweb.core.preferences.UserPreference;
import com.semmtech.plugin.semmweb.core.util.ResourcesUtil;
import com.semmtech.ui.plugin.widgets.Widgets;


/**
 * The "New" wizard page allows setting the container for the new file as well
 * as the file name. The page will only accept file name without the extension
 * OR with the extension that matches the expected one (owl).
 */

public class OntologyAnnotationWizardPage extends WizardPage {
    private static final String PAGE_NAME = "annotationPage";
    private static final String PAGE_TITLE = "Define Ontology";
    private static final String PAGE_DESCRIPTION = "This wizard creates an ontology that can be shared online.";

    private static boolean addMetaInformation = false;

    private static Creator previousCreator;
    private List<Creator> creators;
    private Combo creatorCombo;

    private String name = "";
    private String ontologyDescription = "";
    private String acronym = "";
    private Creator creator;
    private boolean publishOnline;
    private Date datetime = new Date();

    private Text nameText;
    private Text descriptionText;
    private Text acronymText;
    private DateTime dateControl;
    private DateTime timeControl;

    private final Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
    private Button publishCheckbox;
    private Button addMetaCheckbox;
    private Composite metaComposite;
    private Composite container;

    /**
     * Constructor for SampleNewWizardPage.
     * 
     * @param pageName
     */
    public OntologyAnnotationWizardPage() {
        super(PAGE_NAME);
        setTitle(PAGE_TITLE);
        setDescription(PAGE_DESCRIPTION);
    }

    /**
     * @see IDialogPage#createControl(Composite)
     */
    @SuppressWarnings("unused")
    @Override
    public void createControl(Composite parent) {
        container = new Composite(parent, SWT.NULL);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 8;
        container.setLayout(layout);

        Label label = new Label(container, SWT.WRAP);
        GridData layoutData = new GridData(GridData.FILL, GridData.BEGINNING, false, false, 2, 1);
        layoutData.widthHint = 500;
        label.setLayoutData(layoutData);
        label.setText("Please describe the new ontology using the fields below. This information will be used if the ontology is published to a Linked Data Platform.");

        label = new Label(container, SWT.NONE);
        layoutData = new GridData(GridData.FILL, GridData.BEGINNING, false, false, 2, 1);
        layoutData.heightHint = 6;
        label.setLayoutData(layoutData);

        label = new Label(container, SWT.NONE);
        layoutData = new GridData(GridData.FILL, GridData.BEGINNING, false, false, 1, 1);
        layoutData.widthHint = 90;
        label.setLayoutData(layoutData);
        label.setText("Name:");

        nameText = new Text(container, SWT.BORDER);
        nameText.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 1, 1));
        nameText.setText(name == null ? "" : name);
        nameText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                name = nameText.getText();
                setPageComplete(validatePage());
            }
        });

        label = new Label(container, SWT.NONE);
        label.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false, 1, 1));
        label.setText("Acronym:");

        acronymText = new Text(container, SWT.BORDER);
        layoutData = new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false, 1, 1);
        layoutData.widthHint = 50;
        acronymText.setText(acronym == null ? "" : acronym);
        acronymText.setLayoutData(layoutData);
        acronymText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                acronym = acronymText.getText().toLowerCase();
                setPageComplete(validatePage());
            }
        });
        acronymText.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                acronymText.setText(acronym != null ? acronym : "");
            }
        });

        label = new Label(container, SWT.NONE);
        label.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false, 1, 1));
        label.setText("Description:");

        descriptionText = new Text(container, SWT.BORDER | SWT.V_SCROLL | SWT.WRAP);
        layoutData = new GridData(GridData.FILL, GridData.FILL, false, false, 1, 1);
        layoutData.heightHint = 60;
        descriptionText.setText(ontologyDescription == null ? "" : ontologyDescription);
        descriptionText.setLayoutData(layoutData);
        descriptionText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                ontologyDescription = descriptionText.getText();
                setPageComplete(validatePage());
            }
        });

        new Label(container, SWT.NONE);

        publishCheckbox = new Button(container, SWT.CHECK);
        layoutData = new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false, 1, 1);
        layoutData.verticalIndent = 3;
        publishCheckbox.setLayoutData(layoutData);
        publishCheckbox.setText("Publish to an online platform for Linked Data");
        publishCheckbox.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                publishOnline = publishCheckbox.getSelection();
                if (publishOnline && !addMetaInformation) {
                    addMetaCheckbox.setSelection(publishOnline);
                    addMetaInformation = true;
                    createMetaControls();
                }
                addMetaCheckbox.setEnabled(!publishOnline);
                setPageComplete(validatePage());
            }
        });

        new Label(container, SWT.NONE);

        addMetaCheckbox = new Button(container, SWT.CHECK);
        layoutData = new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false, 1, 1);
        layoutData.verticalIndent = 3;
        addMetaCheckbox.setLayoutData(layoutData);
        addMetaCheckbox.setText("Add metadata to ontology");
        addMetaCheckbox.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                addMetaInformation = addMetaCheckbox.getSelection();
                if (addMetaInformation) {
                    createMetaControls();
                }
                else {
                    clearMetaControls();
                }
                setPageComplete(validatePage());
            }
        });
        addMetaCheckbox.setSelection(addMetaInformation);

        if (addMetaInformation) {
            createMetaControls();
        }
        else {
            createFiller();
        }

        setPageComplete(validatePage());
        setControl(container);
    }

    private void clearMetaControls() {
        if (!Widgets.isNullOrDisposed(metaComposite)) {
            metaComposite.dispose();
        }
        if (!Widgets.isNullOrDisposed(container)) {
            container.layout(true, true);
        }
    }

    /**
     * This setting if the metaComposite is required for the wizard page to have
     * enough space for the other controls.
     */
    private void createFiller() {
        metaComposite = new Composite(container, SWT.NONE);
        GridData layoutData = new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1);
        GridLayout layout = new GridLayout(1, false);
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        metaComposite.setLayout(layout);
        metaComposite.setLayoutData(layoutData);

        Label label = new Label(metaComposite, SWT.WRAP);
        layoutData = new GridData(1, 125);
        label.setLayoutData(layoutData);

    }

    private void createMetaControls() {
        if (!Widgets.isNullOrDisposed(metaComposite)) {
            metaComposite.dispose();
        }

        metaComposite = new Composite(container, SWT.NONE);
        GridData layoutData = new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1);
        GridLayout layout = new GridLayout(2, false);
        layout.marginHeight = 0;
        layout.marginTop = 5;
        layout.marginWidth = 0;
        layout.horizontalSpacing = 8;
        metaComposite.setLayout(layout);
        metaComposite.setLayoutData(layoutData);

        Label label = new Label(metaComposite, SWT.WRAP);
        layoutData = new GridData(GridData.FILL, GridData.BEGINNING, false, false, 2, 1);
        layoutData.widthHint = 500;
        layoutData.verticalIndent = 7;
        label.setLayoutData(layoutData);
        label.setText("Provide additional creator, creation date and type of language specification information.");

        label = new Label(metaComposite, SWT.NONE);
        layoutData = new GridData(GridData.FILL, GridData.BEGINNING, false, false, 2, 1);
        layoutData.heightHint = 3;
        label.setLayoutData(layoutData);

        label = new Label(metaComposite, SWT.NONE);
        layoutData = new GridData(GridData.FILL, GridData.CENTER, false, false, 1, 1);
        layoutData.widthHint = 90;
        label.setText("Creator:");
        label.setLayoutData(layoutData);

        Composite creatorComposite = new Composite(metaComposite, SWT.NONE);
        layout = new GridLayout(2, false);
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        layout.horizontalSpacing = 5;
        creatorComposite.setLayout(layout);
        creatorComposite
                .setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 1, 1));

        creatorCombo = new Combo(creatorComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
        layoutData = new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 1);
        creatorCombo.setLayoutData(layoutData);

        creators = CreatorPreference.getCreators();
        Collections.sort(creators);

        creatorCombo.setItems(createCreatorItems(creators));
        creatorCombo.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                int index = creatorCombo.getSelectionIndex();
                if (index == 0) {
                    creator = null;
                }
                else {
                    creator = creators.get(index - 1);
                }
                setPageComplete(validatePage());
            }
        });

        if (previousCreator == null && UserPreference.getPerson() != null) {
            for (Creator creator : creators) {
                if (!creator.hasPerson() || creator.hasOrganisation()) {
                    continue;
                }
                if (creator.getPerson().equals(UserPreference.getPerson())) {
                    previousCreator = creator;
                    break;
                }
            }
        }
        if (previousCreator != null) {
            creatorCombo.select(creators.indexOf(previousCreator) + 1);
            creator = previousCreator;
        }

        Button newCreatorButton = new Button(creatorComposite, SWT.PUSH);
        layoutData = new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 1, 1);
        layoutData.widthHint = 55;
        newCreatorButton.setLayoutData(layoutData);
        newCreatorButton.setText("New...");
        newCreatorButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                CreatorInputDialog dialog = new CreatorInputDialog(getShell(), "New Creator",
                        "Please select or enter the person and/or organisation.");
                if (dialog.open() == Window.OK) {
                    creator = dialog.getCreator();
                    if (!creators.contains(creator)) {
                        creators.add(creator);
                        Collections.sort(creators);
                        CreatorPreference.setCreators(creators);
                        creatorCombo.setItems(createCreatorItems(creators));
                    }
                    int index = creators.indexOf(creator);
                    creatorCombo.select(index + 1);
                }
            }
        });

        label = new Label(metaComposite, SWT.NONE);
        label.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 1, 1));
        label.setText("Date:");

        layout = new GridLayout(3, false);
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        layout.horizontalSpacing = 4;
        layout.verticalSpacing = 0;

        Composite dateComposite = new Composite(metaComposite, SWT.NONE);
        dateComposite.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, true,
                false, 1, 1));
        dateComposite.setLayout(layout);

        if (datetime == null) {
            datetime = new Date();
        }

        calendar.setTime(datetime);

        dateControl = new DateTime(dateComposite, SWT.DROP_DOWN | SWT.LONG);
        dateControl.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, true, false,
                1, 1));
        dateControl.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                calendar.set(dateControl.getYear(), dateControl.getMonth(), dateControl.getDay(),
                        timeControl.getHours(), timeControl.getMinutes(), timeControl.getSeconds());
                datetime = calendar.getTime();
            }
        });
        dateControl.setDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

        timeControl = new DateTime(dateComposite, SWT.TIME | SWT.LONG);
        timeControl.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, true, false,
                1, 1));
        timeControl.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                calendar.set(dateControl.getYear(), dateControl.getMonth(), dateControl.getDay(),
                        timeControl.getHours(), timeControl.getMinutes(), timeControl.getSeconds());
                datetime = calendar.getTime();
            }
        });
        timeControl.setTime(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE),
                calendar.get(Calendar.SECOND));

        Button calendar = new Button(dateComposite, SWT.PUSH);
        calendar.setImage(CorePlugin.getDefault().getImage(CorePluginImages.IMG_DATETIME));
        layoutData = new GridData(22, 23);
        calendar.setLayoutData(layoutData);
        calendar.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
                dateControl.setDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH));
                timeControl.setTime(calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));
            }
        });

        if (!Widgets.isNullOrDisposed(container)) {
            container.layout(true, true);
        }
    }

    private boolean validatePage() {
        String errorMessage = null;
        if (publishOnline && Strings.isNullOrEmpty(name)) {
            errorMessage = "Name is required for publication";
        }
        else if (publishOnline && Strings.isNullOrEmpty(ontologyDescription)) {
            errorMessage = "Description is required for publication";
        }
        else if (publishOnline && Strings.isNullOrEmpty(acronym)) {
            errorMessage = "Acronym is required for publication";
        }
        else if (publishOnline && !acronym.matches("[a-zA-Z][a-zA-Z0-9]*")) {
            errorMessage = "To publish, acronym must start with a letter and only contain letters and digits!";
        }
        else if (publishOnline && creator == null) {
            errorMessage = "Creator is required for publication";
        }
        if (!ResourcesUtil.existsSemanticProjects()) {
            setErrorMessage("Create a Project before creating a new Semantic File");
            return false;

        }
        setErrorMessage(errorMessage);
        return (errorMessage == null);
    }

    private String[] createCreatorItems(List<Creator> creators) {
        String[] items = new String[creators.size() + 1];
        items[0] = "";
        int i = 1;
        for (Creator creator : creators) {
            IWorkbenchAdapter adapter = (IWorkbenchAdapter) creator
                    .getAdapter(IWorkbenchAdapter.class);
            items[i++] = adapter.getLabel(creator);
        }
        return items;
    }

    @Override
    public String getName() {
        return (name == null ? "" : name);
    }

    @Override
    public String getDescription() {
        return (ontologyDescription == null ? "" : ontologyDescription);
    }

    public boolean getAddMetaInformation() {
        return addMetaInformation;
    }

    public String getAcronym() {
        return (acronym == null ? "" : acronym);
    }

    public boolean getPublishOnline() {
        return publishOnline;
    }

    public Date getDateTime() {
        return datetime;
    }

    public Creator getCreator() {
        return creator;
    }
}