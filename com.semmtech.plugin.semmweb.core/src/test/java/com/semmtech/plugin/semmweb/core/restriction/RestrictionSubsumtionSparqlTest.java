package com.semmtech.plugin.semmweb.core.restriction;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.base.Preconditions;
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntDocumentManager;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.Restriction;
import com.hp.hpl.jena.query.ARQ;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ModelMaker;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.mgt.Explain;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.OWL2;
import com.semmtech.plugin.semmweb.core.widgets.RestrictionSubsumptionSPARQL;


public class RestrictionSubsumtionSparqlTest {

    private static final String URI = "http://www.example.org/subsumption/";
    private static final String PREFIX = "sub";

    private OntModel model;

    private OntClass superClass;
    private OntClass clazz;
    private OntClass clazz2;
    private OntClass subClass;

    private OntProperty doSomething;
    private OntProperty doSomethingOther;

    private RestrictionSubsumptionSPARQL subCheck;

    @BeforeClass
    public static final void init() {
        ARQ.setExecutionLogging(Explain.InfoLevel.NONE);
    }

    @Before
    public final void initModel() {
        // I hoped that in this way the QCR was supported but it isn't
        // ProfileRegistry.getInstance().registerProfile(SEMM.getURI(), new
        // SEMMProfile());
        OntDocumentManager ontDocumentManager = new OntDocumentManager();
        ontDocumentManager.setProcessImports(false);
        ModelMaker modelMaker = ModelFactory.createMemModelMaker();
        OntModelSpec spec = new OntModelSpec(modelMaker, ontDocumentManager, null, OWL2.getURI());

        model = ModelFactory.createOntologyModel(spec);
        model.setNsPrefix(PREFIX, URI);

        superClass = model.createClass(URI + "SuperClass");
        clazz = model.createClass(URI + "Class");
        clazz2 = model.createClass(URI + "Class2");
        subClass = model.createClass(URI + "SubClass");

        superClass.addSubClass(clazz);
        superClass.addSubClass(clazz2);
        clazz.addSubClass(subClass);

        doSomething = model.createOntProperty(URI + "doSomethig");
        doSomethingOther = model.createOntProperty(URI + "doSomethigOther");

        subCheck = new RestrictionSubsumptionSPARQL(model);
    }

    @After
    public final void dispose() {
        model.close();
    }

    /**
     * S.onClass >= L.onClass
     */
    @Test
    public void allValuesFrom_allValuesFrom() {
        Restriction allValuesFromSuper = model.createAllValuesFromRestriction(URI
                + "allValuesFromSuper", doSomething, clazz);

        Restriction allValuesFromLocal1 = model.createAllValuesFromRestriction(URI
                + "allValuesFromHidden1", doSomething, subClass);
        Restriction allValuesFromLocal2 = model.createAllValuesFromRestriction(URI
                + "allValuesFromHidden2", doSomething, clazz);
        Restriction allValuesFromLocal3 = model.createAllValuesFromRestriction(URI
                + "allValuesFromHidden3", doSomething, clazz2);

        assertTrue(subCheck.isSubsumed(allValuesFromSuper, allValuesFromLocal1));
        assertTrue(subCheck.isSubsumed(allValuesFromSuper, allValuesFromLocal2));
        assertFalse(subCheck.isSubsumed(allValuesFromLocal1, allValuesFromSuper));
        assertFalse(subCheck.isSubsumed(allValuesFromSuper, allValuesFromLocal3));
    }

    /**
     * L.# = 0
     */
    @Test
    public void allValuesFrom_cardinality() {
        Restriction allValuesFromSuper = model.createAllValuesFromRestriction(URI
                + "allValuesFromSuper", doSomething, clazz);

        Restriction cardinalityHidden = model.createCardinalityRestriction(URI
                + "cardinalityHidden", doSomething, 0);
        Restriction cardinalityVisible = model.createCardinalityRestriction(URI
                + "cardinalityVisible", doSomething, 13);

        assertTrue(subCheck.isSubsumed(allValuesFromSuper, cardinalityHidden));
        assertFalse(subCheck.isSubsumed(allValuesFromSuper, cardinalityVisible));

        cardinalityHidden.removeAll(OWL.cardinality);
        cardinalityHidden.addProperty(OWL.cardinality, OWL.Class);
        assertFalse(subCheck.isSubsumed(allValuesFromSuper, cardinalityHidden));
    }

    /**
     * L.# = 0
     */
    @Test
    public void allValuesFrom_maxCardinality() {
        Restriction allValuesFromSuper = model.createAllValuesFromRestriction(URI
                + "allValuesFromSuper", doSomething, clazz);

        Restriction maxCardinalityLocal1 = model.createMaxCardinalityRestriction(URI
                + "maxCardinalityLocal1", doSomething, 0);
        Restriction maxCardinalityLocal2 = model.createMaxCardinalityRestriction(URI
                + "maxCardinalityLocal2", doSomething, 13);

        assertTrue(subCheck.isSubsumed(allValuesFromSuper, maxCardinalityLocal1));
        assertFalse(subCheck.isSubsumed(allValuesFromSuper, maxCardinalityLocal2));

        maxCardinalityLocal1.removeAll(OWL.maxCardinality);
        maxCardinalityLocal1.addProperty(OWL.maxCardinality, OWL.Class);
        assertFalse(subCheck.isSubsumed(allValuesFromSuper, maxCardinalityLocal1));
    }

