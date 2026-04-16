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

package com.semmtech.plugin.semmweb.editor.views.properties;


import java.util.List;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.impl.ModelCom;
import com.hp.hpl.jena.sparql.core.BasicPattern;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.expr.E_Str;
import com.hp.hpl.jena.sparql.expr.E_StrContains;
import com.hp.hpl.jena.sparql.expr.E_StrLowerCase;
import com.hp.hpl.jena.sparql.expr.ExprVar;
import com.hp.hpl.jena.sparql.expr.nodevalue.NodeValueNode;
import com.hp.hpl.jena.sparql.expr.nodevalue.NodeValueString;
import com.hp.hpl.jena.sparql.syntax.ElementFilter;
import com.hp.hpl.jena.sparql.syntax.ElementGroup;
import com.hp.hpl.jena.sparql.syntax.ElementOptional;
import com.hp.hpl.jena.sparql.syntax.ElementSubQuery;
import com.hp.hpl.jena.sparql.syntax.ElementUnion;
import com.hp.hpl.jena.sparql.syntax.Template;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.semmtech.plugin.semmweb.core.sparql.LabelProviderPropertyFunction;
import com.semmtech.plugin.semmweb.core.sparql.NamespaceLabelProviderPropertyFunction;
import com.semmtech.semantics.sparql.PathUtil;
import com.semmtech.semantics.sparql.QueryBuilder;
import com.semmtech.semantics.sparql.Triples;
import com.semmtech.semantics.util.JenaUtil;
import com.semmtech.semantics.vocabulary.APF;


public class PropertiesViewModel extends ModelCom {
    private Model viewModel;
    private OntModel currentModel;

    public static final class Vocabulary {
        public static final String NS = "urn:propertiesView#";

        protected static final Resource resource(String local) {
            return ResourceFactory.createResource(NS + local);
        }

        protected static final Property property(String local) {
            return ResourceFactory.createProperty(NS + local);
        }

        public static final Property isChildOf = property("isChildOf");
        public static final Property hasNamespace = property("hasNamespace");
        public static final Property text = property("text");
        public static final Property image = property("image");
        public static final Resource Root = resource("Root");
        public static final Resource Namespace = resource("Namespace");
    }

    public PropertiesViewModel(Model viewModel, OntModel currentModel) {
        super(viewModel.getGraph());
        this.viewModel = viewModel;
        this.currentModel = currentModel;
    }

    public int getInversePropertyCount(Property property) {
        if ((viewModel != null) && (property != null)) {
            Var varInverseProperty = Var.alloc("inverseProperty");

            QueryBuilder qb = QueryBuilder.createSelect(false);
            qb.addTriplePattern(varInverseProperty, OWL.inverseOf, property);
            qb.setResultCountVar(varInverseProperty);
            return qb.execCountSelect(viewModel);
        }
        return 0;
    }

    public Property getInverseProperty(Property property, int index) {
        if ((currentModel != null) && (viewModel != null) && (property != null) && (index >= 0)) {
            Var varInverseProperty = Var.alloc("inverseProperty");
            Var varPropertyText = Var.alloc("propertyText");

            QueryBuilder qb = QueryBuilder.createSelect(false);
            qb.addTriplePattern(varInverseProperty, OWL.inverseOf, property);
            qb.addTriplePattern(varInverseProperty, Vocabulary.text, varPropertyText);
            qb.addResultVar(varInverseProperty);
            qb.addOrderBy(new E_StrLowerCase(new ExprVar(varPropertyText)), Query.ORDER_ASCENDING);
            qb.setLimit(1);
            qb.setOffset(index);

            ResultSet iter = qb.execSelect(viewModel);
            while (iter.hasNext()) {
                QuerySolution qs = iter.next();
                Resource resource = qs.getResource(varInverseProperty.getName());
                Property inverseProperty = JenaUtil.asProperty(resource, currentModel);
                return inverseProperty;
            }
        }
        return null;
    }

