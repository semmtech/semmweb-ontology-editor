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


import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.hp.hpl.jena.ontology.OntResource;
import com.semmtech.plugin.semmweb.core.forms.editor.AbstractModelResourceContent;
import com.semmtech.plugin.semmweb.core.model.IModelProvider;
import com.semmtech.ui.plugin.widgets.Widgets;


/**
 * 
 * @author Sander Stolk
 */
abstract public class AbstractModelResourceContentPart extends Composite implements
        DropTargetListener {
    protected final AbstractModelResourceContent contentParent;
    protected FormToolkit toolkit;

    public AbstractModelResourceContentPart(AbstractModelResourceContent contentParent,
            Composite parent, FormToolkit toolkit) {
        super(parent, SWT.NONE);
        this.contentParent = contentParent;
        this.toolkit = toolkit;

        if (toolkit != null) {
            toolkit.adapt(this);
            toolkit.paintBordersFor(this);
        }

        parent.addDisposeListener(new DisposeListener() {

            @Override
            public void widgetDisposed(DisposeEvent e) {
                AbstractModelResourceContentPart.this.dispose();
            }
        });
    }

    public IEditorPart getEditor() {
        return contentParent.getEditor();
    }

    public IModelProvider getModelProvider() {
        return contentParent.getModelProvider();
    }

    public OntResource getResource() {
        return contentParent.getResource();
    }

    public void refresh() {
        if (!Widgets.isNullOrDisposed(this)) {
            setRedraw(false);
            Widgets.layoutControlUpToScrollableParent(this);
            setRedraw(true);
        }
    }

    @Override
    public void dragEnter(DropTargetEvent event) {
    }

    @Override
    public void dragLeave(DropTargetEvent event) {
    }

    @Override
    public void dragOperationChanged(DropTargetEvent event) {
    }

    @Override
    public void dragOver(DropTargetEvent event) {
    }

    @Override
    public void drop(DropTargetEvent event) {
    }

    @Override
    public void dropAccept(DropTargetEvent event) {
    }

}
