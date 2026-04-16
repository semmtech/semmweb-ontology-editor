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
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.Restriction;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.util.iterator.UniqueExtendedIterator;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.semmtech.semantics.sparql.PathUtil;
import com.semmtech.semantics.sparql.QueryBuilder;
import com.semmtech.semantics.util.JenaUtil;


public class OntClassUtil {
    private OntClassUtil() {
    }

    /**
     * Answer an iterator over all of the classes that are super-classes of this
     * class, excluding <code>clazz</code> itself in the result.
     * 
     * @param clazz
     *            The class for which to list its super classes
     * @param direct
     *            Whether to list only the immediate super classes
     */
    public static ExtendedIterator<OntClass> listSuperClasses(OntClass clazz, boolean direct) {
        OntModel model = clazz.getOntModel();

        // Create triple pattern
        Var varS = Var.alloc("superClass");
        Node predicate = (direct) ? RDFS.subClassOf.asNode() : PathUtil
                .getNode(PathUtil.INFERRED_SUBCLASS_OF);
        Triple pattern = Triple.create(clazz.asNode(), predicate, varS);

        // Create query
        QueryBuilder qb = QueryBuilder.createSelect(true);
        qb.addTriplePattern(pattern).addResultVar(varS);

        return createOntClassIterator(model, qb.execSelect(model), varS);
    }

    /**
     * Answer an iterator over all of the classes that are super-classes of
     * <code>clazz</code>, excluding <code>clazz</code> itself in the result.
     * 
     * @param clazz
     *            The class for which to list its super classes
     */
    public static ExtendedIterator<OntClass> listSuperClasses(OntClass clazz) {
        return listSuperClasses(clazz, false);
    }

    /**
     * Answer an iterator over all of the classes that are super-classes of
     * <code>clazz</code>, including <code>clazz</code> itself in the result.
     * 
     * @param clazz
     *            The class for which to list its super classes
     */
    public static ExtendedIterator<OntClass> listSelfAndSuperClasses(OntClass clazz) {
        OntModel model = clazz.getOntModel();

        // Create triple pattern
        Var varS = Var.alloc("superClass");
        Triple pattern = Triple.create(clazz.asNode(),
                PathUtil.getNode(PathUtil.SELF_OR_INFERRED_SUBCLASS_OF), varS);

        // Create query
        QueryBuilder qb = QueryBuilder.createSelect(true);
        qb.addTriplePattern(pattern).addResultVar(varS);

        return createOntClassIterator(model, qb.execSelect(model), varS);
    }

    /**
     * Answer an iterator over all of the classes that are sub-classes of this
     * class, excluding <code>clazz</code> itself in the result.
     * 
     * @param clazz
     *            The class for which to list its sub classes
     * @param direct
     *            Whether to list only the immediate sub classes (without
     *            excluding those sub classes that also exist as a more specific
     *            sub class, as is done in listSubClasses() implementations of
     *            OntClass.class ).
     */
    public static ExtendedIterator<OntClass> listSubClasses(OntClass clazz, boolean direct) {
        OntModel model = clazz.getOntModel();

        // Create triple pattern
        Var varS = Var.alloc("subClass");
        Node predicate = (direct) ? RDFS.subClassOf.asNode() : PathUtil
                .getNode(PathUtil.INFERRED_SUBCLASS_OF);
        Triple pattern = Triple.create(varS, predicate, clazz.asNode());

        // Create query
        QueryBuilder qb = QueryBuilder.createSelect(true);
        qb.addTriplePattern(pattern).addResultVar(varS);

        return createOntClassIterator(model, qb.execSelect(model), varS);
    }

    /**
     * Answer an iterator over all of the classes that are sub-classes of this
     * class, excluding <code>clazz</code> itself in the result.
     * 
     * @param clazz
     *            The class for which to list its sub classes
     */
    public static ExtendedIterator<OntClass> listSubClasses(OntClass clazz) {
        return listSubClasses(clazz, false);
    }

