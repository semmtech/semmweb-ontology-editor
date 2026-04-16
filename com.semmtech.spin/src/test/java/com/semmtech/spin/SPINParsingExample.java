package com.semmtech.spin;


import org.topbraid.spin.arq.ARQ2SPIN;
import org.topbraid.spin.arq.ARQFactory;
import org.topbraid.spin.system.SPINModuleRegistry;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.FileUtils;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;


/**
 * Converts between textual SPARQL representation and SPIN RDF model.
 * 
 * @author Holger Knublauch
 */
public class SPINParsingExample {

    public static void main(String[] args) {

        // Register system functions (such as sp:gt (>))
        SPINModuleRegistry.get().init();

        // Create an empty OntModel importing SP
        Model model = ModelFactory.createDefaultModel();
        model.setNsPrefix("rdf", RDF.getURI());
        model.setNsPrefix("rdfs", RDFS.getURI());
        model.setNsPrefix("ex", "http://example.org/demo#");
        model.setNsPrefix("sp", "http://spinrdf.org/sp#");

        String query = "CONSTRUCT { rdf:first rdfs:label ?text } WHERE { BIND(STRLANG(rdf:rest, \"en\") AS ?text) }";

        Query arqQuery = ARQFactory.get().createQuery(model, query);
        ARQ2SPIN arq2SPIN = new ARQ2SPIN(model);
        org.topbraid.spin.model.Query spinQuery = arq2SPIN.createQuery(arqQuery, null);

        System.out.println("SPIN query in Turtle:");
        model.write(System.out, FileUtils.langTurtle);

        System.out.println("-----");
        String str = spinQuery.toString();
        System.out.println("SPIN query:\n" + str);

        // Now turn it back into a Jena Query
        Query parsedBack = ARQFactory.get().createQuery(spinQuery);
        parsedBack.setPrefix("rdf", RDF.getURI());
        parsedBack.setPrefix("ex", "http://example.org/demo#");
        parsedBack.setPrefix("sp", "http://spinrdf.org/sp#");
        System.out.println("Jena query:\n" + parsedBack);
    }
}
