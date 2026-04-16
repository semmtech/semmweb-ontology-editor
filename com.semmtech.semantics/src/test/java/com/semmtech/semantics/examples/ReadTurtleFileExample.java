package com.semmtech.semantics.examples;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.FileUtils;


public class ReadTurtleFileExample {

    /**
     * @param args
     */
    public static void main(String[] args) {
        String filename = "src/test/resources/examples/ages.ttl";

        // / Using the guessLang the extension of the filename is used to guess
        // a encoding language
        String lang = FileUtils.guessLang(filename, FileUtils.langTurtle);

        // / Read model from file
        OntModel model = readOntologyFromFile(filename, lang);

        // / Print model to the System.out in Turtle format
        CreateModelProgrammaticallyExample.printModel(model, FileUtils.langTurtle);

        writeModelToFile(model, "D:\\test.ttl", lang);
    }

    /**
     * Writes the model to the specified file, using the given language.
     * 
     * @param model
     * @param filename
     * @param lang
     */
    public static void writeModelToFile(Model model, String filename, String lang) {
        try {
            model.write(new FileOutputStream(new File(filename)), lang, null);
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads an ontology model from the specified file, this file is formatted
     * using the given language.
     * 
     * @param filename
     * @param lang
     * @return
     */
    public static OntModel readOntologyFromFile(String filename, String lang) {
        OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);

        try (InputStream input = new FileInputStream(new File(filename))) {
            model.read(input, null, lang);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return model;
    }
}
