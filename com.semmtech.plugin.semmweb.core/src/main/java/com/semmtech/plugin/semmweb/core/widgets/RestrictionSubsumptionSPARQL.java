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

package com.semmtech.plugin.semmweb.core.widgets;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.common.base.Preconditions;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.ontology.Restriction;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.expr.E_Equals;
import com.hp.hpl.jena.sparql.expr.E_GreaterThan;
import com.hp.hpl.jena.sparql.expr.E_LessThan;
import com.hp.hpl.jena.sparql.expr.E_NotEquals;
import com.hp.hpl.jena.sparql.expr.E_NotExists;
import com.hp.hpl.jena.sparql.expr.ExprVar;
import com.hp.hpl.jena.sparql.expr.nodevalue.NodeValueInteger;
import com.hp.hpl.jena.sparql.syntax.ElementFilter;
import com.hp.hpl.jena.sparql.syntax.ElementGroup;
import com.hp.hpl.jena.sparql.syntax.ElementUnion;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.OWL2;
import com.semmtech.semantics.sparql.PathUtil;
import com.semmtech.semantics.sparql.QueryBuilder;
import com.semmtech.semantics.sparql.Triples;


/**
 * Restriction subsumption is when a restriction can be subsumed by another
 * restriction. In other words, one can say that a restriction S is subsumed by
 * a restriction L if L makes S redundant. This can happens if L is the same, or
 * more specific, than S. An example could be an OWL hasValue restriction R
 * (requiring a single specific value for a given property), which subsumes an
 * OWL minCardinality 1 restriction (S) for the property in question.
 * 
 * @author Simone Rondelli
 * 
 */
public class RestrictionSubsumptionSPARQL {

    private Model model;

    public RestrictionSubsumptionSPARQL(Model model) {
        this.model = model;
    }

    public boolean isSubsumed(Restriction superRes, Restriction localRes) {
        QueryBuilder qb = QueryBuilder.createAsk();

        Node superResNode = superRes.asNode();
        Node localResNode = localRes.asNode();

        ElementGroup rootElement = generateIsSubsumedElement(superResNode, localResNode);

        qb.addPattern(rootElement);
        return executeAsk(qb.getQuery());
    }

    public ElementGroup generateIsSubsumedElement(Node superResNode, Node localResNode) {
        ElementGroup rootElement = new ElementGroup();

        Var superProperty = Var.alloc("superProperty");
        Var localProperty = Var.alloc("localProperty");

        rootElement.addTriplePattern(Triples.create(superResNode, OWL.onProperty, superProperty));
        rootElement.addTriplePattern(Triples.create(localResNode, OWL.onProperty, localProperty));

        rootElement.addElementFilter(new ElementFilter(new E_Equals(new ExprVar(localProperty),
                new ExprVar(superProperty))));

        ElementGroup allValuesFromElement = allValuesFrom(superResNode, localResNode);
        rootElement.addElementFilter(new ElementFilter(new E_NotExists(allValuesFromElement)));

        ElementGroup someValuesFromElement = someValuesFrom(superResNode, localResNode);
        rootElement.addElementFilter(new ElementFilter(new E_NotExists(someValuesFromElement)));

        ElementGroup hasValueElement = hasValue(superResNode, localResNode);
        rootElement.addElementFilter(new ElementFilter(new E_NotExists(hasValueElement)));

        ElementGroup cardinalityElement = cardinality(superResNode, localResNode);
        rootElement.addElementFilter(new ElementFilter(new E_NotExists(cardinalityElement)));

        ElementGroup minCardinalityElement = minCardinality(superResNode, localResNode);
        rootElement.addElementFilter(new ElementFilter(new E_NotExists(minCardinalityElement)));

        ElementGroup maxCardinalityElement = maxCardinality(superResNode, localResNode);
        rootElement.addElementFilter(new ElementFilter(new E_NotExists(maxCardinalityElement)));

        ElementGroup qualifiedCardinalityElement = qualifiedCardinality(superResNode, localResNode);
        rootElement
                .addElementFilter(new ElementFilter(new E_NotExists(qualifiedCardinalityElement)));

        ElementGroup minQualifiedCardinalityElement = minQualifiedCardinality(superResNode,
                localResNode);
        rootElement.addElementFilter(new ElementFilter(new E_NotExists(
                minQualifiedCardinalityElement)));

        ElementGroup maxQualifiedCardinalityElement = maxQualifiedCardinality(superResNode,
                localResNode);
        rootElement.addElementFilter(new ElementFilter(new E_NotExists(
                maxQualifiedCardinalityElement)));

        return rootElement;
    }

