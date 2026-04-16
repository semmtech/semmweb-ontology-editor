package com.semmtech.jena.query;


import com.google.common.base.Preconditions;
import com.hp.hpl.jena.query.DatasetFactory;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.sparql.util.Context;


public class QueryExecutionFactoryExtra extends QueryExecutionFactory {

    /**
     * Create a QueryExecution based on a query and a context (why is this
     * method not in QueryExecutionFactory? [EH])
     * 
     * @param query
     *            Query
     * @param model
     *            Model
     * @param ctx
     *            Context
     * @return QueryExecution
     */
    static public QueryExecution create(Query query, Model model, Context ctx) {
        Preconditions.checkNotNull(query, "Query is null");
        Preconditions.checkNotNull(model, "Model is null");

        return QueryExecutionFactory.make(query, DatasetFactory.create(model), ctx);
    }

    /**
     * Create a QueryExecution based on a query string and a context (why is
     * this method not in QueryExecutionFactory? [EH])
     * 
     * @param queryStr
     *            Query string
     * @param model
     *            Model
     * @param ctx
     *            Context
     * @return QueryExecution
     */
    public static QueryExecution create(String queryStr, Model model, Context ctx) {
        Preconditions.checkNotNull(queryStr, "Query string is null");
        Preconditions.checkNotNull(model, "Model is null");

        return QueryExecutionFactory.make(QueryFactory.create(queryStr),
                DatasetFactory.create(model), ctx);
    }
}