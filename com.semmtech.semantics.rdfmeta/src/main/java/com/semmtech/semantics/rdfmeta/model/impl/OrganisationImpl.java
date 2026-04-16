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


import com.hp.hpl.jena.rdf.model.AnonId;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.impl.ModelCom;
import com.hp.hpl.jena.sparql.vocabulary.FOAF;
import com.hp.hpl.jena.vocabulary.RDF;
import com.semmtech.semantics.rdfmeta.model.Organisation;


/**
 * An implementation of Organisation.
 * 
 * @author Mike Henrichs
 * @since 1.0
 */
public class OrganisationImpl extends AgentImpl implements Organisation {

    /**
     * Instantiates a new organisation impl.
     * 
     * @param model
     *            the model
     */
    public OrganisationImpl(ModelCom model) {
        super(model);
        addProperty(RDF.type, FOAF.Organization);
    }

    /**
     * Instantiates a new organisation impl.
     * 
     * @param uri
     *            the uri
     * @param model
     *            the model
     */
    public OrganisationImpl(String uri, ModelCom model) {
        super(uri, model);
        addProperty(RDF.type, FOAF.Organization);
    }

    /**
     * Instantiates a new organisation impl.
     * 
     * @param id
     *            the id
     * @param model
     *            the model
     */
    public OrganisationImpl(AnonId id, ModelCom model) {
        super(id, model);
        addProperty(RDF.type, FOAF.Organization);
    }

    /**
     * Instantiates a new organisation impl.
     * 
     * @param r
     *            the r
     * @param model
     *            the model
     */
    public OrganisationImpl(Resource r, ModelCom model) {
        super(r, model);
        addProperty(RDF.type, FOAF.Organization);
    }

}
