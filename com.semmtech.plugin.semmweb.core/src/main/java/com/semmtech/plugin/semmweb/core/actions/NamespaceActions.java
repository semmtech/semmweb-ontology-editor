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


import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.semmtech.plugin.semmweb.core.dialog.NamespaceURIValidator;
import com.semmtech.plugin.semmweb.core.dialog.RewriteNamespaceRuleDialog;
import com.semmtech.plugin.semmweb.core.dialog.WorkspaceOntologySpecDialog;
import com.semmtech.plugin.semmweb.core.handlers.RewriteNamespacesHandler;
import com.semmtech.plugin.semmweb.core.model.IModelProvider;
import com.semmtech.plugin.semmweb.core.operations.EditNsPrefixOperation;
import com.semmtech.plugin.semmweb.core.operations.ModelOperation;
import com.semmtech.plugin.semmweb.core.operations.RemoveNsPrefixOperation;
import com.semmtech.plugin.semmweb.core.operations.SetNsPrefixOperation;
import com.semmtech.semantics.util.NamespaceMapping;
import com.semmtech.semantics.util.NamespaceRewriteRule;
import com.semmtech.ui.plugin.EclipseUIPlugin;


/**
 * 
 * @author Sander Stolk
 */
public class NamespaceActions {

    public static class AddPrefix extends ModelFileAction {
        private String namespace;

        /** namespace may be null */
        public AddPrefix(IFile file, String namespace) {
            super(file, false, false);
            this.namespace = namespace;
        }

        /** namespace may be null */
        public AddPrefix(IFile file, String namespace, boolean noHistory) {
            super(file, false, noHistory);
            this.namespace = namespace;
        }

        @Override
        protected ModelOperation getOperation(OntModel model) {
            return getAddPrefixOperation(model, namespace);
        }
    }

    public static class RemovePrefix extends ModelFileAction {
        final private String prefix;

        public RemovePrefix(IFile file, String prefix) {
            super(file, false, false);
            this.prefix = prefix;
        }

        public RemovePrefix(IFile file, String prefix, boolean noHistory) {
            super(file, false, noHistory);
            this.prefix = prefix;
        }

        @Override
        protected ModelOperation getOperation(OntModel model) {
            if (prefix == null) {
                return null;
            }
            return getRemovePrefixOperation(prefix);
        }
    }

    public static class EditPrefix extends ModelFileAction {
        final private String prefix;

        public EditPrefix(IFile file, String prefix) {
            super(file, false, false);
            this.prefix = prefix;
        }

        public EditPrefix(IFile file, String prefix, boolean noHistory) {
            super(file, false, noHistory);
            this.prefix = prefix;
        }

        @Override
        protected ModelOperation getOperation(OntModel model) {
            return getEditPrefixOperation(model, prefix);
        }
    }

    public static void addPrefix(IModelProvider modelProvider) {
        if (modelProvider != null) {
            OntModel model = modelProvider.getOntModel();
            if (model != null) {
                SetNsPrefixOperation operation = getAddPrefixOperation(model);
                if (operation != null) {
                    operation.setModel(modelProvider);
                    modelProvider.performUndoRedoOperation(operation);
                }
            }
        }
    }

    private static SetNsPrefixOperation getAddPrefixOperation(OntModel model) {
        return getAddPrefixOperation(model, null);
    }

    private static SetNsPrefixOperation getAddPrefixOperation(OntModel model, String namespace) {
        Shell shell = EclipseUIPlugin.getStandardDisplay().getActiveShell();
        WorkspaceOntologySpecDialog dialog = new WorkspaceOntologySpecDialog(shell, "Add Prefix",
                "Add a new namespace prefix to the base model.", new NamespaceURIValidator());
        dialog.setHideAltURL(true);
        if (!Strings.isNullOrEmpty(namespace)) {
            dialog.setPublicURI(namespace);
            dialog.setEnableURI(false);
        }

        if (dialog.open() == Window.OK) {
            String prefix = "";
            if (dialog.getPrefix() != null) {
                prefix = dialog.getPrefix();
            }
            String uri = dialog.getPublicURI();
            if (!Strings.isNullOrEmpty(uri)) {
                if ((model.getBaseModel().getNsPrefixURI(prefix) == null)
                        || confirmPrefixOverwrite(model, prefix)) {
                    return new SetNsPrefixOperation(prefix, uri);
                }
            }
        }
        return null;
    }

