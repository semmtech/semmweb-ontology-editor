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


import java.util.Collections;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.semmtech.plugin.semmweb.core.model.Creator;
import com.semmtech.plugin.semmweb.core.model.Organisation;
import com.semmtech.plugin.semmweb.core.model.Person;
import com.semmtech.plugin.semmweb.core.preferences.CreatorPreference;
import com.semmtech.ui.plugin.dialog.AbstractMessageInputDialog;


public class CreatorInputDialog extends AbstractMessageInputDialog {

    private List<Person> persons;
    private List<Organisation> organisations;
    private Combo personCombo;
    private Button personCheckbox;
    private Button organisationCheckbox;
    private Combo organisationCombo;
    private Text lastNameText;
    private Text firstNameText;
    private Text emailText;
    private Text organisationNameText;
    private Text acronymText;

    private boolean hasPerson;
    private boolean hasOrganisation;
    private String lastName;
    private String firstName;
    private String email;
    private String organisationName;
    private String acronym;

    public CreatorInputDialog(Shell parentShell, String title, String message) {
        super(parentShell, title, message);
        this.showErrorMessage = true;
        initialize();
    }

    private void initialize() {
        persons = Lists.newArrayList();
        organisations = Lists.newArrayList();
        List<Creator> creators = CreatorPreference.getCreators();
        for (Creator creator : creators) {
            if (creator.getPerson() != null) {
                Person person = creator.getPerson();
                if (!persons.contains(person))
                    persons.add(person);
            }
            if (creator.getOrganisation() != null) {
                Organisation organisation = creator.getOrganisation();
                if (!organisations.contains(organisation))
                    organisations.add(organisation);
            }
        }
        Collections.sort(persons);
        Collections.sort(organisations);
    }

