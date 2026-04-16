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

package tech.laces.ldp.api.message.content;


import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;


/**
 * 
 * @author Sander Stolk
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class LDPPublicationInfo {
    public final String[] VersioningModes = { "UNDEFINED", "NONE", "TIMESTAMP", "INCREMENTAL",
            "DATE_TIME", "CUSTOM" };

    @JsonProperty("id")
    public String id;
    @JsonProperty("name")
    public String name;
    @JsonProperty("description")
    public String description;
    @JsonProperty("repositoryId")
    public String repositoryId;
    @JsonProperty("owner")
    public String owner;
    @JsonProperty("publisher")
    public String publisher;
    @JsonProperty("creator")
    public String creator;
    @JsonProperty("contributor")
    public String contributor;
    @JsonProperty("publicationDate")
    public long publicationDate;
    @JsonProperty("uri")
    public String uri;
    @JsonProperty("sequenceId")
    public String sequenceId;
    @JsonProperty("sparqlEndpoint")
    public String sparqlEndpoint;
    @JsonProperty("useVersionedBaseUri")
    public boolean useVersionedBaseUri;
    @JsonProperty("versioningMode")
    public String versioningMode;
    @JsonProperty("addVersionToContent")
    public boolean addVersionToContent;
    @JsonProperty("licence")
    public LDPLicenceInfo licence;
    @JsonProperty("schemaURIs")
    public String[] schemaURIs;
    @JsonProperty("pending")
    public boolean pending;
    @JsonProperty("icon")
    public String icon;
    @JsonProperty("ldvLink")
    public String ldvLink;
    @JsonProperty("abstract")
    public String _abstract;
}
