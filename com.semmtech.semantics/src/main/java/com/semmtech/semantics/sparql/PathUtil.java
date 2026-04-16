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


import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.query.QueryBuildException;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.sparql.engine.ExecutionContext;
import com.hp.hpl.jena.sparql.path.P_Alt;
import com.hp.hpl.jena.sparql.path.P_Inverse;
import com.hp.hpl.jena.sparql.path.P_Link;
import com.hp.hpl.jena.sparql.path.P_Mod;
import com.hp.hpl.jena.sparql.path.P_OneOrMore1;
import com.hp.hpl.jena.sparql.path.P_Seq;
import com.hp.hpl.jena.sparql.path.P_ZeroOrMore1;
import com.hp.hpl.jena.sparql.path.Path;
import com.hp.hpl.jena.sparql.path.PathPropertyFunction;
import com.hp.hpl.jena.sparql.pfunction.PropFuncArg;
import com.hp.hpl.jena.sparql.pfunction.PropertyFunction;
import com.hp.hpl.jena.sparql.pfunction.PropertyFunctionFactory;
import com.hp.hpl.jena.sparql.pfunction.PropertyFunctionRegistry;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;


public class PathUtil {
    public static final String NS = "http://www.semmweb.com/ns/sparql/paths/";

    /** Property Path: <code>rdfs:subClassOf+</code> */
    public static final String INFERRED_SUBCLASS_OF = NS + "inferredSubClassOf";

    /** Node for Property Path: <code>rdfs:subClassOf+</code> */
    public static final Node subClassOfInferred = getNode(INFERRED_SUBCLASS_OF);

    /** Property Path: <code>rdfs:subClassOf*</code> */
    public static final String SELF_OR_INFERRED_SUBCLASS_OF = NS + "selfOrInferredSubClassOf";

    /** Node for Property Path: <code>rdfs:subClassOf*</code> */
    public static final Node subClassOfAny = getNode(SELF_OR_INFERRED_SUBCLASS_OF);

    /** Property Path: <code>rdfs:subClassOf{2,}</code> */
    public static final String INDIRECT_SUBCLASS_OF = NS + "indirectSubClassOf";

    /** Node for Property Path: <code>rdfs:subClassOf{2,}</code> */
    public static final Node subClassOfIndirect = getNode(INDIRECT_SUBCLASS_OF);

    /** Property Path: <code>rdfs:subPropertyOf+</code> */
    public static final String INFERRED_SUBPROPERTY_OF = NS + "inferredSubPropertyOf";

    /** Node Property Path: <code>rdfs:subPropertyOf+</code> */
    public static final Node subPropertyOfInferred = getNode(INFERRED_SUBPROPERTY_OF);

    /** Property Path: <code>rdfs:subPropertyOf*</code> */
    public static final String SELF_OR_INFERRED_SUBPROPERTY_OF = NS + "selfOrInferredSubPropertyOf";

    /** Node for Property Path: <code>rdfs:subPropertyOf*</code> */
    public static final Node subPropertyOfAny = getNode(SELF_OR_INFERRED_SUBPROPERTY_OF);

    /** Property Path: <code>rdfs:subClassOf{2,}</code> */
    public static final String INDIRECT_SUBPROPERTY_OF = NS + "indirectSubPropertyOf";

    /** Node for Property Path: <code>rdfs:subClassOf{2,}</code> */
    public static final Node subPropertyOfIndirect = getNode(INDIRECT_SUBPROPERTY_OF);

    /** Property Path: <code>rdf:type/rdfs:subClassOf*</code> */
    public static final String IS_INSTANCE_OF = NS + "isInstanceOf";

    /** Node for Property Path: <code>rdf:type/rdfs:subClassOf*</code> */
    public static final Node isInstanceOf = getNode(IS_INSTANCE_OF);

    /** Property Path: <code>rdf:rest* /rdf:first</code> */
    public static final String LIST_MEMBERS = NS + "listMembers";

    /** Node for Property Path: <code>rdf:rest* /rdf:first</code> */
    public static final Node listMembers = getNode(LIST_MEMBERS);

    private static Map<String, Path> pathRegistry;

    private PathUtil() {
    }

