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


public class NamespaceRewriteRule {
    private String from;
    private String to;
    private boolean updatePrefixMap;

    public NamespaceRewriteRule(String from, String to, boolean updatePrefixMap) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(from),
                "Original (from) namespace cannot be null or empty!");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(to),
                "New (to) namespace cannot be null or empty!");
        this.from = from;
        this.to = to;
        this.updatePrefixMap = updatePrefixMap;
    }

    public String getFrom() {
        return from;
    }

    public boolean isUpdatePrefixMap() {
        return updatePrefixMap;
    }

    public void setUpdatePrefixMap(boolean update) {
        this.updatePrefixMap = update;
    }

    public void setFrom(String from) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(from),
                "Original (from) namespace cannot be null or empty!");
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(to),
                "New (to) namespace cannot be null or empty!");
        this.to = to;
    }

    @Override
    public int hashCode() {
        return from.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof NamespaceRewriteRule)) {
            return false;
        }
        return equals((NamespaceRewriteRule) obj);
    }

    public boolean equals(NamespaceRewriteRule other) {
        if (other == null) {
            return false;
        }
        if (!from.equals(other.getFrom())) {
            return false;
        }
        else if (!to.equals(other.getTo())) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return String.format("<%s> -> <%s>%s", from.toString(), to.toString(),
                (updatePrefixMap ? " [update prefix]" : ""));
    }
}
