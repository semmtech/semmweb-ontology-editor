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

package com.semmtech.plugin.semmweb.core.ui;


import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;


public class PartAdapter implements IPartListener, IPartListener2 {

    @Override
    public void partActivated(IWorkbenchPart part) {
    }

    @Override
    public void partBroughtToTop(IWorkbenchPart part) {
    }

    @Override
    public void partClosed(IWorkbenchPart part) {
    }

    @Override
    public void partDeactivated(IWorkbenchPart part) {
    }

    @Override
    public void partOpened(IWorkbenchPart part) {
    }

    @Override
    public void partActivated(IWorkbenchPartReference reference) {
    }

    @Override
    public void partBroughtToTop(IWorkbenchPartReference reference) {
    }

    @Override
    public void partClosed(IWorkbenchPartReference reference) {
    }

    @Override
    public void partDeactivated(IWorkbenchPartReference reference) {
    }

    @Override
    public void partHidden(IWorkbenchPartReference reference) {
    }

    @Override
    public void partInputChanged(IWorkbenchPartReference reference) {
    }

    @Override
    public void partOpened(IWorkbenchPartReference reference) {
    }

    @Override
    public void partVisible(IWorkbenchPartReference reference) {
    }
}
