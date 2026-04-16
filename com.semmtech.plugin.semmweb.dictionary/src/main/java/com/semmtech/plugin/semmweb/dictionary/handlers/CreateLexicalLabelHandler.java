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

package com.semmtech.plugin.semmweb.dictionary.handlers;


import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.model.IModelProvider;
import com.semmtech.plugin.semmweb.core.model.ModelTransaction;
import com.semmtech.plugin.semmweb.dictionary.dialog.LexicalLabelDialog;
import com.semmtech.semantics.vocabulary.SKOS;


public class CreateLexicalLabelHandler extends AbstractHandler {
    public static final String ID = "com.semmtech.plugin.semmweb.dictionary.commands.createLexicalLabel";

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        ISelection selection = HandlerUtil.getCurrentSelection(event);
        if (selection instanceof IStructuredSelection) {
            Object selected = ((IStructuredSelection) selection).getFirstElement();
            IModelProvider provider = CorePlugin.getDefault().getActiveModelProvider();

            if ((selected != null) && (selected instanceof Resource) && (provider != null)) {
                OntModel model = provider.getOntModel();
                Shell parentShell = HandlerUtil.getActiveShell(event);

                boolean done = false;
                Property property = SKOS.prefLabel;
                String label = null;
                String lang = null;

                while (!done) {
                    LexicalLabelDialog dialog = new LexicalLabelDialog(parentShell);
                    dialog.setLabel(label);
                    dialog.setLanguage(lang);
                    dialog.setProperty(property);

                    if (dialog.open() == Window.OK) {
                        property = dialog.getProperty();
                        label = dialog.getLabel();
                        lang = dialog.getLanguage();
                        Literal literal = null;
                        if (lang == null) {
                            literal = model.createLiteral(label);
                        }
                        else {
                            literal = model.createLiteral(label, lang);
                        }
                        Resource resource = (Resource) selected;

                        // Check if there is already a prefLabel statement for
                        // the selected language.
                        Statement previousStatement = null;
                        if (property.getURI().equals(SKOS.prefLabel.getURI())) {
                            for (Statement statement : model.listStatements(
                                    new SimpleSelector(resource, SKOS.prefLabel, (RDFNode) null))
                                    .toList()) {
                                if (statement.getObject().isLiteral()
                                        && statement.getLiteral().getLanguage().equals(lang)) {
                                    previousStatement = statement;
                                    break;
                                }
                            }

                        }

                        boolean replacePreviousStatement = false;
                        if (previousStatement != null) {
                            String dialogTitle = "Preferred Label Exists";
                            String errorMessage = "The resource already has a preferred label for the selected language '"
                                    + lang
                                    + "'. Do you wish to overwrite the preferred label for this language?";
                            String[] buttonLabels = new String[] { "Yes", "No", "Cancel" };

                            MessageDialog infoDialog = new MessageDialog(null, dialogTitle, null,
                                    errorMessage, MessageDialog.QUESTION_WITH_CANCEL, buttonLabels,
                                    1);
                            int result = infoDialog.open();
                            if (result == 0) {
                                replacePreviousStatement = true;
                            }
                            else if (result == 1) {
                                done = false;
                            }
                            else {
                                done = true;
                            }
                        }

                        if ((previousStatement == null) || (replacePreviousStatement)) {
                            String transactionDescription = "Model changed by LexicalLabelDialog";
                            ModelTransaction transaction = provider
                                    .createTransaction(transactionDescription);
                            if (previousStatement != null) {
                                model.remove(previousStatement);
                            }
                            model.add(model.createStatement((Resource) selected, property, literal));
                            provider.commitTransaction(transaction);
                            done = true;
                        }
                    }
                    else {
                        done = true;
                    }
                }
            }
        }
        return null;
    }

}
