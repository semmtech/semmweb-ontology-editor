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

package com.semmtech.plugin.semmweb.core.intro;


import java.io.PrintWriter;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.ColumnLayoutData;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.intro.config.IIntroContentProvider;
import org.eclipse.ui.intro.config.IIntroContentProviderSite;

import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.CorePluginImages;


public class WelcomeContentProvider implements IIntroContentProvider {

    @Override
    public void createContent(String arg0, PrintWriter arg1) {

    }

    @Override
    public void createContent(String id, Composite parent, FormToolkit toolkit) {
        String title = "Welcome";
        String description = "Welcome to the start page of the SEMMweb Editor. "
                + "This page provides quick access to some of the editor's functionality for your convenience.";

        // Entire section
        Section outerSection = toolkit.createSection(parent, ExpandableComposite.TWISTIE
                | ExpandableComposite.TITLE_BAR | Section.DESCRIPTION);
        outerSection.descriptionVerticalSpacing = 10;
        outerSection.setText(title);
        outerSection.setDescription(description);
        outerSection.setLayoutData(new ColumnLayoutData());
        GridLayoutFactory.fillDefaults().applyTo(outerSection);

        // Content within the section, with vertical spacing of 5px
        Composite contentComposite = toolkit.createComposite(outerSection, SWT.NONE);
        contentComposite.setLayoutData(new ColumnLayoutData());
        outerSection.setClient(contentComposite);
        GridLayoutFactory.fillDefaults().numColumns(2).margins(3, 5).spacing(0, 5)
                .applyTo(contentComposite);

        // Composite imageComposite = toolkit.createComposite(contentComposite,
        // SWT.NONE);
        // GridLayoutFactory.fillDefaults().numColumns(2).extendedMargins(0, 10,
        // 0, 0).spacing(35, 0)
        // .applyTo(imageComposite);
        // GridDataFactory.fillDefaults().grab(true, false).span(2,
        // 1).applyTo(imageComposite);

        // Label label = toolkit.createLabel(imageComposite, null);
        // label.setImage(CorePlugin.getDefault()
        // .getImageDescriptor(CorePluginImages.IMG_SEMMWEB_EDITOR_TITLE).createImage());
        // GridDataFactory.fillDefaults().grab(false,
        // false).align(SWT.BEGINNING, SWT.BEGINNING)
        // .applyTo(label);

        // label = toolkit.createLabel(imageComposite, null);
        // label.setImage(CorePlugin.getDefault()
        // .getImageDescriptor(CorePluginImages.IMG_SEMMTECH_LOGO).createImage());
        // GridDataFactory.swtDefaults().hint(86, 75).align(SWT.BEGINNING,
        // SWT.BEGINNING)
        // .applyTo(label);

        Label icon = toolkit.createLabel(contentComposite, "");
        icon.setImage(CorePlugin.getDefault().getImage(CorePluginImages.IMG_HOME_NAV));
        GridDataFactory.swtDefaults().indent(SWT.DEFAULT, 10).applyTo(icon);
        Label instructions = toolkit
                .createLabel(
                        contentComposite,
                        "To reopen this page after it has been closed, click this \"Start Page\" icon in the editor's toolbar.",
                        SWT.WRAP);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).minSize(380, SWT.DEFAULT)
                .hint(380, SWT.DEFAULT).indent(10, 10).applyTo(instructions);

        outerSection.setExpanded(true);
    }

    @Override
    public void dispose() {

    }

    @Override
    public void init(IIntroContentProviderSite site) {

    }
}
