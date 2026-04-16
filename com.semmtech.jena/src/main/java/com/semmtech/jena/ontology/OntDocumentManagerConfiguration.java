package com.semmtech.jena.ontology;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hp.hpl.jena.ontology.OntDocumentManager;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.util.FileUtils;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.vocabulary.OntDocManagerVocab;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.XSD;


/**
 * This configuration can be used to retrieve, modify and store settings used by
 * the OntDocumentManager.
 * 
 * @author Mike Henrichs
 * 
 */
public class OntDocumentManagerConfiguration {
    protected final Model model;
    protected Resource documentPolicy;
    protected final Map<String, Resource> ontologySpecs;

    public OntDocumentManagerConfiguration() {
        this(ModelFactory.createDefaultModel());
    }

    protected OntDocumentManagerConfiguration(Model model) {
        this.model = model;
        this.ontologySpecs = Maps.newHashMap();
        initialize();
    }

    private void initialize() {
        model.setNsPrefix("", OntDocManagerVocab.NS);
        model.setNsPrefix("rdf", RDF.getURI());
        model.setNsPrefix("rdfs", RDFS.getURI());
        model.setNsPrefix("xsd", XSD.getURI());

        for (ExtendedIterator<Resource> iter = model.listSubjectsWithProperty(RDF.type,
                OntDocManagerVocab.DocumentManagerPolicy); iter.hasNext();) {
            documentPolicy = iter.next();
            break;
        }
        for (ExtendedIterator<Resource> iter = model.listSubjectsWithProperty(RDF.type,
                OntDocManagerVocab.OntologySpec); iter.hasNext();) {
            Resource ontologySpec = iter.next();
            if (!ontologySpec.hasProperty(OntDocManagerVocab.publicURI)) {
                continue;
            }
            String publicUri = ontologySpec.getProperty(OntDocManagerVocab.publicURI).getObject()
                    .asResource().getURI();
            ontologySpecs.put(publicUri, ontologySpec);
        }
        if (documentPolicy == null) {
            documentPolicy = model.createResource(OntDocManagerVocab.DocumentManagerPolicy);
        }
    }

    public boolean getProcessImports() {
        if (documentPolicy.hasProperty(OntDocManagerVocab.processImports)) {
            return documentPolicy.getProperty(OntDocManagerVocab.processImports).getBoolean();
        }
        return true;
    }

    public void setProcessImports(boolean processImports) {
        documentPolicy.removeAll(OntDocManagerVocab.processImports);
        documentPolicy.addProperty(OntDocManagerVocab.processImports,
                model.createTypedLiteral(processImports));
    }

    public boolean getCacheModels() {
        if (documentPolicy.hasProperty(OntDocManagerVocab.cacheModels)) {
            return documentPolicy.getProperty(OntDocManagerVocab.cacheModels).getBoolean();
        }
        return true;
    }

    public void setCacheModels(boolean processImports) {
        documentPolicy.removeAll(OntDocManagerVocab.cacheModels);
        documentPolicy.addProperty(OntDocManagerVocab.cacheModels,
                model.createTypedLiteral(processImports));
    }

    public void addOntologySpec(OntologySpec ontologySpec) {
        addOntologySpec(ontologySpec.getPublicURI(), ontologySpec.getAltURL(),
                ontologySpec.getPrefix());
    }

    public void addOntologySpec(String publicUri) {
        addOntologySpec(publicUri, null, null);
    }

    public void addOntologySpec(String publicUri, String altUrl) {
        addOntologySpec(publicUri, altUrl, null);
    }

    public void addOntologySpec(String publicUri, String altUrl, String prefix) {
        removeOntologySpec(publicUri);
        Resource spec = model.createResource(OntDocManagerVocab.OntologySpec);
        spec.addProperty(OntDocManagerVocab.publicURI, model.createResource(publicUri));
        if (altUrl != null) {
            spec.addProperty(OntDocManagerVocab.altURL, model.createResource(altUrl));
        }
        if (prefix != null) {
            spec.addProperty(OntDocManagerVocab.prefix, model.createTypedLiteral(prefix));
        }
        ontologySpecs.put(publicUri, spec);
    }

    public void removeOntologySpec(String publicUri) {
        if (ontologySpecs.containsKey(publicUri)) {
            Resource spec = ontologySpecs.get(publicUri);
            spec.removeProperties();
            ontologySpecs.remove(publicUri);
        }
    }

    public void removeAllOntologySpecs() {
        for (String uri : ontologySpecs.keySet()) {
            removeOntologySpec(uri);
        }
    }

