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

package com.semmtech.plugin.semmweb.laces.ldp.extension;


import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import tech.laces.ldp.api.message.content.LDPPublicationCreateInfo;

import com.semmtech.plugin.semmweb.core.extensionpoint.IDownloadModelHandler;
import com.semmtech.plugin.semmweb.core.extensionpoint.IPublishErrorHandler;
import com.semmtech.plugin.semmweb.core.extensionpoint.IPublisher;
import com.semmtech.plugin.semmweb.laces.ldp.model.LDPPublication;
import com.semmtech.plugin.semmweb.laces.ldp.model.LDPRepository;
import com.semmtech.plugin.semmweb.laces.ldp.model.LDPServer;
import com.semmtech.plugin.semmweb.laces.ldp.preferences.LDPPreference;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.sparql.vocabulary.FOAF;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;


/**
 * 
 * @author Sander Stolk
 */
public class Publisher implements IPublisher {
    private static final String NAME = "Laces LDP";
    private List<VersioningMode> versioningOptions;

    Publisher() {
        versioningOptions = Lists.newArrayList();
        versioningOptions.add(new VersioningMode() {
            {
                id = "NONE";
                name = "None";
                isSeries = false;
                isCustomVersioning = false;
            }
        });
        versioningOptions.add(new VersioningMode() {
            {
                id = "INCREMENTAL";
                name = "Incremental";
                isSeries = true;
                isCustomVersioning = false;
            }
        });
        versioningOptions.add(new VersioningMode() {
            {
                id = "TIMESTAMP";
                name = "Timestamp";
                isSeries = true;
                isCustomVersioning = false;
            }
        });
        versioningOptions.add(new VersioningMode() {
            {
                id = "DATE_TIME";
                name = "Date & time";
                isSeries = true;
                isCustomVersioning = false;
            }
        });
        versioningOptions.add(new VersioningMode() {
            {
                id = "CUSTOM";
                name = "Custom";
                isSeries = true;
                isCustomVersioning = true;
                regexCustomVersioning = "[a-zA-Z\\d_]+";
            }
        });
    }

    @Override
    public String getName() {
        return NAME;
    }

    protected LDPServer getCurrentServer() {
        List<LDPServer> servers = LDPPreference.getServers();
        if (servers.size() > 0) {
            return servers.get(0);
        }
        return null;
    }

    @Override
    public String getBaseURI() {
        return getServerURL();
    }

    @Override
    public String getServerURL() {
        LDPServer server = getCurrentServer();
        if (server != null) {
            return server.getServerUrl();
        }
        return LDPPreference.LDP_SERVER_URL;
    }

    @Override
    public List<VersioningMode> getVersioningOptions() {
        return versioningOptions;
    }

    @Override
    public List<RepositoryInfo> listWritableRepositories() {
        List<RepositoryInfo> result = new ArrayList<>();
        LDPServer server = getCurrentServer();
        if (server != null) {
            LDPRepository[] repos = server.listRepositories();
            for (int i = 0; i < repos.length; i++) {
                if (repos[i].fullInfo != null) {
                    if ("PUBLISHER".equals(repos[i].fullInfo.role)
                            || "MANAGER".equals(repos[i].fullInfo.role)) {
                        RepositoryInfo repoInfo = new RepositoryInfo();
                        repoInfo.id = repos[i].getID();
                        repoInfo.name = repos[i].getName();
                        repoInfo.uri = repos[i].getURL();
                        result.add(repoInfo);
                    }
                }
            }
            return result;
        }
        return null;
    }

    @Override
    public List<PublicationInfo> listPublications(String repoId) {
        List<PublicationInfo> result = new ArrayList<>();
        LDPServer server = getCurrentServer();
        if (server != null) {
            LDPPublication[] pubs = server.listPublications(repoId);
            for (int i = 0; i < pubs.length; i++) {
                if (pubs[i].fullInfo != null) {
                    PublicationInfo pubInfo = new PublicationInfo();
                    pubInfo.id = pubs[i].getID();
                    pubInfo.name = pubs[i].getName();
                    pubInfo.uri = pubs[i].getURL();
                    result.add(pubInfo);
                }
            }
            return result;
        }
        return null;
    }