    public static String getURI(String pathName) {
        Path path = null;
        if (pathName.equals(INFERRED_SUBCLASS_OF)) {
            path = new P_OneOrMore1(new P_Link(RDFS.subClassOf.asNode()));
        }
        else if (pathName.equals(SELF_OR_INFERRED_SUBCLASS_OF)) {
            path = new P_ZeroOrMore1(new P_Link(RDFS.subClassOf.asNode()));
        }
        else if (pathName.equals(INDIRECT_SUBCLASS_OF)) {
            path = new P_Mod(new P_Link(RDFS.subClassOf.asNode()), 2, P_Mod.UNSET);
        }
        else if (pathName.equals(INFERRED_SUBPROPERTY_OF)) {
            path = new P_OneOrMore1(new P_Link(RDFS.subPropertyOf.asNode()));
        }
        else if (pathName.equals(INDIRECT_SUBPROPERTY_OF)) {
            path = new P_Mod(new P_Link(RDFS.subPropertyOf.asNode()), 2, P_Mod.UNSET);
        }
        else if (pathName.equals(SELF_OR_INFERRED_SUBPROPERTY_OF)) {
            path = new P_ZeroOrMore1(new P_Link(RDFS.subPropertyOf.asNode()));
        }
        else if (pathName.equals(IS_INSTANCE_OF)) {
            path = new P_Seq(new P_Link(RDF.type.asNode()), new P_ZeroOrMore1(new P_Link(
                    RDFS.subClassOf.asNode())));
        }
        else if (pathName.equals(LIST_MEMBERS)) {
            path = new P_Seq(new P_ZeroOrMore1(new P_Link(RDF.rest.asNode())), new P_Link(
                    RDF.first.asNode()));
        }

        if (path == null) {
            return null;
        }
        if (pathRegistry == null) {
            pathRegistry = Maps.newHashMap();
        }
        pathRegistry.put(pathName, path);

        installPath(pathName, path);

        return pathName;
    }

    private static void installPath(String pathName, final Path path) {
        PropertyFunctionFactory pathPropFuncFactory = new PropertyFunctionFactory() {
            /**
             * This overriden method is customized here to prevent a
             * QueryBuildException in case of rdf:nil. The resource rdf:nil is
             * seen as a list, but also a node.
             */
            @Override
            public PropertyFunction create(String uri) {

                return new PathPropertyFunction(path) {

                    @Override
                    public void build(PropFuncArg argSubject, Node predicate,
                            PropFuncArg argObject, ExecutionContext execCxt) {

                        if (argSubject.isList()) {
                            // The next check is added to prevent exception of
                            // rdf:nil.
                            if (!argObject.isNode()) {
                                // But allow rdf:nil.
                                throw new QueryBuildException("List arguments (subject) to "
                                        + predicate.getURI());
                            }
                        }

                        if (argObject.isList()) {
                            if (!argObject.isNode()) {
                                // But allow rdf:nil.
                                throw new QueryBuildException("List arguments (object) to "
                                        + predicate.getURI());
                            }
                        }
                    }
                };
            }
        };
        PropertyFunctionRegistry.get().put(pathName, pathPropFuncFactory);
    }

    public static Node getNode(String pathName) {
        String uri = getURI(pathName);
        return (uri == null) ? null : NodeFactory.createURI(uri);
    }

    public static Path getPath(String pathName) {
        getURI(pathName);
        if (pathRegistry.containsKey(pathName)) {
            return pathRegistry.get(pathName);
        }
        return null;
    }

    /**
     * Given the properties <code>p1, p2, ..., pn</code> returns the following
     * path:
     * 
     * <pre>
     * p1 | p2 | ... | pn
     * </pre>
     * 
     * @return
     */
    public static Path alt(Property... properties) {
        Path[] paths = new Path[properties.length];

        for (int i = 0; i < properties.length; i++) {
            paths[i] = link(properties[i]);
        }

        return alt(paths);
    }

    public static Path alt(List<Path> paths) {
        return alt(paths.toArray(new Path[] {}));
    }

    public static Path alt(Path... paths) {
        if (paths.length == 1) {
            return paths[0];
        }

        return _alt(paths[0], Arrays.copyOfRange(paths, 1, paths.length));
    }

    private static Path _alt(Path path, Path... paths) {
        if (paths.length == 0) {
            return path;
        }

        return _alt(new P_Alt(path, paths[0]), Arrays.copyOfRange(paths, 1, paths.length));
    }

    /**
     * ns:property
     * 
     */
    public static Path link(RDFNode node) {
        return new P_Link(node.asNode());
    }

    /**
     * ^ns:property
     */
    public static Path inv(RDFNode node) {
        return new P_Inverse(link(node));
    }

    /**
     * ns:property*
     */
    public static Path zeroOrMore1(RDFNode node) {
        return new P_ZeroOrMore1(link(node));
    }
}
