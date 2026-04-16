package com.semmtech.plugin.semmweb.editor;

import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.hp.hpl.jena.ontology.OntDocumentManager;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

public class Program {
	static final Logger logger = Logger.getLogger(Program.class);
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		configureLoggers();
		
		OntModelSpec spec = new OntModelSpec(OntModelSpec.OWL_MEM);
		OntDocumentManager manager = new OntDocumentManager();
		spec.setDocumentManager(manager);
		
		OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
		model.read(OWL.getURI());
		manager.loadImport(model, RDF.getURI());
		manager.loadImport(model, RDFS.getURI());
		
		model.setNsPrefix("", "http://www.semmtech.com/semm#");		
		Resource huis = model.createResource("http://www.semmtech.com/semm#Huis");
		model.add(model.createStatement(huis, RDFS.label, model.createLiteral("huis", "nl")));
		
		logger.debug(String.format("Base model '%s' contains %d statements", OWL.getURI(), model.getBaseModel().listStatements().toList().size()));
		logger.debug(String.format("Model contains %d statements", model.listStatements().toList().size()));
		
//		String[] uris = new String[] { 
//				"http://www.w3.org/2002/07/owl#Thing",
//				"http://www.w3.org/2002/07/owl#",
//				"http://www.w3.org/2002/07/owl/",
//				"http://www.w3.org/2002/07/owl",
//				"http://www.w3.org/2002/07/owl#Onbekend",
//				"http://www.unknown.org/2002/07/owl#Onbekend",
//				"http://www.semmtech.com/semm#",
//				"http://www.semmtech.com/semm#subjectRole",
//				"http://www.semmtech.com/semm/subjectRole",
//				"http://www.semmtech.com/semm/Huis",
//				"file:D://mappings.xml",
//				"Huis",
//				":Huis",
//				"owl:Thing",
//				"owl:Onbekend",
//				"unknown:Something",
//				"Zomaar",
//				"Dit is een zin",
//				"Met|vage;tekens_erin=niet!OK?",
//				"Spatie Spatie",
//				"Underscore_U",
//				"Comma,comma",
//				"Colon:colon",
//				"Semicolon;semicolon",
//				"Is=Is",
//				"Greater>greater",
//				"Smaller<smaller",
//				"Hyphen-hyphen",
//				"Brackets()Brackets",
//				"112345",
//				"Plus+-minus",
//				"Dollar$",
//				"Apenstaart@",
//				
//				""
//		};
	}

	/**
	 * Configure the loggers used in this Program.
	 */
	private static void configureLoggers() {
		Properties log4jProperties = new Properties();
		log4jProperties.put("log4j.rootLogger", "DEBUG, CA");
		log4jProperties.put("log4j.logger.com.semmtech.plugin.semmweb.editor.dialog", "OFF");
		log4jProperties.put("log4j.appender.CA", "org.apache.log4j.ConsoleAppender");
		log4jProperties.put("log4j.appender.CA.layout", "org.apache.log4j.PatternLayout");
		log4jProperties.put("log4j.appender.CA.layout.ConversionPattern", "%m%n");		
		PropertyConfigurator.configure(log4jProperties);
	}
}
