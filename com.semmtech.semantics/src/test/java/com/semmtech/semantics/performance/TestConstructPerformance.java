package com.semmtech.semantics.performance;


import java.io.FileInputStream;
import java.io.IOException;
import java.util.Calendar;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ModelMaker;
import com.hp.hpl.jena.sparql.core.BasicPattern;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.syntax.Template;
import com.hp.hpl.jena.util.FileUtils;
import com.hp.hpl.jena.vocabulary.RDF;
import com.semmtech.semantics.sparql.PathUtil;
import com.semmtech.semantics.sparql.QueryBuilder;


public class TestConstructPerformance {
    public TestConstructPerformance() {

    }

    @SuppressWarnings("unused")
    @Test
    public void testConstruct() throws IOException {

        ModelMaker maker = ModelFactory.createMemModelMaker();
        Model model = ModelFactory.createOntologyModel();

        try (FileInputStream fis = new FileInputStream("src/test/resources/OTL/otl-recommended.ttl")) {
            model.read(fis, null, FileUtils.langTurtle);
        }

        System.out.println(String.format("Model read (%s statements)", model.size()));

        try (FileInputStream fis = new FileInputStream("src/test/resources/OTL/construct.sparql")) {
            String sparql = IOUtils.toString(fis);
        }

        QueryBuilder builder = QueryBuilder.createSelect(false);
        Var v = Var.alloc("property");

        builder.getQuery().setQueryConstructType();
        BasicPattern bp = new BasicPattern();
        bp.add(new Triple(v, RDF.type.asNode(), RDF.Property.asNode()));
        builder.getQuery().setConstructTemplate(new Template(bp));
        builder.addTriplePattern(v, PathUtil.isInstanceOf, RDF.Property);

        // PropertyFunctionRegistry.get().put("http://lemantle.com/selectProperty",
        // PropertySelectionPropertyFunction.class);

        long start = Calendar.getInstance().getTimeInMillis();
        Model target = maker.createFreshModel();
        QueryExecution execution = QueryExecutionFactory.create(builder.getQuery(), model);
        execution.execConstruct(target);
        long end = Calendar.getInstance().getTimeInMillis();
        System.out.println(String.format("execConstruct(Model) took %s ms.", end - start));
        System.out.println(String.format("Model filled (%s statements)", target.size()));

        // target.write(System.out, FileUtils.langTurtle);
    }
}
