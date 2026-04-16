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

package com.semmtech.plugin.semmweb.editor.handlers;


import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;

import com.google.common.collect.Lists;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Statement;
import com.semmtech.plugin.semmweb.core.handlers.SelectedResourceHandler;
import com.semmtech.plugin.semmweb.core.model.IModelProvider;
import com.semmtech.plugin.semmweb.core.model.ModelTransaction;


/**
 * SourceProvider; for instance check if Statement CAN be deleted (base model)
 * http
 * ://blog.eclipse-tips.com/2009/02/commands-part-5-authentication-in-rcp.html
 * 
 * @author Mike Henrichs
 * 
 */
public class DeleteSelectedStatementHandler extends SelectedResourceHandler {
    public static final String ID = "com.semmtech.plugin.semmweb.editor.commands.deleteSelectedStatement";
    private static Logger logger = Logger.getLogger(DeleteSelectedStatementHandler.class);

    private void createRemoveStatements(OntModel ontModel, List<Statement> statements) {
        ontModel.remove(statements);
    }

    /**
     * Deletes the Statement which is currently being selected.
     */
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        IStructuredSelection structured = getStructuredSelection(event);
        Object[] items = structured.toArray();
        List<Statement> statements = Lists.newArrayList();
        OntModel model = getActiveOntModel(event);

        if (model != null) {
            for (Object item : items) {
                if (item instanceof Statement) {
                    statements.add((Statement) item);
                }
            }
            if (statements.size() == 0) {
                return null;
            }
            String dialogTitle = "Delete Statement";
            String questionText = String.format(
                    "Are you sure you want to delete the selected %d statement%s",
                    statements.size(), (statements.size() > 1 ? "s" : ""));
            MessageDialog dialog = new MessageDialog(null, dialogTitle, null, questionText,
                    MessageDialog.QUESTION, new String[] { "Yes", "No" }, 1);
            if (dialog.open() == 0) {
                IModelProvider modelProvider = getActiveModelProvider(event);
                if (modelProvider != null) {
                    logger.debug(String.format("Removing %d statements from selected model",
                            statements.size()));
                    String transactionDescription = "Due to removal of " + statements.size()
                            + " statements";

                    ModelTransaction transaction = modelProvider
                            .createTransaction(transactionDescription);
                    createRemoveStatements(modelProvider.getOntModel(), statements);
                    modelProvider.commitTransaction(transaction);
                }
            }
        }
        return null;
    }

}