    public int getNamespaceCount() {
        if (viewModel != null) {
            Var varNamespace = Var.alloc("namespace");

            QueryBuilder qb = QueryBuilder.createSelect(false);
            qb.addTriplePattern(varNamespace, RDF.type, Vocabulary.Namespace);
            qb.setResultCountVar(varNamespace);
            return qb.execCountSelect(viewModel);
        }
        return 0;
    }

    public Resource getNamespace(int index) {
        if ((currentModel != null) && (viewModel != null) && (index >= 0)) {
            Var varNamespace = Var.alloc("namespace");
            Var varNamespaceText = Var.alloc("namespaceText");

            QueryBuilder qb = QueryBuilder.createSelect(false);
            qb.addTriplePattern(varNamespace, RDF.type, Vocabulary.Namespace);
            qb.addTriplePattern(varNamespace, Vocabulary.text, varNamespaceText);
            qb.addResultVar(varNamespace);
            qb.addOrderBy(new E_StrLowerCase(new ExprVar(varNamespaceText)), Query.ORDER_ASCENDING);
            qb.setLimit(1);
            qb.setOffset(index);

            ResultSet iter = qb.execSelect(viewModel);
            while (iter.hasNext()) {
                QuerySolution qs = iter.next();
                Resource resource = qs.getResource(varNamespace.getName());
                OntResource ontResource = JenaUtil.asOntResource(resource, currentModel);
                return ontResource;
            }
        }
        return null;
    }

    public int getNamespacePropertyCount(Resource namespace, String propertyNameFilter) {
        if ((viewModel != null) && (namespace != null)) {
            Var varProperty = Var.alloc("property");
            Var varText = Var.alloc("propertyText");

            QueryBuilder qb = QueryBuilder.createSelect(false);
            qb.addTriplePattern(varProperty, Vocabulary.hasNamespace, namespace);
            qb.addTriplePattern(varProperty, Vocabulary.text, varText);
            if (!Strings.isNullOrEmpty(propertyNameFilter)) {
                qb.addFilterPattern(new ElementFilter(new E_StrContains(new E_StrLowerCase(
                        new E_Str(new ExprVar(varText))), new NodeValueString(propertyNameFilter
                        .toLowerCase()))));
            }
            qb.setResultCountVar(varProperty);
            return qb.execCountSelect(viewModel);
        }
        return 0;
    }

    public Property getNamespaceProperty(Resource namespace, String propertyNameFilter, int index) {
        if ((viewModel != null) && (namespace != null) && (index >= 0)) {
            Var varProperty = Var.alloc("property");
            Var varPropertyText = Var.alloc("propertyText");

            QueryBuilder qb = QueryBuilder.createSelect(false);
            qb.addTriplePattern(varProperty, Vocabulary.hasNamespace, namespace);
            qb.addTriplePattern(varProperty, Vocabulary.text, varPropertyText);
            if (!Strings.isNullOrEmpty(propertyNameFilter)) {
                qb.addFilterPattern(new ElementFilter(new E_StrContains(new E_StrLowerCase(
                        new E_Str(new ExprVar(varPropertyText))), new NodeValueString(
                        propertyNameFilter.toLowerCase()))));
            }
            qb.addResultVar(varProperty);
            qb.addOrderBy(new E_StrLowerCase(new ExprVar(varPropertyText)), Query.ORDER_ASCENDING);
            qb.setLimit(1);
            qb.setOffset(index);

            ResultSet iter = qb.execSelect(viewModel);
            while (iter.hasNext()) {
                QuerySolution qs = iter.next();
                Resource resource = qs.getResource(varProperty.getName());
                OntProperty property = JenaUtil.asOntProperty(resource, currentModel);
                return property;
            }
        }
        return null;
    }

