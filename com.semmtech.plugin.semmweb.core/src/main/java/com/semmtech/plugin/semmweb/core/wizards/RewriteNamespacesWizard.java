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

package com.semmtech.plugin.semmweb.core.wizards;


import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import com.google.common.collect.Lists;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.actions.ModelFileAction;
import com.semmtech.plugin.semmweb.core.model.events.ModelActivatedEvent;
import com.semmtech.semantics.util.NamespaceRewriteRule;
import com.semmtech.semantics.util.NamespaceRewriter;


public class RewriteNamespacesWizard extends SemmtechWizard implements INewWizard {

    private static Logger logger = Logger.getLogger(RewriteNamespacesWizard.class);

    public static final String ID = "com.semmtech.plugin.semmweb.wizards.renameNamespaces";
    private static final String WINDOW_TITLE = "Rewrite Namespaces";

    protected final List<NamespaceRewriteRule> initialRules;
    protected final Action action;
    protected RewriteNamespacesWizardPage rewritePage;

    public RewriteNamespacesWizard(IFile file) {
        this(file, null);
    }

    public RewriteNamespacesWizard(IFile file, List<NamespaceRewriteRule> initialRules) {
        super();
        setWindowTitle(WINDOW_TITLE);
        setNeedsProgressMonitor(true);

        this.initialRules = initialRules;
        this.action = new Action(file);
    }

    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
    }

    @Override
    public void addPages() {
        setShellImage();
        rewritePage = new RewriteNamespacesWizardPage(action.getModel(), initialRules);
        addPage(rewritePage);
    }

    @Override
    public boolean performFinish() {
        List<NamespaceRewriteRule> rules = rewritePage.getRules();
        if (!rules.isEmpty()) {
            if (!rewritePage.isCheckUsage()) {
                action.run();
            }
            else {
                List<String> uris = Lists.newArrayList();
                for (NamespaceRewriteRule rule : rules) {
                    uris.add(rule.getFrom());
                }
                CheckNamespaceUsageOperation operation = new CheckNamespaceUsageOperation(
                        action.getModel(), uris);
                operation.addCheckNamespaceUsageListener(new ICheckNamespaceUsageListener() {

                    @Override
                    public boolean resumeOnError(String uri) {
                        return MessageDialog.openQuestion(
                                getShell(),
                                "Namespace Usage",
                                String.format(
                                        "Namespace <%s> is also used in at least one of the base models imports. Rewriting the uri of this namespace will potentially result in invalid models and/or loss of information!\n\nDo you wish continue rewriting namespaces?",
                                        uri));
                    }

                    @Override
                    public void checkCompleted() {
                        action.run();
                    }
                });
                try {
                    getContainer().run(false, false, operation);
                }
                catch (InterruptedException e) {
                    logger.error("Error occured during checking usage", e);
                }
                catch (InvocationTargetException e) {
                    logger.error("Error occured during checking usage", e);
                }
            }
        }
        return true;
    }

    private class Action extends ModelFileAction {
        public Action(IFile file) {
            super(file, true, true);
        }

        @Override
        public void run() {
            if (!satisfiesRunConditions()) {
                return;
            }

            obtainModel();
            if (model == null) {
                return;
            }

            doRewrite(model);
            if (modelProvider == null) {
                saveModel();
            }
        }

        protected void doRewrite(final OntModel ontModel) {
            if (modelProvider != null) {
                // Confirm the rewrite, which will also clear the undo/redo
                // history
                String dialogTitle = "Rewrite namespace URIs";
                String dialogMessage = "The model is about to rewrite the namespace URIs according to "
                        + "the rules specified. Reverting this action is not possible, and the current "
                        + "history of undo/redo operations will be cleared. Do you still wish to proceed?";
                boolean continueRewrite = MessageDialog.openConfirm(getShell(), dialogTitle,
                        dialogMessage);
                if (!continueRewrite) {
                    return;
                }

                // Clear the undo/redo history
                IWorkbench workbench = CorePlugin.getDefault().getWorkbench();
                IOperationHistory history = workbench.getOperationSupport().getOperationHistory();
                history.dispose(modelProvider.getUndoContext(), true, true, false);
            }

            // Perform the actual rewrite
            IRunnableWithProgress operation = new IRunnableWithProgress() {

                @Override
                public void run(IProgressMonitor monitor) throws InvocationTargetException,
                        InterruptedException {

                    try {
                        monitor.beginTask("Rewrite", 2);
                        monitor.subTask("Rewriting namespace URIs...");

                        List<NamespaceRewriteRule> rules = rewritePage.getRules();
                        Model baseModel = ontModel.getBaseModel();
                        NamespaceRewriter rewriter = new NamespaceRewriter();
                        for (NamespaceRewriteRule rule : rules) {
                            rewriter.addRule(rule);
                        }
                        rewriter.rewrite(baseModel);

                        monitor.worked(1);
                        monitor.subTask("Updating model...");

                        ontModel.notifyEvent(new ModelActivatedEvent(ontModel,
                                "Due to rewritten namespace URIs"));

                        monitor.worked(1);
                    }
                    finally {
                        monitor.done();
                    }
                }
            };
            try {
                getContainer().run(false, false, operation);
            }
            catch (InterruptedException e) {
                logger.error("Error occured during rewriting of namespaces", e);
            }
            catch (InvocationTargetException e) {
                logger.error("Error occured during rewriting of namespaces", e);
            }
        }

    }
}