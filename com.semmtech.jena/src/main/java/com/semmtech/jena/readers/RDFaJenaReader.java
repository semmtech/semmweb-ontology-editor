package com.semmtech.jena.readers;


import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.Map;

import org.cyberneko.html.parsers.SAXParser;
import org.semarglproject.jena.core.sink.AbstractJenaSink;
import org.semarglproject.jena.core.sink.JenaSink;
import org.semarglproject.rdf.ParseException;
import org.semarglproject.rdf.rdfa.RdfaParser;
import org.semarglproject.source.StreamProcessor;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFErrorHandler;
import com.hp.hpl.jena.rdf.model.impl.RDFReaderFImpl;
import com.hp.hpl.jena.shared.JenaException;


public class RDFaJenaReader extends JenaReader {
    private final StreamProcessor streamProcessor;
    public static final Map<String, String> knownPrefixes;

    static {
        knownPrefixes = HashBiMap.create();
        knownPrefixes.put("grddl", "http://www.w3.org/2003/g/data-view#");
        knownPrefixes.put("ma", "http://www.w3.org/ns/ma-ont#");
        knownPrefixes.put("owl", "http://www.w3.org/2002/07/owl#");
        knownPrefixes.put("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
        knownPrefixes.put("rdfa", "http://www.w3.org/ns/rdfa#");
        knownPrefixes.put("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
        knownPrefixes.put("rif", "http://www.w3.org/2007/rif#");
        knownPrefixes.put("skos", "http://www.w3.org/2004/02/skos/core#");
        knownPrefixes.put("skosxl", "http://www.w3.org/2008/05/skos-xl#");
        knownPrefixes.put("wdr", "http://www.w3.org/2007/05/powder#");
        knownPrefixes.put("void", "http://rdfs.org/ns/void#");
        knownPrefixes.put("wdrs", "http://www.w3.org/2007/05/powder-s#");
        knownPrefixes.put("xhv", "http://www.w3.org/1999/xhtml/vocab#");
        knownPrefixes.put("xml", "http://www.w3.org/XML/1998/namespace");
        knownPrefixes.put("xsd", "http://www.w3.org/2001/XMLSchema#");
        // non-rec w3c
        knownPrefixes.put("sd", "http://www.w3.org/ns/sparql-service-description#");
        knownPrefixes.put("org", "http://www.w3.org/ns/org#");
        knownPrefixes.put("gldp", "http://www.w3.org/ns/people#");
        knownPrefixes.put("cnt", "http://www.w3.org/2008/content#");
        knownPrefixes.put("dcat", "http://www.w3.org/ns/dcat#");
        knownPrefixes.put("earl", "http://www.w3.org/ns/earl#");
        knownPrefixes.put("ht", "http://www.w3.org/2006/http#");
        knownPrefixes.put("ptr", "http://www.w3.org/2009/pointers#");
        // widely used
        knownPrefixes.put("cc", "http://creativecommons.org/ns#");
        knownPrefixes.put("ctag", "http://commontag.org/ns#");
        knownPrefixes.put("dc", "http://purl.org/dc/terms/");
        knownPrefixes.put("foaf", "http://xmlns.com/foaf/0.1/");
        knownPrefixes.put("gr", "http://purl.org/goodrelations/v1#");
        knownPrefixes.put("ical", "http://www.w3.org/2002/12/cal/icaltzd#");
        knownPrefixes.put("og", "http://ogp.me/ns#");
        knownPrefixes.put("rev", "http://purl.org/stuff/rev#");
        knownPrefixes.put("sioc", "http://rdfs.org/sioc/ns#");
        knownPrefixes.put("v", "http://rdf.data-vocabulary.org/#");
        knownPrefixes.put("vcard", "http://www.w3.org/2006/vcard/ns#");
        knownPrefixes.put("schema", "http://schema.org/");
    }

    /**
     * Default constructor. Creates RDFa parser in 1.1 mode with disabled
     * vocabulary expansion feature. Properties can be changed using
     * {@link #setProperty(String, Object)} calls with property keys from
     * {@link RdfaParser}.
     * 
     * @throws SAXNotSupportedException
     * @throws SAXNotRecognizedException
     */
    public RDFaJenaReader() throws SAXNotRecognizedException, SAXNotSupportedException {
        XMLReader htmlReader = new SAXParser();
        htmlReader.setFeature("http://cyberneko.org/html/features/override-namespaces", false);
        htmlReader.setFeature("http://cyberneko.org/html/features/balance-tags", true);
        htmlReader.setFeature("http://cyberneko.org/html/features/scanner/allow-selfclosing-tags",
                true);
        htmlReader.setProperty("http://cyberneko.org/html/properties/names/elems", "lower");

        streamProcessor = new StreamProcessor(RdfaParser.connect(JenaSink.connect(null)));
        streamProcessor.setProperty(RdfaParser.ENABLE_VOCAB_EXPANSION, true);
        streamProcessor.setProperty(RdfaParser.RDFA_VERSION_PROPERTY,
                org.semarglproject.vocab.RDFa.VERSION_11);
        streamProcessor.setProperty(StreamProcessor.XML_READER_PROPERTY, htmlReader);

    }

    /**
     * Injects information about Semargl RDFa parser to Jena framework.
     */
    public static void inject() {
        RDFReaderFImpl.setBaseReaderClassName("RDFA", RDFaJenaReader.class.getName());
    }

    @Override
    public void read(Model model, Reader r, String base) {
        streamProcessor.setProperty(AbstractJenaSink.OUTPUT_MODEL_PROPERTY, model);
        try {
            streamProcessor.process(r, base);
        }
        catch (ParseException e) {
            throw new JenaException(e);
        }
    }

    @Override
    public void read(Model model, InputStream r, String base) {
        boolean txSupported = model.supportsTransactions();

        try (InputStreamReader reader = new InputStreamReader(r)) {
            if (txSupported) {
                model.begin();
            }
            read(model, reader, base);
        }
        catch (IOException e) {
            throw new JenaException(e);
        }
        finally {
            if (txSupported) {
                model.abort();
            }
        }
        if (txSupported) {
            model.commit();
        }
    }

    @Override
    public void read(Model model, String url) {
        try {
            URL uri = new URL(url);
            read(model, uri.openStream(), url);
        }
        catch (IOException e) {
            throw new JenaException(e);
        }
    }

    @Override
    public Object setProperty(String propName, Object propValue) {
        return streamProcessor.setProperty(propName, propValue);
    }

    @Override
    public RDFErrorHandler setErrorHandler(RDFErrorHandler errHandler) {
        return null;
    }

    public static void addNsPrefixes(Model m) {
        BiMap<String, String> inverseMap = ((HashBiMap<String, String>) knownPrefixes).inverse();
        for (String ns : m.listNameSpaces().toList()) {
            String prefix = inverseMap.get(ns);

            if (prefix != null) {
                m.setNsPrefix(prefix, ns);
            }
        }
    }
}
