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

package com.semmtech.plugin.semmweb.core.util;


import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import com.google.common.base.Strings;


public final class URIs {

    public static boolean hasFileScheme(String uri) {
        if (!Strings.isNullOrEmpty(uri)) {
            return uri.startsWith("file:///");
        }
        return false;
    }

    public static boolean hasFileScheme(URI uri) {
        return "file".equalsIgnoreCase(uri.getScheme());
    }

    public static IPath createPath(String uri) {
        if (!hasFileScheme(uri)) {
            throw new IllegalArgumentException("URI must start with the scheme file:");
        }
        return new Path(StringUtils.removeStart(uri, "file:///"));
    }

    /**
     * Check if the string that represents an URI is a local file.
     * 
     * This method doesn't check the existence of the file and assumes that the
     * URI parameter is a well formed local file system path or an URI.
     * 
     * @param uri
     *            Path on file system or URI
     */
    public static boolean isLocalFile(String uri) {
        try {
            return isLocalFile(new URL(uri));
        }
        catch (MalformedURLException e) {
            return true;
        }
    }

    /**
     * Whether the URL is a file in the local file system.
     */
    public static boolean isLocalFile(URL url) {
        String scheme = url.getProtocol();
        return "file".equalsIgnoreCase(scheme) && !hasHost(url);
    }

    private static boolean hasHost(URL url) {
        String host = url.getHost();
        return host != null && !"".equals(host);
    }

    public static String makePrettyURL(String url) {
        String prettyUrl = url;
        if (prettyUrl.startsWith("file:///")) {
            prettyUrl = StringUtils.removeStart(prettyUrl, "file:///");
            try {
                prettyUrl = URLDecoder.decode(prettyUrl, "UTF-8");
            }
            catch (UnsupportedEncodingException ex) {
            }
            prettyUrl = prettyUrl.replace("/", "\\");
        }
        return prettyUrl;
    }
}
