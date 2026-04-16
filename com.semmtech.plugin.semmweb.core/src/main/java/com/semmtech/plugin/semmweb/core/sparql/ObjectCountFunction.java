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

package com.semmtech.plugin.semmweb.core.sparql;


import java.util.List;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.query.QueryBuildException;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.engine.binding.Binding;
import com.hp.hpl.jena.sparql.expr.ExprList;
import com.hp.hpl.jena.sparql.expr.NodeValue;
import com.hp.hpl.jena.sparql.function.FunctionBase;
import com.hp.hpl.jena.sparql.function.FunctionEnv;
import com.hp.hpl.jena.sparql.util.Utils;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.semmtech.semantics.sparql.PathUtil;
import com.semmtech.semantics.sparql.QueryBuilder;


/**
 * Function that counts the number of objects a certain subject has for a
 * certain predicate. Similar to spl:objectCount but extended to also be capable
 * of counting objects that are instances of a specific class.
 * <ul>
 * <li>Argument #1: the subject.
 * <li>Argument #2: the predicate.
 * <li>Argument #3: boolean indicating whether objects of sub properties should
 * be counted as well.
 * <li>Argument #4: the class which the objects need to be an instance of.
 * (OPTIONAL)
 * </ul>
 * 
 * @author Sander Stolk
 */
public class ObjectCountFunction extends FunctionBase {
    private Graph graph;

    public static String getURI() {
        return CorePropertyFunctions.NS + "objectCount";
    }

    public static Node asNode() {
        return NodeFactory.createURI(getURI());
    }

    @Override
    public NodeValue exec(Binding binding, ExprList args, String uri, FunctionEnv env) {
        graph = env.getActiveGraph();
        return super.exec(binding, args, uri, env);
    }

    @Override
    public void checkBuild(String uri, ExprList args) {
        if (args.size() < 3 || args.size() > 4) {
            throw new QueryBuildException("Function '" + Utils.className(this)
                    + "' takes three or four arguments");
        }
    }

    @Override
    public NodeValue exec(List<NodeValue> args) {
        Node subject = args.get(0).getNode();
        Node predicate = args.get(1).getNode();
        boolean subProperties = args.get(2).getBoolean();
        Node objectOfClass = null;
        if (args.size() > 3) {
            objectOfClass = args.get(3).getNode();
        }

        if (subject != null && predicate != null) {
            int result = getObjectCount(subject, predicate, objectOfClass, subProperties, graph);
            if (result >= 0) {
                return NodeValue.makeInteger(result);
            }
        }
        return null;
    }

    protected int getObjectCount(Node subject, Node predicate, Node objectOfClass,
            boolean subProperties, Graph graph) {
        Model model = ModelFactory.createModelForGraph(graph);

        if (objectOfClass == null) {
            return getAnyObjectCount(subject, predicate, subProperties, model);
        }

        if (objectOfClass.isLiteral()) {
            return -1;
        }

        // Count relevant literals
        int result = 0;
        if (exists(RDFS.Literal.asNode(), PathUtil.subClassOfAny, objectOfClass, model)) {
            result += getLiteralObjectCount(subject, predicate, objectOfClass, subProperties, model);
        }
        else if (exists(objectOfClass, PathUtil.subClassOfInferred, RDFS.Literal.asNode(), model)) {
            // TODO: Provide an accurate count taking datatypes into account.
            return -1;
        }

        // Count relevant resources
        result += getResourceObjectCount(subject, predicate, objectOfClass, subProperties, model);
        return result;
    }

    protected boolean exists(Node subject, Node predicate, Node object, Model model) {
        QueryBuilder qb = QueryBuilder.createAsk();
        qb.addTriplePattern(subject, predicate, object);
        return qb.execAsk(model);
    }

    protected int getAnyObjectCount(Node subject, Node predicate, boolean subProperties, Model model) {
        QueryBuilder qb = createObjectCountQuery(subject, predicate, null, subProperties);
        return qb.execCountSelect(model);
    }

    protected int getResourceObjectCount(Node subject, Node predicate, Node objectOfClass,
            boolean subProperties, Model model) {
        Var varObject = Var.alloc("object");
        QueryBuilder qb = createObjectCountQuery(subject, predicate, objectOfClass, subProperties);
        qb.addTriplePattern(varObject, PathUtil.isInstanceOf, objectOfClass);
        return qb.execCountSelect(model);
    }

    protected int getLiteralObjectCount(Node subject, Node predicate, Node objectOfClass,
            boolean subProperties, Model model) {
        Var varObject = Var.alloc("object");
        QueryBuilder qb = createObjectCountQuery(subject, predicate, objectOfClass, subProperties);
        qb.addFilterIsLiteral(varObject);
        return qb.execCountSelect(model);
    }

    protected QueryBuilder createObjectCountQuery(Node subject, Node predicate, Node objectOfClass,
            boolean subProperties) {
        Var varObject = Var.alloc("object");
        Var varPredicate = Var.alloc("predicate");

        QueryBuilder qb = QueryBuilder.createSelect(false);
        qb.setResultCountVar(varObject, true);

        if (subProperties) {
            qb.addTriplePattern(subject, varPredicate, varObject);
            qb.addTriplePattern(varPredicate, PathUtil.subPropertyOfAny, predicate);
        }
        else {
            qb.addTriplePattern(subject, predicate, varObject);
        }

        return qb;
    }

}