    /**
     * L.# = 0 & S.onClass <= L.onClass
     */
    @Test
    public void allValuesFrom_qualifiedCardinality() {
        Restriction allValuesFromSuper = model.createAllValuesFromRestriction(URI
                + "allValuesFromSuper", doSomething, clazz);

        Restriction qualifiedCardinalityLocal1 = createQCR(URI + "qualifiedCardinalityLocal1",
                doSomething, clazz, 0, OWL2.qualifiedCardinality);
        Restriction qualifiedCardinalityLocal2 = createQCR(URI + "qualifiedCardinalityLocal2",
                doSomething, superClass, 0, OWL2.qualifiedCardinality);
        Restriction qualifiedCardinalityLocal3 = createQCR(URI + "qualifiedCardinalityLocal3",
                doSomething, subClass, 0, OWL2.qualifiedCardinality);
        Restriction qualifiedCardinalityLocal4 = createQCR(URI + "qualifiedCardinalityLocal4",
                doSomething, clazz, 3, OWL2.qualifiedCardinality);
        Restriction qualifiedCardinalityLocal5 = createQCR(URI + "qualifiedCardinalityLocal5",
                doSomething, clazz2, 0, OWL2.qualifiedCardinality);

        assertTrue(subCheck.isSubsumed(allValuesFromSuper, qualifiedCardinalityLocal1));
        assertTrue(subCheck.isSubsumed(allValuesFromSuper, qualifiedCardinalityLocal2));
        assertFalse(subCheck.isSubsumed(allValuesFromSuper, qualifiedCardinalityLocal4));
        assertFalse(subCheck.isSubsumed(allValuesFromSuper, qualifiedCardinalityLocal3));
        assertFalse(subCheck.isSubsumed(allValuesFromSuper, qualifiedCardinalityLocal5));
    }

    /**
     * L.# = 0 & S.onClass <= L.onClass
     */
    @Test
    public void allValuesFrom_maxQualifiedCardinality() {
        Restriction allValuesFromSuper = model.createAllValuesFromRestriction(URI
                + "allValuesFromSuper", doSomething, clazz);

        Restriction maxQualifiedCardinalityLocal1 = createQCR(
                URI + "maxQualifiedCardinalityLocal1", doSomething, clazz, 0,
                OWL2.qualifiedCardinality);
        Restriction maxQualifiedCardinalityLocal2 = createQCR(
                URI + "maxQualifiedCardinalityLocal2", doSomething, superClass, 0,
                OWL2.qualifiedCardinality);
        Restriction maxQualifiedCardinalityLocal3 = createQCR(
                URI + "maxQualifiedCardinalityLocal3", doSomething, subClass, 0,
                OWL2.qualifiedCardinality);
        Restriction maxQualifiedCardinalityLocal4 = createQCR(
                URI + "maxQualifiedCardinalityLocal4", doSomething, clazz, 3,
                OWL2.qualifiedCardinality);
        Restriction maxQualifiedCardinalityLocal5 = createQCR(
                URI + "maxQualifiedCardinalityLocal5", doSomething, clazz2, 0,
                OWL2.qualifiedCardinality);

        assertTrue(subCheck.isSubsumed(allValuesFromSuper, maxQualifiedCardinalityLocal1));
        assertTrue(subCheck.isSubsumed(allValuesFromSuper, maxQualifiedCardinalityLocal2));
        assertFalse(subCheck.isSubsumed(allValuesFromSuper, maxQualifiedCardinalityLocal3));
        assertFalse(subCheck.isSubsumed(allValuesFromSuper, maxQualifiedCardinalityLocal4));
        assertFalse(subCheck.isSubsumed(allValuesFromSuper, maxQualifiedCardinalityLocal5));
    }

    /**
     * S.onClass >= L.onClass
     */
    @Test
    public void someValuesFrom_someValuesFrom() {
        Restriction someValuesFromSuper = model.createSomeValuesFromRestriction(URI
                + "someValuesFromSuper", doSomething, clazz);

        Restriction someValuesFromLocal1 = model.createSomeValuesFromRestriction(URI
                + "someValuesFromLocal1", doSomething, subClass);
        Restriction someValuesFromLocal2 = model.createSomeValuesFromRestriction(URI
                + "someValuesFromLocal2", doSomething, clazz);
        Restriction someValuesFromLocal3 = model.createSomeValuesFromRestriction(URI
                + "someValuesFromLocal3", doSomething, clazz2);

        assertTrue(subCheck.isSubsumed(someValuesFromSuper, someValuesFromLocal1));
        assertTrue(subCheck.isSubsumed(someValuesFromSuper, someValuesFromLocal2));
        assertFalse(subCheck.isSubsumed(someValuesFromLocal1, someValuesFromSuper));
        assertFalse(subCheck.isSubsumed(someValuesFromSuper, someValuesFromLocal3));
    }

    /**
     * L.inst -> S.onClass
     */
    @Test
    public void someValuesFrom_hasValue() {
        Restriction someValuesFromSuper = model.createSomeValuesFromRestriction(URI
                + "someValuesFromSuper", doSomething, clazz);

        Resource instance1 = clazz.createIndividual(URI + "classIndividual");
        Resource instance2 = subClass.createIndividual(URI + "subClassIndividual");
        Resource instance3 = superClass.createIndividual(URI + "superClassIndividual");

        Restriction hasValue1 = model.createHasValueRestriction(URI + "hasValue1", doSomething,
                instance1);
        Restriction hasValue2 = model.createHasValueRestriction(URI + "hasValue2", doSomething,
                instance2);
        Restriction hasValue3 = model.createHasValueRestriction(URI + "hasValue3", doSomething,
                instance3);

        assertTrue(subCheck.isSubsumed(someValuesFromSuper, hasValue1));
        assertTrue(subCheck.isSubsumed(someValuesFromSuper, hasValue2));
        assertFalse(subCheck.isSubsumed(someValuesFromSuper, hasValue3));
    }

