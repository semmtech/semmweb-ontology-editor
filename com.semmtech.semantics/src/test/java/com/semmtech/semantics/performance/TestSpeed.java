package com.semmtech.semantics.performance;

import java.io.File;
import java.io.FileInputStream;
import java.util.Iterator;

import org.junit.Test;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.ReasonerRegistry;
import com.hp.hpl.jena.util.FileUtils;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.semmtech.semantics.model.PredicateSelector;

public class TestSpeed {

    // @Test
    public void testModels() {
        Model model = ModelFactory.createDefaultModel();
        model.read(OWL.getURI());
        model.write(System.out, FileUtils.langTurtle);

        InfModel infModel = ModelFactory.createInfModel(ReasonerRegistry.getRDFSReasoner(), model);

        Resource owlNothing1 = model.getResource(OWL.Nothing.getURI());
        Resource owlNothing2 = infModel.getResource(OWL.Nothing.getURI());

        System.out.println("-- owlNothing1:");
        for (Iterator<Statement> iter = owlNothing1.listProperties(); iter.hasNext();) {
            Statement stmt = iter.next();
            System.out.println(stmt.toString());
        }
        System.out.println("-- owlNothing2:");
        for (Iterator<Statement> iter = owlNothing2.listProperties(); iter.hasNext();) {
            Statement stmt = iter.next();
            System.out.println(stmt.toString());
        }
    }

    @Test
    public void testJena() {
        String path = "C:/Users/Mike Henrichs/runtime-semmwebEditor.product/Default/Coinsweb/otl.1.2-recommended.ttl";
        // String path =
        // "C:/Users/Mike Henrichs/runtime-semmwebEditor.product/Default/imports/owl.ttl";
        Model model = ModelFactory.createDefaultModel();

        SimpleSelector selector = PredicateSelector.of(RDFS.subClassOf, RDFS.subPropertyOf, RDFS.label, RDF.type);
        try {
            model.read(new FileInputStream(new File(path)), null, FileUtils.langTurtle);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        System.out.println(String.format("Read %s statements", model.listStatements().toList().size()));

        Model filtered = ModelFactory.createDefaultModel();
        filtered.add(model.listStatements(selector).toList());
        System.out.println(String.format("Filtered %s statements", filtered.listStatements().toList().size()));

        // String source =
        // String.format("[ (?X <%s> ?Y), (?Y <%s> ?Z) -> (?X <%s> ?Z) ] ",
        // RDFS.subClassOf, RDFS.subClassOf, RDFS.subClassOf);
        // List<Rule> rules = Rule.parseRules(source);
        // Reasoner reasoner = new GenericRuleReasoner(rules);
        Reasoner reasoner = ReasonerRegistry.getRDFSReasoner();
        InfModel inf = ModelFactory.createInfModel(reasoner, filtered);
        System.out.println(String.format("Inferred %s statements", inf.listStatements().toList().size()));

        // String sparqlQuery = String
        // .format("SELECT ?label ?superLabel WHERE { ?class <%s> <%s> . ?class <%s> ?label . ?class <%s> ?super . ?super <%s> ?superLabel . }",
        // RDF.type, OWL.Class, RDFS.label, RDFS.subClassOf, RDFS.label);
        // Query query = QueryFactory.create(sparqlQuery);
        // QueryExecution execution = QueryExecutionFactory.create(query, inf);
        // ResultSet result = execution.execSelect();
        // ResultSetFormatter.out(System.out, result);

        String sparqlQuery = String.format(
                "SELECT ?property ?label WHERE { ?property <%s> <%s> . ?property <%s> ?label . }", RDF.type,
                RDF.Property, RDFS.label);
        Query query = QueryFactory.create(sparqlQuery);
        QueryExecution execution = QueryExecutionFactory.create(query, inf);
        ResultSet result = execution.execSelect();
        ResultSetFormatter.out(System.out, result);

    }
}
