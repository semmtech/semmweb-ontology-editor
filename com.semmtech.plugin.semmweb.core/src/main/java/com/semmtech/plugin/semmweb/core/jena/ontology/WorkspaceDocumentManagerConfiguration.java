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

package com.semmtech.plugin.semmweb.core.jena.ontology;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import com.google.common.collect.Lists;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.util.FileUtils;
import com.hp.hpl.jena.vocabulary.OntDocManagerVocab;
import com.semmtech.jena.ontology.OntDocumentManagerConfiguration;
import com.semmtech.jena.ontology.OntologySpec;
import com.semmtech.plugin.semmweb.core.jena.vocabulary.WorkspaceDocManagerVocab;


/**
 * 
 * @author Sander Stolk
 */
public class WorkspaceDocumentManagerConfiguration extends OntDocumentManagerConfiguration {

    public WorkspaceDocumentManagerConfiguration() {
        super();
    }

    protected WorkspaceDocumentManagerConfiguration(Model model) {
        super(model);
    }

    @Override
    public void addOntologySpec(String publicUri, String altUrl, String prefix) {
        WorkspaceOntologySpec spec = new WorkspaceOntologySpec(publicUri);
        spec.setPrefix(prefix);
        spec.setAltURL(altUrl);
        addOntologySpec(spec);
    }

    public void addOntologySpec(String publicUri, String prefix, String externalUrl,
            String workspaceUrl) {
        WorkspaceOntologySpec spec = new WorkspaceOntologySpec(publicUri, prefix, externalUrl,
                workspaceUrl);
        addOntologySpec(spec);
    }

    @Override
    public void addOntologySpec(OntologySpec spec) {
        if (spec instanceof WorkspaceOntologySpec) {
            addOntologySpec((WorkspaceOntologySpec) spec);
        }
        else {
            super.addOntologySpec(spec);
        }
    }

    public void addOntologySpec(WorkspaceOntologySpec spec) {
        String publicUri = spec.getPublicURI();
        if (publicUri == null) {
            return;
        }

        removeOntologySpec(publicUri);

        Resource rSpec = model.createResource(OntDocManagerVocab.OntologySpec);
        rSpec.addProperty(OntDocManagerVocab.publicURI, model.createResource(publicUri));
        if (spec.getPrefix() != null) {
            rSpec.addProperty(OntDocManagerVocab.prefix, model.createTypedLiteral(spec.getPrefix()));
        }
        if (spec.getAltURL() != null) {
            rSpec.addProperty(OntDocManagerVocab.altURL, model.createResource(spec.getAltURL()));
        }
        if (spec.getExternalAltURL() != null) {
            rSpec.addProperty(WorkspaceDocManagerVocab.externalAltURL,
                    model.createResource(spec.getExternalAltURL()));
        }
        if (spec.getWorkspaceAltURL() != null) {
            rSpec.addProperty(WorkspaceDocManagerVocab.workspaceAltURL,
                    model.createResource(spec.getWorkspaceAltURL()));
        }
        ontologySpecs.put(publicUri, rSpec);
    }

    @Override
    public void setAltURL(String publicUri, String altUrl) {
        WorkspaceOntologySpec wSpec = getOntologySpec(publicUri);
        wSpec.setAltURL(altUrl);
        setAltURLs(wSpec);
    }

    public boolean hasExternalAltURL(String publicUri) {
        return (getExternalAltURL(publicUri) != null);
    }

    public String getExternalAltURL(String publicUri) {
        WorkspaceOntologySpec wSpec = getOntologySpec(publicUri);
        return wSpec.getExternalAltURL();
    }

    public void setExternalAltURL(String publicUri, String url) {
        WorkspaceOntologySpec wSpec = getOntologySpec(publicUri);
        wSpec.setExternalAltURL(url);
        setAltURLs(wSpec);
    }

    public boolean hasWorkspaceAltURL(String publicUri) {
        return (getWorkspaceAltURL(publicUri) != null);
    }

