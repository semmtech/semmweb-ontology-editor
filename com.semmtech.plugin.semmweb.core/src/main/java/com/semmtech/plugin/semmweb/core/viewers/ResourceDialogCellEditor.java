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


import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import com.hp.hpl.jena.rdf.model.Resource;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.CorePluginImages;


public class ResourceDialogCellEditor extends GenericDialogCellEditor {
    // private static final int GAP = 6;

    /**
     * The composite widget containing the resource icon and the name label
     * widgets
     */
    private Composite composite;
    private Text nameText;
    private LabelProvider labelProvider;

    public ResourceDialogCellEditor(Composite parent) {
        this(parent, SWT.NONE);
    }

    public ResourceDialogCellEditor(Composite parent, int style) {
        super(parent, style);
    }

    public void setLabelProvider(LabelProvider labelProvider) {
        this.labelProvider = labelProvider;
    }

    @Override
    protected Control createContents(Composite cell) {
        Color bg = cell.getBackground();
        composite = new Composite(cell, getStyle());
        composite.setBackground(bg);
        {
            GridLayout layout = new GridLayout(1, false);
            layout.marginTop = 0;
            layout.marginHeight = 0;
            composite.setLayout(layout);
        }

        nameText = new Text(composite, SWT.BORDER);
        {
            GridData data = new GridData(SWT.FILL, SWT.TOP, false, false);
            data.horizontalIndent = 3;
            data.verticalIndent = 2;
            nameText.setLayoutData(data);
        }
        nameText.setBackground(bg);
        nameText.setFont(cell.getFont());

        MenuManager manager = new MenuManager();

        // IAction listAction = new Action() {
        // public void run() {
        // }
        // };
        // listAction.setText("List");
        // manager.add(listAction);
        // manager.add(new Separator());

        IAction clearAction = new Action() {
            @Override
            public void run() {
                doSetValue(null);
            }
        };
        clearAction.setText("Clear");
        clearAction.setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(
                CorePlugin.PLUGIN_ID, CorePluginImages.IMG_DELETE));
        manager.add(clearAction);
        Menu menu = manager.createContextMenu(composite);
        composite.setMenu(menu);

        return composite;
    }

    @Override
    protected void updateContents(Object value) {
        if (value != null) {
            if (value instanceof Resource) {
                Resource resource = (Resource) value;
                nameText.setText(labelProvider.getText(resource));
            }
            else {
                nameText.setText(value.toString());
            }
        }
        else {
            nameText.setText("");
        }
    }
}
