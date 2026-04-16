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

package com.semmtech.semantics.sparql;


import java.util.List;

import com.google.common.collect.Lists;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.vocabulary.RDF;


/**
 * Util class for use with Triple objects.
 * 
 * @author Mike Henrichs
 * @author Sander Stolk
 */
public final class Triples {

    /**
     * Creates a triples from the given resources.
     */
    public static Triple create(Resource s, Property p, RDFNode o) {
        return new Triple(s.asNode(), p.asNode(), o.asNode());
    }

    /**
     * Creates a triples from the given node and resources.
     */
    public static Triple create(Node s, Property p, RDFNode o) {
        return new Triple(s, p.asNode(), o.asNode());
    }

    /**
     * Creates a triples from the given resources and node.
     */
    public static Triple create(Resource s, Node p, RDFNode o) {
        return new Triple(s.asNode(), p, o.asNode());
    }

    /**
     * Creates a triples from the given resources and node.
     */
    public static Triple create(Resource s, Property p, Node o) {
        return new Triple(s.asNode(), p.asNode(), o);
    }

    /**
     * Creates a triples from the given nodes and resource.
     */
    public static Triple create(Node s, Node p, RDFNode o) {
        return new Triple(s, p, o.asNode());
    }

    /**
     * Creates a triples from the given resource and nodes.
     */
    public static Triple create(Resource s, Node p, Node o) {
        return new Triple(s.asNode(), p, o);
    }

    /**
     * Creates a triples from the given nodes and resource.
     */
    public static Triple create(Node s, Property p, Node o) {
        return new Triple(s, p.asNode(), o);
    }

    /**
     * Creates a triples from the given nodes.
     */
    public static Triple create(Node s, Node p, Node o) {
        return new Triple(s, p, o);
    }

    /**
     * Creates triples from the given resource, node, and the node list. The
     * node list will be turned into a list as defined in RDF and used as
     * object.
     */
    public static List<Triple> create(Resource s, Node p, List<? extends Node> o) {
        return create(s.asNode(), p, o);
    }

    /**
     * Creates triples from the given node, property, and the node list. The
     * node list will be turned into a list as defined in RDF and used as
     * object.
     */
    public static List<Triple> create(Node s, Property p, List<? extends Node> o) {
        return create(s, p.asNode(), o);
    }

    /**
     * Creates triples from the given resources and the node list. The node list
     * will be turned into a list as defined in RDF and used as object.
     */
    public static List<Triple> create(Resource s, Property p, List<? extends Node> o) {
        return create(s.asNode(), p.asNode(), o);
    }

    /**
     * Creates triples from the given nodes and the node list. The node list
     * will be turned into a list as defined in RDF and used as object.
     */
    public static List<Triple> create(Node s, Node p, List<? extends Node> o) {
        List<Triple> result = createTriples(o);
        if (result == null) {
            result = Lists.newArrayList();
            result.add(Triples.create(s, p, RDF.nil));
        }
        else {
            result.add(0, Triples.create(s, p, result.get(0).getSubject()));
        }
        return result;
    }

    private static List<Triple> createTriples(List<? extends Node> nodes) {
        List<Triple> result = Lists.newArrayList();
        if (nodes.isEmpty()) {
            return result;
        }

        double randomNumber = Math.random();
        Var previousListNode = Var.alloc("_" + randomNumber);
        result.add(Triples.create(previousListNode, RDF.first, nodes.get(0)));
        for (int i = 1; i < nodes.size(); i++) {
            Node nextNode = nodes.get(i);
            Var nextListNode = Var.alloc("_" + (randomNumber + i));
            result.add(Triples.create(previousListNode, RDF.rest, nextListNode));
            result.add(Triples.create(nextListNode, RDF.first, nextNode));
            previousListNode = nextListNode;
        }
        result.add(Triples.create(previousListNode, RDF.rest, RDF.nil));
        return result;
    }
}
