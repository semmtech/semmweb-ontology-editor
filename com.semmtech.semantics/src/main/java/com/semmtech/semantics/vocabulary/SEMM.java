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
public final class SEMM {
    private static Model model = ModelFactory.createDefaultModel();

    public static final String NS = "http://www.semmweb.com/ns/public/2012/09/12/semm/";

    public static final Resource NAMESPACE = model.createResource(NS);

    protected static final Resource resource(String local) {
        return ResourceFactory.createResource(NS + local);
    }

    protected static final Property property(String local) {
        return ResourceFactory.createProperty(NS + local);
    }

    public static final Resource Aspect = resource("Aspect");
    public static final Resource Role = resource("Role");
    public static final Resource AspectPossessor = resource("AspectPossessor");
    public static final Resource PossessedAspect = resource("PossessedAspect");
    public static final Resource Qualification = resource("Qualification");
    public static final Resource PhysicalObject = resource("PhysicalObject");

    public static final Resource Quantification = resource("Quantification");
    public static final Resource Scale = resource("Scale");

    public static final Property hasAspect = property("hasAspect");
    public static final Property hasRole = property("hasRole");
    public static final Property hasObjectRole = property("hasObjectRole");
    public static final Property hasSubjectRole = property("hasSubjectRole");
    public static final Property isQualifiedAs = property("isQualifiedAs");
    public static final Property isRoleOf = property("isRoleOf");
    public static final Property isPossessedAspectOf = property("isPossessedAspectOf");
    public static final Property hasPossessedAspect = property("hasPossessedAspect");

    public static final Property isCorrespodingConceptFor = property("isCorrespodingConceptFor");
    public static final Property hasCorrespondingConcept = property("hasCorrespondingConcept");
    public static final Property isDescribedBy = property("isDescribedBy");

    public static final Property hasPart = property("hasPart");
    public static final Property isPartOf = property("isPartOf");

    public static final Property hasValue = property("hasValue");
    public static final Property hasScale = property("hasScale");
    public static final Property isQuantifiedOnScale = property("isQuantifiedOnScale");
    public static final Property isQualificationOf = property("isQualificationOf");
    public static final Property isNatureOf = property("isNatureOf");

    /**
     * The SEMM vocabulary, expressed for the SPI layer in terms of .graph
     * Nodes.
     */
    public static class Nodes {
        public static final Node Aspect = SEMM.Aspect.asNode();
        public static final Node Role = SEMM.Role.asNode();
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
