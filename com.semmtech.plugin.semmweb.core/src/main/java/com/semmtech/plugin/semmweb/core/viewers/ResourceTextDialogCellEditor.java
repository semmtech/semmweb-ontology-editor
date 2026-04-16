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
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Text;

import com.hp.hpl.jena.rdf.model.Resource;


public class ResourceTextDialogCellEditor extends CellEditor {
    private static final Logger logger = Logger.getLogger(ResourceTextDialogCellEditor.class);

    /**
     * Internal class for laying out the dialog.
     */
    private class DialogCellLayout extends Layout {
        public void layout(Composite editor, boolean force) {
            Rectangle bounds = editor.getClientArea();
            Point size = button.computeSize(SWT.DEFAULT, SWT.DEFAULT, force);
            if (contents != null)
                contents.setBounds(0, 0, bounds.width - size.x, bounds.height);
            button.setBounds(bounds.width - size.x, 0, size.x, bounds.height);
        }

        public Point computeSize(Composite editor, int wHint, int hHint, boolean force) {
            if (wHint != SWT.DEFAULT && hHint != SWT.DEFAULT) {
                return new Point(wHint, hHint);
            }
            Point contentsSize = contents.computeSize(SWT.DEFAULT, SWT.DEFAULT, force);
            Point buttonSize = button.computeSize(SWT.DEFAULT, SWT.DEFAULT, force);

            // Just return the button width to ensure the button is not clipped
            // if the label is long.
            // The label will just use whatever extra width there is
            Point result = new Point(buttonSize.x, Math.max(contentsSize.y, buttonSize.y));
            return result;
        }
    }

    private Composite editor;
    private Control contents;
    @SuppressWarnings("unused")
    private Object input;

    protected Button button;
    protected Text text;

    private ModifyListener modifyListener;

    private Resource resource;

    /**
     * Listens for 'focusLost' events and fires the 'apply' event as long as the
     * focus wasn't lost because the dialog was opened.
     */
    private FocusListener buttonFocusListener;
    private IOpenDialogBoxListener listener;
    private ILabelProvider labelProvider;

    public ResourceTextDialogCellEditor(Composite parent) {
        super(parent);
    }

