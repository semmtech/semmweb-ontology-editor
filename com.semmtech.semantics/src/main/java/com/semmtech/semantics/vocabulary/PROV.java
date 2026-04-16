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


import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;


/**
 * The PROV Ontology [PROV-O] http://www.w3.org/TR/prov-o/
 * 
 * @author Mike Henrichs
 */
public class PROV {
    private static Model model = ModelFactory.createDefaultModel();

    public static final String NS = "http://www.w3.org/ns/prov#";

    public static final Resource NAMESPACE = model.createResource(NS);

    protected static final Resource resource(String local) {
        return ResourceFactory.createResource(NS + local);
    }

    protected static final Property property(String local) {
        return ResourceFactory.createProperty(NS + local);
    }

    /*
     * Starting Point classes and properties provide the basis for the rest of
     * the PROV Ontology and thus it is recommended that readers become
     * comfortable with how to apply these terms before continuing to the
     * remaining categories. These terms are used to create simple provenance
     * descriptions that can be elaborated using terms from other categories.
     * The classes and properties in this category are listed below and are
     * discussed in Section 3.1.
     */
    public static final Resource Activity = resource("Activity");
    public static final Resource Agent = resource("Agent");
    public static final Resource Entity = resource("Entity");

    public static final Property actedOnBehalfOf = property("actedOnBehalfOf");
    public static final Property endedAtTime = property("endedAtTime");
    public static final Property startedAtTime = property("startedAtTime");
    public static final Property used = property("used");
    public static final Property wasAssociatedWith = property("wasAssociatedWith");
    public static final Property wasAttributedTo = property("actedOnBehalfOf");
    public static final Property wasDerivedFrom = property("wasDerivedFrom");
    public static final Property wasGeneratedBy = property("wasGeneratedBy");
    public static final Property wasInformedBy = property("wasInformedBy");

    /*
     * Expanded classes and properties provide additional terms that can be used
     * to relate classes in the Starting Point category. The terms in this
     * category are applied in the same way as the terms in the Starting Point
     * category. Many of the terms in this category are subclasses or
     * subproperties of those in the Starting Point category. The classes and
     * properties in this category are listed below and are discussed in Section
     * 3.2
     */
    public static final Resource Bundle = resource("Bundle");
    public static final Resource Collection = resource("Collection");
    public static final Resource EmptyCollection = resource("EmptyCollection");
    public static final Resource Location = resource("Location");
    public static final Resource Organization = resource("Organization");
    public static final Resource Person = resource("Person");
    public static final Resource SoftwareAgent = resource("SoftwareAgent");

    public static final Property alternateOf = property("alternateOf");
    public static final Property atLocation = property("atLocation");
    public static final Property generated = property("generated");
    public static final Property generatedAtTime = property("generatedAtTime");
    public static final Property hadMember = property("hadMember");
    public static final Property hadPrimarySource = property("hadPrimarySource");
    public static final Property influenced = property("influenced");
    public static final Property invalidated = property("invalidated");
    public static final Property invalidatedAtTime = property("invalidatedAtTime");
    public static final Property specializationOf = property("specializationOf");
    public static final Property value = property("value");
    public static final Property wasEndedBy = property("wasEndedBy");
    public static final Property wasInvalidatedBy = property("wasInvalidatedBy");
    public static final Property wasQuotedFrom = property("wasQuotedFrom");
    public static final Property wasRevisionOf = property("wasRevisionOf");
    public static final Property wasStartedBy = property("wasStartedBy");

    /*
     * Qualified classes and properties provide elaborated information about
     * binary relations asserted using Starting Point and Expanded properties.
     * The terms in this category are applied using a pattern that differs from
     * those in the Starting Point and Expanded categories. While the relations
     * from the previous two categories are applied as direct, binary
     * assertions, the terms in this category are used to provide additional
     * attributes of the binary relations. The pattern used in this category
     * allows users to provide elaborate details that are not available using
     * only Starting Point and Expanded terms. The classes and properties in
     * this category are listed below and are discussed in Section 3.3.
     */
    // / TODO: Add the rest of the resources and properties!

}
