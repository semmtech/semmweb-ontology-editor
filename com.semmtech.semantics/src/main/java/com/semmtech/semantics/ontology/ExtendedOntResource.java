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

package com.semmtech.semantics.ontology;


import java.util.List;

import com.google.common.collect.Lists;
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.enhanced.EnhGraph;
import com.hp.hpl.jena.enhanced.EnhNode;
import com.hp.hpl.jena.enhanced.Implementation;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.ontology.ConversionException;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.impl.OntResourceImpl;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.reasoner.InfGraph;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.util.iterator.UniqueExtendedIterator;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.semmtech.NotImplementedException;
import com.semmtech.semantics.sparql.PathUtil;
import com.semmtech.semantics.sparql.QueryBuilder;
import com.semmtech.semantics.util.JenaUtil;


public class ExtendedOntResource extends OntResourceImpl {
    // Constants
    // ////////////////////////////////

    /** List of namespaces that are reserved for known ontology languages */
    public static final String[] KNOWN_LANGUAGES = new String[] { OWL.NS, RDF.getURI(),
            RDFS.getURI(), XSDDatatype.XSD };

    // Static variables
    // ////////////////////////////////

    /**
     * A factory for generating OntResource facets from nodes in enhanced
     * graphs. Note: should not be invoked directly by user code: use
     * {@link com.hp.hpl.jena.rdf.model.RDFNode#as as()} instead.
     */
    public static Implementation factory = new Implementation() {
        @Override
        public EnhNode wrap(Node n, EnhGraph eg) {
            if (canWrap(n, eg)) {
                return new ExtendedOntResource(n, eg);
            }
            throw new ConversionException("Cannot convert node " + n.toString()
                    + " to ExtendedOntResource");
        }

        @Override
        public boolean canWrap(Node node, EnhGraph eg) {
            // node will support being an OntResource facet if it is a uri or
            // bnode
            return (eg instanceof OntModel) && (node.isURI() || node.isBlank());
        }
    };

    // Instance variables
    // ////////////////////////////////

    // Constructors
    // ////////////////////////////////

    /**
     * <p>
     * Construct an ontology resource represented by the given node in the given
     * graph.
     * </p>
     * 
     * @param n
     *            The node that represents the resource
     * @param g
     *            The enh graph that contains n
     */
    public ExtendedOntResource(Node n, EnhGraph g) {
        super(n, g);
    }

    // Functions
    // ////////////////////////////////

    /**
     * <p>
     * Answer the <code>rdf:type</code> (ie the class) of this resource. If
     * there is more than one type for this resource, the return value will be
     * one of the values, but it is not specified which one (nor that it will
     * consistently be the same one each time). Equivalent to
     * <code>getRDFType( false )</code>.
     * </p>
     * 
     * @return A resource that is the rdf:type for this resource, or one of them
     *         if more than one is defined.
     */
    @Override
    public Resource getRDFType() {
        return getRDFType(false);
    }

    /**
     * <p>
     * Answer the <code>rdf:type</code> (ie the class) of this resource. If
     * there is more than one type for this resource, the return value will be
     * one of the values, but it is not specified which one (nor that it will
     * consistently be the same one each time).
     * </p>
     * 
     * @param direct
     *            If true, only consider the direct types of this resource, and
     *            not the super-classes of the type(s).
     * @return A resource that is the rdf:type for this resource, or one of them
     *         if more than one is defined.
     */
    @Override
    public Resource getRDFType(boolean direct) {
        ExtendedIterator<Resource> i = null;
        try {
            i = listRDFTypes(direct);
            return i.hasNext() ? i.next() : null;
        }
        finally {
            if (i != null)
                i.close();
        }
    }