    public static void editPrefix(IModelProvider modelProvider, String prefix) {
        if (modelProvider != null) {
            OntModel model = modelProvider.getOntModel();
            if (model != null) {
                EditNsPrefixOperation operation = getEditPrefixOperation(model, prefix);
                if (operation != null) {
                    operation.setModel(modelProvider);
                    modelProvider.performUndoRedoOperation(operation);
                }
            }
        }
    }

    private static EditNsPrefixOperation getEditPrefixOperation(OntModel model, String prefix) {
        String namespace = model.getNsPrefixURI(prefix);

        Shell shell = EclipseUIPlugin.getStandardDisplay().getActiveShell();
        WorkspaceOntologySpecDialog dialog = new WorkspaceOntologySpecDialog(shell, "Edit Prefix",
                "Edit the selected namespace prefix within the base model.",
                new NamespaceURIValidator());
        dialog.setEnableURI(false);
        dialog.setHideAltURL(true);
        dialog.setHideOntologyOptions(true);
        dialog.setPrefix(prefix);
        dialog.setPublicURI(namespace);

        if (dialog.open() == Window.OK) {
            String newPrefix = "";
            if (dialog.getPrefix() != null) {
                newPrefix = dialog.getPrefix();
            }
            if ((model.getBaseModel().getNsPrefixURI(newPrefix) == null)
                    || ((prefix != null) && newPrefix.equals(prefix))
                    || confirmPrefixOverwrite(model, newPrefix)) {
                return new EditNsPrefixOperation(namespace, prefix, newPrefix);
            }
        }
        return null;
    }

    public static void removePrefix(IModelProvider modelProvider, String prefix) {
        if (modelProvider != null) {
            OntModel model = modelProvider.getOntModel();
            if (model != null) {
                RemoveNsPrefixOperation operation = getRemovePrefixOperation(prefix);
                if (operation != null) {
                    operation.setModel(modelProvider);
                    modelProvider.performUndoRedoOperation(operation);
                }
            }
        }
    }

    private static RemoveNsPrefixOperation getRemovePrefixOperation(String prefix) {
        return new RemoveNsPrefixOperation(prefix);
    }

    private static boolean confirmPrefixOverwrite(OntModel model, String prefix) {
        if (prefix == null) {
            return false;
        }
        Model baseModel = model.getBaseModel();
        String namespace = baseModel.getNsPrefixURI(prefix);
        if (namespace == null) {
            return true;
        }

        return MessageDialog
                .openConfirm(
                        EclipseUIPlugin.getStandardDisplay().getActiveShell(),
                        "Overwrite prefix",
                        String.format(
                                "The entered prefix \"%s:\" already exists for the following namespace URI:\n%s.\n\nAre you sure you want to overwrite the URI for this prefix?",
                                prefix, namespace));
    }

    public static class Rewrite extends Action {
        private final IFile file;
        private final String namespace;
        private final String prefix;

        /** prefix may be null */
        public Rewrite(IFile file, String namespace, String prefix) {
            this.file = file;
            this.namespace = namespace;
            this.prefix = prefix;
        }

        @Override
        public void run() {
            rewrite(file, namespace, prefix);
        }
    }

    public static void rewrite(IFile file, String uri, String prefix) {
        Shell shell = Display.getDefault().getActiveShell();
        RewriteNamespaceRuleDialog dialog = new RewriteNamespaceRuleDialog(
                shell,
                "Rewrite Rule",
                "Specify the URI to rewrite the selected namespace to. Afterwards, a wizard will open where it is possible to specify further namespace rewrite rules.");
        List<NamespaceMapping> namespaces = Lists.newArrayList();
        namespaces.add(new NamespaceMapping(uri, prefix));
        dialog.setNamespaces(namespaces);
        if (dialog.open() == Window.OK) {
            NamespaceRewriteRule rule = dialog.getRule();
            if (rule != null) {
                List<NamespaceRewriteRule> initialRules = Lists.newArrayList();
                initialRules.add(rule);
                RewriteNamespacesHandler.execute(file, initialRules);
            }
        }
    }

}
