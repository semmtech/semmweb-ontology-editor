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

package com.semmtech.plugin.semmweb.core.handlers;


import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;

import com.hp.hpl.jena.rdf.model.Resource;
import com.semmtech.plugin.semmweb.core.commands.Commands;
import com.semmtech.ui.plugin.EclipseUIPlugin;


/**
 * 
 * @author Sander Stolk
 */
public class CopyResourceToClipboardHandler extends SelectedResourceHandler {
    public static final String ID = "com.semmtech.plugin.semmweb.core.commands.copyResourceToClipboard";

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        Resource selectedResource = getSelectedResource(event);
        // if (selectedResource != null) {
        // Display display = HandlerUtil.getActiveShell(event).getDisplay();
        // Clipboard clipboard = new Clipboard(display);
        // clipboard.setContents(new Object[] { selectedResource.getURI() },
        // new Transfer[] { TextTransfer.getInstance() });
        // clipboard.dispose();
        // }
        setClipboardContents(selectedResource);
        return null;
    }

    private static void setClipboardContents(Resource resource) {
        if (resource != null) {
            Display display = EclipseUIPlugin.getStandardDisplay();
            Clipboard clipboard = new Clipboard(display);
            clipboard.setContents(new Object[] { resource.getURI() },
                    new Transfer[] { TextTransfer.getInstance() });
            clipboard.dispose();
        }
    }

    public static Action createAction(final String actionText) {
        Action result = new Action(actionText) {
            @Override
            public void run() {
                Commands.execute(ID, null);
            }
        };
        return result;
    }

    public static Action createAction(final String actionText, final Resource resource) {
        Action result = new Action(actionText) {
            @Override
            public void run() {
                setClipboardContents(resource);
            }
        };
        return result;
    }
}
