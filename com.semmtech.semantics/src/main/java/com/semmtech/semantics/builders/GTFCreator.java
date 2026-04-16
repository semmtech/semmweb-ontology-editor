/********************************************************************************
 * Copyright (c) 2011-2016, 2026 Semmtech B.V., Hoofddorp.
 *    ___  _____ __  __ __  __ _____ _____ ___ _   _ 
 *   / __|| ____|  \/  |  \/  |_   _| ____/ __| | | |
 *   \__ \|  _| | |\/| | |\/| | | | |  _|| |  | |_| |
 *    __) | |___| |  | | |  | | | | | |__| |__|  _  |
 *   |___/|_____|_|  |_|_|  |_| |_| |_____\___|_| |_| B.V.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package com.semmtech.semantics.builders;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.hp.hpl.jena.ontology.AnnotationProperty;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.InverseFunctionalProperty;
import com.hp.hpl.jena.ontology.ObjectProperty;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.Ontology;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.util.FileUtils;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.DC_11;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.XSD;
import com.semmtech.semantics.Util;
import com.semmtech.semantics.vocabulary.GTF;


// TODO: Auto-generated Javadoc
/**
 * The Class GTFCreator.
 * 
 * @author Mike Henrichs
 */
public class GTFCreator {

    /** The gtf model. */
    private static OntModel gtfModel = null;

    /**
     * The main method.
     * 
     * @param args
     *            the arguments
     */
    public static void main(String[] args) {
        initializeOntology();
        createOntology();
        save();

        System.out.println("[Finished]");
    }

