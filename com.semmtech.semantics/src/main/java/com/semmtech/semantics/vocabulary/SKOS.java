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
 * SKOS Simple Knowledge Organization System
 * 
 * @author Mike Henrichs
 */
public class SKOS {
    private static Model model = ModelFactory.createDefaultModel();

    public static final String URI = "http://www.w3.org/2004/02/skos/core#";
    public static final String NS = "http://www.w3.org/2004/02/skos/core#";

    public static final Resource NAMESPACE = model.createResource(NS);

    public static final String getURI() {
        return URI;
    }

    protected static final Resource resource(String local) {
        return ResourceFactory.createResource(NS + local);
    }

    protected static final Property property(String local) {
        return ResourceFactory.createProperty(NS + local);
    }

    /**
     * Collection
     */
    public static final Resource Collection = resource("Collection");
    /**
     * Concept
     */
    public static final Resource Concept = resource("Concept");
    /**
     * Concept Scheme
     */
    public static final Resource ConceptScheme = resource("ConceptScheme");
    /**
     * Ordered Collection
     */
    public static final Resource OrderedCollection = resource("OrderedCollection");

    /**
     * alternative label
     */
    public static final Property altLabel = property("altLabel");
    /**
     * has broader match
     */
    public static final Property broadMatch = property("broadMatch");
    /**
     * has broader
     */
    public static final Property broader = property("broader");
    /**
     * has broader transitive
     */
    public static final Property broaderTransitive = property("broaderTransitive");
    /**
     * skos:changeNote documents fine-grained changes to a concept, for the
     * purposes of administration and maintenance.
     */
    public static final Property changeNote = property("changeNote");
    /**
     * has close match
     */
    public static final Property closeMatch = property("closeMatch");
    /**
     * skos:definition supplies a complete explanation of the intended meaning
     * of a concept.
     */
    public static final Property definition = property("definition");
    /**
     * skos:editorialNote supplies information that is an aid to administrative
     * housekeeping, such as reminders of editorial work still to be done, or
     * warnings in the event that future editorial changes might be made
     */
    public static final Property editorialNote = property("editorialNote");
    /**
     * has exact match
     */
    public static final Property exactMatch = property("exactMatch");
    /**
     * skos:example supplies an example of the use of a concept.
     */
    public static final Property example = property("example");
    /**
     * label
     */
    public static final Property hasTopConcept = property("hasTopConcept");
    /**
     * hidden label
     */
    public static final Property hiddenLabel = property("hiddenLabel");
    /**
     * skos:historyNote describes significant changes to the meaning or the form
     * of a concept.
     */
    public static final Property historyNote = property("historyNote");
    /**
     * is in scheme
     */
    public static final Property inScheme = property("inScheme");
    /**
     * is in mapping relation with
     */
    public static final Property mappingRelation = property("mappingRelation");
    /**
     * has member
     */
    public static final Property member = property("member");
    /**
     * has member list
     */
    public static final Property memberList = property("memberList");
    /**
     * has narrower match
     */
    public static final Property narrowMatch = property("narrowMatch");
    /**
     * has narrower
     */
    public static final Property narrower = property("narrower");
    /**
     * has narrower transitive
     */
    public static final Property narrowerTransitive = property("narrowerTransitive");
    /**
     * notation
     */
    public static final Property notation = property("notation");
    /**
     * note
     */
    public static final Property note = property("note");
    /**
     * preferred label
     */
    public static final Property prefLabel = property("prefLabel");
    /**
     * has related
     */
    public static final Property related = property("related");
    /**
     * has related match
     */
    public static final Property relatedMatch = property("relatedMatch");
    /**
     * skos:scopeNote supplies some, possibly partial, information about the
     * intended meaning of a concept, especially as an indication of how the use
     * of a concept is limited in indexing practice.
     */
    public static final Property scopeNote = property("scopeNote");
    /**
     * is in semantic relation with
     */
    public static final Property semanticRelation = property("semanticRelation");
    /**
     * is top concept in scheme
     */
    public static final Property topConceptOf = property("topConceptOf");
}
