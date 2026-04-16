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

package com.semmtech.plugin.semmweb.core;


import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Display;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.semmtech.plugin.semmweb.core.extensionpoint.AbstractModelValidator;
import com.semmtech.plugin.semmweb.core.model.events.SubModelAddedEvent;


public class MinimalSubclassValidator extends AbstractModelValidator {
    public static final String ID = "com.semmtech.plugin.semmweb.core.validator.minimalSubclass";

    @SuppressWarnings("unused")
    private static Logger logger = Logger.getLogger(MinimalSubclassValidator.class);

    private static final Query VALIDATION_QUERY = createValidationQuery();

    public static final String MODEL_URI = "owl-implied";

    private static Query createValidationQuery() {
        String sparql = "";
        sparql += "PREFIX rdfs: <" + RDFS.getURI() + "> ";
        sparql += "PREFIX rdf: <" + RDF.getURI() + "> ";
        sparql += "PREFIX owl: <" + OWL.NS + "> ";
        sparql += "CONSTRUCT {";
        sparql += "     ?thing rdfs:subClassOf rdfs:Resource . ";
        sparql += "     ?rdfsClass rdfs:subClassOf rdfs:Resource . ";
        sparql += "     ?owlClass rdfs:subClassOf owl:Thing . ";
        sparql += "}";
        sparql += "WHERE { ";
        sparql += "  { ";
        sparql += "     ?rdfsClass rdf:type rdfs:Class . ";
        sparql += "     FILTER (!sameTerm(?rdfsClass, rdfs:Resource) && NOT EXISTS { ?rdfsClass rdfs:subClassOf ?superClass . } ) ";
        sparql += "  } ";
        sparql += "  UNION ";
        sparql += "  { ";
        sparql += "     ?owlClass rdf:type owl:Class . ";
        sparql += "     FILTER (!sameTerm(?owlClass, owl:Thing) && NOT EXISTS { ?owlClass rdfs:subClassOf ?superClass . ?superClass rdf:type owl:Class . } ) ";
        sparql += "  } ";
        sparql += "  UNION ";
        sparql += "  { ";
        sparql += "     ?thing rdf:type owl:Class . ";
        sparql += "     FILTER (sameTerm(?thing, owl:Thing) && NOT EXISTS { ?thing rdfs:subClassOf [] . } ) ";
        sparql += "  } ";
        sparql += "}";
        return QueryFactory.create(sparql);
    }

    public MinimalSubclassValidator() {
        super(ID, CorePlugin.getDefault().getPreferenceStore(), true);
    }

    @Override
    public String getName() {
        return "Subclass Hierarchy";
    }

    @Override
    public String getDescription() {
        return "Checks if classes have a subClassOf property to another resource.";
    }

    @Override
    public void validateModel(final OntModel model) {
        if (model == null) {
            return;
        }
        if (!isEnabled()) {
            return;
        }

        synchronized (model) {
            QueryExecution execution = QueryExecutionFactory.create(VALIDATION_QUERY, model);
            final Model additionalModel = execution.execConstruct();

            Display.getDefault().syncExec(new Runnable() {
                @Override
                public void run() {
                    if (!additionalModel.isEmpty()) {
                        model.addSubModel(additionalModel);
                        performAddSubModel(additionalModel, MODEL_URI);
                        model.notifyEvent(new SubModelAddedEvent(model, additionalModel, MODEL_URI,
                                "Sub model added due to validateModel in MinimalSubclassValidator"));
                    }
                }
            });
        }
    }
}
