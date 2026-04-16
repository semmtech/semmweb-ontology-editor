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

package com.semmtech.plugin.semmweb.core.viewers;


import java.util.List;

import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

import com.google.common.collect.Lists;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Statement;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.model.IModelProvider;


/**
 * 
 * @author Sander Stolk
 */
public class StatementToolTipContent extends Composite {
    protected static final int INDENT_WIDTH = 16;
    protected static final int TOOLTIP_MARGIN_HEIGHT = 0;
    protected static final int TOOLTIP_MARGIN_WIDTH = 0;
    protected static final int TOOLTIP_MARGIN_TOP = 1;
    protected static final int TOOLTIP_MARGIN_LEFT = 3;
    protected static final int TOOLTIP_MARGIN_RIGHT = 7;
    protected static final int TOOLTIP_MARGIN_BOTTOM = 3;
    protected static final int TOOLTIP_SPACING = 0;

    protected static Font boldFont;

    protected final Statement statement;
    protected LabelProvider labelProvider;
    protected IModelProvider modelProvider;

    public StatementToolTipContent(Composite parent, Statement statement, int style) {
        this(parent, null, statement, style);
    }

    public StatementToolTipContent(Composite parent, IModelProvider modelProvider,
            Statement statement, int style) {
        super(parent, style);
        this.statement = statement;
        this.modelProvider = modelProvider;
        if (this.modelProvider == null) {
            this.modelProvider = CorePlugin.getDefault().getActiveModelProvider();
        }
        createContent();
    }

    protected void initializeBoldFont() {
        initializeBoldFont(getParent());
    }

    protected static void initializeBoldFont(Composite parent) {
        if (boldFont == null) {
            FontData fontData = parent.getFont().getFontData()[0];
            boldFont = new Font(Display.getCurrent(), new FontData(fontData.getName(),
                    fontData.getHeight(), SWT.BOLD));
        }
    }

    private void createContent() {
        if (modelProvider == null) {
            return;
        }

        labelProvider = modelProvider.getLabelProvider();
        initializeBoldFont();

        RowLayout layout = new RowLayout(SWT.VERTICAL);
        layout.marginHeight = TOOLTIP_MARGIN_HEIGHT;
        layout.marginWidth = TOOLTIP_MARGIN_WIDTH;
        layout.marginTop = TOOLTIP_MARGIN_TOP;
        layout.marginLeft = TOOLTIP_MARGIN_LEFT;
        layout.marginRight = TOOLTIP_MARGIN_RIGHT;
        layout.marginBottom = TOOLTIP_MARGIN_BOTTOM;
        layout.spacing = TOOLTIP_SPACING;
        layout.fill = true;

        setLayout(layout);

        createStatementSection(statement);
    }

    protected void createStatementSection(Statement statement) {
        createStatementSection(this, statement, modelProvider);
    }

    public static void createStatementSection(Composite parentComposite, Statement statement,
            IModelProvider modelProvider) {
        if (modelProvider == null) {
            return;
        }

        initializeBoldFont(parentComposite);

        RowLayout layout = new RowLayout(SWT.VERTICAL);
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        layout.marginBottom = 5;
        layout.spacing = 3;

        RowData data = new RowData();

        Composite composite = new Composite(parentComposite, SWT.NONE);
        composite.setLayout(layout);
        composite.setLayoutData(data);

        Label label = new Label(composite, SWT.NONE);
        label.setText("Statement in:");
        label.setFont(boldFont);

        layout = new RowLayout(SWT.VERTICAL);
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        layout.marginLeft = INDENT_WIDTH;
        layout.spacing = 1;

        composite = new Composite(composite, SWT.NONE);
        composite.setLayout(layout);
        composite.setLayoutData(new RowData());

        // Retrieve all model URIs in which this resource occurs
        List<String> containingModelURIs = Lists.newArrayList();
        if (isStatementInModel(statement, modelProvider.getBaseModel())) {
            if (modelProvider.getModelURI() != null) {
                Path basePath = new Path(modelProvider.getModelURI());
                containingModelURIs.add(basePath.lastSegment());
            }
            else {
                containingModelURIs.add("Base");
            }
        }
        List<String> subModelURIs = modelProvider.getSubModelURIs();
        for (String subModelURI : subModelURIs) {
            Model subModel = modelProvider.getSubModel(subModelURI);
            if (isStatementInModel(statement, subModel)) {
                String prefix = modelProvider.getOntModel().getNsURIPrefix(subModelURI);
                if (prefix != null) {
                    containingModelURIs.add(prefix);
                }
                else {
                    containingModelURIs.add(subModelURI);
                }
            }
        }

        // Create labels for those model uris
        for (String containingModelURI : containingModelURIs) {
            if (containingModelURI != null) {
                Label uriLabel = new Label(composite, SWT.NONE);
                uriLabel.setText(containingModelURI);
            }
        }
    }

    private static boolean isStatementInModel(Statement statement, Model model) {
        return model.contains(statement);
    }
}
