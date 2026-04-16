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
 * SKOS Simple Knowledge Organization System eXtension for Labels (SKOS-XL)
 * 
 * @author Mike Henrichs
 * 
 */
public class SKOSXL {
    private static Model model = ModelFactory.createDefaultModel();

    public static final String URI = "http://www.w3.org/2008/05/skos-xl#";
    public static final String NS = "http://www.w3.org/2008/05/skos-xl#";

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
     * Label (see http://www.w3.org/TR/skos-reference/#xl-Label)
     */
    public static final Resource Label = resource("Label");
    /**
     * alternative label
     */
    public static final Property altLabel = property("altLabel");
    /**
     * hidden label
     */
    public static final Property hiddenLabel = property("hiddenLabel");
    /**
     * label relation
     */
    public static final Property labelRelation = property("labelRelation");
    /**
     * literal form
     */
    public static final Property literalForm = property("literalForm");
    /**
     * preferred label
     */
    public static final Property prefLabel = property("prefLabel");
}
