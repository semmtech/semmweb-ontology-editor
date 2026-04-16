package com.semmtech.semantics.performance;


import org.junit.Test;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ModelMaker;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.semmtech.semantics.sparql.QueryBuilder;


public class TestQueryBuilder {
    public TestQueryBuilder() {

    }

    @Test
    public void testUniqueness() {
        Var var = Var.alloc("property");
        QueryBuilder builder = QueryBuilder.createSelect(false);
        builder.addTriplePattern(var, RDF.type.asNode(), RDF.Property.asNode());
        builder.addResultVar(var);
        Query query = builder.getQuery();

        ModelMaker maker = ModelFactory.createMemModelMaker();

        Model owl = maker.createFreshModel();
        owl.read(OWL.getURI());
        System.out.println(String.format("OWL read (%s statements)", owl.size()));

        Model rdf = maker.createFreshModel();
        rdf.read(RDF.getURI());
        System.out.println(String.format("RDF read (%s statements)", rdf.size()));

        System.out.println("Query on OWL:");
        ResultSetFormatter.out(System.out, QueryExecutionFactory.create(query, owl).execSelect());

        System.out.println("Query on RDF:");
        ResultSetFormatter.out(System.out, QueryExecutionFactory.create(query, rdf).execSelect());

    }
}
