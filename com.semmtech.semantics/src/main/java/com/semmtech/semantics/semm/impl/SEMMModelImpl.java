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
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.util.iterator.UniqueExtendedIterator;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.semmtech.semantics.ontology.ExtendedOntModel;
import com.semmtech.semantics.semm.Aspect;
import com.semmtech.semantics.semm.PhysicalObject;
import com.semmtech.semantics.semm.SEMMModel;
import com.semmtech.semantics.vocabulary.SEMM;


public class SEMMModelImpl extends ExtendedOntModel implements SEMMModel {

    public SEMMModelImpl(OntModelSpec spec) {
        super(spec);
    }

    public SEMMModelImpl(OntModelSpec spec, Model model) {
        super(spec, model);
    }

    // protected void checkExtendedProfileEntry(Object profileTerm, String
    // description) {
    // if (getExtendedProfile() == null)
    // throw new ProfileException(
    // "Provided profile is not an instance of ExtendedProfile required by SEMMModel.",
    // getProfile() );
    // checkProfileEntry(profileTerm, description);
    // }

    @Override
    public ExtendedIterator<PhysicalObject> listPhysicalObjects() {
        // checkProfileEntry(getExtendedProfile().SUB_CLASS_OF(),
        // "SUB_CLASS_OF");
        // checkProfileEntry(getExtendedProfile().PHYSICAL_OBJECT(),
        // "PHYSICAL_OBJECT");
        ArrayList<PhysicalObject> list = Lists.newArrayList();
        // for (Resource r :
        // listSubjectsWithProperty(getExtendedProfile().SUB_CLASS_OF(),
        // getExtendedProfile().PHYSICAL_OBJECT()).toList())
        for (Resource r : listSubjectsWithProperty(RDFS.subClassOf, SEMM.PhysicalObject).toList())
            list.add(new PhysicalObjectImpl(r.as(OntClass.class)));
        return UniqueExtendedIterator.create(list.iterator());
    }

    @Override
    public PhysicalObject createPhysicalObject() {
        // checkProfileEntry(getExtendedProfile().SUB_CLASS_OF(),
        // "SUB_CLASS_OF");
        // checkProfileEntry(getExtendedProfile().PHYSICAL_OBJECT(),
        // "PHYSICAL_OBJECT");
        OntClass clazz = createClass(null);
        // clazz.addProperty(getExtendedProfile().SUB_CLASS_OF(),
        // getExtendedProfile().PHYSICAL_OBJECT());
        clazz.addProperty(RDFS.subClassOf, SEMM.PhysicalObject);
        PhysicalObject physicalObject = new PhysicalObjectImpl(clazz);
        return physicalObject;
    }

    @Override
    public PhysicalObject createPhysicalObject(String uri) {
        // checkProfileEntry(getExtendedProfile().SUB_CLASS_OF(),
        // "SUB_CLASS_OF");
        // checkProfileEntry(getExtendedProfile().PHYSICAL_OBJECT(),
        // "PHYSICAL_OBJECT");
        OntClass clazz = createClass(uri);
        // clazz.addProperty(getExtendedProfile().SUB_CLASS_OF(),
        // getExtendedProfile().PHYSICAL_OBJECT());
        clazz.addProperty(RDFS.subClassOf, SEMM.PhysicalObject);
        PhysicalObject physicalObject = new PhysicalObjectImpl(clazz);
        return physicalObject;
    }

    @Override
    public ExtendedIterator<Aspect> listAspects() {
        // TODO Auto-generated method stub
        return null;
    }

    // @Override
    // public ExtendedProfile getExtendedProfile() {
    // OntModelSpec spec = getSpecification();
    // if (!(spec.getProfile() instanceof ExtendedProfile))
    // throw new
    // OntologyException("The current language profile for namespace '" +
    // getProfile().NAMESPACE() +
    // "' is not an instance of the ExtendedProfile, which is required by SEMMModel.");
    // return (ExtendedProfile)spec.getProfile();
    // }

    @Override
    public Aspect createAspect() {
        // checkProfileEntry(getExtendedProfile().SUB_CLASS_OF(),
        // "SUB_CLASS_OF");
        // checkProfileEntry(getExtendedProfile().ASPECT(), "ASPECT");
        OntClass clazz = createClass(null);
        // clazz.addProperty(getExtendedProfile().SUB_CLASS_OF(),
        // getExtendedProfile().ASPECT());
        clazz.addProperty(RDFS.subClassOf, SEMM.Aspect);
        Aspect aspect = new AspectImpl(clazz);
        return aspect;
    }

    @Override
    public Aspect createAspect(String uri) {
        // checkProfileEntry(getExtendedProfile().SUB_CLASS_OF(),
        // "SUB_CLASS_OF");
        // checkProfileEntry(getExtendedProfile().ASPECT(), "ASPECT");
        OntClass clazz = createClass(uri);
        // clazz.addProperty(getExtendedProfile().SUB_CLASS_OF(),
        // getExtendedProfile().ASPECT());
        clazz.addProperty(RDFS.subClassOf, SEMM.Aspect);
        Aspect aspect = new AspectImpl(clazz);
        return aspect;
    }
}
