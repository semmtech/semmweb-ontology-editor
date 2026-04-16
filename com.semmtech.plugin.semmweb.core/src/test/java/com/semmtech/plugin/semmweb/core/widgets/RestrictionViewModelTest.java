package com.semmtech.plugin.semmweb.core.widgets;


import static com.semmtech.plugin.semmweb.core.resourceviewer.AbstractResourceViewModel.Vocabulary.Root;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntDocumentManager;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.Restriction;
import com.hp.hpl.jena.query.ARQ;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ModelMaker;
import com.hp.hpl.jena.sparql.mgt.Explain;
import com.hp.hpl.jena.vocabulary.OWL2;
import com.hp.hpl.jena.vocabulary.RDF;
import com.semmtech.plugin.semmweb.core.resourceviewer.AbstractResourceViewModel.Vocabulary;


public class RestrictionViewModelTest {

    private static final String URI = "http://www.semmtech.com/restrictions/";
    private static final String PREFIX = "sub";

    private OntModel model;

    private OntClass superSuperClass;
    private OntClass superClass;
    private OntClass clazz;
    private OntClass branch;
    private OntClass subClass;

    private OntProperty doSomething;
    private OntProperty doSomethingOther;

    private RestrictionsViewModel viewModel;

    @BeforeClass
    public static final void init() {
        ARQ.setExecutionLogging(Explain.InfoLevel.NONE);
    }

    @Before
    public final void initModel() {
        OntDocumentManager ontDocumentManager = new OntDocumentManager();
        ontDocumentManager.setProcessImports(false);
        ModelMaker modelMaker = ModelFactory.createMemModelMaker();
        OntModelSpec spec = new OntModelSpec(modelMaker, ontDocumentManager, null, OWL2.getURI());

        model = ModelFactory.createOntologyModel(spec);
        model.setNsPrefix(PREFIX, URI);

        superSuperClass = model.createClass(URI + "SuperSuperClass");
        superClass = model.createClass(URI + "SuperClass");
        clazz = model.createClass(URI + "Class");
        subClass = model.createClass(URI + "SubClass");
        branch = model.createClass(URI + "Branch");

        superSuperClass.addSubClass(superClass);
        superClass.addSubClass(clazz);
        branch.addSubClass(clazz);
        clazz.addSubClass(subClass);

        doSomething = model.createOntProperty(URI + "doSomethig");
        doSomethingOther = model.createOntProperty(URI + "doSomethingOther");

        Restriction cardinality0 = model.createCardinalityRestriction(URI + "Cardinality0",
                doSomething, 1);

        Restriction cardinality1 = model.createCardinalityRestriction(URI + "Cardinality1",
                doSomething, 1);

        Restriction cardinality2 = model.createCardinalityRestriction(URI + "Cardinality2",
                doSomething, 1);

        Restriction cardinality3 = model.createCardinalityRestriction(URI + "Cardinality3",
                doSomething, 1);

        Restriction cardinality4 = model.createCardinalityRestriction(URI + "Cardinality4",
                doSomething, 4);

        Restriction cardinality5 = model.createCardinalityRestriction(URI + "Cardinality5",
                doSomething, 1);

        Restriction cardinality6 = model.createCardinalityRestriction(URI + "Cardinality6",
                doSomethingOther, 1);

        superSuperClass.addSuperClass(cardinality0);
        superSuperClass.addSuperClass(cardinality6);
        superClass.addSuperClass(cardinality1);
        clazz.addSuperClass(cardinality2);
        branch.addSuperClass(cardinality3);
        subClass.addSuperClass(cardinality4);
        cardinality4.addSuperClass(cardinality5);

        viewModel = new RestrictionsViewModel(model, subClass);
    }

    @After
    public final void dispose() {
        model.close();
    }

    @Test
    public void testTaxonomy() {
        Model taxonomyModel = viewModel.buildInverseTaxonomy(subClass);

        assertTrue(taxonomyModel.contains(subClass, RDF.type, Root));
        assertTrue(taxonomyModel.contains(clazz, Vocabulary.isChildOf, subClass));
        assertTrue(taxonomyModel.contains(superClass, Vocabulary.isChildOf, clazz));
        assertTrue(taxonomyModel.contains(superSuperClass, Vocabulary.isChildOf, superClass));
        assertTrue(taxonomyModel.contains(branch, Vocabulary.isChildOf, clazz));
    }

}
