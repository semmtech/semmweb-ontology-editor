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


import java.util.Date;
import java.util.List;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.AnonId;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.impl.ModelCom;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.OWL2;
import com.hp.hpl.jena.vocabulary.RDF;
import com.semmtech.semantics.model.impl.BaseResourceImpl;
import com.semmtech.semantics.rdfmeta.model.Ontology;
import com.semmtech.semantics.rdfmeta.model.Agent;
import com.semmtech.semantics.vocabulary.VAEM;
import com.semmtech.semantics.vocabulary.VANN;


/**
 * An implementation of Ontology.
 * 
 * @author Mike Henrichs
 * @since 1.0
 */
public class OntologyImpl extends BaseResourceImpl implements Ontology {

    /**
     * Instantiates a new ontology impl.
     * 
     * @param model
     *            the model
     */
    public OntologyImpl(ModelCom model) {
        super(model);
        addProperty(RDF.type, OWL2.Ontology);
    }

    /**
     * Instantiates a new ontology impl.
     * 
     * @param uri
     *            the uri
     * @param model
     *            the model
     */
    public OntologyImpl(String uri, ModelCom model) {
        super(uri, model);
        addProperty(RDF.type, OWL2.Ontology);
    }

    /**
     * Instantiates a new ontology impl.
     * 
     * @param id
     *            the id
     * @param model
     *            the model
     */
    public OntologyImpl(AnonId id, ModelCom model) {
        super(id, model);
        addProperty(RDF.type, OWL2.Ontology);
    }

    /**
     * Instantiates a new ontology impl.
     * 
     * @param r
     *            the r
     * @param model
     *            the model
     */
    public OntologyImpl(Resource r, ModelCom model) {
        super(r, model);
        addProperty(RDF.type, OWL2.Ontology);
    }

    @Override
    public void setVersion(String version) {
        setSingleProperty(OWL2.versionInfo, version, XSDDatatype.XSDstring);
    }

    @Override
    public String getVersion() {
        return getSingleProperty(OWL2.versionInfo);
    }

    @Override
    public void setResourceLocator(String locator) {
        setSingleProperty(VANN.preferredNamespaceUri, locator, XSDDatatype.XSDstring);
    }

    @Override
    public String getResourceLocator() {
        return getSingleProperty(VANN.preferredNamespaceUri);
    }

    @Override
    public List<Agent> getCreators() {
        return null;
    }

    @Override
    public void addCreator(Agent creator) {

    }

    @Override
    public List<Agent> getContributors() {
        return null;
    }

    @Override
    public void addContributor(Agent contributer) {

    }

    @Override
    public List<String> getNaturalLanguaes() {
        return null;
    }

    @Override
    public void addNaturalLanguage(String language) {

    }

    @Override
    public List<String> getKeywords() {
        return null;
    }

    @Override
    public void addKeyword(String keyword) {

    }

    @Override
    public List<String> getKeyClasses() {
        return null;
    }

    @Override
    public void addKeyClass(String keyClass) {

    }

    @Override
    public String getName() {
        return getSingleProperty(DCTerms.title);
    }

    @Override
    public void setName(String name) {
        setSingleProperty(DCTerms.title, name, XSDDatatype.XSDstring);
    }

    @Override
    public String getAcronym() {
        return getSingleProperty(VAEM.acronym);
    }

    @Override
    public void setAcronym(String acronym) {
        setSingleProperty(VAEM.acronym, acronym, XSDDatatype.XSDstring);
    }

    @Override
    public String getDescription() {
        return getSingleProperty(DCTerms.description);
    }

    @Override
    public void setDescription(String description) {
        setSingleProperty(DCTerms.description, description, XSDDatatype.XSDstring);
    }

    @Override
    public Date getCreationDate() {
        return null;
    }

    @Override
    public void setCreationDate(Date creationDate) {

    }

    @Override
    public String getDocumentation() {
        return null;
    }

    @Override
    public void setDocumentation(String documentation) {

    }

    @Override
    public String getReference() {
        return null;
    }

    @Override
    public void setReference(String reference) {

    }

    @Override
    public String getNotes() {
        return null;
    }

    @Override
    public void setNotes(String notes) {

    }

    @Override
    public String getStatus() {
        return null;
    }

    @Override
    public void setStatus(String status) {

    }

    @Override
    public Date getModificationDate() {
        return null;
    }

    @Override
    public void setModificationDate(Date modificationDate) {

    }

    @Override
    public void setMetaURI(String uri) {
        setSingleProperty(VANN.preferredNamespaceUri, uri, XSDDatatype.XSDstring);
    }

    @Override
    public String getMetaURI() {
        return getSingleProperty(VANN.preferredNamespaceUri);
    }

    protected void setSingleProperty(Property p, String value, XSDDatatype datatype) {
        if (hasProperty(p))
            removeAll(p);
        addProperty(p, value, datatype);
    }
}
