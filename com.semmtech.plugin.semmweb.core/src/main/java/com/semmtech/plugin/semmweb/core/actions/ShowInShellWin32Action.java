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

package com.semmtech.plugin.semmweb.core.actions;


import java.net.URI;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;

import com.semmtech.plugin.semmweb.core.shell.ShellWin32Util;


public class ShowInShellWin32Action extends Action {
    private final ISelectionProvider selectionProvider;
    private IResource selected;

    public ShowInShellWin32Action(String text, ISelectionProvider selectionProvider) {
        super(text);
        this.selectionProvider = selectionProvider;
    }

    @Override
    public boolean isEnabled() {
        ISelection selection = selectionProvider.getSelection();
        selected = null;
        if (!selection.isEmpty()) {
            IStructuredSelection structured = (IStructuredSelection) selection;
            if (structured.size() == 1 && structured.getFirstElement() instanceof IResource) {
                selected = (IResource) structured.getFirstElement();
                return true;
            }
        }
        return false;
    }

    @Override
    public void run() {
        URI uri = selected.getLocationURI();
        if (selected.isLinked()) {
            uri = selected.getRawLocationURI();
        }
        ShellWin32Util.showInExplorer(uri, selected.getType());
    }
}
