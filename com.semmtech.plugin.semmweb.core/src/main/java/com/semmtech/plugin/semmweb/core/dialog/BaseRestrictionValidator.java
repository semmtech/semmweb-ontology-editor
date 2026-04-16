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

package com.semmtech.plugin.semmweb.core.dialog;


import com.hp.hpl.jena.ontology.Restriction;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.OWL2;


public class BaseRestrictionValidator implements RestrictionValidator {

    @Override
    public String isConsistent(Restriction restriction) {
        return null;
    }

    @Override
    public String isValid(Restriction restriction) {
        if (!restriction.hasProperty(OWL.onProperty))
            return "Please provide a property for the restriction";
        else if (!restriction.hasProperty(OWL.allValuesFrom)
                && !restriction.hasProperty(OWL.someValuesFrom)
                && !restriction.hasProperty(OWL.hasValue)
                && !restriction.hasProperty(OWL.minCardinality)
                && !restriction.hasProperty(OWL.maxCardinality)
                && !restriction.hasProperty(OWL.cardinality)
                && !restriction.hasProperty(OWL2.minQualifiedCardinality)
                && !restriction.hasProperty(OWL2.maxQualifiedCardinality)
                && !restriction.hasProperty(OWL2.qualifiedCardinality))
            return "Please specify a property type and a value and/or cardinality";
        else if (!restriction.hasProperty(OWL2.onClass)
                && (restriction.hasProperty(OWL2.minQualifiedCardinality)
                        || restriction.hasProperty(OWL2.maxQualifiedCardinality) || restriction
                            .hasProperty(OWL2.qualifiedCardinality)))
            return "Please specify a qualification for the provided cardinality";
        else if (restriction.hasProperty(OWL2.onClass)
                && (!restriction.hasProperty(OWL2.minQualifiedCardinality)
                        && !restriction.hasProperty(OWL2.maxQualifiedCardinality) && !restriction
                            .hasProperty(OWL2.qualifiedCardinality)))
            return "Please specify a cardinality for the provided qualification";
        return null;
    }
}
