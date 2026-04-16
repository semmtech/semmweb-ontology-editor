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
public class LDPRepositoryInfo {
    @JsonProperty("id")
    public String id;
    @JsonProperty("name")
    public String name;
    @JsonProperty("description")
    public String description;
    @JsonProperty("pathSegment")
    public String pathSegment;
    @JsonProperty("public")
    public boolean _public;
    @JsonProperty("owner")
    public String owner;
    @JsonProperty("path")
    public String path;
    @JsonProperty("parentId")
    public String parentId;
    @JsonProperty("createdBy")
    public String createdBy;
    @JsonProperty("createdOn")
    public long createdOn;
    @JsonProperty("modifiedBy")
    public String modifiedBy;
    @JsonProperty("modifiedOn")
    public long modifiedOn;
    @JsonProperty("role")
    public String role;
}
