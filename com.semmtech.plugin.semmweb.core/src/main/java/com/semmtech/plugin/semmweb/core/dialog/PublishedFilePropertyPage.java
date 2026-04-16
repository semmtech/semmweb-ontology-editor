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


import org.eclipse.core.resources.IResource;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.PropertyPage;

import com.google.common.base.Strings;
import com.semmtech.plugin.semmweb.core.decorators.PublishedResourceFileDecorator;
import com.semmtech.plugin.semmweb.core.extensionpoint.IPublisher.VersioningMode;
import com.semmtech.plugin.semmweb.core.resources.CoreResourcePropertiesManager;
import com.semmtech.ui.plugin.widgets.GuiFactory;


public class PublishedFilePropertyPage extends PropertyPage {

    private IResource resource;
    private Text sourceLocationText;
    private Text sourceVersionText;
    private Text sourceVersioningmethodText;

    public PublishedFilePropertyPage() {
        super();
    }

    @Override
    protected Control createContents(Composite parent) {
        Composite container = GuiFactory.getInstance().createComposite(parent, 2);

        resource = (IResource) getElement().getAdapter(IResource.class);

        Label label = new Label(container, SWT.NONE);
        label.setText(String.format("Details on semantic resource \"%s\".", resource.getName()));
        GridData layoutData = new GridData(GridData.FILL, GridData.BEGINNING, true, false, 2, 1);
        label.setLayoutData(layoutData);

        label = new Label(container, SWT.NONE);
        layoutData = new GridData(GridData.FILL, GridData.BEGINNING, true, false, 2, 1);
        layoutData.heightHint = 2;
        label.setLayoutData(layoutData);

        label = new Label(container, SWT.NONE);
        label.setText("Source location:");
        layoutData = new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 1, 1);
        layoutData.widthHint = 115;
        label.setLayoutData(layoutData);

        sourceLocationText = new Text(container, SWT.BORDER);
        layoutData = new GridData(GridData.FILL, GridData.BEGINNING, true, false, 1, 1);
        sourceLocationText.setLayoutData(layoutData);
        String source = CoreResourcePropertiesManager.getSourceLocation(resource);
        sourceLocationText.setText(Strings.nullToEmpty(source));
        sourceLocationText.setEditable(false);

        label = new Label(container, SWT.NONE);
        label.setText("Source version:");
        layoutData = new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 1, 1);
        layoutData.widthHint = 115;
        label.setLayoutData(layoutData);

        String version = CoreResourcePropertiesManager.getSourceVersion(resource);
        sourceVersionText = new Text(container, SWT.BORDER);
        layoutData = new GridData(GridData.FILL, GridData.BEGINNING, true, false, 1, 1);
        sourceVersionText.setLayoutData(layoutData);
        sourceVersionText.setText(Strings.nullToEmpty(version));
        sourceVersionText.setEditable(false);

        VersioningMode vm = CoreResourcePropertiesManager.getSourceVersioningmethod(resource);
        if (vm != null) {
            label = new Label(container, SWT.NONE);
            label.setText("Versioning:");
            layoutData = new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 1, 1);
            layoutData.widthHint = 115;
            label.setLayoutData(layoutData);

            sourceVersioningmethodText = new Text(container, SWT.BORDER);
            layoutData = new GridData(GridData.FILL, GridData.BEGINNING, true, false, 1, 1);
            sourceVersioningmethodText.setLayoutData(layoutData);
            sourceVersioningmethodText.setText(Strings.nullToEmpty(vm.name));
            sourceVersioningmethodText.setEditable(false);
        }

        return container;
    }

    @Override
    public boolean performOk() {
        PublishedResourceFileDecorator decorator = PublishedResourceFileDecorator
                .getFileDecorator();

        if (decorator != null) {
            decorator.refresh();
        }

        return true;
    }
}
