package com.semmtech.jena;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.log4j.Logger;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.util.FileUtils;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.semmtech.jena.skolem.Skolemizer;
import com.semmtech.jena.vocabulary.Skolem;
import com.semmtech.log4j.StandardConfigurator;


public class SkolemizationTest {

    private static Logger logger = Logger.getLogger(SkolemizationTest.class);

    public static class SkolemUtils {

        // public static String guessSkolemIRI(Model source, Resource resource,
        // Model skolemized) {
        // String sourceId = "urn:graph:source";
        // String skolemizedId = "urn:graph:skolemized";
        //
        // Dataset dataset = DatasetFactory.createMem();
        // dataset.addNamedModel(sourceId, source);
        // dataset.addNamedModel(skolemizedId, skolemized);
        //
//            // @formatter:off
//            
//            
//            String sparl = "SELECT ?skolemized "
//                    + "WHERE { "
//                    + "     "
//                    + "     { "
//                    + "         SELECT ?skolemized "
//                    + "         WHERE { "
//                    + "             { ?skolemized rdf:type skolem:Skolemized . } "
//                    + "             UNION "
//                    + "             { "
//                    + "                 ?skolemized ?p ?q . "
//                    + "                 ?skolemized apf:splitIRI(?ns ?local) ."
//                    + "                 FILTER ( regex(?ns, genid:, 'i') "
//                    + "             } "
//                    + "         } "
//                    + "     } "
//                    + "}";
//            
//            // @formatter:on
        // }
    }

    public static void main(String[] args) {
        StandardConfigurator.configure();

        // Skolemization

        Model model = ModelFactory.createDefaultModel();
        try {
            logger.info("Reading model with anonymous resources...");
            RDFDataMgr.read(model, new FileInputStream(
                    "src/test/resources/model-with-anonymous.ttl"), null, Lang.TURTLE);
        }
        catch (FileNotFoundException ex) {
            logger.error("Error reading model with anonymous resources", ex);
            return;
        }
        model.setNsPrefix("skolem", Skolem.NS);
        model.setNsPrefix("genid", Skolemizer.DEFAULT_GENERATED_NS);

        Skolemizer skolemizer = new Skolemizer();

        logger.info("Skolemizing model...");
        Model skolemized = skolemizer.skolemize(model);

        try {
            logger.info("Writing model to disk...");
            RDFDataMgr.write(new FileOutputStream("src/test/output/skolemized.ttl"), skolemized,
                    Lang.TURTLE);
        }
        catch (FileNotFoundException ex) {
            logger.error("Error writing model to disk", ex);
            return;
        }

        logger.info("Anonymizing skolemized model...");
        skolemized = ModelFactory.createDefaultModel();
        try {
            logger.info("Reading skolemized model...");
            RDFDataMgr.read(skolemized, new FileInputStream("src/test/output/skolemized.ttl"),
                    null, Lang.TURTLE);
        }
        catch (FileNotFoundException ex) {
            logger.error("Error reading skolemized model", ex);
            return;
        }

        Model deskolemized1 = skolemizer.deskolemize(skolemized);
        skolemizer.setKeepSkolemIRIs(false);
        Model deskolemized2 = skolemizer.deskolemize(skolemized);
        try {
            logger.info("Writing de-skolemized models to disk...");
            RDFDataMgr.write(new FileOutputStream("src/test/output/de-skolemized-with-iris.ttl"),
                    deskolemized1, Lang.TURTLE);
            RDFDataMgr.write(new FileOutputStream("src/test/output/de-skolemized.ttl"),
                    deskolemized2, Lang.TURTLE);
        }
        catch (FileNotFoundException ex) {
            logger.error("Error writing model to disk", ex);
            return;
        }

        Model reskolemized = skolemizer.skolemize(deskolemized1);
        try {
            logger.info("Writing re-skolemized models to disk...");
            RDFDataMgr.write(new FileOutputStream("src/test/output/re-skolemized.ttl"),
                    reskolemized, Lang.TURTLE);
        }
        catch (FileNotFoundException ex) {
            logger.error("Error writing re-skolemized model to disk", ex);
            return;
        }

        logger.info(String.format("skolemized == re-skolemized: %s",
                skolemized.isIsomorphicWith(reskolemized)));

        // Comparison

        Model original = ModelFactory.createDefaultModel();
        Model modified = ModelFactory.createDefaultModel();
        try {
            RDFDataMgr.read(original, new FileInputStream("src/test/output/skolemized.ttl"),
                    Lang.TURTLE);
            Skolemizer.readSkolemized(modified, new FileInputStream(
                    "src/test/output/skolemized.ttl"), Lang.TURTLE, true);
        }
        catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }

        skolemized = skolemizer.skolemize(modified);
        logger.info(String.format("original == skolemized (before): %s",
                original.isIsomorphicWith(skolemized)));

        Resource mike = null;
        for (ResIterator resources = modified.listResourcesWithProperty(RDFS.label,
                modified.createLiteral("Mike Henrichs")); resources.hasNext();) {
            mike = resources.next();
            break;
        }

        if (mike == null) {
            logger.error("Could not find anonumous resource");
            return;
        }

        mike.addProperty(RDFS.comment, modified.createLiteral("Commentaar op Mike Henrichs."));
        skolemizer = new Skolemizer();
        skolemizer.setKeepAnonIds(false);

        skolemized = skolemizer.skolemize(modified);
        logger.info(String.format("original == skolemized (after): %s",
                original.isIsomorphicWith(skolemized)));

        logger.info("original.difference(skolemized):");
        Model diffOS = original.difference(skolemized);
        skolemizer.setKeepSkolemIRIs(false);
        skolemizer.deskolemize(diffOS).write(System.out, FileUtils.langTurtle);
        logger.info("skolemized.difference(original):");
        Model diffSO = skolemized.difference(original);
        skolemizer.deskolemize(diffSO).write(System.out, FileUtils.langTurtle);

        // TEST: add skolem info into sub-model.

        logger.info("Done");
    }
}
