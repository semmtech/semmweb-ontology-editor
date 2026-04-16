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

package com.semmtech.plugin.semmweb.dictionary.model;


public class DictionaryLabelKey {
    private String uri;
    private String label;
    private String language;

    public DictionaryLabelKey(String uri, String label, String language) {
        this.uri = uri;
        this.label = label;
        if (language == null)
            this.language = "";
        else
            this.language = language;
    }

    public String getUri() {
        return uri;
    }

    public String getLabel() {
        return label;
    }

    public String getLanguage() {
        return language;
    }

    @Override
    public int hashCode() {
        return String.format("%s;%s;%s;", uri, label, language).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (!(obj instanceof DictionaryLabelKey))
            return false;
        return super.equals(obj);
    }

    public boolean equals(DictionaryLabelKey other) {
        return (uri.equals(other.getUri())) && (label.equals(other.label))
                && (language.equals(other.getLanguage()));
    }
}
