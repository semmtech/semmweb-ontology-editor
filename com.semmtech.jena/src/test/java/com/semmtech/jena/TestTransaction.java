package com.semmtech.jena;


import java.util.List;

import org.junit.Test;

import com.google.common.collect.Lists;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.Restriction;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelChangedListener;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ModelMaker;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.shared.JenaException;
import com.hp.hpl.jena.util.FileUtils;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;


@SuppressWarnings("static-method")
public class TestTransaction {

    public TestTransaction() {

    }

    @Test
    public void test() {
        Model init = ModelFactory.createDefaultModel();
        init.read("file:C:/Temp/mike.ttl", null, FileUtils.langTurtle);

        ModelMaker maker = ModelFactory.createFileModelMaker("C:/Temp/");

        Model m = maker.createFreshModel();
        m.add(init);
        OntModel o = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, m);

        Restriction r1 = o.createRestriction(RDFS.subClassOf);

        Model m2 = o.begin();
        OntModel o2 = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, m2);

        // OntModelUtils.copyModel()

        final List<Statement> added = Lists.newArrayList();
        final List<Statement> removed = Lists.newArrayList();
        o2.register(new ModelChangedListener() {

            @Override
            public void removedStatements(Model m) {
                // TODO Auto-generated method stub

            }

            @Override
            public void removedStatements(StmtIterator statements) {
                // TODO Auto-generated method stub

            }

            @Override
            public void removedStatements(List<Statement> statements) {
                // TODO Auto-generated method stub

            }

            @Override
            public void removedStatements(Statement[] statements) {
                // TODO Auto-generated method stub

            }

            @Override
            public void removedStatement(Statement s) {
                removed.add(s);
            }

            @Override
            public void notifyEvent(Model m, Object event) {
                // TODO Auto-generated method stub

            }

            @Override
            public void addedStatements(Model m) {
                // TODO Auto-generated method stub

            }

            @Override
            public void addedStatements(StmtIterator statements) {
                // TODO Auto-generated method stub

            }

            @Override
            public void addedStatements(List<Statement> statements) {
                // TODO Auto-generated method stub

            }

            @Override
            public void addedStatements(Statement[] statements) {
                // TODO Auto-generated method stub

            }

            @Override
            public void addedStatement(Statement s) {
                added.add(s);
            }
        });
        Restriction r1_2 = o2.getOntResource(r1).as(Restriction.class);
        r1_2.setPropertyValue(OWL.onProperty, RDF.type);

        Restriction r2 = o2.createRestriction(RDFS.comment);

        // o2 niet commit
        // o2.abort();
        // o.commit();

        r2.addProperty(RDFS.comment, "afterwards");

        // o2.commit();

        try {
            o.begin();
        }
        catch (JenaException ex) {
            o.abort();
        }

        System.out.println("r2 = " + o.contains(r2, RDFS.comment));

        System.out.println("model =  " + (r2.getModel().getGraph() == r1.getModel().getGraph()));

    }

    @Test
    public void testModelChangeCannotBeTrackedOutsideCopy() {
        ModelMaker maker = ModelFactory.createFileModelMaker("C:/Temp/");
        Model m = maker.createFreshModel();

        OntModel o1 = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, m);
        o1.register(new ModelChangedListener() {

            @Override
            public void removedStatements(Model m) {
                // TODO Auto-generated method stub

            }

            @Override
            public void removedStatements(StmtIterator statements) {
                // TODO Auto-generated method stub

            }

            @Override
            public void removedStatements(List<Statement> statements) {
                // TODO Auto-generated method stub

            }

            @Override
            public void removedStatements(Statement[] statements) {
                // TODO Auto-generated method stub

            }

            @Override
            public void removedStatement(Statement s) {
                System.out.println("removed: " + s);
            }

            @Override
            public void notifyEvent(Model m, Object event) {
            }

            @Override
            public void addedStatements(Model m) {
                // TODO Auto-generated method stub

            }

            @Override
            public void addedStatements(StmtIterator statements) {
                // TODO Auto-generated method stub

            }

            @Override
            public void addedStatements(List<Statement> statements) {
                // TODO Auto-generated method stub

            }

            @Override
            public void addedStatements(Statement[] statements) {
                // TODO Auto-generated method stub

            }

            @Override
            public void addedStatement(Statement s) {
                System.out.println("added: " + s);
            }
        });

        o1.createRestriction(RDFS.subClassOf);

        Model m2 = o1.begin();
        OntModel o2 = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, m2);

        o2.createRestriction(RDFS.label);
        o2.createRestriction(RDFS.comment);

        o2.abort();

        for (Statement s : o1.listStatements().toList()) {
            System.out.println(s);
        }
    }

}