    @Override
    protected Control createControl(Composite parent) {
        Font font = parent.getFont();
        Color bg = parent.getBackground();

        editor = new Composite(parent, getStyle());
        editor.setFont(font);
        editor.setBackground(bg);
        editor.setLayout(new DialogCellLayout());

        text = new Text(editor, SWT.BORDER);
        text.addSelectionListener(new SelectionAdapter() {
            public void widgetDefaultSelected(SelectionEvent e) {
                // handleDefaultSelection(e);
            }
        });
        text.addKeyListener(new KeyAdapter() {
            // hook key pressed - see PR 14201
            public void keyPressed(KeyEvent e) {
                keyReleaseOccured(e);
                //
                // // as a result of processing the above call, clients may have
                // // disposed this cell editor
                // if ((getControl() == null) || getControl().isDisposed()) {
                // return;
                // }
                // checkSelection(); // see explanation below
                // checkDeleteable();
                // checkSelectable();
            }
        });
        text.addTraverseListener(new TraverseListener() {
            public void keyTraversed(TraverseEvent e) {
                if (e.detail == SWT.TRAVERSE_ESCAPE || e.detail == SWT.TRAVERSE_RETURN)
                    e.doit = false;
            }
        });
        // We really want a selection listener but it is not supported so we
        // use a key listener and a mouse listener to know when selection
        // changes
        // may have occurred
        text.addMouseListener(new MouseAdapter() {
            public void mouseUp(MouseEvent e) {
                // checkSelection();
                // checkDeleteable();
                // checkSelectable();
            }
        });
        text.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                ResourceTextDialogCellEditor.this.focusLost();
            }
        });
        text.setFont(parent.getFont());
        text.setBackground(parent.getBackground());
        text.addModifyListener(getModifyListener());

        contents = text;

        button = createButton(editor);
        button.setFont(font);
        button.addKeyListener(new KeyAdapter() {

            public void keyReleased(KeyEvent e) {
                if (e.character == '\u001b') { // Escape
                    fireCancelEditor();
                }
            }
        });
        button.addFocusListener(getButtonFocusListener());
        button.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent event) {
                // Remove the button's focus listener since it's guaranteed
                // to lose focus when the dialog opens
                button.removeFocusListener(getButtonFocusListener());

                Object newValue = openDialogBox(editor);

                // Re-add the listener once the dialog closes
                button.addFocusListener(getButtonFocusListener());

                if (newValue != null) {
                    boolean newValidState = isCorrect(newValue);
                    if (newValidState) {
                        markDirty();
                        doSetValue(newValue);
                    }
                    else {
                        // try to insert the current value into the error
                        // message.
                        setErrorMessage(MessageFormat.format(getErrorMessage(),
                                new Object[] { newValue.toString() }));
                    }

                    resource = (Resource) newValue;
                    fireApplyEditorValue();
                }
            }
        });

        return editor;
    }

    /**
     * Creates the button for this cell editor under the given parent control.
     * <p>
     * The default implementation of this framework method creates the button
     * display on the right hand side of the dialog cell editor. Subclasses may
     * extend or reimplement.
     * </p>
     * 
     * @param parent
     *            the parent control
     * @return the new button control
     */
    protected Button createButton(Composite parent) {
        Button result = new Button(parent, SWT.DOWN);
        result.setText("...");
        return result;
    }

    public void setOpenDialogBoxListener(IOpenDialogBoxListener listener) {
        this.listener = listener;
    }

    /**
     * Return the modify listener.
     */
    private ModifyListener getModifyListener() {
        if (modifyListener == null) {
            modifyListener = new ModifyListener() {
                public void modifyText(ModifyEvent e) {
                    editOccured(e);
                }
            };
        }
        return modifyListener;
    }

    /**
     * Processes a modify event that occurred in this text cell editor. This
     * framework method performs validation and sets the error message
     * accordingly, and then reports a change via
     * <code>fireEditorValueChanged</code>. Subclasses should call this method
     * at appropriate times. Subclasses may extend or reimplement.
     * 
     * @param e
     *            the SWT modify event
     */
    protected void editOccured(ModifyEvent e) {
        String value = text.getText();
        if (value == null) {
            value = "";
        }
        Object typedValue = value;
        boolean oldValidState = isValueValid();
        boolean newValidState = isCorrect(typedValue);
        if (!newValidState) {
            // / Try to insert the current value into the error message.
            setErrorMessage(MessageFormat.format(getErrorMessage(), new Object[] { value }));
        }
        resource = null;
        valueChanged(oldValidState, newValidState);
    }

    /**
     * Return a listener for button focus.
     * 
     * @return FocusListener
     */
    private FocusListener getButtonFocusListener() {
        if (buttonFocusListener == null) {
            buttonFocusListener = new FocusListener() {

                public void focusGained(FocusEvent e) {
                    // Do nothing
                }

                public void focusLost(FocusEvent e) {
                    // ResourceTextDialogCellEditor.this.focusLost();
                }
            };
        }

        return buttonFocusListener;
    }

    @Override
    protected void focusLost() {
        // if (isActivated()) {
        // fireApplyEditorValue();
        // deactivate();
        // }
    }

    @Override
    protected Object doGetValue() {
        if (resource != null) {
            return resource;
        }
        return text.getText();
    }

    @Override
    protected void doSetFocus() {
        if (text != null) {
            text.selectAll();
            text.setFocus();
            // checkSelection();
            // checkDeleteable();
            // checkSelectable();
        }

    }

    protected void keyReleaseOccured(KeyEvent keyEvent) {
        if (keyEvent.character == '\u001b') { // Escape character
            fireCancelEditor();
        }
        else if (keyEvent.character == '\t' || keyEvent.character == SWT.CR) { // tab
                                                                               // key
            fireApplyEditorValue();
        }
    }

    @Override
    protected void doSetValue(Object value) {
        if (value instanceof String) {
            logger.debug("doSetValue() -> (String)value = " + value.toString());
            if (labelProvider != null)
                text.setText(labelProvider.getText(value));
            else
                text.setText((String) value);
        }
        else if (value instanceof Resource) {
            resource = (Resource) value;
            logger.debug("doSetValue() -> (Resource)value = " + resource.getURI());
            if (labelProvider != null)
                text.setText(labelProvider.getText(resource));
            else
                text.setText(resource.getURI());
        }
    }

    protected Object openDialogBox(Control cellEditorWindow) {
        if (listener != null)
            return listener.openDialogBox(cellEditorWindow);
        return null;
    }

    public void setInput(Object input) {
        this.input = input;
        this.resource = null;
        text.setText("");
    }

    public void setLabelProvider(ILabelProvider labelProvider) {
        this.labelProvider = labelProvider;
    }
}
