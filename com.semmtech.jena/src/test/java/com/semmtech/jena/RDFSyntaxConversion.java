package com.semmtech.jena;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;


public class RDFSyntaxConversion {

    public static void main(String[] args) {
        String input = "C:\\Users\\Mike Henrichs\\Downloads\\otl-otl-1.5.0-11-Besix\\bim\\otl-otl.11.owl";
        String output = "C:\\Users\\Mike Henrichs\\Downloads\\otl-otl-1.5.0-11-Besix\\bim\\otl-otl.11.ttl";

        Model model = ModelFactory.createDefaultModel();
        System.out.println("Reading model...");

        try (FileInputStream fis = new FileInputStream(input)) {
            RDFDataMgr.read(model, fis, Lang.RDFXML);
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }

        System.out.println(String.format("Model contains %s statements", model.size()));
        System.out.println("Writing to Turtle...");

        try (FileOutputStream fos = new FileOutputStream(output)) {
            RDFDataMgr.write(fos, model, Lang.TURTLE);
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }

        System.out.println("Done");
    }
}
