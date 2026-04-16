package com.semmtech.jena.deltas;


import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.DatasetFactory;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDFS;


public class FindDeltaExample {

    public static void main(String[] args) {
        String ns1 = "http://example.com/ns/1/";
        Model version1 = ModelFactory.createDefaultModel();

        Resource mike = version1.createResource(ns1 + "Mike");
        mike.addProperty(RDFS.label, "MikeHenrichs");

        Dataset dataset1 = DatasetFactory.createMem();
        dataset1.addNamedModel("<urn:version1>", version1);

        // @formatter:off
        String sparql = "select ?s ?p ?o where { ?s ?p ?o . } order by ?s ?p";        
        // @formatter:on

        Query query = QueryFactory.create(sparql);
        QueryExecution exec = QueryExecutionFactory.create(query, dataset1);
        ResultSet result = exec.execSelect();

        System.out.println(ResultSetFormatter.asText(result));
        System.out.println("Done");
    }
}
