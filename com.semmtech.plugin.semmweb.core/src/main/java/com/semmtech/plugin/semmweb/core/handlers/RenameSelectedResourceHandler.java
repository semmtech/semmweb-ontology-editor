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


import java.util.Set;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.commands.Commands;
import com.semmtech.plugin.semmweb.core.dialog.URIInputDialog;
import com.semmtech.plugin.semmweb.core.dialog.URIValidator;
import com.semmtech.plugin.semmweb.core.dialog.UniqueResourceURIValidator;
import com.semmtech.plugin.semmweb.core.model.IModelProvider;
import com.semmtech.plugin.semmweb.core.model.ModelTransaction;
import com.semmtech.semantics.util.JenaUtil;


/**
 * This handler lets the user rename a selected resource's URI. Handler should
 * be enabled when a Resource is selected
 * 
 * @author Sander Stolk
 * @author Mike Henrichs
 */
public class RenameSelectedResourceHandler extends SelectedResourceHandler {
    public static final String ID = "com.semmtech.plugin.semmweb.core.commands.renameSelectedResource";

    /**
     * Deletes the Statement which is currently being selected.
     */
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        return renameResource(getSelectedResource(event));
    }

    public static Resource renameResource(final Resource resource) {
        IModelProvider modelProvider = CorePlugin.getDefault().getActiveModelProvider();
        if ((resource != null) && (modelProvider != null)
                && occursInBaseModel(resource, modelProvider)) {
            Model model = resource.getModel();
            String oldUri = null;
            String initialValue = null;
            if (resource.isAnon()) {
                // if (modelProvider.getOntModel().getNsPrefixURI("") != null) {
                // initialValue = ":";
                // }
            }
            else {
                String oldPrefix = model.getNsURIPrefix(resource.getNameSpace());
                String oldLocalName = resource.getLocalName();
                oldUri = resource.getURI();
                initialValue = ((oldPrefix.length() > 0) ? oldPrefix + ":" + oldLocalName
                        : oldLocalName);
            }
            String newUri = null;
            boolean done = false;

            while (!done) {
                String baseUri = modelProvider.getBaseURI();
                Shell parentShell = CorePlugin.getActiveWorkbenchShell();
                URIValidator validator = new URIValidator(model, baseUri);
                URIInputDialog dialog = new URIInputDialog(parentShell, "Set Resource URI",
                        "Set a new URI for the selected resource.", initialValue, model, validator);
                if (dialog.open() == Window.OK) {
                    String uri = dialog.getURI();
                    String shortForm = dialog.getValue();
                    Preconditions.checkNotNull(uri);

                    if ((oldUri != null) && uri.equals(oldUri)) {
                        done = true;
                    }
                    else {
                        UniqueResourceURIValidator existValidator = new UniqueResourceURIValidator(
                                model, baseUri);
                        String errorMessage = existValidator.isValid("<" + uri + ">");

                        if (errorMessage == null) {
                            done = true;
                            newUri = uri;
                        }
                        else {
                            String dialogTitle = "URI Exists";
                            MessageDialog infoDialog = new MessageDialog(null, dialogTitle, null,
                                    errorMessage, MessageDialog.CONFIRM, new String[] { "OK" }, 0);
                            infoDialog.open();

                            initialValue = shortForm;
                            done = false;
                        }
                    }
                }
                else {
                    done = true;
                }
            }
            if (newUri != null) {
                ModelTransaction transaction = modelProvider.createTransaction("Renamed Resource");
                renameResourceInBaseModel(resource, newUri, modelProvider);
                modelProvider.commitTransaction(transaction);
                return modelProvider.getOntModel().createResource(newUri);
            }
        }
        return null;
    }

    private static boolean occursInBaseModel(Resource resource, IModelProvider modelProvider) {
        return modelProvider.getBaseModel().containsResource(resource);
    }

    private static void renameResourceInBaseModel(Resource resource, String newUri,
            IModelProvider modelProvider) {
        if ((modelProvider == null) || (resource == null) || (Strings.isNullOrEmpty(newUri))) {
            return;
        }
        Property resourceAsProperty = JenaUtil.asProperty(resource, modelProvider.getOntModel());

        Model baseModel = modelProvider.getBaseModel();
        Set<Statement> statements = baseModel.listStatements(resource, null, (RDFNode) null)
                .toSet();
        statements.addAll(baseModel.listStatements(null, resourceAsProperty, (RDFNode) null)
                .toSet());
        statements.addAll(baseModel.listStatements(null, null, resource).toSet());

        OntModel ontModel = modelProvider.getOntModel();
        Resource newUriResource = ontModel.createResource(newUri);
        for (Statement oldStatement : statements) {
            Statement newStatement = replaceResourceInStatement(resource, newUriResource,
                    oldStatement, modelProvider);
            ontModel.remove(oldStatement);
            ontModel.add(newStatement);
        }
    }

    private static Statement replaceResourceInStatement(Resource oldResource, Resource newResource,
            Statement statement, IModelProvider modelProvider) {
        Resource subject = statement.getSubject();
        Property predicate = statement.getPredicate();
        RDFNode object = statement.getObject();

        if (subject.equals(oldResource)) {
            subject = newResource;
        }
        if (predicate.equals(oldResource)) {
            predicate = JenaUtil.asProperty(newResource, modelProvider.getOntModel());
        }
        if (object.equals(oldResource)) {
            object = newResource;
        }
        return modelProvider.getOntModel().createStatement(subject, predicate, object);
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

    public static boolean isEnabledFor(final Resource resource) {
        IModelProvider modelProvider = CorePlugin.getDefault().getActiveModelProvider();
        if ((modelProvider == null) || (resource == null)) {
            return false;
        }
        return (resource.isAnon() && occursInBaseModel(resource, modelProvider));
    }

    public static Action createAction(final String actionText, final Resource resource) {
        Action result = new Action(actionText) {
            @Override
            public void run() {
                renameResource(resource);
            }
        };
        return result;
    }
}
