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

package com.semmtech.semantics.xml;


import java.util.Iterator;
import java.util.Map;

import javax.xml.namespace.NamespaceContext;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;


/**
 * The Class RelaticsNamespaceContext.
 * 
 * @author Mike Henrichs
 * @since 0.1
 */
public class RDFXMLNamespaceContext implements NamespaceContext {

    public static final String RDF_PREFIX = "rdf";
    public static final String RDFS_PREFIX = "rdfs";
    public static final String OWL_PREFIX = "owl";

    private final Map<String, String> mappings;

    /**
     * Instantiates a new relatics namespace context.
     */
    public RDFXMLNamespaceContext() {
        Builder<String, String> builder = ImmutableMap.builder();
        builder.put(RDF_PREFIX, RDF.getURI());
        builder.put(RDFS_PREFIX, RDFS.getURI());
        builder.put(OWL_PREFIX, OWL.getURI());

        mappings = builder.build();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * javax.xml.namespace.NamespaceContext#getNamespaceURI(java.lang.String)
     */
    @Override
    public String getNamespaceURI(String prefix) {
        Preconditions.checkNotNull(prefix, "Prefix cannot be null");
        if (mappings.containsKey(prefix))
            return mappings.get(prefix);
        return XMLConstants.NULL_NS_URI;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.xml.namespace.NamespaceContext#getPrefix(java.lang.String)
     */
    @Override
    public String getPrefix(String namespaceURI) {
        Preconditions.checkArgument(namespaceURI != null, "Namespace URI cannot be null");
        if (mappings.containsValue(namespaceURI)) {
            for (String prefix : mappings.keySet())
                if (mappings.get(prefix).equals(namespaceURI))
                    return prefix;
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.xml.namespace.NamespaceContext#getPrefixes(java.lang.String)
     */
    @SuppressWarnings("rawtypes")
    @Override
    public Iterator getPrefixes(String namespaceURI) {
        // TODO Auto-generated method stub
        return null;
    }
}
