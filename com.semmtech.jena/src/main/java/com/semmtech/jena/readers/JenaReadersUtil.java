package com.semmtech.jena.readers;


import com.hp.hpl.jena.rdf.model.impl.RDFReaderFImpl;


public class JenaReadersUtil {

    /**
     * Reset Default readers, which can have been reset by ARQ (due to execution
     * of SPARQL query). Note that this method does not reset the connection
     * timeout values for these readers.
     */
    public static void reset() {
        String[] languages = { "RDF/XML", "RDF/XML-ABBREV", "N-TRIPLE", "N-TRIPLES", "N3",
                "TURTLE", "Turtle", "TTL", "GRDDL" };
        String[] readers = { "com.semmtech.jena.readers.JenaReader",
                "com.semmtech.jena.readers.JenaReader", "com.semmtech.jena.readers.NTripleReader",
                "com.semmtech.jena.readers.NTripleReader",
                "com.semmtech.jena.readers.TurtleReader", "com.semmtech.jena.readers.TurtleReader",
                "com.semmtech.jena.readers.TurtleReader", "com.semmtech.jena.readers.TurtleReader",
                "com.hp.hpl.jena.grddl.GRDDLReader" };

        for (int i = 0; i < languages.length; i++) {
            RDFReaderFImpl.setBaseReaderClassName(languages[i], readers[i]);
        }
    }

    /** Sets the connection timeout value for all default readers */
    public static void setConnectionTimeout(int timeout) {
        JenaReader.setConnectionTimeout(timeout);
        NTripleReader.setConnectionTimeout(timeout);
        TurtleReader.setConnectionTimeout(timeout);
    }
}
