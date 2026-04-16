package com.semmtech.plugin.semmweb.core.model;


import org.junit.Test;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.QuerySolutionMap;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDFS;


@SuppressWarnings("static-method")
public class TestAnonymousBindings {
    public TestAnonymousBindings() {

    }

    @Test
    public void testBindings() {
        Model model = ModelFactory.createDefaultModel();

        Resource a = model.createResource();
        a.addProperty(RDFS.label, "A");

        Resource b = model.createResource();
        b.addProperty(RDFS.label, "B");

        StringBuilder sparql = new StringBuilder();
        sparql.append("PREFIX rdfs: <" + RDFS.getURI() + ">\n");
        sparql.append("SELECT ?label\n");
        sparql.append("WHERE {\n");
        sparql.append("?resource rdfs:label ?label .\n");
        sparql.append("}");

        QueryExecution execution = QueryExecutionFactory.create(sparql.toString(), model);
        QuerySolutionMap bindings = new QuerySolutionMap();
        bindings.add("resource", a);
        execution.setInitialBinding(bindings);

        ResultSet result = execution.execSelect();
        while (result.hasNext()) {
            QuerySolution solution = result.next();
            RDFNode node = solution.get("label");
            if (node == null) {
                System.out.println("node == null");
            }
            else {
                System.out.println(node.asLiteral().getLexicalForm());
            }
        }

    }
}
