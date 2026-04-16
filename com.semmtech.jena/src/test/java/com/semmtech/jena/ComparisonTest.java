package com.semmtech.jena;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.log4j.Logger;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.util.FileUtils;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.semmtech.log4j.StandardConfigurator;


public class ComparisonTest {

    private static Logger logger = Logger.getLogger(ComparisonTest.class);

    @SuppressWarnings("unused")
    public static void main(String[] args) {
        StandardConfigurator.configure();

        Model original = ModelFactory.createDefaultModel();
        Model modified = ModelFactory.createDefaultModel();
        // RDFDataMgr.read(original, RDF.getURI(), Lang.RDFXML);
        // RDFDataMgr.read(modified, RDF.getURI(), Lang.RDFXML);

        logger.info(String.format("Original RDF model contains %s statements", original.size()));

        logger.info(String.format("Modifying model..."));

        Resource a = ResourceFactory.createResource("urn:comparison:a");
        Resource b = ResourceFactory.createResource("urn:comparison:b");

        Resource anonymous = ResourceFactory.createResource();

        Property p = ResourceFactory.createProperty("urn:comparison:p");
        Property q = ResourceFactory.createProperty("urn:comparison:q");

        modified.add(a, p, b);
        modified.add(a, RDFS.label, modified.createLiteral("a"));
        modified.add(a, RDFS.comment, modified.createLiteral("This is resource a"));

        logger.info(String.format("Modified model contains %s statements", modified.size()));
        logger.info(String.format("Calculating difference original > modified..."));
        Model diffOM = original.difference(modified);
        logger.info(String.format("Difference original > modified:"));
        diffOM.write(System.out, FileUtils.langTurtle);
        logger.info(String.format("Calculating difference modified > original..."));
        Model diffMO = modified.difference(original);
        logger.info(String.format("Difference modified > original:"));
        diffMO.write(System.out, FileUtils.langTurtle);

        Model snapshot = ModelFactory.createDefaultModel();
        snapshot.add(modified);
        modified.add(a, RDFS.seeAlso, anonymous);
        modified.add(anonymous, RDFS.label, modified.createLiteral("anonymous"));

        logger.info(String.format("Modified model contains %s statements", modified.size()));
        logger.info(String.format("Calculating difference snapshot > modified..."));
        Model diffSM = snapshot.difference(modified);
        logger.info(String.format("Difference snapshot > modified:"));
        diffSM.write(System.out, FileUtils.langTurtle);
        logger.info(String.format("Calculating difference modified > snapshot..."));
        Model diffMS = modified.difference(snapshot);
        logger.info(String.format("Difference modified > snapshot:"));
        diffMS.write(System.out, FileUtils.langTurtle);

        String snapshotPath = "src/test/tmp/snapshot.ttl";

        try (FileOutputStream fos = new FileOutputStream(snapshotPath)) {
            RDFDataMgr.write(fos, modified, Lang.TURTLE);
        }
        catch (IOException ex) {
            logger.error("Error writing file!", ex);
            return;
        }

        snapshot = ModelFactory.createDefaultModel();
        modified = ModelFactory.createDefaultModel();
        // Not just copy but reread from disk
        // snapshot.add(modified);
        try (FileInputStream fis1 = new FileInputStream(snapshotPath);
                FileInputStream fis2 = new FileInputStream(snapshotPath)) {
            RDFDataMgr.read(snapshot, fis1, Lang.TURTLE);
            RDFDataMgr.read(modified, fis2, Lang.TURTLE);
        }
        catch (IOException ex) {
            logger.error("Error reading file!", ex);
            return;
        }

        // Be sure that no reference is kept to the original anonymous resource!
        // anonymous = null;
        // for (ResIterator resources =
        // modified.listResourcesWithProperty(RDFS.label,
        // modified.createLiteral("anonymous")); resources.hasNext();) {
        // anonymous = resources.next();
        // break;
        // }
        //
        // if (anonymous == null) {
        // logger.error("Unable to find the anonymous resource");
        // return;
        // }
        //
        // modified.add(anonymous, RDFS.comment,
        // modified.createLiteral("Additional comment on the anonymous
        // resource"));
        // modified.add(b, q, a);

        logger.info(String.format("Modified model contains %s statements", modified.size()));
        logger.info(String.format("Calculating difference snapshot > modified..."));
        diffSM = snapshot.difference(modified);
        logger.info(String.format("Difference snapshot > modified:"));
        diffSM.write(System.out, FileUtils.langTurtle);
        logger.info(String.format("Calculating difference modified > snapshot..."));
        diffMS = modified.difference(snapshot);
        logger.info(String.format("Difference modified > snapshot:"));
        diffMS.write(System.out, FileUtils.langTurtle);
        logger.info(String.format("modified == snapshot: %s", modified.isIsomorphicWith(snapshot)));

        modified.write(System.out, FileUtils.langTurtle);

        a = modified.createResource(a.getURI());
        a.removeAll(RDFS.label);
        a.addProperty(RDFS.label, "a (new)");

        modified.write(System.out, FileUtils.langTurtle);

        logger.info(String.format("modified == snapshot: %s", modified.isIsomorphicWith(snapshot)));

        logger.info(String.format("Done"));
    }
}
