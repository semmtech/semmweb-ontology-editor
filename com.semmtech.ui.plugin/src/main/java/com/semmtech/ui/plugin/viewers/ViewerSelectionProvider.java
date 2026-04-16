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

package com.semmtech.ui.plugin.viewers;


import java.util.List;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.Viewer;

import com.google.common.collect.Lists;


/**
 * Custom selection provider which allows for the inner viewer to be changed,
 * while maintaining all listeners to the original provider.
 * 
 * @author Mike
 * 
 */
public class ViewerSelectionProvider implements ISelectionProvider {
    private Viewer viewer;
    private List<ISelectionChangedListener> listeners = Lists.newArrayList();

    public ViewerSelectionProvider() {
    }

    public void updateViewer(Viewer viewer) {
        this.viewer = viewer;
        if (viewer != null) {
            for (ISelectionChangedListener listener : listeners) {
                this.viewer.addSelectionChangedListener(listener);
            }
        }
    }

    @Override
    public void addSelectionChangedListener(ISelectionChangedListener listener) {
        listeners.add(listener);
        if (viewer != null) {
            viewer.addSelectionChangedListener(listener);
        }
    }

    @Override
    public ISelection getSelection() {
        if (viewer != null) {
            return viewer.getSelection();
        }
        return null;
    }

    @Override
    public void removeSelectionChangedListener(ISelectionChangedListener listener) {
        listeners.remove(listener);
        if (viewer != null) {
            viewer.removeSelectionChangedListener(listener);
        }
    }

    @Override
    public void setSelection(ISelection selection) {
        if (viewer != null) {
            viewer.setSelection(selection);
        }
    }
}
