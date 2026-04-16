package com.semmtech.plugin.semmweb.core.functions;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.Restriction;
import com.hp.hpl.jena.ontology.impl.OntModelImpl;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.expr.E_FunctionDynamic;
import com.hp.hpl.jena.sparql.expr.Expr;
import com.hp.hpl.jena.sparql.expr.ExprList;
import com.hp.hpl.jena.sparql.expr.nodevalue.NodeValueNode;
import com.hp.hpl.jena.sparql.syntax.ElementFilter;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.OWL2;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.semmtech.jena.performance.sparql.DatasetProvider;
import com.semmtech.jena.performance.sparql.DatasetProviderFactory;
import com.semmtech.jena.performance.sparql.QueryBenchmark;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.model.FunctionsModelFactory;
import com.semmtech.semantics.sparql.QueryBuilder;
import com.semmtech.spin.system.SPINImports;


public class ConformsToRestrictionQueryTest {
    protected final static String NS = "http://www.semmweb.com/semmweb/owl/functions#";
    protected final static String SATISFIES_RESTRICTION_FUNCTION_URI = NS + "satisfiesRestriction";
    protected final static String SATISFIES_ALL_VALUES_FROM_RESTRICTION_FUNCTION_URI = NS
            + "satisfiesAllValuesFromRestriction";
    protected final static String SATISFIES_SOME_VALUES_FROM_RESTRICTION_FUNCTION_URI = NS
            + "satisfiesSomeValuesFromRestriction";
    protected final static String SATISFIES_HAS_VALUE_RESTRICTION_FUNCTION_URI = NS
            + "satisfiesHasValueRestriction";
    protected final static String SATISFIES_MIN_CARDINALITY_RESTRICTION_FUNCTION_URI = NS
            + "satisfiesMinCardinalityRestriction";
    protected final static String SATISFIES_MAX_CARDINALITY_RESTRICTION_FUNCTION_URI = NS
            + "satisfiesMaxCardinalityRestriction";
    protected final static String SATISFIES_CARDINALITY_RESTRICTION_FUNCTION_URI = NS
            + "satisfiesCardinalityRestriction";
    protected final static String SATISFIES_MIN_QUALIFIED_CARDINALITY_RESTRICTION_FUNCTION_URI = NS
            + "satisfiesMinQualifiedCardinalityRestriction";
    protected final static String SATISFIES_MAX_QUALIFIED_CARDINALITY_RESTRICTION_FUNCTION_URI = NS
            + "satisfiesMaxQualifiedCardinalityRestriction";
    protected final static String SATISFIES_QUALIFIED_CARDINALITY_RESTRICTION_FUNCTION_URI = NS
            + "satisfiesQualifiedCardinalityRestriction";

    public static OntModel inputModel;
    public static Resource resource;
    public static Restriction restriction;

    @BeforeClass
    public static void init() {
        TestCorePlugin.registerFunctions();
        TestCorePlugin.registerPropertyFunctions();
    }

    protected void createTestModel() {
        Model model = ModelFactory.createDefaultModel();
        model.remove(model.listStatements().toList());
        inputModel = new OntModelImpl(OntModelSpec.OWL_MEM, model);

        restriction = inputModel.createRestriction(RDFS.label);
        resource = inputModel.createResource("http://some.url/SomeResource");
    }

    // @Test
    // public void testSPLinstanceOf() {
    // // Test no specified cardinality
    // createTestModel();
    // resource.addLiteral(RDFS.label,
    // inputModel.createLiteral("Some Resource"));
    // List<Node> argList = Lists.newArrayList();
    // // argList.add(resource.asNode());
    // argList.add(inputModel.createLiteral("hello").asNode());
    // argList.add(RDFS.Resource.asNode());
    // // argList.add(OMV.city.asNode());
    // assertTrue(executeSPINFunction(NS + "test", argList, "Test"));
    // }

