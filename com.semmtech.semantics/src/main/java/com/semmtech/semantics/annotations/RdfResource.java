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

package com.semmtech.semantics.annotations;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.openrdf.model.vocabulary.OWL;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.RDFS;

import com.hp.hpl.jena.vocabulary.DCTerms;
import com.semmtech.semantics.vocabulary.SKOS;


@Target(value = { ElementType.FIELD })
@Retention(value = RetentionPolicy.RUNTIME)
public @interface RdfResource {

    PlainLiteral[] labels() default {};

    PlainLiteral[] comments() default {};

    PropertyValue[] properties() default {};

    String typeUri() default "";

    boolean ignore() default false;

    static final String RDF_PROPERTY = RDF.NAMESPACE + "Property";

    static final String RDFS_RESOURCE = RDFS.NAMESPACE + "Resource";
    static final String RDFS_CLASS = RDFS.NAMESPACE + "Class";
    static final String RDFS_DOMAIN = RDFS.NAMESPACE + "domain";
    static final String RDFS_RANGE = RDFS.NAMESPACE + "range";
    static final String RDFS_SUB_PROPERTY_OF = RDFS.NAMESPACE + "subPropertyOf";

    static final String OWL_CLASS = OWL.NAMESPACE + "Class";
    static final String OWL_THING = OWL.NAMESPACE + "Thing";
    static final String OWL_ONTOLOGY = OWL.NAMESPACE + "Ontology";

    static final String DCTERMS_IDENTIFIER = DCTerms.NS + "identifier";

    static final String SKOS_CONCEPT = SKOS.NS + "Concept";
    static final String SKOS_COLLECTION = SKOS.NS + "Collection";
    static final String SKOS_SEMANTIC_RELATION = SKOS.NS + "semanticRelation";
}
