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

package com.semmtech.semantics.util;


import com.hp.hpl.jena.shared.PrefixMapping;
import com.hp.hpl.jena.vocabulary.DC_11;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.XSD;


public interface PrefixMappingUtils {
    /**
     * A PrefixMapping that contains the "standard" prefixes we know about, viz
     * rdf, rdfs, dc, rss, vcard, and owl.
     */
    public static final PrefixMapping Standard = PrefixMapping.Factory.create()
            .setNsPrefix("rdfs", RDFS.getURI()).setNsPrefix("rdf", RDF.getURI())
            .setNsPrefix("dc", DC_11.getURI()).setNsPrefix("owl", OWL.getURI())
            .setNsPrefix("xsd", XSD.getURI()).lock();
}