    @Test
    public void testAllValuesFromSatisfaction() {
        // Test no specified cardinality
        createTestModel();
        resource.addLiteral(RDFS.label, inputModel.createLiteral("Some Resource"));
        assertTrue(restrictionIsSatisfied(SATISFIES_ALL_VALUES_FROM_RESTRICTION_FUNCTION_URI));
        assertTrue(restrictionIsSatisfied(SATISFIES_RESTRICTION_FUNCTION_URI));

        // Test satisfied all values from
        createTestModel();
        restriction.convertToAllValuesFromRestriction(OWL.DeprecatedClass);
        Resource objectClass = inputModel.createResource("http://some.url/resource-without-a-type");
        resource.addProperty(RDFS.label, objectClass);
        objectClass.addProperty(RDF.type, OWL.DeprecatedClass);
        assertTrue(restrictionIsSatisfied(SATISFIES_ALL_VALUES_FROM_RESTRICTION_FUNCTION_URI));
        assertTrue(restrictionIsSatisfied(SATISFIES_RESTRICTION_FUNCTION_URI));

        // Test unsatisfied all values from
        createTestModel();
        restriction.convertToAllValuesFromRestriction(OWL.DeprecatedClass);
        objectClass = inputModel.createResource("http://some.url/resource-without-a-type");
        resource.addProperty(RDFS.label, objectClass);
        objectClass.addProperty(RDF.type, OWL.Class);
        assertFalse(restrictionIsSatisfied(SATISFIES_ALL_VALUES_FROM_RESTRICTION_FUNCTION_URI));
        assertFalse(restrictionIsSatisfied(SATISFIES_RESTRICTION_FUNCTION_URI));
    }

    @Test
    public void testSomeValuesFromSatisfaction() {
        // Test no specified cardinality
        createTestModel();
        resource.addLiteral(RDFS.label, inputModel.createLiteral("Some Resource"));
        assertTrue(restrictionIsSatisfied(SATISFIES_SOME_VALUES_FROM_RESTRICTION_FUNCTION_URI));
        assertTrue(restrictionIsSatisfied(SATISFIES_RESTRICTION_FUNCTION_URI));

        // Test satisfied some values from
        createTestModel();
        restriction.convertToSomeValuesFromRestriction(OWL.DeprecatedClass);
        Resource objectClass = inputModel.createResource("http://some.url/resource-without-a-type");
        resource.addProperty(RDFS.label, objectClass);
        objectClass.addProperty(RDF.type, OWL.DeprecatedClass);
        assertTrue(restrictionIsSatisfied(SATISFIES_SOME_VALUES_FROM_RESTRICTION_FUNCTION_URI));
        assertTrue(restrictionIsSatisfied(SATISFIES_RESTRICTION_FUNCTION_URI));

        // Test unsatisfied some values from
        createTestModel();
        restriction.convertToSomeValuesFromRestriction(OWL.DeprecatedClass);
        objectClass = inputModel.createResource("http://some.url/resource-without-a-type");
        resource.addProperty(RDFS.label, objectClass);
        objectClass.addProperty(RDF.type, OWL.Class);
        assertFalse(restrictionIsSatisfied(SATISFIES_SOME_VALUES_FROM_RESTRICTION_FUNCTION_URI));
        assertFalse(restrictionIsSatisfied(SATISFIES_RESTRICTION_FUNCTION_URI));
    }

    @Test
    public void testHasValueSatisfaction() {
        // Test no specified cardinality
        createTestModel();
        resource.addLiteral(RDFS.label, inputModel.createLiteral("Some Resource"));
        assertTrue(restrictionIsSatisfied(SATISFIES_HAS_VALUE_RESTRICTION_FUNCTION_URI));
        assertTrue(restrictionIsSatisfied(SATISFIES_RESTRICTION_FUNCTION_URI));

        // Test satisfied has value
        createTestModel();
        Literal value = inputModel.createLiteral("This is a test value");
        restriction.convertToHasValueRestriction(value);
        resource.addLiteral(RDFS.label, value);
        assertTrue(restrictionIsSatisfied(SATISFIES_HAS_VALUE_RESTRICTION_FUNCTION_URI));
        assertTrue(restrictionIsSatisfied(SATISFIES_RESTRICTION_FUNCTION_URI));

        // Test unsatisfied has value
        createTestModel();
        value = inputModel.createLiteral("This is a test value");
        restriction.convertToHasValueRestriction(value);
        value = inputModel.createLiteral("This is the actual value");
        resource.addLiteral(RDFS.label, value);
        assertFalse(restrictionIsSatisfied(SATISFIES_HAS_VALUE_RESTRICTION_FUNCTION_URI));
        assertFalse(restrictionIsSatisfied(SATISFIES_RESTRICTION_FUNCTION_URI));
    }

