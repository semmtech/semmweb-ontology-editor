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


import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.dialogs.PropertyPage;

import com.semmtech.plugin.semmweb.core.navigator.ISemanticElement;


/**
 * 
 * @author Mike Henrichs
 */
public abstract class AbstractProjectPropertyPage extends PropertyPage {

    private IProject project;

    public AbstractProjectPropertyPage() {
        super();
    }

    @Override
    public void setElement(IAdaptable element) {
        if (element instanceof IProject) {
            project = (IProject) element;
        }
        else if (element instanceof ISemanticElement) {
            project = ((ISemanticElement) element).getProject();
        }
    }

    protected IProject getProject() {
        return project;
    }
}
