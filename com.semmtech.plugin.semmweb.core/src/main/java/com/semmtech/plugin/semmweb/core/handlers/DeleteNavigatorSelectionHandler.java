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


import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.handlers.HandlerUtil;

import com.semmtech.plugin.semmweb.core.actions.SemanticProjectActionProvider;
import com.semmtech.plugin.semmweb.core.commands.Commands;
import com.semmtech.plugin.semmweb.core.navigator.IImport;
import com.semmtech.plugin.semmweb.core.navigator.IModel;
import com.semmtech.plugin.semmweb.core.navigator.ISemanticElement;
import com.semmtech.ui.plugin.util.Selections;


/**
 * 
 * @author Sander Stolk
 * @author Simone Rondelli
 */
public class DeleteNavigatorSelectionHandler extends AbstractHandler {
    public static final String ID = "com.semmtech.plugin.semmweb.core.commands.deleteNavigatorSelection";

    protected static final String ECLIPSE_DELETE_COMMAND = "org.eclipse.ui.edit.delete";

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        ISelection selection = HandlerUtil.getCurrentSelection(event);

        List<ISemanticElement> semanticElements = Selections.retrieveAllAsType(selection,
                ISemanticElement.class);
        if (!semanticElements.isEmpty()) {
            List<IModel> models = Selections.retrieveAllAsType(selection, IModel.class);
            if (!models.isEmpty()) {
                if (Commands.isEnabled(DeleteSelectedModelsHandler.ID)) {
                    Commands.execute(DeleteSelectedModelsHandler.ID);
                }
            }
            else {
                if (Selections.hasAllOfType(selection, IImport.class)) {
                    List<IImport> imports = Selections.retrieveAllAsType(selection, IImport.class);
                    IAction deleteAction = new SemanticProjectActionProvider.RemoveImportsAction(
                            imports);
                    deleteAction.run();
                }
            }
        }

        // Eclipse delete is enabled only when no SemanticElement is selected
        if (Commands.isEnabled(ECLIPSE_DELETE_COMMAND)) {
            Commands.execute(ECLIPSE_DELETE_COMMAND);
        }

        return null;
    }

}
