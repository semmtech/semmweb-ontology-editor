package com.semmtech.semantics.model;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ModelMaker;
import com.hp.hpl.jena.util.FileUtils;
import com.semmtech.io.StringOutputStream;
import com.semmtech.logging.Log4jConfigurator;


public class BooleanOperatorTest {
    private static Logger logger = Logger.getLogger(BooleanOperatorTest.class);

    public BooleanOperatorTest() {

    }

    @Before
    public void setup() {
        Log4jConfigurator.configureDebugConsole();
        ModelFactory.createDefaultModel();
    }

    /**
     * @throws FileNotFoundException
     * 
     */
    @Test
    public void testDifference() throws IOException {
        // Read the models
        Model modelA = getModel("otl-recommended.ttl", FileUtils.langTurtle);
        Model modelB = getModel("etim.owl", FileUtils.langXMLAbbrev);

        // Perform boolean operations
        long start = Calendar.getInstance().getTimeInMillis();
        logger.info("Checking difference A-B");
        Model diffAB = modelA.difference(modelB);
        logger.info("Checking difference B-A");
        Model diffBA = modelB.difference(modelA);
        logger.info("Checking intersection A^B");
        Model intersection = modelA.intersection(modelB);
        long end = Calendar.getInstance().getTimeInMillis();
        logger.info(String.format("Execution took %s ms.", end - start));

        // Create some output
        try (StringOutputStream output = new StringOutputStream();
                StringOutputStream output1 = new StringOutputStream();
                StringOutputStream output2 = new StringOutputStream()) {

            diffAB.write(output, FileUtils.langTurtle);
            logger.info(String.format("Diff A-B created (%s statements):", diffAB.size()));

            diffBA.write(output1, FileUtils.langTurtle);
            logger.info(String.format("Diff B-A created (%s statements):", diffBA.size()));

            intersection.write(output2, FileUtils.langTurtle);
            logger.info(String.format("Intersection A^B created (%s statements):\n%s",
                    intersection.size(), output2.toString()));
        }
    }

    private Model getModel(String file, String format) throws IOException {
        ModelMaker maker = ModelFactory.createMemModelMaker();
        logger.info(String.format("Reading model %s...", file));
        Model result = maker.createFreshModel();

        try (FileInputStream fis = new FileInputStream(String.format("src/test/resources/OTL/%s",
                file))) {

            result.read(fis, null, format);
            long size = result.size();
            logger.info(String.format("Read %s statements from %s", size, file));
            return result;
        }
    }
}
