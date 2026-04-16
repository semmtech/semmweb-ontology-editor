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

package com.semmtech.plugin.semmweb.core.widgets;


import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.google.common.base.Preconditions;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.model.IModelProvider;


@SuppressWarnings("unused")
public class ResourceStatementWidget extends Composite {
    private Resource subject;
    private Property predicate;
    private Resource object;
    private LabelProvider labelProvider;

    private Label imageLabel;
    private Text valueText;

    public ResourceStatementWidget(Composite parent, int style) {
        super(parent, style);
        setLayout(new GridLayout(2, false));

        IModelProvider provider = CorePlugin.getDefault().getActiveModelProvider();
        Preconditions.checkNotNull(provider);
        labelProvider = provider.getLabelProvider();

        ((GridLayout) getLayout()).horizontalSpacing = 2;
        ((GridLayout) getLayout()).marginWidth = 0;
        ((GridLayout) getLayout()).marginHeight = 0;
        imageLabel = new Label(this, SWT.NONE);
        imageLabel.setLayoutData(new GridData(16, 16));
        ((GridData) imageLabel.getLayoutData()).horizontalIndent = 0;
        ((GridData) imageLabel.getLayoutData()).verticalIndent = 0;

        valueText = new Text(this, SWT.BORDER);
        valueText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));

    }

    public void setSubject(Resource subject) {
        this.subject = subject;
    }

    public void setPredicate(Property predicate) {
        this.predicate = predicate;
    }

    public void setObject(Resource object) {
        this.object = object;
        if (object != null) {
            imageLabel.setImage(labelProvider.getImage(object));
            valueText.setText(labelProvider.getText(object));
        }
        else {
            imageLabel.setImage(null);
            valueText.setText("");
        }
    }
}
