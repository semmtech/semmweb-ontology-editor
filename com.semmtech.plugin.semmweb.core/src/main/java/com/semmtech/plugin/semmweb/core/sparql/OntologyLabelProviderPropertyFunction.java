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
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.graph.impl.LiteralLabel;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.engine.ExecutionContext;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.DC_11;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.semmtech.plugin.semmweb.core.model.DisplayLanguage;
import com.semmtech.plugin.semmweb.core.preferences.LanguagesPreference;
import com.semmtech.semantics.sparql.PathUtil;
import com.semmtech.semantics.sparql.QueryBuilder;
import com.semmtech.semantics.vocabulary.SKOS;


/**
 * 
 * @author Sander Stolk
 */
public class OntologyLabelProviderPropertyFunction extends LabelProviderPropertyFunction {
    public static final List<Node> PREFERRED_PROPERTY_ORDER = Lists.newArrayList(
            DCTerms.title.asNode(), DC_11.title.asNode(), SKOS.prefLabel.asNode(),
            RDFS.label.asNode());

    public static String getURI() {
        return CorePropertyFunctions.NS + "ontologyLabelProvider";
    }

    public static Node asNode() {
        return NodeFactory.createURI(getURI());
    }

    @Override
    protected String getText(Node node, ExecutionContext context) {
        List<DisplayLanguage> languages = LanguagesPreference.getDisplayLanguages();
        Graph graph = context.getActiveGraph();

        List<LiteralLabel> labels = getLabels(graph, node, PREFERRED_PROPERTY_ORDER, true);
        LiteralLabel label = getPreferredLanguageLabel(labels, languages);
        if (label != null) {
            return label.getLexicalForm();
        }
        return new String();
    }

    public static Set<Statement> getAllNameStatements(Resource ontology) {
        Model model = ontology.getModel();
        Set<Statement> result = Sets.newHashSet();
        List<Node> nameProperties = Lists.newArrayList();

        // get all label properties that can be used in naming an ontology
        for (Node nameProperty : OntologyLabelProviderPropertyFunction.PREFERRED_PROPERTY_ORDER) {
            Var varSubPropertyAny = Var.alloc("subPropertyAny");
            QueryBuilder qb = QueryBuilder.createSelect(true);
            qb.addTriplePattern(varSubPropertyAny, PathUtil.subPropertyOfAny, nameProperty);
            qb.addResultVar(varSubPropertyAny);
            for (ResultSet iter = qb.execSelect(model); iter.hasNext();) {
                Resource subProperty = iter.next().getResource(varSubPropertyAny.getName());
                nameProperties.add(subProperty.asNode());
            }
            nameProperties.add(nameProperty);
        }

        // get all statements giving a name to the ontology
        for (Node nameProperty : nameProperties) {
            if (nameProperty.isURI()) {
                Property prop = model.createProperty(nameProperty.getURI());
                result.addAll(ontology.listProperties(prop).toList());
            }
        }

        return result;
    }
}
