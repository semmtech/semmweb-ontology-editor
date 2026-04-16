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


import java.util.List;

import org.eclipse.jface.dialogs.IPageChangeProvider;
import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import com.google.common.base.Strings;
import com.semmtech.plugin.semmweb.core.model.Creator;
import com.semmtech.plugin.semmweb.core.model.Person;
import com.semmtech.plugin.semmweb.core.preferences.CreatorPreference;
import com.semmtech.plugin.semmweb.core.preferences.UserPreference;


public class InitializeEditorWizard extends SemmtechWizard implements INewWizard,
        IPageChangedListener {
    public static final String ID = "com.semmtech.plugin.semmweb.wizards.initializeEditor";
    private static final String WINDOW_TITLE = "Initialize Editor";

    private InitializeEditorWizardPage initializePage;

    public InitializeEditorWizard() {
        super();
        setWindowTitle(WINDOW_TITLE);
        setNeedsProgressMonitor(true);
    }

    public void addPages() {
        setShellImage();

        initializePage = new InitializeEditorWizardPage();

        addPage(initializePage);

        Person person = UserPreference.getPerson();
        if (person != null) {
            initializePage.setLastName(person.getLastName());
            initializePage.setFirstName(person.getFirstName());
            initializePage.setEmail(person.getEmail());
        }

        ((IPageChangeProvider) getContainer()).addPageChangedListener(this);
    }

    @Override
    public void pageChanged(PageChangedEvent event) {

    }

    @Override
    public boolean performCancel() {
        return super.performCancel();
    }

    @Override
    public boolean performFinish() {
        Person person = new Person();
        person.setLastName(initializePage.getLastName());
        person.setFirstName(initializePage.getFirstName());
        person.setEmail(initializePage.getEmail());

        UserPreference.setPerson(person);

        if (!Strings.isNullOrEmpty(person.getLastName())
                && !Strings.isNullOrEmpty(person.getFirstName())
                && !Strings.isNullOrEmpty(person.getEmail())) {

            List<Creator> creators = CreatorPreference.getCreators();
            boolean exists = false;
            for (Creator creator : creators) {
                if (!creator.hasPerson())
                    continue;
                exists = creator.getPerson().equals(person);
                if (exists)
                    break;
            }
            if (!exists)
                creators.add(new Creator(person));
            CreatorPreference.setCreators(creators);
        }

        return true;
    }

    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {

    }
}