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


import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.semmtech.plugin.semmweb.core.model.IModelProvider;


/**
 * 
 * @author Sander Stolk
 */
public class ResourceInStatementToolTipContent extends ResourceToolTipContent {
    protected final Statement statement;
    protected Composite statementComposite;

    public ResourceInStatementToolTipContent(Composite parent, Statement statement,
            Resource resource, int style) {
        super(parent, resource, style);
        this.statement = statement;
        createContent();
    }

    public ResourceInStatementToolTipContent(Composite parent, IModelProvider modelProvider,
            Statement statement, Resource resource, int style) {
        super(parent, modelProvider, resource, style);
        this.statement = statement;
        createContent();
    }

    private void createContent() {
        if (modelProvider == null) {
            return;
        }

        createHorizontalRule();
        createStatementSection(statement);
    }

    protected void createStatementSection(Statement statement) {
        StatementToolTipContent.createStatementSection(this, statement, modelProvider);
    }

    protected void createHorizontalRule() {
        Label hr = new Label(this, SWT.SEPARATOR | SWT.SHADOW_OUT | SWT.HORIZONTAL);
        RowData data = new RowData();
        data.height = 30;
        hr.setLayoutData(data);
    }
}
