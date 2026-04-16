package com.semmtech.semantics.sparql;


import org.openrdf.model.vocabulary.RDF;

import com.hp.hpl.jena.ontology.OntDocumentManager;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.ProfileRegistry;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ModelMaker;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.reasoner.ReasonerFactory;
import com.hp.hpl.jena.reasoner.ReasonerRegistry;
import com.hp.hpl.jena.reasoner.rulesys.RDFSRuleReasonerFactory;
import com.hp.hpl.jena.util.FileUtils;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDFS;


public class GraphsSparqlExample {

    /**
     * @param args
     */
    @SuppressWarnings("unused")
    public static void main(String[] args) {
        OntDocumentManager manager = new OntDocumentManager();
        manager.setProcessImports(false);
        OntModelSpec spec = OntModelSpec.OWL_MEM;
        spec.setDocumentManager(manager);

        OntModel baseModel = ModelFactory.createOntologyModel(spec);
        baseModel.setNsPrefix("lemantle", "http://lemantle.com/ontology/");
        Resource person = baseModel
                .createResource("http://lemantle.com/ontology/Person", OWL.Class);
        Resource man = baseModel.createResource("http://lemantle.com/ontology/Man", OWL.Class);
        Resource physicalObject = baseModel.createResource(
                "http://lemantle.com/ontology/PhysicalObject", OWL.Class);
        Resource car = baseModel.createResource("http://lemantle.com/ontology/Car", physicalObject);
        Resource vw = baseModel.createResource("http://lemantle.com/ontology/VolkswagenBeetle",
                OWL.Class);
        Resource tbtl83 = baseModel.createResource("http://lemantle.com/ontology/TBTL83", vw);
        Resource mike = baseModel.createResource("http://lemantle.com/ontology/MikeHenrichs", man);

        Model rdfModel = ModelFactory.createOntologyModel(spec);
        rdfModel.read("src/test/resources/imports/rdf.ttl", FileUtils.langTurtle);
        Model rdfsModel = ModelFactory.createOntologyModel(spec);
        rdfsModel.read("src/test/resources/imports/rdfs.ttl", FileUtils.langTurtle);
        Model owlModel = ModelFactory.createOntologyModel(spec);
        owlModel.read("src/test/resources/imports/owl.ttl", FileUtils.langTurtle);

        baseModel.addSubModel(rdfModel);
        baseModel.addLoadedImport(RDF.NAMESPACE);
        baseModel.addSubModel(rdfsModel);
        baseModel.addLoadedImport(RDFS.getURI());
        baseModel.addSubModel(owlModel);
        baseModel.addLoadedImport(OWL.NS);

        baseModel.write(System.out, FileUtils.langTurtle);

        System.out.println("----");

        ReasonerFactory factory = ReasonerRegistry.theRegistry().getFactory(
                RDFSRuleReasonerFactory.URI);
        ModelMaker maker = ModelFactory.createMemModelMaker();
        spec = new OntModelSpec(maker, maker, manager, factory, ProfileRegistry.OWL_LANG);
        OntModel infModel = ModelFactory.createOntologyModel(spec, baseModel);
        infModel.write(System.out, FileUtils.langTurtle);

        String sparql = "PREFIX owl: <" + OWL.NS
                + "> SELECT DISTINCT ?individual WHERE { ?individual a owl:Thing . }";
        Query query = QueryFactory.create(sparql);
        QueryExecution execution = QueryExecutionFactory.create(query, infModel);
        ResultSet result = execution.execSelect();

        System.out.println("----");

        ResultSetFormatter.out(System.out, result);
    }
}
