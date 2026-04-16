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

package com.semmtech.plugin.semmweb.editor.reasoner;


import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.ReasonerFactory;


public class TaxonomyReasonerFactory implements ReasonerFactory {
    /** Single global instance of this factory */
    private static ReasonerFactory theInstance = new TaxonomyReasonerFactory();

    /** Static URI for this reasoner type */
    public static final String URI = "http://editor.semmweb.plugin.semmtech.com/2013/TaxonomyReasoner";

    /** Cache of the capabilities description */
    protected Model capabilities;

    /**
     * Return the single global instance of this factory
     */
    public static ReasonerFactory theInstance() {
        return theInstance;
    }

    /**
     * Constructor method that builds an instance of the associated Reasoner
     * 
     * @param configuration
     *            a set of arbitrary configuration information for the reasoner
     */
    public Reasoner create(Resource configuration) {
        return new TaxonomyReasoner(this, configuration);
    }

    @Override
    public Model getCapabilities() {
        return capabilities;
    }

    @Override
    public String getURI() {
        return URI;
    }
}
