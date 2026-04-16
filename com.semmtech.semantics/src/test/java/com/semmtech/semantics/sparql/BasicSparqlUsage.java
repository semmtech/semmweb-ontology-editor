package com.semmtech.semantics.sparql;


import org.junit.Test;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;


public class BasicSparqlUsage {
    public BasicSparqlUsage() {

    }

    @Test
    public void test() {
        Model model = ModelFactory.createDefaultModel();
        Resource mike = model.createResource("http://www.example.com/mike#MikeHenrichs");
        mike.addProperty(RDF.type, OWL.Thing);
        mike.addProperty(RDFS.label, "Mike Henrichs");

        String sparqlQuery = "SELECT * WHERE { ?s ?p ?o .} LIMIT 10";

        Query query = QueryFactory.create(sparqlQuery);
        QueryExecution execution = QueryExecutionFactory.create(query, model);
        ResultSet result = execution.execSelect();
        ResultSetFormatter.outputAsJSON(System.out, result);
    }
}
