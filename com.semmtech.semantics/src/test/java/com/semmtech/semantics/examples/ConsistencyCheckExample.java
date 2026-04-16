package com.semmtech.semantics.examples;

public class ConsistencyCheckExample {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
	}
	
	public static final String UNCLE_RULE = "[ruleUncle: (?f lem:fatherOf ?a) (?u lem:brotherOf ?f) -> (?u lem:uncleOf ?a) ]";
	public static final String DISJOINT_WITH_RULE = "[validationDisjointWith: (?X owl:disjointWith ?Y) (?v rb:validation on()), (?I rdf:type ?X ), (?I rdf:type ?Y ) -> (?I rb:violation error('conflict', 'Individual a member of disjoint classes', ?X ?Y ))]";
}
