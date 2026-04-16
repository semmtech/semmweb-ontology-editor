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

package com.semmtech.semantics.sparql.pfunction;


import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.QueryBuildException;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.engine.ExecutionContext;
import com.hp.hpl.jena.sparql.engine.QueryIterator;
import com.hp.hpl.jena.sparql.engine.binding.Binding;
import com.hp.hpl.jena.sparql.engine.binding.BindingFactory;
import com.hp.hpl.jena.sparql.engine.binding.BindingMap;
import com.hp.hpl.jena.sparql.pfunction.PropFuncArg;
import com.hp.hpl.jena.sparql.pfunction.PropFuncArgType;
import com.hp.hpl.jena.sparql.pfunction.PropertyFunctionEval;
import com.hp.hpl.jena.sparql.util.IterLib;
import com.hp.hpl.jena.sparql.util.Utils;
import com.hp.hpl.jena.vocabulary.RDF;
import com.semmtech.semantics.vocabulary.SKOS;


public class WorkbenchAdapterPropertyFunction extends PropertyFunctionEval {

    public WorkbenchAdapterPropertyFunction() {
        super(PropFuncArgType.PF_ARG_SINGLE, PropFuncArgType.PF_ARG_LIST);
    }

    @Override
    public void build(PropFuncArg argSubject, Node predicate, PropFuncArg argObject,
            ExecutionContext execCxt) {
        // Do some checking.
        // These checks are assumed to be passed in .exec()
        if (argSubject.isList())
            throw new QueryBuildException(Utils.className(this)
                    + "Subject must be a single node or variable, not a list");
        if (!argObject.isList())
            throw new QueryBuildException(Utils.className(this)
                    + "Object must be a list of two elements");
        if (argObject.getArgList().size() != 2)
            throw new QueryBuildException(Utils.className(this) + "Object is a list but it has "
                    + argObject.getArgList().size() + " elements - should be 2");
    }

    @Override
    public QueryIterator execEvaluated(Binding binding, PropFuncArg argSubject, Node predicate,
            PropFuncArg argObject, ExecutionContext execCxt) {

        Graph graph = execCxt.getActiveGraph();
        Node subject = argSubject.getArg();
        Node textNode = argObject.getArg(0);
        Node imageNode = argObject.getArg(1);

        // New binding to return.
        BindingMap bindingMap = null;
        if (Var.isVar(textNode) && Var.isVar(imageNode)) {
            bindingMap = BindingFactory.create(binding);
        }
        else {
            return IterLib.noResults(execCxt);
        }

        String text = "";
        if (subject.isBlank()) {
            text = subject.getBlankNodeLabel();
        }
        else if (subject.isURI()) {
            text = "<" + subject.getURI() + ">";
        }
        for (Triple triple : graph.find(subject, SKOS.prefLabel.asNode(), null).toList()) {
            text = triple.getObject().getLiteralLexicalForm();
        }
        bindingMap.add(Var.alloc(textNode), NodeFactory.createLiteral(text));

        String image = "unknown";
        for (Triple triple : graph.find(subject, RDF.type.asNode(), null).toList()) {
            image = triple.getObject().getLocalName();
        }
        bindingMap.add(Var.alloc(imageNode), NodeFactory.createLiteral(image + ".png"));

        return IterLib.result(bindingMap, execCxt);
    }
}
