package com.semmtech.semantics.ontology;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.semmtech.semantics.model.ExtendedModelFactory;


public class ExtendedOntResourceTest {
    private OntModel regularModel;
    private OntModel extendedModel;
    private List<String> testResourceURIs;

    @Before
    public void before() {
        regularModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_RULE_INF);

        extendedModel = ExtendedModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
        addDefaultSubModels(extendedModel);
        fakeImplicitOWLStatements(extendedModel);

        fillModel(regularModel);
        fillModel(extendedModel);
    }

    private void fakeImplicitOWLStatements(OntModel model) {
        // Currently only adding this rule to get through the JUnit test.
        // OWL also states the rule that all instances of owl:Class need to be a
        // subclass of owl:Thing.
        model.add(model.createStatement(OWL.Thing, RDFS.subClassOf, RDFS.Resource));
    }

    private void addDefaultSubModels(OntModel model) {
        Model rdfModel = ModelFactory.createDefaultModel();
        rdfModel.read(RDF.getURI());
        model.addSubModel(rdfModel);

        Model rdfsModel = ModelFactory.createDefaultModel();
        rdfsModel.read(RDFS.getURI());
        model.addSubModel(rdfsModel);

        Model owlModel = ModelFactory.createDefaultModel();
        owlModel.read(OWL.getURI());
        model.addSubModel(owlModel);
    }

    private void fillModel(OntModel model) {
        testResourceURIs = Lists.newArrayList();

        for (int i = 1; i <= 5; i++) {
            model.createResource(createTestResourceURI(), OWL.Class);
            if (i > 1) {
                model.add(model.createStatement(
                        model.getResource(testResourceURIs.get(testResourceURIs.size() - 2)),
                        RDFS.subClassOf,
                        model.getResource(testResourceURIs.get(testResourceURIs.size() - 1))));
            }
        }
        model.getResource(testResourceURIs.get(testResourceURIs.size() - 1)).addProperty(
                RDFS.subClassOf, OWL.Thing);
        model.createResource(createTestResourceURI(), model.getResource(testResourceURIs.get(0)));

        model.createResource(createTestResourceURI(), RDF.Property);
        model.createResource(createTestResourceURI(), OWL.Restriction);
    }

    private String createTestResourceURI() {
        String uri = "http://test.uri/" + (testResourceURIs.size() + 1);
        testResourceURIs.add(uri);
        return uri;
    }

    @Test
    public void test_listRDFTypes() {
        // Test for:
        // public ExtendedIterator<Resource> listRDFTypes(boolean direct);
        List<Resource> l1, l2;

        for (String uri : testResourceURIs) {
            // System.out.println("Listing types for resource " + uri);
            l1 = removeAnonResources(regularModel.getOntResource(uri).listRDFTypes(true).toList());
            l2 = removeAnonResources(extendedModel.getOntResource(uri).listRDFTypes(true).toList());
            assertEquals(l1.size(), l2.size());
            assertEquals(l2.size(), intersection(l1, l2).size());

            l1 = removeAnonResources(regularModel.getOntResource(uri).listRDFTypes(false).toList());
            l2 = removeAnonResources(extendedModel.getOntResource(uri).listRDFTypes(false).toList());
            // System.out.print("OntModelImpl     shows " + l1.size() + ": " +
            // l1);
            // System.out.print("ExtendedOntModel shows " + l2.size() + ": " +
            // l2);
            assertEquals(l1.size(), l2.size());
            assertEquals(l2.size(), intersection(l1, l2).size());
        }
    }

    private List<Resource> removeAnonResources(List<Resource> list) {
        for (int i = 0; i < list.size(); i++) {
            if (!list.get(i).isURIResource()) {
                list.remove(i);
                i--;
            }
        }
        return list;
    }

    private List<Resource> intersection(List<Resource> lhs, List<Resource> rhs) {
        List<Resource> intersection = Lists.newArrayList();
        for (Resource lhsResource : lhs) {
            for (Resource rhsResource : rhs) {
                if (rhsResource.getURI().equals(lhsResource.getURI())) {
                    intersection.add(lhsResource);
                    break;
                }
            }
        }
        return intersection;
    }

    @Test
    public void test_hasRDFType() {
        // Test for:
        // public boolean hasRDFType(Resource ontClass, boolean direct)
        Resource defaultTypes[] = new Resource[] { OWL.Class, RDFS.Class, RDF.Property,
                OWL.Restriction };

        List<String> checkTypeURIs = Lists.newArrayList();
        for (Resource r : defaultTypes) {
            checkTypeURIs.add(r.getURI());
        }
        checkTypeURIs.addAll(testResourceURIs);

        boolean b1;
        boolean b2;
        for (String uri : testResourceURIs) {
            for (String typeUri : checkTypeURIs) {
                b1 = regularModel.getOntResource(uri).hasRDFType(typeUri);
                b2 = extendedModel.getOntResource(uri).hasRDFType(typeUri);
                assertTrue(b1 == b2);
            }
        }
    }
}
