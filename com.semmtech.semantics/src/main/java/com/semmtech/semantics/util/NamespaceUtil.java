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


import java.util.List;
import java.util.Map;

import org.apache.xerces.util.XMLChar;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.impl.Util;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;


/**
 * Util class for working with model and their namespaces.
 * 
 * @author Mike Henrichs
 * 
 */
public final class NamespaceUtil {

    private NamespaceUtil() {

    }

    /**
     * Returns a list of NamespaceMapping objects based on the models prefix
     * mappings.
     * 
     * @param model
     *            The model for which the mappings are checked
     * @return a list of NamespaceMapping objects based on the models prefix
     *         mappings
     */
    public static List<NamespaceMapping> getNamespaceMappings(Model model) {
        Preconditions.checkNotNull(model, "Model cannot be null");
        return getNamespaceMappings(model, false);
    }

    public static List<NamespaceMapping> getNamespaceMappings(Model model,
            boolean includeNonPrefixed) {
        Preconditions.checkNotNull(model, "Model cannot be null");

        Map<String, String> nsPrefixMap = model.getNsPrefixMap();
        List<String> prefixed = Lists.newArrayList();
        List<NamespaceMapping> mappings = Lists.newArrayListWithCapacity(nsPrefixMap.size());
        for (String prefix : nsPrefixMap.keySet()) {
            String uri = nsPrefixMap.get(prefix);
            prefixed.add(uri);
            mappings.add(new NamespaceMapping(uri, prefix));
        }

        if (includeNonPrefixed) {
            for (String uri : getUsedNamespaces(model)) {
                if (!prefixed.contains(uri)) {
                    mappings.add(new NamespaceMapping(uri, null));
                }
            }
        }

        return mappings;
    }

    /**
     * Returns all namespace URIs either defined in a prefix mapping or used
     * within the model as subject, predicate, or object.
     * 
     * @param model
     *            The model to be scanned
     * @return all namespace URIs either defined in a prefix mapping or used
     *         within the model as subject, predicate, or object
     */
    public static List<String> getAvailableNamespaces(Model model) {
        Preconditions.checkNotNull(model, "Model cannot be null");
        List<String> namespaces = getUsedNamespaces(model);
        for (String uri : model.getNsPrefixMap().values()) {
            if (!namespaces.contains(uri)) {
                namespaces.add(uri);
            }
        }
        return namespaces;
    }

    /**
     * Return the namespaces used in either the subject or predicate or object
     * of all statements of the model.
     * 
     * @param model
     *            The model to be scanned
     * @return the namespaces used in either the subject or predicate or object
     *         of all statements of the model
     */
    public static List<String> getUsedNamespaces(Model model) {
        Preconditions.checkNotNull(model, "Model cannot be null");
        ImmutableSet.Builder<String> builder = ImmutableSet.builder();
        for (ExtendedIterator<Statement> iter = model.listStatements(); iter.hasNext();) {
            Statement stmt = iter.next();
            Resource subject = stmt.getSubject();
            Property predicate = stmt.getPredicate();
            RDFNode node = stmt.getObject();

            if (!subject.isAnon()) {
                builder.add(getNameSpace(subject));
            }
            if (!predicate.isAnon()) {
                builder.add(getNameSpace(predicate));
            }
            if (node.isResource() && !node.asResource().isAnon()) {
                builder.add(getNameSpace(node.asResource()));
            }
        }
        return Lists.newArrayList(builder.build());
    }

    /**
     * Returns true if the model has a statement containing a resource with the
     * given namespace URI as its namespace; otherwise false.
     * 
     * @param model
     *            The model to be scanned
     * @param uri
     *            The URI to be seacrhed for
     * @return true if the model has a statement containing a resource with the
     *         given namespace URI as its namespace; otherwise false
     */
    public static boolean usesNamespace(Model model, String uri) {
        for (ExtendedIterator<Statement> iter = model.listStatements(); iter.hasNext();) {
            Statement stmt = iter.next();
            Resource subject = stmt.getSubject();
            Property predicate = stmt.getPredicate();
            RDFNode node = stmt.getObject();

            if (!subject.isAnon() && getNameSpace(subject).equals(uri)) {
                return true;
            }
            else if (!predicate.isAnon() && getNameSpace(predicate).equals(uri)) {
                return true;
            }
            else if (node.isResource() && !node.asResource().isAnon()
                    && getNameSpace(node.asResource()).equals(uri)) {
                return true;
            }
        }
        return false;
    }

