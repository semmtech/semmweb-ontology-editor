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

package com.semmtech.plugin.semmweb.core.views;


import org.eclipse.help.HelpSystem;
import org.eclipse.help.IContext;
import org.eclipse.help.IContextProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;

import com.semmtech.plugin.semmweb.core.CoreHelpContextIds;
import com.semmtech.plugin.semmweb.core.navigator.IImport;
import com.semmtech.plugin.semmweb.core.navigator.IModel;
import com.semmtech.ui.plugin.util.Selections;


/**
 * Provides the ID for the Dynamic Help mechanism. When the Project Navigator,
 * or one of his items, is selected the {@link ProjectNavigatorView} is asked to
 * provide a IContextProvider and an instance of this class is returned. The
 * {@link #getContext(Object)} method returns the ID of the selected item
 * corresponding to the one defined in context.xml file in the help project.
 * 
 * @author Simone Rondelli
 */
public class ProjectNavigatorHelpContextProvider implements IContextProvider {

    private final ISelectionProvider selectionProvider;

    public ProjectNavigatorHelpContextProvider(ISelectionProvider selectionProvider) {
        this.selectionProvider = selectionProvider;
    }

    @Override
    public int getContextChangeMask() {
        return IContextProvider.SELECTION;
    }

    @Override
    public IContext getContext(Object target) {
        ISelection selection = selectionProvider.getSelection();

        Object selected = Selections.retrieveFirst(selection);

        if (selected instanceof IModel) {
            return HelpSystem.getContext(CoreHelpContextIds.MODEL);
        }
        else if (selected instanceof IImport) {
            return HelpSystem.getContext(CoreHelpContextIds.IMPORT);
        }

        return HelpSystem.getContext(CoreHelpContextIds.PROJECT_NAVIGATOR);
    }

    @Override
    public String getSearchExpression(Object target) {
        return null;
    }
}
