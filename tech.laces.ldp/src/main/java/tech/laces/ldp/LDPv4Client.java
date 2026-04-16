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

package tech.laces.ldp;


import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.codehaus.jackson.map.ObjectMapper;

import tech.laces.ldp.api.message.LDPResponseGetGroups;
import tech.laces.ldp.api.message.LDPResponseGetPublications;
import tech.laces.ldp.api.message.LDPResponseGetRepositories;
import tech.laces.ldp.api.message.LDPResponseGetLicences;
import tech.laces.ldp.api.message.content.LDPGroupInfo;
import tech.laces.ldp.api.message.content.LDPPublicationCreateInfo;
import tech.laces.ldp.api.message.content.LDPPublicationInfo;
import tech.laces.ldp.api.message.content.LDPRepositoryInfo;
import tech.laces.ldp.api.message.content.LDPLicenceInfo;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.core.util.Base64;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.MultiPart;
import com.sun.jersey.multipart.impl.MultiPartWriter;


/**
 * 
 * @author Sander Stolk
 */
public class LDPv4Client {
    public static final String DEFAULT_SERVER_URL = "https://hub.laces.tech";
    public static final int MAX_PAGE_SIZE = 100;

    private Client client;
    private String serverUrl;
    private String username;
    private String password;

    /* Anonymous access */
    public LDPv4Client() {
        this(DEFAULT_SERVER_URL);
    }

    public LDPv4Client(String serverUrl) {
        this(serverUrl, null, null);
    }

    /*
     * Access using token based on Basic Auth. The token id is used as the
     * username
     */
    public LDPv4Client(String tokenId, String password) {
        this(DEFAULT_SERVER_URL, tokenId, password);
    }

    public LDPv4Client(String serverUrl, String tokenId, String password) {
        ClientConfig cc = new DefaultClientConfig();
        cc.getClasses().add(MultiPartWriter.class);
        this.client = Client.create(cc);
        this.serverUrl = serverUrl;
        this.username = tokenId;
        this.password = password;
    }

    public List<LDPGroupInfo> getGroups() {
        String path = "/api/v4/groups";
        return getGroupsViaCalls(path);
    }

    public List<LDPGroupInfo> getSubGroups(String groupId) {
        String path = "/api/v4/groups/" + groupId + "/subgroups";
        return getGroupsViaCalls(path);
    }

    private List<LDPGroupInfo> getGroupsViaCalls(String path) {
        List<LDPGroupInfo> fetchedGroups = new ArrayList<>();
        long totalGroups = 0;
        int page = 0;

        while (totalGroups > fetchedGroups.size() || page == 0) {
            LDPResponseGetGroups msg = getGroupsViaCall(path, MAX_PAGE_SIZE, page);
            if (msg == null) {
                break;
            }
            totalGroups = msg.total;
            fetchedGroups.addAll(msg.contents);
            page++;
        }

        return fetchedGroups;
    }