    private ElementGroup allValuesFrom(Node superRes, Node localRes) {
        Var allValuesFromSuperVar = Var.alloc("allValuesFromS");
        Triple allValuesFromSuperTriple = Triples.create(superRes, OWL.allValuesFrom,
                allValuesFromSuperVar);

        SuperMatch allValuesFromSuper = new SuperMatch(allValuesFromSuperTriple);

        // AllValuesFrom - S.onClass >= L.onClass
        Var allValuesFromLocalVar = Var.alloc("allValuesFromL");
        Triple allValuesFromLocalTriple = Triples.create(localRes, OWL.allValuesFrom,
                allValuesFromLocalVar);

        SubMatch allValuesFromLocal = new SubMatch(allValuesFromSuper, allValuesFromLocalTriple);

        ElementGroup eg = new ElementGroup();
        eg.addTriplePattern(allValuesFromSuperTriple);
        eg.addTriplePattern(allValuesFromLocalTriple);
        // subClassOfInferred should not be used because, after inference, every
        // class is subclass of itself
        ElementGroup classGroup = new ElementGroup();
        classGroup.addTriplePattern(allValuesFromLocalTriple);
        classGroup.addTriplePattern(allValuesFromSuperTriple);
        classGroup.addTriplePattern(Triples.create(allValuesFromLocalVar, PathUtil.subClassOfAny,
                allValuesFromSuperVar));
        eg.addElementFilter(new ElementFilter(new E_NotExists(classGroup)));

        allValuesFromLocal.setFilterElement(eg);

        // Cardinality - L.# = 0
        Var cardinalityLocalVar = Var.alloc("cardinalityL");
        Triple cardinalityLocalTriple = Triples.create(localRes, OWL.cardinality,
                cardinalityLocalVar);

        SubMatch cardinalityLocal = new SubMatch(allValuesFromSuper, cardinalityLocalTriple);

        eg = new ElementGroup();
        eg.addTriplePattern(cardinalityLocalTriple);
        eg.addElementFilter(new ElementFilter(new E_NotEquals(new ExprVar(cardinalityLocalVar),
                new NodeValueInteger(0))));

        cardinalityLocal.setFilterElement(eg);

        // MaxCardinality - L.# = 0
        Var maxCardinalityLocalVar = Var.alloc("maxCardinalityL");
        Triple maxCardinalityLocalTriple = Triples.create(localRes, OWL.maxCardinality,
                maxCardinalityLocalVar);

        SubMatch maxCardinalityLocal = new SubMatch(allValuesFromSuper, maxCardinalityLocalTriple);

        eg = new ElementGroup();
        eg.addTriplePattern(maxCardinalityLocalTriple);
        eg.addElementFilter(new ElementFilter(new E_NotEquals(new ExprVar(maxCardinalityLocalVar),
                new NodeValueInteger(0))));

        maxCardinalityLocal.setFilterElement(eg);

        // QualifiedCardinality - L.# = 0 & S.onClass <= L.onClass
        Var qualifiedCardinalityLocalVar = Var.alloc("qualifiedCardinalityL");
        Var onClassLocalVar = Var.alloc("onClassL");
        Triple qualifiedCardinalityLocalTriple = Triples.create(localRes,
                OWL2.qualifiedCardinality, qualifiedCardinalityLocalVar);
        Triple onClassLocalTriple = Triples.create(localRes, OWL2.onClass, onClassLocalVar);

        SubMatch qualifiedCardinality = new SubMatch(allValuesFromSuper,
                qualifiedCardinalityLocalTriple, onClassLocalTriple);

        eg = new ElementGroup();
        qualifiedCardinality.setFilterElement(eg);
        ElementUnion union = new ElementUnion();
        eg.addElement(union);

        eg = new ElementGroup();
        eg.addTriplePattern(qualifiedCardinalityLocalTriple);
        eg.addElementFilter(new ElementFilter(new E_NotEquals(new ExprVar(
                qualifiedCardinalityLocalVar), new NodeValueInteger(0))));
        union.addElement(eg);
        eg = new ElementGroup();
        classGroup = new ElementGroup();
        classGroup.addTriplePattern(onClassLocalTriple);
        classGroup.addTriplePattern(allValuesFromSuperTriple);
        classGroup.addTriplePattern(Triples.create(allValuesFromSuperVar, PathUtil.subClassOfAny,
                onClassLocalVar));
        eg.addElementFilter(new ElementFilter(new E_NotExists(classGroup)));
        union.addElement(eg);

        // MaxQualifiedCardinality - L.# = 0 & S.onClass <= L.onClass
        Var maxQualifiedCardinalityLocalVar = Var.alloc("maxQualifiedCardinalityL");
        Triple maxQualifiedCardinalityLocalTriple = Triples.create(localRes,
                OWL2.maxQualifiedCardinality, maxQualifiedCardinalityLocalVar);

        SubMatch maxQualifiedCardinality = new SubMatch(allValuesFromSuper,
                maxQualifiedCardinalityLocalTriple, onClassLocalTriple);

        eg = new ElementGroup();
        maxQualifiedCardinality.setFilterElement(eg);
        union = new ElementUnion();
        eg.addElement(union);

        eg = new ElementGroup();
        eg.addTriplePattern(maxQualifiedCardinalityLocalTriple);
        eg.addElementFilter(new ElementFilter(new E_NotEquals(new ExprVar(
                maxQualifiedCardinalityLocalVar), new NodeValueInteger(0))));
        union.addElement(eg);
        eg = new ElementGroup();
        classGroup = new ElementGroup();
        classGroup.addTriplePattern(onClassLocalTriple);
        classGroup.addTriplePattern(allValuesFromSuperTriple);
        classGroup.addTriplePattern(Triples.create(allValuesFromSuperVar, PathUtil.subClassOfAny,
                onClassLocalVar));
        eg.addElementFilter(new ElementFilter(new E_NotExists(classGroup)));
        union.addElement(eg);

        return allValuesFromSuper.asElementGroup();
    }

