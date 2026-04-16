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


import java.util.List;

import com.google.common.collect.Lists;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.impl.ModelCom;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.vocabulary.RDF;
import com.semmtech.plugin.semmweb.editor.views.instances.InstancesViewModel.Vocabulary;
import com.semmtech.semantics.sparql.QueryBuilder;
import com.semmtech.semantics.util.JenaUtil;


/**
 * Represents the difference, or delta, between two InstanceViewModel objects.
 * As some properties may be missing from this model compared to a full
 * InstanceViewModel (such as the text of each resource), the functions listed
 * in this class do not return the items in their correct order.
 */
public class DeltaInstancesViewModel extends ModelCom {
    private Model deltaViewModel;
    private OntModel currentModel;

    public DeltaInstancesViewModel(Model deltaViewModel, OntModel currentModel) {
        super(deltaViewModel.getGraph());
        this.deltaViewModel = deltaViewModel;
        this.currentModel = currentModel;
    }

    public List<OntClass> getDirectTypes() {
        List<OntClass> result = Lists.newArrayList();
        if ((deltaViewModel != null) && (currentModel != null)) {
            Var varDirectType = Var.alloc("directType");

            QueryBuilder qb = QueryBuilder.createSelect(false);
            qb.addTriplePattern(varDirectType, RDF.type, Vocabulary.DirectType);
            qb.addResultVar(varDirectType);

            ResultSet iter = qb.execSelect(deltaViewModel);
            while (iter.hasNext()) {
                QuerySolution qs = iter.next();
                Resource resource = qs.getResource(varDirectType.getName());
                OntClass ontClass = JenaUtil.asOntClass(resource, currentModel);
                result.add(ontClass);
            }
        }
        return result;
    }

    public List<Resource> getDirectInstances(Resource directType) {
        List<Resource> result = Lists.newArrayList();
        if ((deltaViewModel != null) && (currentModel != null) && (directType != null)) {
            Var varInstance = Var.alloc("instance");

            QueryBuilder qb = QueryBuilder.createSelect(false);
            qb.addTriplePattern(varInstance, Vocabulary.hasDirectType, directType);
            qb.addResultVar(varInstance);

            ResultSet iter = qb.execSelect(deltaViewModel);
            while (iter.hasNext()) {
                QuerySolution qs = iter.next();
                Resource resource = qs.getResource(varInstance.getName());
                OntResource ontResource = JenaUtil.asOntResource(resource, currentModel);
                result.add(ontResource);
            }
        }
        return result;
    }

    public List<Resource> getInstances() {
        List<Resource> result = Lists.newArrayList();
        if ((deltaViewModel != null) && (currentModel != null)) {
            Var varInstance = Var.alloc("instance");
            Var varDirectType = Var.alloc("directType");

            QueryBuilder qb = QueryBuilder.createSelect(true);
            qb.addTriplePattern(varInstance, Vocabulary.hasDirectType, varDirectType);
            qb.addResultVar(varInstance);

            ResultSet iter = qb.execSelect(deltaViewModel);
            while (iter.hasNext()) {
                QuerySolution qs = iter.next();
                Resource resource = qs.getResource(varInstance.getName());
                OntResource ontResource = JenaUtil.asOntResource(resource, currentModel);
                result.add(ontResource);
            }
        }
        return result;
    }
}
