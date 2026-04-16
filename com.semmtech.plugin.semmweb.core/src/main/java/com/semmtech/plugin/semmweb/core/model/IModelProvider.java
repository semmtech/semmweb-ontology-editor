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


import java.util.Iterator;
import java.util.List;

import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.commands.operations.IUndoContext;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.reasoner.Derivation;
import com.semmtech.plugin.semmweb.core.viewers.ModelNodeLabelProvider;


/**
 * IModelProvider is the provider of an underlying Model, opened through an
 * SemanticModelFormEditor for instance.
 * 
 * @author Mike Henrichs
 * 
 */
public interface IModelProvider {
    public final static String INFERRED_SUBMODEL_URI = "inferred";

    public OntModel getOntModel();

    public Model getBaseModel();

    public ModelNodeLabelProvider getLabelProvider();

    /**
     * Returns the model URI (filename or URI) from which the model is read. Eg.
     * the model read from a file may result in the model URI
     * "file:/C:/Files/Folder/Example/volkswagen.ttl". In combination with a
     * resource URI a resource can be uniquely identified within the SEMMweb
     * applications.
     * 
     * @return
     */
    public String getModelURI();

    public String getBaseURI();

    public void setBaseURI(String baseUri);

    public String getModelTitle();

    public void setInferredModel(InfModel model);

    public void clearInferredModel();

    /**
     * Returns true if no inferred model has been set or if it might be outdated
     * due to model changes.
     */
    public boolean isInferredModelOutdated();

    /**
     * Returns an iterator on derivations if available for the given statement.
     * Returns null otherwise.
     */
    public Iterator<Derivation> getDerivation(Statement statement);

    /**
     * Adds the listener to this model provider. the model provider will
     * determine when ModelChanged events need to be propagated to its
     * listeners. This way the provider can filter out unnecessary events
     * triggered during the reading or importing of a model.
     * 
     * @param listener
     */
    public void addModelEventListener(ModelEventListener listener);

    public void removeModelEventListener(ModelEventListener listener);

    public List<String> getSubModelURIs();

    public Model getSubModel(String uri);

    public IUndoContext getUndoContext();

    public void performUndoRedoOperation(AbstractOperation operation);

    public ModelTransaction createTransaction(String description);

    public void abortTransaction(ModelTransaction transaction);

    public boolean commitTransaction(ModelTransaction transaction);

    public boolean isModelLoaded();
}