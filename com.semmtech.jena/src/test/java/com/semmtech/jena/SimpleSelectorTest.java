package com.semmtech.jena;


import org.junit.Test;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.vocabulary.RDF;


@SuppressWarnings("static-method")
public class SimpleSelectorTest {
    public SimpleSelectorTest() {

    }

    @Test
    public void test() {
        Model model = ModelFactory.createDefaultModel();

        for (ExtendedIterator<Statement> iter = model.listStatements(new SimpleSelector(null,
                RDF.type, (RDFNode) null)); iter.hasNext();) {
            @SuppressWarnings("unused")
            Statement stmt = iter.next();

        }
    }
}