    public int getRootPropertyCount() {
        if (viewModel != null) {
            Var varProperty = Var.alloc("property");

            QueryBuilder qb = QueryBuilder.createSelect(false);
            qb.addTriplePattern(varProperty, RDF.type, Vocabulary.Root);
            qb.setResultCountVar(varProperty);
            return qb.execCountSelect(viewModel);
        }
        return 0;
    }

    public Property getRootProperty(int index) {
        if ((currentModel != null) && (viewModel != null) && (index >= 0)) {
            Var varProperty = Var.alloc("property");
            Var varPropertyText = Var.alloc("propertyText");

            QueryBuilder qb = QueryBuilder.createSelect(false);
            qb.addTriplePattern(varProperty, RDF.type, Vocabulary.Root);
            qb.addTriplePattern(varProperty, Vocabulary.text, varPropertyText);
            qb.addResultVar(varProperty);
            qb.addOrderBy(new E_StrLowerCase(new ExprVar(varPropertyText)), Query.ORDER_ASCENDING);
            qb.setLimit(1);
            qb.setOffset(index);

            ResultSet iter = qb.execSelect(viewModel);
            while (iter.hasNext()) {
                QuerySolution qs = iter.next();
                Resource resource = qs.getResource(varProperty.getName());
                OntProperty property = JenaUtil.asOntProperty(resource, currentModel);
                return property;
            }
        }
        return null;
    }

    public int getChildPropertyCount(Property property) {
        if ((viewModel != null) && (property != null)) {
            Var varChildProperty = Var.alloc("childProperty");

            QueryBuilder qb = QueryBuilder.createSelect(false);
            qb.addTriplePattern(varChildProperty, Vocabulary.isChildOf, property);
            qb.setResultCountVar(varChildProperty);
            return qb.execCountSelect(viewModel);
        }
        return 0;
    }

    public Property getChildProperty(Property property, int index) {
        if ((currentModel != null) && (viewModel != null) && (property != null) && (index >= 0)) {
            Var varChildProperty = Var.alloc("childProperty");
            Var varPropertyText = Var.alloc("propertyText");

            QueryBuilder qb = QueryBuilder.createSelect(false);
            qb.addTriplePattern(varChildProperty, Vocabulary.isChildOf, property);
            qb.addTriplePattern(varChildProperty, Vocabulary.text, varPropertyText);
            qb.addResultVar(varChildProperty);
            qb.addOrderBy(new E_StrLowerCase(new ExprVar(varPropertyText)), Query.ORDER_ASCENDING);
            qb.setLimit(1);
            qb.setOffset(index);

            ResultSet iter = qb.execSelect(viewModel);
            while (iter.hasNext()) {
                QuerySolution qs = iter.next();
                Resource resource = qs.getResource(varChildProperty.getName());
                OntProperty childProperty = JenaUtil.asOntProperty(resource, currentModel);
                return childProperty;
            }
        }
        return null;
    }

    public String getText(Resource resource) {
        if ((viewModel != null) && (resource != null)) {
            Resource viewModelResource = resource.isURIResource() ? viewModel
                    .createResource(resource.getURI()) : viewModel.createResource(resource.getId());
            Statement statement = viewModelResource.getProperty(Vocabulary.text);
            if (statement != null) {
                return statement.getObject().asLiteral().getString();
            }
        }
        return null;
    }

    public DeltaPropertiesViewModel difference(PropertiesViewModel viewModel) {
        return new DeltaPropertiesViewModel(difference((Model) viewModel), currentModel);
    }

