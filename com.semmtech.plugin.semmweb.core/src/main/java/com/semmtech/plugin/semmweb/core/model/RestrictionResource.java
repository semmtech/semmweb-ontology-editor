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


import com.hp.hpl.jena.enhanced.EnhGraph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.Restriction;
import com.hp.hpl.jena.ontology.impl.RestrictionImpl;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.OWL2;


public class RestrictionResource extends RestrictionImpl {
    public RestrictionResource(Node n, EnhGraph g) {
        super(n, g);
    }

    public int getExactCardinality() {
        return getExactCardinality(this);
    }

    public int getMinCardinality() {
        return getMinCardinality(this);
    }

    public int getMaxCardinality() {
        return getMaxCardinality(this);
    }

    public boolean isCardinalityLimitModifier() {
        return isCardinalityLimitModifier(this);
    }

    public OntClass getOnClass() {
        return getOnClass(this);
    }

    public RDFNode getValue() {
        return getValue(this);
    }

    public final static int getExactCardinality(Restriction restriction) {
        if (restriction == null) {
            return -1;
        }

        try {
            if (restriction.hasProperty(OWL2.qualifiedCardinality)) {
                return restriction.getPropertyValue(OWL2.qualifiedCardinality).asLiteral().getInt();
            }
            if (restriction.hasProperty(OWL.cardinality)) {
                return restriction.getPropertyValue(OWL.cardinality).asLiteral().getInt();
            }
        }
        catch (NumberFormatException e) {
            return -1;
        }
        return -1;
    }

    public final static int getMinCardinality(Restriction restriction) {
        if (restriction == null) {
            return -1;
        }

        int exactCardinality = getExactCardinality(restriction);
        if (exactCardinality != -1) {
            return exactCardinality;
        }
        try {
            if (restriction.hasProperty(OWL.minCardinality)) {
                return restriction.getPropertyValue(OWL.minCardinality).asLiteral().getInt();
            }
            if (restriction.hasProperty(OWL2.minQualifiedCardinality)) {
                return restriction.getPropertyValue(OWL2.minQualifiedCardinality).asLiteral()
                        .getInt();
            }
            if (restriction.hasProperty(OWL.someValuesFrom)
                    || restriction.hasProperty(OWL.hasValue)) {
                return 1;
            }
            if (restriction.hasProperty(OWL.allValuesFrom)) {
                return 0;
            }
        }
        catch (NumberFormatException e) {
            return -1;
        }
        return -1;
    }

    public final static int getMaxCardinality(Restriction restriction) {
        if (restriction == null) {
            return -1;
        }

        int exactCardinality = getExactCardinality(restriction);
        if (exactCardinality != -1) {
            return exactCardinality;
        }
        try {
            if (restriction.hasProperty(OWL.maxCardinality)) {
                return restriction.getPropertyValue(OWL.maxCardinality).asLiteral().getInt();
            }
            if (restriction.hasProperty(OWL2.maxQualifiedCardinality)) {
                return restriction.getPropertyValue(OWL2.maxQualifiedCardinality).asLiteral()
                        .getInt();
            }
        }
        catch (NumberFormatException e) {
            return -1;
        }
        return -1;
    }

    public final static boolean isCardinalityLimitModifier(Restriction restriction) {
        // return (restriction.isCardinalityRestriction() == true
        // || restriction.isMinCardinalityRestriction() == true
        // || restriction.isMaxCardinalityRestriction() == true);

        // The commented out code above does not take into account qualified
        // cardinalities.
        // As such, it's best to avoid using those ambiguous functions.
        return (restriction.hasProperty(OWL.cardinality)
                || restriction.hasProperty(OWL.minCardinality)
                || restriction.hasProperty(OWL.maxCardinality)
                || restriction.hasProperty(OWL2.qualifiedCardinality)
                || restriction.hasProperty(OWL2.minQualifiedCardinality) || restriction
                    .hasProperty(OWL2.maxQualifiedCardinality));
    }

    public final static OntClass getOnClass(Restriction restriction) {
        if (restriction.hasProperty(OWL2.onClass)) {
            return restriction.getPropertyValue(OWL2.onClass).as(OntClass.class);
        }
        else if (restriction.hasProperty(OWL.allValuesFrom)) {
            return restriction.getPropertyValue(OWL.allValuesFrom).as(OntClass.class);
        }
        else if (restriction.hasProperty(OWL.someValuesFrom)) {
            return restriction.getPropertyValue(OWL.someValuesFrom).as(OntClass.class);
        }
        return null;
    }

    public final static RDFNode getValue(Restriction restriction) {
        if (restriction.hasProperty(OWL.hasValue)) {
            return restriction.getPropertyValue(OWL.hasValue);
        }
        return null;
    }
}
