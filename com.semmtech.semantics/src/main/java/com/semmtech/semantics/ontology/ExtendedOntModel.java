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

package com.semmtech.semantics.ontology;


import java.lang.reflect.Field;

import com.hp.hpl.jena.enhanced.BuiltinPersonalities;
import com.hp.hpl.jena.enhanced.Personality;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.ontology.Restriction;
import com.hp.hpl.jena.ontology.impl.OntModelImpl;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.vocabulary.RDFS;


public class ExtendedOntModel extends OntModelImpl {
    /**
     * <p>
     * Construct a new ontology model, using the given model as a base. The
     * document manager given in the specification object will be used to build
     * the imports closure of the model if its policy permits.
     * </p>
     * 
     * @param model
     *            The base model that may contain existing statements for the
     *            ontology. if it is null, a fresh model is created as the base.
     * @param spec
     *            A specification object that allows us to specify parameters
     *            and structure for the ontology model to be constructed.
     */
    public ExtendedOntModel(OntModelSpec spec, Model model) {
        super(spec, model);
        init();
    }

    /**
     * Construct a new ontology model from the given specification. The base
     * model is produced using the baseModelMaker.
     */
    public ExtendedOntModel(OntModelSpec spec) {
        super(spec);
        init();
    }

    protected Personality<RDFNode> createPersonality() {
        Personality<RDFNode> personality = BuiltinPersonalities.model.copy();
        personality.add(OntResource.class, ExtendedOntResource.factory);
        personality.add(Individual.class, ExtendedIndividual.factory);
        personality.add(OntProperty.class, ExtendedOntProperty.factory);
        personality.add(OntClass.class, ExtendedOntClass.factory);
        personality.add(Restriction.class, ExtendedRestriction.factory);
        return personality;
    }

    private void init() {
        setStrictMode(false);

        boolean overriddenPersonality = false;
        Field[] fields = this.getClass().getSuperclass().getSuperclass().getSuperclass()
                .getDeclaredFields();
        for (Field field : fields) {
            if (!field.getName().equals("personality")) {
                continue;
            }
            try {
                field.setAccessible(true);
                field.set(this, createPersonality());
                overriddenPersonality = true;
                field.setAccessible(false);
            }
            catch (IllegalArgumentException ex) {
                ex.printStackTrace();
            }
            catch (IllegalAccessException ex) {
                ex.printStackTrace();
            }
        }
        if (!overriddenPersonality) {
            throw new RuntimeException("Override of Personality failed in ExtendedOntModel.");
        }
    }

    @Override
    public boolean hasLoadedImport(String uri) {
        // Special case for RDFS, which OWL imports without a hash in the URI.
        if (RDFS.getURI().equals(uri + "#")) {
            uri = RDFS.getURI();
        }
        return super.hasLoadedImport(uri);
    }
}
