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

package com.semmtech.plugin.semmweb.core.model;


import java.util.List;

import com.hp.hpl.jena.ontology.OntResource;


/**
 * 
 * @author Sander Stolk
 * @author Simone Rondelli
 */
public class OpenResourceEventAdapter implements OpenResourceEventListener {

    @Override
    public void resourceOpened(OntResource resource) {
        // TODO Auto-generated method stub
    }

    @Override
    public void resourceActivated(OntResource resource) {
        // TODO Auto-generated method stub
    }

    @Override
    public void resourceClosed(OntResource resource) {
        // TODO Auto-generated method stub
    }

    @Override
    public void resourcesClosed(List<OntResource> resources) {
        // TODO Auto-generated method stub
    }

}
