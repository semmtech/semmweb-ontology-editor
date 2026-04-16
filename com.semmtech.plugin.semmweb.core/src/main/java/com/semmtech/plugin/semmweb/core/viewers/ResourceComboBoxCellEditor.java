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


import java.text.MessageFormat;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import com.hp.hpl.jena.rdf.model.Resource;


public class ResourceComboBoxCellEditor extends CellEditor {
    private static Logger logger = Logger.getLogger(ResourceComboBoxCellEditor.class);

    /**
     * The custom combo box control.
     */
    protected ComboViewer viewer;
    protected Object selectedValue;

    private CCombo comboBox;

    private Listener itemAddedListener;

    /**
     * Default ComboBoxCellEditor style
     */
    private static final int defaultStyle = SWT.NONE;

    /**
     * Creates a new cell editor with a combo viewer and a default style
     */
    public ResourceComboBoxCellEditor(Composite parent) {
        this(parent, defaultStyle);
    }

    /**
     * Creates a new cell editor with a combo viewer and the given style
     */
    public ResourceComboBoxCellEditor(Composite parent, int style) {
        super(parent, style);
        setValueValid(true);
    }

    protected Control createControl(Composite parent) {
        comboBox = new CCombo(parent, getStyle());
        comboBox.setFont(parent.getFont());
        viewer = new ComboViewer(comboBox);

        comboBox.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                keyReleaseOccured(e);
            }
        });

        comboBox.addSelectionListener(new SelectionAdapter() {
            public void widgetDefaultSelected(SelectionEvent event) {
                applyEditorValueAndDeactivate();
            }

            public void widgetSelected(SelectionEvent event) {
                ISelection selection = viewer.getSelection();
                if (selection.isEmpty()) {
                    selectedValue = null;
                }
                else {
                    selectedValue = ((IStructuredSelection) selection).getFirstElement();
                }
            }
        });

        comboBox.addTraverseListener(new TraverseListener() {
            public void keyTraversed(TraverseEvent e) {
                if (e.detail == SWT.TRAVERSE_ESCAPE || e.detail == SWT.TRAVERSE_RETURN)
                    e.doit = false;
            }
        });

        comboBox.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                ResourceComboBoxCellEditor.this.focusLost();
            }
        });

        return comboBox;
    }

    /**
     * The <code>ComboBoxCellEditor</code> implementation of this
     * <code>CellEditor</code> framework method returns the zero-based index of
     * the current selection.
     */
    protected Object doGetValue() {
        return selectedValue;
    }

    protected void doSetFocus() {
        viewer.getControl().setFocus();
    }

    /**
     * The <code>ComboBoxCellEditor</code> implementation of this
     * <code>CellEditor</code> framework method sets the minimum width of the
     * cell. The minimum width is 10 characters if <code>comboBox</code> is not
     * <code>null</code> or <code>disposed</code> eles it is 60 pixels to make
     * sure the arrow button and some text is visible. The list of CCombo will
     * be wide enough to show its longest item.
     */
    public LayoutData getLayoutData() {
        LayoutData layoutData = super.getLayoutData();
        if ((viewer.getControl() == null) || viewer.getControl().isDisposed()) {
            layoutData.minimumWidth = 60;
        }
        else {
            // make the comboBox 10 characters wide
            GC gc = new GC(viewer.getControl());
            layoutData.minimumWidth = (gc.getFontMetrics().getAverageCharWidth() * 10) + 10;
            gc.dispose();
        }
        return layoutData;
    }

    /**
     * Set a new value
     */
    protected void doSetValue(Object value) {
        Assert.isTrue(viewer != null);

        selectedValue = value;
        if (value == null) {
            logger.debug("doSetValue() -> value = null");
            viewer.setSelection(StructuredSelection.EMPTY);
        }
        else if (value instanceof Resource) {
            logger.debug("doSetValue() -> (Resource)value = " + value);
            viewer.setSelection(new StructuredSelection(value));
        }
        else if (value instanceof String) {
            logger.debug("doSetValue() -> (String)value = " + value);
            viewer.setSelection(new StructuredSelection(value));
        }
    }

    public void setLabelProvider(IBaseLabelProvider labelProvider) {
        viewer.setLabelProvider(labelProvider);
    }

    public void setContentProvider(IStructuredContentProvider provider) {
        viewer.setContentProvider(provider);
    }

    public void setItemAddedListener(Listener listener) {
        itemAddedListener = listener;
    }

    public void setInput(Object input) {
        viewer.setInput(input);
    }

    /**
     * @return get the viewer
     */
    public ComboViewer getViewer() {
        return viewer;
    }

    /**
     * Applies the currently selected value and deactiavates the cell editor
     */
    void applyEditorValueAndDeactivate() {
        logger.debug("applyEditorValueAndDeactivate()");
        // must set the selection before getting value
        ISelection selection = viewer.getSelection();
        if (selection.isEmpty() && (comboBox.getText() == null || comboBox.getText().length() == 0)) {
            selectedValue = null;
        }
        else if (selection.isEmpty()) {
            String entered = comboBox.getText();
            selectedValue = entered;
            if (itemAddedListener != null)
                itemAddedListener.handleEvent(new Event());
        }
        else {
            selectedValue = ((IStructuredSelection) selection).getFirstElement();
        }

        Object newValue = doGetValue();
        markDirty();
        boolean isValid = isCorrect(newValue);
        setValueValid(isValid);

        if (!isValid)
            MessageFormat.format(getErrorMessage(), new Object[] { selectedValue });

        fireApplyEditorValue();
        deactivate();
    }

    @Override
    protected void fireApplyEditorValue() {
        // TODO Auto-generated method stub
        // super.fireApplyEditorValue();
    }

    protected void focusLost() {
        if (isActivated()) {
            applyEditorValueAndDeactivate();
        }
    }

    protected void keyReleaseOccured(KeyEvent keyEvent) {
        if (keyEvent.character == '\u001b') { // Escape character
            fireCancelEditor();
        }
        else if (keyEvent.character == '\t' || keyEvent.character == SWT.CR) { // tab
                                                                               // key
            applyEditorValueAndDeactivate();
        }
    }
}