    @Test
    public void testMinCardinalitySatisfaction() {
        // Test no specified cardinality
        createTestModel();
        resource.addLiteral(RDFS.label, inputModel.createLiteral("Some Resource"));
        assertTrue(restrictionIsSatisfied(SATISFIES_MIN_CARDINALITY_RESTRICTION_FUNCTION_URI));
        assertTrue(restrictionIsSatisfied(SATISFIES_RESTRICTION_FUNCTION_URI));

        // Test satisfied min cardinality
        createTestModel();
        restriction.convertToMinCardinalityRestriction(1);
        resource.addLiteral(RDFS.label, inputModel.createLiteral("Some Resource"));
        assertTrue(restrictionIsSatisfied(SATISFIES_MIN_CARDINALITY_RESTRICTION_FUNCTION_URI));
        assertTrue(restrictionIsSatisfied(SATISFIES_RESTRICTION_FUNCTION_URI));

        // Test unsatisfied min cardinality
        createTestModel();
        restriction.convertToMinCardinalityRestriction(2);
        resource.addLiteral(RDFS.label, inputModel.createLiteral("Some Resource"));
        assertFalse(restrictionIsSatisfied(SATISFIES_MIN_CARDINALITY_RESTRICTION_FUNCTION_URI));
        assertFalse(restrictionIsSatisfied(SATISFIES_RESTRICTION_FUNCTION_URI));
    }

    @Test
    public void testMaxCardinalitySatisfaction() {
        // Test no specified cardinality
        createTestModel();
        resource.addLiteral(RDFS.label, inputModel.createLiteral("Some Resource"));
        assertTrue(restrictionIsSatisfied(SATISFIES_MAX_CARDINALITY_RESTRICTION_FUNCTION_URI));
        assertTrue(restrictionIsSatisfied(SATISFIES_RESTRICTION_FUNCTION_URI));

        // Test satisfied max cardinality
        createTestModel();
        restriction.convertToMaxCardinalityRestriction(1);
        resource.addLiteral(RDFS.label, inputModel.createLiteral("Some Resource"));
        assertTrue(restrictionIsSatisfied(SATISFIES_MAX_CARDINALITY_RESTRICTION_FUNCTION_URI));
        assertTrue(restrictionIsSatisfied(SATISFIES_RESTRICTION_FUNCTION_URI));

        // Test unsatisfied min cardinality
        createTestModel();
        restriction.convertToMaxCardinalityRestriction(0);
        resource.addLiteral(RDFS.label, inputModel.createLiteral("Some Resource"));
        assertFalse(restrictionIsSatisfied(SATISFIES_MAX_CARDINALITY_RESTRICTION_FUNCTION_URI));
        assertFalse(restrictionIsSatisfied(SATISFIES_RESTRICTION_FUNCTION_URI));
    }

    @Test
    public void testCardinalitySatisfaction() {
        // Test no specified cardinality
        createTestModel();
        resource.addLiteral(RDFS.label, inputModel.createLiteral("Some Resource"));
        assertTrue(restrictionIsSatisfied(SATISFIES_CARDINALITY_RESTRICTION_FUNCTION_URI));
        assertTrue(restrictionIsSatisfied(SATISFIES_RESTRICTION_FUNCTION_URI));

        // Test satisfied cardinality
        createTestModel();
        restriction.convertToCardinalityRestriction(1);
        resource.addLiteral(RDFS.label, inputModel.createLiteral("Some Resource"));
        assertTrue(restrictionIsSatisfied(SATISFIES_CARDINALITY_RESTRICTION_FUNCTION_URI));
        assertTrue(restrictionIsSatisfied(SATISFIES_RESTRICTION_FUNCTION_URI));

        // Test unsatisfied cardinality
        createTestModel();
        restriction.convertToCardinalityRestriction(0);
        resource.addLiteral(RDFS.label, inputModel.createLiteral("Some Resource"));
        assertFalse(restrictionIsSatisfied(SATISFIES_CARDINALITY_RESTRICTION_FUNCTION_URI));
        assertFalse(restrictionIsSatisfied(SATISFIES_RESTRICTION_FUNCTION_URI));

        createTestModel();
        restriction.convertToCardinalityRestriction(2);
        resource.addLiteral(RDFS.label, inputModel.createLiteral("Some Resource"));
        assertFalse(restrictionIsSatisfied(SATISFIES_CARDINALITY_RESTRICTION_FUNCTION_URI));
        assertFalse(restrictionIsSatisfied(SATISFIES_RESTRICTION_FUNCTION_URI));
    }

