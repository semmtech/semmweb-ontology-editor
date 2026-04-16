package com.semmtech.semantics.examples;


import java.util.List;

import com.google.common.collect.Lists;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.reasoner.rulesys.ClauseEntry;
import com.hp.hpl.jena.reasoner.rulesys.GenericRuleReasoner;
import com.hp.hpl.jena.reasoner.rulesys.Rule;
import com.hp.hpl.jena.util.FileUtils;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;


public class RuleBasedReasoningExample {

    /**
     * @param args
     */
    public static void main(String[] args) {
        // printRules();
        // testTableBuiltin();
        organizeRulesFile();
        // generateAssertionRules();
        // testProgrammaticallyRuleParsing();
    }

    @SuppressWarnings("unused")
    private static void testTableBuiltin() {
        Model model = ModelFactory.createDefaultModel();

        Resource a = model.createResource("http://www.test.com/test/A");
        a.addProperty(RDF.type, OWL.Class);
        a.addProperty(RDFS.label, "A");
        a.addProperty(RDFS.comment, "This is the resource A");

        CreateModelProgrammaticallyExample.printModel(model, FileUtils.langTurtle);

        System.out.println("\n-----\n");

        List<Rule> rules = Lists.newArrayList();
        rules.add(Rule
                .parseRule("[ (?x ?p ?y) notEqual(?p, rdf:type) notEqual(?p, rdfs:label) -> hide(?p) ]"));
        GenericRuleReasoner reasoner = new GenericRuleReasoner(rules);
        InfModel infModel = ModelFactory.createInfModel(reasoner, model);

        CreateModelProgrammaticallyExample.printModel(infModel, FileUtils.langTurtle);

    }

    @SuppressWarnings("unused")
    private static void printRules() {
        String rulesFilename = "file:src/test/resources/rules/test.rules";
        List<Rule> rules = Rule.rulesFromURL(rulesFilename);
        if (rules.size() > 0) {
            for (Rule rule : rules) {
                System.out.println(rule.toString());
            }
        }
    }

    private static void organizeRulesFile() {
        Model model = ModelFactory.createDefaultModel();
        model.read(RDF.getURI());

        List<Property> properties = Lists.newArrayList();
        for (Statement statement : model.listStatements().toList()) {
            if (!properties.contains(statement.getPredicate())) {
                properties.add(statement.getPredicate());
            }
        }
        for (Property property : properties) {
            if (property.getURI().equals(RDFS.domain.getURI())
                    || property.getURI().equals(RDFS.range.getURI())) {

                String predicate = model.shortForm(property.getURI());
                for (Statement statement : model.listStatements(
                        new SimpleSelector(null, property, (RDFNode) null)).toList()) {
                    if (statement.getObject().isResource()) {
                        String subject = model.shortForm(statement.getSubject().getURI());
                        String object = model
                                .shortForm(statement.getObject().asResource().getURI());
                        System.out.println(String.format("[ -> (%s %s %s) ]", subject, predicate,
                                object));
                    }
                }
            }
        }
    }

    @SuppressWarnings("unused")
    private static void generateAssertionRules() {
        String filename = RDFS.getURI();
        Model model = ModelFactory.createDefaultModel();
        model.read(filename);

        for (String prefix : model.getNsPrefixMap().keySet()) {
            if (prefix.length() > 0) {
                System.out.println(String.format("@prefix %s:    <%s> .", prefix,
                        model.getNsPrefixURI(prefix)));
            }
        }
        System.out.println();
        // for (Statement statement : model.listStatements().toList()) {
        // /// Only use resources statements - no literals
        // if (statement.getObject().isResource()) {
        // String subject = model.shortForm(statement.getSubject().getURI());
        // String predicate =
        // model.shortForm(statement.getPredicate().getURI());
        // String object =
        // model.shortForm(statement.getObject().asResource().getURI());
        // System.out.println(String.format("[ -> (%s %s %s) ]", subject,
        // predicate, object));
        // }
        // }
        generateStatementsByPropery(model, RDF.type);
        System.out.println();
        generateStatementsByPropery(model, RDFS.subClassOf);
    }

    private static void generateStatementsByPropery(Model model, Property predicate) {
        for (Statement statement : model.listStatements(
                new SimpleSelector(null, predicate, (RDFNode) null)).toList()) {
            // / Only use resources statements - no literals
            if (statement.getObject().isResource()) {
                String subject = model.shortForm(statement.getSubject().getURI());
                String predicateText = model.shortForm(statement.getPredicate().getURI());
                String object = model.shortForm(statement.getObject().asResource().getURI());
                System.out.println(String.format("[ -> (%s %s %s) ]", subject, predicateText,
                        object));
            }
        }
    }

    @SuppressWarnings("unused")
    private static void testProgrammaticallyRuleParsing() {
        List<Rule> rules = Lists.newArrayList();
        ClauseEntry entry = new ClauseEntry() {

            @Override
            public boolean sameAs(Object arg0) {
                // TODO Auto-generated method stub
                return false;
            }
        };
        rules.add(Rule.parseRule("[name1: (?a ?x ?y) -> (?a ?x ?a)]"));
        rules.addAll(Rule.parseRules("[name2: (?a ?a ?a) -> (?b ?b ?b)]" + "[ -> (?x ?x ?x)]"));
        rules.add(new Rule("name", new ClauseEntry[] {}, new ClauseEntry[] {}));

        for (Rule rule : rules) {
            System.out.println(rule.toString());
        }
    }

    // / See http://lpis.csd.auth.gr/systems/DLEJena/templates.txt
}
