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

package com.semmtech.plugin.semmweb.laces.ldp.extension;


import java.util.List;

import com.google.common.collect.Lists;
import com.semmtech.plugin.semmweb.core.extensionpoint.IPublicationProvider;
import com.semmtech.plugin.semmweb.core.extensionpoint.IPublisher;


/**
 * 
 * @author Sander Stolk
 * @author Mike Henrichs
 */
public class PublisherProvider implements IPublicationProvider {
    private static final String NAME = "Laces LDP provider";
    private static IPublisher publisher = null;

    public PublisherProvider() {
    }

    @Override
    public String getProviderName() {
        return NAME;
    }

    @Override
    public IPublisher createNewPublisher() {
        publisher = new Publisher();
        return publisher;
    }

    @Override
    public List<IPublisher> getPublishers() {
        createNewPublisher();
        return Lists.newArrayList(publisher);
    }

}
