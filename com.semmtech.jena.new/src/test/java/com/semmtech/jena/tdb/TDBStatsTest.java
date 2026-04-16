package com.semmtech.jena.tdb;


//import java.util.Calendar;
import java.util.Iterator;

import org.apache.jena.riot.Lang;
//import org.apache.jena.riot.RDFDataMgr;
//import org.apache.log4j.Level;
import org.apache.log4j.Logger;

//import tdb.tdbstats;

import com.hp.hpl.jena.query.Dataset;
//import com.hp.hpl.jena.query.DatasetFactory;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Model;
//import com.hp.hpl.jena.sparql.core.DatasetGraph;
//import com.hp.hpl.jena.sparql.core.assembler.AssemblerUtils;
//import com.hp.hpl.jena.sparql.core.assembler.DatasetAssemblerVocab;
//import com.hp.hpl.jena.sparql.util.Symbol;
import com.hp.hpl.jena.tdb.TDB;
//import com.hp.hpl.jena.tdb.TDBBackup;
import com.hp.hpl.jena.tdb.TDBFactory;
//import com.hp.hpl.jena.tdb.TDBLoader;
//import com.hp.hpl.jena.tdb.mgt.TDBMgt;
//import com.hp.hpl.jena.tdb.sys.TDBMaker;
import com.semmtech.log4j.StandardConfigurator;


public class TDBStatsTest {
    private static Logger logger = Logger.getLogger(TDBStatsTest.class);

    public static void main(String[] args) {
        StandardConfigurator.configure();

        TDB.getContext().set(TDB.symUnionDefaultGraph, true);
        Dataset dataset = TDBFactory.createDataset("tdb");

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
        // Can also be set during execution
        // exec.getContext().set(TDB.symUnionDefaultGraph, true);
        logger.info("Executing SPARQL query...");
        ResultSet result = exec.execSelect();
        ResultSetFormatter.out(System.out, result);

        TDBUtils.exportTDBDataset("tdb", System.out, Lang.TRIG);
    }
}
