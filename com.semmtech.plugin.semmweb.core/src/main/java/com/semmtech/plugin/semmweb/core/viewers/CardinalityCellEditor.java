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


import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorDeactivationEvent;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.semmtech.plugin.semmweb.core.widgets.Cardinality;
import com.semmtech.plugin.semmweb.core.widgets.CardinalitySpinner;


public class CardinalityCellEditor extends CellEditor {

    private static final int DEFAULT_STYLE = SWT.NONE;

    private Composite editor;
    private CardinalitySpinner spinner;

    public CardinalityCellEditor(Composite parent) {
        this(parent, DEFAULT_STYLE);
    }

    public CardinalityCellEditor(Composite parent, int style) {
        super(parent, style);
    }

    @Override
    protected Control createControl(Composite parent) {
        Font font = parent.getFont();
        Color backgroundColor = parent.getBackground();

        editor = new Composite(parent, getStyle());
        editor.setFont(font);
        editor.setBackground(backgroundColor);

        GridLayout layout = new GridLayout();
        layout.marginHeight = 0;
        layout.marginTop = 1;

        editor.setLayout(layout);

        GridData layoutData = new GridData();
        layoutData.grabExcessHorizontalSpace = true;
        layoutData.horizontalAlignment = SWT.FILL;

        spinner = new CardinalitySpinner(editor, SWT.NONE, true, "to");
        spinner.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                keyReleaseOccured(e);
            }
        });
        spinner.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                CardinalityCellEditor.this.focusLost();
            }
        });

        layoutData = new GridData();
        layoutData.horizontalAlignment = SWT.CENTER;
        layoutData.grabExcessHorizontalSpace = true;

        spinner.setLayoutData(layoutData);
        spinner.setBackground(backgroundColor);

        setValueValid(true);

        return editor;
    }

    @Override
    protected Object doGetValue() {
        return spinner.getCardinality();
    }

    @Override
    protected void doSetFocus() {
        validateValue();
    }

    @Override
    protected void doSetValue(Object value) {
        updateContents((Cardinality) value);
    }

    protected void keyReleaseOccured(KeyEvent keyEvent) {
        if (keyEvent.character == '\u001b') // Escape character
            fireCancelEditor();
        else if (keyEvent.character == '\t' || keyEvent.character == SWT.CR) // tab
                                                                             // key
                                                                             // or
                                                                             // Enter
            fireApplyEditorValue();
    }

    @Override
    protected void fireApplyEditorValue() {
        validateValue();

        super.fireApplyEditorValue();
    }

    @Override
    protected void fireCancelEditor() {
        spinner.setErrorMessage(null);
        super.fireCancelEditor();
    }

    private String validateValue() {
        ICellEditorValidator validator = getValidator();
        String errorMessage = null;
        if (validator != null) {
            errorMessage = validator.isValid(getValue());
        }
        setErrorMessage(errorMessage);
        spinner.setErrorMessage(errorMessage);
        return errorMessage;
    }

    @Override
    public void deactivate() {
        super.deactivate();
    }

    @Override
    protected void deactivate(ColumnViewerEditorDeactivationEvent event) {
        super.deactivate(event);
    }

    @Override
    protected void focusLost() {
        fireCancelEditor();
    }

    protected void updateContents(Cardinality value) {
        spinner.setCardinality(value);
    }
}
