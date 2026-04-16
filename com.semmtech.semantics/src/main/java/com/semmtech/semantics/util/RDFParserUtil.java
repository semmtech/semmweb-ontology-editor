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


import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Iterator;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.google.common.base.Strings;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.n3.IRIResolver;
import com.hp.hpl.jena.n3.turtle.ParserBase;
import com.hp.hpl.jena.n3.turtle.TurtleEventHandler;
import com.hp.hpl.jena.n3.turtle.parser.ParseException;
import com.hp.hpl.jena.n3.turtle.parser.TurtleParser;
import com.hp.hpl.jena.util.FileUtils;
import com.hp.hpl.jena.vocabulary.RDF;


/**
 * Util class containing functionality requiring the parsing of a document.
 * 
 * @author Mike Henrichs
 * 
 */
public final class RDFParserUtil {
    private static final Logger logger = Logger.getLogger(RDFParserUtil.class);

    private RDFParserUtil() {
    }

    public static String retrieveBaseURI(InputStream input, String lang) throws IOException {
        String baseUri = null;
        if (lang.equals(FileUtils.langTurtle) || lang.equals(FileUtils.langN3)) {
            baseUri = retrieveBaseFromTurtle(input);
        }
        else if (lang.equals(FileUtils.langXML) || lang.equals(FileUtils.langXMLAbbrev)) {
            baseUri = retrieveBaseFromRDFXML(input);
        }
        return baseUri;
    }

    /**
     * This is kind of a hack, to retrieve the baseUri from the internal
     * workings of a TurtleParser. The retrieval uses reflection to access the
     * internal field "resolver" which is a IRIResolver.
     * 
     * Future solution should be to extend the Jena source code to include a
     * method for setBase in the TurtleEventHandler, which is trigger by the
     * setBase of the TurtleParser...
     * 
     * @param stream
     * @return
     */
    public static String retrieveBaseFromTurtle(InputStream stream) {
        TurtleParser parser = new TurtleParser(stream);
        parser.setEventHandler(new BaseTurtleEventHandler());
        try {
            Field resolverField = ParserBase.class.getDeclaredField("resolver");
            resolverField.setAccessible(true);
            IRIResolver resolver = (IRIResolver) resolverField.get(parser);
            String initialUri = resolver.getBaseIRI();

            parser.parse();
            resolver = (IRIResolver) resolverField.get(parser);
            String baseUri = resolver.getBaseIRI();
            resolverField.setAccessible(false);

            if (!initialUri.equals(baseUri)) {
                return baseUri;
            }
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        catch (SecurityException e) {
            e.printStackTrace();
        }
        catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * This Exception is thrown when the parsing for the xml:base attribute has
     * been completed.
     * 
     * @author Mike Henrichs
     * 
     */
    private static class ParsingAbortedException extends SAXParseException {
        /**
         * 
         */
        private static final long serialVersionUID = 4569711089346653744L;

        public ParsingAbortedException() {
            super("Parsing has been aborted by handler", null);
        }
    }

    /**
     * This ContentHandler is used to discover the xml:base of an XML document.
     * 
     * @author Mike Henrichs
     * 
     */
    private static class BaseURIHandler extends DefaultHandler {
        private String baseUri;
        private boolean done;

        public BaseURIHandler() {

        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes atts)
                throws SAXException {
            if (done) {
                return;
            }
            if ("rdf:RDF".equals(qName)) {
                baseUri = atts.getValue("xml:base");
                done = true;
                // We found what we came for; by throwing an SAXParseException
                // further parsing is cancelled
                fatalError(new ParsingAbortedException());
            }

        }

        public String getBaseURI() {
            return baseUri;
        }
    }

    /**
     * Returns the xml:base from an XML document.
     * 
     * @param stream
     * @return
     */
    public static String retrieveBaseFromRDFXML(InputStream stream) {
        BaseURIHandler handler = new BaseURIHandler();
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setNamespaceAware(true);
            SAXParser saxParser = factory.newSAXParser();
            XMLReader xmlReader = saxParser.getXMLReader();
            xmlReader.setContentHandler(handler);
            xmlReader.parse(new InputSource(stream));
        }
        catch (ParsingAbortedException ex) {
            // No problem; intended for performance!
            if (!Strings.isNullOrEmpty(handler.getBaseURI())) {
                logger.trace(String.format("Found xml:base in document \"%s\"",
                        handler.getBaseURI()));
            }
            else {
                logger.trace("Did not find an xml:base attribute in document");
            }
        }
        catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        catch (SAXException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return handler.getBaseURI();
    }

    /**
     * NamespaceContext used for finding base URI in RDF/XML files.
     * 
     * @author Mike Henrichs
     * 
     */
    @SuppressWarnings("unused")
    private static class RDFXMLNamespaceContext implements NamespaceContext {
        private static final String NULL_NS_URI = "";
        private static final String XML_NAMESPACE_URI = "http://www.w3.org/XML/1998/namespace";
        private static final String RDF_PREFIX = "rdf";
        private static final String XML_PREFIX = "xml";

        @SuppressWarnings("rawtypes")
        @Override
        public Iterator getPrefixes(String namespaceUri) {
            return null;
        }

        @Override
        public String getPrefix(String namespaceUri) {
            if (namespaceUri.equals(RDF.getURI())) {
                return RDF_PREFIX;
            }
            return null;
        }

        @Override
        public String getNamespaceURI(String prefix) {
            if (prefix.equals(RDF_PREFIX)) {
                return RDF.getURI();
            }
            else if (prefix.equals(XML_PREFIX)) {
                return XML_NAMESPACE_URI;
            }
            return null;
        }
    }

    /**
     * Default implementer of the TurtleEventHandler interface.
     * 
     * @author Mike Henrichs
     * 
     */
    public static class BaseTurtleEventHandler implements TurtleEventHandler {

        @Override
        public void triple(int line, int col, Triple triple) {

        }

        @Override
        public void prefix(int line, int col, String prefix, String iri) {

        }

        @Override
        public void startFormula(int line, int col) {

        }

        @Override
        public void endFormula(int line, int col) {

        }
    }
}