    @Test
    public void testMinQualifiedCardinalitySatisfaction() {
        // Test no specified cardinality
        createTestModel();
        resource.addLiteral(RDFS.label, inputModel.createLiteral("Some Resource"));
        assertTrue(restrictionIsSatisfied(SATISFIES_MIN_QUALIFIED_CARDINALITY_RESTRICTION_FUNCTION_URI));
        assertTrue(restrictionIsSatisfied(SATISFIES_RESTRICTION_FUNCTION_URI));

        // Test satisfied min qualified cardinality
        createTestModel();
        restriction.addProperty(OWL2.minQualifiedCardinality, "1",
                XSDDatatype.XSDnonNegativeInteger);
        restriction.addProperty(OWL2.onClass, RDFS.Literal);
        resource.addLiteral(RDFS.label, inputModel.createLiteral("Some Resource"));
        assertTrue(restrictionIsSatisfied(SATISFIES_MIN_QUALIFIED_CARDINALITY_RESTRICTION_FUNCTION_URI));
        assertTrue(restrictionIsSatisfied(SATISFIES_RESTRICTION_FUNCTION_URI));

        // Test unsatisfied min qualified cardinality
        createTestModel();
        restriction.addProperty(OWL2.minQualifiedCardinality, "2",
                XSDDatatype.XSDnonNegativeInteger);
        restriction.addProperty(OWL2.onClass, RDFS.Literal);
        resource.addLiteral(RDFS.label, inputModel.createLiteral("Some Resource"));
        assertFalse(restrictionIsSatisfied(SATISFIES_MIN_QUALIFIED_CARDINALITY_RESTRICTION_FUNCTION_URI));
        assertFalse(restrictionIsSatisfied(SATISFIES_RESTRICTION_FUNCTION_URI));
    }

    @Test
    public void testMaxQualifiedCardinalitySatisfaction() {
        // Test no specified cardinality
        createTestModel();
        resource.addLiteral(RDFS.label, inputModel.createLiteral("Some Resource"));
        assertTrue(restrictionIsSatisfied(SATISFIES_MAX_QUALIFIED_CARDINALITY_RESTRICTION_FUNCTION_URI));
        assertTrue(restrictionIsSatisfied(SATISFIES_RESTRICTION_FUNCTION_URI));

        // Test satisfied min qualified cardinality
        createTestModel();
        restriction.addProperty(OWL2.maxQualifiedCardinality, "1",
                XSDDatatype.XSDnonNegativeInteger);
        restriction.addProperty(OWL2.onClass, RDFS.Literal);
        resource.addLiteral(RDFS.label, inputModel.createLiteral("Some Resource"));
        assertTrue(restrictionIsSatisfied(SATISFIES_MAX_QUALIFIED_CARDINALITY_RESTRICTION_FUNCTION_URI));
        assertTrue(restrictionIsSatisfied(SATISFIES_RESTRICTION_FUNCTION_URI));

        // Test unsatisfied min qualified cardinality
        createTestModel();
        restriction.addProperty(OWL2.maxQualifiedCardinality, "0",
                XSDDatatype.XSDnonNegativeInteger);
        restriction.addProperty(OWL2.onClass, RDFS.Literal);
        resource.addLiteral(RDFS.label, inputModel.createLiteral("Some Resource"));
        assertFalse(restrictionIsSatisfied(SATISFIES_MAX_QUALIFIED_CARDINALITY_RESTRICTION_FUNCTION_URI));
        assertFalse(restrictionIsSatisfied(SATISFIES_RESTRICTION_FUNCTION_URI));
    }

