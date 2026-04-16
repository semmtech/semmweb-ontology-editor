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

package com.semmtech.semantics.util;


import com.hp.hpl.jena.enhanced.EnhGraph;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.ontology.Restriction;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.impl.PropertyImpl;
import com.semmtech.semantics.ontology.ExtendedIndividual;
import com.semmtech.semantics.ontology.ExtendedOntClass;
import com.semmtech.semantics.ontology.ExtendedOntProperty;
import com.semmtech.semantics.ontology.ExtendedOntResource;
import com.semmtech.semantics.ontology.ExtendedRestriction;


public class JenaUtil {
    public static OntResource asOntResource(Resource resource, OntModel model) {
        return new ExtendedOntResource(resource.asNode(), (EnhGraph) model);
    }

    public static OntClass asOntClass(OntResource resource) {
        return new ExtendedOntClass(resource.asNode(), (EnhGraph) resource.getOntModel());
    }

    public static OntClass asOntClass(Resource resource, OntModel model) {
        return new ExtendedOntClass(resource.asNode(), (EnhGraph) model);
    }

    public static Restriction asRestriction(OntResource resource) {
        return new ExtendedRestriction(resource.asNode(), (EnhGraph) resource.getOntModel());
    }

    public static Restriction asRestriction(Resource resource, OntModel model) {
        return new ExtendedRestriction(resource.asNode(), (EnhGraph) model);
    }

    public static Property asProperty(OntResource resource) {
        return new PropertyImpl(resource.asNode(), (EnhGraph) resource.getOntModel());
    }

    public static Property asProperty(Resource resource, OntModel model) {
        return new PropertyImpl(resource.asNode(), (EnhGraph) model);
    }

    public static OntProperty asOntProperty(OntResource resource) {
        return new ExtendedOntProperty(resource.asNode(), (EnhGraph) resource.getOntModel());
    }

    public static OntProperty asOntProperty(Resource resource, OntModel model) {
        return new ExtendedOntProperty(resource.asNode(), (EnhGraph) model);
    }

    public static Individual asIndividual(OntResource resource) {
        return new ExtendedIndividual(resource.asNode(), (EnhGraph) resource.getOntModel());
    }

    public static Individual asIndividual(Resource resource, OntModel model) {
        return new ExtendedIndividual(resource.asNode(), (EnhGraph) model);
    }
}
