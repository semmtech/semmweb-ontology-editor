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

package com.semmtech.spin.inference;


import org.topbraid.spin.util.CommandWrapper;
import org.topbraid.spin.util.QueryWrapper;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.semmtech.spin.system.SPINImports;
import com.semmtech.spin.util.CommandWrapperUtil;


/**
 * 
 * @author Sander Stolk
 */
public class SPINInferences {
    /**
     * Runs a given Jena Query on a given instance and adds the inferred triples
     * to a given Model.
     * 
     * @param queryURI
     *            the URI of the CONSTRUCT query
     * @param queryModel
     *            the query Model
     * @param newTriples
     *            the Model to write the triples to
     * @param instance
     *            the instance to run the inferences on
     * @param checkContains
     *            true to only call add if a Triple wasn't there yet
     * @return true if changes were done (only meaningful if checkContains ==
     *         true)
     */
    public static boolean runQueryOnInstance(String queryURI, Model queryModel, Model newTriples,
            Resource instance, boolean checkContains) {

        OntModel spinModel = SPINImports.createSPINModel(queryModel);

        CommandWrapper commandWrapper = CommandWrapperUtil.find(queryURI, spinModel);
        if (commandWrapper instanceof QueryWrapper) {
            QueryWrapper queryWrapper = (QueryWrapper) commandWrapper;
            return org.topbraid.spin.inference.SPINInferences.runQueryOnInstance(queryWrapper,
                    spinModel, newTriples, instance, checkContains);
        }
        return false;
    }
}
