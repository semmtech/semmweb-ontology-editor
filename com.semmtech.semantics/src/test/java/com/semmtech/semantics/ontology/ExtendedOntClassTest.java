package com.semmtech.semantics.ontology;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.semmtech.semantics.model.ExtendedModelFactory;


public class ExtendedOntClassTest {
    private OntModel regularModel;
    private OntModel extendedModel;
    private List<String> testResourceURIs;
    private List<String> testIndividualURIs;

    @Before
    public void before() {
        regularModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_RULE_INF);
        addDefaultSubModels(regularModel);

        extendedModel = ExtendedModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
        addDefaultSubModels(extendedModel);
        fakeImplicitOWLStatements(extendedModel);

        fillModel(regularModel);
        fillModel(extendedModel);
    }

    private void fakeImplicitOWLStatements(OntModel model) {
        // Currently only adding this rule to get through the JUnit test.
        // OWL also states the rule that all instances of owl:Class need to be a
        // subclass of owl:Thing, but these are taken care of in the fillModel
        // function.
        model.add(model.createStatement(OWL.Thing, RDFS.subClassOf, RDFS.Resource));
        model.add(model.createStatement(RDFS.Class, RDFS.subClassOf, RDFS.Resource));
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
        testIndividualURIs = Lists.newArrayList();

        for (int i = 1; i <= 5; i++) {
            model.createResource(createTestResourceURI(), OWL.Class);
            if (i > 1) {
                model.add(model.createStatement(
                        model.getResource(testResourceURIs.get(testResourceURIs.size() - 2)),
                        RDFS.subClassOf,
                        model.getResource(testResourceURIs.get(testResourceURIs.size() - 1))));
            }
        }
        model.add(model.createStatement(
                model.getResource(testResourceURIs.get(testResourceURIs.size() - 1)),
                RDFS.subClassOf, OWL.Thing));
        model.add(model.createStatement(model.getResource(testResourceURIs.get(0)),
                RDFS.subClassOf,
                model.getResource(testResourceURIs.get(testResourceURIs.size() - 2))));

        model.createResource(createTestResourceURI(), OWL.Restriction);
        model.add(model.createStatement(
                model.getResource(testResourceURIs.get(testResourceURIs.size() - 1)),
                RDFS.subClassOf, OWL.Thing));

        model.createResource(createTestResourceURI(), RDFS.Class);
        model.add(model.createStatement(
                model.getResource(testResourceURIs.get(testResourceURIs.size() - 1)),
                RDFS.subClassOf, RDFS.Class));
        model.createResource("http://not.an.OntClass/1", RDF.Property);

        // create individuals

        for (String typeUri : testResourceURIs) {
            model.createResource(createTestIndividualURI(), model.getResource(typeUri));
        }
    }

    private String createTestResourceURI() {
        String uri = "http://test.uri/" + (testResourceURIs.size() + 1);
        testResourceURIs.add(uri);
        return uri;
    }

    private String createTestIndividualURI() {
        String uri = "http://test.uri/individual/" + (testIndividualURIs.size() + 1);
        testIndividualURIs.add(uri);
        return uri;
    }

    @Test
    public void test_listSuperClasses() {
        // Test for:
        // public ExtendedIterator<OntClass> listSuperClasses(boolean direct)
        List<? extends OntResource> l1, l2;

        for (String uri : testResourceURIs) {
            l1 = removeAnonResources(regularModel.getOntResource(uri).as(OntClass.class)
                    .listSuperClasses(false).toList());
            l2 = removeAnonResources(extendedModel.getOntResource(uri).as(OntClass.class)
                    .listSuperClasses(false).toList());
            // System.out.println("regularModel  gets indirect supers for " +
            // uri + ": " + l1);
            // System.out.println("extendedModel gets indirect supers for " +
            // uri + ": " + l2);
            assertEquals(l1.size(), l2.size());
            assertEquals(l2.size(), intersection(l1, l2).size());

            l1 = removeAnonResources(regularModel.getOntResource(uri).as(OntClass.class)
                    .listSuperClasses(true).toList());
            l2 = removeAnonResources(extendedModel.getOntResource(uri).as(OntClass.class)
                    .listSuperClasses(true).toList());
            // System.out.println("regularModel  gets direct supers for " + uri
            // + ": " + l1);
            // System.out.println("extendedModel gets direct supers for " + uri
            // + ": " + l2);
            assertEquals(l1.size(), l2.size());
            assertEquals(l2.size(), intersection(l1, l2).size());
        }
    }

    @Test
    public void test_listSubClasses() {
        // Test for:
        // public ExtendedIterator<OntClass> listSubClasses(boolean direct)
        List<? extends OntResource> l1, l2;

        for (String uri : testResourceURIs) {
            l1 = removeAnonResources(regularModel.getOntResource(uri).as(OntClass.class)
                    .listSubClasses(false).toList());
            l2 = removeAnonResources(extendedModel.getOntResource(uri).as(OntClass.class)
                    .listSubClasses(false).toList());
            // System.out.println("regularModel  gets indirect subs for " + uri
            // + ": " + l1);
            // System.out.println("extendedModel gets indirect subs for " + uri
            // + ": " + l2);
            assertEquals(l1.size(), l2.size());
            assertEquals(l2.size(), intersection(l1, l2).size());

            l1 = removeAnonResources(regularModel.getOntResource(uri).as(OntClass.class)
                    .listSubClasses(true).toList());
            l2 = removeAnonResources(extendedModel.getOntResource(uri).as(OntClass.class)
                    .listSubClasses(true).toList());
            // System.out.println("regularModel  gets direct subs for " + uri +
            // ": " + l1);
            // System.out.println("extendedModel gets direct subs for " + uri +
            // ": " + l2);
            assertEquals(l1.size(), l2.size());
            assertEquals(l2.size(), intersection(l1, l2).size());
        }
    }

    private List<? extends OntResource> removeAnonResources(List<? extends OntResource> list) {
        for (int i = 0; i < list.size(); i++) {
            if (!list.get(i).isURIResource()) {
                list.remove(i);
                i--;
            }
        }
        return list;
    }

    private List<OntResource> intersection(List<? extends OntResource> lhs,
            List<? extends OntResource> rhs) {
        List<OntResource> intersection = Lists.newArrayList();
        for (OntResource lhsResource : lhs) {
            for (OntResource rhsResource : rhs) {
                if (rhsResource.getURI().equals(lhsResource.getURI())) {
                    intersection.add(lhsResource);
                    break;
                }
            }
        }
        return intersection;
    }

    @Test
    public void test_hasSuperClass() {
        // Test for:
        // public boolean hasSuperClass(Resource cls, boolean direct)
        Resource defaultTypes[] = new Resource[] { OWL.Class, RDFS.Class };

        List<String> checkTypeURIs = Lists.newArrayList();
        for (Resource r : defaultTypes) {
            checkTypeURIs.add(r.getURI());
        }
        checkTypeURIs.addAll(testResourceURIs);

        boolean b1;
        boolean b2;
        for (String uri : testResourceURIs) {
            for (String typeUri : checkTypeURIs) {
                OntClass typeRegularModel = regularModel.getOntResource(typeUri).as(OntClass.class);
                OntClass typeExtendedModel = regularModel.getOntResource(typeUri)
                        .as(OntClass.class);

                b1 = regularModel.getOntResource(uri).as(OntClass.class)
                        .hasSuperClass(typeRegularModel, true);
                b2 = extendedModel.getOntResource(uri).as(OntClass.class)
                        .hasSuperClass(typeExtendedModel, true);
                if (b1 != b2) {
                    // System.out.println("regularModel  considers " + uri
                    // + " to have direct super class " + typeUri + ": " + b1);
                    // System.out.println("extendedModel considers " + uri
                    // + " to have direct super class " + typeUri + ": " + b2);
                }
                assertTrue(b1 == b2);

                b1 = regularModel.getOntResource(uri).as(OntClass.class)
                        .hasSuperClass(typeRegularModel, false);
                b2 = extendedModel.getOntResource(uri).as(OntClass.class)
                        .hasSuperClass(typeExtendedModel, false);
                if (b1 != b2) {
                    // System.out.println("regularModel  considers " + uri
                    // + " to have indirect super class " + typeUri + ": " +
                    // b1);
                    // System.out.println("extendedModel considers " + uri
                    // + " to have indirect super class " + typeUri + ": " +
                    // b2);
                }
                assertTrue(b1 == b2);
            }
        }
    }

    @Test
    public void test_hasSubClass() {
        // Test for:
        // public boolean hasSubClass(Resource cls, boolean direct)
        Resource defaultTypes[] = new Resource[] { OWL.Class, RDFS.Class };

        List<String> checkTypeURIs = Lists.newArrayList();
        for (Resource r : defaultTypes) {
            checkTypeURIs.add(r.getURI());
        }
        checkTypeURIs.addAll(testResourceURIs);

        boolean b1;
        boolean b2;
        for (String uri : testResourceURIs) {
            for (String typeUri : checkTypeURIs) {
                OntClass typeRegularModel = regularModel.getOntResource(typeUri).as(OntClass.class);
                OntClass typeExtendedModel = regularModel.getOntResource(typeUri)
                        .as(OntClass.class);

                b1 = regularModel.getOntResource(uri).as(OntClass.class)
                        .hasSubClass(typeRegularModel, true);
                b2 = extendedModel.getOntResource(uri).as(OntClass.class)
                        .hasSubClass(typeExtendedModel, true);
                if (b1 != b2) {
                    // System.out.println("regularModel  considers " + uri
                    // + " to have direct sub property " + typeUri + ": " + b1);
                    // System.out.println("extendedModel considers " + uri
                    // + " to have direct sub property " + typeUri + ": " + b2);
                }
                assertTrue(b1 == b2);

                b1 = regularModel.getOntResource(uri).as(OntClass.class)
                        .hasSubClass(typeRegularModel, false);
                b2 = extendedModel.getOntResource(uri).as(OntClass.class)
                        .hasSubClass(typeExtendedModel, false);
                if (b1 != b2) {
                    // System.out.println("regularModel  considers " + uri
                    // + " to have indirect sub property " + typeUri + ": " +
                    // b1);
                    // System.out.println("extendedModel considers " + uri
                    // + " to have indirect sub property " + typeUri + ": " +
                    // b2);
                }
                assertTrue(b1 == b2);
            }
        }
    }

    @Test
    public void test_listInstances() {
        // Test for:
        // public ExtendedIterator<Individual> listInstances(final boolean
        // direct)
        List<? extends OntResource> l1, l2;

        for (String uri : testResourceURIs) {
            l1 = removeAnonResources(regularModel.getOntResource(uri).as(OntClass.class)
                    .listInstances(false).toList());
            l2 = removeAnonResources(extendedModel.getOntResource(uri).as(OntClass.class)
                    .listInstances(false).toList());
            // System.out.println("regularModel  gets instances for " + uri +
            // ": " + l1);
            // System.out.println("extendedModel gets instances for " + uri +
            // ": " + l2);
            assertEquals(l1.size(), l2.size());
            assertEquals(l2.size(), intersection(l1, l2).size());

            l1 = removeAnonResources(regularModel.getOntResource(uri).as(OntClass.class)
                    .listInstances(true).toList());
            l2 = removeAnonResources(extendedModel.getOntResource(uri).as(OntClass.class)
                    .listInstances(true).toList());
            // System.out.println("regularModel  gets instances for " + uri +
            // ": " + l1);
            // System.out.println("extendedModel gets instances for " + uri +
            // ": " + l2);
            assertEquals(l1.size(), l2.size());
            assertEquals(l2.size(), intersection(l1, l2).size());
        }
    }

}
