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

package com.semmtech.plugin.semmweb.core.jobs;


import com.hp.hpl.jena.ontology.OntModel;
import com.semmtech.plugin.semmweb.core.jobs.LoadModelJob.ModelMetaData;


public class LoadModelJobAdapter implements ILoadModelJobListener {

    @Override
    public void modelLoaded(OntModel model) {
        // TODO Auto-generated method stub
    }

    @Override
    public void modelLoaded(OntModel model, ModelMetaData metaData) {
        // TODO Auto-generated method stub
    }

}
