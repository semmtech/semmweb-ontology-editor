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

package com.semmtech.plugin.semmweb.editor.preferences;


/**
 * Constant definitions for plug-in preferences
 */
public class PropertiesPreferenceConstants {

    // public static final String PREFERENCE_SHOW_INSTANCE_COUNT =
    // "com.semmtech.plugin.semmweb.editor.preferences.taxonomy.showInstanceCount";
    // public static final String PREFERENCE_SHOW_INSTANCES =
    // "com.semmtech.plugin.semmweb.editor.preferences.taxonomy.showInstances";
    // public static final String PREFERENCE_HIDDEN_CLASSES =
    // "com.semmtech.plugin.semmweb.editor.preferences.taxonomy.hiddenClasses";
    // public static final String PREFERENCE_EXCLUSION_FILTER_PREFERENCE =
    // "com.semmtech.plugin.semmweb.editor.preferences.taxonomy.exclusionFilter";
    //
    // public static final String VALUE_PREFERENCE_DELIMITER = "|";
    // public static final String DEFAULT_TAXONOMY_VIEW_RULES = "" +
    // "@prefix rdfs:   <http://www.w3.org/2000/01/rdf-schema#> .\n" +
    // "@prefix xsd:    <http://www.w3.org/2001/XMLSchema#> .\n" +
    // "@prefix owl:    <http://www.w3.org/2002/07/owl#> .\n" +
    // "@prefix rdf:    <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .\n" +
    // "# RDF types\n" +
    // "	[ -> (rdf:Alt rdf:type rdfs:Class) ]\n" +
    // "	[ -> (rdf:Property rdf:type rdfs:Class) ]\n" +
    // "	[ -> (rdf:List rdf:type rdfs:Class) ]\n" +
    // "	[ -> (rdf: rdf:type owl:Ontology) ] \n" +
    // "	[ -> (rdf:value rdf:type rdf:Property) ] \n" +
    // "	[ -> (rdf:Bag rdf:type rdfs:Class) ] \n" +
    // "	[ -> (rdf:rest rdf:type rdf:Property) ] \n" +
    // "	[ -> (rdf:first rdf:type rdf:Property) ] \n" +
    // "	[ -> (rdf:PlainLiteral rdf:type rdfs:Datatype) ] \n" +
    // "	[ -> (rdf:Seq rdf:type rdfs:Class) ] \n" +
    // "	[ -> (rdf:object rdf:type rdf:Property) ] \n" +
    // "	[ -> (rdf:XMLLiteral rdf:type rdfs:Datatype) ] \n" +
    // "	[ -> (rdf:subject rdf:type rdf:Property) ] \n" +
    // "	[ -> (rdf:nil rdf:type rdf:List) ] \n" +
    // "	[ -> (rdf:predicate rdf:type rdf:Property) ] \n" +
    // "	[ -> (rdf:Statement rdf:type rdfs:Class) ] \n" +
    // "	[ -> (rdf:type rdf:type rdf:Property) ] \n" +
    // "# RDFS types\n" +
    // "	[ -> (rdfs:Resource rdf:type rdfs:Class) ] \n" +
    // "	[ -> (rdfs:subPropertyOf rdf:type rdf:Property) ] \n" +
    // "	[ -> (rdfs:isDefinedBy rdf:type rdf:Property) ] \n" +
    // "	[ -> (rdfs: rdf:type owl:Ontology) ] \n" +
    // "	[ -> (rdfs:ContainerMembershipProperty rdf:type rdfs:Class) ] \n" +
    // "	[ -> (rdfs:Datatype rdf:type rdfs:Class) ] \n" +
    // "	[ -> (rdfs:range rdf:type rdf:Property) ] \n" +
    // "	[ -> (rdfs:subClassOf rdf:type rdf:Property) ] \n" +
    // "	[ -> (rdfs:seeAlso rdf:type rdf:Property) ] \n" +
    // "	[ -> (rdfs:member rdf:type rdf:Property) ] \n" +
    // "	[ -> (rdfs:Class rdf:type rdfs:Class) ] \n" +
    // "	[ -> (rdfs:Container rdf:type rdfs:Class) ] \n" +
    // "	[ -> (rdfs:comment rdf:type rdf:Property) ] \n" +
    // "	[ -> (rdfs:domain rdf:type rdf:Property) ] \n" +
    // "	[ -> (rdfs:Literal rdf:type rdfs:Class) ] \n" +
    // "	[ -> (rdfs:label rdf:type rdf:Property) ] \n" +
    // "#OWL types\n" +
    // "	[ -> (owl:equivalentProperty rdf:type rdf:Property) ] \n" +
    // "	[ -> (owl:assertionProperty rdf:type rdf:Property) ] \n" +
    // "	[ -> (owl:onProperties rdf:type rdf:Property) ] \n" +
    // "	[ -> (owl:DataRange rdf:type rdfs:Class) ] \n" +
    // "	[ -> (owl:AllDisjointProperties rdf:type rdfs:Class) ] \n" +
    // "	[ -> (owl:onProperty rdf:type rdf:Property) ] \n" +
    // "	[ -> (owl:versionIRI rdf:type owl:OntologyProperty) ] \n" +
    // "	[ -> (owl:complementOf rdf:type rdf:Property) ] \n" +
    // "	[ -> (owl:topObjectProperty rdf:type owl:ObjectProperty) ] \n" +
    // "	[ -> (owl:DeprecatedProperty rdf:type rdfs:Class) ] \n" +
    // "	[ -> (owl:AllDifferent rdf:type rdfs:Class) ] \n" +
    // "	[ -> (owl:equivalentClass rdf:type rdf:Property) ] \n" +
    // "	[ -> (owl:Axiom rdf:type rdfs:Class) ] \n" +
    // "	[ -> (owl:Class rdf:type rdfs:Class) ] \n" +
    // "	[ -> (owl:cardinality rdf:type rdf:Property) ] \n" +
    // "	[ -> (owl:onDatatype rdf:type rdf:Property) ] \n" +
    // "	[ -> (owl:DatatypeProperty rdf:type rdfs:Class) ] \n" +
    // "	[ -> (owl:unionOf rdf:type rdf:Property) ] \n" +
    // "	[ -> (owl:minQualifiedCardinality rdf:type rdf:Property) ] \n" +
    // "	[ -> (owl:DeprecatedClass rdf:type rdfs:Class) ] \n" +
    // "	[ -> (owl:disjointUnionOf rdf:type rdf:Property) ] \n" +
    // "	[ -> (owl:NegativePropertyAssertion rdf:type rdfs:Class) ] \n" +
    // "	[ -> (owl:priorVersion rdf:type owl:AnnotationProperty) ] \n" +
    // "	[ -> (owl:targetIndividual rdf:type rdf:Property) ] \n" +
    // "	[ -> (owl:maxCardinality rdf:type rdf:Property) ] \n" +
    // "	[ -> (owl:priorVersion rdf:type owl:OntologyProperty) ] \n" +
    // "	[ -> (owl:withRestrictions rdf:type rdf:Property) ] \n" +
    // "	[ -> (owl:sameAs rdf:type rdf:Property) ] \n" +
    // "	[ -> (owl:IrreflexiveProperty rdf:type rdfs:Class) ] \n" +
    // "	[ -> (owl:hasSelf rdf:type rdf:Property) ] \n" +
    // "	[ -> (owl:differentFrom rdf:type rdf:Property) ] \n" +
    // "	[ -> (owl:TransitiveProperty rdf:type rdfs:Class) ] \n" +
    // "	[ -> (owl:Restriction rdf:type rdfs:Class) ] \n" +
    // "	[ -> (owl:sourceIndividual rdf:type rdf:Property) ] \n" +
    // "	[ -> (owl:AllDisjointClasses rdf:type rdfs:Class) ] \n" +
    // "	[ -> (owl:someValuesFrom rdf:type rdf:Property) ] \n" +
    // "	[ -> (owl:AsymmetricProperty rdf:type rdfs:Class) ] \n" +
    // "	[ -> (owl:disjointWith rdf:type rdf:Property) ] \n" +
    // "	[ -> (owl:backwardCompatibleWith rdf:type owl:OntologyProperty) ] \n" +
    // "	[ -> (owl:AnnotationProperty rdf:type rdfs:Class) ] \n" +
    // "	[ -> (owl:propertyChainAxiom rdf:type rdf:Property) ] \n" +
    // "	[ -> (owl:oneOf rdf:type rdf:Property) ] \n" +
    // "	[ -> (owl:deprecated rdf:type owl:AnnotationProperty) ] \n" +
    // "	[ -> (owl:imports rdf:type owl:OntologyProperty) ] \n" +
    // "	[ -> (owl:topDataProperty rdf:type owl:DatatypeProperty) ] \n" +
    // "	[ -> (owl:SymmetricProperty rdf:type rdfs:Class) ] \n" +
    // "	[ -> (owl:allValuesFrom rdf:type rdf:Property) ] \n" +
    // "	[ -> (owl:Ontology rdf:type rdfs:Class) ] \n" +
    // "	[ -> (owl:backwardCompatibleWith rdf:type owl:AnnotationProperty) ] \n"
    // +
    // "	[ -> (owl:targetValue rdf:type rdf:Property) ] \n" +
    // "	[ -> (owl:members rdf:type rdf:Property) ] \n" +
    // "	[ -> (owl:bottomDataProperty rdf:type owl:DatatypeProperty) ] \n" +
    // "	[ -> (owl:qualifiedCardinality rdf:type rdf:Property) ] \n" +
    // "	[ -> (owl:maxQualifiedCardinality rdf:type rdf:Property) ] \n" +
    // "	[ -> (owl:OntologyProperty rdf:type rdfs:Class) ] \n" +
    // "	[ -> (owl:incompatibleWith rdf:type owl:OntologyProperty) ] \n" +
    // "	[ -> (owl:versionInfo rdf:type owl:AnnotationProperty) ] \n" +
    // "	[ -> (owl:propertyDisjointWith rdf:type rdf:Property) ] \n" +
    // "	[ -> (owl:FunctionalProperty rdf:type rdfs:Class) ] \n" +
    // "	[ -> (owl:annotatedProperty rdf:type rdf:Property) ] \n" +
    // "	[ -> (owl:Annotation rdf:type rdfs:Class) ] \n" +
    // "	[ -> (owl:onDataRange rdf:type rdf:Property) ] \n" +
    // "	[ -> (owl:incompatibleWith rdf:type owl:AnnotationProperty) ] \n" +
    // "	[ -> (owl:Thing rdf:type owl:Class) ] \n" +
    // "	[ -> (owl:onClass rdf:type rdf:Property) ] \n" +
    // "	[ -> (owl:bottomObjectProperty rdf:type owl:ObjectProperty) ] \n" +
    // "	[ -> (owl:ObjectProperty rdf:type rdfs:Class) ] \n" +
    // "	[ -> (owl:annotatedSource rdf:type rdf:Property) ] \n" +
    // "	[ -> (owl:inverseOf rdf:type rdf:Property) ] \n" +
    // "	[ -> (owl:Nothing rdf:type owl:Class) ] \n" +
    // "	[ -> (owl:distinctMembers rdf:type rdf:Property) ] \n" +
    // "	[ -> (owl:hasKey rdf:type rdf:Property) ] \n" +
    // "	[ -> (owl:hasValue rdf:type rdf:Property) ] \n" +
    // "	[ -> (owl:ReflexiveProperty rdf:type rdfs:Class) ] \n" +
    // "	[ -> (owl:minCardinality rdf:type rdf:Property) ] \n" +
    // "	[ -> (owl:intersectionOf rdf:type rdf:Property) ] \n" +
    // "	[ -> (owl:annotatedTarget rdf:type rdf:Property) ] \n" +
    // "	[ -> (owl:NamedIndividual rdf:type rdfs:Class) ] \n" +
    // "	[ -> (owl:InverseFunctionalProperty rdf:type rdfs:Class) ] \n" +
    // "	[ -> (owl:datatypeComplementOf rdf:type rdf:Property) ] \n" +
    // "# RDF sub-classes \n" +
    // "	[ -> (rdf:XMLLiteral rdfs:subClassOf rdfs:Literal) ] \n" +
    // "	[ -> (rdf:List rdfs:subClassOf rdfs:Resource) ] \n" +
    // "	[ -> (rdf:Alt rdfs:subClassOf rdfs:Container) ] \n" +
    // "	[ -> (rdf:Seq rdfs:subClassOf rdfs:Container) ] \n" +
    // "	[ -> (rdf:Bag rdfs:subClassOf rdfs:Container) ] \n" +
    // "	[ -> (rdf:Statement rdfs:subClassOf rdfs:Resource) ] \n" +
    // "	[ -> (rdf:Property rdfs:subClassOf rdfs:Resource) ] \n" +
    // "	[ -> (rdf:PlainLiteral rdfs:subClassOf rdfs:Literal) ] \n" +
    // "# RDFS sub-classes\n" +
    // "	[ -> (rdfs:Datatype rdfs:subClassOf rdfs:Class) ] \n" +
    // "	[ -> (rdfs:ContainerMembershipProperty rdfs:subClassOf rdf:Property) ] \n"
    // +
    // "	[ -> (rdfs:Container rdfs:subClassOf rdfs:Resource) ] \n" +
    // "	[ -> (rdfs:Literal rdfs:subClassOf rdfs:Resource) ] \n" +
    // "	[ -> (rdfs:Class rdfs:subClassOf rdfs:Resource) ] \n" +
    // "# OWL sub-classes \n" +
    // "	[ -> (owl:Thing rdfs:subClassOf rdfs:Resource) ] \n" +
    // "	[ -> (owl:Axiom rdfs:subClassOf rdfs:Resource) ] \n" +
    // "	[ -> (owl:InverseFunctionalProperty rdfs:subClassOf owl:ObjectProperty) ] \n"
    // +
    // "	[ -> (owl:NegativePropertyAssertion rdfs:subClassOf rdfs:Resource) ] \n"
    // +
    // "	[ -> (owl:DeprecatedClass rdfs:subClassOf rdfs:Class) ] \n" +
    // "	[ -> (owl:Ontology rdfs:subClassOf rdfs:Resource) ] \n" +
    // "	[ -> (owl:Restriction rdfs:subClassOf owl:Class) ] \n" +
    // "	[ -> (owl:IrreflexiveProperty rdfs:subClassOf owl:ObjectProperty) ] \n"
    // +
    // "	[ -> (owl:NamedIndividual rdfs:subClassOf owl:Thing) ] \n" +
    // "	[ -> (owl:Nothing rdfs:subClassOf owl:Thing) ] \n" +
    // "	[ -> (owl:AllDisjointClasses rdfs:subClassOf rdfs:Resource) ] \n" +
    // "	[ -> (owl:AsymmetricProperty rdfs:subClassOf owl:ObjectProperty) ] \n"
    // +
    // "	[ -> (owl:AnnotationProperty rdfs:subClassOf rdf:Property) ] \n" +
    // "	[ -> (owl:TransitiveProperty rdfs:subClassOf owl:ObjectProperty) ] \n"
    // +
    // "	[ -> (owl:DeprecatedProperty rdfs:subClassOf rdf:Property) ] \n" +
    // "	[ -> (owl:Class rdfs:subClassOf rdfs:Class) ] \n" +
    // "	[ -> (owl:Annotation rdfs:subClassOf rdfs:Resource) ] \n" +
    // "	[ -> (owl:FunctionalProperty rdfs:subClassOf rdf:Property) ] \n" +
    // "	[ -> (owl:DatatypeProperty rdfs:subClassOf rdf:Property) ] \n" +
    // "	[ -> (owl:AllDifferent rdfs:subClassOf rdfs:Resource) ] \n" +
    // "	[ -> (owl:SymmetricProperty rdfs:subClassOf owl:ObjectProperty) ] \n" +
    // "	[ -> (owl:ReflexiveProperty rdfs:subClassOf owl:ObjectProperty) ] \n" +
    // "	[ -> (owl:DataRange rdfs:subClassOf rdfs:Datatype) ] \n" +
    // "	[ -> (owl:AllDisjointProperties rdfs:subClassOf rdfs:Resource) ] \n" +
    // "	[ -> (owl:OntologyProperty rdfs:subClassOf rdf:Property) ] \n" +
    // "	[ -> (owl:ObjectProperty rdfs:subClassOf rdf:Property) ] \n" +
    // "# Rules:			\n" +
    // "	[ subClassesOfThing: (?C rdf:type owl:Class) -> [  (?C rdfs:subClassOf owl:Thing) <- noValue(?C rdfs:subClassOf ?X) ] ]\n"
    // +
    // "	[ removeThingSubThing: (owl:Thing rdfs:subClassOf owl:Thing) -> remove(0) ] \n"+
    // "# XSD\n" +
    // "	[ -> (xsd:float rdf:type rdfs:Datatype) ] \n" +
    // "	[ -> (xsd:double rdf:type rdfs:Datatype) ] \n" +
    // "	[ -> (xsd:int rdf:type rdfs:Datatype) ] \n" +
    // "	[ -> (xsd:long rdf:type rdfs:Datatype) ] \n" +
    // "	[ -> (xsd:short rdf:type rdfs:Datatype) ] \n" +
    // "	[ -> (xsd:byte rdf:type rdfs:Datatype) ] \n" +
    // "	[ -> (xsd:unsignedByte rdf:type rdfs:Datatype) ] \n" +
    // "	[ -> (xsd:unsignedShort rdf:type rdfs:Datatype) ] \n" +
    // "	[ -> (xsd:unsignedInt rdf:type rdfs:Datatype) ] \n" +
    // "	[ -> (xsd:unsignedLong rdf:type rdfs:Datatype) ] \n" +
    // "	[ -> (xsd:decimal rdf:type rdfs:Datatype) ] \n" +
    // "	[ -> (xsd:integer rdf:type rdfs:Datatype) ] \n" +
    // "	[ -> (xsd:nonPositiveInteger rdf:type rdfs:Datatype) ] \n" +
    // "	[ -> (xsd:nonNegativeInteger rdf:type rdfs:Datatype) ] \n" +
    // "	[ -> (xsd:positiveInteger rdf:type rdfs:Datatype) ] \n" +
    // "	[ -> (xsd:negativeInteger rdf:type rdfs:Datatype) ] \n" +
    // "	[ -> (xsd:boolean rdf:type rdfs:Datatype) ] \n" +
    // "	[ -> (xsd:string rdf:type rdfs:Datatype) ] \n" +
    // "	[ -> (xsd:anyURI rdf:type rdfs:Datatype) ] \n" +
    // "	[ -> (xsd:hexBinary rdf:type rdfs:Datatype) ] \n" +
    // "	[ -> (xsd:base64Binary rdf:type rdfs:Datatype) ] \n" +
    // "	[ -> (xsd:date rdf:type rdfs:Datatype) ] \n" +
    // "	[ -> (xsd:time rdf:type rdfs:Datatype) ] \n" +
    // "	[ -> (xsd:dateTime rdf:type rdfs:Datatype) ] \n" +
    // "	[ -> (xsd:duration rdf:type rdfs:Datatype) ] \n" +
    // "	[ -> (xsd:gDay rdf:type rdfs:Datatype) ] \n" +
    // "	[ -> (xsd:gMonth rdf:type rdfs:Datatype) ] \n" +
    // "	[ -> (xsd:gYear rdf:type rdfs:Datatype) ] \n" +
    // "	[ -> (xsd:gYearMonth rdf:type rdfs:Datatype) ] \n" +
    // "	[ -> (xsd:gMonthDay rdf:type rdfs:Datatype) ] \n" +
    // "	[ -> (xsd:integer rdfs:subClassOf xsd:decimal)]";
}
