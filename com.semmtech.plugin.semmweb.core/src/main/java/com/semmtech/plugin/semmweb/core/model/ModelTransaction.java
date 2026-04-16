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

package com.semmtech.plugin.semmweb.core.model;


import java.util.List;

import com.google.common.base.Preconditions;
import com.hp.hpl.jena.graph.GraphEvents;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelChangedListener;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.semmtech.plugin.semmweb.core.undo.ModelTransactionOperation;


public class ModelTransaction {
    private ModelChangesCollection modelChanges;
    private ModelChangedListener listener;
    private OntModel model;
    private Model shadowBaseModel;
    private String description;
    private Exception errorException;
    private String errorMessage;

    /**
     * Shorthand version of the constructor
     * {@link #ModelTransaction(OntModel, String, boolean)}. Using this
     * constructor, the enableShadowModel parameter is set to false.
     * 
     * @param model
     *            The model that this transaction will track changes on.
     * @param description
     *            The description of the transaction. May be left null.
     */
    public ModelTransaction(OntModel model, String description) {
        this(model, description, false);
    }

    /**
     * Creates a new ModelTransaction that listen <code>model</code> to track
     * added or removed statements. The method {@link #dispose()} should be
     * called when model changes should no longer be tracked by this
     * transaction.
     * 
     * @param model
     *            The model that this transaction will track changes on.
     * @param description
     *            The description of the transaction. May be left null.
     * @param enableShadowModel
     *            Whether to maintain a local copy of the baseModel of
     *            <code>model</code>. Doing so will allow the transaction to
     *            neglect added statements if they were already present in the
     *            model. Similarly, it will allow the transaction to neglect
     *            removed statements if no such statement existed in the model
     *            in the first place. On top of that, it will allow tracking of
     *            removeAll() calls on the model. If this parameter is set to
     *            false, no local copy is maintained and the aforementioned
     *            benefits are forfeit.
     */
    public ModelTransaction(OntModel model, String description, final boolean enableShadowModel) {
        Preconditions.checkNotNull(model);

        this.description = description;
        this.model = model;
        this.modelChanges = new ModelChangesCollection();
        if (enableShadowModel == true) {
            this.shadowBaseModel = ModelFactory.createDefaultModel();
            this.shadowBaseModel.add(model.getBaseModel());
        }

        this.listener = new ModelChangedListener() {
            @Override
            public void addedStatement(Statement s) {
                if (enableShadowModel == false) {
                    modelChanges.add(s);
                }
                else {
                    if (!shadowBaseModel.contains(s)) {
                        modelChanges.add(s);
                        shadowBaseModel.add(s);
                    }
                }
            }

            @Override
            public void removedStatement(Statement s) {
                if (enableShadowModel == false) {
                    modelChanges.remove(s);
                }
                else {
                    if (shadowBaseModel.contains(s)) {
                        modelChanges.remove(s);
                        shadowBaseModel.remove(s);
                    }
                }
            }

            @Override
            public void addedStatements(List<Statement> statements) {
                for (Statement s : statements) {
                    addedStatement(s);
                }
            }

            @Override
            public void removedStatements(List<Statement> statements) {
                for (Statement s : statements) {
                    removedStatement(s);
                }
            }

            @Override
            public void addedStatements(Statement[] statements) {
                for (Statement s : statements) {
                    addedStatement(s);
                }
            }

            @Override
            public void removedStatements(Statement[] statements) {
                for (Statement s : statements) {
                    removedStatement(s);
                }
            }

            @Override
            public void addedStatements(StmtIterator statements) {
                while (statements.hasNext()) {
                    addedStatement(statements.next());
                }
            }

            @Override
            public void removedStatements(StmtIterator statements) {
                while (statements.hasNext()) {
                    removedStatement(statements.next());
                }
            }

            @Override
            public void addedStatements(Model m) {
                addedStatements(m.listStatements());
            }

            @Override
            public void removedStatements(Model m) {
                removedStatements(m.listStatements());
            }

            @Override
            public void notifyEvent(Model m, Object event) {
                if (enableShadowModel) {
                    if (event instanceof GraphEvents) {
                        GraphEvents graphEvents = (GraphEvents) event;
                        if (graphEvents.getTitle().equals("removeAll")) {
                            List<Statement> statementList = shadowBaseModel.listStatements()
                                    .toList();
                            removedStatements(statementList);
                        }
                    }
                }
            }
        };

        model.register(listener);
    }

    /**
     * Should be called when model changes should no longer be tracked by this
     * transaction. In effect, this function removes this transaction as
     * listener from the model and closes its shadow model (if such a model was
     * used).
     */
    public void dispose() {
        if ((model != null) && (listener != null)) {
            model.unregister(listener);
            listener = null;
        }
        if (shadowBaseModel != null) {
            shadowBaseModel.close();
            shadowBaseModel = null;
        }
    }

    /**
     * Returns <code>true</code> if no statements were added to the tracked
     * model and none were removed from the model.
     */
    public boolean isEmpty() {
        return modelChanges.isEmpty();
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setError(String message) {
        setError(null, message);
    }

    public void setError(Exception e) {
        setError(e, null);
    }

    public void setError(Exception e, String message) {
        errorException = e;
        errorMessage = message;
    }

    public boolean hasError() {
        return (errorException != null || errorMessage != null);
    }

    public Exception getErrorException() {
        return errorException;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Returns the list of model changes that this transaction tracked. This
     * returned list may be empty but will not be null.
     */
    public ModelChangesCollection getModelChanges() {
        return modelChanges;
    }

    /**
     * Attempts to revert the base model this transaction was first initiated
     * with to its state at that point in time. If this transaction was set to
     * employ a shadow model (see the <code>enableShadowModel</code> parameter
     * in the constructor) and this transaction has not been disposed yet, this
     * function will ensure the base model is set to to the exact state the
     * initial base model had. If these conditions are not satisfied, however,
     * the tracked model changes will be reverted on the model directly without
     * the aid of a shadow model. In that case, the function cannot provide the
     * guarantee that the revert is spot on. Any model changes tracked so far
     * will be cleared.
     */
    public void revertBaseModel() {
        Model baseModel = model.getBaseModel();

        // Temporarily unregister tracking changes
        if (listener != null) {
            model.unregister(listener);
        }

        if (shadowBaseModel != null) {
            // Undo changes on shadow model
            ModelTransactionOperation.undo(shadowBaseModel, modelChanges, true, null);

            // Overwrite model with shadow model
            baseModel.removeAll();
            baseModel.add(shadowBaseModel);
        }
        else {
            ModelTransactionOperation.undo(baseModel, modelChanges, true, null);
        }

        // Register tracking changes again after overwriting the model
        if (listener != null) {
            model.register(listener);
        }

        modelChanges = new ModelChangesCollection();
    }

    @Override
    public String toString() {
        String result = "Begin of transaction '" + description + "'\n";
        result += modelChanges.toString();
        result += "End of transaction '" + description + "'\n";
        return result;
    }
}
