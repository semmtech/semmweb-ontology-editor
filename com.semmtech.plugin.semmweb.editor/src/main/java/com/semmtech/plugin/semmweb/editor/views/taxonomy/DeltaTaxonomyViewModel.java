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

package com.semmtech.plugin.semmweb.editor.views.taxonomy;


import java.util.List;

import com.google.common.collect.Lists;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.impl.ModelCom;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.vocabulary.RDF;
import com.semmtech.plugin.semmweb.editor.views.taxonomy.TaxonomyViewModel.Vocabulary;
import com.semmtech.semantics.sparql.QueryBuilder;
import com.semmtech.semantics.util.JenaUtil;


/**
 * Represents the difference, or delta, between two TaxonomyViewModel objects.
 * As some properties may be missing from this model compared to a full
 * TaxonomyViewModel (such as the text of each resource), the functions listed
 * in this class do not return the items in their correct order.
 */
public class DeltaTaxonomyViewModel extends ModelCom {
    private Model deltaViewModel;
    private OntModel currentModel;

    public DeltaTaxonomyViewModel(Model deltaViewModel, OntModel currentModel) {
        super(deltaViewModel.getGraph());
        this.deltaViewModel = deltaViewModel;
        this.currentModel = currentModel;
    }

    public List<OntClass> getRootClasses() {
        List<OntClass> result = Lists.newArrayList();
        if ((currentModel != null) && (deltaViewModel != null)) {
            Var varClass = Var.alloc("class");

            QueryBuilder qb = QueryBuilder.createSelect(true);
            qb.addTriplePattern(varClass, RDF.type, Vocabulary.Root);
            qb.addResultVar(varClass);

            ResultSet iter = qb.execSelect(deltaViewModel);
            while (iter.hasNext()) {
                QuerySolution qs = iter.next();
                Resource resource = qs.getResource(varClass.getName());
                OntClass clazz = JenaUtil.asOntClass(resource, currentModel);
                result.add(clazz);
            }
        }
        return result;
    }
}
