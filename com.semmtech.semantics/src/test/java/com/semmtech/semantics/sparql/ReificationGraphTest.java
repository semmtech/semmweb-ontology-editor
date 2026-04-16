package com.semmtech.semantics.sparql;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.DatasetFactory;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ModelMaker;
import com.hp.hpl.jena.rdf.model.ReifiedStatement;
import com.hp.hpl.jena.util.FileUtils;


public class ReificationGraphTest {
    public ReificationGraphTest() {

    }

    @SuppressWarnings("unused")
    @Test
    public void testReification() throws IOException {
        String baseName = "<base>";
        Dataset dataset = DatasetFactory.createMem();

        ModelMaker maker = ModelFactory.createMemModelMaker();
        Model model = maker.createFreshModel();

        try (FileInputStream fis = new FileInputStream("src/test/resources/examples/rei-model.ttl")) {
            model.read(fis, null, FileUtils.langTurtle);
        }

        System.out.println(String.format("Model read (%s statements):", model.size()));
        model.write(System.out, FileUtils.langTurtle);

        dataset.setDefaultModel(model);

        System.out.println("\nReified Statements:");
        for (ReifiedStatement reified : model.listReifiedStatements().toList()) {
            System.out.println("Reified: " + reified.toString());
        }

        // INSERT reified
        try (FileInputStream fis = new FileInputStream("src/test/resources/examples/reify.sparql")) {
            executeQuery(fis, dataset);
        }

        try (FileInputStream fis = new FileInputStream("src/test/resources/examples/simple.sparql")) {
            executeQuery(fis, dataset);
        }

        try (FileInputStream fis = new FileInputStream("src/test/resources/examples/reified.sparql")) {
            executeQuery(fis, dataset);
        }

    }

    private void executeQuery(InputStream stream, Dataset dataset) throws IOException {
        String sparql = IOUtils.toString(stream);
        System.out.println("\nSPARQL:\n" + sparql);

        QueryExecution execution = QueryExecutionFactory.create(sparql, dataset);
        ResultSet result = execution.execSelect();
        System.out.println("\nResult:");
        ResultSetFormatter.out(System.out, result);
    }
}
