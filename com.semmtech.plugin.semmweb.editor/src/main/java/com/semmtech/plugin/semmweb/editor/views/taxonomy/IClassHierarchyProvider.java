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

package com.semmtech.plugin.semmweb.editor.views.taxonomy;


import java.util.List;

import org.apache.commons.collections4.bag.TreeBag;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntResource;


public interface IClassHierarchyProvider {

    List<OntResource> listVisibleSubClasses(OntClass clazz);

    List<OntResource> listInstances(OntClass clazz, List<OntResource> exclude);

    TreeBag<OntResource> getChildren(OntClass clazz);

    boolean containsClass(OntClass clazz);

    void putChildren(OntClass clazz, TreeBag<OntResource> children);

    void removeChildren(OntClass clazz);

    void removeChild(OntResource child);
}
