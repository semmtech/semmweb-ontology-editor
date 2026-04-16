package com.semmtech.jena.datasets;


import java.util.Calendar;
import java.util.Iterator;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.sparql.core.assembler.AssemblerUtils;
import com.hp.hpl.jena.tdb.assembler.VocabTDB;
import com.semmtech.ClassLoaderUtils;
import com.semmtech.log4j.StandardConfigurator;


public class TDBDatasetTest {

    private static Logger logger = Logger.getLogger(TDBDatasetTest.class);

    public static void main(String[] args) {
        StandardConfigurator.configure();

        String assemblerFile = ClassLoaderUtils.getPluginResource(TDBDatasetTest.class,
                "src/test/resources/tdb-assemble.ttl");
        long start = Calendar.getInstance().getTimeInMillis();
        logger.info("Assembling the TDB dataset...");

        Dataset dataset = (Dataset) AssemblerUtils.build(assemblerFile, VocabTDB.tDatasetTDB);
        logger.info(String.format("Assembly took %s ms.", Calendar.getInstance().getTimeInMillis()
                - start));

        dataset.begin(ReadWrite.READ);

        Model defaultModel = dataset.getDefaultModel();
        logger.info(String.format("Default model contains %s statements", defaultModel.size()));
        int count = 0;
        for (Iterator<String> names = dataset.listNames(); names.hasNext(); count++) {
            String name = names.next();
            logger.info(String.format("Dataset contains \"%s\"", name));
        }
        logger.info(String.format("Dataset contains %s named models", count));

        String sparql = "SELECT * WHERE { { GRAPH ?graph { ?s ?p ?o . } } UNION { BIND(<urn:default> AS ?graph) . ?s ?p ?o . } } ORDER BY STR(?graph) ";

        QueryExecution exec = QueryExecutionFactory.create(sparql, dataset);
        logger.info("Executing SPARQL query...");
        ResultSet result = exec.execSelect();
        ResultSetFormatter.out(System.out, result);

        dataset.end();
    }
}
