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
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.hp.hpl.jena.rdf.model.RDFNode;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.model.IModelProvider;
import com.semmtech.plugin.semmweb.core.viewers.ModelNodeLabelProvider;

import junit.framework.Assert;


public class RDFNodeLabel extends Composite {
    private LabelProvider labelProvider = null;
    private RDFNode node = null;
    private final Label imageLabel;
    private final Label textLabel;

    /**
     * This widget displays the icon and text for a Resource. Image and text is
     * retrieved using the ResourceLabelProvider class.
     * 
     * @param parent
     * @param style
     */
    public RDFNodeLabel(Composite parent, int style) {
        super(parent, style);
        setLayout(new GridLayout(2, false));

        ((GridLayout) getLayout()).horizontalSpacing = 2;
        ((GridLayout) getLayout()).marginWidth = 0;
        ((GridLayout) getLayout()).marginHeight = 0;
        imageLabel = new Label(this, SWT.NONE);
        imageLabel.setLayoutData(new GridData(16, 16));
        ((GridData) imageLabel.getLayoutData()).horizontalIndent = 0;
        ((GridData) imageLabel.getLayoutData()).verticalIndent = 0;

        textLabel = new Label(this, SWT.NONE);
        textLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
    }

    public void setLabelProvider(LabelProvider provider) {
        this.labelProvider = provider;
    }

    public void setRDFNode(RDFNode node) {
        this.node = node;
        if (node != null) {
            if (labelProvider == null) {
                IModelProvider provider = CorePlugin.getDefault().getActiveModelProvider();
                Assert.assertTrue(provider != null);
                if (provider != null) {
                    labelProvider = provider.getLabelProvider();
                }
            }
            else if (labelProvider instanceof ModelNodeLabelProvider) {
                ((ModelNodeLabelProvider) labelProvider).refresh();
            }
            imageLabel.setImage(labelProvider.getImage(node));
            textLabel.setText(labelProvider.getText(node));
        }
        else {
            imageLabel.setImage(null);
            textLabel.setText("");
        }
    }

    public RDFNode getRDFNode() {
        return node;
    }

    public Image getImage() {
        return imageLabel.getImage();
    }

    public String getText() {
        return textLabel.getText();
    }
}