    /**
     * Creates the ontology.
     */
    private static void createOntology() {
        OntClass AtomicFact = gtfModel.createClass(GTF.AtomicFact.getURI());
        OntClass Object = gtfModel.createClass(GTF.Object.getURI());
        Object.removeSuperClass(OWL.Thing);
        Object.addSuperClass(RDFS.Resource);
        OntClass MainFact = gtfModel.createClass(GTF.MainFact.getURI());
        OntClass AuxiliaryFact = gtfModel.createClass(GTF.AuxiliaryFact.getURI());

        gtfModel.createList(new RDFNode[] { Object, MainFact });
        // UnionClass Unique = gtfModel.createUnionClass(GTF.Unique.getURI(),
        // members);
        // Unique.addSuperClass(RDFS.Resource);
        // Unique.removeSuperClass(OWL.Thing);
        // Unique.addProperty(RDFS.subClassOf, RDFS.Resource);
        // Unique.addLabel("unique object or fact", "en");
        // Unique.addComment("A uniquely identifyable object or fact about an object",
        // "en");

        AtomicFact.addProperty(RDFS.subClassOf, RDF.Statement);
        AtomicFact
                .addProperty(
                        RDFS.label,
                        gtfModel.createLiteral(
                                "A Gellish fact is a representation of an atomic fact or a proposition about an atomic fact in a context.",
                                "en"));
        AtomicFact.addProperty(RDFS.label, gtfModel.createLiteral("atomic fact", "en"));
        AtomicFact.addProperty(RDFS.label, gtfModel.createLiteral("atomair feit", "nl"));
        MainFact.addProperty(RDFS.subClassOf, GTF.AtomicFact);
        AuxiliaryFact.addProperty(RDFS.subClassOf, GTF.AtomicFact);

        MainFact.addProperty(OWL.disjointWith, GTF.AuxiliaryFact);
        AuxiliaryFact.addProperty(OWL.disjointWith, MainFact);
        AtomicFact.addProperty(OWL.disjointWith, GTF.Object);
        Object.addProperty(OWL.disjointWith, GTF.AtomicFact);

        // / GTF.uid
        InverseFunctionalProperty uid = gtfModel.createInverseFunctionalProperty(GTF.uid.getURI());
        // uid.addRDFType(RDF.Property);
        uid.addRange(XSD.nonNegativeInteger);
        uid.addComment(gtfModel
                .createLiteral(
                        "The Gellish UID is a unique numeric identifier for a single object or statement (fact) about objects.",
                        "en"));
        uid.addDomain(GTF.Unique);

        // / GTF.fullDefinition
        AnnotationProperty fullDefinition = gtfModel.createAnnotationProperty(GTF.fullDefinition
                .getURI());
        // fullDefinition.addRDFType(RDF.Property);
        fullDefinition.addLabel(gtfModel.createLiteral("full definition", "en"));
        fullDefinition.addLabel(gtfModel.createLiteral("impliciete definitie", "nl"));
        fullDefinition
                .addComment(gtfModel
                        .createLiteral(
                                "The full definition is a textual description of the characteristics that identify the object or members of the object class.",
                                "en"));
        fullDefinition.addSuperProperty(RDFS.comment);
        fullDefinition.addDomain(GTF.Object);

        // / GTF.name
        AnnotationProperty name = gtfModel.createAnnotationProperty(GTF.name.getURI());
        // name.addRDFType(RDF.Property);
        name.addSuperProperty(RDFS.label);
        name.addDomain(GTF.Object);

        // / Reality
        ObjectProperty reality = gtfModel.createObjectProperty(GTF.reality.getURI());
        // reality.addRDFType(RDF.Property);
        reality.addLabel("reality", "en");
        reality.addComment(
                "The reality is a classification of the left hand object within a Gellish fact.",
                "en");
        OntClass Reality = gtfModel.createClass(GTF.Reality.getURI());
        AtomicFact.addProperty(RDFS.subClassOf, RDF.Statement);
        gtfModel.createIndividual(GTF.Real.getURI(), Reality).addLabel("real", "en");
        gtfModel.createIndividual(GTF.Imaginary.getURI(), Reality).addLabel("imaginary", "en");
        reality.addProperty(RDFS.range, GTF.Reality);

        // / Intention
        ObjectProperty intention = gtfModel.createObjectProperty(GTF.intention.getURI());
        // intention.addRDFType(RDF.Property);
        intention.addLabel("intention", "en");
        intention
                .addComment(
                        "An intention indicates the xetent to which the main fact is the case or is the case according to the author of a proposition.",
                        "en");
        OntClass Intention = gtfModel.createClass(GTF.Intention.getURI());
        Individual Request = gtfModel.createIndividual(GTF.Request.getURI(), Intention);
        Request.addLabel("request", "en");
        Individual Question = gtfModel.createIndividual(GTF.Question.getURI(), Intention);
        Question.addLabel("question", "en");
        Request.addSameAs(GTF.Question);
        Question.addSameAs(GTF.Request);
        gtfModel.createIndividual(GTF.Statement.getURI(), Intention).addLabel("statement", "en");
        gtfModel.createIndividual(GTF.Confirmation.getURI(), Intention).addLabel("confirmation",
                "en");
        gtfModel.createIndividual(GTF.True.getURI(), Intention).addLabel("true", "en");
        gtfModel.createIndividual(GTF.Declination.getURI(), Intention)
                .addLabel("declination", "en");
        gtfModel.createIndividual(GTF.Promise.getURI(), Intention).addLabel("promise", "en");
        gtfModel.createIndividual(GTF.Denial.getURI(), Intention).addLabel("denial", "en");
        gtfModel.createIndividual(GTF.Probability.getURI(), Intention)
                .addLabel("probability", "en");
        gtfModel.createIndividual(GTF.Acceptance.getURI(), Intention).addLabel("acceptance", "en");
        intention.addRange(GTF.Intention);
        intention.addDomain(GTF.MainFact);

        // / Status
        ObjectProperty status = gtfModel.createObjectProperty(GTF.status.getURI());
        // status.addRDFType(RDF.Property);
        status.addLabel("status", "en");
        status.addLabel("approval status", "en");
        status.addComment("An approval status indicates the status of the main fact.", "en");
        OntClass Status = gtfModel.createClass(GTF.Status.getURI());
        gtfModel.createIndividual(GTF.Proposed.getURI(), Status).addLabel("proposed", "en");
        gtfModel.createIndividual(GTF.Issue.getURI(), Status).addLabel("issue", "en");
        gtfModel.createIndividual(GTF.Deleted.getURI(), Status).addLabel("deleted", "en");
        gtfModel.createIndividual(GTF.ProposedToBeDeleted.getURI(), Status).addLabel(
                "proposed to be deleted", "en");
        gtfModel.createIndividual(GTF.Ignore.getURI(), Status).addLabel("ignore", "en");
        gtfModel.createIndividual(GTF.Agreed.getURI(), Status).addLabel("agreed", "en");
        gtfModel.createIndividual(GTF.Accepted.getURI(), Status).addLabel("accepted", "en");
        gtfModel.createIndividual(GTF.Replaced.getURI(), Status).addLabel("replaced", "en");
        status.addRange(GTF.Status);
        status.addDomain(GTF.MainFact);

        // / Author, Source, and dates based on DC
        OntProperty author = gtfModel.createOntProperty(GTF.author.getURI());
        // author.addRDFType(RDF.Property);
        author.addProperty(RDFS.subPropertyOf, DCTerms.creator);
        author.addLabel("author", "en");
        author.addComment(
                "The person or organisation who is the originator of the proposition or of the expression of the fact.",
                "en");
        author.addDomain(GTF.MainFact);

        OntProperty source = gtfModel.createOntProperty(GTF.source.getURI());
        // source.addRDFType(RDF.Property);
        source.addProperty(RDFS.subPropertyOf, DCTerms.source);
        source.addLabel("source", "en");
        source.addDomain(GTF.MainFact);

        OntProperty startOfLife = gtfModel.createOntProperty(GTF.startOfLife.getURI());
        // startOfLife.addRDFType(RDF.Property);
        startOfLife.addProperty(RDFS.subPropertyOf, DCTerms.created);
        startOfLife.addLabel("start of life", "en");
        startOfLife.addDomain(GTF.MainFact);

        OntProperty latestChange = gtfModel.createOntProperty(GTF.latestChange.getURI());
        // latestChange.addRDFType(RDF.Property);
        latestChange.addProperty(RDFS.subPropertyOf, DCTerms.modified);
        latestChange.addLabel("latest change", "en");
        latestChange.addDomain(GTF.MainFact);

    }

