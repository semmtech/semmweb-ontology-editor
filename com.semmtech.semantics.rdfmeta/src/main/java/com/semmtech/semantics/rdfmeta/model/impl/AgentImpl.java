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

package com.semmtech.semantics.rdfmeta.model.impl;


import java.util.List;

import com.hp.hpl.jena.rdf.model.AnonId;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.impl.ModelCom;
import com.hp.hpl.jena.sparql.vocabulary.FOAF;
import com.hp.hpl.jena.vocabulary.RDF;
import com.semmtech.semantics.model.impl.BaseResourceImpl;
import com.semmtech.semantics.rdfmeta.model.Ontology;
import com.semmtech.semantics.rdfmeta.model.Agent;


// TODO: Auto-generated Javadoc
/**
 * An implementation of Agent.
 * 
 * @author Mike Henrichs
 * @since 1.0
 * 
 */
public abstract class AgentImpl extends BaseResourceImpl implements Agent {

    /** The affiliations. */
    private List<Agent> isMemberOf;

    /** The creates. */
    private List<Ontology> created;

    /**
     * Instantiates a new party impl.
     * 
     * @param uri
     *            the uri
     * @param model
     *            the model
     */
    protected AgentImpl(String uri, ModelCom model) {
        super(uri, model);
        addProperty(RDF.type, FOAF.Agent);
    }

    /**
     * Instantiates a new party impl.
     * 
     * @param id
     *            the id
     * @param model
     *            the model
     */
    protected AgentImpl(AnonId id, ModelCom model) {
        super(id, model);
        addProperty(RDF.type, FOAF.Agent);
    }

    /**
     * Instantiates a new party impl.
     * 
     * @param r
     *            the r
     * @param model
     *            the model
     */
    protected AgentImpl(Resource r, ModelCom model) {
        super(r, model);
        addProperty(RDF.type, FOAF.Agent);
    }

    /**
     * Instantiates a new party impl.
     * 
     * @param model
     *            the model
     */
    protected AgentImpl(ModelCom model) {
        super(model);
        addProperty(RDF.type, FOAF.Agent);
    }

    @Override
    public List<Agent> getIsMemberOf() {
        return isMemberOf;
    }

    @Override
    public List<Ontology> getCreated() {
        return created;
    }
}