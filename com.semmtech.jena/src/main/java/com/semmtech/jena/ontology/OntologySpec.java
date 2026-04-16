package com.semmtech.jena.ontology;


public class OntologySpec {
    protected String publicUri;
    protected String prefix;
    protected String altUrl;

    public OntologySpec() {
        this(null, null, null);
    }

    public OntologySpec(String publicUri) {
        this(publicUri, null, null);
    }

    public OntologySpec(String publicUri, String altUrl, String prefix) {
        this.publicUri = publicUri;
        this.prefix = prefix;
        this.altUrl = altUrl;
    }

    public String getPublicURI() {
        return publicUri;
    }

    public void setPublicURI(String publicUri) {
        this.publicUri = publicUri;
    }

    public String getAltURL() {
        return altUrl;
    }

    public void setAltURL(String altUrl) {
        this.altUrl = altUrl;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public int hashCode() {
        // TODO: this is violating the contract of equals and hashCode: objects
        // which are .equals() MUST have the same .hashCode().
        return publicUri.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof OntologySpec)) {
            return false;
        }
        return equals((OntologySpec) obj);
    }

    public boolean equals(OntologySpec other) {
        if (!publicUri.equals(other.getPublicURI())) {
            return false;
        }
        // Prefix
        if (prefix == null && other.getPrefix() != null) {
            return false;
        }
        if (prefix != null && other.getPrefix() == null) {
            return false;
        }
        if (prefix != null && !prefix.equals(other.getPrefix())) {
            return false;
        }
        // AltURL
        if (altUrl == null && other.getAltURL() != null) {
            return false;
        }
        if (altUrl != null && other.getAltURL() == null) {
            return false;
        }
        if (altUrl != null && !altUrl.equals(other.getAltURL())) {
            return false;
        }
        return true;
    }

    public static final OntologySpec RDF = new OntologySpec(
            "http://www.w3.org/1999/02/22-rdf-syntax-ns#", null, "rdf");
    public static final OntologySpec RDFS = new OntologySpec(
            "http://www.w3.org/2000/01/rdf-schema#", null, "rdfs");
    public static final OntologySpec OWL = new OntologySpec("http://www.w3.org/2002/07/owl#", null,
            "owl");

    public static final OntologySpec DC = new OntologySpec("http://purl.org/dc/elements/1.1/",
            null, "dc");
    public static final OntologySpec DCTERMS = new OntologySpec(
            "http://purl.org/dc/terms/",
            "https://www.dublincore.org/specifications/dublin-core/dcmi-terms/dublin_core_terms.ttl",
            "dcterms");

    public static final OntologySpec SKOS = new OntologySpec(
            "http://www.w3.org/2004/02/skos/core#", "http://www.w3.org/TR/skos-reference/skos.rdf",
            "skos");
    // public static final OntologySpec SKOS2 = new
    // OntologySpec("http://www.w3.org/2004/02/skos/core",
    // "http://www.w3.org/TR/skos-reference/skos.rdf", "skos");
    public static final OntologySpec SKOSXL = new OntologySpec(
            "http://www.w3.org/2008/05/skos-xl#",
            "http://www.w3.org/2009/08/skos-reference/skos-xl.rdf", "skos-xl");
    public static final OntologySpec XSD = new OntologySpec("http://www.w3.org/2001/XMLSchema#",
            null, "xsd");
    public static final OntologySpec FOAF = new OntologySpec("http://xmlns.com/foaf/0.1/",
            "http://xmlns.com/foaf/spec/index.rdf", "foaf");
    public static final OntologySpec VANN = new OntologySpec("http://purl.org/vocab/vann/",
            "https://vocab.org/vann/vann-vocab-20100607.rdf", "vann");
    public static final OntologySpec CC = new OntologySpec("http://creativecommons.org/ns#",
            "https://creativecommons.org/schema.rdf", "cc");
    public static final OntologySpec SEMM = new OntologySpec(
            "http://www.semmweb.com/ns/public/2012/09/12/semm/", null, "semm");
    public static final OntologySpec SH = new OntologySpec("http://www.w3.org/ns/shacl#",
            "https://www.w3.org/ns/shacl.ttl", "sh");

}
