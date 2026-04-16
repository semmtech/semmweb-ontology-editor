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


import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.hp.hpl.jena.rdf.model.RDFNode;
import com.semmtech.plugin.semmweb.core.Colors;
import com.semmtech.plugin.semmweb.core.forms.editor.AbstractModelResourceContent;


/**
 * 
 * @author Sander Stolk
 */
public abstract class AbstractPropertyObjectContentPart extends AbstractModelResourceContentPart {

    protected final String BACKGROUND = "AbstractModelResourceContentPart.background";
    protected final String DEFAULT_BORDER = "AbstractModelResourceContentPart.defaultBorder";
    protected final String MODIFY_BORDER = "AbstractModelResourceContentPart.modifyBorder";
    protected final String ERROR_BORDER = "AbstractModelResourceContentPart.errorBorder";
    protected final String DEFAULT_FOREGROUND = "AbstractModelResourceContentPart.defaultForeground";
    protected final String MODIFY_FOREGROUND = "AbstractModelResourceContentPart.modifyForeground";
    protected final String ERROR_FOREGROUND = "AbstractModelResourceContentPart.errorForeground";

    public AbstractPropertyObjectContentPart(AbstractModelResourceContent contentParent,
            Composite parent, FormToolkit toolkit) {
        super(contentParent, parent, toolkit);
    }

    abstract public RDFNode getObject();

    /**
     * Will update the presentation of the current object if necessary. For
     * regular statements, this won't be necessary. For lists, however, the
     * members in the list may have changed, requiring the representation of the
     * object to update. This function returns true if an update was necessary
     * and has been applied; false otherwise.
     */
    @SuppressWarnings("static-method")
    public boolean updateContent() {
        return false;
    }

    public FormColors getDefaultFormColors() {
        FormColors formColors = new FormColors(Display.getCurrent());
        formColors.createColor(DEFAULT_BORDER, formColors.getBorderColor().getRGB());
        formColors.createColor(MODIFY_BORDER, Colors.RGB_SEMMTECH_BLUE);
        formColors.createColor(ERROR_BORDER, 255, 0, 0);
        formColors.createColor(MODIFY_FOREGROUND, Colors.RGB_SEMMTECH_LIGHT_BLUE);
        formColors.createColor(DEFAULT_FOREGROUND, 0, 0, 0);
        formColors.createColor(ERROR_FOREGROUND, 255, 0, 0);
        formColors.createColor(BACKGROUND, 255, 255, 255);
        return formColors;
    }
}
