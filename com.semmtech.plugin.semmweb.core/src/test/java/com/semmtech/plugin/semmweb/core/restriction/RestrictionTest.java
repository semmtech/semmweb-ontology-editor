package com.semmtech.plugin.semmweb.core.restriction;


import java.io.InputStream;

import org.junit.Test;

import com.hp.hpl.jena.ontology.OntDocumentManager;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ModelMaker;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.FileUtils;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.OWL2;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;


/**
 * http://stackoverflow.com/questions/10337455/read-restriction-values-using-
 * jena
 * http://stackoverflow.com/questions/7779927/get-owl-restrictions-on-classes
 * -using-jena/7805455#7805455
 * 
 * @author Simone
 * 
 */
public class RestrictionTest {

    @Test
    public void test() {
        OntDocumentManager ontDocumentManager = new OntDocumentManager();
        ontDocumentManager.setProcessImports(false);
        ModelMaker modelMaker = ModelFactory.createMemModelMaker();
        final OntModelSpec spec = new OntModelSpec(modelMaker, ontDocumentManager, null,
                OWL.FULL_LANG.getURI());
        OntModel model = ModelFactory.createOntologyModel(spec);

        String filename = "src/test/resources/models/restriction.ttl";

        try (InputStream is = FileUtils.openResourceFileAsStream(filename)) {
            model.read(is, null, FileUtils.guessLang(filename));
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

        StmtIterator si = model.listStatements(
                model.getResource("http://www.example.org/restriction/hasAuthor"), RDFS.range,
                (RDFNode) null);

        while (si.hasNext()) {
            Statement stmt = si.next();
            Resource range = stmt.getObject().asResource();
            // get restrictions collection
            Resource nextNode = range.getPropertyResourceValue(OWL2.withRestrictions);
            for (;;) {
                Resource restr = nextNode.getPropertyResourceValue(RDF.first);
                if (restr == null)
                    break;

                StmtIterator pi = restr.listProperties();
                while (pi.hasNext()) {
                    Statement restrStmt = pi.next();
                    Property restrType = restrStmt.getPredicate();
                    Literal value = restrStmt.getObject().asLiteral();
                    // print type and value for each restriction
                    System.out.println(restrType + " = " + value);
                }
                // go to the next element of collection
                nextNode = nextNode.getPropertyResourceValue(RDF.rest);
            }
        }
    }

}
