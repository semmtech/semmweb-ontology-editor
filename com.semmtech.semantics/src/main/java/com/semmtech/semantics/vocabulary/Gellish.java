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
import com.hp.hpl.jena.rdf.model.ResourceFactory;


/**
 * @author Mike Henrichs
 * 
 */
public class Gellish {
    private static Model model = ModelFactory.createDefaultModel();

    public static final String NS = "http://repo.semmweb.com/ns/semmtech/2012/04/06/gellish/";

    public static final Resource NAMESPACE = model.createResource(NS);

    protected static final Resource resource(String local) {
        return ResourceFactory.createResource(NS + local);
    }

    protected static final Property property(String local) {
        return ResourceFactory.createProperty(NS + local);
    }

    /**
     * The most generic concept.
     */
    public static final Resource Anything = resource("Anything");
    /**
     * Context (5650)
     */
    public static final Resource Context = resource("Context");
    /**
     * Concept is an anything that is a commonality of things.
     */
    public static final Resource Concept = resource("Concept");
    /**
     * Is an anything that is not a product of a mind and exists in the real
     * world.
     */
    public static final Resource RealObject = resource("RealObject");
    /**
     * Is a concept that is a product of a mind.
     */
    public static final Resource AbstractObject = resource("AbstractObject");
    /**
     * Is an abstract object that is an understanding of a commonality of
     * things. The things that have the commonality are therefore called members
     * of the class. It can be used as a criterion to divide things into those
     * which are members of the class and those which are not.
     */
    public static final Resource Class = resource("Class");
    public static final Resource PluralObject = resource("PluralObject");
    public static final Resource SingleObject = resource("SingleObject");
    public static final Resource ConceptualClass = resource("ConceptualClass");
    public static final Resource QualitativeClass = resource("QualitativeClass");

    /**
     * Individual object (a.k.a. individual thing) is a concept which members
     * have a unique single or plural identity or existence in the world with a
     * beginning and possibly an end. A member can be imagined as a mental
     * object or observable. It is not necessarily a physical object.
     */
    public static final Resource IndividualThing = resource("IndividualThing");
    /**
     * Whole individual thing is an individual object that has independent
     * existence. Irrespective of relations with other whole individual things.
     */
    public static final Resource WholeThing = resource("WholeThing");
    /**
     * Physical object is a whole individual thing that is a distribution of
     * matter or energy in time and space that satisfies the laws of physics.
     * All kinds of physical objects are subtypes of this class. It is the top
     * of the hierarchical network of specializations of kinds of physical
     * objects.
     */
    public static final Resource PhysicalObject = resource("PhysicalObject");
    public static final Resource Aspect = resource("Aspect");

    public static final Property relation = property("relation");
    public static final Property relationBetweenClasses = property("relationBetweenClasses");
    public static final Property relationBetweenMembersOfClasses = property("relationBetweenMembersOfClasses");
    public static final Property relationBetweenIndividualThings = property("relationBetweenIndividualThings");

    public static final Property specializationOf = property("specializationOf");
    public static final Property qualificationOf = property("qualificationOf");
    public static final Property classifiedAs = property("classifiedAs");

    public static final Property canBeComponentOf = property("canBeComponentOf");
    public static final Property canBeConnectedTo = property("canBeConnectedTo");
    public static final Property connectedTo = property("connectedTo");
    public static final Property canBePartOf = property("canBePartOf");
    public static final Property canHaveAsPart = property("canHaveAsPart");
    public static final Property shallBePartOf = property("shallBePartOf");
    public static final Property isByDefinitionAPossiblePartOfA = property("isByDefinitionAPossiblePartOfA");

    public static final Property synonymOf = property("synonymOf");
    public static final Property abbreviatedAs = property("abbreviatedAs");
    public static final Property abbreviationOf = property("abbreviationOf");
    public static final Property codeFor = property("codeFor");
    public static final Property codedAs = property("codeFor");
    public static final Property longNameOf = property("longNameOf");
    public static final Property longName = property("longName");

    public static final Property isAnElementOf = property("isAnElementOf");

    public static final Property hasAspect = property("hasAspect");
    public static final Property canHaveAsAspect = property("canHaveAsAspect");

    public static final Property canBeSymbolizedByA = property("canBeSymbolizedByA");
    public static final Property symbolizedBy = property("symbolizedBy");
    public static final Property isByDefinitionQualifiedBy = property("isByDefinitionQualifiedBy");
    public static final Property isAnElementInCollectionOfClasses = property("isAnElementInCollectionOfClasses");
    public static final Property subsetOf = property("subsetOf");
    public static final Property supersetOf = property("supersetOf");
    public static final Property presentedOn = property("presentedOn");
    public static final Property presenterOf = property("presenterOf");

    public static final Property hasFunctionByDefinition = property("hasFunctionByDefinition");
    public static final Property inclusionOfQualitativeInformationAboutAConcept = property("inclusionOfQualitativeInformationAboutAConcept");
    public static final Property hasAdditionalFunctionByDefinition = property("hasAdditionalFunctionByDefinition");

    public static final Property fulfillsFunction = property("fulfillsFunction");

    /*
     * Is a conceptual relation between members of classes that indicates a
     * generic nature of relations being that a member of the related class can
     * be related to a member of the other related class. This implies that
     * relations of that kind express that an individual object is related to
     * another. The conceptual relation itself does not specify the members that
     * are related.
     */
    public static final Property canBeRelatedTo = property("canBeRelatedTo");
    public static final Property canBeCorrelatedTo = property("canBeCorrelatedTo");
    public static final Property conceptualBinaryRelation = property("conceptualBinaryRelation");
    /*
     * Is a conceptual binary relation that specifies that a route of a kind can
     * terminate at a destination physical object of a kind.
     */
    public static final Property canBeDestinationOf = property("canBeDestinationOf");
    /*
     * Is a conceptual destination of a route that specifies that a route of a
     * kind shall terminate at a destination physical object of a kind.
     */
    public static final Property shallBeDestinationOf = property("shallBeDestinationOf");
    /*
     * Is a conceptual binary relation that specifies that a route of a kind can
     * begin at a physical object of a kind.
     */
    public static final Property canBeSourceOf = property("canBeSourceOf");
    /*
     * Is a conceptual source of a route that specifies that a route of a kind
     * shall begin at a physical object of a kind.
     */
    public static final Property shallBeSourceOf = property("shallBeSourceOf");

    /**
     * The Gellish vocabulary, expressed for the SPI layer in terms of .graph
     * Nodes.
     */
    public static class Nodes {
        public static final Node Class = Gellish.Class.asNode();
        public static final Node specializationOf = Gellish.specializationOf.asNode();
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