    /**
     * This method tries to find a single ontology uri which can be used. TODO:
     * Add additional methods which check the number of ontologies?
     * 
     * @param model
     * @return
     */
    public static String guessOntologyUri(Model model) {
        return null;
    }

    /**
     * @see #getNameSpace(String)
     */
    public static String getNameSpace(Resource res) {
        return getNameSpace(res.getURI());
    }

    /**
     * Extract the namespace from the given URI. This function takes in account
     * the RDF 1.1 standard that allows the local part of an IRI to starts with
     * a digit.
     * 
     * @param res
     * @return
     */
    public static String getNameSpace(String uri) {
        return uri.substring(0, splitNamespace(uri));
    }

    /**
     * Given an absolute URI, determine the split point between the namespace
     * part and the localname part. If there is no valid localname part then the
     * length of the string is returned. The algorithm tries to find the longest
     * NCName at the end of the uri, not immediately preceeded by the first
     * colon in the string.
     * 
     * @param uri
     * @return the index of the first character of the localname
     * 
     * @see Util#splitNamespace(String)
     */
    public static int splitNamespace(String uri) {

        // XML Namespaces 1.0:
        // A qname name is NCName ':' NCName
        // NCName ::= NCNameStartChar NCNameChar*
        // NCNameChar ::= NameChar - ':'
        // NCNameStartChar ::= Letter | '_'
        //
        // XML 1.0
        // NameStartChar ::= ":" | [A-Z] | "_" | [a-z] | [#xC0-#xD6] |
        // [#xD8-#xF6] | [#xF8-#x2FF] |
        // [#x370-#x37D] | [#x37F-#x1FFF] |
        // [#x200C-#x200D] | [#x2070-#x218F] |
        // [#x2C00-#x2FEF] | [#x3001-#xD7FF] |
        // [#xF900-#xFDCF] | [#xFDF0-#xFFFD] | [#x10000-#xEFFFF]
        // NameChar ::= NameStartChar | "-" | "." | [0-9] | #xB7 |
        // [#x0300-#x036F] | [#x203F-#x2040]
        // Name ::= NameStartChar (NameChar)*

        char ch;
        int lg = uri.length();
        if (lg == 0) {
            return 0;
        }

        int i = lg - 1;

        for (; i >= 1; i--) {
            ch = uri.charAt(i);

            if (notNameChar(ch)) {
                break;
            }
        }

        int j = i + 1;

        if (j >= lg)
            return lg;

        // Check we haven't split up a %-encoding.
        if (j >= 2 && uri.charAt(j - 2) == '%') {
            j = j + 1;
        }

        if (j >= 1 && uri.charAt(j - 1) == '%') {
            j = j + 2;
        }

        // Have found the leftmost NCNameChar from the
        // end of the URI string.
        // Now scan forward for an NCNameStartChar
        // The split must start with NCNameStart.
        for (; j < lg; j++) {
            ch = uri.charAt(j);
            // if (XMLChar.isNCNameStart(ch))
            // break ;

            if (XMLChar.isNCName(ch)) {
                // "mailto:" is special.
                // Keep part after mailto: at least one charcater.
                // Do a quick test before calling .startsWith
                // OLD: if ( uri.charAt(j - 1) == ':' && uri.lastIndexOf(':', j
                // - 2) == -1)
                if (j == 7 && uri.startsWith("mailto:")) {
                    continue; // split "mailto:me" as "mailto:m" and "e" !
                }

                break;
            }
        }
        return j;
    }

    /**
     * answer true iff this is not a legal NCName character, ie, is a possible
     * split-point start.
     */
    public static boolean notNameChar(char ch) {
        return !XMLChar.isNCName(ch);
    }
}
