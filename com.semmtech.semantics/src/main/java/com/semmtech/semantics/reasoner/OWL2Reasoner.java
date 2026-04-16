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

package com.semmtech.semantics.reasoner;


import java.util.List;

import com.google.common.collect.Lists;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.rulesys.GenericRuleReasoner;
import com.hp.hpl.jena.reasoner.rulesys.Rule;


public class OWL2Reasoner {
    public static Reasoner getReasoner() {
        List<Rule> rules = Lists.newArrayList();
        rules.addAll(Rule.parseRules(""
                + "[ -> tableAll() ]"
                +
                // # RDF types
                "[ -> (rdf:Alt rdf:type rdfs:Class) ]"
                + "[ -> (rdf:Property rdf:type rdfs:Class) ]"
                + "[ -> (rdf:List rdf:type rdfs:Class) ]"
                + "[ -> (rdf: rdf:type owl:Ontology) ]"
                + "[ -> (rdf:value rdf:type rdf:Property) ]"
                + "[ -> (rdf:Bag rdf:type rdfs:Class) ]"
                + "[ -> (rdf:rest rdf:type rdf:Property) ]"
                + "[ -> (rdf:first rdf:type rdf:Property) ]"
                + "[ -> (rdf:PlainLiteral rdf:type rdfs:Datatype) ]"
                + "[ -> (rdf:Seq rdf:type rdfs:Class) ]"
                + "[ -> (rdf:object rdf:type rdf:Property) ]"
                + "[ -> (rdf:XMLLiteral rdf:type rdfs:Datatype) ]"
                + "[ -> (rdf:subject rdf:type rdf:Property) ]"
                + "[ -> (rdf:nil rdf:type rdf:List) ]"
                + "[ -> (rdf:predicate rdf:type rdf:Property) ]"
                + "[ -> (rdf:Statement rdf:type rdfs:Class) ]"
                + "[ -> (rdf:type rdf:type rdf:Property) ]"
                +
                // # RDFS types
                "[ -> (rdfs:Resource rdf:type rdfs:Class) ]"
                + "[ -> (rdfs:subPropertyOf rdf:type rdf:Property) ]"
                + "[ -> (rdfs:isDefinedBy rdf:type rdf:Property) ]"
                + "[ -> (rdfs: rdf:type owl:Ontology) ]"
                + "[ -> (rdfs:ContainerMembershipProperty rdf:type rdfs:Class) ]"
                + "[ -> (rdfs:Datatype rdf:type rdfs:Class) ]"
                + "[ -> (rdfs:range rdf:type rdf:Property) ]"
                + "[ -> (rdfs:subClassOf rdf:type rdf:Property) ]"
                + "[ -> (rdfs:seeAlso rdf:type rdf:Property) ]"
                + "[ -> (rdfs:member rdf:type rdf:Property) ]"
                + "[ -> (rdfs:Class rdf:type rdfs:Class) ]"
                + "[ -> (rdfs:Container rdf:type rdfs:Class) ]"
                + "[ -> (rdfs:comment rdf:type rdf:Property) ]"
                + "[ -> (rdfs:domain rdf:type rdf:Property) ]"
                + "[ -> (rdfs:Literal rdf:type rdfs:Class) ]"
                + "[ -> (rdfs:label rdf:type rdf:Property) ]"
                +
                // #OWL types
                "[ -> (owl:equivalentProperty rdf:type rdf:Property) ]"
                + "[ -> (owl:assertionProperty rdf:type rdf:Property) ]"
                + "[ -> (owl:onProperties rdf:type rdf:Property) ]"
                + "[ -> (owl:DataRange rdf:type rdfs:Class) ]"
                + "[ -> (owl:AllDisjointProperties rdf:type rdfs:Class) ]"
                + "[ -> (owl:onProperty rdf:type rdf:Property) ]"
                + "[ -> (owl:versionIRI rdf:type owl:OntologyProperty) ]"
                + "[ -> (owl:complementOf rdf:type rdf:Property) ]"
                + "[ -> (owl:topObjectProperty rdf:type owl:ObjectProperty) ]"
                + "[ -> (owl:DeprecatedProperty rdf:type rdfs:Class) ]"
                + "[ -> (owl:AllDifferent rdf:type rdfs:Class) ]"
                + "[ -> (owl:equivalentClass rdf:type rdf:Property) ]"
                + "[ -> (owl:Axiom rdf:type rdfs:Class) ]"
                + "[ -> (owl:Class rdf:type rdfs:Class) ]"
                + "[ -> (owl:cardinality rdf:type rdf:Property) ]"
                + "[ -> (owl:onDatatype rdf:type rdf:Property) ]"
                + "[ -> (owl:DatatypeProperty rdf:type rdfs:Class) ]"
                + "[ -> (owl:unionOf rdf:type rdf:Property) ]"
                + "[ -> (owl:minQualifiedCardinality rdf:type rdf:Property) ]"
                + "[ -> (owl:DeprecatedClass rdf:type rdfs:Class) ]"
                + "[ -> (owl:disjointUnionOf rdf:type rdf:Property) ]"
                + "[ -> (owl:NegativePropertyAssertion rdf:type rdfs:Class) ]"
                + "[ -> (owl:priorVersion rdf:type owl:AnnotationProperty) ]"
                + "[ -> (owl:targetIndividual rdf:type rdf:Property) ]"
                + "[ -> (owl:maxCardinality rdf:type rdf:Property) ]"
                + "[ -> (owl:priorVersion rdf:type owl:OntologyProperty) ]"
                + "[ -> (owl:withRestrictions rdf:type rdf:Property) ]"
                + "[ -> (owl:sameAs rdf:type rdf:Property) ]"
                + "[ -> (owl:IrreflexiveProperty rdf:type rdfs:Class) ]"
                + "[ -> (owl:hasSelf rdf:type rdf:Property) ]"
                + "[ -> (owl:differentFrom rdf:type rdf:Property) ]"
                + "[ -> (owl:TransitiveProperty rdf:type rdfs:Class) ]"
                + "[ -> (owl:Restriction rdf:type rdfs:Class) ]"
                + "[ -> (owl:sourceIndividual rdf:type rdf:Property) ]"
                + "[ -> (owl:AllDisjointClasses rdf:type rdfs:Class) ]"
                + "[ -> (owl:someValuesFrom rdf:type rdf:Property) ]"
                + "[ -> (owl:AsymmetricProperty rdf:type rdfs:Class) ]"
                + "[ -> (owl:disjointWith rdf:type rdf:Property) ]"
                + "[ -> (owl:backwardCompatibleWith rdf:type owl:OntologyProperty) ]"
                + "[ -> (owl:AnnotationProperty rdf:type rdfs:Class) ]"
                + "[ -> (owl:propertyChainAxiom rdf:type rdf:Property) ]"
                + "[ -> (owl:oneOf rdf:type rdf:Property) ]"
                + "[ -> (owl:deprecated rdf:type owl:AnnotationProperty) ]"
                + "[ -> (owl:imports rdf:type owl:OntologyProperty) ]"
                + "[ -> (owl:topDataProperty rdf:type owl:DatatypeProperty) ]"
                + "[ -> (owl:SymmetricProperty rdf:type rdfs:Class) ]"
                + "[ -> (owl:allValuesFrom rdf:type rdf:Property) ]"
                + "[ -> (owl:Ontology rdf:type rdfs:Class) ]"
                + "[ -> (owl:backwardCompatibleWith rdf:type owl:AnnotationProperty) ]"
                + "[ -> (owl:targetValue rdf:type rdf:Property) ]"
                + "[ -> (owl:members rdf:type rdf:Property) ]"
                + "[ -> (owl:bottomDataProperty rdf:type owl:DatatypeProperty) ]"
                + "[ -> (owl:qualifiedCardinality rdf:type rdf:Property) ]"
                + "[ -> (owl:maxQualifiedCardinality rdf:type rdf:Property) ]"
                + "[ -> (owl:OntologyProperty rdf:type rdfs:Class) ]"
                + "[ -> (owl:incompatibleWith rdf:type owl:OntologyProperty) ]"
                + "[ -> (owl:versionInfo rdf:type owl:AnnotationProperty) ]"
                + "[ -> (owl:propertyDisjointWith rdf:type rdf:Property) ]"
                + "[ -> (owl:FunctionalProperty rdf:type rdfs:Class) ]"
                + "[ -> (owl:annotatedProperty rdf:type rdf:Property) ]"
                + "[ -> (owl:Annotation rdf:type rdfs:Class) ]"
                + "[ -> (owl:onDataRange rdf:type rdf:Property) ]"
                + "[ -> (owl:incompatibleWith rdf:type owl:AnnotationProperty) ]"
                + "[ -> (owl:Thing rdf:type owl:Class) ]"
                + "[ -> (owl:onClass rdf:type rdf:Property) ]"
                + "[ -> (owl:bottomObjectProperty rdf:type owl:ObjectProperty) ]"
                + "[ -> (owl:ObjectProperty rdf:type rdfs:Class) ]"
                + "[ -> (owl:annotatedSource rdf:type rdf:Property) ]"
                + "[ -> (owl:inverseOf rdf:type rdf:Property) ]"
                + "[ -> (owl:Nothing rdf:type owl:Class) ]"
                + "[ -> (owl:distinctMembers rdf:type rdf:Property) ]"
                + "[ -> (owl:hasKey rdf:type rdf:Property) ]"
                + "[ -> (owl:hasValue rdf:type rdf:Property) ]"
                + "[ -> (owl:ReflexiveProperty rdf:type rdfs:Class) ]"
                + "[ -> (owl:minCardinality rdf:type rdf:Property) ]"
                + "[ -> (owl:intersectionOf rdf:type rdf:Property) ]"
                + "[ -> (owl:annotatedTarget rdf:type rdf:Property) ]"
                + "[ -> (owl:NamedIndividual rdf:type rdfs:Class) ]"
                + "[ -> (owl:InverseFunctionalProperty rdf:type rdfs:Class) ]"
                + "[ -> (owl:datatypeComplementOf rdf:type rdf:Property) ]"
                +
                // # RDF sub-classes
                "[ -> (rdf:XMLLiteral rdfs:subClassOf rdfs:Literal) ]"
                + "[ -> (rdf:List rdfs:subClassOf rdfs:Resource) ]"
                + "[ -> (rdf:Alt rdfs:subClassOf rdfs:Container) ]"
                + "[ -> (rdf:Seq rdfs:subClassOf rdfs:Container) ]"
                + "[ -> (rdf:Bag rdfs:subClassOf rdfs:Container) ]"
                + "[ -> (rdf:Statement rdfs:subClassOf rdfs:Resource) ]"
                + "[ -> (rdf:Property rdfs:subClassOf rdfs:Resource) ]"
                + "[ -> (rdf:PlainLiteral rdfs:subClassOf rdfs:Literal) ]"
                +
                // # RDFS sub-classes
                "[ -> (rdfs:Datatype rdfs:subClassOf rdfs:Class) ]"
                + "[ -> (rdfs:ContainerMembershipProperty rdfs:subClassOf rdf:Property) ]"
                + "[ -> (rdfs:Container rdfs:subClassOf rdfs:Resource) ]"
                + "[ -> (rdfs:Literal rdfs:subClassOf rdfs:Resource) ]"
                + "[ -> (rdfs:Class rdfs:subClassOf rdfs:Resource) ]"
                +
                // # OWL sub-classes
                "[ -> (owl:Thing rdfs:subClassOf rdfs:Resource) ]"
                + "[ -> (owl:Axiom rdfs:subClassOf rdfs:Resource) ]"
                + "[ -> (owl:InverseFunctionalProperty rdfs:subClassOf owl:ObjectProperty) ]"
                + "[ -> (owl:NegativePropertyAssertion rdfs:subClassOf rdfs:Resource) ]"
                + "[ -> (owl:DeprecatedClass rdfs:subClassOf rdfs:Class) ]"
                + "[ -> (owl:Ontology rdfs:subClassOf rdfs:Resource) ]"
                + "[ -> (owl:Restriction rdfs:subClassOf owl:Class) ]"
                + "[ -> (owl:IrreflexiveProperty rdfs:subClassOf owl:ObjectProperty) ]"
                + "[ -> (owl:NamedIndividual rdfs:subClassOf owl:Thing) ]"
                + "[ -> (owl:Nothing rdfs:subClassOf owl:Thing) ]"
                + "[ -> (owl:AllDisjointClasses rdfs:subClassOf rdfs:Resource) ]"
                + "[ -> (owl:AsymmetricProperty rdfs:subClassOf owl:ObjectProperty) ]"
                + "[ -> (owl:AnnotationProperty rdfs:subClassOf rdf:Property) ]"
                + "[ -> (owl:TransitiveProperty rdfs:subClassOf owl:ObjectProperty) ]"
                + "[ -> (owl:DeprecatedProperty rdfs:subClassOf rdf:Property) ]"
                + "[ -> (owl:Class rdfs:subClassOf rdfs:Class) ]"
                + "[ -> (owl:Annotation rdfs:subClassOf rdfs:Resource) ]"
                + "[ -> (owl:FunctionalProperty rdfs:subClassOf rdf:Property) ]"
                + "[ -> (owl:DatatypeProperty rdfs:subClassOf rdf:Property) ]"
                + "[ -> (owl:AllDifferent rdfs:subClassOf rdfs:Resource) ]"
                + "[ -> (owl:SymmetricProperty rdfs:subClassOf owl:ObjectProperty) ]"
                + "[ -> (owl:ReflexiveProperty rdfs:subClassOf owl:ObjectProperty) ]"
                + "[ -> (owl:DataRange rdfs:subClassOf rdfs:Datatype) ]"
                + "[ -> (owl:AllDisjointProperties rdfs:subClassOf rdfs:Resource) ]"
                + "[ -> (owl:OntologyProperty rdfs:subClassOf rdf:Property) ]"
                + "[ -> (owl:ObjectProperty rdfs:subClassOf rdf:Property) ]"
                +
                // # Rules:
                "[ subClassesOfThing: (?C rdf:type owl:Class) -> (?C rdfs:subClassOf owl:Thing) ]"
                + "[ removeThingSubThing: (owl:Thing rdfs:subClassOf owl:Thing) -> remove(0) ]"
                +
                // # XSD
                "[ -> (xsd:float rdf:type rdfs:Datatype) ]"
                + "[ -> (xsd:double rdf:type rdfs:Datatype) ]"
                + "[ -> (xsd:int rdf:type rdfs:Datatype) ]"
                + "[ -> (xsd:long rdf:type rdfs:Datatype) ]"
                + "[ -> (xsd:short rdf:type rdfs:Datatype) ]"
                + "[ -> (xsd:byte rdf:type rdfs:Datatype) ]"
                + "[ -> (xsd:unsignedByte rdf:type rdfs:Datatype) ]"
                + "[ -> (xsd:unsignedShort rdf:type rdfs:Datatype) ]"
                + "[ -> (xsd:unsignedInt rdf:type rdfs:Datatype) ]"
                + "[ -> (xsd:unsignedLong rdf:type rdfs:Datatype) ]"
                + "[ -> (xsd:decimal rdf:type rdfs:Datatype) ]"
                + "[ -> (xsd:integer rdf:type rdfs:Datatype) ]"
                + "[ -> (xsd:nonPositiveInteger rdf:type rdfs:Datatype) ]"
                + "[ -> (xsd:nonNegativeInteger rdf:type rdfs:Datatype) ]"
                + "[ -> (xsd:positiveInteger rdf:type rdfs:Datatype) ]"
                + "[ -> (xsd:negativeInteger rdf:type rdfs:Datatype) ]"
                + "[ -> (xsd:boolean rdf:type rdfs:Datatype) ]"
                + "[ -> (xsd:string rdf:type rdfs:Datatype) ]"
                + "[ -> (xsd:anyURI rdf:type rdfs:Datatype) ]"
                + "[ -> (xsd:hexBinary rdf:type rdfs:Datatype) ]"
                + "[ -> (xsd:base64Binary rdf:type rdfs:Datatype) ]"
                + "[ -> (xsd:date rdf:type rdfs:Datatype) ]"
                + "[ -> (xsd:time rdf:type rdfs:Datatype) ]"
                + "[ -> (xsd:dateTime rdf:type rdfs:Datatype) ]"
                + "[ -> (xsd:duration rdf:type rdfs:Datatype) ]"
                + "[ -> (xsd:gDay rdf:type rdfs:Datatype) ]"
                + "[ -> (xsd:gMonth rdf:type rdfs:Datatype) ]"
                + "[ -> (xsd:gYear rdf:type rdfs:Datatype) ]"
                + "[ -> (xsd:gYearMonth rdf:type rdfs:Datatype) ]"
                + "[ -> (xsd:gMonthDay rdf:type rdfs:Datatype) ]"
                + "[ -> (xsd:integer rdfs:subClassOf xsd:decimal) ]" + ""));
        return new GenericRuleReasoner(rules);
    }
}
