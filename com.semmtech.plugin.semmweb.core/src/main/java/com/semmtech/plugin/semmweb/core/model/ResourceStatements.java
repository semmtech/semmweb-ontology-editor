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


import java.util.List;

import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.semmtech.semantics.vocabulary.SEMM;


public class ResourceStatements {

    public final static void createResourcePropertyStatements(Resource s, Property p, Resource o) {
        if ((s != null) && (p != null) && (o != null)) {
            s.addProperty(p, o);
        }
    }

    public final static void createResourcePropertyStatements(List<? extends Resource> sList,
            Property p, Resource o) {
        if ((sList != null) && (p != null) && (o != null)) {
            for (Resource s : sList) {
                createResourcePropertyStatements(s, p, o);
            }
        }
    }

    public final static void createResourcePropertyStatements(Resource s, Property p,
            List<? extends Resource> oList) {
        if ((s != null) && (p != null) && (oList != null)) {
            for (Resource o : oList) {
                createResourcePropertyStatements(s, p, o);
            }
        }
    }

    public final static void createResourceAsSubclassStatements(Resource resource,
            Resource superClass) {
        createResourcePropertyStatements(resource, RDFS.subClassOf, superClass);
    }

    public final static void createResourceAsSubclassStatements(Resource resource,
            List<? extends Resource> superClasses) {
        createResourcePropertyStatements(resource, RDFS.subClassOf, superClasses);
    }

    public final static void createRemoveResourceAsSubclassStatements(Resource resource,
            Resource superClass) {
        if ((resource != null) && (superClass != null)) {
            resource.getModel().remove(resource, RDFS.subClassOf, superClass);
            if (superClass.isAnon()) {
                superClass.removeProperties();
            }
        }
    }

    public final static void createRemoveResourceAsSubclassStatements(Resource resource,
            List<? extends Resource> superClasses) {
        if ((resource != null) && (superClasses != null)) {
            for (Resource superClass : superClasses) {
                createRemoveResourceAsSubclassStatements(resource, superClass);
            }
        }
    }

    public final static void createResourceAsComplementClassStatements(Resource resource,
            Resource complement) {
        createResourcePropertyStatements(resource, OWL.complementOf, complement);
    }

    public final static void createResourceAsIntersectionClassStatements(Resource resource,
            Resource intersection) {
        createResourcePropertyStatements(resource, OWL.intersectionOf, intersection);
    }

    public final static void createResourceAsUnionClassStatements(Resource resource, Resource union) {
        createResourcePropertyStatements(resource, OWL.unionOf, union);
    }

    public final static void createResourceAsEquivalentClassStatements(Resource resource,
            Resource equivalent) {
        createResourcePropertyStatements(resource, OWL.equivalentClass, equivalent);
    }

    public final static void createResourceAsQualification(Resource qualification, Resource nature) {
        if ((qualification != null) && (nature != null)) {
            qualification.addProperty(SEMM.isQualificationOf, nature);
            nature.addProperty(SEMM.isNatureOf, qualification);
        }
    }

    public final static void createRemoveResourceStatements(OntResource resource) {
        createRemoveResourceStatements(resource, false);
    }

    public final static void createRemoveResourceStatements(OntResource resource,
            boolean alsoRemoveTriplesWithResourceAsProperty) {
        if (resource != null) {
            // remove properties and references, leaving its use in lists
            // intact!
            resource.remove();

            if (alsoRemoveTriplesWithResourceAsProperty == true) {
                // remove predicate use (i.e. all statements with
                // selectedResource as predicate)
                createRemoveResourceAsPredicateStatements(resource);
            }
        }
    }

    public final static void createRemoveResourceStatements(Resource resource) {
        createRemoveResourceStatements(resource, false);
    }

    public final static void createRemoveResourceStatements(Resource resource,
            boolean alsoRemoveTriplesWithResourceAsProperty) {
        if (resource != null) {
            Model model = resource.getModel();
            // remove properties (i.e. all statements with selectedResource as
            // subject)
            resource.removeProperties();
            // remove references (i.e. all statements with selectedResource as
            // object)
            model.remove(model.listStatements(new SimpleSelector(null, null, resource)).toList());

            if (alsoRemoveTriplesWithResourceAsProperty == true) {
                // remove predicate use (i.e. all statements with
                // selectedResource as predicate)
                createRemoveResourceAsPredicateStatements(resource);
            }
        }
    }

    private final static void createRemoveResourceAsPredicateStatements(Resource resource) {
        Model model = resource.getModel();
        Property predicate = resource.as(Property.class);
        if (predicate != null) {
            model.remove(model.listStatements(new SimpleSelector(null, predicate, (RDFNode) null))
                    .toList());
        }
    }

}