    private ElementGroup someValuesFrom(Node superRes, Node localRes) {
        Var someValuesFromSuperVar = Var.alloc("someValuesFromS");
        Triple someValuesFromSuperTriple = Triples.create(superRes, OWL.someValuesFrom,
                someValuesFromSuperVar);

        SuperMatch someValuesFromSuper = new SuperMatch(someValuesFromSuperTriple);

        // SomeValuesFrom - S.onCLass >= L.onClass
        Var someValuesFromLocalVar = Var.alloc("someValuesFromL");
        Triple someValuesFromLocalTriple = Triples.create(localRes, OWL.someValuesFrom,
                someValuesFromLocalVar);

        SubMatch allValuesFromLocal = new SubMatch(someValuesFromSuper, someValuesFromLocalTriple);

        ElementGroup eg = new ElementGroup();
        eg.addTriplePattern(someValuesFromSuperTriple);
        eg.addTriplePattern(someValuesFromLocalTriple);
        // subClassOfInferred should not be used because, after inference, every
        // class is subclass of itself
        ElementGroup classGroup = new ElementGroup();
        classGroup.addTriplePattern(someValuesFromLocalTriple);
        classGroup.addTriplePattern(someValuesFromSuperTriple);
        classGroup.addTriplePattern(Triples.create(someValuesFromLocalVar, PathUtil.subClassOfAny,
                someValuesFromSuperVar));
        eg.addElementFilter(new ElementFilter(new E_NotExists(classGroup)));

        allValuesFromLocal.setFilterElement(eg);

        // HasValue - L.inst -> S.onClass
        Var hasValueLocalVar = Var.alloc("hasValueL");
        Triple hasValueLocalTriple = Triples.create(localRes, OWL.hasValue, hasValueLocalVar);

        SubMatch hasValue = new SubMatch(someValuesFromSuper, hasValueLocalTriple);

        eg = new ElementGroup();
        eg.addTriplePattern(hasValueLocalTriple);
        eg.addTriplePattern(someValuesFromSuperTriple);
        ElementGroup filter = new ElementGroup();
        filter.addTriplePattern(Triples.create(hasValueLocalVar, PathUtil.isInstanceOf,
                someValuesFromSuperVar));
        eg.addElementFilter(new ElementFilter(new E_NotExists(filter)));
        hasValue.setFilterElement(eg);

        // QualifiedCardinality - L.# != 0 & S.onClass >= L.onClass
        Var qualifiedCardinalityLocalVar = Var.alloc("qualifiedCardinalityL");
        Var onClassLocalVar = Var.alloc("onClassL");
        Triple qualifiedCardinalityLocalTriple = Triples.create(localRes,
                OWL2.qualifiedCardinality, qualifiedCardinalityLocalVar);
        Triple onClassLocalTriple = Triples.create(localRes, OWL2.onClass, onClassLocalVar);

        SubMatch qualifiedCardinality = new SubMatch(someValuesFromSuper,
                qualifiedCardinalityLocalTriple, onClassLocalTriple);

        eg = new ElementGroup();
        qualifiedCardinality.setFilterElement(eg);
        ElementUnion union = new ElementUnion();
        eg.addElement(union);

        eg = new ElementGroup();
        eg.addTriplePattern(qualifiedCardinalityLocalTriple);
        eg.addElementFilter(new ElementFilter(new E_Equals(
                new ExprVar(qualifiedCardinalityLocalVar), new NodeValueInteger(0))));
        union.addElement(eg);
        eg = new ElementGroup();
        classGroup = new ElementGroup();
        classGroup.addTriplePattern(onClassLocalTriple);
        classGroup.addTriplePattern(someValuesFromSuperTriple);
        classGroup.addTriplePattern(Triples.create(onClassLocalVar, PathUtil.subClassOfAny,
                someValuesFromSuperVar));
        eg.addElementFilter(new ElementFilter(new E_NotExists(classGroup)));
        union.addElement(eg);

        // MinQualifiedCardinality - L.# != 0 & S.onClass >= L.onClass
        Var minQualifiedCardinalityLocalVar = Var.alloc("minQualifiedCardinalityL");
        Triple minQualifiedCardinalityLocalTriple = Triples.create(localRes,
                OWL2.minQualifiedCardinality, minQualifiedCardinalityLocalVar);

        SubMatch minQualifiedCardinality = new SubMatch(someValuesFromSuper,
                minQualifiedCardinalityLocalTriple, onClassLocalTriple);

        eg = new ElementGroup();
        minQualifiedCardinality.setFilterElement(eg);
        union = new ElementUnion();
        eg.addElement(union);

        eg = new ElementGroup();
        eg.addTriplePattern(minQualifiedCardinalityLocalTriple);
        eg.addElementFilter(new ElementFilter(new E_Equals(new ExprVar(
                minQualifiedCardinalityLocalVar), new NodeValueInteger(0))));
        union.addElement(eg);
        eg = new ElementGroup();
        classGroup = new ElementGroup();
        classGroup.addTriplePattern(onClassLocalTriple);
        classGroup.addTriplePattern(someValuesFromSuperTriple);
        classGroup.addTriplePattern(Triples.create(onClassLocalVar, PathUtil.subClassOfAny,
                someValuesFromSuperVar));
        eg.addElementFilter(new ElementFilter(new E_NotExists(classGroup)));
        union.addElement(eg);

        return someValuesFromSuper.asElementGroup();
    }

