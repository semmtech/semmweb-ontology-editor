package com.semmtech.plugin.semmweb.core.model;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.Restriction;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.OWL2;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;


public class RestrictionsModelTest {
    private OntModel ontModel;
    private RestrictionsModel restrictionsModel;
    private ModelTransaction transaction;
    private Property testOnProperty;
    private Resource testOnClass;
    private List<Restriction> testOldRestrictions;
    private List<Restriction> discardableRestrictions; // subset of
                                                       // testOldRestrictions
    private List<Restriction> nonDiscardableRestrictions; // subset of
                                                          // testOldRestrictions
    private List<Restriction> testNewRestrictions;
    private Resource testResource;

    @Before
    public void before() {
        this.ontModel = ModelFactory.createOntologyModel();
        this.testOnProperty = ontModel.createProperty("http://right.here");
        this.testOnClass = ontModel.createClass();
        this.restrictionsModel = new RestrictionsModel(ontModel);

        this.testOldRestrictions = createTestOldRestrictions();
        this.testNewRestrictions = createTestNewRestrictions();
        this.testResource = ontModel.createResource();

        this.transaction = new ModelTransaction(ontModel, "Test transaction", true);
    }

    @After
    public void after() {
        transaction.dispose();
        ontModel.close();
    }

    List<Restriction> createTestOldRestrictions() {
        discardableRestrictions = Lists.newArrayList();
        nonDiscardableRestrictions = Lists.newArrayList();

        List<Restriction> restrictions = Lists.newArrayList();
        Restriction r = ontModel.createRestriction(testOnProperty);
        restrictions.add(r);
        discardableRestrictions.add(r); // anonymous can be discarded

        r = ontModel.createRestriction(testOnProperty);
        restrictions.add(r);
        discardableRestrictions.add(r); // anonymous can be discarded

        r = ontModel.createRestriction("http://restriction.with.uri", testOnProperty);
        restrictions.add(r);
        nonDiscardableRestrictions.add(r); // uri cannot be discarded

        return restrictions;
    }

    List<Restriction> createTestNewRestrictions() {
        List<Restriction> restrictions = Lists.newArrayList();
        restrictions.add(ontModel.createRestriction(testOnProperty));
        restrictions.add(ontModel.createRestriction(testOnProperty));
        restrictions.add(ontModel.createRestriction(testOnProperty));
        return restrictions;
    }

    private void assertHasRestrictionProperties(Restriction restriction, boolean includesOnClass) {
        assertTrue(restriction.hasProperty(RDF.type));
        assertTrue(restriction.getPropertyValue(RDF.type).equals(OWL.Restriction));
        assertTrue(restriction.hasProperty(OWL.onProperty));
        assertTrue(restriction.getPropertyValue(OWL.onProperty).equals(testOnProperty));
        if (includesOnClass) {
            assertTrue(restriction.hasProperty(OWL2.onClass));
            assertTrue(restriction.getPropertyValue(OWL2.onClass).equals(testOnClass));
        }
    }

    @Test
    public void testCreateSomeValuesFromRestriction() {
        List<Restriction> newRestrictions = restrictionsModel.createRestrictions(testOnProperty, 1,
                -1, testOnClass);

        assertEquals(1, newRestrictions.size());
        assertEquals(3, transaction.getModelChanges().size());

        Restriction newRestriction = newRestrictions.get(0);
        assertHasRestrictionProperties(newRestriction, false);
        assertTrue(newRestriction.hasProperty(OWL.someValuesFrom));
        assertTrue(newRestriction.getPropertyValue(OWL.someValuesFrom).equals(testOnClass));
    }

    @Test
    public void testCreateAllValuesFromRestriction() {
        List<Restriction> newRestrictions = restrictionsModel.createRestrictions(testOnProperty, 0,
                -1, testOnClass);

        assertEquals(1, newRestrictions.size());
        assertEquals(3, transaction.getModelChanges().size());

        Restriction newRestriction = newRestrictions.get(0);
        assertHasRestrictionProperties(newRestriction, false);
        assertTrue(newRestriction.hasProperty(OWL.allValuesFrom));
        assertTrue(newRestriction.getPropertyValue(OWL.allValuesFrom).equals(testOnClass));
    }

