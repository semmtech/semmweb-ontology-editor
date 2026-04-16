package com.semmtech.semantics.examples;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.util.FileUtils;
import com.semmtech.semantics.model.ExtendedModelFactory;
import com.semmtech.semantics.skos.Concept;
import com.semmtech.semantics.skos.ConceptScheme;
import com.semmtech.semantics.skos.DictionaryModel;

public class SKOSExample {

	/**
	 * Check out http://code.google.com/p/lucene-skos/wiki/SKOSThesauri
	 * And also http://www.w3.org/2001/sw/wiki/SKOS/Datasets
	 * @param args
	 */
	public static void main(String[] args) {
		//DictionaryModel model = readDictionaryModel("src/test/resources/SKOS/skos-example.ttl");
		DictionaryModel model = readDictionaryModel("src/test/resources/SKOS/artists.ttl");
//		CreateModelProgrammaticallyExample.printModel(model, FileUtils.langTurtle);
		
		System.out.println("\n-----\n");
		System.out.println("Hierarchical:");
		for (ConceptScheme scheme : model.listConceptSchemes().toList()) {
			System.out.println("scheme.getURI() = " + scheme.getURI());
			for (Concept top : scheme.listTopConcepts().toList()) {
				System.out.println("\ttop.getURI() = " + top.getURI());
//				for (Literal literal : top.listPrefLabels().toList()) {
//					System.out.println("\ttop prefLabel = " + literal.toString());	
//				}
//				System.out.println("\ttop prefLabel('en') = " + top.getPrefLabel("en"));
//				System.out.println("\ttop prefLabel('nl') = " + top.getPrefLabel("nl"));
//				System.out.println("\ttop prefLabel(null) = " + top.getPrefLabel(null));
//				for (Literal literal : top.listAltLabels().toList()) {
//					System.out.println("\ttop altLabel = " + literal.toString());	
//				}
				System.out.println("\tNarrower:");
//				for (Concept narrower : top.listNarrowerConcepts().toList()) {
//					System.out.println("\t\tnarrower.getURI() = " + narrower.getURI());	
//					for (Literal literal : narrower.listPrefLabels().toList()) {
//						System.out.println("\t\tnarrower prefLabel = " + literal.toString());	
//					}
//					System.out.println("\t\tnarrower prefLabel('en') = " + narrower.getPrefLabel("en"));
//					System.out.println("\t\tnarrower prefLabel('nl') = " + narrower.getPrefLabel("nl"));
//					System.out.println("\t\tnarrower prefLabel(null) = " + narrower.getPrefLabel(null));
//					for (Literal literal : narrower.listAltLabels().toList()) {
//						System.out.println("\ttop altLabel = " + literal.toString());	
//					}					
//				}
//				System.out.println("\tBroader:");
//				for (Concept narrower : top.listBroaderConcepts().toList()) {
//					System.out.println("\t\tnarrower.getURI() = " + narrower.getURI());	
//					for (Literal literal : narrower.listPrefLabels().toList()) {
//						System.out.println("\t\tnarrower prefLabel = " + literal.toString());	
//					}
//					System.out.println("\t\tnarrower prefLabel('en') = " + narrower.getPrefLabel("en"));
//					System.out.println("\t\tnarrower prefLabel('nl') = " + narrower.getPrefLabel("nl"));
//					System.out.println("\t\tnarrower prefLabel(null) = " + narrower.getPrefLabel(null));
//					for (Literal literal : narrower.listAltLabels().toList()) {
//						System.out.println("\ttop altLabel = " + literal.toString());	
//					}					
//				}
//				System.out.println("\tRelated:");
//				for (Concept narrower : top.listRelatedConcepts().toList()) {
//					System.out.println("\t\tnarrower.getURI() = " + narrower.getURI());	
//					for (Literal literal : narrower.listPrefLabels().toList()) {
//						System.out.println("\t\tnarrower prefLabel = " + literal.toString());	
//					}
//					System.out.println("\t\tnarrower prefLabel('en') = " + narrower.getPrefLabel("en"));
//					System.out.println("\t\tnarrower prefLabel('nl') = " + narrower.getPrefLabel("nl"));
//					System.out.println("\t\tnarrower prefLabel(null) = " + narrower.getPrefLabel(null));
//					for (Literal literal : narrower.listAltLabels().toList()) {
//						System.out.println("\ttop altLabel = " + literal.toString());	
//					}					
//				}
			}
		}
		
		System.out.println("\n-----\n");
		System.out.println("Alphabetical:");
		for (Concept concept : model.listConcepts().toList()) {			
			System.out.println("\ttop.getURI() = " + concept.getURI());
//			for (Literal literal : concept.listPrefLabels().toList()) {
//				System.out.println("\ttop prefLabel = " + literal.toString());	
//			}
//			System.out.println("\ttop prefLabel('en') = " + concept.getPrefLabel("en"));
//			System.out.println("\ttop prefLabel('nl') = " + concept.getPrefLabel("nl"));
//			System.out.println("\ttop prefLabel(null) = " + concept.getPrefLabel(null));
//			for (Literal literal : concept.listAltLabels().toList()) {
//				System.out.println("\ttop altLabel = " + literal.toString());	
//			}
//			System.out.println("\tNarrower:");
//			for (Concept narrower : concept.listNarrowerConcepts().toList()) {
//				System.out.println("\t\tnarrower.getURI() = " + narrower.getURI());	
//				for (Literal literal : narrower.listPrefLabels().toList()) {
//					System.out.println("\t\tnarrower prefLabel = " + literal.toString());	
//				}
//				System.out.println("\t\tnarrower prefLabel('en') = " + narrower.getPrefLabel("en"));
//				System.out.println("\t\tnarrower prefLabel('nl') = " + narrower.getPrefLabel("nl"));
//				System.out.println("\t\tnarrower prefLabel(null) = " + narrower.getPrefLabel(null));
//				for (Literal literal : narrower.listAltLabels().toList()) {
//					System.out.println("\ttop altLabel = " + literal.toString());	
//				}					
//			}
//			System.out.println("\tBroader:");
//			for (Concept narrower : concept.listBroaderConcepts().toList()) {
//				System.out.println("\t\tnarrower.getURI() = " + narrower.getURI());	
//				for (Literal literal : narrower.listPrefLabels().toList()) {
//					System.out.println("\t\tnarrower prefLabel = " + literal.toString());	
//				}
//				System.out.println("\t\tnarrower prefLabel('en') = " + narrower.getPrefLabel("en"));
//				System.out.println("\t\tnarrower prefLabel('nl') = " + narrower.getPrefLabel("nl"));
//				System.out.println("\t\tnarrower prefLabel(null) = " + narrower.getPrefLabel(null));
//				for (Literal literal : narrower.listAltLabels().toList()) {
//					System.out.println("\ttop altLabel = " + literal.toString());	
//				}					
//			}
//			System.out.println("\tRelated:");
//			for (Concept narrower : concept.listRelatedConcepts().toList()) {
//				System.out.println("\t\tnarrower.getURI() = " + narrower.getURI());	
//				for (Literal literal : narrower.listPrefLabels().toList()) {
//					System.out.println("\t\tnarrower prefLabel = " + literal.toString());	
//				}
//				System.out.println("\t\tnarrower prefLabel('en') = " + narrower.getPrefLabel("en"));
//				System.out.println("\t\tnarrower prefLabel('nl') = " + narrower.getPrefLabel("nl"));
//				System.out.println("\t\tnarrower prefLabel(null) = " + narrower.getPrefLabel(null));
//				for (Literal literal : narrower.listAltLabels().toList()) {
//					System.out.println("\ttop altLabel = " + literal.toString());	
//				}					
//			}
		}
	}
	
	public static DictionaryModel createDictionaryModel() {
		DictionaryModel model = ExtendedModelFactory.createDictionaryModel(OntModelSpec.OWL_MEM);
		return model;
	}
	
	public static DictionaryModel readDictionaryModel(String filename) {
		DictionaryModel model = ExtendedModelFactory.createDictionaryModel(OntModelSpec.OWL_MEM);
		try {
			model.read(new FileInputStream(new File(filename)), null, FileUtils.langTurtle);
		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}		
		return model;
	}

}
