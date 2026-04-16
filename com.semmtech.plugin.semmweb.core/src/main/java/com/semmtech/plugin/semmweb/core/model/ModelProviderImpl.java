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


import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.commands.operations.IUndoContext;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.hp.hpl.jena.graph.GraphEvents;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelChangedListener;
import com.hp.hpl.jena.rdf.model.ModelMaker;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.reasoner.Derivation;
import com.semmtech.plugin.semmweb.core.ModelProviderRegistry;
import com.semmtech.plugin.semmweb.core.model.events.IModelEvent;
import com.semmtech.plugin.semmweb.core.model.events.ModelActivatedEvent;
import com.semmtech.plugin.semmweb.core.model.events.ModelChangedEvent;
import com.semmtech.plugin.semmweb.core.model.events.ModelEvent;
import com.semmtech.plugin.semmweb.core.model.events.ModelSavedEvent;
import com.semmtech.plugin.semmweb.core.model.events.NamespacePrefixChangedEvent;
import com.semmtech.plugin.semmweb.core.model.events.SubModelAddedEvent;
import com.semmtech.plugin.semmweb.core.model.events.SubModelRemovedEvent;
import com.semmtech.plugin.semmweb.core.viewers.ModelNodeLabelProvider;
import com.semmtech.semantics.model.ExtendedModelFactory;


/**
 * Implementation of the IModelProvider that is useful when you need to test
 * part of the program that need the IModelProvider
 * 
 * @author Simone Rondelli
 * 
 */
public class ModelProviderImpl implements IModelProvider, ModelChangedListener {

    private static Logger logger = Logger.getLogger(ModelProviderImpl.class);

    private final List<ModelEventListener> modelEventListeners;
    private final List<ModelChangedListener> modelChangedListeners;

    /**
     * This value is set to false when the model starts to be read. And is set
     * to true when the model is fully loaded with all his dependencies.
     */
    private volatile boolean modelLoaded;

    protected OntModel ontModel;
    protected String baseUri;

    private final TransactionManager transactionManager;

    private final List<Statement> addedStatements;
    private final List<Statement> removedStatements;

    public ModelProviderImpl(OntModel ontModel, String baseUri) {
        transactionManager = new TransactionManager(this);
        addedStatements = Lists.newArrayList();
        removedStatements = Lists.newArrayList();

        modelEventListeners = Lists.newArrayList();
        modelChangedListeners = Lists.newArrayList();

        startReadingModel(false);
        this.ontModel = ontModel;
        this.baseUri = baseUri;
        finishedReadingModel(ontModel);
    }

