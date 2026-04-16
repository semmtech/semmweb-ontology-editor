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


import java.util.Deque;

import org.apache.log4j.Logger;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.shared.JenaException;


public class TransactionManager {

    private static Logger logger = Logger.getLogger(TransactionManager.class);

    private final IModelProvider modelProvider;

    private Deque<ModelTransaction> transactions;

    public TransactionManager(IModelProvider modelProvider) {
        this.modelProvider = modelProvider;
        transactions = Lists.newLinkedList();
    }

    public ModelTransaction createTransaction(String description) {
        OntModel ontModel = modelProvider.getOntModel();

        if (transactions.isEmpty()) {
            try {
                ontModel.begin();
            }
            catch (JenaException ex) {
                ontModel.commit();
                transactions.clear();
                throw ex;
            }
        }

        ModelTransaction transaction = new ModelTransaction(ontModel, description, true);
        transactions.push(transaction);

        return transaction;
    }

    public void abortTransaction(ModelTransaction transaction) {
        Preconditions.checkNotNull(transaction, "ModelTransaction must be not null");

        Preconditions.checkArgument(transactions.contains(transaction),
                "The transaction that you are trying is not part of this session");

        abortTransactionAndRevertModel(transaction);
    }

    public boolean commitTransaction(ModelTransaction transaction) {
        Preconditions.checkNotNull(transaction, "ModelTransaction must be not null");
        ModelTransaction currentTransaction = transactions.pop();
        Preconditions.checkArgument(transaction == currentTransaction,
                "The transaction that you are trying to commit is not the expected one");

        transaction.dispose();
        if (transaction.hasError() == true) {
            logger.error(transaction.getErrorMessage(), transaction.getErrorException());
            abortTransactionAndRevertModel(transaction);
            return false;
        }

        if (transactions.isEmpty()) {
            modelProvider.getOntModel().commit();
        }

        return true;
    }

    public Deque<ModelTransaction> getPendingTransactions() {
        return transactions;
    }

    private void abortTransactionAndRevertModel(ModelTransaction transaction) {
        /*
         * Aborting transaction. Has problem that bnode IDs will be reset and
         * the entire editor will require a refresh even though the user cannot
         * discern a visible change to the model.
         */
        // try {
        // getOntModel().abort();
        // refreshInferredModels();
        // notifyEvent(); // needed to notify that bnode IDs have changed
        // }
        // catch (UnsupportedOperationException e) {
        // // It seems the model transaction had already been aborted
        // // earlier.
        // }

        clear();

        /*
         * Aborting transaction by committing changes and using the model
         * transaction to revert those changes. Doing so will ensure the bnode
         * IDs will not change.
         */
        modelProvider.getOntModel().commit();

        if (!transaction.getModelChanges().isEmpty()) {
            /*
             * Use the transaction to revert to the abort base model. If the
             * transaction was set to employ a shadow model, and the transaction
             * has not been disposed yet, this function will ensure our model
             * reverts to the exact state it had earlier. If these conditions
             * are not satisfied, however, the tracked model changes will be
             * reverted on the model directly. In that case, the function cannot
             * provide the guarantee that the revert is spot on.
             */
            transaction.revertBaseModel();
            transaction.dispose();
        }

    }

    public void clear() {
        transactions.clear();
    }

}
