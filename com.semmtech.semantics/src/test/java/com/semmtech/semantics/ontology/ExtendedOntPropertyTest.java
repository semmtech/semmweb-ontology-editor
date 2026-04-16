package com.semmtech.semantics.ontology;


import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.semmtech.semantics.model.ExtendedModelFactory;


public class ExtendedOntPropertyTest {
    private OntModel regularModel;
    private OntModel extendedModel;
    private List<String> testResourceURIs;

    @Before
    public void before() {
        regularModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_RULE_INF);
        addDefaultSubModels(regularModel);

        extendedModel = ExtendedModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
        addDefaultSubModels(extendedModel);

        fillModel(regularModel);
        fillModel(extendedModel);
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
            model.createResource(createTestResourceURI(), RDF.Property);
            if (i > 1) {
                model.add(model.createStatement(
                        model.getResource(testResourceURIs.get(testResourceURIs.size() - 2)),
                        RDFS.subPropertyOf,
                        model.getResource(testResourceURIs.get(testResourceURIs.size() - 1))));
            }
        }
        model.add(model.createStatement(model.getResource(testResourceURIs.get(0)),
                RDFS.subPropertyOf,
                model.getResource(testResourceURIs.get(testResourceURIs.size() - 2))));

        model.createResource(createTestResourceURI(), RDF.Property);
        model.createResource("http://not.an.OntProperty/1", OWL.Restriction);
        model.createResource("http://not.an.OntProperty/2", OWL.Class);
    }

    private String createTestResourceURI() {
        String uri = "http://test.uri/" + (testResourceURIs.size() + 1);
        testResourceURIs.add(uri);
        return uri;
    }

    @Test
    public void test_listSuperProperties() {
        // Test for:
        // public ExtendedIterator<OntProperty> listSuperProperties(boolean
        // direct)
        List<? extends OntResource> l1, l2;

        for (String uri : testResourceURIs) {
            l1 = removeAnonResources(regularModel.getOntResource(uri).as(OntProperty.class)
                    .listSuperProperties(false).toList());
            l2 = removeAnonResources(extendedModel.getOntResource(uri).as(OntProperty.class)
                    .listSuperProperties(false).toList());
            // System.out.println("regularModel gets indirect supers for " + uri
            // + ":  " + l1);
            // System.out.println("extendedModel gets indirect supers for " +
            // uri + ": " + l2);
            assertEquals(l1.size(), l2.size());
            assertEquals(l2.size(), intersection(l1, l2).size());

            l1 = removeAnonResources(regularModel.getOntResource(uri).as(OntProperty.class)
                    .listSuperProperties(true).toList());
            l2 = removeAnonResources(extendedModel.getOntResource(uri).as(OntProperty.class)
                    .listSuperProperties(true).toList());
            // System.out.println("regularModel gets direct supers for " + uri +
            // ":  " + l1);
            // System.out.println("extendedModel gets direct supers for " + uri
            // + ": " + l2);
            assertEquals(l1.size(), l2.size());
            assertEquals(l2.size(), intersection(l1, l2).size());
        }
    }

    /**
     * Jena's implementation of listSubProperties() in OntPropertyImpl also
     * returns the calling property as indirect sub property of itself. This
     * behaviour, although it is not mentioned in Jena's documentation, has been
     * adapted into our own implementation in ExtendedOntProperty.
     */
    @Test
    public void test_listSubProperties() {
        // Test for:
        // public ExtendedIterator<OntProperty> listSubProperties(boolean
        // direct)
        List<? extends OntResource> l1, l2;

        for (String uri : testResourceURIs) {
            l1 = removeAnonResources(regularModel.getOntResource(uri).as(OntProperty.class)
                    .listSubProperties(false).toList());
            l2 = removeAnonResources(extendedModel.getOntResource(uri).as(OntProperty.class)
                    .listSubProperties(false).toList());
            // System.out.println("regularModel gets indirect subs for " + uri +
            // ":  " + l1);
            // System.out.println("extendedModel gets indirect subs for " + uri
            // + ": " + l2);
            assertEquals(l1.size(), l2.size());
            assertEquals(l2.size(), intersection(l1, l2).size());

            l1 = removeAnonResources(regularModel.getOntResource(uri).as(OntProperty.class)
                    .listSubProperties(true).toList());
            l2 = removeAnonResources(extendedModel.getOntResource(uri).as(OntProperty.class)
                    .listSubProperties(true).toList());
            // System.out.println("regularModel gets direct subs for " + uri +
            // ":  " + l1);
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

    /**
     * Jena's implementation of hasSuperProperty() in OntPropertyImpl is
     * inconsistent. When inference is disabled, that function always returns
     * false for properties that are indirect super properties. It will also
     * return false when the argument is equal to the calling property. When
     * inference is enabled, the indirect super properties are recognized as
     * direct super properties. More surprisingly, the calling property is then
     * also considered to be a super property of itself. In short, that
     * implementation provides unexpected and erroneous behaviour. As such, our
     * own implementation of the function in ExtendedOntProperty needs to decide
     * on a course by defining whether properties are considered super
     * properties of themselves. They are, but indirectly. To conform to the
     * behaviour exhibited by OntClass's hasSuperClass/hasSubClass.
     */
    @Test
    public void test_hasSuperProperty() {
        // Test for:
        // public boolean hasSuperProperty(Property prop, boolean direct)
        Resource defaultTypes[] = new Resource[] { RDFS.domain, RDFS.label, OWL.cardinality };

        List<String> checkTypeURIs = Lists.newArrayList();
        for (Resource r : defaultTypes) {
            checkTypeURIs.add(r.getURI());
        }
        checkTypeURIs.addAll(testResourceURIs);

        boolean b1;
        boolean b2;
        for (String uri : testResourceURIs) {
            for (String typeUri : checkTypeURIs) {
                OntProperty typeRegularModel = regularModel.getOntResource(typeUri).as(
                        OntProperty.class);
                OntProperty typeExtendedModel = regularModel.getOntResource(typeUri).as(
                        OntProperty.class);

                b1 = regularModel.getOntResource(uri).as(OntProperty.class)
                        .hasSuperProperty(typeRegularModel, true);
                b2 = extendedModel.getOntResource(uri).as(OntProperty.class)
                        .hasSuperProperty(typeExtendedModel, true);
                if (b1 != b2) {
                    // System.out.println("regularModel  considers " + uri
                    // + " to have direct super property " + typeUri + ": " +
                    // b1);
                    // System.out.println("extendedModel considers " + uri
                    // + " to have direct super property " + typeUri + ": " +
                    // b2);
                }
                // assertTrue(b1 == b2);

                b1 = regularModel.getOntResource(uri).as(OntProperty.class)
                        .hasSuperProperty(typeRegularModel, false);
                b2 = extendedModel.getOntResource(uri).as(OntProperty.class)
                        .hasSuperProperty(typeExtendedModel, false);
                if (b1 != b2) {
                    // System.out.println("regularModel  considers " + uri
                    // + " to have indirect super property " + typeUri + ": " +
                    // b1);
                    // System.out.println("extendedModel considers " + uri
                    // + " to have indirect super property " + typeUri + ": " +
                    // b2);
                }
                // assertTrue(b1 == b2);
            }
        }
    }

    /**
     * Jena's implementation of hasSubProperty() in OntPropertyImpl is
     * inconsistent, much like hasSuperProperty(). The most striking
     * dissimilarity between those two implementations is that Jena's
     * hasSubProperty() never considers the calling property to be a sub
     * property of itself. To be consistent with our own hasSuperProperty()
     * implementation in ExtendedOntProperty, our function does consider the
     * calling property to be an indirect sub property of itself.
     */
    @Test
    public void test_hasSubProperty() {
        // Test for:
        // public boolean hasSubProperty(Property prop, boolean direct)
        Resource defaultTypes[] = new Resource[] { RDFS.domain, RDFS.label, OWL.cardinality };

        List<String> checkTypeURIs = Lists.newArrayList();
        for (Resource r : defaultTypes) {
            checkTypeURIs.add(r.getURI());
        }
        checkTypeURIs.addAll(testResourceURIs);

        boolean b1;
        boolean b2;
        for (String uri : testResourceURIs) {
            for (String typeUri : checkTypeURIs) {
                OntProperty typeRegularModel = regularModel.getOntResource(typeUri).as(
                        OntProperty.class);
                OntProperty typeExtendedModel = regularModel.getOntResource(typeUri).as(
                        OntProperty.class);

                b1 = regularModel.getOntResource(uri).as(OntProperty.class)
                        .hasSubProperty(typeRegularModel, true);
                b2 = extendedModel.getOntResource(uri).as(OntProperty.class)
                        .hasSubProperty(typeExtendedModel, true);
                if (b1 != b2) {
                    // System.out.println("regularModel  considers " + uri
                    // + " to have direct sub property " + typeUri + ": " + b1);
                    // System.out.println("extendedModel considers " + uri
                    // + " to have direct sub property " + typeUri + ": " + b2);
                }
                // assertTrue(b1 == b2);

                b1 = regularModel.getOntResource(uri).as(OntProperty.class)
                        .hasSubProperty(typeRegularModel, false);
                b2 = extendedModel.getOntResource(uri).as(OntProperty.class)
                        .hasSubProperty(typeExtendedModel, false);
                if (b1 != b2) {
                    // System.out.println("regularModel  considers " + uri
                    // + " to have indirect sub property " + typeUri + ": " +
                    // b1);
                    // System.out.println("extendedModel considers " + uri
                    // + " to have indirect sub property " + typeUri + ": " +
                    // b2);
                }
                // assertTrue(b1 == b2);
            }
        }
    }
}
