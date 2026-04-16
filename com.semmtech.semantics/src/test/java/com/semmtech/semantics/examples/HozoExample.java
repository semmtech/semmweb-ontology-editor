package com.semmtech.semantics.examples;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.util.FileUtils;
import com.semmtech.semantics.semm.SEMMModel;

public class HozoExample {
	
	private static final String NS = "http://www.hozo.org/";
	
	public static void main(String[] args) {
		String lang = FileUtils.langTurtle;
		SEMMModel model = createHozoModel();
		
		CreateModelProgrammaticallyExample.printModel(model, lang);
		
	}
	
	/**
	 * In a context, a player (class constraint) can play a role concept and then becomes a role-holder.
	 * @return
	 */
	public static SEMMModel createHozoModel() {
		SEMMModel model = SEMMExample.createSEMMModel();
		CreateModelProgrammaticallyExample.addPrefix("hozo", NS, model);
		
		OntClass roleConcept = model.createClass(NS + "RoleConcept");
		roleConcept.addLabel("role concept", null);
		roleConcept.addComment("represents a role itself and is defined as a concept played by something", null);
		
		OntClass roleHolder = model.createClass(NS + "RoleHolder");
		roleHolder.addLabel("role holder", null);
		roleHolder.addLabel("role playing thing", null);
		roleHolder.addComment("an entity which is playing the role", null);
		
		OntClass potentialPlayer = model.createClass(NS + "PotentialPlayer");
		potentialPlayer.addLabel("potential player", null);
		potentialPlayer.addComment("thing which is able to play a role", null);
		
		return model;
	}
}
