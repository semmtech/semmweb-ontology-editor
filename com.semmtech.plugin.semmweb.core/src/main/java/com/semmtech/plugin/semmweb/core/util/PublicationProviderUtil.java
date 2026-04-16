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


import java.util.List;
import java.util.ListIterator;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

import com.google.common.collect.Lists;
import com.semmtech.plugin.semmweb.core.extensionpoint.IPublicationProvider;
import com.semmtech.plugin.semmweb.core.extensionpoint.IPublisher;


public class PublicationProviderUtil {
    public static List<IPublicationProvider> getPublicationProviders() {

        IConfigurationElement[] elements = Platform.getExtensionRegistry()
                .getConfigurationElementsFor("com.semmtech.plugin.semmweb.core.publishers");
        List<IPublicationProvider> publicationProviders = Lists.newArrayList();
        for (IConfigurationElement e : elements) {
            Object o;
            try {
                o = e.createExecutableExtension("class");
                publicationProviders.add((IPublicationProvider) o);
            }
            catch (CoreException ex) {
                ex.printStackTrace();
            }
        }
        return publicationProviders;
    }

    public static IPublisher getPublisherFor(String url) {
        List<IPublicationProvider> pps = getPublicationProviders();
        for (ListIterator<IPublicationProvider> ppsi = pps.listIterator(); ppsi.hasNext();) {
            IPublicationProvider pp = ppsi.next();
            List<IPublisher> ps = pp.getPublishers();
            for (ListIterator<IPublisher> psi = ps.listIterator(); psi.hasNext();) {
                IPublisher p = psi.next();
                if (p.serves(url)) {
                    return p;
                }
            }
        }
        return null;
    }
}
