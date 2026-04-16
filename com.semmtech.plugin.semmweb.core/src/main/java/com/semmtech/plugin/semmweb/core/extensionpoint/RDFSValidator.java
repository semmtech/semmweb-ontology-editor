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

package com.semmtech.plugin.semmweb.core.extensionpoint;


import java.util.Iterator;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.reasoner.ValidityReport;
import com.hp.hpl.jena.reasoner.ValidityReport.Report;
import com.semmtech.plugin.semmweb.core.CorePlugin;


/**
 * 
 * @author Sander Stolk
 */
public class RDFSValidator extends AbstractModelValidator {

    private static final Logger logger = Logger.getLogger(RDFSValidator.class);

    public static final String ID = "com.semmtech.plugin.semmweb.core.validator.rdfsValidator";

    public RDFSValidator() {
        super(ID, CorePlugin.getDefault().getPreferenceStore());
    }

    @Override
    public void validateModel(OntModel model) {
        InfModel infModel = ModelFactory.createRDFSModel(model);
        ValidityReport validity = infModel.validate();
        if (validity.isClean()) {
            logger.info("RDFS model is valid (logically consistent) and generated no warnings.");
        }
        else {
            for (Iterator<Report> iter = validity.getReports(); iter.hasNext();) {
                Report report = iter.next();
                logger.warn(report.toString());
            }
            if (validity.isValid()) {
                logger.warn("No logical inconsistencies were detected in RDFS model.");
            }
            else {
                logger.warn("Model is not a valid RDFS model");
            }
        }
    }

    @Override
    public String getName() {
        return "RDFS Validity";
    }

    @Override
    public String getDescription() {
        return "Checks if model is a valid RDFS model";
    }

}
