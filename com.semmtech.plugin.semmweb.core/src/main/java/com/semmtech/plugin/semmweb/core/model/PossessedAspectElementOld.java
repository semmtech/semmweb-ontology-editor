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


import com.hp.hpl.jena.rdf.model.Resource;
import com.semmtech.plugin.semmweb.core.widgets.Cardinality;


public class PossessedAspectElementOld {
    private Resource aspect;
    private Resource value;
    private Cardinality cardinality;

    public PossessedAspectElementOld(Resource aspect) {
        this(aspect, null, new Cardinality(0, true));
    }

    public PossessedAspectElementOld(Resource aspect, Resource value, Cardinality cardinality) {
        this.aspect = aspect;
        this.value = value;
        this.cardinality = cardinality;
    }

    public void setAspect(Resource aspect) {
        this.aspect = aspect;
    }

    public Resource getAspect() {
        return aspect;
    }

    public void setValue(Resource value) {
        this.value = value;
    }

    public Resource getValue() {
        return value;
    }

    public Cardinality getCardinality() {
        return cardinality;
    }

    public void setCardinality(Cardinality cardinality) {
        this.cardinality = cardinality;
    }
}
