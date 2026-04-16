/********************************************************************************
 * Copyright (c) 2011-2016, 2026 Semmtech B.V., Hoofddorp.
 *    ___  _____ __  __ __  __ _____ _____ ___ _   _ 
 *   / __|| ____|  \/  |  \/  |_   _| ____/ __| | | |
 *   \__ \|  _| | |\/| | |\/| | | | |  _|| |  | |_| |
 *    __) | |___| |  | | |  | | | | | |__| |__|  _  |
 *   |___/|_____|_|  |_|_|  |_| |_| |_____\___|_| |_| B.V.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package com.semmtech.plugin.semmweb.core.sparql;


import java.util.List;
import java.util.Map;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.graph.impl.LiteralLabel;
import com.hp.hpl.jena.query.QueryBuildException;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.engine.ExecutionContext;
import com.hp.hpl.jena.sparql.engine.QueryIterator;
import com.hp.hpl.jena.sparql.engine.binding.Binding;
import com.hp.hpl.jena.sparql.engine.binding.BindingFactory;
import com.hp.hpl.jena.sparql.engine.binding.BindingMap;
import com.hp.hpl.jena.sparql.path.PathPropertyFunction;
import com.hp.hpl.jena.sparql.pfunction.PropFuncArg;
import com.hp.hpl.jena.sparql.pfunction.PropFuncArgType;
import com.hp.hpl.jena.sparql.pfunction.PropertyFunctionEval;
import com.hp.hpl.jena.sparql.util.IterLib;
import com.hp.hpl.jena.sparql.util.Utils;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.DC_11;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.OWL2;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.XSD;
import com.semmtech.plugin.semmweb.core.model.DisplayLanguage;
import com.semmtech.plugin.semmweb.core.preferences.LabelsPreference;
import com.semmtech.plugin.semmweb.core.preferences.LanguagesPreference;
import com.semmtech.semantics.sparql.PathUtil;
import com.semmtech.semantics.vocabulary.SKOS;


public class LabelProviderPropertyFunction extends PropertyFunctionEval {
    protected static final List<Node> PREFERRED_PROPERTY_ORDER = Lists.newArrayList(
            DCTerms.title.asNode(), DC_11.title.asNode(), SKOS.prefLabel.asNode(),
            RDFS.label.asNode(), SKOS.altLabel.asNode(), RDF.value.asNode());

    protected static final List<String> NUMBER_DATATYPE_URIS = Lists.newArrayList(
            XSD.integer.getURI(), XSD.decimal.getURI(), XSD.nonNegativeInteger.getURI(),
            XSD.negativeInteger.getURI(), XSD.nonPositiveInteger.getURI(),
            XSD.positiveInteger.getURI(), XSD.xdouble.getURI(), XSD.xfloat.getURI(),
            XSD.xint.getURI(), XSD.xlong.getURI(), XSD.xshort.getURI());

    protected static final Map<Node, String> RESTRICTION_PROPERTIES = createRestrictionProperties();

    protected static Map<Node, String> createRestrictionProperties() {
        Map<Node, String> map = Maps.newLinkedHashMap();
        map.put(OWL.allValuesFrom.asNode(), "only");
        map.put(OWL.hasValue.asNode(), "value");
        map.put(OWL.someValuesFrom.asNode(), "some");
        map.put(OWL.minCardinality.asNode(), "min");
        map.put(OWL.maxCardinality.asNode(), "max");
        map.put(OWL.cardinality.asNode(), "exactly");
        map.put(OWL2.minQualifiedCardinality.asNode(), "min");
        map.put(OWL2.maxQualifiedCardinality.asNode(), "max");
        map.put(OWL2.qualifiedCardinality.asNode(), "exactly");
        return map;
    }

    public static String getURI() {
        return CorePropertyFunctions.NS + "labelProvider";
    }

    public static Node asNode() {
        return NodeFactory.createURI(getURI());
    }

    protected static PathPropertyFunction instanceOfFunction = new PathPropertyFunction(
            PathUtil.getPath(PathUtil.IS_INSTANCE_OF));
    protected static PathPropertyFunction listMembersFunction = new PathPropertyFunction(
            PathUtil.getPath(PathUtil.LIST_MEMBERS));

    public LabelProviderPropertyFunction() {
        super(PropFuncArgType.PF_ARG_SINGLE, PropFuncArgType.PF_ARG_LIST);
    }

    @Override
    public void build(PropFuncArg argSubject, Node predicate, PropFuncArg argObject,
            ExecutionContext execCxt) {
        // Do some checking.
        // These checks are assumed to be passed in .exec()
        if (!argObject.isList())
            throw new QueryBuildException(Utils.className(this)
                    + "Object must be a list of two elements");
        if (argObject.getArgList().size() != 2)
            throw new QueryBuildException(Utils.className(this) + "Object is a list but it has "
                    + argObject.getArgList().size() + " elements - should be 2");
    }

    /**
     * Returns true if the instance is an instance of ( rdf:type /
     * rdfs:subClassOf* ) of the given clazz
     * 
     * @param instance
     * @param clazz
     * @param context
     * @return
     */
    protected boolean instanceOf(Node instance, Node clazz, ExecutionContext context) {
        boolean isInstance = false;
        Var typeVar = Var.alloc("type");
        BindingMap typesMap = BindingFactory.create();
        QueryIterator instances = instanceOfFunction.exec(typesMap, new PropFuncArg(instance),
                (Node) null, new PropFuncArg(typeVar), context);
        while (instances.hasNext()) {
            Binding b = instances.next();
            if (b.contains(typeVar)) {
                Node type = b.get(typeVar);
                if (clazz.equals(type)) {
                    isInstance = true;
                    break;
                }
            }
        }
        return isInstance;
    }

    /**
     * Returns a list of member nodes for the given list
     * 
     * @param list
     * @param context
     * @return
     */
    protected List<Node> listMembers(Node list, ExecutionContext context) {
        List<Node> members = Lists.newArrayList();
        Var memberVar = Var.alloc("member");
        BindingMap membersMap = BindingFactory.create();
        QueryIterator instances = listMembersFunction.exec(membersMap, new PropFuncArg(list),
                (Node) null, new PropFuncArg(memberVar), context);
        while (instances.hasNext()) {
            Binding b = instances.next();
            if (b.contains(memberVar)) {
                members.add(b.get(memberVar));
            }
        }
        return members;
    }

    /**
     * Returns the text for the given node.
     * 
     * @param node
     * @param context
     * @return
     */
    protected String getText(Node node, ExecutionContext context) {
        boolean humanReadable = LabelsPreference.showReadableLabels();
        List<DisplayLanguage> languages = LanguagesPreference.getDisplayLanguages();
        Graph graph = context.getActiveGraph();

        String text = null;
        if (humanReadable) {
            if (LabelsPreference.alwaysShowOntologyUri()
                    && (instanceOf(node, OWL.Ontology.asNode(), context) || graph.contains(
                            Node.ANY, OWL.imports.asNode(), node))) {
                text = "<" + node.getURI() + ">";
            }
            if (text == null) {
                List<LiteralLabel> labels = getLabels(graph, node, PREFERRED_PROPERTY_ORDER, false);
                LiteralLabel label = getPreferredLanguageLabel(labels, languages);
                if (label != null) {
                    text = label.getLexicalForm();
                }
            }

            if (text == null && node.isBlank()) {
                if (graph.contains(node, OWL.intersectionOf.asNode(), Node.ANY)) {
                    Triple triple = graph.find(node, OWL.intersectionOf.asNode(), Node.ANY).next();
                    text = String
                            .format("intersectionOf(%s)", getText(triple.getObject(), context));
                }
                else if (graph.contains(node, OWL.unionOf.asNode(), Node.ANY)) {
                    Triple triple = graph.find(node, OWL.unionOf.asNode(), Node.ANY).next();
                    text = String.format("unionOf(%s)", getText(triple.getObject(), context));
                }
                else if (graph.contains(node, OWL.complementOf.asNode(), Node.ANY)) {
                    Triple triple = graph.find(node, OWL.complementOf.asNode(), Node.ANY).next();
                    text = String.format("complementOf(%s)", getText(triple.getObject(), context));
                }
                else if (graph.contains(node, OWL.oneOf.asNode(), Node.ANY)) {
                    Triple triple = graph.find(node, OWL.oneOf.asNode(), Node.ANY).next();
                    text = String.format("oneOf(%s)", getText(triple.getObject(), context));
                }
                else if (instanceOf(node, RDF.List.asNode(), context)
                        || graph.contains(node, RDF.first.asNode(), Node.ANY)) {
                    List<Node> members = listMembers(node, context);
                    text = "[";
                    for (int i = 0; i < members.size(); i++) {
                        Node member = members.get(i);
                        if (i > 0) {
                            text += ", ";
                        }
                        text += getText(member, context);
                    }
                    text += "]";
                }
                else if (instanceOf(node, OWL.Restriction.asNode(), context)) {
                    String property = "{property}";
                    Node propertyNode = getObjectNode(graph, node, OWL.onProperty.asNode());
                    if (propertyNode != null) {
                        property = getQName(graph, propertyNode);
                    }

                    String qualification = "";
                    Node qualifier = getObjectNode(graph, node, OWL2.onClass.asNode());
                    if (qualifier != null) {
                        qualification = getText(qualifier, context);
                    }

                    for (Node onProperty : RESTRICTION_PROPERTIES.keySet()) {
                        if (graph.contains(node, onProperty, Node.ANY)) {
                            Node object = getObjectNode(graph, node, onProperty);
                            String value = "{value}";
                            if (object.isLiteral()) {
                                String datatypeUri = object.getLiteralDatatypeURI();
                                if (NUMBER_DATATYPE_URIS.contains(datatypeUri)) {
                                    value = String.format("%s", object.getLiteralLexicalForm());
                                }
                                else {
                                    value = String.format("\"%s\"", object.getLiteralLexicalForm());
                                }
                            }
                            else {
                                value = getText(object, context);
                            }
                            String type = RESTRICTION_PROPERTIES.get(onProperty);
                            text = String
                                    .format("%s %s %s%s", property, type, value, qualification);
                        }
                    }
                }
            }
        }
        if (text == null && instanceOf(node, RDF.Statement.asNode(), context)
                && graph.contains(node, RDF.subject.asNode(), Node.ANY)
                && graph.contains(node, RDF.predicate.asNode(), Node.ANY)
                && graph.contains(node, RDF.object.asNode(), Node.ANY)) {

            Node subjectNode = getObjectNode(graph, node, RDF.subject.asNode());
            Node predicateNode = getObjectNode(graph, node, RDF.predicate.asNode());
            Node objectNode = getObjectNode(graph, node, RDF.object.asNode());

            if (objectNode.isLiteral()) {
                text = String.format("%s %s \"%s\"", getText(subjectNode, context),
                        getText(predicateNode, context), objectNode.getLiteralLexicalForm());
            }
            else {
                text = String.format("%s %s %s", getText(subjectNode, context),
                        getText(predicateNode, context), getText(objectNode, context));
            }
        }
        if (text == null) {
            if (node.isBlank()) {
                text = String.format("<%s>", node.getBlankNodeLabel());
            }
            else if (node.isURI()) {
                text = getQName(graph, node);
            }
        }
        return text;
    }

    protected List<LiteralLabel> getLabels(Graph graph, Node node,
            List<Node> preferredPropertyOrder, boolean subProperties) {
        List<LiteralLabel> labels = Lists.newArrayList();

        Node property = null;
        for (Node prop : preferredPropertyOrder) {
            // Check if the property exists in the graph
            if (graph.contains(node, prop, Node.ANY)) {
                property = prop;
                break;
            }

            if (subProperties) {
                // Check if any of the subproperties exists in the graph
                List<Node> propsToSearch = Lists.newArrayList();
                List<Triple> subPropStatements = graph.find(null, PathUtil.subPropertyOfAny, prop)
                        .toList();
                for (Triple subPropStatement : subPropStatements) {
                    if (subPropStatement.getObject().isURI()) {
                        propsToSearch.add(subPropStatement.getObject());
                    }
                }
                for (Node propToSearch : propsToSearch) {
                    if (graph.contains(node, propToSearch, Node.ANY)) {
                        property = propToSearch;
                        break;
                    }
                }
            }
        }

        if (property != null) {
            for (Triple triple : graph.find(node, property, null).toList()) {
                Node obj = triple.getObject();
                if (obj.isLiteral()) {
                    labels.add(triple.getObject().getLiteral());
                }
                else if (graph.contains(obj, RDF.value.asNode(), Node.ANY)) {
                    // This check is needed to be useful for literal
                    // generalization case
                    Node value = graph.find(obj, RDF.value.asNode(), Node.ANY).next().getObject();
                    if (value.isLiteral()) {
                        labels.add(value.getLiteral());
                    }
                }
            }
        }
        return labels;
    }

    protected LiteralLabel getPreferredLanguageLabel(List<LiteralLabel> labels) {
        List<DisplayLanguage> languages = LanguagesPreference.getDisplayLanguages();
        return getPreferredLanguageLabel(labels, languages);
    }

    protected LiteralLabel getPreferredLanguageLabel(List<LiteralLabel> labels,
            List<DisplayLanguage> languages) {
        LiteralLabel result = null;

        if (labels != null && labels.size() > 0) {
            for (DisplayLanguage lang : languages) {
                String code = lang.getCode();
                if (lang.getCode() != null && lang.getCode().length() == 0) {
                    code = null;
                }
                for (LiteralLabel label : labels) {
                    if ((code == null && label.language().length() > 0)
                            || (label.language().length() == 0 && code != null)) {
                        continue;
                    }
                    else if ((label.language().length() == 0 && code == null)
                            || (label.language().equals(code))) {
                        result = label;
                        break;
                    }
                }
                if (result != null && !Strings.isNullOrEmpty(result.getLexicalForm())) {
                    break;
                }
            }
            if (result == null || Strings.isNullOrEmpty(result.getLexicalForm())) {
                result = labels.get(0);
            }
        }

        return result;
    }

    /**
     * Returns the first node found for the given pattern s, p in the graph; and
     * null if the graph does not contain such triple.
     * 
     * @param graph
     * @param s
     * @param p
     * @return
     */
    protected Node getObjectNode(Graph graph, Node s, Node p) {
        if (graph.contains(s, p, Node.ANY)) {
            return graph.find(s, p, Node.ANY).next().getObject();
        }
        return null;
    }

    protected String getQName(Graph graph, Node node) {
        if (node.isURI()) {
            String uri = node.getURI();
            String qname = graph.getPrefixMapping().qnameFor(uri);
            if (qname == null || qname.equals(uri)) {
                qname = String.format("<%s>", uri);
            }
            return qname;
        }
        else if (node.isBlank()) {
            return String.format("<%s>", node.getBlankNodeId());
        }
        return null;
    }

    @Override
    public QueryIterator execEvaluated(Binding binding, PropFuncArg argSubject, Node predicate,
            PropFuncArg argObject, ExecutionContext context) {

        Node textNode = argObject.getArg(0);
        Node imageNode = argObject.getArg(1);
        if (!Var.isVar(textNode) || !Var.isVar(imageNode)) {
            return IterLib.noResults(context);
        }

        BindingMap newBindings = BindingFactory.create(binding);
        Node subject = argSubject.getArg();
        String text = getText(subject, context);
        newBindings.add(Var.alloc(textNode), NodeFactory.createLiteral(text));
        newBindings.add(Var.alloc(imageNode), NodeFactory.createLiteral("todo.png"));

        return IterLib.result(newBindings, context);
    }
}
