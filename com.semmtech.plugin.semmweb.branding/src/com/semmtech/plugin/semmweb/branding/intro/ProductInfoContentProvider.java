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

package com.semmtech.plugin.semmweb.branding.intro;


import java.io.PrintWriter;

import org.eclipse.core.runtime.IProduct;
import org.eclipse.core.runtime.Platform;
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
import org.osgi.framework.Bundle;
import org.osgi.framework.Version;

import com.google.common.base.Strings;


/**
 * 
 * @author Mike Henrichs
 */
public class ProductInfoContentProvider implements IIntroContentProvider {
    private String productVersion;
    private String productName;

    @Override
    public void createContent(String arg0, PrintWriter arg1) {

    }

    @Override
    public void createContent(String id, Composite parent, FormToolkit toolkit) {
        String title = "Product information";
        String description = "This section shows the information about this release of the SEMMweb Editor product.";

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

        Composite imageComposite = toolkit.createComposite(contentComposite, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(2).extendedMargins(0, 10, 0, 0).spacing(35, 0)
                .applyTo(imageComposite);
        GridDataFactory.fillDefaults().grab(true, false).span(2, 1).applyTo(imageComposite);

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

        if (!Strings.isNullOrEmpty(productName) && !Strings.isNullOrEmpty(productVersion)) {
            String text = String.format("This %s product has version %s", productName,
                    productVersion);
            Label label = toolkit.createLabel(contentComposite, text);
            GridDataFactory.fillDefaults().span(2, 1).applyTo(label);
        }

        outerSection.setExpanded(false);
    }

    @Override
    public void dispose() {

    }

    @Override
    public void init(IIntroContentProviderSite site) {
        IProduct product = Platform.getProduct();
        Bundle bundle = product.getDefiningBundle();
        Version version = bundle.getVersion();
        productVersion = String.format("%s.%s.%s.%s", version.getMajor(), version.getMinor(),
                version.getMicro(), version.getQualifier());
        productName = product.getName();
    }
}