    @Override
    protected Control createInputArea(Composite parent) {
        Composite composite = (Composite) super.createInputArea(parent);

        GridLayout layout = new GridLayout(2, false);
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        composite.setLayout(layout);

        Composite personComposite = new Composite(composite, SWT.NONE);
        layout = new GridLayout(2, false);
        layout.marginWidth = 0;
        layout.marginHeight = 0;

        GridData layoutData = new GridData(GridData.FILL, GridData.FILL, true, false, 2, 1);
        layoutData.verticalIndent = 8;
        personComposite.setLayout(layout);
        personComposite.setLayoutData(layoutData);

        personCheckbox = new Button(personComposite, SWT.CHECK);
        personCheckbox.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false,
                false, 1, 1));
        personCheckbox.setText("Person:");
        personCheckbox.setSelection(false);
        personCheckbox.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                hasPerson = personCheckbox.getSelection();
                personCombo.setEnabled(hasPerson);
                lastNameText.setEnabled(hasPerson);
                firstNameText.setEnabled(hasPerson);
                emailText.setEnabled(hasPerson);

                if (!hasPerson)
                    personCombo.deselectAll();
                else
                    personCombo.select(0);
                personSelectionChanged();
                validateInput();
            }
        });

        personCombo = new Combo(personComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
        personCombo.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 1));
        personCombo.setEnabled(false);

        String[] items = new String[persons.size() + 1];
        items[0] = "[New]";
        int i = 1;
        for (Person person : persons) {
            items[i++] = String.format("%s %s (%s)", person.getFirstName(), person.getLastName(),
                    person.getEmail());
        }

        personCombo.setItems(items);
        personCombo.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                personSelectionChanged();
            }
        });

        GridData labelData = new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 1, 1);
        labelData.horizontalIndent = 16;
        labelData.widthHint = 80;

        Label label = new Label(composite, SWT.NONE);
        label.setText("Surname:");
        label.setLayoutData(labelData);

        lastNameText = new Text(composite, SWT.BORDER);
        lastNameText.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 1));
        lastNameText.setEnabled(false);
        lastNameText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                lastName = lastNameText.getText();
                validateInput();
            }
        });

        label = new Label(composite, SWT.NONE);
        label.setText("First Name:");
        label.setLayoutData(labelData);

        firstNameText = new Text(composite, SWT.BORDER);
        firstNameText
                .setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 1));
        firstNameText.setEnabled(false);
        firstNameText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                firstName = firstNameText.getText();
                validateInput();
            }
        });

        label = new Label(composite, SWT.NONE);
        label.setText("Email:");
        label.setLayoutData(labelData);

        emailText = new Text(composite, SWT.BORDER);
        emailText.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 1));
        emailText.setEnabled(false);
        emailText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                email = emailText.getText();
                validateInput();
            }
        });

        Composite organisationComposite = new Composite(composite, SWT.NONE);
        layout = new GridLayout(2, false);
        layoutData = new GridData(GridData.FILL, GridData.FILL, true, false, 2, 1);
        layoutData.verticalIndent = 10;
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        organisationComposite.setLayout(layout);
        organisationComposite.setLayoutData(layoutData);

        organisationCheckbox = new Button(organisationComposite, SWT.CHECK);
        organisationCheckbox.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false,
                false, 1, 1));
        organisationCheckbox.setText("Organisation:");
        organisationCheckbox.setSelection(false);
        organisationCheckbox.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                hasOrganisation = organisationCheckbox.getSelection();
                organisationCombo.setEnabled(hasOrganisation);
                organisationNameText.setEnabled(hasOrganisation);
                acronymText.setEnabled(hasOrganisation);

                if (!organisationCheckbox.getSelection())
                    organisationCombo.deselectAll();
                else
                    organisationCombo.select(0);
                organisationSelectionChanged();
                validateInput();
            }
        });

        organisationCombo = new Combo(organisationComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
        organisationCombo.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false,
                1, 1));
        organisationCombo.setEnabled(false);

        items = new String[organisations.size() + 1];
        items[0] = "[New]";
        i = 1;
        for (Organisation organisation : organisations) {
            items[i++] = String
                    .format("%s (%s)", organisation.getName(), organisation.getAcronym());
        }

        organisationCombo.setItems(items);
        organisationCombo.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                organisationSelectionChanged();
            }
        });

        label = new Label(composite, SWT.NONE);
        label.setText("Name:");
        label.setLayoutData(labelData);

        organisationNameText = new Text(composite, SWT.BORDER);
        organisationNameText.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true,
                false, 1, 1));
        organisationNameText.setEnabled(false);
        organisationNameText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                organisationName = organisationNameText.getText();
                validateInput();
            }
        });

        label = new Label(composite, SWT.NONE);
        label.setText("Acronym:");
        label.setLayoutData(labelData);

        acronymText = new Text(composite, SWT.BORDER);
        acronymText.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 1));
        acronymText.setEnabled(false);
        acronymText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                acronym = acronymText.getText();
                validateInput();
            }
        });

        applyDialogFont(composite);

        return composite;
    }

    private void personSelectionChanged() {
        int index = personCombo.getSelectionIndex();
        if (index < 0 || index == 0) {
            lastNameText.setText("");
            firstNameText.setText("");
            emailText.setText("");
        }
        else {
            Person person = persons.get(index - 1);
            lastNameText.setText(person.getLastName());
            firstNameText.setText(person.getFirstName());
            emailText.setText(person.getEmail());
        }
    }

    private void organisationSelectionChanged() {
        int index = organisationCombo.getSelectionIndex();
        if (index < 0 || index == 0) {
            organisationNameText.setText("");
            acronymText.setText("");
        }
        else {
            Organisation selected = organisations.get(index - 1);
            organisationNameText.setText(selected.getName());
            acronymText.setText(selected.getAcronym());
        }
    }

    @Override
    protected void validateInput() {
        String errorMessage = null;
        if (!hasOrganisation && !hasPerson)
            errorMessage = "Creator shuold at least contain a person or organisation";
        if (hasOrganisation) {
            if (Strings.isNullOrEmpty(organisationName))
                errorMessage = "Organisation Name cannot be empty";
            else if (Strings.isNullOrEmpty(acronym))
                errorMessage = "Organisation Acronym cannot be empty";
            else if (acronym.contains(" "))
                errorMessage = "Organisation Acronym cannot contains spaces";
        }
        if (hasPerson) {
            if (Strings.isNullOrEmpty(lastName))
                errorMessage = "Surname cannot be empty";
            else if (Strings.isNullOrEmpty(firstName))
                errorMessage = "First Name cannot be empty";
            else if (Strings.isNullOrEmpty(email))
                errorMessage = "Email cannot be empty";
        }
        setErrorMessage(errorMessage);
    }

    public Creator getCreator() {
        Person person = null;
        Organisation organisation = null;
        if (hasPerson) {
            person = new Person();
            person.setLastName(lastName);
            person.setFirstName(firstName);
            person.setEmail(email);
        }
        if (hasOrganisation) {
            organisation = new Organisation();
            organisation.setName(organisationName);
            organisation.setAcronym(acronym);
        }
        if (person != null || organisation != null)
            return new Creator(person, organisation);
        return null;
    }
}