    /**
     * L.# != 0 & S.onClass >= L.onClass
     */
    @Test
    public void someValuesFrom_qualifiedCardinality() {
        Restriction someValuesFromSuper = model.createSomeValuesFromRestriction(URI
                + "someValuesFromSuper", doSomething, clazz);

        Restriction qualifiedCardinalityLocal1 = createQCR(URI + "qualifiedCardinalityLocal1",
                doSomething, clazz, 12, OWL2.qualifiedCardinality);
        Restriction qualifiedCardinalityLocal2 = createQCR(URI + "qualifiedCardinalityLocal2",
                doSomething, subClass, 45, OWL2.qualifiedCardinality);
        Restriction qualifiedCardinalityLocal3 = createQCR(URI + "qualifiedCardinalityLocal3",
                doSomething, superClass, 4, OWL2.qualifiedCardinality);
        Restriction qualifiedCardinalityLocal4 = createQCR(URI + "qualifiedCardinalityLocal4",
                doSomething, clazz, 0, OWL2.qualifiedCardinality);
        Restriction qualifiedCardinalityLocal5 = createQCR(URI + "qualifiedCardinalityLocal5",
                doSomething, clazz2, 12, OWL2.qualifiedCardinality);

        assertTrue(subCheck.isSubsumed(someValuesFromSuper, qualifiedCardinalityLocal1));
        assertTrue(subCheck.isSubsumed(someValuesFromSuper, qualifiedCardinalityLocal2));
        assertFalse(subCheck.isSubsumed(someValuesFromSuper, qualifiedCardinalityLocal3));
        assertFalse(subCheck.isSubsumed(someValuesFromSuper, qualifiedCardinalityLocal4));
        assertFalse(subCheck.isSubsumed(someValuesFromSuper, qualifiedCardinalityLocal5));
    }

    /**
     * L.# != 0 & S.onClass >= L.onClass
     */
    @Test
    public void someValuesFrom_minQualifiedCardinality() {
        Restriction someValuesFromSuper = model.createSomeValuesFromRestriction(URI
                + "someValuesFromSuper", doSomething, clazz);

        Restriction minQualifiedCardinalityLocal1 = createQCR(
                URI + "minQualifiedCardinalityLocal1", doSomething, clazz, 12,
                OWL2.minQualifiedCardinality);
        Restriction minQualifiedCardinalityLocal2 = createQCR(
                URI + "minQualifiedCardinalityLocal2", doSomething, subClass, 45,
                OWL2.minQualifiedCardinality);
        Restriction minQualifiedCardinalityLocal3 = createQCR(
                URI + "minQualifiedCardinalityLocal3", doSomething, superClass, 4,
                OWL2.minQualifiedCardinality);
        Restriction minQualifiedCardinalityLocal4 = createQCR(
                URI + "minQualifiedCardinalityLocal4", doSomething, clazz, 0,
                OWL2.minQualifiedCardinality);
        Restriction minQualifiedCardinalityLocal5 = createQCR(
                URI + "minQualifiedCardinalityLocal5", doSomething, clazz2, 12,
                OWL2.minQualifiedCardinality);

        assertTrue(subCheck.isSubsumed(someValuesFromSuper, minQualifiedCardinalityLocal1));
        assertTrue(subCheck.isSubsumed(someValuesFromSuper, minQualifiedCardinalityLocal2));
        assertFalse(subCheck.isSubsumed(someValuesFromSuper, minQualifiedCardinalityLocal3));
        assertFalse(subCheck.isSubsumed(someValuesFromSuper, minQualifiedCardinalityLocal4));
        assertFalse(subCheck.isSubsumed(someValuesFromSuper, minQualifiedCardinalityLocal5));
    }

    /**
     * S.inst = L.inst
     */
    @Test
    public void hasValue_hasValue() {
        Resource instance1 = clazz.createIndividual(URI + "classIndividual");
        Resource instance2 = subClass.createIndividual(URI + "subClassIndividual");
        Resource instance3 = superClass.createIndividual(URI + "superClassIndividual");

        Restriction hasValueSuper = model.createHasValueRestriction(URI + "hasValueSuper",
                doSomething, instance1);

        Restriction hasValue1 = model.createHasValueRestriction(URI + "hasValue1", doSomething,
                instance1);
        Restriction hasValue2 = model.createHasValueRestriction(URI + "hasValue2", doSomething,
                instance2);
        Restriction hasValue3 = model.createHasValueRestriction(URI + "hasValue3", doSomething,
                instance3);

        assertTrue(subCheck.isSubsumed(hasValueSuper, hasValue1));
        assertFalse(subCheck.isSubsumed(hasValueSuper, hasValue2));
        assertFalse(subCheck.isSubsumed(hasValueSuper, hasValue3));
    }

    /**
     * S.# = L.#
     */
    @Test
    public void cardinality_cardinality() {
        Restriction cardinalitySuper = model.createCardinalityRestriction(URI + "cardinalitySuper",
                doSomething, 0);

        Restriction cardinalityLocalS = model.createCardinalityRestriction(URI
                + "cardinalityLocalS", doSomething, 0);
        Restriction cardinalityLocal = model.createCardinalityRestriction(URI + "cardinalityLocal",
                doSomething, 13);

        assertTrue(subCheck.isSubsumed(cardinalitySuper, cardinalityLocalS));
        assertFalse(subCheck.isSubsumed(cardinalitySuper, cardinalityLocal));

        cardinalityLocalS.removeAll(OWL.cardinality);
        cardinalityLocalS.addProperty(OWL.cardinality, OWL.Class);
        assertFalse(subCheck.isSubsumed(cardinalitySuper, cardinalityLocalS));
    }