    @Test
    public void testCreateExactCardinalityRestriction() {
        List<Restriction> newRestrictions = restrictionsModel.createRestrictions(testOnProperty, 9,
                9, null);

        assertEquals(1, newRestrictions.size());
        assertEquals(3, transaction.getModelChanges().size());

        Restriction newRestriction = newRestrictions.get(0);
        assertHasRestrictionProperties(newRestriction, false);
        assertTrue(newRestriction.hasProperty(OWL.cardinality));
    }

    @Test
    public void testCreateExactQualifiedCardinalityRestriction() {
        List<Restriction> newRestrictions = restrictionsModel.createRestrictions(testOnProperty, 9,
                9, testOnClass);

        assertEquals(1, newRestrictions.size());
        assertEquals(4, transaction.getModelChanges().size());

        Restriction newRestriction = newRestrictions.get(0);
        assertHasRestrictionProperties(newRestriction, true);
        assertTrue(newRestriction.hasProperty(OWL2.qualifiedCardinality));
    }

    @Test
    public void testCreateMinCardinalityRestriction() {
        List<Restriction> newRestrictions = restrictionsModel.createRestrictions(testOnProperty,
                11, -1, null);

        assertEquals(1, newRestrictions.size());
        assertEquals(3, transaction.getModelChanges().size());

        Restriction newRestriction = newRestrictions.get(0);
        assertHasRestrictionProperties(newRestriction, false);
        assertTrue(newRestriction.hasProperty(OWL.minCardinality));
    }

    @Test
    public void testCreateMinQualifiedCardinalityRestriction() {
        List<Restriction> newRestrictions = restrictionsModel.createRestrictions(testOnProperty,
                11, -1, testOnClass);

        assertEquals(1, newRestrictions.size());
        assertEquals(4, transaction.getModelChanges().size());

        Restriction newRestriction = newRestrictions.get(0);
        assertHasRestrictionProperties(newRestriction, true);
        assertTrue(newRestriction.hasProperty(OWL2.minQualifiedCardinality));
    }

    @Test
    public void testCreateMaxCardinalityRestriction() {
        List<Restriction> newRestrictions = restrictionsModel.createRestrictions(testOnProperty, 0,
                12, null);

        assertEquals(1, newRestrictions.size());
        assertEquals(3, transaction.getModelChanges().size());

        Restriction newRestriction = newRestrictions.get(0);
        assertHasRestrictionProperties(newRestriction, false);
        assertTrue(newRestriction.hasProperty(OWL.maxCardinality));
    }

    @Test
    public void testCreateMaxQualifiedCardinalityRestriction() {
        List<Restriction> newRestrictions = restrictionsModel.createRestrictions(testOnProperty, 0,
                12, testOnClass);

        assertEquals(1, newRestrictions.size());
        assertEquals(4, transaction.getModelChanges().size());

        Restriction newRestriction = newRestrictions.get(0);
        assertHasRestrictionProperties(newRestriction, true);
        assertTrue(newRestriction.hasProperty(OWL2.maxQualifiedCardinality));
    }

    @Test
    public void testCreateMinMaxCardinalityRestriction() {
        List<Restriction> newRestrictions = restrictionsModel.createRestrictions(testOnProperty, 4,
                8, null);

        assertEquals(2, newRestrictions.size());
        assertEquals(6, transaction.getModelChanges().size());

        Restriction newRestriction = newRestrictions.get(0);
        assertHasRestrictionProperties(newRestriction, false);
        assertTrue(newRestriction.hasProperty(OWL.minCardinality));

        newRestriction = newRestrictions.get(1);
        assertHasRestrictionProperties(newRestriction, false);
        assertTrue(newRestriction.hasProperty(OWL.maxCardinality));
    }

