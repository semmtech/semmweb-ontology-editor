package com.semmtech.semantics;


import java.util.List;

import com.google.common.collect.Lists;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.util.FileUtils;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;


public class Example {

    /**
     * @param args
     */
    public static void main(String[] args) {
        // String content =
        // "@prefix owl: <http://www.w3.org/2002/07/owl#> . @prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> . @prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> . [] a owl:Ontology ; rdfs:label \"people\" . ";
        Model model = ModelFactory.createDefaultModel();

        // InputStream stream = new ByteArrayInputStream(content.getBytes());
        // model.read(stream, null, FileUtils.langTurtle);

        List<String> catalogs = Lists.newArrayList("http://people", "http://pizza", "http://semm");

        for (String id : catalogs) {
            Resource r = model.createResource(id);
            r.addProperty(RDF.type, OWL.Ontology);
            r.addProperty(RDFS.label, "Ontology " + id);
        }

        model.write(System.out, FileUtils.langTurtle, null);
    }

}
