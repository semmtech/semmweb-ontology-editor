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

import com.google.common.collect.Lists;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.impl.ModelCom;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.vocabulary.RDF;
import com.semmtech.plugin.semmweb.editor.views.properties.PropertiesViewModel.Vocabulary;
import com.semmtech.semantics.sparql.QueryBuilder;
import com.semmtech.semantics.util.JenaUtil;


/**
 * Represents the difference, or delta, between two PropertiesViewModel objects.
 * As some properties may be missing from this model compared to a full
 * PropertiesViewModel (such as the text of each resource), the functions listed
 * in this class do not return the items in their correct order.
 */
public class DeltaPropertiesViewModel extends ModelCom {
    private Model deltaViewModel;
    private OntModel currentModel;

    public DeltaPropertiesViewModel(Model deltaViewModel, OntModel currentModel) {
        super(deltaViewModel.getGraph());
        this.deltaViewModel = deltaViewModel;
        this.currentModel = currentModel;
    }

    public List<Resource> getNamespaces() {
        List<Resource> result = Lists.newArrayList();
        if ((currentModel != null) && (deltaViewModel != null)) {
            Var varNamespace = Var.alloc("namespace");

            QueryBuilder qb = QueryBuilder.createSelect(false);
            qb.addTriplePattern(varNamespace, RDF.type, Vocabulary.Namespace);
            qb.addResultVar(varNamespace);

            ResultSet iter = qb.execSelect(deltaViewModel);
            while (iter.hasNext()) {
                QuerySolution qs = iter.next();
                Resource resource = qs.getResource(varNamespace.getName());
                OntResource ontResource = JenaUtil.asOntResource(resource, currentModel);
                result.add(ontResource);
            }
        }
        return result;
    }

    public List<Property> getNamespaceProperties(Resource namespace) {
        List<Property> result = Lists.newArrayList();
        if ((currentModel != null) && (deltaViewModel != null) && (namespace != null)) {
            Var varProperty = Var.alloc("property");

            QueryBuilder qb = QueryBuilder.createSelect(false);
            qb.addTriplePattern(varProperty, Vocabulary.hasNamespace, namespace);
            qb.addResultVar(varProperty);

            ResultSet iter = qb.execSelect(deltaViewModel);
            while (iter.hasNext()) {
                QuerySolution qs = iter.next();
                Resource resource = qs.getResource(varProperty.getName());
                OntProperty property = JenaUtil.asOntProperty(resource, currentModel);
                result.add(property);
            }
        }
        return result;
    }

    public List<Property> getRootProperties() {
        List<Property> result = Lists.newArrayList();
        if ((currentModel != null) && (deltaViewModel != null)) {
            Var varProperty = Var.alloc("property");

            QueryBuilder qb = QueryBuilder.createSelect(false);
            qb.addTriplePattern(varProperty, RDF.type, Vocabulary.Root);
            qb.addResultVar(varProperty);

            ResultSet iter = qb.execSelect(deltaViewModel);
            while (iter.hasNext()) {
                QuerySolution qs = iter.next();
                Resource resource = qs.getResource(varProperty.getName());
                OntProperty property = JenaUtil.asOntProperty(resource, currentModel);
                result.add(property);
            }
        }
        return result;
    }
}