    /**
     * S.# = 1
     */
    @Test
    public void minCardinality_hasValue() {
        Restriction minCardinalitySuper = model.createMinCardinalityRestriction(URI
                + "minCardinalitySuper", doSomething, 0);
        Restriction minCardinalitySuper1 = model.createMinCardinalityRestriction(URI
                + "minCardinalitySuper1", doSomething, 1);
        Restriction minCardinalitySuper2 = model.createMinCardinalityRestriction(URI
                + "minCardinalitySuper2", doSomething, 2);

        Resource instance1 = clazz.createIndividual(URI + "classIndividual");
        Restriction hasValue = model.createHasValueRestriction(URI + "hasValue", doSomething,
                instance1);

        assertFalse(subCheck.isSubsumed(minCardinalitySuper, hasValue));
        assertTrue(subCheck.isSubsumed(minCardinalitySuper1, hasValue));
        assertFalse(subCheck.isSubsumed(minCardinalitySuper2, hasValue));
    }

    /**
     * S.# <= L.#
     */
    @Test
    public void minCardinality_cardinality() {
        Restriction minCardinalitySuper = model.createMinCardinalityRestriction(URI
                + "minCardinalitySuper", doSomething, 1);

        Restriction cardinalityLocal1 = model.createCardinalityRestriction(URI
                + "cardinalityLocal1", doSomething, 1);
        Restriction cardinalityLocal2 = model.createCardinalityRestriction(URI
                + "cardinalityLocal2", doSomething, 13);
        Restriction cardinalityLocal3 = model.createCardinalityRestriction(URI
                + "cardinalityLocal3", doSomething, 0);

        assertTrue(subCheck.isSubsumed(minCardinalitySuper, cardinalityLocal1));
        assertTrue(subCheck.isSubsumed(minCardinalitySuper, cardinalityLocal2));
        assertFalse(subCheck.isSubsumed(minCardinalitySuper, cardinalityLocal3));
    }

    /**
     * S.# <= L.#
     */
    @Test
    public void minCardinality_minCardinality() {
        Restriction minCardinalitySuper = model.createMinCardinalityRestriction(URI
                + "minCardinalitySuper", doSomething, 1);

        Restriction minCardinalityLocal1 = model.createCardinalityRestriction(URI
                + "minCardinalityLocal1", doSomething, 1);
        Restriction minCardinalityLocal2 = model.createCardinalityRestriction(URI
                + "minCardinalityLocal2", doSomething, 13);
        Restriction minCardinalityLocal3 = model.createCardinalityRestriction(URI
                + "minCardinalityLocal3", doSomething, 0);

        assertTrue(subCheck.isSubsumed(minCardinalitySuper, minCardinalityLocal1));
        assertTrue(subCheck.isSubsumed(minCardinalitySuper, minCardinalityLocal2));
        assertFalse(subCheck.isSubsumed(minCardinalitySuper, minCardinalityLocal3));
    }

    /**
     * S.# <= L.#
     */
    @Test
    public void minCardinality_qualifiedCardinality() {
        Restriction minCardinalitySuper = model.createMinCardinalityRestriction(URI
                + "minCardinalitySuper", doSomething, 1);

        Restriction qualifiedCardinalityLocal1 = createQCR(URI + "qualifiedCardinalityLocal1",
                doSomething, clazz, 1, OWL2.qualifiedCardinality);
        Restriction qualifiedCardinalityLocal2 = createQCR(URI + "qualifiedCardinalityLocal2",
                doSomething, subClass, 12, OWL2.qualifiedCardinality);
        Restriction qualifiedCardinalityLocal3 = createQCR(URI + "qualifiedCardinalityLocal3",
                doSomething, superClass, 0, OWL2.qualifiedCardinality);

        assertTrue(subCheck.isSubsumed(minCardinalitySuper, qualifiedCardinalityLocal1));
        assertTrue(subCheck.isSubsumed(minCardinalitySuper, qualifiedCardinalityLocal2));
        assertFalse(subCheck.isSubsumed(minCardinalitySuper, qualifiedCardinalityLocal3));
    }

    /**
     * S.# <= L.#
     */
    @Test
    public void minCardinality_minQualifiedCardinality() {
        Restriction minCardinalitySuper = model.createMinCardinalityRestriction(URI
                + "minCardinalitySuper", doSomething, 1);

        Restriction minQualifiedCardinalityLocal1 = createQCR(
                URI + "minQualifiedCardinalityLocal1", doSomething, clazz, 1,
                OWL2.minQualifiedCardinality);
        Restriction minQualifiedCardinalityLocal2 = createQCR(
                URI + "minQualifiedCardinalityLocal2", doSomething, subClass, 45,
                OWL2.minQualifiedCardinality);
        Restriction minQualifiedCardinalityLocal3 = createQCR(
                URI + "minQualifiedCardinalityLocal3", doSomething, superClass, 0,
                OWL2.minQualifiedCardinality);

        assertTrue(subCheck.isSubsumed(minCardinalitySuper, minQualifiedCardinalityLocal1));
        assertTrue(subCheck.isSubsumed(minCardinalitySuper, minQualifiedCardinalityLocal2));
        assertFalse(subCheck.isSubsumed(minCardinalitySuper, minQualifiedCardinalityLocal3));
    }