    /**
     * Creates a fresh model using a file {@link ModelMaker} to enable the
     * transactions in the generated model. The back file is created in the OS
     * temp dir
     * 
     * @return
     */
    protected OntModel createFreshModel() {
        try {
            String makerPath = File.createTempFile("model", ".ttl").getAbsolutePath();
            ModelMaker maker = ExtendedModelFactory.createFileModelMaker(makerPath);
            Model emptyModel = maker.createFreshModel();
            return ExtendedModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, emptyModel);
        }
        catch (IOException e) {
            throw new IllegalStateException("Impossible to create temp files");
        }
    }

    protected void startReadingModel(boolean preserveOld) {
        notifyEvent(new ModelEvent("startdReadingModel", preserveOld ? ontModel : null));
    }

    protected void finishedReadingModel(OntModel model) {
        notifyEvent(new ModelEvent("finishedReadingModel", model));
        ModelProviderRegistry.register(getModelURI(), this);
    }

    @Override
    public OntModel getOntModel() {
        return ontModel;
    }

    @Override
    public Model getBaseModel() {
        return ontModel.getBaseModel();
    }

    @Override
    public ModelNodeLabelProvider getLabelProvider() {
        return null;
    }

    @Override
    public void setBaseURI(String baseUri) {
        throw new UnsupportedOperationException("setBaseURI(String baseUri)");
    }

    @Override
    public void setInferredModel(InfModel model) {
        throw new UnsupportedOperationException("setInferredModel(InfModel model)");
    }

    @Override
    public void clearInferredModel() {
        throw new UnsupportedOperationException("clearInferredModel()");
    }

    @Override
    public boolean isInferredModelOutdated() {
        throw new UnsupportedOperationException("isInferredModelOutdated()");
    }

    @Override
    public Iterator<Derivation> getDerivation(Statement statement) {
        return null;
    }

    @Override
    public void addModelEventListener(ModelEventListener listener) {
        modelEventListeners.add(listener);
    }

    @Override
    public void removeModelEventListener(ModelEventListener listener) {
        modelEventListeners.remove(listener);
    }

    public void registerModelChangedListener(ModelChangedListener listener) {
        modelChangedListeners.add(listener);

        if (isModelLoaded()) {
            ontModel.register(listener);
        }

    }

    public void unregisterModelChangedListener(ModelChangedListener listener) {
        modelChangedListeners.add(listener);

        if (isModelLoaded()) {
            ontModel.unregister(listener);
        }
    }

    @Override
    public List<String> getSubModelURIs() {
        return Lists.newArrayList();
    }

    @Override
    public Model getSubModel(String uri) {
        return null;
    }

    @Override
    public IUndoContext getUndoContext() {
        return null;
    }

    @Override
    public void performUndoRedoOperation(AbstractOperation operation) {

    }

    @Override
    public ModelTransaction createTransaction(String description) {
        return transactionManager.createTransaction(description);
    }

    @Override
    public void abortTransaction(ModelTransaction transaction) {
        transactionManager.abortTransaction(transaction);
    }

    @Override
    public boolean commitTransaction(ModelTransaction transaction) {
        return transactionManager.commitTransaction(transaction);
    }

    @Override
    public boolean isModelLoaded() {
        return (ontModel == null);
    }

    @Override
    public void addedStatement(Statement s) {
        addedStatements.add(s);
    }

    @Override
    public void addedStatements(Statement[] statements) {
        addedStatements.addAll(Arrays.asList(statements));
    }

    @Override
    public void addedStatements(List<Statement> statements) {
        addedStatements.addAll(statements);
    }

    @Override
    public void addedStatements(StmtIterator statements) {
        addedStatements.addAll(statements.toList());
    }

    @Override
    public void addedStatements(Model m) {
        addedStatements.addAll(m.listStatements().toList());
    }

    @Override
    public void removedStatement(Statement s) {
        removedStatements.add(s);
    }

    @Override
    public void removedStatements(Statement[] statements) {
        removedStatements.addAll(Arrays.asList(statements));
    }

    @Override
    public void removedStatements(List<Statement> statements) {
        removedStatements.addAll(statements);
    }

    @Override
    public void removedStatements(StmtIterator statements) {
        removedStatements.addAll(statements.toList());
    }

    @Override
    public void removedStatements(Model m) {
        removedStatements.addAll(m.listStatements().toList());
    }

    public List<Statement> getAddedStatements() {
        return Lists.newArrayList(addedStatements);
    }

    public List<Statement> getRemovedStatements() {
        return Lists.newArrayList(removedStatements);
    }

    public void clearStatementsHistory() {
        addedStatements.clear();
        removedStatements.clear();
    }

    @Override
    public void notifyEvent(Model m, Object e) {
        if (e instanceof GraphEvents) {
            GraphEvents graphEvents = (GraphEvents) e;
            logger.debug("notifyEvent on model with GraphEvent " + graphEvents.getTitle());
            if (graphEvents.getTitle().equals("remove")) {
            }
            else if (graphEvents.getTitle().equals("removeAll")) {
            }
            else if (graphEvents.getTitle().equals("startRead")) {
            }
            else if (graphEvents.getTitle().equals("finishRead")) {
            }
        }
        else if (e instanceof ModelSavedEvent) {
            ModelSavedEvent event = (ModelSavedEvent) e;
            logger.debug("notifyEvent was called due to ModelSaved: " + event.getTitle());

            notifyEvent(event);
        }

        if (!modelLoaded) {
            return;
        }

        if (e instanceof ModelActivatedEvent) {
            ModelActivatedEvent event = (ModelActivatedEvent) e;

            logger.debug("notifyEvent was called due to ModelActivated: " + event.getTitle());

            notifyEvent(event);
        }
        else if (e instanceof SubModelAddedEvent) {
            SubModelAddedEvent event = (SubModelAddedEvent) e;

            logger.debug("notifyEvent was called due to SubModelAdded: " + event.getTitle());

            notifyEvent(event);
        }
        else if (e instanceof SubModelRemovedEvent) {
            SubModelRemovedEvent event = (SubModelRemovedEvent) e;

            notifyEvent(event);
        }
        else if (e instanceof ModelChangedEvent) {
            ModelChangedEvent event = (ModelChangedEvent) e;

            logger.debug("notifyEvent was called due to ModelChanged: " + event.getTitle());

            notifyEvent(event);
        }
        else if (e instanceof NamespacePrefixChangedEvent) {
            NamespacePrefixChangedEvent event = (NamespacePrefixChangedEvent) e;

            logger.debug("notifyEvent was called due to NamespacePrefixChanged: "
                    + event.getTitle());

            notifyEvent(event);
        }
    }

    private void notifyEvent(IModelEvent event) {
        // Create a copy to prevent a concurrency problem, due to the fact
        // that listeners may create new listeners
        LinkedHashSet<ModelEventListener> toNotify = Sets.newLinkedHashSet(modelEventListeners);

        if ("finishedReadingModel".equals(event.getTitle())) {
            ontModel = (OntModel) event.getModel();
            ontModel.register(this);

            for (ModelChangedListener lst : modelChangedListeners) {
                ontModel.register(lst);
            }

            ModelActivatedEvent activatedEvent = new ModelActivatedEvent(ontModel,
                    "finishedReadingModel");

            for (ModelEventListener listener : toNotify) {
                performNotify(listener, activatedEvent);
            }
            modelLoaded = true;
            return;
        }
        else if ("startReadingModel".equals(event.getTitle())) {
            ontModel = (OntModel) event.getModel();
            modelLoaded = false;
            return;
        }

        if (event instanceof ModelActivatedEvent || event instanceof ModelChangedEvent) {
        }

        for (ModelEventListener listener : toNotify) {
            performNotify(listener, event);
        }
    }

    private void performNotify(ModelEventListener listener, IModelEvent event) {
        if (event instanceof ModelActivatedEvent) {
            listener.modelActivated((ModelActivatedEvent) event);
        }
        else if (event instanceof ModelChangedEvent) {
            listener.modelChanged((ModelChangedEvent) event);
        }
        else if (event instanceof SubModelAddedEvent) {
            listener.subModelAdded((SubModelAddedEvent) event);
        }
        else if (event instanceof SubModelRemovedEvent) {
            listener.subModelRemoved((SubModelRemovedEvent) event);
        }
        else if (event instanceof NamespacePrefixChangedEvent) {
            listener.namespacePrefixChanged((NamespacePrefixChangedEvent) event);
        }
        else if (event instanceof ModelSavedEvent) {
            listener.modelSaved((ModelSavedEvent) event);
        }
        listener.notifyEvent(event);
    }

    @Override
    public String getModelURI() {
        return baseUri;
    }

    @Override
    public String getBaseURI() {
        return baseUri;
    }

    @Override
    public String getModelTitle() {
        return baseUri;
    }

}