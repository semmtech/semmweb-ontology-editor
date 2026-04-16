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

package com.semmtech.semantics.util;


import com.google.common.base.Preconditions;
import com.google.common.base.Strings;


/**
 * Class combining a namespace URI with possibly a prefix used to refer to this
 * URI.
 * 
 * @author Mike Henrichs
 * 
 */
public class NamespaceMapping {
    private String uri;
    private String prefix;

    /**
     * Constructor of NamespaceMapping
     * 
     * @param uri
     *            The URI of the namespace, must be non-null and non-empty
     * @param prefix
     *            An optional known prefix, used to refer to the given URI; may
     *            be null
     */
    public NamespaceMapping(String uri, String prefix) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(uri), "URI cannot be null or empty.");
        this.uri = uri;
        this.prefix = prefix;
    }

    public boolean hasPrefix() {
        return (prefix != null);
    }

    public String getURI() {
        return uri;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setURI(String uri) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(uri), "URI cannot be null or empty.");
        this.uri = uri;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public int hashCode() {
        return uri.hashCode();
    }

    @Override
    public String toString() {
        if (prefix == null) {
            return String.format("<%s>", uri);
        }
        return String.format("%s: <%s>", prefix, uri);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof NamespaceMapping)) {
            return false;
        }
        return equals((NamespaceMapping) obj);
    }

    public boolean equals(NamespaceMapping other) {
        if (other == null) {
            return false;
        }
        if (!uri.equals(other.getURI())) {
            return false;
        }
        if (!hasPrefix() && other.hasPrefix()) {
            return false;
        }
        if (hasPrefix() && !other.hasPrefix()) {
            return false;
        }
        if (hasPrefix() && !prefix.equals(other.getPrefix())) {
            return false;
        }
        return true;
    }
}
