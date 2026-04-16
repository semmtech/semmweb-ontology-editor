package com.semmtech.plugin.semmweb.core;


import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.hp.hpl.jena.rdf.arp.impl.RDFXMLParser;


public class ParserTest {
    public static void main(String[] args) {
        RDFXMLParser parser = RDFXMLParser.create();
        String xml = "<?xml version=\"1.0\"?>"
                + "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" "
                + "xml:base=\"http://www.opengis.net/ont/geosparql\"></rdf:RDF>";

        try {
            parser.allowRelativeURIs();
            // parser.getSAXParser().setDocumentHandler(new DocumentHandler() {
            //
            // @Override
            // public void startElement(String name, AttributeList atts)
            // throws SAXException {
            // // TODO Auto-generated method stub
            //
            // }
            //
            // @Override
            // public void startDocument() throws SAXException {
            // // TODO Auto-generated method stub
            //
            // }
            //
            // @Override
            // public void setDocumentLocator(Locator locator) {
            // // TODO Auto-generated method stub
            //
            // }
            //
            // @Override
            // public void processingInstruction(String target, String data)
            // throws SAXException {
            // // TODO Auto-generated method stub
            //
            // }
            //
            // @Override
            // public void ignorableWhitespace(char[] ch, int start, int length)
            // throws SAXException {
            // // TODO Auto-generated method stub
            //
            // }
            //
            // @Override
            // public void endElement(String name) throws SAXException {
            // // TODO Auto-generated method stub
            //
            // }
            //
            // @Override
            // public void endDocument() throws SAXException {
            // // TODO Auto-generated method stub
            //
            // }
            //
            // @Override
            // public void characters(char[] ch, int start, int length)
            // throws SAXException {
            // // TODO Auto-generated method stub
            //
            // }
            // });
            parser.parse(new InputSource(new ByteArrayInputStream(xml.getBytes())));

        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
