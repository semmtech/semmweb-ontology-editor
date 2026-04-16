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

package com.semmtech.semantics.model.impl;


import com.hp.hpl.jena.enhanced.EnhGraph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.rdf.model.AnonId;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.impl.ModelCom;
import com.hp.hpl.jena.rdf.model.impl.ResourceImpl;


public abstract class BaseResourceImpl extends ResourceImpl {

    protected BaseResourceImpl(Node node, EnhGraph graph) {
        super(node, graph);
    }

    protected BaseResourceImpl(ModelCom model) {
        super(model);
    }

    protected BaseResourceImpl(String uri, ModelCom model) {
        super(uri, model);
    }

    public BaseResourceImpl(AnonId id, ModelCom model) {
        super(id, model);
    }

    public BaseResourceImpl(Resource r, ModelCom model) {
        super(r, model);
    }

    protected void setSingleProperty(Property p, String value) {
        if (hasProperty(p))
            removeAll(p);
        addProperty(p, value);
    }

    protected String getSingleProperty(Property p) {
        if (hasProperty(p))
            return getProperty(p).getString();
        return null;
    }

    // protected void setSingleProperty(Property p, Calendar calendar) {
    // if (hasProperty(p))
    // removeAll(p);
    // addProperty(p, getModel().createTypedLiteral(calendar));
    // }

}
