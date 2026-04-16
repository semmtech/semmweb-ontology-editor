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


import com.hp.hpl.jena.ontology.Restriction;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.OWL2;


/**
 * 
 * @author Simone Rondelli
 */
public class RestrictionUtil {

    /**
     * Try to retrieve the resource pointed by the restriction (someValuesFrom,
     * allValuesFrom, hasValue, onClass).
     * <p>
     * NB: In case of Literal value null is returned
     * 
     * @return The resource if exist, null otherwise or in case of literal value
     */
    public static Resource getQualifiedResource(Restriction restriction) {
        if (restriction.hasProperty(OWL.someValuesFrom)) {
            return restriction.getPropertyResourceValue(OWL.someValuesFrom);
        }
        if (restriction.hasProperty(OWL.allValuesFrom)) {
            return restriction.getPropertyResourceValue(OWL.allValuesFrom);
        }
        if (restriction.hasProperty(OWL.hasValue)) {
            RDFNode node = restriction.getProperty(OWL.hasValue).getObject();
            if (node != null && node.isResource()) {
                return node.asResource();
            }
        }
        if (restriction.hasProperty(OWL2.onClass)) {
            return restriction.getPropertyResourceValue(OWL2.onClass);
        }
        return null;
    }

}