    /**
     * Initialize ontology.
     */
    private static void initializeOntology() {
        // / Enrich GTF vocabulary
        gtfModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
        gtfModel.setNsPrefix("gtf", GTF.NS);
        gtfModel.setNsPrefix("dcterms", DCTerms.NS);
        Ontology ontology = gtfModel.createOntology(GTF.NS);
        ontology.addImport(DC_11.NAMESPACE);
        ontology.addImport(DCTerms.NAMESPACE);
        ontology.addProperty(DC_11.title,
                gtfModel.createLiteral("The Gellish Table Format (GTF) vocabulary", "en"));
        ontology.addProperty(DC_11.creator, gtfModel.createLiteral("Mike Henrichs"));
        ontology.addProperty(DC_11.publisher, gtfModel.createLiteral("Semmtech B.V."));
        ontology.addProperty(
                DC_11.description,
                gtfModel.createLiteral(
                        "This ontology contains the meta information properties found in the Gellish Table Format (GTF). The Gellish Table Format was created by Andries van Renssen. For more information on the Gellish Table Format, or Gellish in more general, please visit http://www.gellish.net.",
                        "en"));
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        ontology.addProperty(DC_11.date, dateFormat.format(new Date()));
    }

    /**
     * Save.
     */
    private static void save() {
        if (gtfModel == null) {
            System.out.println("WARNING:: Nothing to save!");
            return;
        }
        String filename = "output/gellish-table-format.owl";

        System.out.printf("INFO:: Saving SEMM OWL Ontology to %s... ", filename);
        Util.saveOWL(gtfModel, filename, GTF.NS, FileUtils.langTurtle);
        System.out.println("done!");

        // boolean topBraidSaved = false;
        // try {
        // if
        // (Environment.getEnvironmentVariables().containsKey("TBC_WORKSPACE"))
        // {
        // String tbcWorkspace =
        // Environment.getEnvironmentVariables().getProperty("TBC_WORKSPACE");
        // String topBraidFilename =
        // String.format("%s\\TopBraid\\Gellish\\gellish-table-format.owl",
        // tbcWorkspace);
        // System.out.printf("INFO:: Saving SEMM OWL Ontology to %s... ",
        // topBraidFilename);
        // Util.saveOWL(gtfModel, topBraidFilename, GTF.NS);
        // topBraidSaved = true;
        // System.out.println("done!");
        // }
        // }
        // catch(Exception ex) {
        // }
        // catch (Throwable e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
        // if (!topBraidSaved) {
        // String topBraidFilename =
        // "D:\\Projects\\Workspaces\\TopBraid\\TBCFreeWorkspace\\TopBraid\\Gellish\\gellish-table-format.owl";
        // System.out.printf("INFO:: Saving SEMM OWL Ontology to %s... ",
        // topBraidFilename);
        // Util.saveOWL(gtfModel, topBraidFilename, GTF.NS);
        // System.out.println("done!");
        // }
    }
}
