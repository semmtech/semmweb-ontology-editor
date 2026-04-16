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

package com.semmtech.ui.plugin.wizard;


import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;

import com.google.common.base.Strings;


/**
 * The Class BaseWizardPage.
 */
public abstract class BaseWizardPage extends WizardPage {

    /**
     * Instantiates a new base wizard page.
     * 
     * @param pageName
     *            the page name
     */
    protected BaseWizardPage(String pageName) {
        super(pageName);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets
     * .Composite)
     */
    public abstract void createControl(Composite parent);

    /**
     * Clear error message.
     */
    protected void clearErrorMessage() {
        if (!Strings.isNullOrEmpty(getErrorMessage()))
            setErrorMessage(null);
    }
}
