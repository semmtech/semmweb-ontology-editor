package com.semmtech.semantics.examples;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;

public class SPARQLExample {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		OntModel model = CreateModelProgrammaticallyExample.createOntModel();
		String queryString = createQueryString();
		performSPARQLQuery(model, queryString);		
	}
	
	/**
	 * Performs the SPARQL query on the specified model.
	 * @param model
	 * @param queryString
	 */
	public static void performSPARQLQuery(Model model, String queryString) {
		
		/// TODO: Check how to use SPARQL in Jena 2.7	
		/// Not available in the Maven release of Jena....?
//		Query query = QueryFactory.create(queryString);

		// Execute the query and obtain results
//		QueryExecution exec = QueryExecutionFactory.create(query, model);
//		ResultSet results = exec.execSelect();

		// Output query results
//		ResultSetFormatter.out(System.out, results, query);

		// Important – free up resources used running the query
//		exec.close();
	}
	
	/**
	 * Creates a basic query string, which will just list all triples and their subject, predicate and object.
	 * @return
	 */
	public static String createQueryString() {
		String queryString = 	"PREFIX rdfs:    <http://www.w3.org/2000/01/rdf-schema#>" +
								"PREFIX owl:     <http://www.w3.org/2002/07/owl#>" +
								"PREFIX xsd:     <http://www.w3.org/2001/XMLSchema#>" +
								"PREFIX rdf:     <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
								"SELECT * " +
								"WHERE {" +
								"	?subject ?predicate ?object ." +
								"}";
		return queryString;
	}
}
