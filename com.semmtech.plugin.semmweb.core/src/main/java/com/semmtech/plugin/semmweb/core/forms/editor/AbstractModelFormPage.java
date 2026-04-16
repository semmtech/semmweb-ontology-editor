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

package com.semmtech.plugin.semmweb.core.forms.editor;


import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.reasoner.Derivation;
import com.semmtech.plugin.semmweb.core.model.IModelProvider;
import com.semmtech.plugin.semmweb.core.model.ModelEventListener;
import com.semmtech.plugin.semmweb.core.model.ModelTransaction;
import com.semmtech.plugin.semmweb.core.model.events.ModelActivatedEvent;
import com.semmtech.plugin.semmweb.core.model.events.ModelChangedEvent;
import com.semmtech.plugin.semmweb.core.model.events.IModelEvent;
import com.semmtech.plugin.semmweb.core.model.events.ModelSavedEvent;
import com.semmtech.plugin.semmweb.core.model.events.NamespacePrefixChangedEvent;
import com.semmtech.plugin.semmweb.core.model.events.SubModelAddedEvent;
import com.semmtech.plugin.semmweb.core.model.events.SubModelRemovedEvent;
import com.semmtech.plugin.semmweb.core.viewers.ModelNodeLabelProvider;


/**
 * 
 * @author Mike Henrichs
 * 
 */
public abstract class AbstractModelFormPage extends FormPage implements IModelProvider,
        ModelEventListener {
    protected static final Logger logger = Logger.getLogger(AbstractModelFormPage.class);
    private boolean pageInitialized = false;
    private final List<ModelEventListener> queuedListeners;

    public AbstractModelFormPage(FormEditor editor, String id, String title) {
        super(editor, id, title);
        queuedListeners = Lists.newArrayList();
        Preconditions
                .checkArgument((editor instanceof IModelProvider),
                        "AbstractModelListener can only be instantiated with an editor which is a IModelProvider.");

    }

    @Override
    public void dispose() {
        super.dispose();
        removeModelEventListener(this);
        queuedListeners.clear();
    }

    /**
     * Sets the state of the model form page to initialized
     * 
     * @param initialized
     */
    public void setPageInitialized(boolean initialized) {
        this.pageInitialized = initialized;
        if (initialized) {
            for (ModelEventListener listener : queuedListeners) {
                ((IModelProvider) getEditor()).addModelEventListener(listener);
            }
            queuedListeners.clear();
            ((IModelProvider) getEditor()).addModelEventListener(this);
        }
    }

    @Override
    public void notifyEvent(IModelEvent event) {
    }

    @Override
    public void modelActivated(ModelActivatedEvent event) {
    }

    @Override
    public void modelChanged(ModelChangedEvent event) {
    }

    @Override
    public void subModelAdded(SubModelAddedEvent event) {
    }

    @Override
    public void subModelRemoved(SubModelRemovedEvent event) {
    }

    @Override
    public void namespacePrefixChanged(NamespacePrefixChangedEvent event) {
    }

    @Override
    public void modelSaved(ModelSavedEvent event) {
    }

    @Override
    public OntModel getOntModel() {
        return ((IModelProvider) getEditor()).getOntModel();
    }

    @Override
    public Model getBaseModel() {
        return ((IModelProvider) getEditor()).getBaseModel();
    }

    @Override
    public ModelNodeLabelProvider getLabelProvider() {
        return ((IModelProvider) getEditor()).getLabelProvider();
    }

    @Override
    public void setInferredModel(InfModel model) {
        ((IModelProvider) getEditor()).setInferredModel(model);
    }

    @Override
    public void clearInferredModel() {
        ((IModelProvider) getEditor()).clearInferredModel();
    }

    @Override
    public boolean isInferredModelOutdated() {
        return ((IModelProvider) getEditor()).isInferredModelOutdated();
    }

    @Override
    public Iterator<Derivation> getDerivation(Statement statement) {
        return ((IModelProvider) getEditor()).getDerivation(statement);
    }

    @Override
    public boolean isModelLoaded() {
        return ((IModelProvider) getEditor()).isModelLoaded();
    }

    @Override
    public void addModelEventListener(ModelEventListener listener) {
        if (pageInitialized) {
            ((IModelProvider) getEditor()).addModelEventListener(listener);
        }
        else {
            queuedListeners.add(listener);
        }
    }

    @Override
    public void removeModelEventListener(ModelEventListener listener) {
        queuedListeners.remove(listener);
        ((IModelProvider) getEditor()).removeModelEventListener(listener);
    }

    @Override
    public List<String> getSubModelURIs() {
        return ((IModelProvider) getEditor()).getSubModelURIs();
    }

    @Override
    public Model getSubModel(String uri) {
        return ((IModelProvider) getEditor()).getSubModel(uri);
    }

    @Override
    public String getBaseURI() {
        return ((IModelProvider) getEditor()).getBaseURI();
    }

    @Override
    public void setBaseURI(String baseUri) {
        ((IModelProvider) getEditor()).setBaseURI(baseUri);
    }

    @Override
    public String getModelURI() {
        return ((IModelProvider) getEditor()).getModelURI();
    }

    @Override
    public String getModelTitle() {
        return ((IModelProvider) getEditor()).getModelTitle();
    }

    @Override
    public IUndoContext getUndoContext() {
        return ((IModelProvider) getEditor()).getUndoContext();
    }

    @Override
    public void performUndoRedoOperation(AbstractOperation operation) {
        ((IModelProvider) getEditor()).performUndoRedoOperation(operation);
    }

    @Override
    public ModelTransaction createTransaction(String description) {
        return ((IModelProvider) getEditor()).createTransaction(description);
    }

    @Override
    public void abortTransaction(ModelTransaction transaction) {
        ((IModelProvider) getEditor()).abortTransaction(transaction);
    }

    @Override
    public boolean commitTransaction(ModelTransaction transaction) {
        return ((IModelProvider) getEditor()).commitTransaction(transaction);
    }
}
