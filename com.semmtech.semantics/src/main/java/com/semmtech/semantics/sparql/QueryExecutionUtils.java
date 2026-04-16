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
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.semmtech.NotImplementedException;


public class QueryExecutionUtils {

    public static int execCount(Model m, Query q) {
        QueryExecution exec = QueryExecutionFactory.create(q, m);

        try {
            ResultSet rs = exec.execSelect();

            int size = -1;
            if (rs.hasNext()) {
                QuerySolution qs = rs.next();
                size = qs.get("?count").asLiteral().getInt();
            }
            return size;
        }
        finally {
            exec.close();
        }
    }

    public static Resource execAndExtractResource(Model m, Query q, String varName) {
        q.setLimit(1);
        QueryExecution exec = QueryExecutionFactory.create(q, m);

        try {
            ResultSet rs = exec.execSelect();
            List<Resource> result = extractResources(rs, varName);
            return result.isEmpty() ? null : result.get(0);
        }
        finally {
            exec.close();
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T execAndExtractValue(Model m, Query q, String varName, Class<T> type) {
        Literal literal = execAndExtractLiteral(m, q, varName);

        if (literal == null) {
            return null;
        }

        if (String.class == type) {
            return (T) literal.getLexicalForm();
        }
        else if (Double.class == type) {
            return (T) new Double(literal.getDouble());
        }
        else if (Integer.class == type) {
            return (T) new Double(literal.getInt());
        }
        else {
            throw new NotImplementedException("The conversion to " + type
                    + " has not been implemented. Please provide an implementation.");
        }
    }

    public static Literal execAndExtractLiteral(Model m, Query q, String varName) {
        q.setLimit(1);
        QueryExecution exec = QueryExecutionFactory.create(q, m);

        try {
            ResultSet rs = exec.execSelect();
            List<Literal> result = extractLiterals(rs, varName);
            return result.isEmpty() ? null : result.get(0);
        }
        finally {
            exec.close();
        }
    }

    public static List<Resource> execAndExtractResources(Model m, Query q, String varName) {
        QueryExecution exec = QueryExecutionFactory.create(q, m);

        try {
            ResultSet rs = exec.execSelect();
            return extractResources(rs, varName);
        }
        finally {
            exec.close();
        }
    }

    public static List<Resource> execAndExtractResources(Dataset ds, Query q, String varName) {
        QueryExecution exec = QueryExecutionFactory.create(q, ds);

        try {
            ResultSet rs = exec.execSelect();
            return extractResources(rs, varName);
        }
        finally {
            exec.close();
        }
    }

    public static List<Resource> extractResources(ResultSet rs, String varName) {
        List<Resource> resources = Lists.newLinkedList();

        while (rs.hasNext()) {
            QuerySolution qs = rs.next();
            resources.add(qs.getResource(varName));
        }
        return resources;
    }

    public static List<Literal> extractLiterals(ResultSet rs, String varName) {
        List<Literal> literals = Lists.newLinkedList();

        while (rs.hasNext()) {
            QuerySolution qs = rs.next();
            literals.add(qs.getLiteral(varName));
        }
        return literals;
    }

    public static boolean execAsk(Model m, Query q) {
        QueryExecution exec = QueryExecutionFactory.create(q, m);
        try {
            return exec.execAsk();
        }
        finally {
            exec.close();
        }
    }
}
