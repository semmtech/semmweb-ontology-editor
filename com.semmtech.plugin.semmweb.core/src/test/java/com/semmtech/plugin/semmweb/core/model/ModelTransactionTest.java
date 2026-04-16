package com.semmtech.plugin.semmweb.core.model;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.AnonId;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;


public class ModelTransactionTest {

    private OntModel ontModel;
    private OntModel otherOntModel;
    private ModelTransaction transaction;
    private ModelTransaction transactionWithShadowModel;

    private Statement testStatement;
    private List<Statement> testStatements;

    private Statement createTestStatement(Model model) {
        Resource resource = model.createResource();
        Statement s = model.createStatement(resource, RDFS.subClassOf, resource);
        return s;
    }

    private Statement createTestDuplicateStatement(Model model) {
        Statement s = model.createStatement(RDFS.Class, RDF.first, RDFS.domain);
        return s;
    }

    /**
     * Creates a list of statements in which no two are identical.
     */
    private List<Statement> createTestStatementsSet(Model model) {
        ArrayList<Statement> l = Lists.newArrayList();

        Resource resource1 = model.createResource();
        Resource resource2 = model.createResource();
        Statement s1 = model.createStatement(resource1, RDFS.seeAlso, resource1);
        Statement s2 = model.createStatement(resource1, RDFS.subPropertyOf, resource1);
        Statement s3 = model.createStatement(resource2, RDFS.seeAlso, resource1);

        l.add(s1);
        l.add(s2);
        l.add(s3);

        return l;
    }

    StmtIterator getTestStatmentsIterOnProperty(OntModel model, Property p) {
        return model.listStatements(new SimpleSelector(null, p, (RDFNode) null));
    }

    @Before
    public void before() {
        this.ontModel = ModelFactory.createOntologyModel();
        this.otherOntModel = ModelFactory.createOntologyModel();

        ontModel.add(createTestDuplicateStatement(ontModel));

        String description = "Test transaction";
        this.transaction = new ModelTransaction(ontModel, description);

        description = "Test transaction with shadow model enabled";
        this.transactionWithShadowModel = new ModelTransaction(ontModel, description, true);

        // these functions do not yet add the created statements to the model.
        this.testStatement = createTestStatement(ontModel);
        this.testStatements = createTestStatementsSet(ontModel);

        assertEquals(0, transaction.getModelChanges().size());
    }

    @After
    public void after() {
        this.ontModel.close();
        this.otherOntModel.close();

        this.transaction.dispose();
    }

    @Test
    public void test_catchOnlyTrueStatementChanges() {
        /*
         * By "true statement changes", I refer to statements that actually
         * alter the model instead of being applied without any effect. If a
         * model already contains a statement, adding that very statement again
         * has no effect. Similarly, if a model does not contain a certain
         * statement, attempting to remove that statement has no effect on the
         * model.
         */
        /*
         * Transactions without a shadow model enabled cannot distinguish
         * whether a statement effects a "true statement change". Only
         * transactions with a shadow model enabled can.
         */
        ontModel.add(createTestDuplicateStatement(ontModel));
        assertEquals(1, transaction.getModelChanges().size());
        assertEquals(0, transactionWithShadowModel.getModelChanges().size());

        ontModel.remove(createTestDuplicateStatement(ontModel));
        assertEquals(0, transaction.getModelChanges().size());
        assertEquals(1, transactionWithShadowModel.getModelChanges().size());
    }

    @Test
    public void test_catchSingleStatementChanges() {
        // should be tracked by ModelTransaction
        int expectedHistorySize = 0;
        ontModel.add(testStatement);
        expectedHistorySize++;
        assertEquals(expectedHistorySize, transaction.getModelChanges().size());
        assertEquals(expectedHistorySize, transactionWithShadowModel.getModelChanges().size());

        ontModel.remove(testStatement);
        expectedHistorySize--;
        assertEquals(expectedHistorySize, transaction.getModelChanges().size());
        assertEquals(expectedHistorySize, transactionWithShadowModel.getModelChanges().size());
    }

    @Test
    public void test_catchStatementsListChanges() {
        // should be tracked by ModelTransaction
        int expectedHistorySize = 0;
        ontModel.add(testStatements);
        expectedHistorySize += testStatements.size();
        assertEquals(expectedHistorySize, transaction.getModelChanges().size());
        assertEquals(expectedHistorySize, transactionWithShadowModel.getModelChanges().size());

        ontModel.remove(testStatements);
        expectedHistorySize -= testStatements.size();
        assertEquals(expectedHistorySize, transaction.getModelChanges().size());
        assertEquals(expectedHistorySize, transactionWithShadowModel.getModelChanges().size());
    }