    public String getWorkspaceAltURL(String publicUri) {
        WorkspaceOntologySpec wSpec = getOntologySpec(publicUri);
        return wSpec.getWorkspaceAltURL();
    }

    public void setWorkspaceAltURL(String publicUri, String url) {
        WorkspaceOntologySpec wSpec = getOntologySpec(publicUri);
        wSpec.setWorkspaceAltURL(url);
        setAltURLs(wSpec);
    }

    protected void setAltURLs(WorkspaceOntologySpec spec) {
        String publicUri = spec.getPublicURI();
        Resource rSpec = getOntologySpecResource(publicUri);
        rSpec.removeAll(OntDocManagerVocab.altURL);
        rSpec.removeAll(WorkspaceDocManagerVocab.externalAltURL);
        rSpec.removeAll(WorkspaceDocManagerVocab.workspaceAltURL);
        if (spec.getAltURL() != null) {
            rSpec.addProperty(OntDocManagerVocab.altURL, model.createResource(spec.getAltURL()));
        }
        if (spec.getExternalAltURL() != null) {
            rSpec.addProperty(WorkspaceDocManagerVocab.externalAltURL,
                    model.createResource(spec.getExternalAltURL()));
        }
        if (spec.getWorkspaceAltURL() != null) {
            rSpec.addProperty(WorkspaceDocManagerVocab.workspaceAltURL,
                    model.createResource(spec.getWorkspaceAltURL()));
        }
    }

    public List<WorkspaceOntologySpec> listWorkspaceOntologySpecs() {
        List<WorkspaceOntologySpec> result = Lists.newArrayList();
        for (String publicUri : ontologySpecs.keySet()) {
            WorkspaceOntologySpec spec = getOntologySpec(publicUri);
            result.add(spec);
        }
        return result;
    }

    @Override
    public List<OntologySpec> listOntologySpecs() {
        List<OntologySpec> result = Lists.newArrayList();
        result.addAll(listWorkspaceOntologySpecs());
        return result;
    }

    public void setDisabledImport(String uri, boolean disabled) {

    }

    public boolean isDisabledImport(String uri) {
        return false;
    }

    public List<String> listDisabledImports() {
        List<String> result = Lists.newArrayList();

        return result;
    }

    public WorkspaceOntologySpec getOntologySpec(String publicUri) {
        WorkspaceOntologySpec result = new WorkspaceOntologySpec(publicUri);
        if (publicUri != null) {
            result.setPrefix(getPrefix(publicUri));
            String altURL = getAltURL(publicUri, OntDocManagerVocab.altURL);
            if (altURL != null) {
                result.setAltURL(altURL);
            }
            altURL = getAltURL(publicUri, WorkspaceDocManagerVocab.externalAltURL);
            if (altURL != null) {
                result.setExternalAltURL(altURL);
            }
            altURL = getAltURL(publicUri, WorkspaceDocManagerVocab.workspaceAltURL);
            if (altURL != null) {
                result.setWorkspaceAltURL(altURL);
            }
        }
        return result;
    }

    private String getAltURL(String publicUri, Property altUrlProperty) {
        Resource spec = ontologySpecs.get(publicUri);
        if (spec != null && spec.hasProperty(altUrlProperty)) {
            return spec.getPropertyResourceValue(altUrlProperty).getURI();
        }
        return null;
    }

    public static WorkspaceDocumentManagerConfiguration read(Model model) {
        return new WorkspaceDocumentManagerConfiguration(model);
    }

    public static WorkspaceDocumentManagerConfiguration read(String filename) throws IOException {
        Model model = ModelFactory.createDefaultModel();
        String lang = FileUtils.guessLang(filename, FileUtils.langTurtle);

        // Close the stream (Jena implementations dont close them)
        try (FileInputStream stream = new FileInputStream(new File(filename))) {
            model.read(stream, null, lang);
            return read(model);
        }
    }
}
