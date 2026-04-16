package com.semmtech.semantics.examples;

import java.util.Map;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.util.FileUtils;
import com.hp.hpl.jena.vocabulary.OWL2;
import com.hp.hpl.jena.vocabulary.XSD;

public class CreateModelProgrammaticallyExample {

	public static final String PREFIX = "semm";
	public static final String NS = "http://www.semmweb.com/";
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Model model = createOntModelOWL2();
		printModel(model, FileUtils.langTurtle);
	}

	/**
	 * Create a simpole RDF model.
	 * @return
	 */
	public static Model createRDFModel() {
		Model model = ModelFactory.createDefaultModel();
		addPrefix(PREFIX, NS, model);
		
		model.createResource(NS + "Person");
			
		return model;		
	}
	
	/**
	 * Adds the provided namespace-prefix pair to the model.
	 * @param prefix
	 * @param namespace
	 * @param model
	 */
	public static void addPrefix(String prefix, String namespace, Model model) {		
		Map<String, String> prefixes = model.getNsPrefixMap();
		prefixes.put(prefix, namespace);
		model.setNsPrefixes(prefixes);
	}
	
	public static OntModel createOntModel() {
		OntModelSpec spec = OntModelSpec.OWL_MEM;
		
		OntModel model = ModelFactory.createOntologyModel(spec);
		
		addPrefix(PREFIX, NS, model);
		
		/// Create some classes
		OntClass human = model.createClass(NS + "Human");
		OntClass person = model.createClass(NS + "Person");
		person.addEquivalentClass(human);
		OntClass man = model.createClass(NS + "Man");
		man.addSuperClass(human);
		OntClass woman = model.createClass(NS + "Woman");
		woman.addSuperClass(human);
		
		/// Create some properties
		/// By using the createOntProperty instead of createProperty, an OntProperty object is
		/// returned which overs more API functionality for adding additional triples to the property
		/// e.g. addSubPropertyOf, addDomain, etc.
		OntProperty hasWife = model.createOntProperty(NS + "hasWife");
		OntProperty hasSpouse = model.createOntProperty(NS + "hasWife");
		hasWife.addSuperProperty(hasSpouse);
		hasWife.addDomain(man); 
		hasWife.addRange(woman);
		
		OntProperty fatherOf = model.createOntProperty(NS + "fatherOf");
		OntProperty brotherOf = model.createOntProperty(NS + "brotherOf");
		OntProperty familyOf = model.createOntProperty(NS + "familyOf");
		familyOf.addSubProperty(fatherOf);
		familyOf.addSubProperty(brotherOf);
		
		/// Create an instance of the Person class
		Individual john = model.createIndividual(NS + "John", person);
		Individual johannes = model.createIndividual(NS + "Johannes", person);
		john.addSameAs(johannes);
		
		Individual mary = model.createIndividual(NS + "Mary", person);
		Individual lucy = model.createIndividual(NS + "Lucy", person);
		mary.addRDFType(woman);
		lucy.addDifferentFrom(mary);
		john.addProperty(hasWife, mary);		
		
		/// Datatypes
		OntProperty hasAge = model.createDatatypeProperty(NS + "hasAge");
		hasAge.addDomain(person);
		hasAge.addRange(XSD.nonNegativeInteger);
		john.addProperty(hasAge, model.createTypedLiteral(51));				
		
		return model;
	}
	
	/**
	 * This method creates a basic OWL Ontology with a number of classes, properties, restrictions and individuals.
	 * Example is created from the OWL2 Primer document on w3.org (http://www.w3.org/TR/owl-primer)
	 * @return
	 */
	public static OntModel createOntModelOWL2() {
		OntModelSpec spec = OntModelSpec.OWL_MEM;
		
		OntModel model = ModelFactory.createOntologyModel(spec);
		
		addPrefix(PREFIX, NS, model);
		
		/// Create some classes
		OntClass human = model.createClass(NS + "Human");
		OntClass person = model.createClass(NS + "Person");
		person.addEquivalentClass(human);
		OntClass man = model.createClass(NS + "Man");
		man.addSuperClass(human);
		OntClass woman = model.createClass(NS + "Woman");
		woman.addSuperClass(human);
		
		/// Class Disjointness from OWL 2 (anonymous class)
		Resource a = model.createResource(OWL2.AllDisjointClasses);
		a.addProperty(OWL2.members, model.createList(new RDFNode[] { man, woman } ));
				
		/// Create some properties
		/// By using the createOntProperty instead of createProperty, an OntProperty object is
		/// returned which overs more API functionality for adding additional triples to the property
		/// e.g. addSubPropertyOf, addDomain, etc.
		OntProperty hasWife = model.createOntProperty(NS + "hasWife");
		OntProperty hasSpouse = model.createOntProperty(NS + "hasWife");
		hasWife.addSuperProperty(hasSpouse);
		hasWife.addDomain(man); 
		hasWife.addRange(woman);
		
		/// QCR
		model.createMaxCardinalityQRestriction(null, hasWife, 1, woman);
		
		OntProperty fatherOf = model.createOntProperty(NS + "fatherOf");
		OntProperty brotherOf = model.createOntProperty(NS + "brotherOf");
		OntProperty familyOf = model.createOntProperty(NS + "familyOf");
		familyOf.addSubProperty(fatherOf);
		familyOf.addSubProperty(brotherOf);
		
		/// Create an instance of the Person class
		Individual john = model.createIndividual(NS + "John", person);
		Individual johannes = model.createIndividual(NS + "Johannes", person);
		john.addSameAs(johannes);
		
		Individual mary = model.createIndividual(NS + "Mary", person);
		Individual lucy = model.createIndividual(NS + "Lucy", person);
		mary.addRDFType(woman);
		lucy.addDifferentFrom(mary);
		john.addProperty(hasWife, mary);
		
		/// NegativePropertyAssertion (Bill does not have Lucy as wife)
		/// Negative property assertions provide a unique opportunity to make statements where we know something that is not true. This kind of information is particularly important in OWL where the default stance is that anything is possible until you say otherwise.
		Resource n = model.createResource(OWL2.NegativePropertyAssertion);
		n.addProperty(OWL2.sourceIndividual, john);
		n.addProperty(OWL2.assertionProperty, hasWife);
		n.addProperty(OWL2.targetIndividual, lucy);

		
		/// Datatypes
		OntProperty hasAge = model.createDatatypeProperty(NS + "hasAge");
		hasAge.addDomain(person);
		hasAge.addRange(XSD.nonNegativeInteger);
		john.addProperty(hasAge, model.createTypedLiteral(51));
		Resource m = model.createResource(OWL2.NegativePropertyAssertion);
		m.addProperty(OWL2.sourceIndividual, john);
		m.addProperty(OWL2.assertionProperty, hasAge);
		m.addProperty(OWL2.targetValue, model.createTypedLiteral(53));
		
		
		/// Advanced Class Relations
		OntClass parent = model.createClass(NS + "Parent");
		OntClass mother = model.createClass(NS + "Mother");
		mother.addEquivalentClass(model.createIntersectionClass(null, model.createList(new RDFNode[] { woman, parent  } )));		
		
		return model;
	}
	
	/**
	 * Prints the given model to the System.out using the provided language (e.g. FileUtils.langTurtle).
	 * @param model
	 * @param lang
	 */
	public static void printModel(Model model, String lang) {
		model.write(System.out, lang, null);
	}
	
	public static void printStatements(Model model) {
		int i = 0;
		for (Statement stmt : model.listStatements().toList()) {
			System.out.println(String.format("[%d] = {%s}", ++i, stmt.toString()));
		}
	}
	
	public static void printStatements(OntModel model) {
		int i = 0;
		for (Statement stmt : model.listStatements().toList()) {
			System.out.println(String.format("[%d] (%s) = {%s}", ++i, model.isInBaseModel(stmt) ? "in base": "imported", stmt.toString()));
		}
	}
}
