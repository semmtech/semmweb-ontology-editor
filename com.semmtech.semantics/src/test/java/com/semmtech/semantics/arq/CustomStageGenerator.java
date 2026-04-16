package com.semmtech.semantics.arq;


import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.sparql.core.BasicPattern;
import com.hp.hpl.jena.sparql.engine.ExecutionContext;
import com.hp.hpl.jena.sparql.engine.QueryIterator;
import com.hp.hpl.jena.sparql.engine.main.StageGenerator;
import com.hp.hpl.jena.sparql.util.IterLib;


public class CustomStageGenerator implements StageGenerator {

    StageGenerator above = null;

    public CustomStageGenerator(StageGenerator original) {
        above = original;
    }

    @SuppressWarnings("unused")
    @Override
    public QueryIterator execute(BasicPattern pattern, QueryIterator input, ExecutionContext execCxt) {
        Graph g = execCxt.getActiveGraph();
        // Test to see if this is a graph we support.
        if (!(g instanceof CustomStageGenerator)) {
            // Not us - bounce up the StageGenerator chain
            return above.execute(pattern, input, execCxt);
        }
        CustomStageGenerator graph = (CustomStageGenerator) g;
        // Create a QueryIterator for this request
        return IterLib.noResults(execCxt);
    }

}