    /**
     * S.# >= L.#
     */
    @Test
    public void maxCardinality_cardinality() {
        Restriction maxCardinalitySuper = model.createMaxCardinalityRestriction(URI
                + "maxCardinalitySuper", doSomething, 1);

        Restriction cardinalityLocal1 = model.createCardinalityRestriction(URI
                + "cardinalityLocal1", doSomething, 0);
        Restriction cardinalityLocal2 = model.createCardinalityRestriction(URI
                + "cardinalityLocal2", doSomething, 1);
        Restriction cardinalityLocal3 = model.createCardinalityRestriction(URI
                + "cardinalityLocal3", doSomething, 12);

        assertTrue(subCheck.isSubsumed(maxCardinalitySuper, cardinalityLocal1));
        assertTrue(subCheck.isSubsumed(maxCardinalitySuper, cardinalityLocal2));
        assertFalse(subCheck.isSubsumed(maxCardinalitySuper, cardinalityLocal3));
    }

    /**
     * S.# >= L.#
     */
    @Test
    public void maxCardinality_maxCardinality() {
        Restriction maxCardinalitySuper = model.createMaxCardinalityRestriction(URI
                + "maxCardinalitySuper", doSomething, 1);

        Restriction maxCardinalityLocal1 = model.createMaxCardinalityRestriction(URI
                + "maxCardinalityLocal1", doSomething, 0);
        Restriction maxCardinalityLocal2 = model.createMaxCardinalityRestriction(URI
                + "maxCardinalityLocal2", doSomething, 1);
        Restriction maxCardinalityLocal3 = model.createMaxCardinalityRestriction(URI
                + "maxCardinalityLocal3", doSomething, 13);

        assertTrue(subCheck.isSubsumed(maxCardinalitySuper, maxCardinalityLocal1));
        assertTrue(subCheck.isSubsumed(maxCardinalitySuper, maxCardinalityLocal2));
        assertFalse(subCheck.isSubsumed(maxCardinalitySuper, maxCardinalityLocal3));
    }

    /**
     * S.# = L.# & S.onClass >= L.onClass
     */
    @Test
    public void qualifiedCardinality_qualifiedCardinality() {
        Restriction qualifiedCardinalitySuper = createQCR(URI + "qualifiedCardinalitySuper",
                doSomething, clazz, 1, OWL2.qualifiedCardinality);

        Restriction qualifiedCardinalityLocal1 = createQCR(URI + "qualifiedCardinalityLocal1",
                doSomething, clazz, 1, OWL2.qualifiedCardinality);
        Restriction qualifiedCardinalityLocal2 = createQCR(URI + "qualifiedCardinalityLocal2",
                doSomething, subClass, 1, OWL2.qualifiedCardinality);
        Restriction qualifiedCardinalityLocal3 = createQCR(URI + "qualifiedCardinalityLocal3",
                doSomething, clazz, 0, OWL2.qualifiedCardinality);
        Restriction qualifiedCardinalityLocal4 = createQCR(URI + "qualifiedCardinalityLocal4",
                doSomething, clazz, 2, OWL2.qualifiedCardinality);
        Restriction qualifiedCardinalityLocal5 = createQCR(URI + "qualifiedCardinalityLocal5",
                doSomething, superClass, 1, OWL2.qualifiedCardinality);
        Restriction qualifiedCardinalityLocal6 = createQCR(URI + "qualifiedCardinalityLocal6",
                doSomething, clazz, 2, OWL2.qualifiedCardinality);
        Restriction qualifiedCardinalityLocal7 = createQCR(URI + "qualifiedCardinalityLocal7",
                doSomething, clazz2, 1, OWL2.qualifiedCardinality);

        assertTrue(subCheck.isSubsumed(qualifiedCardinalitySuper, qualifiedCardinalityLocal1));
        assertTrue(subCheck.isSubsumed(qualifiedCardinalitySuper, qualifiedCardinalityLocal2));
        assertFalse(subCheck.isSubsumed(qualifiedCardinalitySuper, qualifiedCardinalityLocal3));
        assertFalse(subCheck.isSubsumed(qualifiedCardinalitySuper, qualifiedCardinalityLocal4));
        assertFalse(subCheck.isSubsumed(qualifiedCardinalitySuper, qualifiedCardinalityLocal5));
        assertFalse(subCheck.isSubsumed(qualifiedCardinalitySuper, qualifiedCardinalityLocal6));
        assertFalse(subCheck.isSubsumed(qualifiedCardinalitySuper, qualifiedCardinalityLocal7));
    }

    /**
     * S.# = 1 & S.onClass >= L.onClass
     */
    @Test
    public void minQualifiedCardinality_someValuesFrom() {
        Restriction minQualifiedCardinalitySuper1 = createQCR(
                URI + "minQualifiedCardinalitySuper1", doSomething, clazz, 1,
                OWL2.minQualifiedCardinality);
        Restriction minQualifiedCardinalitySuper2 = createQCR(
                URI + "minQualifiedCardinalitySuper2", doSomething, clazz, 2,
                OWL2.minQualifiedCardinality);

        Restriction someValuesFromLocal1 = model.createSomeValuesFromRestriction(URI
                + "someValuesFromLocal1", doSomething, clazz);
        Restriction someValuesFromLocal2 = model.createSomeValuesFromRestriction(URI
                + "someValuesFromLocal2", doSomething, subClass);
        Restriction someValuesFromLocal3 = model.createSomeValuesFromRestriction(URI
                + "someValuesFromLocal3", doSomething, superClass);
        Restriction someValuesFromLocal4 = model.createSomeValuesFromRestriction(URI
                + "someValuesFromLocal4", doSomething, clazz2);

        assertTrue(subCheck.isSubsumed(minQualifiedCardinalitySuper1, someValuesFromLocal1));
        assertTrue(subCheck.isSubsumed(minQualifiedCardinalitySuper1, someValuesFromLocal2));
        assertFalse(subCheck.isSubsumed(minQualifiedCardinalitySuper2, someValuesFromLocal1));
        assertFalse(subCheck.isSubsumed(minQualifiedCardinalitySuper1, someValuesFromLocal3));
        assertFalse(subCheck.isSubsumed(minQualifiedCardinalitySuper1, someValuesFromLocal4));
    }

