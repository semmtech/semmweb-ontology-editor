package com.semmtech.jena;


import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.semmtech.jena.vocabulary.Skolem;


public class SkolemizeSubTest {

    public static void main(String[] args) {
        Model skolemized = ModelFactory.createDefaultModel();
        Model deskolemized = ModelFactory.createDefaultModel();

        try {
            RDFDataMgr.read(skolemized, new FileInputStream("src/test/output/skolemized.ttl"),
                    Lang.TURTLE);
            RDFDataMgr.read(deskolemized, new FileInputStream(
                    "src/test/output/de-skolemized-with-iris.ttl"), Lang.TURTLE);
        }
        catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }

        OntModel ontModel = ModelFactory.createOntologyModel();
        Model subModel = ModelFactory.createDefaultModel();
        ontModel.addSubModel(subModel);
        for (StmtIterator iter = deskolemized.listStatements(); iter.hasNext();) {
            Statement stmt = iter.next();
            Property predicate = stmt.getPredicate();
            Resource subject = stmt.getSubject();
            RDFNode node = stmt.getObject();

            if (predicate.getNameSpace().equals(Skolem.NS)) {
                subModel.add(stmt);
            }
            else if (!subject.isAnon() && subject.getNameSpace().equals(Skolem.NS)) {
                subModel.add(stmt);
            }
            else if (node.isResource() && !node.isAnon()
                    && node.asResource().getNameSpace().equals(Skolem.NS)) {
                subModel.add(stmt);
            }
            else {
                ontModel.add(stmt);
            }
        }

        System.out.println("Base:");
        RDFDataMgr.write(System.out, ontModel.getBaseModel(), Lang.TURTLE);

        System.out.println("Sub-model:");
        RDFDataMgr.write(System.out, subModel, Lang.TURTLE);
    }
}
