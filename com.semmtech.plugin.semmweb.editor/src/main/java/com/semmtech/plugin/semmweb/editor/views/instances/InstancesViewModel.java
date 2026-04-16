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

package com.semmtech.plugin.semmweb.editor.views.instances;


import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
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
import com.hp.hpl.jena.sparql.expr.nodevalue.NodeValueString;
import com.hp.hpl.jena.sparql.syntax.ElementFilter;
import com.hp.hpl.jena.sparql.syntax.Template;
import com.hp.hpl.jena.vocabulary.RDF;
import com.semmtech.plugin.semmweb.core.sparql.LabelProviderPropertyFunction;
import com.semmtech.semantics.sparql.PathUtil;
import com.semmtech.semantics.sparql.QueryBuilder;
import com.semmtech.semantics.sparql.Triples;
import com.semmtech.semantics.util.JenaUtil;


public class InstancesViewModel extends ModelCom {
    private Model viewModel;
    private OntModel currentModel;

    public static final class Vocabulary {
        public static final String NS = "urn:instancesView#";

        protected static final Resource resource(String local) {
            return ResourceFactory.createResource(NS + local);
        }

        protected static final Property property(String local) {
            return ResourceFactory.createProperty(NS + local);
        }

        public static final Property text = property("text");
        public static final Property image = property("image");
        public static final Property hasDirectType = property("hasDirectType");
        public static final Resource DirectType = resource("DirectType");
    }

    public InstancesViewModel(Model viewModel, OntModel currentModel) {
        super(viewModel.getGraph());
        this.viewModel = viewModel;
        this.currentModel = currentModel;
    }

    public int getDirectTypeCount() {
        if (viewModel != null) {
            Var varDirectType = Var.alloc("directType");

            QueryBuilder qb = QueryBuilder.createSelect(false);
            qb.addTriplePattern(varDirectType, RDF.type, Vocabulary.DirectType);
            qb.setResultCountVar(varDirectType);
            return qb.execCountSelect(viewModel);
        }
        return 0;
    }

    public OntClass getDirectType(int index) {
        if ((viewModel != null) && (currentModel != null) && (index >= 0)) {
            Var varDirectType = Var.alloc("directType");
            Var varText = Var.alloc("directTypeText");

            QueryBuilder qb = QueryBuilder.createSelect(false);
            qb.addTriplePattern(varDirectType, RDF.type, Vocabulary.DirectType);
            qb.addTriplePattern(varDirectType, Vocabulary.text, varText);
            qb.addResultVar(varDirectType);
            qb.addOrderBy(new E_StrLowerCase(new ExprVar(varText)), Query.ORDER_ASCENDING);
            qb.setLimit(1);
            qb.setOffset(index);

            ResultSet iter = qb.execSelect(viewModel);
            while (iter.hasNext()) {
                QuerySolution qs = iter.next();
                Resource resource = qs.getResource(varDirectType.getName());
                OntClass ontClass = JenaUtil.asOntClass(resource, currentModel);
                return ontClass;
            }
        }
        return null;
    }

    public int getDirectInstanceCount(Resource directType, String instanceNameFilter) {
        if ((viewModel != null) && (directType != null)) {
            Var varInstance = Var.alloc("instance");
            Var varText = Var.alloc("instanceText");

            QueryBuilder qb = QueryBuilder.createSelect(false);
            qb.addTriplePattern(varInstance, Vocabulary.hasDirectType, directType);
            qb.addTriplePattern(varInstance, Vocabulary.text, varText);
            if (!Strings.isNullOrEmpty(instanceNameFilter)) {
                qb.addFilterPattern(new ElementFilter(new E_StrContains(new E_StrLowerCase(
                        new E_Str(new ExprVar(varText))), new NodeValueString(instanceNameFilter
                        .toLowerCase()))));
            }
            qb.setResultCountVar(varInstance);
            return qb.execCountSelect(viewModel);
        }
        return 0;
    }

    public Resource getDirectInstance(Resource directType, String instanceNameFilter, int index) {
        if ((viewModel != null) && (currentModel != null) && (directType != null) && (index >= 0)) {
            Var varInstance = Var.alloc("instance");
            Var varText = Var.alloc("instanceText");

            QueryBuilder qb = QueryBuilder.createSelect(false);
            qb.addTriplePattern(varInstance, Vocabulary.hasDirectType, directType);
            qb.addTriplePattern(varInstance, Vocabulary.text, varText);
            if (!Strings.isNullOrEmpty(instanceNameFilter)) {
                qb.addFilterPattern(new ElementFilter(new E_StrContains(new E_StrLowerCase(
                        new E_Str(new ExprVar(varText))), new NodeValueString(instanceNameFilter
                        .toLowerCase()))));
            }
            qb.addResultVar(varInstance);
            qb.addOrderBy(new E_StrLowerCase(new ExprVar(varText)), Query.ORDER_ASCENDING);
            qb.setLimit(1);
            qb.setOffset(index);

            ResultSet iter = qb.execSelect(viewModel);
            while (iter.hasNext()) {
                QuerySolution qs = iter.next();
                Resource resource = qs.getResource(varInstance.getName());
                OntResource ontResource = JenaUtil.asOntResource(resource, currentModel);
                return ontResource;
            }
        }
        return null;
    }