    public static PropertiesViewModel create(OntModel model) {
        if (model == null) {
            return null;
        }

        Var varProperty = Var.alloc("property");
        Var varType = Var.alloc("type");
        Var varSuperProperty = Var.alloc("superProperty");
        Var varRootSuperProperty = Var.alloc("rootSuperProperty"); // non-existent
        Var varInverseProperty = Var.alloc("inverseProperty");
        Var varNamespace = Var.alloc("namespace");
        Var varLocalName = Var.alloc("localName");
        Var varPropertyText = Var.alloc("propertyText");
        Var varPropertyImage = Var.alloc("propertyImage");
        Var varNamespaceText = Var.alloc("namespaceText");
        Var varNamespaceImage = Var.alloc("namespaceImage");

        QueryBuilder qb = QueryBuilder.createConstruct();

        // Create union of two sub queries
        ElementUnion union = new ElementUnion();

        // First sub query: get all root properties, which will have ?type
        // == Vocabulary.Root.
        QueryBuilder subqb = QueryBuilder.createSelect(true);
        subqb.addTriplePattern(varProperty, PathUtil.isInstanceOf, RDF.Property);
        subqb.addFilterPattern(
                Triples.create(varProperty, RDFS.subPropertyOf, varRootSuperProperty), true);
        subqb.addResultVar(varProperty);
        subqb.getProject().add(varType, new NodeValueNode(Vocabulary.Root.asNode()));
        ElementSubQuery subQuery = new ElementSubQuery(subqb.getQuery());
        union.addElement(subQuery);

        // Second sub query: get the remaining properties and their super
        // properties.
        subqb = QueryBuilder.createSelect(true);
        subqb.addTriplePattern(varProperty, PathUtil.isInstanceOf, RDF.Property);
        subqb.addTriplePattern(varProperty, RDFS.subPropertyOf, varSuperProperty);
        subqb.addResultVars(varProperty, varSuperProperty);
        subQuery = new ElementSubQuery(subqb.getQuery());
        union.addElement(subQuery);

        qb.addPattern(union);

        // Separate URIs of properties into a namespace and local name.
        List<Node> varList = Lists.newArrayList(varNamespace.asNode(), varLocalName.asNode());
        qb.addTriplePatterns(Triples.create(varProperty, APF.splitURI, varList));

        // Retrieve the text and image for the property
        qb.addTriplePatterns(Triples.create(varProperty, LabelProviderPropertyFunction.asNode(),
                Lists.newArrayList(varPropertyText.asNode(), varPropertyImage.asNode())));

        // Retrieve the text and image for the namespace
        qb.addTriplePatterns(Triples.create(varNamespace,
                NamespaceLabelProviderPropertyFunction.asNode(),
                Lists.newArrayList(varNamespaceText.asNode(), varNamespaceImage.asNode())));

        // Get inverses of properties (if existent)
        ElementGroup eg = new ElementGroup();
        eg.addTriplePattern(Triples.create(varProperty, OWL.inverseOf, varInverseProperty));
        qb.addPattern(new ElementOptional(eg));

        // Set the Construct pattern
        BasicPattern bgp = new BasicPattern();
        bgp.add(Triples.create(varProperty, RDF.type, RDF.Property));
        bgp.add(Triples.create(varProperty, RDF.type, varType));
        bgp.add(Triples.create(varProperty, Vocabulary.text, varPropertyText));
        bgp.add(Triples.create(varProperty, Vocabulary.image, varPropertyImage));
        bgp.add(Triples.create(varProperty, Vocabulary.isChildOf, varSuperProperty));
        bgp.add(Triples.create(varProperty, OWL.inverseOf, varInverseProperty));
        bgp.add(Triples.create(varProperty, Vocabulary.hasNamespace, varNamespace));
        bgp.add(Triples.create(varNamespace, RDF.type, Vocabulary.Namespace));
        bgp.add(Triples.create(varNamespace, Vocabulary.text, varNamespaceText));
        bgp.add(Triples.create(varNamespace, Vocabulary.image, varNamespaceImage));
        bgp.add(Triples.create(varInverseProperty, OWL.inverseOf, varProperty));
        Template constructTemplate = new Template(bgp);
        qb.setConstructTemplate(constructTemplate);

        return new PropertiesViewModel(qb.execConstruct(model), model);
    }
}
