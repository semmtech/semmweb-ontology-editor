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


import com.google.common.base.Strings;
import com.semmtech.StringUtil;
import com.semmtech.jena.ontology.OntologySpec;
import com.semmtech.plugin.semmweb.core.util.WorkspaceUtils;


public class WorkspaceOntologySpec extends OntologySpec {
    protected String externalAltUrl;
    protected String workspaceAltUrl;

    public WorkspaceOntologySpec() {
        super();
    }

    public WorkspaceOntologySpec(String publicUri) {
        super(publicUri);
    }

    public WorkspaceOntologySpec(String publicUri, String prefix, String externalAltUrl,
            String workspaceAltUrl) {
        super(publicUri);
        this.prefix = prefix;
        this.externalAltUrl = Strings.emptyToNull(externalAltUrl);
        this.workspaceAltUrl = Strings.emptyToNull(workspaceAltUrl);

        updateAltURL();
    }

    protected void updateAltURL() {
        if (!Strings.isNullOrEmpty(workspaceAltUrl)) {
            this.altUrl = workspaceAltUrl;
        }
        else {
            this.altUrl = externalAltUrl;
        }
    }

    /**
     * Checks whether the url is a workspace file or an external url, and sets
     * the appropriate alternate url fields accordingly. If url is null or the
     * empty String, both the workspace alt url and the external alt url will be
     * cleared.
     */
    @Override
    public void setAltURL(String url) {
        url = Strings.emptyToNull(url);
        if (url == null) {
            this.externalAltUrl = null;
            this.workspaceAltUrl = null;
        }
        else {
            if (WorkspaceUtils.isWorkspaceFile(url)) {
                this.workspaceAltUrl = url;
            }
            else {
                this.externalAltUrl = url;
            }
        }
        updateAltURL();
    }

    public String getExternalAltURL() {
        return externalAltUrl;
    }

    public void setExternalAltURL(String url) {
        url = Strings.emptyToNull(url);
        this.externalAltUrl = url;
        updateAltURL();
    }

    public String getWorkspaceAltURL() {
        return workspaceAltUrl;
    }

    public void setWorkspaceAltURL(String url) {
        url = Strings.emptyToNull(url);
        this.workspaceAltUrl = url;
        updateAltURL();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof WorkspaceOntologySpec)) {
            return false;
        }
        return equals((WorkspaceOntologySpec) obj);
    }

    public boolean equals(WorkspaceOntologySpec other) {
        if (!StringUtil.equals(publicUri, other.publicUri)) {
            return false;
        }
        if (!StringUtil.equals(prefix, other.prefix)) {
            return false;
        }
        if (!StringUtil.equals(externalAltUrl, other.externalAltUrl)) {
            return false;
        }
        if (!StringUtil.equals(workspaceAltUrl, other.workspaceAltUrl)) {
            return false;
        }
        if (!StringUtil.equals(altUrl, other.altUrl)) {
            return false;
        }
        return true;
    }

    public static final WorkspaceOntologySpec RDF = new WorkspaceOntologySpec(
            "http://www.w3.org/1999/02/22-rdf-syntax-ns#", "rdf", null, null);
    public static final WorkspaceOntologySpec RDFS = new WorkspaceOntologySpec(
            "http://www.w3.org/2000/01/rdf-schema#", "rdfs", null, null);
    public static final WorkspaceOntologySpec OWL = new WorkspaceOntologySpec(
            "http://www.w3.org/2002/07/owl#", "owl", null, null);

    public static final WorkspaceOntologySpec DC = new WorkspaceOntologySpec(
            "http://purl.org/dc/elements/1.1/", "dc", null, null);
    public static final WorkspaceOntologySpec DCTERMS = new WorkspaceOntologySpec(
            "http://purl.org/dc/terms/", "dcterms", null, null);

    public static final WorkspaceOntologySpec SKOS = new WorkspaceOntologySpec(
            "http://www.w3.org/2004/02/skos/core#", "skos",
            "http://www.w3.org/TR/skos-reference/skos.rdf", null);
    public static final WorkspaceOntologySpec XSD = new WorkspaceOntologySpec(
            "http://www.w3.org/2001/XMLSchema#", "xsd", null, null);
    public static final WorkspaceOntologySpec FOAF = new WorkspaceOntologySpec(
            "http://xmlns.com/foaf/0.1/", "foaf", "http://xmlns.com/foaf/spec/index.rdf", null);
    public static final WorkspaceOntologySpec SEMM = new WorkspaceOntologySpec(
            "http://www.semmweb.com/ns/public/2012/09/12/semm/", "semm", null, null);

}
