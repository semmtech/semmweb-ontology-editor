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


import java.io.File;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.Ontology;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.FileUtils;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.semmtech.semantics.Util;
import com.semmtech.semantics.vocabulary.SEMM;


/**
 * @author Mike Henrichs
 * 
 */
public class SEMMCreator {
    public static OntModel semmModel;

    public static void main(String[] args) {
        initializeOntology();
        createOntology();
        save();

        System.out.println("[Finished]");
    }

    private static void createOntology() {
        if (semmModel == null) {
            System.out.println("WARNING:: Ontology was not initialized, nothing was created!");
            return;
        }

        OntClass aspect = semmModel.createClass(SEMM.Aspect.getURI());
        OntClass role = semmModel.createClass(SEMM.Role.getURI());
        OntClass aspectPossessor = semmModel.createClass(SEMM.AspectPossessor.getURI());
        OntClass possessedAspect = semmModel.createClass(SEMM.PossessedAspect.getURI());
        OntClass qualification = semmModel.createClass(SEMM.Qualification.getURI());
        OntClass physicalObject = semmModel.createClass(SEMM.PhysicalObject.getURI());
        OntClass scale = semmModel.createClass(SEMM.Scale.getURI());
        OntClass quantification = semmModel.createClass(SEMM.Quantification.getURI());

        aspect.addSuperClass(OWL.Thing);
        aspect.addLabel("aspect", "nl");
        aspect.addLabel("aspect", "en");
        role.addSuperClass(aspect);
        role.addLabel("rol", "nl");
        role.addLabel("role", "en");
        aspectPossessor.addSuperClass(role);
        aspectPossessor.addLabel("aspect bezitter", "nl");
        aspectPossessor.addLabel("aspect possessor", "en");
        possessedAspect.addSuperClass(role);
        possessedAspect.addLabel("intrinsiek aspect", "nl");
        possessedAspect.addLabel("possessed aspect", "en");
        qualification.addSuperClass(OWL.Thing);
        qualification.addLabel("qualificatie", "nl");
        qualification.addLabel("qualification", "en");
        quantification.addSuperClass(qualification);
        quantification.addLabel("quantificatie", "nl");
        quantification.addLabel("quantification", "en");
        physicalObject.addSuperClass(OWL.Thing);
        physicalObject.addLabel("fysiek object", "nl");
        physicalObject.addLabel("physical object", "en");
        scale.addSuperClass(OWL.Thing);
        scale.addLabel("schaal", "nl");
        scale.addLabel("scale", "en");

        OntProperty hasAspect = semmModel.createObjectProperty(SEMM.hasAspect.getURI());
        OntProperty hasRole = semmModel.createObjectProperty(SEMM.hasRole.getURI());
        OntProperty hasObjectRole = semmModel.createObjectProperty(SEMM.hasObjectRole.getURI());
        OntProperty hasSubjectRole = semmModel.createObjectProperty(SEMM.hasSubjectRole.getURI());
        OntProperty isQualifiedAs = semmModel.createObjectProperty(SEMM.isQualifiedAs.getURI());
        OntProperty isRoleOf = semmModel.createObjectProperty(SEMM.isRoleOf.getURI());
        OntProperty isPossessedAspectOf = semmModel.createObjectProperty(SEMM.isPossessedAspectOf
                .getURI());
        OntProperty hasPossessedAspect = semmModel.createObjectProperty(SEMM.hasPossessedAspect
                .getURI());

        OntProperty isCorrespodingConceptFor = semmModel
                .createObjectProperty(SEMM.isCorrespodingConceptFor.getURI());
        OntProperty hasCorrespondingConcept = semmModel
                .createObjectProperty(SEMM.hasCorrespondingConcept.getURI());
        // OntProperty isDescribedBy =
        // semmModel.createObjectProperty(SEMM.isDescribedBy.getURI());

        OntProperty hasValue = semmModel.createObjectProperty(SEMM.hasValue.getURI());
        OntProperty hasScale = semmModel.createObjectProperty(SEMM.hasScale.getURI());
        OntProperty isQuantifiedOnScale = semmModel.createObjectProperty(SEMM.isQuantifiedOnScale
                .getURI());
        OntProperty isQualificationOf = semmModel.createObjectProperty(SEMM.isQualificationOf
                .getURI());
        OntProperty isNatureOf = semmModel.createObjectProperty(SEMM.isNatureOf.getURI());

        OntProperty hasPart = semmModel.createObjectProperty(SEMM.hasPart.getURI());
        OntProperty isPartOf = semmModel.createObjectProperty(SEMM.isPartOf.getURI());

        hasAspect.addDomain(OWL.Thing);
        hasAspect.addLabel("has aspect", "en");
        hasAspect.addLabel("heeft aspect", "nl");
        hasAspect.addRange(aspect);

        hasRole.addInverseOf(isRoleOf);
        hasRole.addDomain(OWL.Thing);
        hasRole.addRange(role);
        hasRole.addLabel("has role", "en");
        hasRole.addLabel("heeft role", "nl");

        isRoleOf.addInverseOf(hasRole);
        isRoleOf.addDomain(role);
        isRoleOf.addRange(OWL.Thing);
        isRoleOf.addLabel("is role of", "en");
        isRoleOf.addLabel("is rol voor", "nl");

        hasSubjectRole.addDomain(RDF.Property);
        hasSubjectRole.addRange(OWL.Class);
        hasSubjectRole.addLabel("has subject role", "en");
        hasSubjectRole.addLabel("heeft subject rol", "nl");

        hasObjectRole.addDomain(RDF.Property);
        hasObjectRole.addRange(OWL.Class);
        hasObjectRole.addLabel("has object role", "en");
        hasObjectRole.addLabel("heeft object rol", "nl");

        isQualifiedAs.addRange(qualification);
        isQualifiedAs.addDomain(OWL.Class);
        isQualifiedAs.addLabel("is qualified as", "en");
        isQualifiedAs.addLabel("is gekwalificeerd als", "nl");

        isPossessedAspectOf.addInverseOf(hasPossessedAspect);
        isPossessedAspectOf.addDomain(possessedAspect);
        isPossessedAspectOf.addRange(OWL.Thing);
        isPossessedAspectOf.addLabel("is possessed aspect of", "en");
        isPossessedAspectOf.addLabel("is intrinsiek aspect voor", "nl");

        hasPossessedAspect.addInverseOf(isPossessedAspectOf);
        hasPossessedAspect.addDomain(OWL.Thing);
        hasPossessedAspect.addRange(possessedAspect);
        hasPossessedAspect.addLabel("has possessed aspect", "en");
        hasPossessedAspect.addLabel("heeft intrinsiek aspect", "nl");

        isCorrespodingConceptFor.addInverseOf(hasCorrespondingConcept);
        isCorrespodingConceptFor.addLabel("is corresponding concept for", "en");
        isCorrespodingConceptFor.addLabel("is bijbehorend concept voor", "nl");

        hasCorrespondingConcept.addInverseOf(isCorrespodingConceptFor);
        hasCorrespondingConcept.addLabel("has corresponding concept", "en");
        hasCorrespondingConcept.addLabel("heeft bijbehorend concept", "nl");

        hasScale.addDomain(quantification);
        hasScale.addRange(scale);
        hasScale.addLabel("has scale", "en");
        hasScale.addLabel("heeft schaal", "nl");

        hasValue.addDomain(quantification);
        hasValue.addLabel("has value", "en");
        hasValue.addLabel("heeft waarde", "nl");

        isQuantifiedOnScale.addDomain(OWL.Thing);
        isQuantifiedOnScale.addRange(scale);
        isQuantifiedOnScale.addLabel("is quantified on scale", "en");
        isQuantifiedOnScale.addLabel("is gekwantificeerd op schaal", "nl");

        isQualificationOf.addDomain(qualification);
        isQualificationOf.addRange(OWL.Class);
        isQualificationOf.addInverseOf(isNatureOf);
        isQualificationOf.addLabel("is qualification of", "en");
        isQualificationOf.addLabel("is kwalificatie voor", "nl");

        isNatureOf.addInverseOf(isQualificationOf);
        isNatureOf.addDomain(OWL.Class);
        isNatureOf.addRange(qualification);
        isNatureOf.addLabel("is nature of", "en");

        hasPart.addInverseOf(isPartOf);
        isPartOf.addInverseOf(hasPart);

        hasPart.addDomain(OWL.Thing);
        hasPart.addRange(OWL.Thing);
        hasPart.addLabel("has part", "en");

        isPartOf.addDomain(OWL.Thing);
        isPartOf.addRange(OWL.Thing);
        isPartOf.addLabel("is part of", "en");

    }