    private ElementGroup hasValue(Node superRes, Node localRes) {
        Var hasValueSuperVar = Var.alloc("hasValueS");
        Triple hasValueSuperTriple = Triples.create(superRes, OWL.hasValue, hasValueSuperVar);

        SuperMatch hasValueSuper = new SuperMatch(hasValueSuperTriple);

        // HasValue - S.inst = L.inst
        Var hasValueLocalVar = Var.alloc("hasValueL");
        Triple hasValueLocalTriple = Triples.create(localRes, OWL.hasValue, hasValueLocalVar);

        SubMatch hasValue = new SubMatch(hasValueSuper, hasValueLocalTriple);

        ElementGroup eg = new ElementGroup();
        eg.addTriplePattern(hasValueSuperTriple);
        eg.addTriplePattern(hasValueLocalTriple);
        eg.addElementFilter(new ElementFilter(new E_NotEquals(new ExprVar(hasValueSuperVar),
                new ExprVar(hasValueLocalVar))));
        hasValue.setFilterElement(eg);

        return hasValueSuper.asElementGroup();
    }

    private ElementGroup cardinality(Node superRes, Node localRes) {

        Var cardinalitySuperVar = Var.alloc("cardinalityS");
        Triple cardinalitySuperTriple = Triples.create(superRes, OWL.cardinality,
                cardinalitySuperVar);

        SuperMatch cardinalitySuper = new SuperMatch(cardinalitySuperTriple);

        // Cardinality
        Var cardinalityLocalVar = Var.alloc("cardinalityL");
        Triple cardinalityLocalTriple = Triples.create(localRes, OWL.cardinality,
                cardinalityLocalVar);

        SubMatch cardinalityLocal = new SubMatch(cardinalitySuper, cardinalityLocalTriple);

        ElementGroup eg = new ElementGroup();
        eg.addTriplePattern(cardinalitySuperTriple);
        eg.addTriplePattern(cardinalityLocalTriple);
        eg.addElementFilter(new ElementFilter(new E_NotEquals(new ExprVar(cardinalityLocalVar),
                new ExprVar(cardinalitySuperVar))));
        cardinalityLocal.setFilterElement(eg);

        return cardinalitySuper.asElementGroup();
    }

