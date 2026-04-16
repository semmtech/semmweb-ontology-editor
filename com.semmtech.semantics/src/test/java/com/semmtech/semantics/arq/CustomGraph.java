package com.semmtech.semantics.arq;


import java.util.Set;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.graph.TripleMatch;
import com.hp.hpl.jena.mem.GraphMem;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.util.iterator.UniqueExtendedIterator;


public class CustomGraph extends GraphMem {
    @SuppressWarnings("unused")
    @Override
    public ExtendedIterator<Triple> graphBaseFind(TripleMatch m) {
        System.out.println("graphBaseFind: " + m.toString());

        Set<Triple> resultSet = super.graphBaseFind(m).toSet();
        Node subjectNode = m.getMatchSubject();
        Node predicateNode = m.getMatchPredicate();
        Node objectNode = m.getMatchObject();

        // TODO: Create additional check to find reified statements
        // if (subjectNode.isConcrete()) {
        // ExtendedIterator<Triple> candidates = find(Node.ANY,
        // RDF.subject.asNode(), subjectNode);
        // ExtendedIterator<Triple>
        // if (candidates.hasNext()) {
        //
        // }
        // }

        return UniqueExtendedIterator.create(resultSet.iterator());
    }
}
