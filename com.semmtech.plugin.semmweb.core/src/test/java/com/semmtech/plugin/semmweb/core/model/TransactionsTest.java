package com.semmtech.plugin.semmweb.core.model;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelMaker;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.util.FileUtils;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.semmtech.plugin.semmweb.core.model.ModelProviderImpl;
import com.semmtech.plugin.semmweb.core.model.ModelTransaction;
import com.semmtech.semantics.model.ExtendedModelFactory;


public class TransactionsTest {

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    private static final String NS = "http://test.com/#";
    private static IModelProvider modelProvider;

    public TransactionsTest() throws IOException {
        String makerPath = File.createTempFile("model", ".ttl").getAbsolutePath();
        System.out.println(makerPath);
        ModelMaker maker = ExtendedModelFactory.createFileModelMaker(makerPath);
        Model emptyModel = maker.createFreshModel();
        OntModel ontModel = ExtendedModelFactory.createOntologyModel(OntModelSpec.OWL_MEM,
                emptyModel);
        modelProvider = new ModelProviderImpl(ontModel, NS);
    }

    @Test
    public void testCommitTransaction() {
        OntModel ontModel = modelProvider.getOntModel();
        ModelTransaction t1 = modelProvider.createTransaction("t1");

        Resource r = ontModel.createResource(NS + "A", RDFS.Class);

        modelProvider.commitTransaction(t1);

        assertTrue(ontModel.contains(r, RDF.type, RDFS.Class));
    }

    @Test
    public void testAbortTransaction() {
        OntModel ontModel = modelProvider.getOntModel();
        ModelTransaction t1 = modelProvider.createTransaction("t1");

        Resource r = ontModel.createResource(NS + "A", RDFS.Class);

        modelProvider.abortTransaction(t1);

        assertFalse(ontModel.contains(r, RDF.type, RDFS.Class));
        ontModel.write(System.out, FileUtils.langTurtle);
    }

    @Test
    public void nestedTransaction() {
        OntModel ontModel = modelProvider.getOntModel();

        ModelTransaction trans1 = modelProvider.createTransaction("Transaction 1");

        Resource r1 = ontModel.createResource(NS + "nestA", RDFS.Class);

        ModelTransaction trans2 = modelProvider.createTransaction("Transaction 2");

        Resource r2 = ontModel.createResource(NS + "nestB", RDFS.Class);

        modelProvider.commitTransaction(trans2);
        modelProvider.commitTransaction(trans1);

        assertTrue(ontModel.containsResource(r1));
        assertTrue(ontModel.containsResource(r2));
    }

    @Test
    public void nestedTransactionAbort() {
        OntModel ontModel = modelProvider.getOntModel();

        ModelTransaction trans1 = modelProvider.createTransaction("Transaction 1");

        Resource r1 = ontModel.createResource(NS + "nestA", RDFS.Class);

        modelProvider.createTransaction("Transaction 2");

        Resource r2 = ontModel.createResource(NS + "nestB", RDFS.Class);

        modelProvider.abortTransaction(trans1);

        assertFalse(ontModel.containsResource(r1));
        assertFalse(ontModel.containsResource(r2));
    }

    @Test
    public void nestedTransactionWrong() {
        OntModel ontModel = modelProvider.getOntModel();

        ModelTransaction trans1 = modelProvider.createTransaction("Transaction 1");

        ontModel.createResource(NS + "nestA", RDFS.Class);

        modelProvider.createTransaction("Transaction 2");

        ontModel.createResource(NS + "nestB", RDFS.Class);

        exception.expect(IllegalArgumentException.class);
        modelProvider.commitTransaction(trans1);
    }

}