    @Test
    public void testQualifiedCardinalitySatisfaction() {
        // Test no specified cardinality
        createTestModel();
        resource.addLiteral(RDFS.label, inputModel.createLiteral("Some Resource"));
        assertTrue(restrictionIsSatisfied(SATISFIES_QUALIFIED_CARDINALITY_RESTRICTION_FUNCTION_URI));
        assertTrue(restrictionIsSatisfied(SATISFIES_RESTRICTION_FUNCTION_URI));

        // Test satisfied min qualified cardinality
        createTestModel();
        restriction.addProperty(OWL2.qualifiedCardinality, "1", XSDDatatype.XSDnonNegativeInteger);
        restriction.addProperty(OWL2.onClass, RDFS.Literal);
        resource.addLiteral(RDFS.label, inputModel.createLiteral("Some Resource"));
        assertTrue(restrictionIsSatisfied(SATISFIES_QUALIFIED_CARDINALITY_RESTRICTION_FUNCTION_URI));
        assertTrue(restrictionIsSatisfied(SATISFIES_RESTRICTION_FUNCTION_URI));

        // Test unsatisfied min qualified cardinality
        createTestModel();
        restriction.addProperty(OWL2.qualifiedCardinality, "0", XSDDatatype.XSDnonNegativeInteger);
        restriction.addProperty(OWL2.onClass, RDFS.Literal);
        resource.addLiteral(RDFS.label, inputModel.createLiteral("Some Resource"));
        assertFalse(restrictionIsSatisfied(SATISFIES_QUALIFIED_CARDINALITY_RESTRICTION_FUNCTION_URI));
        assertFalse(restrictionIsSatisfied(SATISFIES_RESTRICTION_FUNCTION_URI));

        createTestModel();
        restriction.addProperty(OWL2.qualifiedCardinality, "2", XSDDatatype.XSDnonNegativeInteger);
        restriction.addProperty(OWL2.onClass, RDFS.Literal);
        resource.addLiteral(RDFS.label, inputModel.createLiteral("Some Resource"));
        assertFalse(restrictionIsSatisfied(SATISFIES_QUALIFIED_CARDINALITY_RESTRICTION_FUNCTION_URI));
        assertFalse(restrictionIsSatisfied(SATISFIES_RESTRICTION_FUNCTION_URI));
    }

    protected Object runBenchmark(QueryBenchmark bench) {
        // System.out.println(bench.getName() + "\n");
        Object queryResult = bench.excec();
        // System.out.println(bench.formatResult() + "\n\n");
        return queryResult;
    }

    protected boolean restrictionIsSatisfied(String functionURI) {
        List<Node> argList = Lists.newArrayList();
        argList.add(resource.asNode());
        argList.add(restriction.asNode());

        return executeSPINFunction(functionURI, argList, "Conforms to Restriction");
    }

    protected boolean executeSPINFunction(String functionURI, List<? extends Node> argList,
            String benchMarkName) {
        OntModel functionsModel = FunctionsModelFactory.create();
        OntModel spinModel = SPINImports.createSPINModel(functionsModel);
        Model unionModel = ModelFactory.createUnion(inputModel, spinModel);

        QueryBuilder qb = QueryBuilder.createAsk();
        List<Expr> argsList = Lists.newArrayList();
        if (argList != null) {
            for (Node argNode : argList) {
                argsList.add(new NodeValueNode(argNode));
            }
        }
        Expr e = new E_FunctionDynamic(new NodeValueNode(NodeFactory.createURI(functionURI)),
                new ExprList(argsList));
        qb.addFilterPattern(new ElementFilter(e));

        DatasetProvider datasetProvider = DatasetProviderFactory.create(unionModel, "model");
        QueryBenchmark benchmark = new QueryBenchmark(benchMarkName, qb.getQuery());
        benchmark.setInput(datasetProvider);

        Object queryResult = runBenchmark(benchmark);

        assertTrue(queryResult instanceof Boolean);
        return ((Boolean) queryResult).booleanValue();
    }

    protected static class TestCorePlugin extends CorePlugin {
        protected static void registerFunctions() {
            CorePlugin.registerFunctions();
        }

        protected static void registerPropertyFunctions() {
            CorePlugin.registerPropertyFunctions();
        }
    }
}