    private ElementGroup minCardinality(Node superRes, Node localRes) {
        Var minCardinalitySuperVar = Var.alloc("minCardinalityS");
        Triple minCardinalitySuperTriple = Triples.create(superRes, OWL.minCardinality,
                minCardinalitySuperVar);

        SuperMatch minCardinalitySuper = new SuperMatch(minCardinalitySuperTriple);

        // HasValue - S.# = 1
        Var hasValueLocalVar = Var.alloc("hasValueL");
        Triple hasValueLocalTriple = Triples.create(localRes, OWL.hasValue, hasValueLocalVar);

        SubMatch hasValue = new SubMatch(minCardinalitySuper, hasValueLocalTriple);

        ElementGroup eg = new ElementGroup();
        eg.addTriplePattern(minCardinalitySuperTriple);
        eg.addElementFilter(new ElementFilter(new E_NotEquals(new ExprVar(minCardinalitySuperVar),
                new NodeValueInteger(1))));
        hasValue.setFilterElement(eg);

        // Cardinality - S.# <= L.#
        Var cardinalityLocalVar = Var.alloc("cardinalityL");
        Triple cardinalityLocalTriple = Triples.create(localRes, OWL.cardinality,
                cardinalityLocalVar);

        SubMatch cardinalityLocal = new SubMatch(minCardinalitySuper, cardinalityLocalTriple);

        eg = new ElementGroup();
        eg.addTriplePattern(minCardinalitySuperTriple);
        eg.addTriplePattern(cardinalityLocalTriple);
        eg.addElementFilter(new ElementFilter(new E_GreaterThan(
                new ExprVar(minCardinalitySuperVar), new ExprVar(cardinalityLocalVar))));
        cardinalityLocal.setFilterElement(eg);

        // MinCardinality - S.# <= L.#
        Var minCardinalityLocalVar = Var.alloc("minCardinalityL");
        Triple minCardinalityLocalTriple = Triples.create(localRes, OWL.minCardinality,
                minCardinalityLocalVar);

        SubMatch minCardinalityLocal = new SubMatch(minCardinalitySuper, minCardinalityLocalTriple);

        eg = new ElementGroup();
        eg.addTriplePattern(minCardinalitySuperTriple);
        eg.addTriplePattern(minCardinalityLocalTriple);
        eg.addElementFilter(new ElementFilter(new E_GreaterThan(
                new ExprVar(minCardinalitySuperVar), new ExprVar(minCardinalityLocalVar))));
        minCardinalityLocal.setFilterElement(eg);

        // QualifiedCardinality - S.# <= L.#
        Var qualifiedCardinalityLocalVar = Var.alloc("qualifiedCardinalityL");
        Var onClassLocalVar = Var.alloc("onClassL");
        Triple qualifiedCardinalityLocalTriple = Triples.create(localRes,
                OWL2.qualifiedCardinality, qualifiedCardinalityLocalVar);
        Triple onClassLocalTriple = Triples.create(localRes, OWL2.onClass, onClassLocalVar);

        SubMatch qualifiedCardinality = new SubMatch(minCardinalitySuper,
                qualifiedCardinalityLocalTriple, onClassLocalTriple);

        eg = new ElementGroup();
        eg.addTriplePattern(minCardinalitySuperTriple);
        eg.addTriplePattern(qualifiedCardinalityLocalTriple);
        eg.addElementFilter(new ElementFilter(new E_GreaterThan(
                new ExprVar(minCardinalitySuperVar), new ExprVar(qualifiedCardinalityLocalVar))));
        qualifiedCardinality.setFilterElement(eg);

        // MinQualifiedCardinality - S.# <= L.#
        Var minQualifiedCardinalityLocalVar = Var.alloc("minQualifiedCardinalityL");
        Triple minQualifiedCardinalityLocalTriple = Triples.create(localRes,
                OWL2.minQualifiedCardinality, minQualifiedCardinalityLocalVar);

        SubMatch minQualifiedCardinality = new SubMatch(minCardinalitySuper,
                minQualifiedCardinalityLocalTriple, onClassLocalTriple);

        eg = new ElementGroup();
        eg.addTriplePattern(minCardinalitySuperTriple);
        eg.addTriplePattern(minQualifiedCardinalityLocalTriple);
        eg.addElementFilter(new ElementFilter(new E_GreaterThan(
                new ExprVar(minCardinalitySuperVar), new ExprVar(minQualifiedCardinalityLocalVar))));
        minQualifiedCardinality.setFilterElement(eg);

        return minCardinalitySuper.asElementGroup();
    }

    private ElementGroup maxCardinality(Node superRes, Node localRes) {
        Var maxCardinalitySuperVar = Var.alloc("maxCardinalityS");
        Triple maxCardinalitySuperTriple = Triples.create(superRes, OWL.maxCardinality,
                maxCardinalitySuperVar);

        SuperMatch maxCardinalitySuper = new SuperMatch(maxCardinalitySuperTriple);

        // Cardinality - S.# >= L.#
        Var cardinalityLocalVar = Var.alloc("cardinalityL");
        Triple cardinalityLocalTriple = Triples.create(localRes, OWL.cardinality,
                cardinalityLocalVar);

        SubMatch cardinalityLocal = new SubMatch(maxCardinalitySuper, cardinalityLocalTriple);

        ElementGroup eg = new ElementGroup();
        eg.addTriplePattern(maxCardinalitySuperTriple);
        eg.addTriplePattern(cardinalityLocalTriple);
        eg.addElementFilter(new ElementFilter(new E_GreaterThan(new ExprVar(cardinalityLocalVar),
                new ExprVar(maxCardinalitySuperVar))));
        cardinalityLocal.setFilterElement(eg);

        // MaxCardinality - S.# >= L.#
        Var maxCardinalityLocalVar = Var.alloc("maxCardinalityL");
        Triple maxCardinalityLocalTriple = Triples.create(localRes, OWL.maxCardinality,
                maxCardinalityLocalVar);

        SubMatch maxCardinalityLocal = new SubMatch(maxCardinalitySuper, maxCardinalityLocalTriple);

        eg = new ElementGroup();
        eg.addTriplePattern(maxCardinalitySuperTriple);
        eg.addTriplePattern(maxCardinalityLocalTriple);
        eg.addElementFilter(new ElementFilter(new E_GreaterThan(
                new ExprVar(maxCardinalityLocalVar), new ExprVar(maxCardinalitySuperVar))));
        maxCardinalityLocal.setFilterElement(eg);

        return maxCardinalitySuper.asElementGroup();
    }

