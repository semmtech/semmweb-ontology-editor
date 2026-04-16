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

package com.semmtech.semantics.ontology;


import com.hp.hpl.jena.ontology.Profile;
import com.hp.hpl.jena.ontology.ProfileRegistry;
import com.semmtech.semantics.vocabulary.SEMM;


public final class ExtendedProfileRegistry {

    public static final String SEMM_LANG = SEMM.getURI();

    private static ProfileRegistry registry;

    private static ExtendedProfileRegistry instance = new ExtendedProfileRegistry();

    static {
        registry = ProfileRegistry.getInstance();
        registry.registerProfile(SEMM_LANG, new SEMMProfile());
    }

    private ExtendedProfileRegistry() {
    }

    public Profile getProfile(String uri) {
        return registry.getProfile(uri);
    }

    public void registerProfile(String uri, Profile profile) {
        registry.registerProfile(uri, profile);
    }

    public static ExtendedProfileRegistry getInstance() {
        return instance;
    }
}
