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

package com.semmtech.plugin.semmweb.core.widgets.trees;


import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;


public class IndividualTreeData extends OntResourceTreeData implements Individual {
    private final Individual individual;

    public IndividualTreeData(Individual individual) {
        super(individual);
        this.individual = individual;
    }

    @Override
    public void addOntClass(Resource cls) {
        individual.addOntClass(cls);
    }

    @Override
    public OntClass getOntClass() {
        return individual.getOntClass();
    }

    @Override
    public OntClass getOntClass(boolean direct) {
        return individual.getOntClass(direct);
    }

    @Override
    public boolean hasOntClass(Resource ontClass) {
        return individual.hasOntClass(ontClass);
    }

    @Override
    public boolean hasOntClass(String uri) {
        return individual.hasOntClass(uri);
    }

    @Override
    public boolean hasOntClass(Resource ontClass, boolean direct) {
        return individual.hasOntClass(ontClass, direct);
    }

    @Override
    public <T extends OntClass> ExtendedIterator<T> listOntClasses(boolean direct) {
        return individual.listOntClasses(direct);
    }

    @Override
    public void removeOntClass(Resource cls) {
        individual.removeOntClass(cls);
    }

    @Override
    public void setOntClass(Resource cls) {
        individual.setOntClass(cls);
    }
}