    @Override
    public boolean serves(String url) {
        if (url != null) {
            try {
                URL content = new URL(url);
                URL server = new URL(getServerURL());
                if (server.getAuthority().equals(content.getAuthority())) {
                    return true;
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public boolean containsOntology(String uri) {
        if (serves(uri)) {
            LDPServer server = getCurrentServer();
            if (server != null) {
                // NOTE: a more elegant solution will be available in the future
                return (server.getPublication(uri) != null);
            }
        }
        return false;
    }

    @Override
    public String publishModel(Model model, String uri, Model metadata,
            VersioningMode versioningMode, Object versioningModeSettings,
            IPublishErrorHandler errorHandler) {
        LDPServer server = getCurrentServer();
        if (server == null) {
            errorHandler.error("Unable to publish model. No configured server has been detected.");
        }
        else if (model == null || model.isEmpty()) {
            errorHandler
                    .error("Unable to publish model. The model provided is empty; it contains no statements/triples.");
        }
        else {
            try {
                Model metadataModel = (metadata != null) ? metadata : model;
                LDPPublicationCreateInfo info = getPublicationCreateInfoFromModel(metadataModel);

                // set default values for mandatory attributes that are empty
                if (Strings.isNullOrEmpty(info.name)) {
                    // use last segment of URI
                    info.name = uri.replaceAll("[/#]$", "").replaceAll(".*/", "");
                }
                if (Strings.isNullOrEmpty(info.description)) {
                    info.description = "";
                }
                if (Strings.isNullOrEmpty(info.owner)) {
                    info.owner = "anonymous";
                }

                // set publicationUri as the path relative to the server, and
                // without trailing slash
                // TODO: the below is bugged. made a ticket on it. currently
                // needs relative to repository -_-'
                info.publicationUri = uri.replaceAll(".*//[^/]*", "").replaceAll("/$", "");
                // set version details
                info.versioningMode = versioningMode.id;
                if (versioningMode.id.equals("CUSTOM") && versioningModeSettings != null) {
                    info.versionLabel = versioningModeSettings.toString();
                }
                // attempt publication
                return server.addPublication(model, uri, info);
            }
            catch (Exception e) {
                errorHandler.error(e.getMessage());
                e.printStackTrace();
            }
        }
        return null;
    }

    protected LDPPublicationCreateInfo getPublicationCreateInfoFromModel(Model model) {
        LDPPublicationCreateInfo info = new LDPPublicationCreateInfo();
        StmtIterator ontoIter = model.listStatements(null, RDF.type, OWL.Ontology);
        if (ontoIter.hasNext()) {
            Resource ontology = ontoIter.next().getSubject();
            info.name = getPropertyValueAsString(ontology, DCTerms.title);
            info.description = getPropertyValueAsString(ontology, DCTerms.description);

            Resource creator = ontology.getPropertyResourceValue(DCTerms.creator);
            if (creator != null) {
                if (creator.hasProperty(RDF.type, FOAF.Person)) {
                    info.creator = getPropertyValueAsString(creator, FOAF.firstName) + " "
                            + getPropertyValueAsString(creator, FOAF.surname);
                }
                else if (creator.hasProperty(RDF.type, FOAF.Organization)) {
                    info.creator = getPropertyValueAsString(creator, FOAF.name);
                }
                info.owner = info.creator;
            }
        }
        return info;
    }

    protected String getPropertyValueAsString(Resource subject, Property property) {
        Statement stmt = subject.getProperty(property);
        if (stmt != null) {
            RDFNode object = stmt.getObject();
            return object.isLiteral() ? object.asLiteral().getLexicalForm() : object.toString();
        }
        return null;
    }

    @Override
    public Model downloadModel(String uri, IDownloadModelHandler downloadHandler) {
        LDPServer server = getCurrentServer();
        if (server != null) {
            return server.getPublication(uri);
        }
        return null;
    }

}
