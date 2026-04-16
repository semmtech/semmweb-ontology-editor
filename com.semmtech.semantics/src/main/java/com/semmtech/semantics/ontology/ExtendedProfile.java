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
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;


/**
 * Interface that encapsulates the elements of a richer semantic vocabulary
 * corresponding to a particular ontology language. The intent is that, using a
 * given vocabulary, a given RDF model can be processed as an semantic rich
 * description, without binding knowledge of the vocabulary into this Java
 * package. For tractability, this limits the vocabularies that can easily be
 * represented to those that are similar (extensions) to OWL and DAML+OIL.
 * 
 * @author Mike Henrichs
 * 
 */
public interface ExtendedProfile extends Profile {
    public Resource PHYSICAL_OBJECT();

    public Resource ASPECT();

    public Property HAS_ASPECT();

    public Property SUBJECT_ROLE();

    public Property OBJECT_ROLE();

}
