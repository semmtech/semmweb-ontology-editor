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
 * A vocabulary for annotating vocabulary descriptions.
 * http://purl.org/vocab/vann/
 * 
 * @author Sander Stolk
 */
public class VANN {
    private static Model model = ModelFactory.createDefaultModel();

    public static final String URI = "http://purl.org/vocab/vann/";
    public static final String NS = URI;

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

    // Properties
    public static final Property changes = property("changes");
    public static final Property example = property("example");
    public static final Property preferredNamespacePrefix = property("preferredNamespacePrefix");
    public static final Property preferredNamespaceUri = property("preferredNamespaceUri");
    public static final Property termGroup = property("termGroup");
    public static final Property usageNote = property("usageNote");

}
