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

package com.semmtech.plugin.semmweb.core.wizards;


import java.util.List;

import org.eclipse.jface.wizard.Wizard;

import com.google.common.collect.Lists;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Statement;
import com.semmtech.plugin.semmweb.core.model.IModelProvider;
import com.semmtech.plugin.semmweb.core.model.OntModelUtils;


public class ModifyModelWizard extends Wizard {
    protected boolean suppressNotify = false;
    protected OntModel model;
    protected OntModel localCopyOfModel;

    protected List<Statement> createdStatements;

    // protected List<Statement> removedStatements;

    public ModifyModelWizard(IModelProvider modelProvider) {
        this.model = modelProvider.getOntModel();
        // / Create a local copy, of the underlying model
        // / TODO: Use a basic GenericRuleReasoner to create a leaner version of
        // the model (only relevant triples are kept).
        this.localCopyOfModel = OntModelUtils.copyModel(model);
        // / Added statements will be used to collect all statements which are
        // temporarily added locally.
        this.createdStatements = Lists.newArrayList();
    }

    @Override
    public boolean performFinish() {
        // TODO Auto-generated method stub
        return false;
    }

    public void setSuppressNotify(boolean suppress) {
        this.suppressNotify = suppress;
    }

}
