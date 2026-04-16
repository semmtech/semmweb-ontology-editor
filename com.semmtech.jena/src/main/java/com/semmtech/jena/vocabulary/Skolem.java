package com.semmtech.jena.vocabulary;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

public class Skolem {
    public static final String NS = "urn:skolem:";

    public static String getURI() {
        return NS;
    }

    public static final Resource Skolemized = ResourceFactory.createResource(NS + "Skolemized");
    public static final Property iri = ResourceFactory.createProperty(NS + "iri");
    public static final Property bnodeId = ResourceFactory.createProperty(NS + "bnodeId");
}