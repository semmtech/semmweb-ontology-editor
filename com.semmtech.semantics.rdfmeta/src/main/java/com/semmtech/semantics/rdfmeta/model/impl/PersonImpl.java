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

import com.google.common.collect.ImmutableList;
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.AnonId;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.impl.ModelCom;
import com.hp.hpl.jena.sparql.vocabulary.FOAF;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.vocabulary.RDF;
import com.semmtech.semantics.rdfmeta.model.Person;


/**
 * An implementation of Person.
 * 
 * @author Mike Henrichs
 * @since 1.0
 */
public class PersonImpl extends AgentImpl implements Person {

    /**
     * Instantiates a new person impl.
     * 
     * @param model
     *            the model
     */
    public PersonImpl(ModelCom model) {
        super(model);
        addProperty(RDF.type, FOAF.Person);
    }

    /**
     * Instantiates a new person impl.
     * 
     * @param uri
     *            the uri
     * @param model
     *            the model
     */
    public PersonImpl(String uri, ModelCom model) {
        super(uri, model);
        addProperty(RDF.type, FOAF.Person);
    }

    /**
     * Instantiates a new person impl.
     * 
     * @param id
     *            the id
     * @param model
     *            the model
     */
    public PersonImpl(AnonId id, ModelCom model) {
        super(id, model);
        addProperty(RDF.type, FOAF.Person);
    }

    /**
     * Instantiates a new person impl.
     * 
     * @param r
     *            the r
     * @param model
     *            the model
     */
    public PersonImpl(Resource r, ModelCom model) {
        super(r, model);
        addProperty(RDF.type, FOAF.Person);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.semmtech.semantics.rdfmeta.model.Person#getLastName()
     */
    public String getLastName() {
        return getSingleProperty(FOAF.surname);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.semmtech.semantics.rdfmeta.model.Person#setLastName(java.lang.String)
     */
    public void setLastName(String lastName) {
        setSingleProperty(FOAF.surname, lastName, XSDDatatype.XSDstring);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.semmtech.semantics.rdfmeta.model.Person#getFirstNames()
     */
    public List<String> getFirstNames() {
        ImmutableList.Builder<String> builder = ImmutableList.builder();
        for (ExtendedIterator<Statement> iter = listProperties(FOAF.firstName); iter.hasNext();) {
            Statement stmt = iter.next();
            builder.add(stmt.getString());
        }
        return builder.build();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.semmtech.semantics.rdfmeta.model.Person#addFirstName(java.lang.String
     * )
     */
    public void addFirstName(String firstName) {
        addProperty(FOAF.firstName, firstName, XSDDatatype.XSDstring);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.semmtech.semantics.rdfmeta.model.Person#getEmails()
     */
    public List<String> getEmails() {
        ImmutableList.Builder<String> builder = ImmutableList.builder();
        for (ExtendedIterator<Statement> iter = listProperties(FOAF.mbox); iter.hasNext();) {
            Statement stmt = iter.next();
            builder.add(stmt.getString());
        }
        return builder.build();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.semmtech.semantics.rdfmeta.model.Person#addEmail(java.lang.String)
     */
    public void addEmail(String email) {
        addProperty(FOAF.mbox, email, XSDDatatype.XSDstring);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.semmtech.semantics.rdfmeta.model.Person#getPhoneNumbers()
     */
    @Override
    public List<String> getPhoneNumbers() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.semmtech.semantics.rdfmeta.model.Person#addPhoneNumber(java.lang.
     * String)
     */
    @Override
    public void addPhoneNumber(String phone) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.semmtech.semantics.rdfmeta.model.Person#getFaxNumbers()
     */
    @Override
    public List<String> getFaxNumbers() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.semmtech.semantics.rdfmeta.model.Person#addFaxNumber(java.lang.String
     * )
     */
    @Override
    public void addFaxNumber(String fax) {
        // TODO Auto-generated method stub

    }

    protected void setSingleProperty(Property p, String value, XSDDatatype datatype) {
        if (hasProperty(p))
            removeAll(p);
        addProperty(p, value, datatype);
    }
}
