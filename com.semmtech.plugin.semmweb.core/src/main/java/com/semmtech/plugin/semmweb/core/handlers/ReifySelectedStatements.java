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


import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Statement;
import com.semmtech.plugin.semmweb.core.model.IModelProvider;
import com.semmtech.plugin.semmweb.core.model.ModelTransaction;


public class ReifySelectedStatements extends AbstractHandler {
    public static final String ID = "com.semmtech.plugin.semmweb.core.commands.reifySelectedStatements";

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {

        ISelection selection = HandlerUtil.getCurrentSelection(event);
        if (selection == null)
            return null;

        if (selection instanceof StructuredSelection) {

            IEditorPart editorPart = HandlerUtil.getActiveEditor(event);
            if (!(editorPart instanceof IModelProvider))
                return null;

            IModelProvider modelProvider = (IModelProvider) editorPart;

            String transactionDescription = "Due to the creation of a reified statement";
            ModelTransaction transaction = modelProvider.createTransaction(transactionDescription);

            OntModel model = modelProvider.getOntModel();
            for (Object selected : ((StructuredSelection) selection).toList()) {
                if (!(selected instanceof Statement))
                    continue;
                Statement statement = (Statement) selected;
                model.createReifiedStatement(statement);
            }
            modelProvider.commitTransaction(transaction);
        }

        return null;
    }

}
