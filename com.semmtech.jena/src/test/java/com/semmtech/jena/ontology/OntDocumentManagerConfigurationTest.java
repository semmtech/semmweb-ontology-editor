package com.semmtech.jena.ontology;


import java.io.File;
import java.io.FileOutputStream;
import java.util.Iterator;

import org.junit.Test;

import com.hp.hpl.jena.ontology.OntDocumentManager;
import com.hp.hpl.jena.util.FileUtils;


@SuppressWarnings("static-method")
public class OntDocumentManagerConfigurationTest {
    public OntDocumentManagerConfigurationTest() {

    }

    private static final String POLICY_FILENAME = "C:\\Temp\\ont-policy.ttl";

    @Test
    public void testTraditional() {
        try {
            OntDocumentManager manager = new OntDocumentManager(POLICY_FILENAME);
            System.out.println("\nTraditional: ");
            printManager(manager);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Test
    public void testNew() {
        try {
            OntDocumentManagerConfiguration config = new OntDocumentManagerConfiguration();
            config.setProcessImports(true);
            config.setCacheModels(false);

            String owlUri = "http://www.w3.org/2002/07/owl";
            config.addOntologySpec(owlUri, "file:///C:/owl.ttl");
            config.setPrefix(owlUri, "owl");

            OntDocumentManager manager = config.createManager();
            System.out.println("\nNew: ");
            printManager(manager);

            // close the stream, Jena implementations doen't close the readers
            // stream
            try (FileOutputStream out = new FileOutputStream(new File(
                    "C:\\Temp\\ont-policy-modified.ttl"))) {
                config.write(out, FileUtils.langTurtle);
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void printManager(OntDocumentManager manager) {
        System.out.println("processImports = " + manager.getProcessImports());
        System.out.println("cacheModels = " + manager.getCacheModels());

        for (Iterator<String> iter = manager.listDocuments(); iter.hasNext();) {
            String document = iter.next();

            System.out.println("document = " + document);
        }
    }
}
