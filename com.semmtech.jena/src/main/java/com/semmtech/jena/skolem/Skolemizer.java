package com.semmtech.jena.skolem;


import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.UUID;

import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;

import com.google.common.collect.Maps;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.rdf.model.AnonId;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.update.UpdateAction;
import com.hp.hpl.jena.update.UpdateFactory;
import com.hp.hpl.jena.update.UpdateRequest;
import com.hp.hpl.jena.vocabulary.RDF;
import com.semmtech.jena.vocabulary.Skolem;


public class Skolemizer {
    public static final String DEFAULT_GENERATED_NS = "http://www.semmweb.com/ns/.well-known/genid/";

    private final SimpleSelector skolemizable = new SimpleSelector() {
        public boolean selects(Statement s) {
            if (s.getSubject().isAnon()) {
                return true;
            }
            if (s.getObject().isAnon()) {
                return true;
            }

            return false;
        }
    };

    private final SimpleSelector skolemizeIgnore = new SimpleSelector() {
        public boolean selects(Statement s) {
            if (Skolem.iri.equals(s.getPredicate())) {
                return true;
            }
            return false;
        }
    };

    private final SimpleSelector all = new SimpleSelector() {
        public boolean selects(Statement s) {
            return true;
        }
    };

    private final SimpleSelector deskolemizable = new SimpleSelector() {

        public boolean selects(Statement s) {
            if (s.getSubject().hasProperty(RDF.type, Skolem.Skolemized)) {
                return true;
            }
            if (genidNamespace.equals(s.getSubject().getNameSpace())) {
                return true;
            }
            if (s.getObject().isLiteral()) {
                return false;
            }
            if (s.getObject().asResource().hasProperty(RDF.type, Skolem.Skolemized)) {
                return true;
            }
            if (genidNamespace.equals(s.getObject().asResource().getNameSpace())) {
                return true;
            }
            return false;
        }
    };

    private static SimpleSelector deskolemizeIgnore = new SimpleSelector() {
        public boolean selects(Statement s) {
            if (Skolem.bnodeId.equals(s.getPredicate())) {
                return true;
            }
            if (s.getObject().isLiteral()) {
                return false;
            }
            if (Skolem.Skolemized.getURI().equals(s.getObject().asResource().getURI())) {
                return true;
            }
            return false;
        }
    };

    private final Map<AnonId, Resource> skolemizedResources;
    private final Map<String, Resource> anonymousResources;
    private final String genidNamespace;

    private boolean keepIds = true;
    private boolean keepIris = true;

    public Skolemizer() {
        this(DEFAULT_GENERATED_NS);
    }

    private Skolemizer(String genidNamespace) {
        this.genidNamespace = genidNamespace;
        skolemizedResources = Maps.newHashMap();
        anonymousResources = Maps.newHashMap();
    }

    public void setKeepSkolemIRIs(boolean keepIris) {
        this.keepIris = keepIris;
    }

    public void setKeepAnonIds(boolean keepIds) {
        this.keepIds = keepIds;
    }

    public Model skolemize(Model model) {
        return skolemize(model, false);
    }

    public Model deskolemize(Model model) {
        Model result = ModelFactory.createDefaultModel();
        result.setNsPrefixes(model.getNsPrefixMap());

        anonymousResources.clear();

        for (StmtIterator statements = model.listStatements(null, null, (RDFNode) null); statements
                .hasNext();) {
            Statement stmt = statements.next();
            if (deskolemizeIgnore.selects(stmt)) {
                continue;
            }
            if (!deskolemizable.selects(stmt)) {
                result.add(stmt);
                continue;
            }
            Property predicate = stmt.getPredicate();
            Resource subject = stmt.getSubject();
            RDFNode object = stmt.getObject();

            if (subject.hasProperty(RDF.type, Skolem.Skolemized)
                    || genidNamespace.equals(subject.getNameSpace())) {
                subject = anonymizeResource(subject, result);
            }
            if (object.isResource()) {
                Resource objectResource = object.asResource();
                if (objectResource.hasProperty(RDF.type, Skolem.Skolemized)
                        || genidNamespace.equals(objectResource.getNameSpace())) {
                    object = anonymizeResource(objectResource, result);
                }
            }
            result.add(subject, predicate, object);
        }

        return result;
    }

    private Resource skolemizeResource(Resource resource, Model model) {
        if (!resource.isAnon()) {
            return resource;
        }
        AnonId id = resource.getId();
        if (!skolemizedResources.containsKey(id)) {
            String skolemIri = null;
            if (resource.hasProperty(Skolem.iri)) {
                skolemIri = resource.getProperty(Skolem.iri).getString();
            }
            else {
                skolemIri = String.format("%ss%s", genidNamespace, UUID.randomUUID().toString());
            }
            Resource skolemized = model.createResource(skolemIri);
            skolemized.addProperty(RDF.type, Skolem.Skolemized);
            if (keepIds) {
                skolemized.addProperty(Skolem.bnodeId, model.createLiteral(id.toString()));
            }
            skolemizedResources.put(id, skolemized);
            return skolemized;
        }
        return skolemizedResources.get(id);
    }

    private Resource anonymizeResource(Resource resource, Model model) {
        if (resource.isAnon()) {
            return resource;
        }
        String skolemIri = resource.getURI();
        if (!anonymousResources.containsKey(skolemIri)) {
            AnonId id = null;
            if (resource.hasProperty(Skolem.bnodeId)) {
                String anonId = resource.getProperty(Skolem.bnodeId).getString();
                id = new AnonId(anonId);
            }
            Resource anonymous = null;
            if (id != null) {
                anonymous = model.createResource(id);
            }
            else {
                anonymous = model.createResource();
            }
            if (keepIris) {
                anonymous.addProperty(Skolem.iri, model.createLiteral(skolemIri));
            }
            anonymousResources.put(skolemIri, anonymous);
            return anonymous;
        }

        return anonymousResources.get(skolemIri);
    }

