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


import org.eclipse.jface.viewers.LabelProvider;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.impl.ResourceImpl;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.engine.binding.Binding;
import com.hp.hpl.jena.sparql.expr.ExprList;
import com.hp.hpl.jena.sparql.expr.NodeValue;
import com.hp.hpl.jena.sparql.expr.nodevalue.NodeFunctions;
import com.hp.hpl.jena.sparql.function.FunctionBase1;
import com.hp.hpl.jena.sparql.function.FunctionEnv;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.model.IModelProvider;


/**
 * 
 * @author Sander Stolk
 */
public class BuildStringFunction extends FunctionBase1 {
    private final static String NS = "http://topbraid.org/sparqlmotionfunctions#";
    private Binding binding;
    private LabelProvider labelProvider;

    public static String getURI() {
        return NS + "buildString";
    }

    public static Node asNode() {
        return NodeFactory.createURI(getURI());
    }

    @Override
    public NodeValue exec(Binding binding, ExprList args, String uri, FunctionEnv env) {
        // Overriding this function here solely to obtain the bindings.
        // Note that bindings are normally used only with PropertyFunctions.
        this.binding = binding;

        // TODO: Get the graph - using env.getActiveGraph() - to provide
        // resources with labels. Using labelProvider, like below, is not
        // recommended in a generic function such as this one.
        IModelProvider provider = CorePlugin.getDefault().getActiveModelProvider();
        if (provider != null) {
            labelProvider = provider.getLabelProvider();
        }

        return super.exec(binding, args, uri, env);
    }

    @Override
    public NodeValue exec(NodeValue v) {
        if (v == null || !v.isString()) {
            return null;
        }

        String format = v.getString();
        String result = insertVariableValues(format, binding);
        return (result != null) ? NodeValue.makeString(result) : null;
    }

    protected String insertVariableValues(String format, Binding binding) {
        boolean done = false;
        int startIndex = 0;
        while (!done) {
            startIndex = format.indexOf('{', startIndex);
            if (startIndex == -1) {
                return format;
            }
            int endIndex = format.indexOf('}', startIndex);
            if (endIndex == -1) {
                return format;
            }

            String varText = format.substring(startIndex + 1, endIndex);
            if (varText.startsWith("$") || varText.startsWith("?")) {
                String varName = varText.substring(1);
                Var var = Var.alloc(varName);
                Node varValue = binding.get(var);

                if (varValue == null) {
                    // not all variables are present/bound,
                    // so don't return a string
                    return null;
                }

                String varValueText = NodeFunctions.str(varValue);

                if (varValue.isURI()) {
                    if (labelProvider != null) {
                        Resource resource = new ResourceImpl(varValue.getURI());
                        varValueText = labelProvider.getText(resource);
                        varValueText = String.format("'%s'", varValueText);
                    }
                    else {
                        varValueText = String.format("<%s>", varValueText);
                    }
                }
                else if (varValue.isLiteral()) {
                    // TODO: Don't put quotes around numerical datatypes.
                    varValueText = String.format("\"%s\"", varValueText);
                }

                if (varValueText == null) {
                    varValueText = new String();
                }

                format = format.replace(String.format("{%s}", varText), varValueText);
            }

            startIndex++;
            if (startIndex >= format.length()) {
                done = true;
            }
        }
        return format;
    }
}
