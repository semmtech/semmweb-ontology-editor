package com.semmtech.semantics.examples;


import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntDocumentManager;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.Ontology;
import com.hp.hpl.jena.ontology.ProfileRegistry;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ModelMaker;
import com.hp.hpl.jena.rdf.model.ModelReader;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.ReasonerFactory;
import com.hp.hpl.jena.reasoner.ReasonerRegistry;
import com.hp.hpl.jena.util.FileUtils;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;


public class ModelMakerExample {
    private static final Logger logger = Logger.getLogger(ModelMakerExample.class);

    public static void main(String[] args) {
        initLoggers();

        ModelMaker baseMaker = ModelFactory.createMemModelMaker();
        ModelMaker importsMaker = ModelFactory.createMemModelMaker();
        // Model owlModel = maker.createModel("owlModel");
        // owlModel.read(OWL.getURI());
        //
        // logger.debug("ontModel has " +
        // owlModel.listStatements().toList().size() + " statements");
        //
        // OntDocumentManager manager = new OntDocumentManager();
        // manager.setProcessImports(true);
        // OntModelSpec spec = new OntModelSpec(maker, maker, manager, null,
        // ProfileRegistry.OWL_LANG);
        // OntModel ontModel = ModelFactory.createOntologyModel(spec);
        // ontModel.read(OWL.getURI());
        //
        // logger.debug("ontModel has " +
        // ontModel.listStatements().toList().size() + " statements");
        //
        //
        // Model defaultModel = maker.createDefaultModel();
        // OntModel ontDefaultModel = ModelFactory.createOntologyModel(spec,
        // defaultModel);
        // for (String prefix : ontDefaultModel.getNsPrefixMap().keySet()) {
        // logger.debug("" + prefix + " = '" +
        // ontDefaultModel.getNsPrefixURI(prefix) +"'");
        // }
        // ontDefaultModel.addSubModel(maker.getModel(RDF.getURI(), new
        // ModelReader() {
        // @Override
        // public Model readModel(Model toRead, String URL) {
        // toRead.read(URL);
        // logger.debug("Model with URL '" + URL + "' has " +
        // toRead.listStatements().toList().size() + " statements");
        // return toRead;
        // }
        // }));
        // logger.debug("ontDefaultModel has " +
        // ontDefaultModel.listStatements().toList().size() + " statements");
        // for (String prefix : ontDefaultModel.getNsPrefixMap().keySet()) {
        // logger.debug("" + prefix + " = '" +
        // ontDefaultModel.getNsPrefixURI(prefix) +"'");
        // }
        // logger.info("ontDefaultModel::");
        // CreateModelProgrammaticallyExample.printModel(ontDefaultModel,
        // FileUtils.langTurtle);

        OntDocumentManager manager = new OntDocumentManager();
        manager.setProcessImports(true);
        OntModelSpec spec = new OntModelSpec(OntModelSpec.OWL_MEM);
        spec.setDocumentManager(manager);
        spec.setBaseModelMaker(baseMaker);
        spec.setImportModelMaker(importsMaker);

        ModelReader modelReader = new ModelReader() {
            @Override
            public Model readModel(Model toRead, String URL) {
                toRead.read(URL);
                return toRead;
            }
        };

        OntModel ontModel = ModelFactory.createOntologyModel(spec);

        ontModel.addSubModel(importsMaker.getModel(RDF.getURI(), modelReader));
        ontModel.addLoadedImport(RDF.getURI());
        ontModel.addSubModel(importsMaker.getModel(RDFS.getURI(), modelReader));
        ontModel.addLoadedImport(RDFS.getURI());
        ontModel.addSubModel(importsMaker.getModel(OWL.getURI(), modelReader));
        ontModel.addLoadedImport(OWL.getURI());

        logger.debug("ontModel has " + ontModel.listStatements().toList().size() + " statements");
        CreateModelProgrammaticallyExample.printModel(ontModel, FileUtils.langTurtle);

        spec.setReasonerFactory(null);
        spec.setReasoner(ReasonerRegistry.getRDFSReasoner());

        OntModel taxonomyModel = ModelFactory.createOntologyModel(spec, ontModel);
        OntClass resource = taxonomyModel.getOntClass(RDFS.Resource.getURI());
        for (OntClass subClass : resource.listSubClasses().toList()) {
            logger.debug("" + subClass.getURI());
        }

        int i = 0;
        for (String name : baseMaker.listModels().toList()) {
            logger.debug("[" + ++i + "] = '" + name + "'");
        }
        i = 0;
        for (String name : importsMaker.listModels().toList()) {
            logger.debug("[" + ++i + "] = '" + name + "'");
        }

    }