    /**
     * <p>
     * Answer an iterator over the RDF classes to which this resource belongs.
     * </p>
     * 
     * @param direct
     *            If true, only answer those resources that are direct types of
     *            this resource, not the super-classes of the class etc.
     * @return An iterator over the set of this resource's classes, each of
     *         which will be a {@link Resource}.
     */
    @Override
    public ExtendedIterator<Resource> listRDFTypes(boolean direct) {
        OntModel model = getOntModel();

        if (direct && workingOnInfGraph()) {
            throw new NotImplementedException(
                    "Cannot yet list direct RDF type(s) when working with an inference model");
        }

        Var varT = Var.alloc("t");
        Node predicate = (direct) ? RDF.type.asNode() : PathUtil.getNode(PathUtil.IS_INSTANCE_OF);
        Triple t1 = Triple.create(asNode(), predicate, varT);
        QueryBuilder qb = QueryBuilder.createSelect(true).addTriplePattern(t1).addResultVar(varT);

        List<Resource> types = Lists.newArrayList();
        for (ResultSet iter = qb.execSelect(model); iter.hasNext();) {
            QuerySolution querySolution = iter.next();
            try {
                Resource type = querySolution.getResource(varT.getName());
                types.add(JenaUtil.asOntResource(type, model));
            }
            catch (Throwable t) {
                // discarding results that are literals
            }
        }
        return UniqueExtendedIterator.create(types.iterator());
    }

    /**
     * <p>
     * Answer true if this resource is a member of the class denoted by the
     * given URI.
     * </p>
     * 
     * @param uri
     *            Denotes the URI of a class to which this value may belong
     * @return True if this resource has the given class as one of its
     *         <code>rdf:type</code>'s.
     */
    @Override
    public boolean hasRDFType(String uri) {
        return hasRDFType(getModel().getResource(uri));
    }

    /**
     * <p>
     * Answer true if this resource is a member of the class denoted by the
     * given class resource. Includes all available types, so is equivalent to
     * <code><pre>
     * hasRDF( ontClass, false );
     * </pre></code>
     * </p>
     * 
     * @param ontClass
     *            Denotes a class to which this value may belong
     * @return True if this resource has the given class as one of its
     *         <code>rdf:type</code>'s.
     */
    @Override
    public boolean hasRDFType(Resource ontClass) {
        return hasRDFType(ontClass, false);
    }

    /**
     * <p>
     * Answer true if this resource is a member of the class denoted by the
     * given class resource.
     * </p>
     * 
     * @param ontClass
     *            Denotes a class to which this value may belong
     * @param direct
     *            If true, only consider the direct types of this resource,
     *            ignoring the super-classes of the stated types.
     * @return True if this resource has the given class as one of its
     *         <code>rdf:type</code>'s.
     */
    @Override
    public boolean hasRDFType(Resource ontClass, boolean direct) {
        OntModel model = getOntModel();

        if (direct && workingOnInfGraph()) {
            throw new NotImplementedException(
                    "Cannot yet list direct RDF type(s) when working with an inference model");
        }

        Node predicate = (direct) ? RDF.type.asNode() : PathUtil.getNode(PathUtil.IS_INSTANCE_OF);
        Triple t1 = Triple.create(asNode(), predicate, ontClass.asNode());
        QueryBuilder qb = QueryBuilder.createAsk().addTriplePattern(t1);
        return qb.execAsk(model);
    }

    // Conversion test methods
    // TODO: override these for our own implementations, without using canAs().

    /** Returns whether <code>resource</code> has type rdfs:Class. */
    @Override
    public boolean isClass() {
        OntModel ontModel = getOntModel();
        Triple t = Triple.create(asNode(), PathUtil.getNode(PathUtil.IS_INSTANCE_OF),
                RDFS.Class.asNode());
        QueryBuilder qb = QueryBuilder.createAsk().addTriplePattern(t);
        return qb.execAsk(ontModel);
    }

    // Other methods

    protected boolean workingOnInfGraph() {
        // are we working on an inference graph?
        OntModel m = (OntModel) getGraph();
        return (m.getGraph() instanceof InfGraph);
    }
}
