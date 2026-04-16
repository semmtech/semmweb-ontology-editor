package com.semmtech.jena;


import java.util.List;
import java.util.UUID;

import org.junit.Test;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.DatasetFactory;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ModelMaker;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.util.FileUtils;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;


@SuppressWarnings("static-method")
public class AnonymousGuidTest {
    public AnonymousGuidTest() {

    }

    @Test
    public void testGuids() {
        String guidUri = "http://mike.com/guids/hasGuid";

        ModelMaker maker = ModelFactory.createFileModelMaker("C:/Temp/");
        OntModel ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM,
                maker.createFreshModel());

        Model guidsModel = ModelFactory.createDefaultModel();
        Property guid = guidsModel.createProperty(guidUri);
        ontModel.addSubModel(guidsModel);

        Resource a1 = ontModel.createResource();
        a1.addProperty(RDF.type, OWL.Thing);
        a1.addProperty(RDFS.label, "Mike Henrichs");

        guidsModel.add(a1, guid, UUID.randomUUID().toString());

        System.out.println("ontModel = ");
        ontModel.write(System.out, FileUtils.langTurtle);
        System.out.println("---");
        System.out.println("guidsModel = ");
        guidsModel.write(System.out, FileUtils.langTurtle);
        System.out.println(String.format("a1.id = %s", a1.getId().toString()));
        System.out.println(String.format("a1.guid = %s", a1.getProperty(guid).getString()));

        for (ExtendedIterator<Resource> iter = ontModel.listSubjects(); iter.hasNext();) {
            Resource r = iter.next();
            if (r.isAnon()) {
                System.out.println(String.format("r.id = %s", r.getId().toString()));
            }
        }

        Dataset dataset = DatasetFactory.createMem();
        dataset.setDefaultModel(ontModel.getBaseModel());
        dataset.addNamedModel("urn:guids", guidsModel);

        dataset.begin(ReadWrite.WRITE);
        ontModel.add(OWL.Thing, RDFS.label, "ding");
        dataset.abort();

        System.out.println("--- Aborted!");
        System.out.println("ontModel = ");
        ontModel.write(System.out, FileUtils.langTurtle);
        for (ExtendedIterator<Resource> iter = ontModel.listSubjects(); iter.hasNext();) {
            Resource r = iter.next();
            if (r.isAnon()) {
                System.out.println(String.format("r.id = %s", r.getId().toString()));
            }
        }

        String sparql = "PREFIX rdf: <" + RDF.getURI() + "> ";
        sparql += "PREFIX owl: <" + OWL.getURI() + "> ";
        sparql += "SELECT *";
        sparql += "WHERE { ";
        sparql += "    ?node a owl:Thing . ";
        sparql += "    GRAPH <urn:guids> { ";
        sparql += "        ?node <" + guidUri + "> ?guid . ";
        sparql += "    } ";
        sparql += "} ";
        Query query = QueryFactory.create(sparql);
        QueryExecution execution = QueryExecutionFactory.create(query, dataset);
        ResultSet result = execution.execSelect();
        while (result.hasNext()) {
            QuerySolution solution = result.next();
            String nodeGuid = solution.getLiteral("guid").getLexicalForm();

            List<Resource> list = ontModel.listSubjectsWithProperty(guid,
                    ontModel.createLiteral(nodeGuid)).toList();
            if (!list.isEmpty()) {
                Resource a = list.get(0);
                System.out.println(String.format("a.id = %s", a.getId().toString()));
                System.out.println(String.format("a.guid = %s", a.getProperty(guid).getString()));
                System.out.println(String.format("a.label = %s", a.getProperty(RDFS.label)
                        .getString()));
            }
        }
    }
}