    /**
     * Answer an iterator over all of the classes that are sub-classes of this
     * class, including <code>clazz</code> itself in the result.
     * 
     * @param clazz
     *            The class for which to list its sub classes
     */
    public static ExtendedIterator<OntClass> listSelfAndSubClasses(OntClass clazz) {
        OntModel model = clazz.getOntModel();

        // Create triple pattern
        Var varS = Var.alloc("subClass");
        Triple pattern = Triple.create(varS,
                PathUtil.getNode(PathUtil.SELF_OR_INFERRED_SUBCLASS_OF), clazz.asNode());

        // Create query
        QueryBuilder qb = QueryBuilder.createSelect(true);
        qb.addTriplePattern(pattern).addResultVar(varS);

        return createOntClassIterator(model, qb.execSelect(model), varS);
    }

    public static Map<Restriction, OntClass> listRestrictionsByClass(OntClass clazz) {
        Map<Restriction, OntClass> result = Maps.newHashMap();
        OntModel model = clazz.getOntModel();

        Var varC = Var.alloc("c");
        Var varR = Var.alloc("r");
        Triple t1 = Triple.create(clazz.asNode(),
                PathUtil.getNode(PathUtil.SELF_OR_INFERRED_SUBCLASS_OF), varC);
        Triple t2 = Triple.create(varC, RDFS.subClassOf.asNode(), varR);
        Triple t3 = Triple.create(varR, PathUtil.getNode(PathUtil.IS_INSTANCE_OF),
                OWL.Restriction.asNode());

        QueryBuilder qb = QueryBuilder.createSelect(true);
        qb.addTriplePatterns(t1, t2, t3).addResultVars(varC, varR);

        for (ResultSet iter = qb.execSelect(model); iter.hasNext();) {
            QuerySolution qs = iter.next();
            try {
                Resource cResource = qs.getResource(varC.getName());
                Resource rResource = qs.getResource(varR.getName());
                OntClass ownerClass = JenaUtil.asOntClass(cResource, model);
                Restriction restriction = JenaUtil.asRestriction(rResource, model);
                result.put(restriction, ownerClass);
            }
            catch (Exception e) {
                // literals cannot be added
            }
        }
        return result;
    }

    public static ExtendedIterator<Restriction> listRestrictions(OntClass clazz,
            Property onProperty, boolean localOnly) {
        OntModel model = clazz.getOntModel();

        Var varR = Var.alloc("r");
        Node predicate = (localOnly) ? RDFS.subClassOf.asNode() : PathUtil
                .getNode(PathUtil.INFERRED_SUBCLASS_OF);
        Triple t1 = Triple.create(clazz.asNode(), predicate, varR);
        Triple t2 = Triple.create(varR, PathUtil.getNode(PathUtil.IS_INSTANCE_OF),
                OWL.Restriction.asNode());
        QueryBuilder qb = QueryBuilder.createSelect(true).addTriplePatterns(t1, t2);
        if (onProperty != null) {
            Triple t3 = Triple.create(varR, OWL.onProperty.asNode(), onProperty.asNode());
            qb.addTriplePattern(t3);
        }
        qb.addResultVar(varR);

        return createRestrictionIterator(model, qb.execSelect(model), varR);
    }

    private static ExtendedIterator<OntClass> createOntClassIterator(OntModel model,
            ResultSet iter, Var var) {
        String varName = var.getName();
        List<OntClass> classes = Lists.newArrayList();
        while (iter.hasNext()) {
            QuerySolution querySolution = iter.next();
            try {
                Resource clazz = querySolution.getResource(varName);
                classes.add(JenaUtil.asOntClass(clazz, model));
            }
            catch (Exception e) {
                // literals cannot be added
            }
        }
        return UniqueExtendedIterator.create(classes.iterator());
    }

    private static ExtendedIterator<Restriction> createRestrictionIterator(OntModel model,
            ResultSet iter, Var var) {
        String varName = var.getName();
        List<Restriction> restrictions = Lists.newArrayList();
        while (iter.hasNext()) {
            QuerySolution querySolution = iter.next();
            try {
                Resource restriction = querySolution.getResource(varName);
                restrictions.add(JenaUtil.asRestriction(restriction, model));
            }
            catch (Exception e) {
                // literals cannot be added
            }
        }
        return UniqueExtendedIterator.create(restrictions.iterator());
    }
}