    private LDPResponseGetGroups getGroupsViaCall(String path, int pageSize, int page) {
        // set up webresource for call
        WebResource resource = client.resource(serverUrl);
        resource = resource.queryParam("pageSize", Integer.toString(pageSize));
        resource = resource.queryParam("page", Integer.toString(page));

        ClientResponse response = setAuthorization(resource.path(path).accept("application/json"))
                .get(ClientResponse.class);
        if (response.getStatus() == 200) {
            ObjectMapper mapper = new ObjectMapper();
            String jsonString = response.getEntity(String.class);
            try {
                return mapper.readValue(jsonString, LDPResponseGetGroups.class);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            // TODO: throw exception and/or inform user
        }
        return null;
    }

    public List<LDPRepositoryInfo> getRepositories() {
        String path = "/api/v4/repositories";
        return getRepositoriesViaCalls(path, null);
    }

    public List<LDPRepositoryInfo> getRepositories(String groupId) {
        String path = "/api/v4/repositories";
        return getRepositoriesViaCalls(path, groupId);
    }

    private List<LDPRepositoryInfo> getRepositoriesViaCalls(String path, String groupId) {
        List<LDPRepositoryInfo> fetchedRepos = new ArrayList<>();
        long totalRepos = 0;
        int page = 0;

        while (totalRepos > fetchedRepos.size() || page == 0) {
            LDPResponseGetRepositories msg = getRepositoriesViaCall(path, groupId, MAX_PAGE_SIZE,
                    page);
            if (msg == null) {
                break;
            }
            totalRepos = msg.total;
            fetchedRepos.addAll(msg.contents);
            page++;
        }

        return fetchedRepos;
    }

    private LDPResponseGetRepositories getRepositoriesViaCall(String path, String groupId,
            int pageSize, int page) {
        // set up webresource and params for call
        WebResource resource = client.resource(serverUrl);
        resource = resource.queryParam("pageSize", Integer.toString(pageSize));
        resource = resource.queryParam("page", Integer.toString(page));
        if (groupId != null) {
            resource = resource.queryParam("groupId", groupId);
        }

        // set up webresource and send message
        ClientResponse response = setAuthorization(resource.path(path).accept("application/json"))
                .get(ClientResponse.class);
        if (response.getStatus() == 200) {
            ObjectMapper mapper = new ObjectMapper();
            String jsonString = response.getEntity(String.class);
            try {
                return mapper.readValue(jsonString, LDPResponseGetRepositories.class);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            // TODO: throw exception and/or inform user
        }
        return null;
    }

    public List<LDPPublicationInfo> getPublications() {
        return getPublications(null, false);
    }

    public List<LDPPublicationInfo> getPublications(String repositoryId) {
        return getPublications(repositoryId, false);
    }

    public List<LDPPublicationInfo> getPublications(boolean latestVersionsOnly) {
        return getPublications(null, latestVersionsOnly);
    }

    public List<LDPPublicationInfo> getPublications(String repositoryId, boolean latestVersionsOnly) {
        String path = "/api/v4/publications";
        return getPublicationsViaCalls(path, repositoryId, latestVersionsOnly);
    }

    private List<LDPPublicationInfo> getPublicationsViaCalls(String path, String repositoryId,
            boolean latestVersionsOnly) {
        List<LDPPublicationInfo> fetchedPubs = new ArrayList<>();
        long totalPubs = 0;
        int page = 0;

        while (totalPubs > fetchedPubs.size() || page == 0) {
            LDPResponseGetPublications msg = getPublicationsViaCall(path, repositoryId,
                    MAX_PAGE_SIZE, page, latestVersionsOnly);
            if (msg == null) {
                break;
            }
            totalPubs = msg.total;
            fetchedPubs.addAll(msg.contents);
            page++;
        }

        return fetchedPubs;
    }

    private LDPResponseGetPublications getPublicationsViaCall(String path, String repositoryId,
            int pageSize, int page, boolean latestVersionsOnly) {
        // set up webresource and params for call
        WebResource resource = client.resource(serverUrl);
        resource = resource.queryParam("pageSize", Integer.toString(pageSize));
        resource = resource.queryParam("page", Integer.toString(page));
        if (repositoryId != null) {
            resource = resource.queryParam("repositoryId", repositoryId);
        }
        if (latestVersionsOnly != false) {
            resource = resource.queryParam("latestVersionsOnly", "true");
        }

        // set up and send message
        ClientResponse response = setAuthorization(resource.path(path).accept("application/json"))
                .get(ClientResponse.class);
        if (response.getStatus() == 200) {
            ObjectMapper mapper = new ObjectMapper();
            String jsonString = response.getEntity(String.class);
            try {
                return mapper.readValue(jsonString, LDPResponseGetPublications.class);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            // TODO: throw exception and/or inform user
        }
        return null;
    }

    public InputStream getPublication(String url, String mimeType) {
        ClientResponse response = getPublicationResponse(url, mimeType);
        if (response.getStatus() == 200) {
            return response.getEntityInputStream();
        }
        return null;
    }

    public ClientResponse getPublicationResponse(String url, String mimeType) {
        if (url == null) {
            return null;
        }

        // if url is a relative url, make it an absolute url
        // by prepending serverUrl
        if (!url.startsWith("http")) {
            url = serverUrl + url;
        }
        // if url is http://, make it https://
        url = url.replaceFirst("http://", "https://");

        // ensure mimeType is set
        if (mimeType == null) {
            mimeType = "text/turtle";
        }

        // set up and send message
        WebResource resource = client.resource(url);
        Builder builder = setAuthorization(resource.accept(mimeType));
        return builder.get(ClientResponse.class);
    }

    public LDPPublicationInfo postPublication(String repositoryId,
            LDPPublicationCreateInfo metadata, String content) throws Exception {
        if (repositoryId == null || metadata == null || content == null) {
            return null;
        }

        String path = "/api/v4/publications";

        // set up webresource and params for call
        WebResource resource = client.resource(serverUrl);
        resource = resource.queryParam("repositoryId", repositoryId);

        // transform LDPPublicationCreateInfo to JSON string
        String metadataJson = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            metadataJson = mapper.writeValueAsString(metadata);
        }
        catch (Exception e) {
            throw new Exception(
                    "Failed to publish model. The model metadata could not be processed successfully.",
                    e);
        }

        // set up multipart content for message
        MultiPart multiPart = new MultiPart().bodyPart(
                new FormDataBodyPart("metadata", metadataJson, MediaType.APPLICATION_JSON_TYPE))
                .bodyPart(
                        new FormDataBodyPart(FormDataContentDisposition.name("content")
                                .fileName("content.ttl").build(), content, new MediaType("text",
                                "turtle")));
        // NOTE: if posting a File instead of an in-memory model, one should use
        // new FileDataBodyPart("content", file)

        // set up and send message
        ClientResponse response = setAuthorization(resource.path(path).accept("application/json"))
                .type(MediaType.MULTIPART_FORM_DATA_TYPE).post(ClientResponse.class, multiPart);
        if (response.getStatus() != 201) {
            throw new Exception("Failed to publish model. Server responded with status: "
                    + response.getStatus());
        }
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = response.getEntity(String.class);
        try {
            return mapper.readValue(jsonString, LDPPublicationInfo.class);
        }
        catch (IOException e) {
            throw new Exception("Successfully published model, but failed to interpret response.",
                    e);
        }
    }

    public List<LDPLicenceInfo> getLicences(String repositoryId) {
        String path = "/api/v4/licences";

        // set up webresource and params for call
        WebResource resource = client.resource(serverUrl);
        if (repositoryId != null) {
            resource = resource.queryParam("repositoryId", repositoryId);
        }

        // set up and send message
        ClientResponse response = setAuthorization(resource.path(path).accept("application/json"))
                .get(ClientResponse.class);
        if (response.getStatus() == 200) {
            ObjectMapper mapper = new ObjectMapper();
            String jsonString = response.getEntity(String.class);
            try {
                LDPResponseGetLicences msg = mapper.readValue(jsonString,
                        LDPResponseGetLicences.class);
                return msg.contents;
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            // TODO: throw exception and/or inform user
        }
        return null;

    }

    private Builder setAuthorization(Builder builder) {
        if (username != null && password != null) {
            String authorization = String.format("Basic %s",
                    new String(Base64.encode(String.format("%s:%s", username, password))));
            builder = builder.header("Authorization", authorization);
        }
        return builder;
    }
}
