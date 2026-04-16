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

package com.semmtech.plugin.semmweb.core.model;


import java.util.List;

import com.google.common.collect.Lists;
import com.hp.hpl.jena.ontology.OntDocumentManager;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ModelMaker;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.semmtech.plugin.semmweb.core.sparql.OntologyLabelProviderPropertyFunction;
import com.semmtech.semantics.sparql.QueryBuilder;
import com.semmtech.semantics.sparql.Triples;


/**
 * Utils for use with Jena's OntModel class.
 * 
 * @author Mike Henrichs
 * 
 */
public class OntModelUtils {

    /**
     * Returns a copy of the original model, it copies all statements from the
     * original into the copy and also duplicates all namespace prefix entries.
     * 
     * @param original
     * @return
     */
    public static OntModel copyModel(OntModel original) {
        if (original == null) {
            return null;
        }
        OntModel copy = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
        copy.add(original);
        for (String prefix : original.getNsPrefixMap().keySet()) {
            copy.setNsPrefix(prefix, original.getNsPrefixURI(prefix));
        }
        return copy;
    }

    /**
     * Returns a list of the ontologies present in the model. Note that if you
     * pass an OntModel, any ontologies in its submodels will also be returned.
     */
    public static List<Resource> getOntologies(Model model) {
        List<Resource> ontologies = Lists.newArrayList();
        Var varOntology = Var.alloc("ontology");

        //@formatter:off
        QueryBuilder qb = QueryBuilder
                .createSelect(true)
                .addResultVar(varOntology)
                .addTriplePattern(varOntology, RDF.type, OWL.Ontology)
                .addFilterIsURI(varOntology)
                .addOrderBy(varOntology, Query.ORDER_ASCENDING);
        //@formatter:on

        for (ResultSet iter = qb.execSelect(model); iter.hasNext();) {
            ontologies.add(iter.next().getResource(varOntology.getName()));
        }

        return ontologies;
    }

    /**
     * Returns the name of the model, which is the name given to the only
     * ontology in its base model.
     * 
     * @return The name of the model. If the number of ontologies in the base
     *         model is not exactly 1, the function will return null. If a
     *         single ontology does exist but no suitable label for the name
     *         could be found, the function will return an empty String.
     */
    public static String getName(OntModel model) {
        Model baseModel = model.getBaseModel();

        // Obtain the single ontology in the base model, if present
        List<Resource> ontologies = getOntologies(baseModel);
        if (ontologies.size() != 1) {
            return null;
        }
        Resource ontology = ontologies.get(0);

        // Obtain the preferred label for the ontology, if present
        Var varText = Var.alloc("text");
        Var varImage = Var.alloc("image");

        //@formatter:off
        QueryBuilder qb = QueryBuilder
                .createSelect(true)
                .addResultVar(varText)
                .addTriplePatterns(Triples.create(ontology,
                                                  OntologyLabelProviderPropertyFunction.asNode(),
                                                  Lists.newArrayList(varText.asNode(), varImage.asNode())));
        //@formatter:on

        for (ResultSet iter = qb.execSelect(baseModel); iter.hasNext();) {
            return iter.next().getLiteral(varText.getName()).getLexicalForm();
        }

        return null;
    }

    /**
     * Returns the URI of the model, which is the URI given to the only ontology
     * in its base model.
     * 
     * @return The URI of the model. If the number of ontologies in the base
     *         model is not exactly 1, the function will return null.
     */
    public static String getURI(OntModel model) {
        Model baseModel = model.getBaseModel();

        // Obtain the single ontology in the base model, if present
        List<Resource> ontologies = getOntologies(baseModel);
        if (ontologies.size() != 1) {
            return null;
        }
        Resource ontology = ontologies.get(0);
        return ontology.getURI();
    }

    public static OntModel createWithoutSubModels(IModelProvider provider,
            List<String> excludedSubModels) {
        if (provider == null) {
            return null;
        }

        OntDocumentManager ontDocumentManager = new OntDocumentManager();
        ontDocumentManager.setProcessImports(false);
        ModelMaker modelMaker = ModelFactory.createMemModelMaker();
        OntModelSpec spec = new OntModelSpec(modelMaker, ontDocumentManager, null,
                OWL.FULL_LANG.getURI());
        OntModel newModel = ModelFactory.createOntologyModel(spec); // empty

        OntModel ontModel = provider.getOntModel();
        newModel.addSubModel(ontModel.getBaseModel()); // add base model
        List<String> subModelURIs = provider.getSubModelURIs();
        if (subModelURIs != null) {
            for (String subModelURI : subModelURIs) {
                if (excludedSubModels == null || !excludedSubModels.contains(subModelURI)) {
                    Model subModel = provider.getSubModel(subModelURI);
                    if (subModel != null) {
                        newModel.addSubModel(subModel); // add sub model
                    }
                }
            }
        }
        return newModel;
    }
}