    @Test
    public void test_catchStatementsArrayChanges() {
        // should be tracked by ModelTransaction
        int expectedHistorySize = 0;
        Statement statementsArray[] = new Statement[testStatements.size()];
        statementsArray = testStatements.toArray(statementsArray);
        ontModel.add(statementsArray);
        expectedHistorySize += testStatements.size();
        assertEquals(expectedHistorySize, transaction.getModelChanges().size());
        assertEquals(expectedHistorySize, transactionWithShadowModel.getModelChanges().size());

        ontModel.remove(statementsArray);
        expectedHistorySize -= testStatements.size();
        assertEquals(expectedHistorySize, transaction.getModelChanges().size());
        assertEquals(expectedHistorySize, transactionWithShadowModel.getModelChanges().size());
    }

    @Test
    public void test_catchStatementsIteratorChanges() {
        // should be tracked by ModelTransaction
        otherOntModel.add(testStatements);
        StmtIterator statementsIter = getTestStatmentsIterOnProperty(otherOntModel, RDFS.seeAlso);
        // check whether the model indeed contained RDFS.seeAlso statements
        int statementsIterSize = statementsIter.toList().size();
        assertTrue(statementsIterSize > 0);

        int expectedHistorySize = 0;
        statementsIter = getTestStatmentsIterOnProperty(otherOntModel, RDFS.seeAlso);
        ontModel.add(statementsIter);
        expectedHistorySize += statementsIterSize;
        assertEquals(expectedHistorySize, transaction.getModelChanges().size());
        assertEquals(expectedHistorySize, transactionWithShadowModel.getModelChanges().size());

        statementsIter = getTestStatmentsIterOnProperty(otherOntModel, RDFS.seeAlso);
        ontModel.remove(statementsIter);
        expectedHistorySize -= statementsIterSize;
        assertEquals(expectedHistorySize, transaction.getModelChanges().size());
        assertEquals(expectedHistorySize, transactionWithShadowModel.getModelChanges().size());
    }

    @Test
    public void test_catchModelChanges() {
        // should be tracked by ModelTransaction
        otherOntModel.add(testStatements);

        int expectedHistorySize = 0;
        ontModel.add(otherOntModel);
        // (although the exact number of changes cannot be known in advance,
        // we should at least check whether changes were indeed tracked)
        assertTrue(transaction.getModelChanges().size() > expectedHistorySize);
        assertTrue(transactionWithShadowModel.getModelChanges().size() > expectedHistorySize);
        int historySizeChangeOnModelAddition = transaction.getModelChanges().size()
                - expectedHistorySize;
        expectedHistorySize += historySizeChangeOnModelAddition;

        ontModel.remove(otherOntModel);
        expectedHistorySize -= historySizeChangeOnModelAddition;
        assertEquals(expectedHistorySize, transaction.getModelChanges().size());
        assertEquals(expectedHistorySize, transactionWithShadowModel.getModelChanges().size());
    }

    @Test
    public void test_catchRemoveAllChanges() {
        // should be tracked by ModelTransaction
        int statementsSize = testStatements.size();

        ontModel.add(testStatements);
        assertEquals(statementsSize, transaction.getModelChanges().size());
        assertEquals(statementsSize, transactionWithShadowModel.getModelChanges().size());

        ontModel.removeAll();
        assertEquals(0, transaction.getModelChanges().size());
        // For transactionWithShadowModel the result is 1 due to removal of
        // duplicateStatement which was already added in the before() method,
        // before any tracking took place.
        assertEquals(1, transactionWithShadowModel.getModelChanges().size());
    }

    @Test
    public void test_setToAbortBaseModel() {
        ontModel.add(testStatements);
        AnonId anonResourceId = ontModel.createResource().addProperty(RDFS.label, "0xB").getId();
        long initialSize = ontModel.size();

        ModelTransaction markerTransaction = new ModelTransaction(ontModel, "tracking changes",
                true);
        ontModel.createRestriction(RDFS.comment);
        assertEquals(2, markerTransaction.getModelChanges().size());

        markerTransaction.revertBaseModel();
        markerTransaction.dispose();
        assertEquals(initialSize, ontModel.size());
        ResIterator resIter = ontModel.listSubjectsWithProperty(RDFS.label, "0xB");
        assertTrue(resIter.hasNext());
        Resource foundResource = resIter.next();
        assertTrue(foundResource.isAnon());
        assertEquals(anonResourceId, foundResource.getId());
    }
}