    public boolean containsOntologySpec(String publicUri) {
        return ontologySpecs.containsKey(publicUri);
    }

    protected Resource getOntologySpecResource(String publicUri) {
        Resource spec = null;
        if (!ontologySpecs.containsKey(publicUri)) {
            spec = model.createResource(OntDocManagerVocab.OntologySpec);
            spec.addProperty(OntDocManagerVocab.publicURI, model.createResource(publicUri));
            ontologySpecs.put(publicUri, spec);
        }
        else {
            spec = ontologySpecs.get(publicUri);
        }
        return spec;
    }

    public void setAltURL(String publicUri, String altUrl) {
        Resource spec = getOntologySpecResource(publicUri);
        spec.removeAll(OntDocManagerVocab.altURL);
        spec.addProperty(OntDocManagerVocab.altURL, model.createResource(altUrl));
    }

    public boolean hasAltURL(String publicUri) {
        if (!ontologySpecs.containsKey(publicUri)) {
            return false;
        }
        return ontologySpecs.get(publicUri).hasProperty(OntDocManagerVocab.altURL);
    }

    /**
     * Returns the AltURL property of the given public URI; otherwise false
     * 
     * @param publicUri
     * @return
     */
    public String getAltURL(String publicUri) {
        if (hasAltURL(publicUri)) {
            Resource spec = ontologySpecs.get(publicUri);
            return spec.getProperty(OntDocManagerVocab.altURL).getObject().asResource().getURI();
        }
        return null;
    }

    /**
     * Returns the uris that have a mapping to the given alternateUrl
     */
    public List<String> getMappingUris(String altUrl) {
        List<String> uris = Lists.newArrayList();

        for (Resource stmt : model.listSubjectsWithProperty(OntDocManagerVocab.altURL).toList()) {
            String url = stmt.getProperty(OntDocManagerVocab.altURL).getObject().toString();

            if (Objects.equal(altUrl, url)) {
                String uri = stmt.getProperty(OntDocManagerVocab.publicURI).getObject().toString();
                uris.add(uri);
            }
        }
        return uris;
    }

    /**
     * Returns the prefix for the given public URI; otherwise null
     * 
     * @param publicUri
     * @return
     */
    public String getPrefix(String publicUri) {
        if (ontologySpecs.containsKey(publicUri)) {
            Resource spec = ontologySpecs.get(publicUri);
            if (spec.hasProperty(OntDocManagerVocab.prefix)) {
                return spec.getProperty(OntDocManagerVocab.prefix).getString();
            }
        }
        return null;
    }

    public List<String> listPublicURIs() {
        List<String> uris = Lists.newArrayList();
        uris.addAll(ontologySpecs.keySet());
        return uris;
    }

    public List<OntologySpec> listOntologySpecs() {
        List<OntologySpec> result = Lists.newArrayList();
        for (String publicUri : ontologySpecs.keySet()) {
            OntologySpec spec = new OntologySpec(publicUri);
            spec.setAltURL(getAltURL(publicUri));
            spec.setPrefix(getPrefix(publicUri));
            result.add(spec);
        }
        return result;
    }

    public void setLanguage(String publicUri, String language) {
        Resource spec = getOntologySpecResource(publicUri);
        spec.removeAll(OntDocManagerVocab.language);
        if (language != null) {
            spec.addProperty(OntDocManagerVocab.language, model.createResource(language));
        }
    }

    public void setPrefix(String publicUri, String prefix) {
        Resource spec = getOntologySpecResource(publicUri);
        spec.removeAll(OntDocManagerVocab.prefix);
        if (prefix != null) {
            spec.addProperty(OntDocManagerVocab.prefix, model.createTypedLiteral(prefix));
        }
    }

    public void configureManager(OntDocumentManager manager) {
        manager.configure(model);
    }

    public void write(OutputStream stream, String lang) {
        model.write(stream, lang);
    }

    public OntDocumentManager createManager() {
        OntDocumentManager manager = new OntDocumentManager();
        configureManager(manager);
        return manager;
    }

    public static OntDocumentManagerConfiguration read(Model model) {
        return new OntDocumentManagerConfiguration(model);
    }

    public static OntDocumentManagerConfiguration read(String filename) throws IOException {
        Model model = ModelFactory.createDefaultModel();
        String lang = FileUtils.guessLang(filename, FileUtils.langTurtle);

        // close the stream, Jena implementations doen't close the readers
        // stream
        try (FileInputStream stream = new FileInputStream(new File(filename))) {
            model.read(stream, null, lang);
            return read(model);
        }
    }
}