    /**
     * S.# = 1 & L.inst -> S.onClass
     */
    @Test
    public void minQualifiedCardinality_hasValue() {
        Restriction minQualifiedCardinalitySuper1 = createQCR(
                URI + "minQualifiedCardinalitySuper1", doSomething, clazz, 1,
                OWL2.minQualifiedCardinality);
        Restriction minQualifiedCardinalitySuper2 = createQCR(
                URI + "minQualifiedCardinalitySuper2", doSomething, superClass, 1,
                OWL2.minQualifiedCardinality);
        Restriction minQualifiedCardinalitySuper3 = createQCR(
                URI + "minQualifiedCardinalitySuper3", doSomething, clazz, 2,
                OWL2.minQualifiedCardinality);

        Resource instance1 = clazz.createIndividual(URI + "classIndividual");
        Resource instance3 = superClass.createIndividual(URI + "superClassIndividual");

        Restriction hasValue1 = model.createHasValueRestriction(URI + "hasValue1", doSomething,
                instance1);
        Restriction hasValue3 = model.createHasValueRestriction(URI + "hasValue3", doSomething,
                instance3);

        assertTrue(subCheck.isSubsumed(minQualifiedCardinalitySuper1, hasValue1));
        assertTrue(subCheck.isSubsumed(minQualifiedCardinalitySuper2, hasValue1));
        assertFalse(subCheck.isSubsumed(minQualifiedCardinalitySuper3, hasValue1));
        assertFalse(subCheck.isSubsumed(minQualifiedCardinalitySuper1, hasValue3));
    }

    /**
     * S.# <= L.# & S.onClass >= L.onClass
     */
    @Test
    public void minQualifiedCardinality_qualifiedCardinality() {
        Restriction minQualifiedCardinalitySuper1 = createQCR(
                URI + "minQualifiedCardinalitySuper1", doSomething, clazz, 1,
                OWL2.minQualifiedCardinality);

        Restriction qualifiedCardinalityLocal1 = createQCR(URI + "qualifiedCardinalityLocal1",
                doSomething, clazz, 1, OWL2.qualifiedCardinality);
        Restriction qualifiedCardinalityLocal2 = createQCR(URI + "qualifiedCardinalityLocal2",
                doSomething, clazz, 2, OWL2.qualifiedCardinality);
        Restriction qualifiedCardinalityLocal3 = createQCR(URI + "qualifiedCardinalityLocal3",
                doSomething, subClass, 1, OWL2.qualifiedCardinality);
        Restriction qualifiedCardinalityLocal4 = createQCR(URI + "qualifiedCardinalityLocal4",
                doSomething, clazz, 0, OWL2.qualifiedCardinality);
        Restriction qualifiedCardinalityLocal5 = createQCR(URI + "qualifiedCardinalityLocal5",
                doSomething, superClass, 1, OWL2.qualifiedCardinality);
        Restriction qualifiedCardinalityLocal6 = createQCR(URI + "qualifiedCardinalityLocal6",
                doSomething, superClass, 0, OWL2.qualifiedCardinality);
        Restriction qualifiedCardinalityLocal7 = createQCR(URI + "qualifiedCardinalityLocal7",
                doSomething, clazz2, 1, OWL2.qualifiedCardinality);

        assertTrue(subCheck.isSubsumed(minQualifiedCardinalitySuper1, qualifiedCardinalityLocal1));
        assertTrue(subCheck.isSubsumed(minQualifiedCardinalitySuper1, qualifiedCardinalityLocal2));
        assertTrue(subCheck.isSubsumed(minQualifiedCardinalitySuper1, qualifiedCardinalityLocal3));
        assertFalse(subCheck.isSubsumed(minQualifiedCardinalitySuper1, qualifiedCardinalityLocal4));
        assertFalse(subCheck.isSubsumed(minQualifiedCardinalitySuper1, qualifiedCardinalityLocal5));
        assertFalse(subCheck.isSubsumed(minQualifiedCardinalitySuper1, qualifiedCardinalityLocal6));
        assertFalse(subCheck.isSubsumed(minQualifiedCardinalitySuper1, qualifiedCardinalityLocal7));
    }

    /**
     * S.# <= L.# & S.onClass >= L.onClass
     */
    @Test
    public void minQualifiedCardinality_minQualifiedCardinality() {
        Restriction minQualifiedCardinalitySuper1 = createQCR(
                URI + "minQualifiedCardinalitySuper1", doSomething, clazz, 1,
                OWL2.minQualifiedCardinality);

        Restriction minQualifiedCardinalityLocal1 = createQCR(
                URI + "minQualifiedCardinalityLocal1", doSomething, clazz, 1,
                OWL2.minQualifiedCardinality);
        Restriction minQualifiedCardinalityLocal2 = createQCR(
                URI + "minQualifiedCardinalityLocal2", doSomething, clazz, 2,
                OWL2.minQualifiedCardinality);
        Restriction minQualifiedCardinalityLocal3 = createQCR(
                URI + "minQualifiedCardinalityLocal3", doSomething, subClass, 1,
                OWL2.minQualifiedCardinality);
        Restriction minQualifiedCardinalityLocal4 = createQCR(
                URI + "minQualifiedCardinalityLocal4", doSomething, clazz, 0,
                OWL2.minQualifiedCardinality);
        Restriction minQualifiedCardinalityLocal5 = createQCR(
                URI + "minQualifiedCardinalityLocal5", doSomething, superClass, 1,
                OWL2.minQualifiedCardinality);
        Restriction minQualifiedCardinalityLocal6 = createQCR(
                URI + "minQualifiedCardinalityLocal6", doSomething, superClass, 0,
                OWL2.minQualifiedCardinality);
        Restriction minQualifiedCardinalityLocal7 = createQCR(
                URI + "minQualifiedCardinalityLocal7", doSomething, clazz2, 1,
                OWL2.minQualifiedCardinality);

        assertTrue(subCheck
                .isSubsumed(minQualifiedCardinalitySuper1, minQualifiedCardinalityLocal1));
        assertTrue(subCheck
                .isSubsumed(minQualifiedCardinalitySuper1, minQualifiedCardinalityLocal2));
        assertTrue(subCheck
                .isSubsumed(minQualifiedCardinalitySuper1, minQualifiedCardinalityLocal3));
        assertFalse(subCheck.isSubsumed(minQualifiedCardinalitySuper1,
                minQualifiedCardinalityLocal4));
        assertFalse(subCheck.isSubsumed(minQualifiedCardinalitySuper1,
                minQualifiedCardinalityLocal5));
        assertFalse(subCheck.isSubsumed(minQualifiedCardinalitySuper1,
                minQualifiedCardinalityLocal6));
        assertFalse(subCheck.isSubsumed(minQualifiedCardinalitySuper1,
                minQualifiedCardinalityLocal7));

    }

