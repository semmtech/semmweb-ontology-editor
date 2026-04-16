package com.semmtech.plugin.semmweb.editor.views.taxonomy;


import static org.junit.Assert.fail;

import java.io.FileInputStream;
import java.io.InputStream;

import org.junit.Test;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.tdb.TDBFactory;
import com.hp.hpl.jena.tdb.base.file.Location;
import com.hp.hpl.jena.util.FileUtils;


public class TestTDB {

    private static final String FILE = "src\\test\\resources\\models\\model.ttl";
    private static final String TDB_FOLDER = "src\\test\\resources\\models\\TDB";

    @Test
    public void test() {
        Location location = new Location(TDB_FOLDER);
        Dataset dataset = TDBFactory.createDataset(location);

        // TDBLoader.load(indexingDataset, inputs, true);

        Model model = dataset.getDefaultModel();

        try (InputStream is = new FileInputStream(FILE)) {
            model.read(is, null, FileUtils.guessLang(FILE));
        }
        catch (Exception e) {
            e.printStackTrace();
            fail("Cannot read model");
        }

        dataset.begin(ReadWrite.READ);
        String qs1 = "SELECT * {?s ?p ?o}";

        QueryExecution qExec = QueryExecutionFactory.create(qs1, dataset);
        ResultSet rs = qExec.execSelect();
        ResultSetFormatter.out(rs);
        qExec.close();

        String qs2 = "SELECT * {?s ?p ?o} OFFSET 10 LIMIT 10";
        qExec = QueryExecutionFactory.create(qs2, dataset);
        rs = qExec.execSelect();
        ResultSetFormatter.out(rs);
        qExec.close();

    }
}
