package com.hp.hpl.jena.rdf.model.impl;


/**
 * This class has been created in order to subclass this reader outside the
 * {@code com.hp.hpl.jena.rdf.model.impl} package; since the original reader has
 * a constructor accessible on package level instead of public .
 * 
 * @author Mike Henrichs
 * 
 */
public class NTripleReaderSubclassable extends NTripleReader {
    public NTripleReaderSubclassable() {
        super();
    }
}
