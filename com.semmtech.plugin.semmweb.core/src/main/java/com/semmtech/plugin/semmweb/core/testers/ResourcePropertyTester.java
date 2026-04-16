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

package com.semmtech.plugin.semmweb.core.testers;


import java.util.List;

import org.eclipse.core.expressions.PropertyTester;

import com.google.common.base.Strings;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.path.Path;
import com.hp.hpl.jena.vocabulary.RDF;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.model.IModelProvider;
import com.semmtech.semantics.sparql.PathUtil;
import com.semmtech.semantics.sparql.QueryBuilder;


public class ResourcePropertyTester extends PropertyTester {
    public static final String PROPERTY_HAS_TYPE = "hasType";
    public static final String PROPERTY_SUB_CLASS_OF = "subClassOf";
    public static final String PROPERTY_DIRECT_TYPE = "directType";
    public static final String PROPERTY_HAS_URI = "hasURI";
    public static final String PROPERTY_IS_CONTAINED_IN_BASE_MODEL = "isContainedInBaseModel";
    public static final String PROPERTY_IS_CONTAINED_IN_SUB_MODELS = "isContainedInSubModels";
    public static final String PROPERTY_IS_ANONYMOUS = "isAnonymous";
    public static final String PROPERTY_HAS_PORPERTY = "hasProperty";
    public static final String PROPERTY_HAS_PORPERTY_INVERSE = "hasPropertyInverse";

    public ResourcePropertyTester() {
    }

    @Override
    public boolean test(Object receiver, String property, Object[] args, final Object expectedValue) {
        if (!(receiver instanceof Resource)) {
            return false;
        }

        Resource resource = (Resource) receiver;

        IModelProvider modelProvider = CorePlugin.getDefault().getActiveModelProvider();
        if (modelProvider == null) {
            return false;
        }

        OntModel model = modelProvider.getOntModel();
        if (model == null) {
            return false;
        }

        if (property.equals(PROPERTY_HAS_TYPE)) {
            String typeUri = expectedValue.toString();
            return hasType(model, resource, typeUri, false);
        }
        else if (property.equals(PROPERTY_DIRECT_TYPE)) {
            String typeUri = expectedValue.toString();
            return hasType(model, resource, typeUri, true);
        }
        else if (property.equals(PROPERTY_HAS_PORPERTY)) {
            String propertyUri = expectedValue.toString();
            return hasProperty(model, resource, propertyUri);
        }
        else if (property.equals(PROPERTY_HAS_PORPERTY_INVERSE)) {
            String propertyUri = expectedValue.toString();
            return hasPropertyInverse(model, resource, propertyUri);
        }
        else if (property.equals(PROPERTY_SUB_CLASS_OF)) {
            String superClassUri = expectedValue.toString();
            return subClassOf(model, resource, superClassUri);
        }
        else if (property.equals(PROPERTY_HAS_URI)) {
            String uri = expectedValue.toString();
            return (resource.getURI() != null && resource.getURI().equals(uri));
        }
        else if (property.equals(PROPERTY_IS_ANONYMOUS)) {
            String expectedValueString = expectedValue.toString();
            boolean expected = Strings.isNullOrEmpty(expectedValueString) ? true : Boolean
                    .parseBoolean(expectedValueString);
            return (resource.isAnon() == expected);
        }
        else if (property.equals(PROPERTY_IS_CONTAINED_IN_BASE_MODEL)) {
            String expectedValueString = expectedValue.toString();
            boolean expected = Strings.isNullOrEmpty(expectedValueString) ? true : Boolean
                    .parseBoolean(expectedValueString);
            return (modelProvider.getBaseModel().containsResource(resource) == expected);
        }
        else if (property.equals(PROPERTY_IS_CONTAINED_IN_SUB_MODELS)) {
            String expectedValueString = expectedValue.toString();
            boolean expected = Strings.isNullOrEmpty(expectedValueString) ? true : Boolean
                    .parseBoolean(expectedValueString);
            List<String> subModelURIs = modelProvider.getSubModelURIs();
            for (String subModelURI : subModelURIs) {
                Model subModel = modelProvider.getSubModel(subModelURI);
                if (subModel.containsResource(resource)) {
                    return (true == expected);
                }
            }
            return (false == expected);
        }
        return false;
    }

    private boolean hasType(Model model, Resource resource, String typeUri, boolean direct) {
        Node type = NodeFactory.createURI(typeUri);
        QueryBuilder builder = QueryBuilder.createAsk();
        if (!direct) {
            Path path = PathUtil.getPath(PathUtil.IS_INSTANCE_OF);
            builder.addTriplePathPattern(resource, path, type);
        }
        else {
            builder.addTriplePattern(resource, RDF.type, type);
        }
        QueryExecution execution = QueryExecutionFactory.create(builder.getQuery(), model);
        return execution.execAsk();
    }

    private boolean hasProperty(Model model, Resource resource, String propertyUri) {
        Node property = NodeFactory.createURI(propertyUri);
        QueryBuilder builder = QueryBuilder.createAsk();
        builder.addTriplePattern(resource, property, Node.ANY);
        QueryExecution execution = QueryExecutionFactory.create(builder.getQuery(), model);
        return execution.execAsk();
    }

    private boolean hasPropertyInverse(Model model, Resource resource, String propertyUri) {
        Node property = NodeFactory.createURI(propertyUri);
        QueryBuilder builder = QueryBuilder.createAsk();
        builder.addTriplePattern(Node.ANY, property, resource);
        QueryExecution execution = QueryExecutionFactory.create(builder.getQuery(), model);
        return execution.execAsk();
    }

    private boolean subClassOf(Model model, Resource resource, String classUri) {
        Node clazz = NodeFactory.createURI(classUri);
        QueryBuilder builder = QueryBuilder.createAsk();
        Path path = PathUtil.getPath(PathUtil.SELF_OR_INFERRED_SUBCLASS_OF);
        builder.addTriplePathPattern(resource, path, clazz);
        QueryExecution execution = QueryExecutionFactory.create(builder.getQuery(), model);
        return execution.execAsk();
    }
}
