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

package com.semmtech.plugin.semmweb.dictionary.wizards;


import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Resource;


public class CreateSKOSConceptSchemeWizardPage extends WizardPage {
    @SuppressWarnings("unused")
    private OntModel model;

    public CreateSKOSConceptSchemeWizardPage(String pageName, OntModel model) {
        this(pageName, model, null);
    }

    public CreateSKOSConceptSchemeWizardPage(String pageName, OntModel model, Resource type) {
        super(pageName);
        setTitle("Scheme");
        setDescription("Create a new concept scheme.");

        this.model = model;
    }

    @Override
    public void createControl(Composite parent) {

    }

}