    @Test
    public void testCreateMinMaxQualifiedCardinalityRestriction() {
        List<Restriction> newRestrictions = restrictionsModel.createRestrictions(testOnProperty, 4,
                8, testOnClass);

        assertEquals(2, newRestrictions.size());
        assertEquals(8, transaction.getModelChanges().size());

        Restriction newRestriction = newRestrictions.get(0);
        assertHasRestrictionProperties(newRestriction, true);
        assertTrue(newRestriction.hasProperty(OWL2.minQualifiedCardinality));

        newRestriction = newRestrictions.get(1);
        assertHasRestrictionProperties(newRestriction, true);
        assertTrue(newRestriction.hasProperty(OWL2.maxQualifiedCardinality));
    }

    @Test
    public void testFunction_createRestrictions() {
        List<Restriction> newRestrictions;

        // Test precondition: onProperty != null
        boolean caught = false;
        try {
            newRestrictions = restrictionsModel.createRestrictions(null, -20, -10, testOnClass);
        }
        catch (NullPointerException e) {
            caught = true;
        }
        assertTrue(caught);
        assertEquals(0, transaction.getModelChanges().size());

        // Test negative numbers for both min and max cardinality.
        // Should create an allValuesFrom (as min is at least 0, and max is set
        // as unbounded).
        newRestrictions = restrictionsModel.createRestrictions(testOnProperty, -20, -10,
                testOnClass);
        assertEquals(1, newRestrictions.size());

        // Test 0 for min cardinality and negative for max cardinality
        // Should create an allValuesFrom.
        newRestrictions = restrictionsModel.createRestrictions(testOnProperty, 0, -10, testOnClass);
        assertEquals(1, newRestrictions.size());

        // Test min cardinality higher than max cardinality.
        // Should simply create both a min and max cardinality restriction, even
        // though logically flawed.
        newRestrictions = restrictionsModel.createRestrictions(testOnProperty, 20, 10, testOnClass);
        assertEquals(2, newRestrictions.size());
    }

    @Test
    public void testFunction_replaceRestrictionSuperclasses() {
        assertFalse(testOldRestrictions.isEmpty());
        assertFalse(testNewRestrictions.isEmpty());

        // Test precondition: resource != null
        boolean caught = false;
        try {
            restrictionsModel.replaceRestrictionSuperclasses(null, testOldRestrictions,
                    testNewRestrictions);
        }
        catch (NullPointerException e) {
            caught = true;
        }
        assertTrue(caught);
        assertEquals(0, transaction.getModelChanges().size());

        // Test without old restrictions and new restrictions
        restrictionsModel.replaceRestrictionSuperclasses(testResource, null, null);
        assertEquals(0, transaction.getModelChanges().size());

        // Test whether the function successfully adds resource to restrictions
        assertFalse(isSubClassToAll(testResource, testOldRestrictions));
        assertFalse(isSubClassToAll(testResource, testNewRestrictions));
        restrictionsModel.replaceRestrictionSuperclasses(testResource, null, testOldRestrictions);
        assertTrue(isSubClassToAll(testResource, testOldRestrictions));
        assertFalse(isSubClassToAll(testResource, testNewRestrictions));

        // Test whether the function successfully replaces super classes
        assertEquals(testOldRestrictions.size(), discardableRestrictions.size()
                + nonDiscardableRestrictions.size());
        restrictionsModel.replaceRestrictionSuperclasses(testResource, testOldRestrictions,
                testNewRestrictions);
        assertFalse(isSubClassToAll(testResource, testOldRestrictions));
        assertTrue(isSubClassToAll(testResource, testNewRestrictions));

        assertFalse(modelContains(discardableRestrictions));
        assertTrue(modelContains(nonDiscardableRestrictions));
    }

    private boolean isSubClassToAll(Resource resource, List<Restriction> restrictions) {
        for (Restriction r : restrictions) {
            if ((r != null)
                    && (!ontModel.contains(ontModel.createStatement(resource, RDFS.subClassOf, r)))) {
                return false;
            }
        }
        return true;
    }

    private boolean modelContains(List<Restriction> restrictions) {
        for (Restriction r : restrictions) {
            if (r == null) {
                return false;
            }
            if (!ontModel.containsResource(r)) {
                return false;
            }
        }
        return true;
    }

    @Test
    public void testFunction_setCardinality() {
        // TODO
    }
}
