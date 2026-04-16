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

package com.semmtech.semantics.semm;


import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;


public interface SEMMModel extends OntModel {

    /**
     * Answer the rich semantic language profile (for example, SEMM or Gellish)
     * that this model is working to. This is an extension of the basic Jena
     * getProfile() method of OntModel interface.
     */
    // public ExtendedProfile getExtendedProfile();

    /**
     * Returns all physical object classes, which are the sub classes of
     * physical object.
     * 
     * @return
     */
    public ExtendedIterator<PhysicalObject> listPhysicalObjects();

    /**
     * Creates a new anonymous physical object class (sub class of physical
     * object).
     * 
     * @return
     */
    public PhysicalObject createPhysicalObject();

    /**
     * Creates a new physical object class (direct sub class of physical
     * object). If the uri already exists the existing resource is used to
     * create the physical object class.
     * 
     * @param uri
     * @return
     */
    public PhysicalObject createPhysicalObject(String uri);

    public ExtendedIterator<Aspect> listAspects();

    public Aspect createAspect();

    public Aspect createAspect(String uri);
}
