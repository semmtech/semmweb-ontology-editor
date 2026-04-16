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
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.util.iterator.UniqueExtendedIterator;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.semmtech.semantics.sparql.PathUtil;
import com.semmtech.semantics.sparql.QueryBuilder;
import com.semmtech.semantics.util.JenaUtil;


public class OntResourceUtil {
    public static ExtendedIterator<OntResource> listRDFTypes(OntResource resource, boolean direct) {
        OntModel model = resource.getOntModel();

        Var varT = Var.alloc("t");
        Node predicate = (direct) ? RDF.type.asNode() : PathUtil.getNode(PathUtil.IS_INSTANCE_OF);
        Triple t1 = Triple.create(resource.asNode(), predicate, varT);
        QueryBuilder qb = QueryBuilder.createSelect(true).addTriplePattern(t1).addResultVar(varT);

        List<OntResource> types = Lists.newArrayList();
        for (ResultSet iter = qb.execSelect(model); iter.hasNext();) {
            QuerySolution querySolution = iter.next();
            Resource type = querySolution.getResource(varT.getName());
            types.add(JenaUtil.asOntResource(type, model));
        }
        return UniqueExtendedIterator.create(types.iterator());
    }

    /** Returns whether <code>resource</code> has type rdfs:Class. */
    public static boolean isClass(OntResource resource) {
        OntModel ontModel = resource.getOntModel();
        Triple t = Triple.create(resource.asNode(), PathUtil.getNode(PathUtil.IS_INSTANCE_OF),
                RDFS.Class.asNode());
        QueryBuilder qb = QueryBuilder.createAsk().addTriplePattern(t);
        return qb.execAsk(ontModel);
    }

    /** Returns whether <code>resource</code> has type owl:Class. */
    public static boolean isOWLClass(OntResource resource) {
        OntModel ontModel = resource.getOntModel();
        Triple t = Triple.create(resource.asNode(), PathUtil.getNode(PathUtil.IS_INSTANCE_OF),
                OWL.Class.asNode());
        QueryBuilder qb = QueryBuilder.createAsk().addTriplePattern(t);
        return qb.execAsk(ontModel);
    }
}