    public static void readSkolemized(Model model, InputStream in, Lang lang, boolean keepIris) {
        Model skolemized = ModelFactory.createDefaultModel();
        RDFDataMgr.read(skolemized, in, lang);
        Skolemizer skolemizer = new Skolemizer();
        skolemizer.setKeepSkolemIRIs(keepIris);
        model.add(skolemizer.deskolemize(skolemized));
    }

    public static void writeSkolemized(OutputStream out, Model model, Lang lang, boolean keepIds) {
        Skolemizer skolemizer = new Skolemizer();
        skolemizer.setKeepAnonIds(keepIds);
        Model skolemized = skolemizer.skolemize(model);
        RDFDataMgr.write(out, skolemized, lang);
    }

    public Model skolemize(Model model, boolean removeUnused) {
        Model result = ModelFactory.createDefaultModel();
        result.setNsPrefixes(model.getNsPrefixMap());
        if (!result.getNsPrefixMap().containsKey("genid")) {
            result.setNsPrefix("genid", genidNamespace);
        }
        if (!result.getNsPrefixMap().containsKey("skolem")) {
            result.setNsPrefix("skolem", Skolem.NS);
        }
        skolemizedResources.clear();

        for (StmtIterator statements = model.listStatements(all); statements.hasNext();) {
            Statement stmt = statements.next();
            if (skolemizeIgnore.selects(stmt)) {
                continue;
            }
            if (!skolemizable.selects(stmt)) {
                result.add(stmt);
                continue;
            }

            Property predicate = stmt.getPredicate();
            Resource subject = stmt.getSubject();
            RDFNode object = stmt.getObject();
            if (subject.isAnon()) {
                subject = skolemizeResource(subject, result);
            }
            if (object.isResource()) {
                Resource objectResource = object.asResource();
                if (objectResource.isAnon()) {
                    object = skolemizeResource(objectResource, result);
                }
            }
            result.add(subject, predicate, object);
        }

        if (removeUnused) {
            removeUnused(result);
        }
        return result;
    }

    /**
     * Returns a model containing skolemization support triples stored within a
     * deskolemized model.
     * 
     * @param deskolemized
     * @return
     */
    public static void extractSkolemData(Model deskolemized, Model anonymous, Model skolemData) {
        if (deskolemized == null) {
            throw new IllegalArgumentException("Deskolemized model cannot be null");
        }
        if (anonymous != null) {
            // @formatter:off
            String sparql = "PREFIX skolem: <" + Skolem.NS+ "> "
                    + "CONSTRUCT { ?s ?p ?o } "
                    + "WHERE { "
                    + "     ?s ?p ?o . "    
                    + "     FILTER ( ?p != skolem:iri && ?p != skolem:bnodeId ) "
                    + "} ";
            // @formatter:on
            QueryExecution exec = QueryExecutionFactory.create(sparql, deskolemized);
            anonymous.add(exec.execConstruct());
        }
        if (skolemData != null) {
            // @formatter:off
            String sparql = "PREFIX skolem: <" + Skolem.NS+ "> "
                    + "CONSTRUCT { ?s ?p ?o } "
                    + "WHERE { "
                    + "     { "
                    + "         ?s skolem:iri ?o . "
                    + "         BIND(skolem:iri AS ?p) ."
                    + "     } UNION { "
                    + "         ?s skolem:bnodeId ?o ."
                    + "         BIND(skolem:bnodeId AS ?p) ."
                    + "     } "
                    + "} ";
            // @formatter:on
            QueryExecution exec = QueryExecutionFactory.create(sparql, deskolemized);
            skolemData.add(exec.execConstruct());
        }
    }

    public static boolean isSkolemizable(Model model) {
        if (model != null) {
            //@formatter:off
            String sparql = "ASK { ?s ?p ?o . FILTER (isBlank(?s)) } ";
            //@formatter:on
            QueryExecution exec = QueryExecutionFactory.create(sparql, model);
            return exec.execAsk();
        }
        return false;
    }

    public static boolean isSkolemized(Model model) {
        if (model != null) {
            //@formatter:off
            String sparql = "PREFIX skolem: <" + Skolem.NS + "> "
                    + "ASK { ?s ?p skolem:Skolemized . } ";
            //@formatter:on
            QueryExecution exec = QueryExecutionFactory.create(sparql, model);
            return exec.execAsk();
        }
        return false;
    }

    private void removeUnused(Model model) {
        // @formatter:off
        String sparql = "PREFIX skolem: <" + Skolem.NS+ "> "
                + "DELETE { ?s ?p ?o } "
                + "WHERE { "
                + "     { "
                + "         ?s skolem:iri ?o . "
                + "         BIND(skolem:iri AS ?p) ."
                + "     } UNION { "
                + "         ?s skolem:bnodeId ?o ."
                + "         BIND(skolem:bnodeId AS ?p) ."
                + "     } "
                + "     FILTER NOT EXISTS { "
                + "         ?s ?p2 ?o2 ."
                + "         FILTER ( ?p2 != skolem:iri && ?p2 != skolem:bnodeId ) "
                + "     }"
                + "} ";
        // @formatter:on
        UpdateRequest request = UpdateFactory.create(sparql);
        UpdateAction.execute(request, model);
    }
}