    public static void someMain(String[] args) {
        initLoggers();

        logger.info("ModelFactory.createDefaultModel()::");
        CreateModelProgrammaticallyExample.printModel(ModelFactory.createDefaultModel(),
                FileUtils.langTurtle);

        logger.info("ModelFactory.createOntologyModel()::");
        CreateModelProgrammaticallyExample.printModel(ModelFactory.createOntologyModel(),
                FileUtils.langTurtle);

        ModelMaker importsMaker = ModelFactory.createMemModelMaker();
        ReasonerFactory reasonerFactory = new ReasonerFactory() {

            @Override
            public String getURI() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public Model getCapabilities() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public Reasoner create(Resource arg0) {
                // TODO Auto-generated method stub
                return null;
            }
        };
        OntDocumentManager documentManager = new OntDocumentManager();
        documentManager.setProcessImports(true);

        ModelMaker baseMaker = ModelFactory.createMemModelMaker();
        Model model01 = baseMaker.createModel("model01");

        model01.setNsPrefix("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
        model01.setNsPrefix("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
        model01.setNsPrefix("owl", "http://www.w3.org/2002/07/owl#");
        model01.setNsPrefix("xsd", "http://www.w3.org/2001/XMLSchema#");
        model01.setNsPrefix("ex", "http://example.org/model01/");
        Resource mike = model01.createResource("http://example.org/model01/Mike");
        mike.addProperty(RDFS.label, "Mike Henrichs");

        logger.info("model01::");
        CreateModelProgrammaticallyExample.printModel(model01, FileUtils.langTurtle);

        Model model01copy = baseMaker.getModel("model01");
        logger.info("model01copy::");
        CreateModelProgrammaticallyExample.printModel(model01copy, FileUtils.langTurtle);

        model01copy.add(model01copy.getResource("http://example.org/model01/Mike"), RDFS.comment,
                "Dit is een comment voor Mike Henrichs!");

        logger.info("model01::");
        CreateModelProgrammaticallyExample.printModel(model01, FileUtils.langTurtle);

        OntModel ontModel01 = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM,
                baseMaker.getModel("model01"));

        logger.info("ontModel01::");
        CreateModelProgrammaticallyExample.printModel(ontModel01, FileUtils.langTurtle);

        // / Option 01
        // Ontology ontology =
        // ontModel01.createOntology("http://example.org/model01/");
        // ontology.addImport(ontModel01.createOntology("http://www.w3.org/1999/02/22-rdf-syntax-ns#"));

        // / Option 02
        Ontology ontology = ontModel01.createOntology("http://example.org/model01/");
        ontology.addProperty(OWL.imports,
                ontModel01.createResource("http://www.w3.org/1999/02/22-rdf-syntax-ns#"));

        logger.info("After ontology created in ontModel01::");
        CreateModelProgrammaticallyExample.printModel(ontModel01, FileUtils.langTurtle);

        OntModelSpec importSpec = new OntModelSpec(baseMaker, importsMaker, documentManager,
                reasonerFactory, ProfileRegistry.OWL_LANG);
        OntModel ontModelX = ModelFactory.createOntologyModel(importSpec,
                baseMaker.getModel("model01"));

        logger.debug("ontModelX.hasLoadedImport(RDF.getURI()) = "
                + ontModelX.hasLoadedImport(RDF.getURI()));
        logger.debug("ontModelX.hasLoadedImport(RDFS.getURI()) = "
                + ontModelX.hasLoadedImport(RDFS.getURI()));

        logger.debug("Injecting RDFS into model...");
        Model rdfsModel = importsMaker.createModel(RDFS.getURI(), true);
        rdfsModel.read(RDFS.getURI());
        logger.info("rdfsModel::");
        CreateModelProgrammaticallyExample.printModel(rdfsModel, FileUtils.langTurtle);

        logger.debug("importsMaker.getModel(RDFS.getURI())...");
        ontModelX.addSubModel(importsMaker.getModel(RDFS.getURI()));
        logger.debug("ontModelX.hasLoadedImport(RDFS.getURI()) = "
                + ontModelX.hasLoadedImport(RDFS.getURI()));

        logger.info("ontModelX::");
        CreateModelProgrammaticallyExample.printModel(ontModelX, FileUtils.langTurtle);

        logger.info("ontModelX statements::");
        CreateModelProgrammaticallyExample.printStatements(ontModelX);

        Model tempModel = baseMaker.createModel("tempModel");
        tempModel.read(RDFS.getURI());
        logger.info("tempModel (RDFS) = " + tempModel.listStatements().toList().size()
                + " statements ::");
        CreateModelProgrammaticallyExample.printModel(tempModel, FileUtils.langTurtle);

        tempModel.read(RDF.getURI());
        logger.info("tempModel (RDF) = " + tempModel.listStatements().toList().size()
                + " statements ::");
        CreateModelProgrammaticallyExample.printModel(tempModel, FileUtils.langTurtle);

        logger.info("[Done]");
    }

    private static void initLoggers() {
        Properties log4jProperties = new Properties();
        log4jProperties.put("log4j.rootLogger", "DEBUG, CA1");

        log4jProperties.put("log4j.appender.CA1", "org.apache.log4j.ConsoleAppender");
        log4jProperties.put("log4j.appender.CA1.layout", "org.apache.log4j.PatternLayout");
        log4jProperties.put("log4j.appender.CA1.layout.ConversionPattern",
                "%-4r [%t] %-5p %c %x - %m%n");

        PropertyConfigurator.configure(log4jProperties);
    }
}