    private ElementGroup qualifiedCardinality(Node superRes, Node localRes) {
        Var qualifiedCardinalitySuperVar = Var.alloc("qualifiedCardinalityS");
        Var onClassSuperVar = Var.alloc("onClassS");
        Triple qualifiedCardinalitySuperTriple = Triples.create(superRes,
                OWL2.qualifiedCardinality, qualifiedCardinalitySuperVar);
        Triple onClassSuperTriple = Triples.create(superRes, OWL2.onClass, onClassSuperVar);

        SuperMatch maxCardinalitySuper = new SuperMatch(qualifiedCardinalitySuperTriple,
                onClassSuperTriple);

        // QualifiedCardinality - S.# = L.# & S.onClass >= L.onClass
        Var qualifiedCardinalityLocalVar = Var.alloc("qualifiedCardinalityL");
        Var onClassLocalVar = Var.alloc("onClassL");
        Triple qualifiedCardinalityLocalTriple = Triples.create(localRes,
                OWL2.qualifiedCardinality, qualifiedCardinalityLocalVar);
        Triple onClassLocalTriple = Triples.create(localRes, OWL2.onClass, onClassLocalVar);

        SubMatch qualifiedCardinality = new SubMatch(maxCardinalitySuper,
                qualifiedCardinalityLocalTriple, onClassLocalTriple);

        ElementGroup eg = new ElementGroup();
        qualifiedCardinality.setFilterElement(eg);
        ElementUnion union = new ElementUnion();
        eg.addElement(union);

        eg = new ElementGroup();
        eg.addTriplePattern(qualifiedCardinalitySuperTriple);
        eg.addTriplePattern(qualifiedCardinalityLocalTriple);
        eg.addElementFilter(new ElementFilter(new E_NotEquals(new ExprVar(
                qualifiedCardinalitySuperVar), new ExprVar(qualifiedCardinalityLocalVar))));
        union.addElement(eg);
        eg = new ElementGroup();
        ElementGroup classGroup = new ElementGroup();
        classGroup.addTriplePattern(onClassLocalTriple);
        classGroup.addTriplePattern(onClassSuperTriple);
        classGroup.addTriplePattern(Triples.create(onClassLocalVar, PathUtil.subClassOfAny,
                onClassSuperVar));
        eg.addElementFilter(new ElementFilter(new E_NotExists(classGroup)));
        union.addElement(eg);

        return maxCardinalitySuper.asElementGroup();
    }