    public int getInstanceCount(String instanceNameFilter) {
        if (viewModel != null) {
            Var varInstance = Var.alloc("instance");
            Var varDirectType = Var.alloc("directType");
            Var varText = Var.alloc("instanceText");

            QueryBuilder qb = QueryBuilder.createSelect(false);
            qb.addTriplePattern(varInstance, Vocabulary.hasDirectType, varDirectType);
            if (!Strings.isNullOrEmpty(instanceNameFilter)) {
                qb.addTriplePattern(varInstance, Vocabulary.text, varText);
                qb.addFilterPattern(new ElementFilter(new E_StrContains(new E_StrLowerCase(
                        new E_Str(new ExprVar(varText))), new NodeValueString(instanceNameFilter
                        .toLowerCase()))));
            }
            qb.setResultCountVar(varInstance, true);
            return qb.execCountSelect(viewModel);
        }
        return 0;
    }

    public Resource getInstance(String instanceNameFilter, int index) {
        if ((viewModel != null) && (currentModel != null) && (index >= 0)) {
            Var varInstance = Var.alloc("instance");
            Var varDirectType = Var.alloc("directType");
            Var varText = Var.alloc("instanceText");

            QueryBuilder qb = QueryBuilder.createSelect(true);
            qb.addTriplePattern(varInstance, Vocabulary.hasDirectType, varDirectType);
            qb.addTriplePattern(varInstance, Vocabulary.text, varText);
            if (!Strings.isNullOrEmpty(instanceNameFilter)) {
                qb.addFilterPattern(new ElementFilter(new E_StrContains(new E_StrLowerCase(
                        new E_Str(new ExprVar(varText))), new NodeValueString(instanceNameFilter
                        .toLowerCase()))));
            }
            qb.addResultVar(varInstance);
            qb.addOrderBy(new E_StrLowerCase(new ExprVar(varText)), Query.ORDER_ASCENDING);
            qb.setLimit(1);
            qb.setOffset(index);

            ResultSet iter = qb.execSelect(viewModel);
            while (iter.hasNext()) {
                QuerySolution qs = iter.next();
                Resource resource = qs.getResource(varInstance.getName());
                OntResource ontResource = JenaUtil.asOntResource(resource, currentModel);
                return ontResource;
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

    public DeltaInstancesViewModel difference(InstancesViewModel viewModel) {
        return new DeltaInstancesViewModel(difference((Model) viewModel), currentModel);
    }

    public static InstancesViewModel create(OntModel model, Resource type) {
        if ((model == null) || (type == null)) {
            return null;
        }

        Var varInstance = Var.alloc("instance");
        Var varInstanceText = Var.alloc("instanceText");
        Var varInstanceImage = Var.alloc("instanceImage");
        Var varDirectType = Var.alloc("directType");
        Var varDirectTypeText = Var.alloc("directTypeText");
        Var varDirectTypeImage = Var.alloc("directTypeImage");

        // Get non-anonymous instances and their direct types
        QueryBuilder qb = QueryBuilder.createConstruct();
        qb.addTriplePattern(varInstance, PathUtil.isInstanceOf, type);
        qb.addTriplePattern(varInstance, RDF.type, varDirectType);
        qb.addFilterIsURI(varInstance);

        // Retrieve the text and image for the instance
        qb.addTriplePatterns(Triples.create(varInstance, LabelProviderPropertyFunction.asNode(),
                Lists.newArrayList(varInstanceText.asNode(), varInstanceImage.asNode())));

        // Retrieve the text and image for the directType
        qb.addTriplePatterns(Triples.create(varDirectType, LabelProviderPropertyFunction.asNode(),
                Lists.newArrayList(varDirectTypeText.asNode(), varDirectTypeImage.asNode())));

        BasicPattern bgp = new BasicPattern();
        bgp.add(Triples.create(varInstance, Vocabulary.hasDirectType, varDirectType));
        bgp.add(Triples.create(varInstance, Vocabulary.text, varInstanceText));
        bgp.add(Triples.create(varInstance, Vocabulary.image, varInstanceImage));
        bgp.add(Triples.create(varDirectType, RDF.type, Vocabulary.DirectType));
        bgp.add(Triples.create(varDirectType, Vocabulary.text, varDirectTypeText));
        bgp.add(Triples.create(varDirectType, Vocabulary.image, varDirectTypeImage));
        Template constructTemplate = new Template(bgp);
        qb.setConstructTemplate(constructTemplate);

        return new InstancesViewModel(qb.execConstruct(model), model);
    }
}
