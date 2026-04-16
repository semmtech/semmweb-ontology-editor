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


import java.util.Arrays;

import com.hp.hpl.jena.ontology.OntDocumentManager;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ModelMaker;
import com.hp.hpl.jena.reasoner.ReasonerFactory;
import com.semmtech.semantics.vocabulary.GTF;
import com.semmtech.semantics.vocabulary.Gellish;
import com.semmtech.semantics.vocabulary.SEMM;


public class ExtendedModelSpec extends OntModelSpec {

    public static final ExtendedModelSpec SEMM_MEM = new ExtendedModelSpec(
            ModelFactory.createMemModelMaker(), null, null, ExtendedProfileRegistry.SEMM_LANG);

    public ExtendedModelSpec(OntModelSpec spec) {
        super(spec);
        extendPrefixes();
    }

    public ExtendedModelSpec(ModelMaker importsMaker, OntDocumentManager docManager,
            ReasonerFactory reasonerFactory, String languageURI) {
        super(importsMaker, docManager, reasonerFactory, languageURI);
        extendPrefixes();
    }

    /**
     * Modifies the Jena defaults with additional SEMM and Gellish prefixes!
     */
    private void extendPrefixes() {
        int length = defaultPrefixes.length;
        String[][] modified = Arrays.copyOf(defaultPrefixes, length + 3);
        modified[length] = new String[] { "semm", SEMM.getURI() };
        modified[length + 1] = new String[] { "gtf", GTF.getURI() };
        modified[length + 2] = new String[] { "gellish", Gellish.getURI() };
        defaultPrefixes = modified;
    }
}
