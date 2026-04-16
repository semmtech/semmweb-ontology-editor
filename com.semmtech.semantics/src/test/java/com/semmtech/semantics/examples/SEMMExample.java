package com.semmtech.semantics.examples;


import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.Restriction;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.util.FileUtils;
import com.semmtech.semantics.model.ExtendedModelFactory;
import com.semmtech.semantics.ontology.ExtendedModelSpec;
import com.semmtech.semantics.semm.Aspect;
import com.semmtech.semantics.semm.PhysicalObject;
import com.semmtech.semantics.semm.SEMMModel;
import com.semmtech.semantics.vocabulary.Gellish;


public class SEMMExample {

    private static final String NS = "http://www.test.com/";

    /**
     * @param args
     */
    @SuppressWarnings("unused")
    public static void main(String[] args) {
        SEMMModel model = createSEMMModel();
        String lang = FileUtils.langTurtle; // / Turtle encoding

        // / Add the 'test' prefix with the given namespace
        CreateModelProgrammaticallyExample.addPrefix("test", NS, model);

        Property relation = model.createProperty(NS + "relation");
        OntClass physicalObject = model.createClass(Gellish.PhysicalObject.getURI());
        physicalObject.addSuperClass(model.createAllValuesFromRestriction(null, Gellish.hasAspect,
                Gellish.Aspect));

        // / Physical Objects
        PhysicalObject pump = model.createPhysicalObject(NS + "Pump");
        PhysicalObject beetle = model.createPhysicalObject(NS + "Beetle");
        PhysicalObject highPressurePump = model.createPhysicalObject(NS + "HighPressurePump");
        highPressurePump.addSuperClass(pump);
        pump.addLabel("pump", "en");

        // / Aspects
        Aspect pressure = model.createAspect(NS + "Pressure");
        Aspect colour = model.createAspect(NS + "Colour");
        Aspect length = model.createAspect(NS + "Length");

        // / QCR

        pump.addAspect(length, false);
        pump.addAspect(colour, true);
        pump.addSuperClass(model
                .createMinCardinalityQRestriction(null, relation, 0, physicalObject));

        // / Individuals
        Individual p101 = model.createIndividual(NS + "P101", pump);
        Individual threeMeter = model.createIndividual(NS + "_3_m", pump);
        Individual tbtl85 = model.createIndividual(NS + "TBTL85", beetle);

        // / Print the model to System.out
        CreateModelProgrammaticallyExample.printModel(model, lang);

        System.out.println("\n---\nRestrictions:");
        for (Restriction r : model.listRestrictions().toList()) {
            System.out.println(" - " + r.toString());
        }
        System.out.println("\n---\npump.listAspects:");
        for (Object a : pump.listAspects().toList()) {
            System.out.println("Aspect = " + a.toString());
        }

        // / Write the model to disk
        ReadTurtleFileExample.writeModelToFile(model, "src/test/resources/SEMM/test.ttl", lang);
    }

    /**
     * Creates a new empty SEMMNModel ontology model.
     * 
     * @return
     */
    public static SEMMModel createSEMMModel() {
        // / Model specification intended for SEMM(web) use
        ExtendedModelSpec spec = ExtendedModelSpec.SEMM_MEM;

        // / Create an empty SEMM model, with a SEMM web specification
        SEMMModel model = ExtendedModelFactory.createSEMMModel(spec);

        return model;
    }
}
