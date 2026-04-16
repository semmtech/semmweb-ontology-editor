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

package com.semmtech.plugin.semmweb.core.jena.vocabulary;


import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.OntDocManagerVocab;


/**
 * 
 * @author Sander Stolk
 */
public class WorkspaceDocManagerVocab extends OntDocManagerVocab {
    /** The RDF model that holds the vocabulary terms */
    private static Model m_model = ModelFactory.createDefaultModel();

    /** The namespace of the vocabulary as a string ({@value} ) */
    public static final String NS = "http://com.semmtech.plugin.semmweb.core/jena.vocabulary#";

    /**
     * The namespace of the vocabulary as a string
     * 
     * @see #NS
     */
    public static String getURI() {
        return NS;
    }

    /** The namespace of the vocabulary as a resource */
    public static final Resource NAMESPACE = m_model.createResource(NS);

    // Vocabulary properties
    // /////////////////////////

    /**
     * The resolvable URL outside of the workspace that an alternative copy of
     * the ontology document may be fetched from
     */
    public static final Property externalAltURL = m_model.createProperty(NS + "externalAltURL");

    /**
     * The resolvable URL within the workspace that an alternative copy of the
     * ontology document may be fetched from
     */
    public static final Property workspaceAltURL = m_model.createProperty(NS + "workspaceAltURL");
}
