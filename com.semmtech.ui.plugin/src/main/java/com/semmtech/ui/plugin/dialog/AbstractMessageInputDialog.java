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

package com.semmtech.ui.plugin.dialog;


import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.semmtech.ui.plugin.BasePlugin;


/**
 * This abstract input dialog is used to remove overhead boiler-plate code form
 * other input dialogs. The class already handles the configuration of the shell
 * (creates a modal dialog with a title bar - SWT.APPLICATION_MODEL | SWT.TITLE)
 * and the creation of the OK and Cancel buttons.
 * <p>
 * The OK button can be retrieved by subclasses of this abstract class by using
 * the getOKButton method.
 * <p>
 * Every subclass should override the createInputArea() method, the first
 * statement within this implementation should be calling the createInputArea of
 * this abstract to create a composite control with the right settings
 * (dimensions and a GridLayout of two columns).
 * 
 * @author Mike Henrichs
 * 
 */
public abstract class AbstractMessageInputDialog extends Dialog {
    protected static final int DIALOG_LABEL_SIZE = 80;
    protected static final int DIALOG_WIDTH_WINT = 450;

    private int style;
    protected boolean showErrorMessage;

    private String title;
    private String message;
    private String errorMessage;

    protected Control inputArea;
    private Button okButton;
    private Text errorMessageText;

    /**
     * 
     * @param parentShell
     */
    protected AbstractMessageInputDialog(Shell parentShell) {
        this(parentShell, null, null);
    }

    /**
     * Constuctor of the AbstractMessageInputDialog.
     * 
     * @param parentShell
     * @param title
     *            The title used to display on the shell of the dialog
     * @param message
     *            The message shown within the dialog
     */
    protected AbstractMessageInputDialog(Shell parentShell, String title, String message) {
        this(parentShell, title, message, false);
    }

    public AbstractMessageInputDialog(Shell parent, int style) {
        this(parent, null, null);
        this.style = style;
    }

    protected AbstractMessageInputDialog(Shell parentShell, String title, String message,
            boolean showErrorMessage) {
        super(parentShell);
        this.title = title;
        this.message = message;
        this.showErrorMessage = showErrorMessage;
    }

    /**
     * Create the dialog area, containing the message label, and afterwards
     * calls the createInputArea composite control.
     */
    @Override
    protected Control createDialogArea(Composite parent) {
        Composite composite = (Composite) super.createDialogArea(parent);
        composite.setLayoutData(new GridData(GridData.FILL));

        GridLayout layout = new GridLayout();
        layout.marginTop = 6;
        layout.marginRight = 5;
        layout.marginBottom = 0;
        layout.marginLeft = 4;
        layout.verticalSpacing = 0;

        composite.setLayout(layout);

        if (message != null) {
            Label label = new Label(composite, SWT.WRAP);
            label.setText(message);
            GridData layoutData = new GridData(GridData.FILL, GridData.BEGINNING, true, false);
            layoutData.widthHint = convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
            label.setLayoutData(layoutData);
            label.setFont(composite.getFont());
        }

        inputArea = createInputArea(composite);

        if (showErrorMessage) {
            errorMessageText = new Text(composite, SWT.READ_ONLY | SWT.WRAP);
            GridData layoutData = new GridData(GridData.GRAB_HORIZONTAL
                    | GridData.HORIZONTAL_ALIGN_FILL);
            layoutData.verticalIndent = 12;
            errorMessageText.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_RED));
            errorMessageText.setLayoutData(layoutData);
            errorMessageText.setBackground(errorMessageText.getDisplay().getSystemColor(
                    SWT.COLOR_WIDGET_BACKGROUND));
        }

        setErrorMessage(errorMessage);
        applyDialogFont(composite);
        return composite;
    }

    /**
     * Creates and returns the contents of the upper part of this dialog (below
     * the message label and above the button bar). The Dialog implementation of
     * this framework method creates and returns a new Composite with standard
     * margins and spacing. The returned control's layout data must be an
     * instance of GridData. This method must not modify the parent's layout.
     * 
     * @param parent
     * @return
     */
    protected Control createInputArea(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);

        GridLayout layout = new GridLayout(2, false);
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        composite.setLayout(layout);

        GridData layoutData = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL);
        layoutData.verticalIndent = 7;
        composite.setLayoutData(layoutData);

        return composite;
    }

    /**
     * Configures the given shell in preparation for opening this window in it.
     * It sets the title and sets the image to the default image (which will be
     * empty).
     */
    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        if (title != null) {
            shell.setText(title);
        }
        try {
            // Added to support WindowBuilder interface
            shell.setImage(BasePlugin.getDefault().getImage("icons/cloud-closed-shadow-16.png"));
        }
        catch (Exception ex) {
        }
    }

    /**
     * Sets the style of the shell to SWT.APPLICATION_MODAL and SWT.TITLE
     */
    @Override
    protected void setShellStyle(int newShellStyle) {
        super.setShellStyle(SWT.APPLICATION_MODAL | SWT.TITLE | SWT.CLOSE);
    }

    protected void resizeShell() {
        Shell shell = getParentShell();
        shell.layout(true, true);
        Point size = shell.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
        shell.setSize(size);
    }

    /**
     * Create the OK and Cancel button, the OK button can be accessed through
     * the okButton field.
     */
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        okButton = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
        createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
        okButton.setEnabled(errorMessage == null);
    }

    /**
     * Returns the OK button.
     */
    @Override
    protected Button getOKButton() {
        return okButton;
    }

    /**
     * Sets the enable state of the OK button.
     * 
     * @param enabled
     *            sets the enable state of the OK button of the dialog.
     */
    protected void setOKButtonEnabled(boolean enabled) {
        if (okButton != null) {
            okButton.setEnabled(enabled);
        }
    }

    public void setText(String text) {
        this.title = text;
    }

    public String getText() {
        return title;
    }

    public Shell getParent() {
        return getShell();
    }

    public int getStyle() {
        return style;
    }

    protected void validateInput() {
        String errorMessage = null;
        setErrorMessage(errorMessage);
    }

    protected void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
        if (errorMessageText != null && !errorMessageText.isDisposed()) {
            errorMessageText.setText(errorMessage == null ? " \n " : errorMessage); //$NON-NLS-1$
            // Disable the error message text control if there is no error, or
            // no error text (empty or whitespace only). Hide it also to avoid
            // color change.
            // See https://bugs.eclipse.org/bugs/show_bug.cgi?id=130281
            boolean hasError = errorMessage != null
                    && (StringConverter.removeWhiteSpaces(errorMessage)).length() > 0;
            errorMessageText.setEnabled(hasError);
            errorMessageText.setVisible(hasError);
            errorMessageText.getParent().update();
        }
        // Access the ok button by id, in case clients have overridden button
        // creation.
        // See https://bugs.eclipse.org/bugs/show_bug.cgi?id=113643
        Control button = getButton(IDialogConstants.OK_ID);
        if (button != null) {
            button.setEnabled(errorMessage == null);
        }
    }

}
