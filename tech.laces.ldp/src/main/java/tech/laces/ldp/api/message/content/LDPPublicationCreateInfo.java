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
import org.codehaus.jackson.map.annotate.JsonSerialize;


/**
 * 
 * @author Sander Stolk
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class LDPPublicationCreateInfo {
    @JsonProperty("publicationUri")
    public String publicationUri;
    @JsonProperty("name")
    public String name;
    @JsonProperty("publisher")
    public String publisher;
    @JsonProperty("owner")
    public String owner;
    @JsonProperty("creator")
    public String creator;
    @JsonProperty("contributor")
    public String contributor;
    @JsonProperty("description")
    public String description;
    @JsonProperty("versioningMode")
    public String versioningMode;
    @JsonProperty("abstract")
    public String _abstract;
    @JsonProperty("versionLabel")
    public String versionLabel;
    @JsonProperty("licence")
    public LDPLicenceInfo licence;
    @JsonProperty("useVersionedBaseUri")
    public boolean useVersionedBaseUri;
    @JsonProperty("addVersionToContent")
    public boolean addVersionToContent;
    @JsonProperty("schemaURIs")
    public String[] schemaURIs;
    @JsonProperty("icon")
    public String icon;
    @JsonProperty("ldvLink")
    public String ldvLink;
}
