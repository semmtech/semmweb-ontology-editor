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


import org.eclipse.jface.window.DefaultToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.semmtech.plugin.semmweb.core.model.IModelProvider;


/**
 * 
 * @author Sander Stolk
 */
public class ResourceInStatementToolTip extends DefaultToolTip {
    public final static int SUBJECT = 0;
    public final static int PREDICATE = 1;
    public final static int OBJECT = 2;

    protected Statement statement;
    protected int position;
    protected IModelProvider modelProvider;

    public ResourceInStatementToolTip(Control control) {
        super(control);
    }

    public void setModelProvider(IModelProvider modelProvider) {
        this.modelProvider = modelProvider;
    }

    /** Position can be SUBJECT, PREDICATE, or OBJECT. */
    public void setResourceInStatement(Statement statement, int position) {
        if (position < SUBJECT || position > OBJECT) {
            position = SUBJECT;
        }
        this.statement = statement;
        this.position = position;
    }

    @Override
    protected Composite createToolTipContentArea(Event event, Composite parent) {
        if (statement != null) {
            Resource resource = null;
            if (position == SUBJECT) {
                resource = statement.getSubject();
            }
            else if (position == PREDICATE) {
                resource = statement.getPredicate();
            }
            else if (position == OBJECT) {
                if (statement.getObject().isResource()) {
                    resource = statement.getObject().asResource();
                }
            }

            if (resource == null) {
                // return Statement tooltip content
                if (modelProvider != null) {
                    return new StatementToolTipContent(parent, modelProvider, statement, SWT.NONE);
                }
                return new StatementToolTipContent(parent, statement, SWT.NONE);
            }

            // return ResourceInStatement tooltip content
            if (modelProvider != null) {
                return new ResourceInStatementToolTipContent(parent, modelProvider, statement,
                        resource, SWT.NONE);
            }
            return new ResourceInStatementToolTipContent(parent, statement, resource, SWT.NONE);
        }
        return null;
    }
}