    /**
     * S.# >= L.# & S.onClass >= L.onClass
     */
    @Test
    public void maxQualifiedCardinality_qualifiedCardinality() {
        Restriction maxQualifiedCardinalitySuper1 = createQCR(
                URI + "maxQualifiedCardinalitySuper1", doSomething, clazz, 1,
                OWL2.maxQualifiedCardinality);

        Restriction qualifiedCardinalityLocal1 = createQCR(URI + "qualifiedCardinalityLocal1",
                doSomething, clazz, 1, OWL2.qualifiedCardinality);
        Restriction qualifiedCardinalityLocal2 = createQCR(URI + "qualifiedCardinalityLocal2",
                doSomething, clazz, 0, OWL2.qualifiedCardinality);
        Restriction qualifiedCardinalityLocal3 = createQCR(URI + "qualifiedCardinalityLocal3",
                doSomething, subClass, 1, OWL2.qualifiedCardinality);
        Restriction qualifiedCardinalityLocal4 = createQCR(URI + "qualifiedCardinalityLocal4",
                doSomething, clazz, 2, OWL2.qualifiedCardinality);
        Restriction qualifiedCardinalityLocal5 = createQCR(URI + "qualifiedCardinalityLocal5",
                doSomething, superClass, 1, OWL2.qualifiedCardinality);
        Restriction qualifiedCardinalityLocal6 = createQCR(URI + "qualifiedCardinalityLocal6",
                doSomething, superClass, 2, OWL2.qualifiedCardinality);
        Restriction qualifiedCardinalityLocal7 = createQCR(URI + "qualifiedCardinalityLocal7",
                doSomething, clazz2, 1, OWL2.qualifiedCardinality);

        assertTrue(subCheck.isSubsumed(maxQualifiedCardinalitySuper1, qualifiedCardinalityLocal1));
        assertTrue(subCheck.isSubsumed(maxQualifiedCardinalitySuper1, qualifiedCardinalityLocal2));
        assertTrue(subCheck.isSubsumed(maxQualifiedCardinalitySuper1, qualifiedCardinalityLocal3));
        assertFalse(subCheck.isSubsumed(maxQualifiedCardinalitySuper1, qualifiedCardinalityLocal4));
        assertFalse(subCheck.isSubsumed(maxQualifiedCardinalitySuper1, qualifiedCardinalityLocal5));
        assertFalse(subCheck.isSubsumed(maxQualifiedCardinalitySuper1, qualifiedCardinalityLocal6));
        assertFalse(subCheck.isSubsumed(maxQualifiedCardinalitySuper1, qualifiedCardinalityLocal7));
    }

    /**
     * S.# >= L.# & S.onClass >= L.onClass
     */
    @Test
    public void maxQualifiedCardinality_maxQualifiedCardinality() {
        Restriction maxQualifiedCardinalitySuper1 = createQCR(
                URI + "maxQualifiedCardinalitySuper1", doSomething, clazz, 1,
                OWL2.maxQualifiedCardinality);

        Restriction maxQualifiedCardinalityLocal1 = createQCR(
                URI + "maxQualifiedCardinalityLocal1", doSomething, clazz, 1,
                OWL2.maxQualifiedCardinality);
        Restriction maxQualifiedCardinalityLocal2 = createQCR(
                URI + "maxQualifiedCardinalityLocal2", doSomething, clazz, 0,
                OWL2.maxQualifiedCardinality);
        Restriction maxQualifiedCardinalityLocal3 = createQCR(
                URI + "maxQualifiedCardinalityLocal3", doSomething, subClass, 1,
                OWL2.maxQualifiedCardinality);
        Restriction maxQualifiedCardinalityLocal4 = createQCR(
                URI + "maxQualifiedCardinalityLocal4", doSomething, clazz, 2,
                OWL2.maxQualifiedCardinality);
        Restriction maxQualifiedCardinalityLocal5 = createQCR(
                URI + "maxQualifiedCardinalityLocal5", doSomething, superClass, 1,
                OWL2.maxQualifiedCardinality);
        Restriction maxQualifiedCardinalityLocal6 = createQCR(
                URI + "maxQualifiedCardinalityLocal6", doSomething, superClass, 2,
                OWL2.maxQualifiedCardinality);
        Restriction maxQualifiedCardinalityLocal7 = createQCR(
                URI + "maxQualifiedCardinalityLocal7", doSomething, clazz2, 1,
                OWL2.maxQualifiedCardinality);

        assertTrue(subCheck
                .isSubsumed(maxQualifiedCardinalitySuper1, maxQualifiedCardinalityLocal1));
        assertTrue(subCheck
                .isSubsumed(maxQualifiedCardinalitySuper1, maxQualifiedCardinalityLocal2));
        assertTrue(subCheck
                .isSubsumed(maxQualifiedCardinalitySuper1, maxQualifiedCardinalityLocal3));
        assertFalse(subCheck.isSubsumed(maxQualifiedCardinalitySuper1,
                maxQualifiedCardinalityLocal4));
        assertFalse(subCheck.isSubsumed(maxQualifiedCardinalitySuper1,
                maxQualifiedCardinalityLocal5));
        assertFalse(subCheck.isSubsumed(maxQualifiedCardinalitySuper1,
                maxQualifiedCardinalityLocal6));
        assertFalse(subCheck.isSubsumed(maxQualifiedCardinalitySuper1,
                maxQualifiedCardinalityLocal7));
    }

