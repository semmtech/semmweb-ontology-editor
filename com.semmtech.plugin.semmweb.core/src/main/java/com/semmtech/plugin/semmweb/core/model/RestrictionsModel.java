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

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.Restriction;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.OWL2;
import com.hp.hpl.jena.vocabulary.RDFS;


public final class RestrictionsModel {
    private OntModel ontModel;

    public RestrictionsModel(OntModel model) {
        this.ontModel = model;
    }

    public OntModel getModel() {
        return ontModel;
    }

    public void setCardinality(Restriction restriction, int minCardinality, int maxCardinality,
            Resource affectedClass) {
        List<Restriction> restrictionsSet = Lists.newArrayList(restriction);
        setCardinality(restrictionsSet, minCardinality, maxCardinality, affectedClass);
    }

    public void setCardinality(Restriction restriction, int minCardinality, int maxCardinality) {
        List<Restriction> restrictionsSet = Lists.newArrayList(restriction);
        setCardinality(restrictionsSet, minCardinality, maxCardinality, null);
    }

    public void setCardinality(List<Restriction> restrictionsSet, int minCardinality,
            int maxCardinality) {
        setCardinality(restrictionsSet, minCardinality, maxCardinality, null);
    }

    public void setCardinality(List<Restriction> restrictionsSet, int minCardinality,
            int maxCardinality, Resource affectedClass) {

        Preconditions.checkNotNull(restrictionsSet);
        Preconditions.checkState(!restrictionsSet.isEmpty());
        Preconditions.checkNotNull(restrictionsSet.get(0));

        // First get the onProperty and onClass properties shared by the
        // restrictions
        Restriction firstRestriction = restrictionsSet.get(0);
        Property onProperty = firstRestriction.getOnProperty();
        Resource onClass = RestrictionResource.getOnClass(firstRestriction);

        // First create the new restrictions
        List<Restriction> newRestrictions = createRestrictions(onProperty, minCardinality,
                maxCardinality, onClass);

        // Then set the affectedClass, if existent, as subClass of the new
        // restrictions instead of the old restrictions
        if (affectedClass != null) {
            replaceRestrictionSuperclasses(affectedClass, restrictionsSet, newRestrictions);
        }
    }

    public void replaceRestrictionSuperclasses(Resource resource,
            List<Restriction> oldRestrictions, List<Restriction> newRestrictions) {

        Preconditions.checkNotNull(resource);

        if ((oldRestrictions != null) && (newRestrictions != null)) {
            // Remove any overlapping restrictions between oldRestrictions and
            // newRestrictions. The relation of this resource will not have to
            // be adjusted for those Restrictions.
            List<Restriction> intersection = getIntersection(oldRestrictions, newRestrictions);
            remove(intersection, oldRestrictions);
            remove(intersection, newRestrictions);
        }

        if (oldRestrictions != null) {
            // Remove subclassOf relations to the old restrictions
            for (Restriction r : oldRestrictions) {
                ontModel.remove(resource, RDFS.subClassOf, r);
            }

            // Remove discardable restrictions from the model
            List<Restriction> discardableRestrictions = getDiscardableRestrictions(oldRestrictions,
                    resource);
            RestrictionStatements.createRemoveRestrictionStatements(discardableRestrictions);
        }

        if (newRestrictions != null) {
            // Add subClassOf relations to the new restrictions
            for (Restriction r : newRestrictions) {
                ontModel.add(resource, RDFS.subClassOf, r);
            }
        }
    }

    private List<Restriction> getIntersection(List<Restriction> lhs, List<Restriction> rhs) {
        Preconditions.checkNotNull(lhs);
        Preconditions.checkNotNull(rhs);

        List<Restriction> intersection = Lists.newArrayList();
        for (Restriction r : lhs) {
            if (rhs.contains(r)) {
                intersection.add(r);
            }
        }
        return intersection;
    }

    private void remove(List<Restriction> removeList, List<Restriction> fromList) {
        Preconditions.checkNotNull(removeList);
        Preconditions.checkNotNull(fromList);

        for (Restriction r : removeList) {
            int index = fromList.indexOf(r);
            while (index >= 0) {
                fromList.remove(index);
                index = fromList.indexOf(r);
            }
        }
    }

