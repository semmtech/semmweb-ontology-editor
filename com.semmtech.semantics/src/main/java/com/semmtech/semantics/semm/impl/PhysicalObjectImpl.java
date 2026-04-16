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


import java.util.ArrayList;

import com.google.common.collect.Lists;
import com.hp.hpl.jena.enhanced.EnhGraph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.ontology.MinCardinalityQRestriction;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.Restriction;
import com.hp.hpl.jena.ontology.impl.OntClassImpl;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.util.iterator.UniqueExtendedIterator;
import com.hp.hpl.jena.vocabulary.OWL2;
import com.semmtech.semantics.semm.Aspect;
import com.semmtech.semantics.semm.PhysicalObject;
import com.semmtech.semantics.semm.SEMMModel;
import com.semmtech.semantics.vocabulary.Gellish;


public class PhysicalObjectImpl extends OntClassImpl implements PhysicalObject {

    public PhysicalObjectImpl(Node n, EnhGraph g) {
        super(n, g);
    }

    public PhysicalObjectImpl(OntClass c) {
        super(c.asNode(), (EnhGraph) c.getModel());
    }

    @Override
    public SEMMModel getSEMMModel() {
        return null;
    }

    @Override
    public ExtendedIterator<Object> listAspects() {
        ArrayList<Object> list = Lists.newArrayList();
        for (OntClass superr : listSuperClasses().toList()) {
            if (superr.isRestriction()) {
                Restriction r = superr.asRestriction();
                if (r.hasProperty(OWL2.onProperty, Gellish.hasAspect)
                        && r.hasProperty(OWL2.onClass)) {
                    Statement stmt = superr.getProperty(OWL2.onClass);
                    list.add(stmt.getObject());
                }
            }
        }
        return UniqueExtendedIterator.create(list.iterator());
    }

    @Override
    public Object listComponents() {
        return null;
    }

    public ExtendedIterator<Aspect> listPossibleAspects() {
        return null;
    }

    @Override
    public void addAspect(Aspect aspect, boolean optional) {
        OntModel model = ((OntModel) getModel());
        if (optional) {
            MinCardinalityQRestriction restriction = model.createMinCardinalityQRestriction(null,
                    Gellish.hasAspect, 0, aspect);
            addSuperClass(restriction);
        }
        else {
            MinCardinalityQRestriction restriction = model.createMinCardinalityQRestriction(null,
                    Gellish.hasAspect, 1, aspect);
            addSuperClass(restriction);
        }

    }

}