    private ElementGroup minQualifiedCardinality(Node superRes, Node localRes) {
        Var minQualifiedCardinalitySuperVar = Var.alloc("minQualifiedCardinalityS");
        Var onClassSuperVar = Var.alloc("onClassS");
        Triple minQualifiedCardinalitySuperTriple = Triples.create(superRes,
                OWL2.minQualifiedCardinality, minQualifiedCardinalitySuperVar);
        Triple onClassSuperTriple = Triples.create(superRes, OWL2.onClass, onClassSuperVar);

        SuperMatch minQualifiedCardinalitySuper = new SuperMatch(
                minQualifiedCardinalitySuperTriple, onClassSuperTriple);

        // SomeValuesFrom - S.# = 1 & S.onClass >= L.onClass
        Var someValuesFromLocalVar = Var.alloc("someValuesFromL");
        Triple someValuesFromLocalTriple = Triples.create(localRes, OWL.someValuesFrom,
                someValuesFromLocalVar);

        SubMatch allValuesFromLocal = new SubMatch(minQualifiedCardinalitySuper,
                someValuesFromLocalTriple);

        ElementGroup eg = new ElementGroup();
        allValuesFromLocal.setFilterElement(eg);
        ElementUnion union = new ElementUnion();
        eg.addElement(union);

        eg = new ElementGroup();
        eg.addTriplePattern(minQualifiedCardinalitySuperTriple);
        eg.addElementFilter(new ElementFilter(new E_NotEquals(new ExprVar(
                minQualifiedCardinalitySuperVar), new NodeValueInteger(1))));
        union.addElement(eg);
        eg = new ElementGroup();

        ElementGroup classGroup = new ElementGroup();
        classGroup.addTriplePattern(someValuesFromLocalTriple);
        classGroup.addTriplePattern(onClassSuperTriple);
        classGroup.addTriplePattern(Triples.create(someValuesFromLocalVar, PathUtil.subClassOfAny,
                onClassSuperVar));
        eg.addElementFilter(new ElementFilter(new E_NotExists(classGroup)));
        union.addElement(eg);

        // HasValue - S.# = 1 & L.inst -> S.onClass
        Var hasValueLocalVar = Var.alloc("hasValueL");
        Triple hasValueLocalTriple = Triples.create(localRes, OWL.hasValue, hasValueLocalVar);

        SubMatch hasValue = new SubMatch(minQualifiedCardinalitySuper, hasValueLocalTriple);

        eg = new ElementGroup();
        hasValue.setFilterElement(eg);
        union = new ElementUnion();
        eg.addElement(union);

        eg = new ElementGroup();
        eg.addTriplePattern(minQualifiedCardinalitySuperTriple);
        eg.addElementFilter(new ElementFilter(new E_NotEquals(new ExprVar(
                minQualifiedCardinalitySuperVar), new NodeValueInteger(1))));
        union.addElement(eg);
        eg = new ElementGroup();
        eg.addTriplePattern(onClassSuperTriple);
        eg.addTriplePattern(hasValueLocalTriple);
        ElementGroup filter = new ElementGroup();
        filter.addTriplePattern(Triples.create(hasValueLocalVar, PathUtil.isInstanceOf,
                onClassSuperVar));
        union.addElement(eg);
        eg.addElementFilter(new ElementFilter(new E_NotExists(filter)));

        // QualifiedCardinality - S.# <= L.# & S.onClass >= L.onClass
        Var qualifiedCardinalityLocalVar = Var.alloc("qualifiedCardinalityL");
        Var onClassLocalVar = Var.alloc("onClassL");
        Triple qualifiedCardinalityLocalTriple = Triples.create(localRes,
                OWL2.qualifiedCardinality, qualifiedCardinalityLocalVar);
        Triple onClassLocalTriple = Triples.create(localRes, OWL2.onClass, onClassLocalVar);

        SubMatch qualifiedCardinality = new SubMatch(minQualifiedCardinalitySuper,
                qualifiedCardinalityLocalTriple, onClassLocalTriple);

        eg = new ElementGroup();
        qualifiedCardinality.setFilterElement(eg);
        union = new ElementUnion();
        eg.addElement(union);

        eg = new ElementGroup();
        eg.addTriplePattern(minQualifiedCardinalitySuperTriple);
        eg.addTriplePattern(qualifiedCardinalityLocalTriple);
        eg.addElementFilter(new ElementFilter(new E_GreaterThan(new ExprVar(
                minQualifiedCardinalitySuperVar), new ExprVar(qualifiedCardinalityLocalVar))));
        union.addElement(eg);
        eg = new ElementGroup();
        classGroup = new ElementGroup();
        classGroup.addTriplePattern(onClassLocalTriple);
        classGroup.addTriplePattern(onClassSuperTriple);
        classGroup.addTriplePattern(Triples.create(onClassLocalVar, PathUtil.subClassOfAny,
                onClassSuperVar));
        eg.addElementFilter(new ElementFilter(new E_NotExists(classGroup)));
        union.addElement(eg);

        // MinQualifiedCardinality - S.# <= L.# & S.onClass >= L.onClass
        Var minQualifiedCardinalityLocalVar = Var.alloc("minQualifiedCardinalityL");
        Triple minQualifiedCardinalityLocalTriple = Triples.create(localRes,
                OWL2.minQualifiedCardinality, minQualifiedCardinalityLocalVar);

        SubMatch minQualifiedCardinality = new SubMatch(minQualifiedCardinalitySuper,
                minQualifiedCardinalityLocalTriple, onClassLocalTriple);

        eg = new ElementGroup();
        minQualifiedCardinality.setFilterElement(eg);
        union = new ElementUnion();
        eg.addElement(union);

        eg = new ElementGroup();
        eg.addTriplePattern(minQualifiedCardinalitySuperTriple);
        eg.addTriplePattern(minQualifiedCardinalityLocalTriple);
        eg.addElementFilter(new ElementFilter(new E_GreaterThan(new ExprVar(
                minQualifiedCardinalitySuperVar), new ExprVar(minQualifiedCardinalityLocalVar))));
        union.addElement(eg);
        eg = new ElementGroup();
        classGroup = new ElementGroup();
        classGroup.addTriplePattern(onClassLocalTriple);
        classGroup.addTriplePattern(onClassSuperTriple);
        classGroup.addTriplePattern(Triples.create(onClassLocalVar, PathUtil.subClassOfAny,
                onClassSuperVar));
        eg.addElementFilter(new ElementFilter(new E_NotExists(classGroup)));
        union.addElement(eg);

        return minQualifiedCardinalitySuper.asElementGroup();
    }

