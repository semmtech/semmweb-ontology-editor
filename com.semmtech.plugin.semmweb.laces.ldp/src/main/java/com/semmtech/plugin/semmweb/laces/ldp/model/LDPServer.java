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

package com.semmtech.plugin.semmweb.laces.ldp.model;


import java.io.InputStream;
import java.io.StringWriter;
import java.net.URI;
import java.net.URL;
import java.util.List;

import com.hp.hpl.jena.rdf.model.Model;
import com.semmtech.jena.util.JenaUtils;
import com.sun.jersey.api.client.ClientResponse;

import tech.laces.ldp.LDPv4Client;
import tech.laces.ldp.api.message.content.*;


/**
 * 
 * @author Sander Stolk
 */
public class LDPServer {
    private String serverUrl;
    private String username;
    private String password;

    public LDPServer() {
    }

    public LDPServer(String serverUrl, String username, String password) {
        this.serverUrl = serverUrl;
        this.username = username;
        this.password = password;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LDPGroup[] listGroups() {
        LDPv4Client client = new LDPv4Client(username, password);
        List<LDPGroupInfo> groupInfo = client.getGroups();
        return LDPGroup.createArray(this, groupInfo);
    }

    public LDPGroup[] listGroups(LDPGroup group) {
        if (group == null) {
            return listGroups();
        }
        LDPv4Client client = new LDPv4Client(username, password);
        List<LDPGroupInfo> groupInfo = client.getSubGroups(group.id);
        return LDPGroup.createArray(this, groupInfo);
    }

    public LDPRepository[] listRepositories() {
        LDPv4Client client = new LDPv4Client(username, password);
        List<LDPRepositoryInfo> repoInfo = client.getRepositories();
        return LDPRepository.createArray(this, repoInfo);
    }

    public LDPRepository[] listRepositories(LDPGroup group) {
        if (group == null) {
            return new LDPRepository[0];
        }
        LDPv4Client client = new LDPv4Client(username, password);
        List<LDPRepositoryInfo> repoInfo = client.getRepositories(group.id);
        return LDPRepository.createArray(group, repoInfo);
    }

    public LDPPublication[] listPublications(LDPRepository repo) {
        if (repo == null || repo.id == null || repo.id.isEmpty()) {
            return new LDPPublication[0];
        }
        LDPv4Client client = new LDPv4Client(username, password);
        List<LDPPublicationInfo> pubInfo = client.getPublications(repo.id, true);
        return LDPPublication.createArray(repo, pubInfo);
    }

    public LDPPublication[] listPublications(String repoId) {
        LDPRepository repo = new LDPRepository(this, repoId, "<name>");
        return listPublications(repo);
    }

    public Model getPublication(String url) {
        if (url == null) {
            return null;
        }

        LDPv4Client client = new LDPv4Client(username, password);
        ClientResponse response = client.getPublicationResponse(url, "text/turtle");
        if (response.getStatus() == 200) {
            String base = url;
            try {
                URI location = response.getLocation();
                if (location != null) {
                    if (!location.isAbsolute()) {
                        // make Location absolute
                        String protocol = new URL(url).getProtocol();
                        String authority = new URL(url).getAuthority();
                        location = new URL(protocol, authority, location.toString()).toURI();
                    }
                    base = location.toString();
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            InputStream input = response.getEntityInputStream();
            if (input != null) {
                Model model = JenaUtils.createDefaultModel().read(input, base, "TURTLE");
                if (model.getNsPrefixURI("") == null) {
                    model.setNsPrefix("", base);
                }
                return model;
            }
        }
        return null;
    }

    public String addPublication(Model model, String uri, LDPPublicationCreateInfo metadata)
            throws Exception {
        if (model == null || uri == null) {
            return null;
        }

        String pubPath = null;
        try {
            URL pubUrl = new URL(uri);
            pubPath = pubUrl.getPath();
        }
        catch (Exception e) {
            throw new Exception("Failed to publish model. The URI provided was invalid.");
        }

        LDPv4Client client = new LDPv4Client(username, password);
        List<LDPRepositoryInfo> repositories = client.getRepositories();
        if (repositories != null) {
            for (int i = 0; i < repositories.size(); i++) {
                String repositoryPath = repositories.get(i).path;
                if (pubPath.contains(repositoryPath + "/")) {
                    String repositoryId = repositories.get(i).id;
                    return addPublication(model, uri, metadata, repositoryId);
                }
            }
        }
        return null;
    }

    public String addPublication(Model model, String uri, LDPPublicationCreateInfo metadata,
            String repositoryId) throws Exception {
        if (model == null || uri == null || metadata == null || repositoryId == null) {
            return null;
        }

        try {
            URL pubUrl = new URL(uri);
            URL serverUrl = new URL(getServerUrl());
            if (!serverUrl.getAuthority().equals(pubUrl.getAuthority())) {
                return null;
            }
        }
        catch (Exception e) {
            throw new Exception("Failed to publish model. An invalid URI was provided.", e);
        }

        String syntax = "TURTLE";
        StringWriter out = new StringWriter();
        model.write(out, syntax);
        String turtle = out.toString();

        LDPv4Client client = new LDPv4Client(username, password);
        LDPPublicationInfo result = client.postPublication(repositoryId, metadata, turtle);
        if (result != null) {
            return result.uri;
        }
        return null;
    }
}
