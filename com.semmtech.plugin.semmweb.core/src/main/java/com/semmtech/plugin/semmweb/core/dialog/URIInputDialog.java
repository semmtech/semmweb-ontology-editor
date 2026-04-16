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

package com.semmtech.plugin.semmweb.core.dialog;


import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

import com.hp.hpl.jena.rdf.model.Model;
import com.semmtech.plugin.semmweb.core.util.ResourceURIUtil;


public class URIInputDialog extends InputDialog {
    private Model model;

    public URIInputDialog(Shell parentShell, String dialogTitle, String dialogMessage,
            String initialValue, Model model, IInputValidator validator) {
        super(parentShell, dialogTitle, dialogMessage, initialValue, validator);

        this.model = model;
    }

    @Override
    protected void setShellStyle(int newShellStyle) {
        super.setShellStyle(SWT.TITLE);
    }

    @Override
    public String getValue() {
        return super.getValue();
    }

    public String getPrefix() {
        return ResourceURIUtil.extractPrefix(getValue());
    }

    public String getLocalName() {
        return ResourceURIUtil.extractLocalName(getValue());
    }

    /** Always returns an absolute URI; never a prefixed version. */
    public String getURI() {
        String prefix = getPrefix();
        String localName = getLocalName();
        String namespace = model.getNsPrefixURI(prefix);
        if (namespace == null) {
            String value = getValue();
            if (value.startsWith("<") && value.endsWith(">")) {
                return value.substring(1, value.length() - 1);
            }
            return value;
        }
        return namespace + localName;
    }

}
