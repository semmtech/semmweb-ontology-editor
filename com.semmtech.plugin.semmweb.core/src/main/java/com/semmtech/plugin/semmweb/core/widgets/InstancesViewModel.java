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

package com.semmtech.plugin.semmweb.core.widgets;


import com.google.common.collect.Lists;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.core.BasicPattern;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.syntax.Template;
import com.hp.hpl.jena.vocabulary.RDF;
import com.semmtech.plugin.semmweb.core.resourceviewer.AbstractResourceViewModel;
import com.semmtech.plugin.semmweb.core.sparql.LabelProviderPropertyFunction;
import com.semmtech.semantics.sparql.PathUtil;
import com.semmtech.semantics.sparql.QueryBuilder;
import com.semmtech.semantics.sparql.Triples;


public class InstancesViewModel extends AbstractResourceViewModel {

    private Resource resourceType;

    public InstancesViewModel(OntModel currentModel) {
        super(ModelFactory.createDefaultModel(), currentModel);
    }

    public void setResourceType(Resource resourceType) {
        this.resourceType = resourceType;
    }

    @Override
    public Query buildQuery() {
        Var varInstance = Var.alloc("instance");
        Var varInstanceText = Var.alloc("instanceText");
        Var varInstanceImage = Var.alloc("instanceImage");
        Var varDirectType = Var.alloc("directType");
        Var varDirectTypeText = Var.alloc("directTypeText");
        Var varDirectTypeImage = Var.alloc("directTypeImage");

        // Get non-anonymous instances and their direct types
        QueryBuilder qb = QueryBuilder.createConstruct();
        qb.addTriplePattern(varInstance, PathUtil.isInstanceOf, resourceType);
        qb.addTriplePattern(varInstance, RDF.type, varDirectType);
        qb.addFilterIsURI(varInstance);

        // Retrieve the text and image for the instance
        qb.addTriplePatterns(Triples.create(varInstance, LabelProviderPropertyFunction.asNode(),
                Lists.newArrayList(varInstanceText.asNode(), varInstanceImage.asNode())));

        // Retrieve the text and image for the directType
        qb.addTriplePatterns(Triples.create(varDirectType, LabelProviderPropertyFunction.asNode(),
                Lists.newArrayList(varDirectTypeText.asNode(), varDirectTypeImage.asNode())));

        BasicPattern bgp = new BasicPattern();
        bgp.add(Triples.create(varDirectType, RDF.type, Vocabulary.Root));
        bgp.add(Triples.create(varDirectType, Vocabulary.text, varDirectTypeText));
        bgp.add(Triples.create(varDirectType, Vocabulary.image, varDirectTypeImage));
        bgp.add(Triples.create(varInstance, RDF.type, varDirectType));
        bgp.add(Triples.create(varInstance, Vocabulary.text, varInstanceText));
        bgp.add(Triples.create(varInstance, Vocabulary.image, varInstanceImage));
        bgp.add(Triples.create(varInstance, Vocabulary.isChildOf, varDirectType));

        Template constructTemplate = new Template(bgp);
        qb.setConstructTemplate(constructTemplate);

        return qb.getQuery();
    }
}