    private ElementGroup maxQualifiedCardinality(Node superRes, Node localRes) {
        Var maxQualifiedCardinalitySuperVar = Var.alloc("maxQualifiedCardinalityS");
        Var onClassSuperVar = Var.alloc("onClassS");
        Triple maxQualifiedCardinalitySuperTriple = Triples.create(superRes,
                OWL2.maxQualifiedCardinality, maxQualifiedCardinalitySuperVar);
        Triple onClassSuperTriple = Triples.create(superRes, OWL2.onClass, onClassSuperVar);

        SuperMatch maxQualifiedCardinalitySuper = new SuperMatch(
                maxQualifiedCardinalitySuperTriple, onClassSuperTriple);

        // QualifiedCardinality - S.# >= L.# & S.onClass >= L.onClass
        Var qualifiedCardinalityLocalVar = Var.alloc("qualifiedCardinalityL");
        Var onClassLocalVar = Var.alloc("onClassL");
        Triple qualifiedCardinalityLocalTriple = Triples.create(localRes,
                OWL2.qualifiedCardinality, qualifiedCardinalityLocalVar);
        Triple onClassLocalTriple = Triples.create(localRes, OWL2.onClass, onClassLocalVar);

        SubMatch qualifiedCardinality = new SubMatch(maxQualifiedCardinalitySuper,
                qualifiedCardinalityLocalTriple, onClassLocalTriple);

        ElementGroup eg = new ElementGroup();
        qualifiedCardinality.setFilterElement(eg);
        ElementUnion union = new ElementUnion();
        eg.addElement(union);

        eg = new ElementGroup();
        eg.addTriplePattern(maxQualifiedCardinalitySuperTriple);
        eg.addTriplePattern(qualifiedCardinalityLocalTriple);
        eg.addElementFilter(new ElementFilter(new E_LessThan(new ExprVar(
                maxQualifiedCardinalitySuperVar), new ExprVar(qualifiedCardinalityLocalVar))));
        union.addElement(eg);
        eg = new ElementGroup();
        ElementGroup classGroup = new ElementGroup();
        classGroup.addTriplePattern(onClassLocalTriple);
        classGroup.addTriplePattern(onClassSuperTriple);
        classGroup.addTriplePattern(Triples.create(onClassLocalVar, PathUtil.subClassOfAny,
                onClassSuperVar));
        eg.addElementFilter(new ElementFilter(new E_NotExists(classGroup)));
        union.addElement(eg);

        // MaxQualifiedCardinality - S.# >= L.# & S.onClass >= L.onClass
        Var maxQualifiedCardinalityLocalVar = Var.alloc("maxQualifiedCardinalityL");
        Triple maxQualifiedCardinalityLocalTriple = Triples.create(localRes,
                OWL2.maxQualifiedCardinality, maxQualifiedCardinalityLocalVar);

        SubMatch maxQualifiedCardinality = new SubMatch(maxQualifiedCardinalitySuper,
                maxQualifiedCardinalityLocalTriple, onClassLocalTriple);

        eg = new ElementGroup();
        maxQualifiedCardinality.setFilterElement(eg);
        union = new ElementUnion();
        eg.addElement(union);

        eg = new ElementGroup();
        eg.addTriplePattern(maxQualifiedCardinalitySuperTriple);
        eg.addTriplePattern(maxQualifiedCardinalityLocalTriple);
        eg.addElementFilter(new ElementFilter(new E_LessThan(new ExprVar(
                maxQualifiedCardinalitySuperVar), new ExprVar(maxQualifiedCardinalityLocalVar))));
        union.addElement(eg);
        eg = new ElementGroup();
        classGroup = new ElementGroup();
        classGroup.addTriplePattern(onClassSuperTriple);
        classGroup.addTriplePattern(onClassLocalTriple);
        classGroup.addTriplePattern(Triples.create(onClassLocalVar, PathUtil.subClassOfAny,
                onClassSuperVar));
        eg.addElementFilter(new ElementFilter(new E_NotExists(classGroup)));
        union.addElement(eg);

        return maxQualifiedCardinalitySuper.asElementGroup();
    }

    static class SuperMatch {
        private List<Triple> superTriples;

        private List<SubMatch> matches;

        public SuperMatch(Triple... superTriple) {
            Preconditions.checkNotNull(superTriple);
            Preconditions.checkArgument(superTriple.length != 0);

            superTriples = Arrays.asList(superTriple);
            matches = new ArrayList<>();
        }

        public void addSubMatch(SubMatch match) {
            matches.add(match);
        }

        public ElementGroup asElementGroup() {
            ElementGroup superRestrictionElement = new ElementGroup();

            // Condition to access the "Row" of the restrictions Table
            for (Triple t : superTriples) {
                superRestrictionElement.addTriplePattern(t);
            }

            for (SubMatch subMatch : matches) {
                superRestrictionElement.addElement(subMatch.asElementGroup());
            }

            return superRestrictionElement;
        }
    }

    static class SubMatch {

        private List<Triple> subTriples;
        private ElementGroup filterElement;

        public SubMatch(SuperMatch superMatch, Triple... subTriple) {
            Preconditions.checkNotNull(subTriple);
            Preconditions.checkArgument(subTriple.length != 0);

            this.subTriples = Arrays.asList(subTriple);
            superMatch.addSubMatch(this);
        }

        public void setFilterElement(ElementGroup filterElement) {
            this.filterElement = filterElement;
        }

        public ElementUnion asElementGroup() {
            ElementGroup eg = new ElementGroup();
            ElementGroup filter = new ElementGroup();
            ElementUnion union = new ElementUnion();

            // Condition to access the "Column" of the restrictions Table
            for (Triple t : subTriples) {
                filter.addTriplePattern(t);
            }

            eg.addElementFilter(new ElementFilter(new E_NotExists(filter)));
            union.addElement(eg);

            // Check if the super Restriction is subsumed by the local one
            Preconditions.checkNotNull(filterElement);
            union.addElement(filterElement);

            return union;
        }
    }

    private boolean executeAsk(Query q) {
        QueryExecution exec = QueryExecutionFactory.create(q, model);
        return exec.execAsk();
    }
}