    public List<Restriction> createRestrictions(Property onProperty, int minCardinality,
            int maxCardinality, Resource onClass) {

        Preconditions.checkNotNull(onProperty);

        List<Restriction> createdRestrictions = Lists.newArrayList();

        if (minCardinality < 0) {
            minCardinality = 0;
        }

        if ((maxCardinality < 0) && (onClass != null)) {
            if (minCardinality == 0) {
                // [0,n]: OWL.allValuesFrom
                Restriction newRestriction = ontModel.createAllValuesFromRestriction(null,
                        onProperty, onClass);
                createdRestrictions.add(newRestriction);
                return createdRestrictions;
            }
            else if (minCardinality == 1) {
                // [1,n]: OWL.someValuesFrom
                Restriction newRestriction = ontModel.createSomeValuesFromRestriction(null,
                        onProperty, onClass);
                createdRestrictions.add(newRestriction);
                return createdRestrictions;
            }
        }

        if (minCardinality == maxCardinality) {
            // OWL.cardinality or OWL2.qualifiedCardinality
            Restriction newRestriction = createCardinalityRestriction(onProperty, onClass,
                    minCardinality, OWL.cardinality, OWL2.qualifiedCardinality);
            createdRestrictions.add(newRestriction);
            return createdRestrictions;
        }

        if (minCardinality > 0) {
            // OWL.minCardinality or OWL2.minQualifiedCardinality
            Restriction newRestriction = createCardinalityRestriction(onProperty, onClass,
                    minCardinality, OWL.minCardinality, OWL2.minQualifiedCardinality);
            createdRestrictions.add(newRestriction);
        }
        if (maxCardinality >= 0) {
            // OWL.maxCardinality or OWL2.maxQualifiedCardinality
            Restriction newRestriction = createCardinalityRestriction(onProperty, onClass,
                    maxCardinality, OWL.maxCardinality, OWL2.maxQualifiedCardinality);
            createdRestrictions.add(newRestriction);
        }

        return createdRestrictions;
    }

    private Restriction createCardinalityRestriction(Property onProperty, Resource onClass,
            int cardinalityNumber, Property unqualifiedCardinalityProperty,
            Property qualifiedCardinalityProperty) {
        Preconditions.checkNotNull(onProperty);

        Restriction newRestriction = ontModel.createRestriction(onProperty);

        if (onClass != null) {
            newRestriction.addProperty(OWL2.onClass, onClass);
            newRestriction.addProperty(qualifiedCardinalityProperty,
                    Integer.toString(cardinalityNumber), XSDDatatype.XSDnonNegativeInteger);
        }
        else {
            newRestriction.addProperty(unqualifiedCardinalityProperty,
                    Integer.toString(cardinalityNumber), XSDDatatype.XSDnonNegativeInteger);
        }

        return newRestriction;
    }

    /**
     * Returns all related restrictions of the currently edited restriction that
     * no longer have any semantic links and can be discarded. Related
     * restrictions are marked as discardable if they adhere to the following
     * criteria: 1) The restriction class is anonymous (otherwise it might be
     * referenced externally). 2) Triples containing the restriction as object
     * only have the affectedClass as subject. 3) The restriction class is not
     * the subject of any owl:equivalentClass or owl:sameAs triple with an
     * object other than affectedClass.
     */
    private List<Restriction> getDiscardableRestrictions(List<Restriction> restrictionSet,
            Resource affectedClass) {

        List<Restriction> discardableRestrictions = Lists.newArrayList();

        if ((restrictionSet == null) || (restrictionSet.isEmpty()) || (affectedClass == null)) {
            return discardableRestrictions;
        }

        for (Restriction restriction : restrictionSet) {
            if (restriction == null || restriction.isAnon() == false) {
                continue;
            }

            if (containsSubjectBesidesResource(ontModel.listStatements(new SimpleSelector(null,
                    null, restriction.asResource())), affectedClass)) {
                continue;
            }

            if (containsNodeBesidesResource(
                    ontModel.listObjectsOfProperty(restriction, OWL.equivalentClass), affectedClass)) {
                continue;
            }

            if (containsNodeBesidesResource(
                    ontModel.listObjectsOfProperty(restriction, OWL.sameAs), affectedClass)) {
                continue;
            }

            discardableRestrictions.add(restriction);
        }

        return discardableRestrictions;
    }

    private boolean containsSubjectBesidesResource(StmtIterator iter, Resource resource) {
        Preconditions.checkNotNull(iter);
        Preconditions.checkNotNull(resource);

        while (iter.hasNext()) {
            if (iter.next().getSubject().equals(resource) == false) {
                return true;
            }
        }
        return false;
    }

    private boolean containsNodeBesidesResource(NodeIterator iter, Resource resource) {
        Preconditions.checkNotNull(iter);
        Preconditions.checkNotNull(resource);

        while (iter.hasNext()) {
            if (iter.next().equals(resource) == false) {
                return true;
            }
        }
        return false;
    }
}
