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

package com.semmtech.semantics.vocabulary;


import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;


/**
 * @author Mike Henrichs
 * 
 */
public class GTF {
    private static Model model = ModelFactory.createDefaultModel();

    public static final String NS = "http://repo.semmweb.com/ns/semmtech/2012/03/30/gellish-table-format/";

    public static final Resource NAMESPACE = model.createResource(NS);

    protected static final Resource resource(String local) {
        return model.createResource(NS + local);
    }

    protected static final Property property(String local) {
        return model.createProperty(NS + local);

    }

    public static final Resource Unique = resource("Unique");
    public static final Resource Object = resource("Object");
    public static final Resource AtomicFact = resource("AtomicFact");
    public static final Resource MainFact = resource("MainFact");
    public static final Resource AuxiliaryFact = resource("AuxiliaryFact");

    public static final Property uid = property("uid");
    public static final Property name = property("name");
    public static final Property fullDefinition = property("fullDefinition");

    public static final Property author = property("author");
    public static final Property source = property("source");
    public static final Property startOfLife = property("startOfLife");
    public static final Property latestChange = property("latestChange");

    public static final Property sequenceKey = property("sequenceKey");

    public static final Property reality = property("reality");
    public static final Resource Reality = resource("Reality");
    public static final Resource Imaginary = resource("Imaginary");
    public static final Resource Real = resource("Real");

    public static final Property status = property("status");
    public static final Resource Status = resource("Status");
    public static final Resource Proposed = resource("Proposed");
    public static final Resource Issue = resource("Issue");
    public static final Resource Deleted = resource("Deleted");
    public static final Resource ProposedToBeDeleted = resource("ProposedToBeDeleted");
    public static final Resource Ignore = resource("Ignore");
    public static final Resource Agreed = resource("Agreed");
    public static final Resource Accepted = resource("Accepted");
    public static final Resource Replaced = resource("Replaced");

    public static final Property intention = property("intention");
    public static final Resource Intention = resource("Intention");
    public static final Resource True = resource("True");
    public static final Resource Request = resource("Request");
    public static final Resource Question = resource("Question");
    public static final Resource Confirmation = resource("Confirmation");
    public static final Resource Promise = resource("Promise");
    public static final Resource Declination = resource("Declination");
    public static final Resource Statement = resource("Statement");
    public static final Resource Denial = resource("Denial");
    public static final Resource Probability = resource("Probability");
    public static final Resource Acceptance = resource("Acceptance");

    /**
     * The GTF vocabulary, expressed for the SPI layer in terms of .graph Nodes.
     */
    public static class Nodes {
        public static final Node uid = GTF.uid.asNode();
        public static final Node name = GTF.name.asNode();
        public static final Node fullDefinition = GTF.fullDefinition.asNode();
    }

    /**
     * returns the URI for this schema
     * 
     * @return the URI for this schema
     */
    public static String getURI() {
        return NS;
    }
}
