package com.semmtech.jena.util;


import java.util.Iterator;
import java.util.List;

import com.google.common.collect.Lists;
import com.hp.hpl.jena.graph.Factory;
import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.shared.PrefixMapping;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.XSD;


public class JenaUtils {

    /**
     * A memory graph with no reification.
     */
    public static Graph createDefaultGraph() {
        return Factory.createDefaultGraph();
    }

    /**
     * Wraps the result of {@link #createDefaultGraph()} into a Model and
     * initializes namespaces.
     * 
     * @return a default Model
     * @see #createDefaultGraph()
     */
    public static Model createDefaultModel() {
        Model m = ModelFactory.createModelForGraph(createDefaultGraph());
        initNamespaces(m);
        return m;
    }

    /**
     * Sets the usual default namespaces for rdf, rdfs, owl and xsd.
     * 
     * @param prefixMapping
     *            the Model to modify
     */
    public static void initNamespaces(PrefixMapping prefixMapping) {
        prefixMapping.setNsPrefix("rdf", RDF.getURI());
        prefixMapping.setNsPrefix("rdfs", RDFS.getURI());
        prefixMapping.setNsPrefix("owl", OWL.getURI());
        prefixMapping.setNsPrefix("xsd", XSD.getURI());
    }

    /**
     * Traverses the subclass hierarchy to find all subclasses of a class.
     * 
     * This method works both for a raw model and a model with inference.
     * Redundant or circular subClass declarations should not pose a problem.
     * 
     * @param cls
     *            the class for which to retrieve the subclasses.
     * @return
     */
    public static List<OntClass> listAllSubClasses(OntClass cls) {
        // Preconditions.checkNotNull(cls);
        List<OntClass> subClasses = Lists.newArrayList(cls.listSubClasses());

        // Add the base class simplify checking for redundant/circular subClass
        // declarations:
        subClasses.add(cls);

        Iterator<OntClass> iter = subClasses.iterator();

        while (iter.hasNext()) {
            OntClass subClass = iter.next();

            ExtendedIterator<OntClass> subIter = subClass.listSubClasses();

            while (subIter.hasNext()) {
                OntClass subSubClass = subIter.next();

                if (!subClasses.contains(subSubClass)) {
                    subClasses.add(subSubClass);
                }
            }
        }

        // Remove the base class to return only real (direct and indirect)
        // subclasses:
        subClasses.remove(cls);

        return subClasses;
    }

    /**
     * Traverses the superclass hierarchy to find all superclasses of a class.
     * 
     * This method works both for a raw model and a model with inference.
     * Redundant or circular subClass declarations should not pose a problem.
     * 
     * @param cls
     *            the class for which to retrieve the subclasses.
     * @return
     */
    public static List<OntClass> listAllSuperClasses(OntClass cls) {
        // Preconditions.checkNotNull(cls);
        List<OntClass> superClasses = Lists.newArrayList(cls.listSuperClasses());

        // Add the initial class simplify checking for redundant/circular super
        // class
        // declarations:
        superClasses.add(cls);

        Iterator<OntClass> iter = superClasses.iterator();

        while (iter.hasNext()) {
            OntClass subClass = iter.next();

            ExtendedIterator<OntClass> superIter = subClass.listSuperClasses();

            while (superIter.hasNext()) {
                OntClass superSuperClass = superIter.next();

                if (!superClasses.contains(superSuperClass)) {
                    superClasses.add(superSuperClass);
                }
            }
        }

        // Remove the initial class to return only real (direct and indirect)
        // super classes:
        superClasses.remove(cls);

        return superClasses;
    }

    public static List<String> ontClassUrisAsStrings(List<OntClass> classList) {
        List<String> list = Lists.newArrayList();

        for (OntClass cls : classList) {
            String uri = cls.getURI();

            list.add(uri);
        }

        return list;
    }

}
