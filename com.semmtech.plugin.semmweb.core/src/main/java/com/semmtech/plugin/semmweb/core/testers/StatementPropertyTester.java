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

package com.semmtech.plugin.semmweb.core.testers;


import org.eclipse.core.expressions.PropertyTester;

import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.vocabulary.RDF;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.model.IModelProvider;
import com.semmtech.semantics.sparql.QueryBuilder;


/**
 * 
 * @author Sander Stolk
 */
public class StatementPropertyTester extends PropertyTester {
    public static final String PROPERTY_IS_REIFIED = "isReified";
    public static final String PROPERTY_HAS_DERIVATION = "hasDerivation";

    @Override
    public boolean test(Object receiver, String property, Object[] args, final Object expectedValue) {
        if (!(receiver instanceof Statement)) {
            return false;
        }

        Statement statement = (Statement) receiver;
        if (property.equals(PROPERTY_IS_REIFIED)) {
            Var varReifiedStatement = Var.alloc("reifiedStatement");

            QueryBuilder qb = QueryBuilder.createAsk();
            qb.addTriplePattern(varReifiedStatement, RDF.type, RDF.Statement);
            qb.addTriplePattern(varReifiedStatement, RDF.subject, statement.getSubject());
            qb.addTriplePattern(varReifiedStatement, RDF.predicate, statement.getPredicate());
            qb.addTriplePattern(varReifiedStatement, RDF.object, statement.getObject());
            boolean isReified = qb.execAsk(statement.getModel());

            return (isReified == Boolean.getBoolean(expectedValue.toString()));
        }
        if (property.equals(PROPERTY_HAS_DERIVATION)) {
            IModelProvider provider = CorePlugin.getDefault().getActiveModelProvider();
            if (provider != null) {
                return (provider.getDerivation(statement) != null);
            }
        }
        return false;
    }
}
