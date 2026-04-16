package com.semmtech.jena;


import java.io.File;
import java.io.UnsupportedEncodingException;

import org.junit.Before;
import org.junit.Test;

import com.hp.hpl.jena.ontology.OntDocumentManager;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ModelMaker;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.reasoner.rulesys.RDFSRuleReasonerFactory;
import com.hp.hpl.jena.util.FileUtils;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;


@SuppressWarnings("static-method")
public class ModelMakerTests {

    public ModelMakerTests() {

    }

    @Before
    public void initialize() {
        // com.semmtech.logging.Log4jConfigurator.configureDebugConsole();
    }

    @Test
    public void testMaker() throws UnsupportedEncodingException {
        // Create the folder to store maker's files
        String root = "C:/Temp/.jena./";
        File folder = new File(root);
        if (!folder.exists()) {
            folder.mkdir();
        }

        // Use maker
        ModelMaker maker = ModelFactory.createFileModelMaker(root);
        OntDocumentManager manager = new OntDocumentManager();
        manager.addAltEntry(RDF.getURI(),
                "C:/Users/Mike Henrichs/runtime-semmwebEditor.product/Default/imports/rdf.ttl");
        // manager.addAltEntry(RDFS.getURI(),
        // "C:/Users/Mike Henrichs/runtime-semmwebEditor.product/Default/imports/rdfs.ttl");
        // manager.addAltEntry(OWL.getURI(),
        // "C:/Users/Mike Henrichs/runtime-semmwebEditor.product/Default/imports/owl.ttl");

        OntModel ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM,
                maker.createModel("base"));

        ontModel.begin();

        for (String uri : new String[] { RDF.getURI(), RDFS.getURI(), OWL.getURI() }) {
            Model model = maker.getModel(uri);
            if (model == null) {
                System.out.println("Model <" + uri + "> not found by maker!");
                System.out.println("Creating model <" + uri + ">");
                model = maker.createModel(uri);
                String url = manager.doAltURLMapping(uri);
                System.out.println("Retrieving model <" + uri + "> from \"" + url + "\"");
                model.read(url);
            }
            else {
                System.out.println("Model <" + uri + "> found by maker!");
            }

            // Do your thing:
            model.begin();
            System.out.println("Model <" + uri + ">:\n-----");
            model.write(System.out, FileUtils.langTurtle);
            ontModel.addSubModel(model);

            System.out.println("-----");
            model.commit();
        }

        Property p = ontModel.createProperty("http://www.mike.com/ontology/hasSomething");
        p.addProperty(RDFS.label, "has something");

        InfModel infModel = ModelFactory.createInfModel(RDFSRuleReasonerFactory.theInstance()
                .create(null), ontModel);

        System.out.println("InfModel:\n-----");
        infModel.write(System.out, FileUtils.langTurtle);
        System.out.println("-----");
        System.out.println("OntModel:\n-----");
        ontModel.write(System.out, FileUtils.langTurtle);
        System.out.println("-----");

        for (ExtendedIterator<OntProperty> iter = ontModel.listOntProperties(); iter.hasNext();) {
            System.out.println(iter.next().toString());
        }

        ontModel.commit();
    }
}
