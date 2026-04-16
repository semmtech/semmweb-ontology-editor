package com.semmtech.jena;


import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.hp.hpl.jena.enhanced.EnhGraph;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.ontology.ConversionException;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.impl.OntClassImpl;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.syntax.ElementGroup;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;


public class ResourceTypeConversionTest {
    private OntModel ontModel;

    @Before
    public void before() {
        // OntModel with basic reasoning disabled
        ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
    }

    @After
    public void after() {
        ontModel.close();
    }

    @Test
    public void test_Resource2OntClass() {
        ontModel.setStrictMode(true);
        test_Resource2OntClass(true);

        ontModel.setStrictMode(false);
        test_Resource2OntClass(false);
    }

    private void test_Resource2OntClass(boolean strictMode) {
        Resource myResourcesDefinition = ontModel.createResource();

        Resource classResource = ontModel.createResource("http://some.uri/Resource");
        classResource.addProperty(RDF.type, OWL.Class);
        classResource.addProperty(RDFS.isDefinedBy, myResourcesDefinition);

        OntClass ontclassResourceViaMethod1 = classResource.as(OntClass.class);
        OntClass ontclassResourceViaMethod2 = new OntClassImpl(classResource.asNode(),
                (EnhGraph) ontModel);

        ontclassResourceViaMethod1.listSuperClasses();
        ontclassResourceViaMethod2.listSuperClasses();

        Resource subclassResource = ontModel.createResource("http://some.uri/SubclassResource");
        subclassResource.addProperty(RDFS.subClassOf, classResource);
        subclassResource.addProperty(RDFS.isDefinedBy, myResourcesDefinition);

        OntClass ontsubclassResourceViaMethod1;
        OntClass ontsubclassResourceViaMethod2;
        try {
            ontsubclassResourceViaMethod1 = subclassResource.as(OntClass.class);
            assertTrue(!strictMode);
            ontsubclassResourceViaMethod1.listSuperClasses();
        }
        catch (ConversionException e) {
            assertTrue(strictMode);
        }
        ontsubclassResourceViaMethod2 = new OntClassImpl(subclassResource.asNode(),
                (EnhGraph) ontModel);
        ontsubclassResourceViaMethod2.listSuperClasses();

        Property property = ontModel.createProperty("http://some.uri/PropertyResource");
        property.addProperty(RDFS.isDefinedBy, myResourcesDefinition);

        Resource individual = ontModel.createResource("http://some.uri/Individual", classResource);
        individual.addProperty(RDFS.isDefinedBy, myResourcesDefinition);

        Resource flawedIndividual = ontModel.createResource("http://some.uri/FlawedIndividual",
                classResource);
        flawedIndividual.addProperty(RDFS.isDefinedBy, myResourcesDefinition);
        flawedIndividual.addProperty(RDFS.subClassOf, classResource);

        Resource flawedClassResource = ontModel.createResource(
                "http://some.uri/FlawedClassResource", flawedIndividual);
        flawedClassResource.addProperty(RDFS.isDefinedBy, myResourcesDefinition);
        flawedClassResource.addProperty(RDFS.subClassOf, classResource);

        List<Resource> queriedResources = queryMyDistinctSubjects(ontModel, myResourcesDefinition);
        for (Resource queriedResource : queriedResources) {
            System.out.println("Trying to convert queried resource " + queriedResource.getURI());

            OntClass queriedOntclassResourceViaMethod1;
            OntClass queriedOntclassResourceViaMethod2;
            try {
                queriedOntclassResourceViaMethod1 = queriedResource.as(OntClass.class);
                assertTrue(queriedResource.hasProperty(RDF.type, OWL.Class) || !strictMode);
                queriedOntclassResourceViaMethod1.listSuperClasses();
            }
            catch (ConversionException e) {
                assertTrue(strictMode);
            }
            queriedOntclassResourceViaMethod2 = new OntClassImpl(queriedResource.asNode(),
                    (EnhGraph) ontModel);
            queriedOntclassResourceViaMethod2.listSuperClasses();

            ExtendedIterator<OntClass> iter = queriedOntclassResourceViaMethod2.listSuperClasses();
            for (; iter.hasNext();) {
                OntClass superClass = iter.next();
                superClass.listSuperClasses();
                System.out.println(".. superClass: " + superClass.getURI());
            }
        }
    }

    private static List<Resource> queryMyDistinctSubjects(OntModel model,
            Resource myResourcesDefinition) {
        // Var varS = Var.alloc("subject");
        // Var varP = Var.alloc("predicate");
        // Var varO = Var.alloc("object");
        // Triple t = new Triple(varS.asNode(), varP.asNode(), varO.asNode());

        Var varS = Var.alloc("subject");
        Triple t = new Triple(varS.asNode(), RDFS.isDefinedBy.asNode(),
                myResourcesDefinition.asNode());
        ElementGroup elg = new ElementGroup();
        elg.addTriplePattern(t);

        Query q = QueryFactory.create();
        q.setQueryPattern(elg);
        q.setQuerySelectType();
        q.addResultVar(varS);
        q.setDistinct(true);
        QueryExecution execution = QueryExecutionFactory.create(q, model);

        List<Resource> result = Lists.newArrayList();
        for (ResultSet iter = execution.execSelect(); iter.hasNext();) {
            QuerySolution querySolution = iter.next();
            Resource resource = querySolution.getResource(varS.getName());
            result.add(resource);
        }
        return result;
    }
}
