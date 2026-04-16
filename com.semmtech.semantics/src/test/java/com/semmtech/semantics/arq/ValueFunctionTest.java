package com.semmtech.semantics.arq;


import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import com.hp.hpl.jena.graph.GraphUtil;
import com.hp.hpl.jena.query.ARQ;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ModelMaker;
import com.hp.hpl.jena.rdf.model.impl.ModelCom;
import com.hp.hpl.jena.sparql.engine.main.StageBuilder;
import com.hp.hpl.jena.sparql.engine.main.StageGenerator;
import com.hp.hpl.jena.util.FileUtils;


public class ValueFunctionTest {
    public ValueFunctionTest() {

    }

    @Test
    public void testReverseFunction() throws IOException {
        ModelMaker maker = ModelFactory.createMemModelMaker();
        Model model = maker.createDefaultModel();

        try (FileInputStream fis = new FileInputStream("src/test/resources/examples/rei-model.ttl")) {
            model.read(fis, null, FileUtils.langTurtle);

        }
        System.out.println(String.format("Model read (%s statements):", model.size()));
        model.write(System.out, FileUtils.langTurtle);

        CustomGraph graph = new CustomGraph();
        GraphUtil.addInto(graph, model.getGraph());
        Model model2 = new ModelCom(graph);

        try (FileInputStream fis = new FileInputStream(
                "src/test/resources/examples/reverse-function.sparql")) {

            String sparql = IOUtils.toString(fis);
            System.out.println("\nSPARQL:\n" + sparql);

            // FunctionRegistry.get().put("http://www.semmweb.com/functions#reverse",
            // ReverseLiteralFunction.class);
            // PropertyFunctionRegistry.get().put("http://www.semmweb.com/eclipse#workbenchAdapter",
            // WorkbenchAdapterPropertyFunction.class);

            // Get the standard one.
            StageGenerator original = (StageGenerator) ARQ.getContext().get(ARQ.stageGenerator);
            // Create a new one
            StageGenerator custom = new CustomStageGenerator(original);
            // Register it
            StageBuilder.setGenerator(ARQ.getContext(), custom);

            QueryExecution execution = QueryExecutionFactory.create(sparql, model2);
            ResultSet result = execution.execSelect();
            System.out.println("\nResult:");
            ResultSetFormatter.out(System.out, result);
        }
    }
}