    private static void initializeOntology() {
        semmModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM); // OWL_LITE_MEM
                                                                               // specifies
                                                                               // the
                                                                               // entailment
                                                                               // used.
        semmModel.setNsPrefix("semm", SEMM.NS);
        semmModel.setNsPrefix("dc", DCTerms.NS);
        Ontology ontology = semmModel.createOntology(SEMM.NS);
        System.out.printf("INFO:: OWL Ontology will be created for namespace '%s'\n", SEMM.NS);
        ontology.addImport(DCTerms.NAMESPACE);
        ontology.addProperty(DCTerms.title,
                semmModel.createLiteral("The Semmtech SEMM upper ontology"));
        ontology.addProperty(DCTerms.creator, semmModel.createLiteral("Mike Henrichs"));
        ontology.addProperty(DCTerms.description,
                semmModel.createLiteral("The SEMM upper ontology", "en"));
        ontology.addProperty(DCTerms.publisher, semmModel.createLiteral("Semmtech B.V."));
    }

    private static void save() {
        if (semmModel == null) {
            System.out.println("WARNING:: Nothing to save!");
            return;
        }
        File outputDirectory = new File("target/data");
        if (!outputDirectory.exists())
            outputDirectory.mkdir();
        String outputFilename = "target/data/semm.ttl";
        System.out.printf("INFO:: Saving SEMM OWL Ontology to %s... ", outputFilename);
        Util.saveOWL(semmModel, outputFilename, SEMM.NS,
                FileUtils.guessLang(outputFilename, FileUtils.langTurtle));
        System.out.println("done!");

        outputFilename = "target/data/semm.owl";
        System.out.printf("INFO:: Saving SEMM OWL Ontology to %s... ", outputFilename);
        Util.saveOWL(semmModel, outputFilename, SEMM.NS,
                FileUtils.guessLang(outputFilename, FileUtils.langTurtle));
        System.out.println("done!");
    }
}
