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

package com.semmtech.semantics.semm.impl;


import com.hp.hpl.jena.enhanced.EnhGraph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.impl.OntClassImpl;
import com.semmtech.semantics.semm.Aspect;
import com.semmtech.semantics.semm.SEMMModel;


public class AspectImpl extends OntClassImpl implements Aspect {

    public AspectImpl(Node n, EnhGraph g) {
        super(n, g);
    }

    public AspectImpl(OntClass c) {
        super(c.asNode(), (EnhGraph) c.getModel());
    }

    @Override
    public SEMMModel getSEMMModel() {
        // TODO Auto-generated method stub
        return null;
    }

}
