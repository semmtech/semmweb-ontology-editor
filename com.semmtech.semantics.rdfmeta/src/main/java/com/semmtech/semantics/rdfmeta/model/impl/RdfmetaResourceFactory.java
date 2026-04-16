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


import java.util.Iterator;

import com.google.common.collect.ImmutableList;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.impl.ModelCom;
import com.hp.hpl.jena.sparql.vocabulary.FOAF;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.util.iterator.UniqueExtendedIterator;
import com.hp.hpl.jena.vocabulary.OWL2;
import com.hp.hpl.jena.vocabulary.RDF;
import com.semmtech.semantics.rdfmeta.model.Ontology;
import com.semmtech.semantics.rdfmeta.model.Organisation;
import com.semmtech.semantics.rdfmeta.model.Person;


// TODO: Auto-generated Javadoc
/**
 * The Class RdfmetaModel.
 */
public final class RdfmetaResourceFactory {

    /** The model. */
    private final ModelCom model;

    /**
     * Instantiates a new RDFmeta model.
     * 
     * @param model
     *            the model
     */
    private RdfmetaResourceFactory(ModelCom model) {
        this.model = model;
    }

    /**
     * Creates the person.
     * 
     * @return the person
     */
    public Person createPerson() {
        return new PersonImpl(model);
    }

    /**
     * Creates the person.
     * 
     * @param uri
     *            the uri
     * @return the person
     */
    public Person createPerson(String uri) {
        return new PersonImpl(uri, model);
    }

    /**
     * Answer a resource that represents a person in this model. If a resource
     * with the given URI exists in the model, and can be viewed as a Person,
     * return the Person, otherwise return null.
     * 
     * @param uri
     *            the uri
     * @return the person
     */
    public Person getPerson(String uri) {
        if (model.getResource(uri).hasProperty(RDF.type, FOAF.Person)) {
            return new PersonImpl(uri, model);
        }
        return null;
    }

    /**
     * List persons.
     * 
     * @return the extended iterator
     */
    public ExtendedIterator<Person> listPersons() {
        ImmutableList.Builder<Person> builder = ImmutableList.builder();
        Iterator<Resource> iter = model.listResourcesWithProperty(RDF.type, FOAF.Person);
        while (iter.hasNext()) {
            builder.add(new PersonImpl(iter.next(), model));
        }
        return UniqueExtendedIterator.create(builder.build().iterator());
    }

    /**
     * Creates the organisation.
     * 
     * @return the organisation
     */
    public Organisation createOrganisation() {
        return new OrganisationImpl(model);
    }

    /**
     * Creates the organisation.
     * 
     * @param uri
     *            the uri
     * @return the organisation
     */
    public Organisation createOrganisation(String uri) {
        return new OrganisationImpl(uri, model);
    }

    /**
     * Gets the organisation.
     * 
     * @param uri
     *            the uri
     * @return the organisation
     */
    public Organisation getOrganisation(String uri) {
        if (model.getResource(uri).hasProperty(RDF.type, FOAF.Organization)) {
            return new OrganisationImpl(uri, model);
        }
        return null;
    }

    /**
     * List organisations.
     * 
     * @return the extended iterator
     */
    public ExtendedIterator<Organisation> listOrganisations() {
        ImmutableList.Builder<Organisation> builder = ImmutableList.builder();
        Iterator<Resource> iter = model.listResourcesWithProperty(RDF.type, FOAF.Organization);
        while (iter.hasNext()) {
            builder.add(new OrganisationImpl(iter.next(), model));
        }
        return UniqueExtendedIterator.create(builder.build().iterator());
    }

    /**
     * Creates the ontology.
     * 
     * @return the ontology
     */
    public Ontology createOntology() {
        return new OntologyImpl(model);
    }

    /**
     * Creates the ontology.
     * 
     * @param uri
     *            the uri
     * @return the ontology
     */
    public Ontology createOntology(String uri) {
        return new OntologyImpl(uri, model);
    }

    /**
     * Gets the ontology.
     * 
     * @param uri
     *            the uri
     * @return the ontology
     */
    public Ontology getOntology(String uri) {
        if (model.getResource(uri).hasProperty(RDF.type, OWL2.Ontology)) {
            return new OntologyImpl(uri, model);
        }
        return null;
    }

    /**
     * List ontologies.
     * 
     * @return the extended iterator
     */
    public ExtendedIterator<Ontology> listOntologies() {
        ImmutableList.Builder<Ontology> builder = ImmutableList.builder();
        Iterator<Resource> iter = model.listResourcesWithProperty(RDF.type, OWL2.Ontology);
        while (iter.hasNext()) {
            builder.add(new OntologyImpl(iter.next(), model));
        }
        return UniqueExtendedIterator.create(builder.build().iterator());
    }

    /**
     * Creates the a model allowing the RDFmeta model interface.
     * 
     * @param model
     *            the model
     * @return the RDFmeta model
     */
    public static RdfmetaResourceFactory createMetaModel(Model model) {
        return new RdfmetaResourceFactory(new ModelCom(model.getGraph()));
    }
}