    /**
     * S.# >= L.# & S.onClass >= L.onClass
     */
    @Test
    public void cheobs() {
        OntProperty hasAspect = model.createOntProperty(URI + "hasAspect");
        OntClass afstand = model.createClass(URI + "Afstand");
        OntClass hoogte = model.createClass(URI + "Hoogte");
        OntClass lengte = model.createClass(URI + "Lengte");
        OntClass constructiehoogteVanEenBrug = model.createClass(URI
                + "ConstructiehoogteVanEenBrug");
        OntClass overspanning = model.createClass(URI + "Overspanning");

        afstand.addSubClass(hoogte);
        afstand.addSubClass(lengte);

        hoogte.addSubClass(constructiehoogteVanEenBrug);
        lengte.addSubClass(overspanning);

        // Test maxQualifiedCardinality, maxQualifiedCardinality
        Restriction superRestriction = createQCR(URI + "superRes", hasAspect, overspanning, 1,
                OWL2.maxQualifiedCardinality);

        Restriction localRestriction = createQCR(URI + "localRes", hasAspect,
                constructiehoogteVanEenBrug, 1, OWL2.maxQualifiedCardinality);

        assertFalse(subCheck.isSubsumed(superRestriction, localRestriction));

        // Test qualifiedCardinality maxQualoifiedCardinality
        Restriction localRestriction2 = createQCR(URI + "localRes2", hasAspect,
                constructiehoogteVanEenBrug, 1, OWL2.qualifiedCardinality);

        assertFalse(subCheck.isSubsumed(superRestriction, localRestriction2));

    }

    @Test
    public void localSchizofrenicRestrictions() {
        Restriction allValuesFromSuper = model.createAllValuesFromRestriction(
                URI + "allValuesFrom", doSomething, clazz);

        Restriction schizoLocal = model.createCardinalityRestriction(URI + "schizoHidden",
                doSomething, 0);
        schizoLocal.addLiteral(OWL.maxCardinality, 0);

        assertTrue(subCheck.isSubsumed(allValuesFromSuper, schizoLocal));

        schizoLocal.removeAll(OWL.maxCardinality);
        schizoLocal.addLiteral(OWL.maxCardinality, new Integer(13));

        assertTrue(subCheck.isSubsumed(allValuesFromSuper, schizoLocal));

    }

    @Test
    public void superSchizofrenicRestrictions() {
        Restriction cardinalitySchizo = model.createCardinalityRestriction(URI
                + "cardinalitySchizo", doSomething, 1);
        cardinalitySchizo.addLiteral(OWL.minCardinality, new Integer(1));

        Restriction cardinalityLocalS = model.createCardinalityRestriction(URI
                + "cardinalityLocalS", doSomething, 1);
        Restriction cardinalityLocal = model.createCardinalityRestriction(URI + "cardinalityLocal",
                doSomething, 10);

        assertTrue(subCheck.isSubsumed(cardinalitySchizo, cardinalityLocalS));
        assertFalse(subCheck.isSubsumed(cardinalitySchizo, cardinalityLocal));

        cardinalitySchizo.removeAll(OWL.minCardinality);
        cardinalitySchizo.addLiteral(OWL.minCardinality, new Integer(13));

        assertFalse(subCheck.isSubsumed(cardinalitySchizo, cardinalityLocalS));

    }

    @Test
    public void differentProperties() {
        Restriction cardinalitySuper = model.createCardinalityRestriction(URI + "cardinalitySuper",
                doSomething, 0);

        Restriction cardinalityLocalS = model.createCardinalityRestriction(URI
                + "cardinalityLocalS", doSomethingOther, 0);
        Restriction cardinalityLocal = model.createCardinalityRestriction(URI + "cardinalityLocal",
                doSomethingOther, 13);

        assertFalse(subCheck.isSubsumed(cardinalitySuper, cardinalityLocalS));
        assertFalse(subCheck.isSubsumed(cardinalitySuper, cardinalityLocal));
    }

    private final Restriction createQCR(String uri, Property onProperty, Resource onClass,
            int cardinalityNumber, Property qualifiedCardinalityProperty) {
        Preconditions.checkNotNull(onProperty);
        Preconditions.checkNotNull(onClass);
        Preconditions.checkNotNull(qualifiedCardinalityProperty);

        Restriction newRestriction = model.createRestriction(uri, onProperty);

        newRestriction.addProperty(OWL2.onClass, onClass);
        newRestriction.addProperty(qualifiedCardinalityProperty,
                Integer.toString(cardinalityNumber), XSDDatatype.XSDnonNegativeInteger);

        return newRestriction;
    }
}
